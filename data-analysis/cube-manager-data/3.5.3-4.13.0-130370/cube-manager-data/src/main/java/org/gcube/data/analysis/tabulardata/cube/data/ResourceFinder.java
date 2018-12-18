package org.gcube.data.analysis.tabulardata.cube.data;

import java.io.InputStream;
import java.util.Collection;
import java.util.regex.Pattern;


public interface ResourceFinder {

	Collection<String> getResourcesPath(Pattern patter);
	
	InputStream getStream(String file);
}
