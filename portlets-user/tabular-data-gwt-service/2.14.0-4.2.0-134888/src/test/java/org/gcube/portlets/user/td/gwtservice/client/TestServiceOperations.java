package org.gcube.portlets.user.td.gwtservice.client;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TestServiceOperations {

	@Test
	public void TestTROperation() {
		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);
	
		AuthorizationProvider.instance.set(new AuthorizationToken(Constants.DEFAULT_USER));
		TabularDataService service = TabularDataServiceFactory.getService();
	
		List<OperationDefinition> trOperations = service.getCapabilities();
		Assert.assertTrue("No operations exists",trOperations.size() > 0);
		System.out
				.println("------------Tabular Resource Operation--------------");
		for (OperationDefinition operation : trOperations) {
			System.out.println("Name: "+operation.getName());
			//System.out.println("Scope: "+operation.getScope());
			System.out.println("Desc: "+operation.toString());
		
			System.out.println("-----------------------------------");
		}	
	}

}
