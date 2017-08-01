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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class InspectEntity extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        HashSet<String> acceptedCategories = (HashSet<String>) session.getAttribute("acceptedCategories");
        if (acceptedCategories == null) {
            acceptedCategories = Resources.MINING_ACCEPTED_CATEGORIES;
        }
        
        HashMap<String, String> endpoints = (HashMap<String, String>) session.getAttribute("endpoints");
        if (endpoints == null) {
            endpoints = Resources.SPARQL_ENDPOINTS;
        }
        
        HashMap<String, String> templateQueries = (HashMap<String, String>) session.getAttribute("templateQueries");
        if (templateQueries == null) {
            templateQueries = Resources.SPARQL_TEMPLATES;
        }
         
        try {

            SparqlRunner existingRunner = (SparqlRunner)session.getAttribute("sparqlRunnerThread");
            if (existingRunner != null) {
                System.out.println("# Stopping previous SPARQL runner...");
                existingRunner.interrupt();
                existingRunner.stop();
                System.out.println("# The previous SPARQL runner was stopped!");
                session.removeAttribute("sparqlRunnerThread");
            }
            
            long start, end;
            start = System.currentTimeMillis();

            System.out.println("# Inspecting selected entity...");

            String category = request.getParameter("category");
            String element = request.getParameter("element");
            element = element.replace("^^^^^", "%");
            String bubbleid = "bubble"+request.getParameter("rand");

            SparqlRunner runner = new SparqlRunner(category, element, acceptedCategories, endpoints, templateQueries);
            runner.start();
            session.setAttribute("sparqlRunnerThread", runner);
            
            boolean finish = false;
            while (finish == false) {
                if (runner.finish) {
                    break;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        IOSLog.writeErrorToLog(ex,"InspectEntity");
                        System.out.println("*** ERROR WHILE TRYING TO SLEEP FOR ONE SECOND!");
                    }
                }
            }
            
            ArrayList<LinkedHashMap<String, String>> results = runner.getResultsData();
            if (results.isEmpty()) {
                out.print("<h3><center>Sorry!<br />No information for this entity!</center></h3>");
            } else {

                for (HashMap<String, String> resultMap : results) {
                    for (String variableName : resultMap.keySet()) {
                        String variableValue = resultMap.get(variableName);
                        out.print("<font class=\"lod_data\">");
                        out.print("&bull;&nbsp;<b>" + variableName + ":</b> ");
                        if (variableValue.toLowerCase().startsWith("http")) {
                            String displayValue = variableValue;
                            if (displayValue.length() > 100) {
                                displayValue = displayValue.substring(0, 99) + "...";
                            }
                            String passvariableValue = variableValue.replace("#", "[NUMBERSIGN]");
                            String href = "javascript:showProperties(\""+category+"\", \""+URLEncoder.encode(passvariableValue, "utf8")+"\", \""+bubbleid+"\");";
                            out.print("<a title='"+displayValue+"' href='" + href + "'>" + displayValue + "</a>");
                            out.print("&nbsp;&nbsp;<a href='" + variableValue + "' target='_blank' class='em_minepage'>(open)</a>");
                        } else {
                            out.print(variableValue);
                        }
                        out.print("<br />");
                        out.print("</font>");
                    }
                    out.print("<hr />");
                }
                              
            }

            end = System.currentTimeMillis() - start;
            System.out.println("# TIME RETRIEVING LOD: " + end + " ms.");

           
            String query = (String) session.getAttribute("submitted_query");
            updateLog(request, query, category, element, end);

        } finally {
            out.close();
        }
    }

    public void updateLog(HttpServletRequest request, String query, String category, String entity, long time) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + query + "\tINSPECT_LOD ENTITY='" + entity + "' CATEGORY='" + category + "' TIME=" + time + "ms";
        IOSLog.writeToLog(line);
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
