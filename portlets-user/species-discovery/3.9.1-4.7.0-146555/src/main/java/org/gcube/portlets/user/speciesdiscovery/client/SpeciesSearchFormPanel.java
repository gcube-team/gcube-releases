package org.gcube.portlets.user.speciesdiscovery.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.event.CapabilitySelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchCompleteEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchCompleteEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchStartedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchStartedEventHandler;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchTypeSelectedEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.StopCurrentSearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.filterresult.ResultFilterPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.window.HelpQueryWindow;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchType;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;


/**
 * The Class SpeciesSearchFormPanel.
 *
 * @author "Federico De Faveri defaveri@isti.cnr.it" - Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class SpeciesSearchFormPanel extends ContentPanel {
	protected static final String EXSEARCH = "e.g. sarda sarda, solea solea";
	protected Button buttSimpleSearch;
	protected Button buttQuerySearch;
	protected Button buttQueryExample;
	private Button buttQueryStopSearch;
	protected Button buttSimpleExample;
	protected Button buttSimpleStopSearch;
	protected SimpleComboBox<String> searchType;
	protected SimpleComboBox<String> searchTypeResults;
	protected EventBus eventBus;
	protected TextField<String> searchField;
	private boolean isActiveAdvanced = false;
	private HorizontalPanel hpAdvancedSearchLinks = new HorizontalPanel();
	private final Html htmlLabelAdvs = new Html("Advanced Option");
	private HorizontalPanel horizontalQueryPanel = new HorizontalPanel();
	public static final int NORTHHEIGHT = 150;
	private List<ToggleButton> tabItemList = new ArrayList<ToggleButton>();
	private AdvancedSearchPanelManager advSearchPanelsManager = AdvancedSearchPanelManager.getInstance();
	private final int DEFAULTLINKPANELTHEIGHT = 22;

	/**
	 * The Enum SEARCHTYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 16, 2017
	 */
	public static enum SEARCHTYPE {SIMPLE, FULLTEXT};
	public TextArea textArea = new TextArea();

	private SEARCHTYPE currentSearchType = SEARCHTYPE.SIMPLE;
	private com.extjs.gxt.ui.client.widget.button.Button buttSearchBy =  new com.extjs.gxt.ui.client.widget.button.Button(SEARCHBYQUERY);
	private final static String SEARCHBYQUERY = "Text query";
	private final static String SEARCHBYSIMPLE = "Simple query";

	private HorizontalPanel simpleSearchPanel = new HorizontalPanel();
	private HorizontalPanel querySeachPanel = new HorizontalPanel();



	protected CheckBox checkValidateOccurrences = new CheckBox();


	/**
	 * Instantiates a new species search form panel.
	 *
	 * @param eventBus the event bus
	 */
	public SpeciesSearchFormPanel(EventBus eventBus) {

		this.eventBus = eventBus;
		bind();

		horizontalQueryPanel.setStyleAttribute("margin-left", "10px");
		horizontalQueryPanel.setStyleAttribute("margin-top", "10px");
		htmlLabelAdvs.setStyleAttribute("font-size", "11px");
		htmlLabelAdvs.setStyleAttribute("margin-left", "10px");

		buttSearchBy.setStyleName("button-hyperlink");
		buttSearchBy.setStyleAttribute("margin-left", "5px");

		setLayout(new FlowLayout());
		setFrame(false);
		setHeaderVisible(false);
		setBorders(false);

		initComboSearchType();
		initComboSearchTypeResult();

		init();
		visibleButtonStopSearch(false); //abort must be hidden on init
		switchSearchType(SEARCHTYPE.SIMPLE);
		add(advSearchPanelsManager.getPanel());
	}


	/**
	 * Inits the combo search type.
	 */
	private void initComboSearchType(){

		searchType = new SimpleComboBox<String>();
		searchType.add(Arrays.asList(new String[]{ConstantsSpeciesDiscovery.SCIENTIFIC_NAME, ConstantsSpeciesDiscovery.COMMON_NAME}));
		searchType.setEditable(false);
		searchType.setTriggerAction(TriggerAction.ALL);
		searchType.setSimpleValue(ConstantsSpeciesDiscovery.SCIENTIFIC_NAME);

		searchType.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {

				eventBus.fireEvent(new SearchTypeSelectedEvent(getSearchType(se.getSelectedItem().getValue())));
			}
		});

	}

	/**
	 * Inits the combo search type result.
	 */
	private void initComboSearchTypeResult(){

		searchTypeResults = new SimpleComboBox<String>();
		searchTypeResults.add(Arrays.asList(new String[]{SpeciesCapability.RESULTITEM.getName(), SpeciesCapability.TAXONOMYITEM.getName()}));
		searchTypeResults.setEditable(false);
		searchTypeResults.setTriggerAction(TriggerAction.ALL);
		searchTypeResults.setSimpleValue(SpeciesCapability.RESULTITEM.getName());

		searchTypeResults.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				eventBus.fireEvent(new CapabilitySelectedEvent(getCapability(se.getSelectedItem().getValue())));
			}
		});


	}

	//RETURN RESULT ITEM OR TAXOMONY ITEM
	/**
	 * Gets the selected capability.
	 *
	 * @return the selected capability
	 */
	public SpeciesCapability getSelectedCapability(){
		return getCapability(searchTypeResults.getValue().getValue());
	}

	/**
	 * Gets the capability.
	 *
	 * @param value the value
	 * @return the capability
	 */
	private SpeciesCapability getCapability(String value){
		if(value.compareTo(SpeciesCapability.TAXONOMYITEM.getName())==0)
			return SpeciesCapability.TAXONOMYITEM;
		else if(value.compareTo(SpeciesCapability.RESULTITEM.getName())==0)
				return SpeciesCapability.RESULTITEM;
		else if(value.compareTo(SpeciesCapability.NAMESMAPPING.getName())==0)
			return SpeciesCapability.NAMESMAPPING;
		else if(value.compareTo(SpeciesCapability.SYNONYMS.getName())==0)
			return SpeciesCapability.SYNONYMS;
		else if(value.compareTo(SpeciesCapability.UNFOLD.getName())==0)
			return SpeciesCapability.UNFOLD;

		return SpeciesCapability.UNKNOWN;
	}

	/**
	 * Gets the search type.
	 *
	 * @param value the value
	 * @return the search type
	 */
	private SearchType getSearchType(String value){

		if(value.compareTo(ConstantsSpeciesDiscovery.SCIENTIFIC_NAME)==0)
			return SearchType.BY_SCIENTIFIC_NAME;
		else if(value.compareTo(ConstantsSpeciesDiscovery.COMMON_NAME)==0)
				return SearchType.BY_COMMON_NAME;

		return SearchType.BY_SCIENTIFIC_NAME;


	}

	/**
	 * Switch search type.
	 *
	 * @param searchType the search type
	 */
	public void switchSearchType(SEARCHTYPE searchType){

		currentSearchType = searchType;

		if(searchType.equals(SEARCHTYPE.SIMPLE)){
			querySeachPanel.setVisible(false);
			simpleSearchPanel.setVisible(true);
			buttSearchBy.setText(SEARCHBYQUERY);
			hpAdvancedSearchLinks.setEnabled(true);
		}else if(searchType.equals(SEARCHTYPE.FULLTEXT)){
			simpleSearchPanel.setVisible(false);
			querySeachPanel.setVisible(true);
			buttSearchBy.setText(SEARCHBYSIMPLE);
			hpAdvancedSearchLinks.setEnabled(false);
			activeAdvancedSearch(false);
		}

	}

	/**
	 * Inits the.
	 */
	protected void init()
	{
		horizontalQueryPanel.setBorders(false);
		horizontalQueryPanel.setSize(1150, 33);

		searchTypeResults.setStyleAttribute("margin-right", "10px");

		initSimpleSearchPanel();
		initQuerySearchPanel();
		createAdvancedSeachLinks();

		horizontalQueryPanel.add(simpleSearchPanel);
		horizontalQueryPanel.add(querySeachPanel);

		buttSearchBy.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(currentSearchType.equals(SEARCHTYPE.SIMPLE))
					switchSearchType(SEARCHTYPE.FULLTEXT);
				else
					switchSearchType(SEARCHTYPE.SIMPLE);

			}
		});

		horizontalQueryPanel.add(buttSearchBy);

		add(horizontalQueryPanel);
		add(hpAdvancedSearchLinks);

	}

	/**
	 * Inits the query search panel.
	 */
	private void initQuerySearchPanel() {

		querySeachPanel.setSpacing(5);
		buttQuerySearch = new Button("Search");
		buttQuerySearch.setStyleName("wizardButton");
		buttQuerySearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchByQuery(textArea.getValue());

			}
		});

		buttQueryExample = new Button("Example", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				textArea.setValue("SEARCH BY SN 'Carcharodon carcharias' in OBIS RETURN Occurrence");

			}
		});

		buttQueryExample.setStyleName("wizardButton");

		textArea.setSize(450, 37);
		textArea.setEmptyText("Write a query");
		textArea.setStyleAttribute("margin-right", "5px");

		querySeachPanel.add(textArea);

		com.extjs.gxt.ui.client.widget.button.Button buttQueryInfo = new com.extjs.gxt.ui.client.widget.button.Button();
	    buttQueryInfo.setStyleAttribute("margin-right", "15px");
		buttQueryInfo.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getHelpIcon()));

		buttQueryInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				new HelpQueryWindow();

			}
		});

	    querySeachPanel.add(buttQueryInfo);

		buttQueryStopSearch = new Button("Stop Search...");
		buttQueryStopSearch.setTitle("Stops loading of the search results");
		buttQueryStopSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new StopCurrentSearchEvent());
			}
		});

		buttQueryStopSearch.setStyleName("wizardButton");


		querySeachPanel.add(buttQuerySearch);
		querySeachPanel.add(buttQueryExample);
		querySeachPanel.add(buttQueryStopSearch);

	}


	/**
	 * Inits the simple search panel.
	 */
	private void initSimpleSearchPanel() {

		simpleSearchPanel.setSpacing(5);
		simpleSearchPanel.setVerticalAlign(VerticalAlignment.MIDDLE);

		VerticalPanel vtPanel = new VerticalPanel();
		HorizontalPanel hpPanel = new HorizontalPanel();
		hpPanel.setVerticalAlign(VerticalAlignment.MIDDLE);

		Text textSearch = new Text("Search:");
		textSearch.setStyleAttribute("margin-left", "5px");
		textSearch.setStyleAttribute("margin-right", "5px");

		hpPanel.add(textSearch);
		hpPanel.add(searchTypeResults);

		Text textBy = new Text("By:");
		textBy.setStyleAttribute("margin-left", "5px");
		textBy.setStyleAttribute("margin-right", "5px");

		hpPanel.add(textBy);
		hpPanel.add(searchType);

    	Text textTerm = new Text("Term:");
		textTerm.setStyleAttribute("margin-left", "10px");
		textTerm.setStyleAttribute("margin-right", "5px");
		hpPanel.add(textTerm);

		searchField = new TextField<String>();
		searchField.setStyleAttribute("margin-left", "0px");
		searchField.setStyleAttribute("margin-right", "10px");
		searchField.setEmptyText(EXSEARCH);
		searchField.setWidth(300);
		searchField.addKeyListener(new KeyListener(){

			@Override
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode()==KeyCodes.KEY_ENTER) search();
			}

		});

		hpPanel.add(searchField);

        checkValidateOccurrences.setBoxLabel("validate occurrences");
        checkValidateOccurrences.setValueAttribute("validate occurrences");
        checkValidateOccurrences.setStyleAttribute("margin-right", "10px");

		buttSimpleSearch = new Button("Search");
		buttSimpleSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				search();

			}
		});

		buttSimpleSearch.setStyleName("wizardButton");
		hpPanel.add(buttSimpleSearch);

		buttSimpleExample = new Button("Example", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchField.setValue("Carcharodon carcharias");

			}
		});

		buttSimpleExample.setStyleName("wizardButton");
		hpPanel.add(buttSimpleExample);

		buttSimpleStopSearch = new Button("Stop Search...");
		buttSimpleStopSearch.setTitle("Stops loading of the search results");
		buttSimpleStopSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new StopCurrentSearchEvent());
			}
		});

		buttSimpleStopSearch.setStyleName("wizardButton");
		hpPanel.add(buttSimpleStopSearch);


		HorizontalPanel hpPanelExpand = new HorizontalPanel();
		hpPanelExpand.setVerticalAlign(VerticalAlignment.MIDDLE);
		hpPanelExpand.setStyleAttribute("margin-top", "10px");
		Text textExpand = new Text("Expand with synonyms: ");
		textExpand.setStyleAttribute("margin-left", "5px");
		textExpand.setStyleAttribute("margin-right", "5px");

		vtPanel.add(hpPanel);
		simpleSearchPanel.add(vtPanel);

	}

	/**
	 * Gets the value check validate occcurrences.
	 *
	 * @return the value check validate occcurrences
	 */
	public boolean getValueCheckValidateOcccurrences(){
		return this.checkValidateOccurrences.getValue();
	}

	/**
	 * Sets the value check validate occcurrences.
	 *
	 * @param bool the new value check validate occcurrences
	 */
	public void setValueCheckValidateOcccurrences(boolean bool){
		this.checkValidateOccurrences.setValue(bool);
	}

	/**
	 * Sets the visible check validate occcurrences.
	 *
	 * @param bool the new visible check validate occcurrences
	 */
	public void setVisibleCheckValidateOcccurrences(boolean bool){
		this.checkValidateOccurrences.setVisible(bool);
	}

	/**
	 * Active button search.
	 *
	 * @param bool the bool
	 */
	public void activeButtonSearch(boolean bool){
		buttSimpleSearch.setEnabled(bool);
	}



	/**
	 * Visible button stop search.
	 *
	 * @param bool the bool
	 */
	public void visibleButtonStopSearch(boolean bool){
		Log.warn("Stop Search visible? "+bool);
		buttSimpleStopSearch.setVisible(bool);
		buttQueryStopSearch.setVisible(bool);
	}


	/**
	 * Creates the advanced seach links.
	 */
	private void createAdvancedSeachLinks() {

		hpAdvancedSearchLinks.setVerticalAlign(VerticalAlignment.MIDDLE);
		hpAdvancedSearchLinks.setStyleAttribute("marginLeft", "15px");
		hpAdvancedSearchLinks.setStyleAttribute("marginRight", "15px");
		hpAdvancedSearchLinks.setStyleAttribute("marginTop", "5px");
		hpAdvancedSearchLinks.setStyleAttribute("border-bottom", "thin solid #99BBE8;");
		hpAdvancedSearchLinks.setStyleAttribute("background-color", "#EFF5FB");
		hpAdvancedSearchLinks.add(htmlLabelAdvs);
		//hpAdvancedSearchLinks.setHeight(DEFAULTLINKPANELTHEIGHT);
//		imageArrowRight.setStyleAttribute("margin-top", "5px");
//		imageArrowDown.setStyleAttribute("margin-top", "5px");

		for(final String advPanel : advSearchPanelsManager.getListAdvancedSearchPanels()){

			ToggleButton butt = new ToggleButton(advPanel);
			butt.setToggleGroup("tab");
			butt.setSize(100, -1);
			butt.setStyleAttribute("marginLeft", "20px");
			butt.setId(advPanel);
			butt.setStyleName("button-hyperlink");
			butt.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getArrowRight()));

			tabItemList.add(butt);

			butt.addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

//					isActiveAdvanced = !isActiveAdvanced;

					ToggleButton butt = (ToggleButton) ce.getButton();

					isActiveAdvanced = butt.isPressed();

					advSearchPanelsManager.setActivePanel(advPanel);
					activeAdvancedSearch(isActiveAdvanced);
					changeStateIcons();
				}

				private void changeStateIcons() {

					for(ToggleButton toggle : tabItemList){
						if(toggle.isPressed()){
							toggle.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getArrowDown()));
							toggle.getElement().getStyle().setBackgroundColor("#D0DEF0");

						}else{
							toggle.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getArrowRight()));
							toggle.getElement().getStyle().clearBackgroundColor();
//							toggle.getElement().getStyle().setFontWeight(Style.FontWeight.NORMAL);
						}
					}
				}

			});
			hpAdvancedSearchLinks.add(butt);
		}
	}


	/**
	 * Active advanced search.
	 *
	 * @param bool the bool
	 */
	private void activeAdvancedSearch(boolean bool){

		if(bool){
//			htmlLabelAdvs.setHtml(messageAdvActive);
			SearchBorderLayoutPanel.getInstance().updateNorthSize(NORTHHEIGHT);
		}
		else{
//			htmlLabelAdvs.setHtml(messageAdvDeactive);
			SearchBorderLayoutPanel.getInstance().updateNorthSize(SearchBorderLayoutPanel.DEFAULTNORTHHEIGHT);
		}
	}

	/**
	 * Search.
	 */
	protected void search()
	{

		String searchTerm = searchField.getValue();
		if (buttSimpleSearch.isEnabled() && searchTerm!=null && searchTerm.length()!=0) {

//			if(isActiveAdvanced){
			search(searchTerm,
					advSearchPanelsManager.getUpperBoundLatitudeField().getValue(),
					advSearchPanelsManager.getUpperBoundLongitudeField().getValue(),
					advSearchPanelsManager.getLowerBoundLatitudeField().getValue(),
					advSearchPanelsManager.getLowerBoundLongitudeField().getValue(),
					advSearchPanelsManager.getFromDate().getValue(),
					advSearchPanelsManager.getToDate().getValue(),
					advSearchPanelsManager.getCheckedDataSources(),
					ResultFilterPanelManager.getInstance().getGroupByRank(),
					advSearchPanelsManager.getCurrentSelectedCapability(),
					advSearchPanelsManager.getCheckedDataSourceForSynonyms(),
					advSearchPanelsManager.getCheckedDataSourceForUnfold());
//			}else
//				search(searchTerm,null,null,null,null,null,null);
		}
		else Info.display("No search term specified", "There is not search term specified");

	}

	/**
	 * Search.
	 *
	 * @param searchTerm the search term
	 * @param upperBoundLongitude the upper bound longitude
	 * @param upperBoundLatitude the upper bound latitude
	 * @param lowerBoundLongitude the lower bound longitude
	 * @param lowerBoundLatitude the lower bound latitude
	 * @param fromDate the from date
	 * @param toDate the to date
	 * @param listDataSources the list data sources
	 * @param groupRank the group rank
	 * @param resultType the result type
	 * @param listDataSourceForSynonyms the list data source for synonyms
	 * @param listDataSourceForUnfold the list data source for unfold
	 */
	protected void search(String searchTerm, Number upperBoundLongitude, Number upperBoundLatitude, Number lowerBoundLongitude, Number lowerBoundLatitude, Date fromDate, Date toDate, List<DataSourceModel> listDataSources, String groupRank, SpeciesCapability resultType, List<DataSourceModel> listDataSourceForSynonyms, List<DataSourceModel> listDataSourceForUnfold)
	{
		mask("Searching...");
		//searchButton.setEnabled(false);

		SearchType type = null;
		if (ConstantsSpeciesDiscovery.SCIENTIFIC_NAME.equals(searchType.getSimpleValue())) type = SearchType.BY_SCIENTIFIC_NAME;
		if (ConstantsSpeciesDiscovery.COMMON_NAME.equals(searchType.getSimpleValue())) type = SearchType.BY_COMMON_NAME;

		SearchEvent event = new SearchEvent(type, searchTerm, upperBoundLongitude, upperBoundLatitude, lowerBoundLongitude, lowerBoundLatitude, fromDate, toDate, listDataSources, groupRank, resultType, listDataSourceForSynonyms, listDataSourceForUnfold);
		eventBus.fireEvent(event);
	}


	/**
	 * Search by query.
	 *
	 * @param query the query
	 */
	protected void searchByQuery(String query)
	{
		if (query!=null && query.length()!=0) {
			SearchEvent event = new SearchEvent(SearchType.BY_QUERY, query);
			eventBus.fireEvent(event);
		}
		else
			Info.display("No query specified", "There is not query specified");
	}

	/**
	 * Bind.
	 */
	protected void bind()
	{
		eventBus.addHandler(SearchStartedEvent.TYPE, new SearchStartedEventHandler() {

			@Override
			public void onSearchStarted(SearchStartedEvent event) {
				unmask();
			}
		});
		eventBus.addHandler(SearchCompleteEvent.TYPE, new SearchCompleteEventHandler() {

			@Override
			public void onSearchComplete(SearchCompleteEvent event) {
				enableSearch();
			}
		});
	}

	/**
	 * Enable search.
	 */
	protected void enableSearch()
	{
		buttSimpleSearch.setEnabled(true);
	}

	/**
	 * Disable search.
	 */
	protected void disableSearch()
	{
		buttSimpleSearch.setEnabled(false);
	}
}
