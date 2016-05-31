package org.gcube.application.framework.http.anonymousaccess.management;

public class FunctionAccess {
	
	String function;
	boolean openAccess;
	
	public FunctionAccess() {
		function = new String();
		openAccess = false;
	}
	
	
	public FunctionAccess(String functionName, Boolean isOpenAccess) {
		function = functionName;
		openAccess = isOpenAccess;
	}
	
	
	public boolean isEqualToFunction(String functionName) {
		if (function.equals(functionName))
			return true;
		else
			return false;
	}
	
	public boolean isOpenAccess() {
		return openAccess;
	}
	
	public void setFunction(String functionName) {
		function = functionName;
	}
	
	public void allowOpenAccess() {
		openAccess = true;
	}
	
	public void restrictOpenAccess() {
		openAccess = false;
	}
	
	public String getFunction() {
		return function;
	}

}
