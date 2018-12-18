package org.gcube.accounting.datamodel.validations.validators;

import java.util.regex.Pattern;

public class RegexReplace {
	
	protected String serviceClass;
	protected String serviceName; 
	
	protected String regex;
	protected Pattern regexPattern;

	protected String replace;
	
	protected RegexReplace() {}
	
	public RegexReplace(String serviceClass, String serviceName, String regex, String replace) {
		super();
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.replace = replace;
		setRegex(regex);
	}
	
	public String getServiceClass() {
		return serviceClass;
	}

	public String getServiceName() {
		return serviceName;
	}

	protected void setRegex(String regex) {
		this.regex = regex;
		this.regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
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