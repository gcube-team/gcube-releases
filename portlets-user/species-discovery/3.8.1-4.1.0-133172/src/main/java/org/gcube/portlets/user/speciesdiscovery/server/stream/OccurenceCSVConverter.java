/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurenceCSVConverter implements Converter<Occurrence, List<String>> {

	public static final List<String> HEADER = Arrays.asList(new String[]{
			"institutionCode",
			"collectionCode",
			"catalogueNumber",
			
			"dataSet",
			"dataProvider",
			"dataSource",
			
			"scientificNameAuthorship",
//			"lsid",
			"credits",
			
			"identifiedBy",
			"recordedBy",
			"eventDate",
			"modified",
			"scientificName",
			"kingdom",
			"family",
			"locality",
			"country",
			"citation",
			"decimalLatitude",
			"decimalLongitude",
			"coordinateUncertaintyInMeters",
			"maxDepth",
			"minDepth",
			"basisOfRecord"});
	
	@Override
	public List<String> convert(Occurrence input) throws Exception {

		List<String> fields = new LinkedList<String>();
		fields.add(cleanValue(input.getInstitutionCode()));
		fields.add(cleanValue(input.getCollectionCode()));
		fields.add(cleanValue(input.getCatalogueNumber()));
		
		fields.add(cleanValue(input.getDataSet()));
		fields.add(cleanValue(input.getDataProvider()));
		fields.add(cleanValue(input.getDataSource()));
		
		fields.add(cleanValue(input.getScientificNameAuthorship()));
//		fields.add(cleanValue(input.getLsid()));
		fields.add(cleanValue(input.getCredits()));
		
		fields.add(cleanValue(input.getIdentifiedBy()));
		fields.add(cleanValue(input.getRecordedBy()));
		fields.add(cleanValue(input.getEventDate()));
		fields.add(cleanValue(input.getModified()));
		fields.add(cleanValue(input.getScientificName()));
		fields.add(cleanValue(input.getKingdom()));
		fields.add(cleanValue(input.getFamily()));
		fields.add(cleanValue(input.getLocality()));
		fields.add(cleanValue(input.getCountry()));
		fields.add(cleanValue(input.getCitation()));
		fields.add(cleanValue(input.getDecimalLatitude()));
		fields.add(cleanValue(input.getDecimalLongitude()));
		fields.add(cleanValue(input.getCoordinateUncertaintyInMeters()));
		fields.add(cleanValue(input.getMaxDepth()));
		fields.add(cleanValue(input.getMinDepth()));
		fields.add(cleanValue(input.getBasisOfRecord()));
		return fields;
	}
	
	protected static String cleanValue(String value)
	{
		if (value==null) return "";
		return value;
	}

}
