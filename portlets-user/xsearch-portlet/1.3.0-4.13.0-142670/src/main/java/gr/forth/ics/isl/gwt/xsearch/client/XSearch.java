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
package gr.forth.ics.isl.gwt.xsearch.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.FacetedSearchType;
import gr.forth.ics.isl.gwt.xsearch.client.facetedexploration.TreeStorage;
import gr.forth.ics.isl.gwt.xsearch.client.paging.Paging;
import gr.forth.ics.isl.gwt.xsearch.client.tree.clustering.ClusteringTree;
import gr.forth.ics.isl.gwt.xsearch.client.tree.metadatagroupings.MetadataGroupingsTree;
import gr.forth.ics.isl.gwt.xsearch.client.tree.mining.MiningTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;

/**
 * The entyPoint class which initializes the main Panel and handles all the GUI
 * listeners.
 *
 * @author kitsos Ioannis(kitsos@ics.forth.gr, kitsos@csd.uoc.gr)
 */
public class XSearch implements EntryPoint {

    private GCubePanel mainPanel = new GCubePanel("Semantically Enriched Results",
            "http://wiki.i-marine.eu/index.php/XSearch");
    private static FlexTable hitsTable = new FlexTable();
    public static Tree categoriesTree = new Tree();
    public static Tree clusterTree = new Tree();
    public static Tree groupingsTree = new Tree();
    private static AbsolutePanel absolutePanel = new AbsolutePanel();
    private HorizontalPanel horizontalPanel = new HorizontalPanel();
    private static Label CLUSTERS = new Label("   ");//Snippet-Based Clustering:
    private static Label ENTITIES = new Label(""); //Mined Entities:
    private static Label GROUPINGS = new Label("Metadata Groupings");
    public static HTML clear = new HTML("clear");
    private static HTML moveTop = new HTML("Move Top");
    private static ScrollPanel scrollPanel = new ScrollPanel();
    private static HorizontalPanel pagingPanel = new HorizontalPanel();
    private static VerticalPanel miningVertPanel = new VerticalPanel();
    private static VerticalPanel clusteringVertPanel = new VerticalPanel();
    private static HTML hitsLoading = new HTML("<div id=\"loading\"><center><img src=\"/xsearch-portlet/images/loading.gif\"/><br/><br/>Loading...please wait...</center><br /><br /></div>");
    private static HTML miningLoading = new HTML("<div id=\"loading\"><center><img src=\"/xsearch-portlet/images/loading_small.gif\"/><br/><br/><b>Mined Entities</b><br />Processing...Please wait...</center><br /><br /></div>");
    private static HTML clusteringLoading = new HTML("<div id=\"loading\"><center><img src=\"/xsearch-portlet/images/loading_small.gif\"/><br/><br/><b>Textual Clustering</b><br />Processing...Please wait...</center><br /><br /></div>");
    private static HTML about = new HTML("<a href=\"http://www.ics.forth.gr/isl/index_main.php?l=e&c=253\" target=\"_blank\">About..</a>");
    private static HTML bookmarklet = new HTML();
    private static Label selectedItemLabel = new Label("Selected: ");
    private static HTML selectedItem = new HTML();
    private static HorizontalPanel selectedItemPanel = new HorizontalPanel();
    public static TabPanel miningPanel = new TabPanel();
    public static TabPanel clustersPanel = new TabPanel();
    private static Widget noDetectedEntities = new HTML("No detected entities!");
    private static FlexTable pagingTable = new FlexTable();
    private static FlexTable selectedItemsTable = new FlexTable();
    private static ScrollPanel selectedItemsScroll = new ScrollPanel();
    private static HorizontalPanel infoSelectedVerticalPanel = new HorizontalPanel();
    /**
     * Number of entities to show when open a Tree
     */
    private static final int numOfEntitiesShow = 5;
    /**
     * Number of chars to show in Clusters or entities
     */
    private static int numOfCharsToShow = 12;
    /**
     * Number of chars to show for collections
     */
    private static int numOfCharsFromCollectionsToShow = 40;
    /**
     * Number of Total Results found
     */
    private static int numOfResults = 0;
    /**
     * Number of Results per Page
     */
    private static int numOfResultsPerPage = 10;
    /**
     * Number of Pages for the results
     */
    @SuppressWarnings("StaticNonFinalUsedInInitialization")
    private static int numOfPages = (int) Math.ceil(((double) numOfResults) / numOfResultsPerPage);
    /**
     * Number of Results to analyze
     */
    public static int numOfResultsToAnalyze = 50;
    /**
     * Start retrieving the results from the offset below
     */
    //public static int startOffset = 0;
    public static boolean miningNewResultsPerPage = true;
    public static boolean mergeSemanticAnalysisResults = true;
    public static int firstAnalyzedDocId = 0;
    public static int lastAnalyzedDocId = 0;
    public static int numOfResultsToConsume = 0;
    public static int totalNumOfResultsWereRequested = 0;
    private static XSearchServiceAsync xsearchSvc = GWT.create(XSearchService.class);
    public static boolean groupingMetadataEnabled = false;
    public static boolean clusteringEnabled = true;
    public static boolean miningEnabled = true;
    public static String selectedPageID = "";
    public static boolean allResultsReaded = false;
    public static FacetedSearchType explorationSearchType = FacetedSearchType.INDEPENDENT;
    public static TreeStorage treeStorage;

    /**
     * Entry point method.
     */
    @Override
    public void onModuleLoad() {
        // Associate the Main panel with the HTML host page.
        RootPanel rootPanel = RootPanel.get("ios");

        // Set style sheet XSearch.css
        hitsTable.addStyleName("hitsTable");
        scrollPanel.addStyleName("scrollPanel");
        absolutePanel.addStyleName("absolutePanel");
        mainPanel.addStyleName("mainPanel");

        // Scroll panel
        scrollPanel.setWidget(horizontalPanel);
        scrollPanelListener();

        // 
        horizontalPanel.add(miningVertPanel);
        horizontalPanel.add(hitsTable);
        horizontalPanel.add(clusteringVertPanel);

        absolutePanel.add(scrollPanel);

        // clear Selection
        //absolutePanel.add(clear);
        clear.addStyleName("clearLabel");
        clear.setVisible(false);
        clearListener();


        // Move to the top
        absolutePanel.add(moveTop);
        moveTop.addStyleName("moveTop");
        moveTop.setVisible(false);
        moveTopListener();

        // locate "about"
        absolutePanel.add(about);
        about.addStyleName("about");
        about.setVisible(false);

        absolutePanel.add(bookmarklet);
        bookmarklet.addStyleName("bookmarklet");
        bookmarklet.setVisible(true);

        // Pagging Table
        pagingPanel.add(pagingTable);
        pagingTable.addStyleName("pagingTable");
        absolutePanel.add(pagingPanel);
        pagingPanel.addStyleName("pagingPanel");

        // Locate SelectedItem..
        VerticalPanel vp = new VerticalPanel();
        vp.add(selectedItemLabel);
        vp.add(clear);
        selectedItemPanel.add(vp);
        selectedItemLabel.addStyleName("selectedItemLabel");

        //selectedItemPanel.add(selectedItem);
        selectedItemsTable.setCellSpacing(2);
        selectedItemsScroll.add(selectedItemsTable);
        selectedItemPanel.add(selectedItemsScroll);
        selectedItemsScroll.setStyleName("selectedItemsTable");
        addMouseOverandOutHandlersToWidget(selectedItemsScroll, "200px", "35px");
        //absolutePanel.add(selectedItemPanel);
        selectedItemPanel.addStyleName("selectedItemPanel");
        selectedItemPanel.setVisible(false);

        // Loading message
        absolutePanel.add(hitsLoading);
        hitsLoading.addStyleName("loading");
        hitsLoading.setVisible(true);

        mainPanel.add(absolutePanel);

        rootPanel.add(mainPanel);

        // Publish the javascript function in the window object
        publish();

        disableTreeScrollDown();

        runQuery();
    }

    /**
     *
     */
    private static void addMouseOverandOutHandlersToWidget(final Widget widget, final String onMouseOverHeight, final String onMouseOutHeight) {
        widget.addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (explorationSearchType == FacetedSearchType.UNION) {
                    widget.setHeight(onMouseOverHeight);
                }
            }
        }, MouseOverEvent.getType());
        widget.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (explorationSearchType == FacetedSearchType.UNION) {
                    widget.setHeight(onMouseOutHeight);
                }
            }
        }, MouseOutEvent.getType());
    }

    /**
     * Function that takes the hit's URI and checks for the URI's content.
     * Depending on hit's type either displays it to a new tab or it shows a
     * pop-up window that contains a set of the URLs that correspond to that
     * hit.
     *
     * @param documentURI the hit's URI
     */
    private static void viewContent(String documentURI) {
        System.out.println("docURI===>"+documentURI);
        if (documentURI == null) {
            Window.alert("No URL was found for that object!");
        } else if (documentURI.equalsIgnoreCase("null")) {
            Window.alert("No URL was found for that object!");
        } else if (!documentURI.contains("tree/")) {
            Window.open(documentURI, "_blank", "");
        } else {

            DOM.getElementById("mask").getStyle().setProperty("display", "block");

            AsyncCallback<TreeMap<String, List<String>>> callback = new AsyncCallback<TreeMap<String, List<String>>>() {
                @Override
                public void onFailure(Throwable caught) {
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");
                    Window.alert("Failed to retrieve the object's content..Please try again.");
                }

                @Override
                @SuppressWarnings("ResultOfObjectAllocationIgnored")
                public void onSuccess(TreeMap<String, List<String>> listOfURLs) {
                    //Cannot find a suitable visualizer to display the content of this object
                    if (listOfURLs != null) {
                        new URLContentViewerPopup(listOfURLs, true);
                    } else {
                        Window.alert("No content is available for this object");
                    }

                    DOM.getElementById("mask").getStyle().setProperty("display", "none");
                }
            };
            // Make the call to the XSearch service.
            xsearchSvc.getURIContent(documentURI, callback);
        }
    }

    /**
     * Run's query to search engine, get query's hits and entities/clusters
     */
    private void runQuery() {
        // Clears hitsTable, mining tree and clustering tree
        hitsTable.clear();
        categoriesTree.clear();
        clusterTree.clear();

        // Builds query answer
        getQueryHits(0, 0, -1);
    }

    /**
     * Make a request(AssyncCallback) at the server and gets back a map which
     * contains the returned hits, the query and the results of semantic
     * analysis and finally builds the presentation of them.
     *
     * @param startOffsetOfHitsRetrieve startOffsetOfHitsRetrieve of hits
     */
    public static void getQueryHits(final int startOffsetOfHitsRetrieve, final int startOffsetOfHitsAnalyze, final int numOfResultsPerformSemanticAnalysis) {
        // Initialize the service proxy.
        if (xsearchSvc == null) {
            xsearchSvc = GWT.create(XSearchService.class);
        }

        // Check if mask should be visible
        if (explorationSearchType == FacetedSearchType.INTERSECTION && (groupingMetadataEnabled && (miningEnabled || clusteringEnabled))) {
            DOM.getElementById("mask").getStyle().setProperty("display", "block");
        }

        // Set up the callback object.
        AsyncCallback<Map<String, ArrayList<String>>> callback = new AsyncCallback<Map<String, ArrayList<String>>>() {
            @Override
            public void onFailure(Throwable caught) {
                if (hitsTable.getRowCount() == 0) {
                    hitsLoading.setStyleName("hitsLoadingError");
                    hitsLoading.setHTML("Failed to retrieve the query results.. Please try again.");
                    hitsLoading.setVisible(true);
                } else {
                    Window.alert("Failed to retrieve the query results.. Please try again.");
                }

                // Make the mask invisible                
                DOM.getElementById("mask").getStyle().setProperty("display", "none");
            }

            @Override
            public void onSuccess(Map<String, ArrayList<String>> map) {

                // If map is null an error occured while perfomrming search
                if (map == null) {
                    if (hitsTable.getRowCount() == 0) {
                        hitsLoading.setStyleName("hitsLoadingError");
                        hitsLoading.setHTML("An error occurred while performing the query at the gCube Search!");
                    } else {
                        Window.alert("An error occurred while performing the query at the gCube Search!");
                    }

                    // Make the mask invisible                
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");

                    return;
                }

                // If the search is not active then 
                if (!Boolean.valueOf(map.get("isActive").get(0))) {
                    if (hitsTable.getRowCount() == 0) {
                        hitsLoading.setStyleName("hitsLoadingError");
                        hitsLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("No results were found. \n Search has not been performed or the session has expired.").toSafeHtml());
                    } else {
                        Window.alert("No results were found. \n Search has not been performed or the session has expired.");
                    }

                    // Make the mask invisible                
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");

                    return;
                }

                ArrayList<String> resutlsToShow = map.get("Results");

                // In case that no Results return show message and terminate process
                if (resutlsToShow == null || resutlsToShow.isEmpty()) {
                    if (hitsTable.getRowCount() == 0) {
                        // If is already empty then non results was found
                        hitsLoading.setStyleName("hitsLoadingError");
                        hitsLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("No results were found. \n Search has not been performed or the session has expired.").toSafeHtml());
                    } else {
                        // if already contains some of the results, it means that are requested
                        // more results that does not exist
                        Window.alert("No more results for the submitted query.");
                        allResultsReaded = true;
                        pagingTable.getCellFormatter().setVisible(0, (numOfPages + 2), false);
                    }

                    // Make the mask invisible                
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");

                    return;
                }

                String bookmarkletServURL = map.get("bookmarkletServiceUrl").get(0);
                String bookmarkletURL = "<a href=\"javascript:(function()%20{window.location='" + bookmarkletServURL + "PageMining?how=2&url='+window.location.toString()})()\" onClick=\"alert('"
                        + "Use this bookmarklet to identify and explore entities in a web page while browsing. "
                        + "Copy the link of this bookmarklet, create a new bookmark with that link, "
                        + "then visit a web page and click the bookmark "
                        + "(PDF web pages are also supported)."
                        + "');return false;\">X-Search Bookmarklet</a>";
                bookmarklet.setHTML(bookmarkletURL);

                // Hide hitsLoading message
                hitsLoading.setVisible(false);

                // Get grouping metadata Json string
                String metadataGroupingsJson = map.get("metadataGroupingsJson").get(0);
                groupingMetadataEnabled = Boolean.parseBoolean(map.get("enableMetadataGroupings").get(0));
                clusteringEnabled = Boolean.parseBoolean(map.get("clusteringEnabled").get(0));
                miningEnabled = Boolean.parseBoolean(map.get("miningEnabled").get(0));

                numOfResultsToConsume = Integer.parseInt(map.get("numOfResultsConsume").get(0));
                totalNumOfResultsWereRequested += numOfResultsToConsume;

                numOfResultsPerPage = Integer.parseInt(map.get("NumOfResultsPerPage").get(0));
                numOfResultsToAnalyze = Integer.parseInt(map.get("NumOfResultsAnalyze").get(0));

                explorationSearchType = FacetedSearchType.valueOf(map.get("explorationSearchType").get(0));
                treeStorage = new TreeStorage(explorationSearchType);

                /* if(explorationSearchType!=FacetedSearchType.UNION){
                 selectedItemsTable.setHeight("30px");
                 }else{
                 selectedItemsTable.setHeight("40px");  
                 }*/


                boolean firstTime = true;
                int row;
                if (hitsTable.getRowCount() == 0) {
                    row = 0;
                } else {
                    firstTime = false;
                    row = hitsTable.getRowCount();
                }

                // Fills results table
                for (int resultsIndex = 0; resultsIndex < resutlsToShow.size(); resultsIndex++) {

                    if (firstTime || row < numOfResultsPerPage) {
                        hitsTable.getRowFormatter().setVisible(row, true);
                    } else {
                        hitsTable.getRowFormatter().setVisible(row, false);
                    }

                    String[] data = resutlsToShow.get(resultsIndex).split("\t");
                    String main = data[0];
                    String metadata = data[1];

                    TabPanel panel = new TabPanel();
                    panel.add(new HTML(main), "Object");
                    panel.add(new HTML(metadata), "Metadata");
                    panel.selectTab(0);
                    panel.setAnimationEnabled(true);
                    panel.setStyleName("tabPanelObj");
                    //HTMLPanel dig = new HTMLPanel(resutlsToShow.get(resultsIndex));
                    //dig.setStyleName("DigitalObject");
                    hitsTable.setWidget(row++, 0, panel);
                }

                // Create Paging and Show First Page
                numOfResults = hitsTable.getRowCount();
                numOfPages = (int) Math.ceil(((double) numOfResults) / numOfResultsPerPage);

                Paging paging = new Paging(hitsTable, scrollPanel, selectedItem, selectedItemPanel, pagingTable, numOfResultsPerPage, selectedItemsTable, treeStorage);
                paging.createInitialResultPages(numOfResultsPerPage, numOfPages, firstTime);

                if (firstTime) {
                    // Set "Clear Selections" visible
                    //clear.setVisible(true);
                    about.setVisible(true);

                    if (clusteringEnabled) {
                        clusteringVertPanel.add(CLUSTERS);
                        CLUSTERS.addStyleName("treeLabels");
                        clusteringVertPanel.add(clusteringLoading);

                        clustersPanel.add(clusterTree, "Textual Clustering");
                        clustersPanel.selectTab(0);
                        clustersPanel.setAnimationEnabled(true);
                        clustersPanel.setStyleName("tabPanelClustering");
                        clusteringVertPanel.add(clustersPanel);
                    }

                    // Add metadataGrouping Tree
                    //miningVertPanel.add(new HTML("").addStyleName("emptyRow"));
                    if (groupingMetadataEnabled) {
                        clusteringVertPanel.add(GROUPINGS);
                        GROUPINGS.addStyleName("metadataLabelStyle");
                        clusteringVertPanel.add(groupingsTree);
                        /* metadataPanel.add(groupingsTree, "Metadata Groupings");
                         metadataPanel.selectTab(0);
                         metadataPanel.setAnimationEnabled(true);
                         metadataPanel.setStyleName("tabPanelMetadata");
                         clusteringVertPanel.add(metadataPanel);*/

                    }

                    // Add Cluster/Mining trees to vertical panels
                    //clusteringVertPanel.add(clusterTree);
                    if (miningEnabled) {
                        miningVertPanel.add(ENTITIES);
                        ENTITIES.addStyleName("treeLabels");
                        miningVertPanel.add(miningLoading);

                        miningPanel.add(categoriesTree, "Mined Entities");
                        miningPanel.selectTab(0);
                        miningPanel.setAnimationEnabled(true);
                        miningPanel.setStyleName("tabPanelMining");
                        miningVertPanel.add(miningPanel);
                    }

                    // Add users query to the Portlet
                    ArrayList<String> queryTerms = map.get("Query");
                    String query = queryTerms.get(0);
                    String info = "<span class='infoTtl'>Query: </span><span class='infoDt'>" + query + "</span>";


                    //HTMLPanel queryPanel = new HTMLPanel("<span class='infoTtl'>Query: </span><span class='infoDt'>" + query + "</span>");
                    //queryPanel.addStyleName("query");

                    // Add search Collections to the Portlet
                    String onlyOneCollectionString = map.get("Collections").get(0);

                    //HTMLPanel collectionsPanel = new HTMLPanel("");
                    if (onlyOneCollectionString.length() <= numOfCharsFromCollectionsToShow) {
                        info += "<br />";
                        info += "<span class='infoTtl'>In Collections:</span> <span class='infoDt'>" + onlyOneCollectionString + "</span>";
                        //collectionsPanel = new HTMLPanel("<u><b>In Collections:</b></u> " + "<a title=\"" + onlyOneCollectionString + "\" style=\"cursor: pointer;\"> " + onlyOneCollectionString + "</a>");
                    } else {
                        String part = onlyOneCollectionString.substring(0, numOfCharsFromCollectionsToShow);

                        info += "<br />";
                        info += "<span class='infoTtl'>In Collections:</span> "
                                + "<span id='partColls'>" + part
                                + "...<a href=\"#\" onClick=\"javascript:document.getElementById('partColls').style.display = 'none';document.getElementById('fullColls').style.display = 'inline';\" title=\"" + onlyOneCollectionString + "\" style=\"text-decoration:none;color:red;font-size:11px;\">(show all)</a>"
                                + "</span>"
                                + "<span id='fullColls' style=\"display:none\">" + onlyOneCollectionString
                                + "&nbsp;<a href=\"#\" onclick=\"javascript:document.getElementById('partColls').style.display = 'inline';document.getElementById('fullColls').style.display = 'none';\" title=\"" + onlyOneCollectionString + "\" style=\"text-decoration:none;color:red;font-size:11px;\">(hide)</a>"
                                + "</span>";
                        //collectionsPanel = new HTMLPanel("<u><b>In Collections:</b></u> " + "<a title=\"" + onlyOneCollectionString + "\" style=\"cursor: pointer;\"> " + onlyOneCollectionString.substring(0, numOfCharsFromCollectionsToShow) + ".." + "</a>");
                    }

                    HTMLPanel infoPanel = new HTMLPanel(info);
                    infoPanel.addStyleName("infoPanelCls");
                    infoSelectedVerticalPanel.add(infoPanel);
                    infoSelectedVerticalPanel.add(selectedItemPanel);
                    absolutePanel.add(infoSelectedVerticalPanel);
                    infoSelectedVerticalPanel.addStyleName("infoPanelHorizontal");
                    //absolutePanel.add(infoPanel);
                    //absolutePanel.add(collectionsPanel);  
                }

                if (!miningEnabled && !clusteringEnabled) {
                    DOM.getElementById("moreResults").getStyle().setVisibility(Style.Visibility.VISIBLE);
                } else {
                    DOM.getElementById("moreResults").getStyle().setVisibility(Style.Visibility.HIDDEN);
                }

                // Feel groupings tree
                if (groupingMetadataEnabled) {
                    MetadataGroupingsTree metadataGroupingTree = new MetadataGroupingsTree(groupingsTree, numOfResultsPerPage, scrollPanel);
                    groupingsTree = metadataGroupingTree.createMetadataGroupingTree(metadataGroupingsJson);
                    groupingsTree.addStyleName("entitiesTree");
                    groupingsTree.setVisible(true);
                }

                /*Prosoxh, o logos pou to semantic results kaleite mesw tou getQueryHits einai gt auta prepei na einai
                 synxrwnismena diaforetika uparxei problima sthn domh tou NKUA h opoia kanei retrieve ta apotelesmata*/
                // Call the function for semantic analysis
                if (numOfResultsPerformSemanticAnalysis == -1) {
                    getSemanticSearchResults(startOffsetOfHitsAnalyze, numOfResultsToAnalyze);
                    lastAnalyzedDocId += (numOfResultsToAnalyze - 1);
                    //Window.alert("Results analyzed are: [" + startOffsetOfHitsAnalyze +", " + (numOfResultsToAnalyze-1) + "]");
                } else {
                    getSemanticSearchResults(startOffsetOfHitsAnalyze, numOfResultsPerformSemanticAnalysis);
                }
            }
        };

        // Make the call to the XSearch service.
        xsearchSvc.getQueryResults(startOffsetOfHitsRetrieve, callback);
    }

    /**
     * Get's from Server the results of the semantic analysis and calls the
     * functions to construct the presentation of them.
     *
     * @param startOffset start offset of hits
     * @param numOfResultsAnalyze number of results to analyze
     */
    public static void getSemanticSearchResults(int startOffset, int numOfResultsAnalyze) {
        // Initialize the service proxy.
        if (xsearchSvc == null) {
            xsearchSvc = GWT.create(XSearchService.class);
        }

        // Set invisible the trees
        miningPanel.setVisible(false);
        clustersPanel.setVisible(false);
        categoriesTree.setVisible(false);
        clusterTree.setVisible(false);

        if (clusteringEnabled) {
            clusteringLoading.setVisible(true);
        }

        if (miningEnabled) {
            miningLoading.setVisible(true);
        }

        // Check if mask should be visible
        if (explorationSearchType == FacetedSearchType.INTERSECTION && (groupingMetadataEnabled && (miningEnabled || clusteringEnabled))) {
            DOM.getElementById("mask").getStyle().setProperty("display", "block");
        }

        // Set up the callback object.
        AsyncCallback<Map<String, ArrayList<String>>> callback = new AsyncCallback<Map<String, ArrayList<String>>>() {
            @Override
            public void onFailure(Throwable caught) {
                if (miningEnabled) {
                    miningLoading.addStyleName("errorMessage");
                    miningLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("An error occurred while \n performing entity mining. \n Please try again later.").toSafeHtml());
                }

                if (clusteringEnabled) {
                    clusteringLoading.addStyleName("errorMessage");
                    clusteringLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("An error occurred while \n performing textual clustering. \n Please try again later.").toSafeHtml());
                }

                // Make the mask invisible
                DOM.getElementById("mask").getStyle().setProperty("display", "none");
            }

            @Override
            public void onSuccess(Map<String, ArrayList<String>> map) {

                if (map == null) {
                    if (miningEnabled) {
                        miningLoading.addStyleName("errorMessage");
                        miningLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("An error occurred while \n performing entity mining. \n Please try again later.").toSafeHtml());
                    }

                    if (clusteringEnabled) {
                        clusteringLoading.addStyleName("errorMessage");
                        clusteringLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("An error occurred while \n performing textual clustering. \n Please try again later.").toSafeHtml());
                    }

                    // Make the mask invisible
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");
                    return;
                }

                // Check if search session has not expired 
                if (!Boolean.valueOf(map.get("isActive").get(0))) {
                    if (hitsTable.getRowCount() == 0) {
                        hitsLoading.setHTML(new SafeHtmlBuilder().appendEscapedLines("No results were found. \n Search has not been performed or the session has expired.").toSafeHtml());
                    } else {
                        Window.alert("No results were found. \n Search has not been performed or the session has expired.");
                    }

                    // Make the mask invisible
                    DOM.getElementById("mask").getStyle().setProperty("display", "none");
                    return;
                }

                DOM.getElementById("moreResults").getStyle().setVisibility(Style.Visibility.VISIBLE);

                // Hide hitsLoading message
                hitsLoading.setVisible(false);

                // Set "Clear Selections" visible
                //clear.setVisible(true);
                about.setVisible(true);

                // 
                miningNewResultsPerPage = Boolean.valueOf(map.get("MiningNewResultsPerPage").get(0));
                mergeSemanticAnalysisResults = Boolean.valueOf(map.get("MergeSemanticAnalysisResults").get(0));

                // Gets arrayList<String> that corresponds to Json results
                ArrayList<String> miningJson = map.get("Json");

                // Gives json to create Cluster tree
                if (clusteringEnabled) {
                    ClusteringTree clusteringTree = new ClusteringTree(clusterTree, scrollPanel, numOfResultsPerPage);
                    clusterTree = clusteringTree.createClusterTree(miningJson.get(0));
                    clusteringLoading.setVisible(false);
                    clustersPanel.setVisible(true);
                    clusterTree.setVisible(true);
                }

                // Gives json to create Mining tree
                if (miningEnabled) {
                    categoriesTree.remove(noDetectedEntities);
                    MiningTree miningTree = new MiningTree(categoriesTree, numOfResultsPerPage, scrollPanel);
                    categoriesTree = miningTree.createMiningTree(miningJson.get(0));
                    categoriesTree.addStyleName("entitiesTree");
                    miningLoading.setVisible(false);
                    categoriesTree.setVisible(true);
                    miningPanel.setVisible(true);

                    if (categoriesTree.getItemCount() == 0) {
                        categoriesTree.add(noDetectedEntities);
                    }
                }

                // Make the mask invisible
                DOM.getElementById("mask").getStyle().setProperty("display", "none");
            }
        };

        // Make the call to the XSearch service.
        xsearchSvc.getSemanticAnalysisResults(startOffset, numOfResultsAnalyze, callback);
    }

    /**
     * Fixing a Tree bug. When a treeItem has changed state the scroll bar goes
     * down.
     */
    private static void disableTreeScrollDown() {
        categoriesTree.addTreeListener(new TreeListener() {
            @Override
            public void onTreeItemStateChanged(TreeItem item) {
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        groupingsTree.getElement().setScrollLeft(scrollPanel.getVerticalScrollPosition());
                    }
                });
            }

            @Override
            public void onTreeItemSelected(TreeItem item) {
            }
        });

        clusterTree.addTreeListener(new TreeListener() {
            @Override
            public void onTreeItemStateChanged(TreeItem item) {
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        groupingsTree.getElement().setScrollLeft(scrollPanel.getVerticalScrollPosition());
                    }
                });
            }

            @Override
            public void onTreeItemSelected(TreeItem item) {
            }
        });

        groupingsTree.addTreeListener(new TreeListener() {
            @Override
            public void onTreeItemStateChanged(TreeItem item) {
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        groupingsTree.getElement().setScrollLeft(scrollPanel.getVerticalScrollPosition());
                    }
                });
            }

            @Override
            public void onTreeItemSelected(TreeItem item) {
            }
        });
    }

    /**
     * Adds a listener to the Scrollpanel and if the right bar is not at the top
     * shows a Link to go to the top.
     */
    private static void scrollPanelListener() {
        scrollPanel.addScrollListener(new ScrollListener() {
            @Override
            public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
                if (scrollTop != 0) {
                    moveTop.setVisible(true);
                } else {
                    moveTop.setVisible(false);
                }

            }
        });
    }

    /**
     * A function that is published as javascript function and it requests more
     * results from the gCubeSearch.
     */
    private static void moreResults() {
        // Attention this has to change depending on the methodology of keeping ranges of analyzed docIds
        // Now it works properly in case that the number of loaded results is the same with the number of 
        // analyzed results
        lastAnalyzedDocId = totalNumOfResultsWereRequested;
        getQueryHits(totalNumOfResultsWereRequested, -1, -1);

        DOM.getElementById("moreResults").getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    /**
     * Reloads all the results for that query and clears the selectedItem form
     * at the top of the screen.
     */
    private void clearListener() {
        clear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // Create Paging and Show First Page
                numOfResults = hitsTable.getRowCount();
                numOfPages = (int) Math.ceil(((double) numOfResults) / numOfResultsPerPage);

                // If intersection then load the tree which is stored first
                if (explorationSearchType == FacetedSearchType.INTERSECTION) {
                    if (miningEnabled) {
                        categoriesTree.removeItems();
                        Tree tmpTree = treeStorage.getStorageOfMiningTrees().get(0);
                        int i = 0;
                        while (tmpTree.getItemCount() != 0) {
                            categoriesTree.addItem(tmpTree.getItem(i));
                        }
                    }

                    if (clusteringEnabled) {
                        clusterTree.removeItems();
                        Tree tmpTree = treeStorage.getStorageOfClusteringTrees().get(0);
                        int i = 0;
                        while (tmpTree.getItemCount() != 0) {
                            clusterTree.addItem(tmpTree.getItem(i));
                        }
                    }

                    if (groupingMetadataEnabled) {
                        groupingsTree.removeItems();
                        Tree tmpTree = treeStorage.getStorageOfMetadataTrees().get(0);
                        int i = 0;
                        while (tmpTree.getItemCount() != 0) {
                            groupingsTree.addItem(tmpTree.getItem(i));
                        }
                    }
                }

                // Diselect every entity/cluster/metdata that is shown as selected
                String tmpId;
                Element el;
                for (int row = 0; row < selectedItemsTable.getRowCount(); row++) {
                    if (selectedItemsTable.getWidget(row, 0) == null) {
                        continue;
                    }

                    el = selectedItemsTable.getWidget(row, 0).getElement();
                    tmpId = el.getId().replace("Item:", "");
                    DOM.getElementById(tmpId).getStyle().clearColor();
                }

                // Remove all items from selected Items Table and set the clear invisible
                selectedItemsTable.removeAllRows();
                clear.setVisible(false);

                // Create a new treeStorage
                treeStorage = new TreeStorage(explorationSearchType);

                // Update with initial paging
                Paging paging = new Paging(hitsTable, scrollPanel, selectedItem, selectedItemPanel, pagingTable, numOfResultsPerPage, selectedItemsTable, treeStorage);
                paging.createInitialResultPages(numOfResultsPerPage, numOfPages, true);
            }
        });
    }

    /**
     * Adds a listener to the scroll panel which moves it to the top.
     */
    private static void moveTopListener() {
        moveTop.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollPanel.scrollToTop();
            }
        });
    }

    /**
     * (JSNI method), creates javascript functions from the specified java
     * functions.
     */
    private static native void publish() /*-{
     $wnd.moreResults = function() {
     @gr.forth.ics.isl.gwt.xsearch.client.XSearch::moreResults()();
     }
           
     $wnd.viewContent = function(docURI) {
     @gr.forth.ics.isl.gwt.xsearch.client.XSearch::viewContent(Ljava/lang/String;)(docURI);
     }
    
     $wnd.loadResults = function(id,docList,calledFromPageNum,numOfPages) {
     @gr.forth.ics.isl.gwt.xsearch.client.paging.Paging::loadResults(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)(id,docList,calledFromPageNum,numOfPages);
     }

     $wnd.showAllMetadata = function(id) {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.metadatagroupings.MetadataGroupingsTree::showAllMetadata(Ljava/lang/String;)(id);
     }
	 
     $wnd.hideMoreMetadata = function(id) {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.metadatagroupings.MetadataGroupingsTree::hideMoreMetadata(Ljava/lang/String;)(id);
     }

     $wnd.showAll = function(id) {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.mining.MiningTree::showAll(Ljava/lang/String;)(id);
     }
	 
     $wnd.hideMore = function(id) {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.mining.MiningTree::hideMore(Ljava/lang/String;)(id);
     }
     
     $wnd.showAllClusters = function() {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.clustering.ClusteringTree::showAllClusters()();
     }
	 
     $wnd.hideMoreClusters = function() {
     @gr.forth.ics.isl.gwt.xsearch.client.tree.clustering.ClusteringTree::hideMoreClusters()();
     }
	 
     $wnd.createResultPages2 = function(numOfResultsPerPage, numOfPages, doclist, selectedItem, selectedItemId){
     @gr.forth.ics.isl.gwt.xsearch.client.paging.Paging::createResultPages2(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)(numOfResultsPerPage,numOfPages,doclist,selectedItem, selectedItemId);
     }
     
     $wnd.previousPage = function(){
     @gr.forth.ics.isl.gwt.xsearch.client.paging.Paging::previousPage()();
     }
      
     $wnd.nextPage = function(){
     @gr.forth.ics.isl.gwt.xsearch.client.paging.Paging::nextPage()();
     }
         
     $wnd.showProperties = function(category, uri, id) {
     @gr.forth.ics.isl.gwt.xsearch.client.lod.EntityEnrichment::showProperties(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(category, uri, id);
     }
      
     $wnd.getEntityEnrichment = function(entityName,categoryName) {
     @gr.forth.ics.isl.gwt.xsearch.client.lod.EntityEnrichment::getEntityEnrichment(Ljava/lang/String;Ljava/lang/String;)(entityName,categoryName);
     }
	 
     }-*/;
}