package org.gcube.accounting.accounting.summary.access.impl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;

public class BasicContextTreeProvider implements ContextTreeProvider {

	@Override
	public ScopeDescriptor getTree(Object unuseful) throws Exception {
		
		final String currentToken=SecurityTokenProvider.instance.get();
		if(currentToken==null) throw new Exception("Unauthorized request. No gcube token found.");
		AuthorizationEntry entry = authorizationService().get(currentToken);
		final String context=entry.getContext();
		
		return new ScopeDescriptor(context.substring(context.lastIndexOf("/")+1, context.length()),context);
		
	}

}
