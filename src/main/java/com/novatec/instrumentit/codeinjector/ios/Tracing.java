package com.novatec.instrumentit.codeinjector.ios;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.novatec.instrumentit.parser.Method;
import com.novatec.instrumentit.parser.MethodController;
import com.novatec.instrumentit.parser.ios.SwiftKeywords;
import com.novatec.instrumentit.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

public class Tracing {

	@Getter
	@Setter
	private MethodController methodController;

	public Tracing(MethodController methodController) {
		this.methodController = methodController;
	}

	public void performTracing() throws IOException {
		HashMap<String, List<Method>> methodMap = this.methodController.getClassToMethodMap();

		for (String key : methodMap.keySet()) {
			String className = key;
			System.out.println(className);
			String source = "";
			File file = null;
			List<Method> methods = methodMap.get(key);
			if (!className.toLowerCase().contains("test")) {

				for (Method m : methods) {
					file = m.getFile();
					source = loadFile(file);
					
					int additionalLines = 0;
					int lineOverflow = 0;
					String pathStart = MethodSwizzling.class.getResource("startInvocation").getPath();
					String start = this.readFile(pathStart, Charset.defaultCharset());
					String pathEnd = MethodSwizzling.class.getResource("closeInvocation").getPath();
					String end = this.readFile(pathEnd, Charset.defaultCharset());
					String pathImport = MethodSwizzling.class.getResource("TraceImport").getPath();
					String importStatement = this.readFile(pathImport, Charset.defaultCharset());
					if (!checkImport(source, importStatement)) {
						source = addImport(source, importStatement);
					}
					String impBlock = m.getImplementationBlock();
					String returnStatement = "return";
					for (int i = 0; i < source.length(); i++) {
						if (StringUtil.substringEquals(source, impBlock, i)) {
							source = StringUtil.addString(source, "\n" + start + "\n", i + 1);
							additionalLines = additionalLines + 1;
							lineOverflow = lineOverflow + start.length();
							i = i + start.length();
							if (m.isHasReturnType()) {
								for (int j = i; j < i + impBlock.length() + lineOverflow; j++) {
									if (StringUtil.substringEquals(source, returnStatement, j)) {
										source = StringUtil.addString(source, "\n" + end + "\n", j);
										additionalLines = additionalLines + 1;
										lineOverflow = lineOverflow + end.length();
										j = j + end.length() + 5;
									}
								}
							} else {
								source = StringUtil.addString(source, "\n" + end + "\n", i + impBlock.length() - 1);
								additionalLines = additionalLines + 1;
								lineOverflow = lineOverflow + end.length();
							}
							i = i + impBlock.length() + lineOverflow;
						}
					}

					System.out.println(source);
					writeFile(file, source);
				}
				
			}

		}

	}

	public String loadFile(File file) {
		try {
			return Files.toString(file, Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = java.nio.file.Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
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
	
	public boolean checkImport(String source, String importString) {
		for (int i = 0; i < source.length(); i++) {
			if (StringUtil.substringEquals(source, importString, i)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String addImport(String source, String importString) {
		for (int i = 0; i < source.length(); i++) {
			if (StringUtil.substringEquals(source, "import", i)) {
				for (int j = i; j < source.length(); j++) {
					if (StringUtil.substringEquals(source, "\n", j)) {
						source = StringUtil.addString(source, importString + "\n", j + 1);
						return source;
					}
				}
			}
		}
		return source;
	}

}
