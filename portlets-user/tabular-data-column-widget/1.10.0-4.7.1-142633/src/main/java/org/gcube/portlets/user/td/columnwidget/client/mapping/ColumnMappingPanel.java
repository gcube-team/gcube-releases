package org.gcube.portlets.user.td.columnwidget.client.mapping;

import java.util.ArrayList;

import org.gcube.portlets.user.td.columnwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowsProperties;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingData;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent.TriggerClickHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnMappingPanel extends FramedPanel {
	private static final String COMBOWIDTH = "220px";
	private static final String CONTAINERHEIGHT = "340px";
	private static final String CONTAINERWIDTH = "500px";

	private ColumnMappingPanel thisPanel;
	private ColumnMappingDialog parent;
	private ColumnData selectedColumn;
	private ColumnData referenceColumn;
	private EventBus eventBus;

	private VerticalLayoutContainer vert;
	private String itemIdSourceValueArg;
	private String itemIdTargetValueArg;
	private String itemIdBtnAdd;
	private String itemIdBtnDel;

	private ColumnMappingList columnMappingList;
	private ArrayList<ColumnMappingData> mapping;

	private TextButton btnSave;
	private TextButton btnClose;
	private ColumnMappingMessages msgs;
	private CommonMessages msgsCommon;

	/**
	 * 
	 * 
	 * @param parent
	 * @param trId
	 * @param selectedColumn
	 * @param dimensionTR
	 * @param referenceColumn
	 * @param eventBus
	 */
	public ColumnMappingPanel(ColumnMappingDialog parent, TRId trId,
			ColumnData selectedColumn, TabResource dimensionTR,
			ColumnData referenceColumn, EventBus eventBus) {
		this.parent = parent;
		this.selectedColumn = selectedColumn;
		this.referenceColumn = referenceColumn;
		this.eventBus = eventBus;
		thisPanel = this;
		Log.debug("ColumnMappingPanel: [parent:" + parent + " , trId:" + trId
				+ ", selectedColumn:" + selectedColumn + ", dimensionTR:"
				+ dimensionTR + ", columnReference:" + referenceColumn
				+ ", eventBus:" + eventBus + "]");
		columnMappingList = new ColumnMappingList();
		mapping = new ArrayList<ColumnMappingData>();
		initMessages();
		initPanel();
		create();
	}

	protected void initMessages() {
		msgs = GWT.create(ColumnMappingMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void initPanel() {
		setHeaderVisible(false);
		setBodyBorder(false);

	}

	protected void create() {
		itemIdSourceValueArg = "SourceArg" + selectedColumn.getName();
		itemIdTargetValueArg = "TargetArg" + selectedColumn.getName();

		SimpleContainer container = new SimpleContainer();
		container.setHeight(CONTAINERHEIGHT);
		container.setWidth(CONTAINERWIDTH);

		btnSave = new TextButton(msgs.btnSaveText());
		btnSave.setIcon(ResourceBundle.INSTANCE.save());
		btnSave.setIconAlign(IconAlign.RIGHT);
		btnSave.setToolTip(msgs.btnSaveToolTip());
		btnSave.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Save");
				btnSave.disable();
				save();

			}
		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		flowButton.add(btnSave, new BoxLayoutData(new Margins(10, 4, 10, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(10, 4, 10, 4)));

		vert = new VerticalLayoutContainer();
		vert.setScrollMode(ScrollMode.AUTO);
		vert.setAdjustForScroll(true);

		setColumnMappingData();

		container.add(vert);
		container.forceLayout();

		FieldLabel rowsLabel = new FieldLabel(null, msgs.rowsLabel());
		rowsLabel.getElement().applyStyles("font-weight:bold");

		VerticalLayoutContainer vPanel = new VerticalLayoutContainer();
		vPanel.add(rowsLabel, new VerticalLayoutData(1, -1, new Margins(1)));
		vPanel.add(container, new VerticalLayoutData(1, -1));
		vPanel.add(flowButton, new VerticalLayoutData(1, -1, new Margins(1)));
		add(vPanel);
		forceLayout();

	}

	protected void save() {
		int lenght = vert.getWidgetCount();
		int i = 0;
		for (; i < lenght; i++) {
			HBoxLayoutContainer h = (HBoxLayoutContainer) vert.getWidget(i);
			if (h != null) {
				@SuppressWarnings("unchecked")
				ComboBox<DimensionRow> comboSourceValue = (ComboBox<DimensionRow>) h
						.getItemByItemId(itemIdSourceValueArg);
				DimensionRow sourceValue = comboSourceValue.getValue();
				if (sourceValue != null) {
					@SuppressWarnings("unchecked")
					ComboBox<DimensionRow> comboTargetValue = (ComboBox<DimensionRow>) h
							.getItemByItemId(itemIdTargetValueArg);
					DimensionRow targetValue = comboTargetValue.getValue();
					if (targetValue != null) {
						ColumnMappingData columnMappingData = new ColumnMappingData(
								sourceValue, targetValue);
						mapping.add(columnMappingData);
					}
				}
			} else {

			}
		}
		if (mapping.size() > 0) {
			columnMappingList = new ColumnMappingList("1", "map", mapping);
			if (parent != null) {
				parent.saveMapping(columnMappingList);
			}
		} else {
			UtilsGXT3.info(msgsCommon.attention(), msgs.createAValidMapping());
			btnSave.enable();
		}

	}

	protected void close() {
		if (parent != null) {
			parent.close();
		}
		hide();
	}

	protected void setColumnMappingData() {

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Source Combo
		DimensionRowsProperties propsSource = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboSourceValue = new ListStore<DimensionRow>(
				propsSource.rowId());

		final ComboBox<DimensionRow> comboSourceValue = new ComboBox<DimensionRow>(
				storeComboSourceValue, propsSource.value());
		comboSourceValue.setItemId(itemIdSourceValueArg);

		Log.debug("ComboSourceValue created");

		final DimensionRowSelectionListener sourceValueSelectedListener = new DimensionRowSelectionListener() {

			@Override
			public void selectedDimensionRow(DimensionRow dimensionRow) {
				Log.debug("Source DimensionRow selected: " + dimensionRow);
				comboSourceValue.setValue(dimensionRow, true);

			}

			@Override
			public void abortedDimensionRowSelection() {
				Log.debug("Source DimensionRow selection aborted");

			}

			@Override
			public void failedDimensionRowSelection(String reason, String detail) {
				Log.error("Source DimensionRow selection error: " + reason);

			}

		};

		comboSourceValue.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboSourceValue TriggerClickEvent");
				comboSourceValue.collapse();

				CellData cellData = new CellData("", selectedColumn.getName(),
						selectedColumn.getColumnId(),
						selectedColumn.getLabel(), null, 0, 0);

				DimensionRowSelectionDialog dimensionRowSelectionDialog = new DimensionRowSelectionDialog(
						selectedColumn, cellData, true, false, true, false,
						true, eventBus);
				dimensionRowSelectionDialog
						.addListener(sourceValueSelectedListener);
				dimensionRowSelectionDialog.show();

			}

		});

		comboSourceValue.setEmptyText(msgs.comboSourceValueEmptyText());
		comboSourceValue.setWidth(COMBOWIDTH);
		comboSourceValue.setEditable(false);
		comboSourceValue.setTriggerAction(TriggerAction.ALL);

		// Target Combo
		DimensionRowsProperties propsTarget = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboTargetValue = new ListStore<DimensionRow>(
				propsTarget.rowId());

		final ComboBox<DimensionRow> comboTargetValue = new ComboBox<DimensionRow>(
				storeComboTargetValue, propsTarget.value());
		comboTargetValue.setItemId(itemIdTargetValueArg);

		Log.debug("ComboTargetValue created");

		final DimensionRowSelectionListener targetValueSelectedListener = new DimensionRowSelectionListener() {

			@Override
			public void selectedDimensionRow(DimensionRow dimensionRow) {
				Log.debug("Target DimensionRow selected: " + dimensionRow);
				comboTargetValue.setValue(dimensionRow, true);

			}

			@Override
			public void abortedDimensionRowSelection() {
				Log.debug("Target DimensionRow selection aborted");

			}

			@Override
			public void failedDimensionRowSelection(String reason, String detail) {
				Log.error("Target DimensionRow selection error: " + reason);

			}

		};

		comboTargetValue.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboTargetValue TriggerClickEvent");
				comboTargetValue.collapse();

				CellData cellData = new CellData("", referenceColumn.getName(),
						referenceColumn.getColumnId(), referenceColumn
								.getLabel(), null, 0, 0);

				DimensionRowSelectionDialog dimensionRowSelectionDialog = new DimensionRowSelectionDialog(
						referenceColumn, cellData, true, false, true, false,
						true, eventBus);
				dimensionRowSelectionDialog
						.addListener(targetValueSelectedListener);
				dimensionRowSelectionDialog.show();

			}

		});

		comboTargetValue.setEmptyText(msgs.comboTargetValueEmptyText());
		comboTargetValue.setWidth(COMBOWIDTH);
		comboTargetValue.setEditable(false);
		comboTargetValue.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ResourceBundle.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				addColumnMappingData();
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnAdd.setVisible(true);

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(ResourceBundle.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setColumnMappingData();
				} else {

				}
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnDel.setVisible(false);

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		horiz.add(comboSourceValue, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboTargetValue, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

	protected void addColumnMappingData() {
		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Source Combo
		DimensionRowsProperties propsSource = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboSourceValue = new ListStore<DimensionRow>(
				propsSource.rowId());

		final ComboBox<DimensionRow> comboSourceValue = new ComboBox<DimensionRow>(
				storeComboSourceValue, propsSource.value());
		comboSourceValue.setItemId(itemIdSourceValueArg);

		Log.debug("ComboSourceValue created");

		final DimensionRowSelectionListener sourceValueSelectedListener = new DimensionRowSelectionListener() {

			@Override
			public void selectedDimensionRow(DimensionRow dimensionRow) {
				Log.debug("Source DimensionRow selected: " + dimensionRow);
				comboSourceValue.setValue(dimensionRow, true);

			}

			@Override
			public void abortedDimensionRowSelection() {
				Log.debug("Source DimensionRow selection aborted");

			}

			@Override
			public void failedDimensionRowSelection(String reason, String detail) {
				Log.error("Source DimensionRow selection error: " + reason);

			}

		};

		comboSourceValue.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboSourceValue TriggerClickEvent");
				comboSourceValue.collapse();

				CellData cellData = new CellData("", selectedColumn.getName(),
						selectedColumn.getColumnId(),
						selectedColumn.getLabel(), null, 0, 0);

				DimensionRowSelectionDialog dimensionRowSelectionDialog = new DimensionRowSelectionDialog(
						selectedColumn, cellData, true, false, true, false,
						true, eventBus);
				dimensionRowSelectionDialog
						.addListener(sourceValueSelectedListener);
				dimensionRowSelectionDialog.show();

			}

		});

		comboSourceValue.setEmptyText(msgs.comboSourceValueEmptyText());
		comboSourceValue.setWidth(COMBOWIDTH);
		comboSourceValue.setEditable(false);
		comboSourceValue.setTriggerAction(TriggerAction.ALL);

		// Target Combo
		DimensionRowsProperties propsTarget = GWT
				.create(DimensionRowsProperties.class);
		ListStore<DimensionRow> storeComboTargetValue = new ListStore<DimensionRow>(
				propsTarget.rowId());

		final ComboBox<DimensionRow> comboTargetValue = new ComboBox<DimensionRow>(
				storeComboTargetValue, propsTarget.value());
		comboTargetValue.setItemId(itemIdTargetValueArg);

		Log.debug("ComboTargetValue created");

		final DimensionRowSelectionListener targetValueSelectedListener = new DimensionRowSelectionListener() {

			@Override
			public void selectedDimensionRow(DimensionRow dimensionRow) {
				Log.debug("Target DimensionRow selected: " + dimensionRow);
				comboTargetValue.setValue(dimensionRow, true);

			}

			@Override
			public void abortedDimensionRowSelection() {
				Log.debug("Target DimensionRow selection aborted");

			}

			@Override
			public void failedDimensionRowSelection(String reason, String detail) {
				Log.error("Target DimensionRow selection error: " + reason);

			}

		};

		comboTargetValue.addTriggerClickHandler(new TriggerClickHandler() {

			@Override
			public void onTriggerClick(TriggerClickEvent event) {
				Log.debug("ComboTargetValue TriggerClickEvent");
				comboTargetValue.collapse();

				CellData cellData = new CellData("", referenceColumn.getName(),
						referenceColumn.getColumnId(), referenceColumn
								.getLabel(), null, 0, 0);

				DimensionRowSelectionDialog dimensionRowSelectionDialog = new DimensionRowSelectionDialog(
						referenceColumn, cellData, true, false, true, false,
						true, eventBus);
				dimensionRowSelectionDialog
						.addListener(targetValueSelectedListener);
				dimensionRowSelectionDialog.show();

			}

		});

		comboTargetValue.setEmptyText(msgs.comboTargetValueEmptyText());
		comboTargetValue.setWidth(COMBOWIDTH);
		comboTargetValue.setEditable(false);
		comboTargetValue.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ResourceBundle.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				addColumnMappingData();
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnAdd.setVisible(true);

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(ResourceBundle.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setColumnMappingData();
				} else {

				}
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnDel.setVisible(true);

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		horiz.add(comboSourceValue, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboTargetValue, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

}
