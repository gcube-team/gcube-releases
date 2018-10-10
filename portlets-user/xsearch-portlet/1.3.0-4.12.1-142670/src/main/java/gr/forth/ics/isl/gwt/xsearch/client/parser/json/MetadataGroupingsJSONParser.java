package gr.forth.ics.isl.gwt.xsearch.client.parser.json;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Contains all the JSNI methods that needed in order to parse 
 * a Json string with extracted meta-data groupings.
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 *
 */
public class MetadataGroupingsJSONParser extends JavaScriptObject {


	/** (JSNI method) Overlay types always have protected, zero-args*/
	protected MetadataGroupingsJSONParser() { }
	
	/** (JSNI method) Returns the query*/
	public final native String getQuery() /*-{
		return this.MetadataGroupingResults.Query;
	}-*/;

	/** (JSNI method) Returns the number of metadata groupings*/
	public final native double getNumOfGroups()/*-{
		return this.MetadataGroupingResults.MetadataGroups.length;
	}-*/;
	
	/** (JSNI method) Returns the number of entities for that specific metadata group 
	 * @param GroupPos is the position of metadata group in json Array at the json string*/ 
	public final native double getNumOfMetadatas(Integer GroupPos)/*-{
		return this.MetadataGroupingResults.MetadataGroups[GroupPos].Metadatas.length;
	}-*/;
	
	/**
	 *  (JSNI method) Returns the size of documents list that contain the entity
	 *  @param GroupPos is the position of metadata group  in json Array of the json string*/
	public final native double getNumOfDocs(Integer GroupPos, Integer MetadataPos)/*-{
		return this.MetadataGroupingResults.MetadataGroups[GroupPos].Metadatas[MetadataPos].DocList.length;
	}-*/;
	
	/** Returns metadataGroup's name at position pos from MetadataGroups Json Array
	 * @param GroupPos is the position of metadata group in json Array of the json string*/
	public final native String getMetadataGroupName(Integer GroupPos) /*-{
		return this.MetadataGroupingResults.MetadataGroups[GroupPos].MetadataGroupName;
	}-*/;
	
	
	/** (JSNI method) Returns the entity name.
	 * @param GroupPos the position of metadata group in json Array of the json string
	 * @param MetadataPos the position of the Metadata in json Array of the json string*/
	public final native String getMetadataName(Integer GroupPos, Integer MetadataPos) /*-{
		return this.MetadataGroupingResults.MetadataGroups[GroupPos].Metadatas[MetadataPos].MetadataName;
	}-*/;
	
	/** (JSNI method) Returns the specified docId.
	  * @param GroupPos the position of metadata group in json Array of the json string
	  * @param MetadataPos the position of the Metadata in json Array of the json string*/
	public final native double getDocID(Integer GroupPos, Integer MetadataPos, Integer docPos)/*-{
		return this.MetadataGroupingResults.MetadataGroups[GroupPos].Metadatas[MetadataPos].DocList[docPos];
	}-*/;
	
}
