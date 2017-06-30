package com.novatec.instrumentit.codeinjector.ios;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.novatec.instrumentit.parser.Class;
import com.novatec.instrumentit.parser.Method;
import com.novatec.instrumentit.parser.MethodController;
import com.novatec.instrumentit.parser.ios.IosMethod;
import com.novatec.instrumentit.parser.ios.SwiftKeywords;

import lombok.Getter;
import lombok.Setter;

public class MethodSwizzling {

	@Getter
	@Setter
	private MethodController methodController;

	public MethodSwizzling(MethodController methodController) {
		this.methodController = methodController;
	}

	public void performSwizzling() throws IOException {
		HashMap<String, List<Method>> methodMap = this.methodController.getClassToMethodMap();
		for (String key : methodMap.keySet()) {
			String className = key;
			List<Method> methods = methodMap.get(key);
			List<String> properties = new LinkedList<String>();
			List<String> swizzledMethods = new LinkedList<String>();
			String pathClassExtension = MethodSwizzling.class.getResource("classExtensions").getPath();
			String classExtensionString = this.readFile(pathClassExtension, Charset.defaultCharset());
			for (int i = 0; i < methods.size(); i++) {
				String computedPropertyName = "property" + i;
				try {
					String path = MethodSwizzling.class.getResource("swizzlingComputedAttribute").getPath();
					String property = this.readFile(path, Charset.defaultCharset());
					IosMethod iosMethod = (IosMethod) methods.get(i);
					property = property.replace("IITM_SWIZZLING_ATTRIBUTE", computedPropertyName);
					property = property.replace("IITM_CLASS_NAME", className);
					property = property.replace("IITM_METHOD_SELECTOR", iosMethod.getSelector());
					property = property.replace("IITM_METHOD_SELECTOR", iosMethod.getSelector());
					property = property.replace("IITM_INJECTED_METHOD_SELECTOR", "iitm_" + iosMethod.getSelector());
					if (iosMethod.getMethodType().equals("")) {
						String pathInstanceMethodExchange = MethodSwizzling.class.getResource("instanceMethodExchange")
								.getPath();
						String instanceMethodExchangeString = this.readFile(pathInstanceMethodExchange,
								Charset.defaultCharset());
						property = property.replace("IITM_GET_METHODS", instanceMethodExchangeString);
					}
					properties.add(property);
					swizzledMethods.add(this.getSwizzledMethod(iosMethod));
					//System.out.println(property);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			classExtensionString = classExtensionString.replace("IITM_CLASS_NAME", className);
			classExtensionString = classExtensionString.replace("IITM_CLASS_NAME", className);
			String propertyCalls = "";
			String propertyDefs = "";
			for (int i = 0; i < properties.size(); i++) {
				propertyCalls = propertyCalls + "property" + i + "(self)\n\t";
				propertyDefs = propertyDefs + properties.get(i) + "\n";
			}
			classExtensionString = classExtensionString.replace("IITM_SWIZZLING_ATTRIBUTE_CALLS", propertyCalls);
			String injectedFunctions = "";
			for (int i = 0; i < swizzledMethods.size(); i++) {
				injectedFunctions = injectedFunctions + swizzledMethods.get(i) + "\n\t";
			}
			classExtensionString = classExtensionString.replace("IITM_INJECTED_FUNCTIONS", injectedFunctions);
			
			String pathTemplate = MethodSwizzling.class.getResource("swizzlingTemplate").getPath();
			String templateString = this.readFile(pathTemplate, Charset.defaultCharset());
			templateString = templateString.replace("IITM_SWIZZLING_ATTRIBUTES", propertyDefs);
			templateString = templateString.replace("IITM_CLASS_EXTENSIONS", classExtensionString);
			System.out.println(templateString);
			System.out.println("\n\n\n");
		}
	}

	private String getSwizzledMethod(IosMethod iosMethod) throws IOException {
		String method = iosMethod.getSignature();
		method = "func iitm_" + method;
		method = method + "{";
		method = method + "\n\t";
		String pathStartInvocation = MethodSwizzling.class.getResource("startInvocation").getPath();
		String startInvocationString = this.readFile(pathStartInvocation, Charset.defaultCharset());
		method = method + startInvocationString;
		method = method + "\n\t";
		method = method + this.getMethodCall(iosMethod.getSignature(), iosMethod);
		method = method + "\n\t";
		String pathCloseInvocation = MethodSwizzling.class.getResource("closeInvocation").getPath();
		String closeInvocationString = this.readFile(pathCloseInvocation, Charset.defaultCharset());
		method = method + closeInvocationString;
		if (iosMethod.getReturnType() != null) {
			method = method + "\n";
			method = method + "return result";
		}
		method = method + "\n\t}\n";
		return method;
	}

	private String getMethodCall(String signature, IosMethod iosMethod) {
		String actualArgument = "";
		String argumentBlock = "(";
		String methodCall = "iitm_";
		boolean argrumentsBlockFound = false;
		boolean argrumentsBlockEnded = false;
		boolean argumentFound = false;
		boolean single = false;
		for (int i = 0; i < signature.length(); i++) {
			if (argrumentsBlockFound && !argrumentsBlockEnded) {
				if (signature.charAt(i) == SwiftKeywords.ROUND_BRACE_CLOSE.charAt(0)) {
					argumentBlock = argumentBlock + ")";
					i = signature.length();
				} else {
					if (argumentFound) {
						if (signature.charAt(i) == '_') {
							single = true;
						}
						if (signature.charAt(i) != ' ' && signature.charAt(i) != '_') {
							actualArgument = actualArgument + signature.charAt(i);
						}
					}
				}
			}
			if (i < signature.length()) {
				if (signature.charAt(i) == SwiftKeywords.ROUND_BRACE_OPEN.charAt(0)) {
					argrumentsBlockFound = true;
					argumentFound = true;
				}
				if (signature.charAt(i) == SwiftKeywords.COLON.charAt(0)) {
					if (single) {
						actualArgument = actualArgument.replace(":", "");
						argumentBlock = argumentBlock + actualArgument;
					} else {
						argumentBlock = argumentBlock + actualArgument + " " + actualArgument.replace(":", "");
					}
					actualArgument = "";
					argumentFound = false;
				}
				if (signature.charAt(i) == SwiftKeywords.COMMA.charAt(0)) {
					argumentFound = true;
					argumentBlock = argumentBlock + ", ";
				}
				if (!argrumentsBlockFound) {
					methodCall = methodCall + signature.charAt(i);
				}
			}
		}
		methodCall = methodCall + argumentBlock;
		if (iosMethod.getReturnType() != null) {
			methodCall = "let result = " + methodCall;
		}
		return methodCall;
	}

	public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
