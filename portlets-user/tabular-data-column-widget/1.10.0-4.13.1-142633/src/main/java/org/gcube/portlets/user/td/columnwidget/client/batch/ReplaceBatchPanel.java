package org.gcube.portlets.user.td.columnwidget.client.batch;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.columnwidget.client.custom.ActionButtonCell;
import org.gcube.portlets.user.td.columnwidget.client.dimension.ConnectCodelistDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.ConnectCodelistListener;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionDialog;
import org.gcube.portlets.user.td.columnwidget.client.dimension.DimensionRowSelectionListener;
import org.gcube.portlets.user.td.columnwidget.client.properties.ShowOccurrencesTypeProperties;
import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.store.ShowOccurrencesTypeElement;
import org.gcube.portlets.user.td.columnwidget.client.store.ShowOccurrencesTypeStore;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;
import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;
import org.gcube.portlets.user.td.gwtservice.shared.tr.RefColumn;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.Occurrences;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.OccurrencesForReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceBatchColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ReplaceEntry;
import org.gcube.portlets.user.td.gwtservice.shared.tr.batch.ShowOccurrencesType;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.CellData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestPropertiesParameterType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnViewData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceBatchPanel extends FramedPanel implements
		SingleValueReplaceListener, DimensionRowSelectionListener,
		ConnectCodelistListener {
	private static final String WIDTH = "560px";
	private static final String HEIGHT = "550px";
	private static final String GRID_HEIGHT = "344px";
	private static final String TOOLBAR_HEAD_HEIGHT = "102px";
	private static final String CONNECTION_FIELD_WIDTH = "470px";
	private static final int SHOW_OCCURENCE_TYPE_WIDTH = 100;
	private static final String COMBOCOLS_WIDTH = "510px";
	
	private ReplaceBatchMessages msgs;
	private CommonMessages msgsCommon;
	
	private EventBus eventBus;
	private TRId trId;
	private ArrayList<ColumnData> columns;

	private RefColumn refColumn;
	private String columnLocalId;
	private ConditionCode conditionCode; // For Curation
	private String validationColumnColumnId; // For Curation

	private boolean curation;
	private ColumnData column;
	private boolean hasValidationColumns;
	private ShowOccurrencesType showOccurencesType;
	private ColumnData connection;

	private ReplaceBatchDialog parent;

	private ReplaceEntry currentReplaceEntry;
	private int currentRowIndex;

	private TextButton btnSave;
	private TextButton btnClose;

	private ToolBar toolBarHead;

	private ComboBox<ShowOccurrencesTypeElement> comboShowOccurrencesType = null;
	private ListStore<ShowOccurrencesTypeElement> storeShowOccurrencesType;

	private TextButton btnConnect;
	private TextButton btnDisconnect;
	private TextField connectionField;

	private ListLoader<ListLoadConfig, ListLoadResult<ReplaceEntry>> loader;
	private Grid<ReplaceEntry> grid;
	private ListStore<ReplaceEntry> store;
	private HTML info;

	private boolean simpleReplace;

	private ComboBox<ColumnData> comboCols;
	

	public ReplaceBatchPanel(ReplaceBatchDialog parent, TRId trId,
			RequestProperties requestProperties, EventBus eventBus) {
		this.curation = true;
		this.parent = parent;
		this.trId = trId;
		this.eventBus = eventBus;
		
		initMessages();
		
		InvocationS invocationS = (InvocationS) requestProperties.getMap().get(
				RequestPropertiesParameterType.InvocationS);

		columnLocalId = invocationS.getColumnId();
		
		refColumn = invocationS.getRefColumn();

		conditionCode = (ConditionCode) requestProperties.getMap().get(
				RequestPropertiesParameterType.ConditionCode);
		validationColumnColumnId = (String) requestProperties.getMap().get(
				RequestPropertiesParameterType.ValidationColumnColumnId);

		connection = null;
		hasValidationColumns = false;
		Log.debug("Create BatchReplacePanel(): [" + trId.toString()
				+ " , RequestProperties:" + requestProperties + "]");
		init();
		retrieveConnection();

	}

	public ReplaceBatchPanel(ReplaceBatchDialog parent, TRId trId,
			String columnLocalId, EventBus eventBus) {
		this.curation = false;
		this.parent = parent;
		this.trId = trId;
		this.columnLocalId = columnLocalId;
		this.eventBus = eventBus;
		this.conditionCode = null;
		this.validationColumnColumnId = null;
		
		initMessages();
		
		connection = null;
		hasValidationColumns = false;
		Log.debug("Create BatchReplacePanel(): [" + trId.toString()
				+ " , columnLocalId:" + columnLocalId + "]");
		init();
		retrieveColumnsWithOnlyViewColumnInRel();

	}
	
	protected void initMessages(){
		msgs = GWT.create(ReplaceBatchMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		showOccurencesType = ShowOccurrencesType.ONLYERRORS;

		FlexTable flexTable = new FlexTable();
		flexTable.setCellSpacing(1);

		// Combo Column
		ColumnDataPropertiesCombo propsCols = GWT
				.create(ColumnDataPropertiesCombo.class);
		Log.debug("Props: " + propsCols);
		final ListStore<ColumnData> storeCols = new ListStore<ColumnData>(
				propsCols.id());
		Log.debug("Store Col: " + storeCols);
		storeCols.addAll(columns);

		Log.debug("StoreCol created");
		comboCols = new ComboBox<ColumnData>(storeCols, propsCols.label());

		Log.debug("Combo Threshold created");

		comboCols.addSelectionHandler(comboColsSelection());

		comboCols.setEmptyText(msgs.selectAColumn());
		comboCols.setEditable(false);
		comboCols.setTriggerAction(TriggerAction.ALL);
		comboCols.setWidth(COMBOCOLS_WIDTH);

		FieldLabel columnField = new FieldLabel(comboCols, msgs.column());

		flexTable.setWidget(0, 0, columnField);

		// Create Combo Show
		ShowOccurrencesTypeProperties propsShowOccurrencesType = GWT
				.create(ShowOccurrencesTypeProperties.class);
		storeShowOccurrencesType = new ListStore<ShowOccurrencesTypeElement>(
				propsShowOccurrencesType.id());

		comboShowOccurrencesType = new ComboBox<ShowOccurrencesTypeElement>(
				storeShowOccurrencesType, propsShowOccurrencesType.label());
		Log.trace("ComboMeasureType created");

		addHandlersForShowOccurrencesType(propsShowOccurrencesType.label());

		comboShowOccurrencesType.setEmptyText(msgs.selectAShowType());
		comboShowOccurrencesType.setWidth(SHOW_OCCURENCE_TYPE_WIDTH);
		comboShowOccurrencesType.setTypeAhead(true);
		comboShowOccurrencesType.setEditable(false);
		comboShowOccurrencesType.setTriggerAction(TriggerAction.ALL);

		comboShowOccurrencesType
				.setValue(ShowOccurrencesTypeStore.onlyErrorsElement);

		FieldLabel comboShowOccurrencesTypeField = new FieldLabel(
				comboShowOccurrencesType, msgs.show());
		flexTable.setWidget(1, 0, comboShowOccurrencesTypeField);

		HorizontalLayoutContainer connectionLayout = new HorizontalLayoutContainer();
		if (column == null || !column.isViewColumn()) {

			// Connect Codelist
			btnConnect = new TextButton();
			btnConnect.setIcon(ResourceBundle.INSTANCE.codelistLink24());
			btnConnect.setIconAlign(IconAlign.TOP);
			btnConnect.setToolTip(msgs.connect());
			btnConnect.addSelectHandler(new SelectHandler() {

				public void onSelect(SelectEvent event) {
					Log.debug("Pressed Connect");
					btnConnect.disable();
					connectCodelist();

				}

			});

			connectionLayout.add(btnConnect, new HorizontalLayoutData(-1, 1,
					new Margins(1)));

			// Disconnect Codelist
			btnDisconnect = new TextButton();
			btnDisconnect
					.setIcon(ResourceBundle.INSTANCE.codelistLinkBreak24());
			btnDisconnect.setIconAlign(IconAlign.TOP);
			btnDisconnect.setToolTip(msgs.disconnect());
			btnDisconnect.addSelectHandler(new SelectHandler() {

				public void onSelect(SelectEvent event) {
					Log.debug("Pressed Disconnect");
					btnDisconnect.disable();
					disconnectCodelist();

				}

			});
			connectionLayout.add(btnDisconnect, new HorizontalLayoutData(-1, 1,
					new Margins(1)));

			connectionField = new TextField();
			connectionField.setWidth(CONNECTION_FIELD_WIDTH);
			connectionLayout.add(connectionField, new HorizontalLayoutData(-1,
					1, new Margins(1)));

		} else {
			connectionField = new TextField();
			connectionField.setWidth(CONNECTION_FIELD_WIDTH);

			connectionLayout.add(connectionField, new HorizontalLayoutData(-1,
					1, new Margins(1)));

		}

		FieldLabel conField = new FieldLabel(connectionLayout, msgs.connection());
		flexTable.setWidget(2, 0, conField);

		cleanCells(flexTable.getElement());

		toolBarHead = new ToolBar();
		toolBarHead.setHeight(TOOLBAR_HEAD_HEIGHT);
		// toolBarHead.setHeight("");
		toolBarHead.add(flexTable, new BoxLayoutData(new Margins(0)));

		// Create Grid
		IdentityValueProvider<ReplaceEntry> identity = new IdentityValueProvider<ReplaceEntry>();
		CheckBoxSelectionModel<ReplaceEntry> sm = new CheckBoxSelectionModel<ReplaceEntry>(
				identity);

		RowNumberer<ReplaceEntry> number = new RowNumberer<ReplaceEntry>(
				identity);
		number.setWidth(50);

		number.setCellClassName("");
		number.setCellPadding(true);
		number.setHeader("N.");
		SafeStylesBuilder styleBuilder = new SafeStylesBuilder();
		styleBuilder.width(50, Unit.PX);
		number.setColumnStyle(styleBuilder.toSafeStyles());

		ReplaceEntryProperties props = GWT.create(ReplaceEntryProperties.class);

		ColumnConfig<ReplaceEntry, String> valueCol = new ColumnConfig<ReplaceEntry, String>(
				props.value(), 130, msgs.values());
		ColumnConfig<ReplaceEntry, Integer> numberCol = new ColumnConfig<ReplaceEntry, Integer>(
				props.number(), 100, msgs.occurrences());
		ColumnConfig<ReplaceEntry, String> replacementValueCol = new ColumnConfig<ReplaceEntry, String>(
				props.replacementValue(), 130, msgs.replacement());

		replacementValueCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				if (value == null) {
					String style = "style='color: black;font-weight:bold'";
					sb.appendHtmlConstant("<span " + style + ">"
							+ msgs.notReplaced() + "</span>");
				} else {

					String style = "style='color: green;font-weight:normal'";
					sb.appendHtmlConstant("<span "
							+ style
							+ ">"
							+ new SafeHtmlBuilder().appendEscaped(value)
									.toSafeHtml().asString() + "</span>");
				}

			}
		});

		ColumnConfig<ReplaceEntry, String> changeColumn = new ColumnConfig<ReplaceEntry, String>(
				props.value(), 24);

		ActionButtonCell button = new ActionButtonCell();
		button.setIcon(ResourceBundle.INSTANCE.magnifier());
		button.setTitle(msgs.change());
		button.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Button Change Pressed");
				Context c = event.getContext();
				int rowIndex = c.getIndex();
				startReplaceEntry(rowIndex);
			}
		});

		changeColumn.setCell(button);

		List<ColumnConfig<ReplaceEntry, ?>> l = new ArrayList<ColumnConfig<ReplaceEntry, ?>>();
		l.add(number);
		l.add(valueCol);
		l.add(numberCol);
		l.add(replacementValueCol);
		l.add(changeColumn);
		ColumnModel<ReplaceEntry> cm = new ColumnModel<ReplaceEntry>(l);

		store = new ListStore<ReplaceEntry>(props.id());

		store.addStoreDataChangeHandler(new StoreDataChangeHandler<ReplaceEntry>() {

			@Override
			public void onDataChange(StoreDataChangeEvent<ReplaceEntry> event) {
				updateInfo();

			}

		});

		RpcProxy<ListLoadConfig, ListLoadResult<ReplaceEntry>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ReplaceEntry>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ReplaceEntry>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<ReplaceEntry>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ReplaceEntry, ListLoadResult<ReplaceEntry>>(
				store) {
		});

		grid = new Grid<ReplaceEntry>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						loader.load();
					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		number.initPlugin(grid);
		grid.setHeight(GRID_HEIGHT);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(valueCol);

		info = new HTML(msgs.noInfo());

		ToolBar toolBar = new ToolBar();
		toolBar.add(info);
		toolBar.addStyleName(ThemeStyles.get().style().borderTop());
		toolBar.getElement().getStyle().setProperty("borderBottom", "none");

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

		BoxLayoutData boxLayoutData = new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnSave, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(toolBarHead, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(toolBar, new VerticalLayoutData(1, 25, new Margins(0)));
		v.add(flowButton,
				new VerticalLayoutData(1, 36, new Margins(5, 2, 5, 2)));
		add(v);

		setSelectedColumn();

	}

	protected void update(ColumnData column) {
		this.column = column;
		if (column == null) {
			setSelectedColumn();
		} else {
			columnLocalId = column.getColumnId();
			connection=null;
			conditionCode=null;
			refColumn = null;
			validationColumnColumnId = null;
			hasValidationColumns = false;
			clear();
			retrieveValidationColumn();
		} 
		

	}

	protected void setSelectedColumn() {
		if (column == null) {
			columnLocalId=null;
			connection = null;
			conditionCode=null;
			refColumn = null;
			validationColumnColumnId = null;
			hasValidationColumns = false;
			comboCols.reset();
			storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
					.getShowOccurrencesType());
			Log.debug("Not Is View Column");
			connectionField.setVisible(false);
			btnDisconnect.setVisible(false);
			btnConnect.setVisible(false);
			if(curation){
				comboCols.setReadOnly(true);
			}
			toolBarHead.forceLayout();
		} else {
			comboCols.setValue(column);
			if (curation) {
				comboCols.setReadOnly(true);
				storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
						.getShowOccurrencesType());
				if (connection != null) {
					Log.debug("Selected connection: " + connection);
					connectionField.setValue(connection.getLabel());
					connectionField.setVisible(true);
					btnDisconnect.setVisible(true);
					btnConnect.setVisible(false);
					btnConnect.disable();
					toolBarHead.forceLayout();
				} else {
					if (column.isViewColumn()) {
						Log.debug("Is View Column");
						// storeShowOccurrencesType
						// .add(ShowOccurrencesTypeStore.onlyErrorsElement);
						storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
								.getShowOccurrencesType());

						ColumnViewData cViewData = column.getColumnViewData();
						RefColumn refCol = new RefColumn(String.valueOf(cViewData
								.getTargetTableId()),
								cViewData.getTargetTableColumnId());
						retrieveConnectionForViewColumn(refCol);
					} else {
						storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
								.getShowOccurrencesType());
						Log.debug("Not Is View Column");
						connectionField.setVisible(false);
						btnDisconnect.setVisible(false);
						btnConnect.setVisible(true);
						btnConnect.setEnabled(true);
						toolBarHead.forceLayout();
					}
				}
				
			} else {

				if (column.isViewColumn()) {
					Log.debug("Is View Column");
					// storeShowOccurrencesType
					// .add(ShowOccurrencesTypeStore.onlyErrorsElement);
					storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
							.getShowOccurrencesType());

					ColumnViewData cViewData = column.getColumnViewData();
					RefColumn refCol = new RefColumn(String.valueOf(cViewData
							.getTargetTableId()),
							cViewData.getTargetTableColumnId());
					retrieveConnectionForViewColumn(refCol);
				} else {
					storeShowOccurrencesType.addAll(ShowOccurrencesTypeStore
							.getShowOccurrencesType());
					Log.debug("Not Is View Column");
					connectionField.setVisible(false);
					btnDisconnect.setVisible(false);
					btnConnect.setVisible(true);
					btnConnect.setEnabled(true);
					toolBarHead.forceLayout();
				}

			}
		}
	}

	protected SelectionHandler<ColumnData> comboColsSelection() {
		SelectionHandler<ColumnData> selectionHandler = new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				if (event.getSelectedItem() != null) {
					ColumnData col = event.getSelectedItem();
					Log.debug("Col selected:" + col.toString());
					column = col;
					btnSave.enable();
					update(col);
				} else {
					column = null;
					btnSave.disable();
					update(null);
				}
			}
		};

		return selectionHandler;
	}

	private void cleanCells(Element elem) {
		NodeList<Element> tds = elem.<XElement> cast().select("td");
		for (int i = 0; i < tds.getLength(); i++) {
			Element td = tds.getItem(i);

			if (!td.hasChildNodes() && td.getClassName().equals("")) {
				td.removeFromParent();
			}
		}
	}

	protected void retrieveColumnsWithOnlyViewColumnInRel() {
		Log.debug("Retrieve Column by ColumnId: columnLocalId:" + columnLocalId
				+ ", trId:" + trId);
		TDGWTServiceAsync.INSTANCE.getColumnWithOnlyViewColumnInRel(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load column failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingColumnHead(),
											msgs.errorRetrievingColumn());
								}
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.debug("Column: " + result);
						columns = result;
						retrieveColumn();
						if (column == null) {
							hasValidationColumns = false;
							create();
						} else {
							retrieveValidationColumn();
						}
					}

				});
	}

	protected void retrieveColumn() {
		for (ColumnData c : columns) {
			if (columnLocalId != null
					&& c.getColumnId().compareTo(columnLocalId) == 0) {
				column = c;
				return;
			}

		}

	}

	protected void retrieveValidationColumn() {
		TDGWTServiceAsync.INSTANCE.getValidationColumns(columnLocalId, trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load column failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingColumnHead(),
											msgs.errorRetrievingColumn());
								}
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.debug("Validation Columns: " + result.size());
						ArrayList<String> validationColumnReferences = new ArrayList<String>();
						if (result.size() > 0) {
							hasValidationColumns = true;
							for (ColumnData columnData : result) {
								validationColumnReferences.add(columnData
										.getColumnId());
							}
							column.setValidationColumnReferences(validationColumnReferences);
						} else {
							hasValidationColumns = false;
						}
						create();
					}

				});
	}

	protected void addHandlersForShowOccurrencesType(
			final LabelProvider<ShowOccurrencesTypeElement> labelProvider) {
		comboShowOccurrencesType
				.addSelectionHandler(new SelectionHandler<ShowOccurrencesTypeElement>() {
					public void onSelection(
							SelectionEvent<ShowOccurrencesTypeElement> event) {
						Log.debug("ComboShowOccurrencesType selected: "
								+ event.getSelectedItem());
						ShowOccurrencesTypeElement showType = event
								.getSelectedItem();
						updateShowType(showType);
					}

				});
	}

	/**
	 * 
	 * @param showType
	 */
	protected void updateShowType(ShowOccurrencesTypeElement showType) {
		showOccurencesType = showType.getType();
		loader.load();
	}

	/**
	 * 
	 * @param loadConfig
	 * @param callback
	 */
	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ReplaceEntry>> callback) {
		
		if(column==null){
			ListLoadResultBean<ReplaceEntry> loadedResult = new ListLoadResultBean<ReplaceEntry>(
					getRecord(new ArrayList<Occurrences>()));
			Log.debug("created " + loadedResult.toString());
			callback.onSuccess(loadedResult);
			return;
		}
		
		OccurrencesForReplaceBatchColumnSession occurrencesSession = new OccurrencesForReplaceBatchColumnSession(
				column, showOccurencesType, hasValidationColumns,
				conditionCode, validationColumnColumnId);

		TDGWTServiceAsync.INSTANCE.getOccurrencesForBatchReplace(
				occurrencesSession,
				new AsyncCallback<ArrayList<Occurrences>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load columns failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert(msgs.errorRetrievingColumnsHead(),
											msgs.errorRetrievingColumns());
								}
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<Occurrences> result) {
						Log.trace("loaded " + result.size() + " Occurrences");
						ListLoadResultBean<ReplaceEntry> loadedResult = new ListLoadResultBean<ReplaceEntry>(
								getRecord(result));
						Log.debug("created " + loadedResult.toString());
						try {
							callback.onSuccess(loadedResult);
						} catch (Throwable e) {
							Log.error("Too many different occurrences");
							UtilsGXT3.alert(msgsCommon.attention(),
									msgs.tooManyDifferentOccurrences());
						}
					}

				});

	}

	protected void updateInfo() {
		Log.trace("updating the info bar");
		int total = store.size();
		int totalErrors = 0;
		int assignedErrors = 0;
		int assigned = 0;
		for (ReplaceEntry record : store.getAll()) {
			int errors = record.getNumber();
			totalErrors += errors;
			if (record.getReplacementValue() != null
					&& record.getReplacementValue().compareTo(msgs.notReplaced()) != 0) {
				assigned++;
				assignedErrors += errors;
			}
		}

		String text = Format.substitute(
				msgs.assigned()+" {0} ({1} "+msgs.occurrencesOf()+" {2} ({3} "+msgs.occurrencesLow()+")",
				String.valueOf(assigned), String.valueOf(assignedErrors),
				String.valueOf(total), String.valueOf(totalErrors));
		info.setText(text);
		Log.debug(text);
	}

	protected ArrayList<ReplaceEntry> getRecord(ArrayList<Occurrences> entries) {
		ArrayList<ReplaceEntry> records = new ArrayList<ReplaceEntry>();
		try {
			for (Occurrences entry : entries)
				records.add(getRecord(entry));

		} catch (Throwable e) {
			Log.debug("Error creating records:" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return records;

	}

	//
	protected ReplaceEntry getRecord(Occurrences entry) {
		ReplaceEntry data = null;
		if (conditionCode == null) {
			if (column.isViewColumn()) {
				data = new ReplaceEntry(entry.getValue(), entry.getRowId(),
						entry.getNumber(), null, null);
			} else {
				data = new ReplaceEntry(entry.getValue(), entry.getNumber(),
						null, null);
			}
		} else {
			switch (conditionCode) {
			case AllowedColumnType:
				break;
			case AmbiguousValueOnExternalReference:
				data = new ReplaceEntry(entry.getValue(), entry.getRowId(),
						entry.getNumber(), null, null);
				break;
			case CastValidation:
				break;
			case CodeNamePresence:
				break;
			case DuplicateTupleValidation:
				break;
			case DuplicateValueInColumn:
				break;
			case GenericTupleValidity:
				break;
			case GenericValidity:
				break;
			case MaxOneCodenameForDataLocale:
				break;
			case MissingValueOnExternalReference:
				if (column.isViewColumn()) {
					data = new ReplaceEntry(entry.getValue(), entry.getRowId(),
							entry.getNumber(), null, null);
				} else {
					data = new ReplaceEntry(entry.getValue(),
							entry.getNumber(), null, null);
				}
				break;
			case MustContainAtLeastOneDimension:
				break;
			case MustContainAtLeastOneMeasure:
				break;
			case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
				break;
			case OnlyOneCodeColumn:
				break;
			case OnlyOneCodenameColumn:
				break;
			case ValidPeriodFormat:
				break;
			default:
				if (column.isViewColumn()) {
					data = new ReplaceEntry(entry.getValue(), entry.getRowId(),
							entry.getNumber(), null, null);
				} else {
					data = new ReplaceEntry(entry.getValue(),
							entry.getNumber(), null, null);
				}
				break;

			}
		}

		return data;
	}

	protected void startReplaceEntry(int rowIndex) {
		currentRowIndex = rowIndex;
		currentReplaceEntry = store.get(rowIndex);
		Log.debug(currentReplaceEntry.toString() + " was clicked.[rowIndex="
				+ currentRowIndex + " ]");
		if (!curation) {
			Log.debug("Not in curation from validation");
			if (column.isViewColumn()) {
				callDimensionRowSelectionDialog();
			} else {
				callSingleValueReplaceDialog();

			}
		} else {
			Log.debug("In curation from validation");
			if (connection != null) {
				callDimensionRowSelectionDialog();
			} else {

			}
		}
	}

	protected void save() {
		ArrayList<ReplaceEntry> effectiveReplaceList = new ArrayList<ReplaceEntry>();
		for (ReplaceEntry re : store.getAll()) {
			if (re.getReplacementValue() != null) {
				effectiveReplaceList.add(re);
			}

		}

		if (effectiveReplaceList.size() == 0) {
			UtilsGXT3
					.alert(msgsCommon.attention(), msgs.selectAtLeastOneValueToReplace());
			btnSave.enable();
		} else {
			startReplaceBatch(effectiveReplaceList);
		}
	}

	protected void startReplaceBatch(
			ArrayList<ReplaceEntry> effectiveReplaceList) {
		ReplaceBatchColumnSession replaceBatchColumnSession = new ReplaceBatchColumnSession(
				trId, column, effectiveReplaceList, column.isViewColumn(),
				connection);
		parent.startBatchReplace(replaceBatchColumnSession);
	}

	protected void close() {
		parent.close();
	}

	protected void callSingleValueReplaceDialog() {
		Log.debug("callSingleValueReplaceDialog");
		simpleReplace = true;
		if (connection == null) {
			SingleValueReplaceDialog dialogSingleValueReplace = new SingleValueReplaceDialog(
					currentReplaceEntry.getValue(),
					currentReplaceEntry.getReplacementValue(), column, eventBus);
			dialogSingleValueReplace.addListener(this);
			dialogSingleValueReplace.show();
		} else {
			Log.debug("callDimensionRowSelectionDialog");
			CellData cellData = new CellData(currentReplaceEntry.getValue(),
					connection.getName(), connection.getColumnId(),
					connection.getLabel(), currentReplaceEntry.getRowId(), 0, 0);
			DimensionRowSelectionDialog dialogDimensionRowSelection = new DimensionRowSelectionDialog(
					connection, cellData, true, eventBus);
			dialogDimensionRowSelection.addListener(this);
			dialogDimensionRowSelection.show();
		}

	}

	protected void callDimensionRowSelectionDialog() {
		simpleReplace = false;
		Log.debug("callDimensionRowSelectionDialog");
		CellData cellData = null;
		ColumnData col = null;
		boolean workOnTable = false;
		if (curation) {
			if (connection != null) {
				cellData = new CellData(currentReplaceEntry.getValue(),
						connection.getName(), connection.getColumnId(),
						connection.getLabel(), currentReplaceEntry.getRowId(),
						0, 0);
				col = connection;
				workOnTable = true;

			} else {
				Log.error("No connection retrieved");
			}
			Log.debug("Col: " + col + ", CellData:" + cellData);
			DimensionRowSelectionDialog dialogDimensionRowSelection;

			switch (conditionCode) {
			case AllowedColumnType:
				break;
			case AmbiguousValueOnExternalReference:
				dialogDimensionRowSelection = new DimensionRowSelectionDialog(
						col, cellData, workOnTable, true, true, true, false,
						eventBus);
				dialogDimensionRowSelection.addListener(this);
				dialogDimensionRowSelection.show();
				break;
			case CastValidation:
				break;
			case CodeNamePresence:
				break;
			case DuplicateTupleValidation:
				break;
			case DuplicateValueInColumn:
				break;
			case GenericTupleValidity:
				break;
			case GenericValidity:
				break;
			case MaxOneCodenameForDataLocale:
				break;
			case MissingValueOnExternalReference:
				dialogDimensionRowSelection = new DimensionRowSelectionDialog(
						col, cellData, workOnTable, eventBus);
				dialogDimensionRowSelection.addListener(this);
				dialogDimensionRowSelection.show();
				break;
			case MustContainAtLeastOneDimension:
				break;
			case MustContainAtLeastOneMeasure:
				break;
			case MustHaveDataLocaleMetadataAndAtLeastOneLabel:
				break;
			case OnlyOneCodeColumn:
				break;
			case OnlyOneCodenameColumn:
				break;
			case ValidPeriodFormat:
				break;
			default:
				dialogDimensionRowSelection = new DimensionRowSelectionDialog(
						col, cellData, workOnTable, eventBus);
				dialogDimensionRowSelection.addListener(this);
				dialogDimensionRowSelection.show();
				break;

			}

		} else {
			cellData = new CellData(currentReplaceEntry.getValue(),
					column.getName(), column.getColumnId(), column.getLabel(),
					currentReplaceEntry.getRowId(), 0, 0);
			col = column;
			workOnTable = false;

			Log.debug("Col: " + col + ", CellData:" + cellData);
			DimensionRowSelectionDialog dialogDimensionRowSelection = new DimensionRowSelectionDialog(
					col, cellData, workOnTable, eventBus);
			dialogDimensionRowSelection.addListener(this);
			dialogDimensionRowSelection.show();
		}
	}

	@Override
	public void selectedDimensionRow(DimensionRow dimensionRow) {
		Log.debug("Change Value: " + dimensionRow);
		if (simpleReplace) {
			currentReplaceEntry.setReplacementValue(dimensionRow.getValue());
			store.update(currentReplaceEntry);
			updateInfo();
		} else {
			currentReplaceEntry.setReplacementValue(dimensionRow.getValue());
			currentReplaceEntry.setReplacementDimensionRow(dimensionRow);
			store.update(currentReplaceEntry);
			updateInfo();
		}
	}

	@Override
	public void abortedDimensionRowSelection() {
		Log.debug("Change Value Aborted");

	}

	@Override
	public void failedDimensionRowSelection(String reason, String detail) {
		Log.error("Change Value Failed:" + reason + " " + detail);

	}

	@Override
	public void selectedSingleValueReplace(String replaceValue) {
		Log.debug("Change Value: " + replaceValue);
		currentReplaceEntry.setReplacementValue(replaceValue);
		store.update(currentReplaceEntry);
		updateInfo();
	}

	@Override
	public void abortedSingleValueReplace() {
		Log.debug("Change Value Aborted");

	}

	@Override
	public void failedSingleValueReplace(String reason, String detail) {
		Log.error("Change Value Failed:" + reason + " " + detail);

	}

	protected void connectCodelist() {
		Log.debug("callConnectCodelistDialog");
		ConnectCodelistDialog connectCodelistDialog = new ConnectCodelistDialog(
				eventBus);
		connectCodelistDialog.addListener(this);
		connectCodelistDialog.show();

	}

	protected void disconnectCodelist() {
		Log.debug("Disconnect codelist");
		this.connection = null;
		connectionField.setValue("");
		connectionField.setVisible(false);
		btnDisconnect.setVisible(false);
		btnConnect.setVisible(true);
		btnConnect.enable();
		toolBarHead.forceLayout();
	}

	@Override
	public void selectedConnectCodelist(ColumnData connection) {
		Log.debug("Selected connection: " + connection);
		this.connection = connection;
		connectionField.setValue(connection.getLabel());
		connectionField.setVisible(true);
		btnDisconnect.setVisible(true);
		btnConnect.setVisible(false);
		btnConnect.enable();
		toolBarHead.forceLayout();
	}

	@Override
	public void abortedConnectCodelist() {
		Log.debug("Connection Aborted");
		btnConnect.enable();
	}

	@Override
	public void failedConnectCodelist(String reason, String detail) {
		Log.debug("Connection Failed: " + reason + " " + detail);
		UtilsGXT3.alert(msgs.errorOnConnectHead(), reason);
		btnConnect.enable();

	}

	protected void retrieveConnection() {
		Log.debug("Retrieve Connection RefColumn:" + refColumn);
		TDGWTServiceAsync.INSTANCE.getConnection(refColumn,
				new AsyncCallback<ColumnData>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load column failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingConnectionColumnHead(),
													msgs.errorRetrievingConnectionColumn());
								}
							}
						}
					}

					public void onSuccess(ColumnData result) {
						Log.debug("Column: " + result);
						connection = result;
						retrieveColumnsWithOnlyViewColumnInRel();
					}

				});

	}

	protected void retrieveConnectionForViewColumn(RefColumn refCol) {
		Log.debug("Retrieve Connection For View Column: " + refCol);
		TDGWTServiceAsync.INSTANCE.getConnection(refCol,
				new AsyncCallback<ColumnData>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.error("load column failure:"
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgs.errorRetrievingColumnsHead(),
													msgs.errorRetrievingConnectionColumn());
								}
							}
						}
					}

					public void onSuccess(ColumnData result) {
						Log.debug("Column: " + result);
						connection = result;
						setConnectionForViewColumn();
					}

				});

	}

	protected void setConnectionForViewColumn() {
		Log.debug("Selected connection: " + connection);
		connectionField.setValue(connection.getLabel());
		connectionField.setVisible(true);
		toolBarHead.forceLayout();
	}

}
