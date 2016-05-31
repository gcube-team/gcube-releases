package org.gcube.application.aquamaps.aquamapsportlet.client;






import org.gcube.application.aquamaps.aquamapsportlet.client.constants.AquaMapsPortletCostants;
import org.gcube.application.aquamaps.aquamapsportlet.client.perturbation.EnvelopeGridsPanel;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletLocalService;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletLocalServiceAsync;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletRemoteService;
import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.AquaMapsPortletRemoteServiceAsync;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.SmartAreaSelectionPanel;
import org.gcube.application.aquamaps.aquamapsportlet.client.selections.SpeciesSelectionPanel;
import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate2Text2Image;
import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.ExtElement;
import com.gwtext.client.core.Function;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AquaMapsPortlet implements EntryPoint {

	/**
	 * This is the entry point method.
	 */

	public static AquaMapsPortletLocalServiceAsync localService = (AquaMapsPortletLocalServiceAsync) GWT.create(AquaMapsPortletLocalService.class);
	public static AquaMapsPortletRemoteServiceAsync remoteService= (AquaMapsPortletRemoteServiceAsync) GWT.create(AquaMapsPortletRemoteService.class);
	private static AquaMapsPortlet singleton;
	private String sessionID = "";
	private static ServiceDefTarget localeEndpoint = (ServiceDefTarget) localService;
	private static ServiceDefTarget remoteEndpoint = (ServiceDefTarget) remoteService;

	private static final String tourImagesFolder="tourImages/";

//	private VerticalPanel mainLayout = new VerticalPanel();
	public TabPanel				mainPanel; 

	public DiscoveringPanel discoveringPanel;

	public AdvancedGenerationPanel advGeneration;

	public  SpeciesSelectionPanel 	species;
	//	AreaSelectionPanel		area=new AreaSelectionPanel();

	public SmartAreaSelectionPanel area;


	public EnvelopeGridsPanel 		envelopeCustomization;



	public AquaMapsPortlet(){
		sessionID = Cookies.getCookie("JSESSIONID");		
		localeEndpoint.setServiceEntryPoint(AquaMapsPortletCostants.AquaMapsPortletLocalImplUrl+ ";jsessionid=" + sessionID);
		remoteEndpoint.setServiceEntryPoint(AquaMapsPortletCostants.AquaMapsPortletRemoteImplUrl+ ";jsessionid=" + sessionID);
	}
	public String getSessionID() {
		return sessionID;
	}

	public static AquaMapsPortlet get(){
		return singleton;
	}

	public void onModuleLoad(){

		singleton=this;
		Log.setUncaughtExceptionHandler();
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

			@Override
			public void execute() {
				Log.debug("Deferred Command");				
				onModuleLoad2();
			}
		});


	}




	public void onModuleLoad2() {
		try{
			// Session checker
			CheckSession.getInstance().startPolling();
			
			AquaMapsPortletCostants.init();
			
			mainPanel = new TabPanel(); 

			discoveringPanel=new DiscoveringPanel();

			species=new SpeciesSelectionPanel();
			//	AreaSelectionPanel		area=new AreaSelectionPanel();

			//		area = new SmartAreaSelectionPanel();


			advGeneration=new AdvancedGenerationPanel();

			envelopeCustomization=new EnvelopeGridsPanel();
			//		environmentCustomization=new EnvironmentGridsPanel();

			//*************************Create layout

			Log.debug("Starting creating layout");

			mainPanel.setWidth(AquaMapsPortletCostants.WIDTH);
			mainPanel.setHeight(AquaMapsPortletCostants.HEIGHT);
			mainPanel.setBorder(false);
			//mainPanel.setLayout(new FitLayout());  
			mainPanel.setId("AquaMainPanel");

			mainPanel.setTitle("Map Creation");
//			mainPanel.setDeferredRender(false);

			mainPanel.add(species);
			mainPanel.add(envelopeCustomization);

			mainPanel.add(advGeneration);
			mainPanel.add(discoveringPanel);


			//*************************Setting Up auto resize


			Log.debug("Panels created");			
			
			RootPanel.get(AquaMapsPortletCostants.DIVNAME).add(mainPanel);
			Log.debug("Div found, attached panel");
			
//			mainLayout.add(mainPanel);
			Log.debug("Layout created");
			
			
			Window.addResizeHandler(new ResizeHandler() {

				@Override
				public void onResize(ResizeEvent event) {
					Log.debug("onWindowResized width: "+event.getWidth()+" height: "+event.getHeight());
					updateSize();
				}
			});

			updateSize();
			mainPanel.hideTabStripItem(envelopeCustomization);
			showGuidedTour();
		}catch(Throwable t){
			Log.debug("Unable to init", t);
		}
	}


	public void updateSize()
	{
		Log.debug("Updating size..");
		RootPanel workspace = RootPanel.get(AquaMapsPortletCostants.DIVNAME);
		//RootPanel footer = RootPanel.get("gridsphere-layout-footer");

		int topBorder = workspace.getAbsoluteTop();

		int leftBorder = workspace.getAbsoluteLeft();

		int rightScrollBar = 17;

		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer == null)?0:(footer.getOffsetHeight()-15));

		int rootWidth = Window.getClientWidth() - 2* leftBorder - rightScrollBar;

		Log.debug("New  dimension Height: "+rootHeight+" Width: "+rootWidth);
		//explorer.setWidth(RootPanel.get("workspace").getOffsetWidth());
		mainPanel.setHeight(rootHeight);
		mainPanel.setWidth(rootWidth);

	}



	public void showLoading(String message, String elementId){
		try{
			final ExtElement element = Ext.get(elementId);    
			if((element!=null)&&(element.isVisible()))
				element.mask(message);
		}catch(Exception e){
			//			Log.error("Hide Loading error : ",e);
		}
	}

	public void hideLoading(String elementId){
		try{
			final ExtElement element = Ext.get(elementId);
			if((element!=null)&&(element.isMasked()))
				element.unmask();
		}catch(Exception e){
			//				Log.error("Hide Loading error : ",e);
		}
	}

	public void notifyError(String msg){
		MessageBox.alert(msg);
	}

	public void showMessage(String msg){
		MessageBox.alert(msg);
	}


	public Tool getHelpTool(final String url){
		return 	new Tool(Tool.HELP, new Function() {  
			public void execute() {  
				Window.open(url,"","");
			}  
		}, "Click to open related wiki page");
	}


	private void showGuidedTour(){
		GCUBEGuidedTour gt = new GCUBEGuidedTour("Maps Generation", AquaMapsPortlet.class.getName(),"https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps", 800, 400, false,ThemeColor.BLUE,TourLanguage.EN);
		TourStep introStep = new GCUBETemplate1Text1Image(true) {

			@Override
			public String setStepTitle() {
				return "Introduction";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"intro.jpg";
			}

			@Override
			public String setStepBody() {
				return "The AquaMaps suite supports the production of AquaMaps and related data products, i.e. compound objects containing species and biodiversity occurrence predictive maps. "+
						"Please refere to the official wiki guide <a href=\"https://gcube.wiki.gcube-system.org/gcube/index.php/AquaMaps\"> here </a> for detailed information.";
			}
		};
		introStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(introStep);

		TourStep speciesFilteringStep = new GCUBETemplate2Text2Image(false) {

			@Override
			public String setStepTitle() {
				return "Selection";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"Species_FilterPanel.png";
			}

			@Override
			public String setStepBody() {
				return "Within the <b>Species Selection</b> Section you can easily select species to use in the generation process by adding them from the <b>Search Result</b> Area to the <b>User-selected Species</b> Area. " +
						"Use the <b>Search/Filter By</b> Area to set as many filters as you whish to find the interested species among the ones managed by the application. " +
						"Action buttons at the bottom-left of the Filter Panel allows you to apply/clear setted filters.";
			}

			@Override
			public String setStepOtherBody() {
				return "You can always check the currently applied filters by activating the <b>Active Filter Summary</b> by pressing the <b>Toggle Filter Details</b> button.";
			}

			@Override
			public String setStepOtherImage() {
				return tourImagesFolder+"Species_ActiveFilterSummary.png";
			}
		};
		speciesFilteringStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(speciesFilteringStep);


		TourStep customizationStep = new GCUBETemplate2Text2Image(false) {

			@Override
			public String setStepTitle() {
				return "Customizations";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"customizeButton.png";
			}

			@Override
			public String setStepBody() {
				return "<b>AquaMaps Suite</b> offers to expert users the possibility to customize the envelope data for each selected species. " +
						"Since this is an optional step the facility is hidden by default, " +
						"however you can access it by pressing the <b>Customize species envelope</b> button below the <b>User-selected Species</b> area.";
			}

			@Override
			public String setStepOtherBody() {
				return "In addition to manual definition, the species envelope can be re-generated by either <b>Good Cells</b> recalculation or <b>Good Cells</b> selection.";
			}

			@Override
			public String setStepOtherImage() {
				return tourImagesFolder+"customization.png";
			}
		};
		customizationStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(customizationStep);



		TourStep mapsSettingsStep = new GCUBETemplate2Text2Image(false) {

			@Override
			public String setStepTitle() {
				return "Generation";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"AddToObject.png";
			}

			@Override
			public String setStepBody() {
				return "Under the <b>Maps Generation</b> section you can specify what kind of <b>AquaMaps</b> object you want to generate and which selected species they will refere to. " +
						"In case you want to generate a <b>Biodiveristy</b> object just select the interested species from the <b>Selected Species</b> area and choose <b>Create -> Biodiversity</b> or <b>Add to current Biodiversity</b>. " +
						"Otherwise for <b>Species Distribution</b> maps just select the species as above and then press <b>Create -> Species Distribution</b>.";
			}

			@Override
			public String setStepOtherBody() {
				return "Thorugh the <b>Submission</b> tab in the lower section you will be asked to type a name for your <b>Job</b>. " +
						"This area offers you the option to select a different data set (<b>HSPEC</b>) for the generation phase.";
			}

			@Override
			public String setStepOtherImage() {
				return tourImagesFolder+"submission.png";
			}
		};
		mapsSettingsStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(mapsSettingsStep);


		TourStep submittedGrid = new GCUBETemplate2Text2Image(false) {

			@Override
			public String setStepTitle() {
				return "Results";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"submitted.png";
			}

			@Override
			public String setStepBody() {
				return "In the last section you can easily keep en eye on your submitted generation processes, orderding them by different characteristics," +
						" from <b>Title</b> to <b>Completion Time</b>. The status bar will be completely green for completed generations.";
			}

			@Override
			public String setStepOtherBody() {
				return "The <b>Submitted AquaMaps Objects</b> grid also allows you to filter displayed objects by various attributes such as <b>Status</b> and <b>Type</b>." +
						" Since <b>AquaMaps Suite</b> offers facilities to let you submit lots of object generations per Job," +
						" you can monitor a Job's overall status by switching to Job view (<b>Filter by</b> -> <b>Show</b> -><b>Jobs</b>).";
			}

			@Override
			public String setStepOtherImage() {
				return tourImagesFolder+"submittedFiltering.png";
			}
		};
		submittedGrid.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(submittedGrid);

		TourStep detailsStep = new GCUBETemplate1Text1Image(false) {

			@Override
			public String setStepTitle() {
				return "Map Details";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"mapDetails.png";
			}

			@Override
			public String setStepBody() {
				return "The <b>Map Details</b> area on the left shows you more details about the selected <b>AquaMaps Object</b> (i.e. used <b>Algorithm</b> and <b>GIS</b> resources location). " +
						"The lower section shows you the thumbnail(s) of the generated static image(s), which can be expanded by clicking it. " +
						"The <b>Additional Details</b> button opens a popup showing more detailed information about species coverage (useful for <b>Biodiversity</b>) and envelope customization.";

			}
		};
		detailsStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(detailsStep);

		TourStep gisStep = new GCUBETemplate1Text1Image(false) {

			@Override
			public String setStepTitle() {
				return "GIS Viewer";
			}

			@Override
			public String setStepImage() {
				return tourImagesFolder+"gis.png";
			}

			@Override
			public String setStepBody() {
				return "<b>AquaMaps Suite</b> has the opportunity to generate GeoSpatial data against probability distributions, and you can access them by clicking the <b>GIS Viewer</b> button below the <b>Map Details</b> area. " +
						"The viewer offers you facilities to <b>zoom</b>, <b>export</b> and <b>inspect</b> rendered data, modifying the <b>overlay</b> and draw <b>transect</b> analisys against distributions.";
			}
		};
		gisStep.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		gt.addStep(gisStep);

		gt.openTour();
	}

}
