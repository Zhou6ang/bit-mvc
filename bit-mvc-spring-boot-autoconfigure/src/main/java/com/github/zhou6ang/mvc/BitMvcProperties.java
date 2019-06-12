package com.github.zhou6ang.mvc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.github.zhou6ang.mvc.util.Constants;

@Validated
@ConfigurationProperties(prefix=Constants.BIT_MVC_PREFIX)
public class BitMvcProperties {
	
	private String viewPrefix;

	public String getViewPrefix() {
		return viewPrefix;
	}

	public void setViewPrefix(String viewPrefix) {
		this.viewPrefix = viewPrefix;
	}

}
