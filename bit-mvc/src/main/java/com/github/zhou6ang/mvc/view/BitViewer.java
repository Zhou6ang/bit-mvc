package com.github.zhou6ang.mvc.view;

public class BitViewer implements IViewer{
	private String viewPath;
	
	public BitViewer() {
	}
	
	public BitViewer(String viewPath) {
		this.viewPath = viewPath;
	}

	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}
	
}
