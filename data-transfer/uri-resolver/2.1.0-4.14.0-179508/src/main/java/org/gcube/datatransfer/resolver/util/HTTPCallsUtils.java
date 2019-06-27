/**
 *
 */
package org.gcube.datatransfer.resolver.util;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;


/**
 * The Class HTTPCallsUtils.
 * copied by Geosolution
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 5, 2016
 */
public class HTTPCallsUtils {

    private static final Logger logger = Logger.getLogger(HTTPCallsUtils.class);
    private final String username;
    private final String pw;

    /**
     * This instance is shared among the various calls, so that the
     * state (mainly cookies) will be preserved.
     */
    private HttpClient client = new HttpClient();

    /**
     * Some apps may require application/xml, so you can set it to whatever is needed.
     */
    private String xmlContentType = "text/xml";

    private int lastHttpStatus;
    private boolean ignoreResponseContentOnSuccess = false;
	private String lastContentType;

    /**
     * Instantiates a new HTTP utils.
     */
    public HTTPCallsUtils() {
        this(null, null);
    }

    /**
     * Instantiates a new HTTP utils.
     *
     * @param userName the user name
     * @param password the password
     */
    public HTTPCallsUtils(String userName, String password) {
        this.username = userName;
        this.pw = password;
    }

    /**
     * Sets the xml content type.
     *
     * @param xmlContentType the new xml content type
     */
    public void setXmlContentType(String xmlContentType) {
        this.xmlContentType = xmlContentType;
    }

    /**
     * Gets the last http status.
     *
     * @return the last http status
     */
    public int getLastHttpStatus() {
        return lastHttpStatus;
    }

    /**
     * Checks if is ignore response content on success.
     *
     * @return true, if is ignore response content on success
     */
    public boolean isIgnoreResponseContentOnSuccess() {
        return ignoreResponseContentOnSuccess;
    }

    /**
     * Sets the ignore response content on success.
     *
     * @param ignoreResponseContentOnSuccess the new ignore response content on success
     */
    public void setIgnoreResponseContentOnSuccess(boolean ignoreResponseContentOnSuccess) {
        this.ignoreResponseContentOnSuccess = ignoreResponseContentOnSuccess;
    }


    /**
     * Performs an HTTP GET on the given URL.
     *
     * @param url       The URL where to connect to.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * @throws MalformedURLException the malformed url exception
     */
	public HttpResponse get(String url) throws MalformedURLException {

        GetMethod httpMethod = null;
		try {
            setAuth(client, url, username, pw);
			httpMethod = new GetMethod(url);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			lastHttpStatus = client.executeMethod(httpMethod);
			if(lastHttpStatus == HttpStatus.SC_OK) {
				setContentType(httpMethod);
                InputStream is = httpMethod.getResponseBodyAsStream();
				String response = IOUtils.toString(is);
				if(response.trim().length()==0) { // sometime gs rest fails
					logger.warn("ResponseBody is empty");
					return null;
				} else {
                    return new HttpResponse(HttpStatus.SC_OK, response);
                }
			} else {
				logger.info("("+lastHttpStatus+") " + HttpStatus.getStatusText(lastHttpStatus) + " -- " + url );
			}
		} catch (ConnectException e) {
			logger.info("Couldn't connect to ["+url+"]");
		} catch (IOException e) {
			logger.info("Error talking to ["+url+"]", e);
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }

		return new HttpResponse(lastHttpStatus, null);
	}

    /**
     * Show content type.
     *
     * @param method the new content type
     */
    private void setContentType(HttpMethod method) {
    	final Header contentTypeHeader=method.getResponseHeader("Content-Type");
    	lastContentType =contentTypeHeader == null ? null : contentTypeHeader.getValue();
    }

	/**
	 * Gets the last content type.
	 *
	 * @return the lastContentType
	 */
	public String getLastContentType() {
		return lastContentType;
	}

    //==========================================================================
    //=== PUT
    //==========================================================================

	/**
     * Gets the client.
     *
     * @return the client
     */
	public HttpClient getClient() {

		return client;
	}

	/**
     * PUTs a String representing an XML document to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The XML content to be sent as a String.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream putXml(String url, String content) {
        return put(url, content, xmlContentType);
    }

    /**
     * PUTs a File to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param file      The File to be sent.
     * @param contentType The content-type to advert in the PUT.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream put(String url, File file, String contentType) {
        return put(url, new FileRequestEntity(file, contentType));
    }

    /**
     * PUTs a String to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The content to be sent as a String.
     * @param contentType The content-type to advert in the PUT.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream put(String url, String content, String contentType) {
        try {
            return put(url, new StringRequestEntity(content, contentType, null));
        } catch (UnsupportedEncodingException ex) {
            logger.error("Cannot PUT " + url, ex);
            return null;
        }
    }

    /**
     * Performs a PUT to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream put(String url, RequestEntity requestEntity) {
        return send(new PutMethod(url), url, requestEntity);
    }

    //==========================================================================
    //=== POST
    //==========================================================================

    /**
     * POSTs a String representing an XML document to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The XML content to be sent as a String.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream postXml(String url, String content) {
        return post(url, content, xmlContentType);
    }

    /**
     * POSTs a Stream content representing an XML document to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The content to be sent as an InputStream.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream postXml(String url, InputStream content) {
        return post(url, content, xmlContentType);
    }

    /**
     * POSTs a File to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param file      The File to be sent.
     * @param contentType The content-type to advert in the POST.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream post(String url, File file, String contentType) {
        return post(url, new FileRequestEntity(file, contentType), null);
    }

    /**
     * POSTs a String to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The content to be sent as a String.
     * @param contentType The content-type to advert in the POST.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream post(String url, String content, String contentType) {
        try {
            return post(url, new StringRequestEntity(content, contentType, null), null);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Cannot POST " + url, ex);
            return null;
        }
    }

    /**
     * POSTs a Stream content to the given URL.
     *
     * @param url       The URL where to connect to.
     * @param content   The content to be sent as an InputStream.
     * @param contentType The content-type to advert in the POST.
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public  InputStream post(String url, InputStream content, String contentType) {
        return post(url, new InputStreamRequestEntity(content, contentType), null);
    }

    /**
     * POSTs a Stream content to the given URL.
     *
     * @param url the url
     * @param content   The content to be sent as an InputStream.
     * @param contentType The content-type to advert in the POST.
     * @param parameterMap the parameter map
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
	public InputStream post(String url, InputStream content, String contentType, Map<String, String[]> parameterMap) {
		 return post(url, new InputStreamRequestEntity(content, contentType), parameterMap);
	}


    /**
     * Performs a POST to the given URL.
     * <BR>Basic auth is used if both username and pw are not null.
     *
     * @param url       The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @param parameterMap the parameter map
     * @return          The HTTP response as a String if the HTTP response code was 200 (OK).
     * the HTTP response or <TT>null</TT> on errors.
     */
    public InputStream post(String url, RequestEntity requestEntity, Map<String, String[]> parameterMap) {
    	PostMethod postMethod = new PostMethod(url);
    	if(parameterMap!=null){
    		for(String stringParameterName : parameterMap.keySet()) {
	            // Iterate the values for each parameter name
	            String[] stringArrayParameterValues = parameterMap.get(stringParameterName);
	            for(String stringParamterValue : stringArrayParameterValues) {
	                // Create a NameValuePair and store in list
	                NameValuePair nameValuePair = new NameValuePair(stringParameterName, stringParamterValue);
	                logger.debug("Adding parameter: "+nameValuePair.toString());
	                postMethod.addParameter(nameValuePair);
	            }
    		}
    	}

        return send(postMethod, url, requestEntity);
    }

    //==========================================================================
    //=== HTTP requests
    //==========================================================================

    /**
     * Send an HTTP request (PUT or POST) to a server.
     * <BR>Basic auth is used if both username and pw are not null.
     * <P>
     * Only <UL>
     *  <LI>200: OK</LI>
     *  <LI>201: ACCEPTED</LI>
     *  <LI>202: CREATED</LI>
     * </UL> are accepted as successful codes; in these cases the response string will be returned.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestEntity the request entity
     * @return the InputStream body response or <TT>null</TT> on errors.
     */
    protected InputStream send(final EntityEnclosingMethod httpMethod, String url, RequestEntity requestEntity) {

        try {
            setAuth(client, url, username, pw);

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            if(requestEntity != null)
                httpMethod.setRequestEntity(requestEntity);

            /*if(logger.isDebugEnabled()){
            	if( httpMethod.getRequestEntity() instanceof InputStreamRequestEntity){
            		InputStreamRequestEntity is = (InputStreamRequestEntity) httpMethod.getRequestEntity();
            		is.getContent();
            	}
            	logger.trace(requestEntity);
            }*/

			lastHttpStatus = client.executeMethod(httpMethod);

			switch(lastHttpStatus) {
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_CREATED:
				case HttpURLConnection.HTTP_ACCEPTED:
                    if(logger.isDebugEnabled())
                        logger.debug("HTTP "+ httpMethod.getStatusText() + " <-- " + url);
                    if(ignoreResponseContentOnSuccess)
                        return null;

					setContentType(httpMethod);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					IOUtils.copy(httpMethod.getResponseBodyAsStream(), out);
					return new ByteArrayInputStream(out.toByteArray());
				default:
					String badresponse = IOUtils.toString(httpMethod.getResponseBodyAsStream());
                    String message = getGeoNetworkErrorMessage(badresponse);
                    setContentType(httpMethod);
					logger.warn("Bad response: "+lastHttpStatus
                            + " " + httpMethod.getStatusText()
							+ " -- " + httpMethod.getName()
                            + " " +url
                            + " : "
                            + message
							);
                    if(logger.isDebugEnabled())
                        logger.debug("Response is:\n"+badresponse);
					return null;
			}
		} catch (ConnectException e) {
			logger.info("Couldn't connect to ["+url+"]");
    		return null;
        } catch (IOException e) {
            logger.error("Error talking to " + url + " : " + e.getLocalizedMessage());
    		return null;
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }
    }

    /**
     * Send an HTTP request (PUT or POST) to a server.
     * <BR>Basic auth is used if both username and pw are not null.
     * <P>
     * Only <UL>
     *  <LI>200: OK</LI>
     *  <LI>201: ACCEPTED</LI>
     *  <LI>202: CREATED</LI>
     * </UL> are accepted as successful codes; in these cases the response string will be returned.
     *
     * @param httpMethod the http method
     * @param url the url
     * @param requestEntity the request entity
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    protected InputStream sendPost(final EntityEnclosingMethod httpMethod, String url, RequestEntity requestEntity) {

        try {
            setAuth(client, url, username, pw);

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            if(requestEntity != null)
                httpMethod.setRequestEntity(requestEntity);

			lastHttpStatus = client.executeMethod(httpMethod);

			switch(lastHttpStatus) {
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_CREATED:
				case HttpURLConnection.HTTP_ACCEPTED:
                    if(logger.isDebugEnabled())
                        logger.debug("HTTP "+ httpMethod.getStatusText() + " <-- " + url);
                    setContentType(httpMethod);
					return httpMethod.getResponseBodyAsStream();
				default:
					logger.warn("Bad response: "+lastHttpStatus
                            + " " + httpMethod.getStatusText()
							+ " -- " + httpMethod.getName()
                            + " " +url
							);
					setContentType(httpMethod);
                    return httpMethod.getResponseBodyAsStream();
			}
		} catch (ConnectException e) {
			logger.info("Couldn't connect to ["+url+"]");
    		return null;
        } catch (IOException e) {
            logger.error("Error talking to " + url + " : " + e.getLocalizedMessage());
    		return null;
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }
    }

	/**
	 * Delete.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public  boolean delete(String url) {

    	DeleteMethod httpMethod = null;

		try {
//            HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
            httpMethod = new DeleteMethod(url);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			lastHttpStatus = client.executeMethod(httpMethod);
			String response = "";
			if(lastHttpStatus == HttpStatus.SC_OK) {
                if(logger.isDebugEnabled())
                    logger.debug("("+lastHttpStatus+") " + httpMethod.getStatusText() + " -- " + url );

                if( ! ignoreResponseContentOnSuccess) {
                    InputStream is = httpMethod.getResponseBodyAsStream();
                    response = IOUtils.toString(is);
                    if(response.trim().equals("")) {
                        if(logger.isDebugEnabled())
                            logger.debug("ResponseBody is empty (this may be not an error since we just performed a DELETE call)");
                    }
                }
                setContentType(httpMethod);
				return true;
			} else {
				setContentType(httpMethod);
				logger.info("("+lastHttpStatus+") " + httpMethod.getStatusText() + " -- " + url );
				logger.info("Response: '"+response+"'" );
			}
		} catch (ConnectException e) {
			logger.info("Couldn't connect to ["+url+"]");
		} catch (IOException e) {
			logger.info("Error talking to ["+url+"]", e);
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }

		return false;
	}

    /**
     * Http ping.
     *
     * @param url the url
     * @return true if the server response was an HTTP_OK
     */
	public  boolean httpPing(String url) {

        GetMethod httpMethod = null;

		try {
//			HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
			httpMethod = new GetMethod(url);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
			lastHttpStatus = client.executeMethod(httpMethod);
            if(lastHttpStatus != HttpStatus.SC_OK) {
                logger.warn("PING failed at '"+url+"': ("+lastHttpStatus+") " + httpMethod.getStatusText());
                return false;
            } else {
                return true;
            }

		} catch (ConnectException e) {
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }
	}

    /**
     * Used to query for REST resources.
     *
     * @param url The URL of the REST resource to query about.
     * @return true on 200, false on 404.
     */
	public  boolean exists(String url) {

        GetMethod httpMethod = null;

		try {
//			HttpClient client = new HttpClient();
            setAuth(client, url, username, pw);
			httpMethod = new GetMethod(url);
			client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
			lastHttpStatus = client.executeMethod(httpMethod);
            switch(lastHttpStatus) {
                case HttpStatus.SC_OK:
                    return true;
                case HttpStatus.SC_NOT_FOUND:
                    return false;
                default:
                    throw new RuntimeException("Unhandled response status at '"+url+"': ("+lastHttpStatus+") " + httpMethod.getStatusText());
            }
		} catch (ConnectException e) {
            throw new RuntimeException(e);
		} catch (IOException e) {
            throw new RuntimeException(e);
		} finally {
            if(httpMethod != null)
                httpMethod.releaseConnection();
        }
	}


    /**
     * Sets the auth.
     *
     * @param client the client
     * @param url the url
     * @param username the username
     * @param pw the pw
     * @throws MalformedURLException the malformed url exception
     */
    private static void setAuth(HttpClient client, String url, String username, String pw) throws MalformedURLException {
        URL u = new URL(url);
        if(username != null && pw != null) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, pw);
            client.getState().setCredentials(new AuthScope(u.getHost(), u.getPort()), defaultcreds);
            client.getParams().setAuthenticationPreemptive(true); // GS2 by default always requires authentication
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Not setting credentials to access to " + url);
            }
        }
    }

    /**
     * Gets the geo network error message.
     *
     * @param msg the msg
     * @return the geo network error message
     */
    protected static String getGeoNetworkErrorMessage(String msg) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document error = builder.build(new StringReader(msg));
            return error.getRootElement().getChildText("message");
        } catch (Exception ex) {
            return "-";
        }
    }

    /**
     * Gets the geo network error message.
     *
     * @param msg the msg
     * @return the geo network error message
     */
    protected static String getGeoNetworkErrorMessage(InputStream msg) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document error = builder.build(msg);
            return error.getRootElement().getChildText("message");
        } catch (Exception ex) {
            return "-";
        }
    }


    /**
     * The Class HttpResponse.
     *
     * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
     * Sep 7, 2016
     */
    public class HttpResponse {
    	int status;
    	String response;

		/**
		 * Instantiates a new http response.
		 */
		public HttpResponse() {

			// TODO Auto-generated constructor stub
		}

		/**
		 * Instantiates a new http response.
		 *
		 * @param status the status
		 * @param response the response
		 */
		public HttpResponse(int status, String response) {

			super();
			this.status = status;
			this.response = response;
		}


		/**
		 * Gets the status.
		 *
		 * @return the status
		 */
		public int getStatus() {

			return status;
		}


		/**
		 * Gets the response.
		 *
		 * @return the response
		 */
		public String getResponse() {

			return response;
		}


		/**
		 * Sets the status.
		 *
		 * @param status the status to set
		 */
		public void setStatus(int status) {

			this.status = status;
		}


		/**
		 * Sets the response.
		 *
		 * @param response the response to set
		 */
		public void setResponse(String response) {

			this.response = response;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			StringBuilder builder = new StringBuilder();
			builder.append("HttpResponse [status=");
			builder.append(status);
			builder.append(", response=");
			builder.append(response);
			builder.append("]");
			return builder.toString();
		}

    }

}
