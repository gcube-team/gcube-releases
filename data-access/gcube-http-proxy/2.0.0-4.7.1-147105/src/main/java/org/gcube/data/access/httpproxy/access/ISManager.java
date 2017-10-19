package org.gcube.data.access.httpproxy.access;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

public class ISManager  {

	private final String 	SECONDARY_TYPE ="SecureProxyDomains",
							RESULTS = "$resource/Profile/Body/Domains/domain/string()";
	
	public List<String> getDomains ()
	{
		SimpleQuery query = ICFactory.queryFor(GenericResource.class);
		
		String queryCondition = generateQuery(SECONDARY_TYPE);
		query.addCondition(queryCondition).setResult(RESULTS);
		DiscoveryClient<String>  client = ICFactory.client();
		List<String> response = client.submit(query);
		return response;
	
	}
	
	
	
	private String generateQuery(String secondaryType)
	{	
		StringBuilder builder = new StringBuilder();
		builder.append("$resource/Profile/SecondaryType/text() eq '");
		builder.append(secondaryType);
		builder.append("'");
		return builder.toString();
		
	}

	public static void main(String[] args) {
		

		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		try
		{
			System.out.println(new ISManager().getDomains());
		} catch (RuntimeException e)
		{
			SOAPFaultException soap = (SOAPFaultException) e.getCause();
			
			System.out.println(soap.getMessage());
			System.out.println(soap.getFault());
		}
		
		
		
	}

}
