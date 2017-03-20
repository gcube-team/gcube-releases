package org.gcube.common.scan;

import static org.gcube.common.scan.Configuration.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scan.matchers.ResourceMatcher;
import org.gcube.common.scan.resources.ClasspathResource;
import org.gcube.common.scan.scanners.resource.ResourceScanner;
import org.gcube.common.scan.scanners.url.URLScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ClasspathScanner}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class DefaultScanner implements ClasspathScanner {

	private static Logger log = LoggerFactory.getLogger(DefaultScanner.class);

	private final Collection<URL> urls;

	/**
	 * Creates an instance over all the URLs visible to the context classloader and its parents, up to the application
	 * classloader.
	 */
	public DefaultScanner() {
		this(defaultClasspath());
	}

	/**
	 * Creates an instance over a given collection of URLs.
	 * @param urls the urls
	 */
	DefaultScanner(Collection<URL> urls) {

		if (urls == null)
			throw new IllegalArgumentException("no urls specified");

		this.urls = urls;

	}

	@Override
	public Collection<ClasspathResource> scan(ResourceMatcher matcher) {

		if (matcher == null)
			throw new IllegalArgumentException("no matcher specified");

		long start = System.currentTimeMillis();

		Collection<ClasspathResource> scanned = scanUrls(urls, matcher);

		log.info("matched {} resources from {} urls in {} ms",
				new Object[] { scanned.size(), urls.size(), System.currentTimeMillis() - start });

		return scanned;
	}

	//helper: scan URLs
	private Collection<ClasspathResource> scanUrls(Collection<URL> urls, ResourceMatcher matcher) {

		Collection<ClasspathResource> resources = new ArrayList<ClasspathResource>();

		toNextUrl: for (URL url : urls) {

			for (URLScanner scanner : urlScanners)

				if (scanner.handles(url)) {

					try {
						scanUrl(resources,url,scanner, matcher);
					} catch (Exception e) {
						log.error("error scanning " + url, e);
					}

					continue toNextUrl;
				}

			log.warn("no handler for {}", url);
		}

		return resources;
	}

	//helper: scan a single URL (and those additionally associated with it)
	private void scanUrl(Collection<ClasspathResource> results, URL url, URLScanner scanner, ResourceMatcher matcher)
			throws Exception {

		// scan this url
		Collection<ClasspathResource> scanned = scanner.scan(url);

		// scan resources in this url
		results.addAll(scanResources(scanned, matcher));

		// repeat recursively for any additional urls (e.g. classpath manifest entries in jars)
		Set<URL> additionalUrls = scanner.additional(url);

		results.addAll(scanUrls(additionalUrls, matcher));

	}

	//helper: scan resources
	private Collection<ClasspathResource> scanResources(Collection<ClasspathResource> resources, ResourceMatcher matcher) {

		Collection<ClasspathResource> closure = new ArrayList<ClasspathResource>();

		// check we need to scan this resource further (e.g. jar resources)
		toNextResource: for (ClasspathResource resource : resources) {

			boolean handled = false;

			for (ResourceScanner scanner : resourceScanners)

				if (scanner.handles(resource)) {

					handled = true;

					try {

						Collection<ClasspathResource> scanned = scanResource(resource, scanner, matcher);

						// match resources
						for (ClasspathResource r : scanned)
							if (matcher.match(r))
								closure.add(r);
					} catch (Exception e) {
						log.warn("error scanning " + resource);
					}

					continue toNextResource;
				}

			// nobody to further scan this resource, add it as it is
			if (!handled && matcher.match(resource))
				closure.add(resource);
		}

		return closure;
	}

	//helper: scans a single resource (and all those derived from it)
	private Collection<ClasspathResource> scanResource(ClasspathResource resource, ResourceScanner scanner,
			ResourceMatcher matcher) throws Exception {

		// scan this resource
		Collection<ClasspathResource> scanned = scanner.scan(resource);

		// repeat recursively
		return scanResources(scanned, matcher);
	}
	
	//helper: obains the URL of the current classpath
	private static Set<URL> defaultClasspath() {
		final Set<URL> result = new HashSet<URL>();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		while (classLoader != null) {
			if (classLoader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) classLoader).getURLs();
				if (urls != null) {
					result.addAll(new HashSet<URL>(Arrays.asList(urls)));
				}
			}
			classLoader = classLoader.getParent();
		}

		return result;
	}
	
	public static void main(String[] args) {
		
		System.out.println(defaultClasspath());
	}
}
