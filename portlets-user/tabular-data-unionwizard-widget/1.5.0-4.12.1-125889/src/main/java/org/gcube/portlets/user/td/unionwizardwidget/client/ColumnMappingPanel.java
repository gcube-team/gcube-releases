package org.gcube.portlets.user.td.unionwizardwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.union.UnionColumnsMapping;
import org.gcube.portlets.user.td.unionwizardwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.unionwizardwidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.unionwizardwidget.client.resources.UnionResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.RelationshipData;
import org.gcube.portlets.user.td.wizardwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.ResizeContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnMappingPanel extends ContentPanel {

	private static final String COMBOWIDTH = "200px";
	private UnionWizardMessages msgs;
	private CommonMessages msgsCommon;

	private ColumnMappingCard parent;
	private VerticalLayoutContainer vert;
	private ArrayList<ColumnData> sourceColumns;
	private ArrayList<ColumnData> unionColumns;
	private ResizeContainer thisPanel;

	private ArrayList<UnionColumnsMapping> listUnionColumnsMapping;

	private String itemIdSourceColumn = "itemIdSourceColumn";
	private String itemIdUnionColumn = "itemIdUnionColumn";
	private String itemIdBtnAdd = "itemIdBtnAdd";
	private String itemIdBtnDel = "itemIdBtnDel";

	/**
	 * 
	 * @param parent
	 * @param res
	 */
	public ColumnMappingPanel(ColumnMappingCard parent, ResourceBundle res) {
		super();
		this.parent = parent;
		thisPanel = this;
		initMessages();

		Log.debug("ColumnMappingPanel");
		init();
		retrieveSourceColumns();
	}

	protected void initMessages() {
		msgs = GWT.create(UnionWizardMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void init() {
		setHeaderVisible(false);
		setBodyStyle("backgroundColor:#DFE8F6;");
		forceLayoutOnResize = true;
	}

	protected void create() {

		SimpleContainer container = new SimpleContainer();

		vert = new VerticalLayoutContainer();
		vert.setScrollMode(ScrollMode.AUTO);
		vert.setAdjustForScroll(true);
		container.add(vert);

		setColumnMap();

		String currentTRLabel = parent.getUnionSession()
				.getCurrentTabularResource().getName();
		FieldLabel sourceColumnLabel = new FieldLabel(null, currentTRLabel);
		sourceColumnLabel.getElement().applyStyles("font-weight:bold");
		sourceColumnLabel.setWidth(COMBOWIDTH);
		sourceColumnLabel.setLabelSeparator("");

		String unionTRLabel = parent.getUnionSession()
				.getUnionTabularResource().getName();
		FieldLabel unionColumnLabel = new FieldLabel(null, unionTRLabel);
		unionColumnLabel.getElement().applyStyles("font-weight:bold");
		unionColumnLabel.setWidth(COMBOWIDTH);
		unionColumnLabel.setLabelSeparator("");

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		horiz.add(sourceColumnLabel, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(unionColumnLabel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		VerticalLayoutContainer vPanel = new VerticalLayoutContainer();
		vPanel.setScrollMode(ScrollMode.AUTO);
		vPanel.setAdjustForScroll(true);

		vPanel.add(horiz, new VerticalLayoutData(1, -1,
				new Margins(1, 1, 1, 10)));
		vPanel.add(container, new VerticalLayoutData(1, -1, new Margins(1, 1,
				1, 10)));

		add(vPanel);
		forceLayout();

	}

	private void retrieveSourceColumns() {
		TRId trId = parent.getUnionSession().getTrId();
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.debug("Error retrieving source columns: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										msgs.errorRetrievingSourceColumns());
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						sourceColumns = result;
						retrieveUnionColumns();

					}
				});

	}

	private void retrieveUnionColumns() {
		TRId trId = parent.getUnionSession().getUnionTabularResource()
				.getTrId();
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							parent.getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.debug("Error retrieving union columns: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										msgs.errorRetrievingUnionColumns());
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						unionColumns = result;
						create();

					}
				});

	}

	protected void setColumnMap() {

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Source Combo
		ColumnDataProperties propsSourceColumn = GWT
				.create(ColumnDataProperties.class);
		ListStore<ColumnData> storeComboSourceColumn = new ListStore<ColumnData>(
				propsSourceColumn.id());
		storeComboSourceColumn.addAll(sourceColumns);

		final ComboBox<ColumnData> comboSourceColumn = new ComboBox<ColumnData>(
				storeComboSourceColumn, propsSourceColumn.label());
		comboSourceColumn.setItemId(itemIdSourceColumn);

		Log.debug("ComboSourceColumn created");

		comboSourceColumn.setEmptyText(msgs.comboSourceColumnEmptyText());
		comboSourceColumn.setWidth(COMBOWIDTH);
		comboSourceColumn.setEditable(false);
		comboSourceColumn.setTriggerAction(TriggerAction.ALL);

		// Union Combo
		ColumnDataProperties propsUnionColumn = GWT
				.create(ColumnDataProperties.class);
		final ListStore<ColumnData> storeComboUnionColumn = new ListStore<ColumnData>(
				propsUnionColumn.id());

		final ComboBox<ColumnData> comboUnionColumn = new ComboBox<ColumnData>(
				storeComboUnionColumn, propsUnionColumn.label());
		comboUnionColumn.setItemId(itemIdUnionColumn);

		Log.debug("ComboUnionColumn created");

		comboUnionColumn.disable();
		comboUnionColumn.setEmptyText(msgs.comboUnionEmptyText());
		comboUnionColumn.setWidth(COMBOWIDTH);
		comboUnionColumn.setEditable(false);
		comboUnionColumn.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(UnionResourceBundle.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				addColumnMap();
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnAdd.setVisible(true);

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(UnionResourceBundle.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setColumnMap();
				} else {

				}
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnDel.setVisible(false);

		comboSourceColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						comboUnionColumn.reset();
						comboUnionColumn.clear();
						storeComboUnionColumn.clear();
						storeComboUnionColumn.commitChanges();
						ColumnData selectedSourceColumn = event
								.getSelectedItem();

						updateStoreComboUnionColumn(selectedSourceColumn,
								storeComboUnionColumn);

						storeComboUnionColumn.commitChanges();
						comboUnionColumn.redraw();
						comboUnionColumn.enable();
						forceLayout();
					}
				});

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		horiz.add(comboSourceColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboUnionColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

	protected void addColumnMap() {

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		// Source Combo
		ColumnDataProperties propsSourceColumn = GWT
				.create(ColumnDataProperties.class);
		ListStore<ColumnData> storeComboSourceColumn = new ListStore<ColumnData>(
				propsSourceColumn.id());
		storeComboSourceColumn.addAll(sourceColumns);

		final ComboBox<ColumnData> comboSourceColumn = new ComboBox<ColumnData>(
				storeComboSourceColumn, propsSourceColumn.label());
		comboSourceColumn.setItemId(itemIdSourceColumn);

		Log.debug("ComboSourceColumn created");

		comboSourceColumn.setEmptyText(msgs.comboSourceColumnEmptyText());
		comboSourceColumn.setWidth(COMBOWIDTH);
		comboSourceColumn.setEditable(false);
		comboSourceColumn.setTriggerAction(TriggerAction.ALL);

		// Union Combo
		ColumnDataProperties propsUnionColumn = GWT
				.create(ColumnDataProperties.class);
		final ListStore<ColumnData> storeComboUnionColumn = new ListStore<ColumnData>(
				propsUnionColumn.id());

		final ComboBox<ColumnData> comboUnionColumn = new ComboBox<ColumnData>(
				storeComboUnionColumn, propsUnionColumn.label());
		comboUnionColumn.setItemId(itemIdUnionColumn);

		Log.debug("ComboUnionColumn created");

		comboUnionColumn.disable();
		comboUnionColumn.setEmptyText(msgs.comboUnionEmptyText());
		comboUnionColumn.setWidth(COMBOWIDTH);
		comboUnionColumn.setEditable(false);
		comboUnionColumn.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(UnionResourceBundle.INSTANCE.add());
		btnAdd.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnAdd");
				addColumnMap();
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnAdd.setVisible(true);

		final IconButton btnDel = new IconButton();
		btnDel.setItemId(itemIdBtnDel);
		btnDel.setIcon(UnionResourceBundle.INSTANCE.delete());
		btnDel.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Log.debug("Clicked btnDel");
				vert.remove(horiz);
				if (vert.getWidgetCount() == 0) {
					setColumnMap();
				} else {

				}
				thisPanel.forceLayout();
				vert.forceLayout();

			}
		});
		btnDel.setVisible(true);

		comboSourceColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						comboUnionColumn.reset();
						comboUnionColumn.clear();
						storeComboUnionColumn.clear();
						storeComboUnionColumn.commitChanges();

						ColumnData selectedSourceColumn = event
								.getSelectedItem();

						updateStoreComboUnionColumn(selectedSourceColumn,
								storeComboUnionColumn);

						storeComboUnionColumn.commitChanges();
						comboUnionColumn.redraw();
						comboUnionColumn.enable();
					}
				});

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		horiz.add(comboSourceColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboUnionColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

	protected void updateStoreComboUnionColumn(ColumnData selectedSourceColumn,
			ListStore<ColumnData> storeComboUnionColumn) {
		for (ColumnData col : unionColumns) {
			if (selectedSourceColumn.getTypeCode().compareTo(
					ColumnTypeCode.DIMENSION.toString()) == 0) {
				if (col.getTypeCode().compareTo(
						ColumnTypeCode.DIMENSION.toString()) == 0) {
					RelationshipData sourceRelData = selectedSourceColumn
							.getRelationship();
					RelationshipData colRelData = col.getRelationship();
					if (sourceRelData != null
							&& colRelData != null
							&& sourceRelData.getTargetTableId() != null
							&& colRelData.getTargetTableId() != null
							&& sourceRelData.getTargetTableId().compareTo(
									colRelData.getTargetTableId()) == 0) {
						storeComboUnionColumn.add(col);
					}

				}
			} else {
				if (selectedSourceColumn.getTypeCode().compareTo(
						ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
					if (col.getTypeCode().compareTo(
							ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
						RelationshipData sourceRelData = selectedSourceColumn
								.getRelationship();
						RelationshipData colRelData = col.getRelationship();

						if (sourceRelData != null
								&& colRelData != null
								&& sourceRelData.getTargetTableId() != null
								&& colRelData.getTargetTableId() != null
								&& sourceRelData.getTargetTableId().compareTo(
										colRelData.getTargetTableId()) == 0
								&& selectedSourceColumn.getPeriodDataType()
										.compareTo(col.getPeriodDataType()) == 0) {
							storeComboUnionColumn.add(col);
						}

					}

				} else {
					if (selectedSourceColumn.getDataTypeName().compareTo(
							ColumnDataType.Text.toString()) == 0
							|| col.getDataTypeName().compareTo(
									ColumnDataType.Text.toString()) == 0) {
						storeComboUnionColumn.add(col);
					} else {
						if ((col.getDataTypeName().compareTo(
								ColumnDataType.Integer.toString()) == 0 && selectedSourceColumn
								.getDataTypeName().compareTo(
										ColumnDataType.Numeric.toString()) == 0)
								|| (col.getDataTypeName().compareTo(
										ColumnDataType.Numeric.toString()) == 0 && selectedSourceColumn
										.getDataTypeName().compareTo(
												ColumnDataType.Integer
														.toString()) == 0)) {
							storeComboUnionColumn.add(col);
						} else {
							if (col.getDataTypeName().compareTo(
									selectedSourceColumn.getDataTypeName()) == 0) {
								storeComboUnionColumn.add(col);
							} else {

							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	protected ArrayList<UnionColumnsMapping> getSelectedMap() {
		listUnionColumnsMapping = new ArrayList<UnionColumnsMapping>();

		int lenght = vert.getWidgetCount();
		int i = 0;
		for (; i < lenght; i++) {
			HBoxLayoutContainer h = (HBoxLayoutContainer) vert.getWidget(i);
			if (h != null) {
				@SuppressWarnings("unchecked")
				ComboBox<ColumnData> comboSourceColumn = (ComboBox<ColumnData>) h
						.getItemByItemId(itemIdSourceColumn);
				ColumnData sourceColumn = comboSourceColumn.getCurrentValue();
				if (sourceColumn != null) {
					@SuppressWarnings("unchecked")
					ComboBox<ColumnData> comboUnionColumn = (ComboBox<ColumnData>) h
							.getItemByItemId(itemIdUnionColumn);
					ColumnData unionColumn = comboUnionColumn.getCurrentValue();
					if (unionColumn != null) {
						UnionColumnsMapping colMatch = new UnionColumnsMapping(
								"default", sourceColumn, unionColumn);
						listUnionColumnsMapping.add(colMatch);
					} else {
						Log.debug("Union Column is null");
					}
				} else {
					Log.debug("Source Column is null");
				}
			} else {
				Log.debug("HorizontalContainer is null");
			}
		}

		return listUnionColumnsMapping;

	}

}
