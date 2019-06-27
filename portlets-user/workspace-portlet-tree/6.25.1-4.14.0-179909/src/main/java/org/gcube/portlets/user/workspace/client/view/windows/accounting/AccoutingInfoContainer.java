package org.gcube.portlets.user.workspace.client.view.windows.accounting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingEntryType;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;


/**
 * The Class AccoutingInfoContainer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 28, 2016
 */
public class AccoutingInfoContainer extends LayoutContainer {

	/**
	 *
	 */
	private static final String OPERATION_NAME = "OperationName";
	/**
	 *
	 */
	private static final String TYPEOPERATION = "typeoperation";
	protected static final String DATE = "Date";
	protected static final String AUTHOR = "Author";
	protected static final String OPERATION = "Operation";
	protected static final String DESCRIPTION = "Description";
	private ColumnModel cm;
	private Grid<ModelData> grid;
	private ContentPanel cp;
	private GroupingStore<ModelData> store = new GroupingStore<ModelData>();
	private boolean groupingEnabled;
	private ListStore<ModelData> typeStoreOperation = new ListStore<ModelData>();

	/**
	 * Instantiates a new accouting info container.
	 */
	public AccoutingInfoContainer() {
		initContentPanel();
		initGrid();
		createToolBar();
	}

	/**
	 * Inits the content panel.
	 */
	private void initContentPanel() {
		setLayout(new FitLayout());
		getAriaSupport().setPresentation(true);
		cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(true);
		cp.setLayout(new FitLayout());
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		// cp.getHeader().setIconAltText("Grid Icon");
		// cp.setSize(550, 280);
		add(cp);
	}

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {

		ToolBar bar = new ToolBar();
		final ToggleButton buttonGrouping = new ToggleButton("",
				Resources.getIconGridView());
		buttonGrouping.setToolTip("Grouping by operation");
		buttonGrouping.setScale(ButtonScale.SMALL);
		buttonGrouping.toggle(true);

		buttonGrouping
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						if (buttonGrouping.isPressed())
							enableGrouping();
						else
							disableGrouping();
					}
				});

		bar.add(buttonGrouping);
		cp.setTopComponent(bar);

	}

	/**
	 * Inits the grid.
	 */
	public void initGrid() {

		store.groupBy(OPERATION_NAME);

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		ColumnConfig icon = createSortableColumnConfig("Type", "", 20);
		columns.add(icon);

		icon.setRenderer(new GridCellRenderer<ModelData>() {

			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {

				if (model.get(OPERATION) != null) {
					if (model.get(OPERATION).equals(
							GxtAccountingEntryType.CREATE))
						return Resources.getIconCreateNew().createImage();
					if (model.get(OPERATION)
							.equals(GxtAccountingEntryType.READ))
						return Resources.getIconRead().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.CUT))
						return Resources.getIconCut().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.PASTE))
						return Resources.getIconPaste().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.REMOVE))
						return Resources.getIconCancel().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.RENAME))
						return Resources.getIconRenameItem().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.UPDATE))
						return Resources.getIconRefresh().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.ADD))
						return Resources.getIconFileUpload().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.SHARE))
						return Resources.getIconShareFolder().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.UNSHARE))
						return Resources.getIconUnShareFolder().createImage();
					else if (model.get(OPERATION).equals(
							GxtAccountingEntryType.RESTORE))
						return Resources.getIconUndo().createImage();
					else if (model.get(OPERATION).equals(
						GxtAccountingEntryType.DISABLED_PUBLIC_ACCESS))
					return Resources.getIconFolderPublicRemove().createImage();
					else if (model.get(OPERATION).equals(
						GxtAccountingEntryType.ENABLED_PUBLIC_ACCESS))
					return Resources.getIconFolderPublic().createImage();
				}
				return null;
			}

		});

		ColumnConfig descr = createSortableColumnConfig(DESCRIPTION, DESCRIPTION, 230);
		columns.add(descr);

		descr.setRenderer(new GridCellRenderer<ModelData>() {

			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				// if(model.get(OPERATION).equals(GxtAccountingEntryType.READ))
				// return "<b><p style=\"color: red;\">"
				// + model.get(DESCRIPTION)+ "</p></b>";
				return model.get(DESCRIPTION);
			}

		});

		ColumnConfig oper = createSortableColumnConfig(OPERATION_NAME, OPERATION, 70);
		columns.add(oper);

		ColumnConfig auth = createSortableColumnConfig(AUTHOR, AUTHOR, 120);
		columns.add(auth);

		ColumnConfig date = createSortableColumnConfig(DATE, DATE, 140);
		columns.add(date);

		cm = new ColumnModel(columns);

		final ColumnModel columnModel = cm;

		grid = new Grid<ModelData>(this.store, cm);

		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(true);
		this.grid.setView(view);

		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String f = columnModel.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";
				return f + ": " + data.group + " (" + data.models.size() + " "
						+ l + ")";
			}
		});

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		DateFilter dateFilter = new DateFilter(DATE);
		filters.addFilter(dateFilter);

		StringFilter descrFilter = new StringFilter(DESCRIPTION);
		filters.addFilter(descrFilter);

		StringFilter authorFilter = new StringFilter(AUTHOR);
		filters.addFilter(authorFilter);

		ListFilter listFilter = new ListFilter(OPERATION_NAME, typeStoreOperation);
		listFilter.setDisplayProperty(TYPEOPERATION);

		filters.addFilter(listFilter);

		grid.addPlugin(filters);

		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.getView().setAutoFill(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.setStyleAttribute("borderTop", "none");
		// grid.setLoadMask(true);
		cp.add(grid);

	}


	/**
	 * Sets the panel size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setPanelSize(int width, int height) {

		if (width > 0 && height > 0 && grid != null) {
			cp.setSize(width, height);
			// grid.setSize(width, height);
		}
	}

	/**
	 * Instantiates a new accouting info container.
	 *
	 * @param accountings the accountings
	 */
	public AccoutingInfoContainer(List<GxtAccountingField> accountings) {

		initContentPanel();
		initGrid();
		updateListAccounting(accountings);
	}

	/**
	 * Disable grouping.
	 */
	public void disableGrouping() {
		GroupingStore<ModelData> groupingStore = null;
		if (store instanceof GroupingStore) {
			groupingStore = store;
			if (groupingStore != null) {
				groupingStore.clearGrouping();
			}
			this.groupingEnabled = false;
		}
	}

	/**
	 * Enable grouping.
	 */
	public void enableGrouping() {
		GroupingStore<ModelData> groupingStore = null;
		if (store instanceof GroupingStore) {
			groupingStore = store;
			if (groupingStore != null) {
				groupingStore.groupBy(OPERATION_NAME);
			}
			this.groupingEnabled = true;
		}
	}

	/**
	 * Update list accounting.
	 *
	 * @param accountings the accountings
	 */
	public void updateListAccounting(List<GxtAccountingField> accountings) {

		List<BaseModelData> listModelData = new ArrayList<BaseModelData>();

		store.removeAll();

		//Used for list store filters
		Map<String, String> hashOperation = new HashMap<String, String>();
		typeStoreOperation.removeAll();

		for (GxtAccountingField gxtAccountingField : accountings) {
			BaseModelData baseModel = new BaseModelData();

			baseModel.set(DESCRIPTION, gxtAccountingField.getDescription());
			baseModel.set(OPERATION, gxtAccountingField.getOperation());
			baseModel.set(OPERATION_NAME, gxtAccountingField.getOperation().getId());

			if(hashOperation.get(gxtAccountingField.getOperation().getId())==null){
				hashOperation.put(gxtAccountingField.getOperation().getId(), "");
//				typeStoreOperation.add(type((gxtAccountingField.getOperation().getId())));
			}

			baseModel.set(AUTHOR, gxtAccountingField.getUser().getName());
			baseModel.set(DATE, gxtAccountingField.getDate());

			listModelData.add(baseModel);
		}

		List<String> operationKeys = new ArrayList<String>(hashOperation.keySet());
		Collections.sort(operationKeys);
		for (String key : operationKeys) {
			typeStoreOperation.add(type(key));
		}

		store.add(listModelData);
	}

	/**
	 * Type.
	 *
	 * @param type the type
	 * @return the model data
	 */
	private ModelData type(String type) {
		ModelData model = new BaseModelData();
		model.set(TYPEOPERATION, type);
		return model;

	}

	/**
	 * Update store.
	 *
	 * @param store the store
	 */
	@SuppressWarnings("unused")
	private void updateStore(ListStore<ModelData> store) {

		resetStore();
		this.grid.getStore().add(store.getModels());
	}

	/**
	 * Reset store.
	 */
	public void resetStore() {
		this.grid.getStore().removeAll();
	}

	/**
	 * Creates the sortable column config.
	 *
	 * @param id the id
	 * @param name the name
	 * @param width the width
	 * @return the column config
	 */
	public ColumnConfig createSortableColumnConfig(String id, String name,
			int width) {
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setSortable(true);

		return columnConfig;
	}

	/**
	 * Sets the header title.
	 *
	 * @param title the new header title
	 */
	public void setHeaderTitle(String title) {
		cp.setHeading(title);
		// cp.layout();
	}

	/**
	 * Checks if is grouping enabled.
	 *
	 * @return true, if is grouping enabled
	 */
	public boolean isGroupingEnabled() {
		return groupingEnabled;
	}

	/**
	 * Sets the grouping enabled.
	 *
	 * @param groupingEnabled the new grouping enabled
	 */
	public void setGroupingEnabled(boolean groupingEnabled) {
		this.groupingEnabled = groupingEnabled;
	}

}