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
	
	public MethodController() {
		this.methodMap = new HashMap<String, List<Method>>();
	}
	
	public void addMethod(String file, Method method) {
		if (!this.methodMap.containsKey(file)) {
			this.methodMap.put(file, new LinkedList<Method>());
		}
		this.methodMap.get(file).add(method);
	}
	
	public void mapMethods(String file, List<Method> methods) {
		for (Method method : methods) {
			this.addMethod(file, method);
		}
	}
	
	public void mapMethods(File file, List<Method> methods) {
		this.mapMethods(file.getAbsolutePath(), methods);
	}
	
	public List<Method> getMethods(String file) {
		if (this.methodMap.containsKey(file)) {
			return this.methodMap.get(file);
		} else {
			return null;
		}
	}
	

}
