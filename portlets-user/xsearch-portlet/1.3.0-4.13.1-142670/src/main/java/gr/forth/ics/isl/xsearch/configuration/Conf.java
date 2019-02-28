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
package gr.forth.ics.isl.xsearch.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * A Class that reads from property file a set of variables.
 *
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class Conf {

    /**
     * Sets the number of the top results we want to perform NEM/Clustering
     */
    public static int numOfResultsToAnalyze = 50;
    /**
     * Sets the total number of results that we want to retrieve
     */
    public static int totalNumOfResConsume = 500;
    /**
     * Start retrieving the results from the offset below
     */
    public static int startRetrResultsFromOffset = 0;
    /**
     * Sets if we want to perform NEM
     */
    public static boolean enableMining = true;
    /**
     * Sets if we want to perform Clustering
     */
    public static boolean enableClustering = true;
    /**
     * Sets the number of Clusters at Cluster label tree
     */
    public static int numOfClusters = 15;
    /**
     * The IP of XSearch Service
     */
    public static String XSearchServiceURL = new String();
    /**
     * The IP of bookmarklet's XSearch Service
     */
    public static String XSearchBookmarkletServiceURL = new String();
    /**
     * The port of XSearch Service
     */
    public static int XSearchServicePort = 0;
    /**
     * Mine only snippets
     */
    public static boolean OnlySnippets = true;
    /**
     * Get the XSearch-ServiceIP through IS or from configuration file
     */
    public static boolean getXSearchServiceURLThroughIS = false;
    /**
     * Number of Search results to show per page at Client side
     */
    public static int numOfResultsPerPage = 10;
    /**
     * If a new page is selected then if the variable mineNewResultsPerPage is
     * true then would be analyzed the results of the next page, otherwise would
     * be analyzed the next "numOfResultsToAnalyze" results
     */
    public static boolean miningNewResultsPerPage = true;
    /**
     * 
     */
    public static boolean mergeSemanticAnalysisResults = true;
    public static boolean enableMetadataGroupings = true;
    public static int cacheUpdateTimeInterval = 2;

    public static String explorationSearchType = "INDEPENDENT";
    private static Logger logger=Resources.initializeLogger(Conf.class.getName());
    
    public Conf() {
    }

    /**
     * Initializes XSearch configuration Variables by reading their values from
     * XSearch.properties file.
     *
     * @param propertiesFilePath the path where is located the properties file.
     */
    public static void InitializeXSearchProperties(String propertiesFilePath) {
        logger.info("# Initializing Properties...");
        try {

            Properties prop = new Properties();
            InputStream in = new FileInputStream(propertiesFilePath);
            prop.load(in);

            explorationSearchType = prop.getProperty("gr.forth.ics.isl.xsearch.explorationSearchType").trim();
            logger.info("=> Exploration search type is: " + explorationSearchType);	                       

            cacheUpdateTimeInterval = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.cacheUpdateTimeInterval"));
            logger.info("=> Update cache interval is : " + cacheUpdateTimeInterval + " minutes");

            mergeSemanticAnalysisResults = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.MergeSemanticAnalysisResults"));
            logger.info("=> Merge the new semantic results with the previous? : " + mergeSemanticAnalysisResults);

            miningNewResultsPerPage = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.MiningNewResultsPerPage"));
            logger.info("=> Mine new results per page? : " + miningNewResultsPerPage);

            enableMining = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.MiningEnabled"));
            logger.info("=> Is Mining enabled: " + enableMining);

            enableClustering = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.ClusteringEnabled"));
            logger.info("=> Is Clustering enabled: " + enableClustering);

            enableMetadataGroupings = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.MetadataGroupingsEnabled"));
            logger.info("=> MetadataGroupings are enabled? : " + enableMetadataGroupings);

            totalNumOfResConsume = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.NumberOfResutlsToRetrieve"));
            logger.info("=> Number of Results to Retrieve: " + totalNumOfResConsume);

            numOfResultsToAnalyze = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.NumberOfResutlsToAnalyze"));
            logger.info("=> Number of Results to Analyze: " + numOfResultsToAnalyze);

            startRetrResultsFromOffset = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.StartRetrievingResultsFromOffset"));
            logger.info("=> Offset to start retrieving results: " + startRetrResultsFromOffset);

            getXSearchServiceURLThroughIS = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.getXSearchServiceURLThroughIS"));
            logger.info("=> Retrieve XSearchServiceURL from IS: " + getXSearchServiceURLThroughIS);

            if (!getXSearchServiceURLThroughIS) {
                XSearchServiceURL = prop.getProperty("gr.forth.ics.isl.xsearch.XSearchServiceURL");
                if (!XSearchServiceURL.endsWith("/")) {
                    XSearchServiceURL += "/";
                }
                logger.info("=> XSearchService IP is : " + XSearchServiceURL);
            }

            XSearchBookmarkletServiceURL = prop.getProperty("gr.forth.ics.isl.xsearch.XSearchBookmarkletServiceURL");
            if (!XSearchBookmarkletServiceURL.endsWith("/")) {
                XSearchBookmarkletServiceURL += "/";
            }
            logger.info("=> XSearchBookmarkletServiceURL URL is : " + XSearchBookmarkletServiceURL);

            XSearchServicePort = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.XSearchServicePort"));
            logger.info("=> XSearchService Port is : " + XSearchServicePort);

            numOfClusters = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.numOfClusters"));
            logger.info("=> Number of clusters for the Cluster tree is : " + numOfClusters);

            OnlySnippets = Boolean.parseBoolean(prop.getProperty("gr.forth.ics.isl.xsearch.OnlySnippets"));
            logger.info("=> Mine only snippets is : " + OnlySnippets);

            numOfResultsPerPage = Integer.parseInt(prop.getProperty("gr.forth.ics.isl.xsearch.numOfResultsPerPage"));
            logger.info("=> Number of results per page to show at client side: " + +numOfResultsPerPage);

            in.close();

        } catch (IOException | NumberFormatException ex) {
            logger.error("An error occured while reading the properties file: "+ex.toString());
        }
    }
}
