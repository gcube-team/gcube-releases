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
 
package gr.forth.ics.isl.xsearch.resources;

import java.util.HashMap;
import java.util.HashSet;
import javax.servlet.ServletContext;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Resources {

    //private static final String prefix=System.getProperty("catalina.home")+"/conf/";
    //private static final String prefix = "/conf/";
    public static String X_SEARCH_PROPERTIES_FILE;  
  
    public static final String SYSTEMNAME = "x-search";
    
    // INITIALIZATION //
    public static final String CONFIGURATION_FILES_URL="http://svn.research-infrastructures.eu/public/d4science/gcube/trunk/semantic-search/XSearch-Service-conf/";
    public static final String CONFIGURATION_TARBALL="XSearchResources.tar.gz";
                
    // MINING //
    public static String MINING_RESULTS = null;
    public static int MAX_NUM_OF_RESULTS_FROM_WSE = 200;
    public static int MAX_NUM_OF_ENTITIES_PER_CATEGORY = -1;
    public static boolean MINE_QUERY = true;
    public static HashSet<String> MINING_ACCEPTED_CATEGORIES;
    public static HashSet<String> MINING_ALL_POSSIBLE_CATEGORIES;
    
    // TCP SERVER //
    public static int TCP_SERVER_PORT = 1500;
    
    // SPARQL //
    public static HashMap<String, String> SPARQL_ENDPOINTS;
    public static HashMap<String, String> SPARQL_TEMPLATES;
    public static HashMap<String, String> SPARQL_ENPOINTS_USERNAMES;
    public static HashMap<String, String> SPARQL_ENPOINTS_PASSWORDS;
    
    public static final String TEMPLATE_PARAMETER = "<ENTITY>";
    public static final String GET_PROPERTIES_TEMPLATE_PARAMETER = "THE_URI";
    public static String GET_PROPERTIES_TEMPLATE_QUERY;
    
   
    //CLUSTERING //
    public static int CLUSTERING_ALGORITHM = 3;
   
    
    // OPEN SEARCH //
    public static String DESCRIPTIONDOCUMENT = null;
    
   
    // LOG //
    public static String LOG = null;
    
    // TEMP FOLDER //
    public static String TEMP_FOLDER = null;
    
    // CONFIGUTATIONS FOLDER //
    public static String CONFIGURATIONS_FOLDER = null;
    
    public static ServletContext MAIN_SERVLET_CONTEXT;
    public static String MAIN_RESOURCES_FOLDER;
    
    
    // SUPPORTED SEARCH SYSTEMS //
    public static HashMap<String, String> SUPPORTED_SEARCH_SYSTEMS = new HashMap<String, String>();
    
    // SUPPORTED CLUSTERING ALGORITHMS //
    public static HashMap<String, String> SUPPORTED_CLUSTERING_ALGORITHMS = new HashMap<String, String>();
    
}
