/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.ClassificationClient;
import org.gcube.data.spd.client.proxies.ExecutorClient;
import org.gcube.data.spd.client.proxies.ManagerClient;
import org.gcube.data.spd.client.proxies.OccurrenceClient;
import org.gcube.data.spd.model.PluginDescription;


/**
 * The Class ListPlugins.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2017
 */
public class ListPlugins {


	protected static ManagerClient call;
	protected static OccurrenceClient occurrencesCall;
	protected static ClassificationClient classificationCall;
	protected static ExecutorClient executorCall;

	/**
	 * The main method.
	 *
	 * @param args the arguments
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
