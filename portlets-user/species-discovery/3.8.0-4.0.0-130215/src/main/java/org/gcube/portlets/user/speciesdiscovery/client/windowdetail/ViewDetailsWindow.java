package org.gcube.portlets.user.speciesdiscovery.client.windowdetail;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SearchController;
import org.gcube.portlets.user.speciesdiscovery.client.SpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateOccurrenceJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.CreateTaxonomyJobEvent.TaxonomyJobType;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOccurrencesMapEvent;
import org.gcube.portlets.user.speciesdiscovery.client.gridview.OccurrenceJobGridManager;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.OccurencesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.DataSourceManager;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingToolBar;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesStatus;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ViewDetailsWindow extends Window {

	protected final ViewDetailsWindow INSTANCE;
	
	protected StreamPagingLoader loader;
	protected int count = 0;
//	private int numberOfSelectedRow = 0;
	private ContentPanel container = new ContentPanel();
	private TabPanel tabPanel = new TabPanel();
	private TabItem tabItemOccrs = new TabItem(ConstantsSpeciesDiscovery.OCCURRENCEPOINTS);
	private TabItem tabItemDetails = new TabItem(ConstantsSpeciesDiscovery.DETAILS);
	private ContentPanel panelDetails = new ContentPanel();
	private ContentPanel panelOccurrences = new ContentPanel();
	
	private int width = 900;
	private int height = 600;
	private int widthPanelOccurrences = 1200;
	private int heightPanelOccurrences  = height-70;
	public final static String FIVEPX = "5px";

	
//	private SearchController searchController;
	private ToolBar toolbarOccurrences;
	private DataSourceManager dataSourceManager;

	
	private ToolBar toolbarTaxonomy;
	private boolean isSearchByCommonName = false;
	private Timer timerGetCountOfOccurrences;
	
//	private List<String> lastlistDataSourceFound = null;
	
	private DetailsFiller detailsFiller;

	private SearchEvent lastSearchEvent;

	public ViewDetailsWindow(SearchEvent lastSearchEvent) {
		
		INSTANCE = this;
		this.setCollapsible(false);
		this.setMaximizable(true);
//		this.setHideCollapseTool(true);
		this.dataSourceManager = DataSourceManager.getInstance();
		this.lastSearchEvent = lastSearchEvent;
		
		if(lastSearchEvent.getType().equals(SearchType.BY_COMMON_NAME))
			isSearchByCommonName = true;
		
		container.setHeaderVisible(false);
		container.setBodyBorder(false);
		container.setLayout(new FitLayout());

		setSize(width, height);
		setPlain(true);
		setModal(false);
		setBlinkModal(false);
		setHeading("View Details");
		setLayout(new FitLayout());
		addWindowListener(new WindowListener() {
			@Override
			public void windowHide(WindowEvent we) {

				//loader is null if items are taxonomy
				if(loader!=null)
					loader.reset();
				
				if(timerGetCountOfOccurrences!=null)
					stopTimerGetCountOfOccurrences(500);
				
			}
		});
		
		addListener(Events.Resize, new Listener<BoxComponentEvent>() {
			  public void handleEvent(BoxComponentEvent event) {
				  windowResize(INSTANCE.getWidth(), INSTANCE.getHeight());
			  }
		});
		
		tabPanel = new TabPanel();
		tabPanel.setBorders(false);
		
		if(lastSearchEvent.getResultType().getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){
			this.toolbarOccurrences = createToolbarOccurrences();
			createTabItemDisplayResultItem();
		}
		else if(lastSearchEvent.getResultType().getName().compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0){
			this.toolbarTaxonomy = createToolbarTaxonomy();
			cretateTabItemDisplayTaxonomyItem();
		}

		container.add(tabPanel,new FitData(4));
		add(container);
		
		show();

	}
	
	protected void windowResize(int width,int height){
		
		if(width>widthPanelOccurrences)
			panelOccurrences.setWidth(width-20);
		else
			panelOccurrences.setWidth(widthPanelOccurrences);
		
		if(height>heightPanelOccurrences)
			panelOccurrences.setHeight(height-70);
		else
			panelOccurrences.setHeight(heightPanelOccurrences);
		
	}
	
	
	private void cretateTabItemDisplayTaxonomyItem() {

		tabItemDetails.setScrollMode(Scroll.AUTO);
		
		ContentPanel cpDetailsTaxonomy = new ContentPanel();
		cpDetailsTaxonomy.setHeaderVisible(false);
		cpDetailsTaxonomy.setBodyBorder(false);
		cpDetailsTaxonomy.add(panelDetails);
		cpDetailsTaxonomy.setTopComponent(this.toolbarTaxonomy);
		tabItemDetails.add(cpDetailsTaxonomy);

		panelDetails.setHeaderVisible(false);
		panelDetails.setBodyBorder(false);
//		panelDetails.setStyleAttribute("padding", FIVEPX);
		
		panelDetails.setStyleAttribute("padding-left", FIVEPX);
		panelDetails.setStyleAttribute("padding-right", FIVEPX);
		panelDetails.setStyleAttribute("padding-bottom", FIVEPX);
		
		panelDetails.setStyleAttribute("margin-left", FIVEPX);
		panelDetails.setStyleAttribute("margin-right", FIVEPX);
		panelDetails.setStyleAttribute("margin-bottom", FIVEPX);
		
//		panelDetails.setTopComponent(this.toolbarTaxonomy);
		
		tabItemDetails.add(panelDetails);
		
		setFocusWidget(getButtonBar().getItem(0));

		tabItemDetails.mask("Loading...", ConstantsSpeciesDiscovery.LOADINGSTYLE);
		enableToolbarTaxonomy(false);
		
		loadStructuresAndFillingPage(SpeciesCapability.TAXONOMYITEM);
		
		tabPanel.add(tabItemDetails);
		
		
	}

	private void createTabItemDisplayResultItem() {
		
		tabItemOccrs.setToolTip(new ToolTipConfig("Show Occurrences", "Shows occurences points from selected results."));
		
		tabItemDetails.setScrollMode(Scroll.AUTO);
		
		panelDetails.setHeaderVisible(false);
		panelDetails.setBodyBorder(false);
		panelDetails.setStyleAttribute("padding", FIVEPX);
		panelDetails.setStyleAttribute("margin", FIVEPX);
		
//		panelDetails.add(createButtonSaveAsHtml());

		panelOccurrences.setHeaderVisible(false);
		panelOccurrences.setBodyBorder(false);
//		panelOccurences.setStyleAttribute("padding", FIVEPX);
		panelOccurrences.setScrollMode(Scroll.AUTO);
		panelOccurrences.setSize(widthPanelOccurrences, heightPanelOccurrences);
		panelOccurrences.setLayout(new FitLayout());
		panelOccurrences.setTopComponent(this.toolbarOccurrences);

		tabItemDetails.add(panelDetails);
		tabItemOccrs.add(panelOccurrences);

		setFocusWidget(getButtonBar().getItem(0));

		createOccurrencesWindow();
		
		tabItemDetails.mask("Loading...",ConstantsSpeciesDiscovery.LOADINGSTYLE);
		enableToolbarOccurrence(false);

//		loadNumberOfSelectedRowAndDetailsFilling(SpeciesCapability.RESULTITEM);
		
		loadStructuresAndFillingPage(SpeciesCapability.RESULTITEM);
		
		tabPanel.add(tabItemDetails);
		tabPanel.add(tabItemOccrs);
		
	}
	
	private void enableToolbarOccurrence(boolean bool){
		toolbarOccurrences.setEnabled(bool);
	}
	
	private void enableToolbarTaxonomy(boolean bool){
		toolbarTaxonomy.setEnabled(bool);
	}
	
	
	public void loadStructuresAndFillingPage(final SpeciesCapability capability){

		if(capability.getName().compareTo(SpeciesCapability.RESULTITEM.getName())==0){
			
			ResultRowDetailsFiller resultRowFiller = new ResultRowDetailsFiller(tabItemDetails, toolbarOccurrences, panelDetails, lastSearchEvent);
			resultRowFiller.loadStructuresAndFillingPage();
		
			detailsFiller = resultRowFiller;
			
		}else if(capability.getName().compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0){

			TaxonomyRowDetailsFiller taxonomyFiller = new TaxonomyRowDetailsFiller(tabItemDetails, toolbarTaxonomy, tabPanel, panelDetails, lastSearchEvent);
			taxonomyFiller.loadStructuresAndFillingPage();
			
			detailsFiller = taxonomyFiller;
		}
	}
	

	public ToolBar createToolbarTaxonomy(){
		
		ToolBar toolbar = new ToolBar();
		
		Button btnTaxonomyMenu = new Button(ConstantsSpeciesDiscovery.SAVE_TAXONOMY_ITEMS);  
		Menu formatSubMenu = new Menu();
		btnTaxonomyMenu.setMenu(formatSubMenu);  
		btnTaxonomyMenu.setScale(ButtonScale.SMALL);  
		btnTaxonomyMenu.setIconAlign(IconAlign.TOP);  
		btnTaxonomyMenu.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		btnTaxonomyMenu.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVE_TAXONOMY_ITEMS, "Save taxonomy items from selected results."));
		
//		btnOccurrencesMenu.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.TAXONOMY_ITEM, SaveFileFormat.DARWIN_CORE_ARCHIVE, count, null));
//				
//			}
//		});
		
		MenuItem darwinCoreArchiveItem = new MenuItem(ConstantsSpeciesDiscovery.DARWIN_CORE_ARCHIVE);
		darwinCoreArchiveItem.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVES_IN_DARWIN_CORE_ARCHIVE_FORMAT));
		darwinCoreArchiveItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				SearchController.eventBus.fireEvent(new CreateTaxonomyJobEvent(TaxonomyJobType.BYIDS));
				
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.TAXONOMY_ITEM, SaveFileFormat.DARWIN_CORE_ARCHIVE, count, null));			
			}
		});
		
		formatSubMenu.add(darwinCoreArchiveItem);

		toolbar.add(btnTaxonomyMenu);
		
		return toolbar;
		
		
	}
	
	public ToolBar createToolbarOccurrences(){
		
		ToolBar toolbar = new ToolBar();

		Button btnShowInGisViewer = new Button(ConstantsSpeciesDiscovery.SHOW_IN_GIS_VIEWER);
		btnShowInGisViewer.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getGisProducts()));
		btnShowInGisViewer.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SHOW_IN_GIS_VIEWER, "Show occurences points from selected results in a Gis Viewer Map."));
		btnShowInGisViewer.setScale(ButtonScale.SMALL);  
		btnShowInGisViewer.setIconAlign(IconAlign.TOP);  
		btnShowInGisViewer.setArrowAlign(ButtonArrowAlign.BOTTOM); 
		btnShowInGisViewer.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				SearchController.eventBus.fireEvent(new ShowOccurrencesMapEvent(count));
			}
		});
		
		
		Button btnOccurrencesMenu = new Button(ConstantsSpeciesDiscovery.SAVE_OCCURRENCES);  
		Menu formatSubMenu = new Menu();
		btnOccurrencesMenu.setMenu(formatSubMenu);  
		btnOccurrencesMenu.setScale(ButtonScale.SMALL);  
		btnOccurrencesMenu.setIconAlign(IconAlign.TOP);  
		btnOccurrencesMenu.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		btnOccurrencesMenu.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVE_OCCURRENCES, ConstantsSpeciesDiscovery.SAVE_OCCURENCES_POINTS_FROM_SELECTED_RESULTS));

		MenuItem csvFormatItem = new MenuItem(ConstantsSpeciesDiscovery.CSV);
		csvFormatItem.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVES_IN_CSV_FILE_FORMAT));
		csvFormatItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD));	
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD, detailsFiller.getLastlistDataSourceFound(), searchTerm,false));	
			}
		});
		
		
		formatSubMenu.add(csvFormatItem);
		
		Menu csvTypeMenu = new Menu();  
	    
	    MenuItem csvStandard = new MenuItem(ConstantsSpeciesDiscovery.PLAIN_CSV);
	    
	    csvStandard.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD));	
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD, detailsFiller.getLastlistDataSourceFound(), searchTerm,false));
			}
		});
	    
	    
	    MenuItem csvStandardByDataSource = new MenuItem(ConstantsSpeciesDiscovery.PLAIN_CSV_BY_DATA_SOURCE);
	    
	    csvStandardByDataSource.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD));	
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.CSV, count, OccurrencesSaveEnum.STANDARD, detailsFiller.getLastlistDataSourceFound(), searchTerm,true));
			}
		});
	    
	    
	    MenuItem csvOpenModeller = new MenuItem(ConstantsSpeciesDiscovery.OPEN_MODELLER);
	    
	    csvOpenModeller.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.CSV, count, OccurrencesSaveEnum.OPENMODELLER));		
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.CSV, count, OccurrencesSaveEnum.OPENMODELLER, detailsFiller.getLastlistDataSourceFound(), searchTerm,false));
			}
		});
	    
	    
	    MenuItem csvOpenModellerByDataSource = new MenuItem(ConstantsSpeciesDiscovery.OPEN_MODELLER_BY_DATA_SOURCE);
	    
	    csvOpenModellerByDataSource.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.CSV, count, OccurrencesSaveEnum.OPENMODELLER));		
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.CSV, count, OccurrencesSaveEnum.OPENMODELLER, detailsFiller.getLastlistDataSourceFound(), searchTerm,true));
			}
		});
	    
	    csvTypeMenu.add(csvStandard);  
	    
	    csvTypeMenu.add(csvStandardByDataSource);
	    
	    csvTypeMenu.add(csvOpenModeller); 
	    
	    csvTypeMenu.add(csvOpenModellerByDataSource);
	    
	    csvFormatItem.setSubMenu(csvTypeMenu); 
		
		MenuItem darwinCoreFormatItem = new MenuItem(ConstantsSpeciesDiscovery.DARWIN_CORE);
		darwinCoreFormatItem.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVES_IN_DARWIN_CORE_FILE_FORMAT));
		darwinCoreFormatItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
//				searchController.getEventBus().fireEvent(new SaveItemsEvent(SearchResultType.OCCURRENCE_POINT, SaveFileFormat.DARWIN_CORE, count, null));			
				
				String searchTerm = OccurrenceJobGridManager.getSearchTermBySearchType(isSearchByCommonName, lastSearchEvent.getSearchTerm());
				
				SearchController.eventBus.fireEvent(new CreateOccurrenceJobEvent(SaveFileFormat.DARWIN_CORE, count, null, detailsFiller.getLastlistDataSourceFound(), searchTerm, false));
			}
		});
		formatSubMenu.add(darwinCoreFormatItem);
		
		toolbar.add(btnOccurrencesMenu);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(btnShowInGisViewer);
		return toolbar;
	}
	
	
	protected void initLoader()
	{
		loader = new StreamPagingLoader(ConstantsSpeciesDiscovery.PAGE_SIZE);
		
//		loader = new StreamPagingLoader(PAGE_SIZE);
		loader.setDataSource(dataSourceManager.getDataSourceByResultType(SpeciesCapability.OCCURRENCESPOINTS));

	}
	

	public void createOccurrencesWindow()
	{
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		
		for (GridField field:OccurencesGridFields.values()) config.add(Util.createColumnConfig(field, 150));
		
		final ColumnModel cm = new ColumnModel(config);
		
		initLoader();
		
		Grid<ModelData> grid = new Grid<ModelData>(loader.getStore(), cm);  
		grid.setBorders(true);
		grid.getView().setEmptyText(ConstantsSpeciesDiscovery.NORESULTS);

		StreamPagingToolBar toolBar = new StreamPagingToolBar();
		toolBar.bind(loader);
		
		panelOccurrences.setBottomComponent(toolBar);

		panelOccurrences.add(grid);  
	}

	public void loadOccurences()
	{
		Log.trace("Loading occurrences");

		count = 0;
		dataSourceManager.setExpectedOccurencePoints(count);

		SpeciesDiscovery.taxonomySearchService.retrieveOccurencesFromSelection(new AsyncCallback<Integer>() {
			
			@Override
			public void onSuccess(Integer expectedPoints) {
				Log.trace("Expected points: "+expectedPoints);
				dataSourceManager.setExpectedOccurencePoints(expectedPoints);
				
				count = expectedPoints.intValue();
				String items = count>0?"items":"item"; 
				tabItemOccrs.setText(tabItemOccrs.getText() + " ("+count +" "+ items+")");
	
				dataSourceManager.setExpectedOccurencePoints(count);
//				count = expectedPoints;
				loader.startLoading(true);
				
				pollingGetCountOfOcccurrences(count);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Error getting occurrences", "Error getting occurrences, retry");	
				Log.trace("Error getting occurrences", caught);
			}
		});
	}
	
	
	private void pollingGetCountOfOcccurrences(final int allItems){
		
		toolbarOccurrences.add(new SeparatorToolItem());
		final Label labelLoading = new Label("loading 0 of "+allItems);
		labelLoading.setStyleAttribute("paddingLeft", "20px");
		labelLoading.setData("count", new Integer(0));
		toolbarOccurrences.add(labelLoading);
		
		timerGetCountOfOccurrences = new Timer() {
		      @Override
		      public void run() {
		    	  
		  		SpeciesDiscovery.taxonomySearchService.getCountOfOccurrencesBatch(new AsyncCallback<OccurrencesStatus>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.error("Error on loading", "An error occurred on count of occurrence point, retry." +caught.getMessage());
						
					}

					@Override
					public void onSuccess(OccurrencesStatus result) {
						
						int currentValue = ((Integer) labelLoading.getData("count")).intValue();
						
						
						if(result.getSize()>currentValue && result.getSize()<allItems)
							labelLoading.setText("loading "+result.getSize()+ " of " + allItems + " and counting...");
					

						if(result.getSize()==allItems){
							setLabelLoadedText(result.getSize(), allItems);
							stopTimerGetCountOfOccurrences(1000);
						}
						
						//case stream completed
						if(result.isResultEOF()){
							Log.trace("EOF of occurrences is true");
							setLabelLoadedText(result.getSize(), allItems);
							stopTimerGetCountOfOccurrences(2000);
						}
						
						labelLoading.setData("count", result.getSize());
						
						Log.trace("loading " + result.getSize() + " occurrences" );
						
						toolbarOccurrences.layout();
					}

					private void setLabelLoadedText(int size, int allItems) {
						
						String msg = "loaded "+size+ " of " + allItems;
						
						if(size<allItems){
							int difference = allItems - size;
							msg+= " (" + difference + " duplicate or not available occurrence points were discarded)";
						}
							
						labelLoading.setText(msg);
						
					}
					
				});

		      }
		    };

		// Schedule the timer to run once in 1 seconds.
		timerGetCountOfOccurrences.scheduleRepeating(ConstantsSpeciesDiscovery.SCHEDULE_MILLIS_COUNT_OCCURRENCES);
		
//		timerGetCountOfOccurrences.run();
		Log.trace("run timerGetCountOfOccurrences");
		
	}
	
	private void stopTimerGetCountOfOccurrences(int delay) {
		
		Log.trace("stop timerGetCountOfOccurrences");
		Timer timer = new Timer() {
			
			@Override
			public void run() {
//				System.out.println("cancel cancel cancel");
				timerGetCountOfOccurrences.cancel();
				
			}
		};
		timer.schedule(delay);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onHide() {
		super.onHide();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				
				if(loader!=null){
					loader.reset();
					SpeciesDiscovery.taxonomySearchService.stopRetrievingOccurrences(new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.error("An error occurred in stopRetrievingOccurrences: " + caught);
							
						}

						@Override
						public void onSuccess(Void result) {
							Log.trace("Stop retrieving Occurrences: OK");
							
						}
					});
				}
				
			}
		});
	}
	
	
	
	
	//TEST
	public Button createButtonSaveAsHtml(){
		
		ToolBar toolbar = new ToolBar();

		Button btnExportAsHtml = new Button("Export As Html");
		btnExportAsHtml.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts()));
		btnExportAsHtml.setToolTip(new ToolTipConfig("Show in Gis Viewer", "Show occurences points from selected results in a Gis Viewer Map."));
		btnExportAsHtml.setScale(ButtonScale.SMALL);  
		btnExportAsHtml.setIconAlign(IconAlign.TOP);  
		btnExportAsHtml.setArrowAlign(ButtonArrowAlign.BOTTOM); 
		btnExportAsHtml.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
//				searchController.getEventBus().fireEvent(new ShowOccurrencesMapEvent());
				
				String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"> <html>" +
						"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">" +
						"<style type=\"text/css\">" +getCssStyles() +
						"</style>" +
						"<title>"+lastSearchEvent.getSearchTerm()+"</title>" +
						"</head>" +
						"	<body>"+panelDetails.getElement().getInnerHTML()+"</body>" +
						"</html> ";
				
//				System.out.println(html);
			}
		});

		return btnExportAsHtml;

	}
	
	
	private String getCssStyles(){

		return ".button-hyperlink .x-btn-text {" +
				"cursor: pointer !important;" +
				"cursor: hand !important;" +
				"border: none !important;" +
				"/* Disable the button-style */" +
				"background-color: transparent !important;" +
				"background: none !important;" +
				"background-image: none !important;" +
				"padding: 0px !important;" +
				"color: #4784C3 !important;" +
				"font-size: 10px;" +
				"font-family: Serif, \"Times New Roman\", Georgia,;" +
				"text-decoration: underline !important;" +
				"}" +
				".button-hyperlink .x-btn-tl,.button-hyperlink.x-btn-tr,.button-hyperlink " +
				".x-btn-tc,.button-hyperlink .x-btn-ml,.button-hyperlink .x-btn-mr,.button-hyperlink " +
				".x-btn-mc,.button-hyperlink .x-btn-bl,.button-hyperlink .x-btn-br,.button-hyperlink .x-btn-bc" +
				"{" +
				"background-image: none !important;" +
				"background: none !important;" +
				"}" +
				".button-hyperlink .x-btn-small .x-btn-mr, .button-hyperlink .x-btn-small .x-btn-ml,.button-hyperlink .x-btn-small .x-btn-mc," +
				".button-hyperlink .x-btn-small .x-btn-br,.button-hyperlink .x-btn-small .x-btn-bl,.button-hyperlink .x-btn-small .x-btn-bc," +
				".button-hyperlink .x-btn-small .x-btn-tc,.button-hyperlink .x-btn-small .x-btn-tr,.button-hyperlink .x-btn-small .x-btn-tl" +
				"{" +
				"background-image: none !important;" +
				"background: none !important;" +
				"}" +
				".button-hyperlink .x-btn-tl i,.button-hyperlink .x-btn-tr i,.button-hyperlink .x-btn-tc i,.button-hyperlink .x-btn-ml i,.button-hyperlink .x-btn-mr i,.button-hyperlink .x-btn-mc i,.button-hyperlink .x-btn-bl i,.button-hyperlink .x-btn-br i,.button-hyperlink .x-btn-bc i" +
				"{" +
				"font-size: 0px;" +
				"}" +
				"table.imagetable {" +
				"font-family: verdana,arial,sans-serif;" +
				"font-size:10px;" +
				"border: 1px solid #e3e3e3;" +
				"background-color: #f2f2f2;" +
				"width: 100%;" +
				"border-radius: 6px;" +
				"-webkit-border-radius: 6px;" +
				"-moz-border-radius: 6px;" +
				"}" +
				"table.imagetable .title {" +
				"background:#b5cfd2;" +
				"border-width: 1px;" +
				"width: 30%;" +
				"padding: 8px;" +
				"border-style: solid;" +
				"border-color: #999999;" +
				"}" +
				"table.imagetable td {" +
				"background:#dcddc0;" +
				"border-width: 1px;" +
				"padding: 8px;" +
				"border-style: solid;" +
				"border-color: #999999;" +
				"}" +
				"table.imagetable th {" +
				"height: 35px;" +
				"font-weight: bold;" +
				"font-size: 12px;" +
				"}";
	}

}
