package org.gcube.common.clients.gcore.queries;

import java.util.List;
import java.util.Map;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISTemplateQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * Default {@link ISFacade} implementation.
 *
 */
public class DefaultISFacade implements ISFacade {

	@Override
	public <R,Q extends ISTemplateQuery<R>> List<R> execute (Class<Q> queryClass,Map<String,String> conditions) throws DiscoveryException {
		
		ISClient client = null;
		try {
			client = GHNContext.getImplementation(ISClient.class);
			if (client == null)
				throw new Exception();
		}
		catch(Exception e) {
			throw new RuntimeException("cannot locate ISClient implementation",e);
		}
		
		//resolve query
		Q isquery=null;
		try {
			isquery = client.getQuery(queryClass);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		//customises query
		for (Map.Entry<String,String> cond: conditions.entrySet())
			isquery.addAtomicConditions(new AtomicCondition(cond.getKey(), cond.getValue()));
		
		//execte query
		try {
			return client.execute(isquery,GCUBEScope.getScope(ScopeProvider.instance.get()));
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e) {
			throw new DiscoveryException(e);
		}
	}
}
