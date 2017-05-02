package org.gcube.common.scan.scanners.resource;

import java.util.Collection;
import java.util.jar.JarFile;

import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.resources.JarEntryResource;
import org.gcube.common.scan.scanners.JarScanner;



public class JarResourceScanner implements ResourceScanner {

	private final JarScanner scanner = new JarScanner();
	
	public boolean handles(ClasspathResource resource) {
		
		//explicitly excludes nested jars
		return resource.name().endsWith(".jar") && 
				!JarEntryResource.class.isAssignableFrom(resource.getClass());
	}
	
	public Collection<ClasspathResource> scan(ClasspathResource resource) throws Exception {
		return scanner.scan(new JarFile(resource.file()));
	}
}
