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

package gr.forth.ics.isl.xsearch.fao;

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.resources.Resources;
import gr.forth.ics.isl.xsearch.retriever.ResultsRetriever;
import gr.forth.ics.isl.xsearch.util.HTMLTag;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class FigisRetriever implements ResultsRetriever {

    private final String url = "http://www.fao.org/fi/oldsite/eims_search/advanced_s_result.asp?querystring=";
    private String query;
    private ArrayList<SearchResult> results;
    ArrayList<iMarineResult> figisResults;
    private int desiredNumber;
    private HashMap<String, String> parameters;

    public FigisRetriever(String query, int desiredNumber) {
        this.query = query;
        this.desiredNumber = desiredNumber;
        results = new ArrayList<SearchResult>();
        figisResults = new ArrayList<iMarineResult>();
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
                //PARAMETERS: &owner=fi&xml=y&xml_no_subject=&FORM_C=AND&sortorder=3&pub_year=2011";
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

            int n = tagger1.getFirstTagIndex("publication");
            int num = 0;
            while (n != -1) {

                iMarineResult marineResult = new iMarineResult();
                SearchResult result = new SearchResult();
                result.setRank(num);


                // GET PUBLICATION ID 
                String pub_id = tagger1.getFirstTagContent("publication", n - 1);
                pub_id = getIdAttribute(pub_id);
                marineResult.setId(pub_id);

                String pub_data = tagger1.getFirstTagData("publication", n - 1);

                HTMLTag tagger2 = new HTMLTag(pub_data);

                // GET LANGUAGES //
                HashSet<String> languages = new HashSet<String>();
                String lan = tagger2.getFirstTagData("dc:language");
                String[] langs = lan.split("/");
                languages.addAll(Arrays.asList(langs));
                marineResult.setLanguages(languages);

                // GET TYPE //
                String type = tagger2.getFirstTagData("dc:type");
                marineResult.setType(type);

                // GET CREATOR //
                String creator = tagger2.getFirstTagData("dc:creator");
                marineResult.setCreator(creator);

                // GET TITLES AND TITLE SUPPLEMENT IF EXISTS //
                HashMap<String, String> titles = new HashMap<String, String>();
                String titleSupplement = "";
                int i = tagger2.getFirstTagIndex("dc:title");
                while (i != -1) {
                    String title_attr = tagger2.getFirstTagContent("dc:title", i - 1);
                    String title_cont = tagger2.getFirstTagData("dc:title", i - 1);
                    if (title_cont == null) {
                        title_cont = "";
                    }

                    if (title_cont.contains("ags:titleSupplement")) {
                        titleSupplement = tagger2.getFirstTagData("ags:titleSupplement");
                    }

                    String lang = getLangAttribute(title_attr);
                    if (lang.toLowerCase().equals("en") && !title_cont.contains("ags:titleSupplement")) {
                        result.setTitle(title_cont);
                    }
                    titles.put(lang, title_cont);

                    i = tagger2.getFirstTagIndex("dc:title", i + 2);
                }
                marineResult.setTitles(titles);
                marineResult.setTitleSupplement(titleSupplement);
                if (!result.getTitle().trim().equals("")) {
                    result.setTitle(type + " (" + pub_id + ") - " + result.getTitle());
                } else {
                    result.setTitle(type + " (" + pub_id + ")");
                }


                // GET DESCRIPTIONS //
                HashMap<String, String> descriptions = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("dcterms:abstract");
                while (i != -1) {
                    String descr_attr = tagger2.getFirstTagContent("dcterms:abstract", i - 1);
                    String descr_cont = tagger2.getFirstTagData("dcterms:abstract", i - 1);
                    if (descr_cont == null) {
                        descr_cont = "";
                    }

                    String lang = getLangAttribute(descr_attr);
                    if (lang.toLowerCase().equals("en")) {
                        result.setDescription(descr_cont);
                    }
                    descriptions.put(lang, descr_cont);

                    i = tagger2.getFirstTagIndex("dcterms:abstract", i + 2);
                }
                marineResult.setDescriptions(descriptions);


                // GET PAGES //
                String pages = tagger2.getFirstTagData("pages");
                if (pages == null) {
                    pages = "-";
                }
                if (pages.equals("")) {
                    pages = "-";
                }
                marineResult.setPages(pages);

                // GET IDENTIFIERS //
                HashMap<String, String> identifiers = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("dc:identifier");
                while (i != -1) {
                    String ident_attr = tagger2.getFirstTagContent("dc:identifier", i - 1);
                    String ident_cont = tagger2.getFirstTagData("dc:identifier", i - 1);

                    String scheme = getSchemeAttribute(ident_attr);
                    identifiers.put(scheme, ident_cont);

                    i = tagger2.getFirstTagIndex("dc:identifier", i + 2);
                }

                if (identifiers.containsKey("URI")) {
                    result.setUrl(identifiers.get("URI"));
                } else if (identifiers.containsKey("PDF_URI")) {
                    result.setUrl(identifiers.get("PDF_URI"));
                } else {
                    if (identifiers.isEmpty()) {
                        result.setUrl("{-}");
                    } else {
                        result.setUrl(identifiers.toString());
                    }

                }


                // GET DATES //
                HashMap<String, String> dates = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("dc:date");
                while (i != -1) {
                    String date_attr = tagger2.getFirstTagContent("dc:date", i - 1);
                    String date_cont = tagger2.getFirstTagData("dc:date", i - 1);

                    String scheme = getSchemeAttribute(date_attr);
                    dates.put(scheme, date_cont);

                    i = tagger2.getFirstTagIndex("dc:date", i + 2);
                }
                marineResult.setDates(dates);

                // GET IS PART OF //
                HashMap<String, String> isPartOf = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("dcterms:isPartOf");
                while (i != -1) {
                    String part_attr = tagger2.getFirstTagContent("dcterms:isPartOf", i - 1);
                    String part_cont = tagger2.getFirstTagData("dcterms:isPartOf", i - 1);

                    String lang = getLangAttribute(part_attr);
                    isPartOf.put(lang, part_cont);

                    i = tagger2.getFirstTagIndex("dcterms:isPartOf", i + 2);
                }
                marineResult.setIsPartOf(isPartOf);

                // GET HAS VERSION //
                HashMap<Integer, String> hasVersion = new HashMap<Integer, String>();
                i = tagger2.getFirstTagIndex("dcterms:hasVersion");
                while (i != -1) {
                    String ver_attr = tagger2.getFirstTagContent("dcterms:hasVersion", i - 1);
                    String ver_cont = tagger2.getFirstTagData("dcterms:hasVersion", i - 1);

                    String infotypeStr = getInfotypeAttribute(ver_attr);
                    int infotype = -1;
                    if (infotypeStr != null) {

                        try {
                            infotype = Integer.parseInt(infotypeStr);
                        } catch (Exception e) {
                            IOSLog.writeErrorToLog(e, "FigisRetriever 2");
                            infotype = -1;
                        }
                    }

                    hasVersion.put(infotype, ver_cont);

                    i = tagger2.getFirstTagIndex("dcterms:hasVersion", i + 2);
                }
                marineResult.setHasVersion(hasVersion);

                // GET DEPARTMENT //
                String department = tagger2.getFirstTagData("department");
                marineResult.setDepartment(department);

                // GET DIVISION //
                String division = tagger2.getFirstTagData("division");
                marineResult.setDevision(division);

                // GET NOTES //
                String notes = tagger2.getFirstTagData("notes");
                marineResult.setNotes(notes);
                if (notes != null) {
                    result.setDescription(result.getDescription() + " - " + notes);
                }

                result.setContent(result.getTitle() + " " + result.getDescription());
                if (result.getContent().trim().equals("")) {
                    result.setContent("- - -");
                }

                // GET SUBJECT THESAURUS //
                HashMap<String, String> subjectThesaurus = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("ags:subjectThesaurus");
                while (i != -1) {
                    String sub_attr = tagger2.getFirstTagContent("ags:subjectThesaurus", i - 1);
                    String sub_cont = tagger2.getFirstTagData("ags:subjectThesaurus", i - 1);

                    String scheme = getSchemeAttribute(sub_attr);
                    String s_id = getIdAttribute(sub_attr);

                    subjectThesaurus.put((scheme + "-" + s_id), sub_cont);

                    i = tagger2.getFirstTagIndex("ags:subjectThesaurus", i + 2);
                }
                marineResult.setSubjectThesaurus(subjectThesaurus);

                // GET SUBJECT CLASSIFICATION //
                HashMap<String, String> subjectClassifications = new HashMap<String, String>();
                i = tagger2.getFirstTagIndex("ags:subjectClassification");
                while (i != -1) {
                    String sub_attr = tagger2.getFirstTagContent("ags:subjectClassification", i - 1);
                    String sub_cont = tagger2.getFirstTagData("ags:subjectClassification", i - 1);

                    String scheme = getSchemeAttribute(sub_attr);
                    subjectClassifications.put(scheme, sub_cont);

                    i = tagger2.getFirstTagIndex("ags:subjectClassification", i + 2);
                }
                marineResult.setSubjectClassification(subjectClassifications);


                figisResults.add(marineResult);
                results.add(result);

                num++;
                if (num == desiredNumber) {
                    break;
                }
                n = tagger1.getFirstTagIndex("publication", n + 2);
            }




        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "FigisRetriever 3");
            System.out.println("*** COULD NOT RETRIEVE RESULTS FROM iMarine!");
            Logger.getLogger(FigisRetriever.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getInfotypeAttribute(String attr) {
        String infot = "";
        attr = attr.toUpperCase();
        int i = attr.indexOf("INFOTYPE=");
        int j = attr.indexOf("\"", i + 1);
        int k = attr.indexOf("\"", j + 1);

        infot = attr.substring(j + 1, k);

        return infot;
    }

    private String getSchemeAttribute(String attr) {
        String scheme = "";
        attr = attr.toUpperCase();
        int i = attr.indexOf("SCHEME=");
        int j = attr.indexOf("\"", i + 1);
        int k = attr.indexOf("\"", j + 1);

        scheme = attr.substring(j + 1, k);

        return scheme;
    }

    private String getIdAttribute(String attr) {
        String scheme = "";
        attr = attr.toUpperCase();
        int i = attr.indexOf("ID=");
        int j = attr.indexOf("\"", i + 1);
        int k = attr.indexOf("\"", j + 1);

        scheme = attr.substring(j + 1, k);

        return scheme;
    }

    private String getLangAttribute(String attr) {
        String lang = "";
        attr = attr.toUpperCase();
        int i = attr.indexOf("XML:LANG=");
        int j = attr.indexOf("\"", i + 1);
        int k = attr.indexOf("\"", j + 1);

        lang = attr.substring(j + 1, k);

        return lang;
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

    public ArrayList<iMarineResult> getFigisResults() {
        return figisResults;
    }

    public void setFigisResults(ArrayList<iMarineResult> figisResults) {
        this.figisResults = figisResults;
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
    
}
