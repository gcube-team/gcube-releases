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

import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.retriever.AtomRetriever;
import gr.forth.ics.isl.xsearch.retriever.RSSRetriever;
import gr.forth.ics.isl.xsearch.retriever.ResultsRetriever;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class OpenSearchRetriever implements ResultsRetriever {

    private String template;
    private String searchTerms;
    private String type;
    private int count;
    private ArrayList<SearchResult> results;
    private HashMap<String, String> parameters;
    private boolean supportsPaging;

    public OpenSearchRetriever(String template, String type, String searchTerms) {
        this.template = template.replace("&amp;", "&");
        this.type = type;
        this.searchTerms = searchTerms;
        this.count = -1;
        this.results = new ArrayList<SearchResult>();
        this.parameters = new HashMap<String, String>();
        this.supportsPaging = false;

        // FIND ALL TEMPLATE PARAMETERS //
        try {
            int ind = template.indexOf("{");
            while (ind != -1) {
                int ind2 = template.indexOf("}", ind);
                String parameter = template.substring(ind + 1, ind2);
                if (parameter.toLowerCase().contains("startindex")) {
                    supportsPaging = true;
                }
                parameters.put(parameter, "");
                ind = template.indexOf("{", ind + 1);
            }
        } catch (Exception e) {
            System.out.println("*** ERROR FINDING TEMPLATE PARAMETERS! PLEASE CHECK THE OPEN SEARCH DESCRIPTION DOCUMENT! ");
        }
    }

    @Override
    public void retrieveResults() {

        if (count <= 0) {
            count = Resources.MAX_NUM_OF_RESULTS_FROM_WSE;
        }

        if (supportsPaging) {

            int numOfPages = 1;
            if (count > 50) {
                numOfPages = count / 50;
                if (count % 50 != 0) {
                    numOfPages++;
                }
            }

            for (int i = 0; i < numOfPages; i++) {

                String template_temp = template;

                // FORMING THE TEMPLATE ACCORDING TO THE PARAMETERS //
                for (String parameter : parameters.keySet()) {
                    if (parameter.toLowerCase().contains("searchterms")) {

                        String encodedSearchTerms = searchTerms;
                        try {
                            encodedSearchTerms = URLEncoder.encode(searchTerms, "utf-8");
                        } catch (UnsupportedEncodingException ex) {
                            System.out.println("*** ERROR ENCODING QUERY TERMS: " + ex.getMessage());
                            //Logger.getLogger(OpenSearchRetriever.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        template_temp = template_temp.replace("{" + parameter + "}", encodedSearchTerms);

                    } else if (parameter.toLowerCase().contains("count")) {
                        template_temp = template_temp.replace("{" + parameter + "}", "50");
                    } else if (parameter.toLowerCase().contains("startindex")) {
                        template_temp = template_temp.replace("{" + parameter + "}", "" + (i * 50));
                    } else {
                        template_temp = template_temp.replace("{" + parameter + "}", "");
                    }
                }

                if (i > 0) {
                    // RETRIEVING RESULTS FROM THE UNDERLYING (OPEN SEARCH) SOURCE //
                    ResultsRetriever retriever = null;
                    if (type.toLowerCase().equals("application/rss+xml")) { // RSS RESULTS
                        retriever = new RSSRetriever(template_temp);
                    } else if (type.toLowerCase().equals("application/atom+xml")) {
                        retriever = new AtomRetriever(template_temp);
                    } else {
                        System.out.println("*** NO SUPPORTED TEMPLATE TYPE: " + type);
                    }

                    if (retriever != null) {

                        retriever.setDesiredNumber(count);
                        retriever.retrieveResults();


                        ArrayList<SearchResult> resultsTemp = retriever.getResults();
                        for (SearchResult res : resultsTemp) {
                            boolean cont = false;
                            for (SearchResult res2 : results) {
                                if (res.getUrl().toLowerCase().equals(res2.getUrl().toLowerCase())) {
                                    cont = true;
                                    break;
                                }
                            }
                            if (!cont) {
                                results.add(res);
                            }
                        }
                    }


                } else {
                    // RETRIEVING RESULTS FROM THE UNDERLYING (OPEN SEARCH) SOURCE //
                    ResultsRetriever retriever = null;
                    if (type.toLowerCase().equals("application/rss+xml")) { // RSS RESULTS
                        retriever = new RSSRetriever(template_temp);
                    } else if (type.toLowerCase().equals("application/atom+xml")) {
                        retriever = new AtomRetriever(template_temp);
                    } else {
                        System.out.println("*** NO SUPPORTED TEMPLATE TYPE: " + type);
                    }

                    if (retriever != null) {
                        retriever.setDesiredNumber(count);
                        retriever.retrieveResults();
                        results.addAll(retriever.getResults());
                    }
                }
            }
        } else {
            // FORMING THE TEMPLATE ACCORDING TO THE PARAMETERS //
            for (String parameter : parameters.keySet()) {
                if (parameter.toLowerCase().contains("searchterms")) {

                    String encodedSearchTerms = searchTerms;
                    try {
                        encodedSearchTerms = URLEncoder.encode(searchTerms, "utf-8");
                    } catch (UnsupportedEncodingException ex) {
                        System.out.println("*** ERROR ENCODING QUERY TERMS: " + ex.getMessage());
                        //Logger.getLogger(OpenSearchRetriever.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    template = template.replace("{" + parameter + "}", encodedSearchTerms);

                } else if (parameter.toLowerCase().contains("count")) {
                    template = template.replace("{" + parameter + "}", "" + count);
                } else {
                    template = template.replace("{" + parameter + "}", "");
                }
            }

            // RETRIEVING RESULTS FROM THE UNDERLYING (OPEN SEARCH) SOURCE //
            ResultsRetriever retriever = null;
            if (type.toLowerCase().equals("application/rss+xml")) { // RSS RESULTS
                retriever = new RSSRetriever(template);
            } else if (type.toLowerCase().equals("application/atom+xml")) {
                retriever = new AtomRetriever(template);
            } else {
                System.out.println("*** NO SUPPORTED TEMPLATE TYPE: " + type);
            }

            if (retriever != null) {
                retriever.setDesiredNumber(count);
                retriever.retrieveResults();
                results.addAll(retriever.getResults());
            }
        }



    }

    @Override
    public ArrayList<SearchResult> getResults() {
        return results;
    }

    @Override
    public void setQuery(String query) {
        this.searchTerms = query;
    }

    @Override
    public String getQuery() {
        return searchTerms;
    }

    @Override
    public void setDesiredNumber(int desiredNumber) {
        this.count = desiredNumber;
    }

    @Override
    public int getDesiredNumber() {
        return count;
    }

    @Override
    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public HashMap<String, String> getParameters() {
        return parameters;
    }
}
