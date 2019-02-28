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

import gr.forth.ics.isl.textentitymining.gate.addcategory.AddCategory;
import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Properties;
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
public class AddNewCategory extends HttpServlet {

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
                out.print("You must give a category name!");
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
                word = word.trim();
                if (!word.equals("")) {
                    wordsSet.add(word);
                }
            }
            
            if (wordsSet.isEmpty()) {
                out.print("You must give a set of words or phrases!");
                out.close();
                return;
            }


            System.out.println("# Initial category name: " + categoryName);
            categoryName = categoryName.replaceAll("[^a-zA-Z0-9]+", "_");
            System.out.println("# Final category name: " + categoryName);
            
//            System.out.println("# List of words/phrase:\n");
//            for (String w  : wordsSet) {
//                System.out.println("- "+w);
//            }
            System.out.println("# Number of words/phrases: "+wordsSet.size());
            
            /* Add new category to Gate */
            AddCategory addCategory = new AddCategory(categoryName, wordsSet);
            addCategory.add();
            
            /* Add new category to current set of possible categories */
            Resources.MINING_ALL_POSSIBLE_CATEGORIES.add(categoryName);
              
            /* Add new cagegory to x-search properties file */
            Properties prop = new Properties();
            InputStream in = new FileInputStream(Resources.X_SEARCH_PROPERTIES_FILE);
            prop.load(in);
            String propertyValue = "";
            for (String cat : Resources.MINING_ALL_POSSIBLE_CATEGORIES) {
                propertyValue += cat.trim();
                propertyValue += ",";
            }
            prop.setProperty("gr.forth.ics.isl.xsearch.resources.mining.allPossibleCategories", propertyValue.substring(0, propertyValue.length()-1));
            prop.store(new FileOutputStream(new File(Resources.X_SEARCH_PROPERTIES_FILE)), "X-Search Properties");
            

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
