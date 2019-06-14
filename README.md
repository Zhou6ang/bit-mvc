Bit-MVC
=============

License
=======

Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements. See the NOTICE file
distributed with this work for additional information
regarding copyright ownership. The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.

Introduction
============
The bit-mvc is an open source mvc framework, it has high 
performance and flexible framework and it's also a lightweigt 
framwork than others(e.g. Spring-MVC). The bit-mvc provides a good 
and easy way for developer/user to implement Model-View-Controller 
mode in modern web project. All the things in bit-mvc are through
declare annotation to implement, so our mission is that make everything 
easy to use in programming world.

Installation
============

For Non-SpringBoot user, please use below dependency:
### maven dependency
```xml
<dependency>
    <groupId>com.github.zhou6ang.framework</groupId>
    <artifactId>bit-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

For SpringBoot user, please use below autoconfigure dependency:
### maven dependency
```xml
<dependency>
    <groupId>com.github.zhou6ang.framework</groupId>
    <artifactId>bit-mvc-spring-boot-autoconfigure</artifactId>
    <version>1.0.0</version>
</dependency>
```


Tutorial
=========
### I. the sample of spring-boot project which integrate bit-mvc as below.

For springboot project that using bit-mvc framework, please go through https://github.com/Zhou6ang/bit-mvc/tree/master/bit-mvc-samples/bit-mvc-spring-boot-sample

For non-springboot project that using bit-mvc framework, please go through https://github.com/Zhou6ang/bit-mvc/tree/master/bit-mvc-samples/bit-mvc-servlet-sample

### II. usage of annotation as below.

Program entry:
```java
@SpringBootApplication
public class App{
	private static final Logger log = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
```

@BitController,@BitAutowired : BitController is controller of MVC and we can define many requestPath inside it. The BitAutowired indicates that current object variable will be binded automatically.
```java
@BitController("/myapp")
public class MyController {
	
	@BitAutowired("userBean")
	private MyUserBean bean;
	
	@BitAutowired
	private MyBusinessBean logicBean;
	
	@BitRequestPath("/example/service/simple")
	public String serviceExample_1(HttpServletRequest request, HttpServletResponse response) {
		return "XXXXX";
	}
}
```


@BitBean : it indicates that current java bean is bitBean and it will be called by BitBeanEngine.
```java
@BitBean("userBean")
public class MyUserBean {
	
	@BitAutowired
	private MyBusinessBean logicBean;

	public int getUserCount(){
		return logicBean.getAllUsers().size();
	}

}
```

@BitRequestPath : it contains path, method and header of http request.
```java
@BitController("/myapp")
public class MyController {
	
	@BitRequestPath(value = "/example/service/json", method=HttpRequestMethod.METHOD_GET,
					reqHeader = { "Content-Type=application/json" }, 
					resHeader = { "Content-Type=application/json","Content-Language=en,zh" })
	public ObjectNode serviceExample(HttpServletRequest request) {
		ObjectMapper json = new ObjectMapper();
		return json.createObjectNode().put("Example", "bit-mvc").put("Content", "Hello BitMVC").put("Info", "others.");
	}
	
	@BitRequestPath(value = "/example/service/json", method=HttpRequestMethod.METHOD_POST,
					reqHeader = { "Content-Type=application/json" }, 
					resHeader = { "Content-Type=application/json","Content-Language=en,zh" })
	public Map<String, String> serviceExample(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		map.put("Example", "bit-mvc");
		map.put("OutputType", "HashMap");
		map.put("Key-1", "value-1");
		map.put("Key-2", "value-2");
		return map;
	}
}
```

BitModelViewer : it contains viewer and model, the view path is mandantory for viewer and all the data stored in model will be parsed in viewer page.
```java
@BitController("/myapp")
public class MyController {
	
	@BitAutowired("userBean")
	private MyUserBean bean;
	
	private int num = 100;
	
	@BitAutowired
	private MyBusinessBean logicBean;
	
	@BitRequestPath(value="/users/profile",method=HttpRequestMethod.METHOD_GET)
	public BitModelViewer requestSample(HttpServletRequest request) {
		BitModelViewer mv = new BitModelViewer();
		mv.setModel(new BitModel().add("a", bean.content() ).add("b", function(num,logicBean.getCount())).add("list", bean.getRank()));
		mv.setStatusCode(200);
		mv.getHeaders().put("Content-Type", "text/html; charset=utf-8");
		mv.getHeaders().put("Content-Language", "en,zh");
		mv.setViewer(new BitViewer("/myapp/users/profile.xhtml"));
		return mv;
	}
}
```

template foler: normally, the template folder stored xhtml/html template and also some static resources, it's under the resources folder of project as below. We can also redefine it via environment variable `bit.mvc.view-prefix`, e.g. `-Dbit.mvc.view-prefix=custom-folder`
```java
example
    |-pom.xml
    |-src
        |-main
            |-java
                |-example.sample.demo
                    |-App.java
                    |-...
            |-resources
                |-logback-spring.xml
                |-template
                    |-index.html
                    |-myapp
                        |-demo.xhtml
                        |-users
                            |-profile.xhtml
                            |-other.html
                            |-...
                        |-jscript
                            |-demo.js
                        |-css
                            |-demo.css
                        |-pic
                            |-example.jpeg
```