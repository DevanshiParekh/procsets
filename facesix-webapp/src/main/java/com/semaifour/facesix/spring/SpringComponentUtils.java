package com.semaifour.facesix.spring;

import com.semaifour.facesix.boot.Application;

public class SpringComponentUtils {
	
	private static ApplicationProperties applicationProperties;
	
	public static ApplicationProperties getApplicationProperties() {
		if (applicationProperties == null) {
			applicationProperties = Application.context.getBean(ApplicationProperties.class);
		}
		return applicationProperties;
	}

	
	private static ApplicationMessages applicationMessages;
	
	public static ApplicationMessages getApplicationMessages() {
		if (applicationMessages == null) {
			applicationMessages = Application.context.getBean(ApplicationMessages.class);
		}
		return applicationMessages;
	}
}
