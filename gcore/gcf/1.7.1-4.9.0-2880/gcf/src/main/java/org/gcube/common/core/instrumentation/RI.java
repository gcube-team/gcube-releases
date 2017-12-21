package org.gcube.common.core.instrumentation;

import java.util.Set;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;

public class RI implements RIMBean {

	private GCUBEServiceContext ctxt;
	private long callCount;
	private long failedCallCount;
	private float lastResponseTime;
	private float totalWorkTime;
	
	public RI(GCUBEServiceContext ctxt) {this.ctxt=ctxt;}
	
	public String getServiceClass() {return ctxt.getServiceClass();}
	public String getServiceName() {return ctxt.getName();}
	public String getStatus() {return ctxt.getStatus().toString();}
	public String getScope() {return ctxt.getInstance().getScopes().values().toString();}
	public String addScope(String s) throws Exception {
		Set<GCUBEScope> scopes =ctxt.addScope(GCUBEScope.getScope(s)); 
		return (scopes.size()>0?scopes.iterator().next():"no scope")+" was added to the Running Instance";
	}
	
	public String removeScope(String s) throws Exception {
		GCUBEScope scope = GCUBEScope.getScope(s);
		Set<GCUBEScope> scopes =ctxt.removeScope(scope);
		return (scopes.size()>0?scopes.iterator().next():"no scope")+" was removed from the Running Instance";
	}

	public synchronized long getCallCount() {return callCount;}
	public synchronized void addCall() {this.callCount++;}
	public synchronized float getAverageResponseTime() {return (callCount>0 && totalWorkTime>0)?totalWorkTime/callCount:0;}
	public synchronized float getLastResponseTime() {return lastResponseTime;}
	public synchronized void setLastResponseTime(float time) {lastResponseTime=time; totalWorkTime =totalWorkTime+time;}
	public synchronized long getFailedCalls() {return failedCallCount;}
	public synchronized void addFailedCall() {this.failedCallCount++;}
	
	
}
