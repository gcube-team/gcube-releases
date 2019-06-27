/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.xsearch.util;

import gr.forth.ics.isl.xsearch.Triple;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author fafalios
 */
public class Util {

    public static String readSPARQLQuery(String queryPath) {
        String templ = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(queryPath), "UTF8"));
            String line;
            while ((line = in.readLine()) != null) {
                templ += (line + " ");
            }
            in.close();
        } catch (Exception ex) {
            System.out.println("ERROR READING TEMPLATE FILE '" + queryPath + "'\n: " + ex.getMessage());
        }

        return templ;
    }

    public static ArrayList<Triple> readSPARQLQueryResponseTriples(String response) {

        ArrayList<Triple> triples = new ArrayList<Triple>();
        ArrayList<String> resultsVariables;

        HTMLTag tagger = new HTMLTag(response);
        String headStr = tagger.getFirstTagData("head");
        String resultsStr = tagger.getFirstTagData("results");

        // FIND RESULTS' VARIABLES //
        resultsVariables = new ArrayList<String>();
        HTMLTag headTagger = new HTMLTag(headStr);
        int i = headTagger.getFirstTagIndex("variable");
        while (i != -1) {
            String variableCont = headTagger.getFirstTagContent("variable", i);
            String variable = HTMLTag.getContentAttribute("name", variableCont);
            resultsVariables.add(variable);
            i = headTagger.getFirstTagIndex("variable", i + 1);
        }


        // GET RESULTS //
        HTMLTag resultsTagger = new HTMLTag(resultsStr);
        i = resultsTagger.getFirstTagIndex("result");
        while (i != -1) {
            String resultData = resultsTagger.getFirstTagData("result", i);
            HTMLTag resTagger = new HTMLTag(resultData);

            String s = "";
            String p = "";
            String o = "";
            for (String variable : resultsVariables) {
                String bindingData = resTagger.getFirstTagDataContains("binding", "\"" + variable + "\"");
                if (bindingData == null) {
                    bindingData = resTagger.getFirstTagDataContains("binding", "'" + variable + "'");
                }
                if (bindingData != null) {
                    bindingData = HTMLTag.removeTags(bindingData).trim();
                }
                if (variable.equals("s")) {
                    s = bindingData;
                }
                if (variable.equals("p")) {
                    p = bindingData;
                }
                if (variable.equals("o")) {
                    o = bindingData;
                }
            }
            
            Triple triple = new Triple(s, p, o);
            triples.add(triple);
            
            i = resultsTagger.getFirstTagIndex("result", i + 1);
        }

        return triples;
    }

    public static String removeLinesAndMultipleSpaces(String text) {

        String newtext = text;
        while (newtext.contains("\n")) {
            newtext = newtext.replace("\n", " ");
        }

        while (newtext.contains("\r")) {
            newtext = newtext.replace("\r", " ");
        }

        while (newtext.contains("\t")) {
            newtext = newtext.replace("\t", " ");
        }

        while (newtext.contains("  ")) {
            newtext = newtext.replace("  ", " ");
        }

        newtext = newtext.trim();

        return newtext;

    }
}
