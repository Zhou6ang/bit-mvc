package com.zg.testing.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean("bitbean_0")
public class MyBean_0 {
	
	@BitAutowired
	private MyBean_1 myBean_1;

	public int getAllUser(){
		return 100;
	}
	
	public List<User> getList(){
		
		return Arrays.asList(new User(1, "a", 10),new User(2, "b", 20),new User(3, "c", 30));
	}
	
	public List<Data> getRank() {

		List<Data> list = Arrays.asList(new Data(1, 96, 3), new Data(2, 99, 2), new Data(3, 100, 1));
		list.sort(new Comparator<Data>() {
			@Override
			public int compare(Data o1, Data o2) {
				return o2.getScore() - o1.getScore();
			}

		});
		return list;
	}
	
	public String title(){
		return "this is testing title from bitbean_0";
	}
	
	public String content(){
		return "The text "+ myBean_1.getCount() +" from dependency bitbean_1";
	}
}
