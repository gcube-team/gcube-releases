package org.gcube.portlets.user.workspace.client.view.trash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.TrashEvent;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
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
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;

public class TrashInfoContainer extends LayoutContainer {

	private Grid<FileTrashedModel> grid;
	private ContentPanel cp;
	private GroupingStore<FileTrashedModel> store = new GroupingStore<FileTrashedModel>();
	private ListStore<FileTrashedModel> typeStoreOperation = new ListStore<FileTrashedModel>();
    private GridDropTarget gridDropTarget;
	private Button buttonDelete;
	private Button buttonRestore;
	private Button buttonRestoreAll;
	private Button buttonEmptyTrash;
	
	public TrashInfoContainer() {
		initContentPanel();
		initGrid();
		createToolBar();
//		initDropTarget();
		
		activeButtonOnSelection(false);
	}

	/**
	 * 
	 */
	private void initDropTarget() {
		
		 this.gridDropTarget = new GridDropTarget(grid){
			 
			 /* (non-Javadoc)
			 * @see com.extjs.gxt.ui.client.dnd.GridDropTarget#onDragDrop(com.extjs.gxt.ui.client.event.DNDEvent)
			 */
			 
			@Override
			protected void onDragDrop(DNDEvent e) {
				// TODO Auto-generated method stub

				List<FileModel> listFileModel  = getDragSource(e);

				List<String> ids  = new ArrayList<String>();
				
				for (FileModel fileModel : listFileModel) {
					fileModel.setIcon();
					ids.add(fileModel.getIdentifier());
				}
				
				System.out.println("** Trash Event move handleInsertDrop is completed");
				
				super.onDragDrop(e);
				
				
			}
			
		    private List<FileModel> getDragSource(DNDEvent event){
				
		    	List<FileModel> listDraggedFile = new ArrayList<FileModel>();
		    	
				if(event.getData() != null){
					
					List<BaseModelData> listItemsSource =  event.getData();
					
					System.out.println("Trash - Number of move " + listItemsSource.size());
					
//					FileModel sourceFileModel = null; //for print

					for(int i=0; i<listItemsSource.size(); i++){

						if(listItemsSource.get(i) instanceof TreeStoreModel){ //DRAG FROM TREE
							
//							System.out.println("qui 1");
//						
//							TreeStoreModel itemSource = (TreeStoreModel) listItemsSource.get(i);
//							
//							listDraggedFile.add((FileModel) itemSource.getModel());
							
//							sourceFileModel = (FileModel) itemSource.getModel();
//							
//							if(sourceFileModel.getParentFileModel()!=null)
//								
//								System.out.println("Trash Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag " + " Parent Name: " + sourceFileModel.getParentFileModel().getName() + "id " + sourceFileModel.getParentFileModel().getIdentifier());
//							else
//								System.out.println("Trash Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag ");
							
							
						}else if(listItemsSource.get(i) instanceof FileGridModel){ //DRAG FROM GRID
							
							listDraggedFile.add((FileModel) listItemsSource.get(i));
//							FileGridModel fileGrid = listItemsSource.get(i);
//							System.out.println("qui 2");
//							System.out.println("class "+listItemsSource.get(i).getClass());
						}
//						System.out.println("Trash Child count: " + itemSource.getChildCount());
						
					}
				}
				
				return listDraggedFile;
			}
		 };
		 
		 
	}

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

	private boolean checkSelection(){
		
		if(grid.getSelectionModel().getSelectedItems().size()==0){
			MessageBox.info("Attention", "You must pick at least one item", null);
			return false;
		}
		
		return true;
	}
	
	private void createToolBar() {

		ToolBar bar = new ToolBar();

		buttonRestore = new Button(WorkspaceTrashOperation.RESTORE.getLabel(),Resources.getIconUndo());
		buttonRestore.setToolTip(WorkspaceTrashOperation.RESTORE.getOperationDescription());
		buttonRestore.setScale(ButtonScale.SMALL);
		buttonRestore.setIconAlign(IconAlign.TOP);
		
		buttonRestore.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				if(checkSelection())
					AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.RESTORE, grid.getSelectionModel().getSelectedItems()));
			}
		});
		
		bar.add(buttonRestore);
		

		buttonDelete = new Button(WorkspaceTrashOperation.DELETE_PERMANENTLY.getLabel(),Resources.getIconDeleteItem());
		buttonDelete.setToolTip(WorkspaceTrashOperation.DELETE_PERMANENTLY.getOperationDescription());
		buttonDelete.setScale(ButtonScale.SMALL);
		buttonDelete.setIconAlign(IconAlign.TOP);
		buttonDelete.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(checkSelection())
					AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.DELETE_PERMANENTLY, grid.getSelectionModel().getSelectedItems()));
			}
		});

		bar.add(buttonDelete);
		

		bar.add(new SeparatorMenuItem());
		
		buttonRestoreAll = new Button(WorkspaceTrashOperation.RESTORE_ALL.getLabel(),Resources.getIconRecycle());
		buttonRestoreAll.setToolTip(WorkspaceTrashOperation.RESTORE_ALL.getOperationDescription());
		buttonRestoreAll.setScale(ButtonScale.SMALL);
		buttonRestoreAll.setIconAlign(IconAlign.TOP);
		buttonRestoreAll.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.RESTORE_ALL, null));
			}
		});

		bar.add(buttonRestoreAll);
		
		
		buttonEmptyTrash = new Button(WorkspaceTrashOperation.EMPTY_TRASH.getLabel(),Resources.getTrashEmpty());
		buttonEmptyTrash.setToolTip(WorkspaceTrashOperation.EMPTY_TRASH.getOperationDescription());
		buttonEmptyTrash.setScale(ButtonScale.SMALL);
		buttonEmptyTrash.setIconAlign(IconAlign.TOP);
		buttonEmptyTrash.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.EMPTY_TRASH, null));
			}
		});

		bar.add(buttonEmptyTrash);
		
		
		bar.add(new FillToolItem());
		
		Button buttonRefresh = new Button(WorkspaceTrashOperation.REFRESH.getLabel(),Resources.getIconRefresh());
		buttonRefresh.setToolTip(WorkspaceTrashOperation.REFRESH.getOperationDescription());
		buttonRefresh.setScale(ButtonScale.SMALL);
		buttonRefresh.setIconAlign(IconAlign.TOP);
		buttonRefresh.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.REFRESH, null));
			}
		});

		bar.add(buttonRefresh);
		
		
		cp.setTopComponent(bar);

	}

	public void initGrid() {

		ColumnConfig icon = new ColumnConfig(ConstantsExplorer.ICON, "", 25);
		ColumnConfig name = createSortableColumnConfig(ConstantsExplorer.NAME, ConstantsExplorer.NAME, 200);
		ColumnConfig type = createSortableColumnConfig(ConstantsExplorer.TYPE, ConstantsExplorer.TYPE, 80);
		ColumnConfig originalPath = createSortableColumnConfig(FileTrashedModel.STOREINFO.ORIGINALPATH.toString(), "Original Path", 200);
		ColumnConfig deleteDate = createSortableColumnConfig(FileTrashedModel.STOREINFO.DELETEDATE.toString(), "Deleted Date", 90);
		deleteDate.setDateTimeFormat(DateTimeFormat.getFormat("dd MMM hh:mm aaa yyyy"));
		ColumnConfig deleteUser = createSortableColumnConfig(FileTrashedModel.STOREINFO.DELETEUSER.toString(), "Deleted By", 150);
		
		ColumnModel cm  = new ColumnModel(Arrays.asList(icon, name, type, originalPath, deleteDate, deleteUser));

		final ColumnModel columnModel = cm;

		grid = new Grid<FileTrashedModel>(this.store, cm);

		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(false);
		
		this.grid.setView(view);
		this.grid.setContextMenu(null);


		GridCellRenderer<FileModel> folderRender = new GridCellRenderer<FileModel>() {  
			@Override
			public String render(FileModel model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileModel> store, Grid<FileModel> grid) {
		          String val = model.get(property);  
		          String color = "black";
		          
		          if(val != null && val.equals(GXTFolderItemTypeEnum.FOLDER.toString())){
//			        	  color = "#EEC900";
		        	  return "<span qtitle='" + columnModel.getColumnById(property).getHeader() + "' qtip='" + val  + "' style='font-weight: bold;color:" + color + "'>" + val + "</span>";
		          }else{
		        	  if(val==null)
		        		  val = "";
		        	  return "<span qtitle='" + columnModel.getColumnById(property).getHeader() + "' qtip='" + val  + "' style='color:" + color + "'>" + val + "</span>";
		          }
			}  
	    };  
	      
	    type.setRenderer(folderRender);

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

	
		StringFilter nameFilter = new StringFilter(ConstantsExplorer.NAME);
		StringFilter typeFilter = new StringFilter(ConstantsExplorer.TYPE);
		DateFilter dateFilter = new DateFilter(FileTrashedModel.STOREINFO.DELETEDATE.toString());
		
		filters.addFilter(nameFilter);
		filters.addFilter(typeFilter);
		filters.addFilter(dateFilter);

		grid.addPlugin(filters);
		
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileTrashedModel>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<FileTrashedModel> se) {
				
				boolean selection = grid.getSelectionModel().getSelectedItems().size()>0;
				activeButtonOnSelection(selection);
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
	


	public void setPanelSize(int width, int height) {

		if (width > 0 && height > 0 && grid != null) {
			cp.setSize(width, height);
			// grid.setSize(width, height);
		}
	}

	public TrashInfoContainer(List<FileTrashedModel> trashFiles) {

		initContentPanel();
		initGrid();
		updateTrash(trashFiles);
	}

	public void updateTrash(List<FileTrashedModel> trashFiles) {

		store.removeAll();
		typeStoreOperation.removeAll();
		
		for (FileTrashedModel fileTrashedModel : trashFiles) {
			fileTrashedModel.setIcon();
		}
		
		store.add(trashFiles);
		
		activeButtonsOnNotEmtpy(store.getModels().size()>0);
	}
	
	/**
	 * @param b
	 */
	private void activeButtonsOnNotEmtpy(boolean isNotEmpty) {
		buttonRestoreAll.setEnabled(isNotEmpty);
		buttonEmptyTrash.setEnabled(isNotEmpty);
	}
	
	/**
	 * @param multi
	 */
	protected void activeButtonOnSelection(boolean bool) {
		buttonDelete.setEnabled(bool);
		buttonRestore.setEnabled(bool);
	}
	private void updateStore(ListStore<FileTrashedModel> store) {

		resetStore();
		this.grid.getStore().add(store.getModels());
	}

	public void resetStore() {
		this.grid.getStore().removeAll();
	}

	public ColumnConfig createSortableColumnConfig(String id, String name,
			int width) {
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setSortable(true);

		return columnConfig;
	}

	public void setHeaderTitle(String title) {
		cp.setHeading(title);
		// cp.layout();
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public FileTrashedModel getFileModelByIdentifier(String identifier){
		
		return store.findModel(ConstantsExplorer.IDENTIFIER, identifier);
		
	}
	
	/**
	 * 
	 * @param identifier (MANDATORY)
	 * @return
	 */
	public boolean deleteItem(String identifier) {
		
		FileTrashedModel fileTarget = getFileModelByIdentifier(identifier);
		
		
		if(fileTarget!=null){
			return deleteItem(fileTarget);
		}
		else
			System.out.println("Delete Error: file target with " + identifier + " identifier not exist in store" );
		
		return false;
	}
	
	/**
	 * 
	 * @param fileTarget (MANDATORY)
	 * @return
	 */
	private boolean deleteItem(FileTrashedModel fileTarget) {

		Record record = store.getRecord(fileTarget);
		
		if (record != null) {
			
			FileTrashedModel item = (FileTrashedModel) record.getModel();
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
	 * 
	 * @return the number of items contained into trash
	 */
	public int trashSize(){
		return store.getCount();
	}

	protected GroupingStore<FileTrashedModel> getStore() {
		return store;
	}

	public ContentPanel getCp() {
		return cp;
	}

}