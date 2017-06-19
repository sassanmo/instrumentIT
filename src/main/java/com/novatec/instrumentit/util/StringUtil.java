package com.novatec.instrumentit.util;

import com.novatec.instrumentit.parser.ios.SwiftKeywords;

public class StringUtil {
	
	public static String removeChar(int index, String string) {
		StringBuilder stringBuilder = new StringBuilder(string);
		stringBuilder.deleteCharAt(index);
		return stringBuilder.toString();
	}
	
	public static String removeUntil(int index, String string, char character) {
		while (string.charAt(index) != character && index < string.length()) {
			string = StringUtil.removeChar(index, string);
		}
		return StringUtil.removeChar(index, string);
	}
	
	public static String trimLineBackwards(int index, String string) {
		while (string.charAt(index) == SwiftKeywords.SPACE_CHARACTER && (index - 1) > 0) {
			string = StringUtil.removeChar((index - 1), string);
			index = index - 1;
		}
		return string;
	}
	
	public static String removeUntil(int index, String string, String stringToRemove) {
		while (!string.substring(index, index + stringToRemove.length()).equals(stringToRemove) && index < string.length()) {
			string = StringUtil.removeChar(index, string);
		}
		for (int i = 0; i < stringToRemove.length(); i++) {
			string = StringUtil.removeChar(index, string);
		}
		return string;
	}
	
	public static boolean substringEquals(String string, String equals, int beginIndex) {
		if (beginIndex > 0) {
			int endIndex = beginIndex + equals.length();
			if (endIndex < string.length()) {
				return string.substring(beginIndex, endIndex).equals(equals);
			}
		}
		return false;
	}

}
