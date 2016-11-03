package org.gcube.data.spd.jobs;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.executor.jobs.dwca.DWCAJobByChildren;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.junit.Test;

public class DWCA {

	@Test
	public void dwcaCreation() throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		DWCAJobByChildren dwcaJob = new DWCAJobByChildren("CatalogueOfLife:13445516");
		dwcaJob.setPluginToUse(getCoLPlugin());
		dwcaJob.run();
		System.out.println(dwcaJob.getStatus());
		System.out.println("error url: "+dwcaJob.getErrorURL());
		System.out.println("result url: "+dwcaJob.getResultURL());
	}
	
	private AbstractPlugin getCoLPlugin() throws Exception{
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'CatalogueOfLife' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);
		/*CatalogueOfLifePlugin col = new CatalogueOfLifePlugin();
		col.initialize(resources.get(0));
		return col;*/
		return null;
	}
	
}
