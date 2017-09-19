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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class FAO {

    public static String endpoint = "http://www.fao.org/figis/flod/endpoint/sparql";
    public static String parameters = "&default-graph-uri=&stylesheet=/xml-to-html.xsl&output=csv&force-accept=text/plain";
    public static HashSet<String> countries;
    public static HashSet<String> species;
    public static HashSet<String> waterAreas;
    public static HashSet<String> regionalFisheriesBodies;
    public static String allCountriesQuery = ""
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX cls: <http://www.ontologydesignpatterns.org/cp/owl/classification.owl#>"
            + "select distinct ?label WHERE {"
            + "?uri a <http://www.fao.org/figis/flod/onto/country.owl#CountryCode> ."
            + "?uri cls:classifies ?country ."
            + "?country rdfs:label ?label FILTER(lang(?label)='en') ."
            + "}";
    public static String allSpeciesQuery = ""
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX sys: <http://www.fao.org/figis/flod/onto/codedentitycollection.owl#>"
            + "PREFIX cls: <http://www.ontologydesignpatterns.org/cp/owl/classification.owl#>"
            + "SELECT DISTINCT ?name WHERE {"
            + "?code cls:classifies ?e . "
            + "?code sys:system <http://www.fao.org/figis/flod/entities/codificationsystem/alpha3> ."
            + "?e rdfs:label ?name FILTER(lang(?name)='en')"
            + "}";
    public static String allWaterAreasQuery = ""
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX sys: <http://www.fao.org/figis/flod/onto/codedentitycollection.owl#>"
            + "PREFIX cls: <http://www.ontologydesignpatterns.org/cp/owl/classification.owl#>"
            + "SELECT DISTINCT ?name WHERE {"
            + "?code cls:classifies ?e . "
            + "?code a <http://www.fao.org/figis/flod/onto/waterarea.owl#FAOareaCode> ."
            + "?e rdfs:label ?name FILTER(lang(?name)='en')"
            + "}";
    public static String allRegionalFisheriesBodiesQuery = ""
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX sys: <http://www.fao.org/figis/flod/onto/codedentitycollection.owl#>"
            + "PREFIX cls: <http://www.ontologydesignpatterns.org/cp/owl/classification.owl#>"
            + "SELECT DISTINCT ?name WHERE {"
            + "?code cls:classifies ?e . "
            + "?code a <http://www.fao.org/figis/flod/onto/rfb.owl#RFBCode> ."
            + "?e rdfs:label ?name FILTER(lang(?name)='en')"
            + "}";

    public static void createLists() throws UnsupportedEncodingException {

        countries = new HashSet<String>();
        species = new HashSet<String>();
        waterAreas = new HashSet<String>();
        regionalFisheriesBodies = new HashSet<String>();

        // RUN QUERY FOR ALL COUNTRIES //
        String encodedQuery = URLEncoder.encode(allCountriesQuery, "utf8");
        String link = endpoint + "?query=" + encodedQuery + "&default-graph-uri=&stylesheet=/xml-to-html.xsl&output=csv&force-accept=text/plain";
        String allCountriesString = runQuery(link);

        // RUN QUERY FOR ALL SPECIES //
        encodedQuery = URLEncoder.encode(allSpeciesQuery, "utf8");
        link = endpoint + "?query=" + encodedQuery + "&default-graph-uri=&stylesheet=/xml-to-html.xsl&output=csv&force-accept=text/plain";
        String allSpeciesString = runQuery(link);

        // RUN QUERY FOR ALL WATER AREAS //
        encodedQuery = URLEncoder.encode(allWaterAreasQuery, "utf8");
        link = endpoint + "?query=" + encodedQuery + "&default-graph-uri=&stylesheet=/xml-to-html.xsl&output=csv&force-accept=text/plain";
        String allWaterAreasString = runQuery(link);

        // RUN QUERY FOR ALL COUNTRIES //
        encodedQuery = URLEncoder.encode(allRegionalFisheriesBodiesQuery, "utf8");
        link = endpoint + "?query=" + encodedQuery + "&default-graph-uri=&stylesheet=/xml-to-html.xsl&output=csv&force-accept=text/plain";
        String allRegionalFisheriesBodiesQueryString = runQuery(link);

        // CREATE SET OF COUNTRIES //
        String[] countriesArray = allCountriesString.split("\n");
        for (String country : countriesArray) {
            if (country.equals("label")) {
                continue;
            }
            if (country.startsWith("\"")) {
                int i1 = country.indexOf(",");
                String part1 = country.substring(1, i1);
                String part2 = country.substring(i1 + 1, country.length() - 1);
                country = part2 + " " + part1;
                country = country.trim();
                country = country.replace("fed.states of ", "");
                country = country.replace("occupied tr. ", "");
                country = country.replace(" sar china", "");
                country = country.replace("dem. rep. of the ", "");
                country = country.replace("republic of ", "");
                country = country.replace("boliv rep of ", "");
                country = country.replace("fmr yug rp of ", "");
                country = country.replace("united rep. of ", "");
                country = country.replace("dem. people's rep ", "");
            } else {
                country = country.replace("is.", "").trim();
                country = country.replace("viet nam", "vietnam").trim();
                country = country.replace(" pdr", "").trim();
                country = country.replace("is.", "").trim();
                country = country.replace(" dem. rep.", "").trim();
                country = country.replace("(islamic rep. of)", "").trim();
                country = country.replace("united states of ", "").trim();
            }

            countries.add(country);
        }

        // CREATE SET OF SPECIES //
        String[] speciesArray = allSpeciesString.split("\n");
        for (String specie : speciesArray) {
            if (specie.equals("name")) {
                continue;
            }
            if (specie.startsWith("\"")) {
                int i1 = specie.indexOf(",");
                String part1 = specie.substring(1, i1);
                String part2 = specie.substring(i1 + 1, specie.length() - 1);
                specie = part2 + " " + part1;
                specie = specie.trim();
            }

            if (specie.contains("(")) {

                int i2 = specie.indexOf("(");
                int i3 = specie.indexOf(")");
                String toremove = specie.substring(i2, i3 + 1);
                specie = specie.replace(toremove, " ");
                while (specie.contains("  ")) {
                    specie = specie.replace("  ", " ").trim();
                }
            }

            species.add(specie);
        }

        // CREATE SET OF WATER AREAS //
        String[] areasArray = allWaterAreasString.split("\n");
        waterAreas.add("pacific");
        waterAreas.add("atlantic");
        waterAreas.add("indian ocean");
        waterAreas.add("antarctic");
        waterAreas.add("mediterranean");
        waterAreas.add("black sea");
        waterAreas.add("inland waters");
        for (String area : areasArray) {
            if (area.equals("name")) {
                continue;
            }
            if (area.startsWith("\"")) {
                int i1 = area.indexOf(",");
                String part1 = area.substring(1, i1);
                String part2 = area.substring(i1 + 1, area.length() - 1);
                area = part2 + " " + part1;
                area = area.trim();
            }
            waterAreas.add(area);
        }

        // CREATE SET OF REGIONAL FISHERIES BODIES //
        String[] bodiesArray = allRegionalFisheriesBodiesQueryString.split("\n");
        for (String body : bodiesArray) {
            if (body.equals("name")) {
                continue;
            }

            //System.out.println("=>" + body);
            if (body.startsWith("\"")) {
                int i1 = body.indexOf(",");
                String part1 = body.substring(1, i1);
                String part2 = body.substring(i1 + 1, body.length() - 1);
                body = part2 + " " + part1;
                body = body.trim();
            }

            if (body.contains("(")) {

                int i2 = body.indexOf("(");
                int i3 = body.indexOf(")");
                String toremove = body.substring(i2, i3 + 1);
                body = body.replace(toremove, " ");
                toremove = toremove.replace("(", "").replace(")", "");
                regionalFisheriesBodies.add(toremove);

                while (body.contains("  ")) {
                    body = body.replace("  ", " ").trim();
                }
            }

            //System.out.println(body);
            regionalFisheriesBodies.add(body);
        }


    }

    public static String runQuery(String queryLink) {

        String result = "";
        try {

            System.out.println("# Query Link: " + queryLink);

            URL url = new URL(queryLink);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");

            String xml_content = "application/sparql-results+xml";
            //String json_content = "application/sparql-results+json";
            con.setRequestProperty("ACCEPT", xml_content);
            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf8");
            BufferedReader in = new BufferedReader(isr);

            String input;

            while ((input = in.readLine()) != null) {
                result = result + input + "\n";
            }

            in.close();
            isr.close();
            is.close();

            System.out.println("# SPARQL query was executed successfully!");

        } catch (IOException ex) {
            IOSLog.writeErrorToLog(ex, "FAO");
            System.out.println("# PROBLEM EXECUTING THE QUERY: " + queryLink);
            System.out.println("ERROR:" + ex.getMessage());
            Logger.getLogger(FAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }
}
