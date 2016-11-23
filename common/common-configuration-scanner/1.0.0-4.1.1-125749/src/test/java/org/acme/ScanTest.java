package org.acme;

import static org.acme.TestUtils.*;
import static org.junit.Assert.*;

import java.util.Collection;

import org.gcube.common.scan.ClasspathScanner;
import org.gcube.common.scan.ClasspathScannerFactory;
import org.gcube.common.scan.matchers.NameMatcher;
import org.gcube.common.scan.matchers.ResourceMatcher;
import org.gcube.common.scan.resources.ClasspathResource;
import org.junit.Test;

public class ScanTest {

	@Test
	public void scanEntersFileJarsOnly() throws Exception {
		
		ClasspathScanner scanner = ClasspathScannerFactory.scanner();
		
		ResourceMatcher matcher = new ResourceMatcher() {
			
			@Override
			public boolean match(ClasspathResource resource) {
				return true;
			}
		};
		Collection<ClasspathResource> resources = scanner.scan(matcher);
		
		//System.out.println(resources);
		
		assertTrue(contains(resources,"jartest.resource","nested.jar"));
		assertFalse(contains(resources,"innerjartest.resource"));
	}
	
	@Test
	public void matcherIsPassedResources() {

		ClasspathScanner scanner = ClasspathScannerFactory.scanner();
		
		ResourceMatcher matcher =  new NameMatcher("test.resource");
		
		Collection<ClasspathResource> matches = scanner.scan(matcher);
		
		System.out.println(matches);
		
		assertEquals(1, matches.size());
	}
	

}
