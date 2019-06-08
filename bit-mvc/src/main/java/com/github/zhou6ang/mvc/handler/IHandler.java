package com.github.zhou6ang.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHandler {
	
	public Object process(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
