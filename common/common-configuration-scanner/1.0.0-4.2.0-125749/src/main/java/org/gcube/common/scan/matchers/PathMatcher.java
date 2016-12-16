package org.gcube.common.scan.matchers;

import java.util.regex.Pattern;

import org.gcube.common.scan.resources.ClasspathResource;


public class PathMatcher implements ResourceMatcher {

	private final Pattern regexp;
	
	public PathMatcher(String regexp) {
		this.regexp=Pattern.compile(regexp);
	}

	@Override
	public boolean match(ClasspathResource resource) {
		return regexp.matcher(resource.path()).matches();
	}
}
