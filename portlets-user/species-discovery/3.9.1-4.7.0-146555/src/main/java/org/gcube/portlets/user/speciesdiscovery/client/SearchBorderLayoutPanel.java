package org.gcube.portlets.user.speciesdiscovery.client;


import org.gcube.portlets.user.speciesdiscovery.client.event.LoadDataSourceEvent;
import org.gcube.portlets.user.speciesdiscovery.client.job.SpeciesJobPanel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Timer;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SearchBorderLayoutPanel extends ContentPanel {

	/**
	 * This is a singleton
	 */
	private static SearchBorderLayoutPanel instance;

	private BorderLayoutData northData;
	private BorderLayoutData centerData;
	private BorderLayoutData westData;
	private BorderLayoutData southData;
	private final BorderLayout layout = new BorderLayout();

	private static SearchController searchController;
	private SpeciesSearchFormPanel speciesNorthPanel;
	private SpeciesResultsPanelCardLayout speciesCenterPanel;
	private SpeciesResultFilterAccordionPanel speciesWestPanel;

	private SpeciesJobPanel speciesSouthPanel;

	private final EventBus eventBus;


	public static final int DEFAULTNORTHHEIGHT = 77; //->33
//	public static final int DEFAULTNORTHHEIGHT = 97; //->33

	public static synchronized SearchBorderLayoutPanel getInstance() {
		if (instance == null)
			instance = new SearchBorderLayoutPanel();
		return instance;
	}


	private SearchBorderLayoutPanel() {

		setLayout(layout);
		setHeaderVisible(false);

		eventBus = new SimpleEventBus();

		searchController = new SearchController(eventBus, this);
		speciesNorthPanel = new SpeciesSearchFormPanel(eventBus);
//		speciesNorthPanel.setScrollMode(Scroll.AUTO);

		speciesSouthPanel = new SpeciesJobPanel(eventBus);

		//TODO REMOVE searchController.getStreamPagingLoader()
		speciesCenterPanel = new SpeciesResultsPanelCardLayout(eventBus, searchController.getStreamPagingLoader(), searchController);

		northData = new BorderLayoutData(LayoutRegion.NORTH, DEFAULTNORTHHEIGHT);
		northData.setCollapsible(false);
		northData.setFloatable(false);
		northData.setHideCollapseTool(true);
		northData.setSplit(false);

		westData = new BorderLayoutData(LayoutRegion.WEST, 250, 300, 350);
	    westData.setSplit(true);
	    westData.setCollapsible(true);
	    westData.setMargins(new Margins(0,0,0,0));

		centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));

		southData = new BorderLayoutData(LayoutRegion.SOUTH, 34, 34, 34);
//		southData = new BorderLayoutData(LayoutRegion.SOUTH, 150, 250, 250);
//		southData.setSplit(true);
//		southData.setCollapsible(true);

		southData.setMargins(new Margins(0,0,0,0));

		speciesWestPanel = new SpeciesResultFilterAccordionPanel(eventBus, searchController.getStreamPagingLoader());

//		eventBus.fireEvent(new LoadDataSourceEvent()); //load Data source event is fired

		add(speciesNorthPanel, northData);
		add(speciesCenterPanel, centerData);
		add(speciesWestPanel, westData);

		add(speciesSouthPanel, southData);

		initApplication();
	}


	private void initApplication(){

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				eventBus.fireEvent(new LoadDataSourceEvent()); //load Data source event is fired

				SearchController.excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, false);
				SearchController.excecuteGetJobs(SearchResultType.OCCURRENCE_POINT, false);
				SearchController.excecuteGetJobs(SearchResultType.GIS_LAYER_POINT, false);

				pollSpeciesJobs(ConstantsSpeciesDiscovery.JOBPOLLINGMILLISECONDS);

			}
		});
	}


	public static void pollSpeciesJobs(int milliseconds){

		   Timer timer = new Timer() {
           @Override
           public void run()
           {

				SearchController.excecuteGetJobs(SearchResultType.TAXONOMY_ITEM, false);
				SearchController.excecuteGetJobs(SearchResultType.OCCURRENCE_POINT, false);
				SearchController.excecuteGetJobs(SearchResultType.GIS_LAYER_POINT, false);

           }
       };

       timer.scheduleRepeating(milliseconds);
	}

	public void updateNorthSize(int height){
		northData.setSize(height);
		layout(true);
	}

	public SpeciesSearchFormPanel getSpeciesNorthPanel() {
		return speciesNorthPanel;
	}

	public SpeciesResultsPanelCardLayout getSpeciesCenterPanel() {
		return speciesCenterPanel;
	}

	public SpeciesResultFilterAccordionPanel getSpeciesWestPanel() {
		return speciesWestPanel;
	}


	public SpeciesJobPanel getSpeciesSouthPanel() {
		return speciesSouthPanel;
	}
}
