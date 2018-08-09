package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;


import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RegexpStringParameterTest {

	RegexpStringParameter param;
	
	@Before
	public void setUp() throws Exception {
		param = new RegexpStringParameter("id", "name", "description", Cardinality.ONE, "^.$");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRegexpStringParameter() throws IllegalArgumentException {
		new RegexpStringParameter("id", "name", "description", Cardinality.ONE, "");
		new RegexpStringParameter("id", "name", "description", Cardinality.ONE, null);
	}

	@Test(expected=Exception.class)
	public void testValidate() throws Exception {
		param.validateValue("p");
		param.validateValue("..");
	}

	@Test(expected=Exception.class)
	public void testGetRegexp() throws Exception {
		Assert.assertNotNull(param.getRegexp());

		RegexpStringParameter param = new RegexpStringParameter("id", "name", "description", Cardinality.ONE, "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		param.validateValue("http://localhost:8080/FusionRegistry/ws/rest/");
		param.validateValue("wrong string");
		
	}

}
