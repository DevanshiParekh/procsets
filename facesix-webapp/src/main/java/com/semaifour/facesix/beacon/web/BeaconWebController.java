package com.semaifour.facesix.beacon.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.beacon.TimeStats;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.beacon.rest.BeaconRestController;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * Configuration Controller for the webapp
 * 
 * @author mjs
 *
 */


@Controller
@RequestMapping("/web/beacon")
public class BeaconWebController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(BeaconWebController.class.getName());

	@Autowired
	private BeaconRestController restController;

	@Autowired
	private FSqlRestController fsqlRestController;

	@Autowired
	private CustomerUtils customerUtils;
	
	@Autowired
	private SiteService siteService;
	
	@Autowired
	private PortionService portionService;
	
	@Autowired
	private BeaconService beaconService;
	
	@Autowired
	private BeaconDeviceService beaconDeviceService;
	
	@Autowired
	private CustomerService customerService;
		
	@RequestMapping("/list/scanned")
	public String scannedBeaconsList(Map<String, Object> model,
			@RequestParam(value = "suid", required = false) String suid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		try {
			
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			model.put("time", new Date());
			model.put("cid", cid);
			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			
			Collection<Beacon> beacons = restController.scannedList(request, response);
			model.put("beacons", beacons);
			
		} catch (Exception e) {
			LOG.warn("Error fetching devices", e);
		}
		return _CCC.pages.getPage("facesix.beacon.list", "beacon-list");
	}

	@RequestMapping("/list")
	public String beaconlist(Map<String, Object> model, @RequestParam(value = "suid", required = false) String suid,
							@RequestParam(value = "cid", required = false) String cid,
							@RequestParam(value = "sid", required = false) String sid,
							@RequestParam(value = "spid", required = false) String spid,
							HttpServletRequest request,HttpServletResponse response) {
		try {
			
			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			model.put("suid", suid);
			model.put("time", new Date());
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid", spid);
			
			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			
			Collection<Beacon> beacons = restController.scannedList(request, response);
			if (beacons != null)
				model.put("scannedbeacons", beacons);
			beacons = restController.BycheckedoutList(cid);
			if (beacons != null)
				model.put("checkedoutbeacons", beacons);

		} catch (Exception e) {
			LOG.warn("Error fetching devices", e);
		}
		return _CCC.pages.getPage("facesix.beacon.list", "beacon-list");
	}
	
	@RequestMapping("/list/filter")
	public @ResponseBody String filter(Map<String,Object> model,
						 @RequestParam(value = "cid", required = true)String cid,
						 @RequestParam(value = "sid", required = false)String sid,
						 @RequestParam(value = "spid", required = false)String spid,
						 @RequestParam(value = "macaddr", required = false)String mac,
						 @RequestParam(value = "param", required = false)String param,
						 @RequestParam(value = "name", required = false)String name,
						 @RequestParam(value = "place", required = false) String place,
						 HttpServletRequest request,
						 HttpServletResponse response ) throws IOException{
		prepare(model, request, response);
		
		//LOG.info("cid = "+cid+" sid = "+sid +" spid " +spid+" macaddr = "+mac+" param = "+param+" name = "+name+" place= "+place);
		
		int count = 0;
		
		String str = null;
		Beacon result = null;
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("time", new Date());
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		List<Beacon> beacon = null;
		
		if(sid==null || sid.isEmpty() || sid.equals("undefined") || 
		   (place != null && place.equals("scanlist"))){
			beacon = beaconService.getSavedBeaconByCid (cid);
		} else {
			beacon = beaconService.getSavedBeaconBySid(sid);
		//	LOG.info("beacon : "+beacon);
		}	
		
		if (name != null) {
			
			Iterator<Beacon> iter = beacon.iterator();
			while (iter.hasNext()){
				Beacon b = iter.next();
				
				String assignedTo 	= b.getAssignedTo().trim();
				String tagType   	= b.getTagType().trim();
				String status    	= b.getStatus().trim();
				String tagid        = b.getMacaddr().trim();
				
				//LOG.info("assignedTo "+assignedTo+" tagType "+tagType+" tagid "+tagid);
				if(name.contains(":") && name.equalsIgnoreCase(tagid)){
					//LOG.info("contains colon so its search by id");
					name = tagid;
					result = b;
					count++;
					break;
				}else if(name.trim().equalsIgnoreCase(assignedTo) && status.equals("checkedout")){
				//	LOG.info("assigned to");
					count++;
					result = b;
					name = assignedTo;
				}else if(name.trim().equalsIgnoreCase(tagType) && status.equals("checkedout")){
				//	LOG.info("checkedout");
					name = tagType;
					count++;
					result = b;
				}
			}
			
		}
		
	//	LOG.info("count value is "+count);
		
		if (count == 0) {
			if(place.equalsIgnoreCase("venue")){
				
				str = "/facesix/web/beacon/venuetag?sid="+sid+"&cid="+cid;
				
			}else if(place.equalsIgnoreCase("floor")){
				
				str = "/facesix/web/site/portion/dashboard?sid="+sid+"&spid="+spid+"&cid="+cid+"&param="+param;
				
			}else if(place.equalsIgnoreCase("tag")){
				
				str = "/facesix/web/beacon/tagDashview?macaddr="+mac+"&sid="+sid+"&spid="+spid+"&cid="+cid;
			}else{
				if(spid !=null && sid != null){
					str = "/facesix/web/beacon/list?sid="+sid+"&spid="+spid+"&cid="+cid;
				}else{
					str = "/facesix/web/beacon/list?sid=&spid=&cid="+cid;
				}
			}
			
			try {
				response.sendRedirect(str);
				return "";
			} catch (IOException e) {
				response.sendRedirect(str);
			}
			
		}
		
		if (count == 1 && !place.equals("scanlist")){
			//LOG.info("count is one " );
			String macaddr 	= result.getMacaddr();
			String nsid 	= result.getSid();
			String nspid 	= result.getSpid();
			
			if (nsid != null) {
				model.put("sid", nsid);
				Site site = siteService.findById(nsid);
				if (site != null) {
					model.put("sitename", site.getUid());
				}
			}

			if (nspid != null) {
				model.put("spid", nspid);
				Portion portion = portionService.findById(nspid);
				if (portion != null) {
					model.put("portionname", portion.getUid());
				}
			}
			if (nsid != null && nspid != null) {
				model.put("uid", result.getUid());
				
				 str = "/facesix/web/beacon/tagDashview?macaddr=" + macaddr + "&sid=" + nsid + "&spid=" + nspid
						+ "&cid=" + cid;
				try {
					response.sendRedirect(str);
					return "";
				} catch (IOException e) {
					response.sendRedirect(str);
					e.printStackTrace();
				}
			}
			
		}
				
	//	LOG.info("name " +name);
		if (count > 1 || place.equals("scanlist")) {
	//		LOG.info("sid "+sid);
			str= "/facesix/web/beacon/list?cid="+cid+"&name="+name+"&sid="+sid;
			try {
				response.sendRedirect(str);
				return "";
			} catch (IOException e) {
				response.sendRedirect(str);
				e.printStackTrace();
			}
		}
		return "";
	
	}
	
	@RequestMapping("/open/scanned")
	public String open(Map<String, Object> model, 
			@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "macaddr", required = false) String macaddr,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		model.put("cid", cid);
		model.put("time", new Date());
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		Beacon beacon = restController.scanned(macaddr, request, response);

		if (beacon == null) {
			model.put("message", Message.newInfo("Beacon not found in scanned list"));
		} else {
			model.put("beacon", beacon);
			model.put("message", Message.newInfo("Please configure beacon details correctly"));
		}
		return _CCC.pages.getPage("facesix.beacon.edit", "beacon-edit");
	}

	@RequestMapping("/remove/scanned")
	public String remove(Map<String, Object> model,
						@RequestParam(value = "id", required = false) String id,
						@RequestParam(value = "uid", required = false) String uid,
						@RequestParam(value = "macaddr", required = false) String macaddr,
						@RequestParam(value = "cid", required = false) String cid,
						@RequestParam(value = "sid", required = false) String sid,
						@RequestParam(value = "spid", required = false) String spid,
						HttpServletRequest request,HttpServletResponse response) {
		
		model.put("time", new Date());
		Beacon beacon = restController.scanned(macaddr, request, response);
		if (beacon != null) {
			restController.removeScannedBeacon(macaddr, request, response);
			model.put("message", Message.newInfo("Beacon successfully removed beacon :" + macaddr));
		} else {
			model.put("message", Message.newError("Could not find beacon with macaddr :" + macaddr));
		}
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		return beaconlist(model,beacon.getScannerUid(),cid,sid,spid,request,response);
	}


	@RequestMapping("/checkin")
	public String checkin(Map<String, Object> model, 
			@RequestParam(value = "id") String id,
			@RequestParam(value = "uid") String uid,
			@RequestParam(value = "macaddr", required = false) String macaddr,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			HttpServletRequest request, HttpServletResponse response) {
		
		//LOG.info("delete Tag macaddr " +macaddr);
		
		model.put("time", new Date());
		Beacon beacon = restController.checkin(id, request, response);
		if (beacon != null) {
			model.put("message", Message.newInfo("Successfully checked-in beacon :" + id));
		} else {
			model.put("message", Message.newError("Could not find beacon with id :" + id));
		}
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}		
		return beaconlist(model, null,cid,sid,spid,request,response);
	}

	/*@RequestMapping("/checkout")
	public String checkout(Map<String, Object> model, @RequestParam(value = "macaddr", required = false) String macaddr,
			@RequestParam(value = "assto", required = false) String patientId,
			@RequestParam(value = "Patient", required = false) String patientType,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			HttpServletRequest request, HttpServletResponse response) {
		
		String patientNameAndType = patientId + "/" + patientType;
		
		Beacon beacon = restController.checkout(macaddr, patientNameAndType, request, response);
		model.put("time", new Date());
		if (beacon == null) {
			model.put("message", Message.newError("Could not find beacon with macaddr :" + macaddr));
		} else {
			model.put("message", Message.newSuccess("Successfully checked-out beacon :" + macaddr));
		}
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		return beaconlist(model,beacon != null ? beacon.getScannerUid() : null,cid,sid,spid,request,response);

	}*/

	/**
	 * shows beacons moving path & timespent
	 * 
	 * @param model
	 * @param visitid
	 * @return
	 */
	@RequestMapping("/movemapx")
	public String movemapx(Map<String, Object> model, @RequestParam(value = "vid", required = false) String visitid,
			@RequestParam(value = "zz", required = false) boolean zz,
			@RequestParam(value = "head", required = false) boolean head) {
		model.put("head", head);
		String fsql = "index="
				+ _CCC.properties.getProperty("facesix.data.beacon.event.table", "facesix-int-beacon-event*")
				+ ",size=500,query=visitId:" + visitid + ",sort=timestamp ASC, from=0, size=500"
				+ " |value(timestamp,timestamp,typecast=date);value(name,Name,null);value(location,location,null);"
				+ "value(timeElapsed,timeElapsed,null);value(opcode,opcode,null);value(opcode,opcodex,"
				+ "eval=return (value==\"checkout\")?\"Patient In Time\":(value==\"checkedout\") ? \"Patient In Time\": (value==\"checkedin\")"
				+ " ? \"Patient Out Time\":value);|table";
		model.put("finder",true);
		if (LOG.isDebugEnabled())
			LOG.debug("movemap fsql :" + fsql);

		List<Map<String, Object>> result = fsqlRestController.newquery(fsql);

		model.put("vid", visitid);
		List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();

		long leftAt = 0;
		long enterAt = 0;
		long delay = 0;
		boolean show = true;

		if (result.size() > 1) {
			model.put("name", result.get(1).get("Name"));
			model.put("locations", events);
			model.put("timestamp", result.get(1).get("timestamp"));

			for (Map<String, Object> map : result) {

				show = true;
				delay = 0;

				String opcode = String.valueOf(map.get("opcode"));
				if (StringUtils.equals(opcode, "checkedout")) {
					leftAt = ((Date) map.get("timestamp")).getTime();
					map.put("cssBadgeColor", "success");
					map.put("cssBadgeArrow", "glyphicon-circle-arrow-left");
				} else if (StringUtils.equals(opcode, "entry")) {
					enterAt = ((Date) map.get("timestamp")).getTime();
					if (leftAt > 0) {
						delay = (enterAt - leftAt);
						leftAt = 0;
						Map<String, Object> tmap = new HashMap<String, Object>();
						tmap.put("timeElapsed", delay / 1000);
						tmap.put("location", "Waiting Time");
						tmap.put("timestamp", "");
						tmap.put("opcodex", "");
						tmap.put("cssBadgeColor", "warning");
						map.put("cssBadgeArrow", " ");
						events.add(tmap);
					}
					show = zz;
				} else if (StringUtils.equals(opcode, "exit")) {
					leftAt = ((Date) map.get("timestamp")).getTime();
					show = zz;
				} else if (StringUtils.equals(opcode, "entry-exit")) {
					map.put("opcodex", "");
					if (delay > 0)
						map.put("delay", delay / 1000);
					leftAt = 0;
				} else if (StringUtils.equals(opcode, "total-summary")) {
					model.put("duration", map.get("timeElapsed"));
					show = zz;
				} else if (StringUtils.equals(opcode, "checkedin")) {
					map.put("cssBadgeColor", "danger");
					map.put("cssBadgeArrow", "glyphicon-circle-arrow-right");
				}

				if (show) {
					events.add(map);
				}
			}
		}
		return _CCC.pages.getPage("facesix.beacon-move", "beacon-move");
		//return "beacon-move";
	}

	@RequestMapping("/movemap")
	public String movemap(Map<String, Object> model, @RequestParam(value = "vid", required = false) String visitid,
			@RequestParam(value = "zz", required = false) boolean zz,
			@RequestParam(value = "head", required = false) boolean head) {
		model.put("head", head);
		String fsql = "index="
				+ _CCC.properties.getProperty("facesix.data.beacon.event.table", "facesix-int-beacon-event*")
				+ ",size=500,query=visitId:" + visitid + ",sort=timestamp ASC, from=0, size=500"
				+ " |value(timestamp,timestamp,typecast=date);value(name,Name,null);value(location,location,null);"
				+ "value(timeElapsed,timeElapsed,null);value(opcode,opcode,null);value(opcode,opcodex,"
				+ "eval=return (value==\"checkout\")?\"Patient In Time\":(value==\"checkedout\") ? \"Patient In Time\": (value==\"checkedin\")"
				+ " ? \"Patient Out Time\":value);|table";

		model.put("finder",true);
		if (LOG.isDebugEnabled())
			LOG.debug("movemap fsql :" + fsql);

		List<Map<String, Object>> result = fsqlRestController.newquery(fsql);

		model.put("vid", visitid);
		List<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
		
		long leftAt = 0;
		long enterAt = 0;
		long delay = 0;
		boolean show = true;
		long checkOutTime = 0;

		if (result.size() > 1) {
			model.put("name", result.get(1).get("Name"));
			model.put("locations", events);
			model.put("timestamp", result.get(1).get("timestamp"));

			for (Map<String, Object> map : result) {

				show = true;
				delay = 0;

				LOG.warn("Timestamp: " + String.valueOf(map.get("timestamp")) + ", Evt:"
						+ String.valueOf(map.get("opcode")) + ", loc:" + String.valueOf(map.get("location")));

				String opcode = String.valueOf(map.get("opcode"));
				if (StringUtils.equals(opcode, "checkedout")) {
					leftAt = ((Date) map.get("timestamp")).getTime();
					checkOutTime = ((Date) map.get("timestamp")).getTime();
					map.put("cssBadgeColor", "success");
					map.put("cssBadgeArrow", "glyphicon-circle-arrow-left");
				} else if (StringUtils.equals(opcode, "entry")) {
					enterAt = ((Date) map.get("timestamp")).getTime();
					if (leftAt > 0) {
						delay = (enterAt - leftAt);
						leftAt = 0;
						Map<String, Object> tmap = new HashMap<String, Object>();
						tmap.put("timeElapsed", delay / 1000);
						tmap.put("location", "Waiting Time");
						tmap.put("timestamp", "");
						tmap.put("opcodex", "");
						tmap.put("cssBadgeColor", "warning");
						tmap.put("cssBadgeArrow", "glyphicon-circle-arrow-down");
						events.add(tmap);
					}
					show = zz;
				} else if (StringUtils.equals(opcode, "exit")) {
					leftAt = ((Date) map.get("timestamp")).getTime();
					if (enterAt > 0) {
						delay = (leftAt - enterAt);
						// map.put("delay", delay/1000);
						enterAt = 0;
						map.put("opcodex", "");
						map.put("timeElapsed", delay / 1000);
					}
				} else if (StringUtils.equals(opcode, "entry-exit")) {
					show = zz;
				} else if (StringUtils.equals(opcode, "total-summary")) {
					/*Corner case when a entry followed by total-summary comes*/
					if(enterAt != 0)
						leftAt = enterAt;
					//The time in timeElaspsed isn't coming proper causing Duration not represented properly
					//As of now, we deduce the timeElapsed from checkin - checkout time
//					model.put("duration", map.get("timeElapsed"));
					long checkinTime = ((Date) map.get("timestamp")).getTime();
					model.put("duration", ((checkinTime-checkOutTime)/1000));
					show = zz;
				} else if (StringUtils.equals(opcode, "checkedin")) {

					enterAt = ((Date) map.get("timestamp")).getTime();
					if (leftAt > 0) {
						delay = (enterAt - leftAt);
						leftAt = 0;
						Map<String, Object> tmap = new HashMap<String, Object>();
						tmap.put("timeElapsed", delay / 1000);
						tmap.put("location", "Waiting Time");
						tmap.put("timestamp", "");
						tmap.put("opcodex", "");
						tmap.put("cssBadgeColor", "warning");
						tmap.put("cssBadgeArrow", "glyphicon-circle-arrow-down");
						events.add(tmap);
					}
					map.put("cssBadgeColor", "danger");
					map.put("cssBadgeArrow", "glyphicon-circle-arrow-right");
				}

				if (show) {
					events.add(map);
				}
			}
		}
		return _CCC.pages.getPage("facesix.beacon-move", "beacon-move");
		//return "beacon-move";
	}
	
	@RequestMapping("/location/stats")
	public String beaconLocationStats(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response,
									  @RequestParam (required=false) String from, @RequestParam (required=false) String to,
									  @RequestParam(value = "cid", required = false) String cid) {
		List<TimeStats> fsobjects = restController.averageOfLocations(from, to);
		
		List<TimeStats> locationStats = new ArrayList<TimeStats>();
		List<TimeStats> hospitalStats = new ArrayList<TimeStats>();
		
		/*Time spend in hospital needs to be taken out and put into another object from TimeStats*/
		for(TimeStats elem : fsobjects) {
			if(elem.getLocation().contains("Hospital")) {
				hospitalStats.add(elem);
			}
			else {
				locationStats.add(elem);
			}
		}
		model.put("locationStats", locationStats);
		model.put("hospitalStats", hospitalStats);
		model.put("from", from);
		model.put("to", to);
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		model.put("cid", cid);
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.location.stats", "beacon-location-stats");
	}

	
	@RequestMapping("/tagDashview")
	public String dashview(Map<String, Object> model,@RequestParam(value = "macaddr", required = true) String macaddr,
													  @RequestParam(value = "sid", required = true) String sid,
													  @RequestParam(value = "spid", required = false) String spid,
													  @RequestParam(value = "cid", required = false) String cid,
													  HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			Beacon  dv 	= null;
			int battery = 0;
			
			String fafa 	= "fa fa-battery-empty fa-2x";
			String  color 	= "black";
			
		    if (macaddr != null) {
				dv = beaconService.findOneByMacaddr(macaddr);
				if (dv.getBattery_level() != 0) {
					battery 			= dv.getBattery_level();
					String batteryinfo  = beaconService.batteryStatus(battery);
					fafa 				= batteryinfo.split("&")[0];
					color 				= batteryinfo.split("&")[1];		
				}
			}
			
			model.put("fsobjects",dv);

			prepare(model, request, response);
			
			if (sid != null) {
				model.put("sid", sid);
				Site site = siteService.findById(sid);
				if (site != null) {
					model.put("sitename", site.getUid());
				}
			}

			if (spid != null) {
				model.put("spid", spid);
				Portion portion = portionService.findById(spid);
				if (portion != null) {
					model.put("portionname", portion.getUid());
					model.put("height", 	portion.getHeight());
					model.put("width",  	portion.getWidth());	
				}
			}
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid", spid);
			model.put("macaddr", macaddr);
			model.put("fafa" , fafa);
			model.put("color" , color);
			model.put("battery", battery);
		    model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));

			model.put("entryexit", 		customerUtils.entryexit(cid));
			model.put("locatum", 		customerUtils.Locatum(cid));			
		} catch(Exception e) {
			LOG.info("while tag dashview page redirection Error " +e);
			return _CCC.pages.getPage("facesix.login", "login");
		}
			
		return _CCC.pages.getPage("facesix.beacon.dashboard", "tag-dashboard");
	}	
	
	
	@RequestMapping("/tagDashboard")
	public String tagDashboard(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "macaddr", required = false) String macaddr,
			HttpServletRequest request,HttpServletResponse response) {
		
		
		try {
			
			Beacon dv 	= null;
			String str 	= null;
			prepare(model, request, response);
			
			if(SessionUtil.isAuthorized(request.getSession())){
				
				if (cid == null || cid.isEmpty()) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
				}
				
				
				if (macaddr != null) {
					dv = beaconService.findOneByMacaddr(macaddr);
				}
				
				if (dv != null) {
					spid = dv.getSpid();
					sid  = dv.getSid();
					model.put("uid", macaddr);
					//LOG.info("!!!!!!");
				}
				
				if (sid != null) {
					model.put("sid", sid);
					Site site = siteService.findById(sid);
					if (site != null) {
						model.put("sitename", site.getUid());
					}
				}

				if (spid != null) {
					model.put("spid", spid);
					Portion portion = portionService.findById(spid);
					if (portion != null) {
						model.put("portionname", portion.getUid());
						model.put("height", 	 portion.getHeight());
						model.put("width",  	 portion.getWidth());							
					}
				}
				
				model.put("cid", cid);
				model.put("sid", sid);
				model.put("spid", spid);
				model.put("macaddr", macaddr);
				
				model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
				model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
				model.put("Gateway", 		customerUtils.Gateway(cid));
				model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
				
				model.put("entryexit", 		customerUtils.entryexit(cid));
				model.put("locatum", 		customerUtils.Locatum(cid));
				if (spid == null) {
					try {
						str = "/facesix/web/beacon/list?sid=&spid=&cid="+cid;
						response.sendRedirect(str);
					} catch (IOException e) {
						LOG.info("While Redirection Error " ,e);
					}
				}
				
				if (sid != null && spid != null) {
					try {
						str = "/facesix/web/beacon/tagDashview?macaddr="+macaddr+"&sid="+sid +"&spid="+spid+"&cid="+cid;
						response.sendRedirect(str);
					} catch (IOException e) {
						LOG.info("While  tagDashview page Redirection Error " ,e);
						return _CCC.pages.getPage("facesix.login", "login");
					}
				}
				
			} else {
				return _CCC.pages.getPage("facesix.login", "login");
			}

		} catch (Exception e) {
			LOG.warn("Error fetching devices", e);
			return _CCC.pages.getPage("facesix.login", "login");
		}
		return _CCC.pages.getPage("facesix.beacon.dashboard", "tag-dashboard");
	}
	
	@RequestMapping("/tag/configure")
	public String tagconfigure(Map<String, Object> model,
							@RequestParam(value = "macaddr", required = false) String macaddr,
							@RequestParam(value = "sid", required = false) String sid,
							@RequestParam(value = "spid", required = false) String spid,
							@RequestParam(value = "cid", required = true) String cid,
							HttpServletRequest request, HttpServletResponse response) {
		
		//LOG.info(" macaddr : " +macaddr + " cid: " +cid);
		
		JSONObject json = new JSONObject();
		
		JSONObject tagJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		
		String serverId = "";
		String serverName = "";
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		Beacon device = null;
		
		if (macaddr != null) {
			device = beaconService.findOneByMacaddr(macaddr);
		}
		
		ArrayList<BeaconDevice> scannerList = new ArrayList<BeaconDevice>();
		List<BeaconDevice> beacondevice = null;
		beacondevice = getBeaconDeviceService().findByCid(cid);
		
		if (beacondevice != null) {
			for (BeaconDevice bcon : beacondevice) {
				if (bcon.getType() != null && bcon.getType().equalsIgnoreCase("scanner")) {
					scannerList.add(bcon);
				}
			}
			model.put("beacondevice", scannerList);
		}
		
		
		List<BeaconDevice> server = null;
		server = getBeaconDeviceService().findByCid(cid);
		
		if (server != null) {
			for (BeaconDevice dv : server) {
				if (dv.getType() != null && dv.getType().equalsIgnoreCase("server")) {
					serverId   = dv.getUid();
					serverName = dv.getName();
					break;
				}
			}
			model.put("serverId",   serverId);
			model.put("serverName", serverName);
		}
	
		if (device != null) {
			model.put("message", Message.newInfo("Please update existing tag config and press submit button to save"));

			if (device.getTemplate() == null) {
				
				json.put("minor", 		device.getMinor());
				json.put("major", 		device.getMajor());
				json.put("assignedto", 	device.getAssignedTo());
				json.put("uuid", 		device.getUid());
				json.put("tagtype", 	device.getTagType());
				json.put("name", 		device.getName());
				json.put("txpower", 	device.getTxPower());
				json.put("interval", 	device.getInterval());
				json.put("tagmod", 		device.getTagModel());
				json.put("reftx", 		device.getRefTxPwr());
				
				tagArray.add(json);
				tagJson.put("attributes", tagArray);
				
				device.setTemplate(tagJson.toString());
				//LOG.info("TAG Configure JSON >>>>>>>. " +tagJson);
			}
		}

		model.put("device", device);
		model.put("configuration", TAB_HIGHLIGHTER);
		model.put("sid", sid);
		model.put("spid",spid);
		model.put("cid", cid);
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.tag.configure", "tag-configure");
	}
	
	
	@RequestMapping("/save")
	public String save(Map<String, Object> model,
					  @RequestParam(value = "macaddr", required = false) String macaddr,
					  @RequestParam(value = "conf", required = false) String conf,
					  @RequestParam(value = "cid", required = false) String  cid,
					  @RequestParam(value = "sid", required = false) String  sid,
					  @RequestParam(value = "spid", required = false) String spid,
					  @RequestParam(value = "type", required = true) String  type,
					  HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			net.sf.json.JSONObject template = net.sf.json.JSONObject.fromObject(conf);
	
			Beacon beacon = null;
			String name = "";
			String tagtype = "";
			String minor = "";
			String major = "";
			String assignedto = "";
			String uuid = "";
			String txpower = "";
			String interval = "0";
			String tagMod = "";
			String  refTx  = "-59";
			
			//LOG.info("conf is " + conf);
			
			beacon = beaconService.findOneByMacaddr(macaddr);
			
			JSONArray attribute = template.getJSONArray("attributes");
			JSONObject obj = attribute.getJSONObject(0);
	
			name 		= obj.getString("name");
			tagtype 	= obj.getString("tagtype");
			minor 		= obj.getString("minor");
			major 		= obj.getString("major");
			assignedto  = obj.getString("assignedto");
			uuid 		= obj.getString("uuid");
			txpower 	= obj.getString("txpower");
			
			tagMod 	   = (String)obj.get("tagmod");
			refTx 	   = (String)obj.get("reftx");
			
			if (obj.getString("interval") != null) {
				interval = obj.getString("interval");
			}
	
			//LOG.info(" macaddr " + macaddr + "	cid	" + cid);
			//LOG.info(" TAG JSON Config" + template);

			LOG.info(" tagMod " + tagMod +" refTx " +refTx);
			
			String universalId = null;
			String universalname = null;
			
			if (beacon != null) {
				
				if (sid != null && !sid.isEmpty() && type.equals("Venue")) {
					universalId = sid;
					universalname = "sid";
				} else if (spid != null && !spid.isEmpty() && type.equals("Floor")) {
					universalId = spid;
					universalname = "spid";
				} else if (cid != null && !cid.isEmpty() && type.equals("Customer")) {
					universalId = cid;
					universalname = "cid";
				} else {
					universalname ="scannerUid";
					
					BeaconDevice device = getBeaconDeviceService().findOneByUid(type);
					
					if (device != null) {
						if (device.getType().equalsIgnoreCase("scanner")) {
							universalname = "scannerUid";
						}else if (device.getType().equalsIgnoreCase("server")) {
							universalname = "serverUid";
						}
					}
					universalId = type;			
				}
				
				//LOG.info(" UniversalId " +universalId + " Universalname  " +universalname +" Type " + type );
				
				beacon.setName(name); 
				beacon.setTagType(tagtype);
				beacon.setMinor(Integer.parseInt(minor));
				beacon.setMajor(Integer.parseInt(major));
				beacon.setAssignedTo(assignedto);
				beacon.setUid(uuid);
				beacon.setTxPower(Integer.parseInt(txpower));
			    beacon.setInterval(Integer.parseInt(interval));
				beacon.setTemplate(template.toString());
				beacon.setCid(cid);
				beacon.setTagModel(tagMod);
				beacon.setRefTxPwr(refTx);
				beacon.setModifiedOn(new Date());
				beacon.setModifiedBy(whoami(request,response));
				beacon = beaconService.saveBeaconTags(beacon,universalId,universalname, true);
			}
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid",spid);
			
			try {
				response.sendRedirect("/facesix/web/beacon/list?cid="+cid);
			} catch (IOException e) {
				response.sendRedirect("/facesix/web/beacon/list?cid="+cid);
			}

		} catch (Exception e) {
			LOG.info("while beacon tag configure save error " + e);
		}

		return _CCC.pages.getPage("facesix.beacon.list", "beacon-list");
	}

	@RequestMapping("/venuetag")
	public String venuetag(Map<String, Object> model, 
			@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			HttpServletRequest request,HttpServletResponse response) {
		
		List<Portion> portionList = null;
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		if (sid != null) {
			model.put("sid", sid);
			portionList = portionService.findBySiteId(sid);
			model.put("portion", portionList);
			
			Site site = siteService.findById(sid);
			if (site != null) {
				model.put("sitename", site.getUid());
			}
		}

		if (spid != null) {
			model.put("spid", spid);
			Portion portion = portionService.findById(spid);
			if (portion != null) {
				model.put("portionname", portion.getUid());
				model.put("height", 	 portion.getHeight());
				model.put("width",  	 portion.getWidth());	
			}
		}
				
		if (portionList != null && portionList.size() > 0) {
			Portion object = portionList.get(0);
			model.put("portionname", object.getUid());
			model.put("spid", 		 object.getId());
			//LOG.info("Width" + object.getWidth());
			//LOG.info("Height" + object.getHeight());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));

		model.put("entryexit", 		customerUtils.entryexit(cid));
		model.put("locatum", 		customerUtils.Locatum(cid));
		
		return _CCC.pages.getPage("facesix.beacon.venue", "venue");
	}
	
	@RequestMapping("/alerts")
	public String alerts(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.alerts", "finderalert");
	}
	
	@RequestMapping("/reports")
	public String reports(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		String page = _CCC.pages.getPage("facesix.login", "login");
		
		if (SessionUtil.isAuthorized(request.getSession())) {

			if (cid == null || cid.isEmpty()) {
				cid = SessionUtil.getCurrentCustomer(request.getSession());
			}
			
			model.put("cid", cid);
			model.put("sid", sid);
			model.put("spid",spid);
			
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				model.put("customername", customer.getCustomerName());
			}
			
			model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
			model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
			model.put("Gateway", 		customerUtils.Gateway(cid));
			model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
			
			page = _CCC.pages.getPage("facesix.beacon.reports", "report");
		}
		return page;
	}
	
	@RequestMapping("/customizeAlert")
	public String customizeAlert(Map<String, Object> model,
			@RequestParam(value = "sid", required = false) String sid,
			@RequestParam(value = "spid", required = false) String spid,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		if (cid == null || cid.isEmpty()) {
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		model.put("cid", cid);
		model.put("sid", sid);
		model.put("spid",spid);
		
		model.put("entryexit",      customerUtils.entryexit(cid));
		model.put("locatum",        customerUtils.Locatum(cid));

		model.put("GatewayFinder", 	customerUtils.GatewayFinder(cid));
		model.put("GeoFinder", 		customerUtils.GeoFinder(cid));
		model.put("Gateway", 		customerUtils.Gateway(cid));
		model.put("GeoLocation", 	customerUtils.GeoLocation(cid));
		
		return _CCC.pages.getPage("facesix.beacon.customizeAlert", "customizeAlert");
	}
	
	private BeaconDeviceService getBeaconDeviceService() {
		if (beaconDeviceService == null) {
			beaconDeviceService = Application.context.getBean(BeaconDeviceService.class);
		}
		return beaconDeviceService;
	}
}
