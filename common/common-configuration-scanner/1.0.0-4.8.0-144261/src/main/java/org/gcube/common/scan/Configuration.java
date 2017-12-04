package org.gcube.common.scan;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scan.scanners.resource.JarResourceScanner;
import org.gcube.common.scan.scanners.resource.ResourceScanner;
import org.gcube.common.scan.scanners.url.ExcludeScanner;
import org.gcube.common.scan.scanners.url.DirScanner;
import org.gcube.common.scan.scanners.url.JarFileScanner;
import org.gcube.common.scan.scanners.url.JarJarScanner;
import org.gcube.common.scan.scanners.url.URLScanner;

/**
 * Defines the configurations of {@link DefaultScanner}s
 * 
 * @author Fabio Simeoni
 *
 */
public class Configuration {

	static Set<URLScanner> urlScanners = new HashSet<URLScanner>();
	
	static Set<ResourceScanner> resourceScanners = new HashSet<ResourceScanner>();
	
	//registers known scanners
	static {
		//register pre-defined ones
		register(new DirScanner(),new JarFileScanner(), new JarJarScanner(), new ExcludeScanner());
		register(new JarResourceScanner());
	}
	
	/**
	 * Registers additional {@link URLScanner}s
	 * @param scanners the scanners.
	 */
	public static void register(URLScanner ... scanners) {
		for (URLScanner scanner : scanners)
			urlScanners.add(scanner);	
	}

	/**
	 * Registers additional {@link ResourceScanner}s
	 * @param scanners the scanners.
	 */
	public static void register(ResourceScanner ... scanners) {
		for (ResourceScanner scanner : scanners)
			resourceScanners.add(scanner);	
	}
}
