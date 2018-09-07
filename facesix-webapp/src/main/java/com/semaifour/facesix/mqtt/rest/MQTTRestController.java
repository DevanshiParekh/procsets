package com.semaifour.facesix.mqtt.rest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.print.DocFlavor.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.mqtt.MqttPubSub;
import com.semaifour.facesix.mqtt.MqttConfiguration.MQTTDefaultAsyncGateway;
import com.semaifour.facesix.mqtt.MqttConfiguration.MQTTDefaultGateway;
import com.semaifour.facesix.web.WebController;

/**
 * 
 * MQTTRestController interestingly confusing Rest Handler. It exposes REST API for posting messages to default topic hosted by the default MQTT broker.
 * 
 * It also exposes REST APIs for clients, that are currently posting to MQTT broker, to fallback on by sending the same MQTT messages strings to facesix via HTTP.
 * 
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/mqtt")
public class MQTTRestController extends WebController {

	private static Logger LOG = LoggerFactory.getLogger(MQTTRestController.class);

	//@Autowired
	//TODO
	MQTTDefaultGateway mqttDefaultGateway;
	
	//@Autowired
	//TODO
	MQTTDefaultAsyncGateway mqttDefaultAsyncGateway;

	@Autowired
	MqttPubSub mqttPubSub;
	
	/**
	 * Publishes given message to the default topic gateway synchronously
	 *  
	 * @param message
	 * @return 
	 */
	@PostMapping("/publish")
	public Restponse<String> publish(@RequestBody String message) {
		try {
			mqttDefaultGateway.sendToMqtt(message);
			return new Restponse<String>(true, 200, "published");
		} catch (Exception e) {
			return new Restponse<String>(false, 500, e.getMessage());
		}
	}
	
	/**
	 * 
	 * Publishes given message to the default topic gateway asynchronously 
	 * 
	 * @param message
	 * @return
	 */
	@PostMapping("/publish/async")
	public Restponse<String> publishAsync(@RequestBody String message) {
		try {
			mqttDefaultGateway.sendToMqtt(message);
			return new Restponse<String>(true, 100, "published");
		} catch (Exception e) {
			return new Restponse<String>(false, 500, e.getMessage());
		}
	}
	
	/**
	 * 
	 * Receives message the given message at the give topic as if it's sent via mqtt
	 * 
	 * @param message
	 * @return
	 */
	@PostMapping("/post/**")
	public Restponse<String> topic(HttpServletRequest request, HttpServletResponse response, @RequestBody String message) {
		String topic = request.getRequestURI().substring(request.getRequestURI().indexOf("/post/") + 6);
		try {
			message = URLDecoder.decode(message, "UTF-8");
			mqttPubSub.getTopicMessageRouter().messageArrived(topic, message);
			return new Restponse<String>("SUCCESS");
		} catch (Exception e) {
			LOG.warn("error occurred while posting to topic: {}", topic, e);
			return new Restponse<String>(false, 500, Arrays.toString(e.getStackTrace()));
		}
	}
	
}
