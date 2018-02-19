package org.gcube.portlets.admin.vredefinition.client.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.admin.vredefinition.shared.Resource;

import com.github.gwtbootstrap.client.ui.AppendButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.SimplePager.TextLocation;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

/**
 * The resources table class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourcesTable extends Composite{

	// the data provider
	protected ListDataProvider<Resource> dataProvider = new ListDataProvider<Resource>();

	// the table that will be displayed
	private CellTable<Resource> table = new CellTable<Resource>();

	// save original list
	private List<Resource> originalResources;

	// max row to show
	private static final int MAX_ROW_TO_SHOW_BEGINNING = 10;

	// TextBox and Button for filtering
	private TextBox searchBox;
	private Button searchButton;

	private Boolean isFilteringEnabled = false;

	// show all or switch to pagination button
	private Button showAllButton = new Button();

	// select all rows button
	private Button selectAllRows = new Button();

	// status
	private boolean allRowsSelected = false;

	// main panel
	private VerticalPanel mainPanel = new VerticalPanel();

	// keep track of the number of checked resources
	private int checkedResourcesNumber = 0;

	// father checkbox (if at least one of the resources is checked, it must be checked as well)
	// we need to call the CheckBox.checkHandler that also selects the associated subfunctionality
	public ResourcesTable(List<Resource> resources, AppendButton apButton, final CheckBox checkBoxSubfunctionality){

		super();
		initWidget(mainPanel);

		// set panel width
		mainPanel.setWidth("100%");

		// save original resources
		this.originalResources = resources;

		// add data to the provider
		dataProvider.setList(resources);
		dataProvider.addDataDisplay(table);

		// style of the table
		table.setStriped(true);
		table.setWidth("100%", false);
		table.addStyleName("table-style");
		table.setBordered(true);

		// visible rows
		table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING)); 
		table.setRowCount(resources.size(), true);

		// add handler to apButton (that contains the searchBox textbox and the search button)
		searchBox = (TextBox)apButton.getWidget(0);
		searchButton = (Button)apButton.getWidget(1);

		// add handlers on click as well as on key down
		searchBox.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				searchOnKeyDown(event);
			}
		});

		// add the handler
		searchButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				event.preventDefault();
				boolean[] isFilteringEnabledReference = {isFilteringEnabled};
				searchOnClickEvent(event, isFilteringEnabledReference);
				isFilteringEnabled = isFilteringEnabledReference[0];
			}
		});

		// Add a checked column to show the address.
		Column<Resource, Boolean> chosenColumn = new Column<Resource, Boolean>(new CheckboxCell(true,false)) {

			@Override
			public Boolean getValue(Resource object) {
				return object.isSelected();
			}

			@Override
			public void onBrowserEvent(Cell.Context context, final Element parent, final Resource user, NativeEvent event) {
				event.preventDefault();

				if(!"change".equals(event.getType()))
					return;

				user.setSelected(!user.isSelected());

				// update checked elements
				checkedResourcesNumber = user.isSelected() ? checkedResourcesNumber + 1 : checkedResourcesNumber - 1;

				// deselect the father
				if(checkedResourcesNumber == 0)
					checkBoxSubfunctionality.setValue(false, true);
				else
					checkBoxSubfunctionality.setValue(true, true);

				// refresh data
				dataProvider.refresh();
			}
		};
		table.addColumn(chosenColumn, "Select");

		// Add a text column to show the name (and make it sortable)
		TextColumn<Resource> nameColumn = new TextColumn<Resource>() {
			@Override
			public String getValue(Resource object) {
				return object.getName();
			}
		};

		ListHandler<Resource> nameColHandler = new ListHandler<Resource>(dataProvider.getList());
		nameColHandler.setComparator(nameColumn, new Comparator<Resource>() {

			public int compare(Resource o1, Resource o2) {

				return o1.getName().compareTo(o2.getName());

			}
		});
		nameColumn.setSortable(true);	
		nameColumn.setDefaultSortAscending(false);
		table.addColumnSortHandler(nameColHandler);
		table.addColumn(nameColumn, "Name");

		// Add a date column to show the description (and make it sortable)
		TextColumn<Resource> descColumn = new TextColumn<Resource>() {
			@Override
			public String getValue(Resource object) {
				return object.getDescription();
			}

			@Override
			public void render(Context context, Resource object,
					SafeHtmlBuilder sb) {

				// keep the first 120 chars
				boolean trunk = object.getDescription().length() > 120;
				String toShow =  trunk ? object.getDescription().substring(0, 120) : object.getDescription();
				HTML htmlDescription;
				String randomPar = Document.get().createUniqueId();

				// escape full description
				String escapedFullDescription = object.getDescription().replaceAll("'", "");
				escapedFullDescription = escapedFullDescription.replaceAll("\"", "");

				if(trunk)
					htmlDescription = new HTML("<p id=\"" + randomPar + "\" >" + toShow + "..." +
							"<a style=\"cursor:pointer;\" onclick=\"javascript:document.getElementById('"+randomPar+"').innerHTML='" + escapedFullDescription + "'\">See more</a>" +
							"</p>");
				else
					htmlDescription = new HTML("<p>" + toShow + "</p>");

				sb.appendHtmlConstant(htmlDescription.getHTML());
			}

		};
		ListHandler<Resource> descColHandler = new ListHandler<Resource>(dataProvider.getList());
		descColHandler.setComparator(descColumn, new Comparator<Resource>() {

			public int compare(Resource o1, Resource o2) {

				return o1.getDescription().compareTo(o2.getDescription());

			}
		});
		descColumn.setSortable(true);	
		table.addColumnSortHandler(descColHandler);
		table.addColumn(descColumn, "Description");

		// sort by columnName
		table.getColumnSortList().push(nameColumn);

		// set width column chosen
		table.setColumnWidth(chosenColumn, 60, Unit.PX);

		// control panel
		FlowPanel controlPanel = new FlowPanel();

		// set buttons information
		showAllButton.setText("Show all resources");
		showAllButton.addStyleName("show-all-resources-button");
		showAllButton.setTitle("Show all resources");
		selectAllRows.setText("Select all resources");
		selectAllRows.setTitle("Select all resources of the table");

		// show all button handler
		addShowButtonsHandler();
		addselectAllRowsHandler(checkBoxSubfunctionality);

		// add it if the number of rows is larger wrt the page size
		if(resources.size() > MAX_ROW_TO_SHOW_BEGINNING){
			controlPanel.add(showAllButton);

		}

		// add the select all rows if there are resources
		if(resources.size() > 0){
			controlPanel.add(selectAllRows);
		}
		
		mainPanel.add(controlPanel);
		
		// pager for pagination
		SimplePager pager = new SimplePager(TextLocation.RIGHT);
		pager.setPageSize(MAX_ROW_TO_SHOW_BEGINNING);
		pager.setDisplay(table);
		pager.getElement().getStyle().setMarginTop(20, Unit.PX);
		mainPanel.add(pager);

		// add the table
		mainPanel.add(table);
	}

	/**
	 * Add handlers for selectAllRowsButtons
	 * @param checkBoxSubfunctionality
	 */
	private void addselectAllRowsHandler(final CheckBox checkBoxSubfunctionality) {

		// add handlers on selection
		selectAllRows.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				List<Resource> currentList = dataProvider.getList();

				if(!allRowsSelected){
					for(Resource resource: currentList)
						resource.setSelected(true);

					allRowsSelected = true;
					selectAllRows.setText("Deselect all resources");
					selectAllRows.setTitle("Deselect all resources of the table");

					// check the father as well
					checkBoxSubfunctionality.setValue(true, true);

					// update checked elements
					checkedResourcesNumber = dataProvider.getList().size();

				}else{
					for(Resource resource: currentList)
						resource.setSelected(false);

					allRowsSelected = false;
					selectAllRows.setText("Select all resources");
					selectAllRows.setTitle("Select all resources of the table");

					// update checked elements
					checkedResourcesNumber = dataProvider.getList().size();

					// uncheck the father as well
					checkBoxSubfunctionality.setValue(false, true);
				}

				// refresh view
				dataProvider.refresh();			
			}
		});
	}

	/**
	 * Add handlers to show buttons
	 */
	private void addShowButtonsHandler() {

		showAllButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				boolean visibleAllActive = table.getVisibleRange().getLength() == dataProvider.getList().size();

				if(visibleAllActive){

					// switch back to pagination
					table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING));

					showAllButton.setText("Show all resources");

				}else{

					// show all rows
					table.setVisibleRange(new Range(0, dataProvider.getList().size()));

					// change text
					showAllButton.setText("Switch to pagination");

				}
			}
		});
	}

	/**
	 * Search on click
	 * @param event
	 * @param isFilteringEnabledReference
	 */
	private void searchOnClickEvent(ClickEvent event,
			boolean[] isFilteringEnabledReference) {

		if(isFilteringEnabledReference[0]){
			// change icon style
			searchButton.setIcon(IconType.SEARCH);
			searchBox.setText("");
			isFilteringEnabledReference[0] = false;

			// show all data
			dataProvider.setList(originalResources);
			table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING));
			table.setRowCount(dataProvider.getList().size(), true);
			dataProvider.refresh();

		}else{
			if(!searchBox.getText().isEmpty()){

				// current filter
				String filter = searchBox.getText();

				// get current data
				List<Resource> currentList = dataProvider.getList();

				// the list to show
				List<Resource> toShow = new ArrayList<Resource>();

				for(Resource resource : currentList) {

					// lower case
					String nameLowerCase = resource.getName().toLowerCase();
					String filterLowerCase = filter.toLowerCase();

					if(nameLowerCase.contains(filterLowerCase))
						toShow.add(resource);

				}

				dataProvider.setList(toShow);
				table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING));
				table.setRowCount(dataProvider.getList().size(), true);
				dataProvider.refresh();

				// change icon style
				searchButton.setIcon(IconType.REMOVE_SIGN);
				searchButton.setTitle("Remove search");
				isFilteringEnabledReference[0] = true;
			}

		}

	}

	/**
	 * Search on key ENTER press
	 * @param event
	 */
	private void searchOnKeyDown(KeyDownEvent event){

		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){

			if(!searchBox.getText().isEmpty()){

				// current filter
				String filter = searchBox.getText();

				// get current data
				List<Resource> currentList = dataProvider.getList();

				// the list to show
				List<Resource> toShow = new ArrayList<Resource>();

				for(Resource resource : currentList) {

					// lower case
					String nameLowerCase = resource.getName().toLowerCase();
					String filterLowerCase = filter.toLowerCase();

					if(nameLowerCase.contains(filterLowerCase))
						toShow.add(resource);

				}

				dataProvider.setList(toShow);
				table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING));
				table.setRowCount(dataProvider.getList().size(), true);
				dataProvider.refresh();

			}else{

				// show all data
				dataProvider.setList(originalResources);
				table.setVisibleRange(new Range(0, MAX_ROW_TO_SHOW_BEGINNING));
				table.setRowCount(dataProvider.getList().size(), true);
				dataProvider.refresh();
				
				// reset the button style
				searchButton.setIcon(IconType.SEARCH);

			}
		}
	}
}
