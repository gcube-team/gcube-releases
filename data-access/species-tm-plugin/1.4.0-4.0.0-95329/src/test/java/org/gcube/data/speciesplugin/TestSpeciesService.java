/**
 * 
 */
package org.gcube.data.speciesplugin;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestSpeciesService {

	/**
	 * @param args
	 * @throws QueryNotValidFault 
	 * @throws UnsupportedPluginException 
	 * @throws InvalidQueryException 
	 * @throws UnsupportedCapabilityException 
	 */
	public static void main(String[] args) throws InvalidQueryException, UnsupportedPluginException, UnsupportedCapabilityException {

		ScopeProvider.instance.set("/gcube/devsec");
		Manager call = manager().withTimeout(5, TimeUnit.MINUTES).build();
		Stream<ResultElement> result = call.search("'parachela' as ScientificName in ITIS return Taxon");
		while(result.hasNext()) 
			System.out.println(result.next());
	}

}
