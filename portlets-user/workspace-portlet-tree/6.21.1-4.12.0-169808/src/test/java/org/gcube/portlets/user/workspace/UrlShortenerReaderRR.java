/**
 *
 */
package org.gcube.portlets.user.workspace;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 26, 2013
 *
 */
public class UrlShortenerReaderRR {

	/**
	 *
	 */
	protected static final String KEY = "key";

	//Base Address
	protected String uri = "";

    //Query URL parameter
	protected String keyValue = "";

	private String uriRequest = "";

	public static Logger log = Logger.getLogger(UrlShortenerReaderRR.class);


	/**
	 * @throws Exception
	 *
	 */
	public UrlShortenerReaderRR() throws Exception {

		ScopeProvider.instance.set("/gcube");

		XQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Name/text() eq 'HTTP-URL-Shortener'").setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<AccessPoint> client = clientFor(AccessPoint.class);

		List<AccessPoint> endpoints = client.submit(query);

		if (endpoints.size() == 0)
			throw new Exception("No Shortener available");

		//Base Address
//	    System.out.println(endpoints.get(0).address());

	    uri = endpoints.get(0)!=null?endpoints.get(0).address():"";

	    if(endpoints.get(0)!=null){

	    	keyValue = endpoints.get(0).propertyMap()!=null?endpoints.get(0).propertyMap().get(KEY).value():"";
	    }

	    uriRequest  = uri+"?"+KEY+"="+keyValue;

	    System.out.println(uriRequest);

	     //Query URL parameter
//		System.out.println(endpoints.get(0).propertyMap().get("parameter").value());

	}

	/**
	 *
	 * @return Base Address of Uri Resolver
	 */
	public String getUri() {
		return uri;
	}


	/**
	 *
	 * @return Query URL parameter of Uri Resolver
	 */
	public String getParameter() {
		return keyValue;
	}



	public static void main(String[] args) throws Exception {
		log.trace(new UrlShortenerReaderRR());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UrlShortenerReaderRR [uri=");
		builder.append(uri);
		builder.append(", keyValue=");
		builder.append(keyValue);
		builder.append(", uriRequest=");
		builder.append(uriRequest);
		builder.append("]");
		return builder.toString();
	}



}
