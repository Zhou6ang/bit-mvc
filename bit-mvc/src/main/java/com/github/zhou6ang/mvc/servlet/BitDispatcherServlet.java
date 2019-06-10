package com.github.zhou6ang.mvc.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zhou6ang.mvc.engine.BitContextEngine;
import com.github.zhou6ang.mvc.handler.IHandler;
import com.github.zhou6ang.mvc.util.Constants;
import com.github.zhou6ang.mvc.view.BitModelViewer;
import com.github.zhou6ang.mvc.view.BitViewEngine;

@WebServlet(name = "BitMVC", urlPatterns = { "/","*.xhtml"}, loadOnStartup = 1, initParams = {
		@WebInitParam(name = "user", value = "Jaye") }, displayName = "BitDispatcherServlet of Bit-MVC", description = "This is dispatcher servlet of bit-mvc.", largeIcon = "")
@MultipartConfig(maxFileSize = 10000, maxRequestSize = 1000, fileSizeThreshold = 8888)
public class BitDispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BitDispatcherServlet.class);
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	public BitDispatcherServlet() {
		BitContextEngine.instance.init();
		logger.info("The BitContextEngine has been initiated.");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		BitContextEngine.instance.destroy();
		logger.info("The BitContextEngine has been destroyed.");
	}
	
	

//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		super.init(config);
//		BitContextEngine.instance.init();
//		logger.info("The BitContextEngine has been initiated.");
//	}
	
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			process(req, resp);
		} catch (Exception e) {
			throw new ServletException("Exception happened: ",e);
		}
	}



	protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("The BitEngine has recieved request message.");
		response.setContentType("text/html;charset=utf-8");
		// logger.info("RequestURI:"+request.getRequestURI());
		// logger.info("ServletPath:"+request.getServletPath());
		// logger.info("PathTranslated:"+request.getPathTranslated());
		// logger.info("ContextPath:"+request.getContextPath());

		IHandler mappedHandler = getHandler(request);
		if (mappedHandler == null) {
			//check for static resources.
			BitModelViewer mv = new BitModelViewer();
			mv.getViewer().setViewPath(request.getServletPath());
			BitViewEngine view = new BitViewEngine(mv,getServletContext());
			if (view.isValidResourcePath()) {
				flyOut(response, view.moveOn());
			}else {
				noHandlerFound(request, response);
			}
		}else {
			Object result = mappedHandler.process(request,response);
			mappedHandler.getResHeaders().forEach((k,v)->{
				response.addHeader(k, v);
			});
			if(result instanceof BitModelViewer) {
				BitViewEngine view = new BitViewEngine((BitModelViewer)result,getServletContext());
				if (view.isValidResourcePath()) {
					if(((BitModelViewer) result).getStatusCode() != 0) {
						response.setStatus(((BitModelViewer) result).getStatusCode());
					}
					flyOut(response, view.moveOn());
				} else {
					noHandlerFound(request, response);
				}
			}else if(result instanceof JsonNode || result instanceof Map) {
				flyOut(response, jsonMapper.writeValueAsString(result));
			}else {
				flyOut(response, result);
			}
		}
	}
	
	private void flyOut(HttpServletResponse response,Object result) throws Exception {
		response.getWriter().println(result);
		response.getWriter().flush();
	}

	private void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.warn("No mapping found for HTTP request with URI [" + getRequestUri(request)
				+ "] in BitDispatcherServlet with name '" + getServletName() + "'");
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such resources: " + getRequestUri(request));
	}

	private String getRequestUri(HttpServletRequest request) {
		return request.getServletPath();
	}

	private IHandler getHandler(HttpServletRequest request) {
		return BitContextEngine.instance
				.getHandler(request.getMethod() + Constants.AT + getRequestUri(request) + Constants.AT);
	}
	
	
	
}
