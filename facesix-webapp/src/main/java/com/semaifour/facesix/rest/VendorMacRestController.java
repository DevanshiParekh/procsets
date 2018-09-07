package com.semaifour.facesix.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.Date;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.semaifour.facesix.data.account.UserAccount;
import com.semaifour.facesix.data.account.UserAccountService;
import com.semaifour.facesix.probe.oui.ProbeOUI;
import com.semaifour.facesix.probe.oui.ProbeOUIService;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;

@RestController
@RequestMapping("/rest/oui")
public class VendorMacRestController extends WebController {

	static Logger LOG = LoggerFactory.getLogger(VendorMacRestController.class.getName());
	
	@Autowired
	UserAccountService userAccountService;
	
	@Autowired
	ProbeOUIService probeOUIService;
	
	private static final String DEFAULT_SOURCE_FILE = "./uploads/oui.log";
	private static final String DEFAULT_DEST_PATH = "vendor-macs-generated.log";

	private static final String DELIM = "(base 16)";
	private static final String EQUAL = "=";
	
	private void updateOUI(final String source, final String curr_user) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DEFAULT_DEST_PATH)));
		String tmp;

		while ((tmp = br.readLine()) != null) {
			if (!tmp.contains(DELIM))
				continue;

			StringTokenizer stk = new StringTokenizer(tmp.replace(DELIM, "="), EQUAL);
			String prefix = null;
			String vendor = null;

			if (stk.hasMoreTokens())
				prefix = stk.nextToken().trim();

			if (stk.hasMoreTokens())
				vendor = stk.nextToken().trim();

			String uid 		  = prefix.replaceAll("..(?!$)", "$0:");
			String vendorName = simplifyString(vendor);

			ProbeOUI oui = probeOUIService.findOneByUid(uid);
			if (oui == null) {
				oui = new ProbeOUI();
				oui.setUid(uid);
				oui.setCreatedOn(new Date());
				oui.setCreatedBy(curr_user);
			} else {
				String prevVendorName = oui.getVendorName();
				if (!vendorName.equalsIgnoreCase(prevVendorName)) {
					oui.setModifiedBy(curr_user);
					oui.setModifiedOn(new Date());
					oui.setVendorName(vendorName);
					probeOUIService.save(oui);
				} else {
					//LOG.info("Alredy Exits uid  " + uid);
					continue;
				}
			}
		}

		br.close();
		bw.close();

		LOG.info("Done!!!, File generated : " + DEFAULT_DEST_PATH);
	}

	private static String simplifyString(String original) {
		String normalizedString = Normalizer.normalize(original, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
				.replace(",", "").replace("&amp;", "&").replaceAll("[.]$", "");
		if (normalizedString.toLowerCase().contains("samsung"))
			return "Samsung";
		else if (normalizedString.toLowerCase().contains("bioMrieux"))
			return "bioMerieux";

		final String arr[] = normalizedString.split("\\s+");
		if (arr.length >= 2) {
			if (arr[1].toLowerCase().contains("inc") || arr[1].toLowerCase().contains("corp")
					|| arr[1].toLowerCase().contains("co.") || arr[1].toLowerCase().contains("ltd")
					|| arr[1].length() <= 2)
				return arr[0];
			else
				return arr[0] + " " + arr[1].replaceAll("[.]$", "");
		} else {
			return arr[0];
		}
	}

	private void downloadouitxt() throws IOException {
		
		LOG.info("Downloading oui file... from http://standards-oui.ieee.org/oui.txt");

		int size;

		URL url = new URL("http://standards-oui.ieee.org/oui.txt");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("HEAD");
		size = conn.getContentLength();

		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buff = new byte[2048];
		int read;
		int progress = -1;
		int total = 0;

		long begin = System.currentTimeMillis();

		while ((read = bis.read(buff)) != -1) {
			total += read;
			int cur = (total * 100) / size;

			if (cur != progress) {
				progress = cur;
				int elasped = (int) ((System.currentTimeMillis() - begin) / 1000);
				LOG.info(String.format("%7d bytes / %7d bytes (%3d percent), %3ds elasped.", total, size,
						progress, elasped));
			}
			baos.write(buff, 0, read);
		}

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(DEFAULT_SOURCE_FILE));
		bos.write(baos.toByteArray());
		bos.close();
		bis.close();
		baos.close();
		conn.disconnect();

		LOG.info("download done!!!, total elasped: " + ((System.currentTimeMillis() - begin) / 1000) + "s.");
	}

	@RequestMapping(value = "/save")
	public String updateOUI(@RequestParam("url") final String url, HttpServletRequest request) throws IOException {

		 String ret = "Unauthorized User";
		 
		if (SessionUtil.isAuthorized(request.getSession())) {
			
			ret = "Authorized User";
			
			String currentuser   = SessionUtil.currentUser(request.getSession());
			UserAccount user 	 = userAccountService.findOneByEmail(currentuser);
			
			LOG.info("Generate vendor mac file from RAW IEEE-oui files(http://standards-oui.ieee.org/oui.txt)");
			
			if (user != null) {
				String cur_role = user.getRole();
				if ("superadmin".equals(cur_role)) {
					if (url.equals("update")) {
						downloadouitxt();
						updateOUI(DEFAULT_SOURCE_FILE,currentuser);
					} 
				}
			} else {
				ret = "User Not Found";
			}
		}
		return ret;
	}
}