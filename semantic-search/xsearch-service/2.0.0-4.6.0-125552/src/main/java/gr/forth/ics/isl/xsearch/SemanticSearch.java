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
package gr.forth.ics.isl.xsearch;

import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class SemanticSearch extends HttpServlet {

    private String query;
    private int n;
    private boolean mining;
    private boolean clustering;
    private boolean only_snippets;
    private int numOfClusters;
    private String locator;
    int resultsStartOffset;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, GRS2ReaderException, URISyntaxException, GRS2RecordDefinitionException, GRS2BufferException {

        synchronized (this) {

            response.setContentType("text/plain;charset=UTF-8");

            long start, end;
            start = System.currentTimeMillis();

            query = request.getParameter("query");
            if (query == null) {
                query = "";
            }

            String num = request.getParameter("n");
            try {
                n = Integer.parseInt(num);
            } catch (Exception e) {
                n = 1000;
            }

            String type = request.getParameter("type");
            if (type == null) {
                type = "";
            }
            if (type.equals("fullContent")) {
                only_snippets = false;
            } else {
                only_snippets = true;
            }

            String mining_checkbox = request.getParameter("mining");
            if (mining_checkbox == null) {
                mining_checkbox = "false";
            }

            if (mining_checkbox.toLowerCase().equals("true")) {
                mining = true;
            } else {
                mining = false;
            }

            String clustering_checkbox = request.getParameter("clustering");
            if (clustering_checkbox == null) {
                clustering_checkbox = "false";
            }

            if (clustering_checkbox.toLowerCase().equals("true")) {
                clustering = true;
            } else {
                clustering = false;
            }

            String clnum = request.getParameter("clnum");
            if (clnum == null) {
                if (clustering) {
                    numOfClusters = 15;
                } else {
                    numOfClusters = 0;
                }
            } else {
                numOfClusters = Integer.parseInt(clnum);
            }

            locator = request.getParameter("locator");
            if (locator == null) {
                System.out.println("*** NULL LOCATOR!");
                locator = "";
            }
            locator = URLDecoder.decode(locator, "utf8");

            String startOffset = request.getParameter("resultsStartOffset");
            if (startOffset == null) {
                System.out.println("*** StartOffset is NULL!!!");
                resultsStartOffset = 0;
            } else {
                resultsStartOffset = Integer.parseInt(startOffset);
                System.out.println("The startOffset value is: " + startOffset);
            }

            Bean_Search results = new Bean_Search(query, resultsStartOffset, clustering, numOfClusters, mining, only_snippets, locator);
            int numOfResults = results.getWseResults().size();

            String jsonResults = results.getJsonResults();
            PrintWriter out = response.getWriter();
            out.print(jsonResults);

            end = System.currentTimeMillis() - start;
            System.out.println("# TOTAL TIME: " + end + " ms.");
            System.out.println("--------");

            updateLog(request, n, mining, clustering, only_snippets, numOfClusters, numOfResults, end);

        }

    }

    private void updateLog(HttpServletRequest request, int num, boolean mining, boolean clustering, boolean only_snippets, int clnum, int numOfResults, long time) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t(GCUBE REQUEST)\tSUBMITTED_BY_GCUBE NUM=" + num + " MINING=" + mining + " CLUSTERING=" + clustering + " ONLY_SNIPPETS=" + only_snippets + " CLNUM=" + clnum + " NUM_OF_RETURNED_RESULTS=" + numOfResults + " RETRIEVAL_TIME=" + time + "ms";

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
        try {
            processRequest(request, response);
        } catch (GRS2ReaderException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GRS2RecordDefinitionException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GRS2BufferException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (GRS2ReaderException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GRS2RecordDefinitionException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GRS2BufferException ex) {
            Logger.getLogger(SemanticSearch.class.getName()).log(Level.SEVERE, null, ex);
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
