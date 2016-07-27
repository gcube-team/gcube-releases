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
package gr.forth.ics.isl.xsearch;

import gr.forth.ics.isl.stellaclustering.CLT_Creator;
import gr.forth.ics.isl.textentitymining.gate.GateAnnie;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class InitializatorListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        /* DOWNLOAD RESOURCES */

        System.out.println("# Downloading resources...");
        Resources.MAIN_SERVLET_CONTEXT = sce.getServletContext();
        Resources.MAIN_RESOURCES_FOLDER = Resources.MAIN_SERVLET_CONTEXT.getRealPath("/resources/");
        Resources.MAIN_RESOURCES_FOLDER += "/";
       
        
        new FileDownloader().download(Resources.CONFIGURATION_FILES_URL + Resources.CONFIGURATION_TARBALL, Resources.MAIN_RESOURCES_FOLDER + Resources.CONFIGURATION_TARBALL).
                             decompress(Resources.MAIN_RESOURCES_FOLDER + Resources.CONFIGURATION_TARBALL, Resources.MAIN_RESOURCES_FOLDER);
        System.out.println("# Resources were downloaded!");

        Resources.X_SEARCH_PROPERTIES_FILE = Resources.MAIN_RESOURCES_FOLDER + "x-search.properties";
       
        /* INITIALIZING THE SUPPORTED SEARCH SYSTEMS */
        Resources.SUPPORTED_SEARCH_SYSTEMS.put("opensearch","OpenSearch (http://www.opensearch.org/). The OpenSearch Description Document must be provided.");
        Resources.SUPPORTED_SEARCH_SYSTEMS.put("gcube","gCube Infrastructure Search System (https://i-marine.d4science.org/web/guest/about-gcube). The ResultSet locator must be provided.");
        Resources.SUPPORTED_SEARCH_SYSTEMS.put("ecoscope","Ecoscope Search System (http://www.ecoscopebc.ird.fr/)");
        
        /* INITIALIZING THE SUPPORTED CLUSTERING ALGORITHMS */
        Resources.SUPPORTED_CLUSTERING_ALGORITHMS.put("cl1","STC: Suffix Tree Clustering Algorithm");
        Resources.SUPPORTED_CLUSTERING_ALGORITHMS.put("cl2","STC+: Variation of STC which differs in the way the clusters are scored and in the way base clusters are merged (http://users.ics.forth.gr/~tzitzik/publications/Tzitzikas_2009_WISE.pdf)");
        Resources.SUPPORTED_CLUSTERING_ALGORITHMS.put("cl3","NM-STC: No Merge Suffix Tree Clustering (http://users.ics.forth.gr/~tzitzik/publications/Tzitzikas_2009_WISE.pdf)");
        Resources.SUPPORTED_CLUSTERING_ALGORITHMS.put("cl4","STC++: Variation of STC+");
        Resources.SUPPORTED_CLUSTERING_ALGORITHMS.put("cl5","NM-STC+: Variation of NM-STC");
        
        /* CREATING CLUSTERING PROPERTIES FILE */
        String clusteringFilename = "clustering.properties";
        String clusteringPropertiesFile = Resources.MAIN_RESOURCES_FOLDER + clusteringFilename;
        File clusteringFile = new File(clusteringPropertiesFile);
        if (!clusteringFile.exists()) {
            try {
                clusteringFile.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(clusteringFile, true));
                bw.write("# CLUSTERING PROPERTIES FILE");
                bw.write("\n");
                bw.write("# " + IOSLog.getCurrentDate());
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.useStemming = true");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.useStopWords = true");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.stopList = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/stopwords.txt");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.endings = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/endingsall.txt");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.prefixes = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/prefixes.txt");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.irregular = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/irregulars.txt");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.aklita = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/aklita.txt");
                bw.write("\n");

                bw.write("gr.forth.ics.stellaclustering.resources.stemmer.stemmerLog = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "lexicalAnalyzer/stemmer/Log4J.stemmerProperties");
                bw.write("\n");
                bw.write("\n");

                bw.flush();
                bw.close();

            } catch (IOException ex) {
                Logger.getLogger(InitializatorListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /* CREATING MINING PROPERTIES FILE */
        String miningFilename = "mining.properties";
        String miningPropertiesFile = Resources.MAIN_RESOURCES_FOLDER + miningFilename;
        File miningFile = new File(miningPropertiesFile);
        if (!miningFile.exists()) {
            try {
                miningFile.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(miningFile, true));
                bw.write("# ENTITY MINING PROPERTIES FILE");
                bw.write("\n");
                bw.write("# " + IOSLog.getCurrentDate());
                bw.write("\n");

                bw.write("gr.forth.ics.textentitymining.resources.gateHomeFolder = " + Resources.MAIN_RESOURCES_FOLDER.replace("\\", "/") + "entityMiningNew/");
                bw.write("\n");

                bw.write("gr.forth.ics.textentitymining.resources.maxNumOfTotalEntities = 10000");
                bw.write("\n");

                bw.flush();
                bw.close();

            } catch (IOException ex) {
                Logger.getLogger(InitializatorListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        /* INITIALIZE CLUSTERING PROPERTIES */
        System.out.println("------");
        CLT_Creator.InitializeClusteringProperties(clusteringPropertiesFile);
        System.out.println("------");

        /* INITIALIZE MINING PROPERTIES */
        GateAnnie.InitializeMiningProperties(miningPropertiesFile);

        /* INITIALIZEZ X-SEARCH PROPERTIES */
        InitializeXSearchProperties(Resources.X_SEARCH_PROPERTIES_FILE);
        System.out.println("------");

        /* START ANNIE GATE */
        System.out.println("Executing Annie Gate...");
        try {
            GateAnnie.InitializeGate();
        } catch (Exception ex) {
            //Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("*** Error Initializing GateAnnie: " + ex.getMessage());
        }

        /* START TCP SERVER */
        System.out.println("# The system is ready to accept requests!");
        TCPServer server = new TCPServer(Resources.TCP_SERVER_PORT);
        server.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("# The context was destroyed! ");
    }

    private void InitializeXSearchProperties(String propertiesFilePath) {
        System.out.println("# Initializing X-Search Properties...");

        try {

            Properties prop = new Properties();
            InputStream in = new FileInputStream(propertiesFilePath);
            prop.load(in);

            // READ THE TCP SERVER PORT //
            String portStr = prop.getProperty("gr.forth.ics.isl.xsearch.resources.tcpport");
            try {
                Resources.TCP_SERVER_PORT = Integer.parseInt(portStr);
            } catch (Exception e) {
                System.out.println("*** Using default port: " + Resources.TCP_SERVER_PORT);
            }

            // READ THE OPENSEARCH DESCRIPTION DOCUMENT //
            String descriptionDocument = prop.getProperty("gr.forth.ics.isl.xsearch.resources.opensearch.descriptionDocument");
            Resources.DESCRIPTIONDOCUMENT = descriptionDocument;

            // READ CLUSTERING ALGORITHM //
            String clusteringAlgorithmString = prop.getProperty("gr.forth.ics.isl.xsearch.resources.clustering.clusteringAlgorithm");
            try {
                int clusteringAlgorithm = Integer.parseInt(clusteringAlgorithmString.trim());
                if (clusteringAlgorithm < 1 || clusteringAlgorithm > 5) {
                    System.out.println("*** NOT APPROPRIATE CLUSTERING ALGORITHM! VALUE NOT AN INTEGER IN THE INTERVAL [1, 5]. SETTING DEFAULT ALGORITHM 3 (NM-STC).");
                    clusteringAlgorithm = 3;
                }
                Resources.CLUSTERING_ALGORITHM = clusteringAlgorithm;
            } catch (Exception e) {
                System.out.println("*** NOT APPROPRIATE CLUSTERING ALGORITHM! VALUE NOT AN INTEGER IN THE INTERVAL [1, 5]. SETTING DEFAULT ALGORITHM 3 (NM-STC).");
                Resources.CLUSTERING_ALGORITHM = 3;
            }

            // READ 'MINE QUERY' PROPERTY //
            String mineQueryString = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.mineQuery");
            if (mineQueryString.toLowerCase().equals("true")) {
                Resources.MINE_QUERY = true;
            } else {
                Resources.MINE_QUERY = false;
            }

            // READ ALL POSSIBLE CATEGORIES //
            Resources.MINING_ALL_POSSIBLE_CATEGORIES = new HashSet<String>();
            String allCategories = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.allPossibleCategories");
            String[] allCategoriesArray = allCategories.split(",");
            for (String oneCategory : allCategoriesArray) {
                if (!oneCategory.trim().equals("")) {
                    Resources.MINING_ALL_POSSIBLE_CATEGORIES.add(oneCategory.trim());
                }
            }

            // READ ACCEPTED CATEGORIES //
            Resources.MINING_ACCEPTED_CATEGORIES = new HashSet<String>();
            String acceptedCategories = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.acceptedCategories");
            String[] acceptedCategoriesArray = acceptedCategories.split(",");
            for (String acceptedCategory : acceptedCategoriesArray) {
                if (!acceptedCategory.trim().equals("")) {
                    Resources.MINING_ACCEPTED_CATEGORIES.add(acceptedCategory.trim());
                }
            }

            // READ SPARQL ENDPOINDS and TEMPLATES//
            Resources.SPARQL_ENDPOINTS = new HashMap<String, String>();
            Resources.SPARQL_TEMPLATES = new HashMap<String, String>();
            Resources.SPARQL_ENPOINTS_USERNAMES = new HashMap<String, String>();
            Resources.SPARQL_ENPOINTS_PASSWORDS = new HashMap<String, String>();
            for (String acceptedCategory : Resources.MINING_ACCEPTED_CATEGORIES) {
                String sparqlEndpoint = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + acceptedCategory);
                if (sparqlEndpoint != null) {
                    Resources.SPARQL_ENDPOINTS.put(acceptedCategory, sparqlEndpoint.trim());
                }

                String sparqlTemplate = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.templatequery." + acceptedCategory);
                if (sparqlTemplate != null) {
                    Resources.SPARQL_TEMPLATES.put(acceptedCategory, Resources.MAIN_RESOURCES_FOLDER + sparqlTemplate.trim());
                }
                
                String sparqlEndpointUsername = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + acceptedCategory + ".username");
                if (sparqlEndpointUsername != null) {
                    Resources.SPARQL_ENPOINTS_USERNAMES.put(sparqlEndpoint, sparqlEndpointUsername.trim());
                }
                    
                String sparqlEndpointPassword = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + acceptedCategory + ".password");
                if (sparqlEndpointPassword != null) {
                    Resources.SPARQL_ENPOINTS_PASSWORDS.put(sparqlEndpoint, sparqlEndpointPassword.trim());
                }
            }

            // READ MINING RESULTS FOLDER //
            //String miningResults = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.miningResultsFolder");
            Resources.MINING_RESULTS = Resources.MAIN_RESOURCES_FOLDER + "miningResults/";

            // READ LOG FOLDER //
            //String log = prop.getProperty("gr.forth.ics.isl.xsearch.resources.log");
            Resources.LOG = Resources.MAIN_RESOURCES_FOLDER + "x-search.log";

            // READ TMP FOLDER //
            //String tmp = prop.getProperty("gr.forth.ics.isl.xsearch.resources.tempFolder");
            Resources.TEMP_FOLDER = Resources.MAIN_RESOURCES_FOLDER + "tmp/";

            // READ CONFIGURATIONS FOLDER //
            //String conf = prop.getProperty("gr.forth.ics.isl.xsearch.resources.configurationsFolder");
            Resources.CONFIGURATIONS_FOLDER = Resources.MAIN_RESOURCES_FOLDER + "configurations/";

            // READ GET PROPERTIES TEMPLATE QUERY //
            String getPropertiesQuery = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.templatequery.getpropertiesquery");
            Resources.GET_PROPERTIES_TEMPLATE_QUERY = Resources.MAIN_RESOURCES_FOLDER + getPropertiesQuery;

            // PRINT PROPERTIES //
            System.out.println("=> OPENSEARCH DESCRIPTION DOCUMENT: " + Resources.DESCRIPTIONDOCUMENT);
            System.out.println("=> CLUSTERING ALGORITHM: " + Resources.CLUSTERING_ALGORITHM);
            System.out.println("=> MINE QUERY: " + Resources.MINE_QUERY);
            System.out.println("=> MINING ALL POSSIBLE CATEGORIES: " + Resources.MINING_ALL_POSSIBLE_CATEGORIES);
            System.out.println("=> MINING ACCEPTED CATEGORIES: " + Resources.MINING_ACCEPTED_CATEGORIES);
            System.out.println("=> SPARQL ENDPOINTS: " + Resources.SPARQL_ENDPOINTS);
            System.out.println("=> SPARQL ENDPOINTS USERNAMES: " + Resources.SPARQL_ENPOINTS_USERNAMES);
            System.out.println("=> SPARQL ENDPOINTS PASSWORDS: " + Resources.SPARQL_ENPOINTS_PASSWORDS);
            System.out.println("=> SPARQL TEMPLATES: " + Resources.SPARQL_TEMPLATES);
            System.out.println("=> MINING RESULTS FOLDER: " + Resources.MINING_RESULTS);
            System.out.println("=> LOG FOLDER: " + Resources.LOG);
            System.out.println("=> TEMP FOLDER: " + Resources.TEMP_FOLDER);
            System.out.println("=> CONFIGURATIONS FOLDER: " + Resources.CONFIGURATIONS_FOLDER);
            System.out.println("=> GET PROPERTIES TEMPLATE QUERY: " + Resources.GET_PROPERTIES_TEMPLATE_QUERY);

            in.close();

        } catch (Exception e) {
            System.out.println("*** PROBLEM READING X-SEARCH PROPERTIES FILE: " + e.getMessage());
        }

    }
}
