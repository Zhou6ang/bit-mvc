package com.github.zhou6ang.mvc.view;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum BitViews {
	instance;
	
	private static Map<String,String> beans = new ConcurrentHashMap<>();
	private static final Logger logger = LogManager.getLogger(BitViews.class);
	
	private ServletContext servletContext;
	public Object forward(String path) throws Exception{
		if(servletContext == null){
			throw new Exception("servlet context is null.");
		}
		URL url = servletContext.getResource(path);
		Path p = Paths.get(url.toURI());
		logger.debug("Will forward to path:["+p+"]");
		return parseContent(p);
	}

	private String parseContent(Path p) throws IOException {
		logger.debug("Starting to process static html template ...");
		byte[] content = Files.readAllBytes(p);
		Matcher matcher = Pattern.compile("\\#\\{.*\\}").matcher(new String(content,StandardCharsets.UTF_8));
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			String s = matcher.group();
			s = s.substring(2, s.length()-1);
			matcher.appendReplacement(sb, beans.get(s));
		}
		matcher.appendTail(sb);
		logger.debug("The content parsing done.");
		logger.debug("Ending to process static html template ...");
		return sb.toString();
	}

	public void initial(ServletContext context){
		if(context == null){
			throw new RuntimeException("servlet context is null.");
		}
		this.servletContext = context;
		
		beans.put("bean.title", "This is testing title");
		beans.put("bean.content", "This is testing content.");
		//@TODO collect all Bean which has annotation as @BitBean
		//then invoke corresponding method as bean.title.
		
		logger.debug("Initial BitViews ...");
	}
	
	public void destroy(){
		this.servletContext = null;
		beans.clear();
		logger.debug("Destroy BitViews ...");
	}
	
	public boolean findResource(String path) throws Exception{
		if(servletContext == null){
			throw new Exception("servlet context is null.");
		}
		URL url = servletContext.getResource(path);
		boolean result = Optional.ofNullable(url).isPresent();
		if(result){
			logger.debug("Found the resouces: "+path);
		}else{
			logger.debug("Not found the resouces: "+path);
		}
		
		return result;
	}
	
}
