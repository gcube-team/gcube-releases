/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.client.proxies.Occurrence;
import org.gcube.data.spd.model.PluginDescription;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ListPlugins {

	
	protected static Manager call;
	protected static Occurrence occurrencesCall;
	protected static Classification classificationCall;
	protected static Executor executorCall;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String scope = "/gcube/devsec";
		ScopeProvider.instance.set(scope);
		
//		this.call = manager().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
//		this.occurrencesCall =  occurrences().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
//	    this.classificationCall = classification().at( URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
		
		
		call = manager().withTimeout(3, TimeUnit.MINUTES).build();
//		executorCall = executor().withTimeout(3, TimeUnit.MINUTES).build();
//		occurrencesCall =  occurrence().withTimeout(3, TimeUnit.MINUTES).build();
//	    classificationCall = classification().withTimeout(3, TimeUnit.MINUTES).build();
		
//		call = manager().at(URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();
		
		//Manager call = manager().withTimeout(3, TimeUnit.MINUTES).build();
		List<PluginDescription> plugins = call.getPluginsDescription();

		for (PluginDescription plugin:plugins) System.out.println(plugin.getName());
	}

}
