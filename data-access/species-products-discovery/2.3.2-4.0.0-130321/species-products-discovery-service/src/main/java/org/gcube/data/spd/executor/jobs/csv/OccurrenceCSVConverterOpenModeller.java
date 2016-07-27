package org.gcube.data.spd.executor.jobs.csv;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gcube.data.spd.model.products.OccurrencePoint;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrenceCSVConverterOpenModeller implements Converter<OccurrencePoint, List<String>> {

	public static final List<String> HEADER = Arrays.asList(new String[]{
			"#id",
			"label",
			"long",
			"lat",
			"abundance"});
	
//	id, label, longitude, latitude, abundance
	
	protected final static String PRESENCE = "1"; //Abundance should be 1 for a presence point.
	
	@Override
	public List<String> convert(OccurrencePoint input) throws Exception {

		List<String> fields = new LinkedList<String>();
		fields.add(cleanValue(input.getId()));
		fields.add(cleanValue(input.getScientificName()));
		fields.add(cleanValue(new Double(input.getDecimalLongitude()).toString()));
		fields.add(cleanValue(new Double(input.getDecimalLatitude()).toString()));
		fields.add(PRESENCE);
		return fields;
	}
	
	protected static String cleanValue(String value)
	{
		if (value==null) return "";
		return value;
	}

}