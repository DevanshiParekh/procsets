package com.semaifour.facesix.geo.mqtt.impl;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.mqtt.DefaultMqttMessageReceiver;

/**
 * 
 * @author jay
 *
 */
public class GeoServiceMqttResponseHandler extends DefaultMqttMessageReceiver {

	private static Logger LOG = LoggerFactory
			.getLogger(GeoServiceMqttResponseHandler.class.getName());

	@Autowired
	PortionService portionService;
	// Don't change these constant names //
	private final String GEN_GEOTIFF_RESPONSE = "gen-geotiff-response";
	private final String MARK_POI_RESPONSE = "mark-poi-response";
	private final String SUCCESS = "success";
	private final String FAILURE = "failure";
	// Don't change these constant names //

	public GeoServiceMqttResponseHandler() {
	}

	@Override
	public boolean messageArrived(String topic, MqttMessage message) {
		LOG.info("Received message from topic " + topic);
		return messageArrived(topic, message.toString());
	}

	@Override
	public boolean messageArrived(String topic, String message) {
		LOG.info("Received Message " + message);
		GeoServiceResponse obj = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			obj = mapper.readValue(message, GeoServiceResponse.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (obj != null) {
			switch (obj.getOpcode()) {
			case GEN_GEOTIFF_RESPONSE:
				LOG.info("Geo tiff conversion response");

				Portion portion = portionService().findById(obj.getSpid());
				if (portion == null) {
					LOG.info("unable to find a portion with the id "
							+ obj.getSpid());
				}
				portion.setPlotOperationStatus(obj.getStatus().trim());
				// store mapbox url on success
				if (obj.getStatus().equals(SUCCESS)) {
					portion.setMapUrl(obj.getMapboximagepath());
				}
				portionService().save(portion);

				break;
			case MARK_POI_RESPONSE:
				LOG.info("PoI response");
				break;
			}
		}
		return true;
	}

	public PortionService portionService() {
		if (portionService == null) {
			portionService = Application.context.getBean(PortionService.class);
		}
		if (portionService == null) {
			LOG.warn("Unable to load PortionService, please check");
		}
		return portionService;

	}
}
