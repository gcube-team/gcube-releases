package org.gcube.opensearch.opensearchlibrary.utils;

import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;

/**
 * A utility messenger class containing the class names of a {@link URLElementFactory} and a {@link QueryElementFactory}
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class FactoryClassNamePair {

	public final String urlElementFactoryClass;
	public final String queryElementFactoryClass;
	
	/**
	 * Creates a new instance
	 * 
	 * @param urlElementFactoryClass The class name of a {@link URLElementFactory}
	 * @param queryElementFactoryClass The class name of a {@link QueryElementFactory}
	 */
	public FactoryClassNamePair(String urlElementFactoryClass, String queryElementFactoryClass) {
		this.urlElementFactoryClass = urlElementFactoryClass;
		this.queryElementFactoryClass = queryElementFactoryClass;
	}
	
	public FactoryClassNamePair(String factoryClassNamePair) throws Exception {
		factoryClassNamePair = factoryClassNamePair.replaceAll("\\(", "");
		factoryClassNamePair = factoryClassNamePair.replaceAll("\\)", "");
		String[] pair = factoryClassNamePair.split(",");
		if(pair.length != 2)
			throw new Exception("Malformed factory pair entry");
		this.urlElementFactoryClass = pair[0].trim();
		this.queryElementFactoryClass = pair[1].trim();
	}
	
	@Override
	public String toString() {
		return "(" + urlElementFactoryClass + "," + queryElementFactoryClass + ")";
	}
}
