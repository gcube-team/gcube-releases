/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * Foundation for Research and Technology - Hellas (FORTH)
 * Institute of Computer Science (ICS) 
 * Information Systems Laboratory (ISL)
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * 
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 * 
 */
package gr.forth.ics.isl.gwt.xsearch.client.parser.json;


import com.google.gwt.core.client.JavaScriptObject;

/**
 * Contains all the JSNI methods that needed in order to parse 
 * a Json string with mined categories.
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 *
 */
public class CategoriesJSONParser extends JavaScriptObject {
	
	/** (JSNI method) Overlay types always have protected, zero-args*/
	protected CategoriesJSONParser() { }
	
	/** (JSNI method) Returns the query*/
	public final native String getQuery() /*-{
		return this.MiningResults.Query;
	}-*/;

	/** (JSNI method) Returns the number of mined categories*/
	public final native double getNumOfCategories()/*-{
		return this.MiningResults.MinedCategories.length;
	}-*/;
	
	/** (JSNI method) Returns the number of entities for that specific category 
	 * @param CategoryPos is the position of category in json Array at the json string*/ 
	public final native double getNumOfEntities(Integer CategoryPos)/*-{
		return this.MiningResults.MinedCategories[CategoryPos].Entities.length;
	}-*/;
	
	/**
	 *  (JSNI method) Rerturns the size of documents list that contain the entity
	 *  @param CategoryPos is the position of category in json Array of the json string*/
	public final native double getNumOfDocs(Integer CategoryPos, Integer EntityPos)/*-{
		return this.MiningResults.MinedCategories[CategoryPos].Entities[EntityPos].DocList.length;
	}-*/;
	
	/** Returns category's name at position pos from MinedCategories Json Array
	 * @param CategoryPos is the position of category in json Array of the json string*/
	public final native String getCategoryName(Integer CategoryPos) /*-{
		return this.MiningResults.MinedCategories[CategoryPos].CategoryName;
	}-*/;
	
	
	/** (JSNI method) Returns the entity name.
	 * @param CategoryPosis the position of category in json Array of the json string
	 * @param EntityPos the position of the Entity in json Array of the json string*/
	public final native String getEntityName(Integer CategoryPos, Integer EntityPos) /*-{
		return this.MiningResults.MinedCategories[CategoryPos].Entities[EntityPos].EntityName;
	}-*/;
	
	/** (JSNI method) Returns the specified docId.
	  * @param CategoryPosis the position of category in json Array of the json string
	  * @param EntityPos the position of the Entity in json Array of the json string*/
	public final native double getDocID(Integer CategoryPos, Integer EntityPos, Integer docPos)/*-{
		return this.MiningResults.MinedCategories[CategoryPos].Entities[EntityPos].DocList[docPos];
	}-*/;
	
	
}


