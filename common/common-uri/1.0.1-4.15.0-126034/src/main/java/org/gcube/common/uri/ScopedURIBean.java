package org.gcube.common.uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Models the information in scoped URIs.
 * 
 * @author Fabio Simeoni
 * 
 * @see ScopedMint
 *
 */
public class ScopedURIBean {

	private final static Pattern scopePattern = Pattern.compile("scope=(.*)");
	
	private final URI uri;
	private final List<String> elements;
	private final String scope;
	
	/**
	 * Creates an instance for a given URI.
	 * @param uri the URI
	 */
	public ScopedURIBean(URI uri) throws IllegalArgumentException {
		
		this.uri=uri;
		
		String path = uri.getPath();
		List<String> temporary = Arrays.asList(path.split("/")); 
		elements = temporary.subList(1, temporary.size());
		
		String query = uri.getQuery();
		if (query==null)
			throw new IllegalArgumentException(uri+" is unscoped");
		
		Matcher m = scopePattern.matcher(query);
		
		if (m.matches())
			scope=m.group(1);
		else
			throw new IllegalArgumentException(uri+" is unscoped");
	}
	
	/**
	 * Returns the path elements of the URI.
	 * @return the elements
	 */
	public List<String> elements() {
		return new ArrayList<String>(elements);
	}
	
	/**
	 * Returns the scope of the URI.
	 * @return the scope
	 */
	public String scope() {
		return scope;
	}
	
	/**
	 * Returns the URI as a {@link URI}.
	 * @return the URI
	 */
	public URI uri() {
		return uri;
	}
	
	@Override
	public String toString() {
		return uri.toString();
	}
}
