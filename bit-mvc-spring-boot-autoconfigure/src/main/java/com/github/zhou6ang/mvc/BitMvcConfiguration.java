package com.github.zhou6ang.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.zhou6ang.mvc.servlet.BitDispatcherServlet;
import com.github.zhou6ang.mvc.util.Constants;


@Configuration
public class BitMvcConfiguration {
	
	@Autowired
	private BitMvcProperties properties;

	@Bean //("dispatcherServletRegistration")
	public ServletRegistrationBean registerBitEngineServlet(ServletRegistrationBean servletRegistrationBean){
		servletRegistrationBean.setEnabled(false);//disabled default dispatcherServlet that provided by SpringBoot.
		
		if(System.getProperty(Constants.VIEW_PREFIX) == null && properties.getViewPrefix() != null && !properties.getViewPrefix().isEmpty()) {
			System.setProperty(Constants.VIEW_PREFIX, properties.getViewPrefix());
		}
		ServletRegistrationBean srb = new ServletRegistrationBean(new BitDispatcherServlet(),"/");
		srb.setLoadOnStartup(1);
		srb.setName("bitDispatcherServlet");
		return srb;
	}
	
}
