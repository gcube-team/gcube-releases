package org.gcube.common.scan;

import java.util.Collection;

import org.gcube.common.scan.matchers.ResourceMatcher;
import org.gcube.common.scan.resources.ClasspathResource;

/**
 * Scans archives of classpath resources and extracts those that satify given conditions.
 * <p>
 * The archives scanned over are properties of implementations.
 * 
 * @author Fabio Simeoni
 *
 */
public interface ClasspathScanner {
	
	/**
	 * Returns {@link ClasspathResource}s that match a given {@link ResourceMatcher}.
	 * @param matcher the matcher
	 * @return the matching resources
	 */
	Collection<ClasspathResource> scan(ResourceMatcher matcher);

}
