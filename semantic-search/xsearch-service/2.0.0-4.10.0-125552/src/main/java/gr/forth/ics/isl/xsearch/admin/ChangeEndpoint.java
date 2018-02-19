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

package gr.forth.ics.isl.xsearch.admin;

import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
public class ChangeEndpoint extends HttpServlet {

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


        String category = request.getParameter("category");
        if (category == null) {
            category = "";
        }

        String newEndpoint = request.getParameter("endpoint");
        if (newEndpoint == null) {
            newEndpoint = "";
        }
        newEndpoint = URLDecoder.decode(newEndpoint, "utf-8");

        try {

            URL url = new URL(newEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String xml_content = "application/sparql-results+xml";
            connection.setRequestProperty("ACCEPT", xml_content);
            int endpointResponseCode = connection.getResponseCode();
            String endpointResponseMess = connection.getResponseMessage();

            String sampleQ = "select ?x where { ?x ?y ?z } limit 1";
            sampleQ = URLEncoder.encode(sampleQ, "utf8");
            String sampleQpath = newEndpoint + sampleQ;
            System.out.println("# Sample query path: "+sampleQpath); 
            url = new URL(sampleQpath);
            
            connection = (HttpURLConnection) url.openConnection();
            int sampleQResponseCode = connection.getResponseCode();
            String sampleQResponseMess = connection.getResponseMessage();
            
            System.out.println("# Endpoint response code: "+endpointResponseCode);
            System.out.println("# Sample query response code:"+sampleQResponseMess);

            if ( (endpointResponseCode == 400 || endpointResponseCode == 500 || endpointResponseCode == 200) && (sampleQResponseCode == 400 || sampleQResponseCode == 200)) {
                
                /* CHANGE THE SPARQL ENDPOINT! */
                Resources.SPARQL_ENDPOINTS.put(category, newEndpoint);
                
            } else {
                if (endpointResponseCode != 400 || endpointResponseCode != 500 || endpointResponseCode != 200) {
                    out.print("Not a SPARQL endpoint! Please check it!");
                } else {
                    out.print("Could not run the sample query! Message: "+sampleQResponseMess + ". <br />(The path of the endpoint must be ready to accept a query, e.g. it must end with 'query=')");
                }
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
