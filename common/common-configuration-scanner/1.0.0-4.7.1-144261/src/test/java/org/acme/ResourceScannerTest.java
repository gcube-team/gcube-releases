package org.acme;

import static junit.framework.Assert.*;
import static org.acme.TestUtils.*;

import java.util.Collection;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.resources.FileResource;
import org.gcube.common.scan.scanners.resource.JarResourceScanner;
import org.junit.Test;

public class ResourceScannerTest {

	@Test
	public void scanJar() throws Exception {
		
		JarResourceScanner scanner = new JarResourceScanner();
		ClasspathResource resource = new FileResource("src/test/resources","test.jar");
		Collection<ClasspathResource> resources = scanner.scan(resource);
		
		assertTrue(contains(resources,"jartest.resource","nested.jar"));
		
		
	}

}
