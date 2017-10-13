package org.gcube.application.aquamaps.datamanagementfacilityportlet.test;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.ComputationalInfrastructure;
import org.gcube.application.datamanagementfacilityportlet.servlet.computational.ComputationalInfrastructureCache;
import org.gcube.common.scope.api.ScopeProvider;



public class GenericResourceRetrieval {
	
	public static final String DEFAULT_SCOPE="/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";
	
	public static void main(String[] args) throws Exception{
		
		ScopeProvider.instance.set(DEFAULT_SCOPE);
		List<ComputationalInfrastructure> retrieved=ComputationalInfrastructureCache.getEnvironments();
		System.out.println(retrieved);
		for(ComputationalInfrastructure comp: retrieved)
		System.out.println(ComputationalInfrastructureCache.getBackendUrl(comp.getSubmissionBackend()));
	}
}
