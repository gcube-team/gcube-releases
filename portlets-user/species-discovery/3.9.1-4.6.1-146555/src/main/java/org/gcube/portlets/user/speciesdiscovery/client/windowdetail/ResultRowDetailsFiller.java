package org.gcube.portlets.user.speciesdiscovery.client.windowdetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterStructuresForResultRow;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
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


public class ResultRowDetailsFiller implements DetailsFiller {

	
	/**
	 * 
	 */
	
	private TabItem tabItemDetails;
	private ToolBar toolbarOccurrences;
	private AbstractImagePrototype imgAttention = AbstractImagePrototype.create(Resources.INSTANCE.getAttention());
	private AbstractImagePrototype imageLoading = AbstractImagePrototype.create(Resources.INSTANCE.loadingBalls());
	private ContentPanel panelDetails;
	private int width = 900;
	private List<String> lastlistDataSourceFound;
	private CommonDetailComponents common;
	private String lastScientificName;

	public ResultRowDetailsFiller(TabItem tabItemDetails, ToolBar toolbarOccurrences, ContentPanel panelDetails, SearchEvent lastSearchEvent){
		this.tabItemDetails = tabItemDetails;
		this.toolbarOccurrences = toolbarOccurrences;
		this.panelDetails = panelDetails;
		this.common = new CommonDetailComponents(lastSearchEvent);
	}
	
	private void enableToolbarOccurrence(boolean bool){
		toolbarOccurrences.setEnabled(bool);
	}
	
	
	public void loadStructuresAndFillingPage(){
		
			final long startTime = System.currentTimeMillis();
		
			SpeciesDiscovery.taxonomySearchService.loadStructuresForResultRowClustering(new AsyncCallback<ClusterStructuresForResultRow>() {

				@Override
				public void onFailure(Throwable caught) {
					Info.display("Sorry", "Error retriving selected results");
					tabItemDetails.unmask();
					enableToolbarOccurrence(true);
					
				}

				@Override
				public void onSuccess(ClusterStructuresForResultRow result) {
					
//					int size = result.getResult().size();
					
					int size = result.getResultSize();

					long returnedTime = System.currentTimeMillis();
					
					long loadTime = returnedTime-startTime;
					
					Log.trace("fillPageDetailsForOccurences: " +size +" cluster result row was loaded in "+  loadTime +" msc");
					
					String items = size>0?"items":"item"; 
					
					String msg = "";
	
					if(result.getAllResultRowSize()>size){
						msg = ConstantsSpeciesDiscovery.ROW_LIMIT_REACHED + " - ";
						tabItemDetails.setIcon(imgAttention);
						tabItemDetails.setToolTip(ConstantsSpeciesDiscovery.THE_MAX_NUMBER_OF_ITEMS_DISPLAYABLE_IS+ConstantsSpeciesDiscovery.RESULT_ROW_LIMIT_ITEM_DETAILS);
					}
					
					msg+= tabItemDetails.getText() + " ("+size +" "+ items+")";
					tabItemDetails.setText(msg);

					createCommonNameDetailsPageForResultRow(result);
					
					enableToolbarOccurrence(true);
					tabItemDetails.unmask();
					
					long avilableTime = System.currentTimeMillis()-returnedTime;
					
					Log.trace("fillPageDetailsForOccurences: " +size +" view details result row are available in "+  avilableTime +" msc");
					
			}


		});
			
	}
	
	private void createCommonNameDetailsPageForResultRow(ClusterStructuresForResultRow result) {

		HashMap<String, ArrayList<String>> hashResultRowIdTables = result.getHashClusterScientificNameResultRowServiceID();
		
		
//		HashMap<String, ClusterCommonNameDataSource<ResultRow>> hashClusterCommonNamesDataSources = result.getHashClusterCommonNamesDataSources();
		
		String title = common.getSearchTitle();
		
		panelDetails.add(new Html(title));

		List<String> listKey = new ArrayList<String>(hashResultRowIdTables.keySet());
		Collections.sort(listKey);
		
		final Anchor anchorIndex = new Anchor();
		anchorIndex.setName("indexOfContents");
		
		panelDetails.add(new Html("<p style=\"font-size:12px;\"><b> Index of Contents"+anchorIndex+"</b></p><br/>"));
		panelDetails.add(common.createIndexOfContents(listKey));
		
		panelDetails.add(new Html("<hr><br/>"));
		
		//update last list data source found
		lastlistDataSourceFound = result.getListFoundDataSources();
		
		VerticalPanel verticaPanelAbouts = common.createPanelAbout(SpeciesCapability.RESULTITEM, result.getListFoundDataSources());
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
					
					SpeciesDiscovery.taxonomySearchService.loadClusterCommonNameForResultRowByScientificName(scientificName, new AsyncCallback<ClusterCommonNameDataSourceForResultRow>() {

						@Override
						public void onFailure(Throwable caught) {
							Html htmlTableResult = new Html();
							htmlTableResult.setHtml("<br/>Error on retrieving data<br/><br/>");
							
						}

						@Override
						public void onSuccess(ClusterCommonNameDataSourceForResultRow result) {
							
							
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
//			ClusterCommonNameDataSource<ResultRow> cluster = hashClusterCommonNamesDataSources.get(scientificName);


//			String tableCommonName = common.createTableWithCheckCommonNameDataSource(cluster.getHashMapCommonNameDataSources(), cluster.getListDataSourcesFound());
//			panelDetails.add(new Html("<br/>"+tableCommonName+"<br/><br/>"));
			

			ArrayList<String> arrayRowID = hashResultRowIdTables.get(scientificName);
			
//			for(int i=0; i<arrayRowID.size(); i++ ){
//				ResultRow row = result.getResultRowByRowID(arrayRowID.get(i));
//				Html table = TablesForResultRow.getTableForResultRow(row, ResultRowDataSource.getClassification(row.getParents()));
//				panelDetails.add(table);
//			}

			for(int i=0; i<arrayRowID.size(); i++ ){
				
				final LayoutContainer lcRRTables = new LayoutContainer();
				lcRRTables.setStyleAttribute("width", "99%");
//				lcRRTables.setLayout(new FitLayout());
				lcRRTables.setStyleAttribute("margin", "5px");
				lcRRTables.setStyleAttribute("padding", "5px");
				lcRRTables.setStyleAttribute("font-size", "12px");
				panelDetails.add(lcRRTables);

				final Image loading = imageLoading.createImage();
				lcRRTables.add(loading);

				String urlRequest = GWT.getModuleBaseURL() + ConstantsSpeciesDiscovery.RESULT_ROW_TABLE + "?" +"oid=" + arrayRowID.get(i);

				
			
	
				   
//				Frame frame = new Frame(urlRequest);
//				
//				frame.addDomHandler(new LoadHandler() {
//
//			        @Override
//			        public void onLoad(LoadEvent event) {
//				    	lcRRTables.remove(loading);
//				    	lcRRTables.layout();
//
//			        }
//			        
//			    }, LoadEvent.getType());
//			    
//			    lcRRTables.add(frame);
				
				
					RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlRequest);
					
					try {
						
						requestBuilder.sendRequest("", new RequestCallback() {
	
						    @Override
						    public void onResponseReceived(Request request,  Response response) {
						    	
						    	lcRRTables.remove(loading);
						    	
						    	/*
						    	String bodyString = "<BODY>";
						    	int start = response.getText().indexOf(bodyString)+bodyString.length();
						    	int end = response.getText().indexOf("</BODY>");
						    	
//						    	System.out.println("start "+start);
//						    	System.out.println("end "+end);
						    	
						    	String text="";
						    	
						    	if(start<end){
						    		text = response.getText().substring(start, end);
						    	}else
						    		text = "Sorry, an error occurred on retrieving item";

//						    	System.out.println("text "+text);*/

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
	
//					panelDetails.add(lcRRTables);

				
			}

		}
		
//		panelDetails.layout(true);
		
		panelDetails.layout();
		
	}
	
//	public static native NodeList<Node> getBodyElement(String htmlResponse) /*-{
//   		var el = document.createElement('div');
//		el.innerHTML = htmlResponse;
//		alert(el.toString());
//		return el.getElementsByTagName('BODY');
//   		
//	}-*/;
	
	
	@Override
	public List<String> getLastlistDataSourceFound() {
		return lastlistDataSourceFound;
	}
	
	@Override
	public String getLastScientificName() {
		return lastScientificName;
	}
}
