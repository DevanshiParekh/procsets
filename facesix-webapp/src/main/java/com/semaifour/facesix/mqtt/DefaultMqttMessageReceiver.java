package com.semaifour.facesix.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMqttMessageReceiver implements MqttMessageReceiver {
	
	private static Logger LOG = LoggerFactory.getLogger(DefaultMqttMessageReceiver.class.getName());

	@Override
	public boolean messageArrived(String topic, MqttMessage message) {
		LOG.info("MQTT messageArrvied at " + topic + " : " + message);
		return true;
	}

	@Override
	public String getName() {
		return "DefaultMqttMessageReceiver";
	}

	@Override
	public boolean messageArrived(String topic, String message) {
		LOG.info("STRING messageArrvied at " + topic + " : " + message);
		return true;
	}
}
