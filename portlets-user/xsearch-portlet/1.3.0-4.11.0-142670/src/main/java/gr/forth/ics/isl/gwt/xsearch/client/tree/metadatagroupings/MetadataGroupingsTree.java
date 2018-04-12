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
package gr.forth.ics.isl.gwt.xsearch.client.tree.metadatagroupings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import gr.forth.ics.isl.gwt.xsearch.client.XSearch;
import gr.forth.ics.isl.gwt.xsearch.client.XSearchService;
import gr.forth.ics.isl.gwt.xsearch.client.XSearchServiceAsync;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.TreeStorage;
import gr.forth.ics.isl.gwt.xsearch.client.parser.json.MetadataGroupingsJSONParser;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that contains the functions to create a tree that contains
 * the metadata groupings of the search results.
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class MetadataGroupingsTree {

    /**Number of chars to show in entities*/
    private static final int numOfCharsToShow = 12;

    /**Number of Results per Page*/
    private static int numOfResultsPerPage = 10;

    /**Number of entities to show when open a Tree*/
    private static int numOfEntitiesShow = 5;

    private static boolean showTreeItemCollapsed = false;

    private static Tree groupingsTree = new Tree();
    //private Label ENTITIES = new Label("Entities:");
    private static ScrollPanel scrollPanel = new ScrollPanel();
    private static XSearchServiceAsync xsearchSvc = GWT.create(XSearchService.class);
	

    /**
     * Constructor that is used for initialization.
     * @param groupingsTree the tree that contains the metadata groupings tree
     * @param numOfResultsPerPage number of results to show per page
     * @param scrollPanel the main scroll panel that is used 
     */
    public MetadataGroupingsTree(Tree groupingsTree, int numOfResultsPerPage, ScrollPanel scrollPanel){
            this.numOfResultsPerPage = numOfResultsPerPage;
            this.scrollPanel = scrollPanel;
            this.groupingsTree = groupingsTree;
    }

    /** Create a JavaScript object from JSON string in order to access its properties in java*/
    public static native MetadataGroupingsJSONParser buildMetadataGroupingsFromJSON(String json) /*-{
            return eval("(" + json + ")");
    }-*/;

    /** 
    * Creates categories tree from a json string which contains the metadata groupings results. 
    * @param jsonText json string which contains the results of entity mining.
    * @return a tree that contains the metadata groupings
    */
    public Tree createMetadataGroupingTree(String jsonText) {

            // Build Json parser
            MetadataGroupingsJSONParser ct = buildMetadataGroupingsFromJSON(jsonText);	
            
            int numOfMetadataGroupings = (int) ct.getNumOfGroups();
            boolean firstTime = false;		
            boolean isNewMetadataGrouing = true;
            
            if(!XSearch.mergeSemanticAnalysisResults){
                //Clear Categories tree from old results				                            
                groupingsTree.clear();
            }
            
            for (int metadataGroupingPos = 0; metadataGroupingPos < numOfMetadataGroupings; metadataGroupingPos++, isNewMetadataGrouing=true) {

                    String metadataGroupName = ct.getMetadataGroupName(metadataGroupingPos);

                    // No previously presented mining tree
                    if (groupingsTree.getItemCount() == 0 || firstTime){				
                            TreeItem treeItem = createTreeItemForNewMetadataGroup(ct, metadataGroupName, metadataGroupingPos);
                            treeItem.setState(showTreeItemCollapsed);
                            groupingsTree.addItem(treeItem);				
                            firstTime = true;
                            continue;
                    }

                    // Check if merge is enabled and if is contained a tree
                    if(XSearch.mergeSemanticAnalysisResults){				

                            // Iterate through the categories tha already contained
                            for (int oldMetadataGroupingPos = 0 ; oldMetadataGroupingPos < groupingsTree.getItemCount(); oldMetadataGroupingPos++){		

                                    String oldMetadataGroupingName = groupingsTree.getItem(oldMetadataGroupingPos).getText().substring(0, groupingsTree.getItem(oldMetadataGroupingPos).getText().lastIndexOf('('));
                                    if (oldMetadataGroupingName.equalsIgnoreCase(metadataGroupName)){
                                            // CategoryName is already contained 				
                                            TreeItem curTreeItem = groupingsTree.getItem(oldMetadataGroupingPos);
                                            TreeItem updatedTreeItem = updateTreeItemForMetadataGroup(ct, curTreeItem, metadataGroupingPos);
                                            TreeItem sortedTreeItem = sortMetadataGroupingEntities(updatedTreeItem, oldMetadataGroupingPos, metadataGroupName);
                                            groupingsTree.insertItem(oldMetadataGroupingPos, sortedTreeItem);
                                            hideMoreMetadata(oldMetadataGroupingPos+"");
                                            groupingsTree.removeItem(groupingsTree.getItem(oldMetadataGroupingPos+1));

                                            // Denote that category is already contained
                                            isNewMetadataGrouing = false;					

                                            // If is found once then we could stop iteration
                                            break;
                                    }
                            }	

                            if(isNewMetadataGrouing){
                                    // The category is not contained so add a new treeItem for the category						
                                    TreeItem treeItem = createTreeItemForNewMetadataGroup(ct, metadataGroupName, metadataGroupingPos);
                                    treeItem.setState(showTreeItemCollapsed);
                                    groupingsTree.addItem(treeItem);	
                            } 			

                    }else{			
                            TreeItem treeItem = createTreeItemForNewMetadataGroup(ct, metadataGroupName, metadataGroupingPos);
                            treeItem.setState(showTreeItemCollapsed);
                            groupingsTree.addItem(treeItem);				
                    }
            }

            sortMetadataGroupings();
            groupingsTree.addStyleName("metadataTree");

            return groupingsTree;
    }
	
    /**
     * Sorts the metadata groups in alphabetic order
     * @return the sorted Tree
     */
    private Tree sortMetadataGroupings(){
            SortedMap<String, TreeItem> sortedMap = new TreeMap<String, TreeItem>(Collections.reverseOrder(Collections.reverseOrder()));

            TreeItem treeItem = new TreeItem();
            for(int i =0; i < groupingsTree.getItemCount(); i++){
                    treeItem = groupingsTree.getItem(i);
                    sortedMap.put(treeItem.getText().toLowerCase(), treeItem);
            }

            //Clear groupingsTree
            groupingsTree.clear();

            // Add treeItems at groupingsTree sorted
            String showAllHTML = "", toRemove = "";
            int start =0, end = 0 , positionTree = 0;
            for(Entry<String, TreeItem> entry : sortedMap.entrySet()){

                // Update tha javascript function of showAll link
                if (entry.getValue().getChild(entry.getValue().getChildCount()-1).getText().equals("showAll")){
                        showAllHTML = entry.getValue().getChild(entry.getValue().getChildCount()-1).getHTML();
                        start =showAllHTML.lastIndexOf("href=")+1;
                        end = showAllHTML.lastIndexOf("')")+2;
                        toRemove = showAllHTML.substring(start, end);

                        entry.getValue().getChild(entry.getValue().getChildCount()-1).setHTML(showAllHTML.replace(toRemove, "ref=\"javascript:showAllMetadata('"+positionTree+"')"));
                }
                groupingsTree.addItem(entry.getValue());
                positionTree++;
            }

            return groupingsTree;
    }
    
    /**
     * Updates the Metadata groupings tree in case that is selected the FacetedSearchType.INTERSECTION.
     * In more details, it stores the existing tree and after that updates the already existing tree
     * depending on the list of documents that corresponds to the selected item.
     * @param selectedItemId the id of the selected item.
     * @param treeStorage the structure that stores the trees and the list of documents that correspond to the 
     * selected item.
     */
     public static void updateMetadataTreeIntersectionMode(String selectedItemId, TreeStorage treeStorage){
            Tree t = new Tree();
           
            // Store the existing tree as it is
            for (int categoryPos = 0; categoryPos < XSearch.groupingsTree.getItemCount(); categoryPos++) {
                final TreeItem trItem = XSearch.groupingsTree.getItem(categoryPos);
                
                t.addItem(new TreeItem(trItem.getText()));
                for(int i=0; i < trItem.getChildCount(); i++){
                    t.getItem(categoryPos).addItem(trItem.getChild(i).getHTML());
                    
                    // Make invisible all the entities that are in position bigger than the "numOfEntitiesShow"
                    if(i>=numOfEntitiesShow && i!=(trItem.getChildCount()-1)){
                        t.getItem(categoryPos).getChild(i).setVisible(false);
                    }
                }
                
                // Check if in treeItem exist hide button and if yes then replace it with showAll button
                int lastChildItem = t.getItem(categoryPos).getChildCount() - 1;
                if(t.getItem(categoryPos).getChild(lastChildItem).getText().trim().equals("hide")){
                    t.getItem(categoryPos).removeItem(t.getItem(categoryPos).getChild(lastChildItem));
                    addShowAllButton(t.getItem(categoryPos), categoryPos);
                }
                t.getItem(categoryPos).setState(showTreeItemCollapsed);
            }                        
            treeStorage.addMetadataTree(t);
            
            
            // Iterate throuth mining tree
            for (int categoryPos = 0; categoryPos < XSearch.groupingsTree.getItemCount();) {
                String categoryName = XSearch.groupingsTree.getItem(categoryPos).getText().substring(0, XSearch.groupingsTree.getItem(categoryPos).getText().lastIndexOf('('));
                TreeItem treeItem = XSearch.groupingsTree.getItem(categoryPos);                
               
                // Remove showAll/hide link if is contained to category's tree
                if (treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("showAll")
                        || treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("hide")) {
                    treeItem.removeItem(treeItem.getChild(treeItem.getChildCount() - 1));
                }

                // Iterate through entities
                for (int entPos = 0; entPos < treeItem.getChildCount();) {

                    String entityName = treeItem.getChild(entPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getTitle();
                    String docIdList = treeItem.getChild(entPos).getHTML().split("'")[1];
                    
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
                        HTML newEntity = createNewEntity(categoryName, entityName,  newListOfDocIdsSize, numOfPages, newListOfDocIds);
                        treeItem.getChild(entPos).setWidget(newEntity);
                        treeItem.getChild(entPos).setVisible(true);                         
                        entPos++;                                
                    }else{
                        treeItem.removeItem(treeItem.getChild(entPos));
                    }                    
                }                
                
                // Add showAll link if is needed
                int numOfContainedEntities = treeItem.getChildCount();
                if (numOfContainedEntities > numOfEntitiesShow) {
                    treeItem = addShowAllButton(treeItem, categoryPos);
                    hideMoreMetadata(categoryPos+"");
                }
                
                // Update CategoryName if it contains entities, otherwise remove it
                if(numOfContainedEntities==0){
                    XSearch.groupingsTree.removeItem(treeItem);
                }else{
                    treeItem.setText(categoryName + "(" + numOfContainedEntities + ")"); 
                    categoryPos++;
                }
                
                // Denote the selected item
                DOM.getElementById(selectedItemId).getStyle().setColor("red");
                treeItem.setState(showTreeItemCollapsed);
            }        
    }
    

    /**
     * Sorts the entities that are contained into a metadata group.
     * @param treeItem the treeItem which children would be sorted
     * @param metadataGroupPos metadata group position in metadata groupings tree
     * @param metadataGroupName the metadata group name
     * @return the treeItem sorted
     */
    private TreeItem sortMetadataGroupingEntities(TreeItem treeItem, int metadataGroupPos, String metadataGroupName){
            TreeItem sortedTreeItem = new TreeItem();
            sortedTreeItem.getElement().setId("Metadata_" + metadataGroupPos);

            SortedMap<String, TreeItem> sortedMap = new TreeMap<String, TreeItem>(Collections.reverseOrder());
           
            String name;
            for(int entPos = 0 ; entPos < treeItem.getChildCount(); entPos++){
                name = treeItem.getChild(entPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getAttribute("title");
                sortedMap.put(name, treeItem.getChild(entPos));
            }

            // Fill treeItem with the sorted list
            for(Entry<String, TreeItem> entry : sortedMap.entrySet()){
                    entry.getValue().setVisible(true);
                    sortedTreeItem.addItem(entry.getValue());
            }

            // Add showAll link if is needed
            int numOfContainedEntities = sortedTreeItem.getChildCount();
            if (numOfContainedEntities > numOfEntitiesShow){
                    sortedTreeItem = addShowAllButton(sortedTreeItem, metadataGroupPos);
            }

            // Update category name
            sortedTreeItem.setText(metadataGroupName + "("+ numOfContainedEntities +")");

            return sortedTreeItem;
    }

    /** 
     * Updates a treeItem that is already included into the metadata groupings Tree.
     * @param ct MetadataGroupingsJSONParser object to parse
     * @param treeItem the treeItem that is already contained into metadataGrouping tree
     * @param newMetadataGroupingPos the position in which would be placed the updated treeItem into the
     *                               metadata groupings Tree
     * @return the updated TreeItem
     */
    private TreeItem updateTreeItemForMetadataGroup(MetadataGroupingsJSONParser ct, TreeItem treeItem, int newMetadataGroupingPos){
            int numOfContainedEntities = Integer.parseInt(treeItem.getText().substring(treeItem.getText().lastIndexOf('(')+1, treeItem.getText().lastIndexOf(')')));
            String metadataGroupName = ct.getMetadataGroupName(newMetadataGroupingPos);
                                        
            // Remove showAll/hide link if is contained to category's tree
            if (treeItem.getChild(treeItem.getChildCount()-1).getText().trim().equals("showAll") 
                    || treeItem.getChild(treeItem.getChildCount()-1).getText().trim().equals("hide")){
                    treeItem.removeItem(treeItem.getChild(treeItem.getChildCount()-1));
            }

            boolean isNewEntity = true;
            //Iterate through new entities of that category
            for (int entityPos = 0; entityPos < ct.getNumOfMetadatas(newMetadataGroupingPos); entityPos++, isNewEntity=true) {

                    String newEntName = ct.getMetadataName(newMetadataGroupingPos, entityPos);
                    // Iterate through entities that are already contained
                    for(int oldEntPos = 0; oldEntPos < treeItem.getChildCount(); oldEntPos++){

                            //String oldEntName = treeItem.getChild(oldEntPos).getHTML().split("title=")[1].split("\" ")[0].replace("\"", "");
                            String oldEntName = treeItem.getChild(oldEntPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getAttribute("title");
                            if(oldEntName.trim().equalsIgnoreCase(newEntName.trim())){
                                    // Entity is already contained	

                                        String oldDocIdsList = treeItem.getChild(oldEntPos).getHTML().split("'")[1];

                                        // Passes Documents list to a string
                                        int sizeOfNewDocList = (int) ct.getNumOfDocs(newMetadataGroupingPos, entityPos); 
                                        String newListOfDocs = new String();
                                        for (int docListPos = 0; docListPos < sizeOfNewDocList; docListPos++){
                                                newListOfDocs += (int)ct.getDocID(newMetadataGroupingPos, entityPos, docListPos)+ ",";
                                        } 
                                        newListOfDocs += oldDocIdsList;
                                        sizeOfNewDocList += oldDocIdsList.split(",").length;

                                        int numOfPages =  (int) Math.ceil(((double) sizeOfNewDocList) / numOfResultsPerPage);

                                        // Create new Entity 
                                        HTML newEntity = createNewEntity(metadataGroupName, newEntName,  sizeOfNewDocList, numOfPages, newListOfDocs);//
                                        treeItem.getChild(oldEntPos).setWidget(newEntity);

                                        isNewEntity = false;
                                        break;
                            }
                    }

                    if (isNewEntity){
                            // Entity is new	

                                // Passes Documents list to a string
                                int sizeOfDocList = (int) ct.getNumOfDocs(newMetadataGroupingPos, entityPos); 
                                String listOfDocs = new String();
                                for (int docListPos = 0; docListPos < sizeOfDocList; docListPos++){
                                        listOfDocs += (int)ct.getDocID(newMetadataGroupingPos, entityPos, docListPos)+ ",";
                                } 

                                int numOfPages =  (int) Math.ceil(((double)ct.getNumOfDocs(newMetadataGroupingPos, entityPos)) / numOfResultsPerPage);

                                // Create new Entity
                                HTML newEntity = createNewEntity(metadataGroupName, newEntName,  sizeOfDocList, numOfPages, listOfDocs);
                                treeItem.addItem(newEntity).addStyleName("metadata");

                                numOfContainedEntities++;
                    }		
            }	

            return treeItem;
    }

    /** 
     * Creates a new metadata group.
     * @param ct MetadataGroupingsJSONParser to parse in order to create the new metadata group
     * @param metadataGroupingName metadata group name
     * @param metadataGroupingPos position of metadata group in metadata grouping tree
     * @return the treeItem that contains the metadata group
     */
    private TreeItem createTreeItemForNewMetadataGroup(MetadataGroupingsJSONParser ct, String metadataGroupingName, int metadataGroupingPos){

            TreeItem treeItem = new TreeItem(metadataGroupingName + "("+ (int)ct.getNumOfMetadatas(metadataGroupingPos) +")");
            treeItem.getElement().setId("Metadata_" + metadataGroupingPos);
            
            String metadataGroupName = ct.getMetadataGroupName(metadataGroupingPos);          

            int numOfEninties = (int) ct.getNumOfMetadatas(metadataGroupingPos);
            for (int entityPos = 0; entityPos < numOfEninties; entityPos++) {

                    String newEntName = ct.getMetadataName(metadataGroupingPos, entityPos);

                    // Passes Documents list to a string
                    int sizeOfDocList = (int) ct.getNumOfDocs(metadataGroupingPos, entityPos); 
                    String listOfDocs = new String();
                    for (int docListPos = 0; docListPos < sizeOfDocList; docListPos++){
                            listOfDocs += (int)ct.getDocID(metadataGroupingPos, entityPos, docListPos)+ ",";
                    } 

                    int numOfPages =  (int) Math.ceil(((double)ct.getNumOfDocs(metadataGroupingPos, entityPos)) / numOfResultsPerPage);

                    // Create new Entity
                    HTML newEntity = createNewEntity(metadataGroupName, newEntName,  sizeOfDocList, numOfPages, listOfDocs);
                    treeItem.addItem(newEntity).addStyleName("metadata");

                // Set invisible all the entities after the fifth
                    if (entityPos > numOfEntitiesShow - 1) {
                            treeItem.getChild(entityPos).setVisible(false);
                    }			
            }	

            if (numOfEninties > numOfEntitiesShow){
                    treeItem = addShowAllButton(treeItem, metadataGroupingPos);
            }

            return treeItem;
    }

    /**
     * Adds to a treeItem a showAll button/link
     * @param treeItem the treeItem in which would be add it
     * @param metadataGroupingPos the position of metadata Group into the metadata groupings tree 
     * @return the treeItem that includes the showAll button
     */
    private static TreeItem addShowAllButton(TreeItem treeItem, int metadataGroupingPos){			
            HTML showall = new HTML(
                            "<a style=\"float:right;color:red;\" href="
                                            + "javascript:showAllMetadata('"+metadataGroupingPos+"') "
                                            + "id=\"showall\">"
                                            + "showAll"
                                            + "</a><br />");
            showall.addStyleName("more");
            treeItem.addItem(showall);
            treeItem.setState(showTreeItemCollapsed);					

            return treeItem;
    }

    /** 
     * Creates a new entity that is included to the metadata group
     * @param ct MetadataGroupingsJSONParser object to parse in oder to take that information
     * @param numOfDocs number of documents in which is contained that metadata 
     * @param metadataGroupingPos the position of metadata group into the MetadataGroupingsJSONParser objects
     * @param entityPos the position of entity into the MetadataGroupingsJSONParser objects
     * @param numOfPages number of pages that has to be created to display the hits that are contain the metadata 
     * @param listOfDocs list of documents that contain this metadata 
     * @return an HTML panel with the new metadata/entity
     */
    private static HTML createNewEntity(String categoryName, String entityName,  int numOfDocs, int numOfPages, String listOfDocs){

        HTML newEntity = new HTML("<a title=\""+entityName+"\" "
                                                + "href=\"javascript:createResultPages2("+numOfResultsPerPage+","+numOfPages+",'"+listOfDocs+"'"+",'("+categoryName+") "+ entityName+"'"+",'m"+categoryName+"_"+entityName +"') \""
                                                + "id=\"m"
                                                + categoryName+"_"+entityName + "\">"
                                                + shortedStr(entityName)
                                                + "("
                                                + numOfDocs
                                                + ")"
                                                + "</a>");
        newEntity.addStyleName("metadata");       

        return newEntity;			
    }

    /**
    * Takes as parameter a string and returns the first numOfCharsToShow 
    * characters ended by two dots..
     * @param str the string that we want to cut
     * @return the shorted string which now contains k chars
     */
    private static  String shortedStr(String str){
            String shortedStr = str;

            if (str.length() > numOfCharsToShow){
                    shortedStr = str.substring(0, numOfCharsToShow) + "..";
            }

            return shortedStr;
    }

    /**
    * Changes ShowAll Hyperlink to HideMore Hyperlink in order to close
    * the minimize the presented entities.
    * @param metadataGroupPos metadata group's position in the tree
    */
    private static void showAllMetadata(String metadataGroupPos){
            // Sets all the entities of that treeItem Visible
            for (int i=0; i < groupingsTree.getItem(Integer.parseInt(metadataGroupPos)).getChildCount(); i ++){
                    groupingsTree.getItem(Integer.parseInt(metadataGroupPos)).getChild(i).setVisible(true);
            }

            if(groupingsTree == null){
                    Window.alert("Is NULL at showAll");
            }
            groupingsTree.getItem(Integer.parseInt(metadataGroupPos))
                            .getChild(groupingsTree.getItem(Integer.parseInt(metadataGroupPos))
                            .getChildCount()-1).setWidget(new HTML(
                                                            "<a style=\"float:right;color:red;\" href="
                                                                            + "javascript:hideMoreMetadata('"+metadataGroupPos+"') "
                                                                            + "id=\"hideMore\">"
                                                                            + "hide"
                                                                            + "</a><br />"));

    }

    /**
    * Changes HideMore Hyperlink to ShowAll Hyperlink in order 
    * to give the opportunity to view again the collapsed entities
    * @param metadataGroupPos gategory's position in the tree
    */
    private static void hideMoreMetadata(String metadataGroupPos){
            // if category does not have more than "numOfEntitiesShow" then just return
            if(groupingsTree.getItem(Integer.parseInt(metadataGroupPos)).getChildCount() <= numOfEntitiesShow){
                    return;
            }

            // Sets all the entities of that treeItem Visible
            for (int i=numOfEntitiesShow; i < groupingsTree.getItem(Integer.parseInt(metadataGroupPos)).getChildCount()-1; i ++){
                    groupingsTree.getItem(Integer.parseInt(metadataGroupPos)).getChild(i).setVisible(false);
            }

            groupingsTree.getItem(Integer.parseInt(metadataGroupPos))
                            .getChild(groupingsTree.getItem(Integer.parseInt(metadataGroupPos))
                            .getChildCount()-1).setWidget(new HTML(
                                                            "<a style=\"float:right;color:red;\" href="
                                                                            + "javascript:showAllMetadata('"+metadataGroupPos+"') "
                                                                            + "id=\"showall\">"
                                                                            + "showAll"
                                                                            + "</a><br />"));

            scrollPanel.scrollToTop();

    }
}
