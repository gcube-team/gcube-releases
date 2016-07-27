package org.gcube.informationsystem.impl.facet;

import org.gcube.informationsystem.model.facet.AccessPointInterfaceFacet;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;

public class Serializer {

	@Test
	public void serializeAccessPoint() throws Exception{
		System.out.println(TypeBinder.serializeType(AccessPointInterfaceFacet.class));
	}
	
}
