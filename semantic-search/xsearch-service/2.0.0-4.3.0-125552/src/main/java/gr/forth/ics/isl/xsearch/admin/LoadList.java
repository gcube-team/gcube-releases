/*
 * 
 * Copyright 2012 FORTH-ICS-ISL (http://www.ics.forth.gr/isl/) 
 * 
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
package gr.forth.ics.isl.xsearch.admin;

import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class LoadList extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        String loggedin = (String) session.getAttribute("loggedin");
        if (loggedin == null) {
            loggedin = "no";
            session.setAttribute("loggedin", loggedin);
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        PrintWriter out = response.getWriter();
        out.print("");

        try {
            String endpoint = request.getParameter("endpoint");
            if (endpoint == null) {
                endpoint = "";
            }
            endpoint = URLDecoder.decode(endpoint, "utf-8");

            String query = request.getParameter("query");
            if (query == null) {
                query = "";
            }
            query = URLDecoder.decode(query, "utf-8");

            if (endpoint.trim().equals("")) {
                out.print("ATTENTION!! Empty SPARQL endpoint!");
                out.close();
                return;
            }

            if (query.trim().equals("")) {
                out.print("ATTENTION!! Empty SPARQL query!");
                out.close();
                return;
            }

            if (!query.toLowerCase().contains("select") || !query.toLowerCase().contains("where") || !query.contains("?") || !query.contains("{") || !query.contains("}")) {
                out.print("ATTENTION!! The SPARQL template query is not valid!");
                out.close();
                return;
            }


            String encodedQuery = URLEncoder.encode(query, "utf8");
            String encodedQuerypath = endpoint + encodedQuery;
            System.out.println("# Query path: " + encodedQuerypath);

            /* RUN SPARQL QUERY */
            URL url = new URL(encodedQuerypath);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            String xml_content = "application/sparql-results+xml";
            //String json_content = "application/sparql-results+json";
            con.setRequestProperty("ACCEPT", xml_content);

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf8");
            BufferedReader in = new BufferedReader(isr);

            String input;
            String resultsString = "";
            while ((input = in.readLine()) != null) {
                resultsString = resultsString + input + "\n";
            }

            in.close();
            isr.close();
            is.close();

            TreeSet<String> values = new TreeSet<String>();
            ArrayList<LinkedHashMap<String, String>> results = handleXMLresult(resultsString);
            for (LinkedHashMap<String, String> map : results) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    values.add(value);
                }
            }
            
            for (String value : values) {
                out.print(value);
                out.println();
            }


        } catch (Exception e) {
            out.print("ATTENTION!! " + e.getMessage().replace("\n", " "));
        }

        out.close();


    }
    
    private ArrayList<LinkedHashMap<String, String>> handleXMLresult(String resultsString) {

        HTMLTag tagger = new HTMLTag(resultsString);
        String headStr = tagger.getFirstTagData("head");
        String resultsStr = tagger.getFirstTagData("results");

        // FIND RESULTS' VARIABLES //
        ArrayList<String> resultsVariables = new ArrayList<String>();
        HTMLTag headTagger = new HTMLTag(headStr);
        int i = headTagger.getFirstTagIndex("variable");
        while (i != -1) {
            String variableCont = headTagger.getFirstTagContent("variable", i);
            String variable = HTMLTag.getContentAttribute("name", variableCont);
            resultsVariables.add(variable);
            i = headTagger.getFirstTagIndex("variable", i + 1);
        }


        // GET RESULTS //
        ArrayList<LinkedHashMap<String, String>> resultsData = new ArrayList<LinkedHashMap<String, String>>();
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
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
