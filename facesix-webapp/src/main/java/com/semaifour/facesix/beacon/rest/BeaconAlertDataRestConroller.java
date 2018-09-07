package com.semaifour.facesix.beacon.rest;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconAlertData;
import com.semaifour.facesix.beacon.data.BeaconAlertDataService;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Restponse;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/beaconAlertData")
public class BeaconAlertDataRestConroller extends WebController{
	
	static Logger LOG = LoggerFactory.getLogger(BeaconAlertDataRestConroller.class.getName());
	
	@Autowired
	private BeaconAlertDataService beaconAlertDataService;
	
	@Autowired
	private SiteService siteService;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private BeaconService beaconService;
	
	@Autowired
	private BeaconDeviceService beaconDeviceService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody List<BeaconAlertData> list(@RequestParam(value="cid", required=true) String cid) {	
			return beaconAlertDataService.findByCid(cid);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Restponse<String> save(@RequestBody BeaconAlertData beaconAlertData,
								  HttpServletRequest request, HttpServletResponse response) {

		String ifNewAlertData = "Your alert data has been updated successfully.";
		boolean flag 	   	  = false;
		try {
			if (SessionUtil.isAuthorized(request.getSession())) {
				String cid = beaconAlertData.getCid();
				String place = beaconAlertData.getPlace();
				
				JSONArray tagids = beaconAlertData.getTagids();
				JSONArray placeids = beaconAlertData.getPlaceIds();
				JSONArray placenames = new JSONArray();
				JSONArray tagnames = new JSONArray();
				
				boolean containsAllPlaces = false;
				boolean containsAllTags = false;
				
				if(placeids.size()==1 && placeids.getString(0).equals("all")){
					containsAllPlaces = true;
					placenames.add("all");
				}
				
				if(tagids.size()==1 && tagids.getString(0).equals("all")){
					containsAllTags = true;
					tagnames.add("all");
				}
				
				if (!containsAllPlaces) {
					
					switch (place) {
					case "venue":
						List<Site> venues = siteService.findByIds(placeids);
						for (Site s : venues) {
							placenames.add(s.getUid().toUpperCase());
						}
						break;
					case "floor":
						List<Portion> floors = portionService.findByIds(placeids);
						for (Portion p : floors) {
							placenames.add(p.getUid().toUpperCase());
						}
						break;

					case "location":
						List<BeaconDevice> locations = beaconDeviceService.findByCidTypeAndUids(cid, "receiver",
								placeids);
						for (BeaconDevice bd : locations) {
							placenames.add(bd.getAlias().toUpperCase());
						}
						break;
					}
				}
				
				if(!containsAllTags){
					List<Beacon> beacons = beaconService.findByCidStatusAndMacaddrs(cid,"checkedout",tagids);
					for(Beacon b: beacons){
						tagnames.add(b.getAssignedTo());
					}
				}
				
				beaconAlertData.setTagnames(tagnames);
				beaconAlertData.setPlacenames(placenames);
				beaconAlertData = beaconAlertDataService.save(beaconAlertData);
				
				flag = true;
			} else {
				ifNewAlertData = "Unauthorized User";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return new Restponse<String>(flag, 200, ifNewAlertData);
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Restponse<String> delete(@RequestParam(value="id", required=true) String id,
									HttpServletRequest request, HttpServletResponse response) {
		
		String ifNewAlertData = "Your alert data has been delted successfully.";
		boolean flag 	   	  = false;
		try{
			if(SessionUtil.isAuthorized(request.getSession())){
				BeaconAlertData beaconAlertData = beaconAlertDataService.findById(id);
				if (beaconAlertData != null) {
					beaconAlertDataService.delete(beaconAlertData);
					flag = true;
				}else{
					ifNewAlertData = "Alert does not exist";
				}
			}else{
				ifNewAlertData = "Unauthorized User";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return new Restponse<String>(flag, 200, ifNewAlertData,id);
	}
	
	
	@RequestMapping(value = "/typeBasedTagNames", method = RequestMethod.GET)
	public JSONArray typeBasedTagNames(@RequestParam(value = "cid", required = true) String cid,
								   	   @RequestParam(value = "type", required = true) String type,
								   	   HttpServletRequest request, HttpServletResponse response) {
		try {
			Collection<Beacon> beacon = beaconService.getSavedBeaconByCidTagTypeAndStatus(cid, type, "checkedout");
			JSONArray tagNames = new JSONArray();
			JSONObject json = null;
			if (beacon != null) {
				for (Beacon b : beacon) {
					json = new JSONObject();
					json.put("tagid", b.getMacaddr());
					json.put("name", b.getAssignedTo());
					tagNames.add(json);
				}
				return tagNames;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/inactivityType", method = RequestMethod.GET)
	public JSONArray inactivityType(@RequestParam(value = "cid", required = true) String cid,
								   @RequestParam(value = "inactivityType", required = true) String inactivityType,
								   HttpServletRequest request,HttpServletResponse response){
		JSONArray jsonArray = null;
		
		if(inactivityType.equals("venue")){
			jsonArray = getVenues(cid);
		}else if(inactivityType.equals("floor")){
			jsonArray = getFloors(cid);
		}else if(inactivityType.equals("location")){
			jsonArray = getLocation(cid);
		}
		
		return jsonArray;
	}

	private JSONArray getVenues(String cid) {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = null;
		String id = null;
		String name = null;
		List<Site> sitelist= siteService.findByCustomerId(cid);
		for(Site s : sitelist){
			id = s.getId();
			name = s.getUid().toUpperCase();
			json = new JSONObject();
			json.put("id", id);
			json.put("name", name);
			jsonArray.add(json);
		}
		return jsonArray;
	}
	
	private JSONArray getFloors(String cid) {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = null;
		String id = null;
		String name = null;
		List<Portion> portionlist= portionService.findByCid(cid);
		for(Portion p : portionlist){
			id = p.getId();
			name = p.getUid().toUpperCase();
			json = new JSONObject();
			json.put("id", id);
			json.put("name", name);
			jsonArray.add(json);
		}
		return jsonArray;
	}
	
	private JSONArray getLocation(String cid) {
		JSONArray jsonArray = new JSONArray();
		JSONObject json = null;
		String id = null;
		String name = null;
		List<BeaconDevice> beacondeviceList = beaconDeviceService.findByCidAndType(cid, "receiver");
		for(BeaconDevice bd : beacondeviceList){
			id = bd.getUid();
			name = bd.getAlias().toUpperCase();
			json = new JSONObject();
			json.put("id", id);
			json.put("name", name);
			jsonArray.add(json);
		}
		return jsonArray;
	}

}
