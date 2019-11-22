package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import java.util.Comparator;
import java.util.Set;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent.NetCDFDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.model.NetCDFDataModel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource.NetCDFBasicResources;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class VariablesPanel extends SimplePanel implements NetCDFDataEventHandler {
	private static final String DATA_GRID_HEIGHT = "300px";
	private static final String DATA_GRID_WIDTH = "700px";

	private final NetCDFPreviewMessages messages = GWT.create(NetCDFPreviewMessages.class);

	private NetCDFDataModel netCDFDataModel;
	private DataGrid<VariableData> variablesDataGrid;
	private SimplePager variablesDataPager;

	public VariablesPanel(NetCDFDataModel netCDFDataModel) {
		this.netCDFDataModel = netCDFDataModel;
		create();
	}

	private void create() {
		netCDFDataModel.addNetCDFDataEventHandler(this);
		setStyleName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getAreaSelectionPanel());

		createGrid();

		// ////////
		// Form
		FlexTable layout = new FlexTable();
		layout.setCellSpacing(10);

		FlexTable netCDFPreviewFlexTable = new FlexTable();
		// netCDFPreviewFlexTable.setStyleName(NetCDFBasicResources.INSTANCE.olBasicCSS().getAreaSelectionContent());
		netCDFPreviewFlexTable.setCellSpacing(2);

		netCDFPreviewFlexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		netCDFPreviewFlexTable.setWidget(0, 0, variablesDataGrid);

		netCDFPreviewFlexTable.getFlexCellFormatter().setColSpan(1, 0, 2);

		variablesDataPager.getElement().getStyle().setProperty("margin", "auto");
		netCDFPreviewFlexTable.setWidget(1, 0, variablesDataPager);

		setWidget(netCDFPreviewFlexTable);

	}

	@Override
	public void onNetCDFDataReady(NetCDFDataEvent event) {
		// Add the CellList to the adapter in the database.
		netCDFDataModel.addVariableDataDisplay(variablesDataGrid);

		netCDFDataModel.refreshVariableDisplays();
	}

	private void createGrid() {

		/*
		 * Set a key provider that provides a unique key for each contact. If
		 * key is used to identify contacts when fields (such as the name and
		 * address) change.
		 */

		variablesDataGrid = new DataGrid<VariableData>(10, VariableData.KEY_PROVIDER);
		variablesDataGrid.setWidth(DATA_GRID_WIDTH);
		variablesDataGrid.setHeight(DATA_GRID_HEIGHT);
		variablesDataGrid.addStyleName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getCellWordWrap());

		/*
		 * Do not refresh the headers every time the data is updated. The footer
		 * depends on the current data, so we do not disable auto refresh on the
		 * footer.
		 */
		variablesDataGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		variablesDataGrid.setEmptyTableWidget(new Label(messages.dataGridVariablesEmpty()));

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		ListHandler<VariableData> sortHandler = new ListHandler<VariableData>(
				netCDFDataModel.getVariableDataProvider().getList());
		variablesDataGrid.addColumnSortHandler(sortHandler);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		variablesDataPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		variablesDataPager.setDisplay(variablesDataGrid);

		// Add a selection model so we can select cells.
		final MultiSelectionModel<VariableData> selectionModel = new MultiSelectionModel<VariableData>(
				VariableData.KEY_PROVIDER);
		variablesDataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<VariableData>createCheckboxManager());

		// Initialize the columns.
		initTableColumns(selectionModel, sortHandler);

	}

	public void refresh() {
		variablesDataGrid.redraw();
	}

	private void initTableColumns(SelectionModel<VariableData> selectionModel, ListHandler<VariableData> sortHandler) {
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
		Column<VariableData, String> fullnameColum = new Column<VariableData, String>(new TextCell()) {
			@Override
			public String getValue(VariableData object) {
				return object.getFullName();
			}
		};
		fullnameColum.setSortable(true);
		sortHandler.setComparator(fullnameColum, new Comparator<VariableData>() {

			@Override
			public int compare(VariableData o1, VariableData o2) {
				return o1.getFullName().compareTo(o2.getFullName());

			}
		});
		variablesDataGrid.addColumn(fullnameColum, messages.columnFullname());
		variablesDataGrid.setColumnWidth(fullnameColum, 200, Unit.PX);

		// Units
		Column<VariableData, String> unitsColumn = new Column<VariableData, String>(new TextCell()) {
			@Override
			public String getValue(VariableData object) {
				return object.getUnits();
			}
		};
		unitsColumn.setSortable(true);
		sortHandler.setComparator(unitsColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				if (o1.getUnits() == null) {
					return -1;
				} else {
					if (o2.getUnits() == null) {
						return 1;

					} else {
						return o1.getUnits().compareTo(o2.getUnits());

					}
				}
			}
		});
		variablesDataGrid.addColumn(unitsColumn, messages.columnUnits());
		variablesDataGrid.setColumnWidth(unitsColumn, 160, Unit.PX);

		// DataType
		Column<VariableData, String> dataTypeColumn = new Column<VariableData, String>(new TextCell()) {
			@Override
			public String getValue(VariableData object) {
				return object.getDataType();
			}
		};
		dataTypeColumn.setSortable(true);
		sortHandler.setComparator(dataTypeColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return o1.getDataType().compareTo(o2.getDataType());
			}
		});
		variablesDataGrid.addColumn(dataTypeColumn, messages.columnDataType());
		variablesDataGrid.setColumnWidth(dataTypeColumn, 100, Unit.PX);

		// Dimensions
		Column<VariableData, String> dimensionsColumn = new Column<VariableData, String>(new TextCell()) {
			@Override
			public String getValue(VariableData object) {
				return object.getDimensionString();
			}
		};
		dimensionsColumn.setSortable(true);
		sortHandler.setComparator(dimensionsColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return o1.getDimensionString().compareTo(o2.getDimensionString());
			}
		});
		variablesDataGrid.addColumn(dimensionsColumn, messages.columnDimensionString());
		variablesDataGrid.setColumnWidth(dimensionsColumn, 160, Unit.PX);

		// Rank
		Column<VariableData, String> rankColumn = new Column<VariableData, String>(new TextCell()) {
			@Override
			public String getValue(VariableData object) {
				return String.valueOf(object.getRank());
			}
		};
		rankColumn.setSortable(true);
		sortHandler.setComparator(rankColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.getRank() < o2.getRank()) ? -1 : ((o1.getRank() == o2.getRank()) ? 0 : 1);

			}
		});

		variablesDataGrid.addColumn(rankColumn, messages.columnRank());
		variablesDataGrid.setColumnWidth(rankColumn, 80, Unit.PX);

		// Coordinate
		Column<VariableData, Boolean> coordinateColumn = new Column<VariableData, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isCoordinateVariable();
			}
		};

		coordinateColumn.setSortable(true);
		sortHandler.setComparator(coordinateColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isCoordinateVariable() == o2.isCoordinateVariable()) ? 0
						: (o1.isCoordinateVariable() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(coordinateColumn, messages.columnCoordinateVariable());
		variablesDataGrid.setColumnWidth(coordinateColumn, 80, Unit.PX);

		// Scalar
		Column<VariableData, Boolean> scalarColumn = new Column<VariableData, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isScalar();
			}
		};
		scalarColumn.setSortable(true);
		sortHandler.setComparator(scalarColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isScalar() == o2.isScalar()) ? 0 : (o1.isScalar() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(scalarColumn, messages.columnScalar());
		variablesDataGrid.setColumnWidth(scalarColumn, 80, Unit.PX);

		// Immutable
		Column<VariableData, Boolean> immutableColumn = new Column<VariableData, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isImmutable();
			}
		};
		immutableColumn.setSortable(true);
		sortHandler.setComparator(immutableColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isImmutable() == o2.isImmutable()) ? 0 : (o1.isImmutable() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(immutableColumn, messages.columnImmutable());
		variablesDataGrid.setColumnWidth(immutableColumn, 110, Unit.PX);

		// Unlimited
		Column<VariableData, Boolean> unlimitedColumn = new Column<VariableData, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isUnlimited();
			}
		};
		unlimitedColumn.setSortable(true);
		sortHandler.setComparator(unlimitedColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isUnlimited() == o2.isUnlimited()) ? 0 : (o1.isUnlimited() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(unlimitedColumn, messages.columnUnlimited());
		variablesDataGrid.setColumnWidth(unlimitedColumn, 110, Unit.PX);

		// Unsigned
		Column<VariableData, Boolean> unsignedColumn = new Column<VariableData, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isUnsigned();
			}
		};
		unsignedColumn.setSortable(true);
		sortHandler.setComparator(unsignedColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isUnsigned() == o2.isUnsigned()) ? 0 : (o1.isUnsigned() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(unsignedColumn, messages.columnUnsigned());
		variablesDataGrid.setColumnWidth(unsignedColumn, 110, Unit.PX);

		// VariableLenght
		Column<VariableData, Boolean> variableLenghtColumn = new Column<VariableData, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isVariableLength();
			}
		};
		variableLenghtColumn.setSortable(true);
		sortHandler.setComparator(variableLenghtColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isVariableLength() == o2.isVariableLength()) ? 0 : (o1.isVariableLength() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(variableLenghtColumn, messages.columnVariableLenght());
		variablesDataGrid.setColumnWidth(variableLenghtColumn, 110, Unit.PX);

		// Member
		Column<VariableData, Boolean> memberColumn = new Column<VariableData, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(VariableData object) {
				return object.isMemberOfStructure();
			}
		};
		memberColumn.setSortable(true);
		sortHandler.setComparator(memberColumn, new Comparator<VariableData>() {
			@Override
			public int compare(VariableData o1, VariableData o2) {
				return (o1.isMemberOfStructure() == o2.isMemberOfStructure()) ? 0 : (o1.isMemberOfStructure() ? 1 : -1);
			}
		});

		variablesDataGrid.addColumn(memberColumn, messages.columnMemberOfStructure());
		variablesDataGrid.setColumnWidth(memberColumn, 90, Unit.PX);

	}

	public Set<VariableData> seleceted() {
		@SuppressWarnings("unchecked")
		Set<VariableData> selected = ((MultiSelectionModel<VariableData>) variablesDataGrid.getSelectionModel())
				.getSelectedSet();
		return selected;

	}

}
