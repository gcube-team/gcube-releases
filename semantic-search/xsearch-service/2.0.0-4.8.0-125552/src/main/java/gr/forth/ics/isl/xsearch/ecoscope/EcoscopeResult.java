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

import java.util.HashMap;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class EcoscopeResult {
    
    private String uri;
    private HashMap<String,String> prefLabels; // language -> prefLabel
    private HashMap<String,String> descriptions; // language -> description
    private HashMap<String,String> comments; // language -> comment
    private HashMap<String,String> titles; // language -> title

    public EcoscopeResult() {
        this.uri = "";
        this.prefLabels = new HashMap<String,String>();
        this.descriptions = new HashMap<String,String>();
        this.comments = new HashMap<String,String>();
        this.titles = new HashMap<String,String>();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HashMap<String, String> getPrefLabels() {
        return prefLabels;
    }

    public void setPrefLabels(HashMap<String, String> prefLabels) {
        this.prefLabels = prefLabels;
    }

    public HashMap<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(HashMap<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, String> comments) {
        this.comments = comments;
    }

    public HashMap<String, String> getTitles() {
        return titles;
    }

    public void setTitles(HashMap<String, String> titles) {
        this.titles = titles;
    }
    
    
    
}
