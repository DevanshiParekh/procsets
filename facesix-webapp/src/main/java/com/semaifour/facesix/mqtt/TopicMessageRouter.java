package com.semaifour.facesix.mqtt;

import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.data.elasticsearch.notif.Notification;
import com.semaifour.facesix.data.elasticsearch.notif.NotificationService;

@Component
public class TopicMessageRouter implements MqttCallback {
	
	private static Logger LOG = LoggerFactory.getLogger(TopicMessageRouter.class);
	
	private Map<String, Topixel> msgRecvrMap;
	
	@Autowired
	private NotificationService notifService;
	
	public TopicMessageRouter() {
		super();
	}
	
	public TopicMessageRouter (Map<String, Topixel> msgRecvrMap) {
		this.msgRecvrMap = msgRecvrMap;
	}

	
	@Override
	public void connectionLost(Throwable cause) {
		LOG.info("mysubClient connection lost for Sub, will try initSubscriptions() again");
	}

	/**
	 * 
	 * messageArrived
	 * This callback is invoked when a message is received on a subscribed topic.
	 */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		messageArrived(topic, message.toString());
	}
	
	/**
	 * 
	 * messageArrived
	 * This callback is invoked when a message is received on a subscribed topic.
	 */
	public void messageArrived(String topic, String message) throws Exception {
		StringBuilder status = new StringBuilder("Received");

		Topixel topixel = msgRecvrMap.get(topic);

		try {
			MqttMessageReceiver receiver = topixel != null ? topixel.receiver : null;
			if (receiver != null) {
				try {
					boolean bool = receiver.messageArrived(topic, message);
					status.append("->").append(receiver.getName()).append(":").append(bool);
				} catch (Throwable t) {
					LOG.warn("Error in invoking message receiver for :" + topic, t);
					status.append("->").append(receiver.getName()).append(":").append(t.getMessage());
				}
			} else {
				status.append("->").append("No receivers");
			}
		} catch (Exception e) {
			LOG.warn("Error in messageArrived :", e);
			status.append("->").append("Error :").append(e.toString());
		}
		if (LOG.isDebugEnabled()) LOG.debug("mysubClient.messageArrived(): {}", status);
		if (topixel == null || topixel.persist == true) {
			try {
				Notification notif = new Notification(message.toString(), status.toString(), topic, "MqttPubSub");
				notifService.save(notif);
			} catch (Exception e) {
				LOG.warn("Error persisting mqtt message :", e);
			}
		}
	}


	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * Sets topic message receivers map
	 * 
	 * @param msgRecvrMap
	 */
	public void setReceiverMap(Map<String, Topixel> msgRecvrMap) {
		this.msgRecvrMap = msgRecvrMap;
	}
	
	/**
	 *  Returns topic message receivers map
	 * 
	 * @return
	 */
	public Map<String, Topixel> getReceiverMap() {
		return this.msgRecvrMap;
	}
				
}

