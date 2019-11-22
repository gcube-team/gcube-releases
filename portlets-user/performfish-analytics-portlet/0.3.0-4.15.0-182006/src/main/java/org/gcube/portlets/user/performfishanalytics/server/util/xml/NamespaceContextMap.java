/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.xml;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 7, 2013
 *
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


/**
 * The Class NamespaceContextMap.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2016
 */
public final class NamespaceContextMap implements NamespaceContext {

	private final Map<String, String> prefixMap;
	private final Map<String, Set<String>> nsMap;

	/**
	 * Constructor that takes a map of XML prefix-namespaceURI values. A
	 * defensive copy is made of the map. An IllegalArgumentException will be
	 * thrown if the map attempts to remap the standard prefixes defined in the
	 * NamespaceContext contract.
	 *
	 * @param prefixMappings
	 *            a map of prefix:namespaceURI values
	 */
	public NamespaceContextMap(Map<String, String> prefixMappings) {
		prefixMap = createPrefixMap(prefixMappings);
		nsMap = createNamespaceMap(prefixMap);
	}

	/**
	 * Convenience constructor.
	 *
	 * @param mappingPairs
	 *            pairs of prefix-namespaceURI values
	 */
	public NamespaceContextMap(String... mappingPairs) {
		this(toMap(mappingPairs));
	}

	/**
	 * To map.
	 *
	 * @param mappingPairs the mapping pairs
	 * @return the map
	 */
	private static Map<String, String> toMap(String... mappingPairs) {
		Map<String, String> prefixMappings = new HashMap<String, String>(
				mappingPairs.length / 2);
		for (int i = 0; i < mappingPairs.length; i++) {
			prefixMappings.put(mappingPairs[i], mappingPairs[++i]);
		}
		return prefixMappings;
	}

	/**
	 * Creates the prefix map.
	 *
	 * @param prefixMappings the prefix mappings
	 * @return the map
	 */
	private Map<String, String> createPrefixMap(
			Map<String, String> prefixMappings) {
		Map<String, String> prefixMap = new HashMap<String, String>(
				prefixMappings);
		addConstant(prefixMap, XMLConstants.XML_NS_PREFIX,
				XMLConstants.XML_NS_URI);
		addConstant(prefixMap, XMLConstants.XMLNS_ATTRIBUTE,
				XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		//return Collections.unmodifiableMap(prefixMap);
		
		return prefixMap;
	}

	/**
	 * Adds the constant.
	 *
	 * @param prefixMap the prefix map
	 * @param prefix the prefix
	 * @param nsURI the ns uri
	 */
	private void addConstant(Map<String, String> prefixMap, String prefix,
			String nsURI) {
		String previous = prefixMap.put(prefix, nsURI);
		if (previous != null && !previous.equals(nsURI)) {
			throw new IllegalArgumentException(prefix + " -> " + previous
					+ "; see NamespaceContext contract");
		}
	}

	/**
	 * Creates the namespace map.
	 *
	 * @param prefixMap the prefix map
	 * @return the map
	 */
	private Map<String, Set<String>> createNamespaceMap(
			Map<String, String> prefixMap) {
		Map<String, Set<String>> nsMap = new HashMap<String, Set<String>>();
		for (Map.Entry<String, String> entry : prefixMap.entrySet()) {
			String nsURI = entry.getValue();
			Set<String> prefixes = nsMap.get(nsURI);
			if (prefixes == null) {
				prefixes = new HashSet<String>();
				nsMap.put(nsURI, prefixes);
			}
			prefixes.add(entry.getKey());
		}
		for (Map.Entry<String, Set<String>> entry : nsMap.entrySet()) {
			Set<String> readOnly = Collections
					.unmodifiableSet(entry.getValue());
			entry.setValue(readOnly);
		}
		return nsMap;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		checkNotNull(prefix);
		String nsURI = prefixMap.get(prefix);
		return nsURI == null ? XMLConstants.NULL_NS_URI : nsURI;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespaceURI) {
		checkNotNull(namespaceURI);
		Set<String> set = nsMap.get(namespaceURI);
		return set == null ? null : set.iterator().next();
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		checkNotNull(namespaceURI);
		Set<String> set = nsMap.get(namespaceURI);
		return set.iterator();
	}

	/**
	 * Check not null.
	 *
	 * @param value the value
	 */
	private void checkNotNull(String value) {
		if (value == null) {
			throw new IllegalArgumentException("null");
		}
	}

	/**
	 * Gets the map.
	 *
	 * @return an unmodifiable map of the mappings in the form
	 *         prefix-namespaceURI
	 */
	public Map<String, String> getMap() {
		return prefixMap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceContextMap [prefixMap=");
		builder.append(prefixMap);
		builder.append(", nsMap=");
		builder.append(nsMap);
		builder.append("]");
		return builder.toString();
	}
}