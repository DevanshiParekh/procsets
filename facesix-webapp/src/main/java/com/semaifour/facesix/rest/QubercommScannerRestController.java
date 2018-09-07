package com.semaifour.facesix.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.probe.oui.ProbeOUI;
import com.semaifour.facesix.probe.oui.ProbeOUIService;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

@RequestMapping("/rest/qubercomm/scanner")
@RestController
public class QubercommScannerRestController  extends WebController  {

	static Logger LOG = LoggerFactory.getLogger(QubercommScannerRestController.class.getName());

	@Autowired
	FSqlRestController fsqlRestController;

	@Autowired
	NetworkDeviceRestController networkDeviceRestController;
	
	@Autowired
	DeviceRestController deviceRestController;
	
	@Autowired
	NetworkDeviceService networkDeviceService;

	@Autowired
	ProbeOUIService probeOUIService;
	
	
	static Font smallBold 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.BOLD);
    static Font catFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 18, 	Font.BOLD);
    static Font redFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 12, 	Font.NORMAL);
    static Font subFont 	= new Font(Font.FontFamily.TIMES_ROMAN, 16,     Font.BOLD);
    static boolean ThreadTobeStarted = false;

    SimpleDateFormat advanceDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private String indexname = "facesix*";
	
	String 	prop_event_table_index = "facesix-prop-client-event";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		prop_event_table_index = _CCC.properties.getProperty("facesix.data.prop.event.table",
				prop_event_table_index);
	}

	public boolean resetHeatmapState(String uid) {
		
		if (uid != null) {

			JSONArray  dev_array = new JSONArray();
			
			String uuid 	 = uid.replaceAll("[^a-zA-Z0-9]", "");
			NetworkDevice nd = networkDeviceService.findOneByUuid(uuid);
		
			if (nd == null) {
				nd = networkDeviceService.findOneByUuid(uuid.toUpperCase());
			}
			
			if (nd != null) {
				nd.setTagString(String.valueOf(dev_array));
				nd.setProbeAndroid(0);
				nd.setProbeIos(0);
				nd.setProbeWindows(0);
				nd.setProbespeaker(0);
				nd.setProbeprinter(0);
				nd.setProbeOthers(0);
				nd.setProbePeercount(0);
				nd.setProbeAssocate(0);
				nd.setModifiedBy("cloud");
				nd.setModifiedOn(new Date());
				networkDeviceService.save(nd);
			}
		}
		return true;
	}
	
	
    @RequestMapping(value = "/probe_req_stats", method = RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public JSONObject probe_req_stats(@RequestParam(value = "uid", required = false) String uid,
									  @RequestParam(value = "time", required = false) String time,
									  @RequestParam(value = "clientmac", required = false) String clientmac,
									  @RequestParam(value = "cid", required = false) String cid,
									  HttpServletRequest request) throws IOException, ParseException{

		JSONObject dev 		 = null;
		JSONArray  dev_array = new JSONArray();
		JSONObject devlist 	 = new JSONObject();

		List<Map<String, Object>> conn = EMPTY_LIST_MAP;

		String fsql = null;
					
		String size="100";
	    if(time.equals("15m"))
			size = "100";
		else if(time.equals("30m"))
			size = "200";
		else if(time.equals("60m"))
			size = "400";
		else if (time.equals("12h"))
			size = "800";
		else if (time.equals("24h"))
			size = "1200";
		else if (time.equals("auto")) {
			size = "100";
			time="15m";
		}
	    
	    if (uid != null) {
	    	uid = uid.toLowerCase();
	    }
			    
		fsql = "index="+indexname +",sort=timestamp desc,size="+size+",query=timestamp:>now-" +time+ " AND ";
		if(uid != null){
			fsql = fsql + "uid:\"" + uid + "\"";
			fsql = fsql+ " AND track_sta_stats:\"Qubercloud Manager\"|value(message,snapshot, NA);value(timestamp,time,NA)|table";
		}

		conn = fsqlRestController.query(fsql);
		
	/*	if (conn == null || conn.isEmpty() || conn.size() < 0) {			
			String uuid 	 = uid.replaceAll("[^a-zA-Z0-9]", "");
			NetworkDevice nd = networkDeviceService.findOneByUuid(uuid);
			if (nd == null) {
				nd = networkDeviceService.findOneByUuid(uuid.toUpperCase());
			}
			
			if (nd != null ) {
				
				String tagString = nd.tagstring;
				if (tagString == null || tagString.isEmpty() || tagString.length() < 0) {
					tagString = "";
				} else {

					JSONParser parser   = new JSONParser();
					dev_array 			= (JSONArray)parser.parse(tagString);	
					devlist.put("probe_req_stats", dev_array);
				}
				return devlist;
			} else {
				resetHeatmapState (uid);
			}
		}*/
		
		
		Map<String, ArrayList> hmap = new ConcurrentHashMap<String, ArrayList>();

		if (conn != null) {
			
			Iterator<Map<String, Object>> iterator = conn.iterator();
		
		while (iterator.hasNext()) {

			TreeMap<String, Object> me = new TreeMap<String, Object>(iterator.next());
			String JStr = (String) me.values().toArray()[0];
						
			JSONObject newJObject = null;
			JSONParser parser = new JSONParser();

			try {
				newJObject = (JSONObject) parser.parse(JStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			JSONArray proberReq = null;
			
			if (newJObject.containsKey("track_sta_list")){
				
				proberReq = (JSONArray) newJObject.get("track_sta_list");
				 
				Iterator dups = proberReq.iterator();
				while (dups.hasNext()) {
					JSONObject slide = (JSONObject) dups.next();
					String peer_mac = (String) slide.get("MAC_ID");
					Long channel =Long.valueOf(String.valueOf(slide.get("CHANNEL")));
					int dupHash = 0;

						
					ArrayList<String> list=new ArrayList<String>();
					if (clientmac != null && !clientmac.isEmpty() && !peer_mac.equals(clientmac)){
						clientmac = clientmac.toLowerCase();
						continue;
					}
					if (channel < 14) {
						peer_mac = peer_mac+ "   2.4Ghz";
					} else {
						peer_mac = peer_mac+ "   5  Ghz";
					}
					if (hmap.isEmpty()) {
						list.add("1"); //0the Index
						list.add(slide.get("RSSI").toString());
						list.add(String.valueOf(slide.get("CHANNEL")));
						list.add(slide.get("TIME").toString());
						hmap.put(peer_mac, list);
					} else {
						Iterator<String> it1 = hmap.keySet().iterator();
						while (it1.hasNext()) {
							String key = it1.next();
							ArrayList<Long> channelValue = hmap.get(key);
							Long mapChannel = Long.valueOf(String.valueOf(channelValue.get(2)));
							if (key.equals(peer_mac) && mapChannel == channel) {
								ArrayList<String> l2 = hmap.get(key);
								String cnt = l2.get(0);
								Integer cntIndex = Integer.parseInt(cnt);
								cntIndex++;
								l2.add(0, cntIndex.toString());
								l2.add(1, slide.get("RSSI").toString());
								l2.add(2, String.valueOf(slide.get("CHANNEL")));
								l2.add(3, slide.get("TIME").toString());
								hmap.put(peer_mac, l2);
								dupHash = 1;
							} else if (key.equals(peer_mac) && mapChannel != channel ) {
							}
						}

						if (dupHash == 0) {
							list.add("1"); // 0the Index
							list.add(slide.get("RSSI").toString());
							list.add(String.valueOf(slide.get("CHANNEL")));
							list.add(slide.get("TIME").toString());
							hmap.put(peer_mac, list);
						}

					}
				}
			}
		}
    }
		org.json.simple.JSONObject deviceType = new org.json.simple.JSONObject();
		
		for (Map.Entry entry : hmap.entrySet()) {
			dev = new JSONObject();
			Object key = entry.getKey();

			String peerMac 		= String.valueOf(key);
			String ClientMac 	= peerMac.split("\\s+")[0];
			
			ArrayList<Object> value = (ArrayList<Object>) entry.getValue();
			
			networkDeviceRestController.probeCount(ClientMac,deviceType);
			
			dev.put("node_mac",  	uid); 
			dev.put("mac_address",  ClientMac); // mac_id
			dev.put("count", 		value.get(0)); // dups counts
			dev.put("signal", 		value.get(1));
			dev.put("channel", 		value.get(2));
			dev.put("timestamp",    value.get(3));
			dev.put("devtype",      deviceType.getOrDefault("client_type", "0"));
			dev_array.add(dev);
		
		}
				
		if (uid != null) {

			String uuid 	 = uid.replaceAll("[^a-zA-Z0-9]", "");
			NetworkDevice nd = networkDeviceService.findOneByUuid(uuid);
		
			if (nd == null) {
				nd = networkDeviceService.findOneByUuid(uuid.toUpperCase());
			}
			
			if (nd !=null) {
				nd.setTagString(String.valueOf(dev_array));
				nd.setModifiedBy("cloud");
				nd.setModifiedOn(new Date());
				networkDeviceService.save(nd);
			}
		}
		
		devlist.put("probe_req_stats", dev_array);
		return devlist;
	}

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/heatmap_list", method = RequestMethod.GET)
    public JSONObject heatmap_list(
    		@RequestParam(value = "uid", required = false) String uid,
    		@RequestParam(value = "spid", required = false) String spid,
    		@RequestParam(value = "time", required = false) String time,
    		@RequestParam(value = "size", required = false) String size) throws IOException {    	

    	size = "1";
    	time = "30s";
		
		//LOG.info(" Time" +time + " Record size " +size);
		
    	JSONObject propObject 	= new JSONObject();
		JSONArray dev_array 	= new JSONArray();
		
    	
    	List<NetworkDevice> deviceList 		  = new ArrayList<NetworkDevice>();
  	    List<NetworkDevice> networkDeviceList = null;
  		NetworkDevice nd 					  = null;
  		
		if (uid != null && !uid.equals("undefinded")) {
			String uuid = uid.replaceAll("[^a-zA-Z0-9]", "");
			nd = networkDeviceService.findOneByUuid(uuid);
			if (nd == null) {
				nd = networkDeviceService.findOneByUuid(uuid.toUpperCase());
			}
			deviceList.add(nd);
		} else if (spid != null) {
			networkDeviceList = networkDeviceService.findBySpid(spid);
			deviceList.addAll(networkDeviceList);
		}

		if (deviceList != null) {
			for (NetworkDevice dev : deviceList) {
				if (dev == null) {
					continue;
				}
				
			uid = dev.getUid().toUpperCase();
			
			String fsql = "index="+prop_event_table_index+",type=prop_client,size="+size+",query= timestamp:>now-"+time+" "
					+ " AND opcode:\"prop_info\"AND uid:\""+uid+"\"|value(uid,Uid, NA);"
					+ " value(prop_list,Prop_list,NA);value(timestamp,time,NA)|table;";
			
			List<Map<String,Object>> propLog 		 = fsqlRestController.query(fsql);
			String node_mac 						 = null;
			Iterator<Map<String, Object>> iterator   = propLog.iterator();
			
			//LOG.info("uid  " +uid);
			
			if (propLog != null) {
				
				while (iterator.hasNext()) {

					TreeMap<String, Object> me 		= new TreeMap<String, Object>(iterator.next());
					 node_mac 						= (String) me.get("Uid");
					HashMap<String,Object> propMap  = (HashMap<String, Object>) me.get("Prop_list");
					
						try {

							ArrayList<HashMap<String,Object> > proberReq  	= (ArrayList) propMap.get("probe_requests");
							Iterator<HashMap<String,Object> > probe_iter  	= proberReq.iterator();
							JSONObject devlist  							= null;
							
							while (probe_iter.hasNext()) {
								devlist = new JSONObject();
								
								HashMap<String,Object> slide = probe_iter.next();
								
								String peer_mac 		= (String) slide.get("mac");
		        				Object count 		    = slide.get("count");
		        				Object min_signal 		= slide.get("min_signal");
		        				Object max_signal 		= slide.get("max_signal");
		        				Object avg_signal 		= slide.get("avg_signal");
		        				Object last_seen_signal = slide.get("last_seen_signal");
		        				Object first_seen 		= slide.get("first_seen");
		        				Object last_seen 		= slide.get("last_seen");
		        				Object associated 		= slide.get("associated");
		        				
		        				devlist.put("node_mac",  		node_mac.toLowerCase());
		        				devlist.put("mac_address",  	peer_mac);
		        				devlist.put("count", 			count);
		        				devlist.put("signal", 			max_signal);
		        				devlist.put("channel", 			"NA");
		        				devlist.put("max_signal", 		min_signal);
		        				devlist.put("avg_signal", 		avg_signal);
		        				devlist.put("last_seen_signal", last_seen_signal);
		        				devlist.put("timestamp", 		first_seen);
		        				devlist.put("last_seen", 		last_seen);
		        				devlist.put("associated", 		associated);
		        				dev_array.add(devlist);

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			//LOG.info("heatmap_list" + dev_array);
			
			propObject.put("probe_req_stats", dev_array);
		}
		return propObject;
	}
    
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String export(@RequestParam(value = "uid", required = false) String uid,
			  			 @RequestParam(value = "time", required = false) String time,
			  			 HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {

		String pdfFileName  = "./uploads/qubexport.pdf";
		String logoFileName = "./uploads/logo-home.png";
		
		OutputStream responseOutputStream = null;
		FileInputStream fileInputStream   = null;
		FileOutputStream os				  = null;

		//String pdfFileName  = "C:/temp/quber.pdf";
		//String logoFileName = "C:/temp/logo-home.png";

		if (SessionUtil.isAuthorized(request.getSession())) {

			Document document = new Document();
			try {
				os = new FileOutputStream(pdfFileName);
				@SuppressWarnings("unused")
				PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
				document.open();
				Paragraph paragraph = new Paragraph();
				Image image2 = Image.getInstance(logoFileName);
				image2.scaleAbsoluteHeight(25f);// scaleAbsolute(50f, 50f);
				image2.scaleAbsoluteWidth(100f);
				paragraph.add(image2);
				paragraph.setAlignment(Element.ALIGN_LEFT);
				paragraph.add("Qubercloud Log Summary");
				paragraph.setAlignment(Element.ALIGN_CENTER);

				addEmptyLine(paragraph, 1);

			    // Will create: Report generated by: _name, _date
			    paragraph.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), smallBold));
			    addEmptyLine(paragraph, 3);
			    document.add(paragraph);

			    document.newPage();

			    addlogContent (document,uid,time);

				document.close();

				File pdfFile = new File(pdfFileName);
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
				response.setContentLength((int) pdfFile.length());
				fileInputStream = new FileInputStream(pdfFile);
				responseOutputStream = response.getOutputStream();
				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				responseOutputStream.close();
				fileInputStream.close();
				os.close();
			}
			//return pdfFileName;
		}

		return pdfFileName;
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private void addlogContent(Document document,String uid,String time) throws DocumentException, IOException, ParseException {
		Anchor anchor = new Anchor("Qubercloud LOG Summary", catFont);
		anchor.setName("Qubercloud LOG Summary");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);

		Paragraph subPara = new Paragraph("Qubercloud LOGS", subFont);
		addEmptyLine(subPara, 1);

		Section subCatPart = catPart.addSection(subPara);

		// add a table
		createPropReqTable(subCatPart, document,uid,time);

		// now add all this to the document
		document.add(catPart);

	}

	private void createPropReqTable(Section subCatPart, Document document,String uid,String time)
			throws IOException, ParseException, DocumentException {

		PdfPTable table = new PdfPTable(5);

		PdfPCell c1 = new PdfPCell(new Phrase("PEER MAC"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);


		c1 = new PdfPCell(new Phrase("CHANNEL"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("SIGNAL"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		table.setHeaderRows(1);
		c1 = new PdfPCell(new Phrase("OCCURANCE"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);



		c1 = new PdfPCell(new Phrase("TIME STAMP"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);


		table.setHeaderRows(1);


		JSONObject newJObject = null;
		newJObject = probe_req_stats(uid,time,null,null,null);

		if (newJObject != null) {

			JSONArray propContent = (JSONArray) newJObject.get("probe_req_stats");

		//	LOG.info("Export file " +propContent.toString());

			if (propContent != null) {
				Iterator i = propContent.iterator();
				while (i.hasNext()) {

					JSONObject slide = (JSONObject) i.next();

					String peer_mac = (String) slide.get("mac_address");
					table.addCell(peer_mac);

					String channel = (String) slide.get("channel");
					table.addCell(channel);

					String signal = String.valueOf(slide.get("signal"));
					table.addCell(signal);

					String occurance = (String) slide.get("count");
					table.addCell(occurance);

					String timestamp = String.valueOf(slide.get("timestamp"));
					table.addCell(timestamp);

				}
			}
		}

		subCatPart.add(table);
	}
}
