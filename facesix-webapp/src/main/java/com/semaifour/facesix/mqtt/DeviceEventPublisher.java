package com.semaifour.facesix.mqtt;

import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceEventPublisher implements MqttCallback {
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceEventPublisher.class.getName());

	//Map<String, MqttClient> dvMqttClients = new HashMap<String, MqttClient>();	
	Map<String, MqttTopic> dvTopics = new HashMap<String, MqttTopic>();
	
	MqttConnectOptions options;
	MqttClient mypubClient = null;

	
	int deliveryQos = 0;
	boolean retained = false;
	
	@Autowired 
	private MqttConfiguration mqttConfiguration;
	
	public DeviceEventPublisher() {
	}
	
	@PostConstruct
	public boolean init() {
		LOG.info("initializing ...");
		try {
			deliveryQos = mqttConfiguration.getDeliveryQoS();
			retained = mqttConfiguration.shoudRetainMessages();
			
			if (StringUtils.isNotEmpty(mqttConfiguration.getBrokerUrl())) {
				LOG.info("Connecting to MQTT at :" + mqttConfiguration.getBrokerUrl());
				// setup MQTT Client
				options = new MqttConnectOptions();	
				options.setCleanSession(false);
				options.setConnectionTimeout(30);
				options.setKeepAliveInterval(60);
				if (StringUtils.isNotEmpty(mqttConfiguration.getCACertFilePath())) {
					options.setSocketFactory(SslUtil.getSocketFactory(mqttConfiguration.getCACertFilePath(), 
																  mqttConfiguration.getClientCertFilePath(), 
																  mqttConfiguration.getClientKeyFilePath(), 
																  mqttConfiguration.getCertPassword()));
				}
				
				if (StringUtils.isNotEmpty(mqttConfiguration.getPrincipal())) {
					options.setUserName(mqttConfiguration.getPrincipal());
					options.setPassword(mqttConfiguration.getPrincipal().toCharArray());
				}
				
			} else {
				LOG.info("diabled");
			}
		} catch (Exception e) {
			LOG.info("Failed to init MQTT at :" + mqttConfiguration.getBrokerUrl(), e);
		}
		return false;
	}
	
	
	/**
	 * Connects to a device specific topic and adds to conn and topic map.
	 * 
	 * @param dvUID
	 * @return
	 */
	private synchronized MqttTopic connect2Topic(String dvUID) {
		//MqttClient mypubClient = null;
		MqttTopic topic2publish = null;
		String topic = mqttConfiguration.getTopic2Publish() + "/" + dvUID;
		//To be checked - no need to check actually [mahi]
		//if (dvUID.length() < 16) {
		//	return false;
		//}

		try {
			if (mypubClient == null || !mypubClient.isConnected()) {
				mypubClient = new MqttClient(mqttConfiguration.getBrokerUrl(), "facesix-pub0-" + mqttConfiguration.properties.getInstanceId() + "-" + dvUID);
				mypubClient.setCallback(this);
				mypubClient.connect(options);
			}
			//connect to topic 2 pubish	
			topic2publish = mypubClient.getTopic(topic);
			//dvMqttClients.put(dvUID, mypubClient);
			if (topic2publish != null) {
				dvTopics.put(dvUID, topic2publish);
				LOG.info("Successfully connected to device topic :{}" + topic);
			} else {
				LOG.error("Failed to connect to device topic :{}" + topic);
			}
			return topic2publish;
		} catch (Exception e) {
			LOG.warn("Error connecting to device topic:" + topic, e);
			return null;
		}
	}
	
	/**
	 * Publish the given message
	 * 
	 * @param message message to be published
	 * @param dvUID device UID
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public boolean publish(String message, String dvUID) {
		//MqttClient mypubClient = dvMqttClients.get(dvUID);
		MqttTopic topic2publish = dvTopics.get(dvUID);
		if (topic2publish == null || !mypubClient.isConnected()) {
			if (connect2Topic(dvUID) != null) {
				topic2publish = dvTopics.get(dvUID);
			} else {
				return false;
			}
		}
		
		LOG.info("MQTT TOPIC " + topic2publish);
		LOG.info("MQTT MESSAGE " + message);
		
		MqttMessage mqttmsg = new MqttMessage(message.getBytes());
		mqttmsg.setQos(deliveryQos);
		mqttmsg.setRetained(retained);
		try {
	    	// Publish the message
	    	MqttDeliveryToken token = null;
			// publish message to broker
			token = topic2publish.publish(mqttmsg);
	    	// Wait until the message has been delivered to the broker
			token.waitForCompletion();
		} catch (Exception e) {
			LOG.error("Failed to publish to device :" +  dvUID, e);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * deliveryComplete
	 * This callback is invoked when a message published by this client
	 * is successfully received by the broker.
	 * 
	 */
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			LOG.debug("deliveryComplete : " + token.getMessage());
		} catch (Exception e) {
			LOG.warn("Error in deliveryComplete :", e);
		}
	}

	/**
	 * 
	 * messageArrived
	 * This callback is invoked when a message is received on a subscribed topic.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOG.warn("Received unexpected message :" + message.toString());
	}
	
	/**
	 * 
	 * connectionLost
	 * This callback is invoked upon losing the MQTT connection.
	 * 
	 */
	@Override
	public void connectionLost(Throwable t) {
		LOG.info("Connection lost, will connect to topic again when publish requested", t);
	}
	
	@PreDestroy
	public void close() {
		try {
			if (mypubClient != null) {
				mypubClient.close();
			}
			LOG.info("MQTT connection closed");
		} catch (Exception e) {}
	}
}