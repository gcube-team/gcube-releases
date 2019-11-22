package org.gcube.common.geoserverinterface.geonetwork.csw;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceCswResolver extends AbstractNamespacesISO19139 implements NamespaceContext {

	private Map<String, String> mapPrefix;

	/*public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("No prefix provided!");
		} else if (prefix.equals("csw")) {
			return namespaceCsw;
		} else if (prefix.equals("dc")) {
			return namespaceDC;
		} else if (prefix.equals("ows")) {
			return namespaceOWS;
		} else {
			return XMLConstants.NULL_NS_URI;
		}
	}

	public String getPrefix(String namespaceURI) {
		// Not needed in this context.
		return null;
	}

	public Iterator getPrefixes(String namespaceURI) {
		// Not needed in this context.
		return null;
	}*/
	
    public NamespaceCswResolver() {
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
     * Returns an iterator to every prefix in this namespace
     * @return
     */
    public Iterator<String> getPrefixIterator() {
        return mapPrefix.keySet().iterator();
    }

    /**
     * This method returns the uri for all prefixes needed.
     * @param prefix
     * @return uri
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("No prefix provided!");

        if (mapPrefix.containsKey(prefix))
            return mapPrefix.get(prefix);
        else
            return XMLConstants.NULL_NS_URI;

    }

    public String getPrefix(String namespaceURI) {
        // Not needed in this context.
        return null;
    }

    public Iterator<String> getPrefixes(String namespaceURI) {
        // Not needed in this context.
        return null;
    }

}
