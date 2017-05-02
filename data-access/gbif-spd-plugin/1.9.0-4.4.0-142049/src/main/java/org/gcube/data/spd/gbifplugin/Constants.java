package org.gcube.data.spd.gbifplugin;

import javax.xml.namespace.QName;

public class Constants {

	//public static final String BASE_URL = "http://api.gbif.org/v1";
	
	public static final int QUERY_LIMIT = 200;
	
	public static final QName GBIFKEY_ATTR= new QName("gbifKey");
	public static final QName ABOUT_ATTR= new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about");
	public static final QName RESOURCE_ATTR= new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");
	public static final QName TOTAL_MATCHED_ATTR = new QName("totalMatched");
	
	
	
	public static final String CHILD_RELATIONSHIP_VALUE = "http://rs.tdwg.org/ontology/voc/TaxonConcept#IsChildTaxonOf";
	
	public static final String REPOSITORY_NAME="GBIF";
	
}
