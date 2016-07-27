package org.gcube.portlets.user.speciesdiscovery.client.gridview;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.SearchController;
import org.gcube.portlets.user.speciesdiscovery.client.event.DisableFilterEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOnlySelectedRowEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateAllRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ViewDetailsOfSelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.resultview.DescriptiveSpeciesGrid;
import org.gcube.portlets.user.speciesdiscovery.client.resultview.SpeciesGrid;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingToolBar;
import org.gcube.portlets.user.speciesdiscovery.shared.OccurrencesSaveEnum;
import org.gcube.portlets.user.speciesdiscovery.shared.SaveFileFormat;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ResultRowResultsPanel extends ContentPanel {


	private static final String ONLY_SELECTED = "Only selected";

	private static final String SHOWS_ONLY_THE_SELECTED_RESULTS = "Shows only the selected results.";

	private static final String SHOW_ONLY_SELECTED = "Show only selected";

	public static final String TOGGLE_CLASS = "SPECIES_TOGGLE";

	protected final static String TOGGLE_GROUP = "SPECIES_VIEW";

	private static ResultRowResultsPanel instance;
	
	protected SpeciesGrid classicGridView;
	
	protected DescriptiveSpeciesGrid descriptiveGridView;

	public final static AbstractImagePrototype deleteFilterImage = AbstractImagePrototype.create(Resources.INSTANCE.getDelete());
	
	public final static AbstractImagePrototype imgCheckBoxFull = AbstractImagePrototype.create(Resources.INSTANCE.getCheckBoxFull());
	
	public final static AbstractImagePrototype imgCheckBoxEmpty = AbstractImagePrototype.create(Resources.INSTANCE.getCheckBoxEmpty());
	
	public final static AbstractImagePrototype imgCheckSelected = AbstractImagePrototype.create(Resources.INSTANCE.getCheckSelected());
	
	public final static AbstractImagePrototype imgDetailsWindow = AbstractImagePrototype.create(Resources.INSTANCE.getDetailsWindow());
	
	protected ContentPanel currentView;

	protected ToggleButton classicGridViewButton;

	protected ToggleButton descriptiveGridViewButton;

	protected Button actionButton;
	
	protected Button btnSelectAllRow;
	
//	protected Button btnDeselectAllRow;

	protected EventBus eventBus;

	protected StreamPagingToolBar pageToolBar;

	private LabelToolItem labelFilter;

	private LabelToolItem labelFilterValue;

	private Button btnViewDetails;
	
	private Button btnRemoveFilter;
	
	private ToolBar viewsToolBar = new ToolBar();  
	
	private final ToggleButton btnShowOnlySelectedButton;

	private ListStore<ModelData> store;

	private Button btnDeSelectAllRow;

	private SearchController searchController;
	
	private Button btnShowInGisViewer;
	private Button btnOccurrencesMenu;

	public static ResultRowResultsPanel getInstance() {
		return instance;
	}

	
	public ResultRowResultsPanel(final EventBus eventBus, StreamPagingLoader loader, SearchController searchController) {

		instance=this;
		setBodyBorder(false);
		this.eventBus = eventBus;
		this.store = loader.getStore();
		this.searchController = searchController;

		bind(eventBus, loader);

		setFrame(false);  
		setCollapsible(false);
		setAnimCollapse(false);  
		setHeaderVisible(false);
		setLayout(new AnchorLayout());
		setScrollMode(Scroll.AUTOX); 
		

		classicGridView = new SpeciesGrid(loader.getStore(), eventBus);  
//		classicGridView.setBodyBorder(false);
		
		descriptiveGridView = new DescriptiveSpeciesGrid(loader.getStore());
//		descriptiveGridView.setBodyBorder(false);

		viewsToolBar = new ToolBar();  


		classicGridViewButton = new ToggleButton("", AbstractImagePrototype.create(Resources.INSTANCE.getGridSpeciesIcon()));
		classicGridViewButton.setToggleGroup(TOGGLE_GROUP);
		classicGridViewButton.setScale(ButtonScale.MEDIUM);
		classicGridViewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				toggleView(classicGridView);
			}
		});
		classicGridViewButton.setAllowDepress(false);
		classicGridViewButton.setToolTip(new ToolTipConfig("Scientific view", "Shows species as a basic grid"));
		viewsToolBar.add(classicGridViewButton);

		descriptiveGridViewButton = new ToggleButton("", AbstractImagePrototype.create(Resources.INSTANCE.getDescriptiveSpeciesIcon()));
		descriptiveGridViewButton.setScale(ButtonScale.MEDIUM);
		descriptiveGridViewButton.setToggleGroup(TOGGLE_GROUP);
		descriptiveGridViewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				toggleView(descriptiveGridView);
			}
		});
		
	
		descriptiveGridViewButton.setAllowDepress(false);
		descriptiveGridViewButton.setToolTip(new ToolTipConfig("Expandable List view", "Shows species as a grid letting users to expand interested rows."));
		viewsToolBar.add(descriptiveGridViewButton);
		
		LabelToolItem labelView = new LabelToolItem("Switch view");
		viewsToolBar.add(labelView);

		actionButton = new Button("Actions");
		
		viewsToolBar.add(new SeparatorToolItem());
		
		btnShowOnlySelectedButton = new ToggleButton(ONLY_SELECTED);	
		btnShowOnlySelectedButton.setIcon(imgCheckSelected);
		btnShowOnlySelectedButton.setScale(ButtonScale.MEDIUM);
		btnShowOnlySelectedButton.addSelectionListener(btnOnlySelectedListner);
		btnShowOnlySelectedButton.setToolTip(new ToolTipConfig(SHOW_ONLY_SELECTED, SHOWS_ONLY_THE_SELECTED_RESULTS));
		viewsToolBar.add(btnShowOnlySelectedButton);

		
		btnViewDetails = new Button(ConstantsSpeciesDiscovery.VIEWDETAILS);
		btnViewDetails.setScale(ButtonScale.MEDIUM);
		btnViewDetails.setIcon(imgDetailsWindow);
		btnViewDetails.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				eventBus.fireEvent(new ViewDetailsOfSelectedEvent());
				
			}
		
		});
		
		viewsToolBar.add(new SeparatorToolItem());
		viewsToolBar.add(btnViewDetails);
		
		btnSelectAllRow = new Button(ConstantsSpeciesDiscovery.SELECTALL);
		btnSelectAllRow.setIcon(imgCheckBoxFull);
		btnSelectAllRow.setScale(ButtonScale.MEDIUM);
		btnSelectAllRow.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
//				if(btnToggleSelectAllRow.isPressed()){
//					btnToggleSelectAllRow.setText(ConstantsSpeciesDiscovery.DESELECTALL);
//					btnToggleSelectAllRow.setIcon(imgCheckBoxEmpty);
//					eventBus.fireEvent(new UpdateAllRowSelectionEvent(true, SearchResultType.SPECIES_PRODUCT));
//				}else{
//					btnToggleSelectAllRow.setText(ConstantsSpeciesDiscovery.SELECTALL);
//					btnToggleSelectAllRow.setIcon(imgCheckBoxFull);
//					eventBus.fireEvent(new UpdateAllRowSelectionEvent(false, SearchResultType.SPECIES_PRODUCT));
//				}
				
				eventBus.fireEvent(new UpdateAllRowSelectionEvent(true, SearchResultType.SPECIES_PRODUCT));
			}
		
		});
		
		viewsToolBar.add(new SeparatorToolItem());
		viewsToolBar.add(btnSelectAllRow);
		
		
		btnDeSelectAllRow = new Button(ConstantsSpeciesDiscovery.DESELECTALL);
		btnDeSelectAllRow.setIcon(imgCheckBoxEmpty);
		btnDeSelectAllRow.setScale(ButtonScale.MEDIUM);
		btnDeSelectAllRow.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				eventBus.fireEvent(new UpdateAllRowSelectionEvent(false, SearchResultType.SPECIES_PRODUCT));
			}
		
		});
		
		viewsToolBar.add(new SeparatorToolItem());
		viewsToolBar.add(btnDeSelectAllRow);
		
		viewsToolBar.add(new SeparatorToolItem());
		addButtonsOccurrencesJob();
	
		viewsToolBar.add(new FillToolItem());
		
		labelFilter = new LabelToolItem(ConstantsSpeciesDiscovery.FILTER);
		viewsToolBar.add(labelFilter);
		
		labelFilterValue = new LabelToolItem(ConstantsSpeciesDiscovery.NONE);
		viewsToolBar.add(labelFilterValue);

		btnRemoveFilter = new Button();
		btnRemoveFilter.setIcon(deleteFilterImage);
		btnRemoveFilter.getElement().getStyle().setLeft(5, Unit.PX);
		btnRemoveFilter.setVisible(false);
		
	
		btnRemoveFilter.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				eventBus.fireEvent(new DisableFilterEvent());
				
			}
		});
		
		btnRemoveFilter.setToolTip(ConstantsSpeciesDiscovery.REMOVEFILTERTOOLTIP);
		
		viewsToolBar.add(btnRemoveFilter);

		setTopComponent(viewsToolBar);

		pageToolBar = new StreamPagingToolBar();

		pageToolBar.bind(loader);

		setBottomComponent(pageToolBar);
		
		add(classicGridView, new AnchorData("100% 100%"));
		add(descriptiveGridView, new AnchorData("100% 100%"));

		activeToolBarButtons(false); //DEFAULT DISABLE BUTTONS
		btnSelectAllRow.setEnabled(false);
		btnDeSelectAllRow.setEnabled(false);
		
		toggleView(classicGridView);
		

		
	}
	
	public void activeToolBarButtons(boolean bool){
		btnShowOnlySelectedButton.setEnabled(bool);
		btnViewDetails.setEnabled(bool);
//		btnShowInGisViewer.setEnabled(bool);
		btnOccurrencesMenu.setEnabled(bool);

	}
	
	public void resetFilter (){
		labelFilter.setLabel(ConstantsSpeciesDiscovery.FILTER);
		labelFilterValue.setLabel(ConstantsSpeciesDiscovery.NONE);
		btnRemoveFilter.setVisible(false);
		classicGridView.setBodyStyleAsFiltered(false);
		
		pageToolBar.reset(); //ADDED 03/08/2012
	}

	public void setFilterActive(boolean isActive, String label){
		
		if(isActive){
			labelFilterValue.setLabel(label);
			btnRemoveFilter.setVisible(true);
			classicGridView.setBodyStyleAsFiltered(true);
		}
		else
			resetFilter();	
	}

	protected void bind(EventBus eventBus, StreamPagingLoader loader)
	{
//		this.gridViewManager = new GridViewManager(eventBus, loader);
	}

	protected void toggleView(ContentPanel view)
	{
		instance.mask("Switching view..");

		actionButton.setEnabled(false);

		if (view.equals(classicGridView)) {
			classicGridViewButton.toggle(true);
			classicGridView.show();
			actionButton.setEnabled(true);
		} else {
			classicGridViewButton.toggle(false);
			classicGridView.hide();
		}

		if (view.equals(descriptiveGridView)) {
			descriptiveGridViewButton.toggle(true);
			descriptiveGridView.show();		
			actionButton.setEnabled(true);
		} else {
			descriptiveGridViewButton.toggle(false);			
			descriptiveGridView.hide();
		}

		currentView = view;
		instance.unmask();		
		

	}

	@Override
	protected void onAfterLayout() {
		super.onAfterLayout();
		descriptiveGridView.layout();
//		imageGridView.layout();
		reload();
	}

	public void reload() {
//		if (currentView!=null) 
//			this.gridViewManager.reload();
	}


	public void activeBtnShowOnlySelected(boolean bool) {
		
		this.btnShowOnlySelectedButton.removeSelectionListener(btnOnlySelectedListner);
		this.btnShowOnlySelectedButton.toggle(bool);
		this.btnShowOnlySelectedButton.addSelectionListener(btnOnlySelectedListner);
		
	}
	
	private SelectionListener< ButtonEvent> btnOnlySelectedListner = new SelectionListener<ButtonEvent>() {
		@Override
		public void componentSelected(ButtonEvent ce) {
			eventBus.fireEvent(new ShowOnlySelectedRowEvent(btnShowOnlySelectedButton.isPressed()));
		}
	};

	public void resetStore() {
		this.store.removeAll();
	}


	public SpeciesGrid getClassicGridView() {
		return classicGridView;
	}


	public void selectAllRows(boolean selectionValue) {
		if(selectionValue)
			classicGridView.selectAll();
		else
			classicGridView.deselectAll();
		
	}

	public void activeCheckAllRows(boolean active) {
		btnSelectAllRow.setEnabled(active);
		btnDeSelectAllRow.setEnabled(active);
	}

	public void addButtonsOccurrencesJob(){

//		btnShowInGisViewer = new Button(ConstantsSpeciesDiscovery.SHOW_IN_GIS_VIEWER);
//		btnShowInGisViewer.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getGisProducts()));
//		btnShowInGisViewer.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SHOW_IN_GIS_VIEWER, "Show occurences points from selected results in a Gis Viewer Map."));
//		btnShowInGisViewer.setScale(ButtonScale.SMALL);  
////		btnShowInGisViewer.setIconAlign(IconAlign.TOP);  
//		btnShowInGisViewer.setArrowAlign(ButtonArrowAlign.BOTTOM); 
//		btnShowInGisViewer.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				
//				SpeciesDiscovery.taxonomySearchService.retrieveOccurencesFromSelection(new AsyncCallback<Integer>() {
//					
//					@Override
//					public void onSuccess(Integer points) {
//						Log.trace("Expected points: "+points);
//						
//						int expectedPoints = points.intValue();
//						
//						if(expectedPoints>0)
//							eventBus.fireEvent(new ShowOccurrencesMapEvent(expectedPoints));
//					}
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						Info.display("Error getting occurrences", "Error getting occurrences, retry");	
//						Log.trace("Error getting occurrences", caught);
//					}
//				});
//				
//				
//			}
//		});
		
		btnOccurrencesMenu = new Button(ConstantsSpeciesDiscovery.SAVE_OCCURRENCES);  
		Menu formatSubMenu = new Menu();
		btnOccurrencesMenu.setMenu(formatSubMenu);  
		btnOccurrencesMenu.setScale(ButtonScale.MEDIUM);  
//		btnOccurrencesMenu.setIconAlign(IconAlign.TOP);  
		btnOccurrencesMenu.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSaveProducts24()));
		btnOccurrencesMenu.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVE_OCCURRENCES, ConstantsSpeciesDiscovery.SAVE_OCCURENCES_POINTS_FROM_SELECTED_RESULTS));

		MenuItem csvFormatItem = new MenuItem(ConstantsSpeciesDiscovery.CSV);
		csvFormatItem.setToolTip(new ToolTipConfig(ConstantsSpeciesDiscovery.SAVES_IN_CSV_FILE_FORMAT));
		csvFormatItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.CSV, OccurrencesSaveEnum.STANDARD,searchController.getLastSearchEvent().getSearchTerm(), false, isSearchByCommonName());
				
				occurrenceJobMng.saveOccurrence();
			}
		});
		
		
		formatSubMenu.add(csvFormatItem);
		
		Menu csvTypeMenu = new Menu();  
	    
	    MenuItem csvStandard = new MenuItem(ConstantsSpeciesDiscovery.PLAIN_CSV);
	    
	    csvStandard.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				

				OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.CSV, OccurrencesSaveEnum.STANDARD,searchController.getLastSearchEvent().getSearchTerm(), false, isSearchByCommonName());
				
				occurrenceJobMng.saveOccurrence();

				
			}
		});
	    
	    
	    MenuItem csvStandardByDataSource = new MenuItem(ConstantsSpeciesDiscovery.PLAIN_CSV_BY_DATA_SOURCE);
	    
	    csvStandardByDataSource.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

				OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.CSV, OccurrencesSaveEnum.STANDARD,searchController.getLastSearchEvent().getSearchTerm(), true, isSearchByCommonName());
				
				occurrenceJobMng.saveOccurrence();

			}
		});
	    
	    
	    MenuItem csvOpenModeller = new MenuItem(ConstantsSpeciesDiscovery.OPEN_MODELLER);
	    
	    csvOpenModeller.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {

	    		OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.CSV, OccurrencesSaveEnum.OPENMODELLER,searchController.getLastSearchEvent().getSearchTerm(), false, isSearchByCommonName());
		
	    		occurrenceJobMng.saveOccurrence();
	
			}
		});
	    
	    
	    MenuItem csvOpenModellerByDataSource = new MenuItem(ConstantsSpeciesDiscovery.OPEN_MODELLER_BY_DATA_SOURCE);
	    
	    csvOpenModellerByDataSource.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				

	    		OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.CSV, OccurrencesSaveEnum.OPENMODELLER,searchController.getLastSearchEvent().getSearchTerm(), true, isSearchByCommonName());
		
	    		occurrenceJobMng.saveOccurrence();
				
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

	    		OccurrenceJobGridManager occurrenceJobMng = new OccurrenceJobGridManager(eventBus,SaveFileFormat.DARWIN_CORE, null,searchController.getLastSearchEvent().getSearchTerm(), false, isSearchByCommonName());
		
	    		occurrenceJobMng.saveOccurrence();
				
			}
		});
		formatSubMenu.add(darwinCoreFormatItem);
		
		viewsToolBar.add(btnOccurrencesMenu);
//		viewsToolBar.add(new SeparatorToolItem());
//		viewsToolBar.add(btnShowInGisViewer);
	}


	protected boolean isSearchByCommonName() {
		
		if(searchController.getLastSearchEvent().getType().equals(SearchType.BY_COMMON_NAME))
			return true;
		
		return false;
	}
}
