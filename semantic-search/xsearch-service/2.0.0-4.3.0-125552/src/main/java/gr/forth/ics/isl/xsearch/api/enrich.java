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

import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.xsearch.Triple;
import gr.forth.ics.isl.xsearch.inspecting.SparqlRunner;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
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
public class enrich extends HttpServlet {

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

        String uri = request.getParameter("uri");
        if (uri == null) {
            uri = "";
        }
        uri = uri.trim();

        if (uri.equals("")) {
            response.sendError(400, "The value of the parameter 'uri' is null or empty.");
            return;
        }

        String category = request.getParameter("category");
        if (category == null) {
            category = "";
        }
        category = category.trim();


        String endpoint = request.getParameter("endpoint");
        if (endpoint == null) {
            endpoint = "";
        }
        endpoint = endpoint.trim();

        if (category.equals("") && endpoint.equals("")) {
            response.sendError(400, "The value of both the parameter 'category' and the parameter 'endpoint' is null or empty. Please provide the entity's category (&category=...) or a SPARQL endpoint (&endpoint=...).");
            return;
        }

        if (endpoint.equals("")) {
            endpoint = Resources.SPARQL_ENDPOINTS.get(category);
            if (endpoint == null) {
                endpoint = "";
            }
            endpoint = endpoint.trim();

            if (endpoint.equals("")) {
                response.sendError(400, "The current configuration of X-Search does not provide a SPARQL endpoint for the category '" + category + "'. Please provide a SPARQL endpoint (&endpoint=...).");
                return;
            }
        }

        String type = request.getParameter("type");
        if (type == null) {
            type = "";
        }
        type = type.toLowerCase().trim();

        if (!type.equals("")) {
            if (!type.equals("outgoing") && !type.equals("incoming") && !type.equals("both")) {
                response.sendError(400, "Wrong value of 'type' parameter. The 'type' parameter must have one of the following values: {outgoing, incoming, both}");
                return;
            }
        } else {
            response.sendError(400, "The value of the parameter 'type' is null or empty. Provide the type of properties to retrieve (outgoing | incoming | both)");
            return;
        }

        String lang = request.getParameter("lang");
        if (lang == null) {
            lang = "";
        }
        lang = lang.trim();

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

        String templateParam = "[URI]";
        String langParam = "[LANG]";
        String query_outgoing = "select distinct ?s ?p ?o where { ?s ?p ?o FILTER(?s = <[URI]>) }";
        String query_incoming = "select distinct ?s ?p ?o where { ?s ?p ?o FILTER(?o = <[URI]>) }";
        String query_both = "select distinct ?s ?p ?o where { { ?s ?p ?o FILTER(?s = <[URI]>) } UNION { ?s ?p ?o FILTER(?o = <[URI]>) } } ";

        String query_outgoing_lang = "SELECT DISTINCT ?s ?p ?o WHERE { { ?s ?p ?o FILTER(?s = <[URI]>) FILTER(!isLiteral(?o)) } UNION { ?s ?p ?o FILTER(?s = <[URI]>) FILTER(lang(?o)='[LANG]') } } ";
        String query_both_lang = "SELECT DISTINCT ?s ?p ?o WHERE { { ?s ?p ?o FILTER(?s = <[URI]>) FILTER(!isLiteral(?o)) } UNION { ?s ?p ?o FILTER(?s = <[URI]>) FILTER(lang(?o)='[LANG]') } UNION { ?s ?p ?o FILTER(?o = <[URI]>) } } ";

        String templateQueryToRun = "";
        if (type.equals("outgoing")) {

            if (lang.equals("")) {
                templateQueryToRun = query_outgoing;
            } else {
                templateQueryToRun = query_outgoing_lang;
            }
        }

        if (type.equals("incoming")) {
            templateQueryToRun = query_incoming;
        }

        if (type.equals("both")) {

            if (lang.equals("")) {
                templateQueryToRun = query_both;
            } else {
                templateQueryToRun = query_both_lang;
            }
        }

        String queryToRun = templateQueryToRun.replace(templateParam, uri).replace(langParam, lang);
        if (!endpoint.toLowerCase().endsWith("?query=")) {
            endpoint = endpoint + "?query=";
        }
        String sparqlQueryPath = endpoint + URLEncoder.encode(queryToRun, "utf8");
        System.out.println("# SPARQL Query Path = " + sparqlQueryPath);

        String queryResponse = SparqlRunner.runQuery(endpoint, sparqlQueryPath);
        ArrayList<Triple> triples = Util.readSPARQLQueryResponseTriples(queryResponse);


        if (format.toLowerCase().equals("csv")) {
            response.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                for (Triple tr : triples) {
                    out.println(tr.s + "\t" + tr.p + "\t" + tr.o);
                }
            } finally {
                out.close();
            }
        } else if (format.toLowerCase().equals("xml")) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("triples");
            doc.appendChild(rootElement);

            for (Triple tr : triples) {

                Element atriple = doc.createElement("triple");
                Element sub = doc.createElement("subject");
                Element pr = doc.createElement("predicate");
                Element obj = doc.createElement("object");
                sub.appendChild(doc.createTextNode(tr.s));
                pr.appendChild(doc.createTextNode(tr.p));
                obj.appendChild(doc.createTextNode(tr.o));

                atriple.appendChild(sub);
                atriple.appendChild(pr);
                atriple.appendChild(obj);
                rootElement.appendChild(atriple);

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
        } else {

            JSONObject json = new JSONObject();
            JSONArray triplesJson = new JSONArray();

            for (Triple tr : triples) {
                JSONObject trObj = new JSONObject();
                trObj.put("subject", tr.s);
                trObj.put("predicate", tr.p);
                trObj.put("object", tr.o);
                triplesJson.add(trObj);
            }

            json.put("triples", triplesJson);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print(json);
            out.close();
        }

        System.out.println("# ENRICH - FINISHED!");
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
            Logger.getLogger(enrich.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(enrich.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(enrich.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(enrich.class.getName()).log(Level.SEVERE, null, ex);
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
