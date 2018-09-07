package com.semaifour.facesix.rest;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.boot.Application;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.device.ClientDevice;
import com.semaifour.facesix.data.elasticsearch.device.ClientDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.Device;
import com.semaifour.facesix.data.elasticsearch.device.DeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.HeaderFooterPageEvent;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/gatewayreport")
public class GatewayReport extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(GatewayReport.class.getName());
	
	
	static Font smallBold  = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	static Font catFont    = new Font(Font.FontFamily.HELVETICA,   16, Font.BOLD);
	static Font redFont    = new Font(Font.FontFamily.HELVETICA,   10, Font.NORMAL);
	static Font subFont    = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	static Font headerFont = new Font(Font.FontFamily.HELVETICA,   12, Font.BOLD);
    
    DateFormat format 			   = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	TimeZone timezone     		   = null;
	
    @Autowired
   	NetworkDeviceRestController 	networkDeviceRestController;
    
    @Autowired
	NetworkDeviceService 	networkDeviceService;
    
    @Autowired
	SiteService siteService;
    
	@Autowired
	PortionService portionService;
	
	@Autowired
	DeviceService devService;
	
	@Autowired
	CustomerService customerservice;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	CustomerUtils customerUtils;		
	
	@Autowired
	DeviceService deviceservice;
	
	@Autowired
	DeviceRestController deviceRestController;

	private String indexname = "facesix*";
	
	@Autowired
	FSqlRestController 		fsqlRestController;
	
	@Autowired
	ClientDeviceService  _clientDeviceService;
	
	String 	device_history_event = "device-history-event";
	
	@PostConstruct
	public void init() {
		indexname 		= _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		device_history_event = _CCC.properties.getProperty("facesix.device.event.history.table",device_history_event);
	}
	
	@RequestMapping(value = "/format", method = RequestMethod.GET)
	public String format(
			@RequestParam(value = "cid", 		required = false) String cid,
			@RequestParam(value = "venuename",  required = false) String sid,
			@RequestParam(value = "floorname",  required = false) String spid,
			@RequestParam(value = "location", 	required = false) String location,
			@RequestParam(value = "macaddr", 	required = false) String macaddr,
			@RequestParam(value = "filtertype", required = true)  String filtertype,
			@RequestParam(value = "time", 		required = false) String days,
			@RequestParam(value = "fileformat", required = true)  String fileformat,
			@RequestParam(value = "devStatus",  required = false) String devStatus,
			HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException {
		
			String result = "";
			
			if (fileformat.equals("pdf")) {
				result = pdf(cid,sid,spid,location,macaddr,filtertype,days,devStatus,request,response);
			} else {
				result = csv(cid,sid,spid,location,macaddr,filtertype,days,devStatus, request,response);
			}
		
		return result;
	}
	
	public String pdf(
					@RequestParam(value = "cid", 		required = false) String cid,
					@RequestParam(value = "venuename",  required = false) String sid,
					@RequestParam(value = "floorname",  required = false) String spid,
					@RequestParam(value = "location", 	required = false) String location,
					@RequestParam(value = "macaddr", 	required = false) String macaddr,
					@RequestParam(value = "filtertype", required = true)  String filtertype,
					@RequestParam(value = "time", 		required = false) String days,
					@RequestParam(value = "devStatus", required = false) String devStatus,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
		
		String pdfFileName  = "./uploads/qubercloud.pdf";
		String logoFileName = "./uploads/logo-home.png";
		
		//LOG.info("PDF func: cid "+cid+" sid "+sid+" spid "+spid + " macaddr "+macaddr+" time "+days);
		
		//String pdfFileName  = "Report.pdf";
		//String logoFileName = "/home/qubercomm/Desktop/pdf/logo.png";
		
		FileOutputStream os       		  = null; 
		FileInputStream fileInputStream   = null;
		OutputStream responseOutputStream = null;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			Document document = new Document(PageSize.A4, 36, 36, 90, 55);
			try {
				
				if (cid == null) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
					if (cid == null) {
						return null;
					}
				}
				
				String currentuser 		= SessionUtil.currentUser(request.getSession());
				UserAccount cur_user 	= userAccountService.findOneByEmail(currentuser);
				String userName 	    = cur_user.getFname() + " " + cur_user.getLname();

				Customer customer = customerservice.findById(cid);
				logoFileName = customer.getLogofile() == null ? logoFileName : customer.getLogofile();
				String customerName = customer.getCustomerName();
				
				Path path = Paths.get(logoFileName);
				
				if (!Files.exists(path)) {
					logoFileName = "./uploads/logo-home.png";
				}
				timezone = customerUtils.FetchTimeZone(customer.getTimezone());
				format.setTimeZone(timezone);
				
				os = new FileOutputStream(pdfFileName);
				PdfWriter writer = PdfWriter.getInstance(document, os);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(customerName, userName, logoFileName, format.format(new Date()));
				writer.setPageEvent(event);
				document.open();
			    
			    addContent (writer,cid, sid, spid, location, macaddr, filtertype, days, devStatus, document);			    
				
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
				if (responseOutputStream != null) {
					responseOutputStream.close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (os != null) {
					os.close();
				}
			}
		}
		return pdfFileName;
	}
	
	private void addContent(PdfWriter writer,String cid, String sid, String spid, String location, String macaddr, String filtertype,
			String days, String devStatus, Document document) throws DocumentException, IOException, ParseException {

		Paragraph subCatPart =  new Paragraph();
		document.add(subCatPart);
		createTable(writer,cid, sid, spid, location, macaddr, filtertype, days, devStatus, subCatPart, document);

	}
	  
	@SuppressWarnings({ "unchecked", "deprecation" })
	private  void createTable(PdfWriter writer,	String cid, String sid, String  spid, String  location, String  macaddr,
				String filtertype, String days, String  devStatus, Paragraph subCatPart, 
				Document document) throws IOException, ParseException, DocumentException {

		//LOG.info("CreateTable filtertype" + filtertype+" cid "+cid+" sid "+sid+" spid "+spid + " macaddr "+macaddr+ "location"+ location + " time "+days);

		if(filtertype.equals("deviceInfo")){
			
			Paragraph content = new Paragraph("Device Information",subFont);
			PdfPTable table   = new PdfPTable(9);
			table.setWidthPercentage(100);
		
			PdfPCell c1 = new PdfPCell(new Phrase("UID",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setColspan(2);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("FLOOR",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("DEVICE UPTIME",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("APP UPTIME",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("STATE",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("LAST SEEN",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setColspan(2);
			table.addCell(c1);
			
			table.setHeaderRows(1);
			
			if(sid != null && (sid.equals("all") || sid.equals("undefined"))){
				sid = "";
			}
			if(spid != null && (spid.equals("all") || spid.equals("undefined"))){
				spid = "";
			}
			
			
			JSONArray processedDetail 	= deviceInfo(cid, sid, spid, null, null);
			Iterator<JSONObject> iterProcessedDetail = processedDetail.iterator();
			JSONObject json = null;
			
			while(iterProcessedDetail.hasNext()) {
				
				json = iterProcessedDetail.next();

				String uid 			= json.get("uid").toString();
				String floorname 	= json.get("floorname").toString();
				String locationname = json.get("locationname").toString();
				String deviceUptime = json.get("deviceUptime").toString();
				String appUptime 	= json.get("appUptime").toString();
				String state 		= json.get("state").toString();
				String lastseen 	= json.get("lastseen").toString();
				
				c1 = new PdfPCell(new Phrase(uid,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				c1.setColspan(2);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(floorname,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(locationname,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(deviceUptime,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(appUptime,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(state,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase(lastseen,redFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				c1.setColspan(2);
				table.addCell(c1);
				
			}
			content.add(table);
			subCatPart.add(content);
			document.add(subCatPart);
			
		} else {

			List<Device> devices = null;
			
			JSONObject details		= null;
			boolean venuewise 		= false;
			boolean floorwise 		= false;
			boolean customerwise 	= false;
			
			
			switch (filtertype) {
			case "default":
				devices = deviceservice.findByCid(cid);
				break;
				
			case "venue":
				
				if (sid.isEmpty() || sid.equals("all"))  {
					devices = deviceservice.findByCid(cid);
					customerwise = true;
					details = reportDetails("cid",days,devices);
				} else {
					devices = deviceservice.findBySid(sid);
					venuewise = true;
					details = reportDetails("sid",days,devices);
				}
			
				break;
				
			case "floor":
				
				if (sid.isEmpty() || sid.equals("all")) {
					customerwise = true;
					devices = deviceservice.findByCid(cid);
					details = reportDetails("cid",days,devices);
				} else if(spid.isEmpty() || spid.equals("all")) {
					venuewise = true;
					devices = deviceservice.findBySid(sid);
					details = reportDetails("sid",days,devices);
				} else {
					floorwise = true;
					devices = deviceservice.findBySpid(spid);
					details = reportDetails("spid",days,devices);
				}
				break;
				
			case "location":
				
				if (sid.isEmpty() || sid.equals("all")) {
					customerwise = true;
					devices = deviceservice.findByCid(cid);
					details = reportDetails("cid",days,devices);
				} else if(spid.isEmpty() || spid.equals("all")) {
					venuewise = true;
					devices = deviceservice.findBySid(sid);
					details = reportDetails("sid",days,devices);
				} else if (location.isEmpty() || location.equals("all")) {
					floorwise = true;
					devices = deviceservice.findBySpid(spid);
					details = reportDetails("spid",days,devices);
				} else {
					devices = deviceservice.findByUid(location);
					details = reportDetails("uid",days,devices);
				}
				break;
				
			case "devStatus":
				devices = (List)getDeviceByCidAndStatus(cid, devStatus);
			}
			
			/*
			 *  AGG Device TX,RX,STATION CLIENTS
			 * 
			 */
			
			PdfPTable agg_dev_tx_rx_Table = new PdfPTable(2);
			agg_dev_tx_rx_Table.setWidthPercentage(100);
			
			PdfPCell a1 = new PdfPCell(new Phrase("TOTAL TX BYTES ",headerFont));
			a1.setHorizontalAlignment(Element.ALIGN_CENTER);
			agg_dev_tx_rx_Table.addCell(a1);

			a1 = new PdfPCell(new Phrase("TOTAL RX BYTES",headerFont));
			a1.setHorizontalAlignment(Element.ALIGN_CENTER);
			agg_dev_tx_rx_Table.addCell(a1);

			agg_dev_tx_rx_Table.setHeaderRows(1);
			
			/*
			 *  DEVICE WISSE TX RX TABLE 
			 * 
			 */
			
			PdfPTable dev_tx_rx_Table = new PdfPTable(4);
			dev_tx_rx_Table.setWidthPercentage(100);
			
			PdfPCell b1 = new PdfPCell(new Phrase("UID ",headerFont));
			b1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_tx_rx_Table.addCell(b1);

			b1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			b1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_tx_rx_Table.addCell(b1);
			
			b1 = new PdfPCell(new Phrase("TX BYTES ",headerFont));
			b1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_tx_rx_Table.addCell(b1);

			b1 = new PdfPCell(new Phrase("RX BYTES",headerFont));
			b1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_tx_rx_Table.addCell(b1);


			dev_tx_rx_Table.setHeaderRows(1);
			
			/*
			 *   2G AND 5G CLIENTS TABLE
			 * 
			 */
			
			PdfPTable avg_2G_5G_Table = new PdfPTable(2);
			avg_2G_5G_Table.setWidthPercentage(100);
			
			PdfPCell c1 = new PdfPCell(new Phrase("AVG 2G",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			avg_2G_5G_Table.addCell(c1);

			c1 = new PdfPCell(new Phrase("AVG 5G",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			avg_2G_5G_Table.addCell(c1);

			/*
			 *  AP wise avg 2G and 5G
			 * 
			 * 
			 */
			
			PdfPTable device_avg_2G_5G_Table = new PdfPTable(4);
			device_avg_2G_5G_Table.setWidthPercentage(100);
			
			PdfPCell d1 = new PdfPCell(new Phrase("UID ",headerFont));
			d1.setHorizontalAlignment(Element.ALIGN_CENTER);
			device_avg_2G_5G_Table.addCell(d1);

			d1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			d1.setHorizontalAlignment(Element.ALIGN_CENTER);
			device_avg_2G_5G_Table.addCell(d1);
		
			d1 = new PdfPCell(new Phrase("AVG 2G",headerFont));
			d1.setHorizontalAlignment(Element.ALIGN_CENTER);
			device_avg_2G_5G_Table.addCell(d1);

			d1 = new PdfPCell(new Phrase("AVG 5G",headerFont));
			d1.setHorizontalAlignment(Element.ALIGN_CENTER);
			device_avg_2G_5G_Table.addCell(d1);

			device_avg_2G_5G_Table.setHeaderRows(1);
			
			/*
			 *  MIN AND MAX 2G AND 5G
			 */
			
			PdfPTable min_max_2G_5G_Table = new PdfPTable(4);
			min_max_2G_5G_Table.setWidthPercentage(100);
			
			PdfPCell e1 = new PdfPCell(new Phrase("MIN 2G ",headerFont));
			e1.setHorizontalAlignment(Element.ALIGN_CENTER);
			min_max_2G_5G_Table.addCell(e1);

			e1 = new PdfPCell(new Phrase("MAX 2G ",headerFont));
			e1.setHorizontalAlignment(Element.ALIGN_CENTER);
			min_max_2G_5G_Table.addCell(e1);

			e1 = new PdfPCell(new Phrase("MIN 5G ",headerFont));
			e1.setHorizontalAlignment(Element.ALIGN_CENTER);
			min_max_2G_5G_Table.addCell(e1);

			e1 = new PdfPCell(new Phrase("MAX 5G ",headerFont));
			e1.setHorizontalAlignment(Element.ALIGN_CENTER);
			min_max_2G_5G_Table.addCell(e1);

			min_max_2G_5G_Table.setHeaderRows(1);
			
			/*
			 *  STATION WISSE MIN AND MAX 25 AND 5G
			 */
			
			PdfPTable dev_min_max_2G_5G_Table = new PdfPTable(6);
			dev_min_max_2G_5G_Table.setWidthPercentage(100);
			
			PdfPCell f1 = new PdfPCell(new Phrase("UID ",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);

			f1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);
			
			f1 = new PdfPCell(new Phrase("MIN 2G ",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);

			f1 = new PdfPCell(new Phrase("MAX 2G ",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);

			f1 = new PdfPCell(new Phrase("MIN 5G ",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);

			f1 = new PdfPCell(new Phrase("MAX 5G ",headerFont));
			f1.setHorizontalAlignment(Element.ALIGN_CENTER);
			dev_min_max_2G_5G_Table.addCell(f1);

			dev_min_max_2G_5G_Table.setHeaderRows(1);
			
			/*
			 *  LOCATION WISE CLIENT COUNT AND BANDWIDTH
			 * 
			 */
		
			PdfPTable client_count_bandwidth_Table = new PdfPTable(6);
			client_count_bandwidth_Table.setWidthPercentage(100);
			
			PdfPCell g1 = new PdfPCell(new Phrase("UID ",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);

			g1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);
			
			g1 = new PdfPCell(new Phrase("TX BYTES ",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);

			g1 = new PdfPCell(new Phrase("RX BYTES",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);

			g1 = new PdfPCell(new Phrase("AVG 2G",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);

			g1 = new PdfPCell(new Phrase("AVG 5G",headerFont));
			g1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_count_bandwidth_Table.addCell(g1);

			client_count_bandwidth_Table.setHeaderRows(1);
			
			/*
			 *  11K ,11R AND 11V COUNT
			 * 
			 */
		
			PdfPTable client_Capability_Table = new PdfPTable(5);
			client_Capability_Table.setWidthPercentage(100);
			
			PdfPCell h1 = new PdfPCell(new Phrase("UID ",headerFont));
			h1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_Capability_Table.addCell(h1);

			h1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			h1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_Capability_Table.addCell(h1);
			
			h1 = new PdfPCell(new Phrase("11K ",headerFont));
			h1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_Capability_Table.addCell(h1);

			h1 = new PdfPCell(new Phrase("11R",headerFont));
			h1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_Capability_Table.addCell(h1);

			h1 = new PdfPCell(new Phrase("11V",headerFont));
			h1.setHorizontalAlignment(Element.ALIGN_CENTER);
			client_Capability_Table.addCell(h1);

			client_Capability_Table.setHeaderRows(1);
			
			/*
			 *  TOP 5 CLIENT CONSUMED TX  
			 * 
			 */
		
			PdfPTable top_5_clients_consumed_TX_Table = new PdfPTable(2);
			top_5_clients_consumed_TX_Table.setWidthPercentage(100);
			
			PdfPCell i1 = new PdfPCell(new Phrase("MAC ID  ",headerFont));
			i1.setHorizontalAlignment(Element.ALIGN_CENTER);
			top_5_clients_consumed_TX_Table.addCell(i1);

			i1 = new PdfPCell(new Phrase("TX",headerFont));
			i1.setHorizontalAlignment(Element.ALIGN_CENTER);
			top_5_clients_consumed_TX_Table.addCell(i1);
			
			top_5_clients_consumed_TX_Table.setHeaderRows(1);
			/*
			 *  TOP 5 CLIENT CONSUMED RX  
			 * 
			 */
		
			PdfPTable top_5_clients_RX_Table = new PdfPTable(2);
			top_5_clients_RX_Table.setWidthPercentage(100);
			
			PdfPCell j1 = new PdfPCell(new Phrase("MAC ID  ",headerFont));
			j1.setHorizontalAlignment(Element.ALIGN_CENTER);
			top_5_clients_RX_Table.addCell(j1);

			j1 = new PdfPCell(new Phrase("RX",headerFont));
			j1.setHorizontalAlignment(Element.ALIGN_CENTER);
			top_5_clients_RX_Table.addCell(j1);
			
			top_5_clients_RX_Table.setHeaderRows(1);
			
			final DefaultCategoryDataset dev_tx_rx_Dataset = new DefaultCategoryDataset();
			
			Image devtxrxbarChartImage 		= null;
			
			final DefaultCategoryDataset client_capability_Dataset = new DefaultCategoryDataset();
			Image client_capability_barChartImage 		= null;
			
			String title = "LOCATION";

			if (customerwise) {
				title = "CUSTOMER";
			} else if (venuewise) {
				title = "VENUE";
			} else if (floorwise) {
				title = "FLOOR";
			}
			
			if (details != null && details.size() > 0) {
				
				if (details.containsKey("agg_txrx")) {

					JSONObject JStr = (JSONObject)details.get("agg_txrx");
					
					String agg_tx = (String)JStr.get("agg_tx");
					String agg_rx = (String)JStr.get("agg_rx");
					
					
					agg_tx  = CustomerUtils.formatFileSize(Long.valueOf(agg_tx));
					agg_rx  = CustomerUtils.formatFileSize(Long.valueOf(agg_rx));
					
					a1 = new PdfPCell(new Phrase(agg_tx,redFont));
					a1.setHorizontalAlignment(Element.ALIGN_CENTER);
					agg_dev_tx_rx_Table.addCell(a1);
					
					a1 = new PdfPCell(new Phrase(agg_rx,redFont));
					a1.setHorizontalAlignment(Element.ALIGN_CENTER);
					agg_dev_tx_rx_Table.addCell(a1);
					
				}
				/*
				 *  Device wises agg Tx and Rx
				 * 
				 */
				
				 
				if (details.containsKey("dev_txrx")) {

					JSONArray JStr = (JSONArray)details.get("dev_txrx");
					Iterator<JSONObject> obj = JStr.iterator();
					
					final String series1 = "TX";
					final String series2 = "RX";
			        
					while (obj.hasNext()) {
						
						JSONObject dataObject = obj.next();
						
						String agg_tx = (String)dataObject.get("tx");
						String agg_rx = (String)dataObject.get("rx");
						
						final String uid = (String)dataObject.get("uid");
						final String alias = (String)dataObject.get("alias");
						
						final String category1 = alias;
						
						agg_tx  = CustomerUtils.formatFileSize(Long.valueOf(agg_tx));
						agg_rx  = CustomerUtils.formatFileSize(Long.valueOf(agg_rx));
						
						 dev_tx_rx_Dataset.addValue(Double.valueOf(agg_tx.split("\\s+")[0]), series1, category1);
					     dev_tx_rx_Dataset.addValue(Double.valueOf(agg_rx.split("\\s+")[0]), series2, category1);
						
						b1 = new PdfPCell(new Phrase(uid,redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_tx_rx_Table.addCell(b1);
						
						b1 = new PdfPCell(new Phrase(alias,redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_tx_rx_Table.addCell(b1);
						
						b1 = new PdfPCell(new Phrase(agg_tx,redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_tx_rx_Table.addCell(b1);
						
						b1 = new PdfPCell(new Phrase(agg_rx,redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_tx_rx_Table.addCell(b1);
						
					}
					
			        final JFreeChart chart = ChartFactory.createBarChart(
			        	"CONSUMED TX AND RX",
			            "Location",
			            "TX and RX (MB)",   
			            dev_tx_rx_Dataset,
			            PlotOrientation.VERTICAL,
			            true, 
			            true, 
			            false 
			        );
			        
			        chart.setBackgroundPaint(new Color(255, 255, 255));
			        CategoryPlot plot = chart.getCategoryPlot();
			       // plot.getRenderer().setSeriesPaint(0, new Color(128, 0, 0));
			       // plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 255));
			        
			        CategoryItemRenderer renderer = ((CategoryPlot)chart.getPlot()).getRenderer();
			        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			        renderer.setBaseItemLabelsVisible(true);
			        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, 
			                TextAnchor.TOP_CENTER);
			        renderer.setBasePositiveItemLabelPosition(position);
			        renderer.setItemLabelPaint(Color.BLACK);
			        
			        plot.setDataset(1, dev_tx_rx_Dataset);
			        plot.mapDatasetToRangeAxis(1, 1);		
			        
					BufferedImage bufferedImage = chart.createBufferedImage(500, 430);
					devtxrxbarChartImage = Image.getInstance(writer, bufferedImage, 1.0f);
				}
				if (details.containsKey("avg_clients")) {

					JSONObject JStr = (JSONObject)details.get("avg_clients");
					
					final String avg_2g = (String)JStr.get("avg_2g");
					final String avg_5g = (String)JStr.get("avg_5g");
					
					c1 = new PdfPCell(new Phrase(avg_2g,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					avg_2G_5G_Table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(avg_5g,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					avg_2G_5G_Table.addCell(c1);
				}
			
				/*
				 * Station wises avg 2G and 5G
				 * 
				 */
				
				if (details.containsKey("dev_txrx")) {
					
					JSONArray JStr = (JSONArray) details.get("dev_txrx");
					Iterator<JSONObject> obj = JStr.iterator();
	
					while (obj.hasNext()) {
	
						JSONObject dataObject = obj.next();
						
						final String avg_2g = (String)dataObject.get("avg_2g");
						final String avg_5g = (String)dataObject.get("avg_5g");
						final String uid 	= (String)dataObject.get("uid");
						final String alias = (String)dataObject.get("alias");
						
						f1 = new PdfPCell(new Phrase(uid,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						device_avg_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(alias,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						device_avg_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(avg_2g,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						device_avg_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(avg_5g,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						device_avg_2G_5G_Table.addCell(f1);

					}
				}
				
				if (details.containsKey("min_max_clients")) {

					JSONObject JStr = (JSONObject)details.get("min_max_clients");
					
					final int min_2g = (int)JStr.get("min_2g");
					final int max_2g = (int)JStr.get("max_2g");
					
					final int min_5g = (int)JStr.get("min_5g");
					final int max_5g = (int)JStr.get("max_5g");
					
					
					e1 = new PdfPCell(new Phrase(String.valueOf(min_2g),redFont));
					e1.setHorizontalAlignment(Element.ALIGN_CENTER);
					min_max_2G_5G_Table.addCell(e1);
					
					e1 = new PdfPCell(new Phrase(String.valueOf(max_2g),redFont));
					e1.setHorizontalAlignment(Element.ALIGN_CENTER);
					min_max_2G_5G_Table.addCell(e1);
					
					
					e1 = new PdfPCell(new Phrase(String.valueOf(min_5g),redFont));
					e1.setHorizontalAlignment(Element.ALIGN_CENTER);
					min_max_2G_5G_Table.addCell(e1);
					
					e1 = new PdfPCell(new Phrase(String.valueOf(max_5g),redFont));
					e1.setHorizontalAlignment(Element.ALIGN_CENTER);
					min_max_2G_5G_Table.addCell(e1);
					
				}
				
				if (details.containsKey("dev_txrx")) {
					
					JSONArray JStr = (JSONArray)details.get("dev_txrx");
					Iterator<JSONObject> obj = JStr.iterator();
					
					DecimalFormat decimalFormat = new DecimalFormat("#.##");
					
					while (obj.hasNext()) {
						
						JSONObject dataObject = obj.next();
						
						final double min2g = (double)dataObject.get("min_2g");
						final double max2g = (double)dataObject.get("max_2g");
						final double min5g = (double)dataObject.get("min_5g");
						final double max_5g = (double)dataObject.get("max_5g");
						
						
						final String uid = (String)dataObject.get("uid");
						final String alias = (String)dataObject.get("alias");
						
						f1 = new PdfPCell(new Phrase(uid,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(alias,redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
						
						
						f1 = new PdfPCell(new Phrase(decimalFormat.format(min2g),redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(decimalFormat.format(max2g),redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(decimalFormat.format(min5g),redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
						
						f1 = new PdfPCell(new Phrase(decimalFormat.format(max_5g),redFont));
						f1.setHorizontalAlignment(Element.ALIGN_CENTER);
						dev_min_max_2G_5G_Table.addCell(f1);
					}
				}
				if (details.containsKey("dev_txrx")) {

					JSONArray JStr = (JSONArray)details.get("dev_txrx");
					Iterator<JSONObject> obj = JStr.iterator();
					
					while (obj.hasNext()) {
						
						JSONObject dataObject = obj.next();
						
						String tx = (String)dataObject.get("tx");
						String rx = (String)dataObject.get("rx");
						
						final String _2g = (String)dataObject.get("avg_2g");
						final String _5g = (String)dataObject.get("avg_5g");
						final String uid = (String)dataObject.get("uid");
						final String alias = (String)dataObject.get("alias");
						
						tx  = CustomerUtils.formatFileSize(Long.valueOf(tx));
						rx  = CustomerUtils.formatFileSize(Long.valueOf(rx));
						
						g1 = new PdfPCell(new Phrase(uid,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(g1);
						
						g1 = new PdfPCell(new Phrase(alias,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(g1);
						
						b1 = new PdfPCell(new Phrase(tx,redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(b1);
						
						g1 = new PdfPCell(new Phrase(rx,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(g1);
						
						g1 = new PdfPCell(new Phrase(_2g.toUpperCase(),redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(g1);
						
						g1 = new PdfPCell(new Phrase(_5g,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_count_bandwidth_Table.addCell(g1);

					}

				}
				if (details.containsKey("dev_txrx")) {

					JSONArray JStr = (JSONArray)details.get("dev_txrx");
					Iterator<JSONObject> obj = JStr.iterator();
					
					while (obj.hasNext()) {
						
						JSONObject dataObject = obj.next();
						
						final int _11k = (int)dataObject.get("_11k");
						final int _11r = (int)dataObject.get("_11r");
						final int _11v = (int)dataObject.get("_11v");
						
						final String uid = (String)dataObject.get("uid");
						final String alias = (String)dataObject.get("alias");
						
						client_capability_Dataset.addValue(_11k, "11K", alias);
						client_capability_Dataset.addValue(_11r, "11R", alias);
						client_capability_Dataset.addValue(_11v, "11V", alias);
					     
						
						g1 = new PdfPCell(new Phrase(uid,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_Capability_Table.addCell(g1);
						
						g1 = new PdfPCell(new Phrase(alias,redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_Capability_Table.addCell(g1);
						
						b1 = new PdfPCell(new Phrase(String.valueOf(_11k),redFont));
						b1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_Capability_Table.addCell(b1);
						
						g1 = new PdfPCell(new Phrase(String.valueOf(_11r),redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_Capability_Table.addCell(g1);
						
						g1 = new PdfPCell(new Phrase(String.valueOf(_11v),redFont));
						g1.setHorizontalAlignment(Element.ALIGN_CENTER);
						client_Capability_Table.addCell(g1);

					}
					
					 final JFreeChart chart = ChartFactory.createBarChart(
					            "CLIENT CAPBALITIY(11K,11R,11V)",
					            "Location",
					            "11K,11R,11V",
					            client_capability_Dataset,
					            PlotOrientation.VERTICAL,
					            true, 
					            true, 
					            false
					        );
					        
					        chart.setBackgroundPaint(Color.white);

					        final CategoryPlot plot = chart.getCategoryPlot();
					        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
					        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

					        CategoryItemRenderer renderer = ((CategoryPlot)chart.getPlot()).getRenderer();
					        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
					        renderer.setBaseItemLabelsVisible(true);
					        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, 
					                TextAnchor.TOP_CENTER);
					        renderer.setBasePositiveItemLabelPosition(position);
					        renderer.setItemLabelPaint(Color.BLACK);
					      				        
					        plot.setDataset(1, client_capability_Dataset);
					        plot.mapDatasetToRangeAxis(1, 1);			       

							BufferedImage bufferedImage = chart.createBufferedImage(520, 400);
							client_capability_barChartImage = Image.getInstance(writer, bufferedImage, 1.0f);

				}
				
				if (details.containsKey("top5clients_consumed_tx_rx")) {

					JSONArray data = (JSONArray) details.get("top5clients_consumed_tx_rx");

					final String sortbyTX = "peer_tx";
					
					List<net.sf.json.JSONObject> jsonListdata = sortByValue(data, sortbyTX);

					Iterator<net.sf.json.JSONObject> it = jsonListdata.iterator();

					while (it.hasNext()) {
						net.sf.json.JSONObject obj = it.next();
						
						String macAddress = (String) obj.get("peer_mac");
						int peer_tx = (int) obj.get("peer_tx");

						String strTx  = CustomerUtils.formatFileSize(Long.valueOf(peer_tx));
						
						i1 = new PdfPCell(new Phrase(macAddress, redFont));
						i1.setHorizontalAlignment(Element.ALIGN_CENTER);
						top_5_clients_consumed_TX_Table.addCell(i1);

						i1 = new PdfPCell(new Phrase(strTx, redFont));
						i1.setHorizontalAlignment(Element.ALIGN_CENTER);
						top_5_clients_consumed_TX_Table.addCell(i1);

					}
					
					final String sortbyRX = "peer_rx";

					JSONArray rxData = (JSONArray) details.get("top5clients_consumed_tx_rx");

					List<net.sf.json.JSONObject> jsonrxData = sortByValue(rxData, sortbyRX);

					Iterator<net.sf.json.JSONObject> rxDataIt = jsonrxData.iterator();
					
					while (rxDataIt.hasNext()) {
						net.sf.json.JSONObject obj = rxDataIt.next();

						String macAddress = (String) obj.get("peer_mac");
						int peer_rx 	  = (int) obj.get("peer_rx");

						String strRx  = CustomerUtils.formatFileSize(Long.valueOf(peer_rx));
						
						j1 = new PdfPCell(new Phrase(macAddress, redFont));
						j1.setHorizontalAlignment(Element.ALIGN_CENTER);
						top_5_clients_RX_Table.addCell(j1);

						j1 = new PdfPCell(new Phrase(strRx, redFont));
						j1.setHorizontalAlignment(Element.ALIGN_CENTER);
						top_5_clients_RX_Table.addCell(j1);

					}
				}
				
				Paragraph Para =  null;
				
				if (agg_dev_tx_rx_Table.size() > 1) {
					Para = new Paragraph(title + " TOTAL CONSUMED TX AND RX ", subFont);
					addEmptyLine(Para, 1);
					Para.add(agg_dev_tx_rx_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}

				if (dev_tx_rx_Table.size() > 1) {
					Para = new Paragraph("LOCATION CONSUMED TX AND RX", subFont);
					addEmptyLine(Para, 1);
					Para.add(dev_tx_rx_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
					
					Paragraph para1=  new Paragraph();
					Para = new Paragraph(" LOCATION TOTAL TX AND RX", subFont);
					para1.add(devtxrxbarChartImage);
					para1.setAlignment(Element.ALIGN_LEFT);
					addEmptyLine(para1, 1);
					Para.add(para1);
					addEmptyLine(para1, 2);
					document.add(para1);
				}
						
				if (avg_2G_5G_Table.size() > 1) {
					Para = new Paragraph(title + " AVG 2G/5G CLIENTS CONNECTED", subFont);
					addEmptyLine(Para, 1);
					Para.add(avg_2G_5G_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				
				if (device_avg_2G_5G_Table.size() > 1) {
					Para = new Paragraph(" LOCATION AVG 2G/5G CLIENTS", subFont);
					addEmptyLine(Para, 1);
					Para.add(device_avg_2G_5G_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				
				if (min_max_2G_5G_Table.size() > 1) {
					Para = new Paragraph(title + " MIN AND  MAX 2G/5G CLIENTS", subFont);
					addEmptyLine(Para, 1);
					Para.add(min_max_2G_5G_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				
				if (dev_min_max_2G_5G_Table.size() > 1) {
					Para = new Paragraph(" LOCATION MIN AND MAX 2G/5G CLIENTS", subFont);
					addEmptyLine(Para, 1);
					Para.add(dev_min_max_2G_5G_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				if (client_count_bandwidth_Table.size() > 1) {
					Para = new Paragraph(" LOCATION CLIENTS COUNT AND BANDWIDTH", subFont);
					addEmptyLine(Para, 1);
					Para.add(client_count_bandwidth_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				if (client_Capability_Table.size() > 1) {
					Para = new Paragraph(" LOCATION CLIENTS CAPBALITIY ", subFont);
					addEmptyLine(Para, 1);
					Para.add(client_Capability_Table);
					addEmptyLine(Para, 1);
					document.add(Para);

					Paragraph para1 = new Paragraph();
					Para = new Paragraph(title + " TOTAL 11K,11R AND 11V", subFont);
					para1.add(client_capability_barChartImage);
					para1.setAlignment(Element.ALIGN_LEFT);
					addEmptyLine(para1, 1);
					Para.add(para1);
					addEmptyLine(para1, 2);
					document.add(para1);

				}
				if (top_5_clients_consumed_TX_Table.size() > 1) {
					Para = new Paragraph(title + " TOP 5 CLIENTS CONSUMED TX ", subFont);
					addEmptyLine(Para, 1);
					Para.add(top_5_clients_consumed_TX_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
				if (top_5_clients_RX_Table.size() > 1) {
					Para = new Paragraph(title + " TOP 5 CLIENTS CONSUMED RX ", subFont);
					addEmptyLine(Para, 1);
					Para.add(top_5_clients_RX_Table);
					addEmptyLine(Para, 1);
					document.add(Para);
				}
			}

		}
	}
	
	private List<net.sf.json.JSONObject> sortByValue(JSONArray data,final String key) {
		
		net.sf.json.JSONArray formatData =net.sf.json.JSONArray.fromObject(data);
		
		List<net.sf.json.JSONObject> jsonValues = new ArrayList<net.sf.json.JSONObject>();
		for (int i = 0; i < formatData.size(); i++) {
			jsonValues.add(formatData.getJSONObject(i));
		}
		
	    Collections.sort(jsonValues, new Comparator<net.sf.json.JSONObject>() {
	        @Override
	        public int compare(net.sf.json.JSONObject a, net.sf.json.JSONObject b) {
	            String valA = String.valueOf(a.get(key));
	            String valB = String.valueOf(b.get(key));
	            return Integer.valueOf(valB).compareTo(Integer.valueOf(valA));
	        }
	    });
		
		int to = jsonValues.size() > 5 ? 5 : jsonValues.size();
	    
	    List<net.sf.json.JSONObject> sortedList = jsonValues.subList(0, to);
	   
		
		return sortedList;

	}
	

	private Iterable<Device> getDeviceByCidAndStatus(String cid, String devStatus) {
		
		Iterable<Device> device = null;
		
		if (devStatus.equals("all")) {
			device = deviceservice.findByCid(cid);
		} else {
			device = deviceservice.findByCidAndState(cid, devStatus);
		}
		
		return device;
	}

	public String csv(
			@RequestParam(value = "cid", 		required = false) String cid,
			@RequestParam(value = "venuename",  required = false) String sid,
			@RequestParam(value = "floorname",  required = false) String spid,
			@RequestParam(value = "location", 	required = false) String location,
			@RequestParam(value = "macaddr", 	required = false) String macaddr,
			@RequestParam(value = "filtertype", required = true)  String filtertype,
			@RequestParam(value = "time", 		required = false) String days,
			@RequestParam(value = "devStatus", required = false) String devStatus,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//LOG.info("Csvfunc filtertype" + filtertype+" cid "+cid+" sid "+sid+" spid "+spid + " macaddr "+macaddr+ "location"+ location + " time "+days);
		
		String csvFileName  = "Report.csv";
		
		
		OutputStream out = null;
		
		try {
			if (SessionUtil.isAuthorized(request.getSession())) {
				String result = "";
				
				if(sid != null && (sid.equals("all") || sid.equals("undefined"))){
					sid = "";
				}
				if(spid != null && (spid.equals("all") || spid.equals("undefined"))){
					spid = "";
				}
				
				JSONObject details  = null;
				
				if (filtertype.equals("deviceInfo")) {

					result = "UID,FLOOR,LOCATION,DEVICE UPTIME,APP UPTIME,STATE,LAST SEEN\n";
					JSONArray processedDetail = deviceInfo(cid, sid, spid, request, response);
					Iterator<JSONObject> iterProcessedDetail = processedDetail.iterator();
					JSONObject json = null;
					while(iterProcessedDetail.hasNext()){
						json 				= iterProcessedDetail.next();
						String uid 			= json.get("uid").toString();
						String floorname 	= json.get("floorname").toString();
						String locationname = json.get("locationname").toString();
						String deviceUptime = json.get("deviceUptime").toString();
						String appUptime 	= json.get("appUptime").toString();
						String state 		= json.get("state").toString();
						String lastseen 	= json.get("lastseen").toString();
						result += uid+","+floorname+","+locationname+","+deviceUptime+","+appUptime+","+state+","+lastseen+"\n";
					}
				} else {
					
					if (days==null)
						days = "12h";
					
					boolean venuewisse 		= false;
					boolean floorwisse 		= false;
					boolean locationwisse 	= false;
					boolean customerwisse 	= false;

					List<Device> devices = null;
					
					switch (filtertype)
					{
					case "default":
						devices = deviceservice.findByCid(cid);
						details = reportDetails("cid",days,devices);
						customerwisse  = true;
						break;
						
					case "venue":
						
						if (sid.isEmpty() || sid.equals("all")) {
							devices = deviceservice.findByCid(cid);
							details = reportDetails("sid",days,devices);
							customerwisse  = true;
						} else {
							devices = deviceservice.findBySid(sid);
							details = reportDetails("sid",days,devices);
							venuewisse = true;
						}
							
						break;
						
					case "floor":
						
						if (sid.isEmpty() || sid.equals("all")) {
							devices = deviceservice.findByCid(cid);
							details = reportDetails("cid",days,devices);
							customerwisse  = true;
						}
							
						else if(spid.isEmpty() || spid.equals("all")) {
							devices = deviceservice.findBySid(sid);
							details = reportDetails("sid",days,devices);
							venuewisse = true;
						} else {
							devices = deviceservice.findBySpid(spid);
							details = reportDetails("spid",days,devices);
							floorwisse = true;
						}
						break;
						
					case "location":
						
						if (sid.isEmpty() || sid.equals("all")) {
							devices = deviceservice.findByCid(cid);
							details = reportDetails("cid",days,devices);
							customerwisse  = true;
						} else if(spid.isEmpty() || spid.equals("all")) {
							devices = deviceservice.findBySid(sid);
							details = reportDetails("sid",days,devices);
							venuewisse = true;
						} else if (location.isEmpty() || location.equals("all")){
							devices = deviceservice.findBySpid(spid);
							details = reportDetails("spid",days,devices);
							floorwisse = true;
						}else {
							devices = deviceservice.findByUid(location);
							details = reportDetails("uid",days,devices);
							locationwisse = true;
						}
						break;
						
					case "devStatus":
						devices = (List<Device>)getDeviceByCidAndStatus(cid, devStatus);
						customerwisse  = true;
					}
					
					
					String title = "LOCATION";

					if (customerwisse) {
						title = "CUSTOMER";
					} else if (venuewisse) {
						title = "VENUE";
					} else if (floorwisse) {
						title = "FLOOR";
					}
					
					if (details != null) {

						if (details.containsKey("agg_txrx")) {

							String header1 = "TX,RX,";
							String data1   = "";
							String Device  = "";
							
							JSONObject JStr = (JSONObject)details.get("agg_txrx");
							
							String agg_tx = (String)JStr.get("agg_tx");
							String agg_rx = (String)JStr.get("agg_rx");
							
							
							agg_tx  = CustomerUtils.formatFileSize(Long.valueOf(agg_tx));
							agg_rx  = CustomerUtils.formatFileSize(Long.valueOf(agg_rx));
							
							Device = agg_tx + "," + agg_rx + "," + "\n";
							data1  = data1.concat(Device);
							
							result = result.concat(title + " TOTAL CONSUMED TX AND RX");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);
							
							result = result.concat("\n\n");
							
						}
						/*
						 *  Device wises agg tx rx
						 * 
						 */
						
						if (details.containsKey("dev_txrx")) {

							JSONArray JStr = (JSONArray)details.get("dev_txrx");
							Iterator<JSONObject> obj = JStr.iterator();
							
							String header1 = "UID,ALIAS,TX,RX,";
							String data1   = "";
							String Device  = "";
					        
							
							while (obj.hasNext()) {
								
								JSONObject dataObject = obj.next();
								
								String agg_tx = (String)dataObject.get("tx");
								String agg_rx = (String)dataObject.get("rx");
								
								final String uid = (String)dataObject.get("uid");
								final String alias = (String)dataObject.get("alias");
								
								agg_tx  = CustomerUtils.formatFileSize(Long.valueOf(agg_tx));
								agg_rx  = CustomerUtils.formatFileSize(Long.valueOf(agg_rx));
								
								Device = uid + "," +alias + "," + agg_tx + "," + agg_rx + "," + "\n";
								data1 = data1.concat(Device);
								
								
							}
							
							result = result.concat("LOCATION CONSUMED TX AND RX");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);
							
							result = result.concat("\n\n");
							
						}
						if (details.containsKey("avg_clients")) {

							String header1 = "AVG 2G,AVG 5G,";
							String data1   = "";
							String Device  = "";
							
							JSONObject JStr = (JSONObject)details.get("avg_clients");
							
							final String avg_2g = (String)JStr.get("avg_2g");
							final String avg_5g = (String)JStr.get("avg_5g");
							
							Device = avg_2g + "," +avg_5g + "," + "\n";
							data1  = data1.concat(Device);
							
							result = result.concat(title + " AVG 2G/5G CLIENTS CONNECTED");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);
							
							result = result.concat("\n\n");
							
						}					
						/*
						 * Station wises avg 2g and 5g
						 * 
						 */
						
						if (details.containsKey("dev_txrx")) {
							
							JSONArray JStr = (JSONArray) details.get("dev_txrx");
							Iterator<JSONObject> obj = JStr.iterator();
			
							String header1 = "UID,ALIAS,AVG 2G,AVG 5G,";
							String data1   = "";
							String Device  = "";
							
							while (obj.hasNext()) {
			
								JSONObject dataObject = obj.next();
								
								final String avg_2g = (String)dataObject.get("avg_2g");
								final String avg_5g = (String)dataObject.get("avg_5g");
								final String uid 	= (String)dataObject.get("uid");
								final String alias = (String)dataObject.get("alias");
								
								Device = uid + "," + alias + "," + avg_2g + "," + avg_5g + "," + "\n";
								data1 = data1.concat(Device);
								
								
							}
							
							result = result.concat("LOCATION AVG 2G/5G CLIENTS");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);
							
							result = result.concat("\n\n");
						}
						
						if (details.containsKey("min_max_clients")) {

							JSONObject JStr = (JSONObject)details.get("min_max_clients");
							
							String data1   = "";
							String header1 = "MIN 2G,MAX 2G,MIN 5G,MAX 5G,";
							String Device  = "";
							
							final int min_2g = (int)JStr.get("min_2g");
							final int max_2g = (int)JStr.get("max_2g");
							
							final int min_5g = (int)JStr.get("min_5g");
							final int max_5g = (int)JStr.get("max_5g");
							
							Device = min_2g + "," + max_2g + "," + min_5g + "," + max_5g + "," + "\n";
							data1 = data1.concat(Device);
							
							result = result.concat(title + " MIN AND MAX 2G/5G CLIENTS");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);

							result = result.concat("\n\n");

						}

						if (details.containsKey("dev_txrx")) {
							
							JSONArray JStr = (JSONArray)details.get("dev_txrx");
							Iterator<JSONObject> obj = JStr.iterator();

							DecimalFormat decimalFormat = new DecimalFormat("#.##");
							
							String data1   = "";
							String header1 = "UID,ALIAS,MIN 2G,MAX 2G,MIN 5G,MAX 5G,";
							String Device  = "";
							
							while (obj.hasNext()) {
								
								JSONObject dataObject = obj.next();
								
								final double min2g = (double)dataObject.get("min_2g");
								final double max2g = (double)dataObject.get("max_2g");
								final double min5g = (double)dataObject.get("min_5g");
								final double max5g = (double)dataObject.get("max_5g");
								
								
								final String uid = (String)dataObject.get("uid");
								final String alias = (String)dataObject.get("alias");
								
								Device = uid + "," + alias + "," + decimalFormat.format(min2g) + "," 
								+ decimalFormat.format(max2g) + "," + decimalFormat.format(min5g) 
								+ "," + decimalFormat.format(max5g)+ ","+ "\n";
								
								data1 = data1.concat(Device);	
								
							}
							
							result = result.concat("LOCATION MIN AND MAX 2G/5G CLIENTS");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);

							result = result.concat("\n\n");
							
						}
						if (details.containsKey("dev_txrx")) {

							JSONArray JStr = (JSONArray)details.get("dev_txrx");
							Iterator<JSONObject> obj = JStr.iterator();
							
							String data1   = "";
							String header1 = "UID,ALIAS,TX BYTES, RX BYTES,AVG 2G,AVG 5G,";
							String Device  = "";
							
							while (obj.hasNext()) {
								
								JSONObject dataObject = obj.next();
								
								String tx = (String)dataObject.get("tx");
								String rx = (String)dataObject.get("rx");
								
								final String _2g = (String)dataObject.get("_2g");
								final String _5g = (String)dataObject.get("_5g");
								final String uid = (String)dataObject.get("uid");
								final String alias = (String)dataObject.get("alias");
								
								tx  = CustomerUtils.formatFileSize(Long.valueOf(tx));
								rx  = CustomerUtils.formatFileSize(Long.valueOf(rx));
								
								Device = uid + "," + alias + "," + tx + "," + rx + "," + _2g + "," + _5g + ","
										+ "\n";
								data1 = data1.concat(Device);	
								
							}
							result = result.concat("LOCATION CLIENTS COUNT AND BANDWIDTH");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);
							
							result = result.concat("\n\n");

						}
						if (details.containsKey("dev_txrx")) {

							JSONArray JStr = (JSONArray)details.get("dev_txrx");
							Iterator<JSONObject> obj = JStr.iterator();
							
							String data1   = "";
							String header1 = "UID,ALIAS,11K, 11R,11V,";
							String Device  = "";
							
							while (obj.hasNext()) {
								
								JSONObject dataObject = obj.next();
								
								final int _11k = (int)dataObject.get("_11k");
								final int _11r = (int)dataObject.get("_11r");
								final int _11v = (int)dataObject.get("_11v");
								
								final String uid   = (String)dataObject.get("uid");
								final String alias = (String)dataObject.get("alias");
								
								Device = uid + "," + alias + "," + _11k + "," + _11r + "," + _11v + "," + "\n";
								data1 = data1.concat(Device);	
								
							}
							
							result = result.concat("LOCATION CLIENTS CAPBALITIY");
							result = result.concat("\n");
							result = result.concat(header1);
							result = result.concat("\n");
							result = result.concat(data1);

							result = result.concat("\n\n");
							
						}
						
						if (details.containsKey("top5clients_consumed_tx_rx")) {

							JSONArray data = (JSONArray) details.get("top5clients_consumed_tx_rx");

							final String sortbyTX = "peer_tx";
							
							List<net.sf.json.JSONObject> jsonListdata = sortByValue(data, sortbyTX);

							Iterator<net.sf.json.JSONObject> it = jsonListdata.iterator();

							String Device = null;
							
							String txdata1 = "";
							String rxdata1 = "";
							
							String txheader1 = "CLIENT MAC,TX,";
							String rxheader1 = "CLIENT MAC,RX,";
							
							while (it.hasNext()) {
								net.sf.json.JSONObject obj = it.next();
								
								String macAddress = (String) obj.get("peer_mac");
								int peer_tx = (int) obj.get("peer_tx");

								String strTx  = CustomerUtils.formatFileSize(Long.valueOf(peer_tx));
								
								Device = macAddress + "," + strTx + "," + "\n";
								txdata1 = txdata1.concat(Device);	
								
							}
							
							if (!txdata1.isEmpty()) {
								result = result.concat(title + " TOP 5 CLIENTS CONSUMED TX");
								result = result.concat("\n");
								result = result.concat(txheader1);
								result = result.concat("\n");
								result = result.concat(txdata1);
								result = result.concat("\n\n");
							}
							
							final String sortbyRX = "peer_rx";

							JSONArray rxData = (JSONArray) details.get("top5clients_consumed_tx_rx");

							List<net.sf.json.JSONObject> jsonrxData = sortByValue(rxData, sortbyRX);

							Iterator<net.sf.json.JSONObject> rxDataIt = jsonrxData.iterator();
							
							String DeviceRX = null;
							
							while (rxDataIt.hasNext()) {
								net.sf.json.JSONObject obj = rxDataIt.next();

								String macAddress = (String) obj.get("peer_mac");
								int peer_rx 	  = (int) obj.get("peer_rx");

								String strRx  = CustomerUtils.formatFileSize(Long.valueOf(peer_rx));
								
								DeviceRX = macAddress + "," + strRx + "," + "\n";
								rxdata1 = rxdata1.concat(DeviceRX);	

							}
							if (!rxdata1.isEmpty()) {
								result = result.concat(title + " TOP 5 CLIENTS CONSUMED RX");
								result = result.concat("\n");
								result = result.concat(rxheader1);
								result = result.concat("\n");
								result = result.concat(rxdata1);
								result = result.concat("\n\n");
							}
						}
					}
				}

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + csvFileName);
				out = response.getOutputStream();
				out.write(result.getBytes());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out !=null) {
				out.flush();
				out.close();
			} 
			
		}
		
		
		return csvFileName;
	}
	
	
	@RequestMapping(value = "/gw_alertpdf", method = RequestMethod.GET)
	public String tagalertpdf(
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {
		
		//String pdfFileName  = "GatewayAlertReport.pdf";
		//String logoFileName = "/home/qubercomm/Desktop/pdf/logo.png";
		
		String pdfFileName  = "./uploads/alert.pdf";
		String logoFileName = "./uploads/logo-home.png";

		if (SessionUtil.isAuthorized(request.getSession())) {

			Document document = new Document(PageSize.A4, 36, 36, 90, 55);
			try {
				
				if(cid == null){
					cid = SessionUtil.getCurrentCustomer(request.getSession());
				}
				Customer cx = customerservice.findByUId(cid);
				String cx_name = cx.getCustomerName();
				timezone = customerUtils.FetchTimeZone(cx.getTimezone());// cx.getTimezone()
				format.setTimeZone(timezone);
				
				String currentuser = SessionUtil.currentUser(request.getSession());
				UserAccount cur_user = userAccountService.findOneByEmail(currentuser);
				String userName = cur_user.getFname() + " " + cur_user.getLname();
				
				logoFileName = cx.getLogofile() == null ? logoFileName : cx.getLogofile();
				Path path = Paths.get(logoFileName);
				
				if (!Files.exists(path)) {
					logoFileName = "./uploads/logo-home.png";
				}
				
				FileOutputStream os = new FileOutputStream(pdfFileName);
				PdfWriter writer = PdfWriter.getInstance(document, os);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(cx_name, userName, logoFileName, format.format(new Date()));
				writer.setPageEvent(event);
				document.open();

				addContent(document, cid,cx_name);
				document.close();

				File pdfFile = new File(pdfFileName);
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
				response.setContentLength((int) pdfFile.length());
				
				FileInputStream fileInputStream   = new FileInputStream(pdfFile);
				OutputStream responseOutputStream = response.getOutputStream();
				int bytes;
				while ((bytes = fileInputStream.read()) != -1) {
					responseOutputStream.write(bytes);
				}

				responseOutputStream.close();
				fileInputStream.close();
				os.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return pdfFileName;
	}

	private void addContent(Document document, String cid,String customerName) {
		try {

			Paragraph subCatPart = new Paragraph();

			// add a table
			createTable(subCatPart, document, cid,customerName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void createTable(Paragraph subCatPart, Document document, String cid,String customerName) throws DocumentException {

		PdfPTable table = new PdfPTable(7);
		table.setWidthPercentage(100);

		PdfPCell c1 = new PdfPCell(new Phrase("UID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Alias",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Status",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Last Active",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		table.addCell(c1);

		table.setHeaderRows(1);

		try {
			Boolean generatepdf = true;
			JSONObject deviceslist 		= networkDeviceRestController.alert(cid,generatepdf);

			if (deviceslist != null && !deviceslist.isEmpty()) {
				
				JSONArray array 	   		  = (JSONArray)deviceslist.get("inactive_list");
				Iterator<JSONObject> iterator = array.iterator();

				while (iterator.hasNext()) {
					JSONObject rep = iterator.next();

					String macaddr 		= (String) rep.get("macaddr");
					String alias		= (String) rep.get("alias");
					String floor		= (String) rep.get("portionname");
					String status		= (String) rep.get("state");
					String lastactive	= (String) rep.get("timestamp");

					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(alias,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(floor,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(status,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase(lastactive,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					table.addCell(c1);
				}
				subCatPart = new Paragraph("Device Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(table);
				document.add(subCatPart);
			}else{
				subCatPart = addNoDataToPDF(subCatPart);
				document.add(subCatPart);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void emailTrigger(String uid) {

		String pdfFileName  = "alert.pdf";
		String logoFileName = "./uploads/logo-home.png";
		
		UserAccount users	= userAccountService.findOneByUid(uid);

		if (users != null && users.isMailalert() != null && users.isMailalert().equals("true")) {
			
			String cid 		  = users.getCustomerId();
			String email      = users.getEmail();

			try {
				
					JSONObject deviceslist	  = networkDeviceRestController.alert(cid,null);
					JSONArray inactiveDevices = (JSONArray)deviceslist.get("inactive_list");
					
					JSONObject inactivetag = (JSONObject) inactiveDevices.get(0);
					String inactivemac 	   = inactivetag.get("macaddr").toString();
				
					if (inactivemac.equals("-")) {
						LOG.info("=====NO DEVICE ALTER FOUND=====");
						return;
					}

					Document document = new Document(PageSize.A4, 36, 36, 90, 55);
					LOG.info("Email Alerts enabled user " +uid);
					
					Customer cx 	= customerservice.findById(cid);
					String cx_name  = cx.getCustomerName();
					String userName = users.getFname() + " " + users.getLname();
					timezone = customerUtils.FetchTimeZone(cx.getTimezone());// cx.getTimezone()
					format.setTimeZone(timezone);
					
					logoFileName = cx.getLogofile() == null ? logoFileName : cx.getLogofile();
					Path path = Paths.get(logoFileName);
					
					if (!Files.exists(path)) {
						logoFileName = "./uploads/logo-home.png";
					}
					
					File file = new File(pdfFileName);
					FileOutputStream os = new FileOutputStream(file);
					PdfWriter writer = PdfWriter.getInstance(document, os);
					HeaderFooterPageEvent event = new HeaderFooterPageEvent(cx_name, userName, logoFileName, format.format(new Date()));
					writer.setPageEvent(event);
					document.open();
					
					addContent(document, cid,cx_name);
					document.close();
					os.close();

					String body = "Dear "+cx_name+",\n\n You have a new Alert Message!!!\n"
								+ "PFA detailed list of inactive devices.\n Please look in to this as a high priority.\n"
								+ "ALERTS - DEVICES @RISK, REQUIRES YOUR IMMEDIATE ATTENTION. \n";
			
					final String subject = "Alert Notification";
					
					customerUtils.customizeSupportEmail(cid, email, subject, body, file);


			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LOG.info("Email Alerts disabled user email " +uid);
		}
	}
	
public void EmailTriggeringForAlerts(File pdfFile,String email) {
		
		StringBuilder mailBody = new StringBuilder();
		
		mailBody
		 .append("Dear Customer,<br/>")
		 .append("You have a new Alert Message!!!<br/>")
		 .append("PFA detailed list of inactive devices.<br/> Please look in to this as a high priority.<br/>")
		 .append("ALERTS - DEVICES/TAGS @RISK, REQUIRE YOUR IMMEDIATE ATTENTION <br/>");
		 
		LOG.info("email id " +		email);
		LOG.info("mail body  " +   mailBody);
					
		javaMailSender.send(new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");			
				message.setTo(email);
				message.setSubject("Qubercomm Notification");
				message.setText(mailBody.toString(), true);
				message.addAttachment("alert.pdf", pdfFile);
			}
		});
	}
	
	@RequestMapping(value = "/gw_alertcsv", method = RequestMethod.GET)
	public String gatewayalertcsv(@RequestParam(value = "cid", required = true) String cid,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		String csvFileName = "./uploads/alert.csv";
		

		try {
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String result 		 = "";
				String gatewayheader = "";

				gatewayheader = "UID,Floor Name ,Alias,Status,Last Active\n";
					
					JSONObject deviceslist = networkDeviceRestController.alert(cid,null);

					if (deviceslist != null && !deviceslist.isEmpty()) {
						
						result = result.concat("DEVICES ALERT");
						result = result.concat("\n");
						result = result.concat(gatewayheader);

						JSONArray array 		= (JSONArray) deviceslist.get("inactive_list");
						Iterator<JSONObject> i  = array.iterator();

						String inactivedevices = null;

						while (i.hasNext()) {

							JSONObject rep = i.next();

							String macaddr 		= (String) rep.get("macaddr");
							String alias 		= (String) rep.get("alias");
							String floor 		= (String) rep.get("portionname");
							String status 		= (String) rep.get("state");
							String lastactive 	= (String) rep.get("state");

							inactivedevices = macaddr + "," + floor + "," + alias 
											+ "," + status + ","+lastactive+"\n";

							result = result.concat(inactivedevices);
						}
						result = result.concat("\n\n");
					}

			
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + csvFileName);
				OutputStream out = response.getOutputStream();
				out.write(result.getBytes());
				
				out.flush();
				out.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return csvFileName;
	}
	
	private Paragraph addNoDataToPDF(Paragraph paragraph) {
		addEmptyLine(paragraph, 5);
		PdfPTable table  = new PdfPTable(1);
		table.setWidthPercentage(100);
		PdfPCell c1 = new PdfPCell(new Phrase("No Data....",redFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorder(Rectangle.NO_BORDER);
		table.addCell(c1);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		paragraph.add(table);
		return paragraph;
	}
	@RequestMapping(value = "/locationlist", method = RequestMethod.GET)
	public JSONObject locationlist(@RequestParam(value = "cid", required = false) String cid,
								   @RequestParam(value = "sid", required = false) String sid,
								   @RequestParam(value = "spid", required = false) String spid,
								HttpServletRequest request,HttpServletResponse response) {
		if(cid == null){
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		JSONObject json 		= null;
		JSONArray jsonArray 	= new JSONArray();
		JSONObject jsonList 	= new JSONObject();
		Iterable<Device> ndList = new ArrayList<Device>();
		
		if(sid.equalsIgnoreCase("all") || spid.equalsIgnoreCase("all")){
			return  jsonList;
		}
		
		ndList = deviceservice.findBySpid(spid);
		
		if (ndList != null) {
			for (Device d : ndList) {
				json = new JSONObject();
					json.put("id",	 d.getUid());
					json.put("name", d.getAlias());
					jsonArray.add(json);
			}
			jsonList.put("location", jsonArray);
		}
		return jsonList;

	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gw_htmlCharts", method = RequestMethod.GET)
	public JSONObject gw_htmlCharts(HttpServletRequest request, HttpServletResponse response) {
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			String filterType 		= request.getParameter("filtertype");
			String cid 		  		= request.getParameter("cid");
			String sid 		  		= request.getParameter("venuename");
			String spid 	  		= request.getParameter("floorname");
			String location   		= request.getParameter("location");
			String time 			= request.getParameter("time");
			
			LOG.info(" filterType " + filterType + " cid " + cid + " sid " + sid + " spid " + spid);
			LOG.info(" location " + location + " time " + time);
			
			
			if (time == null || time.isEmpty()) {
				time = "24h";
			}
			
			String place 				  		= "htmlchart";
			List<Map<String, Object>> cpu 		= null;
			List<Map<String, Object>> mem 		= null;
			List<net.sf.json.JSONObject>  rxtx 	= null;
			List<NetworkDevice> networkDevice 	= null;
			
			JSONObject detailsJson 		= new JSONObject();
			JSONArray   clientDetails 	= null;
			
			
			switch (filterType) {
			
			case "default": {
				networkDevice   = networkDeviceService.findByCid(cid);
				clientDetails 	= clientsDetails(networkDevice,time);
			}
				
				break;
			case "venue":
				
				if (sid != null && sid.equals("all")) {
					networkDevice 	 = networkDeviceService.findByCid(cid);
					clientDetails   = clientsDetails(networkDevice,time);
				} else {
					networkDevice 	= networkDeviceService.findBySid(sid);
					clientDetails  = clientsDetails(networkDevice,time);
					rxtx 		  	= networkDeviceRestController.avg_tx_rx(sid, null, null, time, cid, request, response);
				}
				break;

			case "floor":

				if (sid.equals("all")) {
					networkDevice = networkDeviceService.findByCid(cid);
				} else if (spid != null && spid.equals("all")) {
					networkDevice = networkDeviceService.findBySid(sid);
					rxtx 		  = networkDeviceRestController.avg_tx_rx(sid,null, null, time, cid,request, response);
				} else {
					networkDevice = networkDeviceService.findBySpid(spid);
					rxtx 		  = networkDeviceRestController.avg_tx_rx(null,spid, null, time, cid,request, response);
				}
				break;

			case "location":
				
				if (sid != null && sid.equals("all")) {
					networkDevice = networkDeviceService.findByCid(cid);
					clientDetails = clientsDetails(networkDevice,time);
				} else if (spid != null && spid.equals("all")) {
					networkDevice  = networkDeviceService.findBySid(sid);
					clientDetails = clientsDetails(networkDevice,time);
					rxtx 		   = networkDeviceRestController.avg_tx_rx(sid,null, null, time, cid,request, response);
				} else if (location != null && location.equals("all")) {
					networkDevice  = networkDeviceService.findBySpid(spid);
					clientDetails = clientsDetails(networkDevice,time);
					rxtx 		   = networkDeviceRestController.avg_tx_rx(null,spid, null, time, cid,request, response);
				} else {
					
					String uuid   = location.replaceAll("[^a-zA-Z0-9]", "");
					networkDevice = networkDeviceService.findByUuid(uuid);
					
					if (networkDevice != null && networkDevice.size() >0) {
						
						NetworkDevice dev = networkDevice.get(0);
						String uid 		  = dev.getUid().toLowerCase();
						
						clientDetails  = clientsDetails(networkDevice,time);	
						cpu			 	= networkDeviceRestController.getcpu(null, null, null, uid, time, place);
						mem 		 	= networkDeviceRestController.getmem(null, null, null, uid, time, place);
						rxtx		 	= networkDeviceRestController.rxtx(uid, time, request, response);
					}
					
					detailsJson.put("cpu", cpu);
					detailsJson.put("mem", mem);

				}

				break;

			default:
				break;
			}
			
			detailsJson.put("clientDetails",	 clientDetails);
			detailsJson.put("rxtx",			 rxtx);
			
			return detailsJson;
		}
		return null;
	}

	
	
	@SuppressWarnings("unchecked")
	private JSONArray clientsDetails(List<NetworkDevice> networkDevice,String time) {
		
		JSONArray   result   		=  new JSONArray();
		JSONArray   dev_array   	=  null;
		JSONObject locationObject 	=  null;
		String uidstr   = "";
		String vap_2g   = "";
		String vap_5g	= "";
		Device dv 		= null;
		
		int vap_2g_cnt = 1;
		int vap_5g_cnt = 1;
		
		
		try {
			
			for (NetworkDevice device : networkDevice) {
				if (device.getTypefs() == null) {
					continue;
				}
				
				String uid 		 = device.getUid().toLowerCase();
				String fsType 	 = device.getTypefs();
				
				if (fsType.equals("ap")) {
					
					dv = deviceservice.findOneByUid(uidstr);
					
					if (dv != null) {
						vap_2g = dv.getVap2gcount();
						vap_5g = dv.getVap5gcount();

						if (vap_2g != null) {
							vap_2g_cnt = Integer.parseInt(vap_2g);
						}
						
						if (vap_5g != null) {
							vap_5g_cnt = Integer.parseInt(vap_5g);
						}
						
					}	
					dev_array   	=  new JSONArray();
					exec_fsql_getpeer(uid, vap_2g_cnt, vap_5g_cnt, dev_array, time);
					
					String location =  device.getAlias()+" ("+uid+")";
					
					if (dev_array != null && dev_array.size() > 0) {
						locationObject = new JSONObject();
						
						locationObject.put("uid",	location);
						locationObject.put("details", dev_array);
						
						result.add(locationObject);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("processing clientsDetails  error " + e);
		}

		return result;
	}

	private boolean exec_fsql_getpeer(String uid, int vap2g, int vap5g, JSONArray  dev_array,String duration) throws IOException {
    	
    	String fsql 	= "";
    	String fsql_5g 	= "";
    	int i			= 0;
  
    	List<Map<String, Object>>  logs  = EMPTY_LIST_MAP;
    	List<Map<String, Object>>  qlogs = EMPTY_LIST_MAP;
    		
    	for (i = 0; i < vap2g; i++ ) {
    		
    		fsql 	= "index=" + indexname + ",sort=timestamp desc,";
    		
    		fsql	= fsql + "query=timestamp:>now-"+duration+"" + " AND ";
    		fsql 	= fsql + "uid:\"" + uid + "\"";
			fsql 	= fsql + " AND vap_id:\"" + i + "\"";
			fsql 	= fsql + " AND radio_type:\"2.4Ghz\"|value(message,snapshot, NA);value(timestamp,timestamp, NA)|table";
			
			//LOG.info("2G FSQL PEER QUERY" + fsql);
			logs = fsqlRestController.query(fsql);
			
			if (logs != EMPTY_LIST_MAP) {
				addPeers (uid, logs, dev_array);
			}
    	}
    	    	
    	for (i= 0; i < vap5g; i++ ) {
    		
    		fsql_5g 	= "index=" + indexname + ",sort=timestamp desc,";
    		
    		fsql_5g	= fsql_5g + "query=timestamp:>now-"+duration+"" + " AND ";
    		fsql_5g = fsql_5g + "uid:\"" + uid + "\"";
    		fsql_5g = fsql_5g + " AND vap_id:\"" + i + "\"";
    		fsql_5g = fsql_5g + " AND radio_type:\"5Ghz\"|value(message,snapshot, NA);value(timestamp,timestamp, NA)|table";
    		
    		//LOG.info("5G FSQL PEER QUERY" + fsql_5g);
			
    		qlogs = fsqlRestController.query(fsql_5g);
					
			if (qlogs != EMPTY_LIST_MAP) {
				addPeers (uid, qlogs, dev_array);
			}
    	}
    	return true;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes", "rawtypes" })
	private boolean addPeers (String uid, List<Map<String, Object>> logs, JSONArray dev_array) throws IOException {
		
		try {
			
			int recordCount 	= 0;
			JSONObject dev_obj 	= null;
			Iterator<Map<String, Object>> iterator = logs.iterator();
			
			if (logs != null) {
				recordCount = logs.size();
			}

			long prev_Count = 0;
			
			while (iterator.hasNext()) {
				
				TreeMap<String , Object> me = new TreeMap<String, Object> (iterator.next());

				long count_2G = 0;
				long count_5G = 0;
				
				String JStr 		= (String) me.values().toArray()[0];
				String timestamp 	= (String) me.values().toArray()[1];

				JSONObject newJObject = null;
				JSONParser parser = new JSONParser();
				
				newJObject = (JSONObject) parser.parse(JStr);
				
				String radio_type 		= (String) newJObject.get("radio_type");
				long client_count 		= (long) newJObject.getOrDefault("client_count",0);
				
				if (radio_type.equals("2.4Ghz")) {
					count_2G =  client_count;
				} else {
					count_5G =  client_count;
				}
				
				if (count_2G != 0 || count_5G != 0) {
					dev_obj = new JSONObject();

					if (prev_Count != client_count) {

						if (count_2G != 0) {
							dev_obj.put("twoG", count_2G);
						}
						if (count_5G != 0) {
							dev_obj.put("fiveG", count_5G);
						}
						
						dev_obj.put("time", timestamp);
						dev_array.add(dev_obj);
						prev_Count = client_count;
					} else {
						continue;
					}

				}

			}

			if ((dev_array != null && !dev_array.isEmpty()) && dev_array.size() == 1) {
				
				Map<String, Object> last = logs.get(recordCount-1);
				JSONParser parser 		 = new JSONParser();
				
				String JStr 		= (String)last.get("snapshot");
				String timestamp 	= (String)last.get("timestamp");
				
				JSONObject newJObject   = (JSONObject) parser.parse(JStr);
				String radio_type 		= (String) newJObject.get("radio_type");
				long client_count 		= (long) newJObject.getOrDefault("client_count",0);
				
				long count_2G = 0;
				long count_5G = 0;
				
				if (radio_type.equals("2.4Ghz")) {
					count_2G =  client_count;
				} else {
					count_5G =  client_count;
				}
				
				if (count_2G != 0 || count_5G != 0) {
					
					dev_obj = new JSONObject();

					if (count_2G != 0) {
						dev_obj.put("twoG", count_2G);
					}
					if (count_5G != 0) {
						dev_obj.put("fiveG", count_5G);
					}
					dev_obj.put("time", timestamp);
					
					dev_array.add(dev_obj);
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}    

	@RequestMapping(value = "/deviceInfo", method = RequestMethod.GET)
	public JSONArray deviceInfo(@RequestParam(value = "cid", required = false) String cid,
								@RequestParam(value = "sid", required = false) String sid,
								@RequestParam(value = "spid", required = false) String spid, 
								HttpServletRequest request,HttpServletResponse response) {
		
		JSONArray deviceInfo = new JSONArray();
		JSONObject deviceObj = null;
		List<Device> deviceList = null;
		Map<String,String> portionmap = new HashMap<String,String>();
			
		if (spid != null && !spid.isEmpty()) {
			deviceList = deviceservice.findBySpid(spid);
		} else if (sid != null && !sid.isEmpty()) {
			deviceList = deviceservice.findBySid(sid);
		} else {
			deviceList = deviceservice.findByCid(cid);
		}
		
		String duration = "30m";
		List<Map<String, Object>>  uptimeInfo = null;
		if (deviceList != null && deviceList.size() > 0) {
			for(Device device : deviceList){
				
				String uid 				= device.getUid();
				String deviceUptime 	= "0d:0h:0m";
				String appUptime 		= "0d:0h:0m";
				String locationname 	= device.getAlias().toUpperCase();
				String state		    = device.getState().toUpperCase();
				String lastseen 	    = device.getLastseen() == null ? "NA" : device.getLastseen();
				String devspid 		    = device.getSpid() == null ? "NA" : device.getSpid() ;
				String fileStatus   	= device.getDevCrashDumpUploadStatus() == null ? "NA" : device.getDevCrashDumpUploadStatus();
				String fileName   	    = device.getDevCrashdumpFileName() == null ? "NA" : device.getDevCrashdumpFileName();
				
				String floorname 		= "NA";
				
				if (portionmap.containsKey(devspid)) {
					floorname = portionmap.get(devspid);
				} else {
					Portion p = portionService.findById(devspid);
					if (p != null) {
						floorname = p.getUid().toUpperCase();
					}
					portionmap.put(devspid, floorname);
				}
				
				String buildVersion = device.getBuildVersion();
				String buildTime 	= device.getBuildTime();
				
				buildVersion = (buildVersion == null || buildVersion.isEmpty()) ? "-" : buildVersion; 
				buildTime    = (buildTime == null || buildTime.isEmpty()) ? "-" : buildTime; 

				uid = uid.toLowerCase();
		    	
				String fsql = " index="+indexname+",sort=timestamp desc,size=1,query=cpu_stats:\"Qubercloud Manager\""
		    			+" AND timestamp:>now-"+duration+" AND uid:\""+uid+"\"|value(uid,uid,NA);"
		    			+" value(cpu_percentage,cpu,NA);value(timestamp,time,NA);"
		    			+" value(cpu_days,cpuDays,NA);value(cpu_hours,cpuHours,NA);value(cpu_minutes,cpuMinutes,NA);" 
						+" value(app_days,appDays,NA);value(app_hours,appHours,NA);value(app_minutes,appMinutes,NA);|table";
    	
				uptimeInfo = fsqlRestController.query(fsql);
				
				if(uptimeInfo != null && uptimeInfo.size()>0){
					Map<String, Object> info = uptimeInfo.get(0);
					
					deviceUptime = info.getOrDefault("cpuDays", 0) +"d:"
								 + info.getOrDefault("cpuHours", 0) +"h:"
								 + info.getOrDefault("cpuMinutes", 0) +"m";
					
					appUptime    = info.getOrDefault("appDays", 0) +"d:"
							 	 + info.getOrDefault("appHours", 0) +"h:"
							 	 + info.getOrDefault("appMinutes", 0) +"m";
				}
				
				deviceObj = new JSONObject();

				String crashState = "enabled";
				if (fileStatus.equals("NA") || fileStatus.isEmpty() || !fileStatus.equals("0")) {
					crashState = "disabled";
				}
				
				deviceObj.put("uid", 			uid);
				deviceObj.put("floorname", 		floorname);
				deviceObj.put("locationname", 	locationname);
				deviceObj.put("deviceUptime", 	deviceUptime);
				deviceObj.put("appUptime", 		appUptime);
				deviceObj.put("state", 			state);
				deviceObj.put("lastseen", 		lastseen);
				deviceObj.put("fileName",       fileName);
				deviceObj.put("filestatus", 	fileStatus);
				deviceObj.put("crashState", 	crashState);
				deviceObj.put("buildVersion", 	buildVersion);
				deviceObj.put("buildTime", 		buildTime);
				
				deviceInfo.add(deviceObj);
				
			}
		}
		return deviceInfo;
	}
	
	
    @SuppressWarnings("unchecked")
	public  JSONObject reportDetails(String reportType,String time, List<Device> devices) {
    	
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
		 
		 	JSONObject result 		= new JSONObject();
		 	JSONObject jsonObject 	= null;
		 	JSONArray txrxArray 	= new JSONArray();
		 	
		 	HashMap<String, HashMap<String, Integer>> hashmap = new HashMap<String, HashMap<String, Integer>>();
		 	
			double add_TX = 0;
			double add_RX = 0;
			double add_2G = 0;
			double add_5G = 0;
			
			double avg_2G = 0;
			double avg_5G = 0;
			
			int totla_min_2g = 0;
			int totla_max_2g = 0;
			
			int totla_min_5g = 0;
			int totla_max_5g = 0;
	
		try {
			
			if (devices != null) {

				int size = devices.size();
		
				for (Device dev : devices) {
					
					String devUid = dev.getUid();
					String alias  = dev.getAlias().toUpperCase();

					String deviceFsql ="index="+device_history_event+",type=device_metrics, "
							+ " query=opcode:device_details AND timestamp:>now-"+time+""
							+ " AND uid:\""+devUid+"\" | sum(tx,TX,NA);sum(rx,RX,NA);"
							+ " avg(_2G,avg_2G,NA);avg(_5G,avg_5G,NA);"
							+ " min(_2G,min_2G,NA);max(_2G,max_2G,NA);"
							+ " min(_5G,min_5G,NA);max(_5G,max_5G,NA);"
							+ " avg(_11k_count,_11k_count,NA);"
							+ " avg(_11r_count,_11r_count,NA);"
							+ " avg(_11v_count,_11v_count,NA); "
							+ " value(timestamp,timestamp,NA);|table,sort=Date:desc;";
	
						List<Map<String,Object>> logs = fsqlRestController.query(deviceFsql);
						
					String clientsFsql = "index="+device_history_event+",type=location_change_event,"
							+ " query=opcode:device_details AND exit_time:* AND timestamp:>now-"+time+" AND uid:\""+devUid+"\" "
							+ "|value(peer_mac,peer_mac,NA);value(peer_tx,peer_tx,NA);value(peer_rx,peer_rx,NA);"
							+ " value(timestamp,timestamp,NA);|table,sort=Date:desc;";
						
					//	LOG.info("clientsFsql " + clientsFsql);
					
						List<Map<String,Object>> clientsLogs = fsqlRestController.query(clientsFsql);
					
						if (!logs.isEmpty() && logs.size() > 0) {
							
							Map<String, Object> map = logs.get(0);
						
							double TX 		= (double)map.getOrDefault("TX",0);
							double RX 		= (double)map.getOrDefault("RX",0);
							int average_2G 	= (int)map.getOrDefault("avg_2G", 0);
							int average_5G 	= (int)map.getOrDefault("avg_5G", 0);
							int _11k_count 	= (int)map.getOrDefault("_11k_count", 0);
							int _11r_count 	= (int)map.getOrDefault("_11r_count", 0);
							int _11v_count 	= (int)map.getOrDefault("_11v_count", 0);
							
							double min_2G 	= 0;
							double max_2G 	= 0;
							double min_5G 	= 0;
							double max_5G 	= 0;
							
							String strMin_2G = String.valueOf(map.get("min_2G"));
							String strMax_2G = String.valueOf(map.get("max_2G"));
							String strMin_5G = String.valueOf(map.get("min_5G"));
							String strMax_5G = String.valueOf(map.get("max_5G"));
							
							min_2G = strMin_2G == "null" ? 0 : Double.parseDouble(strMin_2G);
							max_2G = strMax_2G == "null" ? 0 : Double.parseDouble(strMax_2G);
		
							min_5G = strMin_5G == "null" ? 0 : Double.parseDouble(strMin_5G);
							max_5G = strMax_5G == "null" ? 0 : Double.parseDouble(strMax_5G);

							totla_min_2g += min_2G;
							totla_max_2g += max_2G;
		
							totla_min_5g += min_5G;
							totla_max_5g += max_5G;
						
							avg_2G +=  average_2G;
							avg_5G +=  average_5G;
							
							add_TX += TX;
							add_RX += RX;
							add_2G += avg_2G;
							add_5G += avg_5G;

							String tx = decimalFormat.format(TX);
							String rx = decimalFormat.format(RX);
							
						//	String _2g = decimalFormat.format(average_2G);
						//	String _5g = decimalFormat.format(average_5G);
						
							
							jsonObject = new JSONObject();
							jsonObject.put("alias", alias);
							jsonObject.put("uid", devUid);
							jsonObject.put("tx",  tx);
							jsonObject.put("rx",  rx);
							//jsonObject.put("_2g", _2g);
							//jsonObject.put("_5g", _5g);
							
							jsonObject.put("avg_2g",  decimalFormat.format(average_2G));
							jsonObject.put("avg_5g",  decimalFormat.format(average_5G));
							
							jsonObject.put("min_2g", min_2G);
							jsonObject.put("max_2g", max_2G);
							jsonObject.put("min_5g", min_5G);
							jsonObject.put("max_5g", max_5G); 
							jsonObject.put("_11k", _11k_count);
							jsonObject.put("_11r", _11r_count);
							jsonObject.put("_11v", _11v_count);
							
							txrxArray.add(jsonObject);
					}

					/*
					 * CURRENT ACTIVE CLIENTS LIST
					 * 
					 */
						
					String uuid = devUid.replaceAll("[^a-zA-Z0-9]", "");
					List<ClientDevice> clientDevice = getClientDeviceService().findByUuid(uuid);
					
					if (clientDevice != null) {
						
						for (ClientDevice device : clientDevice) {

							String peermac = device.getMac();

							long prev_tx = device.getPrev_peer_tx_bytes();
							long prev_rx = device.getPrev_peer_rx_bytes();

							long cur_tx = device.getCur_peer_tx_bytes();
							long cur_rx = device.getCur_peer_rx_bytes();

							long used_tx = cur_tx - prev_tx;
							long used_rx = cur_rx - prev_rx;

							if (used_tx < 0) {
								used_tx = 0;
							}
							if (used_rx < 0) {
								used_rx = 0;
							}

							//LOG.info("clientObject peermac " +peermac);
							//LOG.info("jsonMap  " +hashmap);
							
							if (!hashmap.containsKey(peermac)) {
								
								HashMap<String, Integer> newMAp = new HashMap<String, Integer>();
								newMAp.put("peer_tx", (int)used_tx);
								newMAp.put("peer_rx", (int)used_rx);
								hashmap.put(peermac, newMAp);
								
							} else {

								HashMap<String, Integer> valMap = hashmap.get(peermac);
								int addpeer_tx = (int) valMap.get("peer_tx") + (int)used_tx;
								int addpeer_rx = (int) valMap.get("peer_rx") + (int)used_rx;
								
								HashMap<String, Integer> newMAp = new HashMap<String, Integer>();
								newMAp.put("peer_tx", addpeer_tx);
								newMAp.put("peer_rx", addpeer_rx);
								hashmap.put(peermac,newMAp);
							}

						}

						/*
						 * ES CLIENTS LIST
						 */
						
						LOG.info("clientsLogs " +clientsLogs);
						
						if (clientsLogs != null && clientsLogs.size() > 0) {
							
							Iterator<Map<String, Object>> iter = clientsLogs.iterator();
							
							while (iter.hasNext()) {
							
								Map<String, Object> map = iter.next();
								String peermac = (String) map.get("peer_mac");

								int peer_tx = (int) map.getOrDefault("peer_tx", 0);
								int peer_rx = (int) map.getOrDefault("peer_rx", 0);
								
								//LOG.info("peermac " +peermac);
								//LOG.info("query function  json map " +hashmap);
														
								if (!hashmap.containsKey(peermac)) {
									
									HashMap<String, Integer> newMAp = new HashMap<String, Integer>();
									newMAp.put("peer_tx", (int)peer_tx);
									newMAp.put("peer_rx", (int)peer_rx);
									hashmap.put(peermac, newMAp);
									
								} else {

									HashMap<String, Integer> valMap = hashmap.get(peermac);
									int addpeer_tx = (int) valMap.get("peer_tx") + (int)peer_tx;
									int addpeer_rx = (int) valMap.get("peer_rx") + (int)peer_rx;
									
									HashMap<String, Integer> newMAp = new HashMap<String, Integer>();
									newMAp.put("peer_tx", addpeer_tx);
									newMAp.put("peer_rx", addpeer_rx);
									
									hashmap.put(peermac,newMAp);
								}
							}
						}
					}
				}
				
				
				result.put("dev_txrx", 	  txrxArray);

				JSONObject aggDetails = new JSONObject();
				
				if (!reportType.equals("uid")) {
					
					aggDetails.put("agg_tx",  decimalFormat.format(add_TX));
					aggDetails.put("agg_rx",  decimalFormat.format(add_RX));
					aggDetails.put("agg_2g",  decimalFormat.format(add_2G));
					aggDetails.put("agg_5g",  decimalFormat.format(add_5G));
					result.put("agg_txrx", aggDetails);
					
					double average_2G = avg_2G / size;
					double average_5G = avg_5G / size;
					
					JSONObject avg2g_5gDetails = new JSONObject();
					
					avg2g_5gDetails.put("avg_2g", decimalFormat.format(average_2G));
					avg2g_5gDetails.put("avg_5g", decimalFormat.format(average_5G));
					result.put("avg_clients",     avg2g_5gDetails);
					
					JSONObject minandmax2g_5gDetails = new JSONObject();
					
					minandmax2g_5gDetails.put("min_2g", totla_min_2g);
					minandmax2g_5gDetails.put("max_2g", totla_max_2g);
					minandmax2g_5gDetails.put("min_5g", totla_min_5g);
					minandmax2g_5gDetails.put("max_5g", totla_max_5g);
					result.put("min_max_clients", minandmax2g_5gDetails);
					
				}

				JSONObject obj = null;
				JSONArray array = new JSONArray();

				for (Map.Entry<String, HashMap<String, Integer>> letterEntry : hashmap.entrySet()) {

					String macAddress = letterEntry.getKey();

					Map<String, Integer> nameEntry = letterEntry.getValue();
					int peer_tx = nameEntry.get("peer_tx");
					int peer_rx = nameEntry.get("peer_rx");

					if (peer_tx != 0 && peer_rx != 0) {
						obj = new JSONObject();
						obj.put("peer_mac", macAddress);
						obj.put("peer_tx", peer_tx);
						obj.put("peer_rx", peer_rx);
						array.add(obj);
					}
					
				}
				result.put("top5clients_consumed_tx_rx", array);
			}

		} catch (Exception e) {
			LOG.error("While Gateway Report generation error-> " + e);
			e.printStackTrace();
		}

		LOG.info("AGG Device TXRX  result " + result);

		return result;

	}
    
    public ClientDeviceService getClientDeviceService() {
		if (_clientDeviceService == null) {
			_clientDeviceService = Application.context.getBean(ClientDeviceService.class);
		}
		return _clientDeviceService;
	}
}
