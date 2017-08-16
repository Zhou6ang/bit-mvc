package com.github.zhou6ang.mvc.parsing;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum BeanEngine {
	instance;
	private Map<String, Class<?>> classloaderMap;
	private final Logger logger = LogManager.getLogger(BeanEngine.class);
	private BeanEngine() {
		AnnotationParser ap = new AnnotationParser();
		classloaderMap = ap.parsing(true);
		logger.info("Parsing annotation clazz done. ClassloaderMap size:"+classloaderMap.size());
	}
	
	public Object getValue(String clazzMethod) throws Exception{
		String[] tmp = clazzMethod.split("\\.");
		if(tmp.length != 2){
			throw new RuntimeException("Incorrect format of key word: "+clazzMethod);
		}
		String clazz = tmp[0];
		String method = tmp[1];
		return execute(clazz,method);
	}
	
	private Object execute(String clazz,String method) throws Exception{
		if(classloaderMap == null || classloaderMap.isEmpty()){
			throw new RuntimeException("Please initial BeanEngine.");
		}
		Class<?> clz = classloaderMap.get(clazz);
		if(clz == null){
			throw new RuntimeException("Could not found tag #{"+clazz+"."+method+"}");
		}
		Object targetObj = clz.newInstance();
		Method mtd = targetObj.getClass().getMethod(method);
		mtd.setAccessible(true);
		return mtd.invoke(targetObj);
	}
}
