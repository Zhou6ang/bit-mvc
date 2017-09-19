package com.zg.testing;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.zhou6ang.mvc.parsing.AnnotationParser;
import com.github.zhou6ang.mvc.parsing.BeanEngine;

@WebServlet(name = "MyServlet", urlPatterns = { "/servlet" }, initParams = {
		@WebInitParam(name = "myuser", value = "tom") }, asyncSupported = true)
public class MyServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5061559266749545375L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().println("This shows my servlet response.");
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println(BeanEngine.instance.getValue("bitbean_0.title"));
	}
}
