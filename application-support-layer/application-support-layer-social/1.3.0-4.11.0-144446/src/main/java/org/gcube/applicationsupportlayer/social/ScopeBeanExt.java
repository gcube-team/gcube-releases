package org.gcube.applicationsupportlayer.social;

import org.gcube.common.scope.impl.ScopeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeBeanExt extends ScopeBean {
	private static final Logger _log = LoggerFactory.getLogger(ScopeBeanExt.class);
	private String context;
	public ScopeBeanExt(String scope) throws IllegalArgumentException {
		super(scope);
		_log.trace("ScopeBeanExt instanciated with context " + scope);
		this.context = scope;
	}
	/**
	 * 
	 * @return the infrastructure scope e.g. /gcube
	 */
	public String getInfrastructureScope() {
		String[] components = this.context.split(separator);
		return separator+components[1];
	}

}
