package org.gcube.portlets.user.speciesdiscovery.client.gridview;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.DisableFilterEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ShowOnlySelectedRowEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateAllRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.ViewDetailsOfSelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.resultview.DescriptiveTaxonomyGrid;
import org.gcube.portlets.user.speciesdiscovery.client.resultview.TaxonomyGrid;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingLoader;
import org.gcube.portlets.user.speciesdiscovery.client.util.stream.StreamPagingToolBar;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchResultType;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class TaxonomyRowResultsPanel extends ContentPanel {

	public static final String TOGGLE_CLASS = "SPECIES_TOGGLE";

	protected final static String TOGGLE_GROUP = "SPECIES_VIEW";
	
	private static final String ONLY_SELECTED = "Only selected";
	
	private static final String SHOW_ONLY_SELECTED = "Show only selected";

	private static final String SHOWS_ONLY_THE_SELECTED_RESULTS = "Shows only the selected results.";

	private static TaxonomyRowResultsPanel instance;
	
	protected TaxonomyGrid classicGridView;
	
	protected DescriptiveTaxonomyGrid descriptiveGridView;
	
	protected ContentPanel imageGridView = new ContentPanel();

	public final static AbstractImagePrototype deleteFilterImage = AbstractImagePrototype.create(Resources.INSTANCE.getDelete());
	
	public final static AbstractImagePrototype imgCheckBoxFull = AbstractImagePrototype.create(Resources.INSTANCE.getCheckBoxFull());
	
	public final static AbstractImagePrototype imgCheckBoxEmpty = AbstractImagePrototype.create(Resources.INSTANCE.getCheckBoxEmpty());
	
	public final static AbstractImagePrototype imgCheckSelected = AbstractImagePrototype.create(Resources.INSTANCE.getCheckSelected());
	
	public final static AbstractImagePrototype imgDetailsWindow = AbstractImagePrototype.create(Resources.INSTANCE.getDetailsWindow());
	
	protected ContentPanel currentView;

	protected ToggleButton classicGridViewButton;

	protected ToggleButton descriptiveGridViewButton;
	
	protected Button actionButton;

	protected EventBus eventBus;

	protected StreamPagingToolBar pageToolBar;

	private LabelToolItem labelFilter;

	private LabelToolItem labelFilterValue;

	private Button btnViewDetails;
	
	private Button btnRemoveFilter;
	
	private ToolBar viewsToolBar = new ToolBar();  
	
	private final ToggleButton btnShowOnlySelectedButton;

	private ListStore<ModelData> store;
	
	protected Button btnSelectAllRow;

	private Button btnDeSelectAllRow;


	public static TaxonomyRowResultsPanel getInstance() {
		return instance;
	}

	
	public TaxonomyRowResultsPanel(final EventBus eventBus, StreamPagingLoader loader) {

		instance=this;
		setBodyBorder(false);
		this.eventBus = eventBus;
		this.store = loader.getStore();

//		bind(eventBus, loader);

		setFrame(false);  
		setCollapsible(false);
		setAnimCollapse(false);  
		setHeaderVisible(false);
		setLayout(new AnchorLayout());
		setScrollMode(Scroll.AUTOX); 
		
		classicGridView = new TaxonomyGrid(this.store, eventBus);  
//		classicGridView.setBodyBorder(false);
		
		descriptiveGridView = new DescriptiveTaxonomyGrid(loader.getStore());
//		descriptiveGridView.setBodyBorder(false);

	/*	viewsToolBar = new ToolBar();  
		LabelToolItem labelView = new LabelToolItem("Switch view : ");
		viewsToolBar.add(labelView);

		classicGridViewButton = new ToggleButton("", AbstractImagePrototype.create(Resources.INSTANCE.getGridSpeciesIcon()));
		classicGridViewButton.setToggleGroup(TOGGLE_GROUP);
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
		*/
		
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
				
				eventBus.fireEvent(new UpdateAllRowSelectionEvent(true, SearchResultType.TAXONOMY_ITEM));
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
				eventBus.fireEvent(new UpdateAllRowSelectionEvent(false, SearchResultType.TAXONOMY_ITEM));
			}
		
		});
		
		viewsToolBar.add(new SeparatorToolItem());
		viewsToolBar.add(btnDeSelectAllRow);
		
		viewsToolBar.add(new FillToolItem());
		
		labelFilter = new LabelToolItem(ConstantsSpeciesDiscovery.FILTER);
		viewsToolBar.add(labelFilter);
//		viewsToolBar.add(new SeparatorToolItem());
		
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
//		btnSelectAllRow.setEnabled(bool);
//		btnDeSelectAllRow.setEnabled(bool);
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
		imageGridView.layout();
		reload();
	}

	public void reload() {
//		if (currentView!=null) this.gridViewManager.reload();
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
	
	
	public void resetStore(){
		this.store.removeAll();
	}


	public TaxonomyGrid getClassicGridView() {
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

}
