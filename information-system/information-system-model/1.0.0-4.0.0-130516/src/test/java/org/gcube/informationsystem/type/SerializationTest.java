package org.gcube.informationsystem.type;

import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;

public class SerializationTest {

	@Test
	public void serialize() throws Exception{
		TypeBinder.serializeType(EntityTest.class);
	}
	
	
}
