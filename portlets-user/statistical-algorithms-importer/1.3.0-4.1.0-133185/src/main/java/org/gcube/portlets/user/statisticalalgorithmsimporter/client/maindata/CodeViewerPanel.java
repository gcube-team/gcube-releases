package org.gcube.portlets.user.statisticalalgorithmsimporter.client.maindata;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties.CodeDataProperties;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.resource.StatAlgoImporterResources;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.SessionExpiredType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CodeViewerPanel extends ContentPanel {
	private static final String GRID_CODE_HEIGHT = "300px";
	private static final String GRID_IO_HEIGHT = "100px";
	private static final String SET_INPUT = "SetInput";

	private EventBus eventBus;

	private ListStore<CodeData> storeCode;
	private Grid<CodeData> gridCode;

	private ListStore<CodeData> storeInput;
	private Grid<CodeData> gridInput;

	private ListStore<CodeData> storeOutput;
	private Grid<CodeData> gridOutput;

	// private TextField fileNameField;

	public CodeViewerPanel(EventBus eventBus) {
		super();
		Log.debug("CodeViewerPanel");
		this.eventBus = eventBus;

		// msgs = GWT.create(ServiceCategoryMessages.class);
		init();
		create();

	}

	private void init() {
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);
		setHeaderVisible(false);
		setResize(true);

	}

	private void create() {
		/*
		 * ToolBar toolBar = new ToolBar();
		 * 
		 * toolBar.add(new LabelToolItem("File: ")); fileNameField=new
		 * TextField(); fileNameField.setWidth("250px");
		 * toolBar.add(fileNameField);
		 */

		/*
		 * toolBar.add(new LabelToolItem("Search: ")); final TextField
		 * searchField = new TextField(); toolBar.add(searchField);
		 * 
		 * TextButton btnReload = new TextButton(); //
		 * btnReload.setText("Reload");
		 * btnReload.setIcon(StatisticalRunnerResources.INSTANCE.reload24());
		 * btnReload.setToolTip("Reload"); toolBar.add(btnReload);
		 */

		
		CodeDataProperties props = GWT.create(CodeDataProperties.class);

		/*
		 * storeCode.addFilter(new StoreFilter<CodeData>() {
		 * 
		 * public boolean select(Store<CodeData> store, CodeData parent,
		 * CodeData item) { String searchTerm = searchField.getCurrentValue();
		 * if (searchTerm == null) return true; return
		 * CodeViewerPanel.this.select(item, searchTerm); } });
		 * 
		 * storeCode.setEnableFilters(true);
		 */

		// The row numberer for the first column
		RowNumberer<CodeData> numbererColumn = new RowNumberer<CodeData>();
		numbererColumn.setSortable(false);
		numbererColumn.setHeader("N.");
		numbererColumn.setWidth(80);
		numbererColumn.setResizable(true);

		// numbererColumn.setHorizontalAlignment(HorizontalAlignmentConstant.endOf(Direction.LTR));
		// numbererColumn.setColumnStyle(ss.toSafeStyles());

		SafeStylesBuilder ss = new SafeStylesBuilder()
				.textAlign(TextAlign.CENTER);
		ColumnConfig<CodeData, Integer> codeIdColumn = new ColumnConfig<CodeData, Integer>(
				props.id(), 80, "N.");
		codeIdColumn.setSortable(false);
		codeIdColumn.setColumnStyle(ss.toSafeStyles());

		ColumnConfig<CodeData, String> codeLineColumn = new ColumnConfig<CodeData, String>(
				props.codeLine(), 100, "Code");
		
		
		codeLineColumn.setCell(new AbstractCell<String>(){

			@Override
			public void render(Context context,
					String value, SafeHtmlBuilder sb) {
				Log.debug(value);
				SafeHtmlBuilder shb=new SafeHtmlBuilder();
				shb.appendEscaped(value);
				sb.appendHtmlConstant("<pre>"+shb.toSafeHtml().asString()+"</pre>");
			}
			
		});
		
		codeLineColumn.setSortable(false);

		// Grid Code
		List<ColumnConfig<CodeData, ?>> gridCodeColumns = new ArrayList<ColumnConfig<CodeData, ?>>();
		gridCodeColumns.add(numbererColumn);
		gridCodeColumns.add(codeLineColumn);

		ColumnModel<CodeData> gridCodeColumnModel = new ColumnModel<CodeData>(
				gridCodeColumns);

		IdentityValueProvider<CodeData> identityCode = new IdentityValueProvider<CodeData>();
		final CheckBoxSelectionModel<CodeData> smCode = new CheckBoxSelectionModel<CodeData>(
				identityCode);
		smCode.setSelectionMode(SelectionMode.SINGLE);
		
		storeCode = new ListStore<CodeData>(props.code());
		
		gridCode = new Grid<CodeData>(storeCode, gridCodeColumnModel) {
			@Override
			protected void onAfterFirstAttach() {
				// TODO Auto-generated method stub
				super.onAfterFirstAttach();
				loadData();
			}
		};
		
		gridCode.setSelectionModel(smCode);
		gridCode.getView().setStripeRows(true);
		gridCode.getView().setColumnLines(true);
		gridCode.getView().setAutoFill(true);
		gridCode.setBorders(false);
		gridCode.setLoadMask(true);
		gridCode.setHeight(GRID_CODE_HEIGHT);
		gridCode.setColumnReordering(false);
		gridCode.setColumnResize(false);
		// gridCode.getView().setAutoExpandColumn(codeLineColumn);
		
		// Initialize the row numberer
		numbererColumn.initPlugin(gridCode);

		// GridFilters<CodeData> filtersSelectedRules = new
		// GridFilters<CodeData>();
		// filtersSelectedRules.initPlugin(gridInput);
		// filtersSelectedRules.setLocal(true);
		// filtersSelectedRules.addFilter(nameFilter);
		// filtersSelectedRules.addFilter(descriptionFilter);

		/*
		 * grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
		 * 
		 * @Override public void onRowDoubleClick(RowDoubleClickEvent event) {
		 * int rowIndex = event.getRowIndex(); requestOpen(rowIndex); }
		 * 
		 * });
		 */
		/*
		 * SelectHandler sh = new SelectHandler() { public void
		 * onSelect(SelectEvent event) { loadData(); } };
		 * 
		 * btnReload.addSelectHandler(sh);
		 */

		createContextMenuGridCode();

		// Grid Input
		List<ColumnConfig<CodeData, ?>> gridInputColumns = new ArrayList<ColumnConfig<CodeData, ?>>();
		gridInputColumns.add(codeIdColumn);
		gridInputColumns.add(codeLineColumn);

		ColumnModel<CodeData> gridInputColumnModel = new ColumnModel<CodeData>(
				gridInputColumns);
		
		IdentityValueProvider<CodeData> identityInput = new IdentityValueProvider<CodeData>();
		final CheckBoxSelectionModel<CodeData> smInput = new CheckBoxSelectionModel<CodeData>(
				identityInput);
		smInput.setSelectionMode(SelectionMode.SINGLE);
		
		storeInput = new ListStore<CodeData>(props.code());
		
		gridInput = new Grid<CodeData>(storeInput, gridInputColumnModel);
	
		gridInput.setSelectionModel(smInput);
		gridInput.getView().setStripeRows(true);
		gridInput.getView().setColumnLines(true);
		gridInput.getView().setAutoFill(true);
		gridInput.setBorders(false);
		gridInput.setLoadMask(true);
		gridInput.setHeight(GRID_IO_HEIGHT);
		gridInput.setColumnReordering(false);
		gridInput.setColumnResize(false);
		gridInput.getView().setAutoExpandColumn(codeLineColumn);

		createContextMenuGridInput();

		// Output
		List<ColumnConfig<CodeData, ?>> gridOutputColumns = new ArrayList<ColumnConfig<CodeData, ?>>();
		gridOutputColumns.add(codeIdColumn);
		gridOutputColumns.add(codeLineColumn);

		ColumnModel<CodeData> gridOutputColumnModel = new ColumnModel<CodeData>(
				gridOutputColumns);

		IdentityValueProvider<CodeData> identityOutput = new IdentityValueProvider<CodeData>();
		final CheckBoxSelectionModel<CodeData> smOutput = new CheckBoxSelectionModel<CodeData>(
				identityOutput);
		smOutput.setSelectionMode(SelectionMode.SINGLE);
		
		storeOutput = new ListStore<CodeData>(props.code());

		gridOutput = new Grid<CodeData>(storeOutput, gridOutputColumnModel);
		
		gridOutput.setSelectionModel(smOutput);
		gridOutput.getView().setStripeRows(true);
		gridOutput.getView().setColumnLines(true);
		gridOutput.getView().setAutoFill(true);
		gridOutput.setBorders(false);
		gridOutput.setLoadMask(true);
		gridOutput.setHeight(GRID_IO_HEIGHT);
		gridOutput.setColumnReordering(false);
		gridOutput.setColumnResize(false);
		gridOutput.getView().setAutoExpandColumn(codeLineColumn);

		createContextMenuGridOutput();

		// DND
		GridDragSource<CodeData> sourceCode = new GridDragSource<CodeData>(
				gridCode);
		sourceCode.setGroup(SET_INPUT);

		GridDropTarget<CodeData> targetInput = new GridDropTarget<CodeData>(
				gridInput);
		targetInput.setGroup(SET_INPUT);
		targetInput.setOperation(Operation.COPY);

		GridDropTarget<CodeData> targetOutput = new GridDropTarget<CodeData>(
				gridOutput);
		targetOutput.setGroup(SET_INPUT);
		targetOutput.setOperation(Operation.COPY);

		// Label
		FieldLabel gridCodeLabel = new FieldLabel(gridCode, "Code");
		gridCodeLabel.setLabelWidth(50);
		FieldLabel gridInputLabel = new FieldLabel(gridInput, "Input");
		gridInputLabel.setLabelWidth(50);
		FieldLabel gridOutputLabel = new FieldLabel(gridOutput, "Output");
		gridOutputLabel.setLabelWidth(50);

		//
		VerticalLayoutContainer con = new VerticalLayoutContainer();
		// con.setAdjustForScroll(false);
		// con.setScrollMode(ScrollMode.AUTO);

		con.add(gridCodeLabel, new VerticalLayoutData(1, 1, new Margins(0)));
		con.add(gridInputLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		con.add(gridOutputLabel, new VerticalLayoutData(1, -1, new Margins(0)));

		add(con, new MarginData(new Margins(0)));
		// add(gridCode, new MarginData(new Margins(0)));

	}

	private void createContextMenuGridCode() {
		Menu contextMenuGridCode = new Menu();
		MenuItem inputItem = new MenuItem("Input");
		inputItem.setId("InputStatement");
		inputItem.setIcon(StatAlgoImporterResources.INSTANCE.input16());
		inputItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (gridCode != null) {
					CodeData codeData = getGridCodeSelectedItem();
					if (storeInput.findModel(codeData) == null) {
						storeInput.add(new CodeData(codeData.getId(), codeData
								.getCodeLine()));
						storeInput.commitChanges();
					}
					// UtilsGXT3.info("Input", "Row " + codeData.getId()
					// + " is set as input statement!");

				}

			}
		});
		contextMenuGridCode.add(inputItem);

		MenuItem outputItem = new MenuItem("Output");
		outputItem.setId("OutputStatement");
		outputItem.setIcon(StatAlgoImporterResources.INSTANCE.output16());
		outputItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (gridCode != null) {
					CodeData codeData = getGridCodeSelectedItem();
					if (storeOutput.findModel(codeData) == null) {
						storeOutput.add(new CodeData(codeData.getId(), codeData
								.getCodeLine()));
						storeOutput.commitChanges();
					}
					// UtilsGXT3.info("Output", "Row " + codeData.getId()
					// + " is set as otuput statement!");

				}

			}
		});
		contextMenuGridCode.add(outputItem);

		gridCode.setContextMenu(contextMenuGridCode);

	}

	private void createContextMenuGridInput() {
		Menu contextMenuGridInput = new Menu();
		MenuItem deleteItem = new MenuItem("Delete");
		deleteItem.setId("Delete");
		deleteItem.setIcon(StatAlgoImporterResources.INSTANCE.input16());
		deleteItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (gridInput != null) {
					CodeData codeData = getGridInputSelectedItem();
					storeInput.remove(codeData);
					storeInput.commitChanges();

				}

			}
		});
		contextMenuGridInput.add(deleteItem);

		gridInput.setContextMenu(contextMenuGridInput);

	}

	private void createContextMenuGridOutput() {
		Menu contextMenuGridOutput = new Menu();
		MenuItem deleteItem = new MenuItem("Delete");
		deleteItem.setId("Delete");
		deleteItem.setIcon(StatAlgoImporterResources.INSTANCE.input16());
		deleteItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if (gridOutput != null) {
					CodeData codeData = getGridOutputSelectedItem();
					storeOutput.remove(codeData);
					storeOutput.commitChanges();

				}

			}
		});
		contextMenuGridOutput.add(deleteItem);

		gridOutput.setContextMenu(contextMenuGridOutput);

	}

	private void loadData() {
		StatAlgoImporterServiceAsync.INSTANCE
				.getCode(new AsyncCallback<ArrayList<CodeData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof StatAlgoImporterSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error retrieving code: "
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									caught.getLocalizedMessage());
						}
						caught.printStackTrace();

					}

					public void onSuccess(ArrayList<CodeData> result) {
						Log.debug("loaded " + result.size() + " code lines");
						ArrayList<CodeData> availables = new ArrayList<CodeData>();
						for (CodeData codeData : result) {
							Log.debug("Read: " + codeData);
							availables.add(codeData);
						}
						storeCode.clear();
						storeCode.addAll(availables);
						storeCode.commitChanges();

						forceLayout();
					}
				});
	}

	/*
	 * private boolean select(CodeData item, String searchTerm) { if
	 * (item.getCodeLine() != null && item.getCodeLine().toLowerCase()
	 * .contains(searchTerm.toLowerCase())) return true; return false; }
	 */

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<CodeData> handler) {
		return gridCode.getSelectionModel().addSelectionHandler(handler);
	}

	public CodeData getGridCodeSelectedItem() {
		return gridCode.getSelectionModel().getSelectedItem();

	}
	
	public CodeData getGridInputSelectedItem() {
		return gridInput.getSelectionModel().getSelectedItem();

	}
	
	public CodeData getGridOutputSelectedItem() {
		return gridOutput.getSelectionModel().getSelectedItem();

	}

	public void gridReload() {
		storeCode.clear();
		storeCode.commitChanges();
		storeInput.clear();
		storeInput.commitChanges();
		storeOutput.clear();
		storeOutput.commitChanges();
		gridCode.getSelectionModel().deselectAll();
		gridInput.getSelectionModel().deselectAll();
		gridOutput.getSelectionModel().deselectAll();

		loadData();
	}

}
