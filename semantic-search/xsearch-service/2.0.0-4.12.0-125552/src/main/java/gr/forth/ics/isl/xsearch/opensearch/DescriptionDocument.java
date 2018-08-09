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

package gr.forth.ics.isl.xsearch.opensearch;

import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class DescriptionDocument {

    private String path;
    private String shortName;
    private String description;
    private String inputEncoding;
    private String exampleQuery;
    private HashMap<String, String> urlTemplates; // type -> url
    private String content;
    private boolean errorReadingDocument;

    public DescriptionDocument(String path) throws MalformedURLException, FileNotFoundException, UnsupportedEncodingException, IOException {
        errorReadingDocument = false;
        this.path = path;
        urlTemplates = new HashMap<String, String>();

        if (path.toLowerCase().startsWith("http")) {
            readHttpContent();
        } else {
            readFileContent();
        }

        readDescriptionDocument();
    }

    private void readDescriptionDocument() {

        HTMLTag tagger = new HTMLTag(content);

        this.shortName = tagger.getFirstTagData("shortName");
        if (this.shortName == null) {
            this.shortName = "-";
        }

        this.description = tagger.getFirstTagData("Description");
        if (this.description == null) {
            this.description = "-";
        }

        this.inputEncoding = tagger.getFirstTagData("InputEncoding");
        String exampleQueryTmp = tagger.getFirstTagContentContains("Query", "role=\"example\"");
        if (exampleQueryTmp != null) {
            this.exampleQuery = HTMLTag.getContentAttribute("searchTerms", exampleQueryTmp);
            if (this.exampleQuery == null) {
                this.exampleQuery = "";
            }
        }

        int ind = tagger.getFirstTagIndex("Url");
        while (ind != -1) {
            String urlContent = tagger.getFirstTagContent("Url", ind - 1).trim();
            String type = HTMLTag.getContentAttribute("type", urlContent).trim();
            String template = HTMLTag.getContentAttribute("template", urlContent).trim();

            urlTemplates.put(type, template);
            ind = tagger.getFirstTagIndex("Url", ind + 2);
        }

    }

    private void readFileContent() throws FileNotFoundException, UnsupportedEncodingException, IOException {
    
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF8"));

            content = "";
            String line;
            while ((line = in.readLine()) != null) {
                content += (line + "\n");
            }
            in.close();
       
    }

    private void readHttpContent() throws MalformedURLException {
       
            URL theurl = new URL(path);
            HTMLTag tagger = new HTMLTag(theurl, true);
            content = tagger.getSourceCode();
       
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputEncoding() {
        return inputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public HashMap<String, String> getUrlTemplates() {
        return urlTemplates;
    }

    public void setUrlTemplates(HashMap<String, String> urlTemplates) {
        this.urlTemplates = urlTemplates;
    }

    public String getExampleQuery() {
        return exampleQuery;
    }

    public void setExampleQuery(String exampleQuery) {
        this.exampleQuery = exampleQuery;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isErrorReadingDocument() {
        return errorReadingDocument;
    }

    public void setErrorReadingDocument(boolean errorReadingDocument) {
        this.errorReadingDocument = errorReadingDocument;
    }
    
    
    
}
