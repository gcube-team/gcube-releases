package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources;

import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;

/**
 * The resources table class.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourcesTable extends Composite{

	// the data provider
	protected ListDataProvider<ResourceBeanWrapper> dataProvider = new ListDataProvider<ResourceBeanWrapper>();

	// the table that will be displayed
	private CellTable<ResourceBeanWrapper> table;

	// save original list
	private List<ResourceBeanWrapper> originalResources;

	// main panel
	private VerticalPanel mainPanel = new VerticalPanel();

	// button labels
	private static final String SELECT_ALL_LABEL = "Select All";
	private static final String DESELECT_ALL_LABEL = "Deselect All";

	// alert block	
	private AlertBlock alert = new AlertBlock();
	
	/*
	 * The key provider that allows us to identify ResourceBeanWrapper even if a field
	 * changes. We identify ResourceBeanWrapper by their unique ID.
	 */
	private static final ProvidesKey<ResourceBeanWrapper> KEY_PROVIDER = new ProvidesKey<ResourceBeanWrapper>() {
		@Override
		public Object getKey(ResourceBeanWrapper item) {
			return item.getId();
		}
	};

	// maintain the selection status
	private boolean selectedAll = true;
	final Button selectAllButton = new Button(DESELECT_ALL_LABEL);

	// is read only?
	private boolean readOnly = false;

	public ResourcesTable(List<ResourceBeanWrapper> resources){

		super();
		initWidget(mainPanel);

		// set panel width
		mainPanel.setWidth("100%");

		// save original resources
		this.originalResources = resources;

		// create table with key provider
		table = new CellTable<ResourceBeanWrapper>(KEY_PROVIDER);
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// add data to the provider
		dataProvider.setList(resources);
		dataProvider.addDataDisplay(table);

		// style of the table
		table.setStriped(true);
		table.setWidth("100%", false);
		table.addStyleName("table-style");
		table.setBordered(true);

		// visible rows
		table.setVisibleRange(new Range(0, originalResources.size())); 
		table.setRowCount(originalResources.size(), true);

		// Add a checked column to add the resource.
		Column<ResourceBeanWrapper, Boolean> chosenColumn = new Column<ResourceBeanWrapper, Boolean>(new CheckboxCell(true,false)) {

			@Override
			public Boolean getValue(ResourceBeanWrapper object) {
				return object.isToBeAdded();
			}

			@Override
			public void onBrowserEvent(Context context, final Element parent, final ResourceBeanWrapper res, NativeEvent event) {
				super.onBrowserEvent(context, parent, res, event);

				// set the value
				res.setToBeAdded(!res.isToBeAdded());
			}

			@Override
			public void render(Context context, ResourceBeanWrapper object,
					SafeHtmlBuilder sb) {

				if(readOnly){

					String checked = object.isToBeAdded() ? "checked" : "unchecked";
					sb.appendHtmlConstant("<input type='checkbox'" ).
					appendHtmlConstant("tabindex='-1' ").
					appendEscaped(checked).appendEscaped(" disabled ").appendHtmlConstant("/>");

				}else{

					super.render(context, object, sb);

				}

			}
		};
		table.addColumn(chosenColumn, "Select");

		// Add a text column to show the name (and make it sortable)
		final EditTextCell nameCell = new EditTextCell();
		Column<ResourceBeanWrapper, String> nameColumn = new Column<ResourceBeanWrapper, String>(nameCell) {

			@Override
			public String getValue(ResourceBeanWrapper object) {
				return object.getName();
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					ResourceBeanWrapper object, NativeEvent event) {
				if(!readOnly){
					super.onBrowserEvent(context, elem, object, event);
				}
			}

		};

		// Add a field updater to be notified when the user enters a new name for the resource.
		nameColumn.setFieldUpdater(new FieldUpdater<ResourceBeanWrapper, String>() {
			@Override
			public void update(int index, ResourceBeanWrapper object, String value) {
				if(value.isEmpty() || value.length() < 5){
					nameCell.clearViewData(KEY_PROVIDER.getKey(object));
					table.redraw();
					alert("Resource's name cannot be empty at should be at least of 5 characters", AlertType.ERROR);
					return;
				}

				// push the changes into the object
				object.setName(value);

				// Redraw the table with the new data.
				table.redraw();

			}
		});

		ListHandler<ResourceBeanWrapper> nameColHandler = new ListHandler<ResourceBeanWrapper>(dataProvider.getList());

		nameColHandler.setComparator(nameColumn, new Comparator<ResourceBeanWrapper>() {

			public int compare(ResourceBeanWrapper o1, ResourceBeanWrapper o2) {

				return o1.getName().compareTo(o2.getName());

			}
		});
		nameColumn.setSortable(true);	
		nameColumn.setDefaultSortAscending(false);
		table.addColumnSortHandler(nameColHandler);
		table.addColumn(nameColumn, "Name (Editable)");

		// Add a date column to show the url 
		TextColumn<ResourceBeanWrapper> urlColumn = new TextColumn<ResourceBeanWrapper>() {
			@Override
			public String getValue(ResourceBeanWrapper object) {
				return object.getUrl();
			}
		};
		table.addColumn(urlColumn, "Url (Temporary)");

		// Add a date column to show the description (and make it sortable)
		Column<ResourceBeanWrapper, String> descColumn = new Column<ResourceBeanWrapper, String>(new EditTextCell()) {
			@Override
			public String getValue(ResourceBeanWrapper object) {
				return object.getDescription();
			}

			@Override
			public void onBrowserEvent(Context context, Element elem,
					ResourceBeanWrapper object, NativeEvent event) {
				if(!readOnly){
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		};

		// Add a field updater to be notified when the user enters a new description.
		descColumn.setFieldUpdater(new FieldUpdater<ResourceBeanWrapper, String>() {
			@Override
			public void update(int index, ResourceBeanWrapper object, String value) {

				// push the changes into the object
				object.setDescription(value);

				// Redraw the table with the new data.
				table.redraw();
			}
		});

		table.addColumn(descColumn, "Description (Editable)");

		// sort by columnName
		table.getColumnSortList().push(nameColumn);

		// set width column chosen
		table.setColumnWidth(chosenColumn, 5, Unit.PCT);
		table.setColumnWidth(nameColumn, 20, Unit.PCT);
		table.setColumnWidth(urlColumn, 45, Unit.PCT);
		table.setColumnWidth(descColumn, 30, Unit.PCT);

		// add a select all button

		selectAllButton.getElement().getStyle().setMarginTop(15, Unit.PX);
		selectAllButton.getElement().getStyle().setMarginBottom(15, Unit.PX);

		// add handler
		selectAllButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {				
				selectedAll = !selectedAll;
				checkAllResources(selectedAll);
			}
		});

		// add the button
		mainPanel.add(selectAllButton);

		// add the table
		mainPanel.add(table);
		
		// add the alert block
		mainPanel.add(alert);
		alert.setVisible(false);
	}

	/**
	 * Check/Uncheck all resources according to value
	 * @param boolean value
	 */
	public void checkAllResources(boolean value){

		if(value)
			selectAllButton.setText(DESELECT_ALL_LABEL);
		else
			selectAllButton.setText(SELECT_ALL_LABEL);

		for(ResourceBeanWrapper resource: originalResources)
			resource.setToBeAdded(value);

		// refresh data
		dataProvider.refresh();
	}

	/**
	 * Freeze table content and select/deselect all button
	 */
	public void freezeTable() {

		selectAllButton.setEnabled(false);
		readOnly = true;

		// redraw the table (freezing it)
		table.redraw();
	}
	
	
	/**
	 * Alert the user
	 */
	private void alert(String msg, AlertType alertType){
		
		alert.setText(msg);
		alert.setType(alertType);
		alert.setVisible(true);
		alert.setAnimation(true);
		
		// hide anyway after a while
		Timer t = new Timer() {
			
			@Override
			public void run() {
				
				alert.setVisible(false);
				
			}
		};
		
		t.schedule(5000);
	}
}
