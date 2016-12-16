package org.gcube.application.datamanagementfacilityportlet.client;

import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.DataManagementFacilityService;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.DataManagementFacilityServiceAsync;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

public class DataManagementFacility implements EntryPoint {


	private static DataManagementFacility singleton;
	public static DataManagementFacilityServiceAsync localService = (DataManagementFacilityServiceAsync) GWT.create(DataManagementFacilityService.class);
	private static ServiceDefTarget localeEndpoint = (ServiceDefTarget) localService;
	
	final TabPanel mainPanel=new TabPanel();
	private static final String tourImagesFolder="tourImages/";
	
	
	
	public SubmittedRequestsGrid centerGrid;
	public ResourceGrid resourceGrid;
	public AnalysisGrid analysisGrid;
	public CustomQueryPanel  customQuery;
	
	private String sessionID="";
	
	public DataManagementFacility() {
		sessionID=Cookies.getCookie("JSESSIONID");
		DataManagementFacilityConstants.init(sessionID);
		localeEndpoint.setServiceEntryPoint(DataManagementFacilityConstants.servletUrl.get(Tags.serviceImpl)+ ";jsessionid=" + sessionID);
		
	}
	
	public static DataManagementFacility get(){
		return singleton;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	
	
	
	public void onModuleLoad() {
		singleton=this;
		
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				Log.setUncaughtExceptionHandler();
				onModuleLoad2();
			}
		});
		
		
		
		

	}
	
	public void onModuleLoad2(){
		// Session checker
		CheckSession.getInstance().startPolling();
		
		mainPanel.setSize(700, 800);
		TabItem submissionsTab=new TabItem("Table Generation");
		submissionsTab.setLayout(new FitLayout());
		submissionsTab.setIcon(Resources.ICONS.cog());
		centerGrid=new SubmittedRequestsGrid();
		submissionsTab.add(centerGrid);
		submissionsTab.addListener(Events.Select, new Listener<TabPanelEvent>(){
			@Override
			public void handleEvent(TabPanelEvent be) {
				centerGrid.loader.load();
			}
			
		});
		mainPanel.add(submissionsTab);
		TabItem tablesTab=new TabItem("Table Management");
		tablesTab.setLayout(new FitLayout());
		resourceGrid=new ResourceGrid();
		tablesTab.add(resourceGrid);		
		tablesTab.setIcon(Resources.ICONS.table());
		tablesTab.addListener(Events.Select, new Listener<TabPanelEvent>(){
			@Override
			public void handleEvent(TabPanelEvent be) {
				resourceGrid.loader.load();
			}
		});
		mainPanel.add(tablesTab);
		TabItem analysisTab=new TabItem("Performed Analysis");
		analysisTab.setLayout(new FitLayout());
		analysisGrid=new AnalysisGrid();
		analysisTab.add(analysisGrid);
		analysisTab.setIcon(Resources.ICONS.chart_analysis());
		analysisTab.addListener(Events.Select, new Listener<TabPanelEvent>(){
			@Override
			public void handleEvent(TabPanelEvent be) {
				analysisGrid.loader.load();
			}
		});
		mainPanel.add(analysisTab);
		TabItem queryTab=new TabItem("Inspector");
		queryTab.setLayout(new FitLayout());
		queryTab.add(new CustomQueryPanel());
		queryTab.setIcon(Resources.ICONS.magnifier());
		mainPanel.add(queryTab);
		
		mainPanel.setDeferredRender(true);
		
		
		


		RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV).add(mainPanel);
		
		
		Window.addWindowResizeListener(new WindowResizeListener(){

			public void onWindowResized(int width, int height) {
				Log.trace("onWindowResized width: "+width+" height: "+height);
				updateSize();
			}
		});
		updateSize();
		showGuidedTour();
	}
	
	public void updateSize()
	{
		Log.trace("Updating size..");
		RootPanel workspace = RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV);

		int topBorder = workspace.getAbsoluteTop();

		int leftBorder = workspace.getAbsoluteLeft();

		int rightScrollBar = 17;

		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer == null)?0:(footer.getOffsetHeight()-15));

		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;

		Log.trace("New  dimension Height: "+rootHeight+" Width: "+rootWidth);
		mainPanel.setHeight(rootHeight);
		mainPanel.setWidth(rootWidth);

		printData();
	}
	
	
	protected void printData()
	{
		
		Log.trace("Client Height: "+Window.getClientHeight());
		Log.trace("Client Width: "+Window.getClientWidth());

		Log.trace("Absolute Left: "+RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV).getAbsoluteLeft());
		Log.trace("Absolute Top: "+RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV).getAbsoluteTop());

		Log.trace("Offset Height: "+RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV).getOffsetHeight());
		Log.trace("Offset Width: "+RootPanel.get(DataManagementFacilityConstants.COMMONGUIDIV).getOffsetWidth());
		

	}
	
	private void showGuidedTour(){
//		GCUBEGuidedTour gt = new GCUBEGuidedTour("Dataset Managment", "org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacility","https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps", 800, 400, false,ThemeColor.BLUE,TourLanguage.EN);
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
//				"Please refere to the official wiki guide <a href=\"https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps\"> here </a> for detailed information.";
//			}
//		};
//		introStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(introStep);
//		
//		TourStep generationStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Generation";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"submitted.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "The <b>Table Generation</b> section allows <b>VRE</b> Data Managers to <b>monitor</b> and <b>submit</b> batch dataset generation. " +
//						"Every row of the grid can be expanded to show more details about a generation process. ";
//			}
//		};
//		generationStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(generationStep);
//		
//		TourStep algorithmStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Submission";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"algorithmSelection.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "The submission interface is designed to generate various resources per process. " +
//						"Multiple tables can be produced just by selecting more then an algorithm, and optionally static images and <b>GIS</b> data for each produced <b>HSPEC</b>.";
//			}
//		};
//		algorithmStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(algorithmStep);
//		
//		TourStep environmentStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Environment";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"environmentSelection.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "Since <b>AquaMaps Suite</b> exploits the advantage of <b>Cloud Computing</b> offered by the <b>gCube Infrastructure</b>, " +
//						"specifying the number and the kind of resources to allocate for the generation process is a mandatory step. " +
//						"The displayed information in this section may vary according to the status of the current <b>VRE</b>.";
//			}
//		};
//		environmentStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(environmentStep);
//		
//		TourStep monitorStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Management";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"csvImport.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "Thorugh the <b>Table Management</b> section you will be able to edit metadata associated to each datasource, " +
//						"set the defaults for <b>Map Generation</b> and <b>import</b> / <b>export</b> csv files.";
//			}
//		};
//		monitorStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(monitorStep);
//		
//		TourStep inspectorStep = new GCUBETemplate1Text1Image(false) {
//			 
//			@Override
//			public String setStepTitle() {
//				return "Inspector";
//			}
// 
//			@Override
//			public String setStepImage() {
//				return tourImagesFolder+"inspector.png";
//			}
// 
//			@Override
//			public String setStepBody() {
//				return "<b>Inspector</b> is an advanced feature which allows users to inspect tabular data by displaying the result of an arbitrary read-only <b>SQL</b> query " +
//						"(e.g. <span style=\"font-family: Courier,Courier New,arial,helvetica;\"> Select s.* from speciesoccursum as s inner join occurrencecells as o on s.speciesid=o.speciesid </span>)";
//			}
//		};
//		inspectorStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
//		gt.addStep(inspectorStep);
//		
//		gt.openTour();
	}
}
