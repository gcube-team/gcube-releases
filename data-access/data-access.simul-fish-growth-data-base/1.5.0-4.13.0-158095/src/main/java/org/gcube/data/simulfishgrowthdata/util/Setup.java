package org.gcube.data.simulfishgrowthdata.util;

import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Setup {
	private static final Logger logger = LoggerFactory.getLogger(Setup.class);

	final static String DEF_DB_ENDPOINT_NAME = "SimulFishGrowth";

	public Setup(final String scope) {
		this(scope, DEF_DB_ENDPOINT_NAME);
	}

	public Setup(final String scope, final String dbEndpointName) {
		ScopeProvider.instance.set(scope);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("configuring Hibernate"));
		}
		HibernateUtil.configGently(dbEndpointName, scope);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("configuring Hibernate - done"));
		}

	}

	public Setup(String host, String dbname, String user, String pass) {
		GCubeUtils.prefillDBCredentials(host, dbname, user, pass);
	}

}
