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
import java.net.URLDecoder;
import java.net.URLEncoder;
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
public class ShowProperties extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {

            String uri = request.getParameter("uri");
            if (uri == null) {
                System.out.println("*** EMPTY URI!");
                out.print("<h3><center>Sorry!<br />No properties for this URI!</center></h3>");
                out.close();
                return;
            }
            uri = URLDecoder.decode(uri, "utf8");
            uri = uri.replace("[NUMBERSIGN]", "#");

            String category = request.getParameter("category");
            if (category == null) {
                System.out.println("*** EMPTY CATEGORY!");
                out.print("<h3><center>Sorry!<br />No properties for this URI!</center></h3>");
                out.close();
                return;
            }

            String bubbleid = "bubble" + request.getParameter("rand");

            System.out.println("# Retrieving properties of " + uri);
            HttpSession session = request.getSession();
            String query = (String) session.getAttribute("submitted_query");
            updateLog(request, query, uri);

            HashMap<String, String> endpoints = (HashMap<String, String>) session.getAttribute("endpoints");
            if (endpoints == null) {
                endpoints = Resources.SPARQL_ENDPOINTS;
            }

            LinkedHashMap<String, HashSet<String>> resultsMap = SparqlRunner.getProperties(category, uri, endpoints);
            if (resultsMap.isEmpty()) {
                out.print("<a target='_blank' href='" + uri + "'>" + uri + "</a>");
                out.close();
                return;
            }

            int ii = uri.lastIndexOf("/");
            String displayURI = uri;
            if (ii != -1) {
                displayURI = uri.substring(ii + 1);
                int ii2 = displayURI.lastIndexOf("#");
                if (ii2 != -1) {
                    displayURI = displayURI.substring(ii2 + 1);
                }
            }
            displayURI = displayURI.substring(0, 1).toUpperCase() + displayURI.substring(1);

            out.print("<font style=\"font-size: 13px; font-weight:bold\">Properties of: <a title=\"" + uri + "\" href=\"" + uri + "\" target=\"_blank\">" + displayURI + "</a></font>");
            out.print("<br />&nbsp;<br />");

            for (String property : resultsMap.keySet()) {
                HashSet<String> propertyValues = resultsMap.get(property);
                out.print("<table class='infoboxPropertyTable'>");
                out.print("<tr>");
                out.print("<td align='center' class='infoboxPropertyTitle'>");
                if (property.toLowerCase().startsWith("http://")) {
                    String displayProp = property;
                    int i1 = property.lastIndexOf("/");
                    if (i1 != -1) {
                        displayProp = property.substring(i1 + 1);

                        int i2 = displayProp.lastIndexOf("#");
                        if (i2 != -1) {
                            displayProp = displayProp.substring(i2 + 1);
                        }
                    }
                    displayProp = displayProp.substring(0, 1).toUpperCase() + displayProp.substring(1);
                    if (displayProp.length() > 100) {
                        displayProp = displayProp.substring(0, 99) + "...";
                    }
                    String passproperty = property.replace("#", "[NUMBERSIGN]");
                    String href = "javascript:showProperties(\"" + category + "\", \"" + URLEncoder.encode(passproperty, "utf8") + "\",\"" + bubbleid + "\");";
                    out.print("<a title='" + property + "' href='" + href + "'>" + displayProp + "</a>");
                } else {
                    out.print(property);
                }

                out.print("</td>");
                out.print("<tr>");
                out.print("<td class='infoboxPropertyValues'>");
                for (String value : propertyValues) {

                    if (value.toLowerCase().startsWith("http://")  || value.toLowerCase().startsWith("_:node") || value.toLowerCase().startsWith("nodeid:")) {
                        String displayValue = value;
                        int i1 = displayValue.lastIndexOf("/");
                        if (i1 != -1) {
                            displayValue = displayValue.substring(i1 + 1);

                            int i2 = displayValue.lastIndexOf("#");
                            if (i2 != -1) {
                                displayValue = displayValue.substring(i2 + 1);
                            }
                        }
                        if (displayValue.length() >= 2) {
                            displayValue = displayValue.substring(0, 1).toUpperCase() + displayValue.substring(1);
                        }
                        
                        if (value.toLowerCase().startsWith("nodeid:")) {
                            displayValue = "_:"+displayValue;
                        }
                        
                        String passvalue = value.replace("#", "[NUMBERSIGN]");
                        String href = "javascript:showProperties(\"" + category + "\", \"" + URLEncoder.encode(passvalue, "utf8") + "\",\"" + bubbleid + "\");";
                        out.print("&bull;&nbsp;<a title='" + value + "' href='" + href + "'>" + displayValue + "</a>");
                        out.print("&nbsp;&nbsp;<a href='" + value + "' target='_blank' class='em_minepage'>(open)</a>");
                    } else {
                        out.print("&bull;&nbsp;" + value);
                    }
                    out.print("<br />");
                }
                out.print("</td>");
                out.print("</tr>");
                out.print("</table>");
            }


        } finally {
            out.close();
        }
    }

    public void updateLog(HttpServletRequest request, String query, String uri) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + query + "\tRETRIEVING PROPERTIES OF '" + uri;
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
