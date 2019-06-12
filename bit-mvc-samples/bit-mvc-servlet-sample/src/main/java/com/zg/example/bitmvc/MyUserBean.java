package com.zg.example.bitmvc;

import java.util.Comparator;
import java.util.List;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean("userBean")
public class MyUserBean {
	
	@BitAutowired
	private MyBusinessBean logicBean;

	public int getUserCount(){
		return logicBean.getAllUsers().size();
	}
	
	public List<User> getList(){
		List<User> list = logicBean.getAllUsers();
		return list;
	}
	
	public List<Data> getRank() {

		List<Data> list = logicBean.getAllData();
		list.sort(new Comparator<Data>() {
			@Override
			public int compare(Data o1, Data o2) {
				return o2.getScore() - o1.getScore();
			}

		});
		return list;
	}
	
	public String title(){
		return "Example title from bitbean";
	}
	
	public String content(){
		return "<br/>Note: This text "+ logicBean.getCount() +" was from MyBusinessBean.";
	}
}
