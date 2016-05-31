package org.gcube.execution.rr.bridge.scope;

public interface ScopeHelper {

	public abstract String getVOScope(String scope);

	public abstract Boolean isInfraScope(String scope);

	public abstract Boolean isVOScope(String scope);

	public abstract Boolean isVREScope(String scope);
	
	public abstract String getEnclosingScope(String scope);

}