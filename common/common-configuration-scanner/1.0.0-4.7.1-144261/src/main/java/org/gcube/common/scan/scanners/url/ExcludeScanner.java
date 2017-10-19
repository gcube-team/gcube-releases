package org.gcube.common.scan.scanners.url;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.gcube.common.scan.resources.ClasspathResource;

/**
 * Implements {@link URLScanner} for <code>file:</code> URLs to be excluded.
 * @author Fabio Simeoni
 *
 */
public class ExcludeScanner implements URLScanner {

	@Override
	public boolean handles(URL url) {
		String u = url.toExternalForm();
		return u.endsWith(".dylib") || u.endsWith(".zip") || u.endsWith(".jnilib");
	}
	
	@Override
	public Set<URL> additional(URL url) {
		return Collections.emptySet();
	}
	
	@Override
	public Set<ClasspathResource> scan(URL url) throws Exception {
		
		return Collections.emptySet();
	}

}
