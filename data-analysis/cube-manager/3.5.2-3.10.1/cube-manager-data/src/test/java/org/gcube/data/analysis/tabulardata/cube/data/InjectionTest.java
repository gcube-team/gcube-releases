package org.gcube.data.analysis.tabulardata.cube.data;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.admin.Admin;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class InjectionTest {
	
	@Inject @Admin 
	DatabaseConnectionProvider adminDBConnProvider;
	
	@Inject @Unprivileged
	DatabaseConnectionProvider unprivilegedDBConnProvider;
	
	@BeforeClass
	public static void beforeClass(){
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void testDatabaseConnectionProviderInjection(){
		Assert.assertNotNull(adminDBConnProvider);
		Assert.assertNotNull(unprivilegedDBConnProvider);
	}
		
}
