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

import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
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
public class AddEntityEnrichment extends HttpServlet {

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

            String category = request.getParameter("category");
            if (category == null) {
                category = "";
            }

            String endpoint = request.getParameter("endpoint");
            if (endpoint == null) {
                endpoint = "";
            }
            endpoint = URLDecoder.decode(endpoint, "utf-8");

            String template = request.getParameter("template");
            if (template == null) {
                template = "";
            }
            template = URLDecoder.decode(template, "utf-8");

            if (endpoint.trim().equals("")) {
                out.print("Attention! Empty SPARQL endpoint!");
                out.close();
                return;
            }

            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String xml_content = "application/sparql-results+xml";
            connection.setRequestProperty("ACCEPT", xml_content);
            int endpointResponseCode = connection.getResponseCode();

            String sampleQ = "select ?x where { ?x ?y ?z } limit 1";
            sampleQ = URLEncoder.encode(sampleQ, "utf8");
            String sampleQpath = endpoint + sampleQ;
            System.out.println("# Sample query path: " + sampleQpath);
            url = new URL(sampleQpath);
            
            connection = (HttpURLConnection) url.openConnection();
            int sampleQResponseCode = connection.getResponseCode();
            String sampleQResponseMess = connection.getResponseMessage();
            
            System.out.println("# Endpoint response code: "+endpointResponseCode);
            System.out.println("# Sample query response code:"+sampleQResponseMess);

            if ( (endpointResponseCode == 400 || endpointResponseCode == 500 || endpointResponseCode == 200) && (sampleQResponseCode == 400 || sampleQResponseCode == 200)) {
                /* THE SPARQL ENDPOINT IS OK! */
            } else {
                if (endpointResponseCode != 400 || endpointResponseCode != 500 || endpointResponseCode != 200) {
                    out.print("Not a SPARQL endpoint! Please check it!");
                    out.close();
                    return;
                } else {
                    out.print("Could not run the sample query! Message: "+sampleQResponseMess + ". <br />(The path of the endpoint must be ready to accept a query, e.g. it must end with 'query=')");
                    out.close();
                    return;
                }
            }


            if (template.trim().equals("")) {
                out.print("Attention! Empty SPARQL template query!");
                out.close();
                return;
            }

            if (!template.toLowerCase().contains("select") || !template.toLowerCase().contains("where") || !template.toLowerCase().contains("?") || !template.toLowerCase().contains("{") || !template.toLowerCase().contains("}")) {
                out.print("Attention! The SPARQL template query is not valid!");
                out.close();
                return;
            }

            if (!template.contains(Resources.TEMPLATE_PARAMETER)) {
                out.print("Attention! The SPARQL template query does not contain the parameter '&lt;ENTITY&gt;'!");
                out.close();
                return;
            }

            String filename = category.toLowerCase().replaceAll("[^a-zA-Z]+", "_");

            String templateFilename = Resources.TEMP_FOLDER;
            if (!Resources.TEMP_FOLDER.endsWith("/") && !Resources.TEMP_FOLDER.endsWith("\\")) {
                templateFilename += "/";
            }
            templateFilename += filename;
            templateFilename += ".template";

            ChangeTemplate.writeTemplateQuery(category, template, templateFilename);

            if (ses.equals("y")) {
                HashMap<String, String> endpoints = (HashMap<String, String>) session.getAttribute("endpoints");
                if (endpoints == null) {
                    endpoints = new HashMap<String, String>();
                    endpoints.putAll(Resources.SPARQL_ENDPOINTS);
                }
                endpoints.put(category, endpoint);
                session.setAttribute("endpoints", endpoints);

                HashMap<String, String> templateQueries = (HashMap<String, String>) session.getAttribute("templateQueries");
                if (templateQueries == null) {
                    templateQueries = new HashMap<String, String>();
                    templateQueries.putAll(Resources.SPARQL_TEMPLATES);
                }
                templateQueries.put(category, templateFilename);
                session.setAttribute("templateQueries", templateQueries);
                System.out.println("The (session-based) entity enrichment was successfully added!");
                
            } else {
                Resources.SPARQL_ENDPOINTS.put(category, endpoint);
                Resources.SPARQL_TEMPLATES.put(category, templateFilename);
                System.out.println("The entity enrichment was successfully added!");
            }


        } catch (Exception e) {
            out.print(e.getMessage().replace("\n", " "));
        }

        out.close();

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
