package org.gcube.portlets.user.dataminermanager;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.portlets.user.dataminermanager.server.is.InformationSystemUtils;
import org.gcube.portlets.user.dataminermanager.server.util.ServiceCredentials;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TestDataMinerService extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(TestDataMinerService.class);

	private String wpsToken;
	private String wpsUser;
	private String wpsProcessingServlet;
	private String scope;

	private void retrieveServicesInfo() throws Exception {
	
			logger.info("Use test user");

			// Remove comment for Test
			wpsUser = Constants.DEFAULT_USER;
			scope = Constants.DEFAULT_SCOPE;
			
			ServiceCredentials serviceCredentials=new ServiceCredentials();
			serviceCredentials.setUserName(wpsUser);
			serviceCredentials.setScope(scope);
			
			List<String> userRoles = new ArrayList<>();
			userRoles.add(Constants.DEFAULT_ROLE);
			/*
			 * if (aslSession.getUsername().compareTo("lucio.lelii") == 0)
			 * userRoles.add("VRE-Manager");
			 */
			
			try {
				wpsToken = authorizationService().generateUserToken(
						new UserInfo(serviceCredentials.getUserName(), userRoles),
						serviceCredentials.getScope());
			} catch (Exception e) {
				logger.error("Error generating the token for test: "
						+ e.getLocalizedMessage());
				e.printStackTrace();
				throw new Exception("Error generating the token for test: "
						+ e.getLocalizedMessage());
			}
			serviceCredentials.setToken(wpsToken);

			List<String> serviceAddress = InformationSystemUtils
					.retrieveServiceAddress(
							Constants.DATAMINER_SERVICE_CATEGORY,
							Constants.DATA_MINER_SERVICE_NAME, serviceCredentials.getScope());
			logger.debug("Service Address retrieved:" + serviceAddress);
			if (serviceAddress == null || serviceAddress.size() < 1) {
				logger.error("No DataMiner service address available!");
				throw new Exception("No DataMiner service address available!");
			} else {
				logger.info("DataMiner service address found: "
						+ serviceAddress.get(0));
				wpsProcessingServlet = serviceAddress.get(0);

			}
	
	}

	public void testExecuteProcess() {
		if(Constants.TEST_ENABLE){
			executeProcess();
		} else {
			assertTrue(true);
			
		}
	}
	
	private void executeProcess(){
		
		try {
			
			retrieveServicesInfo();
			
			String urlString=wpsProcessingServlet;

			logger.debug("RetrieveDataViaPost(): " + urlString);
			String authString = wpsUser + ":" + wpsToken;
			logger.info("auth string: " + authString);
			byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
			String encoded = new String(authEncBytes);
			logger.info("Base64 encoded auth string: " + encoded);

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Basic " + encoded);
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setDoOutput(true);
			OutputStream output = conn.getOutputStream();
			Path currentPath=Paths.get(".");
			logger.info("CurrentPath:"+currentPath.toAbsolutePath().toString());
			
			Files.copy(Paths.get("TestDataMinerServiceDBSCAN.xml"), output);

			InputStream input = null;
			String encoding = conn.getContentEncoding();
			if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
				input = new GZIPInputStream(conn.getInputStream());
			} else {
				input = conn.getInputStream();
			}

			BufferedReader r = new BufferedReader(new InputStreamReader(input,
					StandardCharsets.UTF_8));

			logger.info("Response:");
			String str = null;
			while ((str = r.readLine()) != null) {
				logger.info(str);
			}
			
			logger.info("End Response!");

			assertTrue(true);
			
		} catch (Exception e) {
			logger.debug(e.getLocalizedMessage());
			e.printStackTrace();
			fail(e.getLocalizedMessage());
			
		}

	}

}
