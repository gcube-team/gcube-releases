package org.gcube.data.analysis.tabulardata;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.junit.Before;
import org.junit.Test;

import static  org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.externalResource;

public class ExternalResourceTST {

	
	@Before
	public void init(){
		AuthorizationProvider.instance.set(new AuthorizationToken("lucio.lelii"));
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
	}
	
	
	@Test
	public void getExternalResource() throws Exception{
		List<ResourceDescriptor> descriptors = externalResource().build().getResourcePerTabularResource(57);
		
		for (ResourceDescriptor d : descriptors)
			System.out.println(d);
	}
}
