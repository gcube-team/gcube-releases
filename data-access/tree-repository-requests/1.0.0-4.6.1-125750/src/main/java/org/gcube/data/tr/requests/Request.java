package org.gcube.data.tr.requests;

import org.w3c.dom.Element;

/**
 * Common interface for binding requests to the plugin via T-Binder services.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Request {

	/**
	 * Converts the request to an {@link Element}.
	 * @return the element.
	 * */
	Element toElement();
}

