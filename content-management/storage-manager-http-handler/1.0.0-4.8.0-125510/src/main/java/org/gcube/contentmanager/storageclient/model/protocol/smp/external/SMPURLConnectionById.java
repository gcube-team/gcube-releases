package org.gcube.contentmanager.storageclient.model.protocol.smp.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPConnection;
import org.gcube.contentmanager.storageclient.model.protocol.smp.external.SMPURLConnectionById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is invoked by the platform with a URL of the smp protocol with new format (>= 2.2.0 version) .
* @author Roberto Cirillo (ISTI-CNR)
 *
 * Example: smp://data.gcube.org?uhdnfounhcfnshfnrhbvyaeegytf6dfawiuawgcyg
 */
public class SMPURLConnectionById extends SMPConnection {

	private Logger logger= LoggerFactory.getLogger(SMPURLConnectionById.class);
	private String serviceClass="Storage-manager";
	private String serviceName="resolver-uri";
	private String owner="storage-manager-http-handler";
	private URLConnection urlConnection;
	
	/**
	 * Constructs a new instance for a given <code>sm</code> URL.
	 * @param url the URL.
	 */
	public SMPURLConnectionById(URL url) {
		super(url);
	}
	
	public SMPURLConnectionById() {
		super(null);
	}
	
	public URLConnection init(URL url){
		URL httpUrl=null;
		try {
			httpUrl = translate(url);
			
		} catch (IOException e) {
			logger.error("problem to translate the following url: "+url);
			e.printStackTrace();
		}
		this.url=httpUrl;
		try {
			return urlConnection=httpUrl.openConnection();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
//		return new SMPURLConnectionById(httpUrl);
	}
	
	private URL translate(URL url) throws IOException {
		logger.debug("translating: "+url);
		String urlString=url.toString().replace("smp://", "http://");
		String baseUrl="http://"+url.getHost()+"/";
		logger.debug("base Url extracted is: "+baseUrl);
		int index=urlString.lastIndexOf(".org/");
		String params = urlString.substring(baseUrl.length());
		logger.debug("get params: "+baseUrl+" "+params);
		//encode params
		params=Base64.encodeBase64URLSafeString(params.getBytes("UTF-8"));
		// merge string
		urlString=baseUrl+params;
		logger.info("uri translated in http url: "+urlString);
		return new URL(urlString);
	}

	/**{@inheritDoc}
	 * external handler implementation
	 * */
	@Override 
	public synchronized InputStream getInputStream() throws IOException {
		return urlConnection.getInputStream();
					
	}

	@Override
	protected InputStream storageClient(String url) throws Exception {
		logger.error("bad method invoked");
		return null;
	}
	
	

}
