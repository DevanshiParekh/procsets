package com.semaifour.facesix.rest;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.semaifour.facesix.data.elasticsearch.device.NetworkDevice;
import com.semaifour.facesix.data.site.Portion;
import com.semaifour.facesix.data.site.PortionService;
import com.semaifour.facesix.data.site.Site;
import com.semaifour.facesix.data.site.SiteService;
import com.semaifour.facesix.domain.Message;
import com.semaifour.facesix.imageconverter.ImageConverter;
import com.semaifour.facesix.imageconverter.JpegToTiffConverter;
import com.semaifour.facesix.util.CustomerUtils;
import com.semaifour.facesix.util.SessionUtil;
import com.semaifour.facesix.web.WebController;
import net.sf.json.JSONObject;

/**
 * 
 * Rest Device Controller handles all rest calls
 * 
 * @author mjs
 *
 */
@RestController
@RequestMapping("/rest/site/portion")
public class SitePortionRestController extends WebController {
	
	static Logger LOG = LoggerFactory.getLogger(SitePortionRestController.class.getName());
	
	@Autowired
	ServletContext context; 
	
	@Autowired
	SiteService siteService;
	
	@Autowired
	PortionService portionService;
	
	@Autowired
	HttpServletResponse servresponse;
	
	HttpServletRequest  request;
	
	private ImageConverter converter;

	
	
    @RequestMapping(value = "conf", method = RequestMethod.GET)
    public  String confGet(@RequestParam("spid") String spid) {
    	Portion portion = portionService.findById(spid);
    	return portion.getNetworkConfigJson();
    }
    
    @RequestMapping(value = "conf", method = RequestMethod.POST)
    public  String confPost(@RequestParam(value="spid", required=true) String spid, 
    						@RequestBody String conf) {
    	
    	Portion portion = portionService.findById(spid);
    	portion.setNetworkConfigJson(conf);
    	portionService.save(portion);
    	//TODO: conf = getLatestStatus;
		return conf;
    }
    
    @RequestMapping(value="/reset",method = RequestMethod.DELETE)
    public String resetFloorPlan(@RequestParam(value="spid", required=true) String spid,
    						     HttpServletRequest request, HttpServletResponse response) {
    	
    	String ret = "Floor Plan Deleted Failed.";
    	if (SessionUtil.isAuthorized(request.getSession())) {
    		Portion portion = portionService.findById(spid);
    		if (portion != null) {
    			
    			String jniimg 	= portion.getJNIFilepath();
    			String floorimg = portion.getPlanFilepath();
    			
    			deleteFile(jniimg);
    			deleteFile(floorimg);
    			
    			portion.setModifiedOn(new Date());
    			portion.setImageJson("");
    			portion.setPlanFilepath("");
    			portion.setJNIFilepath("");
    			portion.setHeight(0);
    			portion.setWidth(0);
    			portion = portionService.save(portion);
				ret = "Floor Plan Deleted successfully.";
			}
		} else {
			ret = "Unauthorized User.";
		}

    	return ret;
    }
    
	public void deleteFile(String path) {
		try {

			File file = new File(path);

			if (file.exists()) {
				if (file.delete()) {
					LOG.info(file.getName() + " is deleted!");
				} else {
					LOG.info("Delete operation is failed.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
    @RequestMapping(value = "/floorplan/save",method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "file" ,		required = true) MultipartFile planFile,
			@RequestParam(value = "spid",  		required = false) String spid,
			@RequestParam(value = "sid",  		required = true) String sid,
			@RequestParam(value = "json",  		required = true) String json,
			@RequestParam(value = "title",  	required = true) String floorname,
			@RequestParam(value = "desc",		required = true) String description,
			HttpServletRequest request,	  	HttpServletResponse response){
		
		
		try {
			
			LOG.info("Floor plan  SId " + sid);
			LOG.info("Floor plan  json " + json.toString());
			LOG.info("Floor plan  spid " + spid);
			
			if (SessionUtil.isAuthorized(request.getSession())) {
				
				Site site = siteService.findById(sid);
				if (site != null) {
					LOG.info("Floor plan  Site name " + site.getUid());
					
					Portion portion =  null;
					
					if (spid != null && !spid.isEmpty()) {
						portion = portionService.findById(spid);
					} else {
						portion = new Portion();
						portion.setCreatedBy(SessionUtil.currentUser(request.getSession()));
						portion.setCreatedOn(new Date());
						portion.setCid(site.getCustomerId());
						portion.setSiteId(site.getId());
						portion.setStatus(CustomerUtils.ACTIVE());
					}
					portion.setUid(floorname);
					portion.setDescription(description);
					portion.setModifiedOn(new Date());
					portion.setImageJson(json.toString());
					portion = portionService.save(portion);
					
					if(!planFile.isEmpty() && planFile.getSize() > 1) {
						
						Path path = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), (portion.getId() + "_" + planFile.getOriginalFilename()));
						Files.createDirectories(path.getParent());
					
						LOG.info( "FileName " + planFile.getOriginalFilename());
						LOG.info( "GetName " + planFile.getName());
						LOG.info( "PlanFile " + planFile.toString());
						LOG.info( "Dest " + 	path.toString());
						LOG.info( "PathFile " + path.getFileName());
					
						String name 	= planFile.getOriginalFilename();
						String fileName = name.split("\\.")[0];
						
						Path jnipath = Paths.get(_CCC.properties.getProperty("facesix.fileio.root", "./_FSUPLOADS_"), ("geo"+portion.getId() + "_" + fileName + ".tif"));
						
						converter = new JpegToTiffConverter(); 
						try {
							converter.convert(planFile.getInputStream(), jnipath.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						int width  = 0;
						int height = 0;
						
						BufferedImage bimg = ImageIO.read(planFile.getInputStream());
						width      		   = bimg.getWidth();
						height     		   = bimg.getHeight();
						
						LOG.info(" geo path  " + jnipath.toString());
						LOG.info(" jni file name  " + fileName + " height " +height + " width " +width);
						
						Files.copy(planFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
						portion.setPlanFilepath(path.toString());
						portion.setJNIFilepath(jnipath.toString());
						portion.setHeight(height);
						portion.setWidth(width);
						portion = portionService.save(portion);
					
					}
					
					spid = portion.getId();
					
					LOG.info("spid " +	   portion.getId());
					LOG.info("site id  " + portion.getSiteId());
					LOG.info("cid  " +     portion.getCid());
					
					return spid;
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "OK";
	}	

    
    @RequestMapping("/open")
	public String open(@RequestParam(value = "spid", required = true) String spid, HttpServletRequest request, HttpServletResponse response) {
		
    	LOG.info("portion edit spid " +	  spid);
    	
    	Portion portion =  portionService.findById(spid);
		
		if (portion !=null) {
			String imageJson = portion.getImageJson();
			return imageJson;
		}
		return null;
		
	}
   
}