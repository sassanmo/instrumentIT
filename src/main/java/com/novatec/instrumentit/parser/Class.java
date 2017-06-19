package com.novatec.instrumentit.parser;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Class {
	
	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String language;
	
	@Getter
	@Setter
	private int beginIndex;
	
	@Getter
	@Setter
	private int endIndex;
	
	@Getter
	@Setter
	private List<Class> children;
	
	public Class(String name, String language, int beginIndex, int endIndex) {
		this.name = name;
		this.language = language;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.children = new LinkedList<Class>();
	}

}
