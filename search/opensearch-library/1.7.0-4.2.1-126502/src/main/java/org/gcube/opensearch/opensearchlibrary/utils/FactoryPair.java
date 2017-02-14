package org.gcube.opensearch.opensearchlibrary.utils;

import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;

/**
 * A utility messenger class containing references to {@link URLElementFactory} and a {@link QueryElementFactory}
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class FactoryPair {
	public final URLElementFactory urlElFactory;
	public final QueryElementFactory queryElFactory;
	
	/**
	 * Creates a new instance
	 * 
	 * @param urlElFactory A reference to a {@link URLElementFactory}
	 * @param queryElFactory A reference to a {@link QueryElementFactory}
	 */
	public FactoryPair(URLElementFactory urlElFactory, QueryElementFactory queryElFactory) {
		this.urlElFactory = urlElFactory;
		this.queryElFactory = queryElFactory;
	}
}
