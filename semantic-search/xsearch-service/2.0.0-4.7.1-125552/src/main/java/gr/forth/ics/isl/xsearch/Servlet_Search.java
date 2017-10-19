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

import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
public class Servlet_Search extends HttpServlet {

    private String query;
    private int n;
    private boolean mining;
    private boolean clustering;
    private boolean only_snippets;
    private int numOfClusters;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        synchronized (this) {

            response.setContentType("text/html;charset=UTF-8");

            long start, end;
            start = System.currentTimeMillis();

            HttpSession session = request.getSession();

            /* INITIALIZE CONFIGURATION */
            String descrDoc = (String) session.getAttribute("descrDoc");
            if (descrDoc == null) {
                descrDoc = Resources.DESCRIPTIONDOCUMENT;
            }

            int clusteringAlgorithm = Resources.CLUSTERING_ALGORITHM;
            String clustAlg = (String) session.getAttribute("clustAlg");
            if (clustAlg != null) {
                clusteringAlgorithm = Integer.parseInt(clustAlg);
            }

            boolean mineQuery = Resources.MINE_QUERY;
            String mineQ = (String) session.getAttribute("mineQuery");
            if (mineQ != null) {
                mineQuery = Boolean.parseBoolean(mineQ);
            }

            HashSet<String> acceptedCategories = (HashSet<String>) session.getAttribute("acceptedCategories");
            if (acceptedCategories == null) {
                acceptedCategories = new HashSet<String>();
                acceptedCategories.addAll(Resources.MINING_ACCEPTED_CATEGORIES);
            }

            HashMap<String, String> endpoints = (HashMap<String, String>) session.getAttribute("endpoints");
            if (endpoints == null) {
                endpoints = new HashMap<String, String>();
                endpoints.putAll(Resources.SPARQL_ENDPOINTS);
            }

            HashMap<String, String> templateQueries = (HashMap<String, String>) session.getAttribute("templateQueries");
            if (templateQueries == null) {
                templateQueries = new HashMap<String, String>();
                templateQueries.putAll(Resources.SPARQL_TEMPLATES);
            }


            /* *********************** */

            query = request.getParameter("query");
            if (query == null) {
                query = "";
            }
            session.setAttribute("submitted_query", query);

            String num = request.getParameter("n");
            try {
                n = Integer.parseInt(num);
            } catch (Exception e) {
                n = 50;
            }

            String type = request.getParameter("type");
            if (type.equals("fullContent")) {
                only_snippets = false;
            } else {
                only_snippets = true;
            }


            String mining_checkbox = request.getParameter("mining");
            if (mining_checkbox == null) {
                mining_checkbox = "false";
            }

            if (mining_checkbox.toLowerCase().equals("true")) {
                mining = true;
            } else {
                mining = false;
            }

            String clustering_checkbox = request.getParameter("clustering");
            if (clustering_checkbox == null) {
                clustering_checkbox = "false";
            }

            if (clustering_checkbox.toLowerCase().equals("true")) {
                clustering = true;
            } else {
                clustering = false;
            }

            String clnum = request.getParameter("clnum");
            if (clnum == null) {
                if (clustering) {
                    numOfClusters = 15;
                } else {
                    numOfClusters = 0;
                }
            } else {
                numOfClusters = Integer.parseInt(clnum);
            }


            Bean_Search results = new Bean_Search(query, n, clustering, numOfClusters, mining, only_snippets, descrDoc, clusteringAlgorithm, mineQuery, acceptedCategories, endpoints, templateQueries);
            session.setAttribute("entities", results);
            session.setAttribute("docs", "");
            session.setAttribute("submitted_query", query);
            int numOfResults = results.getWseResults().size();

            end = System.currentTimeMillis() - start;
            System.out.println("# TOTAL TIME: " + end + " ms.");
            System.out.println("--------");

            updateLog(request, query, n, mining, clustering, only_snippets, numOfClusters, numOfResults, end);


            String destination = "search.jsp";
            RequestDispatcher dispatcher = request.getRequestDispatcher(destination);
            dispatcher.forward(request, response);

        }

    }

    private void updateLog(HttpServletRequest request, String q, int num, boolean mining, boolean clustering, boolean only_snippets, int clnum, int numOfResults, long time) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + q.trim() + "\tSUBMITTED NUM=" + num + " MINING=" + mining + " CLUSTERING=" + clustering + " ONLY_SNIPPETS=" + only_snippets + " CLNUM=" + clnum + " NUM_OF_RETURNED_RESULTS=" + numOfResults + " RETRIEVAL_TIME=" + time + "ms";

        IOSLog.writeToLog(line);
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

        processRequest(request, response);

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

        processRequest(request, response);

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
