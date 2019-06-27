package org.gcube.portlets.user.simulfishgrowth.util;

import org.apache.http.HttpMessage;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;

/**
 * we are adding headers regarding gcube (eg scope) and headers regarding db
 * connection (eg credentials). We shall refactor when we are stable on how to
 * use the infrastructure
 * 
 * @author bluebridge
 *
 */
public class AddGCubeHeaders implements ConnectionUtils.AddHeadersListener {
	/*-	
		static private AddGCubeHeaders addGCubeHeaders;
	
		static public void setupGlobalAddGCubeHeaders(String scope, String token, AccessPoint dbAccessPoint) {
			if (addGCubeHeaders == null)
				addGCubeHeaders = new AddGCubeHeaders(scope, token, dbAccessPoint);
		}
	
		static public AddGCubeHeaders getGlobalAddGCubeHeaders() {
			if (addGCubeHeaders == null)
				throw new RuntimeException("global listener is null; you should setup first");
			return addGCubeHeaders;
		}
	*/

	String scope;
	String token;
	AccessPoint dbAccessPoint;

	public AddGCubeHeaders(String scope, String token, AccessPoint dbAccessPoint) {
		this.scope = scope;
		this.token = token;
		this.dbAccessPoint = dbAccessPoint;
	}

	public AddGCubeHeaders(String scope, String token) {
		this(scope, token, null);
	}

	@Override
	public void addHeaders(HttpMessage message) throws Exception {
		if (token != null) {
			message.addHeader("gcube-token", token);
		}
		if (scope != null) {
			message.addHeader("scope", scope);
		}
		if (dbAccessPoint != null) {
			message.addHeader("dbhost", dbAccessPoint.address());
			message.addHeader("dbname", dbAccessPoint.name());
			message.addHeader("dbuser", dbAccessPoint.username());
			message.addHeader("dbpass", StringEncrypter.getEncrypter().decrypt(dbAccessPoint.password()));
		}
	}
}
