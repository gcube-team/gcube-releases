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
package gr.forth.ics.isl.xsearch.api;

import gr.forth.ics.isl.stellaclustering.CLT_Creator;
import gr.forth.ics.isl.stellaclustering.util.TreeNode;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.xsearch.Bean_Search;
import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class processresults extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String searchsystem = request.getParameter("searchsystem");
        if (searchsystem == null) {
            searchsystem = "";
        }
        searchsystem = searchsystem.trim();

        if (searchsystem.equals("")) {
            response.sendError(400, "The value of the parameter 'searchsystem' is null or empty.");
            return;
        }

        if (!Resources.SUPPORTED_SEARCH_SYSTEMS.keySet().contains(searchsystem.toLowerCase())) {
            response.sendError(400, "The value of the parameter 'searchsystem' is not valid. Accepted values: " + Resources.SUPPORTED_SEARCH_SYSTEMS.keySet() + ".");
            return;
        }

        String locator = request.getParameter("locator");
        if (locator == null) {
            locator = "";
        }
        locator = locator.trim();
        locator = URLDecoder.decode(locator, "utf8");

        if (searchsystem.toLowerCase().equals("gcube") && locator.equals("")) {
            response.sendError(400, "The value of the parameter 'locator' (gCube locator) is NULL or empty. Provide a value or change the search system.");
            return;
        }

        String descrdoc = request.getParameter("descrdoc");
        if (descrdoc == null) {
            descrdoc = "";
        }
        descrdoc = descrdoc.trim();
        descrdoc = URLDecoder.decode(descrdoc, "utf8");

        if (searchsystem.toLowerCase().equals("opensearch") && descrdoc.equals("")) {
            response.sendError(400, "The value of the parameter 'descrdoc' (URL of OpenSearch Description Document) is NULL or empty. Provide a value or change the search system.");
            return;
        }


        String query = request.getParameter("query");
        if (query == null) {
            query = "";
        }
        query = query.trim();

        if (query.equals("") && !searchsystem.toLowerCase().equals("gcube")) {
            response.sendError(400, "The value of the parameter 'query' is null or empty.");
            return;
        }

        String numofresults = request.getParameter("numofresults");
        if (numofresults == null) {
            numofresults = "";
        }
        numofresults = numofresults.trim();

        int num = 100; //default value
        if (!numofresults.equals("")) {
            try {
                num = Integer.parseInt(numofresults);
            } catch (Exception e) {
                response.sendError(400, "Error in reading the value of the parameter 'numofresults'. Please check the value.");
                return;
            }
        }

        if (num <= 0) {
            response.sendError(400, "The value of the parameter 'numofresults' cannot be a negative number. Please check the value.");
            return;
        }

        String mining = request.getParameter("mining");
        if (mining == null) {
            mining = "";
        }
        mining = mining.trim();

        boolean allowMining = true; //default
        if (!mining.equals("")) {

            if (!mining.toLowerCase().equals("true") && !mining.toLowerCase().equals("false")) {
                response.sendError(400, "The value of the parameter 'mining' is not valid. Valid values: {true, false}.");
                return;
            }

            try {
                allowMining = Boolean.parseBoolean(mining);
            } catch (Exception e) {
                response.sendError(400, "Error in reading the value of the parameter 'mining'. Please check the value.");
                return;
            }
        }


        String categories = request.getParameter("categories");
        if (categories == null) {
            categories = "";
        }
        categories = categories.trim();

        HashSet<String> acceptedCategories = new HashSet<String>();
        if (categories.equals("")) {
            for (String c : Resources.MINING_ACCEPTED_CATEGORIES) {
                acceptedCategories.add(c);
            }
        } else {
            String[] categs = categories.split(";");
            for (String c : categs) {
                acceptedCategories.add(c.trim());
            }
        }

        if (!Resources.MINING_ALL_POSSIBLE_CATEGORIES.containsAll(acceptedCategories)) {
            response.sendError(400, "One or more of the provided categories are not currently supported by the entity mining component.");
            return;
        }

        String clustering = request.getParameter("clustering");
        if (clustering == null) {
            clustering = "";
        }
        clustering = clustering.trim();

        boolean allowClustering = true; //default
        if (!clustering.equals("")) {

            if (!clustering.toLowerCase().equals("true") && !clustering.toLowerCase().equals("false")) {
                response.sendError(400, "The value of the parameter 'clustering' is not valid. Valid values: {true, false}.");
                return;
            }

            try {
                allowClustering = Boolean.parseBoolean(clustering);
            } catch (Exception e) {
                response.sendError(400, "Error in reading the value of the parameter 'clustering'. Please check the value.");
                return;
            }
        }

        String numofclusters = request.getParameter("numofclusters");
        if (numofclusters == null) {
            numofclusters = "";
        }
        numofclusters = numofclusters.trim();

        int clusters = 15; //default value
        if (!numofclusters.equals("")) {
            try {
                clusters = Integer.parseInt(numofclusters);
            } catch (Exception e) {
                response.sendError(400, "Error in reading the value of the parameter 'numofclusters'. Please check the value.");
                return;
            }
        }

        if (clusters <= 0) {
            response.sendError(400, "The value of the parameter 'numofclusters' cannot be a negative number. Please check the value.");
            return;
        }

        String clusteringalg = request.getParameter("clusteringalg");
        if (clusteringalg == null) {
            clusteringalg = "";
        }
        clusteringalg = clusteringalg.trim();

        if (!clusteringalg.equals("")) {
            if (!Resources.SUPPORTED_CLUSTERING_ALGORITHMS.keySet().contains(clusteringalg)) {
                response.sendError(400, "The value of the parameter 'clusteringalg' is not valid. Valid values: " + Resources.SUPPORTED_CLUSTERING_ALGORITHMS.keySet() + ".");
                return;
            }
        } else {
            clusteringalg = "cl3"; //default
        }


        String typeofresults = request.getParameter("typeofresults");
        if (typeofresults == null) {
            typeofresults = "";
        }
        typeofresults = typeofresults.trim();


        if (!typeofresults.equals("")) {
            if (!typeofresults.equals("snippets") && !typeofresults.equals("contents")) {
                response.sendError(400, "The value of the parameter 'typeofresults' is not valid. Valid values: {snippets, contents}.");
                return;
            }
        } else {
            typeofresults = "snippets"; //default
        }

        String format = request.getParameter("format");
        if (format == null) {
            format = "";
        }
        format = format.trim();


        if (!format.equals("")) {
            if (!format.toLowerCase().equals("json") && !format.toLowerCase().equals("xml")) {
                response.sendError(400, "The value of the parameter 'format' is not valid. Valid values: {json, xml}.");
                return;
            }
        } else {
            format = "json"; //default
        }

        System.out.println("# Input:");
        System.out.println("searchsystem = " + searchsystem);
        System.out.println("descrdoc = " + descrdoc);
        System.out.println("locator = " + locator);
        System.out.println("query = " + query);
        System.out.println("numofresults = " + num);
        System.out.println("mining = " + allowMining);
        System.out.println("categories = " + acceptedCategories);
        System.out.println("clustering = " + allowClustering);
        System.out.println("numofclusters = " + clusters);
        System.out.println("clusteringalg = " + clusteringalg);
        System.out.println("typeofresults = " + typeofresults);
        System.out.println("format = " + format);

        Bean_Search bean;
        try {
            bean = new Bean_Search(searchsystem, query, descrdoc, locator, num, allowMining, acceptedCategories, allowClustering, clusters, clusteringalg, typeofresults);
        } catch (Exception e) {
            response.sendError(400, "Error: " + e.getMessage() + " - Please check the given parameters and try again.");
            return;
        }


        if (format.toLowerCase().equals("json")) {
            JSONArray json_rs = createResultsJSONArray(bean.getWseResults());

            JSONObject j = new JSONObject();
            j.put("topResults", json_rs);

            if (allowMining) {
                JSONArray json_em = createEntityMiningJSONArray(bean.getEntities());
                j.put("resultOfEntityMining", json_em);
            }

            if (allowClustering) {
                JSONArray json_cl = createClusteringJSONArray(bean.getClusteringComponent().getClusterer());
                j.put("resultOfClustering", json_cl);
            }

            JSONObject inputParams = new JSONObject();
            inputParams.put("searchsystem", searchsystem);
            inputParams.put("query", query);
            inputParams.put("descrdoc", descrdoc);
            inputParams.put("locator", locator);
            inputParams.put("numofresults", num);
            inputParams.put("mining", allowMining);

            JSONArray categs = new JSONArray();
            for (String categ : acceptedCategories) {
                categs.add(categ);
            }
            inputParams.put("categories", categs);
            inputParams.put("clustering", allowClustering);
            inputParams.put("numofclusters", clusters);
            inputParams.put("clusteringalg", clusteringalg);
            inputParams.put("typeofresults", typeofresults);
            inputParams.put("format", format);

            j.put("inputParameters", inputParams);
            j.put("date", IOSLog.getCurrentDate());

            JSONObject json = new JSONObject();
            json.put("result", j);

            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                out.print(json);
            } finally {
                out.close();
            }

        } else { // Create XML result
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("result");
            doc.appendChild(rootElement);

            Element inputParameters = doc.createElement("inputParameters");
            Element resultOfEntityMining = doc.createElement("resultOfEntityMining");
            Element resultOfClustering = doc.createElement("resultOfClustering");
            Element topResults = doc.createElement("topResults");
            Element date = doc.createElement("date");

            rootElement.appendChild(inputParameters);
            rootElement.appendChild(resultOfEntityMining);
            rootElement.appendChild(resultOfClustering);
            rootElement.appendChild(topResults);
            rootElement.appendChild(date);

            /* START: INPUT PARAMETERS */
            Element searchsystem_el = doc.createElement("searchsystem");
            Element query_el = doc.createElement("query");
            Element descrdoc_el = doc.createElement("descrdoc");
            Element locator_el = doc.createElement("locator");
            Element numofresults_el = doc.createElement("numofresults");
            Element mining_el = doc.createElement("mining");
            Element categories_el = doc.createElement("categories");
            Element clustering_el = doc.createElement("clustering");
            Element numofclusters_el = doc.createElement("numofclusters");
            Element clusteringalg_el = doc.createElement("clusteringalg");
            Element typeofresults_el = doc.createElement("typeofresults");
            Element format_el = doc.createElement("format");

            searchsystem_el.appendChild(doc.createTextNode(searchsystem));
            inputParameters.appendChild(searchsystem_el);

            query_el.appendChild(doc.createTextNode(query));
            inputParameters.appendChild(query_el);

            descrdoc_el.appendChild(doc.createTextNode(descrdoc));
            inputParameters.appendChild(descrdoc_el);

            locator_el.appendChild(doc.createTextNode(locator));
            inputParameters.appendChild(locator_el);

            numofresults_el.appendChild(doc.createTextNode("" + num));
            inputParameters.appendChild(numofresults_el);

            mining_el.appendChild(doc.createTextNode("" + allowMining));
            inputParameters.appendChild(mining_el);

            for (String categ : acceptedCategories) {
                Element categ_el = doc.createElement("categoryName");
                categ_el.appendChild(doc.createTextNode(categ));
                categories_el.appendChild(categ_el);
            }
            inputParameters.appendChild(categories_el);

            clustering_el.appendChild(doc.createTextNode("" + allowClustering));
            inputParameters.appendChild(clustering_el);

            numofclusters_el.appendChild(doc.createTextNode("" + clusters));
            inputParameters.appendChild(numofclusters_el);

            clusteringalg_el.appendChild(doc.createTextNode(clusteringalg));
            inputParameters.appendChild(clusteringalg_el);

            typeofresults_el.appendChild(doc.createTextNode(typeofresults));
            inputParameters.appendChild(typeofresults_el);

            format_el.appendChild(doc.createTextNode(format));
            inputParameters.appendChild(format_el);
            /* END: INPUT PARAMETERS */

            /* START: RESULT OF ENTITY MINING */
            Element categs_el = doc.createElement("categories");
            for (int i = 0; i < bean.getEntities().size(); i++) {

                Element categ_el = doc.createElement("category");
                categs_el.appendChild(categ_el);

                Element categName_el = doc.createElement("categoryName");
                categName_el.appendChild(doc.createTextNode(bean.getEntities().get(i).getName()));
                categ_el.appendChild(categName_el);

                Element categrank_el = doc.createElement("rank");
                categrank_el.appendChild(doc.createTextNode("" + (i + 1)));
                categ_el.appendChild(categrank_el);

                Element entities_el = doc.createElement("entities");
                categ_el.appendChild(entities_el);
                for (int j = 0; j < bean.getEntities().get(i).getEntities().size(); j++) {

                    Element entity_el = doc.createElement("entity");

                    Element entityName_el = doc.createElement("entityName");
                    String entityName = bean.getEntities().get(i).getEntities().get(j).getName();
                    entityName_el.appendChild(doc.createTextNode(entityName));
                    entity_el.appendChild(entityName_el);

                    Element documents_els = doc.createElement("documentsIDs");
                    for (int k = 0; k < bean.getEntities().get(i).getEntities().get(j).getDocIds().size(); k++) {
                        Element id_el = doc.createElement("id");
                        id_el.appendChild(doc.createTextNode("" + bean.getEntities().get(i).getEntities().get(j).getDocIds().get(k)));
                        documents_els.appendChild(id_el);
                    }
                    entity_el.appendChild(documents_els);

                    Element score_el = doc.createElement("score");
                    double score = bean.getEntities().get(i).getEntities().get(j).getRank();
                    score_el.appendChild(doc.createTextNode("" + score));
                    entity_el.appendChild(score_el);

                    entities_el.appendChild(entity_el);
                }
            }
            resultOfEntityMining.appendChild(categs_el);
            /* END: RESULT OF ENTITY MINING */

            /* START: RESULT OF CLUSTERING */

            Element clusters_el = doc.createElement("clusters");
            int numCl = 1;
            Enumeration enumer = bean.getClusteringComponent().getClusterer().getClusterTree().preorderEnumeration();
            while (enumer.hasMoreElements()) {
                TreeNode node = (TreeNode) enumer.nextElement();

                if (node.getLevel() > 1) { // currently supported only level 1
                    continue;
                }

                if (node.isRoot()) {
                    continue;
                }

                Element cluster_el = doc.createElement("cluster");
                Element clusterLabel_el = doc.createElement("clusterLabel");
                clusterLabel_el.appendChild(doc.createTextNode(node.getTitle()));
                cluster_el.appendChild(clusterLabel_el);

                Element clusterRank_el = doc.createElement("rank");
                clusterRank_el.appendChild(doc.createTextNode("" + numCl++));
                cluster_el.appendChild(clusterRank_el);

                Element documents_els = doc.createElement("documentsIDs");
                for (int i = 0; i < node.getDocumentsList().size(); i++) {
                    Element id_el = doc.createElement("id");
                    id_el.appendChild(doc.createTextNode("" + (node.getDocumentsList().get(i) - 1)));
                    documents_els.appendChild(id_el);
                }
                cluster_el.appendChild(documents_els);

                clusters_el.appendChild(cluster_el);
            }
            resultOfClustering.appendChild(clusters_el);
            /* END: RESULT OF CLUSTERING */

            /* START: TOP RESULTS */
            for (int i = 0; i < bean.getWseResults().size(); i++) {
                SearchResult result = bean.getWseResults().get(i);

                Element result_el = doc.createElement("result");
                topResults.appendChild(result_el);

                Element title_el = doc.createElement("title");
                title_el.appendChild(doc.createTextNode(result.getTitle()));
                result_el.appendChild(title_el);

                Element description_el = doc.createElement("description");
                description_el.appendChild(doc.createTextNode(result.getDescription()));
                result_el.appendChild(description_el);

                Element url_el = doc.createElement("url");
                url_el.appendChild(doc.createTextNode(result.getUrl()));
                result_el.appendChild(url_el);

                Element rank_el = doc.createElement("rank");
                rank_el.appendChild(doc.createTextNode("" + result.getRank()));
                result_el.appendChild(rank_el);

                Element id_el = doc.createElement("id");
                id_el.appendChild(doc.createTextNode("" + i));
                result_el.appendChild(id_el);
            }
            /* END: TOP RESULTS */

            /* START: DATE */
            date.appendChild(doc.createTextNode(IOSLog.getCurrentDate()));
            /* END: DATE */


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                StreamResult result = new StreamResult(out);
                transformer.transform(source, result);

            } finally {
                out.close();
            }
        }
        System.out.println("# PROCESS RESULTS - FINISHED!");
    }

    /**
     * Creates a JSON representation of the identified entities
     *
     * @param categories The result of entity mining (entities grouped in
     * categories).
     * @return The string of the JSON object
     */
    private JSONArray createEntityMiningJSONArray(ArrayList<Category> categories) {

        JSONArray jsonCategories = new JSONArray();

        for (int i = 0; i < categories.size(); i++) {
            //	System.out.println("Category: " + categories.get(i).getName());
            //	System.out.println("Category: "	+ categories.get(i).getNum_of_different_docs());

            JSONArray jsonEntitiesArray = new JSONArray();
            JSONObject jsonCategory = new JSONObject();

            for (int j = 0; j < categories.get(i).getEntities().size(); j++) {

                JSONObject jsonEntity = new JSONObject();
                jsonEntity.put("entityName", categories.get(i).getEntities().get(j).getName());

                // Passes Entity's doclist to json format;
                JSONArray jsonDocArray = new JSONArray();
                for (int k = 0; k < categories.get(i).getEntities().get(j).getDocIds().size(); k++) {
                    jsonDocArray.add(categories.get(i).getEntities().get(j).getDocIds().get(k));
                }
                jsonEntity.put("documentsIDs", jsonDocArray);

                // Passes Entity's rank value to json format;
                jsonEntity.put("score", categories.get(i).getEntities().get(j).getRank());

                // Adds jsonEntity object to Entities Json Object
                jsonEntitiesArray.add(jsonEntity);
            }

            // Passes Category's entities
            jsonCategory.put("entities", jsonEntitiesArray);

            // Passes Category's name to json format
            jsonCategory.put("categoryName", categories.get(i).getName());

            // Passes Category's rank value to json Format
            jsonCategory.put("rank", i + 1);

            // Passes Category to categories array at json Format
            jsonCategories.add(jsonCategory);
        }

        return jsonCategories;
    }

    /**
     * Creates a JSON representation of the Cluster Label Tree
     *
     * @param clt The Cluster Label Tree
     * @return The string of the JSON object
     */
    private JSONArray createClusteringJSONArray(CLT_Creator clt) {

        JSONArray jsonClustersArray = new JSONArray();

        Enumeration enumer = clt.getClusterTree().preorderEnumeration();
        int num = 1;
        while (enumer.hasMoreElements()) {
            TreeNode node = (TreeNode) enumer.nextElement();

            if (node.getLevel() > 1) {
                continue;
            }

            if (node.isRoot()) {
                continue;
            }

            // Passes Cluster's name to json format
            JSONObject jsonCluster = new JSONObject();
            jsonCluster.put("clusterLabel", node.getTitle());
            jsonCluster.put("rank", num++);

            // Passes Cluster's doclist to json format;
            JSONArray jsonDocArray = new JSONArray();
            for (int i = 0; i < node.getDocumentsList().size(); i++) {
                jsonDocArray.add(node.getDocumentsList().get(i) - 1);
            }
            jsonCluster.put("documentsIDs", jsonDocArray);
            jsonClustersArray.add(jsonCluster);
        }

        return jsonClustersArray;
    }

    /**
     * Creates a JSON representation of the top-K results
     *
     * @param wseResults The top-K search results
     * @return The string of the JSON object
     */
    private JSONArray createResultsJSONArray(ArrayList<SearchResult> wseResults) {

        JSONArray jsonResultsArray = new JSONArray();
        for (int i = 0; i < wseResults.size(); i++) {
            SearchResult result = wseResults.get(i);

            JSONObject jsonResult = new JSONObject();
            jsonResult.put("title", result.getTitle());
            jsonResult.put("description", result.getDescription());
            jsonResult.put("url", result.getUrl());
            jsonResult.put("rank", result.getRank());
            jsonResult.put("id", i);
            jsonResultsArray.add(jsonResult);
        }

        return jsonResultsArray;
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MalformedURLException, FileNotFoundException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(processresults.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(processresults.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
