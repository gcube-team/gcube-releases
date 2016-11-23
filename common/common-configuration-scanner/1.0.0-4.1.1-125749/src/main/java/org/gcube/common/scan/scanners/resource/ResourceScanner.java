package org.gcube.common.scan.scanners.resource;

import java.util.Collection;

import org.gcube.common.scan.resources.ClasspathResource;



public interface ResourceScanner {

	boolean handles(ClasspathResource resource);
	
	//can go wrong if contents at URL cannot be accessed
	Collection<ClasspathResource> scan(ClasspathResource url) throws Exception;
}
