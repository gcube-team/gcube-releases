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
package gr.forth.ics.isl.gwt.xsearch.client.paging;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gr.forth.ics.isl.gwt.xsearch.client.XSearch;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.FacetedSearchType;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.TreeStorage;
import gr.forth.ics.isl.gwt.xsearch.client.tree.clustering.ClusteringTree;
import gr.forth.ics.isl.gwt.xsearch.client.tree.metadatagroupings.MetadataGroupingsTree;
import gr.forth.ics.isl.gwt.xsearch.client.tree.mining.MiningTree;
import java.util.ArrayList;

/**
 * A class that implements the functionality of paging
 * that is used to show the query  results in pages
 * @author kitsos Ioannis
 */
public class Paging {

    private static FlexTable hitsTable = new FlexTable();
    private static ScrollPanel scrollPanel = new ScrollPanel();
    private static HTML selectedItem = new HTML();
    private static HorizontalPanel selectedItemPanel = new HorizontalPanel();
    private static FlexTable pagingTable = new FlexTable();
    private static int numOfItemCharsToShow = 25;
    private static FlexTable selectedItemsTable = new FlexTable();
    private static TreeStorage treeStorage;
    
    /**
     * Number of Results per Page
     */
    private static int numOfResultsPerPage = 10;

    /**
     * Constructor that is used for initialization.
     * @param hitsTable the table that contains hit's results
     * @param scrollPanel the main scroll panel contains everything
     * @param selectedItem the HTML that shows the item that is selected from user
     * @param selectedItemPanel the panel that contains the selectedItem
     * @param paging the HTML that contains the html code of the paging field, which
     *               has the information for the pages that already provided
     * @param numOfResultsPerPage maximum number of results to be presented per page     */
     
    public Paging(FlexTable hitsTable, ScrollPanel scrollPanel, HTML selectedItem, HorizontalPanel selectedItemPanel, FlexTable pagingTable, final int numOfResultsPerPage,  FlexTable selectedItemsTable, TreeStorage treeStorage) {
        this.hitsTable = hitsTable;
        this.scrollPanel = scrollPanel;
        this.selectedItem = selectedItem;
        this.selectedItemPanel = selectedItemPanel;
        this.pagingTable = pagingTable;
        this.numOfResultsPerPage = numOfResultsPerPage;
        this.selectedItemsTable = selectedItemsTable;
        this.treeStorage = treeStorage;
    }

    /**
     * Creates the paging of the total results.
     * @param numOfResultsPerPage maximum number of results that contained to each page
     * @param numOfPages number of pages to create
     */
    public static void createInitialResultPages(int numOfResultsPerPage, int numOfPages, boolean showFirstPage) {
        
        pagingTable.removeAllRows();
        pagingTable.setWidget(0, 0, new HTML("<a href=\"javascript:previousPage()\" class=\"previousPage\" title=\"Previous Page..\" id=\"previousPage\"><img src=\"/xsearch-portlet/images/previousPage.png\" ></a>"));
        try {
            String firstPageResults = "";
            int pageNum = 1;
            for (; pageNum <= numOfPages; pageNum++) {

                String hitsToShow = "";
                for (int i = ((pageNum - 1) * numOfResultsPerPage); i < (pageNum * numOfResultsPerPage); i++) {
                    hitsToShow += i + ",";
                }

                if (pageNum == 1) {
                    firstPageResults = hitsToShow;
                }
                
                if(pageNum <= 10){
                    pagingTable.setWidget(0, pageNum, new HTML("<a href=\"javascript:loadResults('page" + pageNum + "','" + hitsToShow + "'," + "'true'" + "," + numOfPages + ")\" class=\"pageNumbersFormat\" id=\"page" + pageNum + "\">" + pageNum + "</a>"));
                }else{
                    pagingTable.setWidget(0, pageNum, new HTML("<a href=\"javascript:loadResults('page" + pageNum + "','" + hitsToShow + "'," + "'true'" + "," + numOfPages + ")\" class=\"pageNumbersFormat\" id=\"page" + pageNum + "\">" + pageNum + "</a>"));
                    pagingTable.getCellFormatter().setVisible(0, pageNum, false);
                }
                
            }

            selectedItem.setText("-");
            selectedItem.setVisible(false);
            selectedItemPanel.setVisible(false);

            pagingTable.setWidget(0, (pageNum+1), new HTML("<a href=\"javascript:nextPage()\" class=\"nextPage\" title=\"Next Page..\" id=\"nextPage\"><img src=\"/xsearch-portlet/images/nextPage.png\" ></a>"));
            //pagingTable.setWidget(0, (pageNum+2), new HTML("<a href=\"javascript:moreResults()\" class=\"moreResults\" title=\"Fetch More Results..\" id=\"moreResults\">" + "&Gt;"));
            pagingTable.setWidget(0, (pageNum+2), new HTML("<a href=\"javascript:moreResults()\" class=\"moreResults\" title=\"Fetch More Results..\" id=\"moreResults\"><img src=\"/xsearch-portlet/images/moreResults.png\" ></a>"));
            
             //load the first page of results only if the first time we load the results
            if (showFirstPage) {
                loadResults("page1", firstPageResults, "false", numOfPages);
            } else {
                // Update pageNumber
                XSearch.selectedPageID = "page"+(Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())+1);
                
                //Get docList of selected page number
                Element nextPageElement = DOM.getElementById(XSearch.selectedPageID);
                String pageHTML =  nextPageElement.toString();           
                String code = pageHTML.substring((pageHTML.indexOf('('))+1, (pageHTML.indexOf(")")));    
                String docList = code.split("','")[1];
                
                // Load results and updating paging field
                loadResults(XSearch.selectedPageID, docList, "true", numOfPages);
                DOM.getElementById(XSearch.selectedPageID).addClassName("selectedPage");
                updatePagingNumberVisibility();
                updatePagingSymbolsVisibility(numOfPages);
            }


        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A function that implements the functionality of the paging button "nextPage".
     */
    public static void previousPage(){
         // Select next page number
        String nextPageId = "page"+(Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())-1);
        Element nextPageElement = DOM.getElementById(nextPageId);
        if(nextPageElement!=null){            
            // Load the results of next page
           String pageHTML =  nextPageElement.toString();           
           String code = pageHTML.substring((pageHTML.indexOf('('))+1, (pageHTML.indexOf(")")));
           String[] args = code.split("','");      
           String docList = args[1];
           int numOfpages = Integer.parseInt(args[2].split("',")[1]);
           
           loadResults(nextPageId, docList, "true", numOfpages);               
        }
    }
    
    /**
     * A function that implements the functionality of the paging button "previousPage".
     * Also, in case that there are not available results in the portlet it requests
     * more results results.
     */
    public static void nextPage(){
        // Select next page number
        String nextPageId = "page"+(Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())+1);
        Element nextPageElement = DOM.getElementById(nextPageId);
        if(nextPageElement!=null){            
            // Load the results of next page
           String pageHTML =  nextPageElement.toString();           
           String code = pageHTML.substring((pageHTML.indexOf('('))+1, (pageHTML.indexOf(")")));
           String[] args = code.split("','");      
           String docList = args[1];
           int numOfpages = Integer.parseInt(args[2].split("',")[1]);
           
           loadResults(nextPageId, docList, "true", numOfpages);         
        }else{
             // No more results were available. So ask for more..
             XSearch.getQueryHits(hitsTable.getRowCount(), XSearch.lastAnalyzedDocId, XSearch.numOfResultsToAnalyze);                    
             XSearch.lastAnalyzedDocId += XSearch.numOfResultsToAnalyze;
        }
    }
    
    /**
     * Based on the selected page id, updates the visibility of the rest of the pages.
     */
    public static void updatePagingNumberVisibility(){
           // Make each page number invisible
           int i=1;
           String tmpPageId;
           while(true){
               tmpPageId = "page"+ (i++);
               if(tmpPageId.equals(XSearch.selectedPageID)){
                   continue;
               }
               Element tmpElement = DOM.getElementById(tmpPageId);
               if(tmpElement != null){
                    //tmpElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
                    pagingTable.getCellFormatter().setVisible(0, (i-1), false);
               }else{
                   break;
               }         
           }
           
           //4 visible from left side
           int rightVissible=5;
           int nextPage = 1;
           String nextPageId;
           Element nextPageElement;
           while(rightVissible>0){
               nextPageId = "page"+(Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim()) + (nextPage));
               nextPageElement = DOM.getElementById(nextPageId);
               if(nextPageElement!=null){ 
                   //nextPageElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
                   pagingTable.getCellFormatter().setVisible(0, (Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim()) + (nextPage)), true);
               }else{
                   break;
               }
               rightVissible--;
               nextPage++;
           }
           
           //4 visible from right side
           int leftVissible=4 + rightVissible;
           int previousPage = 1;
           String prvPageId;
           Element prvPageElement;
           while(leftVissible>0){
               prvPageId = "page"+(Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim()) - (previousPage));               
               prvPageElement = DOM.getElementById(prvPageId);
               if(prvPageElement!=null){ 
                   //prvPageElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
                   pagingTable.getCellFormatter().setVisible(0, (Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim()) - (previousPage)), true);               
               }else{
                   break;
               }
               
               leftVissible--;
               previousPage++;
           }
           
           if(rightVissible == 0 && leftVissible !=0){
                //4 visible from left side
                rightVissible = leftVissible;
                nextPage = 1;
                while(rightVissible>0){
                    nextPageId = "page"+(((Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())+5) + (nextPage)));
                    nextPageElement = DOM.getElementById(nextPageId);
                    if(nextPageElement!=null){ 
                       // nextPageElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
                        pagingTable.getCellFormatter().setVisible(0, (((Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())+5) + (nextPage))), true);
                    }else{
                        break;
                    }
                    rightVissible--;
                    nextPage++;
                }
           }
    }
    
    /**
     * Checks if the item that was selected has already been chosen.
     * @param item the selected item
     * @return true if the item is already selected, otherwise false
     */
    public static boolean isItemAlreadySelected(String item){
        // Check if item is already selected
         for(int row=0; row < selectedItemsTable.getRowCount(); row++){         
            if (selectedItemsTable.getText(row, 0).isEmpty())
                 continue;
            HorizontalPanel hpaPanel = (HorizontalPanel) selectedItemsTable.getWidget(row, 0);
            HTML html = (HTML)hpaPanel.getWidget(0);
            if(html != null & html.getText().equals(item)){
                return true;
            }
         }
         
         return false;
    }
    
    /**
     * Creates the paging for the selected entity/cluster.
     *
     * @param numOfResultsPerPage number of results that contained to each page
     * @param numOfPages number of pages
     * @param docList a string with all the documents contain that
     * entity/cluster
     */
    public static void createResultPages2(int numOfResultsPerPage, int numOfPages, String docList, String item, String selectedItemId) {
        try {
            // Check if item is already selected            
            if(isItemAlreadySelected(item)){
                return;
            } 
            
            // Denote item as selected by setting the color red
            DOM.getElementById(selectedItemId).getStyle().setColor("red");
        
            // Update table with selected items and get back the list with the visible docIds
            docList = updateSelectedItemsTable(item, docList, selectedItemId);   
            
            if(XSearch.explorationSearchType != FacetedSearchType.INDEPENDENT){   
                numOfPages = (int) Math.ceil(((double) docList.split(",").length) / numOfResultsPerPage);
            }
            
            // Update the hitsTable with the presentable hits
            updatePresentableHits(docList, numOfPages);   
                                   
            // If is in intersection mode then update the enabled trees according to selected item
            if(XSearch.explorationSearchType == FacetedSearchType.INTERSECTION){
                if(XSearch.miningEnabled){
                    MiningTree.updateMiningTreeIntersectionMode(selectedItemId, treeStorage);
                }                
                
                if(XSearch.clusteringEnabled){
                    ClusteringTree.updateClusteringTreeIntersectionMode(selectedItemId, treeStorage);
                }
                
                if(XSearch.groupingMetadataEnabled){
                    MetadataGroupingsTree.updateMetadataTreeIntersectionMode(selectedItemId, treeStorage);
                }
            }

        } catch (NumberFormatException e) {
            
        }
    }
    
    /**
     * A function that adds the selected item to the table that contains the selectedItems.
     * Moreover, updates the treeStorage bean depending on the FacetedExploration mode.
     * @param item the name of the selected item.
     * @param docList the list of documents contain that item concuted with comma
     * @param selectedItemId the id of the selected item.
     * @return the docList that should be visible based on the FacetedExploration mode and the 
     * selected item that was just added.
     */
    public static String updateSelectedItemsTable(String item, String docList, final String selectedItemId){
         selectedItemPanel.setVisible(true);
        // selectedItem.setVisible(true);
         //selectedItem.setHTML("<div class='selecteditemcl' title=\""+item+"\" >"+shortedItem(item)+"</div>");
                           
                           
         // Create remove button
         HTML removeButton = new HTML("<a href=\"#\"><img src=\"/xsearch-portlet/images/remove.png\" border=\"0\" style=\"padding-left:3px; text-decoration:none\"/></a>");
         final String docListToRemove = docList;
         final int position = selectedItemsTable.getRowCount(); 
         removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
               if(XSearch.explorationSearchType == FacetedSearchType.INDEPENDENT){
                    selectedItemsTable.clearCell(0, 0);
               }else {
                   selectedItemsTable.clearCell(position, 0);
                   if(XSearch.explorationSearchType == FacetedSearchType.INTERSECTION){
                        if(position!=0){
                            selectedItemsTable.getRowFormatter().setVisible(position-1, true);
                        }else{
                            selectedItemsTable.getRowFormatter().setVisible(position, true);
                        }    
                   }else{
                       selectedItemsTable.getRowFormatter().setVisible(position, false);
                   }
                   
                   treeStorage.removeDocIdsFromListOfVisibleDocIds(docListToRemove);         
                   String hitsToShow = treeStorage.getListOfVisibleDocIdsToString();
                   if(!hitsToShow.isEmpty()){
                        int numOfResults = hitsToShow.split(",").length;
                        int numOfPages = (int) Math.ceil(((double) numOfResults) / numOfResultsPerPage);
                        updatePresentableHits(treeStorage.getListOfVisibleDocIdsToString(), numOfPages);
                   }   
                   
                   if(XSearch.explorationSearchType == FacetedSearchType.INTERSECTION){
                       if(XSearch.miningEnabled){
                            XSearch.categoriesTree.removeItems();
                            Tree tmpTree = treeStorage.removeMiningTree();

                            int i=0;
                            while(tmpTree.getItemCount() != 0){
                                XSearch.categoriesTree.addItem(tmpTree.getItem(i));
                            }                               
                       }
                       
                       if(XSearch.clusteringEnabled){
                            XSearch.clusterTree.removeItems();

                            Tree tmpTree = treeStorage.removeClusteringTree();
                            int i=0;
                            while(tmpTree.getItemCount() != 0){
                                XSearch.clusterTree.addItem(tmpTree.getItem(i));
                            }  
                       }
                                              
                       if(XSearch.groupingMetadataEnabled){
                           XSearch.groupingsTree.removeItems();
                           
                            Tree tmpTree = treeStorage.removeMetadataTree();
                            int i=0;
                            while(tmpTree.getItemCount() != 0){
                                XSearch.groupingsTree.addItem(tmpTree.getItem(i));
                            } 
                       }
                   }
               }
               
               //Check if every cell is emtpy and if yes hide the selectedItem field
               boolean isSelectedTableEmtpy = true;
               for(int row=0; row < selectedItemsTable.getRowCount(); row++){         
                    if (!selectedItemsTable.getText(row, 0).isEmpty())
                        isSelectedTableEmtpy = false;
               }
               
               
               // Check if the table with the selected items is empty and if yes then create the paging with the initial results
               if(isSelectedTableEmtpy){
                   selectedItemPanel.setVisible(false);
                   selectedItemsTable.removeAllRows();
                   XSearch.clear.setVisible(false);
                   
                   int numOfResults = hitsTable.getRowCount();
                   int numOfPages = (int) Math.ceil(((double) numOfResults) / numOfResultsPerPage);
                   
                   createInitialResultPages(numOfResultsPerPage, numOfPages, true);
               }
               
               // Diselect item by removing the color
               DOM.getElementById(selectedItemId).getStyle().clearColor();
            }
         });
         
         // Create a new Item with removal button
         HorizontalPanel hpanel = new HorizontalPanel();
         HTML selecteditem =new HTML(shortedItem(item));
         selecteditem.setTitle(item);
         hpanel.add(selecteditem);
         hpanel.add(removeButton);   
         hpanel.setStyleName("selectedItem");
         hpanel.getElement().setId("Item:"+selectedItemId);
         
         if(XSearch.explorationSearchType != FacetedSearchType.UNION){
            hpanel.getElement().getStyle().setMarginBottom(2, Style.Unit.PX);
            hpanel.getElement().getStyle().setMarginTop(4, Style.Unit.PX);
         }
               
         if(XSearch.explorationSearchType == FacetedSearchType.INDEPENDENT){                  
            // First diselect the previously selected item
             if(selectedItemsTable.getRowCount()!=0){
                Element  el = selectedItemsTable.getWidget(0, 0).getElement();
                DOM.getElementById(el.getId().replace("Item:", "")).getStyle().clearColor();
             }

             // Update the widget
             selectedItemsTable.setWidget(0, 0, hpanel);
         }else if(XSearch.explorationSearchType == FacetedSearchType.UNION){
            // Display Selected item 
            selectedItemsTable.setWidget(selectedItemsTable.getRowCount(), 0, hpanel);
            
            // Update treeStorage
            treeStorage.addDocIdsToListOfVisibleDocIds(docList);
            docList = treeStorage.getListOfVisibleDocIdsToString();
         }else if(XSearch.explorationSearchType == FacetedSearchType.INTERSECTION){
             //Display selected item
             selectedItemsTable.setWidget(selectedItemsTable.getRowCount(), 0, hpanel);
             if(selectedItemsTable.getRowCount()!=1){
                 selectedItemsTable.getRowFormatter().setVisible(selectedItemsTable.getRowCount()-2, false);
             }             
             //Update treeStorage
             treeStorage.addDocIdsToListOfVisibleDocIds(docList);
             docList =  treeStorage.getListOfVisibleDocIdsToString();
         }         
         
         // Show clear all button if more than one items are selected
         if(selectedItemsTable.getRowCount()>1){
             XSearch.clear.setVisible(true);
         }
         
         return docList;
    }
    
    /**
     * Update table with the presentable hits.
     * @param docList list of documents that have to be visible, concated with comma
     * @param numOfPages the number of pages that should be created
     */
    public static void updatePresentableHits(String docList, int numOfPages){
         // Splits docList string
            String[] docIdTable = docList.split(",");

            // Passes the values to arraylist in a structure that
            // supports contain function in order to be faster
            ArrayList<Integer> arr = new ArrayList<Integer>();
            for (int i = 0; i < docIdTable.length; i++) {
                arr.add((Integer.parseInt(docIdTable[i])));
            }

            String firstPageResults = "";
            pagingTable.removeAllRows();            
            pagingTable.setWidget(0, 0, new HTML("<a href=\"javascript:previousPage()\" class=\"previousPage\" title=\"Previous Page..\" id=\"previousPage\"><img src=\"/xsearch-portlet/images/previousPage.png\" ></a>"));
            int arrPosition = 0;
            int pageNum = 1;
            for (; pageNum <= numOfPages; pageNum++) {

                String hitsToShow = "";
                for (; arrPosition < arr.size() && arrPosition < (pageNum * numOfResultsPerPage); arrPosition++) {
                    hitsToShow += arr.get(arrPosition) + ",";
                }

                if (pageNum == 1) {
                    firstPageResults = hitsToShow;
                }

                if(pageNum <= 10){
                    pagingTable.setWidget(0, pageNum, new HTML("<a href=\"javascript:loadResults('page" + pageNum + "','" + hitsToShow + "'," + "'true'" + "," + numOfPages + ")\" class=\"pageNumbersFormat\" id=\"page" + pageNum + "\">" + pageNum + "</a>"));
                }else{
                    pagingTable.setWidget(0, pageNum, new HTML("<a href=\"javascript:loadResults('page" + pageNum + "','" + hitsToShow + "'," + "'true'" + "," + numOfPages + ")\" class=\"pageNumbersFormat\" id=\"page" + pageNum + "\">" + pageNum + "</a>"));
                    pagingTable.getCellFormatter().setVisible(0, pageNum, false);
                }
            }

            //
            //pages += "<a href=\"javascript:moreResults()\" class=\"moreResults\" title=\"Fetch more Results..\"> " + " &Gt;" + "</a>" + "&nbsp;";
            pagingTable.setWidget(0, (pageNum+1), new HTML("<a href=\"javascript:nextPage()\" class=\"nextPage\" title=\"Next Page..\" id=\"nextPage\"><img src=\"/xsearch-portlet/images/nextPage.png\" ></a>"));
            
            // load the first page of results
            loadResults("page1", firstPageResults, "false", numOfPages);
     }
    
    /**
     * Takes as input a string that contains the documents IDs that
     * have to be visible.
     *
     * @param docList the list of documents that have to be visible at result
     * page.
     * @param id the objects id
     * @param numOfPages the number of pages
     */
    public static void loadResults(String id, String docList, String calledFromPageNum, int numOfPages) {

        // Update selected page
        updateSelectedPageNumber(id, numOfPages);
        
        // Update visibility of paping numbers
        updatePagingNumberVisibility();
        
        // Update visibility of paging symbols
        updatePagingSymbolsVisibility(numOfPages);

        // Splits docList string
        String[] docIdTable = docList.split(",");

        // Passes the values to arraylist in a structure that
        // supports contain function in order to be faster
        ArrayList<Integer> arr = new ArrayList<Integer>();
        int maxDocId = 0;
        int tmpDocId;
        int minDocId = Integer.parseInt(docIdTable[0]);
        for (int i = 0; i < docIdTable.length; i++) {
            tmpDocId =(Integer.parseInt(docIdTable[i])); 
            arr.add(tmpDocId);
            if(maxDocId<tmpDocId){
                maxDocId = tmpDocId;
            }
            if(minDocId>tmpDocId){
                minDocId = tmpDocId;
            }
        }


        // Hide all documents that do not contain selected entity/cluster
        for (int row = 0; row < hitsTable.getRowCount(); row++) {
            if (arr.contains(row)) {
                // The following rows have to be visible
                hitsTable.getRowFormatter().setVisible(row, true);
            } else {
                // The following rows have to be invisible
                hitsTable.getRowFormatter().setVisible(row, false);
            }
        }

        if (!selectedItemPanel.isVisible()
            && calledFromPageNum.equals("true")
            && !((minDocId >= XSearch.firstAnalyzedDocId 
                && Integer.parseInt(docIdTable[0]) <= XSearch.lastAnalyzedDocId)
            && (maxDocId >= XSearch.firstAnalyzedDocId 
                &&  maxDocId <= XSearch.lastAnalyzedDocId))) {

            if (XSearch.miningNewResultsPerPage) {
                XSearch.getSemanticSearchResults(minDocId, numOfResultsPerPage);
                XSearch.firstAnalyzedDocId = minDocId;
                XSearch.lastAnalyzedDocId += numOfResultsPerPage;
            }else{                
                int firstHitToAnalyze= XSearch.lastAnalyzedDocId+1;
                int lastHitToAnalyze= XSearch.lastAnalyzedDocId + XSearch.numOfResultsToAnalyze;
                while(lastHitToAnalyze < maxDocId){
                    lastHitToAnalyze += XSearch.numOfResultsToAnalyze;
                }         
                int totalHitsAnalyze = (lastHitToAnalyze - XSearch.lastAnalyzedDocId);
                if(lastHitToAnalyze < hitsTable.getRowCount()){
                    //Window.alert("Analyze results: [" + firstHitToAnalyze +", " + lastHitToAnalyze + "]");
                    XSearch.getSemanticSearchResults(firstHitToAnalyze, totalHitsAnalyze);
                    XSearch.lastAnalyzedDocId += totalHitsAnalyze;
                }else{
                   // Window.alert("Requested more Results. \n Total results analyzed are: [" +(XSearch.lastAnalyzedDocId) +", " + lastHitToAnalyze + "]");
                    //Window.alert("Total hits to analyze" + totalHitsAnalyze);
                    XSearch.getQueryHits(hitsTable.getRowCount(), firstHitToAnalyze, totalHitsAnalyze);
                    XSearch.lastAnalyzedDocId += totalHitsAnalyze;
                }     
                
                // Update labels of Cluster/Mining panel with the range of analyzed hits
                //XSearch.miningPanel.setTitle("Range of analyzed hits: [" + XSearch.firstAnalyzedDocId +", "+ XSearch.lastAnalyzedDocId + "]");
                //XSearch.clustersPanel.setTitle("Range of analyzed hits: [" + XSearch.firstAnalyzedDocId +", "+ XSearch.lastAnalyzedDocId + "]");
       
            }
        }

        scrollPanel.scrollToTop();
    }
    
    /**
     * A function that updates the visibility of the paging buttons (previous page, next page).
     * @param numOfPages number of pages exist.
     */
    private static void updatePagingSymbolsVisibility(int numOfPages){
        int selectedPageNumer = Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim());
        if(selectedPageNumer == 1){
            pagingTable.getCellFormatter().setVisible(0, 0, false);
        }else{
            pagingTable.getCellFormatter().setVisible(0, 0, true);
        }
        
        int positionOfNextPage =  numOfPages+2;
        int positionofMoreResults = numOfPages+3;
        if (selectedPageNumer == numOfPages){
            
            pagingTable.getCellFormatter().setVisible(0, positionofMoreResults, false);
            
            if(selectedItemPanel.isVisible()){
                pagingTable.getCellFormatter().setVisible(0, positionOfNextPage, false);
            }else{
                if(XSearch.allResultsReaded){
                    pagingTable.getCellFormatter().setVisible(0, positionOfNextPage, false);
                }else{
                    pagingTable.getCellFormatter().setVisible(0, positionOfNextPage, true);
                }
            }
                                       
        }else{          
            pagingTable.getCellFormatter().setVisible(0, positionofMoreResults, false);
            pagingTable.getCellFormatter().setVisible(0, positionOfNextPage, true);
        }
    }

     /**
     * Takes as parameter a string and returns the first numOfCharsToShow
     * characters ended by two dots..
     *
     * @param str the string that we want to cut
     * @return a string that is shorted to contain only the first k chars
     */
    private static String shortedItem(String str) {
        String shortedStr = str;

        if (str.length() > numOfItemCharsToShow) {
            shortedStr = str.substring(0, numOfItemCharsToShow) + "..";
        }

        return shortedStr;
    }
    
    
    /**
     * Updates the selected page number.
     *
     * @param id page's id
     * @param numOfPages number of pages that are loaded
     */
    private static void updateSelectedPageNumber(String id, int numOfPages) {
        int selectedPageNumer = Integer.parseInt(id.replace("page", "").trim());
        
        //Choose a pageNumber as selected
        DOM.getElementById(id).addClassName("selectedPage");
        pagingTable.getCellFormatter().setVisible(0,selectedPageNumer , true);
        
        // Deselect previously selected page
        if (!XSearch.selectedPageID.isEmpty() && id != XSearch.selectedPageID && numOfPages >= Integer.parseInt(XSearch.selectedPageID.replace("page", "").trim())) {
            DOM.getElementById(XSearch.selectedPageID).removeClassName("selectedPage");
        }
        XSearch.selectedPageID = id;

    }
}
