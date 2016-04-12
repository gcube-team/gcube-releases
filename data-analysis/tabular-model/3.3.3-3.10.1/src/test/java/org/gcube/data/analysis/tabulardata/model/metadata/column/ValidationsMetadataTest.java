package org.gcube.data.analysis.tabulardata.model.metadata.column;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.gcube.data.analysis.tabulardata.SerializationTester;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ValidationsMetadata;
import org.junit.Before;
import org.junit.Test;

public class ValidationsMetadataTest {

	ValidationsMetadata meta;
	
	DataValidationMetadata data;
	
	@Before
	public void setUp(){
		List<Validation> validations=new ArrayList<>();
		validations.add(new Validation("Valid", true,10));
		validations.add(new Validation("invalid",false,100));
		meta=new ValidationsMetadata(validations);
		
		data=new DataValidationMetadata(new Validation("Column XXX must have unique values",false,111), 13);
		
	}
	
	@Test
	public void Test() throws JAXBException{
		System.out.println(meta);
		SerializationTester.roundTripTest(meta);
		System.out.println(data);
		SerializationTester.roundTripTest(data);
	}
}
