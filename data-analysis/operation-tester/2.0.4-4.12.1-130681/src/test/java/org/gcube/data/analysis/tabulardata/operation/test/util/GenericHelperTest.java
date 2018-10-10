package org.gcube.data.analysis.tabulardata.operation.test.util;

import javax.inject.Inject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class GenericHelperTest {
	
	@Inject
	GenericHelper helper;
	
	@BeforeClass
	public static void setupScope(){
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public final void testComplex() {
		Table table = helper.createComplexTable();
		Assert.assertNotNull(table);
	}
	
	@Test
	public final void testTimePeriod() {
		Table table = helper.createTimePeriodTable();
		Assert.assertNotNull(table);
	}

}
