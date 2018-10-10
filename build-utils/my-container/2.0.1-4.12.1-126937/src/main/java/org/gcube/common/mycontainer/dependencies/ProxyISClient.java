package org.gcube.common.mycontainer.dependencies;

import java.util.List;

import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISInputStream;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.mycontainer.MyContainerDependencies;

/**
 * An {@link ISClient} that dispatches invocations to an implementation set on {@link MyContainerDependencies}, typically
 * a mock created in the scope of a test.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProxyISClient implements ISClient {

	private ISClient client;
	
	public ProxyISClient() {
		client = MyContainerDependencies.resolve(ISClient.class);
	}

	public <RESULT> List<RESULT> execute(ISQuery<RESULT> query, GCUBEScope scope)
			throws ISMalformedQueryException, ISUnsupportedQueryException,
			ISException {
		return client.execute(query, scope);
	}

	public <RESULT> ISInputStream<RESULT> executeByRef(ISQuery<RESULT> query,
			GCUBEScope scope) throws ISMalformedQueryException,
			ISUnsupportedQueryException, ISException {
		return client.executeByRef(query, scope);
	}

	public <RESULT, QUERY extends ISQuery<RESULT>> QUERY getQuery(
			Class<QUERY> type) throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException {
		return client.getQuery(type);
	}

	public GCUBEGenericQuery getQuery(String name)
			throws ISUnsupportedQueryException {
		return client.getQuery(name);
	}
	


}
