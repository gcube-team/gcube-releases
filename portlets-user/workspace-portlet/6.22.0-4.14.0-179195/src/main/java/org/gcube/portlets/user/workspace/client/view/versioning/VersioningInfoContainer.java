package org.gcube.portlets.user.workspace.client.view.versioning;

import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.gridevent.FileVersioningEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.windows.DialogConfirm;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.grid.filters.BooleanFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


/**
 * The Class VersioningInfoContainer.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 21, 2017
 */
public class VersioningInfoContainer extends LayoutContainer {

	private Grid<FileVersionModel> grid;
	private ContentPanel cp;
	private GroupingStore<FileVersionModel> store = new GroupingStore<FileVersionModel>();
	private ListStore<FileVersionModel> typeStoreOperation = new ListStore<FileVersionModel>();
    //private GridDropTarget gridDropTarget;
	private Button buttonDelete;
	//private Button buttonRestore;
	private Button buttonDownload;
	private Button buttonPublicLink;
	private Button buttonEmptyVersions;
	private FileModel currentVersion;
	private WindowVersioning windowVersioning;

	/**
	 * Instantiates a new versioning info container.
	 *
	 * @param file the versioning files
	 * @param windowVersioning the window versioning
	 */
	public VersioningInfoContainer(FileModel file, WindowVersioning windowVersioning) {

		initContentPanel();
		initGrid();
		createToolBar();
		this.currentVersion = file;
		this.windowVersioning = windowVersioning;
		activeButtonOnSelection(false);
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
		cp.setScrollMode(Scroll.AUTOY);
		add(cp);
	}

	/**
	 * Check selection.
	 *
	 * @return true, if successful
	 */
	private boolean checkSelection(){

		if(grid.getSelectionModel().getSelectedItems().size()==0){
			MessageBox.info("Attention", "You must pick at least one item", null);
			return false;
		}

		GWT.log("Returning checkSelection");

		return true;
	}

	/**
	 * Creates the tool bar.
	 */
	private void createToolBar() {

		ToolBar bar = new ToolBar();

		buttonDelete = new Button(WorkspaceVersioningOperation.DELETE_PERMANENTLY.getLabel(),Resources.getIconDeleteItem());
		buttonDelete.setToolTip(WorkspaceVersioningOperation.DELETE_PERMANENTLY.getOperationDescription());
		buttonDelete.setScale(ButtonScale.SMALL);
		buttonDelete.setIconAlign(IconAlign.TOP);
		buttonDelete.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(checkSelection()){
					final List<FileVersionModel> selectedItems = grid.getSelectionModel().getSelectedItems();
					String msg = "Deleting older ";
					msg+=selectedItems.size()>1?"versions:":"version:";
					for (FileVersionModel file : selectedItems) {
						msg+="<br/> - "+file.getName();
					}
					msg+="<br/>Confirm?";

					String title = selectedItems.size()>1?"Delete versions":"Delete version";
					title+=" of: "+currentVersion.getName();
					final DialogConfirm box = new DialogConfirm(null, title, msg);
					box.getYesButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.DELETE_PERMANENTLY,
								currentVersion,
								grid.getSelectionModel().getSelectedItems(),
								grid.getStore().getModels(),
								windowVersioning));
						}
					});
					box.setModal(true);
					box.center();
				}
			}
		});

		//bar.add(buttonDelete);

		buttonDownload = new Button(WorkspaceVersioningOperation.DOWNLOAD.getLabel(),Resources.getIconDownload());
		buttonDownload.setToolTip(WorkspaceVersioningOperation.DOWNLOAD.getOperationDescription());
		buttonDownload.setScale(ButtonScale.SMALL);
		buttonDownload.setIconAlign(IconAlign.TOP);
		buttonDownload.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(checkSelection())
					AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.DOWNLOAD,
						currentVersion,
						grid.getSelectionModel().getSelectedItems(),
						grid.getStore().getModels(),
						windowVersioning));
			}
		});


		buttonPublicLink = new Button(WorkspaceVersioningOperation.PUBLIC_LINK.getLabel(),Resources.getIconPublicLink());
		buttonPublicLink.setToolTip(WorkspaceVersioningOperation.PUBLIC_LINK.getOperationDescription());
		buttonPublicLink.setScale(ButtonScale.SMALL);
		buttonPublicLink.setIconAlign(IconAlign.TOP);
		buttonPublicLink.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(checkSelection())
					AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.PUBLIC_LINK,
						currentVersion,
						grid.getSelectionModel().getSelectedItems(),
						grid.getStore().getModels(),
						windowVersioning));
			}
		});

		//bar.add(new SeparatorMenuItem());

		buttonEmptyVersions = new Button(WorkspaceVersioningOperation.DELETE_ALL_OLDER_VERSIONS.getLabel(),Resources.getTrashEmpty());
		buttonEmptyVersions.setToolTip(WorkspaceVersioningOperation.DELETE_ALL_OLDER_VERSIONS.getOperationDescription());
		buttonEmptyVersions.setScale(ButtonScale.SMALL);
		buttonEmptyVersions.setIconAlign(IconAlign.TOP);
		buttonEmptyVersions.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(store.getModels().size()>0){

					String msg = "Deleting all older versions of: <br/> - "+ currentVersion.getName()+"." +
						"<br/>Confirm?";
					final DialogConfirm box = new DialogConfirm(null, "Delete older versions of: "+ currentVersion.getName(), msg);
					box.getYesButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							box.hide();
							AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.DELETE_ALL_OLDER_VERSIONS,
								currentVersion,
								grid.getSelectionModel().getSelectedItems(),
								grid.getStore().getModels(),
								windowVersioning));
						}
					});
					box.setModal(true);
					box.center();
				}

			}
		});

		//bar.add(buttonEmptyVersions);

		Button buttonRefresh = new Button(WorkspaceTrashOperation.REFRESH.getLabel(),Resources.getIconRefresh());
		buttonRefresh.setToolTip(WorkspaceTrashOperation.REFRESH.getOperationDescription());
		buttonRefresh.setScale(ButtonScale.SMALL);
		buttonRefresh.setIconAlign(IconAlign.TOP);
		buttonRefresh.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(grid.getSelectionModel().getSelectedItems()==null || grid.getSelectionModel().getSelectedItems().size()==0)
					return;

				AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.REFRESH,
					currentVersion,
					grid.getSelectionModel().getSelectedItems(),
					grid.getStore().getModels(),
					windowVersioning));
			}
		});

		bar.add(buttonRefresh);
		bar.add(buttonDownload);
		bar.add(buttonPublicLink);
		cp.setTopComponent(bar);

	}

	/**
	 * Inits the grid.
	 */
	public void initGrid() {

		//ColumnConfig icon = new ColumnConfig(FileGridModel.ICON, "", 25);
		ColumnConfig name = createSortableColumnConfig(FileModel.NAME, "Version Id", 120);
		//ColumnConfig type = createSortableColumnConfig(FileModel.TYPE, FileModel.TYPE, 80);
		//ColumnConfig originalPath = createSortableColumnConfig(FileVersionModel.PATH, "Original Path", 200);
		ColumnConfig created = createSortableColumnConfig(FileVersionModel.CREATED, "Created", 120);
		//ColumnConfig deleteDate = createSortableColumnConfig(FileTrashedModel.STOREINFO.DELETEDATE.toString(), "Deleted Date", 90);
		//deleteDate.setDateTimeFormat(DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy"));
		//ColumnConfig user = createSortableColumnConfig(FileVersionModel.USER_VERSIONING, "User", 130);
		ColumnConfig currVersion = createSortableColumnConfig(FileVersionModel.IS_CURRENT_VERSION, "Current Version", 75);

		ColumnModel cm  = new ColumnModel(Arrays.asList(name, created, currVersion));

		grid = new Grid<FileVersionModel>(this.store, cm);

		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(false);

		this.grid.setView(view);
		this.grid.setContextMenu(null);

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		StringFilter nameFilter = new StringFilter(FileModel.NAME);
		DateFilter dateFilter = new DateFilter(FileVersionModel.CREATED);
		StringFilter userFilter = new StringFilter(FileVersionModel.USER_VERSIONING);
		BooleanFilter currVersionFilter = new BooleanFilter(FileVersionModel.IS_CURRENT_VERSION);

		final ColumnModel columnModel = cm;

		GridCellRenderer<FileVersionModel> nameRender = new GridCellRenderer<FileVersionModel>() {
			@Override
			public String render(FileVersionModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionModel> store, Grid<FileVersionModel> grid) {
		          Boolean isCurrVersion = model.isCurrentVersion();
		          String val = model.get(property);
		          String color = "black";

		          if(val!=null && isCurrVersion != null && isCurrVersion){
		        	  return "<span qtitle='" + columnModel.getColumnById(property).getHeader() + "' qtip='" + val  + "' style='font-weight: bold;color:" + color + "'>" + val + "</span>";
		          }else{
		        	  if(val==null)
		        		  val = "";
		        	  return "<span qtitle='" + columnModel.getColumnById(property).getHeader() + "' qtip='" + val  + "' style='color:" + color + "'>" + val + "</span>";
		          }
			}
	    };

		GridCellRenderer<FileVersionModel> currVersionRender = new GridCellRenderer<FileVersionModel>() {
			@Override
			public String render(FileVersionModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionModel> store, Grid<FileVersionModel> grid) {
		          Boolean isCurrVersion = model.isCurrentVersion();
		          //String val = model.get(property);

		          if(isCurrVersion != null && isCurrVersion){
		        	  return "<span title='Current Version'>Yes</span>";
		          }else{
		        	  return "<span title='Previous Version'>No</span>";
		          }
			}
	    };


	    name.setRenderer(nameRender);
	    currVersion.setRenderer(currVersionRender);
		filters.addFilter(nameFilter);
		filters.addFilter(dateFilter);
		filters.addFilter(userFilter);
		filters.addFilter(currVersionFilter);

		grid.addPlugin(filters);

		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileVersionModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<FileVersionModel> se) {

				List<FileVersionModel> selected = grid.getSelectionModel().getSelectedItems();
				boolean selection = selected.size()>0;
				activeButtonOnSelection(selection);

				for (FileVersionModel fileVersionModel : selected) {
					activeCurrentVersionOperation(!fileVersionModel.isCurrentVersion());
				}

				if(selected.size()>1){
					activeDownloadOperation(false);
					activePublicLinkperation(false);
				}


			}

		});

		grid.getView().setAutoFill(true);
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.getView().setShowDirtyCells(false);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.setStyleAttribute("borderTop", "none");
		cp.add(grid);

	}



	/**
	 * Active download operation.
	 *
	 * @param b the b
	 */
	private void activeDownloadOperation(boolean b) {
		buttonDownload.setEnabled(b);
	}

	/**
	 * Active download operation.
	 *
	 * @param b the b
	 */
	private void activePublicLinkperation(boolean b) {
		buttonPublicLink.setEnabled(b);
	}



	/**
	 * Active current version operation.
	 *
	 * @param bool the bool
	 */
	private void activeCurrentVersionOperation(boolean bool){
		buttonDelete.setEnabled(bool);
		//buttonRestore.setEnabled(bool);
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
		}
	}

	/**
	 * Update versions.
	 *
	 * @param versioningFiles the versioning files
	 */
	public void updateVersions(List<FileVersionModel> versioningFiles) {

		store.removeAll();
		typeStoreOperation.removeAll();
		store.add(versioningFiles);
		activeButtonsOnNotEmtpy(store.getModels().size()>0);
	}

	/**
	 * Active buttons on not emtpy.
	 *
	 * @param isNotEmpty the is not empty
	 */
	private void activeButtonsOnNotEmtpy(boolean isNotEmpty) {
		buttonEmptyVersions.setEnabled(isNotEmpty);
	}

	/**
	 * Active button on selection.
	 *
	 * @param bool the bool
	 */
	protected void activeButtonOnSelection(boolean bool) {
		buttonDelete.setEnabled(bool);
		//buttonRestore.setEnabled(bool);
		buttonDownload.setEnabled(bool);
		buttonPublicLink.setEnabled(bool);
	}

	/**
	 * Update store.
	 *
	 * @param store the store
	 */
	private void updateStore(ListStore<FileVersionModel> store) {

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
	 * Gets the file model by identifier.
	 *
	 * @param identifier the identifier
	 * @return the file model by identifier
	 */
	public FileVersionModel getFileModelByIdentifier(String identifier){

		return store.findModel(FileModel.IDENTIFIER, identifier);

	}

	/**
	 * Delete item.
	 *
	 * @param identifier (MANDATORY)
	 * @return true, if successful
	 */
	public boolean deleteItem(String identifier) {

		FileVersionModel fileTarget = getFileModelByIdentifier(identifier);


		if(fileTarget!=null){
			return deleteItem(fileTarget);
		}
		else
			System.out.println("Delete Error: file target with " + identifier + " identifier not exist in store" );

		return false;
	}

	/**
	 * Delete item.
	 *
	 * @param fileTarget (MANDATORY)
	 * @return true, if successful
	 */
	private boolean deleteItem(FileVersionModel fileTarget) {

		Record record = store.getRecord(fileTarget);

		if (record != null) {

			FileVersionModel item = (FileVersionModel) record.getModel();
			store.remove(item);

			return true;
		} else
			System.out.println("Record Error: file target with "
					+ fileTarget.getIdentifier()
					+ " identifier not exist in store");

		activeButtonsOnNotEmtpy(store.getModels().size()>0);

		return false;

	}


	/**
	 * Versioning items.
	 *
	 * @return the int
	 */
	public int versioningItems(){
		return store.getCount();
	}

	/**
	 * Gets the store.
	 *
	 * @return the store
	 */
	protected GroupingStore<FileVersionModel> getStore() {
		return store;
	}

	/**
	 * Gets the cp.
	 *
	 * @return the cp
	 */
	public ContentPanel getCp() {
		return cp;
	}

}