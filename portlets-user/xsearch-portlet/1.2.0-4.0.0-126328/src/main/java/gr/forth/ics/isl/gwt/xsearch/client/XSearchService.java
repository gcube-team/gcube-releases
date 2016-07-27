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

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * XSearch service interface.
 *
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
@RemoteServiceRelativePath("XSearchService")
public interface XSearchService extends RemoteService {

    /**
     * Gets a map that contains the query results combined with configurations that 
     * retrieved from configuration file.
     * @param startOffset start offset of which to start retrieving the results
     * @param numOfResultsAnalyze number of results to analyze
     * @return a map which contains the results of semantic analysis.
     */
    Map<String, ArrayList<String>> getSemanticAnalysisResults(int startOffset, int numOfResultsAnalyze);

    /**
     * Gets a map that contains the query results combined with configuration 
     * retrieved from configuration file.
     * @param startOffset start offset of which to start retrieving the results
     * @return a map which contains the query results.
     */
    Map<String, ArrayList<String>> getQueryResults(int startOffset);

    /**
     * A function that takes the information for the entity enrichment.
     * @param entityName the name of the entity for which we want to retrieve
     * more information
     * @param categoryName the category of the entity
     * @return a string with the semantic information about the entity
     */
    String getEntityEnrichment(String entityName, String categoryName);

    /**
     * A function that get URI's properties.
     * @param category the category of the entity for which we want to retrieve
     * its properties
     * @param uri the URI of the entity for which we want to retrieve its
     * properties
     * @return URI's properties
     */
    String getURIProperties(String category, String uri);
    
    /**
     * A function that returns the URLs that contained at URI's 
     * content.
     * @param URI hit's URI
     * @return a treeMap that contains the URLs that contained at
     * URI's content.
     */
    TreeMap<String, List<String>> getURIContent(String URI);
}
