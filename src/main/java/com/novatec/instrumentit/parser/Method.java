package com.novatec.instrumentit.parser;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public class Method {

	@Getter
	@Setter
	private Class methodHolder;
	
	@Getter
	@Setter
	private String methodText;
	
	@Getter
	@Setter
	private String signature;
	
	@Getter
	@Setter
	private String implementationBlock;
	
	@Getter
	@Setter
	private String methodType;
	
	@Getter
	@Setter
	private File file;
	
	@Getter
	@Setter
	private int beginIndex;
	
	@Getter
	@Setter
	private int endIndex;
	
	@Getter
	@Setter
	private String language;
	
	@Getter
	@Setter
	private boolean hasReturnType;
	
	public Method(String signature, String implementation, int beginIndex, int endIndex, File file, String language) {
		this.signature = signature;
		this.implementationBlock = implementation;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
		this.file = file;
		this.language = language;
		this.methodText = this.signature + this.implementationBlock;
	}
}
