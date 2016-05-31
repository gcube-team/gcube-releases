package org.gcube.data.analysis.tabulardata.di;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class DBIdentifiersProviderTest {

	@Inject
	@Named("Data-Admin")
	Instance<DatabaseEndpointIdentifier> dataAdmin;
	
	@Inject
	@Named("Data-User")
	Instance<DatabaseEndpointIdentifier> dataUser;
	
	@Inject
	@Named("Metadata-Admin")
	Instance<DatabaseEndpointIdentifier> metaAdmin;

	@Test
	public void test() {
		Assert.assertNotNull(dataAdmin.get());
		Assert.assertNotNull(dataUser.get());
		Assert.assertNotNull(metaAdmin.get());
	}

}
