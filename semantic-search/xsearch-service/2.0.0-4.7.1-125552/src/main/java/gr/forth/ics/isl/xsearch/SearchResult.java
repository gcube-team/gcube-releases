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

import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.net.URL;
import java.net.URLConnection;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class SearchResult {

    private String title;
    private String url;
    private String description;
    private int rank;
    private String content;
    private boolean hasRetrieved;

    public SearchResult(String title, String url, String description, int rank) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.rank = rank;
        this.content = title + "  " + description;
        this.hasRetrieved = false;
    }

    public SearchResult() {
        title = "";
        url = "";
        description = "";
        rank = -1;
        content = "";
        hasRetrieved = false;
    }

    public void retriveContent() {

        try {
            URL the_url = new URL(url);
            URLConnection urlConn = the_url.openConnection();
            String source = "";

            if (urlConn.getContentType().equalsIgnoreCase("application/pdf")) {
                System.out.println("# Reading PDF file!");

                try {
                    PdfReader reader = new PdfReader(the_url);
                    int n = reader.getNumberOfPages();

                    for (int i = 1; i <= n; i++) {
                        source += (PdfTextExtractor.getTextFromPage(reader, i) + "\n");
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
                source = tagger.getSourceCode();
            }

            if (source == null) {
                return;
            } else {

                source = source.replace("<?xml ", "<html "); //otherwise AnnieGate cannot parse it!

                addContent(source);
                hasRetrieved = true;
            }
        } catch (Exception e) {
            IOSLog.writeErrorToLog(e, "SearchResult");
            System.out.println("*** ERROR RETRIEVING THE CONTENT OF: " + url + "\n" + e.getMessage());
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content += (" | " + content);
        hasRetrieved = true;
    }

    public boolean isHasRetrieved() {
        return hasRetrieved;
    }

    public void setHasRetrieved(boolean hasRetrieved) {
        this.hasRetrieved = hasRetrieved;
    }
}
