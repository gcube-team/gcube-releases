/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation;

import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SpeciesKeyProvider extends FieldKeyProvider<SpeciesGridFields, ResultRow> {


	public SpeciesKeyProvider(SpeciesGridFields field) {
		super(field);
	}

	@Override
	public String getKey(ResultRow value, SpeciesGridFields field) {
		switch (field) {
			case DATASOURCE: return value.getDataSourceName();
			case MATCHING_RANK: return NormalizeString.lowerCaseUpFirstChar(value.getParents().get(0).getRank());
			case DATAPROVIDER: return value.getDataProviderName();
		}
		return null;
	}

}
