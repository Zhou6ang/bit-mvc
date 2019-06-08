package com.github.zhou6ang.mvc.engine;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zhou6ang.mvc.exception.BitMvcException;
import com.github.zhou6ang.mvc.handler.IHandler;

public enum BitContextEngine {
	instance;
	private Map<String, Object> beans;
	private Map<String, Object> contollers;
	private Map<String, IHandler> handlers;
	private final Logger logger = LogManager.getLogger(BitContextEngine.class);
	private BitContextEngine() {
		AnnotationParser ap = new AnnotationParser(true);
		beans = ap.getBitBeans();
		contollers = ap.getBitControllers();
		handlers = ap.getHandlers();
		logger.info("Parsing annotation clazz done. ClassloaderMap size:"+beans.size());
	}
	
	public void init() {
		logger.info("BeanEngine already initiated, clazz size:",beans.size());
	}
	
	public Object getValue(String clazzMethod){
		String bitbeanName = clazzMethod.substring(0,clazzMethod.lastIndexOf("."));
		String method = clazzMethod.substring(clazzMethod.lastIndexOf(".") + 1);
		
		if(bitbeanName.isEmpty() || method.isEmpty()){
			throw new BitMvcException("Incorrect format of key word: "+clazzMethod);
		}

		Object obj = null;
		try {
			obj = execute(bitbeanName,method);
		} catch (Exception e) {
			logger.error("exception happened:",e);
			throw new BitMvcException("exception happened: ",e);
		}
		return obj;
	}
	
	private Object execute(String bitbeanName,String method) throws Exception{
		if(beans == null || beans.isEmpty()){
			throw new BitMvcException("Please initial BeanEngine.");
		}
		Object instance = beans.get(bitbeanName);
		if(instance == null){
			throw new BitMvcException("No declear BitBean for tag #{"+bitbeanName+"."+method+"}");
		}
		Method mtd = instance.getClass().getMethod(method);
		mtd.setAccessible(true);
		return mtd.invoke(instance);
	}

	public Object getBitBean(String beanName) {
		return beans.get(beanName);
	}

	public Object getBitContoller(String controllerName) {
		return contollers.get(controllerName);
	}

	public IHandler getHandler(String handlerName) {
		return handlers.get(handlerName);
	}
	
	public void destroy() {
		
	}
	
}
