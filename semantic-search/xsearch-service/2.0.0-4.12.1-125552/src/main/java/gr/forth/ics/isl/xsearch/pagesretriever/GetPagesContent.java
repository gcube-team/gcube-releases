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
package gr.forth.ics.isl.xsearch.pagesretriever;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.IOSLog;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class GetPagesContent extends Thread {

    private ArrayList<SearchResult> pages;
    public boolean finish = false;

    public GetPagesContent(ArrayList<SearchResult> pages) {
        super();
        this.pages = new ArrayList<SearchResult>(pages);
    }

    @Override
    public void run() {

        for (int i = 0; i < pages.size(); i++) {
            String url = pages.get(i).getUrl().toLowerCase();
            if (url.endsWith(".ppt") || url.endsWith(".doc") || url.endsWith(".pptx") || url.endsWith(".docx")) {
                continue;
            } else {
                pages.get(i).addContent(getContent(url));
            }
        }
        finish = true;
    }

    private String getContent(String url) {

        String source = "";
        try {

            URL the_url = new URL(url);
            URLConnection urlConn = the_url.openConnection();

            if (urlConn.getContentType().equalsIgnoreCase("application/pdf")) {
                System.out.println("# Reading PDF file!");

                PdfReader reader = new PdfReader(url);
                int n = reader.getNumberOfPages();

                for (int i = 1; i <= n; i++) {
                    source += (PdfTextExtractor.getTextFromPage(reader, i) + "\n");
                }

                reader.close();
                System.out.println("# PDF file was successfully read!");
            } else {
                HTMLTag tagger = new HTMLTag(the_url);
                source = tagger.getSourceCode();
            }

            if (source == null) {
                return "";
            } else {

                source = source.replace("<?xml ", "<html "); //otherwise AnnieGate cannot parse it!

            }

        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "GetPagesContent");
            System.out.println("*** ERROR RETRIEVING CONTENT OF: " + url);
            return "";
        }

        return source;

    }

    public ArrayList<SearchResult> getPages() {
        return pages;
    }

    public void setPages(ArrayList<SearchResult> pages) {
        this.pages = pages;
    }
}
