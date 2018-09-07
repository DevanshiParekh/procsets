package com.semaifour.facesix.boot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.semaifour.facesix.jni.GeoServiceJniHandler;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * Spring boot app
 * 
 * @author mjs
 *
 */

@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = "com.semaifour.facesix")
public class Application extends SpringBootServletInitializer {
	
	static Logger LOG = LoggerFactory.getLogger(Application.class.getName());
	
	public static ApplicationContext context; 

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
    public static void main(String[] args) {
        context = SpringApplication.run(Application.class, args);
        
    	LOG.info("facesix configured as app: [{}], env: [{}], node: [{}], cluster:[{}]",
    			System.getProperty("fs.app", "default"),
    			System.getProperty("fs.env", "default"),
    			System.getProperty("fs.node", "default"),
    			System.getProperty("fs.cluster", "default"));

        LOG.info("facesix is running!");
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }
    
    @Value("${facesix.geojni.lib.path}")
    private String geojniLibPath;
    
	@Bean
	public GeoServiceJniHandler geoServiceJniHandler() {
		GeoServiceJniHandler Geohandler = null;
		Path path = Paths.get(geojniLibPath);
		if (Files.exists(path)) {
			try {
				
				LOG.info("******** JNI LIB PATH  ******" + geojniLibPath);
				System.load(geojniLibPath);
				LOG.info("************** JNI LIB LOADED SUCCESS********************");
				Geohandler = new GeoServiceJniHandler();
	
			} catch (Exception ex) {
				LOG.error("Error loading jni lib : " + ex.getLocalizedMessage(), ex);
	
			}			
		} else {
			LOG.info("******** NOT FOUND *** JNI LIB ******" + geojniLibPath);
		}

		return Geohandler;
	}
}