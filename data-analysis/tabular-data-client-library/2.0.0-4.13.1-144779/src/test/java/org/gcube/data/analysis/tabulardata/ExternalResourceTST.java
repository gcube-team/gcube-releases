package org.gcube.data.analysis.tabulardata;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.externalResource;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.junit.Before;
import org.junit.Test;

public class ExternalResourceTST {

	
	@Before
	public void init(){
		
	}
	
	
	@Test
	public void getExternalResource() throws Exception{
		List<ResourceDescriptor> descriptors = externalResource().build().getResourcePerTabularResource(57);
		
		for (ResourceDescriptor d : descriptors)
			System.out.println(d);
	}
}
