package org.gcube.common.gxhttp.request;

import java.net.HttpURLConnection;

import org.gcube.common.gxhttp.reference.GXHTTPRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common logic across GXHTTP requests.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class GXHTTPCommonRequest {
	
	protected static final Logger logger = LoggerFactory.getLogger(GXHTTPStringRequest.class);

	protected GXHTTPRequestBuilder builder = new GXHTTPRequestBuilder();
	
	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#put()
	 */
	public HttpURLConnection put() throws Exception {
		return builder.put();

	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#delete()
	 */
	public HttpURLConnection delete() throws Exception {
		return builder.delete();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#head()
	 */
	public HttpURLConnection head() throws Exception {
		return builder.head();

	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#get()
	 */
	public HttpURLConnection get() throws Exception {
		return builder.get();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#post()
	 */
	public HttpURLConnection post() throws Exception {
		return builder.post();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#trace()
	 */
	public HttpURLConnection trace() throws Exception {
		return builder.trace();

	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#patch()
	 */
	public HttpURLConnection patch() throws Exception {
		return builder.patch();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#options()
	 */
	public HttpURLConnection options() throws Exception {
		return builder.options();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#connect()
	 */
	public HttpURLConnection connect() throws Exception {
		return builder.connect();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#setSecurityToken(java.lang.String)
	 */
	public void setSecurityToken(String token) {
		builder.setSecurityToken(token);		
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.gxhttp.reference.GXHTTP#isExternalCall(boolean)
	 */
	public void isExternalCall(boolean ext) {
		builder.isExternalCall(ext);		
	}
	
	/**
	 * Clear up the request.
	 */
	public void clear() {
		builder.clear();		
	}
}
