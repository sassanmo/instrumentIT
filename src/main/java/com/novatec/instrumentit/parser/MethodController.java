package com.novatec.instrumentit.parser;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MethodController {
	
	@Getter
	@Setter
	private HashMap<String, List<Method>> methodMap;
	
	@Getter
	@Setter
	private HashMap<String, List<Method>> classToMethodMap;
	
	public MethodController() {
		this.methodMap = new HashMap<String, List<Method>>();
		this.classToMethodMap = new HashMap<String, List<Method>>();
	}
	
	public void addMethod(String file, Method method) {
		if (!this.methodMap.containsKey(file)) {
			this.methodMap.put(file, new LinkedList<Method>());
		}
		this.methodMap.get(file).add(method);
	}
	
	public void addMethodToClassMap(Method method) {
		if (!this.classToMethodMap.containsKey(method.getMethodHolder().getName())) {
			this.classToMethodMap.put(method.getMethodHolder().getName(), new LinkedList<Method>());
		}
		this.classToMethodMap.get(method.getMethodHolder().getName()).add(method);
	}
	
	public List<Method> getMethodsOfClass(String className) {
		if (this.classToMethodMap.containsKey(className)) {
			return this.classToMethodMap.get(className);
		} else {
			return null;
		}
	}
	
	public void mapMethods(String file, List<Method> methods) {
		for (Method method : methods) {
			this.addMethod(file, method);
		}
	}
	
	public void mapMethods(File file, List<Method> methods) {
		this.mapMethods(file.getAbsolutePath(), methods);
	}
	
	public void mapMethodsToClass(List<Method> methods) {
		for (Method method : methods) {
			this.addMethodToClassMap(method);
		}
	}
	
	public List<Method> getMethods(String file) {
		if (this.methodMap.containsKey(file)) {
			return this.methodMap.get(file);
		} else {
			return null;
		}
	}
	

}
