/**
 *
 */
package org.gcube.datacatalogue.ckanutillibrary.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.gcube.datacatalogue.ckanutillibrary.server.patch.PatchedCkan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import eu.trentorise.opendata.jackan.CheckedCkanClient;
import eu.trentorise.opendata.jackan.exceptions.CkanException;
import eu.trentorise.opendata.jackan.exceptions.JackanException;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.fluent.Request;
import eu.trentorise.opendata.jackan.internal.org.apache.http.client.fluent.Response;
import eu.trentorise.opendata.jackan.model.CkanGroup;
import eu.trentorise.opendata.jackan.model.CkanOrganization;
import eu.trentorise.opendata.jackan.model.CkanResponse;


/**
 * The Class ExtendCkanClient.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jun 14, 2019
 */
public class ExtendCkanClient extends CheckedCkanClient  implements PatchedCkan{

	private static final Logger logger = LoggerFactory.getLogger(ExtendCkanClient.class);
	
	@Nullable
	private static ObjectMapper objectMapper;

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

	  /* (non-Javadoc)
     * @see eu.trentorise.opendata.jackan.CkanClient#getOrganization(java.lang.String)
     */
    public synchronized CkanOrganization getOrganization(String idOrName) {
       checkNotNull(idOrName, "Need a valid id or name!");
       logger.info("Patched read organization for id/name: {}", idOrName);
       return  getHttp(OrganizationResponse.class, "/api/3/action/organization_show", "id", idOrName,
                "include_datasets", "false", "include_users", "true").result;

    }
    

    /* (non-Javadoc)
     * @see eu.trentorise.opendata.jackan.CkanClient#getGroup(java.lang.String)
     */
    public synchronized CkanGroup getGroup(String idOrName) {
        checkNotNull(idOrName, "Need a valid id or name!");
        logger.info("Patched read group for id/name: {}", idOrName);
        return getHttp(GroupResponse.class, "/api/3/action/group_show", "id", idOrName, "include_datasets",
                "false", "include_users", "true").result; 
    }
    
    
    /**
     * Retrieves the Jackson object mapper for reading operations. Internally,
     * Object mapper is initialized at first call.
     *
     * @return the object mapper
     */
    static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            configureObjectMapper(objectMapper);
        }
        return objectMapper;
    }
    
    /* (non-Javadoc)
     * @see org.gcube.datacatalogue.ckanutillibrary.server.patch.PatchedCkan#getHttp(java.lang.Class, java.lang.String, java.lang.Object[])
     */
    public <T extends CkanResponse> T getHttp(Class<T> responseType, String path, Object... params) {

        checkNotNull(responseType);
        checkNotNull(path);
        String fullUrl = calcFullUrl(path, params);
        T ckanResponse;
        String returnedText;

        try {
        	
        	logger.debug("getting {}", fullUrl);
            Request request = Request.Get(fullUrl);
            configureRequest(request);
            Response response = request.execute();
            InputStream stream = response.returnResponse()
                                         .getEntity()
                                         .getContent();

            try (InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8)) {
                returnedText = CharStreams.toString(reader);
            }
            
        	logger.trace("returnedText {}", returnedText);
        } catch (Exception ex) {
            throw new CkanException("Error while performing GET. Request url was: " + fullUrl, this, ex);
        }
        try {
            ckanResponse = getObjectMapper().readValue(returnedText, responseType);
        } catch (Exception ex) {
            throw new CkanException(
                    "Couldn't interpret json returned by the server! Returned text was: " + returnedText, this, ex);
        }

        if (!ckanResponse.isSuccess()) {
            throwCkanException("Error while performing GET. Request url was: " + fullUrl, ckanResponse);
        }
        return ckanResponse;
    }


    /* (non-Javadoc)
     * @see org.gcube.datacatalogue.ckanutillibrary.server.patch.PatchedCkan#calcFullUrl(java.lang.String, java.lang.Object[])
     */
    public String calcFullUrl(String path, Object[] params) {
        checkNotNull(path);

        try {
            StringBuilder sb = new StringBuilder().append(catalogueURL)
                                                  .append(path);
            for (int i = 0; i < params.length; i += 2) {
                sb.append(i == 0 ? "?" : "&")
                  .append(URLEncoder.encode(params[i].toString(), "UTF-8"))
                  .append("=")
                  .append(URLEncoder.encode(params[i + 1].toString(), "UTF-8"));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new JackanException("Error while building url to perform GET! \n path: " + path + " \n params: "
                    + Arrays.toString(params), ex);
        }
    }

}
