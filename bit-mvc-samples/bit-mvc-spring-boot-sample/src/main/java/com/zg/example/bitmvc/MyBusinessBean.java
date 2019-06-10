package com.zg.example.bitmvc;

import java.util.List;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean
public class MyBusinessBean {
	
	@BitAutowired
	private MyDBRepository dbAccess;

	public int getCount(){
		return dbAccess.getCount();
	}
	
	public List<User> getAllUsers(){
		return dbAccess.getAllUsers();
	}
	
	public List<Data> getAllData(){
		return dbAccess.getAllDatas();
	}
}
