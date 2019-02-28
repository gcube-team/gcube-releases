package org.gcube.data.access.accounting.summary.access.test;

import org.gcube.accounting.accounting.summary.access.impl.ContextTreeProvider;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;

public class DummyContextTreeProvider implements ContextTreeProvider {

	
	@Override
	public ScopeDescriptor getTree(Object context) {
		ScopeDescriptor toReturn=new ScopeDescriptor("Portal", "20508");
		ScopeDescriptor group1=new ScopeDescriptor("D4Science labs","group1");
		group1.getChildren().add(new ScopeDescriptor("Analytics", "/d4science.research-infrastructures.eu/D4Research/AnalyticsLab"));
//		group1.getChildren().add(new ScopeDescriptor("Infra Training", "/d4science.research-infrastructures.eu/D4Research/InfraTraining"));
//		group1.getChildren().add(new ScopeDescriptor("RProto", "/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab"));
		toReturn.getChildren().add(group1);
		ScopeDescriptor group2=new ScopeDescriptor("Group2","group2");
//		group2.getChildren().add(new ScopeDescriptor("AGINFRA","/d4science.research-infrastructures.eu/D4Research/AGINFRAplus"));
//		group2.getChildren().add(new ScopeDescriptor("Tag Me","/d4science.research-infrastructures.eu/SoBigData/TagMe"));
		toReturn.getChildren().add(group2);		
		return toReturn;
	}
}
