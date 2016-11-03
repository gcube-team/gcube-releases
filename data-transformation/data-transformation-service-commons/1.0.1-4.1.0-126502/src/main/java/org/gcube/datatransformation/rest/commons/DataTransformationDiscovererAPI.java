package org.gcube.datatransformation.rest.commons;

import java.util.Set;

public interface DataTransformationDiscovererAPI {

	public Set<String> discoverDataTransformationRunninInstances(String scope);
}