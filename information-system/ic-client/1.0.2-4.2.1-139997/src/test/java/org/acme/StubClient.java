package org.acme;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.resources.discovery.icclient.ICFactory.*;
import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.*;

import java.net.URI;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.icclient.Helper;
import org.gcube.resources.discovery.icclient.stubs.CollectorStub;

public class StubClient {


	public static void main(String[] args) throws Exception {
		
		//when needed, setup tcpmon and decomment to see messages on the wire for debugging purposes
		//StubFactory.setProxy("localhost", 8081);
		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		String address = ServiceMap.instance.endpoint(localname);
		
		CollectorStub proxy = stubFor(collector).at(URI.create(address));

		//SimpleQuery query = queryFor(GCoreEndpoint.class);
		 
		String queryString ="declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; "+
"for $profiles in collection('/db/Profiles/GenericResource')//Document/Data/ic:Profile/Resource "+
" let $scopes := string-join( $profiles/Scopes//Scope/text(), ';') "+
" let $subtype := $profiles/Profile/SecondaryType/text() "+
"    return $profiles";
			
		Query query = new QueryBox(queryString);
		
		
		
		try {

			System.out.println(query.expression());
			String response = proxy.execute(Helper.queryAddAuthenticationControl(query.expression()));
			System.err.println(response);
		}
		catch(SOAPFaultException e) {
			throw new RuntimeException(JAXWSUtils.remoteCause(e));
		}
	}

}
