/**
 *
 */
package org.gcube.datatransfer.resolver.gis.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


/**
 * The Class NamespaceCsw.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class NamespaceCsw extends NamespaceISO19139 implements NamespaceContext {

	private Map<String, String> mapPrefix;


    /**
     * Instantiates a new namespace csw.
     */
    public NamespaceCsw() {
        mapPrefix = new HashMap<String, String>();
        mapPrefix.put(getPrefixGMD(), getNamespaceGMD());
        mapPrefix.put(getPrefixCSW(), getNamespaceCSW());
        mapPrefix.put(getPrefixOWS(), getNamespaceOWS());
        mapPrefix.put(getPrefixDC(), getNamespaceDC());
        mapPrefix.put(getPrefixGCO(), getNamespaceGCO());
        mapPrefix.put(getPrefixXLINK(), getNamespaceXLINK());
        mapPrefix.put(getPrefixSRV(), getNamespaceSRV());
        mapPrefix.put(getPrefixXSI(), getNamespaceXSI());
        mapPrefix.put(getPrefixGML(), getNamespaceGML());
        mapPrefix.put(getPrefixGTS(), getNamespaceGTS());
        mapPrefix.put(getPrefixGEONET(), getNamespaceGEONET());
        mapPrefix.put(getPrefixDCT(), getNamespaceDCT());
    };

    /**
     * Returns an iterator to every prefix in this namespace.
     *
     * @return the prefix iterator
     */
    public Iterator<String> getPrefixIterator() {
        return mapPrefix.keySet().iterator();
    }


    /* (non-Javadoc)
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("No prefix provided!");

        if (mapPrefix.containsKey(prefix))
            return mapPrefix.get(prefix);
        else
            return XMLConstants.NULL_NS_URI;

    }

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespaceURI) {

		for (String prefix : mapPrefix.keySet()) {
			String nu = mapPrefix.get(prefix);
			if(nu.compareTo(namespaceURI)==0)
				return prefix;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {

		return mapPrefix.keySet().iterator();
	}
}
