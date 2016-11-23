package org.gcube.data.analysis.statisticalmanager.test.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMComputations;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMComputation;
import org.gcube.data.analysis.statisticalmanager.test.TestCommon;

public class ClearComputations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScopeProvider.instance.set(TestCommon.SCOPE);
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining");
		StatisticalManagerFactory factory=StatisticalManagerDSL.createStateful().build();
		SMComputations comps=factory.getComputations("fabio.sinibaldi", null);
		int removedCount=0;
		
		List<Long> failedRemovals=new ArrayList<>();
		
		for(SMComputation comp:comps.list()){
			try{
				System.out.println("Removing "+(removedCount++)+" / "+comps.list().size());
				factory.removeComputation(comp.operationId()+"");
			}catch(Exception e){
				failedRemovals.add(comp.operationId());
			}
		}
		
		
		System.out.println("Failures ("+failedRemovals.size()+"): ");
		System.out.println(failedRemovals);

	}

}
