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

import gr.forth.ics.isl.xsearch.IOSLog;
import gr.forth.ics.isl.xsearch.SearchResult;
import gr.forth.ics.isl.xsearch.util.EditDistance;
import gr.forth.ics.isl.textentitymining.Category;
import gr.forth.ics.isl.textentitymining.Entity;
import gr.forth.ics.isl.textentitymining.gate.GateEntityMiner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class Mining {

    private ArrayList<SearchResult> wseResults;
    private ArrayList<Category> entities;
    private String query;
    private HashSet<String> categoriesInQuery;
    private ArrayList<Category> entitiesInQuery;
    private String statistics;
    private HashSet<String> acceptedCategories;
    private HashMap<String, String> endpoints;
    private HashMap<String, String> templateQueries;

    public Mining(ArrayList<SearchResult> wseResults, String query, HashSet<String> acceptedCategories, HashMap<String, String> endpoints, HashMap<String, String> templateQueries) {

        this.wseResults = wseResults;
        this.query = query;
        this.acceptedCategories = acceptedCategories;
        this.endpoints = endpoints;
        this.templateQueries = templateQueries;

        // SET CONTENTS TO MINE //
        ArrayList<String> contentsToMine = new ArrayList<String>();
        for (SearchResult wse_res : wseResults) {
            String content = wse_res.getContent();
            contentsToMine.add(content);
        }


        // FIND ENTITIES //
        GateEntityMiner miner = new GateEntityMiner();
        miner.setAcceptedCategories(acceptedCategories);
        miner.setCollectionToMine(contentsToMine);
        miner.findEntities();
        entities = miner.getEntities();


    }

    public void mineQuery() {
        // FIND THE CATEGORIES OF THE QUERY
        findQueryCategories();
        for (String category : categoriesInQuery) {
            //System.out.print(category + " | ");
            for (int k = 0; k < entities.size(); k++) {
                if (entities.get(k).getName().trim().toLowerCase().equals(category.toLowerCase())) {
                    entities.get(k).increaseRank();
                }
            }
        }

        // MINE QUERY AND FIND ITS ENTITIES
        findQueryEntities();
        for (int k = 0; k < entities.size(); k++) {
            String cat = entities.get(k).getName();

            for (int l = 0; l < entitiesInQuery.size(); l++) {
                String q_cat = entitiesInQuery.get(l).getName();

                if (cat.equals(q_cat)) {
                    //System.out.print("=> Query mining result: "+q_cat+"-> ");
                    entities.get(k).increaseRank();

                    for (int q = 0; q < entities.get(k).getEntities().size(); q++) {
                        String name = entities.get(k).getEntities().get(q).getName();

                        for (int r = 0; r < entitiesInQuery.get(l).getEntities().size(); r++) {
                            String qname = entitiesInQuery.get(l).getEntities().get(r).getName();

                            if (name.toLowerCase().contains(qname.toLowerCase())) {
                                //System.out.print(name+" | ");
                                entities.get(k).getEntities().get(q).increaseRank(50);
                            }
                            if (name.toLowerCase().equals(qname.toLowerCase())) {
                                //System.out.print(qname+" ");
                                entities.get(k).getEntities().get(q).increaseRank(50);
                            }
                        }
                    }
                }
            }
        }
    }

    private void findQueryEntities() {

        try {
            GateEntityMiner queryMiner = new GateEntityMiner();
            queryMiner.setAcceptedCategories(acceptedCategories);
            queryMiner.setTextToMine(query);
            queryMiner.findEntities();
            entitiesInQuery = queryMiner.getEntities();
            System.out.println("# Entities in query: "+entitiesInQuery.size());
            
        } catch (Exception ex) {
            IOSLog.writeErrorToLog(ex, "Mining");
            System.out.println("*** PROBLEM ADDING DOCUMENTS TO CORPUS:");
            //Logger.getLogger(Bean_Search.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void findQueryCategories() {
        categoriesInQuery = new HashSet<String>();

        String q = this.query;
        while (q.contains("  ")) {
            q = q.replace("  ", " ");
        }
        q = q.trim().toLowerCase();

        String[] words = q.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            for (String category : acceptedCategories) {
                int edit_dist = EditDistance.getLevenshteinDistance(word, category);

                if (category.length() <= 4) {
                    if (edit_dist <= 1) {
                        //word is maybe a category
                        categoriesInQuery.add(category);
                    }
                } else {
                    if (edit_dist <= 2) {
                        //word is maybe a category
                        categoriesInQuery.add(category);
                    }
                }
            }
        }
        
        System.out.println("# Categories in query: "+categoriesInQuery);
    }
    
    public void giveRankToElements(int max_num_of_results_from_wse) {

        for (int i = 0; i < entities.size(); i++) {

            Category category = entities.get(i);

            for (int j = 0; j < category.getEntities().size(); j++) {

                Entity entity = category.getEntities().get(j);

                for (int k = 0; k < entity.getDocIds().size(); k++) {

                    int doc_id = entity.getDocIds().get(k);
                    SearchResult result = wseResults.get(doc_id);
                    int doc_rank = result.getRank();
                    entities.get(i).getEntities().get(j).increaseRank(max_num_of_results_from_wse - doc_rank);
                }

            }
        }
    }
    
    public void createEntitiesHTMLFormat() {

        int total_entities = 0;
        System.out.println("=> Number of categories: " + entities.size());
        statistics = "=> Number of categories: " + entities.size() + ".\n";
        for (int k = 0; k < entities.size(); k++) {

            String one_entity = "";
            String category = entities.get(k).getName();

            ArrayList<Entity> els = entities.get(k).getEntities();
            int num_of_elements = els.size();

            total_entities += num_of_elements;
            Collections.sort(entities.get(k).getEntities());

            HashSet<String> distinct_docs = new HashSet<String>();

            if (category.equals("Address")) {
                category = "Mail / URL / Tel";
            }

            one_entity += "<div class=\"one_entity\"><span class=\"em_category_name\">" + category.replace("_", " ") + "</span>";
            if (num_of_elements == 1) {
                one_entity += "<span class=\"em_num_of_entities\"> (" + num_of_elements + " entity)</span>";
            } else {
                one_entity += "<span class=\"em_num_of_entities\"> (" + num_of_elements + " entities)</span>";
            }
            one_entity += "<br />";

            int num = 0;
            for (int i = 0; i < num_of_elements; i++) {

                num++;

                Entity entity = els.get(i);
                int size = entity.getDocIds().size();

                String docIds = "";
                //String its_results = "<font class='em_result_show'>Results of entity:</font><font class='em_category_show'> <b>" + category + " &rarr; " + element.getElementName() + "</b></font>";
                //its_results += "&nbsp;&nbsp;<a href='javascript:getAllResults()'><font class='reset'>reset</font></a><br />&nbsp;<br />";
                for (int j = 0; j < size; j++) {

                    int id = entity.getDocIds().get(j);

                    SearchResult res = wseResults.get(id);
                    String url = res.getUrl();
                    distinct_docs.add(url);

                    docIds += id;
                    if (j != (size - 1)) {
                        docIds += ",";
                    }

                }


                String element_name = entity.getName();
                String element_name_cut = element_name;
                String element_name_pass = element_name.replace("\"", "&quot;").replace("'", "&quot;").replace("%", "^^^^^");

                String element_id = category.toLowerCase().trim() + "_" + num;
                String element_img_id = category.toLowerCase().trim().replace(" ", "_").replace(".", "") + "_img_" + num;

                if (element_name.length() > 24) {
                    element_name_cut = element_name.substring(0, 23) + "...";
                }

                String id_el_name = k + "_" + i;
                String id_href_name = "img_" + id_el_name;
                String link_href_name = "a_" + id_el_name;

                if (num > 10) {
                    String id_name = category + "_hidden_entities";
                    String show_name = category + "_show_all";
                    if (num == 11) {
                        one_entity += "<div id=\"" + id_name + "\" style=\"display:none\">";
                    }
                    one_entity += "<a id=\"" + id_href_name + "\" style=\"display:none\" href=\"javascript:unloadEntityResults('" + category + "', '" + element_name_pass + "', '" + docIds + "', '" + id_el_name + "')\"><img border=\"0\" src=\"files/graphics/remove.gif\"/></a>&nbsp;";
                    one_entity += "<a id=\"" + link_href_name + "\" title=\"" + element_name + "\" href=\"javascript:loadEntityResults('" + category + "', '" + element_name_pass + "', '" + docIds + "', '" + id_el_name + "')\">";
                    one_entity += "<font id=\"" + id_el_name + "\" class=\"em_element_name\">" + element_name_cut;
                    one_entity += " (" + size + ")";
                    one_entity += "</font></a>";
                    if (templateQueries.containsKey(category)) {
                        one_entity += "&nbsp;<a id=\"" + element_img_id + "\" href=\"javascript:inspectElement('" + category + "', '" + element_name_pass + "', '" + element_img_id + "')\"><img border=\"0\" title=\"Click to retrieve semantic information\" src=\"files/graphics/lod.jpg\"/></a>";
                    }
                    one_entity += "<br />";
                    if (num == num_of_elements) {
                        one_entity += "</div>";
                        one_entity += "<div id=\"" + show_name + "\" align=\"right\" class=\"em_show_name \"><a href=\"javascript:showAll('" + id_name + "', '" + show_name + "')\" class=\"em_show_name_a\">show all</a></div>";
                    }
                } else {
                    one_entity += "<a id=\"" + id_href_name + "\" style=\"display:none\" href=\"javascript:unloadEntityResults('" + category + "', '" + element_name_pass + "', '" + docIds + "', '" + id_el_name + "')\"><img border=\"0\" src=\"files/graphics/remove.gif\"/></a>&nbsp;";
                    one_entity += "<a id=\"" + link_href_name + "\" title=\"" + element_name + "\" href=\"javascript:loadEntityResults('" + category + "', '" + element_name_pass + "', '" + docIds + "', '" + id_el_name + "')\">";
                    one_entity += "<font id=\"" + id_el_name + "\" class=\"em_element_name\">" + element_name_cut;
                    one_entity += " (" + size + ")";
                    one_entity += "</font></a>";
                    if (templateQueries.containsKey(category)) {
                        one_entity += "&nbsp;<a id=\"" + element_img_id + "\" href=\"javascript:inspectElement('" + category + "', '" + element_name_pass + "', '" + element_img_id + "')\"><img border=\"0\" title=\"Click to retrieve semantic information\" src=\"files/graphics/lod.jpg\"/></a>";
                    }
                    one_entity += "<br />";
                }
            }
            one_entity += "</div>";

            one_entity = one_entity.replace("'", "&quot;").replace("\n", " ").replace("\r", " ").replace("\t", " ");
            entities.get(k).setNum_of_different_docs(distinct_docs.size());
            entities.get(k).setCategory_representation(one_entity);

        }

        System.out.println("=> Number of entities in all categories: " + total_entities);
        statistics += "=> Number of entities in all categories: " + total_entities + ".\n";
    }

    public HashSet<String> getCategoriesInQuery() {
        return categoriesInQuery;
    }

    public void setCategoriesInQuery(HashSet<String> categoriesInQuery) {
        this.categoriesInQuery = categoriesInQuery;
    }

    public ArrayList<Category> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Category> entities) {
        this.entities = entities;
    }

    public ArrayList<Category> getEntitiesInQuery() {
        return entitiesInQuery;
    }

    public void setEntitiesInQuery(ArrayList<Category> entitiesInQuery) {
        this.entitiesInQuery = entitiesInQuery;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<SearchResult> getWseResults() {
        return wseResults;
    }

    public void setWseResults(ArrayList<SearchResult> wseResults) {
        this.wseResults = wseResults;
    }

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }
    
    
}
