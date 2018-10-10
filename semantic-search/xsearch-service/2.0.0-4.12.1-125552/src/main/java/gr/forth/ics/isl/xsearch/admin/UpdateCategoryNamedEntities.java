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

import gr.forth.ics.isl.textentitymining.gate.GateAnnie;
import gr.forth.ics.isl.textentitymining.gate.addcategory.AddCategory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.TreeSet;
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
public class UpdateCategoryNamedEntities extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

            String categoryName = request.getParameter("categoryName");
            if (categoryName == null) {
                categoryName = "";
            }

            if (categoryName.trim().equals("")) {
                out.print("Attention! You must give a category name!");
                out.close();
                return;
            }

            String list = request.getParameter("list");
            if (list == null) {
                list = "";
            }
            list = URLDecoder.decode(list, "utf-8");

            TreeSet<String> wordsSet = new TreeSet<String>();
            String[] words = list.split("\n");
            for (String word : words) {
                word = word.trim().toLowerCase();
                if (!word.equals("")) {
                    wordsSet.add(word);
                }
            }

            if (wordsSet.isEmpty()) {
                out.print("Attention! You must give a set of words or phrases!");
                out.close();
                return;
            }

            System.out.println("# Category: " + categoryName);
            System.out.println("# Number of words/phrases: " + wordsSet.size());

            /* Update category's named entities */
            AddCategory add = new AddCategory();
            add.setCategoryName(categoryName);
            add.setCategoryNameNoSpace(categoryName.replace(" ", "_"));
            add.initializeVariables();
            String categoryFile = add.getLIST_FILE_NAME();
            System.out.println("# Category file: ");
            PrintWriter writer = new PrintWriter(categoryFile, "UTF-8");
            for (String word : wordsSet) {
                writer.println(word);
            }
            writer.close();
            GateAnnie.RestartGate();
            

        } catch (Exception e) {
            out.print("Attention!" + e.getMessage().replace("\n", " "));
        }

        out.close();

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
