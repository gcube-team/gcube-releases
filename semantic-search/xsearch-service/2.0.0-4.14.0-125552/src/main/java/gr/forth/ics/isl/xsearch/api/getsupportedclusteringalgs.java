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
package gr.forth.ics.isl.xsearch.api;

import gr.forth.ics.isl.xsearch.resources.Resources;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class getsupportedclusteringalgs extends HttpServlet {

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
            throws ServletException, IOException, ParserConfigurationException, TransformerConfigurationException {
        PrintWriter out = response.getWriter();

        String format = request.getParameter("format");
        if (format == null) {
            format = "";
        }
        format = format.trim();

        if (!format.equals("")) {
            if (!format.toLowerCase().equals("json") && !format.toLowerCase().equals("xml") && !format.toLowerCase().equals("csv")) {
                response.sendError(400, "The value of the parameter 'format' is not valid. Valid values: {json, xml, csv}.");
                return;
            }
        } else {
            format = "json"; //default
        }

        try {

            if (format.toLowerCase().equals("csv")) {
                response.setContentType("text/plain;charset=UTF-8");
                out.println("\"CLUSTERING_ALGORITHM_ID\"" + "\t" + "\"CLUSTERING_ALGORITHM_DESCRIPTION\"");
                for (String cl : Resources.SUPPORTED_CLUSTERING_ALGORITHMS.keySet()) {
                    out.println(cl + "\t" + Resources.SUPPORTED_CLUSTERING_ALGORITHMS.get(cl));
                }


            } else if (format.toLowerCase().equals("xml")) {
                response.setContentType("application/xml;charset=UTF-8");

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("supportedClusteringAlgorithms");
                doc.appendChild(rootElement);


                for (String cl : Resources.SUPPORTED_CLUSTERING_ALGORITHMS.keySet()) {


                    Element alg = doc.createElement("algorithm");

                    Element id = doc.createElement("id");
                    id.appendChild(doc.createTextNode(cl));

                    Element desc = doc.createElement("description");
                    desc.appendChild(doc.createTextNode(Resources.SUPPORTED_CLUSTERING_ALGORITHMS.get(cl)));

                    alg.appendChild(id);
                    alg.appendChild(desc);

                    rootElement.appendChild(alg);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);

                StreamResult result = new StreamResult(out);
                try {
                    transformer.transform(source, result);
                } catch (TransformerException ex) {
                    Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
                }


            } else {
                response.setContentType("application/json;charset=UTF-8");

                JSONObject json = new JSONObject();
                JSONArray algs = new JSONArray();

                for (String cl : Resources.SUPPORTED_CLUSTERING_ALGORITHMS.keySet()) {
                    JSONObject alg = new JSONObject();
                    alg.put("id", cl);
                    alg.put("description", Resources.SUPPORTED_CLUSTERING_ALGORITHMS.get(cl));
                    algs.add(alg);
                }

                json.put("supportedClusteringAlgorithms", algs);
                out.print(json);
                out.close();
            }

        } finally {
            out.close();
        }

        System.out.println("# GET SUPPORTED CLUSTERING ALGORITHMS - FINISHED!");
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
        try {
            processRequest(request, response);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(getsupportedclusteringalgs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(getsupportedclusteringalgs.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(getsupportedclusteringalgs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(getsupportedclusteringalgs.class.getName()).log(Level.SEVERE, null, ex);
        }
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
