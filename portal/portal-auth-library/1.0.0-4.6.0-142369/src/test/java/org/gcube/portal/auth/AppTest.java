package org.gcube.portal.auth;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;

import com.liferay.portal.kernel.json.JSONObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( AppTest.class );
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		System.out.println("getAuthorisedRedirectURLsFromIs ... ");
		try {
			ServiceEndpoint authorisedApp = AuthUtil.getAuthorisedApplicationInfoFromIsICClient(PortalContext.getConfiguration().getInfrastructureName(), "c96d4477-236c-4f98-ba7d-7897991ef412");
			List<String> authorisedRedirectURLs = AuthUtil.getAuthorisedRedirectURLsFromIs(authorisedApp);

			for (String red : authorisedRedirectURLs) {
				System.out.println(red);
			}
			
			String oauthendPoint = AuthUtil.getOAuthServiceEndPoint(PortalContext.getConfiguration().getInfrastructureName());
			System.out.println(oauthendPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
