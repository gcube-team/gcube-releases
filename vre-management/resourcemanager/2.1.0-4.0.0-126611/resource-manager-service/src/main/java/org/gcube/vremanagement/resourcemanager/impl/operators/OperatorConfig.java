package org.gcube.vremanagement.resourcemanager.impl.operators;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * Basic configuration for every {@link Operator}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class OperatorConfig {

	public final Session session;
	
	public final GCUBEScope scope;
	
	public final ScopeState scopeState;
	
	public OperatorConfig(Session report, ScopeState scopeState, GCUBEScope ... scope) {
		this.session = report;
		this.scopeState = scopeState;
		this.scope = (scope!=null && scope.length >0)? scope[0] : ServiceContext.getContext().getScope();
	}
	
}
