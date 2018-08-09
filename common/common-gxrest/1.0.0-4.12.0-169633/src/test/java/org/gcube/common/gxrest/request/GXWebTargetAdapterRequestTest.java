package org.gcube.common.gxrest.request;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.gxrest.response.inbound.GXInboundResponse;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Test cases for {@link GXWebTargetAdapterRequest}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class GXWebTargetAdapterRequestTest {

	private GXWebTargetAdapterRequest request;
	
	public static String DEFAULT_TEST_SCOPE = "";
	
	static String DEFAULT_RM_URL = "";

	static String DEFAULT_RR_URL = "";

	private static boolean skipTest = false;
	
	static {
		Properties properties = new Properties();
		try (InputStream input = GXWebTargetAdapterRequest.class.getClassLoader().getResourceAsStream("token.props")) {
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
		if (!skipTest)
			setContext(DEFAULT_TEST_SCOPE);
	}

	public static void setContext(String token) throws ObjectNotFound, Exception {
		if (skipTest || DEFAULT_TEST_SCOPE.isEmpty()) {
			skipTest = true;
			return;
		} else {
			SecurityTokenProvider.instance.set(token);
		}
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
	 * Test method for {@link org.gcube.common.gxrest.request.GXHTTPRequest#newRequest(java.lang.String)}.
	 */
	@Before
	public void testNewRequest() {
		request  = GXWebTargetAdapterRequest.newRequest(DEFAULT_RM_URL).from("GXRequestTest");
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXWebTargetAdapterRequest#put()}.
	 */
	@Test
	public void testPut() {
		if (skipTest)
			return;
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXWebTargetAdapterRequest#delete()}.
	 */
	@Test
	public void testDelete() {
		if (skipTest)
			return;
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXWebTargetAdapterRequest#post()}.
	 */
	@Test
	public void testPost() {
		if (skipTest)
			return;
		String context ="{\"@class\":\"Context\",\"header\":{\"@class\":\"Header\",\"uuid\":\"6f86dc81-2f59-486b-8aa9-3ab5486313c4\",\"creator\":null,\"modifiedBy\":\"gxRestTest\",\"creationTime\":null,\"lastUpdateTime\":null},\"name\":\"gxTest\",\"parent\":null,\"children\":[]}";
		Map<String,Object[]> queryParams = new WeakHashMap<>();
		queryParams.put("rrURL", new String[]{DEFAULT_RR_URL});
		try {
			GXInboundResponse response = request.path("gxrest")
					.queryParams(queryParams).post(Entity.entity(context, MediaType.APPLICATION_JSON + ";charset=UTF-8"));
			assertTrue("Unexpected returned code.", response.hasCREATEDCode());
				if (response.hasGXError()) {
					if (response.hasException()) {
						try {
							throw response.getException();
						} catch (ClassNotFoundException e) {
							//that's OK, we can tolerate this
						} catch (Exception e) {
							e.printStackTrace();
							throw e;
						}
					}
				} else {
					if (response.hasCREATEDCode()) {
						System.out.println("Resource successfully created!");
						System.out.println("Returned message: " + response.getStreamedContentAsString());
					} else {
						System.out.println("Resource creation failed. Returned status:" + response.getHTTPCode());
						//if you want to use the original responser
						 Response jsResponse = response.getSource();
						//then consume the response 
					}
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to send a POST request");
		}	
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXWebTargetAdapterRequest#head()}.
	 */
	@Test
	public void testHead() {
		if (skipTest)
			return;
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.common.gxrest.request.GXWebTargetAdapterRequest#get()}.
	 */
	@Test
	public void testGet() {
		if (skipTest)
			return;
		//fail("Not yet implemented");
	}

}
