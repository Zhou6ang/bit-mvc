package com.github.zhou6ang.mvc.model;

import java.util.concurrent.ConcurrentHashMap;

public class BitModel extends ConcurrentHashMap<String, Object> implements IModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -904211218697795246L;

	public BitModel add(String key, Object value) {
		this.put(key, value);
		return this;
	}

	
}
