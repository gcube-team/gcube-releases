package org.gcube.portlets.user.performfish.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author F. Sinibaldi CNR-ISTI
 * @author M. Assante CNR-ISTI
 */
public class ImportedBatchChecker {
	private static com.liferay.portal.kernel.log.Log _log = LogFactoryUtil.getLog(ImportedBatchChecker.class);
	
	private static String ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_NAME = "perform-service";
	private static String ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_CLASS = "Application";
	private static String CONFIGURATION_RESOURCE_SECONDARY_TYPE="ApplicationConfiguration";
	private static String CONFIGURATION_RESOURCE_NAME="PerformFishConfiguration";
	private static final int N_BATCHES_THRESHOLD_FALLBACK = 4;


	public static boolean checkAnalysisAvailability(long farmid,String batchType, String context) throws JSONException, IOException, KeyManagementException, NoSuchAlgorithmException, NumberFormatException, XPathExpressionException {
		fixUntrustCertificate();
		String servicecall=formServiceCall(batchType,farmid, context);
		JSONObject jsonObject=readFromURL(servicecall);
		String csvUrl=jsonObject.getString("BatchesTable_internal");
		long csvLines=readCSVLines(csvUrl);
		return csvLines-1>getThresholdFromIS();
	}


	private static int getThresholdFromIS() throws NumberFormatException, XPathExpressionException {
		try {
			SimpleQuery query = queryFor(GenericResource.class);
			query.addCondition("$resource/Profile/SecondaryType/text() eq '"+ CONFIGURATION_RESOURCE_SECONDARY_TYPE+"'");
			query.addCondition("$resource/Profile/Name/text() eq '"+ CONFIGURATION_RESOURCE_NAME+"'");
			DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
			String xmlConfiguration=client.submit(query).get(0).profile().bodyAsString();

			InputSource source = new InputSource(new StringReader(xmlConfiguration));
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();

			return Integer.parseInt(xpath.evaluate("//config/analysis_threshold/text()", source)); 
		}
		catch (Exception e) {
			_log.error("There was an error contacting the IS for reading the Batch threshold, returning fallback value="+N_BATCHES_THRESHOLD_FALLBACK, e);
			return N_BATCHES_THRESHOLD_FALLBACK;
		}

	}

	private static String formServiceCall(String batchtype, long farmid, String context) {
		return new StringBuilder(Utils.getAnalyticalToolkitEndpoint(context))
				.append("/performance")
				.append("?")
				.append("batch_type=").append(batchtype)
				.append("&gcube-token="+SecurityTokenProvider.instance.get())
				.append("&farmid=").append(farmid).toString();
	}


	private static long readCSVLines(String csvUrl) throws IOException {
		URL url= new URL(null, csvUrl, new sun.net.www.protocol.https.Handler());
		HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( true );
		conn.setRequestMethod( "GET" );

		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		int lines = 0;
		while (reader.readLine() != null) lines++;
		reader.close();
		return lines;
	}

	private static JSONObject readFromURL(String request) throws IOException, JSONException {		
		URL url= new URL(null, request, new sun.net.www.protocol.https.Handler());
		HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();           
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "GET" );
		conn.setRequestProperty( "Content-Type", "application/json"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setUseCaches( false );

		InputStream in = conn.getInputStream();
		String encoding = conn.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		String body = IOUtils.toString(in, encoding);

		return new JSONObject(body);
	}


	private static void fixUntrustCertificate() throws KeyManagementException, NoSuchAlgorithmException{
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
							throws CertificateException {			
					}

				}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// set the  allTrusting verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}



	public static List<GCoreEndpoint> getAnalyticalToolkitServiceInstance() throws Exception  {
		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+ ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_CLASS +"'");
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+ ANALYTICAL_TOOLKIT_SERVICE_GCORE_ENDPOINT_NAME +"'");
		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> toReturn = client.submit(query);
		return toReturn;
	}


	//	public static void main(String[] args) throws JSONException, IOException, KeyManagementException, NoSuchAlgorithmException, NumberFormatException, XPathExpressionException {
	//		//SecurityTokenProvider.instance.set("****");
	//		ScopeProvider.instance.set("/gcube/preprod/preVRE");
	//		String batchtype="GROW_OUT_INDIVIDUAL";
	//		long farmid=12682549;
	//		
	//		System.out.println("IS ANALYSIS AVAILABLE : "+checkAnalysisAvailability(farmid, batchtype));
	//	}
}
