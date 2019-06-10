package com.zg.example.bitmvc;

import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean("orange")
public class Orange implements IFruit{

	@Override
	public String color() {
		return "yellow";
	}

}
