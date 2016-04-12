/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 30, 2014
 *
 */
public class test {
	
	public static void main(String[] args) {
		
		TabularDataService service = TabularDataServiceFactory.getService();
		ScopeProvider.instance.set("/gcube/devsec");
		List<OperationDefinition> caps = service.getCapabilities();
		
		for (OperationDefinition operationDefinition : caps) {
			System.out.println(operationDefinition);
		}
	}

}
