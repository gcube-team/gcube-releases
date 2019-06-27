package org.gcube.data.analysis.tabulardata;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.junit.Test;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.query;

public class QueryTST {

	@Test
	public void getQuery() throws NoSuchTableException{
		ScopeProvider.instance.set("/gcube/devsec");
		//System.out.println(query().build().queryAsJson(52l, null, null, null));
		System.out.println(query().build().queryAsJson(323l, new QueryPage(0, 200), null, null, null, null));
	}
	
}
