package org.gcube.data.simulfishgrowthdata.util;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCubeUtils {
	private static final Logger logger = LoggerFactory.getLogger(GCubeUtils.class);

	private static final String JDBC_URI = "jdbc:postgresql://%s/%s";

	private static String className = "org.postgresql.Driver";
	private static String dialect = "org.hibernate.dialect.PostgreSQLDialect";

	static private final Map<String, String> prefilledDBCredentials = new HashMap<>();
	
	synchronized static public Map<String, String> getCredentials(final String dbEndpoint, final String scope) throws Exception {
		if (logger.isTraceEnabled())
			logger.trace("getCredentials");

		try {
			ScopeProvider.instance.set(scope);
			AccessPoint result = new AccessPointer(dbEndpoint).getIt();
			Map<String, String> toRet = new HashMap<>();
			toRet.put("hibernate.connection.driver_class", className);
			toRet.put("hibernate.dialect", dialect);
			toRet.put("hibernate.connection.url", String.format(JDBC_URI, result.address(), result.name()));
			toRet.put("hibernate.connection.username", result.username());
			toRet.put("hibernate.connection.password", StringEncrypter.getEncrypter().decrypt(result.password()));
			return toRet;

		} catch (Exception e) {
			logger.error("Exception on ic-client test", e);
			throw new Exception("Error while getting database credentials", e);
		}
	}
	
	synchronized static public void prefillDBCredentials(final String host, final String dbname, final String dbuser, final String dbpassPlain) {
		prefilledDBCredentials.clear();
		prefilledDBCredentials.put("hibernate.connection.driver_class", className);
		prefilledDBCredentials.put("hibernate.dialect", dialect);
		prefilledDBCredentials.put("hibernate.connection.url", String.format(JDBC_URI, host, dbname));
		prefilledDBCredentials.put("hibernate.connection.username", dbuser);
		prefilledDBCredentials.put("hibernate.connection.password", dbpassPlain);
	}
	
	synchronized static public Map<String, String> getPrefilledCredentials() throws Exception {
		if (prefilledDBCredentials.isEmpty())
			throw new Exception("Credentials table is empty. You should prefill it first!");
		return prefilledDBCredentials;
	}
	
	synchronized static public boolean isPrefilledDBCredentials() {
		return !prefilledDBCredentials.isEmpty();
	}

}
