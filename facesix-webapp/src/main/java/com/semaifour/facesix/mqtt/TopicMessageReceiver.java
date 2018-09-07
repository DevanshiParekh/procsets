package com.semaifour.facesix.mqtt;

public interface TopicMessageReceiver {
	public String getName();
	public boolean messageArrived(String topic, String message);
}
