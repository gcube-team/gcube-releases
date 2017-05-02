package gr.cite.repo.auth.app;

import gr.cite.repo.auth.app.config.DatabaseInfo;
import gr.cite.repo.auth.app.config.DistributedSession;
import gr.cite.repo.auth.app.config.Security;
import gr.cite.repo.auth.app.config.SessionMgr;
import gr.cite.repo.auth.filters.CustomSecurityFilter;
import io.dropwizard.jersey.sessions.HttpSessionProvider;
import io.dropwizard.setup.Environment;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.sql.DataSource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.JDBCSessionIdManager;
import org.eclipse.jetty.server.session.JDBCSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.h2.jdbcx.JdbcDataSource;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class SecureAppHelpers {

	private static final Logger logger = LoggerFactory
			.getLogger(SecureAppHelpers.class);
	private Environment environment;

	public SecureAppHelpers(Environment environment) {
		logger.debug("Initializing SecureAppHelpers...");
		this.environment = environment;
		logger.debug("Initialized SecureAppHelpers");
	}

	DataSource initializeDatasource(DatabaseInfo databaseInfo) {

		String database = databaseInfo.getDatabase();
		logger.debug("Initializing datasource: "+database+"...");
		if (database.equals("postgres")) {
			PGPoolingDataSource ds = new PGPoolingDataSource();
			ds.setUser(databaseInfo.getUsername());
			ds.setPassword(databaseInfo.getPassword());
			ds.setDatabaseName(databaseInfo.getDatabaseName());
			ds.setServerName(databaseInfo.getServerName());

			logger.debug("Initialized datasource: "+database);
			return ds;
		} else if (database.equals("mysql")) {
			MysqlDataSource ds = new MysqlDataSource();
			ds.setUser(databaseInfo.getUsername());
			ds.setPassword(databaseInfo.getPassword());
			ds.setDatabaseName(databaseInfo.getDatabaseName());
			ds.setServerName(databaseInfo.getServerName());

			logger.debug("Initialized datasource: "+database);
			return ds;
		} else if (database.equals("h2")) {
			JdbcDataSource ds = new JdbcDataSource();
			ds.setUser(databaseInfo.getUsername());
			ds.setPassword(databaseInfo.getPassword());
			ds.setUrl(databaseInfo.getDatabaseName());

			logger.debug("Initialized datasource: "+database);
			return ds;
		} else {
			logger.error("database : " + database + " is not postgres, mysql or h2");
			throw new IllegalArgumentException("database : " + database
					+ " is not postgres, mysql or h2");
		}
	}
	
	public static boolean hasSessionManager(SessionMgr configuration){
		return configuration != null;
	}

	public void applySessionManager(SessionMgr configuration, Server server) {
		if (!hasSessionManager(configuration)) {
			logger.info("no session manager configuration found. using default");
			
//			environment.jersey().register(HttpSessionProvider.class);
//			environment.servlets().setSessionHandler(new SessionHandler());
			return;
		}

		SessionManager sessionManager = null;
		SessionIdManager sessionIDManger = null;

		if (configuration.getSimpleSessionManager() != null
				&& configuration.getSimpleSessionManager().equals(Boolean.TRUE)) {
			logger.info("default session manager");

			sessionManager = new HashSessionManager();
			sessionIDManger = new HashSessionIdManager();

			environment.jersey().register(HttpSessionProvider.class);
		} else {

			DistributedSession distributedSession = configuration
					.getDistributedSession();
			DatabaseInfo databaseInfo = distributedSession.getDatabaseInfo();

			sessionManager = new JDBCSessionManager();
			sessionIDManger = new JDBCSessionIdManager(server);

			DataSource ds = initializeDatasource(databaseInfo);
			((JDBCSessionIdManager) sessionIDManger).setDatasource(ds);
			((JDBCSessionIdManager) sessionIDManger)
					.setWorkerName(distributedSession.getWorkerName());
		}

		sessionManager.setSessionIdManager(sessionIDManger);

		environment.servlets().setSessionHandler(
				new SessionHandler(sessionManager));
		environment.jersey().register(HttpSessionProvider.class);
	}

	public void applySecurity(Security configuration)
			throws IOException {
		if (configuration == null) {
			logger.info("no configuration for security found");
			return;
		}
		
		List<String> urls = configuration.getProtectedUrls();

		logger.info("the following urls will be protected : " + urls);
		
		String unauthorizedLocation = configuration.getUnauthorizedLocation();
		boolean includeTarget = configuration.getIncludeTarget();

		Filter securityFilter = new CustomSecurityFilter(unauthorizedLocation,
				includeTarget);
		environment
				.servlets()
				.addFilter("Saml-Security-Filter", securityFilter)
				.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
						true, urls.toArray(new String[] {}));

	}

}
