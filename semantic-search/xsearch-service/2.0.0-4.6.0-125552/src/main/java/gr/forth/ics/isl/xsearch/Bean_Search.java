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

import com.thoughtworks.xstream.XStream;
import gr.forth.ics.isl.stellaclustering.CLT_Creator;
import gr.forth.ics.isl.stellaclustering.util.TreeNode;
import gr.forth.ics.isl.xsearch.clustering.Clustering;
import gr.forth.ics.isl.xsearch.mining.Mining;
import gr.forth.ics.isl.xsearch.opensearch.DescriptionDocument;
import gr.forth.ics.isl.xsearch.opensearch.OpenSearchRetriever;
import gr.forth.ics.isl.xsearch.pagesretriever.PagesRetriever;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.xsearch.ecoscope.EcoscopeRetriever;
import gr.forth.ics.isl.xsearch.fao.FigisRetriever;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import gr.forth.ics.isl.xsearch.util.MD5;
import gr.uoa.di.madgik.grs.buffer.IBuffer;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.StringField;
import java.io.StringWriter;
import java.net.URI;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Bean_Search {

    private String query;
    private boolean only_snippets;
    private ArrayList<SearchResult> wseResults;
    private ArrayList<Category> entities;
    private String results_first_page;
    private String statistics;
    private int max_num_of_results_from_wse;
    private String clustersContent;
    private int numOfClusters;
    private boolean clustering;
    private boolean mining;
    private String exampleQuery;
    private String jsonResults;
    private String jsonMiningResults;
    private String jsonClusteringResults;
    private Clustering clusteringComponent;

    public Bean_Search(boolean only_snippets, int max_num_of_results_from_wse) {
        this.only_snippets = only_snippets;
        this.max_num_of_results_from_wse = max_num_of_results_from_wse;
        wseResults = new ArrayList<SearchResult>();
        entities = new ArrayList<Category>();
        results_first_page = "";
    }

    public Bean_Search(String query, int max_num_of_results_from_wse, boolean clustering, int numOfClusters, boolean mining, boolean only_snippets, String descrDoc, int clusteringAlgorithm, boolean mineQuery, HashSet<String> acceptedCategories, HashMap<String, String> endpoints, HashMap<String, String> templateQueries) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException, IOException {

        this.statistics = "";
        this.exampleQuery = "";
        long start, end;

        // INITIALIZE THE PARAMETERS //
        System.out.println("--------");
        System.out.println("# Initializing query parameters...");
        this.query = query;
        System.out.println("=> Query: " + query);
        statistics += "=> Query: " + query + "\n";

        this.max_num_of_results_from_wse = max_num_of_results_from_wse;
        System.out.println("=> Number of results to mine/cluster: " + max_num_of_results_from_wse);
        statistics += "=> Number of results to mine/cluster: " + max_num_of_results_from_wse + "\n";

        this.mining = mining;
        System.out.println("=> Do mining: " + mining);
        statistics += "=> Do mining: " + mining + "\n";

        this.clustering = clustering;
        System.out.println("=> Do clustering: " + clustering);
        statistics += "=> Do clustering: " + clustering + "\n";

        this.numOfClusters = numOfClusters;
        System.out.println("=> Number of desired clusters: " + numOfClusters);
        statistics += "=> Number of desired clusters: " + numOfClusters + "\n";

        this.only_snippets = only_snippets;
        System.out.println("=> Do only snippets: " + only_snippets);
        statistics += "=> Do only snippets: " + only_snippets + "\n";


        // READ OPEN SEARCH DESCRIPTION DOCUMENT //
        start = System.currentTimeMillis();

        if (descrDoc.toLowerCase().startsWith("figis")) {
            HashMap<String, String> parameters = new HashMap<String, String>();

            int ind = descrDoc.indexOf("?");
            if (ind == -1) {
                parameters.put("owner", "fi");
                parameters.put("xml", "y");
                parameters.put("xml_no_subject", "");
                parameters.put("FORM_C", "AND");
                parameters.put("sortorder", "3");
                parameters.put("pub_year", "2011");
                //parameters.put("progname","26");
            } else {
                String parametersString = descrDoc.substring(ind + 1);
                System.out.println("# Parameters string: " + parametersString);
                String[] pairs = parametersString.split("&");
                for (int j = 0; j < pairs.length; j++) {
                    String pair = pairs[j];
                    String[] mapping = pair.split("=");
                    if (mapping.length == 2) {
                        parameters.put(mapping[0], mapping[1]);
                    }
                    if (mapping.length == 1) {
                        parameters.put(mapping[0], "");
                    }
                }
                System.out.println("# Parameters: " + parameters);
            }

            FigisRetriever retriever = new FigisRetriever(query, max_num_of_results_from_wse);
            retriever.setParameters(parameters);
            retriever.retrieveResults();
            wseResults = retriever.getResults();
        } else if (descrDoc.toLowerCase().startsWith("ecoscope")) {
            HashMap<String, String> parameters = new HashMap<String, String>();

            int ind = descrDoc.indexOf("?");
            if (ind == -1) {
                //parameters.put("owner", "fi");
            } else {
                String parametersString = descrDoc.substring(ind + 1);
                System.out.println("# Parameters string: " + parametersString);
                String[] pairs = parametersString.split("&");
                for (int j = 0; j < pairs.length; j++) {
                    String pair = pairs[j];
                    String[] mapping = pair.split("=");
                    if (mapping.length == 2) {
                        parameters.put(mapping[0], mapping[1]);
                    }
                    if (mapping.length == 1) {
                        parameters.put(mapping[0], "");
                    }
                }
                System.out.println("# Parameters: " + parameters);
            }
            EcoscopeRetriever retriever = new EcoscopeRetriever(query, max_num_of_results_from_wse);
            retriever.setParameters(parameters);
            retriever.retrieveResults();
            wseResults = retriever.getResults();

        } else {
            DescriptionDocument descrDocument = new DescriptionDocument(descrDoc);
            System.out.println("# OpenSearch Provider: " + descrDocument.getShortName() + " (" + descrDocument.getDescription() + ")");
            HashMap<String, String> urlTemplates = descrDocument.getUrlTemplates();
            exampleQuery = descrDocument.getExampleQuery();

            end = System.currentTimeMillis() - start;
            System.out.println("# Reading OpenSearch description document in: " + end + " ms.");
            statistics += "# Reading OpenSearch description document in: " + end + " ms.\n";


            // GET TOP RESULTS FROM UNDERLYING (OPENSEARCH) WSE //
            start = System.currentTimeMillis();
            wseResults = new ArrayList<SearchResult>();
            OpenSearchRetriever retriever = null;

            if (urlTemplates.containsKey("application/rss+xml")) { // RSS RESULTS
                String template = urlTemplates.get("application/rss+xml");
                System.out.println("# Using 'application/rss+xml' template: " + template);
                retriever = new OpenSearchRetriever(template, "application/rss+xml", query);
            } else if (urlTemplates.containsKey("application/atom+xml")) { // ATOM RESULTS
                String template = urlTemplates.get("application/atom+xml");
                System.out.println("# Using 'application/atom+xml' template: " + template);
                retriever = new OpenSearchRetriever(template, "application/atom+xml", query);
            } else {
                System.out.println("# NO SUPPORTED TEMPLATE TYPES: " + urlTemplates.keySet().toString());
            }

            if (retriever != null) {
                retriever.setDesiredNumber(max_num_of_results_from_wse);
                retriever.retrieveResults();
                wseResults = retriever.getResults();
            }
        }

        for (int i = 0; i < wseResults.size(); i++) {
            wseResults.get(i).setRank(i + 1);
        }

        System.out.println(
                "=> Number of retrieved results: " + wseResults.size());
        statistics += "=> Number of retrieved results: " + wseResults.size() + "\n";
        end = System.currentTimeMillis() - start;

        System.out.println(
                "# Retrieving results from search engine in: " + end + " ms.");
        statistics += "# Retrieving results from search engine in: " + end + " ms.\n";


        // GET CONTENT OF ALL RESULTS //
        if (!only_snippets) {
            start = System.currentTimeMillis();

            PagesRetriever pageRetriever = new PagesRetriever(wseResults, max_num_of_results_from_wse);
            wseResults = pageRetriever.getWseResults();

            end = System.currentTimeMillis() - start;
            System.out.println("# Retrieving content of all results in: " + end + " ms.");
            statistics += "# Retrieving content of all results in: " + end + " ms.\n";
        }

        // EXECUTE CLUSTERING //
        if (clustering) {
            start = System.currentTimeMillis();

            Clustering clusteringComp = new Clustering(wseResults, query, only_snippets, numOfClusters, clusteringAlgorithm);
            clustersContent = clusteringComp.getClustersContent();

            end = System.currentTimeMillis() - start;
            System.out.println("# Clustering results in: " + end + " ms.");
            statistics += "# Clustering results in: " + end + " ms.\n";
        }


        // EXECUTE MINING //
        if (mining) {

            System.out.println("# Finding entities in results...");
            start = System.currentTimeMillis();

            Mining miningComp = new Mining(wseResults, query, acceptedCategories, endpoints, templateQueries);

            end = System.currentTimeMillis() - start;
            System.out.println("# Finding entities in: " + end + " ms.");
            statistics += "# Finding entities in: " + end + " ms.\n";


            // MINE QUERY AND GIVE BONUS TO THE FOUND ENTITIES
            if (mineQuery) {
                start = System.currentTimeMillis();
                System.out.println("# Mining query...");
                miningComp.mineQuery();
                end = System.currentTimeMillis() - start;
                System.out.println("# Mining query in: " + end + " ms.");
                statistics += "# Mining query in: " + end + " ms.\n";
            }



            //GIVE SCORE TO ELEMENTS
            start = System.currentTimeMillis();
            miningComp.giveRankToElements(max_num_of_results_from_wse);
            end = System.currentTimeMillis() - start;
            System.out.println("# Giving score to elements in: " + end + " ms.");
            statistics += "# Giving score to elements in: " + end + " ms.\n";


            // CREATE STRING OF EACH ENTITY //
            start = System.currentTimeMillis();
            miningComp.createEntitiesHTMLFormat();
            end = System.currentTimeMillis() - start;
            statistics += miningComp.getStatistics();
            System.out.println("# Creating string representations of all entities in: " + end + " ms.");
            statistics += "# Creating string representations of all entities in: " + end + " ms.\n";

            // GET THE ENTITIES //
            entities = miningComp.getEntities();

            // SORT THE CATEGORIES //
            start = System.currentTimeMillis();
            Collections.sort(entities);
            end = System.currentTimeMillis() - start;
            System.out.println("# Sorting entities and elements in: " + end + " ms.");
            statistics += "# Sorting entities and elements in: " + end + " ms.\n";

        }

        // CREATE STRING OF FIRST PAGE OF RESULTS //
        start = System.currentTimeMillis();
        createResultsPage();
        end = System.currentTimeMillis() - start;
        System.out.println("# Creating string of first page of results in: " + end + " ms.");
        statistics += "# Creating string of first page of results in: " + end + " ms.\n";
        // SAVE RESULTS TO LOG FILE //

        if (!only_snippets) {
            try {
                AllResults all = new AllResults(query.trim(), statistics, results_first_page, entities);
                String md5 = MD5.getMD5(query.trim());
                String file = Resources.MINING_RESULTS + md5 + ".em";
                ObjectOutputStream out;

                out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

                out.writeObject(all);
                out.flush();
                out.close();

                System.out.println("=> File " + file + " succesfully saved.");
            } catch (IOException ex) {
                IOSLog.writeErrorToLog(ex, "Bean_Search");
                System.out.println("*** ERROR TRYING TO SAVE THA FILE.");
                //Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Takes as parameters a clusteringComp and categories object and transforms
     * them to XML format
     *
     * @param clusteringComp a Clustering component
     * @param categories an ArrayList<Category>
     * @return a map which has two entries, one for each XML string.
     */
    public Map<String, String> createJsonObjects(Clustering clusteringComp, ArrayList<Category> categories) {
        System.out.println("------------Clustering----------------");
        XStream xstreamCluster = new XStream();
        xstreamCluster.alias("TreeNode", gr.forth.ics.isl.stellaclustering.util.TreeNode.class);
        xstreamCluster.alias("DefaultMutableTreeNode", javax.swing.tree.DefaultMutableTreeNode.class);
        String clusterignXML = "";
        if (clusteringComp == null) {
            clusterignXML = xstreamCluster.toXML(clusteringComp.getClusterer().getClusterTree());
        } else {
            System.out.println("Attention the clustering object (cluseringComp) is null!!!");
        }
        System.out.println("Clustering results in xml format: " + clusterignXML);

        System.out.println("-----------Mining---------------------");
        XStream xstream = new XStream();
        xstream.alias("Category", Category.class);
        xstream.alias("Entity", Entity.class);
        xstream.aliasField("entities", Category.class, "Entities");
        xstream.aliasField("id", Entity.class, "int");
        String miningXML = xstream.toXML(categories);
        System.out.println("Entity mining results in xml format: \n" + miningXML);

        Map<String, String> map = new HashMap<String, String>();
        map.put("clusterignXML", clusterignXML);
        map.put("miningXML", miningXML);

        return map;
    }

    public Bean_Search(String results) {
        this.results_first_page = results;
        wseResults = new ArrayList<SearchResult>();

        int num = 0;
        HTMLTag tagger = new HTMLTag(results);
        int i = tagger.getFirstTagIndex("div");
        while (i != -1) {
            String div_data = tagger.getFirstTagData("div", i - 1);

            HTMLTag tagger2 = new HTMLTag(div_data);

            String title = tagger2.getFirstTagDataContains("span", "one_result_title");
            title = HTMLTag.removeTags(title).trim();

            String descr = tagger2.getFirstTagDataContains("span", "one_result_descr");
            if (descr == null) {
                descr = "";
            }
            descr = HTMLTag.removeTags(descr).trim();

            String url = tagger2.getFirstTagDataContains("span", "one_result_url");
            url = HTMLTag.removeTags(url).trim();


            SearchResult result = new SearchResult(title, url, descr, num);
            wseResults.add(result);

            num++;
            i = tagger.getFirstTagIndex("div", i + 2);
        }
    }
    /* *********************************** */

    /* CONSTRUCTOR FOR GCUBE PORTLET */
    public Bean_Search(String query, int resultsStartOffset, boolean clustering, int numOfClusters, boolean mining, boolean only_snippets, String locator) throws GRS2ReaderException, URISyntaxException, GRS2BufferException, GRS2RecordDefinitionException {

        // INITIALIZE THE PARAMETERS //
        System.out.println("===========================");
        System.out.println("# Initializing query parameters...");
        this.query = query;
        System.out.println("=> Query: " + query);
        statistics += "=> Query: " + query + "\n";

        this.mining = mining;
        System.out.println("=> Do mining: " + mining);
        statistics += "=> Do mining: " + mining + "\n";

        this.clustering = clustering;
        System.out.println("=> Do clustering: " + clustering);
        statistics += "=> Do clustering: " + clustering + "\n";

        this.numOfClusters = numOfClusters;
        System.out.println("=> Number of desired clusters: " + numOfClusters);
        statistics += "=> Number of desired clusters: " + numOfClusters + "\n";

        this.only_snippets = only_snippets;
        System.out.println("=> Do only snippets: " + only_snippets);
        statistics += "=> Do only snippets: " + only_snippets + "\n";

        System.out.println("=> StartOffset is: " + resultsStartOffset);

        System.out.println("=> Locator: " + locator);

        long start, end;

        /* READ THE RESULT SET BASED ON THE LOCATOR AND FILL THE wseResults */
        start = System.currentTimeMillis();
        wseResults = new ArrayList<SearchResult>();
        System.out.println("\n# Start reading the result set...");
        System.out.println("  - Locator: " + locator);
        URI locURI = new URI(locator);
        ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(locURI);

        int numOfRecords = 0;
        GenericRecord rec;

        Iterator<GenericRecord> it = reader.iterator();
        while (it.hasNext()) {

            rec = it.next();

            String title = "", snippet = "", url = "";
            if (rec != null) {
                if (((StringField) rec.getField("title")) != null) {
                    title = ((StringField) rec.getField("title")).getPayload();
                }

                if ((StringField) rec.getField("snippet") != null) {
                    snippet = ((StringField) rec.getField("snippet")).getPayload();
                }
                url = "";
            } else {
                System.out.println("  - Attention the record " + numOfRecords + " is null!!");
            }

            SearchResult searchresult = new SearchResult(title, url, snippet, numOfRecords);
            wseResults.add(searchresult);

            numOfRecords++;
        }
        reader.close();
        System.out.println("  - Total Number of Records in the Result Set: " + numOfRecords);
        end = System.currentTimeMillis() - start;
        System.out.println("# Reading the result set in: " + end + " ms.");

        // EXECUTE CLUSTERING //
        if (clustering) {
            start = System.currentTimeMillis();

            Clustering clusteringComp = new Clustering(wseResults, query, only_snippets, numOfClusters, Resources.CLUSTERING_ALGORITHM);
            clustersContent = clusteringComp.getClustersContent();

            jsonClusteringResults = createClusterJsonString(query, clusteringComp.getClusterer(), resultsStartOffset);

            end = System.currentTimeMillis() - start;
            System.out.println("# Clustering results in: " + end + " ms.");
            statistics += "# Clustering results in: " + end + " ms.\n";
        }

        // EXECUTE MINING //
        if (mining) {

            System.out.println("# Finding entities in results...");
            start = System.currentTimeMillis();

            Mining miningComp = new Mining(wseResults, query, Resources.MINING_ACCEPTED_CATEGORIES, Resources.SPARQL_ENDPOINTS, Resources.SPARQL_TEMPLATES);

            end = System.currentTimeMillis() - start;
            System.out.println("# Finding entities in: " + end + " ms.");
            statistics += "# Finding entities in: " + end + " ms.\n";


            // MINE QUERY AND GIVE BONUS TO THE FOUND ENTITIES
            if (Resources.MINE_QUERY) {
                start = System.currentTimeMillis();
                System.out.println("# Mining query...");
                miningComp.mineQuery();
                end = System.currentTimeMillis() - start;
                System.out.println("# Mining query in: " + end + " ms.");
                statistics += "# Mining query in: " + end + " ms.\n";
            }

            //GIVE SCORE TO ELEMENTS
            start = System.currentTimeMillis();
            miningComp.giveRankToElements(max_num_of_results_from_wse);
            end = System.currentTimeMillis() - start;
            System.out.println("# Giving score to elements in: " + end + " ms.");
            statistics += "# Giving score to elements in: " + end + " ms.\n";


            // CREATE STRING OF EACH ENTITY //
//            start = System.currentTimeMillis();
//            miningComp.createEntitiesHTMLFormat();
//            end = System.currentTimeMillis() - start;
//            statistics += miningComp.getStatistics();
//            System.out.println("# Creating string representations of all entities in: " + end + " ms.");
//            statistics += "# Creating string representations of all entities in: " + end + " ms.\n";

            // GET THE ENTITIES //
            entities = miningComp.getEntities();

            // SORT THE CATEGORIES //
            start = System.currentTimeMillis();
            Collections.sort(entities);
            end = System.currentTimeMillis() - start;
            System.out.println("# Sorting entities and elements in: " + end + " ms.");
            statistics += "# Sorting entities and elements in: " + end + " ms.\n";

            start = System.currentTimeMillis();
            System.out.println("# Creating the JSON mining String...");
            jsonMiningResults = createMiningJSONString(entities, query, resultsStartOffset);
            end = System.currentTimeMillis() - start;
            System.out.println("# Creating the JSON mining String: " + end + " ms.");
        }



        if (clustering && mining) {
            jsonResults = "{ \"MiningResults\":" + jsonMiningResults + ", \"ClusteringResults\":" + jsonClusteringResults + "}";
        } else if (clustering && !mining) {
            jsonResults = "{ \"MiningResults\":" + "{}" + ", \"ClusteringResults\":" + jsonClusteringResults + "}";
        } else if (!clustering && mining) {
            jsonResults = "{ \"MiningResults\":" + jsonMiningResults + ", \"ClusteringResults\":" + "{}" + "}";
        } else {
            jsonResults = "{}";
        }




        // this.jsonResults = "{xa}";

    }
    /* ************************************** */

    /* CONSTRUCTOR FOR THE API */
    public Bean_Search(String searchSystem, String query, String descrDoc, String locator, int numOfResults, boolean mining, HashSet<String> categories, boolean clustering, int numOfClusters, String clusteringAlg, String typeOfResuls) throws GRS2RecordDefinitionException, GRS2RecordDefinitionException, GRS2BufferException, GRS2ReaderException, MalformedURLException, FileNotFoundException, UnsupportedEncodingException, IOException, URISyntaxException {

        this.statistics = "";
        this.exampleQuery = "";
        long start, end;

        // INITIALIZE THE PARAMETERS //
        this.query = query;
        System.out.println("=> Query: " + query);

        this.max_num_of_results_from_wse = numOfResults;
        System.out.println("=> Number of results to mine/cluster: " + max_num_of_results_from_wse);

        this.mining = mining;
        System.out.println("=> Do mining: " + mining);

        this.clustering = clustering;
        System.out.println("=> Do clustering: " + clustering);

        this.numOfClusters = numOfClusters;
        System.out.println("=> Number of desired clusters: " + numOfClusters);
        statistics += "=> Number of desired clusters: " + numOfClusters + "\n";

        if (typeOfResuls.equals("contents")) {
            this.only_snippets = false;
        } else {
            this.only_snippets = true;
        }
        System.out.println("=> Do only snippets: " + only_snippets);

        start = System.currentTimeMillis();
        if (searchSystem.toLowerCase().equals("figis")) {
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("owner", "fi");
            parameters.put("xml", "y");
            parameters.put("xml_no_subject", "");
            parameters.put("FORM_C", "AND");
            parameters.put("sortorder", "3");
            parameters.put("pub_year", "2011");

            FigisRetriever retriever = new FigisRetriever(query, max_num_of_results_from_wse);
            retriever.setParameters(parameters);
            retriever.retrieveResults();
            wseResults = retriever.getResults();
        } else if (searchSystem.toLowerCase().equals("ecoscope")) {
            HashMap<String, String> parameters = new HashMap<String, String>();

            EcoscopeRetriever retriever = new EcoscopeRetriever(query, max_num_of_results_from_wse);
            retriever.setParameters(parameters);
            retriever.retrieveResults();
            wseResults = retriever.getResults();

        } else if (searchSystem.toLowerCase().equals("gcube")) {
            /* READ THE RESULT SET BASED ON THE LOCATOR AND FILL THE wseResults */
            start = System.currentTimeMillis();
            wseResults = new ArrayList<SearchResult>();
            System.out.println("\n# Start reading the result set...");
            System.out.println("  - Locator: " + locator);
            URI locURI = new URI(locator); 
            ForwardReader<GenericRecord> reader = new ForwardReader<GenericRecord>(locURI);

            int numOfRecords = 0;
            GenericRecord rec;

            Iterator<GenericRecord> it = reader.iterator();
            while (it.hasNext()) {

                rec = it.next();

                String title = "", snippet = "", url = "";
                if (rec != null) {
                    if (((StringField) rec.getField("title")) != null) {
                        title = ((StringField) rec.getField("title")).getPayload();
                    }

                    if ((StringField) rec.getField("snippet") != null) {
                        snippet = ((StringField) rec.getField("snippet")).getPayload();
                    }
                    url = "";
                } else {
                    System.out.println("  - Attention the record " + numOfRecords + " is null!!");
                }

                SearchResult searchresult = new SearchResult(title, url, snippet, numOfRecords);
                wseResults.add(searchresult);

                numOfRecords++;
            }
            reader.close();
            System.out.println("  - Total Number of Records in the Result Set: " + numOfRecords);
            end = System.currentTimeMillis() - start;
            System.out.println("# Reading the result set in: " + end + " ms.");
        } else { // opensearch
            // use the 'description document'
            DescriptionDocument descrDocument = new DescriptionDocument(descrDoc);
            System.out.println("# OpenSearch Provider: " + descrDocument.getShortName() + " (" + descrDocument.getDescription() + ")");
            HashMap<String, String> urlTemplates = descrDocument.getUrlTemplates();

            end = System.currentTimeMillis() - start;
            System.out.println("# Reading OpenSearch description document in: " + end + " ms.");

            // GET TOP RESULTS FROM UNDERLYING (OPENSEARCH) WSE //
            start = System.currentTimeMillis();
            wseResults = new ArrayList<SearchResult>();
            OpenSearchRetriever retriever = null;

            if (urlTemplates.containsKey("application/rss+xml")) { // RSS RESULTS
                String template = urlTemplates.get("application/rss+xml");
                System.out.println("# Using 'application/rss+xml' template: " + template);
                retriever = new OpenSearchRetriever(template, "application/rss+xml", query);
            } else if (urlTemplates.containsKey("application/atom+xml")) { // ATOM RESULTS
                String template = urlTemplates.get("application/atom+xml");
                System.out.println("# Using 'application/atom+xml' template: " + template);
                retriever = new OpenSearchRetriever(template, "application/atom+xml", query);
            } else {
                System.out.println("# NO SUPPORTED TEMPLATE TYPES: " + urlTemplates.keySet().toString());
            }

            if (retriever != null) {
                retriever.setDesiredNumber(max_num_of_results_from_wse);
                retriever.retrieveResults();
                wseResults = retriever.getResults();
            }
        }

        for (int i = 0; i < wseResults.size(); i++) {
            wseResults.get(i).setRank(i + 1);
        }

        System.out.println(
                "=> Number of retrieved results: " + wseResults.size());
        end = System.currentTimeMillis() - start;

        System.out.println(
                "# Retrieving results from search engine in: " + end + " ms.");

        // GET CONTENT OF ALL RESULTS //
        if (!only_snippets) {
            start = System.currentTimeMillis();
            PagesRetriever pageRetriever = new PagesRetriever(wseResults, max_num_of_results_from_wse);
            wseResults = pageRetriever.getWseResults();
            end = System.currentTimeMillis() - start;
            System.out.println("# Retrieving content of all results in: " + end + " ms.");
        }


        // EXECUTE CLUSTERING //
        if (clustering) {
            start = System.currentTimeMillis();

            int clusteringAlgorithm = 3;
            if (clusteringAlg.equals("cl1")) {
                clusteringAlgorithm = 1;
            }
            if (clusteringAlg.equals("cl2")) {
                clusteringAlgorithm = 2;
            }
            if (clusteringAlg.equals("cl4")) {
                clusteringAlgorithm = 4;
            }
            if (clusteringAlg.equals("cl5")) {
                clusteringAlgorithm = 5;
            }
            clusteringComponent = new Clustering(wseResults, query, only_snippets, numOfClusters, clusteringAlgorithm);

            end = System.currentTimeMillis() - start;
            System.out.println("# Clustering results in: " + end + " ms.");
        }


        // EXECUTE MINING //
        if (mining) {

            System.out.println("# Finding entities in results...");
            start = System.currentTimeMillis();

            Mining miningComp = new Mining(wseResults, query, categories, Resources.SPARQL_ENDPOINTS, Resources.SPARQL_TEMPLATES);

            end = System.currentTimeMillis() - start;
            System.out.println("# Finding entities in: " + end + " ms.");


            //GIVE SCORE TO ELEMENTS
            start = System.currentTimeMillis();
            miningComp.giveRankToElements(max_num_of_results_from_wse);
            end = System.currentTimeMillis() - start;
            System.out.println("# Giving score to elements in: " + end + " ms.");

            // GET THE ENTITIES //
            entities = miningComp.getEntities();

            // SORT THE CATEGORIES //
            start = System.currentTimeMillis();
            Collections.sort(entities);
            end = System.currentTimeMillis() - start;
            System.out.println("# Sorting entities and elements in: " + end + " ms.");
        }

    }

    private void createResultsPage() {

        results_first_page = "";
        if (wseResults.isEmpty()) {
            results_first_page += "<h1>Sorry, no results! Try something else!</h1>";
            if (!exampleQuery.equals("")) {
                results_first_page += "Example query: <i><a onClick='document.getElementById(\"suggestion\").value = \"" + exampleQuery + "\"' href='Servlet_Search?query=" + exampleQuery + "&start=0&n=50&type=onlySnippets&mining=true&clustering=true&clnum=15'>" + exampleQuery + "</a></i>";
            }
        } else {

            //results_first_page += "<font class=\"numOfResults\">"+wseResults.size()+" results:</font>";

            for (int i = 0; i < wseResults.size(); i++) {
                SearchResult one = wseResults.get(i);

                String title = one.getTitle();
                String url = one.getUrl();
                String description = one.getDescription();

                String titleToShow = title;
                String hiddenTitle = "";
                String idTitle = "resultTitle" + i;
                String showTitleId = "resultShowTitle" + i;
                String script1 = "javascript:showAllText('" + idTitle + "', '" + showTitleId + "');";
                String showAllText = "";

                if (title.length() > 124) {
                    hiddenTitle = "<span id=\"" + idTitle + "\" style=\"display:none\">" + title.substring(124) + "</span>";
                    titleToShow = title.substring(0, 124) + hiddenTitle;
                    showAllText = "<span id=\"" + showTitleId + "\"><a href=\"" + script1 + "\" class=\"em_show_name_a\">...show all</a></span>";
                }

                String descrToShow = description;
                String hiddenDescr = "";
                String idDescr = "resultDescr" + i;
                String showDescrId = "resultShowDescr" + i;
                String script2 = "javascript:showAllText('" + idDescr + "', '" + showDescrId + "');";

                if (description.length() > 330) {
                    hiddenDescr = "<span id=\"" + idDescr + "\" style=\"display:none\">" + description.substring(328) + "</span>";
                    descrToShow = description.substring(0, 328) + "<span id=\"" + showDescrId + "\" align=\"right\" class=\"em_showAllText\"><a href=\"" + script2 + "\" class=\"em_show_name_a\">...show all</a></span>" + hiddenDescr;
                }

                /*  HIGHLIGHT QUERY WORDS
                 String q = query.trim();
                 while (q.contains("  ")) {
                 q = q.replace("  ", " ");
                 }
                 String [] qwords = q.split(" ");
                 for (String word : qwords) {
                 titleToShow = titleToShow.replace(word, "<b>"+word+"</b>");
                 titleToShow = titleToShow.replace(word.toUpperCase(), "<b>"+word.toUpperCase()+"</b>");
                 descrToShow = descrToShow.replace(word, "<b style='background-color:yellow'>"+word+"</b>");
                 descrToShow = descrToShow.replace(word.toUpperCase(), "<b style='background-color:yellow'>"+word.toUpperCase()+"</b>");
                 }
                 */


                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("ftp")) {
                    results_first_page += "<div class='one_result'><span class='one_result_title'><a href='Servlet_OpenResult?doc=" + i + "&doc_url=" + url + "'>" + titleToShow + "</a></span>" + showAllText;
                } else {
                    results_first_page += "<div class='one_result'><span class='one_result_title'>" + titleToShow + "</span>" + showAllText;
                }

                if (!descrToShow.trim().equals("")) {
                    results_first_page += "<br />";
                    results_first_page += "<span class='one_result_descr'>" + descrToShow + "</span>";
                }
                results_first_page += "<br />";
                results_first_page += "<span class='em_url'>" + url.replace("#", "-") + "</span>";
                if ((url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("ftp")) && !url.toLowerCase().endsWith(".jpg") && !url.toLowerCase().endsWith(".png") && !url.toLowerCase().endsWith(".gif") && !url.toLowerCase().endsWith(".jpeg")) {
                    results_first_page += " - <font><a class='em_minepage' href=javascript:minePage(" + i + ")>find its entities</a></font>";
                }
                if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png") || url.toLowerCase().endsWith(".gif") || url.toLowerCase().endsWith(".jpeg")) {
                    results_first_page += "<br /><img  border='0' src='" + url + "' class='imgOfDesc' />";
                }
                results_first_page += "</div><br />";
            }
        }
        results_first_page = results_first_page.replace("\n", " ").replace("\r", " ").replace("\t", " ");
    }

    /**
     * Get's the results of entity mining and creates a representation of them
     * in Json.
     *
     * @param categories
     * @param query
     * @param resultsStartOffset the offset of the first result in order to
     * update the references to correspond at the proper documents
     * @return
     */
    private String createMiningJSONString(ArrayList<Category> categories,
            String query, int resultsStartOffset) {

        JSONArray jsonCategories = new JSONArray();

        //System.out.println("-----------------");
        //System.out.println("All entities: \n");
        for (int i = 0; i < categories.size(); i++) {
            //	System.out.println("Category: " + categories.get(i).getName());
            //	System.out.println("Category: "	+ categories.get(i).getNum_of_different_docs());

            JSONArray jsonEntitiesArray = new JSONArray();
            JSONObject jsonCategory = new JSONObject();

            for (int j = 0; j < categories.get(i).getEntities().size(); j++) {
//                System.out.println("\tEntity: "
//                 + categories.get(i).getEntities().get(j).getName()
//                 + " - "
//                 + categories.get(i).getEntities().get(j).getDocIds());


                // Passes Entity's name to json format
                JSONObject jsonEntity = new JSONObject();
                jsonEntity.put("EntityName", categories.get(i).getEntities().get(j).getName());

                // Passes Entity's doclist to json format;
                JSONArray jsonDocArray = new JSONArray();
                for (int k = 0; k < categories.get(i).getEntities().get(j).getDocIds().size(); k++) {
                    jsonDocArray.add(categories.get(i).getEntities().get(j).getDocIds().get(k) + resultsStartOffset);
                }
                jsonEntity.put("DocList", jsonDocArray);

                // Passes Entity's rank value to json format;
                jsonEntity.put("Rank", categories.get(i).getEntities().get(j).getRank());

                // Adds jsonEntity object to Entities Json Object
                jsonEntitiesArray.add(jsonEntity);
            }

            // Passes Category's entities
            jsonCategory.put("Entities", jsonEntitiesArray);

            // Passes Category's name to json format
            jsonCategory.put("CategoryName", categories.get(i).getName());

            // Passes Category's num of Different Documents to json format
            jsonCategory.put("NumOfDiffDocs", categories.get(i).getNum_of_different_docs());

            // Passes Category's rank value to json Format
            jsonCategory.put("Rank", categories.get(i).getRank());

            // Passes Category to categories array at json Format
            jsonCategories.add(jsonCategory);
        }

        JSONObject MinedCategories = new JSONObject();
        MinedCategories.put("MinedCategories", jsonCategories);
        MinedCategories.put("Query", query);
        StringWriter out = new StringWriter();
        try {
            MinedCategories.writeJSONString(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonText = out.toString();
        //System.out.print("Mining JSON string: \n" + jsonText);

        return jsonText;
    }

    /**
     * Creates the clusters to Json format.
     *
     * @param query
     * @param clt
     * @param resultsStartOffset the offset of the first result in order to
     * update the references to correspond at the proper documents
     * @return
     */
    private String createClusterJsonString(String query, CLT_Creator clt, int resultsStartOffset) {
        JSONArray jsonClustersArray = new JSONArray();

        /* PRINT RESULTS */
        //System.out.println("Cluster Label Tree: \n");
        Enumeration enumer = clt.getClusterTree().preorderEnumeration();


        /* try {
         printClusterTreeRecursive(clt.getClusterTree(), 2);
         } catch (Exception e1) {
         // TODO Auto-generated catch block
         //e1.printStackTrace();
         }*/


        while (enumer.hasMoreElements()) {
            TreeNode node = (TreeNode) enumer.nextElement();

            /**
             * ** TEMPORARY ALLOW ONLY LEVEL 1 ***
             */
            if (node.getLevel() > 1) {
                continue;
            }
            //System.out.println(node.getTitle() + " " + node.getDocumentsList());

            // Passes Cluster's name to json format
            JSONObject jsonCluster = new JSONObject();
            jsonCluster.put("ClusterName", node.getTitle());

            // Passes Cluster's doclist to json format;
            JSONArray jsonDocArray = new JSONArray();
            for (int i = 0; i < node.getDocumentsList().size(); i++) {
                jsonDocArray.add(node.getDocumentsList().get(i) + resultsStartOffset);
            }
            jsonCluster.put("DocList", jsonDocArray);

            // In case that is a root for another tree
            /*
             Enumeration en = node.children();
             JSONArray jsonsubClusterArray = new JSONArray();
             while (en.hasMoreElements()) {
             TreeNode leftChild = (TreeNode) en.nextElement();
            
             // Passes sub Cluster's name to json format
             JSONObject jsonSubCluster = new JSONObject();
             jsonSubCluster.put("ClusterName", leftChild.getTitle());
            
            
             // Passes sub Cluster's doclist to json format;
             JSONArray jsonSubClusterDocArray = new JSONArray();
             for (int i = 0; i < leftChild.getDocumentsList().size(); i++) {
             jsonSubClusterDocArray.add(leftChild.getDocumentsList().get(i));
             }
             jsonSubCluster.put("DocList", jsonSubClusterDocArray);
            
             jsonsubClusterArray.add(jsonSubCluster);
             }
            
             jsonCluster.put("subCluster", jsonsubClusterArray);
             */


            // Passes Cluster to clusters array at json Format
            jsonClustersArray.add(jsonCluster);
        }

        System.out.println();

        JSONObject clusters = new JSONObject();
        clusters.put("Clusters", jsonClustersArray);
        clusters.put("Query", query);
        StringWriter out = new StringWriter();
        try {
            clusters.writeJSONString(out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String jsonText = out.toString();
        //System.out.print("Clustering json string: \n" + jsonText);

        return jsonText;
    }

    /**
     * Prints recursively a tree.
     *
     * @param subTree a tree node
     * @param spaces spaces to be printed before a node's title
     */
    public void printClusterTreeRecursive(TreeNode subTree, int spaces)
            throws Exception {
        if (subTree == null) {
            throw new NullPointerException("The tree is empty.");
        }

        for (int i = 0; i < spaces; i++) {
            System.out.print("  ");
        }

        Enumeration en = subTree.breadthFirstEnumeration();
        TreeNode enNode = ((TreeNode) en.nextElement());

        System.out.print(" *Title: \"" + enNode.getTitle() + "\" Id=" + enNode.getId());
        System.out.println(".Files:" + enNode.getUserObject());

        en = subTree.children();
        while (en.hasMoreElements()) {
            TreeNode leftChild = (TreeNode) en.nextElement();
            printClusterTreeRecursive(leftChild, spaces + 2);
        }
    }

    /* SETTERS AND GETTERS */
    public boolean isOnly_snippets() {
        return only_snippets;
    }

    public void setOnly_snippets(boolean only_snippets) {
        this.only_snippets = only_snippets;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResults_first_page() {
        return results_first_page;
    }

    public void setResults_first_page(String results_first_page) {
        this.results_first_page = results_first_page;
    }

    public int getMax_num_of_results_from_wse() {
        return max_num_of_results_from_wse;
    }

    public void setMax_num_of_results_from_wse(int max_num_of_results_from_wse) {
        this.max_num_of_results_from_wse = max_num_of_results_from_wse;
    }

    public String getClustersContent() {
        return clustersContent;
    }

    public void setClustersContent(String clustersContent) {
        this.clustersContent = clustersContent;
    }

    public int getNumOfClusters() {
        return numOfClusters;
    }

    public void setNumOfClusters(int numOfClusters) {
        this.numOfClusters = numOfClusters;
    }

    public boolean isClustering() {
        return clustering;
    }

    public void setClustering(boolean clustering) {
        this.clustering = clustering;
    }

    public boolean isMining() {
        return mining;
    }

    public void setMining(boolean mining) {
        this.mining = mining;
    }

    public ArrayList<Category> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Category> entities) {
        this.entities = entities;
    }

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public String getExampleQuery() {
        return exampleQuery;
    }

    public void setExampleQuery(String exampleQuery) {
        this.exampleQuery = exampleQuery;
    }

    public ArrayList<SearchResult> getWseResults() {
        return wseResults;
    }

    public void setWseResults(ArrayList<SearchResult> wseResults) {
        this.wseResults = wseResults;
    }

    public String getJsonClusteringResults() {
        return jsonClusteringResults;
    }

    public void setJsonClusteringResults(String jsonClusteringResults) {
        this.jsonClusteringResults = jsonClusteringResults;
    }

    public String getJsonMiningResults() {
        return jsonMiningResults;
    }

    public void setJsonMiningResults(String jsonMiningResults) {
        this.jsonMiningResults = jsonMiningResults;
    }

    public String getJsonResults() {
        return jsonResults;
    }

    public void setJsonResults(String jsonResults) {
        this.jsonResults = jsonResults;
    }

    public Clustering getClusteringComponent() {
        return clusteringComponent;
    }

    public void setClusteringComponent(Clustering clusteringComponent) {
        this.clusteringComponent = clusteringComponent;
    }
}
