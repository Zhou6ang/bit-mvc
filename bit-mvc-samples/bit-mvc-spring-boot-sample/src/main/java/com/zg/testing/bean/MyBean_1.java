package com.zg.testing.bean;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean
public class MyBean_1 {
	
	@BitAutowired
	private MyBean_2 myBean_2;

	public int getCount(){
		return myBean_2.getCount();
	}
}
