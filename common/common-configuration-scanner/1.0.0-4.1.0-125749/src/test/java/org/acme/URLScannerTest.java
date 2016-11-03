package org.acme;

import static junit.framework.Assert.*;
import static org.acme.TestUtils.*;

import java.net.URL;
import java.util.Collection;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.scanners.url.DirScanner;
import org.gcube.common.scan.scanners.url.JarFileScanner;
import org.gcube.common.scan.scanners.url.JarJarScanner;
import org.junit.Test;

public class URLScannerTest {

	@Test
	public void scanDirUrl() throws Exception {
		
		DirScanner scanner = new DirScanner();
		
		URL url = new URL("file:target/test-classes/");
		
		assertTrue(scanner.handles(url));
		
		Collection<ClasspathResource> resources = scanner.scan(url);
		
		System.out.println(resources);
		
		//scanned resources include files
		assertTrue(contains(resources,"test.resource","test.jar"));

		//scanned resources include files nested in folders
		assertTrue(contains(resources,"innertest.resource","innertest.jar"));

		//scanned resources do not include jar file entries
		assertFalse(contains(resources,"jartest.resource","nested.jar"));
		
	}
	
	@Test
	public void scanJarFileUrl() throws Exception {
		
		JarFileScanner scanner = new JarFileScanner();
		
		URL url = new URL("file:src/test/resources/test.jar");
		
		Collection<ClasspathResource> resources = scanner.scan(url);
		
		System.out.println(resources);
		
		assertTrue(contains(resources,"jartest.resource","nested.jar"));
	}
	
	@Test
	public void scanJarUrl() throws Exception {
		
		JarJarScanner scanner = new JarJarScanner();
		
		URL url = new URL("jar:file:src/test/resources/test.jar!/");
		
		Collection<ClasspathResource> resources = scanner.scan(url);
		
		System.out.println(resources);
		
		assertTrue(contains(resources,"jartest.resource","nested.jar"));
		
	}
	


}
