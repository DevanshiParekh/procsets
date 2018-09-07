package com.semaifour.facesix.beacon.rest;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
//import org.json.simple.JSONArray;
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

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
import com.semaifour.facesix.account.PrivilegeService;
import com.semaifour.facesix.beacon.data.Beacon;
import com.semaifour.facesix.beacon.data.BeaconService;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDevice;
import com.semaifour.facesix.data.elasticsearch.beacondevice.BeaconDeviceService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.rest.FSqlRestController;
import com.semaifour.facesix.spring.CCC;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.HeaderFooterPageEvent;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/rest/beacon/trilaterationReports")
public class FinderReport extends WebController{

	static Logger LOG 	   = LoggerFactory.getLogger(FinderReport.class.getName());
	static Font smallBold  = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	static Font catFont    = new Font(Font.FontFamily.HELVETICA,   16, Font.BOLD);
	static Font redFont    = new Font(Font.FontFamily.HELVETICA,   10, Font.NORMAL);
	static Font subFont    = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	static Font headerFont = new Font(Font.FontFamily.HELVETICA,   12, Font.BOLD);

	private static final Integer BATTERY_STATUS_COLUMN_INDEX 		 = 6;
	private static final Integer TAGSTATE_COLUMN_INDEX 		 		 = 7;
	private static final Integer TAGSTATUS_COLUMN_INDEX 			 = 8;
	private static final Integer GENERIC_COLUMN_INDEX 		 		 = 10;
	private static final Integer TAG_COLUMN_INDEX 			 		 = 11;

	@Autowired
	CCC _CCC;

	@Autowired
	PortionService portionservice;

	@Autowired
	CustomerService customerservice;

	@Autowired
	FSqlRestController fsqlRestController;

	@Autowired
	BeaconService beaconservice;

	@Autowired
	NetworkDeviceService networkDeviceService;
	
	@Autowired
	BeaconDeviceService beacondeviceservice;

	@Autowired
	SiteService siteService;

	@Autowired
	PrivilegeService privilegeService;
	
	@Autowired
	BLENetworkDeviceRestController bleRestController;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	BLENetworkDeviceRestController bleNetworkDeviceRestController;
	
	@Autowired
	BeaconDeviceRestController beaconDeviceRestController;
	
	String trilaterationEventTable = "facesix-int-beacon-event";
	String indexname 			   = "";
	DateFormat format 			   = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	TimeZone timezone     		   = null;

	@PostConstruct
	public void init() {
		trilaterationEventTable = _CCC.properties.getProperty("facesix.data.beacon.trilateration.table",
				trilaterationEventTable);

		indexname = _CCC.properties.getProperty("elasticsearch.indexnamepattern", "facesix*");
		//LOG.info("service started************************************..." + indexname);
	}
	
	@RequestMapping(value = "/pdf", method = RequestMethod.GET)
	public String pdf(HttpServletRequest request, HttpServletResponse response) {
		
		String pdfFileName = "./uploads/qubercloud.pdf";
		String logoFileName = "./uploads/logo-home.png";
		
		Document document 					= new Document(PageSize.A4, 36, 36, 90, 55);// Document document = new Document(pageSize, marginLeft, marginRight, marginTop, marginBottom);
		String cid 							= request.getParameter("cid");
		String filterType 					= null;
		String reportType 					= null;
		Customer customer 					= null;
		FileOutputStream os 				= null;
		PdfWriter writer 					= null;
		String customerName 				= null;
		FileInputStream fileInputStream 	= null;
		OutputStream responseOutputStream 	= null;

		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				if (cid == null) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
					if (cid == null) {
						return null;
					}
				}
				String currentuser = SessionUtil.currentUser(request.getSession());
				UserAccount cur_user = userAccountService.findOneByEmail(currentuser);
				String userName = cur_user.getFname() + " " + cur_user.getLname();

				customer = customerservice.findById(cid);
				logoFileName = customer.getLogofile() == null ? logoFileName : customer.getLogofile();
				customerName = customer.getCustomerName();
				
				Path path = Paths.get(logoFileName);
				
				if (!Files.exists(path)) {
					logoFileName = "./uploads/logo-home.png";
				}
				timezone = customerUtils.FetchTimeZone(customer.getTimezone());
				format.setTimeZone(timezone);
				
				filterType = request.getParameter("filtertype");
				reportType = request.getParameter("reporttype");

				os = new FileOutputStream(pdfFileName);
				writer = PdfWriter.getInstance(document, os);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(customerName, userName, logoFileName, format.format(new Date()));
				writer.setPageEvent(event);
				document.open();
				/*Paragraph paragraph = new Paragraph();
				Image logo = Image.getInstance(logoFileName);
				logo.scaleAbsoluteHeight(25f);// scaleAbsolute(50f, 50f);
				logo.scaleAbsoluteWidth(100f);
				paragraph.add(logo);
				paragraph.setAlignment(Element.ALIGN_LEFT);

				String title = customerName + " : " + getTitle(filterType, reportType);
				paragraph.add(title);
				paragraph.setAlignment(Element.ALIGN_CENTER);
				addEmptyLine(paragraph, 1);

				paragraph.add(new Paragraph("Report generated by: " + userName + ", " + format.format(new Date()),
						smallBold));
				addEmptyLine(paragraph, 3);
				document.add(paragraph);*/

				String title = getTitle(filterType, reportType);
				addContent(document, writer, title, request, response);
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
				try {
					if (responseOutputStream != null) {
						responseOutputStream.close();
					}
					if (fileInputStream != null) {
						fileInputStream.close();
					}
					if (os != null) {
						os.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return pdfFileName;
	}

	private void addContent(Document document, PdfWriter writer,String title,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Anchor anchor = new Anchor(title, catFont);
			anchor.setName(title);
			
			Chapter chapter 	= new Chapter(new Paragraph(anchor), 1);
			Paragraph subPara 	= new Paragraph();
			String reporttype 	= request.getParameter("reporttype");
			
			/*if (reporttype != null && reporttype.equals("asset")) {
				subPara = new Paragraph("Asset Details", subFont);
			} else if (reporttype != null && reporttype.equals("nonasset")) {
				subPara = new Paragraph("Tag Details", subFont);
			} else {
				subPara = new Paragraph("Overall Details", subFont);
			}
			addEmptyLine(subPara, 1);
			
			Section subChapter = chapter.addSection(subPara);*/
			
			Paragraph para = new Paragraph(title,catFont);
			document.add(para);
			createTable(subPara, document, writer, request, response);

			document.add(subPara);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTable(Paragraph subChapter, Document document,
							 PdfWriter writer, HttpServletRequest request,
							 HttpServletResponse response) {
		try{
			
			Paragraph content	 = new Paragraph();
			PdfPTable table 	 = null;
			int col_num 		 = 0;
			String filterType 	 = request.getParameter("filtertype");
			String tagstatus 	 = request.getParameter("tagstatus");
			
			if (filterType.equals("tagBased") || filterType.equals("tagname")){
				col_num = TAG_COLUMN_INDEX;
			} else if (filterType.equals("tagstatus")) {
				switch (tagstatus) {
				case "battery":
					col_num = BATTERY_STATUS_COLUMN_INDEX;
					break;
				case "active":
					col_num = TAGSTATE_COLUMN_INDEX;
					break;
				case "checkedin":
					col_num = BATTERY_STATUS_COLUMN_INDEX;
					break;
				case "checkedout":
					col_num = BATTERY_STATUS_COLUMN_INDEX;
					break;
				default:
					col_num = TAGSTATUS_COLUMN_INDEX;
					break;
				}
			}else {
				col_num = GENERIC_COLUMN_INDEX;
			}
			table = new PdfPTable(col_num);
			table.setWidthPercentage(100);
			
			if(filterType.equals("deviceInfo")){
				
				String cid 		  		= request.getParameter("cid");
				String sid 		  		= request.getParameter("venuename");
				String spid 	  		= request.getParameter("floorname");
				JSONObject json 		= null;
				
				PdfPCell c1 = new PdfPCell(new Phrase("UID",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				c1.setColspan(2);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("Location Name",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("Version",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("Build Time",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("Device Uptime",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("App Uptime",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("State",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				c1 = new PdfPCell(new Phrase("Last Seen",headerFont));
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
				
				JSONArray processedDetail 	= deviceInfo(cid, sid, spid, request, response);
				
				if(processedDetail == null || processedDetail.isEmpty()){
						subChapter = addNoDataToPDF(subChapter);
				}else{
					
					Iterator<JSONObject> iterPorcessedDetail = processedDetail.iterator();
					while(iterPorcessedDetail.hasNext()){
						
						json = iterPorcessedDetail.next();
					
						String uid 			= json.getString("uid");
						String locationname = json.getString("locationname");
						String version 		= json.getString("version");
						String build 		= json.getString("build");
						String deviceUptime = json.getString("deviceUptime");
						String appUptime 	= json.getString("appUptime");
						String state 		= json.getString("state");
						String lastSeen 	= json.getString("lastSeen");
						
						c1 = new PdfPCell(new Phrase(uid,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						c1.setColspan(2);
						table.addCell(c1);
						
						c1 = new PdfPCell(new Phrase(locationname,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
						
						c1 = new PdfPCell(new Phrase(version,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
						
						c1 = new PdfPCell(new Phrase(build,redFont));
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
						
						c1 = new PdfPCell(new Phrase(lastSeen,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						c1.setColspan(2);
						table.addCell(c1);
					}
					
					content.add(table);
					addEmptyLine(subChapter, 3);
					subChapter.add(content);
				}
				
			} else {
				
				PdfPCell c1 = new PdfPCell(new Phrase("ID",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				c1.setColspan(2);
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Name",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Type",headerFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				
				if(filterType.equals("tagstatus")){
					
					if(!tagstatus.equals("battery")){
						c1 = new PdfPCell(new Phrase("Status",headerFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
					}
					if(tagstatus.equals("inactive") ||tagstatus.equals("active") || tagstatus.equalsIgnoreCase("all")){
						c1 = new PdfPCell(new Phrase("State",headerFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
					}
					if (tagstatus.equals("inactive")) {
						c1 = new PdfPCell(new Phrase("Lastseen",headerFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
					}
					
					if (tagstatus.equalsIgnoreCase("battery")||tagstatus.equals("all")){
						c1 = new PdfPCell(new Phrase("Battery Level",headerFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
					}
				}
				
				if (filterType.equals("location")) {
					c1 = new PdfPCell(new Phrase("Location",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
				} else {
					c1 = new PdfPCell(new Phrase("Floor",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
				}
				
				if (filterType.equals("tagBased") || filterType.equals("tagname")) {
					c1 = new PdfPCell(new Phrase("Location",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
				}
				
				if(!filterType.equals("tagstatus")){
					c1 = new PdfPCell(new Phrase("Enter",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("Exit",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					table.addCell(c1);

					c1 = new PdfPCell(new Phrase("TimeSpent",headerFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
				}
				
				table.setHeaderRows(1);
				
				JSONObject processedDetail 				= tagDetailsProcessing(writer,request,response);
				net.sf.json.JSONArray report 			= (net.sf.json.JSONArray) processedDetail.get("report");
				boolean locationwiseDataSet				= (boolean) processedDetail.get("locationwiseDataSet");
				boolean floorwiseDataSet				= (boolean) processedDetail.get("floorwiseDataSet");
				boolean tagBasedDataSet 				= (boolean) processedDetail.get("tagBasedDataSet");
				JSONObject tagTypeJson 					= (JSONObject) processedDetail.get("tagTypeJson");
				JSONObject tagCountJson 				= (JSONObject) processedDetail.get("tagCountJson");
				JSONObject tagBasedJson 				= (JSONObject) processedDetail.get("tagBasedJson");
				String overallElapsedTime				= (String) processedDetail.get("overallTime");
				int noOfTags							= (int) processedDetail.get("tags");
				DefaultCategoryDataset barChartDataSet 	= new DefaultCategoryDataset();
				DefaultPieDataset pieChartDataSet 		= new DefaultPieDataset();

				Image barChartImage 					= null;
				Image pieChartImage 					= null;
				
				
				if (report != null && !report.isEmpty()) {
					
					@SuppressWarnings("unchecked")
					Iterator<net.sf.json.JSONObject> i = report.iterator();
					
					while (i.hasNext()) {
						
						net.sf.json.JSONObject rep = i.next();
						
						String tagid 		= (String) rep.get("tagid");
						String assignedto 	= (String) rep.get("assignedTo");
						String tagtype 		= (String) rep.get("tagType");
						
						String tagStatus	= null;
						String tagstate		= null;
						String floorname 	= null;
						String location 	= null;
						String lastseendate = null;
						String exit 	 	= null;
						String entry 	 	= null;
						String elapsed 		= null;
						String timespent    = null;
						String battery		= null;
						
						c1 = new PdfPCell(new Phrase(tagid,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						c1.setColspan(2);
						table.addCell(c1);
						
						c1 = new PdfPCell(new Phrase(assignedto,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
						
						c1 = new PdfPCell(new Phrase(tagtype,redFont));
						c1.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(c1);
						
						if(filterType.equals("tagstatus")){
							if(!tagstatus.equals("battery")){
								tagStatus    = (String) rep.get("tagStatus");
								c1 = new PdfPCell(new Phrase(tagStatus,redFont));
								c1.setHorizontalAlignment(Element.ALIGN_CENTER);
								table.addCell(c1);
								
							}
							if(tagstatus.equals("inactive") ||tagstatus.equals("active") || tagstatus.equalsIgnoreCase("all")){
								tagstate     = (String) rep.get("tagstate");
								c1 = new PdfPCell(new Phrase(tagstate,redFont));
								c1.setHorizontalAlignment(Element.ALIGN_CENTER);
								table.addCell(c1);
							}
							if (tagstatus.equals("inactive")) {
								lastseendate = (String) rep.get("lastSeen");
								c1 = new PdfPCell(new Phrase(lastseendate,redFont));
								c1.setHorizontalAlignment(Element.ALIGN_CENTER);
								table.addCell(c1);
							}
							
							if (tagstatus.equalsIgnoreCase("battery")||tagstatus.equals("all")){
								battery 	 = rep.get("battery").toString();
								c1 = new PdfPCell(new Phrase(battery,redFont));
								c1.setHorizontalAlignment(Element.ALIGN_CENTER);
								table.addCell(c1);
							}
						}
						
						if (filterType.equals("location")) {
							location 	 = (String) rep.get("deviceLocation");
							c1 = new PdfPCell(new Phrase(location.toUpperCase(),redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
						} else {
							floorname 	 = (String) rep.get("floorname");
							c1 = new PdfPCell(new Phrase(floorname.toUpperCase(),redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
						}
						
						if (filterType.equals("tagBased") || filterType.equals("tagname")) {
							location 	 = (String) rep.get("deviceLocation");
							c1 = new PdfPCell(new Phrase(location.toUpperCase(),redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
						}
						
						if (!filterType.equals("tagstatus")) {
							entry     = (String) rep.get("entry");
							exit 	  = "Did not exit";
							timespent = "-";
							if (rep.containsKey("exit")) {
								exit      = (String) rep.get("exit");
								elapsed   = (String) rep.get("elapsed");
								timespent = getTimeSpent(elapsed);
							}
							c1 = new PdfPCell(new Phrase(entry,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							c1.setColspan(2);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(exit,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							c1.setColspan(2);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(timespent,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
						}
					}
					/*
					 * Overall time spend for all the Tags 
					 */
					
					String overallDetail = " Number Of Tags :  "+ noOfTags+" \n Overall Time Spent (HH:MM:SS) is  : " + getTimeSpent(overallElapsedTime);
					Paragraph para = new Paragraph(overallDetail,headerFont);
					content.add(para);
					
					addEmptyLine(content, 2);
					
					content.add(table);
					
					addEmptyLine(subChapter, 2);
					
					subChapter.add(content);
					
					Paragraph statistics = null;
					Paragraph barChart = null;
					Paragraph pieChart = null;
					
					if (locationwiseDataSet || floorwiseDataSet || tagBasedDataSet) {
						if (locationwiseDataSet || floorwiseDataSet) {
							String title = locationwiseDataSet?"LOCATION":"FLOOR";
							barChart = new Paragraph();
							addEmptyLine(barChart, 1);
							Iterator iterator = tagCountJson.keySet().iterator();
							while (iterator.hasNext()) {
								String name = (String) iterator.next();
								int tagCount = tagCountJson.getInt(name);
								barChartDataSet.setValue(tagCount, "Tag Information", name.toUpperCase());
							}
							barChartImage = getBarChartImage(writer,filterType, barChartDataSet,locationwiseDataSet,floorwiseDataSet,tagBasedDataSet);
						}else if(tagBasedDataSet){
							barChart = new Paragraph();
							addEmptyLine(barChart, 1);
							Iterator iterator = tagBasedJson.keySet().iterator();
							while (iterator.hasNext()) {
								String name = (String) iterator.next();
								int frequency = tagBasedJson.getInt(name);
								barChartDataSet.setValue(frequency, "Frequency Information", name.toUpperCase());
							}
							barChartImage = getBarChartImage(writer,filterType, barChartDataSet,locationwiseDataSet,floorwiseDataSet,tagBasedDataSet);
						}
						
						if (!filterType.equals("tagBased") && !filterType.equals("tagname") && !filterType.equals("tagType")) {
							if (!(filterType.equals("tagstatus") && tagstatus.equals("checkedin"))) {
								Iterator iterator = tagTypeJson.keySet().iterator();
								while (iterator.hasNext()) {
									String type = (String) iterator.next();
									int tagCount = tagTypeJson.getInt(type);
									pieChartDataSet.setValue(type, tagCount);
								}
								pieChartImage = getPieChartImage(writer, pieChartDataSet,request,response);
							}
						}
						
						if(barChartImage != null || pieChartImage != null){
							subChapter.breakUp();
							statistics = new Paragraph();
							addEmptyLine(statistics, 1);
							statistics.setAlignment(Element.ALIGN_LEFT);
							
							if (barChartImage != null) {
								barChart.add(barChartImage);
								barChart.setAlignment(Element.ALIGN_LEFT);
								addEmptyLine(barChart, 1);
								statistics.add(barChart);
							}
							
							if (pieChartImage != null) {
								pieChart = new Paragraph();
								addEmptyLine(pieChart, 1);
								pieChart.add(pieChartImage);
								pieChart.setAlignment(Element.ALIGN_LEFT);
								statistics.add(pieChart);
								
							}
							subChapter.add(statistics);
						}
					}
				} else {
					subChapter = addNoDataToPDF(subChapter);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private JSONObject tagDetailsProcessing(PdfWriter writer, HttpServletRequest request, HttpServletResponse response) {
		JSONObject processedDetail 				= new JSONObject();
		JSONArray report_array 					= new JSONArray();
		Collection<Beacon> beaconList 			= null;
		int  tags	  				    		= 0;
		JSONObject tagTypeJson 					= new JSONObject();
		JSONObject tagCountJson 				= new JSONObject();
		JSONObject tagBasedJson					= new JSONObject();
		boolean floorwiseDataSet  				= false;
		boolean locationwiseDataSet  			= false;
		boolean tagBasedDataSet  			    = false;
		
		String status 			= "checkedout";
		String filterType 		= request.getParameter("filtertype");
		String reportType 		= request.getParameter("reporttype");
		String cid 		  		= request.getParameter("cid");
		String sid 		  		= request.getParameter("venuename");
		String spid 	  		= request.getParameter("floorname");
		String location   		= request.getParameter("location");
		String macaddr 	  		= request.getParameter("macaddr");
		String tagType 	  		= request.getParameter("tagType");
		String tagstatus  		= request.getParameter("tagstatus");
		String assignedto 		= request.getParameter("tagname");
		String time 	  		= request.getParameter("time");
		
		Map<String,String> portionmap = new HashMap<String,String>();
		Map<String,String> receivermap = new HashMap<String,String>();

		boolean entryExit = customerUtils.entryexit(cid);

		long overallTimeSpent = 0;
		
		Customer cx 	  		= customerservice.findById(cid);
		timezone = customerUtils.FetchTimeZone(cx.getTimezone());
		format.setTimeZone(timezone);
		
		if(time == null){
			time = "24h";
		}
		
		long reqTime = Long.parseLong(time.replaceAll("[a-zA-Z]", ""));
		long intoMillis = time.contains("h")? 3600000 : (24*3600000);
		
		reqTime *= intoMillis;
		
		long reqDate = (new Date().getTime()) - reqTime;
		
		if(filterType.equals("tagBased")){
			beaconList = beaconservice.getSavedBeaconByMacaddr(macaddr);
		}else if(filterType.equals("tagType")){
			tagType = tagType.substring(0,1).toUpperCase()+tagType.substring(1).toLowerCase();
			beaconList = beaconservice.getSavedBeaconByCidTagTypeAndStatus(cid, tagType, status);
		}else if(!filterType.equals("tagstatus")){
			beaconList = beaconservice.getSavedBeaconByCidAndStatus(cid, status);
		}else{
			String state  = "active";
			switch (tagstatus) {
			case "all":
				beaconList = beaconservice.getSavedBeaconByCid(cid);
				break;
			case "checkedin":
				status = "checkedin";
				beaconList = (List<Beacon>) beaconservice.getSavedBeaconByCidAndStatus(cid, status);
				break;
			case "checkedout":
				beaconList = (List<Beacon>) beaconservice.getSavedBeaconByCidAndStatus(cid, status);
				break;
			case "battery":
				String battery = cx.getBattery_threshold();
				int battery_threshold = battery==null?40:Integer.parseInt(cx.getBattery_threshold());
				beaconList = beaconservice.findByCidStatusAndBatteryLevel(cid,status,battery_threshold);
				break;
			case "inactive":
				state = "inactive";
				beaconList = (List<Beacon>) beaconservice.getSavedBeaconByCidStateAndStatus(cid, state,status);
				break;
			case "active":
				beaconList = (List<Beacon>) beaconservice.getSavedBeaconByCidStateAndStatus(cid, state,status);
				break;
			}
		}

		if (writer == null || (filterType.equals("tagstatus") && tagstatus.equals("checkedin")) || beaconList == null || beaconList.size()==0) {
			floorwiseDataSet = false;
			locationwiseDataSet = false;
			tagBasedDataSet = false;
			
		}else if ((filterType.equals("floor") && !spid.equals("all")) || 
				  (filterType.equals("location") && location.equals("all")) ||
				  (filterType.equals("tagname") && location.equals("all") && !spid.equals("all"))) {
			floorwiseDataSet    = false;
			locationwiseDataSet = true;
			tagBasedDataSet     = false;
			List<BeaconDevice> receiverList = null;
			if(spid != null && !spid.equals("all")){
				receiverList = beacondeviceservice.findBySpidAndType(spid, "receiver");
			}else if(sid != null && !sid.equals("all")){
				receiverList = beacondeviceservice.findBySidAndType(sid, "receiver");
			}else{
				receiverList = beacondeviceservice.findByCidAndType(cid, "receiver");
			}
			for (BeaconDevice receiver : receiverList) {
				String receiverName = receiver.getAlias().toUpperCase();
				tagCountJson.put(receiverName,0);
			}
		
		} else if (filterType.equals("tagType")   ||
				   filterType.equals("tagstatus") || 
				   filterType.equals("tagBased")) {
			floorwiseDataSet    = false;
			locationwiseDataSet = false;
			tagBasedDataSet     = true;
			
			List<Portion> floorList = portionservice.findByCid(cid);
			for (Portion floor : floorList) {
				String floorName = floor.getUid().toUpperCase();
				tagBasedJson.put(floorName, 0);
			}
			
		} else if(!filterType.equals("location") && (location == null || location.equals("all"))) { 

			locationwiseDataSet = false;
			floorwiseDataSet    = true;
			tagBasedDataSet     = false;

			List<Portion> floorList = null;
			if(sid != null && !sid.equals("all")){
				floorList = portionservice.findBySiteId(sid);
			}else{
				floorList = portionservice.findByCid(cid);
			}
			for (Portion floor : floorList) {
				String floorName = floor.getUid().toUpperCase();
				tagCountJson.put(floorName, 0);
			}
		}
		for(Beacon tag: beaconList){
			
			String tag_id 		  = tag.getMacaddr();
			String tag_assignedTo = tag.getAssignedTo();
			String tag_type 	  = tag.getTag_type();
			String tag_status 	  = tag.getStatus();
			String tag_state 	  = tag.getState();
			String tag_lastSeen   = tag.getLastReportingTime();
			String tag_floorname  = tag.getLocation();
			int tag_battery       = tag.getBattery_level();
			String tag_sid		  = tag.getSid();
			String tag_spid       = tag.getSpid();
			String tag_receiverId = tag.getReciverinfo();
			String tag_location   = null;
			String entry 		  = null;
			String exit 		  = null;
			String elapsed 		  = null;
			boolean currentUpdate = true;
			boolean addTagType 	  = true;
			long lastNoticed	  = tag.getLastSeen();
			
			if(tag_receiverId != null && !tag_receiverId.isEmpty()){
				if (receivermap.containsKey(tag_receiverId)) {
					tag_location = receivermap.get(tag_receiverId);
				} else {
					BeaconDevice device = beacondeviceservice.findOneByUid(tag_receiverId);
					if (device != null) {
						tag_location = device.getAlias().toUpperCase();
					}
					if (tag_location == null) {
						tag_location = tag_receiverId;
					}
					receivermap.put(tag_receiverId, tag_location);
				}
			}
			
			if(tag_floorname != null){
				tag_floorname = tag_floorname.toUpperCase();
			}else{
				tag_floorname = "Unknown";
			}
			List<Map<String, Object>> logs = null;
			
			if (reportType!= null && reportType.equals("asset")) {
				if(tag_type.equalsIgnoreCase("male")  || tag_type.equalsIgnoreCase("female") || 
				   tag_type.equalsIgnoreCase("child") || tag_type.equalsIgnoreCase("doctor") || 
				   tag_type.equalsIgnoreCase("user")){
					continue;
				}
			} else if (reportType!= null && reportType.equals("nonasset")) {
				if(!tag_type.equalsIgnoreCase("male")  && !tag_type.equalsIgnoreCase("female") && 
				   !tag_type.equalsIgnoreCase("child") && !tag_type.equalsIgnoreCase("doctor") && 
				   !tag_type.equalsIgnoreCase("user")){
					continue;
				}
			}
			
			if (filterType.equals("tagname") || 
				filterType.equals("venue")   || 
				filterType.equals("floor")   || 
				filterType.equals("location")) {
				
				if (sid != null && !sid.equals("all") && !sid.equals(tag_sid)) {
					currentUpdate = false;
				}
				if (spid != null && !spid.equals("all")
					&& !spid.equals(tag_spid)) {
					currentUpdate = false;
				}
				if (location != null && !location.equals("all")
					&& !location.equals(tag_receiverId)) {
					currentUpdate = false;
				}
			}
			
			if(filterType.equals("tagname") && !assignedto.equalsIgnoreCase(tag_assignedTo)){
				currentUpdate = false;
			}
			
			if(lastSeenBeforeRequiredTime(lastNoticed,reqDate)){
				currentUpdate = false;
			}
			
			if (filterType.equals(location) || filterType.equals("tagBased")) {
				entry = tag.getEntry_loc();
			} else {
				entry = tag.getEntryFloor();
			}
			
			if ((entry == null || entry.isEmpty()) && !entryExit) {
				currentUpdate = false;
			}
			
			if(currentUpdate){
				JSONObject report = new JSONObject();

				if (!filterType.equals("tagstatus")) {
					
					if(entry == null || entry.isEmpty()){
						entry = "inactive";
					}
					exit = tag.getLastReportingTime();
					
					if(exit == null || exit.isEmpty()){
						exit = "-";
						elapsed ="0";
					}else{
						elapsed = calculateElapsedTime(entry,exit);
						/* 
						 *  elapsed Time for tags
						 * 
						 */
						if(!tag.getState().equals("inactive")){
							exit = "Did Not Exit";
						}
						overallTimeSpent += Long.valueOf(elapsed);
					}
					
					report.put("entry",   entry);
					report.put("exit",    exit);
					report.put("elapsed", elapsed);
					
				}
				
				report.put("tagid", 		tag_id);
				report.put("assignedTo", 	tag_assignedTo);
				report.put("tagType", 		tag_type);
				
				if(filterType.equals("tagstatus")){
					if(!tagstatus.equals("battery")){
						report.put("tagStatus", tag_status);
					}
					if(tagstatus.equals("inactive") ||tagstatus.equals("active") || tagstatus.equalsIgnoreCase("all")){
						report.put("tagstate",tag_state);
					}
					if (tagstatus.equals("inactive")) {
						report.put("lastSeen",tag_lastSeen);
					}
					
					if (tagstatus.equalsIgnoreCase("battery")||tagstatus.equals("all")){
						report.put("battery",tag_battery);
					}
				}
				
				if (filterType.equalsIgnoreCase("location")) {
					report.put("deviceLocation",tag_location);
				} else {
					report.put("floorname",tag_floorname);
				}
				
				if (filterType.equals("tagBased") || filterType.equals("tagname")) {
					report.put("deviceLocation",tag_location);
				}
				
				if(!tag_state.equalsIgnoreCase("inactive")){
					report_array.add(report);
				}
				
				if(!filterType.equals("tagstatus") && !filterType.equals("tagBased") && 
				   !filterType.equals("tagType")) {
					
					if (floorwiseDataSet) {
						if (tagCountJson.containsKey(tag_floorname)) {
							int count = tagCountJson.getInt(tag_floorname);
							tagCountJson.put(tag_floorname, count+1);
						}else{
							tagCountJson.put(tag_floorname, 1);
						}
					}

					if (locationwiseDataSet) {
						if (tagCountJson.containsKey(tag_location)) {
							int count = tagCountJson.getInt(tag_location);
							tagCountJson.put(tag_location, count+1);
						}else{
							tagCountJson.put(tag_location, 1);
						}
					}
				}
				if (tagBasedDataSet) {

					if (tagBasedJson.containsKey(tag_floorname)) {
						int frequency = tagBasedJson.getInt(tag_floorname);
						tagBasedJson.put(tag_floorname, frequency + 1);
					}else{
						tagBasedJson.put(tag_floorname, 1);
					}
				}
			}
			
			tags++;
			
			if (filterType.equals("tagstatus") && tagstatus.equals("checkedin")) {
				continue;
			} else if (!filterType.equals("tagBased") && !filterType.equals("tagname") && !filterType.equals("tagType")) {
				
				if (sid != null && !sid.equals("all")&& !sid.equals(tag_sid)) {
					addTagType = false;
				}
				if (spid != null && !spid.equals("all") && !spid.equals(tag_spid)) {
					addTagType = false;
				}
				if (location != null && !location.equals("all") && !location.equals(tag_receiverId)) {
					addTagType = false;
				}

				if (addTagType) {
					if (tagTypeJson.containsKey(tag_type)) {
						int count = tagTypeJson.getInt(tag_type);
						tagTypeJson.put(tag_type, count + 1);
					} else {
						tagTypeJson.put(tag_type, 1);
					}
				}
			}
		
			if(filterType.equals("tagstatus")){
				continue;
			}
			
			String fsql = "index="+trilaterationEventTable + ",type=trilateration,query=timestamp:>now-" + time +
						  " AND opcode:\"reports\" AND cid:" + cid;
			
			if (sid != null && !sid.equals("all")) {
				fsql = fsql.concat(" AND sid:" + sid);
			}
			
			if (spid != null && !spid.equals("all")) {
				fsql = fsql.concat(" AND spid:" + spid);
			} 
			
			if(location != null && !location.equals("all")){
				fsql = fsql.concat(" AND location:\"" + location+"\"");
			}
			
			if(assignedto == null){
				fsql = fsql.concat(" AND tagid:\"" + tag_id+ "\"" );
			}else if (assignedto != null && assignedto.equalsIgnoreCase(tag_assignedTo)) {
				fsql = fsql.concat(" AND assingedto:\"" + tag_assignedTo+"\"");
			}else{
				continue;
			}
			
			fsql = fsql.concat(" AND exit_loc:*,sort=timestamp DESC"
					+ "|value(timestamp,Date,typecast=date);" + " value(tagid,tagid,null);"
					+ " value(assingedto,assingedto,null);value(spid,spid,null);value(location,location,null);"
					+ "value(entry_floor ,entry_floor,null);value(exit_floor,exit_floor,null);value(elapsed_floor,elapsed_floor,null);"
					+ "value(entry_loc ,entry_loc,null);value(exit_loc,exit_loc,null);value(elapsed_loc,elapsed_loc,null);"
					+ "|table,sort=Date:desc;");
			
			logs = fsqlRestController.query(fsql);
			
			//LOG.info("fsql "+fsql);
			if ((logs == null || logs.isEmpty())) {
				continue;
			}
			
			Iterator<Map<String, Object>> iterator = logs.iterator();

			if (!filterType.equals("tagBased") && !filterType.equals("tagname") && !filterType.equals("tagType") && !addTagType) {
				if (tagTypeJson.containsKey(tag_type)) {
					int count = tagTypeJson.getInt(tag_type);
					tagTypeJson.put(tag_type, count + 1);
				} else {
					tagTypeJson.put(tag_type, 1);
				}
			}
			
			while (iterator.hasNext()) {
				try {
					TreeMap<String, Object> log = new TreeMap<String, Object>(iterator.next());
					JSONObject report 			= new JSONObject();
					
					String log_spid = log.get("spid").toString();
					String log_receiverId = log.get("location").toString();
					String log_entry = null;
					String log_exit  = null;
					String log_elapsed = null;
					String log_assignedTo = null;
					BeaconDevice device   = null;
					String log_floorname = "NA";
					String log_receiverAlias = "NA";
					
					if (portionmap.containsKey(log_spid)) {
						log_floorname = portionmap.get(log_spid);
					} else {
						Portion portion = portionservice.findById(log_spid);
						if (portion != null) {
							log_floorname = portion.getUid() == null ? "NA" : portion.getUid();
						}
						portionmap.put(log_spid, log_floorname);
					}

					if (receivermap.containsKey(log_receiverId)) {
						log_receiverAlias = receivermap.get(log_receiverId);
					} else {
						device = beacondeviceservice.findOneByUid(log_receiverId);
						if (device != null) {
							log_receiverAlias = device.getAlias() == null ? log_receiverId : device.getAlias().toUpperCase();
						}
						receivermap.put(log_receiverId, log_receiverAlias);
					}
					
					
					if (log.containsKey("exit_loc") && locationwiseDataSet) {
						if (tagCountJson.containsKey(log_receiverAlias)) {
							int count = tagCountJson.getInt(log_receiverAlias);
							tagCountJson.put(log_receiverAlias, count+1);
						}
					}
					
					if (log.containsKey("exit_floor")) {
						if (floorwiseDataSet) {
							if (tagCountJson.containsKey(log_floorname)) {
								int count = tagCountJson.getInt(log_floorname);
								tagCountJson.put(log_floorname, count + 1);
							}
						}

						if (tagBasedDataSet) {
							if (tagBasedJson.containsKey(tag_floorname)) {
								int frequency = tagBasedJson.getInt(tag_floorname);
								tagBasedJson.put(tag_floorname, frequency + 1);
							}
						}
					}
					
					if(filterType.equals("location") || filterType.equals("tagBased") || filterType.equals("tagname")){
						if (log.containsKey("exit_loc") && log.containsKey("entry_loc")) {
							log_entry   = log.get("entry_loc").toString();
							log_exit    = log.get("exit_loc").toString();
							log_elapsed = log.get("elapsed_loc").toString();
						}else{
							continue;
						}
					}else{
						if (log.containsKey("exit_floor") && log.containsKey("entry_floor")) {
							log_entry   = log.get("entry_floor").toString();
							log_exit    = log.get("exit_floor").toString();
							log_elapsed = log.get("elapsed_floor").toString();
						}else{
							continue;
						}
					}
					
					overallTimeSpent += Long.valueOf(log_elapsed);
					report.put("entry",   log_entry);
					report.put("exit",    log_exit);
					report.put("elapsed", log_elapsed);
					
					if(log.containsKey("assingedto")){
						log_assignedTo = (String)log.get("assingedto");
					}else{
						log_assignedTo = tag_assignedTo;
					}

					report.put("tagid", 		tag_id);
					report.put("assignedTo", 	log_assignedTo);
					report.put("tagType", 		tag_type);
					
					if (filterType.equals("location")) {
						report.put("deviceLocation", log_receiverAlias);
					}else{
						report.put("floorname", log_floorname);
					}
					
					if(filterType.equals("tagBased") || filterType.equals("tagname")){
						report.put("deviceLocation", log_receiverAlias);
					}
					report_array.add(report);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		processedDetail.put("report", report_array);
		processedDetail.put("locationwiseDataSet", locationwiseDataSet);
		processedDetail.put("floorwiseDataSet", floorwiseDataSet);
		processedDetail.put("tagBasedDataSet", tagBasedDataSet);
		processedDetail.put("tagTypeJson", tagTypeJson);
		processedDetail.put("tagCountJson", tagCountJson);
		processedDetail.put("tagBasedJson", tagBasedJson);
		processedDetail.put("overallTime", String.valueOf(overallTimeSpent));
		processedDetail.put("tags", tags);
		return processedDetail;
	}
	
	private boolean lastSeenBeforeRequiredTime(long tag_lastSeen, long reqTime) {
		Date d1 = new Date(tag_lastSeen);
		Date d2 = new Date(reqTime);
		if(d1.before(d2)){
			return true;
		}
		return false;
	}

	private Image getBarChartImage(PdfWriter writer, String filterType, CategoryDataset barChartDataSet, 
								   boolean locationwiseDataSet, boolean floorwiseDataSet , boolean tagBasedDataSet) {
		Image barChartImage = null;
		int width  = 525;
		int height = 360;
		String title = null;
		String xLabel = null;
		String yLabel = null;
		JFreeChart barChart = null;
		
		java.awt.Font title_font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14);
		java.awt.Font axis_font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12);
		java.awt.Font label_font = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 10);

		try{

			int column_count = barChartDataSet.getColumnCount();
			if (column_count <= 1) {
				height = 150;
			}
			
			if (floorwiseDataSet) {
				title = "TAG FREQUENCY IN FLOOR";
				xLabel = "FLOOR NAME";
				yLabel = "TAG FREQUENCY";
			} else if (locationwiseDataSet) {
				title = "TAG FREQUENCY IN LOCATION";
				xLabel = "LOCATION NAME";
				yLabel = "TAG FREQUENCY";
			} else if (tagBasedDataSet) {
				title = "FREQUENCY OF VISITING FLOOR";
				xLabel = "FLOOR NAME";
				yLabel = "FREQUENCY";
			}
			
			barChart =  ChartFactory.createBarChart(title, xLabel, yLabel,
					barChartDataSet, PlotOrientation.HORIZONTAL,false, true, false);
			barChart.setTitle(new org.jfree.chart.title.TextTitle(title, title_font));
			barChart.setBorderVisible(false);
			barChart.setBackgroundPaint(Color.lightGray);
			
			
			CategoryPlot categoryplot = (CategoryPlot) barChart.getPlot();
			categoryplot.setBackgroundPaint(Color.white);
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			
			domainAxis.setLabelFont(axis_font);
			domainAxis.setTickLabelFont(label_font);
			domainAxis.setMaximumCategoryLabelLines(12);
			domainAxis.setCategoryLabelPositionOffset(0);
			categoryplot.setDomainAxis(domainAxis);
			
			categoryplot.getRangeAxis().setLabelFont(axis_font);
			categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			
			BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
			barrenderer.setMaximumBarWidth(.30); // 15% of the chart
			barrenderer.setSeriesPaint(0, Color.green); // color of bar
			barrenderer.setShadowVisible(false);
			NumberAxis numberAxis = (NumberAxis) categoryplot.getRangeAxis();
			numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // y axis range whole number
			
			BufferedImage bufferedImage = barChart.createBufferedImage(width, height);
	        
			barChartImage				= Image.getInstance(writer, bufferedImage, 1.0f);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return barChartImage;
	}
	
	private Image getPieChartImage(PdfWriter writer, DefaultPieDataset pieChartDataSet, HttpServletRequest request,
			HttpServletResponse response) {

		if(pieChartDataSet.getItemCount()==0){
			return null;
		}
		Image pieChartImage = null;
		int height = 250;
		int width  = 525;
		JFreeChart pieChart = null;
		String title = null;
		String filtertype = request.getParameter("filtertype");
		String sid = request.getParameter("venue");
		String spid = request.getParameter("floor");
		String location = request.getParameter("location");
		String tagstatus = request.getParameter("tagstatus");
		
		java.awt.Font title_font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14);
		
		try{
			if (!filtertype.equals("tagname") && !filtertype.equals("tagType")) {
				if (filtertype.equals("default")) {
					title = "OVERALL TAG TYPES";
				} else if (filtertype.equals("venue")) {
					if (sid==null) {
						title = "OVERALL TAG TYPES";
					} else {
						title = "TAG TYPES IN VENUE";
					}

				} else if (filtertype.equals("floor")) {
					if (sid==null) {
						title = "OVERALL TAG TYPES";
					} else if (spid.equals("all")) {
						title = "TAG TYPES IN VENUE";
					} else {
						title = "TAG TYPES IN FLOOR";
					}

				} else if (filtertype.equals("location")) {

					if (sid==null) {
						title = "OVERALL TAG TYPES";
					} else if (spid.equals("all")) {
						title = "TAG TYPES IN VENUE";
					} else if (location.equals("all")) {
						title = "TAG TYPES IN FLOOR";
					} else {
						title = "TAG TYPES IN LOCATION";
					}
				} else if (filtertype.equals("tagstatus")) {
					switch (tagstatus) {
					case "all":
						title = "OVERALL TAG TYPES";
						break;
					case "checkedout":
						title = "CHECKEDOUT TAG TYPES";
						break;
					case "battery":
						title = "LOW BATTERY TAG TYPES";
						break;
					case "inactive":
						title = "INACTIVE TAG TYPES";
						break;
					case "active":
						title = "ACTIVE TAG TYPES";
						break;
					}

				}
			}
		
			pieChart = ChartFactory.createPieChart(title,pieChartDataSet,true,true,false);
			pieChart.setTitle(new org.jfree.chart.title.TextTitle(title, title_font));
			pieChart.setBackgroundPaint(Color.lightGray);
			PiePlot pieChartPlot = (PiePlot) pieChart.getPlot();
			pieChartPlot.setOutlinePaint(null);
			pieChartPlot.setBackgroundPaint(Color.lightGray);
			pieChartPlot.setLabelGenerator(null);
			pieChartPlot.setShadowXOffset(0);
			pieChartPlot.setShadowYOffset(0);
			
			 BufferedImage bufferedImage =  pieChart.createBufferedImage(width, height);
			 pieChartImage 		         =  Image.getInstance(writer, bufferedImage, 1.0f);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return pieChartImage;
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	private String getTitle(String filterType, String reportType) {
		String title = "";
		if (filterType.equals("default")) {
			title += "OVERALL ";
		} else {
			title += filterType.toUpperCase() + " ";
		}
		if (reportType != null && reportType.equals("nonasset")) {
			title += "PERSON BASED ";
		} else if (reportType != null && reportType.equals("asset")) {
			title += "ASSET BASED ";
		}
		title += "REPORT";
		return title;
	}
	
	private String getTimeSpent(String elapsed) {
		String timespent = "-";
		int elps = Integer.parseInt(elapsed);
		if (elps != 0) {
			int hours = elps / 3600;
			int minutes = (elps % 3600) / 60;
			int seconds = (elps % 3600) % 60;
			timespent = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		}
		return timespent;
	}

	
	@RequestMapping(value = "/alertpdf", method = RequestMethod.GET)
	public String tagalertpdf(
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request,HttpServletResponse response) {

		//String pdfFileName  = "TrilaterationReport.pdf";
		//String logoFileName = "C:/Users/sudavasu/Desktop/Testing/pic.png";
		
		String pdfFileName  = "./uploads/qubercloud.pdf";
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
				FileInputStream fileInputStream = new FileInputStream(pdfFile);
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
		
		PdfPTable table = new PdfPTable(12);
		table.setWidthPercentage(100);
		
		PdfPTable batterytable = new PdfPTable(10);
		batterytable.setWidthPercentage(100);
		
		PdfPTable devicetable = new PdfPTable(9);
		devicetable.setWidthPercentage(100);

		PdfPCell c1 = new PdfPCell(new Phrase("ID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Minor",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Major",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Tag Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("AssignedTo",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("State",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Last Seen",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		table.addCell(c1);

		table.setHeaderRows(1);
		
		
		
		//battery table
		
		c1 = new PdfPCell(new Phrase("ID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		batterytable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Minor",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Major",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Tag Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("AssignedTo",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		batterytable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Battery Level",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batterytable.addCell(c1);
		
		batterytable.setHeaderRows(1);
		
		
		// device table 
		
		
		c1 = new PdfPCell(new Phrase("UID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		devicetable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		devicetable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		devicetable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		devicetable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Status",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		devicetable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Last Active",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		devicetable.addCell(c1);

			
		devicetable.setHeaderRows(1);
		
		try {
			boolean pdfgenration = true;
			
			org.json.simple.JSONObject tagalerts 		= bleRestController.inactiveTags(cid,pdfgenration);
			org.json.simple.JSONObject batteryalerts 	= bleRestController.beaconBatteryAlert(cid,"40",pdfgenration);
			org.json.simple.JSONObject devicealerts 	= bleRestController.beaconDeviceAlert(cid,pdfgenration);
		
			Paragraph emptyLines = new Paragraph();
			addEmptyLine(emptyLines, 3);

			if (tagalerts != null && !tagalerts.isEmpty()) {
				
				org.json.simple.JSONArray array = (org.json.simple.JSONArray)tagalerts.get("inactivetags");
				Iterator<org.json.simple.JSONObject> i = array.iterator();
				
				while (i.hasNext()) {
					
					org.json.simple.JSONObject rep = i.next();
					
					String macaddr 		= (String) rep.get("macaddr");
					int  minor 			= Integer.parseInt(rep.get("minor").toString());
					int major 			= Integer.parseInt(rep.get("major").toString());
					String assignedTo	= (String) rep.get("assignedTo");
					String tagtype		= (String) rep.get("tagtype");
					String state 		= (String) rep.get("state");
					String floorname    = (String) rep.get("floorname");
					String location     = (String) rep.get("alias");
					String lastseen     = (String) rep.get("lastSeen");
				
					//LOG.info( "====INACTIVE TAG ALERTS=========");
					//LOG.info( " minor " +minor + " major " +major);
					
					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(String.valueOf(minor),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(String.valueOf(major),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(tagtype,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(assignedTo,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(floorname,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(location,redFont));
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
				
				subCatPart = new Paragraph("Tag Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(table);
				document.add(subCatPart);
				document.add(emptyLines);
//				document.newPage();
			}
			
			if (batteryalerts != null && !batteryalerts.isEmpty()) {
				
				org.json.simple.JSONArray array = (org.json.simple.JSONArray)batteryalerts.get("beaconbattery");
				Iterator<org.json.simple.JSONObject> i = array.iterator();
				
				while (i.hasNext()) {
					
					org.json.simple.JSONObject rep = i.next();
					
					String macaddr 		= (String) rep.get("macaddr");
					int  minor 			= Integer.parseInt(rep.get("minor").toString());
					int major 			= Integer.parseInt(rep.get("major").toString());
					String assignedTo	= (String) rep.get("assignedTo");
					String tagtype		= (String) rep.get("tagtype");
					String batterylevel = (String) rep.get("batterylevel");
					String location     = (String) rep.get("alias");
					String floorname    = (String) rep.get("floorname");
				
					//LOG.info( "====BATTERY ALERTS=========");
					
					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(String.valueOf(minor),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(String.valueOf(major),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(tagtype,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(assignedTo,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(floorname,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(location,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(batterylevel,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batterytable.addCell(c1);

				}
				
				subCatPart = new Paragraph("Battery Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(batterytable);
				document.add(subCatPart);
				document.add(emptyLines);
//				document.newPage();
			}
			
			//device alert
			
			
				if (devicealerts != null && !devicealerts.isEmpty()) {
				
				org.json.simple.JSONArray array = (org.json.simple.JSONArray)devicealerts.get("beacondevicealert");
				Iterator<org.json.simple.JSONObject> i = array.iterator();
				
				while (i.hasNext()) {
					
					org.json.simple.JSONObject rep = i.next();
					
					String portionname 		= (String) rep.get("portionname");
					//String  sitename 		= rep.get("sitename").toString();
					String uid				= (String) rep.get("uid");
					String type				= (String) rep.get("type");
					String status 			= (String) rep.get("status");
					String alias   			= (String) rep.get("alias");
					String timestamp		= (String) rep.get("timestamp");
					//LOG.info( "====DEVICE ALERTS=========");
					
					
					c1 = new PdfPCell(new Phrase(uid,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					devicetable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(String.valueOf(type),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					devicetable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(portionname,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					devicetable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(alias,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					devicetable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(status,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					devicetable.addCell(c1);
					
					c1 = new PdfPCell(new Phrase(timestamp,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					devicetable.addCell(c1);
				}
				
				subCatPart = new Paragraph("Device Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(devicetable);
				document.add(subCatPart);
				document.add(emptyLines);
//				document.newPage();
			}
			
			if ((devicealerts == null || devicealerts.isEmpty()) 
				&& (batteryalerts == null || batteryalerts.isEmpty())
				&& (tagalerts == null || tagalerts.isEmpty())) {
				subCatPart = addNoDataToPDF(subCatPart);
				document.add(subCatPart);
			}

		}catch(Exception e) {
			LOG.info("Tag Report display error " +e);
			e.printStackTrace();
		}
	}
	
	public void emailTrigger(String uid) {

		//String pdfFileName  = "/home/qubercomm/Desktop/pdf/pdf-sample.pdf";
		//String logoFileName = "/home/qubercomm/Desktop/pdf/logo.png";
		
		String pdfFileName  = "./uploads/qubercloud.pdf";
		String logoFileName = "./uploads/logo-home.png";

		UserAccount users	= userAccountService.findOneByUid(uid);
			
		if (users != null && users.isMailalert() != null && users.isMailalert().equals("true")) {
				
				String cid 		  = users.getCustomerId();
				String email      = users.getEmail();
				
				org.json.simple.JSONObject tagalerts     = bleRestController.inactiveTags(cid,null);
				org.json.simple.JSONObject batteryalerts = bleRestController.beaconBatteryAlert(cid,"40",null);
				org.json.simple.JSONObject devicealerts  = bleRestController.beaconDeviceAlert(cid,null);
				
				org.json.simple.JSONArray inactivetags   = (org.json.simple.JSONArray)tagalerts.get("inactivetags");
				org.json.simple.JSONArray lowbattery     = (org.json.simple.JSONArray)batteryalerts.get("beaconbattery");
				org.json.simple.JSONArray inactivedevice = (org.json.simple.JSONArray)devicealerts.get("beacondevicealert");
				
				org.json.simple.JSONObject inactivetag = (org.json.simple.JSONObject) inactivetags.get(0);
				String inactivemac = inactivetag.get("macaddr").toString();
				
				org.json.simple.JSONObject lowbatterytag = (org.json.simple.JSONObject) lowbattery.get(0);
				String lowbatterymac = lowbatterytag.get("macaddr").toString();
				
				org.json.simple.JSONObject inactivedev = (org.json.simple.JSONObject) inactivedevice.get(0);
				String inactivedevicemac = inactivedev.get("uid").toString();
				
				if (inactivemac.equals("-") && lowbatterymac.equals("-") && inactivedevicemac.equals("-")) {
					LOG.info("no email");
					return;
				}
			
				Document document = new Document(PageSize.A4, 36, 36, 90, 55);
				//LOG.info("Email Alerts enabled user " +uid);
				
				try {
					
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
								+ " PFA detailed list of inactive devices and tags.\n Please look in to this as a high priority.\n"
								+ " ALERTS - DEVICES/TAGS @RISK, REQUIRES YOUR IMMEDIATE ATTENTION. \n";
				
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
		 .append("PFA detailed list of inactive devices and tags.<br/> Please look in to this as a high priority.<br/>")
		 .append("ALERTS - DEVICES/TAGS @RISK, REQUIRE YOUR IMMEDIATE ATTENTION <br/>");
		 
		//LOG.info("email id " +		email);
		//LOG.info("mail body  " +   mailBody);
					
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
	
	@RequestMapping(value = "/alertcsv", method = RequestMethod.GET)
	public String tagalertcsv(@RequestParam(value = "cid", required = true) String cid,
							  HttpServletRequest request, HttpServletResponse response) throws IOException {

		String csvFileName = "./uploads/report.csv";
		//String csvFileName  = "/home/qubercomm/Desktop/pdf/report.csv";
		OutputStream out = null;

		try {
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String result        = "";
				String tagheader 	 = "";
				String batteryheader = "";
				String deviceheader  = "";
				// tag alerts
				tagheader = "ID,Minor,Major,Tag Type,Assigned To,Floor Name,Location,State,Last Seen\n";

				// battery table
				batteryheader = "ID,Minor,Major,Tag Type,Assigned To,Floor Name,Location,Battery Level\n";

				// device table

				deviceheader = "UID,Type,Floor Name,Location,Status,Last Active\n";

				try {
					org.json.simple.JSONObject tagalerts     = bleRestController.inactiveTags(cid,null);
					org.json.simple.JSONObject batteryalerts = bleRestController.beaconBatteryAlert(cid,"40",null);
					org.json.simple.JSONObject devicealerts  = bleRestController.beaconDeviceAlert(cid,null);

					if (tagalerts != null && !tagalerts.isEmpty()) {
                        result                                 = result.concat("TAG ALERT");
                        result 					               = result.concat("\n");
						result								   = result.concat(tagheader);
						
						org.json.simple.JSONArray array        = (org.json.simple.JSONArray) tagalerts.get("inactivetags");
						Iterator<org.json.simple.JSONObject> i = array.iterator();
						String inactivetagalerts    		   = "";
						
						while (i.hasNext()) {

							org.json.simple.JSONObject rep = i.next();

							String macaddr 		= (String) rep.get("macaddr");
							String minor 		= rep.get("minor").toString();
							String major 		= rep.get("major").toString();
							String assignedTo   = (String) rep.get("assignedTo");
							String tagtype 		= (String) rep.get("tagtype");
							String state 		= (String) rep.get("state");
							String floorname 	= (String) rep.get("floorname");
							String location     = (String) rep.get("alias");
							String lastseen     = (String) rep.get("lastSeen");
							
							inactivetagalerts 	= macaddr + "," + minor + "," + major + "," + tagtype + ","
												  + assignedTo + "," + floorname + "," +location+","+ state
												  + ","+lastseen + "\n";
							
							result 				= result.concat(inactivetagalerts);
						}
						result				    = result.concat("\n\n");
					}
					
					if (batteryalerts != null && !batteryalerts.isEmpty()) {
						result                                  = result.concat("BATTERY ALERT");
						result 					                = result.concat("\n");
						result									= result.concat(batteryheader);
						org.json.simple.JSONArray array			= (org.json.simple.JSONArray) batteryalerts.get("beaconbattery");
						Iterator<org.json.simple.JSONObject> i 	= array.iterator();
						String batteryalert 					= "";
						
						while (i.hasNext()) {

							org.json.simple.JSONObject rep = i.next();

							String macaddr 		= (String) rep.get("macaddr");
							String minor		= rep.get("minor").toString();
							String major		= rep.get("major").toString();
							String assignedTo 	= (String) rep.get("assignedTo");
							String tagtype 		= (String) rep.get("tagtype");
							String batterylevel = (String) rep.get("batterylevel");
							String floorname 	= (String) rep.get("floorname");
							String location     = (String) rep.get("alias");


							batteryalert 		= macaddr + "," + minor + "," + major + "," + tagtype + "," + assignedTo
												  + "," + floorname + "," +location+","+ batterylevel + "\n";
							
							result 				= result.concat(batteryalert);
						}
						result 					= result.concat("\n\n");
					}

					// device alert
					if (devicealerts != null && !devicealerts.isEmpty()) {
						result                                  = result.concat("DEVICE ALERT");
						result 					                = result.concat("\n");
						result 									= result.concat(deviceheader);
						org.json.simple.JSONArray array 		= (org.json.simple.JSONArray) devicealerts.get("beacondevicealert");
						Iterator<org.json.simple.JSONObject> i 	= array.iterator();
						String devicealert 						= "";
						
						while (i.hasNext()) {

							org.json.simple.JSONObject rep 	= i.next();

							String portionname 				= (String) rep.get("portionname");
							String uid 						= (String) rep.get("uid");
							String type 					= (String) rep.get("type");
							String status 					= (String) rep.get("status");
							String alias 					= (String) rep.get("alias");
							String lastactive 				= (String) rep.get("timestamp");
							
							devicealert 					= uid + "," + type + "," + portionname
															+ "," + alias + "," + status + "," + lastactive + "\n";
							result 							= result.concat(devicealert);
						}
						result 								= result.concat("\n\n");
					}

				} catch (Exception e) {
					LOG.info("Tag alert  csv file format download error " + e);
					e.printStackTrace();
				}

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + csvFileName);
				out = response.getOutputStream();
				out.write(result.getBytes());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			out.flush();
			out.close();
		}

		return csvFileName;
	}
	
	@RequestMapping(value = "/format", method = RequestMethod.GET)
	public String format(
			@RequestParam(value = "cid", 		required = false) String cid,
			@RequestParam(value = "venuename",  required = false) String sid,
			@RequestParam(value = "floorname",  required = false) String spid,
			@RequestParam(value = "location", 	required = false) String location,
			@RequestParam(value = "reporttype", required = false) String reporttype,
			@RequestParam(value = "macaddr", 	required = false) String macaddr,
			@RequestParam(value = "tagType",	required = false) String tagtype,
			@RequestParam(value = "tagstatus",	required = false) String tagstatus,
			@RequestParam(value = "tagname",	required = false) String assignedto,
			@RequestParam(value = "filtertype", required = true)  String filtertype,
			@RequestParam(value = "time", 		required = false) String days,
			@RequestParam(value = "fileformat", required = true)  String fileformat,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		String result = "";

		if (fileformat.equals("pdf")) {
			result = pdf(request, response);
		} else if (fileformat.equals("html")) {
			JSONObject jsonObj = htmlCharts(request, response);
			if (jsonObj != null) {
				result = jsonObj.toString();
			}
			LOG.info("result " + result);
		} else {
			result = csv(cid, sid, spid, location, reporttype, macaddr, tagtype, tagstatus, assignedto, filtertype,
					days, request, response);
		}

		return result;
	}
	
	@RequestMapping(value = "/csv", method = RequestMethod.GET)
	public String csv(
			@RequestParam(value = "cid", 		required = false) String cid,
			@RequestParam(value = "venuename",  required = false) String sid,
			@RequestParam(value = "floorname",  required = false) String spid,
			@RequestParam(value = "location", 	required = false) String location,
			@RequestParam(value = "reporttype", required = false) String reporttype,
			@RequestParam(value = "macaddr", 	required = false) String macaddr,
			@RequestParam(value = "tagType",	required = false) String tagtype,
			@RequestParam(value = "tagstatus",	required = false) String tagstatus,
			@RequestParam(value = "tagname",	required = false) String assignedto,
			@RequestParam(value = "filtertype", required = false) String filtertype,
			@RequestParam(value = "time", 		required = false) String days,
			HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		//String csvFileName  = "/home/qubercomm/Desktop/pdf/report.csv";
		String csvFileName  = "./uploads/report.csv";
		OutputStream out    = null;
		String header 	 	= "";
		 
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				
				if(sid != null && (sid.equals("all") || sid.equals("undefined"))){
					sid = "";
				}
				if(spid != null && (spid.equals("all") || spid.equals("undefined"))){
					spid = "";
				}
				
				if(filtertype.equals("deviceInfo")){

					header = "UID,Location Name,Version,Build,Device Uptime,App Uptime,State,Last Seen\n";
					JSONArray processedDetail   = deviceInfo(cid, sid, spid, request, response);
					JSONObject json = null;
					Iterator<JSONObject> iterProcessedDetail = processedDetail.iterator();
					while (iterProcessedDetail.hasNext()) {
						json = iterProcessedDetail.next();
						String uid = json.getString("uid").toUpperCase();
						String locationname = json.getString("locationname").toUpperCase();
						String version = json.getString("version").toUpperCase();
						String build = json.getString("build").toUpperCase();
						String deviceUptime = json.getString("deviceUptime").toUpperCase();
						String appUptime = json.getString("appUptime").toUpperCase();
						String state = json.getString("state").toUpperCase();
						String lastseen = json.getString("lastSeen").toUpperCase();
						header += uid + "," + locationname + "," + version + "," + build + "," + deviceUptime + ","
								+ appUptime + "," + state + "," + lastseen + "\n";
					}
				}else{
					JSONObject processedDetail   = tagDetailsProcessing(null,request,response);
					net.sf.json.JSONArray report = (net.sf.json.JSONArray) processedDetail.get("report");
					String overallElapsedTime	 = (String) processedDetail.get("overallTime");
					int noOfTags			     = (int) processedDetail.get("tags");
					
					String exit 	 = null;
					String timespent = null;
					
					header = "NO Of Tags,Man Hours Spent\n" + noOfTags + "," + getTimeSpent(overallElapsedTime) + "\n\n";
					header = header.concat("Details\n");
					header = header.concat("ID,Name,Type");
					
					if (tagstatus != null && !tagstatus.equals("battery")) {
						
						header = header.concat(",Status");
					}
					
					if (tagstatus != null && (tagstatus.equals("inactive") || tagstatus.equals("active") || tagstatus.equals("all"))) {
						
						header = header.concat(",State");
						
						if( (tagstatus.equals("inactive"))){
							
							header = header.concat(",Lastseen");
							
						}
					}

					if (filtertype.equals("location") || filtertype.equals("tagname")) {
						
						header = header.concat(",Location");
						
					} else {
						
						header = header.concat(",Floor");
					}
					
					if(macaddr != null){
						header = header.concat(",Location");
					}
					
					if (tagstatus != null && (tagstatus.equalsIgnoreCase("battery")||tagstatus.equals("all"))) {
						header = header.concat(",Battery Level");
					}
					
					if (tagstatus == null) {
						header = header.concat(",Enter,Exit,TimeSpent");
					}

					header = header.concat("\n");

					if (report != null && !report.isEmpty()) {
						
						Iterator<net.sf.json.JSONObject> i = report.iterator();
						
						while (i.hasNext()) {
							
							net.sf.json.JSONObject rep = i.next();
							
							String tagid 		= rep.getString("tagid");
							assignedto 			= rep.getString("assignedTo");
							tagtype 			= rep.getString("tagType");
							String tagStatus	= "";
							String tagstate		= "";
							String enter 		= "no data";
							String elapsed      = "";
							long lastSeen       = 0;
							Date lastseendate   = null;
							
							if (tagstatus != null && !tagstatus.equals("battery")) {
								tagStatus = rep.getString("tagStatus");
							}
							
							if (tagstatus != null && (tagstatus.equals("inactive") || tagstatus.equals("active") || tagstatus.equals("all"))) {
								tagstate = rep.getString("tagState");
								if(tagstatus.equals("inactive")) {
									lastSeen = rep.getLong("lastSeen");
								}
							}
	                        
							if (tagstatus == null) {

								if (rep.containsKey("entry")) {
									enter	  = rep.getString("entry");
								}
								if (rep.containsKey("exit")) {
									exit      =  rep.getString("exit");
									elapsed   =  rep.getString("elapsed");
									timespent = getTimeSpent(elapsed);
								}
								if (exit == null || timespent == null) {
									exit = "Did not exit";
									timespent = "-";
								}
							}
							
							header = header.concat(tagid.toUpperCase() + "," + assignedto.toUpperCase() + "," + tagtype.toUpperCase());

							if (tagstatus != null && !tagstatus.equals("battery")) {
								header = header.concat("," + tagStatus.toUpperCase());
							}
							
							if (tagstatus != null && (tagstatus.equals("inactive") || tagstatus.equals("active") || tagstatus.equals("all"))) {
								header = header.concat("," + tagstate.toUpperCase());
							}
							
							if ((tagstatus != null && tagstatus.equals("inactive"))) {
								if (lastSeen != 0){
									lastseendate = new Date(lastSeen);
									header = header.concat("," + format.format(lastseendate));
								}else{
									header = header.concat("," + "-");
								}								
							}

							if (filtertype.equals("location") || filtertype.equals("tagname")) {
								header = header.concat("," + rep.getString("deviceLocation").toUpperCase());
							} else {
								header = header.concat("," + rep.getString("floorname").toUpperCase());
							}

							if(macaddr != null){
								header = header.concat("," + rep.getString("deviceLocation").toUpperCase());
							}
							if (tagstatus != null && (tagstatus.equals("battery") || tagstatus.equals("all"))) {
								header = header.concat("," + rep.getString("battery") + "%");
							}

							if (tagstatus == null) {
								header = header.concat("," + enter + "," + exit + "," + timespent);
							}
							header = header.concat("\n");
						}
					}
				}
				
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + csvFileName);
				out = response.getOutputStream();
				out.write(header.getBytes());

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.flush();
				out.close();
			}
		}
		return csvFileName;
	}
	
	@RequestMapping(value = "/venuelist", method = RequestMethod.GET)
	public JSONObject venuelist(@RequestParam(value = "cid", required = false) String cid,
								HttpServletRequest request,HttpServletResponse response) {
		
		if(cid == null){
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		JSONObject json = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonList = new JSONObject();
		
		
		Iterable<Site> siteList = new ArrayList<Site>();
		siteList = siteService.findByCustomerId(cid);
		
		if (siteList != null) {
			for (Site site : siteList) {
				json = new JSONObject();
				if (site.getStatus().equals(CustomerUtils.ACTIVE())) {
					json.put("id", site.getId());
					json.put("name", site.getUid());
					jsonArray.add(json);
				}
			}
			jsonList.put("site", jsonArray);
		}
		return jsonList;

	}
	
	@RequestMapping(value = "/floorlist", method = RequestMethod.GET)
	public JSONObject floorlist(@RequestParam(value = "cid", required = false) String cid,
								@RequestParam(value = "sid", required = false) String sid,
								HttpServletRequest request,HttpServletResponse response) {
		
		if(cid == null){
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		
		Iterable<Portion> portionList = new ArrayList<Portion>();
	
		///LOG.info("sid is "+sid);
		
		JSONObject json = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonList = new JSONObject();
		if(sid.equalsIgnoreCase("all")){
			portionList = portionservice.findByCid(cid);
		}else{
			portionList = portionservice.findBySiteId(sid);
		}
		
		if (portionList != null) {
			for (Portion p : portionList) {
				json = new JSONObject();
					json.put("id", p.getId());
					json.put("name", p.getUid());
					jsonArray.add(json);
			}
			jsonList.put("portion", jsonArray);
		}
		//LOG.info("list sent is "+jsonList);
		return jsonList;

	}
	
	@RequestMapping(value = "/locationlist", method = RequestMethod.GET)
	public JSONObject locationlist(@RequestParam(value = "cid", required = false) String cid,
								   @RequestParam(value = "sid", required = false) String sid,
								   @RequestParam(value = "spid", required = false) String spid,
								HttpServletRequest request,HttpServletResponse response) {
		
		if(cid == null){
			cid = SessionUtil.getCurrentCustomer(request.getSession());
		}
		JSONObject json = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonList = new JSONObject();
		if(sid.equalsIgnoreCase("all") || spid.equalsIgnoreCase("all")){
			return  jsonList;
		}
		Iterable<BeaconDevice> ndList = new ArrayList<BeaconDevice>();
		
		ndList = beacondeviceservice.findBySpid(spid);
		
		if (ndList != null) {
			for (BeaconDevice d : ndList) {
				json = new JSONObject();
				if(d.getType().equalsIgnoreCase("receiver")){
					json.put("id",	 d.getUid());
					json.put("name", d.getAlias());
					jsonArray.add(json);
				}
			}
			jsonList.put("location", jsonArray);
		}
		//LOG.info("list sent is "+jsonList);
		return jsonList;

	}
	
	@RequestMapping(value = "/htmlCharts", method = RequestMethod.GET)
	public JSONObject htmlCharts(HttpServletRequest request, HttpServletResponse response) {
		if (SessionUtil.isAuthorized(request.getSession())) {
			String filterType = request.getParameter("filtertype");
			JSONObject htmlChartDetail = null;
		
			switch(filterType){
			
			case "tagType":
				htmlChartDetail = getTagTypeBasedDetails(request);
				break;
			case "tagname":
				htmlChartDetail = getTagNameBasedDetails(request);
				break;

			default:
				htmlChartDetail = getDefaultDetails(request);

			}
			return htmlChartDetail;
		}
		return null;
	}

	private JSONObject getDefaultDetails(HttpServletRequest request) {

		JSONObject chartDetails = new JSONObject();
		JSONArray floorFreqCount = new JSONArray();
		JSONArray locationFreqCount = new JSONArray();
		JSONArray floorBasedTagType = null;
		JSONArray locationBasedTagType = null;

		List<Map<String, Object>> rxtx = null;
		List<Map<String, Object>> cpu = null;
		List<Map<String, Object>> mem = null;

		String cid = request.getParameter("cid");
		String sid = request.getParameter("venuename");
		String spid = request.getParameter("floorname");
		String location = request.getParameter("location");

		String filtertype = request.getParameter("filtertype");
		String time = request.getParameter("time");
		String listisbasedon = "";
		String reportType = request.getParameter("reporttype");
		
		if (time == null || time.isEmpty()) {
			time = "24h";
		}

		int flag = 0;
		
		switch (filtertype) {
		
		case "location":
			if (location != null && !location.equals("undefined") && !location.isEmpty() && !location.equals("all")) {
				rxtx = bleNetworkDeviceRestController.rxtx(null, null, null, location, time, "report", request, null);
				cpu = bleNetworkDeviceRestController.getcpu(null, null, null, location.toLowerCase(), "report", time);
				mem = bleNetworkDeviceRestController.getmem(null, null, null, location.toLowerCase(), "report", time);
				listisbasedon ="location";
				flag = 1;
			}
			if (flag == 1) {
				break;
			}
		case "floor":
			if (spid != null && !spid.equals("undefined") && !spid.isEmpty() && !spid.equals("all")) {
				rxtx = bleNetworkDeviceRestController.rxtx(null, spid, null, null, time, "report", request, null);
				listisbasedon="spid";
				flag = 1;
			}
			if (flag == 1) {
				break;
			}
		case "venue":
			if (sid != null && !sid.equals("undefined") && !sid.isEmpty() && !sid.equals("all")) {
				rxtx = bleNetworkDeviceRestController.rxtx(sid, null, null, null, time, "report", request, null);
				listisbasedon = "sid";
				flag = 1;
			}
			if (flag == 1) {
				break;
			}

		default:
			listisbasedon = "cid";

		}

		if(filtertype.equals("location")){
			if(location.equals("all")){
				floorFreqCount = getfloorFreqCount(listisbasedon,cid,sid,spid,location,reportType,false,time,null,null);
			}
			locationFreqCount = getlocationFreqCount(listisbasedon,cid,sid,spid,location,reportType,true,time,null);
		}else{
			floorFreqCount = getfloorFreqCount(listisbasedon,cid,sid,spid,location,reportType,true,time,null,null);
			locationFreqCount = getlocationFreqCount(listisbasedon,cid,sid,spid,location,reportType,false,time,null);
		}
		
		if(filtertype.equals("location")){
			locationBasedTagType = getLocationBasedTagTypes(locationFreqCount);
			chartDetails.put("locationBasedTagType", locationBasedTagType);
			chartDetails.put("cpu", cpu);
			chartDetails.put("mem", mem);
		}else{
			floorBasedTagType = getFloorBasedTagTypes(floorFreqCount);
			chartDetails.put("floorBasedTagType", floorBasedTagType);
		}
		if(rxtx != null){
			chartDetails.put("rxtx", rxtx);
		}
		chartDetails.put("locationFreqCount", locationFreqCount);
		chartDetails.put("floorFreqCount", floorFreqCount);
		

		return chartDetails;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getTagNameBasedDetails(HttpServletRequest request) {
	
		List<Map<String, Object>> logs = null;
		HashMap<String,Integer> locmap   = new HashMap<String,Integer>();
		Map<String,String> receivers = new HashMap<String,String>();
		List<BeaconDevice> receiverList = null;
		List<Beacon> beaconlist = null;
		
		String tagname  = request.getParameter("tagname");
		String cid 	    = request.getParameter("cid");
		String sid 	    = request.getParameter("venuename");
		String spid     = request.getParameter("floorname");
		String location = request.getParameter("location");

		String time = request.getParameter("time");
		String listisbasedon = "cid";
		String deviceType = "receiver";
		
		if(location != null && !location.equals("all")){
			listisbasedon = "location";
		}else if(spid != null && !spid.equals("all")){
			receiverList = beacondeviceservice.findBySpidAndType(spid, deviceType);
			listisbasedon = "spid";
		}else if(sid != null && !sid.equals("all")){
			receiverList = beacondeviceservice.findBySidAndType(sid, deviceType);
			listisbasedon = "sid";
		}else{
			receiverList = beacondeviceservice.findByCidAndType(cid, deviceType);
		}
		
		if (receiverList != null && receiverList.size() > 0) {
			for (BeaconDevice receiver : receiverList) {
				String uid = receiver.getUid();
				String name = receiver.getAlias() == null ? uid : receiver.getAlias();
				receivers.put(uid, name.toLowerCase());
				beaconlist = beaconservice.findByCidAssingnedtoStatusAndReceiverInfo(cid,tagname,"checkedout",uid);
				if (beaconlist != null && beaconlist.size() > 0) {
					locmap.put(name, beaconlist.size());
				} else {
					locmap.put(name, 0);
				}
			}
		}
		
		if(time==null || time.isEmpty()){
			time = "24h";
		};
		
		int size = 100;
		
		JSONObject tagnameBasedDetails = new JSONObject();
		JSONArray floorFreqCount = new JSONArray();
		JSONArray locationFreqCount = new JSONArray();
		JSONArray activity = null;
		JSONArray activityArray = new JSONArray();
		JSONObject activityJson = null;
		JSONObject json = new JSONObject();

		try{
			
			if (!listisbasedon.equals("location")) {
				floorFreqCount = getfloorFreqCount(listisbasedon, cid, sid, spid, null, "all", false, time, null,tagname);
			}
			
			String fsql = "index=" + trilaterationEventTable + ",size = "+size+",type=trilateration,query=timestamp:>now-" + time
					+ " AND opcode:\"reports\" AND assingedto:"+tagname+" AND cid:" + cid ;
			
			if (sid != null && !sid.equals("all") && !sid.equals("undefined")) {
				fsql += " AND sid:" + sid;
			} 
			
			if (spid != null && !spid.equals("all") && !spid.equals("undefined")) {
				fsql += " AND spid:" + spid;
			} 
			
			if (location != null && !location.equals("all") && !location.equals("undefined")) {
				fsql += " AND location:\"" + location + "\"";
			}
			
			fsql+= " AND tagtype:* AND entry_loc:* AND exit_loc:*,sort=timestamp DESC|value(timestamp,Date,typecast=date);"
					+ "value(tagid,tagid,null); value(assingedto,assingedto,null);value(location,location,null);"
					+ "value(entry_loc ,entry_loc,null);value(exit_loc,exit_loc,null);value(elapsed_loc,elapsed_loc,null);"
					+ "|table,sort=Date:desc;";

			logs = fsqlRestController.query(fsql);
			
			if (logs != null && !logs.isEmpty()) {
				Iterator<Map<String, Object>> iterator = logs.iterator();

				while (iterator.hasNext()) {
					TreeMap<String, Object> log = new TreeMap<String, Object>(iterator.next());
					
					String tagid 		= log.get("tagid").toString();
					location 			= log.get("location").toString();
					String locationname = "";
					
					if(!receivers.containsKey(location)){
						BeaconDevice bd = beacondeviceservice.findByUidAndCid(location, cid);
						locationname = bd.getAlias().toLowerCase();
						receivers.put(location, locationname);
					}else{
						locationname = receivers.get(location);
					}
					
					String entry_loc = log.get("entry_loc").toString();
					String exit_loc = log.get("exit_loc").toString();
					String elapsed_loc = log.get("elapsed_loc").toString();
					String timespent = getTimeSpent(elapsed_loc);
					if(json.containsKey(tagid)){
						activity = json.getJSONArray(tagid);
					}else{
						activity = new JSONArray();
					}
					activityJson = new JSONObject();
					activityJson.put("locationname", locationname);
					activityJson.put("entry_loc", entry_loc);
					activityJson.put("exit_loc", exit_loc);
					activityJson.put("timespent", timespent);
					activity.add(0,activityJson);
					json.put(tagid, activity);

					if (!listisbasedon.equals("location")) {
						if (locmap.containsKey(locationname)) {

							int count = Integer.parseInt(String.valueOf(locmap.get(locationname)));
							locmap.put(locationname, count + 1);
						} else {
							locmap.put(locationname, 1);
						}
					}
				}
			}
			
			Iterator jsonkeyiter = json.keys();
			while(jsonkeyiter.hasNext()){
				String tagid = jsonkeyiter.next().toString();
				activity = json.getJSONArray(tagid);
				JSONObject obj = new JSONObject();
				obj.put("tagid", tagid);
				obj.put("activity", activity);
				activityArray.add(obj);
			}
			
			if (!listisbasedon.equals("location")) {
				for (Map.Entry<String, Integer> entry_map : locmap.entrySet()) {
					String locationname = entry_map.getKey();
					Integer count = entry_map.getValue();
					if(count>0){
						json = new JSONObject();
						json.put("locationname", locationname);
						json.put("frequency", count);
						locationFreqCount.add(json);
					}
				}
			}
			
			tagnameBasedDetails.put("floorFreqCount", floorFreqCount);
			tagnameBasedDetails.put("locationFreqCount", locationFreqCount);
			tagnameBasedDetails.put("activityArray", activityArray);
			return tagnameBasedDetails;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject getTagTypeBasedDetails(HttpServletRequest request) {
		
		String cid 	= request.getParameter("cid");
		String tagtype = request.getParameter("tagType");
		String time = request.getParameter("time");
		String listisbasedon = "cid";
		if(time==null || time.isEmpty()){
			time = "24h";
		}
		
		JSONObject tagTypeBasedDetails = new JSONObject();
		JSONArray floorFreqCount 	   = null;
		JSONArray locationFreqCount    = null;
		
		floorFreqCount = getfloorFreqCount(listisbasedon,cid,null,null,null,"all",false,time,tagtype,null);
		locationFreqCount = getlocationFreqCount(listisbasedon,cid,null,null,null,"all",false,time,tagtype);
		tagTypeBasedDetails.put("floorFreqCount", floorFreqCount);
		tagTypeBasedDetails.put("locationFreqCount", locationFreqCount);

		return tagTypeBasedDetails;
		
	}
	
	private JSONArray getfloorFreqCount(String listisbasedon, String cid, String sid, String spid, String location,
			String reportType,boolean tagtypedetail,String time,String type,String assingedto) {
		
		JSONArray floorFreqCount = new JSONArray();
		JSONObject json = null;
		JSONArray tagTypeArray = null;
		List<Map<String, Object>> logs = null;
		List<Beacon> beaconsCurrentlyInFloor = null;
		List<Portion> portionlist = null;
		HashMap<String,String> portionMap   = new HashMap<String,String>();
		HashMap<String,Integer> floormap   = new HashMap<String,Integer>();
		HashMap<String,HashMap<String, Integer>> floorwisetagType = new HashMap<String,HashMap<String, Integer>>();
		HashMap<String,Integer> tagTypemap = null;
		List<String> nonAssetList = Arrays.asList("Male","Female","Child","Doctor","User");
		
		String nonAsset = "tagtype:(male OR female OR child OR doctor OR user)";
		
		String fsql = "index="+trilaterationEventTable + ",type=trilateration,query=timestamp:>now-" + time 
				+ " AND opcode:\"reports\" AND ";
		switch(listisbasedon){
		case "cid":
			fsql += "cid:"+cid+" ";
			portionlist = portionservice.findByCid(cid);
			break;
		case "sid":
			fsql += "sid:"+sid+" ";
			portionlist = portionservice.findBySiteId(sid);
			break;
		case "spid":
			fsql += "spid:"+spid+" ";
			Portion p = portionservice.findById(spid);
			portionlist = new ArrayList<Portion>();
			portionlist.add(p);
			break;
		}
		
		if(portionlist != null && portionlist.size()>0){
			String p_spid,p_name;
			for(Portion p:portionlist){
				p_spid = p.getId();
				p_name = p.getUid().toLowerCase();
				floormap.put(p_spid, 0);
				portionMap.put(p_spid, p_name);
			}
		}
		
		if (reportType != null && reportType.equals("asset")) {
			fsql += "NOT "+nonAsset;
		} else if (reportType != null && reportType.equals("nonasset")) {
			fsql += nonAsset;
		}
		
		if(type!= null){
			fsql += " AND tagtype:"+type;
		}else{
			fsql += " AND tagtype:*";
		}
		
		if (assingedto != null) {
			fsql += " AND assingedto:" + assingedto;
		}
		
		fsql += " AND exit_floor:* AND entry_floor:* ,sort=timestamp DESC|value(timestamp,Date,typecast=date);value(tagid,tagid,null);"
				+ "value(assingedto,assingedto,null);value(tagtype,tagtype,null);value(spid,spid,null);"
				+ "value(entry_floor ,entry_floor,null);value(exit_floor,exit_floor,null);value(elapsed_floor,elapsed_floor,null);"
				+ "|table,sort=Date:desc;";
		
		
		logs = fsqlRestController.query(fsql);
		
		if (logs != null && !logs.isEmpty()) {

			Iterator<Map<String, Object>> iterLogs = logs.iterator();
			if (tagtypedetail) {
				tagTypemap = new HashMap<String, Integer>();
			}

			while (iterLogs.hasNext()) {
				HashMap<String, Object> hashMap = (HashMap<String, Object>) iterLogs.next();

				String log_spid = (String) hashMap.getOrDefault("spid", "");
				String tagtype = (String) hashMap.getOrDefault("tagtype", "");

				if (floormap.containsKey(log_spid)) {
					int count = Integer.parseInt(String.valueOf(floormap.get(log_spid)));
					floormap.put(log_spid, count + 1);
				} else {
					floormap.put(log_spid, 1);
				}
				
				if (tagtypedetail) {
					
					if(floorwisetagType.containsKey(log_spid)){
						tagTypemap = floorwisetagType.get(log_spid);
					}else{
						tagTypemap = new HashMap<String, Integer>();
					}

					if (tagTypemap.containsKey(tagtype)) {
						int cnt = Integer.parseInt(String.valueOf(tagTypemap.get(tagtype)));
						tagTypemap.put(tagtype, cnt + 1);
					} else {
						tagTypemap.put(tagtype, 1);
					}
					floorwisetagType.put(log_spid, tagTypemap);
				
				}
			}
		}
		
		for (Map.Entry<String, Integer> m : floormap.entrySet()) {

			String map_spid = m.getKey();
			Integer count = m.getValue();
			tagTypeArray = new JSONArray();
			String floorname;
			
			json = new JSONObject();

			if(portionMap.containsKey(map_spid)){
				floorname = portionMap.get(map_spid);
			}else{
				Portion p = portionservice.findById(map_spid);
				floorname = p.getUid() == null ? "unknown" : p.getUid().toLowerCase();
				portionMap.put(map_spid, floorname);
			}
			
			String status = "checkedout";
			
			if (type != null) {
				beaconsCurrentlyInFloor = beaconservice.getSavedBeaconBySpidTagTypeAndStatus(map_spid, type, status);
			} else if (assingedto != null) {
				beaconsCurrentlyInFloor = beaconservice.getSavedBeaconBySpidStatusAndAssignedto(map_spid, status,assingedto);

			} else {
				if (reportType != null && reportType.equals("asset")) {
					beaconsCurrentlyInFloor = beaconservice.getSavedBeaconBySpidNotInTagTypeAndStatus(map_spid,nonAssetList, status);
				} else if (reportType != null && reportType.equals("nonasset ")) {
					beaconsCurrentlyInFloor = beaconservice.getSavedBeaconBySpidInTagTypeAndStatus(map_spid,nonAssetList, status);
				} else {
					beaconsCurrentlyInFloor = beaconservice.getSavedBeaconBySpidAndStatus(map_spid, status);
				}
			}
			
			if (beaconsCurrentlyInFloor != null && beaconsCurrentlyInFloor.size() > 0) {
				count += beaconsCurrentlyInFloor.size();
				if(tagtypedetail){
					String tagtype;
					if(floorwisetagType.containsKey(map_spid)){
						tagTypemap = floorwisetagType.get(map_spid);
					}else{
						tagTypemap = new HashMap<String, Integer>();
					}
					
					for (Beacon b : beaconsCurrentlyInFloor) {
						tagtype = b.getTag_type();
						if (tagTypemap.containsKey(tagtype)) {
							int cnt = Integer.parseInt(String.valueOf(tagTypemap.get(tagtype)));
							tagTypemap.put(tagtype, cnt + 1);
						} else {
							tagTypemap.put(tagtype, 1);
						}
					}
					floorwisetagType.put(map_spid, tagTypemap);
				}
				
			}
			
			if(tagtypedetail && floorwisetagType.containsKey(map_spid)){
				tagTypemap = floorwisetagType.get(map_spid);
				for (Map.Entry<String, Integer> entry_map : tagTypemap.entrySet()) {
					String tagtype = entry_map.getKey();
					Integer tagcount = entry_map.getValue();
					if(tagcount>0){
						json = new JSONObject();
						json.put("tagtype",tagtype);
						json.put("count", tagcount);
						tagTypeArray.add(json);
					}
				}
			}
			
			if (count > 0) {
				json.put("floorname", floorname);
				json.put("frequency", count);
				if (tagtypedetail) {
					json.put("tagTypes", tagTypeArray);
				}
				floorFreqCount.add(json);
			}

		}
		return floorFreqCount;
	}
	
	private JSONArray getlocationFreqCount(String listisbasedon, String cid, String sid, String spid, String location,
			String reportType,boolean tagtypedetail,String time,String type) {
		
		JSONArray locFreqCount = new JSONArray();
		JSONObject json = null;
		JSONArray tagTypeArray = null;
		List<Map<String, Object>> logs = null;
		
		HashMap<String,Integer> locmap   = new HashMap<String,Integer>();
		HashMap<String,HashMap<String, Integer>> locwisetagType  = new HashMap<String,HashMap<String, Integer>>();
		HashMap<String,Integer> tagTypemap = null;
		HashMap<String,String> recvrMap = new HashMap<String,String>();
		List<BeaconDevice> reciverList = null;
		String deviceType ="receiver";
		
		List<String> nonAssetList = Arrays.asList("Male","Female","Child","Doctor","User");
		String nonAsset = " tagtype:(male OR female OR child OR doctor OR user)";
		
		String fsql = "index="+trilaterationEventTable + ",type=trilateration,query=timestamp:>now-" + time 
				+ " AND opcode:\"reports\" AND ";
		switch(listisbasedon){
		case "cid":
			fsql += "cid:"+cid;
			reciverList = beacondeviceservice.findByCidAndType(cid, deviceType);
			break;
		case "sid":
			fsql += "sid:"+sid;
			reciverList = beacondeviceservice.findBySidAndType(sid, deviceType);
			break;
		case "spid":
			fsql += "spid:"+spid;
			reciverList = beacondeviceservice.findBySpidAndType(spid, deviceType);
			break;
		case "location":
			fsql += "location:\""+location+"\"";
			BeaconDevice bd = beacondeviceservice.findByUidAndCid(location, cid);
			String r_uid = bd.getUid();
			String r_name = bd.getAlias() == null ? r_uid : bd.getAlias().toLowerCase();
			recvrMap.put(r_uid, r_name);
			locmap.put(r_uid, 0);
			reciverList = new ArrayList<BeaconDevice>();
			reciverList.add(bd);
			break;
		}
		
		if (reportType != null && reportType.equals("asset")) {
			fsql += " NOT "+nonAsset;
		} else if (reportType != null && reportType.equals("nonasset")) {
			fsql += nonAsset;
		}
		
		if(type!= null){
			fsql += " AND tagtype:"+type;
		}else{
			fsql += " AND tagtype:*";
		}
		
		if (reciverList != null && reciverList.size() > 0) {
			String r_uid, r_name;
			for (BeaconDevice bd : reciverList) {
				r_uid = bd.getUid();
				r_name = bd.getAlias() == null ? r_uid : bd.getAlias().toLowerCase();
				locmap.put(r_uid, 0);
				recvrMap.put(r_uid, r_name);
			}
		}
		
		fsql += " AND exit_loc:* AND entry_loc:* AND location:*,sort=timestamp DESC|value(timestamp,Date,typecast=date);value(tagid,tagid,null);"
				+ "value(assingedto,assingedto,null);value(tagtype,tagtype,null);value(location,location,null);"
				+ "value(entry_loc ,entry_loc,null);value(exit_loc,exit_loc,null);value(elapsed_loc,elapsed_loc,null);"
				+ "|table,sort=Date:desc;";
		
		
		logs = fsqlRestController.query(fsql);
		
		Iterator<Map<String, Object>> iterLogs = logs.iterator();
		
		if(tagtypedetail){
			tagTypemap = new HashMap<String,Integer>();
		}
		
		while (iterLogs.hasNext()) {
			HashMap<String, Object> hashMap = (HashMap<String, Object>) iterLogs.next();

			String log_receiverId = (String) hashMap.getOrDefault("location", "");
			String tagtype = (String) hashMap.getOrDefault("tagtype", "");
			
			if (locmap.containsKey(log_receiverId)) {
				int count = Integer.parseInt(String.valueOf(locmap.get(log_receiverId)));
				locmap.put(log_receiverId, count + 1);
			} else {
				locmap.put(log_receiverId, 1);
			}

			if (tagtypedetail) {
				if(locwisetagType.containsKey(log_receiverId)){
					tagTypemap = (HashMap<String, Integer>) locwisetagType.get(log_receiverId);
				}else{
					tagTypemap = new HashMap<String, Integer>();
				}
				if (tagTypemap.containsKey(tagtype)) {
					int cnt = Integer.parseInt(String.valueOf(tagTypemap.get(tagtype)));
					tagTypemap.put(tagtype, cnt + 1);
				} else {
					tagTypemap.put(tagtype, 1);
				}
				locwisetagType.put(log_receiverId, tagTypemap);
			}
		}
		
		for (Map.Entry<String, Integer> m : locmap.entrySet()) {

			String map_receiverId = m.getKey();
			Integer count = m.getValue();
			tagTypeArray = new JSONArray();
			json = new JSONObject();
			String locationName;
			
			if(recvrMap.containsKey(map_receiverId)){
				locationName = recvrMap.get(map_receiverId);
			}else{
				BeaconDevice devi 	= beacondeviceservice.findByUidAndCid(map_receiverId, cid);
				locationName = devi.getAlias() == null ? map_receiverId : devi.getAlias().toLowerCase();
			}
			
			List<Beacon> beaconsCurrentlyInLocation = null;
			String status = "checkedout";
			
			if(type != null){
				beaconsCurrentlyInLocation = beaconservice.getSavedBeaconByRecieverInfoTagTypeAndStatus(map_receiverId, type, status);
			}else{

				if (reportType != null && reportType.equals("asset")) {
					beaconsCurrentlyInLocation = beaconservice.getSavedBeaconByRecieverInfoNotInTagTypeAndStatus(map_receiverId,nonAssetList, status);
				} else if (reportType != null && reportType.equals("nonasset")) {
					beaconsCurrentlyInLocation = beaconservice.getSavedBeaconByRecieverInfoInTagTypeAndStatus(map_receiverId,nonAssetList, status);
				} else {
					beaconsCurrentlyInLocation = beaconservice.findByreciverinfoAndStatus(map_receiverId, status);
				}
				
			}
			
			if (beaconsCurrentlyInLocation != null && beaconsCurrentlyInLocation.size() > 0) {
				count += beaconsCurrentlyInLocation.size();
				if (tagtypedetail) {
					if (locwisetagType.containsKey(map_receiverId)) {
						tagTypemap = locwisetagType.get(map_receiverId);
					} else {
						tagTypemap = new HashMap<String, Integer>();
					}
					String tagtype;
					for (Beacon b : beaconsCurrentlyInLocation) {

						tagtype = b.getTag_type();
						if (tagTypemap.containsKey(tagtype)) {
							int cnt = Integer.parseInt(String.valueOf(tagTypemap.get(tagtype)));
							tagTypemap.put(tagtype, cnt + 1);
						} else {
							tagTypemap.put(tagtype, 1);
						}
						locwisetagType.put(map_receiverId, tagTypemap);
					}
				}
			}
			
			if(tagtypedetail && locwisetagType.containsKey(map_receiverId)){
				tagTypemap = (HashMap<String, Integer>)locwisetagType.get(map_receiverId);
				
				for (Map.Entry<String, Integer> entry_map : tagTypemap.entrySet()) {
					String tagtype = entry_map.getKey();
					Integer tagcount = entry_map.getValue();
					json = new JSONObject();
					json.put("tagtype",tagtype);
					json.put("count", tagcount);
					tagTypeArray.add(json);
				}
			}
			
			if (count > 0) {
				json.put("locationname", locationName);
				json.put("frequency", count);
				if (tagtypedetail) {
					json.put("tagTypes", tagTypeArray);
				}
				locFreqCount.add(json);
			}
		}
		return locFreqCount;
	}

	private JSONArray getFloorBasedTagTypes(JSONArray floorFreqCount) {
		JSONArray floorBasedTagTypes = new JSONArray();
		JSONArray tagTypes = null;
		JSONObject floorDetail = null;
		JSONObject json = null;
		String floorname = null;
		Iterator<JSONObject> iterJsonArray = floorFreqCount.iterator();
		while(iterJsonArray.hasNext()){
			floorDetail = iterJsonArray.next();
			if(floorDetail.containsKey("tagTypes")){
				tagTypes = floorDetail.getJSONArray("tagTypes");
				floorname = floorDetail.getString("floorname");
				json = new JSONObject();
				json.put("floorname", floorname);
				json.put("tagTypes", tagTypes);
				floorBasedTagTypes.add(json);
			}
		}
		return floorBasedTagTypes;
	}

	private JSONArray getLocationBasedTagTypes(JSONArray locationFreqCount) {
		JSONArray locationBasedTagTypes = new JSONArray();
		JSONArray tagTypes = null;
		JSONObject locationDetail = null;
		JSONObject json = null;
		String locationname = null;
		Iterator<JSONObject> iterJsonArray = locationFreqCount.iterator();
		while (iterJsonArray.hasNext()) {
			locationDetail = iterJsonArray.next();
			if (locationDetail.containsKey("tagTypes")) {
				json = new JSONObject();
				tagTypes = locationDetail.getJSONArray("tagTypes");
				locationname = locationDetail.getString("locationname");
				json.put("locationname", locationname);
				json.put("tagTypes", tagTypes);
				locationBasedTagTypes.add(json);
			}
		}
		return locationBasedTagTypes;
	}
	
	@RequestMapping(value = "/deviceInfo", method = RequestMethod.GET)
	public JSONArray deviceInfo(@RequestParam(value = "cid", required = false) String cid,
								@RequestParam(value = "sid", required = false) String sid,
								@RequestParam(value = "spid", required = false) String spid,
								HttpServletRequest request,	HttpServletResponse response) {
		
		JSONArray deviceInfo 		  = new JSONArray();
		JSONObject deviceObj 		  = null;
		String duration 	 		  = "30m";
		List<BeaconDevice> deviceList = null;
		DateFormat parse	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		try{
			
			if (spid != null && !spid.isEmpty()) {
				deviceList = beacondeviceservice.findBySpid(spid);
			} else if (sid != null && !sid.isEmpty()) {
				deviceList = beacondeviceservice.findBySid(sid);
			} else {
				deviceList = beacondeviceservice.findByCid(cid);
			}
			
			List<Map<String, Object>>  uptimeInfo = null;
			Customer cx = customerservice.findById(cid);
			TimeZone totimezone = customerUtils.FetchTimeZone(cx.getTimezone());
			format.setTimeZone(totimezone);
			if (deviceList != null && deviceList.size() > 0) {
				
				for(BeaconDevice bd : deviceList){
					
					String uid 			= bd.getUid();
					String version 		= bd.getVersion();
					String build 		= bd.getBuild();
					String deviceUptime = "0 Days : 0 Hours : 0 Minutes";
					String appUptime	= "0 Days : 0 Hours : 0 Minutes";
					String locationname = bd.getAlias().toUpperCase();
					String state 		= bd.getState().toUpperCase();
					String fileStatus   = bd.getDevCrashDumpUploadStatus() == null ? "NA" : bd.getDevCrashDumpUploadStatus();
					String fileName   	= bd.getDevCrashdumpFileName() == null ? "NA" : bd.getDevCrashdumpFileName();
					int tagcount 		= 0;
					String lastSeen 	= "NA";
					
					List<Beacon> beaconlist = beaconservice.getSavedBeaconByReciverinfoAndStatus(uid, "checkedout");
					
					/*String spid    = bd.getSpid() == null ? "NA" : bd.getSpid();
					Portion p 	     = portionservice.findById(spid);
					String floorname = p.getUid().toUpperCase();*/
	
					if (version == null || version.isEmpty()) {
						version = "unknown";
					}
					if (build == null || build.isEmpty()) {
						build = "unknown";
					}
					
					uid = uid.toUpperCase();
			    	
					String fsql = " index=" + indexname + ",sort=timestamp desc,size=1,query=opcode:\"system_stats\" "
							+" AND timestamp:>now-"+duration+" AND uid:\"" + uid +"\" |value(uid,uid,NA);"
							+" value(cpu_percentage,cpu,NA);value(ram_percentage,ram_value,NA);" 
							+" value(cpu_days,cpuDays,NA);value(cpu_hours,cpuHours,NA);value(cpu_minutes,cpuMinutes,NA);" 
							+" value(app_days,appDays,NA);value(app_hours,appHours,NA);value(app_minutes,appMinutes,NA);" 
							+" value(uplink,bletx,NA);value(downlink,blerx,NA);value(timestamp,time,NA);|table ";
			
					
					uptimeInfo = fsqlRestController.query(fsql);

					if (bd.getLastseen() != null) {
						lastSeen = bd.getLastseen();
					}

					if (uptimeInfo != null && uptimeInfo.size()>0){
						Map<String, Object> info = uptimeInfo.get(0);
						
						deviceUptime = info.getOrDefault("cpuDays", 0) +" Days: "
									 + info.getOrDefault("cpuHours", 0) +" Hours: "
									 + info.getOrDefault("cpuMinutes", 0) +" Minutes";
						
						appUptime    = info.getOrDefault("appDays", 0) +" Days: "
								 	 + info.getOrDefault("appHours", 0) +" Hours: "
								 	 + info.getOrDefault("appMinutes", 0) +" Minutes";
					}
					
					tagcount = beaconlist.size();
					
					deviceObj = new JSONObject();

					String crashState = "enabled";
					if (fileStatus.equals("NA") || fileStatus.isEmpty() || !fileStatus.equals("0")) {
						crashState = "disabled";
					}
					deviceObj.put("uid", 			uid);
					deviceObj.put("locationname", 	locationname);
					//deviceObj.put("floorname", 	floorname);
					deviceObj.put("version", 		version);
					deviceObj.put("build", 			build);
					deviceObj.put("deviceUptime", 	deviceUptime);
					deviceObj.put("appUptime", 		appUptime);
					deviceObj.put("tagcount", 		tagcount);
					deviceObj.put("state", 			state);
					deviceObj.put("lastSeen", 		lastSeen);
					deviceObj.put("fileName", 	    fileName);
					deviceObj.put("filestatus", 	fileStatus);
					deviceObj.put("crashState", 	crashState);
					deviceInfo.add(deviceObj);
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return deviceInfo;
	}
	
	@RequestMapping(value = "/finder_logs_csv", method = RequestMethod.GET)
	public String gw_logscsv(
			@RequestParam(value="searchTerm",required=false) String uid,
			@RequestParam(value="duration",required=false) String days,
			@RequestParam(value = "cid", required = false) String cid,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		String csvFileName = "./uploads/alert.csv";
		

		try {
			
			LOG.info(" csv export  uid " +uid +" days " +days +" cid " +cid);
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				String result 		 = "DEVICES LOGS";
				String gatewayheader = "";

				gatewayheader = "Date & Time,Event Log Summary\n";
				
				if (days == null || days.isEmpty()) {
					days = "1d";
				}
				
				List<Map<String, Object>> finderlog = bleRestController.finderlog(days, cid,uid);

					if (finderlog != null && !finderlog.isEmpty()) {
						
						result = result.concat("\n");
						result = result.concat(gatewayheader);

						String devicesLogs = null;
						
						Iterator<Map<String, Object>> iter = finderlog.iterator();
						
						while (iter.hasNext()) {
							
							Map<String, Object> str = iter.next();

							String time 		= (String)str.get("time");
							String snapshot 	= (String)str.get("snapshot");
							
							snapshot = snapshot.replaceAll(",", " ");

							devicesLogs = time + "," + snapshot+"\n";
							result = result.concat(devicesLogs);
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
	
	private String calculateElapsedTime(String entryString, String exitString) {
		long diffInSeconds = 0;
		try{
			DateFormat parse	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date entry,exit;
			entry = parse.parse(entryString);
			exit = parse.parse(exitString);
			long duration = exit.getTime() - entry.getTime();
			diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.valueOf(diffInSeconds);
	}
}
