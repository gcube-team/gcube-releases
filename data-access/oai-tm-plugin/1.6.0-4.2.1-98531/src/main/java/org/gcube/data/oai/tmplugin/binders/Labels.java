package org.gcube.data.oai.tmplugin.binders;

import java.io.Serializable;

public class Labels implements Serializable{
	/**
	 * Labels used in OAI Tree
	 */
	private static final long serialVersionUID = 1L;

	/**Content.*/
	public static final String CONTENT ="content";
	
	/**Metadata property.*/
	public static final String METADATA="metadata";
	
	/**Title.*/
	public static final String TITLE="title";
	
	/**Provenance.*/
	public static final String PROVENANCE="provenance";
	
	/**Last update property.*/
	public static final String LAST_UPDATE="lastUpdateTime";
	
	/**Content mime type property.*/
	public static final String MIME_TYPE ="mimeType";
	
	/**Creation time property.*/
	public static final String CREATION_TIME = "creationTime";

	/**Collection ID.*/
	public static final String COLLECTION_ID = "collectionID";

	/**Statement.*/
	public static final String STATEMENT = "statement";

	/**Schema.*/
	public static final String SCHEMA = "schema";

	/**Schema location.*/
	public static final String SCHEMALOCATION = "schemaLocation";

	/**Record.*/
	public static final String RECORD = "record";

	/**Content type.*/
	public static final String CONTENT_TYPE = "contentType";

	/**url.*/
	public static final String URL = "url";

	public static final String SET_ID = "setID";

	public static final String RECORD_ID = "recordID";
}
