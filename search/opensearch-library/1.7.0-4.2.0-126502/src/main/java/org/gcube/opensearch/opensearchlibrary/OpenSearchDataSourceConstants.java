package org.gcube.opensearch.opensearchlibrary;


public class OpenSearchDataSourceConstants {
	/** Separator for the published field info */
	public static final String FIELD_SEPARATOR = ":";

	/** Tag for presentable fields in the published field info */
	public static final String PRESENTABLE_TAG = "p";

	/** Tag for searchable fields in the published field info */
	public static final String SEARCHABLE_TAG = "s";

	/** The field name for the collection id of a record */
	public static final String COLLECTION_FIELD = "gDocCollectionID";

	/** The field name for the collection language of a record */
	public static final String LANGUAGE_FIELD = "gDocCollectionLang";

	public static final String ALL_INDEXES = "allIndexes";

	/**
	 * The field name of an index type that indicates the document id of a
	 * record
	 */
	public static final String OBJECTID_FIELD = "ObjectID";

	public static final String LANGUAGE_PARAMETER = OpenSearchConstants.languageQName;

	public static final String RESULTSNO_EVENT = "resultsNumber";
	public static final String RESULTSNOFINAL_EVENT = "resultsNumberFinal";
}
