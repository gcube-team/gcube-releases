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
 * Contains the JSNI methods that needed in order to parse a 
 * clustering json string.
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public class ClusteringJSONParser extends JavaScriptObject{
	
	/** Overlay types always have protected, zero-args*/
	protected ClusteringJSONParser() { }
	
	/** Returns the query*/
	public final native String getQuery() /*-{
		return this.ClusteringResults.Query;
	}-*/;
	
	/** Returns the number of clusters*/
	public final native double getNumOfClusters()/*-{
		return this.ClusteringResults.Clusters.length;
	}-*/;
	
	/** Returns the root cluster*/
	public final native String getMainCluster()/*-{
		return this.ClusteringResults.Clusters[0].ClusterName;
	}-*/;
	
	
	/** Returns the cluster's name at for the specified position.
	 *  @param ClusterPos the position of cluster in json Array*/
	public final native String getClusterName(Integer ClusterPos)/*-{
		return this.ClusteringResults.Clusters[ClusterPos].ClusterName;
	}-*/;
	
	/**  Returns the number of documents containing the cluster which 
	  *  is located at position ClusterPos in the Json text
	  *  @param ClusterPos the position of cluster in json Array*/	   
	public final native double getNumOfDocs(Integer ClusterPos)/*-{
		return this.ClusteringResults.Clusters[ClusterPos].DocList.length;
	}-*/;
	
	/** Returns documents id for the Cluster at position ClusterPos 
	 *  and at position docPos of document's list
	 *  @param ClusterPos the position of cluster in json Array
	 *  @param docPos the position of document in the doclist in json Array*/
	public final native double getDocID(Integer ClusterPos, Integer docPos)/*-{
		return this.ClusteringResults.Clusters[ClusterPos].DocList[docPos];
	}-*/;
	
}
