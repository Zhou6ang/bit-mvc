package com.github.zhou6ang.mvc.parsing;

import java.io.IOException;

import com.github.zhou6ang.mvc.annotation.BitBean;
import com.google.common.reflect.ClassPath;

public class AnnotationParser {

//	private ClassLoader classloader;
//
//	public AnnotationParser(ClassLoader classloader) {
//		this.classloader = classloader;
//	}
	
	public void parsing(){
		try {
			ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());
			cp.getTopLevelClasses().stream().filter(p ->{
			Class<?> cl = p.load();
			for(BitBean bitBean : cl.getDeclaredAnnotationsByType(BitBean.class)){
				System.out.println(bitBean.value());
			}
			return true;
			}).forEach(e ->{
				System.out.println(e.getPackageName()+":"+e.getName()+":"+e.getResourceName()+":"+e.url()+":"+e.getSimpleName()+":"+e.getClass());
				System.out.println(e.getName());
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	class A{
		
	}
	
	class B{
		
	}
	
	static class C{
		
	}
}
