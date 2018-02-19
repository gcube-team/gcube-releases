package org.gcube.portlets.user.tokengenerator.client;

import static org.gcube.common.authorization.client.Constants.authorizationService;
/**
 * Test class
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class JTests {

	//@Test
	public void getToken() throws Exception{

		String username  = "costantino.perciante";
		String context = "/gcube/devNext/NextNext";

		String token = authorizationService().resolveTokenByUserAndContext(username, context);
		System.out.println(token);

	}
}
