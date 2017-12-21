/**
 * 
 */
package org.gcube.vremanagement.executor.client.plugins.query.filter;

import static java.lang.String.format;

import java.util.List;
import java.util.Random;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class RandomEndpointDiscoveryFilter implements EndpointDiscoveryFilter {

	@Override
	public void filter(SimpleQuery simpleQuery, List<ServiceEndpoint> serviceEndpoints) {
		/* 
		 * Generating a random number, assuring that is positive and 
		 * and limiting from 0 to the number of discovered ServiceEndpoints
		 * Please note that there is only one ServiceEndpoints for each running
		 * ghn
		 */
		Random random = new Random();
		int number = random.nextInt();
		while(number == Integer.MIN_VALUE){
			number = random.nextInt();
		}
		number = Math.abs(number);
		int i = number % serviceEndpoints.size();
		
		/* Getting random hosts using the generated random number*/
		String hostedOn = serviceEndpoints.get(i).profile().runtime().hostedOn();
		simpleQuery.addCondition(format(SmartExecutorPluginQuery.containsFormat, hostedOn));
		
		
	}

}
