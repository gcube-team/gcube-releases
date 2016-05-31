package org.gcube.data.tr;

import static junit.framework.Assert.*;

import java.util.Arrays;

import javax.xml.namespace.QName;

import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.Mode;
import org.gcube.data.tr.requests.RequestBinder;
import org.junit.Test;
import org.w3c.dom.Element;

public class BinderTest {

	RequestBinder binder = new RequestBinder();
	
	
	@Test
	public void createSourceBuildsACorrectObjectGraph() throws Exception {
		
		
		Binder sut = new Binder();
		
		BindSource request  = new BindSource("name");
		request.setDescription("desc");
		request.addTypes(new QName("type"));
		
		Element e = binder.bind(request);
		TreeSource source = sut.bind(e).get(0);
		
		System.out.println(source);
		
		assertNotNull(source.id());
		assertEquals("name", source.name());
		assertEquals("desc", source.description());
		assertEquals(Arrays.asList(new QName("type")), source.types());
		assertNotNull(source.lifecycle());
		assertNotNull(source.reader());
		assertNotNull(source.writer());
		assertNotNull(source.store());
		assertEquals(source.id(), source.store().id());
		assertNull(source.store().location());
		
	}
	
	@Test
	public void reconfigureSourceBuildsACorrectObjectGraph() throws Exception {
		
		Binder sut = new Binder();
		
		BindSource request  =new BindSource("id",Mode.READABLE);
		
		
		TreeSource source = sut.bind(binder.bind(request)).get(0);
		
		System.out.println(source);
		
		assertEquals("id", source.id());
		assertNotNull(source.lifecycle());
		assertNotNull(source.reader());
		assertNull(source.writer());
		assertNotNull(source.store());
		
	
	}
	
}
