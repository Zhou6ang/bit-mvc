package com.github.zhou6ang.mvc.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.zhou6ang.mvc.view.BitViews;

@WebServlet(name = "BitMVC", urlPatterns = { "*.xhtml","*.html"}, loadOnStartup = 1, initParams = {
		@WebInitParam(name = "user", value = "tom") }, displayName = "bit-mvc engine", description = "This is bit-mvc engine.", largeIcon = "")
@MultipartConfig(maxFileSize = 10000, maxRequestSize = 1000, fileSizeThreshold = 8888)
public class BitEngineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BitEngineServlet.class);
	
	@Override
	public void destroy() {
		super.destroy();
		BitViews.instance.destroy();
		logger.info("The BitEngine has been destroyed.");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		BitViews.instance.initial(config.getServletContext());
		logger.info("The BitEngine has been initiated.");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("The BitEngine has recieved request message.");
//		response.setHeader("Connection", "Keep-Alive");
		response.setContentType("text/html;charset=utf-8");
//		request.
//		String path = request.getRequestURI().replaceFirst(request.getContextPath(), "");
		String path = request.getServletPath();
//		logger.info("RequestURI:"+request.getRequestURI());
//		logger.info("ServletPath:"+request.getServletPath());
//		logger.info("PathTranslated:"+request.getPathTranslated());
//		logger.info("ContextPath:"+request.getContextPath());
		logger.info("request path:"+path);
		try {
			if (BitViews.instance.findResource(path)) {
				response.getWriter().println(BitViews.instance.forward(path));
				response.getWriter().flush();
				
			}else{
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "No such resources: "+path);
			}
		} catch (Exception e) {
			logger.error("Parsing views error.", e);
			throw new ServletException("Parsing views error.", e);
		}

	}
	
	
	
}
