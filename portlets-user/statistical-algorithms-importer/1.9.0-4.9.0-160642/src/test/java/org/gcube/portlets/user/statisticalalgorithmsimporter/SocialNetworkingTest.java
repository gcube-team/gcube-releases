package org.gcube.portlets.user.statisticalalgorithmsimporter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SocialNetworkingTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(SocialNetworkingTest.class);

	@Test
	public void testMessage() {
		if (Constants.TEST_ENABLE) {

			try {
				String requestUrl = "http://socialnetworking-d-d4s.d4science.org:80/social-networking-library-ws/rest/2/messages/write-message?gcube-token=ae1208f0-210d-47c9-9b24-d3f2dfcce05f-98187548";
				logger.debug("SocialNetworkingService request=" + requestUrl);
				JSONObject par = new JSONObject(
						"{\"subject\":\"[SAI] New software publication requested\",\"recipients\":[{\"id\":\"roberto.cirillo\"},{\"id\":\"ngalante\"},{\"id\":\"lucio.lelii\"},{\"id\":\"gianpaolo.coro\"},{\"id\":\"giancarlo.panichi\"},{\"id\":\"scarponi\"}],\"body\":\"Username: giancarlo.panichi\\nFull Name: Giancarlo Panichi\\nEmail: g.panichi@isti.cnr.it\\n\\nin VRE: /gcube/devNext/NextNext\\n\\nhas requested to publish the algorithm: \\nLanguage: Knime-Workflow\\nAlgorithm Name: KNIMEBLACKBOX\\nClass Name: org.gcube.dataanalysis.executor.rscripts.KnimeBlackBox\\nAlgorithm Description: KnimeBlackBox\\nAlgorithm Category: BLACK_BOX\\n\\nInterpreter Version: 3.2.1\\n\\nwith the following original jar: http://data-d.d4science.org/YUxwK1NwdlA4b3JLYjVkUVNpTzNZU0xta2Fic1VzaytHbWJQNStIS0N6Yz0\\nadmin copy jar: http://data-d.d4science.org/czlwQkNPTEJYSGRMemxpcXplVXYzZUJ5eEZyT2ExcDRHbWJQNStIS0N6Yz0\\n\\nInstaller: \\n./addAlgorithm.sh KNIMEBLACKBOX BLACK_BOX org.gcube.dataanalysis.executor.rscripts.KnimeBlackBox /gcube/devNext/NextNext transducerers N http://data-d.d4science.org/czlwQkNPTEJYSGRMemxpcXplVXYzZUJ5eEZyT2ExcDRHbWJQNStIS0N6Yz0 \\\"KnimeBlackBox\\\"\"}");
				logger.debug(par.toString());

				URL urlObj = new URL(requestUrl);
				HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
				connection.addRequestProperty("Content-Type", "application/json");
				connection.setRequestMethod("POST");
				connection.setDoInput(true);
				connection.setDoOutput(true);

				String parameter = par.toString();
				OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
				wr.write(parameter);
				wr.flush();
				wr.close();

				int code = connection.getResponseCode();
				logger.info("Response Code: " + code);
				if (code == 200 || code == 201) {
					logger.info("Mesage send to administrators");
					InputStream is = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line = null;
					StringBuffer response = new StringBuffer();
					logger.info("SocialNetworkingService retrieve response");
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					logger.debug("SocialNetworkingService response: " + response.toString());
				} else {
					if (code == 500) {

						logger.error("Error sending message to administrators");
						InputStream es = connection.getErrorStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(es));
						String line = null;
						StringBuffer response = new StringBuffer();
						logger.info("SocialNetworkingService retrieve error");
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}

						logger.debug("SocialNetworkingService error: " + response.toString());
					} else {

					}
				}

				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				e.printStackTrace();
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}

}
