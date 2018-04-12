package org.gcube.resources.discovery.icclient;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.resources.discovery.icclient.stubs.CollectorConstants.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.soap.SOAPFaultException;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils;
import org.gcube.common.scope.api.ServiceMap;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.icclient.stubs.CollectorStub;
import org.gcube.resources.discovery.icclient.stubs.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link DiscoveryClient} that submits queries to the Information Collector, without parsing the results.
 * 
 */
public class ICClient implements DiscoveryClient<String> {

	private static final Logger log = LoggerFactory.getLogger(ICClient.class);

	//result split pattern
	private static final Pattern pattern = Pattern.compile("<Record>(.*?)</Record>", Pattern.DOTALL);
	
	
	public List<String> submit(Query query) throws DiscoveryException {
		
		try {
			CollectorStub stub = getStub();
			String results = callService(query, stub);
			return splitIntoList(results);	
		}
		catch(MalformedQueryException e) {
			throw new DiscoveryException("malformed query "+query.expression(),e);
		}
		catch(SOAPFaultException e) {
			throw new RuntimeException(JAXWSUtils.remoteCause(e));
		}
		
	}
	
	//helper
	private String callService(Query query, CollectorStub stub) {
		
		String expression = query.expression();
		
		log.info("executing query {}",expression);
		
		long time = System.currentTimeMillis();
		
		String submittedExpression = Helper.queryAddAuthenticationControl(expression);
		
		String response = stub.execute(submittedExpression);
		
		log.info("executed query {} in {} ms",expression,System.currentTimeMillis()-time);
		
		return response;
	}
		
	//helper
	private CollectorStub getStub() {
		
		//find endpoint address in service map currently in scope
		String address = ServiceMap.instance.endpoint(localname);
		log.info("connectinfg to "+address);
		//obtain a JAXWS stub configured for gCube calls
		return stubFor(collector).at(URI.create(address));
	}
		
	//helper
	private List<String> splitIntoList(String response) {
		
		List<String> results = new ArrayList<String>();
		
		Matcher m = pattern.matcher(response);
		    
		while (m.find())
			results.add(m.group(1).trim());

		return results;
	}
}
