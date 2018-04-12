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

import gr.forth.ics.isl.textentitymining.Category;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class AllResults implements Serializable {
    
    private String query;
    private String statistics;
    private String top_results;
    private ArrayList<Category> entities;

    public AllResults(String query, String statistics, String top_results, ArrayList<Category> entities) {
        this.query = query;
        this.statistics = statistics;
        this.top_results = top_results;
        this.entities = entities;
    }

    public ArrayList<Category> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Category> entities) {
        this.entities = entities;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getStatistics() {
        return statistics;
    }

    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }

    public String getTop_results() {
        return top_results;
    }

    public void setTop_results(String top_results) {
        this.top_results = top_results;
    }
    
    
    
}
