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
package gr.forth.ics.isl.gwt.xsearch.client.facetedexploration;


import com.google.gwt.user.client.ui.Tree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A Class that is used to store the trees and the list of document Ids that
 * should be visible.
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class TreeStorage {

    //private List<String> listOfVisibleDocIds;
    private ArrayList<Tree> storageOfMiningTrees;
    private ArrayList<Tree> storageOfClusteringTrees;
    private ArrayList<Tree> storageOfMetadataTrees;
    private ArrayList<Set<String>> listOflistsOfVisibleDocIds;
    private FacetedSearchType facetedType;

    /**
     * Initializes the structures based on the given FacetedSearchType.
     * @param type the selected FacetedSearchType
     */
    public TreeStorage(FacetedSearchType type) {
        facetedType = type;

        storageOfMiningTrees = new ArrayList<Tree>();
        storageOfClusteringTrees = new ArrayList<Tree>();
        storageOfMetadataTrees = new ArrayList<Tree>();
        listOflistsOfVisibleDocIds = new ArrayList<Set<String>>();  
    }

    /**
     * @return an ArrayList that contains the clustering trees
     */
    public ArrayList<Tree> getStorageOfClusteringTrees() {
        return storageOfClusteringTrees;
    }

    /**
     * @return an ArrayList that contains the metadata trees
     */
    public ArrayList<Tree> getStorageOfMetadataTrees() {
        return storageOfMetadataTrees;
    }

    /**
     * @return an ArrayList that contains the mining trees
     */
    public ArrayList<Tree> getStorageOfMiningTrees() {
        return storageOfMiningTrees;
    }
    
    /**
     * Adds a mining tree to the ArrayList that stores them
     * @param miningTree the Tree that should be stored
     */
    public void addMiningTree(Tree miningTree){
         storageOfMiningTrees.add(miningTree);
    }
    
    /**
     * Adds a clustering tree to the ArrayList that stores them
     * @param clusteringTree the Tree that should be stored
     */
    public void addClusteringTree(Tree clusteringTree){
        storageOfClusteringTrees.add(clusteringTree);
    }
    
    /**
     * Adds a metadata tree to the ArrayList that stores them
     * @param metadataTree the Tree that should be stored
     */
    public void addMetadataTree( Tree metadataTree){
        storageOfMetadataTrees.add(metadataTree);
    }
    
    /**
     * Add the three different trees to the their storage
     * @param miningTree the mining Tree that should be stored
     * @param clusteringTree the clustering Tree that should be stored
     * @param metadataTree the metadata Tree that should be stored 
     */
    public void addTrees(Tree miningTree, Tree clusteringTree, Tree metadataTree) {
        storageOfMiningTrees.add(miningTree);
        storageOfClusteringTrees.add(clusteringTree);
        storageOfMetadataTrees.add(metadataTree);
    }

    /**
     * Add a set of document ids to the set of presentable document ids.
     * In more details, it takes a string that contains the document ids concated with comma.
     * Depending, on the FacetedSearchType updates accordingly the document lists.
     * @param docIds 
     */
    public void addDocIdsToListOfVisibleDocIds(String docIds) {

        String[] docIdsList = docIds.split(",");

        if (facetedType == FacetedSearchType.INTERSECTION) {

            Set<String> newList = new HashSet<String>();
            for (String docId : docIdsList) {
                if (!docId.isEmpty()) {
                    newList.add(docId);
                }
            }
            listOflistsOfVisibleDocIds.add(newList);

        } else if (facetedType == FacetedSearchType.UNION) {
           
            if(listOflistsOfVisibleDocIds.isEmpty()){
                listOflistsOfVisibleDocIds.add(new HashSet<String>());
            }
            
            for (String docId : docIdsList) {
                if (!docId.isEmpty()) {
                    listOflistsOfVisibleDocIds.get(0).add(docId);
                }
            }
        }
    }
    
    /**
     * @return the mining tree that is stored last in the Mining Tree.
     */
    public Tree getLastMiningTree(){
        int storageMiningTreeSize = storageOfMiningTrees.size();
        Tree t = storageOfMiningTrees.get(storageMiningTreeSize-1);
        if(storageMiningTreeSize==1){
            storageOfMiningTrees.remove(0);
        }
        
        return t;
    }
    
    /**
     * Removes and returns the last mining tree that is contained into miningStorage tree
     * @return the mining tree that is stored in the last position of the storage
     */
    public Tree removeMiningTree() {         
        return storageOfMiningTrees.remove(storageOfMiningTrees.size() - 1);
    }
    
     /**
     * Removes and returns the last clustering tree that is contained into clusteringStorage tree
     * @return the clustering tree that is stored in the last position of the storage
     */
    public Tree removeClusteringTree() {
        return storageOfClusteringTrees.remove(storageOfClusteringTrees.size() - 1);
    }
    
     /**
     * Removes and returns the last metadataGroupings Tree that is contained into MetadataGroupingsStorage tree
     * @return the metadataGroupings Tree that is stored in the last position of the storage
     */
    public Tree removeMetadataTree() {
        return storageOfMetadataTrees.remove(storageOfMetadataTrees.size() - 1);
    }

    /**
     * Remove the Trees that are stored at the last position of each storage structure.
     */
    @SuppressWarnings("element-type-mismatch")
    public void removeTrees() {
        storageOfMiningTrees.remove(storageOfMiningTrees.size() - 1);
        storageOfClusteringTrees.remove(storageOfClusteringTrees.size() - 1);
        storageOfMetadataTrees.remove(storageOfMetadataTrees.size() - 1);
    }

    /**
     * A function that takes as parameter a string with the document ids (concated with comma)
     * that should be removed from the list that contains the document ids that should be visible.
     * @param docIds the document ids that should be removed (concated with comma) 
     */
    public void removeDocIdsFromListOfVisibleDocIds(String docIds) {
        String[] docIdsList = docIds.split(",");

        if (facetedType == FacetedSearchType.INTERSECTION) {
            listOflistsOfVisibleDocIds.remove(listOflistsOfVisibleDocIds.size() - 1);

        } else if (facetedType == FacetedSearchType.UNION) {

            for (String docId : docIdsList) {
                listOflistsOfVisibleDocIds.get(0).remove(docId);
            }

        }
    }

    /**
     * @return the FacetedSearchType that is selected.
     */
    public FacetedSearchType getFacetedType() {
        return facetedType;
    }

    /**
     * Set's the selected FacetedSearchType.
     * @param facetedType the FacetedSearchType
     */
    public void setFacetedType(FacetedSearchType facetedType) {
        this.facetedType = facetedType;
    }

    /**
     * @return  the last list of visible document ids that were stored.
     */
    public Set<String> getLastListOfVisibleDocIds() {
        return listOflistsOfVisibleDocIds.get(listOflistsOfVisibleDocIds.size()-1);
    }

    /**
     * Set's the a new set that contains the visible document ids.
     * @param listOfVisibleDocIds the set of string with the visible document ids.
     */
    public void setListOfVisibleDocIds(Set<String> listOfVisibleDocIds) {
        this.listOflistsOfVisibleDocIds.add(0, listOfVisibleDocIds);
    }

    /**
     * @return the list of visible document ids based on the FacetedSearchType
     * that is selected at treeStorage initialization.
     */
    public String getListOfVisibleDocIdsToString() {
        String docList = "";

        if (facetedType == FacetedSearchType.INTERSECTION) {
            if(!listOflistsOfVisibleDocIds.isEmpty()){
                for (String docId : listOflistsOfVisibleDocIds.get(listOflistsOfVisibleDocIds.size()-1)) {
                    docList += docId + ",";
                }            
            }
        } else if (facetedType == FacetedSearchType.UNION) {
            for (String docId : listOflistsOfVisibleDocIds.get(0)) {
                docList += docId + ",";
            }
        }
        return docList;
    }
}
