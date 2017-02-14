package org.gcube.application.framework.http.error.messages;

public class HTTPErrorMessages {
	
	public static String WrongJSONInput(String parameterName) {
		return "Wrong JSON Parameter given: " + parameterName + ".";
	}
	
	public static String MissingParameter(String parameterName) {
		return "Input Parameter Missing: " + parameterName + ".";
	}

}
