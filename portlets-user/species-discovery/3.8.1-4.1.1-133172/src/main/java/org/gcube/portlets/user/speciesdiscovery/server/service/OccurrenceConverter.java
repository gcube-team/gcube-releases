/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.data.spd.model.BasisOfRecord;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.server.stream.Converter;
import org.gcube.portlets.user.speciesdiscovery.server.util.XStreamUtil;
import org.gcube.portlets.user.speciesdiscovery.server.util.XStreamUtil.AliasItem;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OccurrenceConverter implements Converter<OccurrencePoint, org.gcube.portlets.user.speciesdiscovery.shared.Occurrence> {

//	protected static final SimpleDateFormat FORMAT = new SimpleDateFormat();
	protected static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //Added for Gianpaolo Ticket #824
	protected int id = 0;
	private XStreamUtil<ItemParameter> xstreamUtil;
	
	public OccurrenceConverter() {
		xstreamUtil = new XStreamUtil<ItemParameter>(AliasItem.OCCURRECENCE, ItemParameter.class);
	}

	
	@Override
	public org.gcube.portlets.user.speciesdiscovery.shared.Occurrence convert(OccurrencePoint input) throws Exception {
		
		org.gcube.portlets.user.speciesdiscovery.shared.Occurrence occurrence = new org.gcube.portlets.user.speciesdiscovery.shared.Occurrence();
		
		occurrence.setId(id++);
	
		occurrence.setServiceId(input.getId());
		
		//Retrieve Properties
		List<ElementProperty> listProperties = input.getProperties(); 
	
		//Fill properties
		if(listProperties!=null){
			for (ElementProperty elementProperty : listProperties)
				occurrence.getProperties().add(new ItemParameter(elementProperty.getName(), elementProperty.getValue()));
			
			occurrence.setExistsProperties(true);
		}
		

		//set author
		if(input.getScientificNameAuthorship()!=null && !input.getScientificNameAuthorship().isEmpty()){
			occurrence.setScientificNameAuthorship(StringEscapeUtils.escapeSql(input.getScientificNameAuthorship()));
		}
		else
			occurrence.setScientificNameAuthorship(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		//set credits
		if(input.getCredits()!=null && !input.getCredits().isEmpty()){
			occurrence.setCredits(StringEscapeUtils.escapeSql(input.getCredits()));
		}
		else
			occurrence.setCredits(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		
		//set credits
		if(input.getIdentifiedBy()!=null && !input.getIdentifiedBy().isEmpty()){
			occurrence.setIdentifiedBy(StringEscapeUtils.escapeSql(input.getIdentifiedBy()));
		}
		else
			occurrence.setIdentifiedBy(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		
		if(input.getDataSet()!=null){
			occurrence.setDataSet(cleanValueEscapeSql(input.getDataSet().getName()));
			
			if(input.getDataSet().getDataProvider()!=null){
				occurrence.setDataProvider(cleanValueEscapeSql(input.getDataSet().getDataProvider().getName()));
			}
			else
				occurrence.setDataProvider(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		else{
			occurrence.setDataSet(ConstantsSpeciesDiscovery.NOT_FOUND);
			occurrence.setDataProvider(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		
//		occurrence.setDataProvider(input.getDataSet().getDataProvider().getName());
		
		occurrence.setDataSource(cleanValueEscapeSql(input.getProvider()));
		occurrence.setInstitutionCode(cleanValueEscapeSql(input.getInstitutionCode()));
		occurrence.setCollectionCode(cleanValueEscapeSql(input.getCollectionCode()));
		occurrence.setCatalogueNumber(cleanValueEscapeSql(input.getCatalogueNumber()));
		occurrence.setRecordedBy(cleanValueEscapeSql(input.getRecordedBy()));
		occurrence.setEventDate(cleanValue(input.getEventDate()));
		occurrence.setModified(cleanValue(input.getModified()));
		occurrence.setScientificName(cleanValueEscapeSql(input.getScientificName()));
		occurrence.setKingdom(cleanValueEscapeSql(input.getKingdom()));
		occurrence.setFamily(cleanValueEscapeSql(input.getFamily()));
		occurrence.setLocality(cleanValueEscapeSql(input.getLocality()));
		occurrence.setCountry(cleanValueEscapeSql(input.getCountry()));
		occurrence.setCitation(cleanValueEscapeSql(input.getCitation()));
		occurrence.setDecimalLatitude(String.valueOf(input.getDecimalLatitude()));
		occurrence.setDecimalLongitude(String.valueOf(input.getDecimalLongitude()));
		occurrence.setCoordinateUncertaintyInMeters(cleanValueEscapeSql(input.getCoordinateUncertaintyInMeters()));
		occurrence.setMaxDepth(String.valueOf(input.getMaxDepth()));
		occurrence.setMinDepth(String.valueOf(input.getMinDepth()));
		occurrence.setBasisOfRecord(cleanValue(input.getBasisOfRecord()));
		
		return occurrence;
	}
	
	protected static String cleanValue(BasisOfRecord basisOfRecord)
	{
		if (basisOfRecord==null) return "";
		return basisOfRecord.toString();
	}
	
	protected static String cleanValue(Calendar value)
	{
		if (value==null) return "";
		return FORMAT.format(value.getTime());
	}
	
	protected static String cleanValueEscapeSql(String value)
	{
		if (value==null || value.isEmpty()) return ConstantsSpeciesDiscovery.NOT_FOUND;
		return StringEscapeUtils.escapeSql(value);
	}

}
