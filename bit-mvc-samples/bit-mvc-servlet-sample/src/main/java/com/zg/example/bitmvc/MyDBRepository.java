package com.zg.example.bitmvc;

import java.util.Arrays;
import java.util.List;

import com.github.zhou6ang.mvc.annotation.BitBean;

@BitBean
public class MyDBRepository {
	
	public int getCount(){
		//assume that access DB to get count.
		return 111;
	}
	
	public List<User> getAllUsers(){
		//assume that access DB to get users.
		return Arrays.asList(new User(1, "a", 10),new User(2, "b", 20),new User(3, "c", 30),new User(4, "d", 50));
	}
	
	public List<Data> getAllDatas(){
		//assume that access DB to get data.
		return Arrays.asList(new Data(1, 96, 3), new Data(2, 99, 2), new Data(3, 100, 1), new Data(4, 80, 4));
	}
}
