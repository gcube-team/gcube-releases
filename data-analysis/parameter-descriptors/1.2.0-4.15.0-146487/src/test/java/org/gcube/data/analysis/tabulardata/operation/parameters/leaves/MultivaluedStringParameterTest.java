package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MultivaluedStringParameterTest {

	MultivaluedStringParameter param;
	
	@Before
	public void setUp() throws Exception {
		List<String> stringList = new ArrayList<String>();
		stringList.add("test1");
		stringList.add("test2");
		param = new MultivaluedStringParameter("id", "name", "description", Cardinality.ONE , stringList);
	}

	@Test
	public void testGetAdmittedValues() {
		List<String> values = param.getAdmittedValues();

		Assert.assertEquals(2, values.size());
		Assert.assertTrue(values.contains("test1"));
		Assert.assertFalse(values.contains("test3"));
	}
	
	@Test
	public void testValidate(){
		Assert.assertTrue(param.validate("test1"));
		Assert.assertFalse(param.validate("test3"));
	}
	

}
