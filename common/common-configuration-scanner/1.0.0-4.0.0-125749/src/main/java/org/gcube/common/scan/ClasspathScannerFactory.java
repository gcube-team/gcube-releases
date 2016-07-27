package org.gcube.common.scan;

import java.net.URL;
import java.util.Collection;


/**
 * Creates {@link ClasspathScanner}s for given URLs.
 * 
 * @author Fabio Simeoni
 *
 */
public class ClasspathScannerFactory {

	private static ClasspathScanner scanner;
	
	/**
	 * Returns a {@link ClasspathScanner} defined over all the URLs visible to the context classloader and its parents, up to the application
	 * classloader.
	 * @return the scanner
	 */
	public synchronized static ClasspathScanner scanner() {
		if (scanner==null)
			scanner = new DefaultScanner();
		return scanner;
	}
	
	/**
	 * Returns a {@link ClasspathScanner} defined over a given collection of URLs.
	 * @param the URLs
	 * @return the scanner
	 */
	public synchronized static ClasspathScanner scanner(Collection<URL> urls) {
		if (scanner==null)
			scanner = new DefaultScanner(urls);
		return scanner;
	}
	
	//test facility
	/**
	 * Sets the scanner to return from {@link #scanner()} and {@link #scanner(Collection)} for testing purposes.
	 * @param scanner the scanner
	 */
	public static void setScanner(ClasspathScanner scanner) {
		ClasspathScannerFactory.scanner=scanner;
	}
}
