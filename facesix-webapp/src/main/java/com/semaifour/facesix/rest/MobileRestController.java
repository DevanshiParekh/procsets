package com.semaifour.facesix.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.shiro.codec.Base64;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.geo.data.Poi;
import com.semaifour.facesix.geo.data.PoiRequest;
import com.semaifour.facesix.geo.data.PoiService;

import net.sf.json.JSONArray;

/**
 * Rest Controller for managing poi search, save and list etc.
 * 
 * @author jay
 *
 */
@RestController
@RequestMapping(path = "/api/geo")
public class MobileRestController {

	static Logger LOG = LoggerFactory.getLogger(MobileRestController.class.getName());

	final String SITE_IDENT_REQUEST = "site_ident_request";
	final String SITE_IDENT_RESPONSE = "site_ident_response";
	final String FLOOR_MAP_REQUEST = "floor_map_request";
	final String FLOOR_MAP_RESPONSE = "floor_map_response";
	final String POI_REQUEST = "point_of_interest_request";
	final String POI_RESPONSE = "point_of_interest_response";
	final String OP_CODE = "opcode";
	final String SID = "sid";
	final String SPID = "spid";
	final String NAME = "name";

	@Autowired
	private PoiService poiService;

	@Autowired
	private BeaconService beaconService;

	@Autowired
	private PortionService portionService;

	@Autowired
	private SiteService siteService;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@RequestMapping(value = "/mobapi", method = RequestMethod.GET)
	public Restponse<String> search(@RequestParam("request") String request)
			throws JsonParseException, JsonMappingException, IOException {
		// decode base64 encoded param
		String json = Base64.decodeToString(request);
		String response = "";
		ObjectMapper mapper = new ObjectMapper();
		PoiRequest requestObject = mapper.readValue(json, PoiRequest.class);

		switch (requestObject.getOpcode()) {
		case SITE_IDENT_REQUEST:
			response = handleSiteIdentRequest(requestObject);
			break;
		case FLOOR_MAP_REQUEST:
			response = handleFloorMapRequest(requestObject);
			break;
		case POI_REQUEST:
			response = handlePoiRequest(requestObject);
			break;
		}

		return new Restponse<String>(response);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String handleSiteIdentRequest(PoiRequest obj) {

		if (obj.getUuid().equals("") && obj.getUuid().equals("")) {
			LOG.info("Invalid inputs - uuid should not be empty");
			return "";
		}

		LOG.info("request_object fields : " + obj.getUuid() + " - " + obj.getMajor() + " - " + obj.getMinor());
		// Beacon beacon = beaconService.getSavedBeaconByUuid(obj.getUuid());
		Beacon beacon = beaconService.getSavedBeaconByUuidAndMajorAndMinor(obj.getUuid().trim(),
				Integer.parseInt(obj.getMajor()), Integer.parseInt(obj.getMinor()));

		LOG.info("Beacon" + beacon);

		if (beacon != null) {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put(OP_CODE, SITE_IDENT_RESPONSE);
			jsonObject.put(SID, beacon.getSid());
			jsonObject.put(SPID, beacon.getSpid());
			jsonObject.put("beacon-name", beacon.getName());

			Site site = siteService.findById(beacon.getSid());
			if (site != null) {
				jsonObject.put("site-name", site.getName());
			}

			Portion portion = portionService.findById(beacon.getSpid());
			if (portion != null) {
				jsonObject.put("portion-name", portion.getName());
			}

			return jsonObject.toJSONString();
		}

		return "";
	}

	/**
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String handleFloorMapRequest(PoiRequest obj) {
		String response = "";

		if (obj.getSid().equals("") && obj.getSid().equals("")) {
			LOG.info("Invalid inputs - either sid or spid should be present");
			return response;
		}

		JSONObject json = new JSONObject();

		// fetch all the portions if spid not present
		if (obj.getSpid() != null && !obj.getSpid().isEmpty()) {
			LOG.info("Fetch portions using spid ");
			// fetch portion
			Portion portion = portionService.findById(obj.getSpid());

			json.put(OP_CODE, FLOOR_MAP_RESPONSE);
			json.put(SPID, obj.getSpid());
			if (obj.getSid() != null) {
				json.put(SID, obj.getSid());
			}

			JSONObject jobject = new JSONObject();
			jobject.put("spid", portion.getId());
			jobject.put("portion-name", portion.getName());
			jobject.put("floor-map-url", portion.getMapUrl());

			JSONArray jsonArray = new JSONArray();
			jsonArray.add(jobject);
			json.put("floor-list", jsonArray);
			LOG.info(response);

		} else {
			LOG.info("Fetch portions using sid ");
			// fetch portions
			List<Portion> portions = portionService.findBySiteId(obj.getSid());

			json.put(OP_CODE, FLOOR_MAP_RESPONSE);
			json.put(SID, obj.getSid());

			LOG.info("Total portions found : " + portions.size());
			JSONArray jsonArray = new JSONArray();
			for (Portion p : portions) {
				JSONObject jobject = new JSONObject();
				jobject.put("spid", p.getId());
				jobject.put("portion-name", p.getName());
				jobject.put("floor-map-url", p.getMapUrl());
				jsonArray.add(jobject);
			}

			json.put("floor-list", jsonArray);

			LOG.info(response);
		}
		response = json.toJSONString();
		return response;
	}

	/**
	 * 
	 * 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public String handlePoiRequest(PoiRequest obj) {
		String response = "";

		JSONObject object = new JSONObject();

		if (obj.getSid().equals("") && obj.getSid().equals("")) {
			LOG.info("Invalid inputs - either sid or spid should be present");
			return response;
		}

		if (obj.getSpid() != null && !obj.getSpid().isEmpty()) {
			LOG.info("Fetch pois using spid ");

			List<Poi> pois = poiService.findSavedPoiBySpid(obj.getSpid());

			object.put(OP_CODE, POI_RESPONSE);
			object.put(SPID, obj.getSpid());
			if (obj.getSid() != null) {
				object.put(SID, obj.getSid());
			}

			JSONArray array = new JSONArray();
			JSONObject arrayElementOne = new JSONObject();
			arrayElementOne.put("spid", obj.getSpid());
			JSONArray arrayElementOneArray = new JSONArray();

			LOG.info("Total pois found : " + pois.size());

			for (Poi p : pois) {
				JSONObject arrayElementOneArrayElementOne = new JSONObject();
				arrayElementOneArrayElementOne.put("title", p.getName());
				arrayElementOneArrayElementOne.put("description", p.getDescription());
				arrayElementOneArrayElementOne.put("icon-url", p.getIconUrl());
				arrayElementOneArrayElementOne.put("latitude", p.getLatitude());
				arrayElementOneArrayElementOne.put("longitude", p.getLongitude());
				arrayElementOneArray.add(arrayElementOneArrayElementOne);
			}

			arrayElementOne.put("point-of-interest-list", arrayElementOneArray);
			array.add(arrayElementOne);
			object.put("portion-list", array);
		} else {
			LOG.info("Fetch portions using sid ");

			List<Portion> portions = portionService.findBySiteId(obj.getSid());
			object.put(OP_CODE, POI_RESPONSE);
			object.put(SPID, obj.getSpid());
			if (obj.getSid() != null) {
				object.put(SID, obj.getSid());
			}

			JSONArray array = new JSONArray();

			for (Portion por : portions) {

				JSONObject arrayElementOne = new JSONObject();
				arrayElementOne.put("spid", por.getId());
				JSONArray arrayElementOneArray = new JSONArray();

				List<Poi> pois = poiService.findSavedPoiBySpid(por.getId());
				LOG.info("Total pois found : " + pois.size());

				for (Poi p : pois) {
					JSONObject arrayElementOneArrayElementOne = new JSONObject();
					arrayElementOneArrayElementOne.put("title", p.getName());
					arrayElementOneArrayElementOne.put("description", p.getDescription());
					arrayElementOneArrayElementOne.put("icon-url", p.getIconUrl());
					arrayElementOneArrayElementOne.put("latitude", p.getLatitude());
					arrayElementOneArrayElementOne.put("longitude", p.getLongitude());
					arrayElementOneArray.add(arrayElementOneArrayElementOne);
				}

				arrayElementOne.put("point-of-interest-list", arrayElementOneArray);
				array.add(arrayElementOne);
			}

			object.put("portion-list", array);

		}

		response = object.toJSONString();
		return response;
	}
}
