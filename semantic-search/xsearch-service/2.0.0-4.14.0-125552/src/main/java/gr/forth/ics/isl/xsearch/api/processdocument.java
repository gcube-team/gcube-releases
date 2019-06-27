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

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.textentitymining.gate.GateEntityMiner;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
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
public class processdocument extends HttpServlet {

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

        String url = request.getParameter("url");
        if (url == null) {
            url = "";
        }
        url = url.trim();

        if (url.equals("")) {
            response.sendError(400, "The value of the parameter 'url' is null or empty.");
            return;
        }
        url = URLDecoder.decode(url, "urf-8");

        String categories = request.getParameter("categories");
        if (categories == null) {
            categories = "";
        }
        categories = categories.trim();

        HashSet<String> acceptedCategories = new HashSet<String>();
        if (categories.equals("")) {
            for (String c : Resources.MINING_ACCEPTED_CATEGORIES) {
                acceptedCategories.add(c);
            }
        } else {
            String[] categs = categories.split(";");
            for (String c : categs) {
                acceptedCategories.add(c.trim());
            }
        }

        if (!Resources.MINING_ALL_POSSIBLE_CATEGORIES.containsAll(acceptedCategories)) {
            response.sendError(400, "One or more of the provided categories are not currently supported by the entity mining component.");
            return;
        }


        String urlContents = "";

        try {
            URL the_url = new URL(url);
            URLConnection urlConn = the_url.openConnection();

            if (urlConn.getContentType().equalsIgnoreCase("application/pdf")) {

                System.out.println("# Reading PDF file!");

                try {
                    PdfReader reader = new PdfReader(the_url);
                    int n = reader.getNumberOfPages();

                    for (int i = 1; i <= n; i++) {
                        urlContents += (PdfTextExtractor.getTextFromPage(reader, i) + "\n");
                    }
                    reader.close();
                    //System.out.println("# PDF CONTENT: \n" + source);
                } catch (Exception e) {
                    System.out.println("*** ERROR READING PDF CONTENT: " + e.getMessage());
                    response.sendError(400, "Error reading the contents of the PDF file. Please check the URL and/or the file.");
                    return;
                }

            } else if (urlConn.getContentType().equalsIgnoreCase("application/msword")) {
                response.sendError(400, "MS Word files are not currently supported.");
                return;
            } else {
                HTMLTag tagger = new HTMLTag(the_url, true);
                urlContents = tagger.getSourceCode();
            }

        } catch (Exception e) {
            response.sendError(400, "Problem connecting to the given URL. Please check the URL.");
            return;
        }

        if (urlContents == null) {
            response.sendError(400, "The content of the given URL is NULL. Please check the URL.");
            return;
        }

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

        ArrayList<Category> entities;

        synchronized (this) {

            if (!urlContents.equals("")) {

                urlContents = urlContents.replace("<?xml ", "<html ");

                // MINE THE PAGE //     
                GateEntityMiner miner = new GateEntityMiner();
                miner.setAcceptedCategories(acceptedCategories);
                miner.setTextToMine(urlContents);
                miner.findEntities();
                entities = miner.getEntities();
                System.out.println("# Page mining was finished!");

            } else {
                response.sendError(400, "The content of the given URL is empty. Please check the URL.");
                return;
            }
        }

        if (format.toLowerCase().equals("csv")) {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain;charset=UTF-8");

            out.println("\"ENTITY_NAME\"" + "\t" + "\"CATEGORY_NAME\"");
            for (int i = 0; i < entities.size(); i++) {
                Category c = entities.get(i);
                for (int j = 0; j < entities.get(i).getEntities().size(); j++) {
                    Entity e = entities.get(i).getEntities().get(j);
                    out.println(e.getName() + "\t" + c.getName());
                }
            }
            out.close();
        } else if (format.toLowerCase().equals("xml")) {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("identifiedEntities");
            doc.appendChild(rootElement);

            for (int i = 0; i < entities.size(); i++) {
                Category c = entities.get(i);
                for (int j = 0; j < entities.get(i).getEntities().size(); j++) {
                    Entity e = entities.get(i).getEntities().get(j);

                    Element entity = doc.createElement("entity");
                    Element entityName = doc.createElement("entityName");
                    Element categName = doc.createElement("categoryName");
                    entityName.appendChild(doc.createTextNode(e.getName()));
                    categName.appendChild(doc.createTextNode(c.getName()));

                    entity.appendChild(entityName);
                    entity.appendChild(categName);
                    rootElement.appendChild(entity);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            response.setContentType("application/xml;charset=UTF-8");
            PrintWriter out = response.getWriter();

            StreamResult result = new StreamResult(out);
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
            }

            out.close();

        } else { // json

            JSONObject json = new JSONObject();
            JSONArray listOfEntities = new JSONArray();

            for (int i = 0; i < entities.size(); i++) {
                Category c = entities.get(i);
                for (int j = 0; j < entities.get(i).getEntities().size(); j++) {
                    Entity e = entities.get(i).getEntities().get(j);
                    JSONObject entity = new JSONObject();
                    entity.put("categoryName", c.getName());
                    entity.put("entityName", e.getName());
                    listOfEntities.add(entity);
                }
            }
            json.put("identifiedEntities", listOfEntities);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print(json);
            out.close();
        }

        System.out.println("# PROCESS DOCUMENT - FINISHED!");
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
            Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(processdocument.class.getName()).log(Level.SEVERE, null, ex);
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
