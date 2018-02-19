package org.gcube.accounting.datamodel.validations.validators;

import java.util.regex.Pattern;

public class RegexReplace {
	
	protected final String serviceClass;
	protected final String serviceName; 
	
	protected final String regex;
	protected final Pattern regexPattern;

	protected final String replace;
	
	public RegexReplace(String serviceClass, String serviceName, String regex, String replace) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.regex = regex;
		this.regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		this.replace = replace;
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getRegex() {
		return regex;
	}
	
	public Pattern getRegexPattern() {
		return regexPattern;
	}

	public String getReplace() {
		return replace;
	}
	
}