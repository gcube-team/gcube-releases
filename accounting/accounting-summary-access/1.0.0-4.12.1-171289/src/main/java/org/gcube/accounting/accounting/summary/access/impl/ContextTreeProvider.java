package org.gcube.accounting.accounting.summary.access.impl;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;



public interface ContextTreeProvider {

	
	public ScopeDescriptor getTree(Object context) throws Exception;
	
}
