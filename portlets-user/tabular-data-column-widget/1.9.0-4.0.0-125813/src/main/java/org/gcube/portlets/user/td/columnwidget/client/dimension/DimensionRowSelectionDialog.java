package org.gcube.portlets.user.td.columnwidget.client.dimension;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.Constants;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdwx.client.TabularDataX;
import org.gcube.portlets.user.tdwx.client.TabularDataXGridPanel;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent;
import org.gcube.portlets.user.tdwx.client.event.FailureEvent.FailureEventHandler;
import org.gcube.portlets.user.tdwx.shared.StaticFilterInformation;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class DimensionRowSelectionDialog extends Window {
	private static final int WIDTH = 550;
	private static final int HEIGHT = 520;

	private ColumnData column;
	
	private DimensionRow dimRow;
	private CellData cellData;
	private boolean workOnTable;
	private boolean showValue;
	private boolean errorNotColored;
	private boolean staticFilterBool;
	private boolean visibleOnlyColumn;

	private ArrayList<DimensionRowSelectionListener> listeners;

	private static TabularDataX tabularData;
	private TabularDataXGridPanel gridPanel;
	private TextField value;
	private TextButton btnSelect;
	private DimensionRowSelectionMessages msgs;
	private CommonMessages msgsCommon;

	public DimensionRowSelectionDialog(ColumnData column, CellData cellData,
			EventBus eventBus) {
		this.workOnTable = false;
		this.showValue = true;
		this.errorNotColored = false;
		this.staticFilterBool = false;
		this.visibleOnlyColumn = true;
		config(column, cellData, eventBus);
	}

	public DimensionRowSelectionDialog(ColumnData column, CellData cellData,
			boolean workOnTable, EventBus eventBus) {
		this.workOnTable = workOnTable;
		this.showValue = true;
		this.errorNotColored = false;
		this.staticFilterBool = false;
		this.visibleOnlyColumn = true;
		config(column, cellData, eventBus);

	}

	public DimensionRowSelectionDialog(ColumnData column, CellData cellData,
			boolean workOnTable, boolean showValue, EventBus eventBus) {
		this.workOnTable = workOnTable;
		this.showValue = showValue;
		this.errorNotColored = false;
		this.staticFilterBool = false;
		this.visibleOnlyColumn = true;
		config(column, cellData, eventBus);

	}

	public DimensionRowSelectionDialog(ColumnData column, CellData cellData,
			boolean workOnTable, boolean showValue, boolean errorNotColored,
			boolean staticFilterBool, boolean visibleOnlyColumn,
			EventBus eventBus) {
		this.workOnTable = workOnTable;
		this.showValue = showValue;
		this.errorNotColored = errorNotColored;
		this.staticFilterBool = staticFilterBool;
		this.visibleOnlyColumn = visibleOnlyColumn;
		config(column, cellData, eventBus);

	}

	protected void config(ColumnData column, CellData cellData,
			EventBus eventBus) {
		Log.debug("DimensionRowSelectionDialog: " + column + ", " + cellData);
		this.column = column;
		this.cellData = cellData;
		listeners = new ArrayList<DimensionRowSelectionListener>();
		initMessages();
		initWindow();
		create();
		open();
	}
	
	protected void initMessages(){
		msgs = GWT.create(DimensionRowSelectionMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	
	
	protected void create() {
		final FramedPanel panel = new FramedPanel();
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);

		VerticalLayoutContainer v = new VerticalLayoutContainer();

		if (showValue) {
			value = new TextField();
			if (cellData.getValue() != null) {
				value.setValue(cellData.getValue());
			}
			value.setReadOnly(true);
		}

		// Grid
		tabularData = new TabularDataX(Constants.TDX_DATASOURCE_FACTORY_ID);
		tabularData.addFailureHandler(new FailureEventHandler() {

			public void onFailure(FailureEvent event) {
				Info.display("Error: " + event.getMessage(), event.getCaught()
						.getMessage());

			}
		});

		gridPanel = tabularData.getGridPanel();
		gridPanel.setHeaderVisible(false);
		gridPanel.setErrorNotColored(errorNotColored);
		gridPanel.setSelectionModel(SelectionMode.SINGLE);

		Log.debug("Set Static Filter: " + staticFilterBool);
		if (staticFilterBool) {
			StaticFilterInformation sfi = new StaticFilterInformation(
					cellData.getColumnName(), cellData.getColumnId(),
					cellData.getValue());
			ArrayList<StaticFilterInformation> sfiList = new ArrayList<StaticFilterInformation>();
			sfiList.add(sfi);
			gridPanel.setStaticFilters(sfiList);
		}

		Log.debug("SetVisibleOnlyColumn: " + visibleOnlyColumn + " - " + column);
		if (visibleOnlyColumn) {
			if (workOnTable) {
				Log.debug("Work on Table");
				gridPanel.setVisibleOnlyColumn(column.getColumnId());
			} else {
				if (column.isViewColumn()) {
					Log.debug("column Is View Column");
					gridPanel.setVisibleOnlyColumn(column.getColumnId());
				} else {
					Log.debug("column Not Is View Column");
					if (column.getRelationship() != null) {
						// Used a Dimension Column
						gridPanel.setVisibleOnlyColumn(column.getRelationship()
								.getTargetColumnId());
					} else {

						UtilsGXT3
								.alert(msgsCommon.attention(),
										msgs.noValidViewColumnAssociatedWithThisColumn());
						hide();
						return;

					}
				}
			}
		}

		if (showValue) {
			v.add(new FieldLabel(value, msgs.valueLabel()), new VerticalLayoutData(1, -1));
		}
		v.add(gridPanel, new VerticalLayoutData(1, 1));

		panel.add(v);

		btnSelect = new TextButton(msgs.btnSelectText());
		btnSelect.setToolTip(msgs.btnSelectToolTip());
		btnSelect.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				startSelect();
			}
		});

		panel.addButton(btnSelect);

		add(panel);
		forceLayout();
	}

	protected void startSelect() {
		ArrayList<String> rowsId = gridPanel.getSelectedRowsId();
		if (rowsId == null || rowsId.size() == 0) {
			Log.debug("No row selected");
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectARow());
		} else {
			String rowId = rowsId.get(0);
			Log.debug("Row selected: " + rowId);
			ArrayList<String> cellValues;
			if (workOnTable) {
				cellValues = gridPanel.getCellValue(column.getColumnId());
			} else {
				if (column.isViewColumn()) {
					cellValues = gridPanel.getCellValue(column.getColumnId());
				} else {
					if (column.getRelationship() != null) {
						// Used a Dimension Column
						cellValues = gridPanel.getCellValue(column
								.getRelationship().getTargetColumnId());
					} else {
						UtilsGXT3
								.alert(msgsCommon.attention(),
										msgs.noValidViewColumnAssociatedWithThisColumn());
						hide();
						return;
					}
				}
			}

			if (cellValues == null || cellValues.size() == 0) {
				Log.debug("No value retrieved");
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectARow());
			} else {
				String cellValue = cellValues.get(0);
				Log.debug("Retrieved: " + rowId + " " + cellValue);
				dimRow = new DimensionRow(rowId, cellValue);
				fireCompleted(dimRow);
			}
		}

	}

	protected void open() {
		long tableId = 0;
		if (workOnTable) {
			tableId = Long.valueOf(column.getTrId().getTableId());
		} else {
			if (column.isViewColumn()) {
				tableId = column.getColumnViewData().getTargetTableId();
			} else {
				if (column.getRelationship() != null) {
					// Used a Dimension Column
					tableId = Long.valueOf(column.getRelationship()
							.getTargetTableId());
				} else {
					UtilsGXT3
							.alert(msgsCommon.attention(),
									msgs.noValidRelationshipAssociatedWithThisColumn());
					hide();
					return;
				}

			}
		}

		TableId tableOpening = new TableId(Constants.TDX_DATASOURCE_FACTORY_ID,
				String.valueOf(tableId));
		Log.debug("Open Table:" + tableId);
		tabularData.openTable(tableOpening);

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText(msgs.dialogHead());
		setClosable(true);
		// getHeader().setIcon(ResourceBundle.INSTANCE.replace());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				fireAborted();
				hide();
			}
		});

	}

	public void addListener(DimensionRowSelectionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(DimensionRowSelectionListener listener) {
		listeners.remove(listener);
	}

	public void fireCompleted(DimensionRow dimensionRow) {
		for (DimensionRowSelectionListener listener : listeners)
			listener.selectedDimensionRow(dimensionRow);
		hide();
	}

	public void fireAborted() {
		for (DimensionRowSelectionListener listener : listeners)
			listener.abortedDimensionRowSelection();
		hide();
	}

	public void fireFailed(String reason, String details) {
		for (DimensionRowSelectionListener listener : listeners)
			listener.failedDimensionRowSelection(reason, details);
		hide();
	}

}
