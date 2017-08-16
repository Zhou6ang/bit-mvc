package com.github.zhou6ang.mvc.parsing;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zhou6ang.mvc.annotation.BitBean;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class AnnotationParser {
	private static final String BITBEAN_ANNOTATION_TYPE = "Lcom/github/zhou6ang/mvc/annotation/BitBean;";
	private static final Logger logger = LogManager.getLogger(AnnotationParser.class);

	/**
	 * Parsing all clazz for current ClassLoader.
	 * @param bytecodeAnalasis indicates that whether analyze clazz via byte code or not.
	 * @return Map, key is value of annotation if present, value is clazz object.
	 */
	public Map<String, Class<?>> parsing(boolean bytecodeAnalasis) {
		
		try {
			if(bytecodeAnalasis){
				return parsingWithBytecode();
			}else{
				return parsingWithoutBytecode();
			}
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parsing clazz with byte code analysis, after that loading it to jvm.
	 * 
	 * @return Map, key is value of annotation if present, value is clazz object.
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	private Map<String, Class<?>> parsingWithBytecode() throws IOException {

		logger.debug("Trying to parse all Clazz with bytecode analysis which have @BitBean annotation.");
		Map<String, Class<?>> map = new ConcurrentHashMap<>();
		ClassPath cp = ClassPath.from(getClass().getClassLoader());
		cp.getTopLevelClasses().forEach(p -> {
			try {
				ClassParser cpa = new ClassParser(p.asByteSource().openStream(), null);
				JavaClass jc = cpa.parse();
				AnnotationEntry[] ae = jc.getAnnotationEntries();
				for (AnnotationEntry annotationEntry : ae) {
					if (BITBEAN_ANNOTATION_TYPE.equals(annotationEntry.getAnnotationType())) {
						logger.debug("Found @BitBean annotation for Clazz: " + p.getName());
						String beanName = processAnnotationBitBean(annotationEntry, p.getSimpleName());
						map.put(beanName, p.load());
					}
				}
			} catch (IOException e) {
				logger.debug("Parsing bytecode error and skip that class.", e);
			}
		});
		return map;
	}

	/**
	 * Parsing and loading clazz without bytecode analysis.
	 * 
	 * @return Map, key is value of annotation if present, value is clazz object.
	 * @throws IOException throw exception if loading clazz from classloader.
	 */
	private Map<String, Class<?>> parsingWithoutBytecode() throws IOException {
		logger.debug("Trying to parse all Clazz without bytecode analysis which have @BitBean annotation.");
		Map<String, Class<?>> map = new ConcurrentHashMap<>();
		getAllClazzByAnnotationType(BitBean.class).forEach(cl -> {
			String name = "";
			for (BitBean bitB : cl.getDeclaredAnnotationsByType(BitBean.class)) {
				name = bitB.value();
			}
			if ("".equalsIgnoreCase(name) || name == null) {
				name = cl.getSimpleName();
			}
			map.put(name, cl);
		});
		return map;
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

}
