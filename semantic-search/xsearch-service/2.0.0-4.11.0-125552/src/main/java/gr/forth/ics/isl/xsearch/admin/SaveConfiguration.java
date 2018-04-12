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

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
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
public class SaveConfiguration extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        String ses = request.getParameter("ses");
        if (ses == null) {
            ses = "";
        }

        if (!ses.equals("y")) {
            String loggedin = (String) session.getAttribute("loggedin");
            if (loggedin == null) {
                loggedin = "no";
                session.setAttribute("loggedin", loggedin);
                RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }

        PrintWriter out = response.getWriter();
        out.print("");

        try {

            Random random = new Random();
            int rand = random.nextInt(100);
            String id = IOSLog.getCurrentDate().replace("/", "").replace(" ", "").replace(":", "") + rand;
            String filename = "conf" + id + ".properties";
            String propertiesFile = Resources.CONFIGURATIONS_FOLDER + filename;

            File file = new File(propertiesFile);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write("# X-SEARCH CONFIGURATION FILE");
            bw.write("\n");

            bw.write("# " + IOSLog.getCurrentDate());
            bw.write("\n");

            if (ses.equals("y")) {
                String descrDoc = (String) session.getAttribute("descrDoc");
                if (descrDoc == null) {
                    descrDoc = Resources.DESCRIPTIONDOCUMENT;
                }
                bw.write("gr.forth.ics.isl.xsearch.resources.opensearch.descriptionDocument = " + descrDoc);
                bw.write("\n");

                int clusteringAlgorithm = Resources.CLUSTERING_ALGORITHM;
                String clustAlg = (String) session.getAttribute("clustAlg");
                if (clustAlg != null) {
                    clusteringAlgorithm = Integer.parseInt(clustAlg);
                }
                bw.write("gr.forth.ics.isl.xsearch.resources.clustering.clusteringAlgorithm = " + clusteringAlgorithm);
                bw.write("\n");

                boolean mineQuery = Resources.MINE_QUERY;
                String mineQ = (String) session.getAttribute("mineQuery");
                if (mineQ != null) {
                    mineQuery = Boolean.parseBoolean(mineQ);
                }
                bw.write("gr.forth.ics.isl.xsearch.resources.mining.mineQuery = " + mineQuery);
                bw.write("\n");

                HashSet<String> acceptedCategories = (HashSet<String>) session.getAttribute("acceptedCategories");
                if (acceptedCategories == null) {
                    acceptedCategories = new HashSet<String>();
                    acceptedCategories.addAll(Resources.MINING_ACCEPTED_CATEGORIES);
                }
                bw.write("gr.forth.ics.isl.xsearch.resources.mining.acceptedCategories = " + acceptedCategories.toString().replace("[", "").replace("]", ""));
                bw.write("\n");
                bw.write("\n");
            } else {
                bw.write("gr.forth.ics.isl.xsearch.resources.opensearch.descriptionDocument = " + Resources.DESCRIPTIONDOCUMENT);
                bw.write("\n");

                bw.write("gr.forth.ics.isl.xsearch.resources.clustering.clusteringAlgorithm = " + Resources.CLUSTERING_ALGORITHM);
                bw.write("\n");

                bw.write("gr.forth.ics.isl.xsearch.resources.mining.mineQuery = " + Resources.MINE_QUERY);
                bw.write("\n");

                bw.write("gr.forth.ics.isl.xsearch.resources.mining.acceptedCategories = " + Resources.MINING_ACCEPTED_CATEGORIES.toString().replace("[", "").replace("]", ""));
                bw.write("\n");
                bw.write("\n");
            }

            if (ses.equals("y")) {
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

                for (String category : endpoints.keySet()) {
                    bw.write("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + category + " = " + endpoints.get(category));
                    bw.write("\n");
                    bw.write("\n");

                    Random ran = new Random();
                    int r = ran.nextInt(100);

                    String cat = category.toLowerCase().replaceAll("[^a-zA-Z]+", "_");
                    String pfilename = cat + "_" + IOSLog.getCurrentDate().replace("/", "").replace(" ", "").replace(":", "") + "_" + r + ".template";

                    String templateFilename = Resources.CONFIGURATIONS_FOLDER;
                    if (!Resources.CONFIGURATIONS_FOLDER.endsWith("/") && !Resources.CONFIGURATIONS_FOLDER.endsWith("\\")) {
                        templateFilename += "/";
                    }
                    templateFilename += pfilename;

                    ChangeTemplate.writeTemplateQuery(category, HTMLTag.readFile(templateQueries.get(category)), templateFilename);
                    bw.write("gr.forth.ics.isl.xsearch.resources.entityenrichment.templatequery." + category + " = " + templateFilename);
                    bw.write("\n");
                }

            } else {
                for (String category : Resources.SPARQL_ENDPOINTS.keySet()) {
                    bw.write("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + category + " = " + Resources.SPARQL_ENDPOINTS.get(category));
                    bw.write("\n");
                    bw.write("\n");

                    Random ran = new Random();
                    int r = ran.nextInt(100);

                    String cat = category.toLowerCase().replaceAll("[^a-zA-Z]+", "_");
                    String pfilename = cat + "_" + IOSLog.getCurrentDate().replace("/", "").replace(" ", "").replace(":", "") + "_" + r + ".template";

                    String templateFilename = Resources.CONFIGURATIONS_FOLDER;
                    if (!Resources.CONFIGURATIONS_FOLDER.endsWith("/") && !Resources.CONFIGURATIONS_FOLDER.endsWith("\\")) {
                        templateFilename += "/";
                    }
                    templateFilename += pfilename;

                    ChangeTemplate.writeTemplateQuery(category, HTMLTag.readFile(Resources.SPARQL_TEMPLATES.get(category)), templateFilename);
                    bw.write("gr.forth.ics.isl.xsearch.resources.entityenrichment.templatequery." + category + " = " + templateFilename);
                    bw.write("\n");
                }
            }


            bw.flush();
            bw.close();

            out.print("<font class='successfullStoring'>Your configuration was successfully saved! The ID is: <code><font size='+1'>" + id + "</font></code>");
            out.print("<br /> Keep this ID in order to load your configuration in future!</font>");

            System.out.println("# The configuration file with ID = " + id + " was successfully saved!");

        } catch (Exception e) {
            out.print("<font class='errorStoring'>" + e.getMessage().replace("\n", " ") + "</span>");
        }
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
