package org.gcube.portlets.user.td.replacebyexternalwidget.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal.ReplaceByExternalColumnsMapping;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.custom.IconButton;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.replacebyexternalwidget.client.resources.ReplaceByExternalResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
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
	// private static final String COLUMNLABELWIDTH = "120px";
	protected ResourceBundle res;
	protected ColumnMappingCard parent;
	protected VerticalLayoutContainer vert;
	protected ArrayList<ColumnData> currentColumns;
	protected ArrayList<ColumnData> externalColumns;
	protected ResizeContainer thisPanel;

	protected HashMap<ColumnData, ColumnData> columnMap;
	protected ArrayList<ReplaceByExternalColumnsMapping> listColumnsMapping;

	protected String itemIdCurrentColumn = "itemIdCurrentColumn";
	protected String itemIdExternalColumn = "itemIdExternalColumn";
	protected String itemIdLabelColumn = "itemIdLabelColumn";
	protected String itemIdBtnAdd = "itemIdBtnAdd";
	protected String itemIdBtnDel = "itemIdBtnDel";

	/**
	 * 
	 * @param parent
	 * @param res
	 */
	public ColumnMappingPanel(ColumnMappingCard parent, ResourceBundle res) {
		super();
		this.res = res;
		this.parent = parent;
		thisPanel = this;

		Log.debug("ColumnMappingPanel");
		init();
		currentColumns=parent.getReplaceByExternalSession().getCurrentColumns();
		retrieveExternalColumns();
	}

	public ArrayList<ColumnData> getCurrentColumns() {
		return currentColumns;
	}

	public ArrayList<ColumnData> getExternalColumns() {
		return externalColumns;
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

	

		String currentTRLabel = parent.replaceByExternalSession.getCurrentTabularResource()
				.getName();
		FieldLabel sourceColumnLabel = new FieldLabel(null, currentTRLabel);
		sourceColumnLabel.getElement().applyStyles("font-weight:bold");
		sourceColumnLabel.setWidth(COMBOWIDTH);
		sourceColumnLabel.setLabelSeparator("");

		String unionTRLabel = parent.replaceByExternalSession.getExternalTabularResource()
				.getName();
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

	

	private void retrieveExternalColumns() {
		TRId trId = parent.replaceByExternalSession.getExternalTabularResource().getTrId();
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
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());

							} else {
								Log.debug("Error retrieving union columns: "
										+ caught.getLocalizedMessage());
								UtilsGXT3
										.alert("Error retrieving external columns",
												"Error retrieving external columns on server!");
							}
						}
					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						externalColumns = result;
						create();

					}
				});

	}

	protected void setColumnMap() {

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();

		

		// Current Combo
		ColumnDataProperties propsCurrentColumn = GWT
				.create(ColumnDataProperties.class);
		ListStore<ColumnData> storeComboCurrentColumn = new ListStore<ColumnData>(
				propsCurrentColumn.id());
		storeComboCurrentColumn.addAll(currentColumns);

		final ComboBox<ColumnData> comboCurrentColumn = new ComboBox<ColumnData>(
				storeComboCurrentColumn, propsCurrentColumn.label());
		comboCurrentColumn.setItemId(itemIdCurrentColumn);

		Log.debug("ComboSourceColumn created");

		comboCurrentColumn.setEmptyText("Select a column...");
		comboCurrentColumn.setWidth(COMBOWIDTH);
		comboCurrentColumn.setEditable(false);
		comboCurrentColumn.setTriggerAction(TriggerAction.ALL);

		// External Combo
		ColumnDataProperties propsExternalColumn = GWT
				.create(ColumnDataProperties.class);
		final ListStore<ColumnData> storeComboExternalColumn = new ListStore<ColumnData>(
				propsExternalColumn.id());

		final ComboBox<ColumnData> comboExternalColumn = new ComboBox<ColumnData>(
				storeComboExternalColumn, propsExternalColumn.label());
		comboExternalColumn.setItemId(itemIdExternalColumn);

		Log.debug("ComboExternalColumn created");

		comboExternalColumn.disable();
		comboExternalColumn.setEmptyText("Select a column...");
		comboExternalColumn.setWidth(COMBOWIDTH);
		comboExternalColumn.setEditable(false);
		comboExternalColumn.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ReplaceByExternalResourceBundle.INSTANCE.add());
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
		btnDel.setIcon(ReplaceByExternalResourceBundle.INSTANCE.delete());
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

		comboCurrentColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						comboExternalColumn.reset();
						storeComboExternalColumn.clear();

						ColumnData selectedCurrentColumn = event
								.getSelectedItem();
						storeComboExternalColumn.clear();
						for (ColumnData col : externalColumns) {
							if (selectedCurrentColumn.getDataTypeName()
									.compareTo(ColumnDataType.Text.toString()) == 0
									|| col.getDataTypeName().compareTo(
											ColumnDataType.Text.toString()) == 0) {
								storeComboExternalColumn.add(col);
							} else {
								if ((col.getDataTypeName().compareTo(
										ColumnDataType.Integer.toString()) == 0 && selectedCurrentColumn
										.getDataTypeName().compareTo(
												ColumnDataType.Numeric
														.toString()) == 0)
										|| (col.getDataTypeName().compareTo(
												ColumnDataType.Numeric
														.toString()) == 0 && selectedCurrentColumn
												.getDataTypeName().compareTo(
														ColumnDataType.Integer
																.toString()) == 0)) {
									storeComboExternalColumn.add(col);
								} else {
									if (col.getDataTypeName().compareTo(
											selectedCurrentColumn
													.getDataTypeName()) == 0) {
										storeComboExternalColumn.add(col);
									} else {

									}
								}
							}
						}
						storeComboExternalColumn.commitChanges();
						comboExternalColumn.redraw();
						comboExternalColumn.enable();
					}
				});

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		// horiz.add(columnLabel, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboCurrentColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboExternalColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

	protected void addColumnMap() {

		final HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		
		// Current Combo
		ColumnDataProperties propsCurrentColumn = GWT
				.create(ColumnDataProperties.class);
		ListStore<ColumnData> storeComboCurrentColumn = new ListStore<ColumnData>(
				propsCurrentColumn.id());
		storeComboCurrentColumn.addAll(currentColumns);

		final ComboBox<ColumnData> comboCurrentColumn = new ComboBox<ColumnData>(
				storeComboCurrentColumn, propsCurrentColumn.label());
		comboCurrentColumn.setItemId(itemIdCurrentColumn);

		Log.debug("ComboCurrentColumn created");

		comboCurrentColumn.setEmptyText("Select a column...");
		comboCurrentColumn.setWidth(COMBOWIDTH);
		comboCurrentColumn.setEditable(false);
		comboCurrentColumn.setTriggerAction(TriggerAction.ALL);

		// External Combo
		ColumnDataProperties propsExternalColumn = GWT
				.create(ColumnDataProperties.class);
		final ListStore<ColumnData> storeComboExternalColumn = new ListStore<ColumnData>(
				propsExternalColumn.id());

		final ComboBox<ColumnData> comboExternalColumn = new ComboBox<ColumnData>(
				storeComboExternalColumn, propsExternalColumn.label());
		comboExternalColumn.setItemId(itemIdExternalColumn);

		Log.debug("ComboExternalColumn created");

		comboExternalColumn.disable();
		comboExternalColumn.setEmptyText("Select a column...");
		comboExternalColumn.setWidth(COMBOWIDTH);
		comboExternalColumn.setEditable(false);
		comboExternalColumn.setTriggerAction(TriggerAction.ALL);

		final IconButton btnAdd = new IconButton();
		btnAdd.setItemId(itemIdBtnAdd);
		btnAdd.setIcon(ReplaceByExternalResourceBundle.INSTANCE.add());
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
		btnDel.setIcon(ReplaceByExternalResourceBundle.INSTANCE.delete());
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

		comboCurrentColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					@Override
					public void onSelection(SelectionEvent<ColumnData> event) {
						comboExternalColumn.reset();
						storeComboExternalColumn.clear();

						ColumnData selectedCurrentColumn = event
								.getSelectedItem();
						storeComboExternalColumn.clear();
						for (ColumnData col : externalColumns) {
							if (selectedCurrentColumn.getDataTypeName()
									.compareTo(ColumnDataType.Text.toString()) == 0
									|| col.getDataTypeName().compareTo(
											ColumnDataType.Text.toString()) == 0) {
								storeComboExternalColumn.add(col);
							} else {
								if ((col.getDataTypeName().compareTo(
										ColumnDataType.Integer.toString()) == 0 && selectedCurrentColumn
										.getDataTypeName().compareTo(
												ColumnDataType.Numeric
														.toString()) == 0)
										|| (col.getDataTypeName().compareTo(
												ColumnDataType.Numeric
														.toString()) == 0 && selectedCurrentColumn
												.getDataTypeName().compareTo(
														ColumnDataType.Integer
																.toString()) == 0)) {
									storeComboExternalColumn.add(col);
								} else {
									if (col.getDataTypeName().compareTo(
											selectedCurrentColumn
													.getDataTypeName()) == 0) {
										storeComboExternalColumn.add(col);
									} else {

									}
								}
							}
						}
						storeComboExternalColumn.commitChanges();
						comboExternalColumn.redraw();
						comboExternalColumn.enable();
					}
				});

		horiz.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		horiz.setPack(BoxLayoutPack.START);

		// horiz.add(columnLabel, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboCurrentColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(comboExternalColumn, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnAdd, new BoxLayoutData(new Margins(2, 1, 2, 1)));
		horiz.add(btnDel, new BoxLayoutData(new Margins(2, 1, 2, 1)));

		vert.add(horiz);
	}

	/**
	 * 
	 * @return
	 */
	protected ArrayList<ReplaceByExternalColumnsMapping> getSelectedMap() {
		listColumnsMapping = new ArrayList<ReplaceByExternalColumnsMapping>();

		int lenght = vert.getWidgetCount();
		int i = 0;
		for (; i < lenght; i++) {
			HBoxLayoutContainer h = (HBoxLayoutContainer) vert.getWidget(i);
			if (h != null) {
				@SuppressWarnings("unchecked")
				ComboBox<ColumnData> comboCurrentColumn = (ComboBox<ColumnData>) h
						.getItemByItemId(itemIdCurrentColumn);
				ColumnData currentColumn = comboCurrentColumn.getCurrentValue();
				if (currentColumn != null) {
					@SuppressWarnings("unchecked")
					ComboBox<ColumnData> comboExternalColumn = (ComboBox<ColumnData>) h
							.getItemByItemId(itemIdExternalColumn);
					ColumnData externalColumn = comboExternalColumn.getCurrentValue();
					if (externalColumn != null) {
						ReplaceByExternalColumnsMapping colMatch = new ReplaceByExternalColumnsMapping(
								"default", currentColumn, externalColumn);
						listColumnsMapping.add(colMatch);
					} else {
						Log.debug("External Column is null");
					}
				} else {
					Log.debug("Current Column is null");
				}
				
			} else {
				Log.debug("HorizontalContainer is null");
			}
		}

		return listColumnsMapping;

	}

}
