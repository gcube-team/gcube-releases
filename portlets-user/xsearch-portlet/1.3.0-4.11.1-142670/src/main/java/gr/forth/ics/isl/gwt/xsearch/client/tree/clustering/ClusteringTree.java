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
package gr.forth.ics.isl.gwt.xsearch.client.tree.clustering;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gr.forth.ics.isl.gwt.xsearch.client.XSearch;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.TreeStorage;
import gr.forth.ics.isl.gwt.xsearch.client.parser.json.ClusteringJSONParser;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that contains the functions to create a tree that contains
 * the textual clustering results.
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class ClusteringTree {

    /**
     * Number of chars to show in entities
     */
    private static final int numOfCharsToShow = 12;
    /**
     * Number of Results per Page
     */
    private static int numOfResultsPerPage = 10;
    /**
     * Number of entities to show when open a Tree
     */
    private static int numOfClustersShow = 5;
    private static boolean showTreeCollapsed = true;
    private static Tree clusterTree = new Tree();
    private static ScrollPanel scrollPanel = new ScrollPanel();

    /**
     * Constructor that is used for initialization.
     * @param clusterTree the tree that contains the textual clusters
     * @param scrollPanel the main scroll panel that is used 
     * @param numOfResultsPerPage maximum number of results to show per page
     */
    public ClusteringTree(Tree clusterTree, ScrollPanel scrollPanel, int numOfResultsPerPage) {
        this.numOfResultsPerPage = numOfResultsPerPage;
        this.clusterTree = clusterTree;
        this.scrollPanel = scrollPanel;
    }

    /**
     * Create a JavaScript object from JSON string in order to access its
     * properties with java code
     *
     * @param json is the json string we want to publish.
     */
    public static native ClusteringJSONParser buildClustersFromJSON(String json) /*-{
     return eval("(" + json + ")");
     }-*/;

    /**
     * Creates categories tree from a json string which contains the clustering
     * results of semantic analysis.
     *
     * @param jsonText json string which contains the results of clustering.
     * @return the Tree that contains the textual clusters
     */
    public Tree createClusterTree(String jsonText) {

        //clusteringVertPanel.remove(1);

        ClusteringJSONParser cl = buildClustersFromJSON(jsonText);

        // Check if clusters found and create the root of Cluster tree
        TreeItem treeItem = new TreeItem();
        treeItem.addStyleName("aClusterItem");
        int numOfClusters = (int) cl.getNumOfClusters();

        
        if (!XSearch.mergeSemanticAnalysisResults) {
            clusterTree.clear();
        }

        // Iterate through new Clusters (JSON Objects)	  
        for (int clusterPos = 1; clusterPos < numOfClusters; clusterPos++) {
            String newClusterName = cl.getClusterName(clusterPos);


            boolean isNewCluster = true;
            for (int oldClusterPos = 0; clusterTree.getItemCount() != 0 && oldClusterPos < clusterTree.getItem(0).getChildCount(); oldClusterPos++) {
                String oldClusterName = clusterTree.getItem(0).getChild(oldClusterPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getTitle();
               
                if (oldClusterName.equalsIgnoreCase(newClusterName)) {
                    String oldDocIdsList = clusterTree.getItem(0).getChild(oldClusterPos).getHTML().split("'")[1];

                    // Passes Documents list to a string
                    int sizeOfNewDocList = (int) cl.getNumOfDocs(clusterPos);
                    String newListOfDocs = new String();
                    for (int docListPos = 0; docListPos < sizeOfNewDocList; docListPos++) {
                        // decrease by one because for clustering first documents id is one and not zero as for clustering
                        newListOfDocs += ((int) cl.getDocID(clusterPos, docListPos) - 1) + ",";
                    }
                    newListOfDocs += oldDocIdsList;
                    sizeOfNewDocList += oldDocIdsList.split(",").length;

                    // Create new Cluster 
                    HTML newEntity = updateCluster(oldClusterName, newListOfDocs, sizeOfNewDocList, clusterPos);
                    clusterTree.getItem(0).getChild(oldClusterPos).setWidget(newEntity);

                    // Denote that category is already contained
                    isNewCluster = false;

                    // If is found once then we could stop iteration
                    break;
                }
            }

            // If cluster is new then add it
            if (isNewCluster) {
                
                String listOfDocsIds = new String();
                int numOfDocs= (int) cl.getNumOfDocs(clusterPos);
                for (int docListPos = 0; docListPos < numOfDocs; docListPos++) {
                    // decrease by one because for clustering first documents id is one and not zero as for clustering
                    listOfDocsIds += ((int) cl.getDocID(clusterPos, docListPos) - 1) + ",";
                }                               

                int numOfPages = (int) Math.ceil(((double) numOfDocs) / numOfResultsPerPage);
                
                HTML newCluster = createNewCluster(listOfDocsIds, cl.getClusterName(clusterPos), numOfPages, numOfDocs, clusterPos);
                newCluster.setStyleName("newclusterclass");
                if (clusterTree.getItemCount() == 0) {
                    treeItem.addItem(newCluster);
                } else {
                    clusterTree.getItem(0).addItem(newCluster);
                }
            }
           // }
        }

        //Add TreeItem only in case the tree is Empty
        if (clusterTree.getItemCount() == 0) {
            clusterTree.addItem(treeItem);
            clusterTree.getItem(0).setText("Root(" + clusterTree.getItem(0).getChildCount() + ")");
        } else {
            // Sort the updated tree
            if (XSearch.mergeSemanticAnalysisResults) {
                treeItem = sortClusters(clusterTree.getItem(0), 0, "Root");
                clusterTree.insertItem(0, treeItem);
                hideMoreClusters();
                clusterTree.removeItem(clusterTree.getItem(1));
            } else {
                clusterTree.getItem(0).setText("Root(" + clusterTree.getItem(0).getChildCount() + ")");
            }
        }

        treeItem.setState(showTreeCollapsed);
        clusterTree.addStyleName("clustersTree");

        hideMoreClusters();
        return clusterTree;

    }
    
    /**
     * Updates the clustering tree in case that is selected the FacetedSearchType.INTERSECTION.
     * In more details, it stores the existing tree and after that updates the already existing tree
     * depending on the list of documents that corresponds to the selected item.
     * @param selectedItemId the id of the selected item.
     * @param treeStorage the structure that stores the trees and the list of documents that correspond to the 
     * selected item.
     */
    public static void updateClusteringTreeIntersectionMode(String selectedItemId, TreeStorage treeStorage){
            Tree t = new Tree();
           
            // Store the existing tree as it is
            for (int clusterPos = 0; clusterPos < XSearch.clusterTree.getItemCount(); clusterPos++) {
                final TreeItem trItem = XSearch.clusterTree.getItem(clusterPos);
                
                t.addItem(new TreeItem(trItem.getText()));
                for(int i=0; i < trItem.getChildCount(); i++){
                    t.getItem(clusterPos).addItem(trItem.getChild(i).getHTML());
                    
                    // Make invisible all the entities that are in position bigger than the "numOfEntitiesShow"
                    if(i>=numOfClustersShow && i!=(trItem.getChildCount()-1)){
                        t.getItem(clusterPos).getChild(i).setVisible(false);
                    }
                }
                
                // Check if in treeItem exist hide button and if yes then replace it with showAll button
                int lastChildItem = t.getItem(clusterPos).getChildCount() - 1;
                if(lastChildItem > 0 && t.getItem(clusterPos).getChild(lastChildItem).getText().trim().equals("hide")){
                    t.getItem(clusterPos).removeItem(t.getItem(clusterPos).getChild(lastChildItem));
                    addShowAllButton(t.getItem(clusterPos));
                }
                t.getItem(clusterPos).setState(showTreeCollapsed);
            }                        
            treeStorage.addClusteringTree(t);
            
            
            // Iterate throuth clustering tree
            for (int clusterPos = 0; clusterPos < XSearch.clusterTree.getItemCount(); clusterPos++) {
                String rootName = XSearch.clusterTree.getItem(clusterPos).getText().substring(0, XSearch.clusterTree.getItem(clusterPos).getText().lastIndexOf('('));
                TreeItem treeItem = XSearch.clusterTree.getItem(clusterPos);
                
                // If the root is empty, then return
                if(treeItem.getChildCount()==0){
                    return;
                }
               
                // Remove showAll/hide link if is contained to category's tree
                if (treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("showAll")
                        || treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("hide")) {
                    treeItem.removeItem(treeItem.getChild(treeItem.getChildCount() - 1));
                }

                // Iterate through entities
                for (int subClusterPos = 0; subClusterPos < treeItem.getChildCount();) {

                    String subClusterName = treeItem.getChild(subClusterPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getTitle();
                    String docIdList = treeItem.getChild(subClusterPos).getHTML().split("'")[1];
                    
                    // Create the new entity's docIds list 
                    String[] docIds = docIdList.split(",");
                    String newListOfDocIds = "";
                    int newListOfDocIdsSize = 0;
                    for(String docId : docIds){
                        if(treeStorage.getLastListOfVisibleDocIds().contains(docId)){
                           newListOfDocIds += docId + ",";
                           newListOfDocIdsSize++;
                        }
                    }
                    
                   // Check if the entity remains to tree, and if yes update it, otherwise remove it..
                   if(newListOfDocIdsSize !=0){
                        int numOfPages = (int) Math.ceil(((double) newListOfDocIdsSize) / numOfResultsPerPage);

                        // Create new Entity 
                       // HTMLPanel newEntity = createNewEntity(categoryName, subClusterName,  newListOfDocIdsSize, subClusterPos, numOfPages, newListOfDocIds);
                        HTML newCluster = createNewCluster(newListOfDocIds, subClusterName, numOfPages, newListOfDocIdsSize, subClusterPos);
                        treeItem.getChild(subClusterPos).setWidget(newCluster);
                        treeItem.getChild(subClusterPos).setVisible(true);                         
                        subClusterPos++;                                
                    }else{
                        treeItem.removeItem(treeItem.getChild(subClusterPos));
                    }                    
                }                
                
                // Add showAll link if is needed
                int numOfContainedClusters = treeItem.getChildCount();
                if (numOfContainedClusters > numOfClustersShow) {
                    treeItem = addShowAllButton(treeItem);
                    hideMoreClusters();
                }
                
                // Update CategoryName if it contains entities, otherwise remove it
                treeItem.setText(rootName + "(" + numOfContainedClusters + ")");               
                
                // Denote the selected item
                DOM.getElementById(selectedItemId).getStyle().setColor("red");
                treeItem.setState(showTreeCollapsed);
            }        
    }

    /**
     * Sort clusters by their frequency.     
     * @param treeItem treeItem that contains the textual clusters
     * @param clusterPos the position of that cluster in to the Tree
     * @param oldClusterName the cluster name 
     * @return
     */
    private TreeItem sortClusters(TreeItem treeItem, int clusterPos, String oldClusterName) {
        TreeItem sortedTreeItem = new TreeItem();
        sortedTreeItem.getElement().setId("Cluster_" + clusterPos);
        sortedTreeItem.getElement().setClassName("clusterClss");

        SortedMap<String, TreeItem> sortedMap = new TreeMap<String, TreeItem>(Collections.reverseOrder());
        // Adding ranking of Entity as an attribute with name "Rank"
        for (int entPos = 0; entPos < treeItem.getChildCount(); entPos++) {

            String clusterName = treeItem.getChild(entPos).getText();

            // Remove showAll/hide link if is contained to category's tree
            if (clusterName.trim().equals("showAll") || clusterName.trim().equals("hide")) {
                treeItem.removeItem(treeItem.getChild(entPos));
                continue;
            }

            int clusterFrequency = Integer.parseInt(clusterName.substring(treeItem.getChild(entPos).getText().lastIndexOf('(') + 1, treeItem.getChild(entPos).getText().lastIndexOf(')')));
            if (clusterFrequency < 10) {
                sortedMap.put("00000" + clusterFrequency + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (clusterFrequency >= 10 && clusterFrequency < 100) {
                sortedMap.put("0000" + clusterFrequency + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (clusterFrequency >= 100 && clusterFrequency < 1000) {
                sortedMap.put("000" + clusterFrequency + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (clusterFrequency >= 1000 & clusterFrequency < 10000) {
                sortedMap.put("00" + clusterFrequency + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (clusterFrequency >= 10000 & clusterFrequency < 100000) {
                sortedMap.put("0" + clusterFrequency + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            }
        }

        // Fill treeItem with the sorted list
        for (Entry<String, TreeItem> entry : sortedMap.entrySet()) {
            entry.getValue().setVisible(true);
            sortedTreeItem.addItem(entry.getValue());
        }

        // Add showAll link if is needed
        int numOfContainedEntities = sortedTreeItem.getChildCount();
        if (numOfContainedEntities > numOfClustersShow) {
            sortedTreeItem = addShowAllButton(sortedTreeItem);
        }

        // Update category name
        sortedTreeItem.setText(oldClusterName + "(" + numOfContainedEntities + ")");

        return sortedTreeItem;
    }

    /**
     * Returns a tree Item that contains a button/link that shows all the clusters
     * @param treeItem the treeItem in which the showAll button/link would be add it
     * @param clusterPos the position of that cluster in to tree
     * @return the new treeItem which includes the showAll button/link
     */
    private static TreeItem addShowAllButton(TreeItem treeItem) {
        HTML showall = new HTML(
                "<a style=\"float:right;color:red;\" href="
                + "javascript:showAllClusters() "
                + "id=\"showAllClusters\">"
                + "showAll"
                + "</a><br />");
        showall.addStyleName("more");
        treeItem.addItem(showall);
        treeItem.setState(showTreeCollapsed);

        return treeItem;
    }

    /**
     * Updates a cluster that is included in cluster tree
     * @param clusterName cluster name
     * @param listOfDocsIds a list of doc ids that contained into this cluster separated with comma
     * @param numOfDocs number of documents that contained into this cluster
     * @return an HTML that represents the new Cluster
     */
    private HTML updateCluster(String clusterName, String listOfDocsIds, int numOfDocs, int clusterPos) {
        int numOfPages = (int) Math.ceil(((double) numOfDocs) / numOfResultsPerPage);

        return new HTML(
                "<a class=\"clusterItem\" title=\"" + clusterName
                + "\" href=\"javascript:createResultPages2(" + numOfResultsPerPage + ","
                + numOfPages + ",'" + listOfDocsIds + "'" + ",'" + " (Cluster) " + clusterName + "'"+ ",'cl_"+clusterName+"')\" id=\"cl_"+clusterName+"\">"
                + shortedStr(clusterName)
                + "(" + (int) numOfDocs
                + ")</a>");
    }

    /**
     * Creates a new cluster label.
     * @param cl a clusteringJSONParser object that is used to retrieve information
     * @param clusterPos the position in cl object which has to be analyzed
     * @return the cluster label
     */
    private static HTML createNewCluster(String listOfDocsIds, String clusterName, int numOfPages, int numOfDocs, int clusterPos) {
        return new HTML(
                "<a class=\"clusterItem\" title=\"" + clusterName
                + "\" href=\"javascript:createResultPages2(" + numOfResultsPerPage + ","
                 + numOfPages + ",'" + listOfDocsIds + "'" + ",'" + "(Cluster) " + clusterName + "'"+ ",'cl_"+clusterName+"')\" id=\"cl_"+clusterName+"\">"
                + shortedStr(clusterName)
                + "(" + numOfDocs
                + ")</a>");
    }

    /**
     * Changes ShowAll Hyperlink to HideMore Hyperlink in order to close the
     * minimize the presented clusters.
     */
    private static void showAllClusters() {

        // Sets all the entities of that treeItem Visible
        for (int i = 0; i < clusterTree.getItem(0).getChildCount(); i++) {
            clusterTree.getItem(0).getChild(i).setVisible(true);
        }

        clusterTree.getItem(0)
                .getChild(clusterTree.getItem(0)
                .getChildCount() - 1).setWidget(new HTML(
                "<a style=\"float:right;color:red;\" href="
                + "javascript:hideMoreClusters() "
                + "id=\"hideMore\">"
                + "hide"
                + "</a><br />"));

    }

    /**
     * Changes HideMore Hyperlink to ShowAll Hyperlink in order to give the
     * opportunity to view again the collapsed clusters
     */
    private static void hideMoreClusters() {
        // if category does not have more than "numOfEntitiesShow" then just return
        if (clusterTree.getItem(0).getChildCount() <= numOfClustersShow) {
            return;
        }

        // Sets all the entities of that treeItem Visible
        for (int i = numOfClustersShow; i < clusterTree.getItem(0).getChildCount() - 1; i++) {
            clusterTree.getItem(0).getChild(i).setVisible(false);
        }

        clusterTree.getItem(0)
                .getChild(clusterTree.getItem(0)
                .getChildCount() - 1).setWidget(new HTML(
                "<a style=\"float:right;color:red;\" href=\"#\" "
                + "onclick=\"javascript:showAllClusters()\" "
                + " id=\"showall\">"
                + "showAll"
                + "</a><br />"));

        scrollPanel.scrollToTop();

    }

    /**
     * Takes as parameter a string and returns the first numOfCharsToShow
     * characters ended by two dots..
     *
     * @param str a string that we want to cut
     * @return the shorted string that contains k chars
     */
    private static String shortedStr(String str) {
        String shortedStr = str;

        if (str.length() > numOfCharsToShow) {
            shortedStr = str.substring(0, numOfCharsToShow) + "..";
        }

        return shortedStr;
    }
}
