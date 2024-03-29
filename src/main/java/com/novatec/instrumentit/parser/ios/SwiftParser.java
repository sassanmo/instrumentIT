package com.novatec.instrumentit.parser.ios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.novatec.instrumentit.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

import com.novatec.instrumentit.parser.Class;
import com.novatec.instrumentit.parser.Method;

public class SwiftParser {
	
	@Getter
	@Setter
	private File actualFile;
	
	public String loadFile(File file) {
		try {
			this.actualFile = file;
			return Files.toString(file, Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Method> parse(File file) {
		String source = this.loadFile(file);
		return this.parse(source);
	}
	
	public List<Method> parse(String source) {
		source = this.deleteComments(source);
		this.writeFile(actualFile, source);
		List<Class> classes = this.parseClasses(source);
		return this.parseMethods(source, classes);
	}
	
	public List<Method> parseMethods(String source, List<Class> classes) {
		LinkedList<Method> methods = new LinkedList<Method>();
		for (int i = 0; i < source.length(); i++) {
			String protocolString = SwiftKeywords.PROTOCOL + SwiftKeywords.SPACE;
			if (StringUtil.substringEquals(source, protocolString, i)) {
				int blocksize = countBlockSize(source, i);
				i = blocksize;
			}
			String enumString = SwiftKeywords.ENUM + SwiftKeywords.SPACE;
			if (StringUtil.substringEquals(source, enumString, i)) {
				int blocksize = countBlockSize(source, i);
				i = blocksize;
			}
			String structString = SwiftKeywords.STRUCT + SwiftKeywords.SPACE;
			if (StringUtil.substringEquals(source, structString, i)) {
				int blocksize = countBlockSize(source, i);
				i = blocksize;
			}
			
			String functionString = SwiftKeywords.FUNCTION + SwiftKeywords.SPACE;
			if (StringUtil.substringEquals(source, functionString, i)) {
				String methodType = "";
				String staticMethod = SwiftKeywords.STATIC + SwiftKeywords.SPACE;
				if (StringUtil.substringEquals(source, staticMethod, i - staticMethod.length())) {
					methodType = SwiftKeywords.STATIC;
				}
				String classMethod = SwiftKeywords.CLASS + SwiftKeywords.SPACE;
				if (StringUtil.substringEquals(source, classMethod, i - classMethod.length())) {
					methodType = SwiftKeywords.CLASS;
				}
				i = i + functionString.length();
				String functionSelector = this.readNextIdentifier(source, i);
				String returnType = this.parseReturnType(source, i + functionSelector.length() - 1);
				
				int functionBeginIndex = i + functionSelector.length();
				int functionEndIndex = this.countBlockSize(source, functionBeginIndex);
				String functionBlock = source.substring(functionBeginIndex, functionEndIndex);
				String functionLanguage = SwiftKeywords.LANGUAGE;
				Method parsedMethod = new IosMethod(functionSelector, functionBlock, functionBeginIndex, functionEndIndex, this.actualFile, functionLanguage);
				parsedMethod.setMethodType(methodType);
				parsedMethod.setReturnType(returnType);
				parsedMethod.setHasReturnType(returnType != null);
				
				int deltaBegin = Integer.MAX_VALUE;
				Class holderClass = null;
				for (Class c : classes) {
					if (c.getBeginIndex() <= parsedMethod.getBeginIndex() && c.getEndIndex() >= parsedMethod.getEndIndex()) {
						if (parsedMethod.getBeginIndex() - c.getBeginIndex() < deltaBegin) {
							deltaBegin = parsedMethod.getBeginIndex() - c.getBeginIndex();
							holderClass = c;
						}
					}
				}
				parsedMethod.setMethodHolder(holderClass);
				methods.add(parsedMethod);
			}
		}
		return methods;
	}
	
	public List<Class> parseClasses(String source) {
		LinkedList<Class> classes = new LinkedList<Class>();
		for (int i = 0; i < source.length(); i++) {
			String classString = SwiftKeywords.CLASS + SwiftKeywords.SPACE;
			if (StringUtil.substringEquals(source, classString, i)) {
				i = i + classString.length();
				String className = this.readNextIdentifier(source, i);
				int classStartIndex = i + className.length();
				int classEndIndex = this.countBlockSize(source, classStartIndex);
				String classLanguage = SwiftKeywords.LANGUAGE;
				Class parsedClass = new Class(className, classLanguage, classStartIndex, classEndIndex);
				
				for (Class c : classes) {
					if (c.getBeginIndex() > parsedClass.getBeginIndex() && c.getEndIndex() < parsedClass.getEndIndex()) {
						List<Class> children = parsedClass.getChildren();
						children.add(c);
						parsedClass.setChildren(children);
					} else if (c.getBeginIndex() < parsedClass.getBeginIndex() && c.getEndIndex() > parsedClass.getEndIndex()) {
						List<Class> children = c.getChildren();
						children.add(parsedClass);
						c.setChildren(children);
					}
				}
				
				classes.add(parsedClass);
				
			}
		}
		return classes;
	}
	
	public String deleteComments(String source) {
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == SwiftKeywords.SLASH) {
				if (source.substring(i, i + SwiftKeywords.COMMENT_1.length()).equals(SwiftKeywords.COMMENT_1)) {
					source = StringUtil.trimLineBackwards(i, source);
					source = StringUtil.removeUntil(i, source, SwiftKeywords.NEWLINE);
					i = i - 1;
				} else if (source.substring(i, i + SwiftKeywords.COMMENT_DOC_1.length()).equals(SwiftKeywords.COMMENT_DOC_1)) {
					source = StringUtil.trimLineBackwards(i, source);
					source = StringUtil.removeUntil(i, source, SwiftKeywords.NEWLINE);
					i = i - 1;
				} else if (source.substring(i, i + SwiftKeywords.COMMENT_DOC_N_BEGIN.length()).equals(SwiftKeywords.COMMENT_DOC_N_BEGIN)) {
					source = StringUtil.trimLineBackwards(i, source);
					source = StringUtil.removeUntil(i, source, SwiftKeywords.COMMENT_DOC_N_END);
				} else if (source.substring(i, i + SwiftKeywords.COMMENT_N_BEGIN.length()).equals(SwiftKeywords.COMMENT_N_BEGIN)) {
					source = StringUtil.trimLineBackwards(i, source);
					source = StringUtil.removeUntil(i, source, SwiftKeywords.COMMENT_N_END);
				}
			}
		}
		return source;
	}
	
	public String readNextIdentifier(String source, int index) {
		String identifier = "";
		int openedRoundBraces = 0;
		int closedRoundBraces = 0;
		for (int i = index; i < source.length(); i++) {
			if (StringUtil.substringEquals(source, SwiftKeywords.ROUND_BRACE_OPEN, i)) {
				openedRoundBraces = openedRoundBraces + 1;
			} else if (StringUtil.substringEquals(source, SwiftKeywords.ROUND_BRACE_CLOSE, i)) {
				closedRoundBraces = closedRoundBraces + 1;
			} else if (StringUtil.substringEquals(source, SwiftKeywords.BLOCK_BEGIN, i)) {
				if (openedRoundBraces == closedRoundBraces) {
					return identifier;
				}
			} else if (StringUtil.substringEquals(source, SwiftKeywords.COLON, i)) {
				if (openedRoundBraces == closedRoundBraces) {
					return identifier;
				}
			}
			identifier = identifier + source.charAt(i);	
		}
		return null;
	}
	
	public String parseReturnType(String source, int index) {
		String returnType = "";
		boolean hasReturnType = false;
		for (int i = index; i > 0; i--) {
			if (!hasReturnType) {
				if (source.charAt(i) == '>') {
					hasReturnType = true;
					return returnType;
				} else if (StringUtil.substringEquals(source, SwiftKeywords.ROUND_BRACE_CLOSE, i)) {
					hasReturnType = false;
					return null;
				}
			} else {
				if (StringUtil.substringEquals(source, SwiftKeywords.ROUND_BRACE_CLOSE, i)) {
					returnType = returnType.trim();
					return returnType;
				}
			}
			returnType = source.charAt(i) + returnType;
		}
		return null;
	}
	
	public int countBlockSize(String source, int index) {
		int openedBraces = 0;
		int closedBraces = 0;
		for (int i = index; i < source.length(); i++) {
			if (StringUtil.substringEquals(source, SwiftKeywords.BLOCK_BEGIN, i)) {
				openedBraces = openedBraces + 1;
			} else if (StringUtil.substringEquals(source, SwiftKeywords.BLOCK_END, i)) {
				closedBraces = closedBraces + 1;
				if (openedBraces != 0 && openedBraces == closedBraces) {
					return i;
				}
			} 
		}
		return -1;
	}
	
	public void writeFile(File file, String string) {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(file, false);
			byte[] b = string.getBytes(Charset.forName("UTF-8"));
			stream.write(b);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
