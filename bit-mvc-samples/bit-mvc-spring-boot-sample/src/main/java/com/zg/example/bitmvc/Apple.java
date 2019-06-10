package com.zg.example.bitmvc;

import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean
public class Apple implements IFruit{

	@Override
	public String color() {
		return "green";
	}

}
