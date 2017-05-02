package org.gcube.data.analysis.tabulardata.model.harmonization;

import javax.xml.bind.JAXBException;

import org.junit.Assert;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.junit.Test;

public class HarmonizationRulesTest {

	@Test
	public void testSerialization() throws JAXBException{
		HarmonizationRule rule=new HarmonizationRule(new TDText("bla"), new TDText("Bla"), new ColumnLocalId("10"), true, "bla lowercase", "bla uppercase");
		
		Assert.assertTrue(rule.equals(new HarmonizationRule(rule.asMap())));
	}
	
}
