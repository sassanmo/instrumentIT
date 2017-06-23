package com.novatec.instrumentit.parser.ios;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.novatec.instrumentit.parser.Method;

import lombok.Getter;
import lombok.Setter;

public class IosMethod extends Method {
	
	@Getter
	@Setter
	private String selector;
	
	@Getter
	@Setter
	private String methodIdentfier;

	public IosMethod(String signature, String implementation, int beginIndex, int endIndex, File file,
			String language) {
		super(signature, implementation, beginIndex, endIndex, file, language);
		this.selector = this.createSelector();
	}
	
	public String createSelector() {
		String selector = this.getSignature();
		boolean argrumentsBlockFound = false;
		boolean argrumentsBlockEnded = false;
		boolean argumentFound = false;
		List<String> arguments = new LinkedList<String>();
		String argument = "";
		String name = "";
		for (int i = 0; i < selector.length(); i++) {
			if (argrumentsBlockFound && !argrumentsBlockEnded) {
				if (selector.charAt(i) == SwiftKeywords.ROUND_BRACE_CLOSE.charAt(0)) {
					i = selector.length();
				} else {
					if (argumentFound) {
						argument = argument + selector.charAt(i);
					}
				}
			}
			if (i < selector.length()) {
			if (selector.charAt(i) == SwiftKeywords.ROUND_BRACE_OPEN.charAt(0)) {
				argrumentsBlockFound = true;
				argumentFound = true;
			}
			if (!argrumentsBlockFound) {
				name = name + selector.charAt(i);
			}
			if (selector.charAt(i) == SwiftKeywords.COLON.charAt(0)) {
				arguments.add(argument);
				argument = "";
				argumentFound = false;
			}
			if (selector.charAt(i) == SwiftKeywords.COMMA.charAt(0)) {
				argumentFound = true;
			}
			}
		}
		this.methodIdentfier = name;
		selector = name;
		if (arguments.size() > 0) {
			selector = selector + SwiftKeywords.ROUND_BRACE_OPEN;
			for (String arg : arguments) {
				selector = selector + arg;
			}
			selector = selector + SwiftKeywords.ROUND_BRACE_CLOSE;
		}
		return selector;
	}
	
}
