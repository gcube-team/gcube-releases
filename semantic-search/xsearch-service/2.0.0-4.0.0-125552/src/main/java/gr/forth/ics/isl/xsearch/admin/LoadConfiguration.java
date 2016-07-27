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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
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
public class LoadConfiguration extends HttpServlet {
    
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
        
        InputStream in = null;
        try {
            
            String id = request.getParameter("id");
            if (id == null) {
                id = "";
            }

            /* READ CONFIGURATION/PROPERTIES FILE */
            String propertiesFile = Resources.CONFIGURATIONS_FOLDER;
            if (!Resources.CONFIGURATIONS_FOLDER.endsWith("/") && !Resources.CONFIGURATIONS_FOLDER.endsWith("\\")) {
                propertiesFile += "/";
            }
            propertiesFile += ("conf" + id + ".properties");
            
            Properties prop = new Properties();
            in = new FileInputStream(propertiesFile);
            prop.load(in);


            // READ OPENSEARCH DESCRIPTION DOCUMENT //
            String descriptionDocument = prop.getProperty("gr.forth.ics.isl.xsearch.resources.opensearch.descriptionDocument");
            if (descriptionDocument != null) {
                if (ses.equals("y")) {
                    session.setAttribute("descrDoc", descriptionDocument.trim());
                } else {
                    Resources.DESCRIPTIONDOCUMENT = descriptionDocument.trim();
                }
                
            }


            // READ CLUSTERING ALGORITHM //
            String clusteringAlgorithmString = prop.getProperty("gr.forth.ics.isl.xsearch.resources.clustering.clusteringAlgorithm");
            try {
                int clusteringAlgorithm = Integer.parseInt(clusteringAlgorithmString.trim());
                if (clusteringAlgorithm < 1 || clusteringAlgorithm > 5) {
                    System.out.println("*** NOT APPROPRIATE CLUSTERING ALGORITHM! THE CLUSTERING ALGORITH WAS NOT LOADED!");
                } else {
                    if (ses.equals("y")) {
                        session.setAttribute("clustAlg", "" + clusteringAlgorithm);
                    } else {
                        Resources.CLUSTERING_ALGORITHM = clusteringAlgorithm;
                    }
                    
                }
            } catch (Exception e) {
                System.out.println("*** ERROR READING CLUSTERING ALGORITHM! THE CLUSTERING ALGORITH WAS NOT LOADED! ");
            }

            // READ 'MINE QUERY' PROPERTY //
            String mineQueryString = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.mineQuery");
            try {
                if (mineQueryString.trim().toLowerCase().equals("true")) {
                    if (ses.equals("y")) {
                        session.setAttribute("mineQuery", "true");
                    } else {
                        Resources.MINE_QUERY = true;
                    }
                    
                } else {
                    if (ses.equals("y")) {
                        session.setAttribute("mineQuery", "false");
                    } else {
                        Resources.MINE_QUERY = false;
                    }                    
                }
            } catch (Exception e) {
                System.out.println("*** ERROR READING PROPERTY 'MINE QUERY'! THIS PROPERTY WAS NOT LOADED! ");
            }

            // READ ACCEPTED CATEGORIES //
            HashSet<String> acceptedCats = new HashSet<String>();
            String acceptedCategories = prop.getProperty("gr.forth.ics.isl.xsearch.resources.mining.acceptedCategories");
            String[] acceptedCategoriesArray = acceptedCategories.split(",");
            for (String acceptedCategory : acceptedCategoriesArray) {
                if (!acceptedCategory.trim().equals("")) {
                    acceptedCats.add(acceptedCategory.trim());
                }
            }
            if (ses.equals("y")) {
                session.setAttribute("acceptedCategories", acceptedCats);
            } else {
                Resources.MINING_ACCEPTED_CATEGORIES = new HashSet<String>();
                Resources.MINING_ACCEPTED_CATEGORIES.addAll(acceptedCats);
            }

            // READ SPARQL ENDPOINDS and TEMPLATES//
            HashMap<String, String> endpoints = new HashMap<String, String>();
            HashMap<String, String> templates = new HashMap<String, String>();
            for (String acceptedCategory : acceptedCats) {
                String sparqlEndpoint = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.sparqlendpoint." + acceptedCategory);
                if (sparqlEndpoint != null) {
                    endpoints.put(acceptedCategory, sparqlEndpoint.trim());
                }
                
                String sparqlTemplate = prop.getProperty("gr.forth.ics.isl.xsearch.resources.entityenrichment.templatequery." + acceptedCategory);
                if (sparqlTemplate != null) {
                    templates.put(acceptedCategory, sparqlTemplate.trim());
                }
            }
            if (ses.equals("y")) {
                session.setAttribute("endpoints", endpoints);
                session.setAttribute("templateQueries", templates);
            } else {
                Resources.SPARQL_ENDPOINTS = new HashMap<String, String>();
                Resources.SPARQL_ENDPOINTS.putAll(endpoints);
                
                Resources.SPARQL_TEMPLATES = new HashMap<String, String>();
                Resources.SPARQL_TEMPLATES.putAll(templates);
            }


            /********************/
            System.out.println("# The configuration file with ID = " + id + " was successfully loaded!");
            
        } catch (Exception e) {
            out.print(e.getMessage().replace("\n", " "));
            
        }
        
        in.close();
        
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
