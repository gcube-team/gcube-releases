package gr.uoa.di.madgik.workflow.adaptor.search.test;

import java.util.HashSet;
import java.util.Set;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

public class SimpleOpenSearchPlanGenerator implements Generator 
{

	@Override
	public PlanNode generate() 
	{
//		<DataSource>
//		<Instances>ff5b4ee0-ad5e-11e0-a803-ce3591c520ff, 		</Instances>
//		<CQL>((((gDocCollectionID == &quot;53321780-ab50-11e0-9da1-fda94ff03826&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (ccdabcef-2d60-4248-8d5f-16d42c233b9f = &quot;cms experiment&quot;)) project ead6f5a8-d8e6-40fd-afd2-ffcec3b042d0 79697524-e3bf-457b-891a-faa3a9b0385f 		</CQL>
//		</DataSource>
		String cql = "((((gDocCollectionID == &quot;53321780-ab50-11e0-9da1-fda94ff03826&quot;) and (gDocCollectionLang == &quot;en&quot;))) and (ccdabcef-2d60-4248-8d5f-16d42c233b9f = &quot;cms experiment&quot;)) project ead6f5a8-d8e6-40fd-afd2-ffcec3b042d0 79697524-e3bf-457b-891a-faa3a9b0385f";
		Set<String> datasourceInstanceIds = new HashSet<String>();
		datasourceInstanceIds.add("ff5b4ee0-ad5e-11e0-a803-ce3591c520ff");
		PlanNode openSearchNode = new DataSourceNode(datasourceInstanceIds, null, cql, null);
		return openSearchNode;
	}

}
