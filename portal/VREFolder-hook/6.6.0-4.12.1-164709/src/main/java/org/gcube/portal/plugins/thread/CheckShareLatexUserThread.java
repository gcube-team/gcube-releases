package org.gcube.portal.plugins.thread;
import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class CheckShareLatexUserThread implements Runnable {

	private static Log _log = LogFactoryUtil.getLog(CheckShareLatexUserThread.class);

	private static final String SERVICE_NAME = "ShareLatex";
	private static final String SERVICE_CLASS = "DataAccess";
	private static final String ENTRY_NAME = "org.gcube.data.access.sharelatex.connector.Connector";
	private static final String USER_AGENT = "Mozilla/5.0";

	private String username;
	private String scope;

	public CheckShareLatexUserThread(String username, String scope) {
		super();
		this.username = username;
		this.scope = scope;
	}

	@Override
	public void run() {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		//construct the xquery
		org.gcube.resources.discovery.client.queries.api.SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceName/text() eq '" + SERVICE_NAME + "'");
		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + SERVICE_CLASS + "'");

		org.gcube.resources.discovery.client.api.DiscoveryClient<GCoreEndpoint> client = clientFor(
				GCoreEndpoint.class);
		java.util.List<GCoreEndpoint> conf = client.submit(query);
		if (conf == null || conf.isEmpty()) {
			_log.info("Not creating user in ShareLatex as no gcore endpoint named <b>" + SERVICE_NAME + "</b> is present in the scope <b>" + scope + "</b>");
			ScopeProvider.instance.set(currScope);
		} else {
			_log.debug("Getting token for " + username + " in " + scope);
			String token = "";
			try {
				java.util.List<String> userRoles = new java.util.ArrayList<String>();
				String DEFAULT_ROLE = "OrganizationMember";
				userRoles.add(DEFAULT_ROLE);
				token = authorizationService().generateUserToken(new UserInfo(username, userRoles), scope);		
			} catch (Exception e) {
				e.printStackTrace();
				ScopeProvider.instance.set(currScope);
			}		
			ScopeProvider.instance.set(currScope);
			GCoreEndpoint re2s = conf.get(0);
			Iterator<Endpoint> it = re2s.profile().endpoints().iterator();
			String uriService = "";
			while (it.hasNext()) {
				Endpoint ep = it.next();
				if (ep.name().compareTo(ENTRY_NAME)==0) {
					uriService = ep.uri().toString();
					_log.debug(" ** Found uriService for "+ENTRY_NAME+"="+uriService);
					break;
				}
			}
			createUserInShareLatex(uriService, token);
		}

	}

	private void createUserInShareLatex(String uriService, String token) {
		String connectURL = uriService+"/connect?gcube-token="+token;
		String disconnectURL = uriService+"/disconnect?gcube-token="+token;
		try {
			sendGet(connectURL);
			Thread.sleep(1000);
			sendGet(disconnectURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HTTP GET request
	private static void sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		_log.debug("\nSending 'GET' request to URL : " + url);
		_log.debug("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		_log.debug(response.toString());

	}
}


