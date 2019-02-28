package org.gcube.common.gxhttp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.common.gxhttp.util.ContentUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test cases for {@link GXHTTPStringRequest}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GXHTTPStringRequestTest {

	private GXHTTPStringRequest request;
	
	public static String DEFAULT_TEST_SCOPE = "";
	
	static String DEFAULT_RM_URL = "";

	static String DEFAULT_RR_URL = "";

	
	private static boolean skipTest = false;

	static {
		Properties properties = new Properties();
		try (InputStream input = GXHTTPStringRequestTest.class.getClassLoader().getResourceAsStream("token.props")) {
			// load the properties file
			properties.load(input);
			DEFAULT_TEST_SCOPE = properties.getProperty("DEFAULT_SCOPE_TOKEN");
			if (DEFAULT_TEST_SCOPE.isEmpty())
				skipTest = true;
			DEFAULT_RM_URL = properties.getProperty("RM_URL");
			DEFAULT_RR_URL = properties.getProperty("RR_URL");
		} catch (IOException | NullPointerException e) {
			skipTest = true;
		}
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		setContext(DEFAULT_TEST_SCOPE);
	}

	public static void setContext(String token) throws ObjectNotFound, Exception {
		if (DEFAULT_TEST_SCOPE.isEmpty()) {
			skipTest = true;
			return;
		}
		SecurityTokenProvider.instance.set(token);
	}

	public static String getCurrentScope(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		return context;
	}

	@AfterClass
	public static void afterClass() throws Exception {
		SecurityTokenProvider.instance.reset();
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXHTTPStringRequest#newRequest(java.lang.String)}.
	 */
	@Before
	public void testNewRequest() {
		request  = GXHTTPStringRequest.newRequest(DEFAULT_RM_URL).from("GXRequestTest");
	}

	/**
	 * Test method for {@link org.gcube.common.gxhttp.request.GXHTTPStringRequest#post(java.lang.String)}.
	 */
	@Test
	public void testPostString() {
		if (skipTest)
			return;
		request.clear();
		String context ="{\"@class\":\"Context\",\"header\":{\"@class\":\"Header\",\"uuid\":\"6f86dc81-2f59-486b-8aa9-3ab5486313c4\",\"creator\":null,\"modifiedBy\":\"gxRestTest\",\"creationTime\":null,\"lastUpdateTime\":null},\"name\":\"gxTest\",\"parent\":null,\"children\":[]}";
		Map<String,String> queryParams = new WeakHashMap<>();
		queryParams.put("rrURL", DEFAULT_RR_URL);
		try {
			 HttpURLConnection response = request.path("gxrest")
					.header("Another header", "GXHTTPRequestTest")
					.queryParams(queryParams).post(context);
			assertTrue("Unexpected returned code.", response.getResponseCode() == Status.CREATED.getStatusCode());
			String body =  ContentUtils.toString(ContentUtils.toByteArray(response.getInputStream()));
			System.out.println("Returned string " + body);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to send a POST request");
		}	
		
	}

}
