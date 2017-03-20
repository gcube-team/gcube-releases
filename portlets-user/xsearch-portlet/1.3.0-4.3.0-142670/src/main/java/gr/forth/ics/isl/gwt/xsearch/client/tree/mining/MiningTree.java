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
package gr.forth.ics.isl.gwt.xsearch.client.tree.mining;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import gr.forth.ics.isl.gwt.xsearch.client.XSearch;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.TreeStorage;
import gr.forth.ics.isl.gwt.xsearch.client.parser.json.CategoriesJSONParser;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that contains the functions to create a tree that contains
 * the mining results. 
 * @author kitsos Ioannis (kitsos@ics.forth.gr)
 */
public class MiningTree {

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
    public static int numOfEntitiesShow = 5;
    private static boolean showTreeCollapsed = true;
    private static Tree categoriesTree = new Tree();
    private static ScrollPanel scrollPanel = new ScrollPanel();

    public MiningTree(Tree categoriesTree, int numOfResultsPerPage, ScrollPanel scrollPanel) {
        this.numOfResultsPerPage = numOfResultsPerPage;
        this.scrollPanel = scrollPanel;
        this.categoriesTree = categoriesTree;
    }

    /**
     * Create a JavaScript object from JSON string in order to access its
     * properties in java
     */
    public static native CategoriesJSONParser buildCategoriesFromJSON(String json) /*-{
     return eval("(" + json + ")");
     }-*/;

    /**
     * Creates categories tree from a json string which contains the mining
     * results of semantic analysis.
     *
     * @param jsonText json string which contains the results of entity mining.
     * @return a Tree that contains the mined categories/entities.
     */
    public Tree createMiningTree(String jsonText) {

        // Build Json parser
        CategoriesJSONParser ct = buildCategoriesFromJSON(jsonText);

        int numOfCategories = (int) ct.getNumOfCategories();
        boolean firstTime = false;
        boolean isNewCategory = true;
        
        if (!XSearch.mergeSemanticAnalysisResults) {
            //Clear Categories tree from old results
            categoriesTree.clear();
        }
        
        for (int categoryPos = 0; categoryPos < numOfCategories; categoryPos++, isNewCategory = true) {

            String categoryName = ct.getCategoryName(categoryPos);

            // No previously presented mining tree
            if (categoriesTree.getItemCount() == 0 || firstTime) {
                TreeItem treeItem = createTreeItemForNewCategory(ct, categoryName, categoryPos);
                treeItem.setState(showTreeCollapsed);
                categoriesTree.addItem(treeItem);
                firstTime = true;
                continue;
            }

            // Check if merge is enabled and if is contained a tree
            if (XSearch.mergeSemanticAnalysisResults) {

                // Iterate through the categories tha already contained
                for (int oldCategoryPos = 0; oldCategoryPos < categoriesTree.getItemCount(); oldCategoryPos++) {

                    String oldCategoryName = categoriesTree.getItem(oldCategoryPos).getText().substring(0, categoriesTree.getItem(oldCategoryPos).getText().lastIndexOf('('));
                    if (oldCategoryName.equalsIgnoreCase(categoryName)) {
                        // CategoryName is already contained 				
                        TreeItem curTreeItem = categoriesTree.getItem(oldCategoryPos);
                        TreeItem updatedTreeItem = updateTreeItemForCategory(ct, curTreeItem, categoryName, categoryPos, oldCategoryPos);
                        TreeItem sortedTreeItem = sortCategoryEntities(updatedTreeItem, oldCategoryPos, categoryName);
                        curTreeItem.setState(showTreeCollapsed);
                        updatedTreeItem.setState(showTreeCollapsed);
                        sortedTreeItem.setState(showTreeCollapsed);
                        categoriesTree.insertItem(oldCategoryPos, sortedTreeItem);
                        hideMore(oldCategoryPos + "");
                        categoriesTree.removeItem(categoriesTree.getItem(oldCategoryPos + 1));

                        // Denote that category is already contained
                        isNewCategory = false;

                        // If is found once then we could stop iteration
                        break;
                    }
                }

                if (isNewCategory) {
                    // The category is not contained so add a new treeItem for the category						
                    TreeItem treeItem = createTreeItemForNewCategory(ct, categoryName, categoryPos);
                    treeItem.setState(showTreeCollapsed);
                    categoriesTree.addItem(treeItem);
                }

            } else {
                TreeItem treeItem = createTreeItemForNewCategory(ct, categoryName, categoryPos);
                treeItem.setState(showTreeCollapsed);
                categoriesTree.addItem(treeItem);
            }
        }
        return categoriesTree;
    }

    /**
     * Sort the entities that are contained into a category
     * @param treeItem the treeItem that want to sort it's children
     * @param categoryPos the category position into mining Tree
     * @param categoryName the category name
     * @return the sorted TreeItem
     */
    private TreeItem sortCategoryEntities(TreeItem treeItem, int categoryPos, String categoryName) {
        TreeItem sortedTreeItem = new TreeItem();
        sortedTreeItem.getElement().setId("Category_" + categoryPos);
        sortedTreeItem.getElement().setClassName("categoryItemCls");

        SortedMap<String, TreeItem> sortedMap = new TreeMap<String, TreeItem>(Collections.reverseOrder());
        // Adding ranking of Entity as an attribute with name "Rank"
        EntityRankingFormulas rankingEntity = new EntityRankingFormulas();
        for (int entPos = 0; entPos < treeItem.getChildCount(); entPos++) {
            String listOfDocs = treeItem.getChild(entPos).getHTML().split("createResultPages2")[1].split("'")[1];
            double rank = (rankingEntity.rankEntity(listOfDocs, EntityRankingFormulas.RANKING_FORMULA_AGGREGATIONS));
            treeItem.getChild(entPos).getElement().setAttribute("Rank", rank + "");
            if (rank < 10) {
                sortedMap.put("00000" + rank + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (rank >= 10 && rank < 100) {
                sortedMap.put("0000" + rank + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (rank >= 100 && rank < 1000) {
                sortedMap.put("000" + rank + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (rank >= 1000 & rank < 10000) {
                sortedMap.put("00" + rank + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            } else if (rank >= 10000 & rank < 100000) {
                sortedMap.put("0" + rank + treeItem.getChild(entPos).getText(), treeItem.getChild(entPos));
            }
        }

        // Fill treeItem with the sorted list
        for (Entry<String, TreeItem> entry : sortedMap.entrySet()) {
            entry.getValue().setVisible(true);
            sortedTreeItem.addItem(entry.getValue());
        }

        // Add showAll link if is needed
        int numOfContainedEntities = sortedTreeItem.getChildCount();
        if (numOfContainedEntities > numOfEntitiesShow) {
            sortedTreeItem = addShowAllButton(sortedTreeItem, categoryPos);
        }

        // Update category name
        sortedTreeItem.setText(categoryName + "(" + numOfContainedEntities + ")");

        return sortedTreeItem;
    }

    /**
     * Update the contents of the treeItem by merging them with the new results.
     * @param ct a CategoriesJSONParser to parse
     * @param treeItem the treeItem that is already contained in that tree
     * @param categoryName the category name
     * @param newCategoryPos the position in which would be placed the new category into the mining tree
     * @param oldCategoryPos the old position in which was placed the category into the mining tree
     * @return the updated treeItem
     */
    private TreeItem updateTreeItemForCategory(CategoriesJSONParser ct, TreeItem treeItem, String categoryName, int newCategoryPos, int oldCategoryPos) {
        int numOfContainedEntities = Integer.parseInt(treeItem.getText().substring(treeItem.getText().lastIndexOf('(') + 1, treeItem.getText().lastIndexOf(')')));

        // Remove showAll/hide link if is contained to category's tree
        if (treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("showAll")
                || treeItem.getChild(treeItem.getChildCount() - 1).getText().trim().equals("hide")) {
            treeItem.removeItem(treeItem.getChild(treeItem.getChildCount() - 1));
        }

        boolean isNewEntity = true;
        //Iterate through new entities of that category
        for (int entityPos = 0; entityPos < ct.getNumOfEntities(newCategoryPos); entityPos++, isNewEntity = true) {

            String newEntName = ct.getEntityName(newCategoryPos, entityPos);
            // Iterate through entities that are already contained
            for (int oldEntPos = 0; oldEntPos < treeItem.getChildCount(); oldEntPos++) {

                String oldEntName = treeItem.getChild(oldEntPos).getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getTitle();
                
                if (oldEntName.trim().equalsIgnoreCase((newEntName).trim())) {
                    // Entity is already contained

                    String oldDocIdsList = treeItem.getChild(oldEntPos).getHTML().split("'")[1];

                    // Passes Documents list to a string
                    int sizeOfNewDocList = (int) ct.getNumOfDocs(newCategoryPos, entityPos);
                    String newListOfDocs = new String();
                    for (int docListPos = 0; docListPos < sizeOfNewDocList; docListPos++) {
                        newListOfDocs += (int) ct.getDocID(newCategoryPos, entityPos, docListPos) + ",";
                    }
                    newListOfDocs += oldDocIdsList;
                    sizeOfNewDocList += oldDocIdsList.split(",").length;

                    int numOfPages = (int) Math.ceil(((double) sizeOfNewDocList) / numOfResultsPerPage);

                    // Create new Entity 
                    HTMLPanel newEntity = createNewEntity(ct.getCategoryName(newCategoryPos), ct.getEntityName(newCategoryPos, entityPos), sizeOfNewDocList, entityPos, numOfPages, newListOfDocs);
                    treeItem.getChild(oldEntPos).setWidget(newEntity);

                    isNewEntity = false;
                    break;
                }
            }

            if (isNewEntity) {
                // Entity is new		

                // Passes Documents list to a string
                int sizeOfDocList = (int) ct.getNumOfDocs(newCategoryPos, entityPos);
                String listOfDocs = new String();
                for (int docListPos = 0; docListPos < sizeOfDocList; docListPos++) {
                    listOfDocs += (int) ct.getDocID(newCategoryPos, entityPos, docListPos) + ",";
                }

                int numOfPages = (int) Math.ceil(((double) ct.getNumOfDocs(newCategoryPos, entityPos)) / numOfResultsPerPage);

                // Create new Entity
                HTMLPanel newEntity = createNewEntity(ct.getCategoryName(newCategoryPos), ct.getEntityName(newCategoryPos, entityPos), (int) ct.getNumOfDocs(newCategoryPos, entityPos), entityPos, numOfPages, listOfDocs);
                treeItem.addItem(newEntity).addStyleName("entity");

                numOfContainedEntities++;
            }
        }



        return treeItem;
    }

    /**
     * Create and return a new treeItem for a new category.
     * @param ct a CategoriesJSONParser object to parse
     * @param categoryName the category name 
     * @param categoryPos the position of that treeItem into the mining tree
     * @return the created treeItem
     */
    private TreeItem createTreeItemForNewCategory(CategoriesJSONParser ct, String categoryName, int categoryPos) {

        TreeItem treeItem = new TreeItem(categoryName + "(" + (int) ct.getNumOfEntities(categoryPos) + ")");
        treeItem.getElement().setId("Category_" + categoryPos);
        treeItem.getElement().setClassName("categoryItemCls");

        int numOfEninties = (int) ct.getNumOfEntities(categoryPos);
        for (int entityPos = 0; entityPos < numOfEninties; entityPos++) {

            // Passes Documents list to a string
            int sizeOfDocList = (int) ct.getNumOfDocs(categoryPos, entityPos);
            String listOfDocs = new String();
            for (int docListPos = 0; docListPos < sizeOfDocList; docListPos++) {
                listOfDocs += (int) ct.getDocID(categoryPos, entityPos, docListPos) + ",";
            }

            int numOfPages = (int) Math.ceil(((double) ct.getNumOfDocs(categoryPos, entityPos)) / numOfResultsPerPage);

            // Create new Entity
            HTMLPanel newEntity = createNewEntity(ct.getCategoryName(categoryPos), ct.getEntityName(categoryPos, entityPos), (int) ct.getNumOfDocs(categoryPos, entityPos), entityPos, numOfPages, listOfDocs);
            treeItem.insertItem(0, newEntity).addStyleName("entity");

            // Set invisible all the entities after the fifth
           /* if (entityPos > numOfEntitiesShow - 1) {
                treeItem.getChild(entityPos).setVisible(false);
            }*/
        }

        // Set invisible all the entities after the numOfEntitiesShow
        for(int i = numOfEntitiesShow; i < treeItem.getChildCount(); i++){
             treeItem.getChild(i).setVisible(false);
        }
        
        if (numOfEninties > numOfEntitiesShow) {
            treeItem = addShowAllButton(treeItem, categoryPos);
        }

        return treeItem;
    }

    /**
     * Adds to a treeItem a showAll button/link
     * @param treeItem the treeItem in which would be add it
     * @param categoryPos the position of cateory into the mining groupings tree 
     * @return the treeItem that includes the showAll button
     */
    public static TreeItem addShowAllButton(TreeItem treeItem, int categoryPos) {
        HTMLPanel showall = new HTMLPanel(
                "<a style=\"float:right;color:red;\" href="
                + "javascript:showAll('" + categoryPos + "') "
                + "id=\"showall\">"
                + "showAll"
                + "</a><br />");
        showall.addStyleName("more");
        treeItem.addItem(showall);
        treeItem.setState(showTreeCollapsed);

        return treeItem;
    }

    /**
     * Creates a new entity that is included to a category
     * @param ct CategoriesJSONParser  object to parse in oder to take that information
     * @param numOfDocs number of documents in which is contained that entity 
     * @param categoryPos the position of category into the CategoriesJSONParser objects
     * @param entityPos the position of entity into the CategoriesJSONParser objects
     * @param numOfPages number of pages that has to be created to display the hits that contain the entity 
     * @param listOfDocs list of documents that contain this entity 
     * @return an HTML panel with the new entity
     */
    public static  HTMLPanel createNewEntity(final String categoryName, final String entityName, int numOfDocs, int entityPos, int numOfPages, String listOfDocs) {

        HTML entityClick = new HTML();
        entityClick.setHTML("<a href=\"javascript:getEntityEnrichment('"+entityName+"','"+categoryName+"') \"><img src=\"/xsearch-portlet/images/rdf.png\" border=\"0\" style=\"padding-left:3px; text-decoration:none\"/></a>");

        HTMLPanel newEntity = new HTMLPanel(
                "<a style=\"float:left;margin-right:2px;\" title=\"" + entityName + "\" "
                + "href=\"javascript:createResultPages2(" + numOfResultsPerPage 
                                                          + "," + numOfPages + ",'" 
                                                          + listOfDocs + "'" + ",' " + " (" + categoryName.toUpperCase() + ") "
                                                          + entityName +"'"
                                                          + ",'e"+categoryName+"_"+entityName +"') \""
                + "id=\"e"
                + categoryName+"_"+entityName + "\">"
                + shortedStr(entityName)
                + "("
                + numOfDocs
                + ")"
                + "</a>");
        newEntity.add(entityClick);
        newEntity.getElement().setId("entity_" + entityPos);
        newEntity.addStyleName("entity");
       
        return newEntity;
    }  

    /**
     * Takes as parameter a string and returns the first numOfCharsToShow 
     * characters ended by two dots..
     * @param str the string that we want to cut
     * @return the shorted string which now contains k chars
     */
    private static String shortedStr(String str) {
        String shortedStr = str;

        if (str.length() > numOfCharsToShow) {
            shortedStr = str.substring(0, numOfCharsToShow) + "..";
        }

        return shortedStr;
    }

    /**
     * Changes ShowAll Hyperlink to HideMore Hyperlink in order to close the
     * minimize the presented entities.
     *
     * @param categorypos gategory's position in the tree
     */
    private static void showAll(String categorypos) {

        // Sets all the entities of that treeItem Visible
        for (int i = 0; i < categoriesTree.getItem(Integer.parseInt(categorypos)).getChildCount(); i++) {
            categoriesTree.getItem(Integer.parseInt(categorypos)).getChild(i).setVisible(true);
        }

        categoriesTree.getItem(Integer.parseInt(categorypos))
                .getChild(categoriesTree.getItem(Integer.parseInt(categorypos))
                .getChildCount() - 1).setWidget(new HTMLPanel(
                "<a style=\"float:right;color:red;\" href="
                + "javascript:hideMore('" + categorypos + "') "
                + "id=\"hideMore\">"
                + "hide"
                + "</a><br />"));

    }

    /**
     * Changes HideMore Hyperlink to ShowAll Hyperlink in order to give the
     * opportunity to view again the collapsed entities
     *
     * @param categorypos gategory's position in the tree
     */
    public static void hideMore(String categorypos) {
        // if category does not have more than "numOfEntitiesShow" then just return
        if (categoriesTree.getItem(Integer.parseInt(categorypos)).getChildCount() <= numOfEntitiesShow) {
            return;
        }

        // Sets all the entities of that treeItem Visible
        for (int i = numOfEntitiesShow; i < categoriesTree.getItem(Integer.parseInt(categorypos)).getChildCount() - 1; i++) {
            categoriesTree.getItem(Integer.parseInt(categorypos)).getChild(i).setVisible(false);
        }

        categoriesTree.getItem(Integer.parseInt(categorypos))
                .getChild(categoriesTree.getItem(Integer.parseInt(categorypos))
                .getChildCount() - 1).setWidget(new HTMLPanel(
                "<a style=\"float:right;color:red;\" href="
                + "javascript:showAll('" + categorypos + "') "
                + "id=\"showall\">"
                + "showAll"
                + "</a><br />"));

        scrollPanel.scrollToTop();

    }

    /**
     * Updates the mining tree in case that is selected the FacetedSearchType.INTERSECTION.
     * In more details, it stores the existing tree and after that updates the already existing tree
     * depending on the list of documents that corresponds to the selected item.
     * @param selectedItemId the id of the selected item.
     * @param treeStorage the structure that stores the trees and the list of documents that correspond to the 
     * selected item.
     */
    public static void updateMiningTreeIntersectionMode(String selectedItemId, TreeStorage treeStorage){
            Tree t = new Tree();
           
            // Store the existing tree as it is
            for (int categoryPos = 0; categoryPos < XSearch.categoriesTree.getItemCount(); categoryPos++) {
                final TreeItem trItem = XSearch.categoriesTree.getItem(categoryPos);
                
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
                if(lastChildItem>0 && t.getItem(categoryPos).getChild(lastChildItem).getText().trim().equals("hide")){
                    t.getItem(categoryPos).removeItem(t.getItem(categoryPos).getChild(lastChildItem));
                    addShowAllButton(t.getItem(categoryPos), categoryPos);
                }
                t.getItem(categoryPos).setState(showTreeCollapsed);
            }                        
            treeStorage.addMiningTree(t);
            
            
            // Iterate throuth mining tree
            for (int categoryPos = 0; categoryPos < XSearch.categoriesTree.getItemCount();) {
                String categoryName = XSearch.categoriesTree.getItem(categoryPos).getText().substring(0, XSearch.categoriesTree.getItem(categoryPos).getText().lastIndexOf('('));
                TreeItem treeItem = XSearch.categoriesTree.getItem(categoryPos);                
               
                if(treeItem.getChildCount()==0){
                    return;
                }
                
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
                        HTMLPanel newEntity = createNewEntity(categoryName, entityName,  newListOfDocIdsSize, entPos, numOfPages, newListOfDocIds);
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
                    hideMore(categoryPos+"");
                }
                
                // Update CategoryName if it contains entities, otherwise remove it
                if(numOfContainedEntities==0){
                    XSearch.categoriesTree.removeItem(treeItem);
                }else{
                    treeItem.setText(categoryName + "(" + numOfContainedEntities + ")"); 
                    categoryPos++;
                }
                
                // Denote the selected item
                DOM.getElementById(selectedItemId).getStyle().setColor("red");
                treeItem.setState(showTreeCollapsed);
            }     
            
            if(XSearch.categoriesTree.getItemCount()==0){
                XSearch.categoriesTree.addItem("No entities found!!");
            }
    }
    
    /**
     * Takes as parameter an XML that contains the mined entities and creates
     * a mining tree
     * @param the XML string
     */
    private void createMiningTreeFROMXML(String XMLText) {
        //XMLText = "<list><Category><name>FAOCountry</name><entities><Entity><name>Greece</name><docIds><int>0</int><int>1</int></docIds><rank>945.0</rank></Entity><Entity><name>Germany</name><docIds><int>6</int></docIds><rank>43.0</rank></Entity><Entity><name>Cyprus</name><docIds><int>25</int><int>35</int></docIds><rank>38.0</rank></Entity></entities><num__of__different__docs>35</num__of__different__docs><rank>0.0</rank><category__representation></category__representation></Category></list>";

        // System.out.println(jsonText);	
        Document messageDom = XMLParser.parse(XMLText);

        int numOfCategories = (int) messageDom.getElementsByTagName("list").getLength();
        Window.alert("NumOf Mined categories" + numOfCategories);

        for (int categoryPos = 0; categoryPos < numOfCategories; categoryPos++) {
            Node category = messageDom.getElementsByTagName("Category").item(categoryPos);
            NodeList categoryChilds = category.getChildNodes();
            String categoryName = "";
            int numOfEntities = 0;
            int numOfDiffDocs = 0;
            double rank = 0;
            int posOfEntitiesField = 0;
            for (int i = 0; i < categoryChilds.getLength(); i++) {
                String tmp = categoryChilds.item(i).getNodeName();
                Node categoryChild = categoryChilds.item(i);
                if (tmp.equals("name")) {
                    categoryName = categoryChild.getFirstChild().getNodeValue();
                } else if (tmp.equals("entities")) {
                    numOfEntities = categoryChild.getChildNodes().getLength();
                    posOfEntitiesField = i;
                    Window.alert("Number of Entities " + numOfEntities);
                } else if (tmp.equals("num__of__different__docs")) {
                    numOfDiffDocs = Integer.parseInt(categoryChild.getFirstChild().getNodeValue());
                    Window.alert("Diff Docs: " + categoryChild.getFirstChild().getNodeValue());
                } else if (tmp.equals("rank")) {
                    rank = Double.parseDouble(categoryChild.getFirstChild().getNodeValue());
                    Window.alert("Rank: " + categoryChild.getFirstChild().getNodeValue());
                }
            }

            int numOfPages = (int) Math.ceil(((double) numOfDiffDocs) / numOfResultsPerPage);

            TreeItem treeItem = new TreeItem(categoryName + "(" + numOfEntities + ")");
            treeItem.getElement().setId("Category_" + categoryPos);
            treeItem.getElement().setClassName("categoryItemCls");
            for (int entityPos = 0; entityPos < numOfEntities; entityPos++) {
                Node entityNode = category.getChildNodes().item(posOfEntitiesField).getChildNodes().item(entityPos);
                NodeList entityNodeChildList = entityNode.getChildNodes();
                String entityName = "";
                int sizeOfDocList = 0;
                String listOfDocs = "";
                double erank = 0;
                Window.alert("For the entity at pos: " + entityPos + " has childes " + entityNodeChildList);
                for (int j = 0; j < entityNodeChildList.getLength(); j++) {
                    Node entityNodeChild = entityNodeChildList.item(j);
                    String tmp = entityNodeChild.getNodeName();
                    if (tmp.equals("name")) {
                        entityName = entityNodeChild.getFirstChild().getNodeValue();
                        Window.alert(entityName);
                    }
                    if (tmp.equals("docIds")) {
                        sizeOfDocList = entityNodeChild.getChildNodes().getLength();

                        for (int docListPos = 0; docListPos < sizeOfDocList; docListPos++) {
                            listOfDocs += entityNodeChild.getChildNodes().item(docListPos).getFirstChild().getNodeValue() + ",";
                        }
                        Window.alert(listOfDocs);
                    }
                    if (tmp.equals("rank")) {
                        erank = Double.parseDouble(entityNodeChild.getFirstChild().getNodeValue());
                        Window.alert("" + erank);
                    }
                }


                HTMLPanel newEntity = new HTMLPanel(
                        "<a title=\"" + entityName + "\" "
                        + "href=\"javascript:createResultPages2(" + numOfResultsPerPage + "," 
                                                                  + numOfPages + ",'" 
                                                                  + listOfDocs + "',' " 
                                                                  + " (" + categoryName.toUpperCase() + ") "+ entityName + "'"
                                                                  + ",'e"+categoryName+"_"+entityName +"') \""
                        + "id=\"e"
                        + categoryName+"_"+entityName + "\"> "
                        + shortedStr(entityName)
                        + "("
                        + (int) sizeOfDocList
                        + ")"
                        + "</a>");
                //newEntity.getElement().setId("entity_" + entityPos);
                treeItem.addItem(newEntity).addStyleName("entity");


                // Set invisible all the entities after the fifth
                if (entityPos > numOfEntitiesShow - 1) {
                    treeItem.getChild(entityPos).setVisible(false);
                }


            }


            if (numOfEntities > numOfEntitiesShow) {
                HTMLPanel showall = new HTMLPanel(
                        "<a style=\"float:right;color:red;\" href="
                        + "javascript:showAll('" + categoryPos + "') "
                        + "id=\"showall\"> "
                        + "showAll"
                        + "</a><br />");
                showall.addStyleName("more");
                treeItem.addItem(showall);
                treeItem.setState(showTreeCollapsed);
            }

            categoriesTree.addItem(treeItem);
            treeItem.setState(showTreeCollapsed);
        }


        // Entities label
        //miningVertPanel.add(ENTITIES);
        //ENTITIES.addStyleName("treeLabels");
        //miningVertPanel.remove(miningloading);
        //miningVertPanel.add(categoriesTree);
    }
}
