package org.gcube.application.aquamaps.aquamapsspeciesview.client;







import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.maps.MapsResultsPanel;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.AquaMapsSpeciesViewLocalService;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.AquaMapsSpeciesViewLocalServiceAsync;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.Response;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.SettingsDescriptor;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.search.SpeciesSearchForm;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.species.SpeciesResultsPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AquaMapsSpeciesView implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	
	public static AquaMapsSpeciesViewLocalServiceAsync localService = (AquaMapsSpeciesViewLocalServiceAsync) GWT.create(AquaMapsSpeciesViewLocalService.class);
	private static AquaMapsSpeciesView singleton;
	private String sessionID = "";
	private static ServiceDefTarget localeEndpoint = (ServiceDefTarget) localService;
	
	private static final String tourImagesFolder="tourImages/";
		
	
	public AquaMapsSpeciesView(){
		sessionID = Cookies.getCookie("JSESSIONID");	
		AquaMapsSpeciesViewConstants.init(sessionID);
		localeEndpoint.setServiceEntryPoint(AquaMapsSpeciesViewConstants.servletUrl.get(Tags.localService));
	}
	public String getSessionID() {
		return sessionID;
	}
	
	public static AquaMapsSpeciesView get(){
		return singleton;
	}
	
	public void onModuleLoad(){
		
		
		singleton=this;
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				Log.setUncaughtExceptionHandler();
				onModuleLoad2();
			}
		});
	}
	
	
	public ContentPanel mainPanel;
	public SpeciesSearchForm search;
	public MapsResultsPanel maps;
	public SpeciesResultsPanel speciesResults;
	public BorderLayoutData northData=new BorderLayoutData(LayoutRegion.NORTH);
	BorderLayoutData centerData=new BorderLayoutData(LayoutRegion.CENTER);
	
	
	public void onModuleLoad2() {
		search=new SpeciesSearchForm();
		speciesResults=new SpeciesResultsPanel();
		mainPanel=new ContentPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setHeaderVisible(false);
		
		northData.setCollapsible(true);
		northData.setSize(80);
		mainPanel.add(search,northData);
		
		
		
		mainPanel.add(speciesResults,centerData);
		localService.getSessionSettings(new AsyncCallback<SettingsDescriptor>() {
		
			
			@Override
			public void onSuccess(SettingsDescriptor result) {
				search.setSearchSettings(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Session Details retrieval", caught.getMessage(), null);
				Log.error("",caught);
			}
		});
		RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV).add(mainPanel);
		
		
		Window.addWindowResizeListener(new WindowResizeListener(){

			public void onWindowResized(int width, int height) {
				Log.trace("onWindowResized width: "+width+" height: "+height);
				updateSize();
			}
		});
//		switchToMapsView();
		updateSize();
		showGuidedTour();
		
		
		
		// check Query String
		
		String mapId=Window.Location.getParameter("mapId");
		if(mapId!=null){
			//need to load map
			Log.debug("Found map Id "+mapId+", loading data..");
			mainPanel.mask("Loading map [ID : "+mapId+"]information..");
			localService.loadSpeciesByMapsId(mapId, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					mainPanel.unmask();
					MessageBox.alert("Error", "Something went wrong, either the map id is unvalid or the service is not responding.", null);					
				}

				@Override
				public void onSuccess(String result) {
					final String toShowSpeciesName=result;
					ArrayList<String> selection=new ArrayList<String>();
					selection.add(result);
					
					AquaMapsSpeciesView.get().mainPanel.mask("Harvesting data..");
					AquaMapsSpeciesView.localService.retrieveMapPerSpeciesList(selection, new AsyncCallback<Response>() {
						
						@Override
						public void onSuccess(Response result) {
							if(result.getStatus()){
								Integer count=Integer.parseInt(result.getAdditionalObjects().get(Tags.RETRIEVED_MAPS)+"");
								if(count>0){
									AquaMapsSpeciesView.get().switchToMapsView(toShowSpeciesName.toString());
								}else{
									AquaMapsSpeciesView.get().mainPanel.unmask();
									MessageBox.alert("Info", "No map available for selected species ", null);
								}							
							}else{
								AquaMapsSpeciesView.get().mainPanel.unmask();
								MessageBox.alert("Error", "Something went wrong : "+result.getAdditionalObjects().get(Tags.ERROR_MESSAGE), null);							
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AquaMapsSpeciesView.get().mainPanel.unmask();
							MessageBox.alert("Error", "Something went wrong : "+caught.getMessage(), null);
							Log.error("Unexpected error while switching view",caught);
						}
					});
				}
			});
		}
	}
	
	
	public void updateSize()
	{
		Log.debug("Updating size..");
		RootPanel workspace = RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV);

		int topBorder = workspace.getAbsoluteTop();

		int leftBorder = workspace.getAbsoluteLeft();

		int rightScrollBar = 17;

		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer == null)?0:(footer.getOffsetHeight()-15));

		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;

		Log.trace("New  dimension Height: "+rootHeight+" Width: "+rootWidth);
		mainPanel.setHeight(rootHeight);
		mainPanel.setWidth(rootWidth);
		mainPanel.layout(true);
		printData();
	}
	
	
	protected void printData()
	{
		
		Log.trace("Client Height: "+Window.getClientHeight());
		Log.trace("Client Width: "+Window.getClientWidth());

		Log.trace("Absolute Left: "+RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV).getAbsoluteLeft());
		Log.trace("Absolute Top: "+RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV).getAbsoluteTop());

		Log.trace("Offset Height: "+RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV).getOffsetHeight());
		Log.trace("Offset Width: "+RootPanel.get(AquaMapsSpeciesViewConstants.COMMONGUIDIV).getOffsetWidth());

	}
	
	
	public void switchToMapsView(String scientificName){
		mainPanel.mask("Switching to Maps Mode"); 
		search.mask("Displaying maps for species "+scientificName);
		search.advanced.collapse();
		if(maps==null)maps=new MapsResultsPanel();
		mainPanel.remove(speciesResults);
		mainPanel.add(maps,centerData);
		maps.reload();
		mainPanel.layout();
		mainPanel.unmask();
	}
	
	
	public void switchToSpeciesView(){
		mainPanel.mask("Switching to Species Mode");
		search.unmask();
		if(speciesResults==null)speciesResults=new SpeciesResultsPanel();
		mainPanel.remove(maps);
		mainPanel.add(speciesResults,centerData);
		speciesResults.reload();
		mainPanel.layout();
		mainPanel.unmask();
	}
	
	private void showGuidedTour(){
//		GCUBEGuidedTour gt = new GCUBEGuidedTour("Species View", "org.gcube.application.aquamaps.aquamapsspeciesview.client.AquaMapsSpeciesView","https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps", 800, 400, false,ThemeColor.BLUE,TourLanguage.EN);
//		TourStep introStep = new GCUBETemplate1Text1Image(true) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Introduction";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"intro.jpg";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "The AquaMaps suite supports the production of AquaMaps and related data products, i.e. compound objects containing species and biodiversity occurrence predictive maps. "+
//				"Please refere to the official wiki guide <a href=\"https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps\"> here </a> for detailed information."+
//				"The <b>Specie View</b> portlet offers functionalities to search for a particoular species within the systems and navigate through already generated maps.";
//			}
//		};
//		introStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(introStep);
//		
//		TourStep viewsStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Views";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"submitted.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "The portlet shows species information in 3 different layouts :<br/>" +
//						"<ul><li> <b>Images view</b> : Only default species thumbnail are shown, details are available by selecting the interested species.</li>" +
//						"<li> <b>Descriptive view</b> : Grid layout thumbnail preview and grouped characteristics.</li>" +
//						"<li> <b>Scientific view</b> : Basic tabular layout.</li></ul>" +
//						"Users can switch to one view by pressing the related icon on the toolbar.";
//			}
//		};
//		viewsStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(viewsStep);
//		
//		TourStep filtersStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Filtering";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"algorithmSelection.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "The Fitler section lets users filter displayed species either by generic name or by specifying advanced filters on species characteristics.";
//			}
//		};
//		filtersStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(filtersStep);
//		
//		TourStep mapsStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Maps";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"environmentSelection.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "On a selected species, users can access to available maps by pressing the <b>Switch to maps view</b> in the toolbar." +
//						"In <b>Maps View Mode</b>, information about generated maps are listed in different layouts just like as in <b>Species View Mode</b>";
//			}
//		};
//		mapsStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(mapsStep);
//		
//		
//		gt.openTour();
	}
}
