package com.github.zhou6ang.mvc.parsing;

import java.io.IOException;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

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
			cp.getTopLevelClasses().forEach(p->{
				try {
					ClassParser cpa = new ClassParser(p.asByteSource().openStream(),null);
					JavaClass jc = cpa.parse();
					if(jc.getAnnotationEntries().length != 0){
						System.out.println(jc.getClassName());
					}
				} catch (IOException e) {
					System.out.println("####"+p.getName());
					e.printStackTrace();
				}
			});
//			filter(p ->{
//				try {
//					ClassReader cr = new ClassReader(p.asByteSource().openBufferedStream());
//					cr.accept(new ClassVisitor(Opcodes.ASM6) {
//
//						@Override
//						public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//							if(desc.equals("Lcom/github/zhou6ang/mvc/annotation/BitBean") && visible){
//								System.out.println(p.getName());
//							}
//							return super.visitAnnotation(desc, visible);
//							
//						}
//
//					}, 0);
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
////			Class<?> cl = p.load();
////			for(BitBean bitBean : cl.getDeclaredAnnotationsByType(BitBean.class)){
////				System.out.println(bitBean.value());
////			}
//			return true;
//			}).forEach(e ->{
//				System.out.println("==="+e.getPackageName()+":"+e.getName()+":"+e.getResourceName()+":"+e.url()+":"+e.getSimpleName()+":"+e.getClass());
//			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class A{
		
	}
	
	class B{
		
	}
	
	static class C{
		
	}
}
