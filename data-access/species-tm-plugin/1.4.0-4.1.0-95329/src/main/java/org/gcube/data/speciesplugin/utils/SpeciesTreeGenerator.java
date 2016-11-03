/**
 * 
 */
package org.gcube.data.speciesplugin.utils;

import static org.gcube.data.trees.data.Nodes.e;
import static org.gcube.data.trees.data.Nodes.n;

import java.util.Hashtable;
import java.util.List;

import javax.xml.namespace.QName;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.exceptions.StreamStopSignal;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;

/**
 * Generates trees from taxonomy items.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class SpeciesTreeGenerator implements Generator<TaxonomyItem, Tree> {

	protected static GCUBELog logger = new GCUBELog(SpeciesTreeGenerator.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tree yield(TaxonomyItem element) throws StreamSkipSignal, StreamStopSignal {

		Tree tree = new Tree();

		//set ID as tree attribute
		tree.setAttribute(SpeciesService.SPECIES_SERVICE_ID, element.getId());	
		
		tree.add(e("title",element.getScientificName() + " " + element.getScientificNameAuthorship() +" - Taxonomic Information"));	
		//create edges
		Edge propertiesEdge = createProperties(element);
		Edge dwcItemEdge = createDwCA(element);
		Edge provenanceItemEdge = createProvenance(element);

		//Add nodes
		tree.add(propertiesEdge);		
		tree.add(dwcItemEdge);
		tree.add(provenanceItemEdge);

		return tree;

	}

	/**
	 * Create subtree with provenance information
	 */
	private Edge createProvenance(TaxonomyItem item) {
		//Provenance
		InnerNode provenanceItem = new InnerNode();	
		Edge provenanceItemEdge = new Edge(new QName("provenance"),provenanceItem);		

		provenanceItem.add(					
				//				e("plugin",item.getProvider()),
				e("wasDerivedFrom",item.getCitation()),
				e("wasGeneratedBy",item.getCredits()));

		return provenanceItemEdge;
	}

	/**
	 * Create subtree with DwC-A information
	 */
	private Edge createDwCA(TaxonomyItem item) {
		//create hashtable with taxonomy keys
		Hashtable<String, String> hashTaxa = new Hashtable<String,String>();
		getTax(item, hashTaxa);
		//spit scientific name
		String[] name = item.getScientificName().split(" ");

		//DwC-A taxonomyItem
		InnerNode dwcItem = new InnerNode();	
		Edge dwcItemEdge = new Edge(new QName("dwc"),dwcItem);		

		dwcItem.add(
				e("scientificNameAuthorship", item.getScientificNameAuthorship()),
				e("acceptedNameUsageID",item.getStatus().getRefId()), 
				e("taxonomicStatus",item.getStatus().getStatus()), 
				e("taxonRemarks",item.getStatus().getStatusAsString()),
				e("rightsHolder", item.getCredits()),		
				e("scientificNameID", item.getLsid()),
				e("modified", item.getModified()),
				e("taxonRank", item.getRank()),
				e("scientificName", item.getScientificName()),
				e("nameAccordingTo", item.getCitation())
				);

		String parent = "";
		try{
			parent = item.getParent().getId();
		}catch (Exception e) {}
		dwcItem.add(e("parentNameUsageID", parent));

		String kingdom = "";
		try{
			kingdom = (String)hashTaxa.get("kingdom");
		}catch (Exception e) {}
		dwcItem.add(e("kingdom", kingdom));		   


		String phylum = "";
		try{
			phylum = (String) hashTaxa.get("phylum");
		}catch (Exception e) {}
		dwcItem.add(e("phylum", phylum));	

		String claz = "";
		try{ 
			claz = (String)hashTaxa.get("class");
		}catch (Exception e) {}
		dwcItem.add(e("class", claz));	


		String order = "";
		try{
			order = (String)hashTaxa.get("order");
		}catch (Exception e) {}
		dwcItem.add(e("order", order));	

		String  family = "";
		try{
			family = (String)hashTaxa.get("family");
		}catch (Exception e) {}
		dwcItem.add(e("family", family));	

		String genus = "";
		try{
			genus = (String)hashTaxa.get("genus");
		}catch (Exception e) {}
		dwcItem.add(e("genus", genus));	

		String subgenus = "";
		try{
			subgenus = (String)hashTaxa.get("subgenus");
		}catch (Exception e) {}
		dwcItem.add(e("subgenus", subgenus));	


		String specificEpithet = "";
		if (name.length>1)
			specificEpithet = name[1];
		dwcItem.add(e("specificEpithet", specificEpithet));

		String infraspecificEpithet = "";
		if (name.length>2)
			infraspecificEpithet = name[name.length-1];
		dwcItem.add(e("infraspecificEpithet", infraspecificEpithet));

		String verbatimTaxonRank = "";
		if (name.length>2)
			verbatimTaxonRank = name[name.length-2];
		dwcItem.add(e("verbatimTaxonRank", verbatimTaxonRank));

		InnerNode commonNames = new InnerNode();
		

		List<CommonName> commonNameList = null;

		if ((commonNameList = item.getCommonNames())!=null){
			for (CommonName c: commonNameList){						
															
				String language = "";
				try{
					language = c.getLanguage();
				}catch (Exception e) {}
				
				String locality = "";
				try{
					locality = c.getLocality();
				}catch (Exception e) {}
				
				String vernacularName = "";
				try{
					vernacularName = c.getName();
				}catch (Exception e) {}
				
				commonNames.add(e("language",language), 
						e("locality",locality), 
						e("vernacularName", vernacularName));
				
				Edge commonNamesEdge = new Edge(new QName("vernacularNames"),commonNames);
				dwcItem.add(commonNamesEdge);
			}
		}
		return dwcItemEdge;
	}

	/**
	 * Create subtree with TaxonomyItem information
	 */
	private Edge createProperties(TaxonomyItem item) {

		//taxonomyItem
		InnerNode properties = new InnerNode();	
		Edge propertiesEdge = new Edge(new QName("properties"),properties);	

		List<ElementProperty> listProp = null;

		if ((listProp = item.getProperties())!=null){
			for (ElementProperty property: listProp){
				properties.add(
						e("property",
								n(
										e("key", property.getName()), 
										e("value", property.getValue()))));
			}
		}

		return propertiesEdge;
	}


	/**
	 * Create hashtable with taxonomy keys
	 */
	private Hashtable<String, String> getTax(TaxonomyItem item, Hashtable<String, String> taxa){

		taxa.put((item.getRank()).toLowerCase(), item.getScientificName());
		if (item.getParent()!=null)
			getTax(item.getParent(), taxa);

		return taxa;

	}

}
