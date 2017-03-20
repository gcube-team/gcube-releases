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
package gr.forth.ics.isl.xsearch.ecoscope;

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.retriever.ResultsRetriever;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class EcoscopeRetriever implements ResultsRetriever {

    private final String url = "http://ecoscopebc.mpl.ird.fr/EcoscopeKB/GetOpensearchGenesiDec?freeText=";
    private String query;
    private ArrayList<SearchResult> results;
    ArrayList<EcoscopeResult> ecoscopeResults;
    private int desiredNumber;
    private HashMap<String, String> parameters;

    public EcoscopeRetriever(String query, int desiredNumber) {
        this.query = query;
        this.desiredNumber = desiredNumber;
        results = new ArrayList<SearchResult>();
        ecoscopeResults = new ArrayList<EcoscopeResult>();
        parameters = new HashMap<String, String>();
    }

    @Override
    public void retrieveResults() {
        try {

            if (desiredNumber <= 0) {
                desiredNumber = Resources.MAX_NUM_OF_RESULTS_FROM_WSE;
            }

            String enc_query = URLEncoder.encode(query.trim(), "utf-8");
            String queryPath = url + enc_query;

            if (!parameters.isEmpty()) {

                String paramsString = "";
                for (String paramKey : parameters.keySet()) {
                    String paramValue = parameters.get(paramKey);
                    paramsString += ("&" + paramKey + "=" + paramValue);
                }
                queryPath += paramsString;
            }

            System.out.println("QUERY PATH: " + queryPath);
            URL theurl = new URL(queryPath);
            HTMLTag tagger1 = new HTMLTag(theurl, true);

            int n = tagger1.getFirstTagIndex("rdf:Description");
            int num = 0;
            while (n != -1) {

                EcoscopeResult ecoscopeResult = new EcoscopeResult();
                SearchResult result = new SearchResult();
                result.setRank(num);


                // GET RDF:ABOUT OF RDF:DESCRIPTION //
                String aboutStr = tagger1.getFirstTagContent("rdf:Description", n - 1);
                String about = HTMLTag.getContentAttribute("rdf:about", aboutStr);
                if (about == null) {
                    about = "";
                }
                if (about.equals("")) {
                    about = "{-}";
                }
                ecoscopeResult.setUri(about);
                result.setUrl(about);

                // GET ALL CONTENT OF RDF:DESCRIPTION //
                String descrData = tagger1.getFirstTagData("rdf:Description", n - 1);
                HTMLTag tagger2 = new HTMLTag(descrData);


                // GET PREF LABELS //
                HashMap<String, String> prefLabels = new HashMap<String, String>();
                int i_pl = tagger2.getFirstTagIndex("j.1:prefLabel");
                String title = "";
                while (i_pl != -1) {
                    String label_attr = tagger2.getFirstTagContent("j.1:prefLabel", i_pl - 1);
                    if (label_attr == null) {
                        label_attr = "";
                    }
                    String label_cont = tagger2.getFirstTagData("j.1:prefLabel", i_pl - 1);
                    if (label_cont == null) {
                        label_cont = "";
                    }
                    label_cont = label_cont.trim();

                    String lang = HTMLTag.getContentAttribute("xml:lang", label_attr);
                    if (lang == null) {
                        lang = "";
                    }
                    lang = lang.trim().toLowerCase();
                    prefLabels.put(lang, label_cont);
                    if (lang.equals("en") || lang.equals("")) {
                        title = label_cont;
                    }

                    i_pl = tagger2.getFirstTagIndex("j.1:prefLabel", i_pl + 2);
                }
                ecoscopeResult.setPrefLabels(prefLabels);

                if (title.equals("")) {
                    title = prefLabels.get("fr");
                }
                if (title == null) {
                    title = "";
                }
                result.setTitle(title);

                // GET DC:TITLE IF NO PREF LABELS
                if (prefLabels.isEmpty()) {

                    HashMap<String, String> dcTitles = new HashMap<String, String>();
                    int i_dc = tagger2.getFirstTagIndex("dc:title");
                    while (i_dc != -1) {
                        String dcTitle_attr = tagger2.getFirstTagContent("dc:title", i_dc - 1);
                        if (dcTitle_attr == null) {
                            dcTitle_attr = "";
                        }
                        String dcTitle_cont = tagger2.getFirstTagData("dc:title", i_dc - 1);
                        if (dcTitle_cont == null) {
                            dcTitle_cont = "";
                        }

                        String lang = HTMLTag.getContentAttribute("xml:lang", dcTitle_attr);
                        if (lang == null) {
                            lang = "";
                        }
                        lang = lang.trim().toLowerCase();
                        dcTitles.put(lang, dcTitle_cont);
                        if (lang.equals("en") || lang.equals("")) {
                            title = dcTitle_cont;
                        }

                        i_dc = tagger2.getFirstTagIndex("dc:title", i_dc + 2);
                    }
                    ecoscopeResult.setTitles(dcTitles);

                    if (title.equals("")) {
                        title = dcTitles.get("fr");
                    }
                    if (title == null) {
                        title = "";
                    }
                    result.setTitle(title);
                }


                // GET DESCRIPTIONS //
                HashMap<String, String> prefDescriptions = new HashMap<String, String>();
                int i_d = tagger2.getFirstTagIndex("dc:description");
                String descr = "";
                while (i_d != -1) {
                    String descr_attr = tagger2.getFirstTagContent("dc:description", i_d - 1);
                    if (descr_attr == null) {
                        descr_attr = "";
                    }
                    String descr_cont = tagger2.getFirstTagData("dc:description", i_d - 1);
                    if (descr_cont == null) {
                        descr_cont = "";
                    }

                    String lang = HTMLTag.getContentAttribute("xml:lang", descr_attr);
                    if (lang == null) {
                        lang = "";
                    }
                    lang = lang.trim().toLowerCase();
                    prefDescriptions.put(lang, descr_cont);
                    if (lang.equals("en") || lang.equals("")) {
                        descr = descr_cont;
                    }

                    i_d = tagger2.getFirstTagIndex("dc:description", i_d + 2);
                }
                ecoscopeResult.setDescriptions(prefDescriptions);

                if (descr.equals("")) {
                    descr = prefDescriptions.get("fr");
                }
                if (descr == null) {
                    descr = "";
                }
                result.setDescription(descr);

                // GET COMMENTS IF NO DESCRIPTIONS
                if (prefDescriptions.isEmpty()) {

                    HashMap<String, String> comments = new HashMap<String, String>();
                    int i_c = tagger2.getFirstTagIndex("rdfs:comment");
                    while (i_c != -1) {
                        String comment_attr = tagger2.getFirstTagContent("rdfs:comment", i_c - 1);
                        if (comment_attr == null) {
                            comment_attr = "";
                        }
                        String comment_cont = tagger2.getFirstTagData("rdfs:comment", i_c - 1);
                        if (comment_cont == null) {
                            comment_cont = "";
                        }

                        String lang = HTMLTag.getContentAttribute("xml:lang", comment_attr);
                        if (lang == null) {
                            lang = "";
                        }
                        lang = lang.trim().toLowerCase();
                        comments.put(lang, comment_cont);
                        if (lang.equals("en") || lang.equals("")) {
                            descr = comment_cont;
                        }

                        i_c = tagger2.getFirstTagIndex("rdfs:comment", i_c + 2);
                    }
                    ecoscopeResult.setComments(comments);

                    if (descr.equals("")) {
                        descr = comments.get("fr");
                    }
                    if (descr == null) {
                        descr = "";
                    }
                    result.setDescription(descr);

                }

                result.setContent(result.getTitle() + " " + result.getDescription());
                if (result.getContent().trim().equals("")) {
                    result.setContent("- - -");
                }

                ecoscopeResults.add(ecoscopeResult);
                results.add(result);

                num++;
                if (num == desiredNumber) {
                    break;
                }

                n = tagger1.getFirstTagIndex("rdf:Description", n + 2);
            }




        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "EcoscopeRetriever Error 1");
            System.out.println("*** COULD NOT RETRIEVE RESULTS FROM iMarine!");
            Logger.getLogger(EcoscopeRetriever.class.getName()).log(Level.SEVERE, null, ex);
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

    public String getUrl() {
        return url;
    }

    public void setResults(ArrayList<SearchResult> results) {
        this.results = results;
    }

    public ArrayList<EcoscopeResult> getEcoscopeResults() {
        return ecoscopeResults;
    }

    public void setEcoscopeResults(ArrayList<EcoscopeResult> ecoscopeResults) {
        this.ecoscopeResults = ecoscopeResults;
    }
}
