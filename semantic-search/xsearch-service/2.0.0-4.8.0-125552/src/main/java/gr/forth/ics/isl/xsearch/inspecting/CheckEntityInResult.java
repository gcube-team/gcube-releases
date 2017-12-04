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

import gr.forth.ics.isl.xsearch.util.HTMLTag;
import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.Bean_Search;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class CheckEntityInResult extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {

            String element = request.getParameter("element");
            String category = request.getParameter("category");
            category = category.substring(0, category.lastIndexOf("_"));

            if (element == null) {
                out.print("<div class='em_snip'>Could not find the entity. Please try manually.</div>");
                return;
            }

            element = element.toLowerCase();
            int doc = Integer.parseInt(request.getParameter("doc"));


            HttpSession session = request.getSession();

            String query = (String) session.getAttribute("submitted_query");
            updateLog(request, query, element, category, doc);


            Bean_Search results = (Bean_Search) session.getAttribute("entities");
            String page_content = results.getWseResults().get(doc).getContent();

            page_content = removeTags(page_content);

            ArrayList<Integer> positions = new ArrayList<Integer>();
            int index = page_content.toLowerCase().indexOf(element);

            while (index != -1) {

                positions.add(index);
                index = page_content.toLowerCase().indexOf(element, index + 1);
            }

            System.out.println("=> Occurrence: " + positions.size());

            ArrayList<String> snippets = new ArrayList<String>();
            for (int i = 0; i < positions.size(); i++) {
                int pos = positions.get(i);
                int start = 0;
                if (pos > 150) {
                    start = pos - 148;
                }

                int end = page_content.length() - 1;
                if (pos + 150 < page_content.length()) {
                    end = pos + 148;
                }

                if (pos >= start && end >= pos + element.length()) {
                    String part1 = page_content.substring(start, pos);
                    String part2 = page_content.substring(pos + element.length(), end);

                    String snippet = "<div class='em_snip'>..." + part1 + "<span class='em_highlight'>" + page_content.substring(pos, pos + element.length()) + "</span>" + part2 + "...</div>";

                    snippets.add(snippet);
                }

            }

            if (!positions.isEmpty()) {
                if (snippets.isEmpty()) {
                    out.print("<div class='em_snip'>Could not find the entity '" + element + "'. Please try manually.</div>");
                } else {
                    for (String snippet : snippets) {
                        out.print(snippet);
                        out.print("<br />");
                    }
                }
            } else {
                out.print("<div class='em_snip'>Could not find the entity '" + element + "'. Please try manually.</div>");
            }


        } finally {
            out.close();
        }
    }

    public void updateLog(HttpServletRequest request, String query, String element, String category, int doc) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + query + "\tCHECK ELEMENT '" + element + "' OF CATEGORY '" + category + "' OF DOC " + doc;
        IOSLog.writeToLog(line);
    }

    private String removeTags(String content) {

        String temp1 = HTMLTag.removeTag("script", content);
        if (temp1 != null) {
            content = temp1;
        }

        String temp2 = HTMLTag.removeTag("javascript", content);
        if (temp2 != null) {
            content = temp2;
        }

        String temp3 = HTMLTag.removeTag("style", content);
        if (temp3 != null) {
            content = temp3;
        }

        String temp4 = HTMLTag.removeTags(content);
        if (temp4 != null) {
            content = temp4;
        }

        if (content == null) {
            return "";
        }
        return content;
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
