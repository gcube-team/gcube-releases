package org.gcube.common.scan.scanners.url;

import java.net.URL;
import java.util.Collection;
import java.util.Set;

import org.gcube.common.scan.resources.ClasspathResource;


/**
 * Scans URLs for {@link ClasspathResource}s.
 * 
 * @author Fabio Simeoni
 *
 */
public interface URLScanner {

	/**
	 * Returns <code>true</code> if this handler can scan a given URL.
	 * @param url the URL
	 * @return <code>true</code> if this handler can scan the URL
	 */
	boolean handles(URL url);
	
	/**
	 * Returns additional URLs to be scanned in addition to a given URL.
	 * @param url the URL
	 * @return the additional URLs
	 * @throws Exception if the additional URLs cannot be derived
	 */
	Set<URL> additional(URL url) throws Exception;
	
	/**
	 * Scans a given {@link URL} for {@link ClasspathResource}s.
	 * @param url the URL
	 * @return the scanned resources
	 * @throws Exception if the URL cannot be scanned
	 */
	Collection<ClasspathResource> scan(URL url) throws Exception;
}
