package com.semaifour.facesix.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.geo.data.GeoLayoutData;
import com.semaifour.facesix.geo.data.GeoLayoutDataService;
import com.semaifour.facesix.geo.data.Poi;
import com.semaifour.facesix.geo.data.PoiService;
import com.semaifour.facesix.mqtt.MqttPubSub;
import com.semaifour.facesix.mqtt.Payload;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.SessionUtil;

/**
 * 
 * @author jay
 *
 */
@RestController
public class GeoServiceRestController {
	
	static Logger LOG = LoggerFactory.getLogger(GeoServiceRestController.class.getName());
	
	@Autowired
	private MqttPubSub publisher;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private GeoLayoutDataService geoService;
	
	@Value("${mqtt.topic2publish}")
	private String topicName;
	
	@Autowired
	private PoiService poiService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	protected CCC _CCC;
	
	private static final String PLOT_CORNERS_OPCODE = "gen-geotiff-request";
	private static final String QUBERCOMM_ADMIN_SERVICE = "qubercomm-admin-service";	
	private static final String GEO_REF_SERVICE = "geo-reference-service";
	private static final String PLOT = "plot";
	private static final String POI = "poi";
	
	@RequestMapping(value = "/api/geo/plot/{spid}", method = RequestMethod.POST, consumes="application/json")
	public String plotCorners(HttpServletRequest request, @PathVariable String spid, 
			@RequestBody String data) throws JsonParseException, JsonMappingException, IOException{	
		
		GeoLayoutData obj = null; 
		obj = geoService.findBySpidAndType(spid, PLOT);
		if(obj == null){
			LOG.info("object not found");
			obj = new GeoLayoutData();			
		}				
		ObjectMapper mapper = new ObjectMapper();		
		GeoLayoutData temp = mapper.readValue(data, GeoLayoutData.class);		
		obj.setSpid(spid);
		obj.setType(temp.getType());
		obj.setFgJson(temp.getFgJson());
		obj.setGeoPoints(temp.getGeoPoints());
		obj.setGmapMarkers(temp.getGmapMarkers());
		obj.setPixels(temp.getPixels());
		geoService.save(obj);		
		
		// construct fully qualified path
	    String scheme = request.getScheme();             // http
	    String serverName = request.getServerName();     // hostname
	    int serverPort = request.getServerPort();        // port_no
	    
	    // construct a file download url for the geo service
	    String filepath = scheme+ "://"+serverName +":"+ Integer.toString(serverPort)+"/facesix/web/site/portion/planfile?spid="+spid;
	    String sessionid= request.getSession().getId();
		LOG.info("path: "+filepath);
	    LOG.info("Session ID"+sessionid);
		
		// construct message
		String message = 
				"{\"cid\":\""+spid+"\",\"sid\":\""+spid+"\", \"spid\":\""+spid+"\",\"srcimagepath\":\""+filepath+"\",\"sessionid\":\""+sessionid+"\","
				+ "\"geopoints\":"+obj.getGeoPoints()
				+ "}";		
		// construct a payload 
		Payload payload = new Payload(PLOT_CORNERS_OPCODE, QUBERCOMM_ADMIN_SERVICE, GEO_REF_SERVICE, message);				
		// publish over topic
		boolean status = publisher.publish(payload, topicName);		
		if(status){
			LOG.info("Successsfully published !");
		}		
		// set status of the corners plotting and geotiff conversion status as "in-progress"
		setOperationStatusOfPortion(spid, "in-progress");	
		// generate response url to know the status of the operation
				
		return spid;
	}
	
	@RequestMapping(value = "/api/geo/plot/status/{spid}", method = RequestMethod.GET)
	public String getStatusPlotCornerOperation(HttpServletRequest request, @PathVariable String spid){	
		LOG.info("Get status of the geo tiff conversion process...");
		return getOperationStatusOfPortion(spid);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/geo/plot/{spid}/{type}", method = RequestMethod.GET, produces="application/json")
	public String retrievePlotLayoutData(HttpServletRequest request, @PathVariable String spid,
			@PathVariable String type)
			throws JsonParseException, JsonMappingException, IOException{
		
		LOG.info("Request type : "+type);
		String output = "";
		GeoLayoutData obj = geoService.findBySpidAndType(spid, PLOT);
		if(obj == null){
			LOG.info("object not found");
			return "";
		}
		
		if(type.equals("canvas")){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("fg_json", obj.getFgJson());
			jsonObject.put("pixels", obj.getPixels());			
			return jsonObject.toJSONString();
		} else if(type.equals("map")){
			LOG.info(obj.getGmapMarkers());
			return obj.getGmapMarkers();
		}
		
		return output;
	}
	
	@RequestMapping(value = "/api/geo/poi/{spid}", method = RequestMethod.POST, consumes="application/json")
	public String plotPois(HttpServletRequest request, @PathVariable String spid, 
			@RequestBody String data) throws JsonParseException, JsonMappingException, IOException{		
		LOG.info("store poi info");
		GeoLayoutData obj = null; 
		obj = geoService.findBySpidAndType(spid, POI);
		if(obj == null){
			LOG.info("object not found");
			obj = new GeoLayoutData();			
		}				
		ObjectMapper mapper = new ObjectMapper();		
		GeoLayoutData temp = mapper.readValue(data, GeoLayoutData.class);		
		obj.setSpid(spid);
		obj.setType(temp.getType());
		obj.setFgJson(temp.getFgJson());
		geoService.save(obj);						
		return spid;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/api/geo/poi/{spid}/{type}", method = RequestMethod.GET, produces="application/json")
	public String retrievePoiLayoutSegment(HttpServletRequest request, @PathVariable String spid,
			@PathVariable String type)
			throws JsonParseException, JsonMappingException, IOException{
		LOG.info("poi segment request");		
		LOG.info("Request type : "+type);
		String output = "";
		GeoLayoutData obj = geoService.findBySpidAndType(spid, POI);
		if(obj == null){
			LOG.info("object not found");
			return "";
		}
		
		if(type.equals("canvas")){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("fg_json", obj.getFgJson());					
			return jsonObject.toJSONString();
		} 		
		return output;
	}

	private String getOperationStatusOfPortion(String spid){
		Portion portion = portionService.findById(spid);
		return portion.getPlotOperationStatus();
	}

	private void setOperationStatusOfPortion(String spid, String msg){
		LOG.info("set status for spid "+spid);
		LOG.info("status message : "+ msg);
		Portion portion = portionService.findById(spid);
		portion.setPlotOperationStatus(msg);;
		portionService.save(portion);
		LOG.info("Successfully stored");
	}
	
	/**
	 * Saves poi
	 * 
	 * @param model
	 * @param newPoi
	 * @return
	 */
	@RequestMapping(value = "/api/geo/poi/save", method = RequestMethod.POST)
	public String save(Map<String, Object> model, @ModelAttribute Poi newPoi,@RequestParam(value="file", required=false) MultipartFile poiFile, 
			HttpServletRequest request, HttpServletResponse response) {
		LOG.info("Request received to store the poi icon");
		Poi oldPoi = null;
		oldPoi = poiService.getSavedPoiById(newPoi.getId());
		//LOG.info("site cid " +site.getCustomerId());
		boolean shouldSave = true;
		if (oldPoi == null) {
			LOG.info("poi not found with");
			oldPoi = new Poi();
			oldPoi.setCreatedOn(new Date());
			oldPoi.setModifiedOn(new Date());
			oldPoi.setCreatedBy(SessionUtil.currentUser(request.getSession()));
			oldPoi.setModifiedBy(oldPoi.getCreatedBy());
			oldPoi.setSpid(newPoi.getSpid());
			oldPoi.setName(newPoi.getName());
			oldPoi.setDescription(newPoi.getDescription());
		} else {
			//check the mac/device id not overwritten
			oldPoi.setName(newPoi.getName());
			oldPoi.setDescription(newPoi.getDescription());
			oldPoi.setModifiedOn(new Date());
			oldPoi.setModifiedBy(SessionUtil.currentUser(request.getSession()));			
		}
		
		newPoi = oldPoi;
		if (shouldSave ) {
			newPoi = poiService.save(newPoi);
			LOG.info("poi saved successfully");
		}
		//LOG.info("site portion Cid " + newfso.getCid() + "UID " + newfso.getUid());
		if(!poiFile.isEmpty() && poiFile.getSize() > 1) {
			try {
				Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), (newPoi.getId() + "_" + poiFile.getOriginalFilename()));
				Files.createDirectories(path.getParent());
				Files.copy(poiFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				newPoi.setFilepath(path.toString());
				newPoi.setIconUrl("/api/geo/poi/poifile?id="+newPoi.getId());
				newPoi = poiService.save(newPoi);
			}  catch (IOException e) {
				LOG.warn("Failed save floor plan file", e);
			}
		}		
		LOG.info("image stored successfully!");
		return newPoi.getId();
	}
	
	
	/**
	 * Returns floor plan file content
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/api/geo/poi/poifile", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getPlanFile(@RequestParam(value = "id", required = true) String id) {
		try {
			Poi poi = poiService.getSavedPoiById(id);
			if (poi != null && poi.getIconUrl() != null) {				
				return ResponseEntity.ok(resourceLoader.getResource("file:" + poi.getFilepath()));				
			}
			LOG.info("FILE PATH " + poi.getIconUrl());
		} catch (Exception e) {
			LOG.warn("Failed to load floor plan for portion :" + id, e);
			return ResponseEntity.notFound().build();
		}		
		return ResponseEntity.notFound().build();
	}

}
