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
package gr.forth.ics.isl.xsearch.mining;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.Bean_Search;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.textentitymining.gate.GateEntityMiner;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class PageMining extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");

        String url = request.getParameter("url");
        PrintWriter out = null;

        if (url == null) {
            out.print("<h1>No URL given!</h1>");
            out.close();
            return;
        }
        
        if (url.trim().equals("")) {
            out.print("<h1>No URL given!</h1>");
            out.close();
            return;
        }


        String content = "";
        String how = "1";

        boolean pdf = false;

        try {

            out = response.getWriter();

            how = request.getParameter("how");
            if (how == null) {
                how = "2";
            }
            if (!how.equals("1") && !how.equals("2")) {
                how = "2";
            }

            URL the_url = new URL(url);
            URLConnection urlConn = the_url.openConnection();

            if (urlConn.getContentType().equalsIgnoreCase("application/pdf")) {

                System.out.println("# Reading PDF file!");
                pdf = true;

                try {
                    PdfReader reader = new PdfReader(the_url);
                    int n = reader.getNumberOfPages();

                    for (int i = 1; i <= n; i++) {
                        content += (PdfTextExtractor.getTextFromPage(reader, i) + "\n");
                    }
                    reader.close();
                    //System.out.println("# PDF CONTENT: \n" + source);
                } catch (Exception e) {
                    System.out.println("*** ERROR READING PDF CONTENT: " + e.getMessage());
                }

            } else if (urlConn.getContentType().equalsIgnoreCase("application/msword")) {
                //System.out.println("# Reading MSWORD file!");
            } else {
                HTMLTag tagger = new HTMLTag(the_url);
                content = tagger.getSourceCode();
            }


        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, request);
            out.print("<h1>No contents to mine! Please check the URL or try another page!</h1>");
            System.out.println("*** PROBLEM ADDING DOCUMENTS TO CORPUS:");
            Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
            out.close();
            return;
        }


        synchronized (this) {

            try {
                if (content == null) {
                    content = "";
                }

                if (!content.equals("")) {

                    content = content.replace("<?xml ", "<html ");

                    // MINE THE PAGE //     
                    GateEntityMiner miner = new GateEntityMiner();
                    miner.setAcceptedCategories(Resources.MINING_ACCEPTED_CATEGORIES);
                    miner.setTextToMine(content);
                    miner.findEntities();
                    ArrayList<Category> entities = miner.getEntities();

                    // ANNOTATE ENTITIES //
                    if (how.equals("1")) {
                        Collections.sort(entities);
                        showInRightBar(out, entities, url);
                    } else {
                        if (pdf) {
                            Collections.sort(entities);
                            showInRightBar(out, entities, url);
                        } else {
                            highlightText(out, entities, url, content);
                        }
                    }

                    System.out.println("# Page mining was finished!");

                } else {
                    out.print("<h1>No contents to mine! Please try another page!</h1>");
                    out.close();
                    return;
                }

                out.close();
                updateLog(request, url);

            } catch (Exception ex) {
                IOSLog.writeErrorToLog(ex, request);
                out.print("<h1>Please try again later!</h1>");
                System.out.println("*** PROBLEM ADDING DOCUMENTS TO CORPUS:");
                Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
                out.close();
            }
        }
    }

    private void highlightText(PrintWriter out, ArrayList<Category> entities, String url, String content) throws MalformedURLException {

        System.out.println("# Highlighting entities in the page...");

        int num = 0;
        content += "  ";
        int endOfHead = content.toLowerCase().indexOf("</head>");

        for (Category cat : entities) {
            String category = cat.getName();
            for (Entity en : cat.getEntities()) {

                String element = en.getName();
                if (element.trim().length() <= 2) {
                    continue;
                }

                //System.out.println("Entity:"+element);
                int index = content.toLowerCase().indexOf(element.toLowerCase());

                while (index != -1) {

                    if ((content.toLowerCase().charAt(index - 1) > 64 && content.toLowerCase().charAt(index - 1) < 91) || (content.toLowerCase().charAt(index - 1) > 96 && content.toLowerCase().charAt(index - 1) < 123)) {
                        index = content.toLowerCase().indexOf(element.toLowerCase(), index + element.length());
                        continue;
                    }

                    if ((content.toLowerCase().charAt(index + element.toLowerCase().length()) > 64 && content.toLowerCase().charAt(index + element.toLowerCase().length()) < 91) || (content.toLowerCase().charAt(index + element.toLowerCase().length()) > 96 && content.toLowerCase().charAt(index + element.toLowerCase().length()) < 123)) {
                        index = content.toLowerCase().indexOf(element.toLowerCase(), index + element.length());
                        continue;
                    }

                    if (index <= endOfHead) {
                        index = content.toLowerCase().indexOf(element.toLowerCase(), index + element.length());
                    } else {

                        int i1 = content.indexOf(">", index + 1);
                        int i2 = content.indexOf("<", index + 1);
                        if (i1 != -1 && i1 < i2) {
                            index = content.toLowerCase().indexOf(element.toLowerCase(), index + element.length());
                        } else {

                            String id = "entity_" + num;
                            String element_name_pass = element.replace("\"", "&quot;").replace("'", "&quot;").replace("%", "^^^^^").trim();
                            num++;
                            String spanPart = "<span class=\"highlighted_entity\" id=\"" + id + "\" style=\"background-color:yellow;\">";
                            int spanSize = spanPart.length();
                            String part1 = content.substring(0, index);
                            String part2 = content.substring(index + element.length(), content.length());
                            if (Resources.SPARQL_TEMPLATES.containsKey(category)) {
                                content = part1 + spanPart + content.substring(index, index + element.length()) + "</span><img border=\"0\" style=\"cursor: pointer\" onClick=\"return inspectEntity('" + category + "', '" + element_name_pass + "', '" + id + "');\" src='files/graphics/lod.jpg' title='Entity of category: " + category + ". Get more information about this entity!' />" + part2;
                            } else {
                                content = part1 + spanPart + content.substring(index, index + element.length()) + "</span>" + part2;
                            }
                            index = content.toLowerCase().indexOf(element.toLowerCase(), index + spanSize + element.length());
                        }
                    }
                }
            }
        }

        URL theurl = new URL(url);

        String urlHost = theurl.getHost();

        content = content.replace("href=\"..", "href=\"http://" + urlHost + "/..");
        content = content.replace("src=\"..", "src=\"http://" + urlHost + "/..");
        content = content.replace("href='..", "href='http://" + urlHost + "/..");
        content = content.replace("src='..", "src='http://" + urlHost + "/..");

        endOfHead = content.toLowerCase().indexOf("</head>");
        String head = content.substring(0, endOfHead);
        String body = content.substring(endOfHead);
        body = body.replace("href=\"/", "href=\"http://" + urlHost + "/");
        content = head + body;


        content = content.replace("</head>", " <link rel='icon' href='files/graphics/favicon.ico' type='image/x-icon' /> </head>");
        content = content.replace("</head>", " <link rel='stylesheet' type='text/css' href='css/box.css' /> </head>");
        content = content.replace("</head>", " <script type=\"text/javascript\" src=\"js/bookmarklet.js\"></script> </head> ");
        content = content.replace("</head>", " <script type=\"text/javascript\" src=\"js/jquery-1.7.1.min.js\"></script> </head> ");

        String box = "<div id='bubbleInfo' class='bubbleInfo'><table width='100%'><tr><td align='center' valign='middle'><font class='popup_title'>Entity Exploration</font>&nbsp;<a class=\"closePopup\" href=\"javascript:closePopup()\">(close)</a></td><td align='center' valign='middle'><img border='0' src='files/graphics/lod_big.png' width='28' height='30' /></td></tr><tr><td style='padding-left:15px'><div id='popup' class='popup'>Pop up Data here</div></td></tr><tr><td align='center' style='padding-left:15px; padding-top:5px;'><a class='closePopup' href='javascript:closePopup()'>(close)</a></td></tr></table></div>";
        content = content.replace("</html>", box + "</html>");
        out.print(content);

    }

    private void showInRightBar(PrintWriter out, ArrayList<Category> entities, String url) {

        System.out.println("# Loading entities in the sidebar...");

        out.print("<html><head>");
        out.print("<link rel='stylesheet' type='text/css' href='css/box.css' /> ");
        out.print("<link rel='icon' href='files/graphics/favicon.ico' type='image/x-icon' /> ");
        out.print("<script type=\"text/javascript\" src=\"js/bookmarklet.js\"></script> ");
        out.print("<script type=\"text/javascript\" src=\"js/jquery-1.7.1.min.js\"></script> ");
        out.print("</head>");
        out.print("<body>");

        out.print("<div style=\"width:100%;\">");
        out.print("<div style=\"width:60%; height:100%; float:left\">");
        out.print("<iframe width=\"100%\" height=\"100%\" src=\"" + url + "\"><p>Your browser does not support iframes.</p></iframe>");
        out.print("</div>");

        out.print("<div style=\"width:25%; height:100%; float:left; padding-left:10px;\">");
        int catNum = 0;
        for (Category cat : entities) {
            String category = cat.getName();
            out.println("<font class='em_category_name'>" + category + "</font>");
            out.println("<br />");
            int entNum = 0;
            for (Entity ent : cat.getEntities()) {

                String id = "entity_" + catNum + "_" + entNum;
                entNum++;

                String name = ent.getName();
                String element_name_pass = name.replace("\"", "&quot;").replace("'", "&quot;").replace("%", "^^^^^");
                name = name.replace("'", "&quot;").replace("\"", "&quot;");
                if (Resources.SPARQL_TEMPLATES.containsKey(category)) {
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;<font class='em_element_name' id='" + id + "'>" + "<a href=\"javascript:inspectEntityInSidebar('" + category + "', '" + element_name_pass + "', '" + id + "')\">" + name + "</a></font>");
                } else {
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;<font class='em_element_name' id='" + id + "'>" + name + "</font>");
                }

                out.println("<br />");
            }
            catNum++;
            out.println("<br />");
        }
        out.print("</div>");
        out.print("</div>");
        String box = "<div id='bubbleInfo' class='bubbleInfo'><table width='100%'><tr><td align='center' valign='middle'><font class='popup_title'>Entity Exploration</font>&nbsp;<a class=\"closePopup\" href=\"javascript:closePopup()\">(close)</a></td><td align='center' valign='middle'><img border='0' src='files/graphics/lod_big.png' width='28' height='30' /></td></tr><tr><td style='padding-left:15px'><div id='popup' class='popup'>Pop up Data here</div></td></tr><tr><td align='center' style='padding-left:15px; padding-top:5px;'><a class='closePopup' href='javascript:closePopup()'>(close)</a></td></tr></table></div>";
        out.print(box);

        out.print("</body>");
        out.print("</html>");
    }

    public void updateLog(HttpServletRequest request, String url) {

        String ip = request.getRemoteAddr();
        String date = IOSLog.getCurrentDate();

        String line = "\n" + Resources.SYSTEMNAME + "\t" + date + "\t" + ip + "\t" + "|NO_QUERY|" + "\tMINE PAGE " + url;
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
