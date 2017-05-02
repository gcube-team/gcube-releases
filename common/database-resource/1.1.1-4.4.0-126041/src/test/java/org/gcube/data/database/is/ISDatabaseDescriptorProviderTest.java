package org.gcube.data.database.is;

import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.endpoint.DatabaseProperty;
import org.gcube.common.database.engine.DatabaseInstance;
import org.gcube.common.database.is.ISDatabaseProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ISDatabaseDescriptorProviderTest {
	
	private Logger log = LoggerFactory.getLogger(ISDatabaseDescriptorProviderTest.class);

	private ISDatabaseProvider isDBProvider;
	
	@Before
	public void setUp() throws Exception {
		isDBProvider = new ISDatabaseProvider();
	}

	@Test
	public void testDevsec() {
		ScopeProvider.instance.set("/gcube/devsec");
		String dbInsanceId = "TabularData Database";
		
		DatabaseInstance di = isDBProvider.get(dbInsanceId);
		log.info("Retrieved database instance: " + di);
		
		
		String endpointId = "Data-Admin";
		DatabaseEndpoint dd = isDBProvider.get(dbInsanceId, endpointId);
		
		Assert.assertEquals("luigi", dd.getCredentials().getUsername());
		Assert.assertEquals("luigi", dd.getCredentials().getPassword());
		Assert.assertEquals("jdbc:postgresql://pc-fortunati.isti.cnr.it:5432/tabulardata", dd.getConnectionString());
		Assert.assertEquals("Tabular Data Database endpoint for data (tables) storage (Admin user)",dd.getDescription());
		Assert.assertEquals(1, dd.getProperties().size());
		Assert.assertEquals(new DatabaseProperty("driver", "org.postgresql.Driver"), dd.getProperties().iterator().next());
		
		endpointId = "Data-User";
		
		dd = isDBProvider.get(dbInsanceId,endpointId);
		
		Assert.assertEquals("client", dd.getCredentials().getUsername());
		Assert.assertEquals("client", dd.getCredentials().getPassword());
		Assert.assertEquals("jdbc:postgresql://pc-fortunati.isti.cnr.it:5432/tabulardata", dd.getConnectionString());
		Assert.assertEquals("Tabular Data Database endpoint for data (tables) storage (Unprivileged user)",dd.getDescription());
		Assert.assertEquals(1, dd.getProperties().size());
		Assert.assertEquals(new DatabaseProperty("driver", "org.postgresql.Driver"), dd.getProperties().iterator().next());
		
		endpointId = "Metadata-Admin";
		dd = isDBProvider.get(dbInsanceId,endpointId);
		
		Assert.assertEquals("luigi", dd.getCredentials().getUsername());
		Assert.assertEquals("luigi", dd.getCredentials().getPassword());
		Assert.assertEquals("jdbc:postgresql://pc-fortunati.isti.cnr.it:5432/tabularmetadata", dd.getConnectionString());
		Assert.assertEquals("Tabular Data Database endpoint for metadata storage (Admin user)",dd.getDescription());
		Assert.assertEquals(2, dd.getProperties().size());		
	}

}
