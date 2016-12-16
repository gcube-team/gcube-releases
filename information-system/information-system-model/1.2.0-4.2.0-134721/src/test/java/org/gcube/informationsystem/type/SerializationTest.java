package org.gcube.informationsystem.type;

import org.gcube.informationsystem.model.embedded.RelationProperty.ReferentiaIntegrity;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializationTest {

	private static Logger logger = LoggerFactory.getLogger(SerializationTest.class);
	
	@Test
	public void serialize() throws Exception{
		TypeBinder.serializeType(EntityTest.class);
	}
	
	@Test
	public void testGetEnumcostants(){
		Class<?> clz = ReferentiaIntegrity.class;
		
		Object[] constants = clz.getEnumConstants();
		for(Object constant : constants){
			logger.trace("{}", constant.toString());
		}
		
	}
	
}
