package org.acme;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.stubFor;
import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.collector;
import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.localname;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.icclient.Helper;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.resources.discovery.icclient.stubs.CollectorStub;
import org.junit.Test;

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


	@Test
	public void accessPointQuery(){

		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/ForkysVRE");
		
		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq 'DataMiner'");
		query.addCondition("$resource/Profile/AccessPoint/Description/text() ne 'GetCapabilities'");
		query.setResult("<accesspoint>{$resource/Profile}</accesspoint>");
		System.out.println(query.toString());
		DiscoveryClient<String> client = ICFactory.client();
		//this should return at least one AccessPoint, but it doesn't anymore
		List<String> results = client.submit(query);
		for (String ap: results)
			System.out.println(ap.toString());
	}
	
	@XmlRootElement(name = "accesspoint")
	static class AccessPointResult {
	    @XmlElement(name = "id")
	    String id;
	    @XmlElementRef
	    AccessPoint ap;
		@Override
		
		public String toString() {
			return "AccessPointResult [id=" + id + ", ap=" + ap + "]";
		}
    
	}
}
