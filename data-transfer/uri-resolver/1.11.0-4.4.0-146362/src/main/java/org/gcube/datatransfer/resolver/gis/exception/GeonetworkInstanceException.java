/**
 *
 */
package org.gcube.datatransfer.resolver.gis.exception;

import org.gcube.spatial.data.geonetwork.model.faults.AuthorizationException;


/**
 * The Class GeonetworkInstanceException.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 13, 2016
 */
public class GeonetworkInstanceException extends Exception {

	private static final long serialVersionUID = 8589705350737964325L;

	/**
	 * Instantiates a new geonetwork instance exception.
	 */
	public GeonetworkInstanceException() {
		super();
	}

    /**
     * Instantiates a new geonetwork instance exception.
     *
     * @param message the message
     */
    public GeonetworkInstanceException(String message) {
        super(message);
    }

	/**
	 * Instantiates a new geonetwork instance exception.
	 *
	 * @param e the e
	 */
	public GeonetworkInstanceException(AuthorizationException e) {
		super(e);
	}

}
