package org.gcube.common.core.instrumentation;




public interface RIMBean {

	public String getServiceName();
	public String getServiceClass();
	public String getStatus();
	public String getScope();
	public long getCallCount();
	public float getAverageResponseTime();
	public float getLastResponseTime();
	public long getFailedCalls();
	public String addScope(String s) throws Exception; 
	public String removeScope(String s) throws Exception;
}
