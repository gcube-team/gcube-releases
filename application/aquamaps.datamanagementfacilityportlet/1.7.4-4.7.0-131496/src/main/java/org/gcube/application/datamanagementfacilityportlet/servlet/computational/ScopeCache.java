package org.gcube.application.datamanagementfacilityportlet.servlet.computational;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ComputationalInfrastructure;

class ScopeCache{
	private static final long cacheLifeTime=5*60*1000;
	
	private List<ComputationalInfrastructure> infras;
	private long timestamp;
	
	public ScopeCache(List<ComputationalInfrastructure> theList) {
		infras=theList;
		timestamp=System.currentTimeMillis();
	}
	public List<ComputationalInfrastructure> getInfras() {
		return infras;
	}
	
	public boolean isValid() {
		return (System.currentTimeMillis()-timestamp<cacheLifeTime);
	}
}