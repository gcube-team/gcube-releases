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
 * Is Class that describes a MetadataGroup.
 * Each MetadataGroup has a name and a list of metadatas that belongs to it.
 * 
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class MetadataGroup implements Serializable, Comparable<MetadataGroup>{

    /** MetadataGroup's name.*/
    private String metadataGroupName = new String();
    
    /** A list of metadata that belongs to this MetadataGroup.*/
    private ArrayList<Metadata> metadatas = new ArrayList<>();
    
    /** Number of metadata's distinct docs */
    private int num_of_different_docs = 0;
    
    /** MetadataGroup's rank*/
    private double rank;
    
    /**Constructor. makes an empty instance of MetadataGroup*/
    public MetadataGroup() {
        this.rank = 0.0;
    }

    /**
     * Constructor: Creates a new instance of MetadataGroup with name that takes as
     * parameter.
     * @param name MetadataGroup's name.
     */
    public MetadataGroup(String name) {
        this.metadataGroupName = name;
        this.rank = 0.0;
    }

    /**
     * Constructor. Creates a new instance of MetadataGroup and
     * initialize Metadata type and Metadata's element list.
     *
     * @param name MetadataGroup's name
     * @param metadatas A list of metadatas that belong to this MetadataGroup.
     */
    public MetadataGroup(String name, ArrayList<Metadata> metadatas) {
        this.metadataGroupName = name;
        this.metadatas = metadatas;
        this.rank = 0.0;
    }

    /**
     * Removes Metadata from MetadataGroup's list of metadatas
     * @param Metadata the Metadata we want to remove from the metadatas list.
     */
    public void removeMetadata(Metadata Metadata) {
        metadatas.remove(Metadata);
    }
    
    /**
     * Adds an Metadata to Metadata's arrayList.
     * @param Metadata is the Metadata we want to add in the list of metadatas.
     */
    public void addMetadata(Metadata Metadata) {
        metadatas.add(Metadata);
    }

    /**
     * Increases the metadataGroup rank by one.
     */
    public void increaseRank() {
        rank++;
    }

    /**
     * 
     * @return the list of Metadata that belongs to the metadataGroup.
     */
    public ArrayList<Metadata> getMetadatas() {
        return metadatas;
    }

    /**
     * Set's the metadatas that belongs to metadataGroup
     * @param metadatas the list of metadatas.
     */
    public void setMetadatas(ArrayList<Metadata> metadatas) {
        this.metadatas = metadatas;
    }

    /**
     * 
     * @return metadataGroup name
     */
    public String getMetadataGroupName() {
        return metadataGroupName;
    }

    /**
     * Set's metadataGroup name.
     * @param name  metadaGroupName
     */
    public void setMetadataGroupName(String name) {
        this.metadataGroupName = name;
    }

    /**
     * @return number of different document ids in which identified MetadataGroup.
     */
    public int getNum_of_different_docs() {
        return num_of_different_docs;
    }

    /**
     * Set's number of different document ids in which identified MetadataGroup.
     * @param num_of_different_docs number of different document ids in which identified MetadataGroup.
     */
    public void setNum_of_different_docs(int num_of_different_docs) {
        this.num_of_different_docs = num_of_different_docs;
    }

    /**
     * 
     * @return metadataGroup rank.
     */
    public double getRank() {
        return rank;
    }

    /**
     * Set's metadataGroup rank.
     * @param rank metadataGroup rank
     */
    public void setRank(double rank) {
        this.rank = rank;
    }
    
   /**
     * Compares metadataGroups based on their rank.
     * If the rank is equal compares them based on documents list size 
     * of each metadataGroup.
     */
    @Override    
    public int compareTo(MetadataGroup n) {
        if (rank > n.getRank()) {
            return -1;
        } else if (rank < n.getRank()) {
            return 1;
        } else {
            if (metadatas.size() > n.getMetadatas().size())
                return -1;
            else if (metadatas.size() < n.getMetadatas().size())
                return 1;
            else {
                if (num_of_different_docs > n.getNum_of_different_docs())
                    return -1;
                else if (num_of_different_docs < n.getNum_of_different_docs())
                    return 1;
                else {
                    return (metadataGroupName.compareTo(n.getMetadataGroupName()));
                }  
            }
        }
    }

}