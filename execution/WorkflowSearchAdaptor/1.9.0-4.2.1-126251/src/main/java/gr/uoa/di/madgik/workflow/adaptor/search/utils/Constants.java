package gr.uoa.di.madgik.workflow.adaptor.search.utils;

public class Constants {

	public enum SupportedRelations {
		adj, fuzzy, proximity, within
	};

	public static final String EQUALS = "=";

	public static final String ALL_INDEXES = "allIndexes";
	
	public static final String WILDCARD = "*";
	
	/** The field name for the collection id of a record*/
	public static final String COLLECTION_FIELD = "gDocCollectionID";
	
	/** The field name for the collection language of a record*/
	public static final String LANGUAGE_FIELD = "gDocCollectionLang";
	
	/** The field name of an index type that indicates the document id of a record*/
	public static final String DOCID_FIELD = "ObjectID";
}
