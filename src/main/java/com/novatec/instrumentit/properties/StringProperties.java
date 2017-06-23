package com.novatec.instrumentit.properties;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StringProperties {

	public static final String IOS_SYSTEM = "iOS";
	public static final String SWIFT_FILE_EXTENSION = "swift";
	public static final String OBJECTIVEC_FILE_EXTENSION = "m";
	
	public static final String ANDROID_SYSTEM = "Android";
	public static final List<String> ANDROID_SOURCEFILES = new LinkedList<>(Arrays.asList(".java", ".noidea"));
	
	public static final List<String> IOS_COLLECTION_STRATEGIES = new LinkedList<>(Arrays.asList("Tracing", "Method Swizzling"));
	public static final String IOS_METHOD_SWIZZLING = "Method Swizzling";
	
	public static final String FOLDER_EXTENSION = "";
}
