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

package gr.forth.ics.isl.xsearch.retriever;

import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class AtomRetriever implements ResultsRetriever {

    private String url;
    private String query;
    private String queryAttribute;
    private int desiredNumber;
    private HashMap<String, String> parameters;
    private ArrayList<SearchResult> results;

    public AtomRetriever(String url) {
        this.url = url;
        this.queryAttribute = null;
        this.query = null;
        this.desiredNumber = -1;
        this.parameters = new HashMap<String, String>();
        this.results = new ArrayList<SearchResult>();
    }

    @Override
    public void retrieveResults() {

        try {

            if (desiredNumber <= 0) {
                desiredNumber = Resources.MAX_NUM_OF_RESULTS_FROM_WSE;
            }

            String queryPath = url;
            if (query != null && queryAttribute != null) {
                String encQuery = URLEncoder.encode(query, "utf-8");
                if (!queryPath.contains("?")) {
                    queryPath += "?";
                } else {
                    queryPath += "&";
                }
                queryPath += (queryAttribute + "=" + encQuery);
            }

            if (!parameters.isEmpty()) {
                if (!queryPath.contains("?")) {
                    queryPath += "?";
                }

                String paramsString = "";
                for (String paramKey : parameters.keySet()) {
                    String paramValue = parameters.get(paramKey);
                    paramsString += ("&" + paramKey + "=" + paramValue);
                }
                queryPath += paramsString;
            }

            System.out.println("# QUERY PATH: " + queryPath);
            URL theurl = new URL(queryPath);
            HTMLTag tagger = new HTMLTag(theurl, true);

            int i1 = tagger.getFirstTagIndex("entry");
            int num = 1;
            while (i1 != -1) {


                String title = tagger.getFirstTagData("title", i1);
                if (title == null) {
                    title = "";
                }

                String link = tagger.getFirstTagContentContains("link", "http", i1);
                if (link == null) {
                    link = tagger.getFirstTagContent("link", i1);
                    if (link == null) {
                        link = "#";
                    }
                }
                
                link = tagger.getHref(link);
                
                if (link == null) {
                    link = "#";
                } else {
                    if (link.trim().equals("")) {
                        link = "#";
                    }
                }


                String description = tagger.getFirstTagData("summary", i1);
                if (description == null) {
                    description = "";
                }

                SearchResult result = new SearchResult(title, link, description, num);
                results.add(result);

                if (num == desiredNumber) {
                    break;
                }

                num++;

                i1 = tagger.getFirstTagIndex("entry", i1 + 2);
            }

        } catch (Exception e) {
            System.out.println("*** ERROR RETRIEVING RESULTS: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<SearchResult> getResults() {
        return results;
    }

    @Override
    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void setDesiredNumber(int desiredNumber) {
        this.desiredNumber = desiredNumber;
    }

    @Override
    public int getDesiredNumber() {
        return desiredNumber;
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setQueryAttribute(String queryAttribute) {
        this.queryAttribute = queryAttribute;
    }

    public String getQueryAttribute() {
        return queryAttribute;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
