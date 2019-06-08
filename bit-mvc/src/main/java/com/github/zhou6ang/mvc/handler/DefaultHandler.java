package com.github.zhou6ang.mvc.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHandler implements IHandler{

	private Method method;
	private Object[] parameters;
	private Object returnValue;
	private Object controller;
	
	public DefaultHandler(Method method,Object controller) {
		this.method = method;
		this.controller = controller;
	}
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public Object getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
	
	@Override
	public Object process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Object> list = new ArrayList<>();
		for (Parameter para : method.getParameters()) {
			if (para.getType().isAssignableFrom(HttpServletRequest.class)) {
				list.add(request);
			}else if(para.getType().isAssignableFrom(HttpServletResponse.class)) {
				list.add((HttpServletResponse)response);
			}else {
				String name = para.getName();
				String value = request.getParameter(name);
				list.add(value);
			}
		}
		
		return method.invoke(controller, list.toArray());
	}
	
}
