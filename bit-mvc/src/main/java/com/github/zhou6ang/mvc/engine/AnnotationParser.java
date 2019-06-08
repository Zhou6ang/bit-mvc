package com.github.zhou6ang.mvc.engine;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitBean;
import com.github.zhou6ang.mvc.annotation.BitRequestPath;
import com.github.zhou6ang.mvc.exception.BitMvcException;
import com.github.zhou6ang.mvc.handler.DefaultHandler;
import com.github.zhou6ang.mvc.handler.IHandler;
import com.github.zhou6ang.mvc.util.Constants;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class AnnotationParser {
	private static final String BITBEAN_ANNOTATION_TYPE = "Lcom/github/zhou6ang/mvc/annotation/BitBean;";
	private static final String BITCONTROLLER_ANNOTATION_TYPE = "Lcom/github/zhou6ang/mvc/annotation/BitController;";
	private static final Logger logger = LogManager.getLogger(AnnotationParser.class);
	private Map<String, Object> bitBean = new ConcurrentHashMap<>();
	private Map<String, Object> bitController = new ConcurrentHashMap<>();
	private Map<String, IHandler> handler = new ConcurrentHashMap<>();
	/**
	 * Parsing all clazz for current ClassLoader.
	 * @param bytecodeAnalasis indicates that whether analyze clazz via byte code or not.
	 * @return Map, key is value of annotation if present, value is clazz object.
	 */
	public AnnotationParser(boolean bytecodeAnalasis) {
		
		try {
			if(bytecodeAnalasis){
				parsingWithBytecode();
			}else{
				parsingWithoutBytecode();
			}
			resolveAllBeanDependencies();
		} catch (Exception e) {
			logger.error(e);
			throw new BitMvcException("Exception happened in AnnotationParser.",e);
		}
	}

	/**
	 * Parsing clazz with byte code analysis, after that loading it to jvm.
	 * 
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	private void parsingWithBytecode() throws IOException {

		logger.debug("Parsing all Clazz with bytecode analysis which have @BitBean annotation.");
		ClassPath cp = ClassPath.from(getClass().getClassLoader());
		cp.getTopLevelClasses().forEach(p -> {
			try {
				ClassParser cpa = new ClassParser(p.asByteSource().openStream(), null);
				JavaClass jc = cpa.parse();
				AnnotationEntry[] ae = jc.getAnnotationEntries();
				for (AnnotationEntry annotationEntry : ae) {
					if (BITBEAN_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType())) {
						logger.debug("Found @BitBean annotation for Clazz: " + p.getName());
						String beanName = processAnnotationBitBean(annotationEntry, p.getName());
						bitBean.put(beanName, p.load().newInstance());
					}else if (BITCONTROLLER_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType())) {
						logger.debug("Found @BitController annotation for Clazz: " + p.getName());
						String controllerPath = processAnnotationBitBean(annotationEntry, p.getName());
						Object controllerInstance = p.load().newInstance();
						bitController.put(controllerPath, controllerInstance);
						
						for(Method method : controllerInstance.getClass().getDeclaredMethods()) {
							BitRequestPath[] anns = method.getDeclaredAnnotationsByType(BitRequestPath.class);
							if(anns != null && anns.length > 0) {
								BitRequestPath requestPath = anns[0];
								if(!requestPath.value().isEmpty()) {
									logger.debug("Found @BitRequestPath annotation for Clazz: " + p.getName());
									handler.put(getHandlerKey(requestPath,controllerPath), new DefaultHandler(method,controllerInstance));
								}else {
									throw new BitMvcException("Error definition for "+requestPath.value()+" in @Controller class "+p.getName());
								}
							}
						}
						
						
					}
				}
			} catch (Exception e) {
				logger.debug("Parsing bytecode error for "+p.getName(), e);
				throw new BitMvcException("Parsing bytecode error for "+p.getName(), e);
			}
		});
		
	}

	private void resolveAllBeanDependencies() throws Exception {

		//process all dependencies for BitBean.
		for (Object instance : bitBean.values()) {
			resolveBeanDependencies(instance);
		}
		//process all dependencies for BitController.
		for (Object instance : bitController.values()) {
			resolveBeanDependencies(instance);
		}

	}

	private void resolveBeanDependencies(Object instance) throws Exception {
		for (Field field : instance.getClass().getDeclaredFields()) {
			BitAutowired[] anns = field.getDeclaredAnnotationsByType(BitAutowired.class);
			if (anns != null && anns.length > 0) {
				String val = anns[0].value();
				field.setAccessible(true);
				if (!val.isEmpty()) {
					field.set(instance, bitBean.get(val));
				} else {
					if (field.getType().isInterface()) {
						List<Object> list = bitBean.values().stream()
								.filter(p -> field.getType().isAssignableFrom(p.getClass()))
								.collect(Collectors.toList());
						if (list.isEmpty()) {
							throw new Error(
									"There is no class implemented interface " + field.getType().getName());
						}
						if (list.size() > 1) {
							throw new Error(
									"There are multiple class implemented interface " + field.getType().getName());
							
						}
						field.set(instance, list.get(0));
					} else {
						String className = field.getType().getName();
						Object instant = bitBean.get(className);
						if(instant == null) {
							throw new Error(
									"Please make sure that defined Class [" + className + "] without alias name.");
						}
						field.set(instance, instant);
					}
				}
			}
		}
	}

	private String getHandlerKey(BitRequestPath requestPath,String pathPrefix) {
		return requestPath.method() + Constants.AT + pathPrefix +requestPath.value() + Constants.AT;
	}

	/**
	 * Parsing and loading clazz without bytecode analysis.
	 * 
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	private void parsingWithoutBytecode() throws IOException {
		logger.debug("Trying to parse all Clazz without bytecode analysis which have @BitBean annotation.");
		getAllClazzByAnnotationType(BitBean.class).forEach(cl -> {
			String name = "";
			for (BitBean bitB : cl.getDeclaredAnnotationsByType(BitBean.class)) {
				name = bitB.value();
			}
			if ("".equalsIgnoreCase(name) || name == null) {
				name = cl.getName();
			}
			bitBean.put(name, cl);
		});
	}

	/**
	 * Get all clazz from ClassLoader and filtered by gave annotation type.
	 * 
	 * @param annotationClass
	 * @return List which contains all Clazz of annotation.
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	public List<Class<?>> getAllClazzByAnnotationType(Class<? extends Annotation> annotationClass) throws IOException {
		return getAllClazzByAnnotationType(null, annotationClass);
	}

	/**
	 * Get all clazz(include sub-package clazz) and filtered by gave annotation
	 * type.
	 * 
	 * @param packageName
	 * @param annotationClass
	 * @return List which contains all Clazz of annotation.
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	public List<Class<?>> getAllClazzByAnnotationType(String packageName, Class<? extends Annotation> annotationClass)
			throws IOException {
		List<Class<?>> list = new ArrayList<Class<?>>();
		ClassPath cp = ClassPath.from(getClass().getClassLoader());
		ImmutableSet<ClassInfo> immutableSet;
		if (packageName == null || packageName.isEmpty()) {
			immutableSet = cp.getTopLevelClasses();
		} else {
			immutableSet = cp.getTopLevelClasses(packageName);
		}
		for (ClassInfo clazzInfo : immutableSet) {
			try {
				Class<?> clazz = clazzInfo.load();
				if (clazz.isAnnotationPresent(annotationClass)) {
					list.add(clazz);
				}
			} catch (NoClassDefFoundError e) {
				logger.debug("Loading clazz error and will skip this clazz.", e);
			}
		}

		return list;
	}

	private String processAnnotationBitBean(AnnotationEntry annotationEntry, String simpleName) {
		for (ElementValuePair evp : annotationEntry.getElementValuePairs()) {
			if ("value".equals(evp.getNameString())) {
				return evp.getValue().stringifyValue();
			}
		}
		return simpleName;
	}
	
	public Map<String, Object> getBitBeans() {
		return bitBean;
	}

	public Map<String, Object> getBitControllers() {
		return bitController;
	}

	public Map<String, IHandler> getHandlers() {
		return handler;
	}

}
