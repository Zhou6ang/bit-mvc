package com.github.zhou6ang.mvc.view;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.zhou6ang.mvc.engine.BitContextEngine;
import com.github.zhou6ang.mvc.exception.BitMvcException;
import com.github.zhou6ang.mvc.util.Constants;
import com.github.zhou6ang.mvc.util.GrammarUtils;
import com.google.common.io.CharStreams;

public class BitViewEngine {
	
	private static final Logger logger = LogManager.getLogger(BitViewEngine.class);
	private ServletContext servletContext;
	private BitModelViewer bitModelViewer;
	private InputStream inputstream = null;
	private String validPath = "";
	private String[] configuredPrefixs = { "", System.getProperty(Constants.VIEW_PREFIX, "/template") };

	public BitViewEngine(BitModelViewer bitModelViewer,ServletContext servletContext) {
		this.bitModelViewer = bitModelViewer;
		this.servletContext = servletContext;
		
		if(bitModelViewer == null){
			throw new BitMvcException("bitModelViewer context is invalid.");
		}
		if(servletContext == null){
			throw new BitMvcException("servletContext context is not set.");
		}
	}

	public Object moveOn() throws Exception {
		
		if (inputstream == null) {
			throw new BitMvcException("Could not found view path: " + validPath);
		}
		logger.debug("Will forward to path:[" + validPath + "]");
		return parseContent(inputstream);
	}

	private String parseContent(InputStream inputStream) throws Exception {
		logger.debug("Starting to process static html template ...");
		String metaData = CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		if (checkBitMvcSchema(metaData)) {
			StringBuffer content = new StringBuffer(metaData);
			findAndExecuteELexpression(content);
			findAndCallBitBeanMethod(content);
			findAndFillWithModel(content);
			metaData = content.toString();
		}
		logger.debug("Ending to process static html template ...");
		return metaData;
	}
	
	private boolean checkBitMvcSchema(String content) {
		return content.matches(Constants.BIT_MVC_SCHEMA);
	}
	private void findAndFillWithModel(StringBuffer content) {
		logger.debug("The findAndFillWithModel start.");
		Matcher matcher = GrammarUtils.getVaraibleParsePattern().matcher(content);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String variable = matcher.group(1);
			Object value = bitModelViewer.getModel().get(variable);
			matcher.appendReplacement(sb, value + "");
		}
		matcher.appendTail(sb);
		clearAndSetValue(content,sb.toString());
		logger.debug("The findAndFillWithModel done.");
	}

	private void findAndCallBitBeanMethod(StringBuffer content) {
		logger.debug("The callBitBeanMethod start.");
		Matcher matcher = GrammarUtils.getMethodParsePattern().matcher(content);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String clazzMethod = matcher.group(1);
			matcher.appendReplacement(sb, String.valueOf(BitContextEngine.instance.getValue(clazzMethod)));
		}
		matcher.appendTail(sb);
		clearAndSetValue(content,sb.toString());
		logger.debug("The callBitBeanMethod done.");
	}
	
	private void findAndExecuteELexpression(StringBuffer content) {
		logger.debug("The findAndExecuteELexpression start.");
		Document document = Jsoup.parse(content.toString());
		Elements rootElements = document.getElementsByTag(Constants.ROOT_TAG);
		if(rootElements.size() <= 0 ) {
			throw new BitMvcException("The template has not html tag.");
		}
		Element rootElement = rootElements.get(0);
		executeEACHgrammar(rootElement);
//		executeIFELSEgrammar(rootElement);
		clearAndSetValue(content, document.toString());
		logger.debug("The findAndExecuteELexpression done.");
	}
	
	private void executeEACHgrammar(Element rootElement) {
		String prefix = getPrefixFromRoot(rootElement);
		logger.info("EACH expression executed start.");
		String key = prefix + Constants.COLON + Constants.EACH;
		for (Element e : rootElement.getAllElements()) {
			//e.g. it's should be view:each="emp : #{bitbean_0.getList}"
			//or view:each="emp : ${list}"
			Attribute attr = findAttribute(e, key, null);
			if(attr != null) {
				String formula = attr.getValue().replaceAll(" ", ""); //trim.
				String[] var = formula.split(Constants.COLON);
				if(var.length != 2) {
					throw new BitMvcException("The "+attr.getValue()+" is incorrect.");
				}
				String localVar = var[0];
				//check matched $() or ${}
				String varStatement = GrammarUtils.findVaraibleName(var[1]);
				Object obj = null;
				if(varStatement != null && !varStatement.isEmpty()) {
					obj = bitModelViewer.getModel().get(varStatement);
				}else {
					String clazzMethod = GrammarUtils.findBeanMethodName(var[1]);
					obj = BitContextEngine.instance.getValue(clazzMethod);
				}
				if( obj != null && obj instanceof Collection) {
					Collection<?> collection = (Collection<?>)obj;
					Iterator<?> iterator = collection.iterator();
					Element parent = e.parent();
					while (iterator.hasNext()) {
						Object type = iterator.next();
						Element element = e.clone();
						element.clearAttributes();
						for(Element childElement : element.children()) {
							String express = childElement.attr(prefix + Constants.COLON + Constants.TEXT);
							String varName = GrammarUtils.findVaraibleName(express.replaceAll(localVar+".", ""));
							String value = getFieldValue(type,varName) + "";//convert all data type to String
							childElement.clearAttributes();
							childElement.text(value);
						}
						element.appendTo(parent);
					}
					e.remove();
				}
			}
		}
		
		logger.info("EACH expression executed done.");
	}
	
	private void clearAndSetValue(StringBuffer content, String value) {
		content.delete(0, content.length());
		content.append(value);
	}
	
	private Object getFieldValue(Object type, String varName) {
		try {
			Field field = type.getClass().getDeclaredField(varName);
			field.setAccessible(true);
			return field.get(type);
		} catch (Exception e1) {
			logger.error("Exception happened in getFieldValue()", e1);
			throw new BitMvcException("Exception happened in getFieldValue.", e1);
		}
	}
	private String getPrefixFromRoot(Element rootElement) {
		Attribute attr = findAttribute(rootElement,Constants.XMLNS+Constants.COLON,Constants.BIT_KEYWORD);
		if (attr == null)
			return "";
		String prefix = attr.getKey().replaceAll(Constants.XMLNS+Constants.COLON, Constants.SPACE);
		return prefix;
	}
	private Attribute findAttribute(Element rootElement, String key, String value) {
		for (Attribute attr : rootElement.attributes().asList()) {
			
			if(key != null && !key.isEmpty() && value != null && !value.isEmpty()) {
				if(attr.getKey().contains(key) && attr.getValue().contains(value)) {
					return attr;
				}
			}else if(key != null && !key.isEmpty() && value == null) {
				if(attr.getKey().contains(key)) {
					return attr;
				}
			}else if(key == null && value != null && !value.isEmpty()) {
				if(attr.getValue().contains(value)) {
					return attr;
				}
			}
		}
		return null;
	}
//	public void initial(ServletContext context){
//		if(context == null){
//			throw new RuntimeException("servlet context is null.");
//		}
//		this.servletContext = context;
//		
////		beans.put("bean.title", "This is testing title");
////		beans.put("bean.content", "This is testing content.");
//		//@TODO collect all Bean which has annotation as @BitBean
//		//then invoke corresponding method as bean.title.
//		BitContextEngine.instance.init();
//		
//		logger.debug("Initial BitViews done...");
//	}
	
	public void destroy(){
		this.servletContext = null;
		logger.debug("Destroy BitViews ...");
	}
	
	public boolean isValidResourcePath() throws Exception{
		
		
		for (String prefix : configuredPrefixs) {
			String path = prefix + bitModelViewer.getViewer().getViewPath();
			
			if(checkPathFromJarPackage(path) || checkPathFromServletContext(path)) {
				validPath = path;
				logger.debug("Found the resouces: "+validPath);
				return true;
			}
		}
		logger.debug("Not found the resouces: "+bitModelViewer.getViewer().getViewPath());
		return false;
	}
	
	private boolean checkPathFromServletContext(String path) throws Exception {
		URL url = servletContext.getResource(path);
		if(url != null) {
			inputstream = servletContext.getResourceAsStream(path);
			logger.debug("Full url: "+ url);
			return inputstream != null;
		}
		return false;
	}
	
	private boolean checkPathFromJarPackage(String path) {
		URL url = getClass().getClassLoader().getResource(path);
		if(url != null) {
			inputstream = getClass().getClassLoader().getResourceAsStream(path);
			logger.debug("Full url: "+ url);
			return inputstream != null;
		}
		return false;
	}
	
}
