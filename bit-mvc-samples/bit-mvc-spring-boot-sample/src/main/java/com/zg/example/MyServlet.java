package com.zg.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
	
}
