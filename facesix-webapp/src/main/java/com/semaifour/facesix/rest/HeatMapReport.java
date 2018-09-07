package com.semaifour.facesix.rest;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
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
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDeviceService;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.probe.oui.ProbeOUI;
import com.semaifour.facesix.probe.oui.ProbeOUIService;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.HeaderFooterPageEvent;
import com.semaifour.facesix.util.SessionUtil;


@RestController
@RequestMapping("/rest/heatMapReport")
public class HeatMapReport {
	static Logger LOG = LoggerFactory.getLogger(HeatMapReport.class.getName());

	static Font smallBold  = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	static Font catFont    = new Font(Font.FontFamily.HELVETICA,   16, Font.BOLD);
	static Font redFont    = new Font(Font.FontFamily.HELVETICA,   10, Font.NORMAL);
	static Font subFont    = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	static Font headerFont = new Font(Font.FontFamily.HELVETICA,   12, Font.BOLD);
	
	private static final Integer GENERIC_COLUMN_COUNT 	   = 8;
	DateFormat format 			   = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	NetworkDeviceService networkDeviceService;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	QubercommScannerRestController qubercommScannerRestController;
	
	@Autowired
	PortionService portionService;
	
	@Autowired
	SiteService siteService;

	@Autowired
	ProbeOUIService probeOUIService;
	
	TimeZone timezone     		   = null;
	
	@RequestMapping("/pdf")
	public String heatmapReport(HttpServletRequest request, HttpServletResponse response) {

		FileOutputStream os 				= null;
		PdfWriter writer 					= null;
		String customerName 				= null;
		FileInputStream fileInputStream 	= null;
		OutputStream responseOutputStream 	= null;
		Customer customer					= null;
		String title						= null;
		
		if (SessionUtil.isAuthorized(request.getSession())) {
			try {
				String pdfFileName  = "./uploads/heatmap.pdf";
				String logoFileName = "./uploads/logo-home.png";
				Document document = new Document(PageSize.A4, 36, 36, 90, 55);
				
				String cid = request.getParameter("cid");
				String mac = request.getParameter("mac");

				if (cid == null || cid.equals("undefined")) {
					cid = SessionUtil.getCurrentCustomer(request.getSession());
					if (cid == null) {
						return null;
					}
				}
				
				if (mac == null || mac.isEmpty()) {
					title = "Device Based";
				} else {
					title = "Floor Based";
				}
				
				String currentuser	 = SessionUtil.currentUser(request.getSession());
				UserAccount cur_user = userAccountService.findOneByEmail(currentuser);
				String userName		 = cur_user.getFname() + " " + cur_user.getLname();

				customer 	 = customerService.findById(cid);
				timezone = customerUtils.FetchTimeZone(customer.getTimezone());// cx.getTimezone()
				format.setTimeZone(timezone);
				logoFileName = customer.getLogofile() == null ? logoFileName : customer.getLogofile();
				customerName = customer.getCustomerName();

				Path path = Paths.get(logoFileName);
				
				if (!Files.exists(path)) {
					logoFileName = "./uploads/logo-home.png";
				}
				
				File file = new File(pdfFileName);
				os = new FileOutputStream(file);
				writer  = PdfWriter.getInstance(document, os);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(customerName, userName, logoFileName, format.format(new Date()));
				writer.setPageEvent(event);
				document.open();
				
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
		
		return null;
	}
	
	private void addContent(Document document, PdfWriter writer, String title, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			
			Paragraph subChapter = new Paragraph(title, subFont);

			addEmptyLine(subChapter, 1);

			createTable(subChapter, document, writer, request, response);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTable(Paragraph subChapter, Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) {
		try{
			
			Paragraph content	 = new Paragraph();
			PdfPTable table 	 = null;
			int col_num 		 = GENERIC_COLUMN_COUNT;
			
			table = new PdfPTable(col_num);
			table.setWidthPercentage(100);
			
			PdfPCell c1 = new PdfPCell(new Phrase("CLIENT MAC",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setColspan(2);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("ASSOCIATED",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("OS TYPE",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("VENUE NAME",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("FLOOR NAME",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("LOCATION",headerFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setColspan(2);
			table.addCell(c1);
			
			table.setHeaderRows(1);
			
			JSONObject processedDetail 				= heatmapDetailsProcessing(writer,request,response);
			JSONArray report 						= (JSONArray) processedDetail.get("report");
			JSONObject osTypeJson 					=  new JSONObject();
			
			DefaultPieDataset pieChartDataSet 		= new DefaultPieDataset();
			Image pieChartImage 					= null;
			
			
			if (report != null && !report.isEmpty()) {
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> i = report.iterator();
				while (i.hasNext()) {
					
					JSONObject rep		        = i.next();
					JSONArray probe_req_stats  = (JSONArray) rep.get("probe_req_stats");
					String floorname 			= "NA";
					String venuename 			= "NA";
					String associated			= null;
					String sid 					= (String) rep.get("sid");
					String spid 				= (String) rep.get("spid");
					
					Site site = siteService.findById(sid);
					Portion portion = portionService.findById(spid);
					
					
					if(site != null){
						venuename = site.getUid();
						venuename = venuename.toUpperCase();
					}
					
					if(portion != null){
						floorname = portion.getUid();
						floorname = floorname.toUpperCase();
					}
					
					if (probe_req_stats != null && !probe_req_stats.isEmpty()) {
						Iterator<JSONObject> iter = probe_req_stats.iterator();
						while(iter.hasNext()){
							String clientMac   = null;
							String devMac	   = null;
							String osType 	   = "UNKNOWN";
							JSONObject jsonObj = iter.next();
							clientMac 		   = jsonObj.get("mac_address").toString();
							if(jsonObj.containsKey("associated")){
								associated 	   = jsonObj.get("associated").toString().toUpperCase();
							}
							devMac	  		   = jsonObj.get("node_mac").toString().toUpperCase();
							
							if(associated == null){
								associated = "FALSE";
							}
							
							String ouimac = clientMac;
							ouimac 		  = ouimac.trim().substring(0,8).trim().toUpperCase();
							ProbeOUI oui  = probeOUIService.findOneByUid(ouimac);
							
							if(oui != null){
								osType = getOsType(oui.getVendorName());
							}
							
							if(osTypeJson.containsKey(osType)){
								int count = Integer.parseInt(osTypeJson.get(osType).toString());
								osTypeJson.put(osType,count+1);
							}else{
								osTypeJson.put(osType,1);
							}
							
							clientMac = clientMac.toUpperCase();
							
							c1 = new PdfPCell(new Phrase(clientMac,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							c1.setColspan(2);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(associated,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(osType,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(venuename,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(floorname,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							table.addCell(c1);
							
							c1 = new PdfPCell(new Phrase(devMac,redFont));
							c1.setHorizontalAlignment(Element.ALIGN_CENTER);
							c1.setColspan(2);
							table.addCell(c1);
						}
					}
				}
				if (table.size() > 1) {
					content.add(table);
					subChapter.add(content);
					document.add(subChapter);
				} else {
					Paragraph Para = new Paragraph();
					Para = addNoDataToPDF(Para);
					document.add(Para);
				}

				Paragraph pieChart = null;

				Iterator iterator = osTypeJson.keySet().iterator();
			
			while (iterator.hasNext()) {
				String type = (String) iterator.next();
				int typecount = Integer.parseInt(osTypeJson.get(type).toString());
				pieChartDataSet.setValue(type + "(" + typecount + ")", typecount);
				}

				pieChartImage = getPieChartImage(writer, pieChartDataSet, request, response);
				if (pieChartImage != null) {
					pieChart = new Paragraph();
					addEmptyLine(pieChart, 1);
					pieChart.add(pieChartImage);
					pieChart.setAlignment(Element.ALIGN_LEFT);
					Paragraph para = new Paragraph();
					addEmptyLine(para, 3);
					document.add(para);
					document.add(pieChart);
					
				}
			}else{
				Paragraph Para = new Paragraph();
				Para = addNoDataToPDF(Para);
				document.add(Para);
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
	
	private String getOsType(String vendorName) {
		String osType = "others";
		vendorName = vendorName.toLowerCase();
		if(vendorName != null && !vendorName.isEmpty()){

			if (vendorName.contains("apple")) {
				osType = "mac";
			}  else if (vendorName.contains("lenovo")
				      || vendorName.contains("asustek") 
				      || vendorName.contains("oppo")
				      || vendorName.contains("vivo")
				      || vendorName.contains("lgelectr")
				      || vendorName.contains("sonymobi")
				      || vendorName.contains("motorola")
				      || vendorName.contains("google")
				      || vendorName.contains("xiaomi")
				      || vendorName.contains("oneplus")
				      || vendorName.contains("samsung")
				      || vendorName.contains("htc")
				      || vendorName.contains("gioneeco")
				      || vendorName.contains("zte")
				      || vendorName.contains("huawei")
				      || vendorName.contains("chiunmai")) {
				osType = "android";
			} else if (  vendorName.contains("bose")
					   ||vendorName.contains("jbl")) {
				osType = "speaker";
			} else if (   vendorName.contains("canon")
					   || vendorName.contains("roku")
					   || vendorName.contains("nintendo")
					   || vendorName.contains("hp")
					   || vendorName.contains("hewlett")) {
				osType = "printer";
			}else if (vendorName.contains("microsof")) {
				osType = "windows";
			} 
		}
		return osType.toUpperCase();
	}

	private Image getPieChartImage(PdfWriter writer,
			DefaultPieDataset pieChartDataSet, HttpServletRequest request,
			HttpServletResponse response) {
		
		java.awt.Font title_font = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14);
		
		try {
			if (pieChartDataSet.getItemCount() == 0) {
				return null;
			}
			Image pieChartImage = null;
			int height = 250;
			int width = 525;
			JFreeChart pieChart = null;
			String title = " TYPES OF OS PRESENT";

			pieChart = ChartFactory.createPieChart(title, pieChartDataSet, true, true, false);
			pieChart.setTitle(new org.jfree.chart.title.TextTitle(title, title_font));
			pieChart.setBackgroundPaint(Color.lightGray);
			PiePlot pieChartPlot = (PiePlot) pieChart.getPlot();
			pieChartPlot.setOutlinePaint(null);
			pieChartPlot.setBackgroundPaint(Color.lightGray);
			pieChartPlot.setLabelGenerator(null);
			pieChartPlot.setShadowXOffset(0);
			pieChartPlot.setShadowYOffset(0);

			BufferedImage bufferedImage = pieChart.createBufferedImage(width, height);
			pieChartImage = Image.getInstance(writer, bufferedImage, 1.0f);
			
			return pieChartImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private JSONObject heatmapDetailsProcessing(PdfWriter writer, 
					   HttpServletRequest request,HttpServletResponse response) {
		JSONObject report = new JSONObject();
		JSONArray reportArray = new JSONArray();
		
		try {
				String cid = request.getParameter("cid");
				String mac = request.getParameter("mac");
				String sid = request.getParameter("sid");
				String spid = request.getParameter("spid");
	
				List<NetworkDevice> networkDeviceList = null;
				if (mac != null && !mac.isEmpty()) {
					networkDeviceList = networkDeviceService.findByUuid(mac.replaceAll("[^a-zA-Z0-9]", ""));
				} else if (spid != null && !spid.isEmpty()) {
					networkDeviceList = networkDeviceService.findBySpid(spid);
				} else if (sid != null && !sid.isEmpty()) {
					networkDeviceList = networkDeviceService.findBySid(sid);
				} else {
					networkDeviceList = networkDeviceService.findByCid(cid);
				}
	
				if (networkDeviceList != null && networkDeviceList.size() > 0) {
					for (NetworkDevice nd : networkDeviceList) {
						String uid 		  = nd.getUid().toLowerCase();
						String siteId 	  = nd.getSid()  == null ? "NA" : nd.getSid();
						String portionId  = nd.getSpid() == null ? "NA" : nd.getSpid();
						String location   = nd.getAlias()== null ? "NA" : nd.getAlias();
						JSONObject result = qubercommScannerRestController.probe_req_stats(uid,"auto",null, cid, request);
						if(result != null && !result.isEmpty()){
							result.put("sid", siteId);
							result.put("spid", portionId);
							result.put("location", location);
							reportArray.add(result);
						}
					}
				}
			report.put("report", reportArray);

			return report;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
