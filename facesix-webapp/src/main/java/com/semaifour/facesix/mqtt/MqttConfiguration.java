package com.semaifour.facesix.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.semaifour.facesix.spring.ApplicationProperties;
import com.semaifour.facesix.util.HostUtil;

/**
 * MqttConfiguration Configuration
 * 
 * @author mjs
 *
 */

@Component
public class MqttConfiguration {
	
	@Autowired
	ApplicationProperties properties;
	
	private String mqttClietnId = null;
	
	public ApplicationProperties properties() {
		return properties;
	}
	
	
	public String getBrokerUrl() {
		return properties().getProperty("mqtt.brokerurl");
	}
	
	public String getTopic2Publish() {
		return  properties().getProperty("mqtt.topic2publish");
	}

	public String getTopic2Consume() {
		return  properties().getProperty("mqtt.topic2consume");
	}

	public String getPrincipal() {
		return  properties().getProperty("mqtt.principal");
	}
	
	public String getSecret() {
		return  properties().getProperty("mqtt.secret");
	}

	public long getRetryCount() {
		return  properties().getLong("mqtt.retrycount", 3);
	}

	public String getCACertFilePath() {
		return  properties().getProperty("mqtt.cert.cacertfilepath");
	}

	public String getClientCertFilePath() {
		return  properties().getProperty("mqtt.cert.clientcertfilepath");
	}

	public String getClientKeyFilePath() {
		return  properties().getProperty("mqtt.cert.clientkeyfilepath");
	}

	public String getCertPassword() {
		return  properties().getProperty("mqtt.cert.certpassword");
	}
	
	public int getDeliveryQoS() {
		return  properties().getInt("mqtt.qos.delivery", 0);
	}

	/**
	 * 
	 * mqtt.qos.shouldretain=true|false(default)
	 * 
	 * @return
	 */
	public boolean shoudRetainMessages() {
		return  properties().getBoolean("mqtt.qos.shouldretain", false);
	}


	/*
	 * mqtt.topic2consume.topic/name/with/path.shouldsave=false|true(default)
	 */
	public boolean shouldSaveMessage(String topicName) {
		return properties().getBoolean("mqtt.topic2consume."+ topicName + ".shouldsave", true);
	}


	public long getConnRetryDelay() {
		return properties().getLong("mqtt.connection.retry.delay", 300000);
	}


	public long getConnRetryInterval() {
		return properties().getLong("mqtt.connection.retry.interval", 300000);
	}
	
	public long getConnRefreshInterval() {
		return properties().getLong("mqtt.connection.refresh.interval", 3600000);
	}
	
	
	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(this.getBrokerUrl(), this.getClientId(), this.getTopic2Consume().split(","));
		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(1);
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {
			public void handleMessage(Message<?> message) throws MessagingException {
				System.out.println(message.getPayload());
			}
		};
	}

	@Bean
	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setServerURIs(this.getBrokerUrl().split(","));
		if (this.getPrincipal() != null) factory.setUserName(this.getPrincipal());
		if (this.getSecret() != null) factory.setPassword(this.getSecret());
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(this.getClientId(), mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("testTopic");
		return messageHandler;
	}
	
	private String getClientId() {
		if (mqttClietnId == null) {
			mqttClietnId = "facesix-" + HostUtil.hostname() + "-"  
							+ System.getProperty("fs.app","app") + "-"
							+ System.getProperty("fs.env","env") + "-"
							+ System.getProperty("fs.node","node") + "-"
							+ System.getProperty("fs.cluster","cluster");
		}
		return this.mqttClietnId;
	}


	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannelAsync")
	public MessageHandler mqttOutboundAsync() {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(this.getClientId(), mqttClientFactory());
		messageHandler.setAsync(true);
		messageHandler.setDefaultTopic("testTopic");
		return messageHandler;
	}

	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
	public interface MQTTDefaultGateway {
		void sendToMqtt(String data);
	}
	
	@MessagingGateway(defaultRequestChannel = "mqttOutboundChannelAsync")
	public interface MQTTDefaultAsyncGateway {
		void sendToMqtt(String data);
	}

	
}