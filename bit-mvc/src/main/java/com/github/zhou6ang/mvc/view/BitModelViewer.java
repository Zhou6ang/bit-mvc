package com.github.zhou6ang.mvc.view;

import java.util.HashMap;
import java.util.Map;

import com.github.zhou6ang.mvc.model.BitModel;

public class BitModelViewer{

	public BitViewer viewer = new BitViewer();
	public BitModel model = new BitModel();
	public int statusCode;
	public Map<String, Object> header = new HashMap<>();

	public BitViewer getViewer() {
		return viewer;
	}
	public void setViewer(BitViewer viewer) {
		this.viewer = viewer;
	}
	public BitModel getModel() {
		return model;
	}
	public void setModel(BitModel model) {
		this.model = model;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public Map<String, Object> getHeaders() {
		return header;
	}
	public void setHeaders(Map<String, Object> header) {
		this.header = header;
	}
	
}
