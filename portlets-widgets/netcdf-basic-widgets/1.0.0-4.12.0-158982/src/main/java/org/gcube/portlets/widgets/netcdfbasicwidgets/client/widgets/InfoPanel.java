package org.gcube.portlets.widgets.netcdfbasicwidgets.client.widgets;

import java.util.Comparator;
import java.util.Set;

import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.event.NetCDFDataEvent.NetCDFDataEventHandler;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.model.NetCDFDataModel;
import org.gcube.portlets.widgets.netcdfbasicwidgets.client.resource.NetCDFBasicResources;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.AttributeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFDetailData;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InfoPanel extends SimplePanel implements NetCDFDataEventHandler {
	private static final String HEIGHT = "350px";
	private static final String WIDTH = "680px";

	private static final String DATA_GRID_HEIGHT = "250px";
	private static final String DATA_GRID_WIDTH = "608px";

	private final NetCDFPreviewMessages messages = GWT.create(NetCDFPreviewMessages.class);

	private NetCDFDataModel netCDFDataModel;
	private DataGrid<AttributeData> globalAttributesDataGrid;
	private SimplePager globalAttributesPager;

	private TextBox typeId;
	private TextBox typeDescription;
	private TextBox typeVersion;

	public InfoPanel(NetCDFDataModel netCDFDataModel) {
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

		createGrid();

		// ////////
		// Form
		FlexTable infoFlexTable = new FlexTable();
		infoFlexTable.setCellSpacing(2);

		/*
		 * info = new TextBox(); info.setReadOnly(true);
		 * infoFlexTable.setHTML(0, 0, "Info:"); infoFlexTable.setWidget(0, 1,
		 * info);
		 * 
		 * title = new TextBox(); title.setReadOnly(true);
		 * infoFlexTable.setHTML(1, 0, "Title:"); infoFlexTable.setWidget(1, 1,
		 * title);
		 */

		typeId = new TextBox();
		typeId.setReadOnly(true);
		infoFlexTable.setHTML(0, 0, "Id:");
		infoFlexTable.setWidget(0, 1, typeId);

		typeDescription = new TextBox();
		typeDescription.setReadOnly(true);
		infoFlexTable.setHTML(1, 0, "Descr.:");
		infoFlexTable.setWidget(1, 1, typeDescription);

		typeVersion = new TextBox();
		typeVersion.setReadOnly(true);
		infoFlexTable.setHTML(2, 0, "Version:");
		infoFlexTable.setWidget(2, 1, typeVersion);

		infoFlexTable.getFlexCellFormatter().setVerticalAlignment(3, 0, HasVerticalAlignment.ALIGN_TOP);
		infoFlexTable.setHTML(3, 0, "Glob.Attrs:");

		globalAttributesPager.getElement().getStyle().setProperty("margin", "auto");

		VerticalPanel vp = new VerticalPanel();
		vp.getElement().getStyle().setBackgroundColor("#f8f8fb");
		vp.add(globalAttributesDataGrid);
		vp.add(globalAttributesPager);

		infoFlexTable.setWidget(3, 1, vp);

		setWidget(infoFlexTable);

	}

	@Override
	public void onNetCDFDataReady(NetCDFDataEvent event) {
		NetCDFDetailData detail = event.getNetCDFData().getDetail();

		// info.setValue(detail.getInfo());
		// title.setValue(detail.getTitle());
		typeId.setValue(detail.getTypeId());
		typeDescription.setValue(detail.getTypeDescription());
		typeVersion.setValue(detail.getTypeVersion());

		// Add the CellList to the adapter in the database.
		netCDFDataModel.addGlobalAttributeDataDisplay(globalAttributesDataGrid);

		netCDFDataModel.refreshGlobalAttributeDisplays();

	}

	private void createGrid() {

		/*
		 * Set a key provider that provides a unique key for each contact. If
		 * key is used to identify contacts when fields (such as the name and
		 * address) change.
		 */

		globalAttributesDataGrid = new DataGrid<AttributeData>(7, AttributeData.KEY_PROVIDER);
		globalAttributesDataGrid.setWidth(DATA_GRID_WIDTH);
		globalAttributesDataGrid.setHeight(DATA_GRID_HEIGHT);
		globalAttributesDataGrid.addStyleName(NetCDFBasicResources.INSTANCE.netCDFBasicCSS().getCellWordWrap());

		/*
		 * Do not refresh the headers every time the data is updated. The footer
		 * depends on the current data, so we do not disable auto refresh on the
		 * footer.
		 */
		globalAttributesDataGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		globalAttributesDataGrid.setEmptyTableWidget(new Label(messages.dataGridGlobalAttributeEmpty()));

		// Attach a column sort handler to the ListDataProvider to sort the
		// list.
		ListHandler<AttributeData> sortHandler = new ListHandler<AttributeData>(
				netCDFDataModel.getGlobalAttributeDataProvider().getList());
		globalAttributesDataGrid.addColumnSortHandler(sortHandler);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		globalAttributesPager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		globalAttributesPager.setDisplay(globalAttributesDataGrid);

		// Add a selection model so we can select cells.
		final MultiSelectionModel<AttributeData> selectionModel = new MultiSelectionModel<AttributeData>(
				AttributeData.KEY_PROVIDER);
		globalAttributesDataGrid.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<AttributeData>createCheckboxManager());

		// Initialize the columns.
		initTableColumns(selectionModel, sortHandler);

	}

	public void refresh() {
		globalAttributesDataGrid.redraw();
	}

	private void initTableColumns(SelectionModel<AttributeData> selectionModel,
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
		globalAttributesDataGrid.addColumn(fullnameColum, messages.columnFullname());
		globalAttributesDataGrid.setColumnWidth(fullnameColum, 130, Unit.PX);

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
		globalAttributesDataGrid.addColumn(dataTypeColumn, messages.columnDataType());
		globalAttributesDataGrid.setColumnWidth(dataTypeColumn, 100, Unit.PX);

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

		globalAttributesDataGrid.addColumn(valuesColumn, messages.columnValue());
		// dataGrid.setColumnWidth(valuesColumn, 40, Unit.PX);

	}

	public Set<AttributeData> seleceted() {
		@SuppressWarnings("unchecked")
		Set<AttributeData> selected = ((MultiSelectionModel<AttributeData>) globalAttributesDataGrid
				.getSelectionModel()).getSelectedSet();
		return selected;

	}

}
