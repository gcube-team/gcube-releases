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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
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
public class ChangeTemplate extends HttpServlet {

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

            String category = request.getParameter("category");
            if (category == null) {
                category = "";
            }

            String newTemplate = request.getParameter("template");
            if (newTemplate == null) {
                newTemplate = "";
            }

            newTemplate = URLDecoder.decode(newTemplate, "utf-8");

            if (newTemplate.trim().equals("")) {
                out.print("Attention! Empty SPARQL query!");
                out.close();
                return;
            }

            if (!newTemplate.toLowerCase().contains("select") || !newTemplate.toLowerCase().contains("where") || !newTemplate.toLowerCase().contains("?") || !newTemplate.toLowerCase().contains("{") || !newTemplate.toLowerCase().contains("}")) {
                out.print("Attention! The SPARQL template query is not valid!");
                out.close();
                return;
            }

            if (!newTemplate.contains(Resources.TEMPLATE_PARAMETER)) {
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

            ChangeTemplate.writeTemplateQuery(category, newTemplate, templateFilename);
            Resources.SPARQL_TEMPLATES.put(category, templateFilename);

        } catch (Exception e) {
            out.print(e.getMessage().replace("\n", " "));
        }

        out.close();

    }

    public static void writeTemplateQuery(String category, String template, String filename) {

        try {
            File file = new File(filename);
            file.delete();
            file = new File(filename);
            file.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(template);
            bw.flush();
            bw.close();

        } catch (IOException ex) {
            System.out.println("*** ERROR CREATING TEMPLATE FILE: " + ex.getMessage());
            //Logger.getLogger(ChangeTemplate.class.getName()).log(Level.SEVERE, null, ex);
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
