package com.semaifour.facesix.beacon.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutData;
import com.semaifour.facesix.beacon.finder.geo.GeoFinderLayoutDataService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.jni.GeoServiceJniHandler;
import com.semaifour.facesix.jni.bean.Coordinate;
import com.semaifour.facesix.jni.bean.GeoPoint;
import com.semaifour.facesix.jni.bean.Pixel;
import com.semaifour.facesix.mqtt.MqttPubSub;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RequestMapping("/rest/beacon/geo")
@RestController
public class GeoFinderRestController {
	
	private static String classname = GeoFinderRestController.class.getName();
	
	static Logger LOG = LoggerFactory.getLogger(classname);
	
	@Autowired
	private MqttPubSub publisher;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private GeoFinderLayoutDataService geoService;
	
	@Value("${mqtt.topic2publish}")
	private String topicName;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	protected CCC _CCC;
	
	@Autowired
    private GeoServiceJniHandler geoServiceJniHandler;
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	BeaconDeviceService beaconDeviceService;
	
	@Autowired
	CustomerService customerservice;
	
	@Autowired
	CustomerUtils customerUtils;
			
	private static final String PLOT = "plot";
	
	@RequestMapping(value = "/byspid", method = RequestMethod.GET)
	public @ResponseBody GeoFinderLayoutData findByspid(@RequestParam("spid") String spid) {
		return geoService.getSavedGeoLayoutDataBySpid(spid);
	}
	
	@RequestMapping(value = "/byuid", method = RequestMethod.GET)
	public @ResponseBody GeoFinderLayoutData findByUid(@RequestParam("uid") String uid) {
		return geoService.findByUid(uid);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<GeoFinderLayoutData> list() {
		return geoService.findAll();
	}
	
	@RequestMapping(value = "/plot", method = RequestMethod.POST, consumes="application/json")
	public String plotCorners(@RequestBody String data,
							  @RequestParam(value = "spid", required = true) String spid,
							  @RequestParam(value = "uid", required = false) String uid,
							  HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException{	
		
		GeoFinderLayoutData obj = null; 
		
		int bUpdate 			= 1;
		String rotationangel 	= "0";
		String inputFilepath 	= "";
		String result 			= "";
		double rotation 		= 0.0d;
		String cid 				= "";
		String sid 				= "";
		int width 				= 0;
		int height 				= 0;
		org.json.JSONArray corr = null;
		List<GeoPoint> list 	= new ArrayList<>();
		boolean enablelog       = false;
		
		Portion portion = portionService.findById(spid);
		if (portion != null) {
			inputFilepath = portion.getJNIFilepath();
			cid = portion.getCid();
			sid = portion.getSiteId();
			width = portion.getWidth();
			height = portion.getHeight();
		}
		
		Customer cx = customerservice.findById(cid);
		if(cx.getLogs()!=null && cx.getLogs().equals("true")){
			enablelog=true;
		}
		obj = geoService.getSavedGeoLayoutDataBySpid(spid);
		
		if(obj == null){
			//LOG.info("object not found");
			customerUtils.logs(enablelog, classname, "object not found");
			obj = new GeoFinderLayoutData();
		} else {
			bUpdate = 1;
		}
		
		customerUtils.logs(enablelog, classname,"JSON Post===> "+data);
		customerUtils.logs(enablelog, classname," spid ===> " + spid + " sid " +sid +" cid " +cid + " uid " +uid);
		 		
		 org.json.JSONObject myObject = new org.json.JSONObject(data);
		
		 
		if (myObject != null) {
		    corr = myObject.getJSONArray("latlng");
			list = getGeoPoints(corr);
		}
		customerUtils.logs(enablelog, classname," geopoints array  ===> " + corr);
		customerUtils.logs(enablelog, classname," geopoints list  ===> " + list);
		
		net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(data);
		
		if (template.get("rotation") != null) {
			rotationangel = template.get("rotation").toString();
			rotation = Double.valueOf(rotationangel);
		}
		
		net.sf.json.JSONObject fgJson = net.sf.json.JSONObject.fromObject(data);
		fgJson.put("width", String.valueOf(width));
		fgJson.put("height", String.valueOf(height));

		//LOG.info(" fgJson  " + fgJson);
		customerUtils.logs(enablelog, classname," inputFilepath" + inputFilepath + " img width  " + width + " imgheight " + height);				

		//LOG.info(" geopints===> " + jsonArray);
		//LOG.info(" geopoints list  ===> " + list);
		//LOG.info(" rotationangel===> " +rotationangel);
		
		obj.setUid(uid);
		obj.setCid(cid);
		obj.setSid(sid);
		obj.setSpid(spid);
		obj.setRotationangel(rotationangel);
		obj.setFgJson(fgJson.toString());
		obj.setGeoPoints(corr.toString());
		obj.setGeoPointslist(list);
		obj.setType(PLOT);
		
		if (bUpdate == 1) {
			customerUtils.logs(enablelog, classname,"bUpdate===> " +bUpdate);
			obj.setModifiedBy("cloud");
			obj.setModifiedOn(new Date(System.currentTimeMillis()));
		}
		
		obj=geoService.save(obj);

		customerUtils.logs(enablelog, classname,"obj===> " +obj.toString());
		
		
		try {
			
			customerUtils.logs(enablelog, classname,"########### POST  JNI >>>>>>>>>>>>>>> " +obj.getGeoPointslist());
			
			GeoPoint[] points = obj.getGeoPointslist().toArray(new GeoPoint[0]);
			Path outputFilePath = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), ("geoout"+UUID.randomUUID().toString() + ".tif"));
			
			//inputFilepath = "/home/qubercomm/Desktop/plainfloor.tif";
			//String outputFilePath = "/home/qubercomm/Desktop/" + UUID.randomUUID().toString() + ".tif";

			customerUtils.logs(enablelog, classname," inputFilepath " + inputFilepath);
			customerUtils.logs(enablelog, classname," outputFilePath " + outputFilePath);
			customerUtils.logs(enablelog, classname," points.length " + points.length);

			Path path = Paths.get(inputFilepath);
			
			if (Files.exists(path)) {
				if (geoServiceJniHandler != null) {
					result = geoServiceJniHandler.doGeoReference(inputFilepath, outputFilePath.toString(), points, points.length,rotation);
					customerUtils.logs(enablelog, classname," JNI PlotCorners result " +result);
					if (result != null && result != "") {
						net.sf.json.JSONObject resulStatus = net.sf.json.JSONObject.fromObject(result);
						String jsonResult = (String)resulStatus.get("status");
						customerUtils.logs(enablelog, classname," *** plotCorners JNI Response Status ****" +jsonResult);
						if (jsonResult.equalsIgnoreCase("success")) {
							obj.setOutputFilePath(outputFilePath.toString());
							obj.setGeoresult(result);
							geoService.save(obj);
						}						
					}					
				}

			}else {
				customerUtils.logs(enablelog, classname," ***  No such file or directory.****");
			}		
			customerUtils.logs(enablelog, classname,"*****Done ******* ");
			customerUtils.logs(enablelog, classname,"result ************ " +result);

		} catch (Exception e) {
			e.printStackTrace();
			customerUtils.logs(enablelog, classname,"while JNI Processing error " + e);
		}
		
		
		return spid;
	}
	
	public void Pixel2Coordinate(String spid, String uid, String xposition, String yposition) {
		
		int count  = 1;
		String filePath =  "";
		String result = "";
		String rotationangel= "0";
		boolean enablelog = false;
		Customer cx = null;
		String  cid 	= null;
		
		GeoFinderLayoutData obj = null; 
		obj = geoService.getSavedGeoLayoutDataBySpid(spid);
		
		if(obj != null){
			cid = obj.getCid();
			cx = customerservice.findById(cid);
			if (cx.getLogs() != null && cx.getLogs().equals("true")) {
				enablelog = true;
			}
		}
		
		Pixel[] pixel = {new Pixel(convertDouble(xposition), convertDouble(yposition))}; 
		
		customerUtils.logs(enablelog, classname," ********* Entry pixcelToCoordination ******* ");
		customerUtils.logs(enablelog, classname," device xposition " + xposition + " device yposition " + yposition);

		try {
			if (obj != null) {
				filePath = obj.getOutputFilePath();
				rotationangel = obj.getRotationangel();
				
				Path path = Paths.get(filePath);
				
				if (Files.exists(path)) {
					
					if (geoServiceJniHandler != null) {
						result = geoServiceJniHandler.pixelToCoordinate(filePath, pixel, count, Double.valueOf(rotationangel));
						
						customerUtils.logs(enablelog, classname," Pixel2Coordinate Result " +result);
					
						if (result != null && result != "") {
								
							net.sf.json.JSONObject resulStatus  = net.sf.json.JSONObject.fromObject(result);			
							String jsonResult 					= (String)resulStatus.get("status");
							
							customerUtils.logs(enablelog, classname," *** Pixel2Coordinate JNI Response Status ****" +jsonResult);
							
							if (jsonResult.equalsIgnoreCase("success")) {
								
								org.json.JSONObject myObject = new org.json.JSONObject(result);
								org.json.JSONArray corr 	 = myObject.getJSONArray("result");
								org.json.JSONObject ob 		 = corr.getJSONObject(0);

								Double latitude  			 = BigDecimal.valueOf(ob.getDouble("latitude")).doubleValue();
								Double longitude 			 = BigDecimal.valueOf(ob.getDouble("longitude")).doubleValue();
								Double 		x1 			     = ob.getDouble("x");
								Double 		y1 			     = ob.getDouble("y");
								
								String lat 	= String.valueOf(latitude);
								String lng 	= String.valueOf(longitude);
								String x 	= String.valueOf(x1);
								String y 	= String.valueOf(y1);
								
								customerUtils.logs(enablelog, classname," lat " + lat + " lng " + lng + " x " + x + " y " + y);
								
								JSONObject pixcelObj = new JSONObject();
								JSONArray  pixArray  = new JSONArray();
								JSONObject pixcel    = new JSONObject();
								
								pixcel.put("x", x);
								pixcel.put("y", y);
								pixcel.put("latitude", lat);
								pixcel.put("longitude",lng);
								
								pixArray.add(pixcel);
								pixcelObj.put("result", pixArray);
								
								customerUtils.logs(enablelog, classname," ***pixcel JSON  " +pixcelObj.toString());
								
								obj.setUid(uid);
								obj.setPixelresult(pixcelObj.toString());
								obj.setModifiedBy("cloud");
								obj.setModifiedOn(new Date(System.currentTimeMillis()));
								obj = geoService.save(obj);
	
								BeaconDevice device = beaconDeviceService.findOneByUid(uid);							
								if (device != null) {
									device.setGeopoints(obj.getGeoPoints());
									device.setGeoresult(obj.getGeoresult());
									device.setPixelresult(pixcelObj.toString());
									device.setModifiedBy("cloud");
									device.setModifiedOn(new Date(System.currentTimeMillis()));
									device=beaconDeviceService.save(device, true);
								}

							}
						}						
					}

				}else {
					customerUtils.logs(enablelog, classname," *** No such file or directory.****");
				}
			}
			
			customerUtils.logs(enablelog, classname," ********* PixcelToCoordination DONE********  ");
			customerUtils.logs(enablelog, classname," result  " + result);
			customerUtils.logs(enablelog, classname," filePath " + filePath);
			customerUtils.logs(enablelog, classname," xposition " + xposition + " yposition " + yposition);
			customerUtils.logs(enablelog, classname," rotationangel" +rotationangel);
		} catch(Exception e) {
			e.printStackTrace();
			customerUtils.logs(enablelog, classname,"while Pixel2Coordinate update error " +e);
		}
		

	}

	public String  Coordinate2Pixel(String spid, List<Coordinate> coordinates) {
		
		GeoFinderLayoutData obj = null; 
		obj = geoService.getSavedGeoLayoutDataBySpid(spid);
		String cid = null;
		boolean enablelog = false;
		if (obj != null) {
			cid = obj.getCid();
			Customer cx = customerservice.findById(cid);
			if (cx.getLogs() != null && cx.getLogs().equals("true")) {
				enablelog = true;
			}
		}
		int count = 0;
		if (coordinates != null) {
			count = coordinates.size();
//			customerUtils.logs(enablelog, classname," Coordinate2Pixel coordinate " + coordinates.toString());
		}
		String filePath =  "";
		String result = "";
		String rotationangel= "0";
		
		try {
			//Coordinate[] coordinate = {new Coordinate(latitude, longitude)}; 
			Coordinate[] coordinate =  coordinates.toArray(new Coordinate[count]); 
			
//			customerUtils.logs(enablelog, classname," ********* Entry Coordinate2Pixel ******* ");
			//LOG.info(" --------Coordinate2Pixel ****** " + coordinates.toString());

			if (obj != null) {
				filePath = obj.getOutputFilePath();
				rotationangel = obj.getRotationangel();
				
				//LOG.info(" Coordinate2Pixel intput file path " +filePath);
				//LOG.info(" rotationangel" +rotationangel);
				
				Path path = Paths.get(filePath);
				if (Files.exists(path)) {
					if (geoServiceJniHandler != null) {
						result = geoServiceJniHandler.coordinateToPixel(filePath, coordinate, count, Double.valueOf(rotationangel));
						//LOG.info(" *** Coordinate2Pixel JNI Response Status ****" +result);
					    return result;
					}
				} else {
					customerUtils.logs(enablelog, classname," *** No such file or directory.****");
				}
			}
		} catch (Exception e) {
			customerUtils.logs(enablelog, classname,"while Coordinate2Pixel update error " +e);
		}
		return null;
	}


	public String  Coordinate2PixelPEER_TAG(String spid, double latitude, double longitude) {
		
		int count 		 	= 1;
		String filePath 	=  "";
		String result 		= "";
		String rotationangel= "0";
		
		try {
			Coordinate[] coordinate = {new Coordinate(latitude, longitude)}; 
			
			//LOG.info(" ********* Entry Coordinate2Pixel ******* ");
			//LOG.info(" Coordinate2Pixel latitude " + latitude + " Coordinate2Pixel longitude " + longitude);

			GeoFinderLayoutData obj = null; 
			obj = geoService.getSavedGeoLayoutDataBySpid(spid);

			if (obj != null) {
				
				filePath 		= obj.getOutputFilePath();
				rotationangel   = obj.getRotationangel();
				
				Path path = Paths.get(filePath);
				
				//LOG.info(" Coordinate2Pixel intput file path " +filePath);
				//LOG.info(" rotationangel" +rotationangel);
				
				if (Files.exists(path)) {
					
					if (geoServiceJniHandler != null) {
						result = geoServiceJniHandler.coordinateToPixel(filePath, coordinate, count,Double.valueOf(rotationangel));
						//LOG.info(" *** Coordinate2Pixel JNI Response Status ****" + result);
						return result;
					}
				} else {
					LOG.info(" *** No such file or directory.****");
				}
			}
		} catch (Exception e) {
			LOG.info("while Coordinate2Pixel update error " +e);
		}
		return null;
	}
	
	private List<GeoPoint> getGeoPoints(org.json.JSONArray jsonArray) {
		
		ArrayList<GeoPoint> list= new ArrayList<GeoPoint>();
		
		if (jsonArray != null) {
			int len = jsonArray.length();
			for (int i = 0; i < len; i++) {
				org.json.JSONObject object = (org.json.JSONObject) jsonArray.get(i);
				if (object != null) {
					
					Double latitude  	 = BigDecimal.valueOf(object.getDouble("latitude")).doubleValue();
					Double longitude 	 = BigDecimal.valueOf(object.getDouble("longitude")).doubleValue();
					Double 		x1 	     = object.getDouble("x");
					Double 		y1       = object.getDouble("y");
					
					list.add(new GeoPoint(x1,y1, latitude,longitude, ""));
				}
				
			}
		}
		return list;
	}

	public Double convertDouble(Object obj) {
		return Double.valueOf(String.valueOf(obj));
	}
		
	@RequestMapping(value = "/api/geo/plot/status/{spid}", method = RequestMethod.GET)
	public String getStatusPlotCornerOperation(HttpServletRequest request, @PathVariable String spid){	
		LOG.info("Get status of the geo tiff conversion process...");
		return getOperationStatusOfPortion(spid);
	}
	
	@RequestMapping(value = "/plot/{spid}/{sid}/{type}", method = RequestMethod.GET, produces="application/json")
	public String retrievePlotLayoutData(HttpServletRequest request, @PathVariable String spid,@PathVariable String sid,
			@PathVariable String type)
			throws JsonParseException, JsonMappingException, IOException{
		
		String output = "";
		double lat = 0;
		double lgt = 0;
		int width 	= 0;
		int height 	= 0;
		
		String cid  = null;
		Customer cx = null;
		boolean enablelog = false;
		
		GeoFinderLayoutData obj = geoService.getSavedGeoLayoutDataBySpid(spid);
		Portion portion 		= portionService.findById(spid);
		
		if (portion != null) {
			width 	= portion.getWidth();
			height 	= portion.getHeight();
			cid 	= portion.getCid();
			cx 		= customerservice.findById(cid);
			
			if (cx.getLogs() != null && cx.getLogs().equals("true")) {
				enablelog = true;
			}
		}
		
		if(obj == null){
			
			JSONObject imgDetails 		= new JSONObject();			
			JSONObject imgObjDetails 	= new JSONObject();
			JSONArray   imgArray  		= new JSONArray();
	
				customerUtils.logs(enablelog, classname, "Request type : "+type);
				
				imgDetails.put("mapzoom", "19");
				imgDetails.put("opacity", "0.7");
				imgDetails.put("zoom", 	  "50");
				imgDetails.put("rotation", "1");
				
				Site site = siteService.findById(sid);
				if (site != null) {
					lat = site.getLatitude();
					lgt = site.getLongitude();
				}
				
				imgObjDetails.put("latitude", String.valueOf(lat));//"11.67120641379852"
				imgObjDetails.put("longitude", String.valueOf(lgt)); //"78.14361798761638"
				imgArray.add(imgObjDetails);
				
				
				imgDetails.put("latlng", imgArray);
				imgDetails.put("width",  width);
				imgDetails.put("height", height);
				output = imgDetails.toString();
				customerUtils.logs(enablelog, classname,"object not found");
		}else {
			output = obj.getFgJson();
		}
		customerUtils.logs(enablelog, classname,"JSON Get===> "+output);
		return output;
	}
	
	private String getOperationStatusOfPortion(String spid){
		Portion portion = portionService.findById(spid);
		return portion.getPlotOperationStatus();
	}

	

}
