package org.acme;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scan.resources.ClasspathResource;

public class TestUtils {

	public static boolean contains(Collection<ClasspathResource> resources, String ... names) {
		
		Set<String> resourceNames = new HashSet<String>();
		
		for (ClasspathResource r : resources)
			resourceNames.add(r.name());
		
		boolean result = true;
		for (String name : names)
			result = result && resourceNames.contains(name);
		
		return result;
	}
}
