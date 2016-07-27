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
package gr.forth.ics.isl.gwt.xsearch.server.metadatagroupings;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Its a class that each metadata that found in an object. For each metadata it
 * holds metadata's name and an Arraylist with documents ids that contains the metadata.
 * 
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class Metadata implements Serializable, Comparable<Metadata> {

    /**Metadata's name.*/
    private String metadatName;
    /**A list of documents that include this element.*/
    private ArrayList<Integer> docIds = new ArrayList<>();
    private double rank;

    /**Constructor: Creates a new element with name and docIdsList that takes
     * as parameters.
     * @param name is the name of the Metadata
     * @param docIds is a list with the IDs of all documents that contain that Metadata */
    public Metadata(String name, ArrayList<Integer> docIds){
        this.metadatName = name;
        this.docIds = docIds;
        this.rank = 0.0;
    }

    /**
     * Constructor. Makes an instance of Metadata with name initialized for
     * parameter.
     * @param name is the name of the Metadata
     */
    public Metadata(String name) {
        this.metadatName = name;
        this.rank = 0.0;
    }

    /**Adds a DocId to metadatas list of documents ids.
     *@param docId adds a docId to elements documents id list*/
    public void addDocId(Integer docId) {
        docIds.add(docId);
    }

    /**
     * Prints element's name and documents ids that contains element.
     *@param element a instance of metadata that we want to see name and docId list*/
    public static void printElementsContents(Metadata element) {
        System.out.print(element.getMetadataName());
        System.out.print(" [");
        for (int i = 0; i < element.getDocIds().size(); i++) {
            System.out.print(element.getDocIds().get(i) + " ");
        }
        System.out.println("]");
    }

    /**
     * 
     * @return the list with document ids that contain the metadata
     */
    public ArrayList<Integer> getDocIds() {
        return docIds;
    }

    /**
     * Set's the list with the document ids that contain the metadata.
     * @param docIds the list with the document ids
     */
    public void setDocIds(ArrayList<Integer> docIds) {
        this.docIds = docIds;
    }

    /**
     * 
     * @return metadata name
     */
    public String getMetadataName() {
        return metadatName;
    }

    /**
     * Set's metadata name.
     * @param name metadata name
     */
    public void setMetadataName(String name) {
        this.metadatName = name;
    }

    /**
     * 
     * @return rank of metadata
     */
    public double getRank() {
        return rank;
    }

    /**
     * Set's rank of metadata
     * @param rank rank of metadata
     */
    public void setRank(double rank) {
        this.rank = rank;
    }

    /**
     * Increases metadata rank by one
     */
    public void increaseRank() {
        rank++;
    }

    /**
     * Increases the metadata rank by a value that takes as paremeter.
     * @param num the number want to add at the metadata rank
     */
    public void increaseRank(int num) {
        rank += num;
    }

    /**
     * Compares metadatas based on their rank.
     * If the rank is equal compares them based on documents list size 
     * of each metadata.
     */
    @Override
    public int compareTo(Metadata n) {
        if (rank > n.getRank()) {
            return -1;
        } else if (rank < n.getRank()) {
            return 1;
        } else {
            if (docIds.size() > n.getDocIds().size()) {
                return -1;
            } else if (docIds.size() < n.getDocIds().size()) {
                return 1;
            } else {
                return (metadatName.compareTo(n.getMetadataName()));
            }
        }
    }
}