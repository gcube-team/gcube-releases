/**
 * 
 */
package org.gcube.data.speciesplugin;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.classification;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.streams.Stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestQueryService {
	
	public static void main(String[] args) throws IdNotValidException, Exception
	{
		ScopeProvider.instance.set("/gcube/devsec");
		Classification classificationCall = classification().withTimeout(5, TimeUnit.MINUTES).build();
		
		Stream<TaxonomyItem> taxonomyItems = classificationCall.getTaxonTreeById("ITIS:710256");
		
		while (taxonomyItems.hasNext()) {
			System.out.println(taxonomyItems.next().getCitation());
		}
	}

}
