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
public class OccurenceCSVConverterOpenModeller implements Converter<Occurrence, List<String>> {

	public static final List<String> HEADER = Arrays.asList(new String[]{
			"#id",
			"label",
			"long",
			"lat",
			"abundance"});
	
//	id, label, longitude, latitude, abundance
	
	protected final static String PRESENCE = "1"; //Abundance should be 1 for a presence point.
	
	@Override
	public List<String> convert(Occurrence input) throws Exception {

		List<String> fields = new LinkedList<String>();
		fields.add(cleanValue(input.getServiceId()));
		fields.add(cleanValue(input.getScientificName()));
		fields.add(cleanValue(input.getDecimalLongitude()));
		fields.add(cleanValue(input.getDecimalLatitude()));
		fields.add(PRESENCE);
		return fields;
	}
	
	protected static String cleanValue(String value)
	{
		if (value==null) return "";
		return value;
	}

}
