package com.zg.example.bitmvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.zhou6ang.mvc.annotation.BitAutowired;
import com.github.zhou6ang.mvc.annotation.BitController;
import com.github.zhou6ang.mvc.annotation.BitRequestPath;
import com.github.zhou6ang.mvc.model.BitModel;
import com.github.zhou6ang.mvc.util.HttpRequestMethod;
import com.github.zhou6ang.mvc.view.BitModelViewer;
import com.github.zhou6ang.mvc.view.BitViewer;

@BitController("/myapp")
public class MyController {
	
	@BitAutowired("userBean")
	private MyUserBean bean;
	
	@BitAutowired
	private MyBusinessBean logicBean;
	
	private int num = 100;
	
	private String output = "Hello BitMVC ...";
	
	@BitAutowired("orange")
	private IFruit fruit;

	@BitRequestPath("/example/service/simple")
	public String serviceExample_1(HttpServletRequest request, HttpServletResponse response) {
		return "Simple output string.";
	}
	
	@BitRequestPath(value = "/example/service/json", method=HttpRequestMethod.METHOD_GET,
					reqHeader = { "Content-Type=application/json", "Cache-Control=no-cache" }, 
					resHeader = { "Content-Type=application/json", "Cache-Control=no-cache","Content-Language=en,zh" })
	public ObjectNode serviceExample_2(HttpServletRequest request) {
		ObjectMapper json = new ObjectMapper();
		return json.createObjectNode().put("Example", "bit-mvc").put("Content", output).put("Info", "others.");
	}
	
	@BitRequestPath(value = "/example/service/json", method=HttpRequestMethod.METHOD_POST,
					reqHeader = { "Content-Type=application/json", "Cache-Control=no-cache" }, 
					resHeader = { "Content-Type=application/json", "Cache-Control=no-cache","Content-Language=en,zh" })
	public Map<String, String> serviceExample_3(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		map.put("Example", "bit-mvc");
		map.put("OutputType", "HashMap");
		map.put("Key-1", "value-1");
		map.put("Key-2", "value-2");
		return map;
	}
	
	@BitRequestPath(value="/users/profile",method=HttpRequestMethod.METHOD_GET)
	public BitModelViewer requestSample_2(HttpServletRequest request) {
		BitModelViewer mv = new BitModelViewer();
		mv.setModel(new BitModel().add("a", bean.content() ).add("b", function(num,logicBean.getCount())).add("list", bean.getRank()));
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en,zh");
		mv.setViewer(new BitViewer("/myapp/users/profile.xhtml"));
		return mv;
	}
	
	@BitRequestPath("/db/info")
	public BitModelViewer requestSample_3(HttpServletRequest request,HttpServletResponse response) {
		BitModelViewer mv = new BitModelViewer();
		mv.setModel(new BitModel().add("text", bean.content() ).add("sum", function(num,logicBean.getCount())));
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en");
		mv.setViewer(new BitViewer("/myapp/db/info.xhtml"));
		return mv;
	}
	
	@BitRequestPath("/index")
	public BitModelViewer requestSample_1(HttpServletRequest request,HttpServletResponse response) {
		BitModelViewer mv = new BitModelViewer();
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en");
		mv.setViewer(new BitViewer("/index.html"));
		return mv;
	}
	
	private int function(int a, int b) {
		return a + b;
	}
}
