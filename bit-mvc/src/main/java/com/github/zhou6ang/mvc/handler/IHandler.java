package com.github.zhou6ang.mvc.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHandler {
	
	public Object process(HttpServletRequest request, HttpServletResponse response) throws Exception;
	public Map<String,String> getReqHeaders();
	public Map<String,String> getResHeaders();
	public String getHttpRequestMethod();
}
