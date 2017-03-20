package org.gcube.portlets.user.speciesdiscovery.server.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.server.TaxonomySearchServiceImpl;
import org.gcube.portlets.user.speciesdiscovery.server.stream.Converter;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class TaxonomyItemConverter implements Converter<TaxonomyItem, TaxonomyRow> {

	protected Logger logger = Logger.getLogger(TaxonomyItemConverter.class);
	protected int id = 0;
	private ASLSession session;

	/**
	 * @param dataProviderId
	 */
	public TaxonomyItemConverter(ASLSession session) {
		this.session = session;
	}

	@Override
	public TaxonomyRow convert(TaxonomyItem input) throws Exception {
		
		TaxonomyRow taxonomy = createTaxonomyRow(input);
		
		//SET PARENTS
		
//		System.out.println("Convert Taxonomy parent for: "+taxonomy.getId());
		
		taxonomy.setParent(convertParentsTaxonomy(input));
		
		//SET BASE TAXON
		taxonomy.setBaseTaxonValue(NormalizeString.lowerCaseUpFirstChar(getTaxonomyValue(TaxonomySearchServiceImpl.BASETAXONOMY,taxonomy)));
//		System.out.println("#############################PARENT ID: " + taxonomy.getParent().getId());
		
		if(taxonomy.getParents()!=null && taxonomy.getParents().size()>0)
			taxonomy.setParentID(""+taxonomy.getParents().get(0).getId());
	
		return taxonomy;
	}
	

	public LightTaxonomyRow convertLightTaxonomy(TaxonomyItem input) throws Exception {
		
		LightTaxonomyRow taxonomy = createLightTaxonomyRow(input);
		
		//SET PARENTS
		
//		System.out.println("Convert Taxonomy parent for: "+taxonomy.getId());
		
		taxonomy.setParent(convertParentsLightTaxonomy(input));
		
		//SET BASE TAXON
		taxonomy.setBaseTaxonValue(NormalizeString.lowerCaseUpFirstChar(getTaxonomyValue(TaxonomySearchServiceImpl.BASETAXONOMY,taxonomy)));
		
		if(taxonomy.getParents()!=null && taxonomy.getParents().size()>0)
			taxonomy.setParentID(""+taxonomy.getParents().get(0).getId());
	
		return taxonomy;
	}
	
	
	protected LightTaxonomyRow createLightTaxonomyRow(TaxonomyItem input) throws Exception{
		
		LightTaxonomyRow tax = new LightTaxonomyRow(id++);
		

		if(input.getId()!=null){
			tax.setServiceId(input.getId());
		}
	
		if(input.getProvider()!=null && !input.getProvider().isEmpty()){
			tax.setDataProviderId(input.getProvider());
			tax.setDataProviderName(input.getProvider());
		}else{
			tax.setDataProviderId(ConstantsSpeciesDiscovery.NOT_FOUND);
			tax.setDataProviderName(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		
		if(input.getRank()!=null && !input.getRank().isEmpty()){
			tax.setRank(NormalizeString.lowerCaseUpFirstChar(input.getRank()));
		}else{
			tax.setRank(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		if(input.getScientificName()!=null && !input.getScientificName().isEmpty()){
			tax.setName(input.getScientificName());
		}else{
			tax.setName(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
			
		if(input.getStatus()!=null){
			
			if(input.getStatus().getRefId()!=null && !input.getStatus().getRefId().isEmpty()){
				tax.setStatusRefId(input.getStatus().getRefId());
			}else{
				tax.setStatusRefId("");
			}
			
			
			if(input.getStatus().getStatusAsString()!=null && !input.getStatus().getStatusAsString().isEmpty()){
				tax.setStatusRemarks(input.getStatus().getStatusAsString());
			}else{
				tax.setStatusRemarks(ConstantsSpeciesDiscovery.NOT_FOUND);
			}
			
			if(input.getStatus().getStatus()!=null){
				
				if(input.getStatus().getStatus().name()!=null && !input.getStatus().getStatus().name().isEmpty()){
					tax.setStatusName(input.getStatus().getStatus().name());
				}else{
					tax.setStatusName(ConstantsSpeciesDiscovery.NOT_FOUND);
				}

			}
				
		}
		return tax;
	}
	

	protected TaxonomyRow createTaxonomyRow(TaxonomyItem input) throws Exception{
		
		TaxonomyRow tax = new TaxonomyRow(id++);
		
		//Retrieve Properties
		List<ElementProperty> listProperties = input.getProperties(); 

		//Fill properties
		if(listProperties!=null){
			for (ElementProperty elementProperty : listProperties)
				tax.getProperties().add(new ItemParameter(elementProperty.getName(), elementProperty.getValue()));
			
			tax.setExistsProperties(true);
		}

		if(input.getId()!=null){
			tax.setServiceId(input.getId());
		}
		
		if(input.getScientificNameAuthorship()!=null && !input.getScientificNameAuthorship().isEmpty()){
			tax.setScientificNameAuthorship(input.getScientificNameAuthorship());
		}
		else
			tax.setScientificNameAuthorship(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		if(input.getCredits()!=null && !input.getCredits().isEmpty()){
			tax.setCredits(input.getCredits());
		}
		else
			tax.setCredits(ConstantsSpeciesDiscovery.NOT_FOUND);
		
		if(input.getLsid()!=null && !input.getLsid().isEmpty()){
			tax.setLsid(input.getLsid());
		}
		else
			tax.setLsid(ConstantsSpeciesDiscovery.NOT_FOUND);

		if(input.getProvider()!=null && !input.getProvider().isEmpty()){
			tax.setDataProviderId(input.getProvider());
			tax.setDataProviderName(input.getProvider());
		}else{
			tax.setDataProviderId(ConstantsSpeciesDiscovery.NOT_FOUND);
			tax.setDataProviderName(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		if(input.getCitation()!=null && !input.getCitation().isEmpty()){
			tax.setDataSetCitation(input.getCitation());
		}else{
			tax.setDataSetCitation(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		if(input.getRank()!=null && !input.getRank().isEmpty()){
			tax.setRank(NormalizeString.lowerCaseUpFirstChar(input.getRank()));
		}else{
			tax.setRank(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
		
		if(input.getScientificName()!=null && !input.getScientificName().isEmpty()){
			tax.setName(input.getScientificName());
		}else{
			tax.setName(ConstantsSpeciesDiscovery.NOT_FOUND);
		}
			
		if(input.getStatus()!=null){
			
			if(input.getStatus().getRefId()!=null && !input.getStatus().getRefId().isEmpty()){
				tax.setStatusRefId(input.getStatus().getRefId());
			}else{
				tax.setStatusRefId("");
			}
			
			
			if(input.getStatus().getStatusAsString()!=null && !input.getStatus().getStatusAsString().isEmpty()){
				tax.setStatusRemarks(input.getStatus().getStatusAsString());
			}else{
				tax.setStatusRemarks(ConstantsSpeciesDiscovery.NOT_FOUND);
			}
			
			if(input.getStatus().getStatus()!=null){
				
				if(input.getStatus().getStatus().name()!=null && !input.getStatus().getStatus().name().isEmpty()){
					tax.setStatusName(input.getStatus().getStatus().name());
				}else{
					tax.setStatusName(ConstantsSpeciesDiscovery.NOT_FOUND);
				}

			}
				
		}
		
		if(input.getModified()!=null){
			tax.setDateModified(input.getModified().getTime().toString());
		}
		
		if(input.getCommonNames()!=null){
			for (org.gcube.data.spd.model.CommonName commonName : input.getCommonNames()){
				
				CommonName com = new CommonName(commonName.getName(), commonName.getLanguage(), tax.getId());
//				DaoSession.createOrUpdateCommonName(com, session);
				tax.getCommonNames().add(com);
				tax.setExistsCommonName(true);
			}
		}
		
//		System.out.println("convert completed: " +tax);
		
		return tax;
	}
	
	private String getTaxonomyValue(String rank, TaxonomyRow taxon){
		
		List<TaxonomyRow> listTaxonomy = taxon.getParents();
		
		for (TaxonomyRow taxonomyRow : listTaxonomy) {
			
			if(taxonomyRow.getRank().compareToIgnoreCase(rank)==0)
				return taxonomyRow.getName();
		}

		return TaxonomySearchServiceImpl.TAXONOMYUNKNOWN;
	}
	
	
	private String getTaxonomyValue(String rank, LightTaxonomyRow taxon){
		
		List<LightTaxonomyRow> listTaxonomy = taxon.getParents();
		
		for (LightTaxonomyRow taxonomyRow : listTaxonomy) {
			
			if(taxonomyRow.getRank().compareToIgnoreCase(rank)==0)
				return taxonomyRow.getName();
		}

		return TaxonomySearchServiceImpl.TAXONOMYUNKNOWN;
	}
	
	
	protected List<LightTaxonomyRow> convertParentsLightTaxonomy(TaxonomyItem taxon) throws Exception
	{
		if (taxon == null) return null;
		List<LightTaxonomyRow> listTaxonomy = new ArrayList<LightTaxonomyRow>();
		TaxonomyItem parent = taxon.getParent();
		
		int i=0;
		
		while(parent!=null){
			LightTaxonomyRow row = createLightTaxonomyRow(parent);
			row.setParent(true);
			row.setParentIndex(i++);
			
//			System.out.println("Insert Taxonomy parent id: "+row.getId() + ", row.getParentIndex: "+row.getParentIndex());
			
			listTaxonomy.add(row);
			parent = parent.getParent();
		}

		return listTaxonomy;	
	}
	
	
	protected List<TaxonomyRow> convertParentsTaxonomy(TaxonomyItem taxon) throws Exception
	{
		if (taxon == null) return null;
		List<TaxonomyRow> listTaxonomy = new ArrayList<TaxonomyRow>();
		TaxonomyItem parent = taxon.getParent();
		
		int i=0;
		
		while(parent!=null){
			TaxonomyRow row = createTaxonomyRow(parent);
			row.setParent(true);
			row.setParentIndex(i++);
			
//			System.out.println("Insert Taxonomy parent id: "+row.getId() + ", row.getParentIndex: "+row.getParentIndex());
			
			listTaxonomy.add(row);
			parent = parent.getParent();
		}

		return listTaxonomy;	
	}
	

}
