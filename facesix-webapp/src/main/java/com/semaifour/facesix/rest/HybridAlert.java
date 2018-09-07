package com.semaifour.facesix.rest;

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
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
import com.semaifour.facesix.beacon.rest.BLENetworkDeviceRestController;
import com.semaifour.facesix.beacon.rest.FinderReport;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.HeaderFooterPageEvent;
import com.semaifour.facesix.util.SessionUtil;

@RestController
@RequestMapping("/rest/hybridAlert")

public class HybridAlert {
	static Logger LOG = LoggerFactory.getLogger(FinderReport.class.getName());
	static Font smallBold  = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	static Font catFont    = new Font(Font.FontFamily.HELVETICA,   16, Font.BOLD);
	static Font redFont    = new Font(Font.FontFamily.HELVETICA,   10, Font.NORMAL);
	static Font subFont    = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	static Font headerFont = new Font(Font.FontFamily.HELVETICA,   12, Font.BOLD);

	DateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	TimeZone timezone = null;

	@Autowired
	CustomerService customerservice;
	
	@Autowired
	CustomerUtils customerUtils;
	
	@Autowired
	BLENetworkDeviceRestController bleRestController;
	
	 @Autowired
	 NetworkDeviceRestController networkDeviceRestController;

	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	private JavaMailSender javaMailSender;

	 @RequestMapping(value = "/alertpdf", method = RequestMethod.GET)
	public String alertPdf(@RequestParam(value = "cid", required = false) String cid, HttpServletRequest request,
			HttpServletResponse response) {

		// String pdfFileName = "TrilaterationReport.pdf";
		// String logoFileName = "C:/Users/sudavasu/Desktop/Testing/pic.png";

		String pdfFileName = "./uploads/qubercloud.pdf";
		String logoFileName = "./uploads/logo-home.png";

		if (SessionUtil.isAuthorized(request.getSession())) {

			Document document = new Document(PageSize.A4, 36, 36, 90, 55);
			try {
				if (cid == null) {
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
				addContent(document, cid, cx_name);
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

	private void addContent(Document document, String cid, String customerName) {
		try {
			Paragraph subCatPart = new Paragraph();
			// add a table
			createTable(subCatPart, document, cid, customerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTable(Paragraph subCatPart, Document document, String cid, String customerName)
			throws DocumentException {

		PdfPTable tagTable = new PdfPTable(10);
		tagTable.setWidthPercentage(100);

		PdfPTable batteryTable = new PdfPTable(10);
		batteryTable.setWidthPercentage(100);

		PdfPTable beaconDeviceTable = new PdfPTable(7);
		beaconDeviceTable.setWidthPercentage(100);
		
		PdfPTable deviceTable = new PdfPTable(5);
		deviceTable.setWidthPercentage(100);

		PdfPCell c1 = new PdfPCell(new Phrase("ID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Minor",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Major",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Tag Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("AssignedTo",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("State",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		tagTable.addCell(c1);

		tagTable.setHeaderRows(1);

		// battery table

		c1 = new PdfPCell(new Phrase("ID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Minor",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Major",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Tag Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("AssignedTo",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Battery Level",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		batteryTable.addCell(c1);

		batteryTable.setHeaderRows(1);

		// beaconDevice table

		c1 = new PdfPCell(new Phrase("UID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		beaconDeviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Type",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		beaconDeviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		beaconDeviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Location",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		beaconDeviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Status",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		beaconDeviceTable.addCell(c1);

		beaconDeviceTable.setHeaderRows(1);
		
		// gateway device table
		
		c1 = new PdfPCell(new Phrase("UID",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setColspan(2);
		deviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Alias",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		deviceTable.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Floor Name",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		deviceTable.addCell(c1);

		c1 = new PdfPCell(new Phrase("Status",headerFont));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		deviceTable.addCell(c1);

		deviceTable.setHeaderRows(1);

		try {
			Boolean generatepdf = true;
			org.json.simple.JSONObject tagalerts		  = bleRestController.inactiveTags(cid,generatepdf);
			org.json.simple.JSONObject batteryalerts 	  = bleRestController.beaconBatteryAlert(cid, "40",generatepdf);
			org.json.simple.JSONObject beaconDevicealerts = bleRestController.beaconDeviceAlert(cid,generatepdf);
			org.json.simple.JSONObject devicealert 		  = networkDeviceRestController.alert(cid,generatepdf);

			Paragraph emptyLines = new Paragraph();
			addEmptyLine(emptyLines, 3);
			
			if (tagalerts != null && !tagalerts.isEmpty()) {

				org.json.simple.JSONArray array = (org.json.simple.JSONArray) tagalerts.get("inactivetags");
				Iterator<org.json.simple.JSONObject> i = array.iterator();

				while (i.hasNext()) {

					org.json.simple.JSONObject rep = i.next();

					String macaddr = (String) rep.get("macaddr");
					int minor = Integer.parseInt(rep.get("minor").toString());
					int major = Integer.parseInt(rep.get("major").toString());
					String assignedTo = (String) rep.get("assignedTo");
					String tagtype = (String) rep.get("tagtype");
					String state = (String) rep.get("state");
					String floorname = (String) rep.get("floorname");
					String location = (String) rep.get("alias");

					// LOG.info( "====INACTIVE TAG ALERTS=========");
					// LOG.info( " minor " +minor + " major " +major);

					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(String.valueOf(minor),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(String.valueOf(major),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(tagtype,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(assignedTo,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(floorname,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(location,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(state,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					tagTable.addCell(c1);

				}
				subCatPart = new Paragraph("Tag Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(tagTable);
				document.add(subCatPart);
				document.add(emptyLines);
			}

			if (batteryalerts != null && !batteryalerts.isEmpty()) {

				org.json.simple.JSONArray array = (org.json.simple.JSONArray) batteryalerts.get("beaconbattery");
				Iterator<org.json.simple.JSONObject> i = array.iterator();

				while (i.hasNext()) {

					org.json.simple.JSONObject rep = i.next();

					String macaddr = (String) rep.get("macaddr");
					int minor = Integer.parseInt(rep.get("minor").toString());
					int major = Integer.parseInt(rep.get("major").toString());
					String assignedTo = (String) rep.get("assignedTo");
					String tagtype = (String) rep.get("tagtype");
					String batterylevel = (String) rep.get("batterylevel");
					String location = (String) rep.get("alias");
					String floorname = (String) rep.get("floorname");

					// LOG.info( "====BATTERY ALERTS=========");

					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(String.valueOf(minor),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(String.valueOf(major),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(tagtype,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(assignedTo,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(floorname,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(location,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(batterylevel,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					batteryTable.addCell(c1);

				}
				subCatPart = new Paragraph("Battery Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(batteryTable);
				document.add(subCatPart);
				document.add(emptyLines);
			}

			// beaconDevice alert

			if (beaconDevicealerts != null && !beaconDevicealerts.isEmpty()) {

				org.json.simple.JSONArray array = (org.json.simple.JSONArray) beaconDevicealerts.get("beacondevicealert");
				Iterator<org.json.simple.JSONObject> i = array.iterator();

				while (i.hasNext()) {

					org.json.simple.JSONObject rep = i.next();

					String portionname = (String) rep.get("portionname");
					// String sitename = rep.get("sitename").toString();
					String uid = (String) rep.get("uid");
					String type = (String) rep.get("type");
					String status = (String) rep.get("status");
					String alias = (String) rep.get("alias");

					// LOG.info( "====DEVICE ALERTS=========");

					c1 = new PdfPCell(new Phrase(uid,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					beaconDeviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(String.valueOf(type),redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					beaconDeviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(portionname,redFont));
					c1.setColspan(2);
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					beaconDeviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(alias,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					beaconDeviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(status,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					beaconDeviceTable.addCell(c1);

				}
				subCatPart = new Paragraph("Beacon Device Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(beaconDeviceTable);
				document.add(subCatPart);
				document.add(emptyLines);
			}
			
			if (devicealert != null && !devicealert.isEmpty()) {

				JSONArray array = (JSONArray) devicealert.get("inactive_list");
				Iterator<JSONObject> iterator = array.iterator();

				while (iterator.hasNext()) {
					JSONObject rep = iterator.next();

					String macaddr = (String) rep.get("macaddr");
					String alias = (String) rep.get("alias");
					String floor = (String) rep.get("portionname");
					String status = (String) rep.get("state");

					c1 = new PdfPCell(new Phrase(macaddr,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					c1.setColspan(2);
					deviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(alias,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					deviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(floor,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					deviceTable.addCell(c1);

					c1 = new PdfPCell(new Phrase(status,redFont));
					c1.setHorizontalAlignment(Element.ALIGN_CENTER);
					deviceTable.addCell(c1);
				}
				subCatPart = new Paragraph("Device Alerts ", subFont);
				addEmptyLine(subCatPart, 1);
				subCatPart.add(deviceTable);
				document.add(subCatPart);
			}
			
			if ((tagalerts == null || tagalerts.isEmpty()) 
			 && (devicealert == null || devicealert.isEmpty())
			 && (batteryalerts == null || batteryalerts.isEmpty())
			 && (beaconDevicealerts == null || beaconDevicealerts.isEmpty())) {
				subCatPart = addNoDataToPDF(subCatPart);
				document.add(subCatPart);
			}

		} catch (Exception e) {
			LOG.info("Hybrid Alert display error " + e);
			e.printStackTrace();
		}
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
				String beacondeviceheader  = "";
				String gatewayheader  = "";
				// tag alerts
				tagheader = "ID,Minor,Major,Tag Type,Assigned To,Floor Name,Location,State\n";

				// battery table
				batteryheader = "ID,Minor,Major,Tag Type,Assigned To,Floor Name,Location,Battery Level\n";

				// device table

				beacondeviceheader = "UID,Type,Floor Name,Location,Status\n";
				
				gatewayheader = "UID,Floor Name ,Alias,Status\n";

				try {
					org.json.simple.JSONObject tagalerts     	   = bleRestController.inactiveTags(cid,null);
					org.json.simple.JSONObject batteryalerts 	   = bleRestController.beaconBatteryAlert(cid,"40",null);
					org.json.simple.JSONObject beacondevicealerts  = bleRestController.beaconDeviceAlert(cid,null);
					org.json.simple.JSONObject deviceslist   	   = networkDeviceRestController.alert(cid,null);
					
					// beacon device alert
					if (beacondevicealerts != null && !beacondevicealerts.isEmpty()) {
						result                                  = result.concat("BEACON DEVICE ALERT");
						result 					                = result.concat("\n");
						result 									= result.concat(beacondeviceheader);
						org.json.simple.JSONArray array 		= (org.json.simple.JSONArray) beacondevicealerts.get("beacondevicealert");
						Iterator<org.json.simple.JSONObject> i 	= array.iterator();
						String devicealert 						= "";
						
						while (i.hasNext()) {

							org.json.simple.JSONObject rep 	= i.next();

							String portionname 				= (String) rep.get("portionname");
							String uid 						= (String) rep.get("uid");
							String type 					= (String) rep.get("type");
							String status 					= (String) rep.get("status");
							String alias 					= (String) rep.get("alias");
							
							devicealert 					= uid + "," + type + "," + portionname
															  + "," + alias + "," + status + "\n";
							result 							= result.concat(devicealert);
						}
						result 								= result.concat("\n\n");
					}
					
					// gateway device alert
					if (deviceslist != null && !deviceslist.isEmpty()) {

						
						result = result.concat("DEVICES ALERT");
						result = result.concat("\n");
						result = result.concat(gatewayheader);

						JSONArray array 		= (JSONArray) deviceslist.get("inactive_list");
						Iterator<JSONObject> i  = array.iterator();

						String inactivedevices = null;

						while (i.hasNext()) {

							JSONObject rep = i.next();

							String macaddr 	= (String) rep.get("macaddr");
							String alias 	= (String) rep.get("alias");
							String floor 	= (String) rep.get("portionname");
							String status 	= (String) rep.get("state");

							inactivedevices = macaddr + "," + floor + "," + alias + "," + status + "\n";

							result = result.concat(inactivedevices);
						}
						result = result.concat("\n\n");
					}
					
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
							
							inactivetagalerts 	= macaddr + "," + minor + "," + major + "," + tagtype + ","
												  + assignedTo + "," + floorname + "," +location+","+ state + "\n";
							
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


				} catch (Exception e) {
					LOG.info("Hybrid alert  csv file format download error " + e);
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
	
	public void emailTrigger(String uid) {

		//String pdfFileName  = "/home/qubercomm/Desktop/pdf/pdf-sample.pdf";
		//String logoFileName = "/home/qubercomm/Desktop/pdf/logo.png";
		
		String pdfFileName  = "./uploads/qubercloud.pdf";
		String logoFileName = "./uploads/logo-home.png";

		UserAccount users	= userAccountService.findOneByUid(uid);
		try{		
			if (users != null && users.isMailalert() != null && users.isMailalert().equals("true")) {
					
					String cid 		  = users.getCustomerId();
					String email      = users.getEmail();
					
					org.json.simple.JSONObject tagalerts     	   = bleRestController.inactiveTags(cid,null);
					org.json.simple.JSONObject batteryalerts 	   = bleRestController.beaconBatteryAlert(cid,"40",null);
					org.json.simple.JSONObject beacondevicealerts  = bleRestController.beaconDeviceAlert(cid,null);
					org.json.simple.JSONObject deviceslist   	   = networkDeviceRestController.alert(cid,null);
					
					org.json.simple.JSONArray inactivetags   	   = (org.json.simple.JSONArray)tagalerts.get("inactivetags");
					org.json.simple.JSONArray lowbattery           = (org.json.simple.JSONArray)batteryalerts.get("beaconbattery");
					org.json.simple.JSONArray inactiveBeaconDevice = (org.json.simple.JSONArray)beacondevicealerts.get("beacondevicealert");
					org.json.simple.JSONArray inactivedevice 	   = (org.json.simple.JSONArray)deviceslist.get("inactive_list");
					
					org.json.simple.JSONObject inactivetag = (org.json.simple.JSONObject) inactivetags.get(0);
					String inactivemac = inactivetag.get("macaddr").toString();
					
					org.json.simple.JSONObject lowbatterytag = (org.json.simple.JSONObject) lowbattery.get(0);
					String lowbatterymac = lowbatterytag.get("macaddr").toString();
					
					org.json.simple.JSONObject inactiveBeaconDev = (org.json.simple.JSONObject) inactiveBeaconDevice.get(0);
					String inactivebeacondevicemac = inactiveBeaconDev.get("uid").toString();
					
					org.json.simple.JSONObject inactivedev = (org.json.simple.JSONObject) inactivedevice.get(0);
					String inactivedevicemac = inactivedev.get("macaddr").toString();
					
					if (inactivemac.equals("-") && lowbatterymac.equals("-") && inactivebeacondevicemac.equals("-") && inactivedevicemac.equals("-")) {
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
				
						customerUtils.customizeSupportEmail(cid, email, "Alert Notification", body, file);

					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
					LOG.info("Email Alerts disabled user email " +uid);
				}
		} catch (Exception e) {
			e.printStackTrace();
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
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
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
}
