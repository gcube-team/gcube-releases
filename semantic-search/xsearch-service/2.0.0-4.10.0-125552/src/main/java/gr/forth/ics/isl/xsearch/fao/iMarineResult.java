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

import gr.forth.ics.isl.xsearch.SearchResult;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */

public class iMarineResult extends SearchResult {
    
    private String id;
    private HashMap<String,String> titles; // language -> title
    private String titleSupplement;
    private HashMap<String,String> descriptions; // language -> description
    private HashSet<String> languages;
    private String type;
    private String creator;
    private HashMap<String,String> dates; // scheme -> date
    private String pages;
    private HashMap<String,String> identifiers;
    private HashMap<String,String> isPartOf; // language -> part
    private HashMap<Integer,String> hasVersion; // infotype -> version
    private String department;
    private String devision;
    private HashMap<String,String> subjectThesaurus; // scheme -> subject
    private HashMap<String,String> subjectClassification; // scheme -> subject
    private String notes;

    public iMarineResult() {
        id = "";
        titles = new HashMap<String,String>();
        titleSupplement = "";
        descriptions = new HashMap<String,String>();
        languages = new HashSet<String>();
        type = "";
        creator = "";
        dates = new HashMap<String,String>();
        pages = "-";
        identifiers = new HashMap<String,String>();
        isPartOf = new HashMap<String,String>();
        hasVersion = new HashMap<Integer,String>();
        department = "";
        devision = "";
        subjectThesaurus = new HashMap<String,String>();
        subjectClassification = new HashMap<String,String>();
        notes = "";
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public HashMap<String, String> getDates() {
        return dates;
    }

    public void setDates(HashMap<String, String> dates) {
        this.dates = dates;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public HashMap<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(HashMap<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    public String getDevision() {
        return devision;
    }

    public void setDevision(String devision) {
        this.devision = devision;
    }

    public HashMap<Integer, String> getHasVersion() {
        return hasVersion;
    }

    public void setHasVersion(HashMap<Integer, String> hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(HashMap<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public HashMap<String, String> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(HashMap<String, String> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public HashSet<String> getLanguages() {
        return languages;
    }

    public void setLanguages(HashSet<String> languages) {
        this.languages = languages;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public HashMap<String, String> getSubjectClassification() {
        return subjectClassification;
    }

    public void setSubjectClassification(HashMap<String, String> subjectClassification) {
        this.subjectClassification = subjectClassification;
    }

    public HashMap<String, String> getSubjectThesaurus() {
        return subjectThesaurus;
    }

    public void setSubjectThesaurus(HashMap<String, String> subjectThesaurus) {
        this.subjectThesaurus = subjectThesaurus;
    }

    public String getTitleSupplement() {
        return titleSupplement;
    }

    public void setTitleSupplement(String titleSupplement) {
        this.titleSupplement = titleSupplement;
    }

    public HashMap<String, String> getTitles() {
        return titles;
    }

    public void setTitles(HashMap<String, String> titles) {
        this.titles = titles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String print = "-------------\n";
        print += "# id=" + id + "\n";
        print += "# titles=" + titles + "\n";
        print += "# titleSupplement=" + titleSupplement + "\n";
        print += "# descriptions=" + descriptions + "\n";
        print += "# languages=" + languages + "\n";
        print += "# type=" + type + "\n";
        print += "# creator=" + creator + "\n";
        print += "# dates=" + dates + "\n";
        print += "# pages=" + pages + "\n";
        print += "# identifiers=" + identifiers + "\n";
        print += "# isPartOf=" + isPartOf + "\n";
        print += "# hasVersion=" + hasVersion + "\n";
        print += "# department=" + department + "\n";
        print += "# devision=" + devision + "\n";
        print += "# subjectThesaurus=" + subjectThesaurus + "\n";
        print += "# subjectClassification=" + subjectClassification + "\n";
        print += "# notes=" + notes + "\n";
        print += "-------------\n";
        return print;
    }
   
    
    
    
}
