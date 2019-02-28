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
package gr.forth.ics.isl.xsearch.inspecting;

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class SparqlRunner extends Thread {

    private String category;
    private String element;
    private String sparqlEndpoint;
    private String sparqlQueryTemplate;
    private String sparqlQueryPath;
    private String resultsString;
    private ArrayList<String> resultsVariables;
    private ArrayList<LinkedHashMap<String, String>> resultsData;
    public boolean finish = false;
    private HashSet<String> acceptedCategories;
    private HashMap<String, String> endpoints;
    private HashMap<String, String> templateQueries;

    public SparqlRunner(String category, String element, HashSet<String> acceptedCategories, HashMap<String, String> endpoints, HashMap<String, String> templateQueries) {

        super();

        this.category = category;
        this.element = element;
        this.acceptedCategories = acceptedCategories;
        this.endpoints = endpoints;
        this.templateQueries = templateQueries;
    }

    @Override
    public void run() {
        this.sparqlEndpoint = endpoints.get(category);
        this.sparqlQueryTemplate = readTemplate(templateQueries.get(category));
        createSparqlQueryPath();

        System.out.println("- - - - - - - - - - -");
        System.out.println("# Category: " + this.category);
        System.out.println("# Element: " + this.element);
        System.out.println("# SPARQL Endpoint: " + this.sparqlEndpoint);
        System.out.println("# SPARQL Template: " + this.sparqlQueryTemplate);
        System.out.println("# SPARQL QUERY PATH: " + sparqlQueryPath);

        runSparqlQuery();
        handleXMLresult();

        System.out.print("# Results: ");
        if (resultsData.isEmpty()) {
            System.out.println("-");
        } else {
            System.out.println("");
            int num = 1;
            for (HashMap resultSet : resultsData) {
                System.out.println((num++) + ". " + resultSet);
            }
        }

        System.out.println("- - - - - - - - - - -");

        finish = true;
    }

    private void handleXMLresult() {

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
    }

    private void createSparqlQueryPath() {
        try {

            String sparqlQuery = sparqlQueryTemplate.replace(Resources.TEMPLATE_PARAMETER, element);
            sparqlQuery = URLEncoder.encode(sparqlQuery, "utf8");
            sparqlQueryPath = sparqlEndpoint + sparqlQuery;

        } catch (UnsupportedEncodingException ex) {
            System.out.println("*** ERROR ENCODING SPARQL QUERY: ");
            Logger.getLogger(SparqlRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String readTemplate(String templatePath) {
        String templ = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(templatePath), "UTF8"));
            String line;
            while ((line = in.readLine()) != null) {
                templ += (line + " ");
            }
            in.close();
        } catch (Exception ex) {
            System.out.println("ERROR READING TEMPLATE FILE '" + templatePath + "'\n: " + ex.getMessage());
        }

        return templ;
    }

    private void runSparqlQuery() {

        try {

            try {

                URL url = new URL(sparqlQueryPath);
                URLConnection con = url.openConnection();
                con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
                String xml_content = "application/sparql-results+xml";
                //String json_content = "application/sparql-results+json";
                con.setRequestProperty("ACCEPT", xml_content);

                if (Resources.SPARQL_ENPOINTS_USERNAMES.containsKey(sparqlEndpoint) && Resources.SPARQL_ENPOINTS_PASSWORDS.containsKey(sparqlEndpoint)) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(Resources.SPARQL_ENPOINTS_USERNAMES.get(sparqlEndpoint), Resources.SPARQL_ENPOINTS_PASSWORDS.get(sparqlEndpoint).toCharArray());
                        }
                    });
                }

                InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "utf8");
                BufferedReader in = new BufferedReader(isr);

                String input;
                resultsString = "";
                while ((input = in.readLine()) != null) {
                    resultsString = resultsString + input + "\n";
                }

                in.close();
                isr.close();
                is.close();

                System.out.println("# SPARQL query was executed successfully!");

            } catch (Exception ex) {
                IOSLog.writeErrorToLog(ex, "SparqlRunner 1");
                System.out.println("# PROBLEM EXECUTING THE SPARQL QUERY! ERROR:" + ex.getMessage());
                Logger.getLogger(SparqlRunner.class.getName()).log(Level.SEVERE, null, ex);
            }



        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "SparqlRunner 2");
            Logger.getLogger(SparqlRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static LinkedHashMap<String, HashSet<String>> getProperties(String category, String uri, HashMap<String, String> endpoints) throws UnsupportedEncodingException {

        String endpoint = endpoints.get(category);
        String propertiesQuery = readTemplate(Resources.GET_PROPERTIES_TEMPLATE_QUERY);
        propertiesQuery = propertiesQuery.replace(Resources.GET_PROPERTIES_TEMPLATE_PARAMETER, uri);
        String sparqlQPath = endpoint + URLEncoder.encode(propertiesQuery, "utf8");
        System.out.println("# Query path: " + sparqlQPath);
        String results = runQuery(endpoint, sparqlQPath);

        HTMLTag tagger = new HTMLTag(results);
        String resultsStr = tagger.getFirstTagData("results");

        LinkedHashMap<String, HashSet<String>> resultsMap = new LinkedHashMap<String, HashSet<String>>();
        HTMLTag resultsTagger = new HTMLTag(resultsStr);
        int i1 = resultsTagger.getFirstTagIndex("result");
        while (i1 != -1) {
            String resultData = resultsTagger.getFirstTagData("result", i1);
            HTMLTag resTagger = new HTMLTag(resultData);

            String propNameData = resTagger.getFirstTagDataContains("binding", "name='propertyName'");
            if (propNameData == null) {
                propNameData = resTagger.getFirstTagDataContains("binding", "name=\"propertyName\"");
            }
            if (propNameData != null) {
                propNameData = HTMLTag.removeTags(propNameData).trim();
            }

            String propValData = resTagger.getFirstTagDataContains("binding", "name='propertyValue'");
            if (propValData == null) {
                propValData = resTagger.getFirstTagDataContains("binding", "name=\"propertyValue\"");
            }
            if (propValData != null) {
                propValData = HTMLTag.removeTags(propValData).trim();
            }

            if (resultsMap.containsKey(propNameData)) {
                HashSet<String> existingSet = resultsMap.get(propNameData);
                existingSet.add(propValData);
                resultsMap.put(propNameData, existingSet);
                //System.out.println("=> In existing: "+propNameData + "" + existingSet);

            } else {
                HashSet<String> newSet = new HashSet<String>();
                newSet.add(propValData);
                resultsMap.put(propNameData, newSet);
                //System.out.println("=> In new: "+propNameData + "" + newSet);
            }

            i1 = resultsTagger.getFirstTagIndex("result", i1 + 1);
        }

        return resultsMap;

    }

    public static String runQuery(final String sparqlEndpoint, String queryPath) {
        String results = "";

        try {

            URL url = new URL(queryPath);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            String xml_content = "application/sparql-results+xml";
            //String json_content = "application/sparql-results+json";
            con.setRequestProperty("ACCEPT", xml_content);

            if (Resources.SPARQL_ENPOINTS_USERNAMES.containsKey(sparqlEndpoint) && Resources.SPARQL_ENPOINTS_PASSWORDS.containsKey(sparqlEndpoint)) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Resources.SPARQL_ENPOINTS_USERNAMES.get(sparqlEndpoint), Resources.SPARQL_ENPOINTS_PASSWORDS.get(sparqlEndpoint).toCharArray());
                    }
                });
            }

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf8");
            BufferedReader in = new BufferedReader(isr);

            String input;
            while ((input = in.readLine()) != null) {
                results = results + input + "\n";
            }

            in.close();
            isr.close();
            is.close();

            System.out.println("# SPARQL query was executed successfully!");

        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "SparqlRunner 3");
            System.out.println("# PROBLEM EXECUTING THE SPARQL QUERY! ERROR:" + ex.getMessage());
            Logger.getLogger(SparqlRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

        return results;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public ArrayList<LinkedHashMap<String, String>> getResultsData() {
        return resultsData;
    }

    public void setResultsData(ArrayList<LinkedHashMap<String, String>> resultsData) {
        this.resultsData = resultsData;
    }

    public String getResultsString() {
        return resultsString;
    }

    public void setResultsString(String resultsString) {
        this.resultsString = resultsString;
    }

    public ArrayList<String> getResultsVariables() {
        return resultsVariables;
    }

    public void setResultsVariables(ArrayList<String> resultsVariables) {
        this.resultsVariables = resultsVariables;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public String getSparqlQueryPath() {
        return sparqlQueryPath;
    }

    public void setSparqlQueryPath(String sparqlQueryPath) {
        this.sparqlQueryPath = sparqlQueryPath;
    }

    public String getSparqlQueryTemplate() {
        return sparqlQueryTemplate;
    }

    public void setSparqlQueryTemplate(String sparqlQueryTemplate) {
        this.sparqlQueryTemplate = sparqlQueryTemplate;
    }
}
