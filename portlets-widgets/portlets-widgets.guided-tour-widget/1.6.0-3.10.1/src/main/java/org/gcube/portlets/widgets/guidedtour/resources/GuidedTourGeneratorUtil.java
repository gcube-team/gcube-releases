/**
 * 
 */
package org.gcube.portlets.widgets.guidedtour.resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.dev.util.collect.Maps;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.ext.ClientBundleRequirements;
import com.google.gwt.resources.ext.DefaultExtensions;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class GuidedTourGeneratorUtil {

	public static Map<TourLanguage, URL> findResources(TreeLogger logger, ResourceContext context, JMethod method, TourLanguage[] locales) throws UnableToCompleteException {
		JClassType returnType = method.getReturnType().isClassOrInterface();
		assert returnType != null;
		DefaultExtensions annotation = returnType.findAnnotationInTypeHierarchy(DefaultExtensions.class);
		String[] extensions;
		if (annotation != null) {
			extensions = annotation.value();
		} else {
			extensions = new String[0];
		}
		return findResources(logger, context, method, locales, extensions);
	}

	public static Map<TourLanguage, URL> findResources(TreeLogger logger, ResourceContext context, JMethod method, TourLanguage[] locales, String[] defaultSuffixes)	throws UnableToCompleteException {
		Locator[] locators = getDefaultLocators(context.getGeneratorContext());
		Map<TourLanguage, URL> toReturn = findResources(logger, locators, context, method, locales, defaultSuffixes);
		return toReturn;
	}

	/**
	 * Main implementation of findResources.
	 */
	private static Map<TourLanguage, URL> findResources(TreeLogger logger, Locator[] locators, ResourceContext context, JMethod method, TourLanguage[] locales, String[] defaultSuffixes) throws UnableToCompleteException {
		logger = logger.branch(TreeLogger.DEBUG, "Finding resources");

		//getLocale(logger, context.getGeneratorContext());

		//checkForDeprecatedAnnotations(logger, method);

		Source resourceAnnotation = method.getAnnotation(Source.class);
		Map<TourLanguage, URL> toReturn = new HashMap<TourLanguage, URL>();

		if (resourceAnnotation != null) {
			// The user has put an @Source annotation on the accessor method
			String[] resources = resourceAnnotation.value();

			for (String resource : resources) {
				for (TourLanguage locale:locales){
					// Try to find the resource relative to the package.
					URL resourceURL = null;

					for (Locator locator : locators) {
						resourceURL = tryFindResource(locator, context, getPathRelativeToPackage(method.getEnclosingType().getPackage(), resource), locale.toString());

						/*
						 * If we didn't find the resource relative to the package, assume it
						 * is absolute.
						 */
						if (resourceURL == null) {
							resourceURL = tryFindResource(locator, context, resource, locale.toString());
						}

						// If we have found a resource, take the first match
						if (resourceURL != null) {
							break;
						}
					}

					if (resourceURL != null) toReturn.put(locale, resourceURL);				}
			}
		}

		addTypeRequirementsForMethod(context, method);
		return toReturn;
	}

	private static URL tryFindResource(Locator locator, String resourceName, String locale) {

		URL toReturn = null;

		// Convert language_country_variant to independent pieces
		int lastDot = resourceName.lastIndexOf(".");
		String prefix = lastDot == -1 ? resourceName : resourceName.substring(0, lastDot);
		String extension = lastDot == -1 ? "" : resourceName.substring(lastDot);

		String localeInsert = "_" + locale;

		toReturn = locator.locate(prefix + localeInsert + extension);

		return toReturn;
	}

	/**
	 * Performs the locale lookup function for a given resource name.  Will also
	 * add the located resource to the requirements object for the context.
	 *
	 * @param locator the Locator to use to load the resources
	 * @param context the ResourceContext
	 * @param resourceName the string name of the desired resource
	 * @param locale the locale of the current rebind permutation
	 * @return a URL by which the resource can be loaded, <code>null</code> if one
	 *         cannot be found
	 */
	private static URL tryFindResource(Locator locator, ResourceContext context, String resourceName, String locale) {

		URL toReturn = tryFindResource(locator, resourceName, locale);
		if (toReturn != null && context != null) {
			ClientBundleRequirements reqs = context.getRequirements();
			if (reqs != null) {
				reqs.addResolvedResource(resourceName, toReturn);
			}
		}

		return toReturn;
	}

	/**
	 * Add the type dependency requirements for a method, to the context.
	 * 
	 * @param context
	 * @param method
	 */
	private static void addTypeRequirementsForMethod(ResourceContext context, JMethod method) {
		ClientBundleRequirements reqs = context.getRequirements();
		if (reqs != null) {
			reqs.addTypeHierarchy(method.getEnclosingType());
			reqs.addTypeHierarchy((JClassType) method.getReturnType());
		}
	}

	/**
	 * Converts a package relative path into an absolute path.
	 *
	 * @param pkg the package
	 * @param path a path relative to the package
	 * @return an absolute path
	 */
	private static String getPathRelativeToPackage(JPackage pkg, String path) {
		return pkg.getName().replace('.', '/') + '/' + path;
	}

	/**
	 * Get default list of resource Locators, in the default order.
	 * 
	 * @param context
	 * @return an ordered array of Locator[]
	 */
	private static Locator[] getDefaultLocators(GeneratorContext genContext) {
		Locator[] locators = {
				NamedFileLocator.INSTANCE,
				new ResourceOracleLocator(genContext.getResourcesOracle()),
				new ClassLoaderLocator(Thread.currentThread().getContextClassLoader())};

		return locators;
	}

	private static Map<String, File> namedFiles = Maps.create();

	/**
	 * Wrapper interface around different strategies for loading resource data.
	 */
	private interface Locator {
		URL locate(String resourceName);
	}

	private static class ResourceOracleLocator implements Locator {
		private final Map<String, Resource> resourceMap;

		public ResourceOracleLocator(ResourceOracle oracle) {
			resourceMap = oracle.getResourceMap();
		}

		@SuppressWarnings("deprecation")
		public URL locate(String resourceName) {
			Resource r = resourceMap.get(resourceName);
			return (r == null) ? null : r.getURL();
		}
	}

	private static class ClassLoaderLocator implements Locator {
		private final ClassLoader classLoader;

		public ClassLoaderLocator(ClassLoader classLoader) {
			this.classLoader = classLoader;
		}

		public URL locate(String resourceName) {
			return classLoader.getResource(resourceName);
		}
	}

	/**
	 * A locator which will use files published via
	 * {@link ResourceGeneratorUtil#addNamedFile(String, File)}.
	 */
	private static class NamedFileLocator implements Locator {
		public static final NamedFileLocator INSTANCE = new NamedFileLocator();

		private NamedFileLocator() {
		}

		public URL locate(String resourceName) {
			File f = namedFiles.get(resourceName);
			if (f != null && f.isFile() && f.canRead()) {
				try {
					return f.toURI().toURL();
				} catch (MalformedURLException e) {
					throw new RuntimeException("Unable to make a URL for file "
							+ f.getName());
				}
			}
			return null;
		}
	}

}
