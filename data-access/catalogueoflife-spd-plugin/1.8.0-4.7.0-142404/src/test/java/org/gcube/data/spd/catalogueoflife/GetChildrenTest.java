package org.gcube.data.spd.catalogueoflife;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Before;

public class GetChildrenTest {

	CatalogueOfLifePlugin col = new CatalogueOfLifePlugin();
	
	@Before
	public void start() throws Exception{
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'CatalogueOfLife' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		col.initialize(resources.get(0));
	}
	
	public void getChildren() throws Exception{
		System.out.println(col.getClassificationInterface().retrieveTaxonChildrenByTaxonId("12640178"));
	}
	

	public void unfold() throws Exception{
		col.getUnfoldInterface().unfold(new ObjectWriter<String>() {
			
			@Override
			public boolean write(StreamException error) {
				error.printStackTrace();
				return true;
			}
			
			@Override
			public boolean write(String t) {
				System.out.println("retuned: "+ t);
				return true;
			}
			
			@Override
			public boolean isAlive() {
				return true;
			}
		}, "cervidae");
	}
}
