/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.util.concurrent.TimeUnit;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;
import org.gcube.portlets.user.speciesdiscovery.server.service.ResultItemConverter;
import org.gcube.portlets.user.speciesdiscovery.server.service.StreamIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CastConverter;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.server.stream.ConversionIterator;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ServiceQuery {

	/**
	 * @param args
	 * @throws UnsupportedPluginException 
	 * @throws InvalidQueryException 
	 * @throws UnsupportedCapabilityException 
	 */
	
	private static String username = "test.user";
	
	public static void main(String[] args) throws InvalidQueryException, UnsupportedPluginException, UnsupportedCapabilityException {
		String scope = "/gcube/devsec";
//		String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment"; //Production
		ScopeProvider.instance.set(scope);
		
		ASLSession session = SessionManager.getInstance().getASLSession("123", username);
		
		Manager call  = manager().withTimeout(3, TimeUnit.MINUTES).build();
		
//		Manager call = manager().at(URI.create("http://node24.d.d4science.research-infrastructures.eu:9000")).withTimeout(3, TimeUnit.MINUTES).build();

		//Stream<ResultElement> results = call.search("SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS RETURN Product");
//		Stream<ResultElement> results = call.search("SEARCH BY CN 'shark' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product");
		
		
//		Stream<ResultElement> results = call.search("SEARCH BY SN 'sarda sarda' RESOLVE WITH OBIS EXPAND WITH ITIS WHERE coordinate <= 15.12, 16.12 RETURN Product");
		
		System.out.println("start query...");
		
		Stream<ResultElement> results = call.search("SEARCH BY SN 'sarda' IN GBIF, OBIS, SpeciesLink RETURN Product HAVING xpath(\"//product[type='Occurrence' and count>0]\")");
		
//		Stream<ResultElement> results = call.search("SEARCH BY SN 'Palinurus elephas' IN WoRMS RETURN Taxon");
		
		StreamIterator<ResultElement> input = new StreamIterator<ResultElement>(results);
		
		
		System.out.println("Results from service...");
		int i=0;
		while(results.hasNext()) {
			ResultElement elem = results.next();
			System.out.println(++i +") el: "+elem.getId() +"  type: "+elem.getType().name());
		}
		
		
		System.out.println("Results from conversion...");
		ConversionIterator<ResultElement, ResultItem> caster = buildCaster(input);
			
			//from ResultItem to ResultRow
		ResultItemConverter converter = new ResultItemConverter(session);
		ConversionIterator<ResultItem, ResultRow> inputConverter = new ConversionIterator<ResultItem, ResultRow>(caster, converter);

		while (inputConverter.hasNext()) {
			ResultRow row = inputConverter.next();
			
			System.out.println(++i +") row: "+row);
			
		}
		
		
		results.close();
		System.out.println("DONE");
	}
	
	protected static <I,O> ConversionIterator<I, O> buildCaster(CloseableIterator<I> input)
	{		
		CastConverter<I, O> elementConverter = new CastConverter<I, O>();
		ConversionIterator<I, O> caster = new ConversionIterator<I, O>(input, elementConverter);
		return caster;
	}

}
