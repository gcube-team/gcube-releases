package org.gcube.data.spd.executor.jobs.csv;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gcube.data.spd.model.products.OccurrencePoint;



/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrenceCSVConverter implements Converter<OccurrencePoint, List<String>> {

	protected static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public static final List<String> HEADER = Arrays.asList(new String[]{
			"institutionCode",
			"collectionCode",
			"catalogueNumber",
			"dataSet",
			"dataProvider",
			"dataSource",
			
			"scientificNameAuthorship",
			"identifiedBy",
//			"lsid",
			"credits",
			
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
	public List<String> convert(OccurrencePoint input) throws Exception {
				
		List<String> fields = new LinkedList<String>();
		fields.add(cleanValue(input.getInstitutionCode()));
		fields.add(cleanValue(input.getCollectionCode()));
		fields.add(cleanValue(input.getCatalogueNumber()));
		
		if(input.getDataSet()!=null){
			fields.add(cleanValue(input.getDataSet().getName()));
			if(input.getDataSet().getDataProvider()!=null)
				fields.add(cleanValue(input.getDataSet().getDataProvider().getName()));
			else 
				fields.add("");
		}else{
			fields.add("");
			fields.add("");
		}
		
		fields.add(cleanValue(input.getProvider()));
		fields.add(cleanValue(input.getScientificNameAuthorship()));
		fields.add(cleanValue(input.getIdentifiedBy()));
//		fields.add(cleanValue(input.getLsid()));
		fields.add(cleanValue(input.getCredits()));
		
		fields.add(cleanValue(input.getRecordedBy()));
		
		if (input.getEventDate() != null)
			fields.add(cleanValue(dateFormatter.format(input.getEventDate().getTime())));
		else fields.add("");
		if (input.getModified() != null)
			fields.add(cleanValue(dateFormatter.format(input.getModified().getTime())));
		else fields.add("");
		
		fields.add(cleanValue(input.getScientificName()));
		fields.add(cleanValue(input.getKingdom()));
		fields.add(cleanValue(input.getFamily()));
		fields.add(cleanValue(input.getLocality()));
		fields.add(cleanValue(input.getCountry()));
		fields.add(cleanValue(input.getCitation()));
		fields.add(cleanValue(new Double(input.getDecimalLatitude()).toString()));
		fields.add(cleanValue(new Double(input.getDecimalLongitude()).toString()));
		fields.add(cleanValue(input.getCoordinateUncertaintyInMeters()));
		fields.add(cleanValue(new Double(input.getMaxDepth()).toString()));
		fields.add(cleanValue(new Double(input.getMinDepth()).toString()));
		fields.add(cleanValue(input.getBasisOfRecord().name()));
		return fields;
	}
	
	protected static String cleanValue(String value)
	{
		if (value==null) return "";
		return value;
	}

}