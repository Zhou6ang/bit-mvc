package com.zg.testing.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitController;
import com.github.zhou6ang.mvc.annotation.BitRequestPath;
import com.github.zhou6ang.mvc.model.BitModel;
import com.github.zhou6ang.mvc.util.HttpRequestMethod;
import com.github.zhou6ang.mvc.view.BitModelViewer;
import com.github.zhou6ang.mvc.view.BitViewer;

@BitController("/myapp")
public class MyController {
	
	@BitAutowired("bitbean_0")
	private MyBean_0 bean;
	
	@BitAutowired
	private MyBean_1 myBean_1;
	
	private int num = 100;
	
	private String output = "Hello BitMVC ...";
	
	@BitAutowired("orange")
	private IFruit fruit;

	@BitRequestPath("/a/b")
	public String request_sample_1(HttpServletRequest request) {
		return output;
	}
	
	@BitRequestPath(value="/users/profile",method=HttpRequestMethod.METHOD_GET)
	public BitModelViewer request_sample_2(HttpServletRequest request) {
		BitModelViewer mv = new BitModelViewer();
		mv.setModel(new BitModel().add("a", bean.content() ).add("b", sum(num,myBean_1.getCount())).add("list", bean.getRank()));
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en,zh");
		mv.setViewer(new BitViewer("/myapp/users/profile.xhtml"));
		return mv;
	}
	
	@BitRequestPath("/db/info")
	public BitModelViewer request_sample_3(HttpServletRequest request,HttpServletResponse response) {
		BitModelViewer mv = new BitModelViewer();
		mv.setModel(new BitModel().add("text", bean.content() ).add("sum", sum(num,myBean_1.getCount())));
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en");
		mv.setViewer(new BitViewer("/myapp/db/info.xhtml"));
		return mv;
	}
	
	@BitRequestPath("/index")
	public BitModelViewer request_sample_4(HttpServletRequest request,HttpServletResponse response) {
		BitModelViewer mv = new BitModelViewer();
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en");
		mv.setViewer(new BitViewer("/index.html"));
		return mv;
	}
	
	private int sum(int a, int b) {
		return a + b;
	}
}
