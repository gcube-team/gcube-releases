package org.gcube.portlets.user.workspace.client.view.trash;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEvent;
import org.gcube.portlets.user.workspace.client.model.FileTrashedModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 23, 2013
 * 
 * Singleton 
 */
public class WindowTrash extends Window {

	private List<FileTrashedModel> trashedFiles;
	private TrashInfoContainer trashContainers;
	private HorizontalPanel hpItemsNumber;
	private static WindowTrash INSTANCE = null;
	private Label labelItemsNumber = new Label();
	
	
	private WindowTrash() {
		initAccounting();
	    setIcon(Resources.getTrashFull()); //TODO
	    setHeading("Trash");
	}
	
	/**
	 * 
	 * @return
	 */
	public static synchronized WindowTrash getInstance(){
		if(INSTANCE==null)
			INSTANCE = new WindowTrash();
		
		return INSTANCE;
	}

	/**
	 * This method is called only once
	 */
	private void initAccounting() {
		setLayout(new FitLayout());
		setSize(770, 400);
		setResizable(true);
		setMaximizable(true);
		this.trashContainers = new TrashInfoContainer();
		add(trashContainers);
		
		ToolBar toolBar = new ToolBar();
		hpItemsNumber = new HorizontalPanel();
		hpItemsNumber.setStyleAttribute("margin-left", "10px");
		hpItemsNumber.setHorizontalAlign(HorizontalAlignment.CENTER);
		hpItemsNumber.add(labelItemsNumber);
		toolBar.add(hpItemsNumber);
		
		setBottomComponent(toolBar);

		addStoreListeners();
	}
	

	/**
	 * 
	 */
	private void addStoreListeners() {
		
		trashContainers.getStore().addListener(Store.Add,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
//				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});
		
		trashContainers.getStore().addListener(Store.Remove,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});
		
		trashContainers.getStore().addListener(Store.Clear,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});
		
	}
	
	/**
	 * @param size
	 */
	public void updateItemsNumber(int size) {
		if(size<=0)
			labelItemsNumber.setText("No Items");
		else if(size==1)
			labelItemsNumber.setText("1 Item");
		else if(size>1)
			labelItemsNumber.setText(size +" Items");
		
		hpItemsNumber.layout();
	}

	public void setWindowTitle(String title) {
		this.setHeading(title);

	}
	
	/**
	 * 
	 * @return -1 if store is null. The size otherwise
	 */
	private int storeSize(){
		
		if(trashContainers.getStore()!=null && trashContainers.getStore().getModels()!=null){
			return trashContainers.getStore().getModels().size();
		}
		
		return -1;
		
	}
	
	
	/**
	 * 
	 * @param fileModelId
	 * @return
	 */
	public boolean deleteFileFromTrash(String fileModelId){
		boolean deleted = this.trashContainers.deleteItem(fileModelId);
		
		updateTrashIcon(this.trashContainers.trashSize()>0);
		
		return deleted;
	}
	

	public void updateTrashContainer(List<FileTrashedModel> trashFiles) {

		this.trashContainers.resetStore();
		this.trashedFiles = trashFiles;
		this.trashContainers.updateTrash(trashFiles);
		
		updateTrashIcon(this.trashContainers.trashSize()>0);
	}
	
	public void executeOperationOnTrashContainer(List<String> trashIds, WorkspaceTrashOperation operation) {
		
		if(operation.equals(WorkspaceTrashOperation.DELETE_PERMANENTLY)){
			this.mask("Deleting");
			deleteListItems(trashIds);
			
		}else if(operation.equals(WorkspaceTrashOperation.RESTORE)){
			this.mask("Restoring");
			deleteListItems(trashIds);
		}
		
		this.unmask();
		
		updateTrashIcon(this.trashContainers.trashSize()>0);
	}

	public List<FileTrashedModel> getTrashedFiles() {
		return trashedFiles;
	}
	
	private void deleteListItems(List<String> trashIds){
	
		for (String identifier : trashIds) {
			this.trashContainers.deleteItem(identifier);
		}
		
	}
	
	public void maskContainer(String title){
		this.trashContainers.mask(title, ConstantsExplorer.LOADINGSTYLE);
	}
	
	public void unmaskContainer(){
		this.trashContainers.unmask();
	}
	
	public void updateTrashIcon(boolean trashIsFull){
		
		if(trashIsFull)
			setIcon(Resources.getTrashFull());
		else
			setIcon(Resources.getTrashEmpty());
	}
	
	public void showTrashErrors(WorkspaceTrashOperation operation, List<FileTrashedModel> errors){
		
		if(errors!=null && errors.size()>0){
			
			List<String> fileNames = new ArrayList<String>(errors.size());
			
			//BUILDING NAMES
			for (FileTrashedModel fileTrashedModel : errors) {
				
				FileTrashedModel trashFile = trashContainers.getFileModelByIdentifier(fileTrashedModel.getIdentifier());
				fileNames.add(trashFile.getName());
			}
			
			String htmlError = "<div style=\"padding:10px 10px 10px 10px\"><b style=\"font-size:12px\">Sorry an error occured on removing the ";
			htmlError+=fileNames.size()>1?"items":"item";
			htmlError+=": </b><br/>";
			
			for (String fileName : fileNames) {
				htmlError+="<br/> - "+fileName;
			}
			
			htmlError+="<br/><br/><b>"+"Try again later</b></div>";
			
			Dialog dialog = new Dialog();
			dialog.setStyleAttribute("background-color", "#FAFAFA");
			dialog.setSize(380, 180);
			dialog.setLayout(new FitLayout());
			dialog.setIcon(Resources.getIconInfo());
			dialog.setModal(true);
			dialog.setHeading("Trash Errors");
			dialog.setButtons(Dialog.OK);
			dialog.setHideOnButtonClick(true);
			dialog.setButtonAlign(HorizontalAlignment.CENTER);
//			dialog.setSize(200, 150);
			
			ContentPanel cp = new ContentPanel();
			cp.setHeaderVisible(false);
			cp.setFrame(false);
			cp.setScrollMode(Scroll.AUTOY);
			cp.add(new Html(htmlError));
			dialog.add(cp);
			dialog.show();
			
		}
	}


}
