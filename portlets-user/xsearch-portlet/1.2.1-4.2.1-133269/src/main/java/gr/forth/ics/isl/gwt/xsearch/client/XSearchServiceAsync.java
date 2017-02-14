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
package gr.forth.ics.isl.gwt.xsearch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *  * XSearch Async service interface.
 *
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public interface XSearchServiceAsync {

    /**
     * Gets a map that contains the query results combined with configurations that 
     * retrieved from configuration file.
     * @param callback the map that is returned from the server containing the
     * semantic analysis results.
     */
    void getSemanticAnalysisResults(int startOffset, int numOfResultsAnalyze, AsyncCallback<Map<String, ArrayList<String>>> callback);

    /**
     * Gets a map that contains the query results combined with configuration 
     * retrieved from configuration file.
     * @param callback the map that is returned from the server containing the
     * query results.
     */
    void getQueryResults(int startOffset, AsyncCallback<Map<String, ArrayList<String>>> callback);

    /**
     * A function that takes the information for the entity enrichment.
     * @param entityName the name of the entity for which we want to retrieve
     * more information
     * @param categoryName the category of the entity
     * @param callback the map that is returned from the server containing the
     * query results.
     */
    void getEntityEnrichment(String entityName, String categoryName, AsyncCallback<String> callback);

    /**
     * A function that get URI's properties.
     * @param category the category of the entity for which we want to retrieve
     * its properties
     * @param uri the URI of the entity for which we want to retrieve its
     * properties
     * @param callback the map that is returned from the server containing the
     * query results.
     */
    void getURIProperties(String category, String uri, AsyncCallback<String> callback);
    
    /**
     * A function that returns the URLs that contained at URI's 
     * content.
     * @param uri hit's URI
     * @param callback the map that is returned from the server containing the
     * the URLs that contained at URI's content.
     */
    void getURIContent(String uri, AsyncCallback<TreeMap<String, List<String>>> callback);
}
