package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent.NetCDFDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.model.NetCDFDataModel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource.NetCDFBasicResources;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.AttributeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.RangeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DetailPanel extends SimplePanel implements NetCDFDataEventHandler {
	private static final String HEIGHT = "350px";
	private static final String WIDTH = "680px";
	private static final String ATTRIBUTE_DATA_GRID_HEIGHT = "155px";
	private static final String ATTRIBUTE_DATA_GRID_WIDTH = "608px";
	private static final String RANGE_DATA_GRID_HEIGHT = "105px";
	private static final String RANGE_DATA_GRID_WIDTH = "608px";

	private static final NetCDFPreviewMessages messages = GWT.create(NetCDFPreviewMessages.class);

	private ListBox variablesBox;
	private ArrayList<VariableData> variables;

	private NetCDFDataModel netCDFDataModel;

	private ListDataProvider<AttributeData> attributesOfVariableProvider;
	private DataGrid<AttributeData> attributesOfVariableGrid;
	private SimplePager attributesOfVariablePager;

	private ListDataProvider<RangeData> rangesOfVariableProvider;
	private DataGrid<RangeData> rangesOfVariableGrid;
	private SimplePager rangesOfVariablePager;

	public DetailPanel(NetCDFDataModel netCDFDataModel) {
		this.netCDFDataModel = netCDFDataModel;
		init();
		create();
	}

	private void init() {
		setHeight(HEIGHT);
		setWidth(WIDTH);
	}

	private void create() {
		netCDFDataModel.addNetCDFDataEventHandler(this);
		createAttributesOfVariableGrid();
		createRangesOfVariableGrid();
		// ////////
		// Form
		FlexTable detailFlexTable = new FlexTable();
		detailFlexTable.setCellSpacing(2);
		// detailFlexTable.setBorderWidth(1);

		// Add a drop box with the list types
		variablesBox = new ListBox();
		variablesBox.setEnabled(false);
		variablesBox.ensureDebugId("detailPanelVariablesBox");
		variablesBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = variablesBox.getSelectedIndex();
				String value = variablesBox.getValue(index);
				retrieveVariableData(value);
			}

		});

		detailFlexTable.setHTML(0, 0, "Variable:");
		detailFlexTable.setWidget(0, 1, variablesBox);

		// Attributes
		detailFlexTable.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		detailFlexTable.setHTML(1, 0, "Attributes:");

		attributesOfVariablePager.getElement().getStyle().setProperty("margin", "auto");

		VerticalPanel vpAttributes = new VerticalPanel();
		vpAttributes.getElement().getStyle().setBackgroundColor("#f8f8fb");
		vpAttributes.add(attributesOfVariableGrid);
		vpAttributes.add(attributesOfVariablePager);

		detailFlexTable.setWidget(1, 1, vpAttributes);

		// Ranges
		detailFlexTable.getFlexCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
		detailFlexTable.setHTML(2, 0, "Ranges:");

		// detailFlexTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		rangesOfVariablePager.getElement().getStyle().setProperty("margin", "auto");
		// detailFlexTable.setWidget(2, 1, attributesOfVariablePager);

		VerticalPanel vpRanges = new VerticalPanel();
		vpRanges.getElement().getStyle().setBackgroundColor("#f8f8fb");
		vpRanges.add(rangesOfVariableGrid);
		vpRanges.add(rangesOfVariablePager);

		detailFlexTable.setWidget(2, 1, vpRanges);

		setWidget(detailFlexTable);

	}

	public void refresh() {
		refreshAttributes();
		refreshRanges();
	}

	private void retrieveVariableData(String selectedValue) {
		VariableData variableRequested = null;
		if (Integer.valueOf(selectedValue) != -1) {
			for (VariableData variableData : variables) {
				if (variableData.getId() == Integer.valueOf(selectedValue)) {
					variableRequested = variableData;
					break;
				}

			}

			if (variableRequested != null) {
				GWT.log("Variable retrieved: " + variableRequested);
				List<AttributeData> attributesOfVariable = attributesOfVariableProvider.getList();
				attributesOfVariable.clear();
				attributesOfVariable.addAll(variableRequested.getAttributes());
				attributesOfVariableProvider.refresh();
				refreshAttributes();

				List<RangeData> rangesOfVariable = rangesOfVariableProvider.getList();
				rangesOfVariable.clear();
				rangesOfVariable.addAll(variableRequested.getRanges());
				rangesOfVariableProvider.refresh();
				refreshRanges();

			} else {
				GWT.log("Variable not retrieved: " + selectedValue);
			}
		}
	}

	@Override
	public void onNetCDFDataReady(NetCDFDataEvent event) {
		variables = event.getNetCDFData().getVariables();
		variablesBox.clear();
		variablesBox.addItem(" ", "-1");
		for (VariableData varData : variables) {
			variablesBox.addItem(varData.getFullName(), String.valueOf(varData.getId()));
		}
		variablesBox.setEnabled(true);

		//
		attributesOfVariableProvider.addDataDisplay(attributesOfVariableGrid);
		attributesOfVariableProvider.refresh();

		rangesOfVariableProvider.addDataDisplay(rangesOfVariableGrid);
		rangesOfVariableProvider.refresh();

	}

	private void createAttributesOfVariableGrid() {

		/*
		 * Set a key provider that provides a unique key for each contact. If
		 * key is used to identify contacts when fields (such as the name and
		 * address) change.
		 */

		attributesOfVariableGrid = new DataGrid<AttributeData>(8, AttributeData.KEY_PROVIDER);
		attributesOfVariableGrid.setWidth(ATTRIBUTE_DATA_GRID_WIDTH);
		attributesOfVariableGrid.setHeight(ATTRIBUTE_DATA_GRID_HEIGHT);
		attributesOfVariableGrid.addStyleName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getCellWordWrap());

		/*
		 * Do not refresh the headers every time the data is updated. The footer
		 * depends on the current data, so we do not disable auto refresh on the
		 * footer.
		 */
		attributesOfVariableGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		attributesOfVariableGrid.setEmptyTableWidget(new Label(messages.dataGridAttributeOfVariableEmpty()));

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.

		attributesOfVariableProvider = new ListDataProvider<>();

		ListHandler<AttributeData> sortHandler = new ListHandler<AttributeData>(attributesOfVariableProvider.getList());
		attributesOfVariableGrid.addColumnSortHandler(sortHandler);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		attributesOfVariablePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		attributesOfVariablePager.setDisplay(attributesOfVariableGrid);

		// Add a selection model so we can select cells.
		final MultiSelectionModel<AttributeData> selectionModel = new MultiSelectionModel<AttributeData>(
				AttributeData.KEY_PROVIDER);
		attributesOfVariableGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<AttributeData>createCheckboxManager());

		// Initialize the columns.
		initAttributesTableColumns(selectionModel, sortHandler);

	}

	public void refreshAttributes() {
		attributesOfVariableGrid.redraw();
	}

	private void initAttributesTableColumns(SelectionModel<AttributeData> selectionModel,
			ListHandler<AttributeData> sortHandler) {
		// Checkbox column. This table will uses a checkbox column for
		// selection.
		// Alternatively, you can call dataGrid.setSelectionEnabled(true) to
		// enable
		// mouse selection.
		/*
		 * Column<VariableData, Boolean> checkColumn = new Column<VariableData,
		 * Boolean>(new CheckboxCell(true, false)) {
		 * 
		 * @Override public Boolean getValue(VariableData object) { // Get the
		 * value from the selection model. return
		 * selectionModel.isSelected(object); } };
		 * dataGrid.addColumn(checkColumn,
		 * SafeHtmlUtils.fromSafeConstant("<br/>"));
		 * dataGrid.setColumnWidth(checkColumn, 40, Unit.PX);
		 */

		// Fullname
		Column<AttributeData, String> fullnameColum = new Column<AttributeData, String>(new TextCell()) {
			@Override
			public String getValue(AttributeData object) {
				return object.getFullName();
			}
		};
		fullnameColum.setSortable(true);
		sortHandler.setComparator(fullnameColum, new Comparator<AttributeData>() {

			@Override
			public int compare(AttributeData o1, AttributeData o2) {
				return o1.getFullName().compareTo(o2.getFullName());

			}
		});
		attributesOfVariableGrid.addColumn(fullnameColum, messages.columnFullname());
		attributesOfVariableGrid.setColumnWidth(fullnameColum, 130, Unit.PX);

		// DataType
		Column<AttributeData, String> dataTypeColumn = new Column<AttributeData, String>(new TextCell()) {
			@Override
			public String getValue(AttributeData object) {
				return object.getDataType();
			}
		};
		dataTypeColumn.setSortable(true);
		sortHandler.setComparator(dataTypeColumn, new Comparator<AttributeData>() {
			@Override
			public int compare(AttributeData o1, AttributeData o2) {
				return o1.getDataType().compareTo(o2.getDataType());
			}
		});
		attributesOfVariableGrid.addColumn(dataTypeColumn, messages.columnDataType());
		attributesOfVariableGrid.setColumnWidth(dataTypeColumn, 100, Unit.PX);

		// Values
		Column<AttributeData, String> valuesColumn = new Column<AttributeData, String>(new TextCell()) {
			@Override
			public String getValue(AttributeData object) {
				return String.valueOf(object.getValues());
			}
		};
		valuesColumn.setSortable(true);
		sortHandler.setComparator(valuesColumn, new Comparator<AttributeData>() {
			@Override
			public int compare(AttributeData o1, AttributeData o2) {
				return o1.getValues().compareTo(o2.getValues());

			}
		});

		attributesOfVariableGrid.addColumn(valuesColumn, messages.columnValue());
		// dataGrid.setColumnWidth(valuesColumn, 40, Unit.PX);

	}

	public Set<AttributeData> selecetedAttributes() {
		@SuppressWarnings("unchecked")
		Set<AttributeData> selected = ((MultiSelectionModel<AttributeData>) attributesOfVariableGrid
				.getSelectionModel()).getSelectedSet();
		return selected;

	}

	private void createRangesOfVariableGrid() {

		/*
		 * Set a key provider that provides a unique key for each contact. If
		 * key is used to identify contacts when fields (such as the name and
		 * address) change.
		 */

		rangesOfVariableGrid = new DataGrid<RangeData>(8, RangeData.KEY_PROVIDER);
		rangesOfVariableGrid.setWidth(RANGE_DATA_GRID_WIDTH);
		rangesOfVariableGrid.setHeight(RANGE_DATA_GRID_HEIGHT);
		rangesOfVariableGrid.addStyleName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getCellWordWrap());

		/*
		 * Do not refresh the headers every time the data is updated. The footer
		 * depends on the current data, so we do not disable auto refresh on the
		 * footer.
		 */
		rangesOfVariableGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		rangesOfVariableGrid.setEmptyTableWidget(new Label(messages.dataGridRangeOfVariableEmpty()));

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.

		rangesOfVariableProvider = new ListDataProvider<>();

		ListHandler<RangeData> sortHandler = new ListHandler<RangeData>(rangesOfVariableProvider.getList());
		rangesOfVariableGrid.addColumnSortHandler(sortHandler);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		rangesOfVariablePager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		rangesOfVariablePager.setDisplay(rangesOfVariableGrid);

		// Add a selection model so we can select cells.
		final MultiSelectionModel<RangeData> selectionModel = new MultiSelectionModel<RangeData>(
				RangeData.KEY_PROVIDER);
		rangesOfVariableGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<RangeData>createCheckboxManager());

		// Initialize the columns.
		initRangesTableColumns(selectionModel, sortHandler);

	}

	public void refreshRanges() {
		rangesOfVariableGrid.redraw();
	}

	private void initRangesTableColumns(SelectionModel<RangeData> selectionModel, ListHandler<RangeData> sortHandler) {

		// Name
		Column<RangeData, String> nameColum = new Column<RangeData, String>(new TextCell()) {
			@Override
			public String getValue(RangeData object) {
				return object.getName();
			}
		};
		nameColum.setSortable(true);
		sortHandler.setComparator(nameColum, new Comparator<RangeData>() {

			@Override
			public int compare(RangeData o1, RangeData o2) {
				return o1.getName().compareTo(o2.getName());

			}
		});
		rangesOfVariableGrid.addColumn(nameColum, messages.columnName());
		rangesOfVariableGrid.setColumnWidth(nameColum, 130, Unit.PX);

		// N
		Column<RangeData, String> nColumn = new Column<RangeData, String>(new TextCell()) {
			@Override
			public String getValue(RangeData object) {
				return String.valueOf(object.getN());
			}
		};
		nColumn.setSortable(true);
		sortHandler.setComparator(nColumn, new Comparator<RangeData>() {
			@Override
			public int compare(RangeData o1, RangeData o2) {
				return (o1.getN() < o2.getN()) ? -1 : ((o1.getN() == o2.getN()) ? 0 : 1);
			}
		});
		rangesOfVariableGrid.addColumn(nColumn, messages.columnN());
		rangesOfVariableGrid.setColumnWidth(nColumn, 80, Unit.PX);

		// First
		Column<RangeData, String> firstColumn = new Column<RangeData, String>(new TextCell()) {
			@Override
			public String getValue(RangeData object) {
				return String.valueOf(object.getFirst());
			}
		};
		firstColumn.setSortable(true);
		sortHandler.setComparator(firstColumn, new Comparator<RangeData>() {
			@Override
			public int compare(RangeData o1, RangeData o2) {
				return (o1.getFirst() < o2.getFirst()) ? -1 : ((o1.getFirst() == o2.getFirst()) ? 0 : 1);
			}
		});
		rangesOfVariableGrid.addColumn(firstColumn, messages.columnFirst());
		rangesOfVariableGrid.setColumnWidth(firstColumn, 80, Unit.PX);

		// Stride
		Column<RangeData, String> strideColumn = new Column<RangeData, String>(new TextCell()) {
			@Override
			public String getValue(RangeData object) {
				return String.valueOf(object.getStride());
			}
		};
		strideColumn.setSortable(true);
		sortHandler.setComparator(strideColumn, new Comparator<RangeData>() {
			@Override
			public int compare(RangeData o1, RangeData o2) {
				return (o1.getStride() < o2.getStride()) ? -1 : ((o1.getStride() == o2.getStride()) ? 0 : 1);

			}
		});

		rangesOfVariableGrid.addColumn(strideColumn, messages.columnStride());
		// dataGrid.setColumnWidth(valuesColumn, 40, Unit.PX);

	}

	public Set<RangeData> selecetedRanges() {
		@SuppressWarnings("unchecked")
		Set<RangeData> selected = ((MultiSelectionModel<RangeData>) rangesOfVariableGrid.getSelectionModel())
				.getSelectedSet();
		return selected;

	}

}
