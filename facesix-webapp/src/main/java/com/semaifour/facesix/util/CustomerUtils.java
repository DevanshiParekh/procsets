package com.semaifour.facesix.util;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import com.semaifour.facesix.account.Customer;
import com.semaifour.facesix.account.CustomerService;
@Controller
public class CustomerUtils {
	
	@Autowired
	CustomerService customerService;
	
	@Value("${facesix.cloud.name}")
	private String cloudUrl;
	
	@Value("${facesix.cloud.security.enable}")
	private int cloudSecurity;
	
	@Value("${spring.mail.host}")
	private String quberHost;
	
	@Value("${spring.mail.port}")
	private String quberPort;
	
	@Value("${spring.mail.username}")
	private String quberUsername;
	
	@Value("${spring.mail.password}")
	private String quberPassword;
	
	@Autowired
	private Cryptor cryptor;
	
	static Logger LOG = LoggerFactory.getLogger(CustomerUtils.class.getName());
	
	public static long getRemainglicenceDays(Date expireDate) throws ParseException {
		long diff = expireDate.getTime() - new Date().getTime();
		long remainDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		//System.out.println("SimpleDateCalulation remainDays " + remainDays + "startDate " + startDate);
		return remainDays;
	}

	public static SimpleMailMessage constructResetTokenEmail(String emailId, String name, String message) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(emailId);
		email.setSubject("Qubercomm Notification");
		email.setText("Hi" + "," + name + " " + message);
		return email;
	}
	
	public static Date formatDate(Date date) throws ParseException {
		final String pattern = "dd/MM/yyyy";
		Date dateValue = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			if (null != date) {
				String strDate = sdf.format(date);
				dateValue = sdf.parse(strDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dateValue;
	}

	public static final String ACTIVE() {
		return "ACTIVE";
	}

	public static final String INACTIVE() {
		return "INACTIVE";
	}

	public static String getRemaingDaysHoursMinutesSeconds(Date startDate, Date endDate) throws ParseException {

		String ddhhmmss = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		startDate = sdf.parse(sdf.format(startDate));
		endDate = sdf.parse(sdf.format(endDate));
		long different = endDate.getTime() - startDate.getTime();

		//System.out.println("startDate : " + startDate);
		//System.out.println("endDate : " + endDate);
		//System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		ddhhmmss = elapsedDays + "-" + elapsedHours + "-" + elapsedMinutes + "-" + elapsedSeconds;
		//System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes,
		//		elapsedSeconds);
		return ddhhmmss;

	}

	public  TimeZone FetchTimeZone(String zone){
		TimeZone timezone = null;
		timezone = TimeZone.getTimeZone(zone);
		return timezone;
	}
	public boolean GeoFinder(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			if (customer.getSolution().equals("GeoFinder")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean Gateway(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			 if (customer.getSolution().equals("Gateway")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean Vpn(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			String vpn = customer.getVpn();
			 if (vpn != null && vpn.equals("true")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean GatewayFinder(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			 if (customer.getSolution().equals("GatewayFinder")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean GeoLocation(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			 if (customer.getSolution().equals("GeoLocation")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean entryexit(String customerId) {
		Customer customer = customerService.findById(customerId);
		if (customer != null) {
			 if (customer.getVenueType().equalsIgnoreCase("Patient-Tracker")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean Locatum(String cid) {
		Customer customer = customerService.findById(cid);
		if (customer != null) {
			if (customer.getVenueType().equalsIgnoreCase("Locatum")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean trilateration(String cid) {
	
		if (cid != null && !cid.isEmpty()) {
			Customer customer = customerService.findById(cid);
			if (customer != null) {
				 if (GeoFinder(cid) || GatewayFinder(cid)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public boolean Heatmap(String cid) {
		Customer customer = customerService.findById(cid);
		if (customer != null) {
			if (customer.getSolution().equals("Heatmap")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void logs(boolean logenabled,String classname, String msg){
		if (logenabled) {
			Logger LOG = LoggerFactory.getLogger(classname);
			LOG.info(msg);
		}
	}
	
	public static boolean isStringNotEmpty(String input){
        if(input == null || input.equals("null") ||  input.length() == 0 || input.isEmpty() ){
            return false;
        }
        return true;
    }
	
	public String cloudUrl() {

		String security = null;

		
		if (cloudSecurity == 1) {
			security = "https://";
		} else {
			security = "http://";
		}

		String url = security + cloudUrl;

		LOG.info("CLOUD URL " +url);
		
		return url;
	}
	
	
	public void customizeSupportEmail (String cid, String toEmail, String subject, String body, File attachment) {
		
		try {

			String fromEmail = quberUsername;
			String password  = quberPassword;
			String host 	 = quberHost;
			String port 	 = quberPort;
			String regards 	 = "QUBERCOMM TECHNOLOGIES.";

			Customer cx = null;
			if (cid != null && !cid.isEmpty()) {
				cx = customerService.findById(cid);
			}

			if (cx != null) {
				if (cx.getCustSupportEmailEnable() != null) {
					String supportEnable = cx.getCustSupportEmailEnable();
					if (supportEnable.equals("true")) {
						fromEmail 		= cx.getCustSupportEmailId();
						password 		= cx.getCustSupportPassword();
						host			= cx.getCustSupportHost();
						port 			= cx.getCustSupportPort();
						regards 		= cx.getCustomerName().toUpperCase();
					}
				}
			}

			if (body.contains("<br/>")) {
				body += "&nbsp;&nbsp;Regards,</b> <br/>"
					 + "&nbsp;&nbsp;" + regards + "</b> <br/>";
			} else {
				body += "\n\n Regards,\n " + regards;
			}
				
			Properties props = new Properties();
			
			props.put("mail.smtp.host", host); 
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			
			final String fromEmailId 	= fromEmail;
			final String emailPassword 	= password;
			
			Authenticator auth = new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmailId, emailPassword);
				}
			};
			
			Session session = Session.getInstance(props, auth);
			
			sendEmail(session, fromEmailId, toEmail, subject, body, attachment);

		} catch (Exception e) {
			LOG.error("while mail send error ");
			e.printStackTrace();
		}
	}
	
	public void sendEmail(Session session, String fromEmail, String toEmail, 
						  String subject, String body,File attachment) {

		try {

			MimeMessage msg = new MimeMessage(session);

			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			msg.setFrom(new InternetAddress(fromEmail));
			msg.setReplyTo(InternetAddress.parse(fromEmail, false));
			msg.setSubject(subject, "UTF-8");

			if (body.contains("<br/>")) { // HTML Table Format
				msg.setText(body, "UTF-8", "html");
			} else { // Plain Text Format
				msg.setText(body, "UTF-8");
			}

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

			if (attachment != null) {

				Multipart multipart = new MimeMultipart();
				BodyPart messageAttachmentBody = new MimeBodyPart();
				BodyPart messagePlainTextBodyPart = new MimeBodyPart();

				/*
				 * File Attachment
				 * 
				 */

				messageAttachmentBody = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment.getPath());
				messageAttachmentBody.setDataHandler(new DataHandler(source));
				messageAttachmentBody.setFileName(attachment.getName());
				multipart.addBodyPart(messageAttachmentBody);

				/*
				 * Plain Text
				 * 
				 */

				messagePlainTextBodyPart = new MimeBodyPart();
				messagePlainTextBodyPart.setText(body);
				multipart.addBodyPart(messagePlainTextBodyPart);

				msg.setContent(multipart);
			}

			LOG.info("Email Ready To send.....");
			Transport.send(msg);
			LOG.info("EMail Sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long calculateElapsedTime(Date entry, Date exit) {
		long duration = exit.getTime() - entry.getTime();
		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		return diffInSeconds;
	}
	
	private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		}
	};

	public String getCurrentTimeForZone(String cid) {
		String formatDate = null;
		try {
			if (cid != null) {
				Customer customer = customerService.findById(cid);
				if (customer != null) {
					TimeZone zone = FetchTimeZone(customer.getTimezone());
					df.get().setTimeZone(zone);
				}
			}
		} catch (Exception e) {
			formatDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date());
			LOG.error("while get getCurrentTimeForZone error " +e);
			e.printStackTrace();
		}

		formatDate = df.get().format(new Date());
		return formatDate;
	}
	
	  
	 public static String formatFileSize(long size) {
		 
	        String hrSize = "0.00 KB";

	       double b = size;
	       double k = size/1024.0;
	       double m = ((size/1024.0)/1024.0);
	      // double g = (((size/1024.0)/1024.0)/1024.0);
	      // double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

	        DecimalFormat dec = new DecimalFormat("0.00");
	        hrSize = dec.format(m).concat(" MB");
	        
			/*if (t > 1) {
				hrSize = dec.format(t).concat(" TB");
			} else if (g > 1) {
				hrSize = dec.format(g).concat(" GB");
			} else if (m > 1) {
				hrSize = dec.format(m).concat(" MB");
			} else if (k > 1) {
				hrSize = dec.format(k).concat(" KB");
			} else {
				hrSize = dec.format(b).concat(" Bytes");
			} */

	        return hrSize;
	    }
	 
	 
	 public static int minutes_to_seconds(String time,double buff_sec) {
			
		int in_minutes 			= Integer.parseInt(time);
		int in_seconds   		= (in_minutes * 60);
		double buffer_in_sec 	= (in_seconds * buff_sec);
		double fsql_in_sec 		= (in_seconds + buffer_in_sec);
		int fs_time 			= (int) Math.round(fsql_in_sec);

		return fs_time;
	}

	public static String secondsto_hours_minus_days(long seconds) {

		int day 	= (int) TimeUnit.SECONDS.toDays(seconds);
		long hours  = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

		String ssmmhhdd 	= String.valueOf(day) 	 +"d:" 
							+ String.valueOf(hours)  +"h:" 
							+ String.valueOf(minute) +"m:"
							+ String.valueOf(second) +"s";

		return ssmmhhdd;

	}
	
	 public static String bytesconverter(long size) {
		 
	        String hrSize = "0 KB";

	       double b = size;
	       double k = size/1024.0;
	       double m = ((size/1024.0)/1024.0);
	       double g = (((size/1024.0)/1024.0)/1024.0);
	       double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

	        DecimalFormat dec = new DecimalFormat("0.00");
	        hrSize = dec.format(m).concat(" MB");
	        
			if (t > 1) {
				hrSize = dec.format(t).concat(" TB");
			} else if (g > 1) {
				hrSize = dec.format(g).concat(" GB");
			} else if (m > 1) {
				hrSize = dec.format(m).concat(" MB");
			} else if (k > 1) {
				hrSize = dec.format(k).concat(" KB");
			} else {
				hrSize = dec.format(b).concat(" Bytes");
			} 

	        return hrSize;
	    }
	 
	public DateFormat set_date_format_hh_mm_ss(String cid) {
		DateFormat hhmmss = new SimpleDateFormat("HH:mm");
		if (cid != null) {
			Customer cx = customerService.findById(cid);
			if (cx != null) {
				TimeZone istTimeZone = FetchTimeZone(cx.getTimezone());
				hhmmss.setTimeZone(istTimeZone);
			}
		} else {
			TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");
			hhmmss.setTimeZone(istTimeZone);
		}
		return hhmmss;
	}

	public boolean isRetail(String cid) {
		boolean isTrue = false;
		Customer customer = customerService.findById(cid);
		if (customer != null) {
			isTrue = customer.getSolution().equalsIgnoreCase("Retail") ? true : false;
		}
		return isTrue;
	}
}
