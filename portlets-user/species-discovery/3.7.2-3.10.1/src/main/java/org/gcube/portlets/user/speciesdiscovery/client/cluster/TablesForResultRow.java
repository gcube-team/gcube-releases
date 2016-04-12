package org.gcube.portlets.user.speciesdiscovery.client.cluster;

import java.util.Collections;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.widget.Html;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TablesForResultRow {

	public static Html getTableForResultRow(ResultRow row, String classification){
		
		//Init values
		String dataSourceName = "";
		String dataProviderName = "";
		String dataSetName = "";
		String dataSetCitation= "";
		String matchingTaxonName= "";
		String matchingAccordionTo= "";
		String matchingRank= "";
		String matchingCredits= "";
		int occurencesCount = 0;
		
		String scientificNameAuthorship = "";
		String lsid = "";
		String credits = "";
		
		String propertiesHtml = "";
		
		
		//Validate values
		if(row.getParents()!=null){
			
			if(row.getParents().get(0).getName()!=null) matchingTaxonName = row.getParents().get(0).getName();
			
			if(row.getParents().get(0).getAccordingTo()!=null) matchingAccordionTo = row.getParents().get(0).getAccordingTo();
			
			if(row.getParents().get(0).getRank()!=null) 	matchingRank = row.getParents().get(0).getRank();
		}
	
		if(row.getDataSourceName()!=null) dataSourceName = row.getDataSourceName();
		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
		if(row.getDataSetName()!=null) dataSetName = row.getDataSetName();
		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
		if(row.getMatchingCredits()!=null) matchingCredits = row.getMatchingCredits();
		if(row.getOccurencesCount()!=0) occurencesCount = row.getOccurencesCount();
		
	
		String commonNames = "";
		if(row.getCommonNames()!=null){
			for (CommonName comName : row.getCommonNames()) {
				commonNames+= "<b>"+comName.getName()+"</b>" +" ("+comName.getLanguage()+") - ";
			}
		}
		
		
		if(row.getScientificNameAuthorship()!=null) scientificNameAuthorship = row.getScientificNameAuthorship();
		
		if(row.getLsid()!=null) lsid = row.getLsid();
		
		if(row.getCredits()!=null) credits = row.getCredits();
		
	
		if(row.getProperties()!=null){
			
			List<ItemParameter> listProperties = row.getProperties();
			Collections.sort(listProperties, ItemParameter.COMPARATOR);
			
			propertiesHtml+="<table class=\"parameters\">";
			
			for (ItemParameter itemParameter : listProperties) {
				
				propertiesHtml+=
					"<tr>" +
					"	<td class=\"title\">"+itemParameter.getKey()+"</td>" +
					"	<td>"+itemParameter.getValue()+"</td>" +
					"</tr>";
			}
			
			propertiesHtml+="</table>";
		}

		String table = "<table class=\"imagetable\">" +
		"<tr>" +
		"	<th>"+matchingTaxonName+"</th>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.CLASSIFICATION_STRING.getName()+"</td>" +
		"	<td>"+classification+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.COMMON_NAMES.getName()+" (Language)</td>" +
		"	<td>"+commonNames+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASOURCE.getName()+"</td>" +
		"	<td>"+dataSourceName+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATAPROVIDER.getName()+"</td>" +
		"	<td>"+dataProviderName+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASET.getName()+"</td>" +
		"	<td>"+dataSetName+"</td>" +
		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_AUTHOR.getName()+"</td>" +
//		"	<td>"+matchingAccordionTo+"</td>" +
//		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_RANK.getName()+"</td>" +
		"	<td>"+matchingRank+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASET_CITATION.getName()+"</td>" +
		"	<td>"+dataSetCitation+"</td>" +
		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_CREDITS.getName()+"</td>" +
//		"	<td>"+matchingCredits+"</td>" +
//		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.PRODUCT_OCCURRENCES.getName()+"</td>" +
		"	<td>"+occurencesCount+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.LSID.getName()+"</td>" +
		"	<td>"+lsid+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.SCIENTIFICNAMEAUTHORSHIP.getName()+"</td>" +
		"	<td>"+scientificNameAuthorship+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.CREDITS.getName()+"</td>" +
		"	<td>"+credits+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.PROPERTIES.getName()+"</td>" +
		"	<td>"+propertiesHtml+"</td>" +
		"</tr>" +

		"</table>";
		return new Html(table);
		
	}
}
