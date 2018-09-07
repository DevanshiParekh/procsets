package com.semaifour.facesix.mqtt;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttPubSub extends TimerTask {
	
	private static Logger LOG = LoggerFactory.getLogger(MqttPubSub.class.getName());

	MqttClient mypubClient;	
	MqttClient mysubClient;

	MqttConnectOptions connoptions;
	int trycount = 0;
	
	Map<String, Topixel> subTopicMap = new ConcurrentHashMap<String, Topixel>();
	Map<String, MqttTopic> pubTopicMap = new ConcurrentHashMap<String, MqttTopic>();
	
	@Autowired 
	private MqttConfiguration mqttConfiguration;
	
	Timer connectionTimer;
	
	@Autowired
	private TopicMessageRouter topicMessageRouter;
	
	int deliveryQos = 0;
	boolean retained = false;

	
	public MqttPubSub() {
	}
	
	@PostConstruct
	public boolean init() {		
		LOG.info("initializing ...");
		try {
			deliveryQos = mqttConfiguration.getDeliveryQoS();
			retained = mqttConfiguration.shoudRetainMessages();

			if (StringUtils.isNotEmpty(mqttConfiguration.getBrokerUrl())) {
				LOG.info("Connecting to MQTT at :" + mqttConfiguration.getBrokerUrl());
				// Init MQTT Client options
				connoptions = new MqttConnectOptions();	
				connoptions.setCleanSession(false);
				connoptions.setConnectionTimeout(30);
				connoptions.setKeepAliveInterval(60);
				if (StringUtils.isNotEmpty(mqttConfiguration.getCACertFilePath())) {
					connoptions.setSocketFactory(SslUtil.getSocketFactory(mqttConfiguration.getCACertFilePath(), 
																  mqttConfiguration.getClientCertFilePath(), 
																  mqttConfiguration.getClientKeyFilePath(), 
																  mqttConfiguration.getCertPassword()));
				}
				
				if (StringUtils.isNotEmpty(mqttConfiguration.getPrincipal())) {
					connoptions.setUserName(mqttConfiguration.getPrincipal());
					connoptions.setPassword(mqttConfiguration.getPrincipal().toCharArray());
				}
				//init client object for publishing
				initPubClient();
				//now connect to topic2publish
				initPubscription();

				
				//now read and map consumable topics and receivers configured
				String topic2consume = mqttConfiguration.getTopic2Consume();
				
				if (StringUtils.isNotEmpty(topic2consume)) {
					LOG.info("Subscribing to topic2consume :" + topic2consume);
					String[] topics = topic2consume.split(",");
					for (String tp : topics) {
						String[] tmp = tp.split("=");
						try {
							LOG.info("Loading MqttMessageReceiver class :" + tmp[1]);
							Class<MqttMessageReceiver> clazz = (Class<MqttMessageReceiver>) Class.forName(tmp[1]);
					
							Topixel topixel = new Topixel();
							topixel.receiver = clazz.newInstance();
							topixel.className = tmp[1];
							topixel.topicName = tmp[1];
							topixel.persist = mqttConfiguration.shouldSaveMessage(topixel.topicName);

							subTopicMap.put(tmp[0], topixel);
							LOG.info("Successfully mapped receiver for  topic :" + tp);
		
						} catch (Throwable tr) {
							LOG.warn("Error mapping MqttMessageReceiver for  :" + tp, tr);
						}
					}
				}
				
				topicMessageRouter.setReceiverMap(subTopicMap);
				
				//init client object for subscriptions
				initSubClient();
				//now connect & subscribe to topics2consume
				initSubscriptions();
				
				//set up a trigger to health check connections
				connectionTimer = new Timer("mqtt-connection-timer", true);
				connectionTimer.scheduleAtFixedRate(this, mqttConfiguration.getConnRetryDelay(), 
														  mqttConfiguration.getConnRetryInterval());

				return true;
				
			} else {
				LOG.info("MQTT is diabled");
			}
		} catch (Exception e) {
			LOG.info("Failed to init MQTT at :" + mqttConfiguration.getBrokerUrl(), e);
		}
		return false;
	}
	
	/**
	 * Initializes a client object for Publishing (doesn't establish connection though)
	 * 
	 * @throws MqttException
	 */
	private void initPubClient() throws MqttException {
		// NEW Connect to Broker
		mypubClient = new MqttClient(mqttConfiguration.getBrokerUrl(), "facesix-pub0-"+ mqttConfiguration.properties.getInstanceId());

		mypubClient.setCallback(new MqttCallback() {
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
				try {
					if (LOG.isDebugEnabled()) LOG.debug("mypubClient.deliveryComplete(): [{}]: ", token);
				} catch (Exception e) {
					LOG.warn("Error in deliveryComplete :", e);
				}
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				LOG.warn("message should not arrive at here ");
			}
			
			@Override
			public void connectionLost(Throwable t) {
				LOG.warn("mypubClient connection last, will be reconnected when pub triggered");
			}
			
		});


	}
	
	/**
	 * Connects to MQTT server and topic2publish
	 * 
	 * @return
	 * @throws MqttException 
	 * @throws MqttSecurityException 
	 */
	protected synchronized boolean initPubscription() throws MqttSecurityException, MqttException {
		//check if mqtt is disabled
		if (connoptions == null || mypubClient == null) return false;
			
		mypubClient.connect(connoptions);
			
		//connect to topic 2 publish		
		MqttTopic defaultTopic2Publish = mypubClient.getTopic(mqttConfiguration.getTopic2Publish());
		
		//enlist to map
		pubTopicMap.clear();
		pubTopicMap.put(mqttConfiguration.getTopic2Publish(), defaultTopic2Publish);
			
		LOG.info("Connected to defaultTopic2Publish  :" + defaultTopic2Publish.getName());
		return true;
	}

	/**
	 * Initializes mqtt connection object for subscriptions (doesn't establish connection though)
	 * 
	 * @throws MqttException
	 */
	private  void initSubClient() throws MqttException {
		mysubClient = new MqttClient(mqttConfiguration.getBrokerUrl(), "facesix-sub0-" + mqttConfiguration);
		//topicMessageRouter = new TopicMessageRouter(msgRecvrMap);
		mysubClient.setCallback(getTopicMessageRouter());
		
//		mysubClient.setCallback(new MqttCallback() {
//
//			@Override
//			public void connectionLost(Throwable cause) {
//				LOG.info("mysubClient connection lost for Sub, will try initSubscriptions() again");
//			}
//
//			/**
//			 * 
//			 * messageArrived
//			 * This callback is invoked when a message is received on a subscribed topic.
//			 */
//			@Override
//			public void messageArrived(String topic, MqttMessage message) throws Exception {
//				StringBuilder status = new StringBuilder("Received");
//
//				Topixel topixel = msgRecvrMap.get(topic);
//
//				try {
//					MqttMessageReceiver receiver = topixel != null ? topixel.receiver : null;
//					if (receiver != null) {
//						try {
//							boolean bool = receiver.messageArrived(topic, message);
//							status.append("->").append(receiver.getName()).append(":").append(bool);
//						} catch (Throwable t) {
//							LOG.warn("Error in invoking message receiver for :" + topic, t);
//							status.append("->").append(receiver.getName()).append(":").append(t.getMessage());
//						}
//					} else {
//						status.append("->").append("No receivers");
//					}
//				} catch (Exception e) {
//					LOG.warn("Error in messageArrived :", e);
//					status.append("->").append("Error :").append(e.toString());
//				}
//				if (LOG.isDebugEnabled()) LOG.debug("mysubClient.messageArrived(): {}", status);
//				if (topixel.persist == true) {
//					try {
//						Notification notif = new Notification(message.toString(), status.toString(), topic, "MqttPubSub");
//						notifService.save(notif);
//					} catch (Exception e) {
//						LOG.warn("Error persisting mqtt message :", e);
//					}
//				}
//			}
//
//			@Override
//			public void deliveryComplete(IMqttDeliveryToken token) {
//				if(LOG.isDebugEnabled()) LOG.debug("mysubClient.deliveryComplete() [{}]", token);
//			}
//			
//		});

	}
	
	/**
	 * 
	 * init/refresh subscriptions
	 * 
	 * @return
	 */
	int mysubcounter = 1;
	protected synchronized boolean initSubscriptions() throws MqttSecurityException, MqttException {
		//check if mqtt is disabled
		if (connoptions == null || mysubClient == null) return false;

		mysubClient.connect(connoptions);
		LOG.info("MQTT mysubClient connected with counter {}", mysubcounter);;
		
		for(String topic : subTopicMap.keySet()) {
			try {
				mysubClient.subscribe(topic);
				LOG.info("Successfully subscribed to  topic :" + topic);
			} catch (Exception e) {
				LOG.warn("Failed to subscribe to topic :" + topic);
			}

		}
		return true;
	}
	
	/**
	 * Publish the given message
	 * 
	 * @param message
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public boolean publish(String message, String topicName) {
		if (connoptions == null) return false;
   		int pubQoS = 0;
		MqttMessage mqttmsg = new MqttMessage(message.getBytes());
		mqttmsg.setQos(pubQoS);
		mqttmsg.setRetained(false);
		try {
			if (!mypubClient.isConnected()) {
				this.initPubscription();
			}
	    	// Publish the message
	    	MqttDeliveryToken token = null;
	    	
	    	//get respective topic
	    	MqttTopic topic = pubTopicMap.get(topicName);
	    	
	    	if (topic == null) {
	    		topic = mypubClient.getTopic(topicName);
	    		pubTopicMap.put(topicName, topic);
	    	}
	    	
    		// publish message to broker
    		token = topic.publish(mqttmsg);

	    	// Wait until the message has been delivered to the broker
			token.waitForCompletion();
		} catch (Exception e) {
			LOG.error("Failed to publish T:[{}] M:[{}] E:[{}]", topicName, message, e);
			return false;
		}
		return true;
	}

	public boolean publish(String message) {
		return publish(message, mqttConfiguration.getTopic2Publish());
	}
	public boolean publish(Payload payload) {
		return publish(payload.toJSONString(), mqttConfiguration.getTopic2Publish());
	}
	
	public boolean publish(Payload payload, String topicName) {
		return publish(payload.toJSONString(), topicName);
	}


	@Override
	public void run() {
		
		//ignore if mqtt is disabled
		if (connoptions == null) return;
		
		if (!mysubClient.isConnected()) {
			LOG.info("MQTT.mysubClient found DISCONNECTED, may be MQTT service died, try connecting again");
			try {
				if (!initSubscriptions()) {	
					LOG.warn("MQTT.mysubClient connection: FALSE");
				}

			} catch (Exception e) {
				LOG.error("Error connecting to MQTT.mysubClient", e);
			}
		}

		if (!mypubClient.isConnected()) {
			LOG.info("MQTT.mysubClient found DISCONNECTED, may be MQTT service died, try connecting again");
			try {
				if (!initPubscription()) {	
					LOG.warn("MQTT.mypubClient connection. FALSE");
				}
			} catch (Exception e) {
				LOG.error("Error connecting to MQTT.mypubClient", e);
			}
		}

	}
	
	
	@PreDestroy
	public void close() {
		closePubClient();
		closeSubClient();
	}
	
	private void closePubClient() {
		try {
			if (mypubClient != null) {
				mypubClient.close();
				
			}
			LOG.info("MQTT.mypubClient connection closed");
		} catch (Exception e) {}
	}
	
	private void closeSubClient() {
		try {
			if (mysubClient != null) {
				mysubClient.close();
			}
			LOG.info("MQTT.mysubClient connection closed");
			
		} catch (Exception e) {}
	}

	public TopicMessageRouter getTopicMessageRouter() {
		return topicMessageRouter;
	}

	public void setTopicMessageRouter(TopicMessageRouter topicMessageRouter) {
		this.topicMessageRouter = topicMessageRouter;
	}

}

class Topixel {
	MqttMessageReceiver  		receiver;
	boolean 					persist;
	String 						topicName;
	String 						className;
	
	public Topixel() {
	}
	
	@Override
	public String toString() {
		return "Topixel [receiver=" + receiver + ", persist=" + persist + ", topicName=" + topicName + ", className="
				+ className + "]";
	}

	
}