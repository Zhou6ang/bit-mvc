package com.github.zhou6ang.mvc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarUtils {

	private static Pattern methodPattern = Pattern.compile("\\#\\{([^{}]+)\\}");
	private static Pattern varPattern = Pattern.compile("\\$\\{([^{}]+)\\}");
	
	public static Pattern getMethodParsePattern() {
		return methodPattern;
	}
	
	public static Pattern getVaraibleParsePattern() {
		return varPattern;
	}
	
	public static Matcher getMatcher(String content) {
		return methodPattern.matcher(content);
	}
	
	public static String findVaraibleName(String input) {
		Matcher m = varPattern.matcher(input);
		if(m.matches()) {
			return m.group(1);
		}
		return "";
	}
	
	public static String findBeanMethodName(String input) {
		Matcher m = methodPattern.matcher(input);
		if(m.matches()) {
			return m.group(1);
		}
		return "";
	}
}
