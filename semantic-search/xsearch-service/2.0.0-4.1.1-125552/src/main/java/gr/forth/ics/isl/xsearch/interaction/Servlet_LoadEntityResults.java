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

package gr.forth.ics.isl.xsearch.interaction;

import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.Bean_Search;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Servlet_LoadEntityResults extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            HttpSession session = request.getSession();

            String category = request.getParameter("category");
            String element = request.getParameter("element");
            element = element.replace("^^^^^", "%");

            String docs = (String) session.getAttribute("docs");

            String docids_string = request.getParameter("docids");

            String query = (String) session.getAttribute("submitted_query");
            updateLog(request, query, category, element, docids_string);


            String all_docs = "";
            if (docs == null) {
                docs = "";
            }
            if (docs.equals("")) {
                all_docs = docids_string;
            } else {
                all_docs = docs + "," + docids_string;
            }

            //System.out.println("# All docs:"+all_docs);
            session.setAttribute("docs", all_docs);

            out.println("<font class='em_result_show'>Results of selected entities:</font>");
            out.println("&nbsp;&nbsp;<a href='javascript:getAllResults()'><font class='reset'>reset</font></a><br />&nbsp;<br />");


            ArrayList<SearchResult> bing_results = new ArrayList<SearchResult>();

            Bean_Search results = (Bean_Search) session.getAttribute("entities");


            if (results == null) {
                results = new Bean_Search((String) session.getAttribute("resultsFirstPage"));
                session.setAttribute("entities", results);
                bing_results = new ArrayList<SearchResult>(results.getWseResults());

            } else {

                if (((String) session.getAttribute("query_submitted")).equals("yes")) {
                    bing_results = new ArrayList<SearchResult>(results.getWseResults());

                } else {
                    results = new Bean_Search((String) session.getAttribute("resultsFirstPage"));
                    session.setAttribute("entities", results);
                    bing_results = new ArrayList<SearchResult>(results.getWseResults());
                }
            }

            String[] docids = all_docs.split(",");
            ArrayList<Integer> docids_list = new ArrayList<Integer>();

            for (int i = 0; i < docids.length; i++) {
                int id = Integer.parseInt(docids[i].trim());
                if (!docids_list.contains(id)) {
                    docids_list.add(id);
                }
            }
            Collections.sort(docids_list);
            //String new_docs_list = docids_list.toString();
            //System.out.println("SORTED LIST:"+new_docs_list);

            ArrayList<Integer> docids_list_to_show = new ArrayList<Integer>();
            for (int id : docids_list) {
                if (!docids_list_to_show.contains(id)) {
                    docids_list_to_show.add(id);
                }
            }
            //System.out.println("LIST TO SHOW:"+docids_list_to_show);


            for (int i = 0; i < docids_list_to_show.size(); i++) {

                int id = docids_list_to_show.get(i);
                if (id < 0) {
                    continue;
                }
                SearchResult res = bing_results.get(id);
                String title = res.getTitle();
                String url = res.getUrl();
                String descr = res.getDescription();

                String titleToShow = title;
                String hiddenTitle = "";
                String idTitle = "resultTitle" + i;
                String showTitleId = "resultShowTitle" + i;
                String script1 = "javascript:showAllText('" + idTitle + "', '" + showTitleId + "');";
                String showAllText = "";

                if (title.length() > 125) {
                    hiddenTitle = "<span id=\"" + idTitle + "\" style=\"display:none\">" + title.substring(124) + "</span>";
                    titleToShow = title.substring(0, 124) + hiddenTitle;
                    showAllText = "<span id=\"" + showTitleId + "\"><a href=\"" + script1 + "\" class=\"em_show_name_a\">...show all</a></span>";
                }

                String descrToShow = descr;
                String hiddenDescr = "";
                String idDescr = "resultDescr" + i;
                String showDescrId = "resultShowDescr" + i;
                String script2 = "javascript:showAllText('" + idDescr + "', '" + showDescrId + "');";

                if (descr.length() > 330) {
                    hiddenDescr = "<span id=\"" + idDescr + "\" style=\"display:none\">" + descr.substring(328) + "</span>";
                    descrToShow = descr.substring(0, 328) + "<span id=\"" + showDescrId + "\" align=\"right\" class=\"em_showAllText\"><a href=\"" + script2 + "\" class=\"em_show_name_a\">...show all</a></span>" + hiddenDescr;
                }

                if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("ftp")) {
                    out.println("<font class='one_result_title'><a href='Servlet_OpenResult?doc=" + id + "&doc_url=" + url + "'>" + titleToShow + "</a></font>" + showAllText);
                } else {
                    out.println("<font class='one_result_title'>" + titleToShow + "</font>" + showAllText);
                }

                if (!descrToShow.trim().equals("")) {
                    out.println("<br />");
                    out.println("<font>" + descrToShow + "</font>");
                }   
                out.println("<br />");
                out.println("<font class='em_url'>" + url.replace("#", "-") + "</font>");
                if ((url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("ftp")) && !url.toLowerCase().endsWith(".jpg") && !url.toLowerCase().endsWith(".png") && !url.toLowerCase().endsWith(".gif") && !url.toLowerCase().endsWith(".jpeg")) {
                    out.println(" - <font><a class='em_minepage' href=javascript:minePage(" + id + ")>find its entities</a></font>");
                }
                 
                if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png") || url.toLowerCase().endsWith(".gif") || url.toLowerCase().endsWith(".jpeg")) {
                    out.println("<br /><img border='0' src='"+url+"' class='imgOfDesc' />");
                }
                

                out.println("<br />&nbsp;<br />");
            }


            out.println("<br />&nbsp;<br />&nbsp;<br />&nbsp;<br />");

        } finally {
            out.close();
        }
    }

    public void updateLog(HttpServletRequest request, String query, String category, String entity, String docs) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + query + "\tLOAD ENTITY '" + entity + "' FROM CATEGORY '" + category + "' WITH DOCS '" + docs + "'";
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
