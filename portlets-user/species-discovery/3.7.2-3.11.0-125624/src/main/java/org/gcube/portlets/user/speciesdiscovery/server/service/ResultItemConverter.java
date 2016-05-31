/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.model.products.Product;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyInterface;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.server.TaxonomySearchServiceImpl;
import org.gcube.portlets.user.speciesdiscovery.server.stream.Converter;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ResultItemConverter implements Converter<ResultItem, ResultRow> {

	protected Logger logger = Logger.getLogger(ResultItemConverter.class);
	protected int id = 0;
	protected ASLSession session;
	
	public ResultItemConverter(ASLSession session) {
		this.session = session;
	}

	@Override
	public ResultRow convert(ResultItem input) throws Exception {

		ResultRow row = new ResultRow(id++);
		
		row.setServiceId(input.getId());
		
		//Retrieve Properties
		List<ElementProperty> listProperties = input.getProperties(); 

		//Fill properties
		if(listProperties!=null){
			for (ElementProperty elementProperty : listProperties)
//				row.getProperties().add(new ItemParameter(StringEscapeUtils.escapeSql(elementProperty.getName()), StringEscapeUtils.escapeSql(elementProperty.getValue())));
				row.getProperties().add(new ItemParameter(elementProperty.getName(), elementProperty.getValue()));
			
			row.setExistsProperties(true);
		}

		//set author
		if(input.getScientificNameAuthorship()!=null && !input.getScientificNameAuthorship().isEmpty()){
//			row.setAuthor(StringEscapeUtils.escapeSql(input.getAuthor()));
			row.setScientificNameAuthorship(input.getScientificNameAuthorship());
		}
		else
			row.setScientificNameAuthorship(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		//set credits
		if(input.getCredits()!=null && !input.getCredits().isEmpty()){
//			row.setCredits(StringEscapeUtils.escapeSql(input.getCredits()));
			row.setCredits(input.getCredits());
		}
		else
			row.setCredits(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		//set lsid
		if(input.getLsid()!=null && !input.getLsid().isEmpty()){
//			row.setLsid(StringEscapeUtils.escapeSql(input.getLsid()));
			row.setLsid(input.getLsid());
		}
		else
			row.setLsid(ConstantsSpeciesDiscovery.NOT_FOUND);
		

		if(input.getProvider()!=null && !input.getProvider().isEmpty()){
//			row.setDataSourceId(StringEscapeUtils.escapeSql(input.getProvider()));
//			row.setDataSourceName(StringEscapeUtils.escapeSql(input.getProvider()));
			row.setDataSourceId(input.getProvider());
			row.setDataSourceName(input.getProvider());
		}else{
			row.setDataSourceId("Provider Id not found");
			row.setDataSourceName("Provider Name not found");
		}

		if (input.getDataSet()!=null) {
			DataSet dataSet = input.getDataSet();
			
			if(dataSet.getCitation()==null || dataSet.getCitation().isEmpty())
				row.setDataSetCitation("Citation Id not found");
			else
//				row.setDataSetCitation(StringEscapeUtils.escapeSql(dataSet.getCitation()));
				row.setDataSetCitation(dataSet.getCitation());
			
			if(dataSet.getId()==null || dataSet.getId().isEmpty())
				row.setDataSetId("Data Set Id not found");
			else
				row.setDataSetId(dataSet.getId());
			
			if(dataSet.getName()==null || dataSet.getName().isEmpty())
				row.setDataSetName("Data Set Name not found");
			else
//				row.setDataSetName(StringEscapeUtils.escapeSql(dataSet.getName()));
				row.setDataSetName(dataSet.getName());

			
			if (input.getDataSet().getDataProvider()!=null) {
				DataProvider dataProvider = dataSet.getDataProvider();
				
				if(dataProvider.getId() == null || dataProvider.getId().isEmpty())
					row.setDataProviderId("Data Provider Id not found");
				else
//					row.setDataProviderId(StringEscapeUtils.escapeSql(dataProvider.getId()));
					row.setDataProviderId(dataProvider.getId());
				
				if(dataProvider.getName()==null || dataProvider.getName().isEmpty())
					row.setDataProviderName("Data Provider not found");
				else
//					row.setDataProviderName(StringEscapeUtils.escapeSql(dataProvider.getName()));
					row.setDataProviderName(dataProvider.getName());
			}
		}
		
		if(input.getCommonNames()!=null){
			for (org.gcube.data.spd.model.CommonName commonName : input.getCommonNames()){
				
				CommonName com = new CommonName(commonName.getName(), commonName.getLanguage(), row.getId());
//				DaoSession.createOrUpdateCommonName(com, session);
				row.getCommonNames().add(com);
				row.setExistsCommonName(true);
			}
		}
		

		if (input.getProducts()!=null) {
			for (Product product:input.getProducts()) {
				switch (product.getType()) {
					case Occurrence: {
						row.setOccurencesCount(product.getCount());
						row.setOccurencesKey(product.getKey());
					} break;
				}
			}
		}

		
		//DEBUG
//		System.out.println("Insert row id: "+row.getId());
		
		row.setMatchingTaxon(convertTaxon(input));
		
		row.setBaseTaxonValue(NormalizeString.lowerCaseUpFirstChar(getBaseTaxonValue(TaxonomySearchServiceImpl.BASETAXONOMY,input)));
//		row.setMatchingCredits(StringEscapeUtils.escapeSql(input.getCredits()));
		row.setMatchingCredits(input.getCredits());
	
//		logger.trace("convert completed: " +row);
		
		return row;
	}

	private String getBaseTaxonValue(String rank, TaxonomyInterface taxon){
		
		while(taxon!=null){
			
			if(taxon.getRank()!=null && taxon.getRank().equalsIgnoreCase(rank))
				return taxon.getRank();
			
			taxon = taxon.getParent();
		}
		
		return TaxonomySearchServiceImpl.TAXONOMYUNKNOWN;
	}
	
	
	protected List<Taxon> convertTaxon(TaxonomyInterface taxon)
	{
		List<Taxon> listTaxon = new ArrayList<Taxon>();
		int count=0;
		while(taxon != null){
//			Taxon tax = new Taxon(count++, StringEscapeUtils.escapeSql(taxon.getScientificName()), StringEscapeUtils.escapeSql(taxon.getCitation()), taxon.getRank());
			Taxon tax = new Taxon(count++, taxon.getScientificName(), taxon.getCitation(), NormalizeString.lowerCaseUpFirstChar(taxon.getRank()));
			listTaxon.add(tax);
			taxon = taxon.getParent();
			
//			System.out.println("Insert tax parent id: "+tax.getId());
		}
		return listTaxon;	
	}
}
