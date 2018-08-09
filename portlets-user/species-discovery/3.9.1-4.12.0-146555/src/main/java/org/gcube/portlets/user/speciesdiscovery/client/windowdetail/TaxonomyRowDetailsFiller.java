package org.gcube.portlets.user.speciesdiscovery.client.windowdetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SearchController;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.cluster.TablesForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.LightTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForTaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForTaxonomyRow;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;

public class TaxonomyRowDetailsFiller implements DetailsFiller {

	private TabItem tabItemDetails;
	private ToolBar toolbarTaxonomy;
	private AbstractImagePrototype imgAttention = AbstractImagePrototype.create(Resources.INSTANCE.getAttention());
	private AbstractImagePrototype imageLoading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls());
	private ContentPanel panelDetails;
	private int width = 900;
	private List<String> lastlistDataSourceFound;
	private CommonDetailComponents common;
	private String lastScientificName;
	private TabPanel tabPanel;
	
	public TaxonomyRowDetailsFiller(TabItem tabItemDetails, ToolBar toolbarTaxonomy, TabPanel tabPanel, ContentPanel panelDetails, SearchEvent lastSearchEvent){
		this.tabItemDetails = tabItemDetails;
		this.toolbarTaxonomy = toolbarTaxonomy;
		this.panelDetails = panelDetails;
		this.tabPanel = tabPanel;
		this.common = new CommonDetailComponents(lastSearchEvent);
	}
	

	private void enableToolbarTaxonomy(boolean bool){
		toolbarTaxonomy.setEnabled(bool);
	}
	
	public void loadStructuresAndFillingPage(){
		
		final long startTime = System.currentTimeMillis();
		
		SpeciesDiscovery.taxonomySearchService.loadStructuresForTaxonomyClustering(new AsyncCallback<ClusterStructuresForTaxonomyRow>() {

			@Override
			public void onFailure(Throwable caught) {
				Info.display("Sorry", "Error retriving selected results");
				tabItemDetails.unmask();
				enableToolbarTaxonomy(true);
				
			}

			@Override
			public void onSuccess(ClusterStructuresForTaxonomyRow result) {
				
				int size = result.getResult().size();
				
				long returnedTime = System.currentTimeMillis();
				
				long loadTime = returnedTime-startTime;

				Log.trace("fillPageDetailsForTaxonomy: " +size +" cluster taxonomy row was loaded in "+  loadTime +" msc");
				
//				TablesForTaxonomyRow tableClassification = new TablesForTaxonomyRow(result, DEFAULTLANGUAGE, tabPanel, isSearchByCommonName, searchController.getEventBus());
				
				String items = size>0?"items":"item"; 
				
				String msg = "";
				
				if(result.getTotalRow()>size){
					msg = ConstantsSpeciesDiscovery.ROW_LIMIT_REACHED + " - ";
					tabItemDetails.setIcon(imgAttention);
					tabItemDetails.setToolTip(ConstantsSpeciesDiscovery.THE_MAX_NUMBER_OF_ITEMS_DISPLAYABLE_IS+ConstantsSpeciesDiscovery.TAXONOMY_LIMIT_ITEMS_DETAILS);
				}
				
				msg+= tabItemDetails.getText() + " ("+size +" "+ items+")";
				tabItemDetails.setText(msg);
				
				
				createCommonNameDetailsPageForTaxonomyRow(result);
				enableToolbarTaxonomy(true);
				tabItemDetails.unmask();
				
				long avilableTime = returnedTime-loadTime;
				
				Log.trace("fillPageDetailsForTaxonomy: " +size +" view details taxonomy row are available in "+  avilableTime +" msc");
			}

			
		});
		
	}
	
	private void createCommonNameDetailsPageForTaxonomyRow(ClusterStructuresForTaxonomyRow result) {
		
		HashMap<String, ArrayList<String>> hashTaxonomyRowServiceID = result.getHashClusterScientificNameTaxonomyRowServiceID();
//		ArrayList<String> listDataSourceFound = tableClassification.getListFoundDataSources();
//		HashMap<String,ClusterCommonNameDataSource<TaxonomyRow>> hashClusterCommonNamesDataSources = result.getHashClusterCommonNamesDataSources();
		
//		HashMap<String, ArrayList<GxtClassificationModel>> hashHTMLTables = result.getHashClassificationTables();
		
		String title = common.getSearchTitle();
		panelDetails.add(new Html(title));
			
//		for (String dataSource : listDataSourceFound) {
//			  System.out.println("############# Data Source found " + dataSource);
//		}
		
		List<String> listKey = new ArrayList<String>(hashTaxonomyRowServiceID.keySet());
		Collections.sort(listKey);
		
		
		final Anchor anchorIndex = new Anchor();
		anchorIndex.setName("indexOfContents");
		
		panelDetails.add(new Html("<p style=\"font-size:12px;\"><b> Index of Contents"+anchorIndex+"</b></p><br/>"));
		panelDetails.add(common.createIndexOfContents(listKey));
		
		//update last list data source found
		lastlistDataSourceFound = result.getListFoundDataSources();
		
		//Create panels about
		panelDetails.add(new Html("<hr><br/>"));
		VerticalPanel verticaPanelAbouts = common.createPanelAbout(SpeciesCapability.TAXONOMYITEM, result.getListFoundDataSources());
		panelDetails.add(verticaPanelAbouts);
		panelDetails.add(new Html("<br/>"));
		
		for (final String scientificName : listKey) {
			
			Anchor anchor = new Anchor("[top index]");
			anchor.setName(scientificName);
			
			anchor.addClickHandler(new ClickHandler() {
			    public void onClick(ClickEvent event) {
			         com.google.gwt.user.client.Window.Location.assign("#indexOfContents");
			    }
			});
			
			LayoutContainer titleContainer = new LayoutContainer();
			titleContainer.setLayout(new ColumnLayout());
			titleContainer.setWidth(width-80);
			
			LayoutContainer left = new LayoutContainer();
		    FormLayout layout = new FormLayout();
		    left.setLayout(layout);
		    
		    LayoutContainer right = new LayoutContainer();
		    layout = new FormLayout();
		    right.setLayout(layout);
	

		    HorizontalPanel hp = new HorizontalPanel();
		    hp.setTableWidth("100%");
		    TableData td = new TableData();
		    td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		    hp.add(anchor, td);
		    

			titleContainer.add(left, new ColumnData(.8));
			titleContainer.add(right, new ColumnData(.2));
			
//			HorizontalPanel hp = new HorizontalPanel();
//			hp.setHorizontalAlign(HorizontalAlignment.CENTER);
//			hp.setStyleAttribute("margin", FIVEPX);
			
			this.lastScientificName = scientificName;
			
			left.add(new Html("<p style=\"margin-right:5px; font-size:14px;\"><b>" + ConstantsSpeciesDiscovery.SCIENTIFICNAME + ": " + scientificName + "</b></p>"));
			right.add(hp);
			
			panelDetails.add(titleContainer);
			panelDetails.add(new Html("<br/><br/>"));
			panelDetails.add(common.createExternalLinks(scientificName));
			
			String commonNamesTitle = "<p style=\"font-size:12px;\"><b>Common Names (Data Source/s)</b></p>";
			panelDetails.add(new Html(commonNamesTitle));
			
			final LayoutContainer lcCommonName = new LayoutContainer();
			lcCommonName.setStyleAttribute("width", "99%");
			lcCommonName.setStyleAttribute("margin", "5px");
			lcCommonName.setStyleAttribute("padding", "5px");
			lcCommonName.setStyleAttribute("font-size", "12px");
			
//			lcCommonName.setLayout(new FitLayout());
			
			final Anchor anchorCommon = new Anchor("Compare Common Names for "+scientificName);
			
			anchorCommon.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					lcCommonName.remove(anchorCommon);
					final Image loading = imageLoading.createImage();
					lcCommonName.add(loading);
					lcCommonName.layout();
					SpeciesDiscovery.taxonomySearchService.loadClusterCommonNameForTaxonomyRowByScientificName(scientificName, new AsyncCallback<ClusterCommonNameDataSourceForTaxonomyRow>() {

						@Override
						public void onFailure(Throwable caught) {
							Html htmlTableResult = new Html();
							htmlTableResult.setHtml("<br/>Error on retrieving data<br/><br/>");
							
						}

						@Override
						public void onSuccess(ClusterCommonNameDataSourceForTaxonomyRow result) {
							
							
							Html htmlTableResult = new Html();
							htmlTableResult.setHtml("<br/>Not Found<br/><br/>");

							if(result !=null){

								if(result.getCluster()!=null){
									String tableCommonName = common.createTableWithCheckCommonNameDataSource(result.getCluster().getHashMapCommonNameDataSources(), result.getCluster().getListDataSourcesFound());
									htmlTableResult.setHtml("<br/>"+tableCommonName+"<br/><br/>");
								}
								else
									htmlTableResult.setHtml("<br/>Error on retrieving data<br/><br/>");
							}else
								htmlTableResult.setHtml("<br/>Error on retrieving data<br/><br/>");
							
							
							lcCommonName.remove(loading);
							lcCommonName.add(htmlTableResult);
					
							panelDetails.layout();
						}
					
					
					});
					
					
				}
			});
			
			lcCommonName.add(anchorCommon);

			panelDetails.add(lcCommonName);

			ArrayList<String> arrayTaxonomyID = hashTaxonomyRowServiceID.get(scientificName);
			
			for(int i=0; i<arrayTaxonomyID.size(); i++ ){
				
				LightTaxonomyRow lightTaxonomy = result.getLightTaxonomyRowByKey(arrayTaxonomyID.get(i));
				
				String dataSource = lightTaxonomy.getDataProviderName();
				
				TablesForTaxonomyRow tables = new TablesForTaxonomyRow(tabPanel, SearchController.eventBus);
	
				HorizontalPanel horizontalPanel = new HorizontalPanel();
				horizontalPanel.add(new Html("<br/><p style=\"font-size:12px;\"><b> Scientific Classification of "+ scientificName + " on " +dataSource+ " Data Source</b> </p> "));
				horizontalPanel.setVerticalAlign(VerticalAlignment.BOTTOM);
				
				DataSourceModel ds = AdvancedSearchPanelManager.getInstance().findDataSourceByCapabilityAndName(SpeciesCapability.TAXONOMYITEM, dataSource);
				
				Anchor createAbout = common.createAbout(ds,true);
				if(createAbout!=null){
					createAbout.getElement().getStyle().setMarginLeft(25, Unit.PX);
					horizontalPanel.add(createAbout);
				}

				panelDetails.add(horizontalPanel);
				panelDetails.add(tables.getPanelClassificationForTaxonomy(lightTaxonomy, dataSource, false));

				panelDetails.add(new Html("<p style=\"font-size:11px; margin-top:10px;\"><b>Status and Synonyms</b></p>"));
				panelDetails.add(tables.getStatusAndSynonyms(lightTaxonomy, dataSource));

				tables.setParents(lightTaxonomy.getParents());
				
				panelDetails.add(new Html("<p style=\"font-size:11px; margin-top:10px;\"><b>Metadata</b></p>"));

				final LayoutContainer lcRRTables = new LayoutContainer();
				lcRRTables.setStyleAttribute("width", "99%");
	//				lcRRTables.setLayout(new FitLayout());
				lcRRTables.setStyleAttribute("margin", "5px");
				lcRRTables.setStyleAttribute("padding", "5px");
				lcRRTables.setStyleAttribute("font-size", "12px");
				panelDetails.add(lcRRTables);
	
				final Image loading = imageLoading.createImage();
				lcRRTables.add(loading);
	
				String urlRequest = GWT.getModuleBaseURL() + ConstantsSpeciesDiscovery.TAXONOMY_ROW_TABLE + "?" +"oid=" + arrayTaxonomyID.get(i);
				
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlRequest);
				
				try {
					requestBuilder.sendRequest("", new RequestCallback() {

					    @Override
					    public void onResponseReceived(Request request,  Response response) {
					    	lcRRTables.remove(loading);
					    	Html respHtml = new Html(response.getText());
					    	lcRRTables.add(respHtml);
					    	lcRRTables.layout();
					    }

					    @Override
					    public void onError(Request request, Throwable exception) {
					    	lcRRTables.remove(loading);
					    	lcRRTables.add(new Html("Sorry, an error occurred while contacting server, try again"));
					    }
					});
					
				} catch (RequestException e) {
					lcRRTables.remove(loading);
			    	lcRRTables.add(new Html("Sorry, an error occurred while contacting server, try again"));
				}	
			}
		}
		
		panelDetails.layout(true);
		
	}

	@Override
	public String getLastScientificName() {
		return lastScientificName;
	}

	@Override
	public List<String> getLastlistDataSourceFound() {
		return lastlistDataSourceFound;
	}
}
