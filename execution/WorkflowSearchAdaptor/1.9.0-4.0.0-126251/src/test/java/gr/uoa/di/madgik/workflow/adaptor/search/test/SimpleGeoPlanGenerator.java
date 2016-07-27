package gr.uoa.di.madgik.workflow.adaptor.search.test;

import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.DataSourceNode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.PlanNode;

import java.util.HashSet;
import java.util.Set;

public class SimpleGeoPlanGenerator implements Generator 
{
	@Override
	public PlanNode generate() 
	{
//		<DataSource>
//		<Instances>72a7cea0-a7c9-11e0-ae56-935e766b8e8e, </Instances>
//		<CQL>0caf1204-ff05-4508-9bab-65c3e93da60f geosearch/inclusion=&quot;1&quot;/ranker=&quot;GenericRanker false&quot;/refiner=&quot;TimeSpanRefiner 1998-12-05T00:00:00 1998-12-08T00:00:00&quot;/colID=&quot;cdabe220-a6ff-11
//		e0-9d70-fda94ff03826&quot;/lang=&quot;en&quot; &quot;10 10 10 1000 1000 1000 1000 10&quot; project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687 </CQL>
//		</DataSource>
		String cql = "0caf1204-ff05-4508-9bab-65c3e93da60f geosearch/inclusion=&quot;1&quot;/ranker=&quot;GenericRanker false&quot;/refiner=&quot;TimeSpanRefiner 1998-12-05T00:00:00 1998-12-08T00:00:00&quot;/colID=&quot;cdabe220-a6ff-11e0-9d70-fda94ff03826&quot;/lang=&quot;en&quot; &quot;10 10 10 1000 1000 1000 1000 10&quot; project 150fae0f-e0d7-4a01-bb9e-cacb4e5da9e0 b7f6b092-eed9-4a1f-b869-3f248034683c 49daf947-94fe-488f-84be-0308a3c46687";
		Set<String> datasourceInstanceIds = new HashSet<String>();
		datasourceInstanceIds.add("72a7cea0-a7c9-11e0-ae56-935e766b8e8e");
		PlanNode GeoIndexNode = new DataSourceNode(datasourceInstanceIds, null, cql, null);
		return GeoIndexNode;
	}
}
