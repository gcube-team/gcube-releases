package org.gcube.common.database.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.Credential;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.endpoint.DatabaseProperty;
import org.gcube.common.database.engine.DatabaseInstance;
import org.gcube.common.database.engine.HostingNode;
import org.gcube.common.database.engine.Platform;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Default
@Singleton
public class ISDatabaseProvider implements DatabaseProvider {

	private Map<String, Map<String, DatabaseInstance>> cache = new HashMap<String, Map<String, DatabaseInstance>>();

	private Logger log = LoggerFactory.getLogger(ISDatabaseProvider.class);

	@Override
	public DatabaseEndpoint get(String databaseInstanceId, String endpointId) {
		log.debug(String.format("Retrieving database descriptor for identifier ['%1$s','%2$s'].",
				databaseInstanceId, endpointId));
		
		return get(databaseInstanceId).getEndpoint(endpointId);
	}

	@Override
	public DatabaseInstance get(String databaseEngineId) {
		Map<String, DatabaseInstance> engines = getCachedDatabaseEngines();
		if (!engines.containsKey(databaseEngineId)) return retrieveDatabaseEngineFromIS(databaseEngineId);
		return engines.get(databaseEngineId);
	}

	@Override
	public DatabaseEndpoint get(DatabaseEndpointIdentifier endpointIdentifier) {
		return get(endpointIdentifier.getDatabaseId(),endpointIdentifier.getEndpointId());
	}

	private Map<String, DatabaseInstance> getCachedDatabaseEngines() {
		String scope = ScopeProvider.instance.get();
		if (scope == null)
			throw new RuntimeException("Scope is not set. Unable to retrieve DB runtime resource from IS.");

		if (!cache.containsKey(scope))
			cache.put(scope, new HashMap<String, DatabaseInstance>());
		return cache.get(scope);

	}

	private DatabaseInstance retrieveDatabaseEngineFromIS(String identifier) {
		log.debug(String.format("Retrieving database descriptor for db engine identifier '%s'", identifier));
		String scope = ScopeProvider.instance.get();
		if (scope == null)
			throw new RuntimeException("Scope is not set. Unable to retrieve DB runtime resource from IS.");

		log.debug(String.format("Querying IS for database resources (RuntimeResource) with name '%1$s' ", identifier));
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq 'Database'").addCondition(
				String.format("$resource/Profile/Name/text() eq '%1$s'", identifier));
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);

		log.trace("Retrieved list of Runtime Resources: " + resources);

		if (resources.size() == 0)
			throw new RuntimeException("Found no runtime resources for the given database name: " + identifier);
		if (resources.size() > 1)
			throw new RuntimeException("Too many runtime resources found on the IS for the given database name: "
					+ identifier);

		// Retrieve whole engine instance data and cache it...
		ServiceEndpoint se = resources.get(0);

		DatabaseInstance databaseInstance = new DatabaseInstance();
		databaseInstance.setId(identifier);

		Platform platform = new Platform(se.profile().platform().name(), se.profile().platform().version(), se
				.profile().platform().minorVersion(), se.profile().platform().revisionVersion(), se.profile()
				.platform().buildVersion());
		databaseInstance.setPlatform(platform);

		HostingNode node = new HostingNode(se.profile().runtime().hostedOn(), se.profile().runtime().ghnId(), se
				.profile().runtime().status());
		databaseInstance.setNode(node);

		for (AccessPoint ap : se.profile().accessPoints()) {
			// Cache all first
			DatabaseEndpoint databaseEndpoint = new DatabaseEndpoint();
			databaseEndpoint.setId(ap.name());
			databaseEndpoint.setDescription(ap.description());
			databaseEndpoint.setConnectionString(ap.address());

			try {
				databaseEndpoint.setCredentials(new Credential(ap.username(), StringEncrypter.getEncrypter().decrypt(ap.password())));
			} catch (Exception e) {
				log.error(
						String.format("Error encountered while decrypting access point password '%1$s' for: %2$s",
								ap.password(), ap), e);
				throw new RuntimeException(e.getMessage());
			}
			for (Property p : ap.properties()) {
				databaseEndpoint.getProperties().add(new DatabaseProperty(p.name(), p.value()));
			}
			databaseInstance.addEndpoint(databaseEndpoint);
		}

		cache.get(scope).put(identifier, databaseInstance);
		return databaseInstance;
	}
}
