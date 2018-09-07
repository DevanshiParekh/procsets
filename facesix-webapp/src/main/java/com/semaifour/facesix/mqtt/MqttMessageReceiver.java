package com.semaifour.facesix.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttMessageReceiver extends TopicMessageReceiver {
	public boolean messageArrived(String topic, MqttMessage message);
}
