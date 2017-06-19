package com.novatec.instrumentit.parser;

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
	
	public List<Method> getMethods(String file) {
		if (this.methodMap.containsKey(file)) {
			return this.methodMap.get(file);
		} else {
			return null;
		}
	}
	

}
