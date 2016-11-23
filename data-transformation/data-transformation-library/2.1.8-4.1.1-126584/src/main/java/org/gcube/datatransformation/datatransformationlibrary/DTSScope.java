package org.gcube.datatransformation.datatransformationlibrary;

public class DTSScope {

	private static InheritableThreadLocal<String> transactionScope = new InheritableThreadLocal<String>();

	public static synchronized void setScope(String scope) {
		transactionScope.set(scope);
	}

	public static synchronized String getScope() {
		return transactionScope.get();
	}
}
