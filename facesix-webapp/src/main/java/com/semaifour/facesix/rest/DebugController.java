package com.semaifour.facesix.rest;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.semaifour.facesix.web.WebController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@RestController
@RequestMapping("/rest/debug")
public class DebugController  extends WebController {

	 @GetMapping("/level")
	 public @ResponseBody Level level() {
		 Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		 return root.getLevel();
	 }
	
	 @PostMapping("/level/{level}")
	 public @ResponseBody Level level(@PathVariable("level")String level) {
		 Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		 root.setLevel(Level.valueOf(level));
		 return root.getLevel();
	 }
	 
	 @PostMapping("/level/{path}/{level}")
	 public @ResponseBody Level level(@PathVariable("path")String path, @PathVariable("level")String level) {
		 Logger log = (Logger)LoggerFactory.getLogger(path);
		 if (log != null) {
			 log.setLevel(Level.valueOf(level));
			 return log.getLevel();
		 }
		 return null;
	 }
	 
}