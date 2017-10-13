package org.gcube.common.scan.matchers;

import org.gcube.common.scan.resources.ClasspathResource;

public interface ResourceMatcher {

	boolean match(ClasspathResource resource);

}
