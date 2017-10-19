package org.gcube.data.analysis.tabulardata.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.AgencyMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.junit.Test;


public class ImportCodelist {

	@Test
	public void importCodelist() throws NoSuchTabularResourceException, NoSuchOperationException{
		
		ScopeProvider.instance.set("/gcube/devsec");
		TabularDataService service = TabularDataServiceFactory.getService();
		Map<String, Object> parameter = new HashMap<>();
		
		String name = "CL_SPECIES";
		
		parameter.put("registryBaseUrl", "http://data.fao.org/sdmx/registry/");
		parameter.put("id", name);
		parameter.put("agency", "FAO");
		parameter.put("version", "1.0");
		
		
		TabularResource tabularResource = service.createTabularResource();
		Collection<TabularResourceMetadata<? >> metadata = new ArrayList<>();
		metadata.add(new NameMetadata(name));
		metadata.add(new AgencyMetadata("FAO"));
		tabularResource.setAllMetadata(metadata);
		
		
		service.execute(new OperationExecution(200, parameter), tabularResource.getId() );
	}
	
}
