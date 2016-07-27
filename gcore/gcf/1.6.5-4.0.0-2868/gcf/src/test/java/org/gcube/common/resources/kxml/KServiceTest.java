package org.gcube.common.resources.kxml;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.kxml.KGCUBEResource;
import org.gcube.common.resources.kxml.service.KGCUBEService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KServiceTest {
	
	static KGCUBEService gs;
	
	static StringWriter sw;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		gs = new KGCUBEService();
		gs.setVersion("1.0.0");
		gs.setServiceClass("Class");
		gs.setServiceName("Name");
		gs.setDescription("desc");
		List<org.gcube.common.core.resources.service.Package> allPackages = gs.getPackages();
		org.gcube.common.core.resources.service.Software p = new org.gcube.common.core.resources.service.Software();
		List<String> files = new ArrayList<String>();
		files.add("file.jar");
		p.setFiles(files);
		p.setMavenCoordinates("org.gcube", "publisher", "1.5", "test-classifier");
        p.setName("ISPublisher");
        p.setVersion("1.5.0");
		allPackages.add(p);
		sw = new StringWriter(); 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testStoreWriter() {
		System.out.println("Serializing the resource...");
		try {
			KGCUBEResource.store(gs, sw);
			System.out.println("...done");
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to serialize the resource");
		}
		System.out.println(sw.toString());
	}

	@Test
	public final void testLoadReaderFromString() {
		gs = new KGCUBEService();//cleanup the resource
		System.out.println("Checking deserialization from string...");
		try {
			KGCUBEResource.load(gs, new StringReader(sw.toString()));
			System.out.println("...done");

		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to deserialize the resource from string");

		}

	}
	
	@Test
	public final void testLoadReaderFromFile() {
		
		System.out.println("Checking deserialization from file...");
		gs = new KGCUBEService();//cleanup the resource
		try {
			KGCUBEResource.load(gs	, new InputStreamReader(this.getClass().getResourceAsStream("/profiles/service.xml")));
			System.out.println("...done");
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to deserialize the resource from file");
		}

	}

}
