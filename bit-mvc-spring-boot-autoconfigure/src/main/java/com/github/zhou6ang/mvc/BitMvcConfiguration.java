package com.github.zhou6ang.mvc;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.zhou6ang.mvc.servlet.BitDispatcherServlet;

@Configuration
public class BitMvcConfiguration {

	@Bean //("dispatcherServletRegistration")
	public ServletRegistrationBean registerBitEngineServlet(ServletRegistrationBean servletRegistrationBean){
		servletRegistrationBean.setEnabled(false);//disabled default dispatcherServlet that provided by SpringBoot.
		ServletRegistrationBean srb = new ServletRegistrationBean(new BitDispatcherServlet(),"/");
		srb.setLoadOnStartup(1);
		srb.setName("bitDispatcherServlet");
		return srb;
	}
	
}
