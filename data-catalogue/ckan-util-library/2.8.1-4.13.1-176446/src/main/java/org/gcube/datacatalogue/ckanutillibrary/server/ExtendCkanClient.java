/**
 *
 */
package org.gcube.datacatalogue.ckanutillibrary.server;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.fluent.Request;


/**
 * The Class ExtendCkanClient.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 27, 2018
 */
public class ExtendCkanClient extends CheckedCkanClient{

	private static final Logger logger = LoggerFactory.getLogger(ExtendCkanClient.class);

	private String catalogueURL;
	private String ckanToken;
    private int extendTimeout = 120000; //in milliseconds


	/**
	 * Instantiates a new extend ckan client.
	 *
	 * @param catalogueURL the catalogue url
	 */
	public ExtendCkanClient(String catalogueURL) {
		super(catalogueURL);
		this.catalogueURL = catalogueURL;
		
	}

    /**
     * Instantiates a new extend ckan client.
     *
     * @param catalogueURL the catalogue url
     * @param ckanToken the ckan token
     */
    public ExtendCkanClient(String catalogueURL, @Nullable String ckanToken) {
        super(catalogueURL, ckanToken);
        this.catalogueURL = catalogueURL;
        this.ckanToken = ckanToken;
    }

    /**
     * Instantiates a new extend ckan client.
     *
     * @param catalogueURL the catalogue url
     * @param ckanToken the ckan token
     * @param timeout the timeout
     */
    public ExtendCkanClient(String catalogueURL, @Nullable String ckanToken, int timeout) {
        super(catalogueURL, ckanToken);
        this.catalogueURL = catalogueURL;
        this.ckanToken = ckanToken;
        this.extendTimeout = timeout;
    }


    /**
     * Configures the request. Should work both for GETs and POSTs.
     *
     * @param request the request
     * @return the request
     */
    @Override
    protected Request configureRequest(Request request) {
        request = super.configureRequest(request);
        
        logger.debug("Setting timeout to {}", extendTimeout);
        
        request.socketTimeout(this.extendTimeout)
               .connectTimeout(this.extendTimeout);

        return request;
    }

	/**
	 * Gets the catalogue url.
	 *
	 * @return the catalogueURL
	 */
	public String getCatalogueURL() {

		return catalogueURL;
	}


	/**
	 * Gets the ckan token.
	 *
	 * @return the ckanToken
	 */
	@Override
	public String getCkanToken() {

		return ckanToken;
	}

	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	public int getTimeout() {
		return extendTimeout;
	}
	
	


}
