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

import gr.forth.ics.isl.xsearch.Triple;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import gr.forth.ics.isl.xsearch.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class link extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParserConfigurationException, TransformerConfigurationException {

        String entityName = request.getParameter("name");
        if (entityName == null) {
            entityName = "";
        }
        entityName = entityName.trim();

        if (entityName.equals("")) {
            response.sendError(400, "The value of the parameter 'name' is null or empty.");
            return;
        }


        String categoryName = request.getParameter("category");
        if (categoryName == null) {
            categoryName = "";
        }
        categoryName = categoryName.trim();

        if (categoryName.trim().equals("")) {
            response.sendError(400, "The value of the parameter 'category' is null or empty.");
            return;
        }

        if (!Resources.MINING_ALL_POSSIBLE_CATEGORIES.contains(categoryName)) {
            response.sendError(400, "The category '" + categoryName + "' is not supported by X-Search.");
            return;
        }


        String endpoint = request.getParameter("endpoint");
        if (endpoint == null) {
            endpoint = "";
        }
        endpoint = endpoint.trim();

        String tquery = request.getParameter("tquery");
        if (tquery == null) {
            tquery = "";
        }
        tquery = tquery.trim();

        if ((endpoint.equals("") && !tquery.equals("")) || (!endpoint.equals("") && tquery.equals(""))) { // tquery is given, but endpoint is not given OR endpoint is given, but tquery is not given
            response.sendError(400, "The value of the parameter 'endpoint' or of the parameter 'tquery' is null or empty.");
            return;
        }


        if (!tquery.equals("") && !tquery.toLowerCase().contains("<entity>")) {
            response.sendError(400, "The SPARQL template query must contain the template parameter <ENTITY>.");
            return;
        }

        String format = request.getParameter("format");
        if (format == null) {
            format = "";
        }
        format = format.trim();

        if (!format.equals("")) {
            if (!format.toLowerCase().equals("json") && !format.toLowerCase().equals("xml") && !format.toLowerCase().equals("csv")) {
                response.sendError(400, "The value of the parameter 'format' is not valid. Valid values: {json, xml, csv}.");
                return;
            }
        } else {
            format = "json"; //default
        }


        if (endpoint.equals("")) {
            endpoint = Resources.SPARQL_ENDPOINTS.get(categoryName);
            if (endpoint == null) {
                endpoint = "";
            }
            endpoint = endpoint.trim();
            if (endpoint.equals("")) {
                // No Endpoint in the current configuration
                System.out.println("# NO 'SPARQL ENDPOINT' IN THE CURRENT CONFIGURATION!");
                response.sendError(400, "The current configuration of X-Search does not provide a SPARQL endpoint for the category '" + categoryName + "'. Please provide a SPARQL endpoint (&endpoint=...) and a SPARQL template query (&tquery=...).");
                return;
            }
        }
        System.out.println("# Endpoint = " + endpoint);

        if (tquery.equals("")) {
            tquery = Resources.SPARQL_TEMPLATES.get(categoryName);
            if (tquery == null) {
                tquery = "";
            }
            tquery = tquery.trim();
            if (tquery.equals("")) {
                System.out.println("# NO 'TEMPLATE QUERY' IN THE CURRENT CONFIGURATION!");
                response.sendError(400, "The current configuration of X-Search does not provide a SPARQL template query for the category '" + categoryName + "'. Please provide a SPARQL endpoint (&endpoint=...) and a SPARQL template query (&tquery=...).");
                return;
            }
            tquery = Util.readSPARQLQuery(tquery);
        }
        System.out.println("# Template Query = " + tquery);

        String resultString = runQuery(entityName, endpoint, tquery);
        ArrayList<LinkedHashMap<String, String>> resultsData = handleXMLresult(resultString);


        if (format.toLowerCase().equals("csv")) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                System.out.print("# Results: ");
                if (resultsData.isEmpty()) {
                    System.out.println("-");
                } else {
                    System.out.println("");
                    for (HashMap resultSet : resultsData) {
                        String resultToPrint = resultSet.toString().replace("{", "").replace("}", "").replace(", ", "\t");
                        out.println(resultToPrint);
                    }
                }
            } finally {
                out.close();
            }
        } else if (format.toLowerCase().equals("xml")) {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("results");
            doc.appendChild(rootElement);

            for (HashMap resultSet : resultsData) {
                
                Element resultEl = doc.createElement("result");
                for (Object k : resultSet.keySet()) {
                    
                    String key = (String) k; 
                    String value = (String) resultSet.get(key);
  
                    Element keyEl = doc.createElement(key);
                    keyEl.appendChild(doc.createTextNode(value));
                    resultEl.appendChild(keyEl);
                
                }
                rootElement.appendChild(resultEl);
            }
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();

            StreamResult result = new StreamResult(out);
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
            }

            out.close();

        } else {

            JSONObject json = new JSONObject();
            JSONArray results = new JSONArray();

            for (HashMap resultSet : resultsData) {
                
                JSONObject result = new JSONObject();
                for (Object k : resultSet.keySet()) {
                    
                    String key = (String) k; 
                    String value = (String) resultSet.get(key);
  
                    result.put(key, value);
                }
                results.add(result);
            }

            json.put("results", results);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print(json);
            out.close();
        }

        System.out.println("# LINK - FINISHED!");
    }

    private String runQuery(String entityName, String endpoint, String tquery) throws UnsupportedEncodingException, MalformedURLException, IOException {

        String query = tquery.replace(Resources.TEMPLATE_PARAMETER, entityName);

        if (!endpoint.toLowerCase().endsWith("?query=")) {
            endpoint = endpoint + "?query=";
        }
        String sparqlQueryPath = endpoint + URLEncoder.encode(query, "utf8");
        System.out.println("# SPARQL Query Path = " + sparqlQueryPath);

        URL url = new URL(sparqlQueryPath);
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
        String xml_content = "application/sparql-results+xml";
        con.setRequestProperty("ACCEPT", xml_content);

        if (Resources.SPARQL_ENPOINTS_USERNAMES.containsKey(endpoint) && Resources.SPARQL_ENPOINTS_PASSWORDS.containsKey(endpoint)) {
            final String username = Resources.SPARQL_ENPOINTS_USERNAMES.get(endpoint);
            final String password = Resources.SPARQL_ENPOINTS_PASSWORDS.get(endpoint);

            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }

        InputStream is = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf8");
        BufferedReader in = new BufferedReader(isr);

        String input;
        String resultString = "";
        while ((input = in.readLine()) != null) {
            resultString = resultString + input + "\n";
        }

        in.close();
        isr.close();
        is.close();
        System.out.println("# SPARQL query was executed successfully!");

        return resultString;
    }

    private ArrayList<LinkedHashMap<String, String>> handleXMLresult(String resultsString) {

        ArrayList<String> resultsVariables;
        ArrayList<LinkedHashMap<String, String>> resultsData;

        HTMLTag tagger = new HTMLTag(resultsString);
        String headStr = tagger.getFirstTagData("head");
        String resultsStr = tagger.getFirstTagData("results");

        // FIND RESULTS' VARIABLES //
        resultsVariables = new ArrayList<String>();
        HTMLTag headTagger = new HTMLTag(headStr);
        int i = headTagger.getFirstTagIndex("variable");
        while (i != -1) {
            String variableCont = headTagger.getFirstTagContent("variable", i);
            String variable = HTMLTag.getContentAttribute("name", variableCont);
            resultsVariables.add(variable);
            i = headTagger.getFirstTagIndex("variable", i + 1);
        }


        // GET RESULTS //
        resultsData = new ArrayList<LinkedHashMap<String, String>>();
        HTMLTag resultsTagger = new HTMLTag(resultsStr);
        i = resultsTagger.getFirstTagIndex("result");
        while (i != -1) {
            String resultData = resultsTagger.getFirstTagData("result", i);
            HTMLTag resTagger = new HTMLTag(resultData);

            LinkedHashMap<String, String> resultMap = new LinkedHashMap<String, String>();
            for (String variable : resultsVariables) {
                String bindingData = resTagger.getFirstTagDataContains("binding", "\"" + variable + "\"");
                if (bindingData == null) {
                    bindingData = resTagger.getFirstTagDataContains("binding", "'" + variable + "'");
                }
                if (bindingData != null) {
                    bindingData = HTMLTag.removeTags(bindingData).trim();
                    resultMap.put(variable, bindingData);
                    //System.out.println("BINDIND DATA OF '"+variable+"': "+bindingData);
                }
            }
            resultsData.add(resultMap);
            i = resultsTagger.getFirstTagIndex("result", i + 1);
        }

        return resultsData;
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
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(link.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(link.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(link.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(link.class.getName()).log(Level.SEVERE, null, ex);
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
