package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.Collections;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;
import org.junit.Test;

public class DynamicCheckTest {

	
	@Test
	public void test() throws Exception{
		LeafParameter<?> leaf=new TDTypeValueParameter("id", "name", "desc", Cardinality.ONE);
		Object obj=new TDBoolean(false);
		leaf.validateValue(obj);
	}
	
	@Test
	public void mapTest()throws Exception{
		MapParameter map=new MapParameter("id", "name", "desc", Cardinality.ONE, String.class, String.class,Collections.singletonList("ciao"));
		Map value=Collections.singletonMap("ciao", "no");
		map.validateValue(value);
	}
	
}
