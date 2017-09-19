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
package gr.forth.ics.isl.xsearch.mining;

import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.Bean_Search;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.textentitymining.gate.GateEntityMiner;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Servlet_MinePage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        synchronized (this) {

            PrintWriter out = response.getWriter();

            try {

                int doc = Integer.parseInt(request.getParameter("doc"));
                System.out.println("# Mining Result: " + doc);

                HttpSession session = request.getSession();

                HashSet<String> acceptedCategories = (HashSet<String>) session.getAttribute("acceptedCategories");
                if (acceptedCategories == null) {
                    acceptedCategories = Resources.MINING_ACCEPTED_CATEGORIES;
                }


                HashMap<String, String> templateQueries = (HashMap<String, String>) session.getAttribute("templateQueries");
                if (templateQueries == null) {
                    templateQueries = Resources.SPARQL_TEMPLATES;
                }

                String query = (String) session.getAttribute("submitted_query");
                updateLog(request, query, doc);

                Bean_Search results = (Bean_Search) session.getAttribute("entities");

                if (results == null) {
                    results = new Bean_Search((String) session.getAttribute("resultsFirstPage"));
                    session.setAttribute("entities", results);
                } else {
                    if (((String) session.getAttribute("query_submitted")).equals("no")) {
                        results = new Bean_Search((String) session.getAttribute("resultsFirstPage"));
                        session.setAttribute("entities", results);
                    }
                }

                SearchResult result = results.getWseResults().get(doc);
                String title = result.getTitle();
                String descr = result.getDescription();
                String url = result.getUrl();


                if (!result.isHasRetrieved()) {
                    System.out.println("# Retrieving result's content....");
                    result.retriveContent();
                }

                String content = result.getContent();

                // PRINT PAGE'S SNIPPET
                out.println("<font><a href='" + url + "'>" + title + "</a></font>&nbsp;&nbsp;<a href='javascript:getAllResults()'><font class='em_reset'>reset</font></a>");
                if (!descr.trim().equals("")) {
                    out.println("<br />");
                    out.println("<font>" + descr + "</font>");
                }
                out.println("<br />");
                out.println("<font class='em_url'>" + url + "</font>");
                out.println("<br />&nbsp;<br />");
                out.println("<font class='em_entities_title'>Entities: </font>");
                out.println("<br />&nbsp;<br />");

                ArrayList<Category> entities = new ArrayList<Category>();

                try {

                    GateEntityMiner miner = new GateEntityMiner();
                    miner.setTextToMine(content);
                    miner.setAcceptedCategories(acceptedCategories);
                    miner.findEntities();
                    entities = miner.getEntities();

                } catch (Exception ex) {
                    System.out.println("*** PROBLEM ADDING DOCUMENTS TO CORPUS:");
                    //Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
                }




                if (entities.isEmpty()) {
                    out.println("<font color='red'><i>No entities were found!</i></font>");
                } else {
                    for (Category cat : entities) {
                        String category = cat.getName();
                        out.println("<font class='em_category_name'>" + category + "</font>");
                        out.println("<br />");
                        int n = 0;
                        for (Entity ent : cat.getEntities()) {
                            String name = ent.getName();
                            name = name.replace("'", "&quot;").replace("\"", "&quot;");
                            String element_name = category.trim().replace(" ", "_") + "_" + n;
                            String element_name_pass = name.replace("\"", "&quot;").replace("'", "&quot;").replace("%", "^^^^^");
                            n++;
                            String element_img_id = category.toLowerCase().trim().replace(" ", "_").replace(".", "") + "_imgp_" + n;
                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"javascript:checkElement('" + name + "','" + element_name + "'," + doc + ");\"><font class='em_element_name'>" + name + "</font></a>");
                            if (templateQueries.containsKey(category)) {
                                out.println("<a id=\"" + element_img_id + "\" href=\"javascript:inspectElement('" + category + "', '" + element_name_pass + "', '" + element_img_id + "')\"><img border=\"0\" title=\"Click to retrieve semantic information\" src=\"files/graphics/lod.jpg\"/></a>");
                            }
                            out.println("<br />");
                            out.println("<div class=\"entity_instance\" id=\"" + element_name + "\"></div>");
                        }
                        out.println("<br />");
                    }
                }

                out.println("<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />");

            } finally {
                out.close();
            }

        }
    }

    public void updateLog(HttpServletRequest request, String query, int pageid) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + query + "\tMINE PAGE " + pageid;
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
