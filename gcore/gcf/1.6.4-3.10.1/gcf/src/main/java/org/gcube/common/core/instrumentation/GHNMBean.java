package org.gcube.common.core.instrumentation;

public interface GHNMBean {

	public String getUptime();
	public void restart() throws Exception; 
	public String addScope(String s) throws Exception;
	public String removeScope(String s) throws Exception;
}
