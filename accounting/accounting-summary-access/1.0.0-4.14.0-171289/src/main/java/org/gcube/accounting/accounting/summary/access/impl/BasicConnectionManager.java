package org.gcube.accounting.accounting.summary.access.impl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BasicConnectionManager implements ConnectionManager {

	
	@Getter
	@AllArgsConstructor
	private static class DataBaseDescriptor{
		private String url;
		private String user;
		private String password;
		private long loadedTimestamp;
	}
	
	
	
	private static final ConcurrentHashMap<String,DataBaseDescriptor> cache=new ConcurrentHashMap<>();
	
	
	
	private synchronized DataBaseDescriptor getDatabase() {
		try {
		final String currentToken=SecurityTokenProvider.instance.get();
		if(currentToken==null) throw new Exception("Unauthorized request. No gcube token found.");
		AuthorizationEntry entry = authorizationService().get(currentToken);
		final String context=entry.getContext();
		return cache.computeIfAbsent(context, new Function<String, DataBaseDescriptor>(){
			@Override
			public DataBaseDescriptor apply(String t) {
				try{
					log.debug("Querying for database in context {} ",context);
				SecurityTokenProvider.instance.set(currentToken);
				SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
				query.addCondition("$resource/Profile/Category/text() eq 'Database'")
						.addCondition("$resource/Profile/Name/text() eq 'AccountingDashboard'");
				DiscoveryClient<ServiceEndpoint> client = ICFactory.clientFor(ServiceEndpoint.class);
				ServiceEndpoint endpoint= client.submit(query).get(0);
				log.debug("Found SE id {} ",endpoint.id());
				AccessPoint ap=endpoint.profile().accessPoints().iterator().next();
				return new DataBaseDescriptor(ap.address(),ap.username(),StringEncrypter.getEncrypter().decrypt(ap.password()),System.currentTimeMillis());
				}catch(Throwable th) {
					throw new RuntimeException("Unable to load Database credentials under context "+context,th); 
				}
			}
		});	
		}catch (Exception t) {
			throw new RuntimeException("Unable to get DB Descriptor ",t);
		}
	}
	
	
	
	@Override
	public Connection getConnection() throws SQLException {
		DataBaseDescriptor db=getDatabase();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}	
		Connection conn = DriverManager.getConnection(db.getUrl(), db.getUser(), db.getPassword());
		
		// PRODUCTION OLD DB
	
		
		conn.setAutoCommit(false);
		return conn;
	}
	
}
