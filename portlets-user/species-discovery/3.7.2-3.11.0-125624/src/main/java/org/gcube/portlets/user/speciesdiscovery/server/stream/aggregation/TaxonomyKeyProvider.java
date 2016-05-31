/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation;

import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TaxonomyKeyProvider extends FieldKeyProvider<TaxonomyGridField, TaxonomyRow> {


	public TaxonomyKeyProvider(TaxonomyGridField field) {
		super(field);
	}

	@Override
	public String getKey(TaxonomyRow value, TaxonomyGridField field) {
		switch (field) {
			case MATCHING_RANK: return NormalizeString.lowerCaseUpFirstChar(value.getRank());
			case DATASOURCE: return value.getDataProviderName();
		}
		return null;
	}

}
