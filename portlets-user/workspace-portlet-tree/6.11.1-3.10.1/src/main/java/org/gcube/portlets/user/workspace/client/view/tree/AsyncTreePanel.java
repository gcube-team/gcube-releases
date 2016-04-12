package org.gcube.portlets.user.workspace.client.view.tree;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.constant.WorkspaceOperation;
import org.gcube.portlets.user.workspace.client.event.ExpandFolderEvent;
import org.gcube.portlets.user.workspace.client.event.MoveItemEvent;
import org.gcube.portlets.user.workspace.client.event.SelectedItemEvent;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FolderModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class AsyncTreePanel extends LayoutContainer {

	private static final String TREE_MESSAGE_PANEL_ASYNC = "treeMessagePanelAsync";
	
	private static final String ROOT_SUFFIX = "'s workspace";
	//	private TreeLoader<FileModel> loader;
	private TreePanel<FileModel> treePanel;
	private TreeStore<FileModel> store;
	private ContextMenuTree contextMenuTree;
	private HandlerManager eventBus = AppControllerExplorer.getEventBus();
	private ContentPanel cp = new ContentPanel();
	private boolean isSubTreeLoaded = false;
	private boolean isSearch = false;
	private String scopeId = null;
	private enum DragType {INSERT, APPEND};
//	private int contextMenuPosX = -1;
//	private int contextMenuPosY = -1;
	
	private boolean rightClick = false;

	public AsyncTreePanel() {

		//Init Store
		store = new TreeStore<FileModel>();
		
		//Load Root Item
//		this.loadRootItem();
		
		treePanel = new TreePanel<FileModel>(store){	
			@Override
			public boolean hasChildren(FileModel parent) {
				if (parent instanceof FolderModel) {
					return true;
				}
				return super.hasChildren(parent);
			}

			  @Override
			  @SuppressWarnings({"unchecked", "rawtypes"})
			  public void onComponentEvent(ComponentEvent ce) {
			    super.onComponentEvent(ce);
			    TreePanelEvent<FileModel> tpe = (TreePanelEvent) ce;
			    EventType typeEvent = tpe.getType();
			    
			    rightClick = false;
			    
				if (typeEvent == Events.OnMouseDown) {
					 
					if (ce.isRightClick()){
						rightClick = true;
					 }
				}
	
			    int type = ce.getEventTypeInt();
			    switch (type) {
			      case Event.ONCLICK:
			        onClick(tpe);
			        break;
			      case Event.ONDBLCLICK:
			        onDoubleClick(tpe);
			        break;
			      case Event.ONSCROLL:
			        onScroll(tpe);
			        break;
			      case Event.ONFOCUS:
			        onFocus(ce);
			        break;
			    }
	
			    view.onEvent(tpe);
			  }
		};
		

		treePanel.setStateful(false);
		treePanel.setDisplayProperty(ConstantsExplorer.NAME);
		
		// statefull components need a defined id
		treePanel.setId(TREE_MESSAGE_PANEL_ASYNC);

		// SET icons in tree panel
		treePanel.setIconProvider(new ModelIconProvider<FileModel>() {

			public AbstractImagePrototype getIcon(FileModel model) {
//				model.setIcon();
				if (!model.isDirectory()) {
					if(model.getType()!=null)
						return Resources.getIconByType(model.getName(), model.getType());
					
					return Resources.getIconByFolderItemType(model.getGXTFolderItemType());
				}
				if (model.isRoot()) {
					model.setName(AppControllerExplorer.myLoginFirstName+ROOT_SUFFIX);
					return Resources.getCloudDriveIcon();
				}

				if(model.getStatus() == ConstantsExplorer.FOLDERNOTLOAD)
					return Resources.getIconLoading2();
				
				if(model.isVreFolder())
					return Resources.getIconVREFolder();
				
				if(model.isSpecialFolder())
					return Resources.getIconSpecialFolder();
				
				//SHARE ICON
				if(model.isShared()){
					if(model.isShareable())
						return Resources.getIconSharedFolder();
					else
						return null; //Set default folder icon
				}
				return null; //Set default folder icon
			}
		});
		
		contextMenuTree = new ContextMenuTree();
		
		treePanel.setContextMenu(contextMenuTree.getContextMenu());
		
		//Single selection Mode
		treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	
		this.addListners();
		this.addDragAndDrop();
		this.setAlphanumericStoreSorter();
		
		cp.setHeading(ConstantsExplorer.TREEVIEW);
		cp.setHeaderVisible(false);
		
//		//BULK
//		Header headerCp = cp.getHeader();
//		buttBulk.setIcon(Resources.getIconBulkUpdate());
//		headerCp.addTool(buttBulk);
		
		cp.setBodyBorder(false);
		cp.setBodyStyle("padding: 5px");
		cp.setLayout(new FitLayout());

		//***For Debug the store 
		Button butt = new Button();
		butt.setText("Print Store");
		butt.setVisible(false);
		
		butt.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				List<FileModel> listFileModel = treePanel.getStore().getRootItems();
				System.out.println("\n\nStore by root");

				for (FileModel item : listFileModel) {
					System.out.println(item.getName() + " ID " + item.getIdentifier() + " isDirectory " + item.isDirectory());
					
//					eventBus.fireEvent(new SelectedItemEvent(treePanel.getStore().getRootItems().get(0)));
					printingTreeLevel(item);
				}
			}
			
		});
		
//		cp.add(butt);
		
		//***End Debug the store 
		
		cp.add(treePanel);
		
		add(cp);
		
	}

	/**
	 * 
	 * 	
	 * Status values
	 * 	ConstantsExplorer.FOLDERNOTLOAD = "notload";
	 *  ConstantsExplorer.FOLDERLOADED = "loaded";
	 * 
	 * @param itemIdentifier
	 * @param status
	 */

	public void changeFolderIconStatus(String itemIdentifier, String status){
		
		FileModel fileModel = getFileModelByIdentifier(itemIdentifier);
		fileModel.setStatus(status);
	}

	
	public void loadRootItem(String scopeId, final boolean selectRoot){
		
		this.scopeId = scopeId;

		System.out.println("***Start Root load with scopeid: " + scopeId);
		
		
		if(this.scopeId !=null){
			//Load Root with specific scope id
			AppControllerExplorer.rpcWorkspaceService.getRootForTree(scopeId, new AsyncCallback<FolderModel>() {
	
				@Override
				public void onFailure(Throwable caught) {
					new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting root item. " +ConstantsExplorer.TRY_AGAIN, null);
					
				}
	
				@Override
				public void onSuccess(FolderModel result) {
					loadRootInStore(result, selectRoot);
					
				}
			});
		}
		
		else{
			//Load Root without filtering on scope id
			AppControllerExplorer.rpcWorkspaceService.getRootForTree(new AsyncCallback<FolderModel>() {
				
				@Override
				public void onFailure(Throwable caught) {
					new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting root item. " +ConstantsExplorer.TRY_AGAIN, null);
					
				}
	
				@Override
				public void onSuccess(FolderModel result) {
					loadRootInStore(result, selectRoot);
				}
			});
		}		
	}
	
	
	private void loadRootInStore(FolderModel root, boolean selectRootOnLoad){
		
		if(root!=null){
			store.removeAll();
			store.insert(root, 0, false);
			System.out.println("Root Name: " + store.getRootItems().get(0).get(ConstantsExplorer.NAME));
			treePanel.setExpanded(store.getRootItems().get(0),true); //expand level 1
			if(selectRootOnLoad)
				selectRootItem(); //select root item
			
			System.out.println("***End Root load ");
		}
		else
			new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting root item. Root item is null " +ConstantsExplorer.TRY_AGAIN, null);
		
	}
	
	private void addDragAndDrop() {
		
		//Drag & Drop Event
		TreePanelDragSource source = new TreePanelDragSource(treePanel);
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent event) {
				
				GWT.log("***Event Start drag");
				FileModel sel = treePanel.getSelectionModel().getSelectedItem();
				
				if (sel != null && sel == treePanel.getStore().getRootItems().get(0)) {
					event.setCancelled(true);
					event.getStatus().setStatus(false);
					return;
				}
				
				if(sel.isVreFolder() || sel.isSpecialFolder()){
					event.setCancelled(true);
					event.getStatus().setStatus(false);
					return;
				}
				
				if(sel != null && sel.getName()!= null)
					GWT.log("Start drag of " + sel.getName());
				
				super.dragStart(event);
			}
		});
		

		TreePanelDropTarget targetTreePanel = new TreePanelDropTarget(treePanel){
//			@Override
//			protected void onDragDrop(DNDEvent event) {
//				System.out.println("on Drop");
//				super.onDragDrop(event);
//			}
			
//			@Override
//			protected void showFeedback(DNDEvent event) {
//				// TODO Auto-generated method stub
//				super.showFeedback(event);
//				
//			}
			
			@Override
		    protected void showFeedback(DNDEvent event) {
		        if (!isValidDropTarget(event)) {
		            Insert.get().hide();
		            event.getStatus().setStatus(false);
		            return;
		        }
		        super.showFeedback(event);
		    }
			
			@SuppressWarnings("unchecked")
		    private boolean isValidDropTarget(DNDEvent event) {
				TreePanel<FileModel> target = (TreePanel<FileModel>) event.getDropTarget().getComponent();
				TreePanel<FileModel>.TreeNode zone = target.findNode(event.getTarget());
				
		        if (zone == null) {
		            return true; // let it check from super-class
		        }
		        
		        Component com = event.getDragSource().getComponent();
		        
		        if(com instanceof TreePanel<?>){
		        	
		        	TreePanel<FileModel> source = (TreePanel<FileModel>) event.getDragSource().getComponent();
			        List<FileModel> selection = source.getSelectionModel().getSelection();

			        for (FileModel model : selection) {
			                // check the "model" against "zone" and return false
			                // if "zone" is not a valid drop target for "model", otherwise check the next "model"
			                // example:
			                if (source.getStore().getParent(model) == zone.getModel()) 
			                	return false;
			                
//			                if(source.getStore().getParent(model) == target.getModel())
//		                	return false;
			                
			                if(zone.getModel().isSpecialFolder()) //NOT DROPPABLE INTO SPECIAL FOLDER
			                	return false;

			        }
		        	return true;
		        }
		        return false;
		        
//		        TreePanel<FileModel> source = (TreePanel<FileModel>) event.getDragSource().getComponent();
//		        List<FileModel> selection = source.getSelectionModel().getSelection();
//
//		        for (FileModel model : selection) {
//		                // check the "model" against "zone" and return false
//		                // if "zone" is not a valid drop target for "model", otherwise check the next "model"
//		                // example:
//		                if (source.getStore().getParent(model) == zone.getModel()) 
//		                	return false;
//		                
////		                if(source.getStore().getParent(model) == target.getModel())
////		                	return false;
//		        }
//
//		        return true;
		    }

			//Called when drop on folder
			@Override
			protected void handleAppendDrop(DNDEvent event, @SuppressWarnings("rawtypes") TreeNode item) {
				super.handleAppendDrop(event, item);
				
				GWT.log("***Event move handleAppendDrop");
				List<FileModel> listFileModel  = getDragSource(event);
				
				if(listFileModel.size() == 1){
						FileModel destination = getDragDestination(item, DragType.APPEND);
						if(destination != null){
							//REMOVE THIS COMMENT TODO
							eventBus.fireEvent(new MoveItemEvent(listFileModel.get(0), (FolderModel) destination));
							System.out.println("Destination: "  +destination.getName() + " id "+ destination.getIdentifier());	
						}
				}else{
					//MULTIDRAG
				}
				GWT.log("***End Event move handleAppendDrop");
			}
			
//			@Override
//			protected void onDragMove(DNDEvent event){
////				System.out.println("on Drag Move");
//				super.onDragMove(event);
//			}
			
			//Called when drop between files
			@Override
			protected void handleInsertDrop(DNDEvent event, @SuppressWarnings("rawtypes") TreeNode item, int index) {
				super.handleInsertDrop(event, item, index);
				
				GWT.log("***Event move handleInsertDrop");
				List<FileModel> listFileModel  = getDragSource(event);
				if(listFileModel.size() == 1){ //one element dragged
					
					FileModel destination = getDragDestination(item, DragType.INSERT);
					if(destination != null){			
						GWT.log("Destination: "  +destination.getName() + " id "+ destination.getIdentifier());
						//REMOVE THIS COMMENT TODO
						eventBus.fireEvent(new MoveItemEvent(listFileModel.get(0), (FolderModel) destination));
					}
				}else{
					//multi drag
				}
				GWT.log("***End Event move handleInsertDrop");				
			}
			
			private FileModel getDragDestination(@SuppressWarnings("rawtypes") TreeNode item, DragType insertType){
				
				FileModel destination = null;
				if(item!=null){
					destination = (FileModel) item.getModel();
					if(destination != null){
						if(insertType.equals(DragType.APPEND))
							return destination; //APPEND ON FOLDER
						else
							return destination.getParentFileModel(); //INSERT BETWEEN FILE - RETUR FOLDER PARENT
					}
				}
				return destination;
			}
			
		    private List<FileModel> getDragSource(DNDEvent event){
				
		    	List<FileModel> listDraggedFile = new ArrayList<FileModel>();
		    	
				if(event.getData() != null){
					List<TreeStoreModel> listItemsSource =  event.getData();
					GWT.log("Number of move " + listItemsSource.size());
					FileModel sourceFileModel = null; //for print
			
					for(TreeStoreModel itemSource : listItemsSource){
						
						listDraggedFile.add((FileModel) itemSource.getModel());
						sourceFileModel = (FileModel) itemSource.getModel();
						if(sourceFileModel.getParentFileModel()!=null)
							GWT.log("Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag " + " Parent Name: " + sourceFileModel.getParentFileModel().getName() + "id " + sourceFileModel.getParentFileModel().getIdentifier());
						else
							GWT.log("Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag ");
						
						GWT.log("Child count: " + itemSource.getChildCount());
					}
				}
				return listDraggedFile;
			}
			
//			@Override
//			protected void handleInsert(DNDEvent event, TreeNode treeNode){
//				super.handleAppend(event, treeNode);
////				System.out.println("in handle insert");
//			}
		};

		targetTreePanel.setAllowSelfAsSource(true);
		targetTreePanel.setFeedback(Feedback.APPEND);
		targetTreePanel.setScrollElementId(cp.getId());
//		targetTreePanel.setAllowDropOnLeaf(true);
	}

	
	private void addListners() {
		
		treePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileModel>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<FileModel> objEvent) {

				FileModel fileModel = objEvent.getSelectedItem();

				if(rightClick){
//					treePanel.disableEvents(true);
//					deselectItem(fileModel);
//					treePanel.enableEvents(true);
					return;
				}

				System.out.println("***Event selection change");

				if(fileModel != null && !isSearch){	//is null on drag & drop event

					//For printing
					String type;
					if(fileModel.getType()!=null)
						type = fileModel.getType();
					else
						type = "null";
					
					//DEBUG
//					String folderItemType;
//					if(fileModel.getGXTFolderItemType()!=null)
//						folderItemType = fileModel.getGXTFolderItemType().toString();
//					else
//						folderItemType = null;
//					if(fileModel.getParentFileModel()!=null)
//						System.out.println("Item selected " + fileModel.getName() +  " Id " + fileModel.getIdentifier() + " Parent " + fileModel.getParentFileModel().getName() +  " with id " + fileModel.getParentFileModel().getIdentifier() + " IsDirectory " + ""+fileModel.isDirectory() + " type  "+ type + " HLFolderItemType "+folderItemType);
//					else
//						System.out.println("Item selected " + fileModel.getName() +  " Id " + fileModel.getIdentifier() + " Parent null " + " IsDirectory " + ""+fileModel.isDirectory()+ " type  "+ type +" HLFolderItemType "+folderItemType );
					
					System.out.println("Item selected" + fileModel);
					
					eventBus.fireEvent(new SelectedItemEvent(fileModel));

				}
				
				
				System.out.println("***End Event selection change");

			}	
			
		});
		
//		// Listner on Click
		treePanel.addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {
					public void handleEvent(TreePanelEvent<ModelData> be) {
						
						if(be != null){
							if(isSearch){ //TODO WORK AROUND - used after search
								setSearch(false);
//								FileModel fileModel = (FileModel) be.getNode().getModel();
//								reSelectItem(fileModel.getIdentifier());
								
								TreeNode node = be.getNode();
								
								if(node!=null){
									FileModel fileModel = (FileModel) be.getNode().getModel();
									reSelectItem(fileModel.getIdentifier());
								}
							}
						}
						 
					};
				});
		
//		// Listner on Menu Hide
		treePanel.getContextMenu().addListener(Events.Hide, new Listener<MenuEvent>() {

			@Override
			public void handleEvent(MenuEvent be) {
				
				if(be!=null)
					deselectItem(treePanel.getSelectionModel().getSelectedItem());
			}
		});
		
		
		treePanel.addListener(Events.BeforeExpand, new Listener<TreePanelEvent<ModelData>>() {
            public void handleEvent(final TreePanelEvent<ModelData> be) {
            	
             	if(be != null && !isSubTreeLoaded){
             		
             		GWT.log("***Event beforeExpand Node");
					GWT.log("Expand Folder Model: " + be.getNode().getModel().get(ConstantsExplorer.NAME));
					final FolderModel folder = (FolderModel) be.getNode().getModel();
					int numChildrenFolder = store.getChildCount(folder);
					
					if (folder!=null){
						eventBus.fireEvent(new ExpandFolderEvent(folder));
						if (numChildrenFolder==0) {
							treePanel.mask(ConstantsExplorer.LOADING,ConstantsExplorer.LOADINGSTYLE);
							loadTreeLevelFromWorkspace(folder);
						}
					}
					GWT.log("***End Event beforeExpand Node");
             	}
            }	 
        });
		
		// Add lister to context menu
		treePanel.addListener(Events.ContextMenu, new Listener<TreePanelEvent<FileModel>>() {

			public void handleEvent(TreePanelEvent<FileModel> be) {

				GWT.log("***Event Context Menu open");

				@SuppressWarnings("rawtypes")
				TreeNode node = be.getNode();

				if(node!=null){
					GWT.log("Menu on: " + node.getModel().get(ConstantsExplorer.NAME));
					GWT.log("node "+ 	treePanel.findNode(be.getTarget()));
				}else{
					GWT.log("Menu on: null");
					GWT.log("node "+ 	treePanel.findNode(be.getTarget()));
				}
				List<FileModel> listSelected = treePanel.getSelectionModel().getSelectedItems();
//
				if (listSelected != null && listSelected.size() > 0) {			
					manageContextMenu();
				}
//
			}
				

		});
		
		
	}
	
	private void manageContextMenu(){
		
		System.out.println("***Event Context Menu open");
			
		contextMenuTree.setListSelectedItems(treePanel.getSelectionModel().getSelectedItems()); //Set items list selected in context menu tree
		
		FileModel selectedItem = treePanel.getSelectionModel().getSelectedItems().get(0); //get first element
		
		if (store.getRootItems().get(0).equals(selectedItem)) {//selected root item?
			setVisibleRenameAndRemoveContextMenu(false);
			setVisibileGetWebDavUrl(true);
		}
		else{
			setVisibleRenameAndRemoveContextMenu(true);
			setVisibileGetWebDavUrl(false);
		}
		
			contextMenuTree.contextMenuSwitch(selectedItem);

		System.out.println("***End Event Context Menu open");
	}
	
	
	public void reloadTreeLevelAndExpandFolder(String folderIdentifier, boolean expandFolder){
		final FolderModel folder = (FolderModel) getFileModelByIdentifier(folderIdentifier);
		reloadTreeLevelAndExpandFolder(folder, expandFolder);
	}
	
	public void addItemIdAndExpandFolder(final FileModel parent, String itemId, boolean expandFolder){
		
		treePanel.mask(ConstantsExplorer.VALIDATINGOPERATION,ConstantsExplorer.LOADINGSTYLE);
		AppControllerExplorer.rpcWorkspaceService.getItemForFileTree(itemId, new AsyncCallback<FileModel>(){

			@Override
			public void onFailure(Throwable caught) {
				
				if(caught instanceof SessionExpiredException){
					GWT.log("Session expired");
					eventBus.fireEvent(new SessionExpiredEvent());
					return;
				}
				treePanel.unmask();
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting item from workspace." +ConstantsExplorer.TRY_AGAIN, null);
				removeAllAndRecoveryRoot();
			}
			
			@Override
			public void onSuccess(FileModel result) {
				GWT.log("GetItemForFileTree adding: "+result);
				treePanel.unmask();
				store.add(parent,result,false);
			}
		});
	}
	
	private void reloadTreeLevelAndExpandFolder(final FolderModel folder, final boolean expandFolder){
		
		AppControllerExplorer.rpcWorkspaceService.getFolderChildren(folder, new AsyncCallback<List<FileModel>>(){

		@Override
		public void onFailure(Throwable caught) {
			
			if(caught instanceof SessionExpiredException){
				GWT.log("Session expired");
				eventBus.fireEvent(new SessionExpiredEvent());
				return;
			}
			treePanel.unmask();
			new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting folder children items." +ConstantsExplorer.TRY_AGAIN, null);
			removeAllAndRecoveryRoot();
		}

		@Override
		public void onSuccess(List<FileModel> result) {
			treePanel.mask(ConstantsExplorer.VALIDATINGOPERATION,ConstantsExplorer.LOADINGSTYLE);
			store.removeAll(folder);
//			addChildrenToFolder(folder.getIdentifier(), result);
			addChildrenToFolder(folder, result);
			setExpandTreeLevel(folder, expandFolder);
			treePanel.unmask();
		}
	});
		
	}
	
	private void loadTreeLevelFromWorkspace(final FolderModel folder){
		GWT.log("Start RPC - getFolderChildren");	
//		Log.info("Start RPC - getFolderChildren");	
//		final CountTimer count = new CountTimer(1000);

		AppControllerExplorer.rpcWorkspaceService.getFolderChildren(folder, new AsyncCallback<List<FileModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				
				if(caught instanceof SessionExpiredException){
					GWT.log("Session expired");
					eventBus.fireEvent(new SessionExpiredEvent());
					return;
				}
					
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting folder children items. " +ConstantsExplorer.TRY_AGAIN, null);
				removeAllAndRecoveryRoot();
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				if(treePanel.isMasked())
					treePanel.unmask();
				
				store.removeAll(folder);
				addChildrenToFolder(folder, result);
				GWT.log("End RPC - getFolderChildren");
			}
	
		});
		
	}
	
	public void addChildrenToFolder(String idParent, List<FileModel> children){
		
		FileModel parent = getFileModelByIdentifier(idParent);
		if(parent!=null)
			store.add(parent,children,false);
		
	}
	
	private void addChildrenToFolder(FolderModel parent, List<FileModel> children){
		
		if(parent!=null){
			store.add(parent,children,false);
			System.out.println("Added children in store");
		}
	}
	
	private void setVisibleRenameAndRemoveContextMenu(boolean bool){
		
		treePanel.getContextMenu().getItemByItemId(WorkspaceOperation.REMOVE.getId()).setVisible(bool);
		treePanel.getContextMenu().getItemByItemId(WorkspaceOperation.RENAME.getId()).setVisible(bool);
		
	}
	
	private void setVisibileGetWebDavUrl(boolean bool){
		treePanel.getContextMenu().getItemByItemId(WorkspaceOperation.WEBDAV_URL.getId()).setVisible(bool);
	}

	private void setAlphanumericStoreSorter(){
		
		// Sorting files
		store.setStoreSorter(new StoreSorter<FileModel>() {

			@Override
			public int compare(Store<FileModel> store, FileModel m1, FileModel m2, String property) {
				boolean m1Folder = m1 instanceof FolderModel;
				boolean m2Folder = m2 instanceof FolderModel;

				if (m1Folder && !m2Folder) {
					return -1;
				} else if (!m1Folder && m2Folder) {
					return 1;
				}
				
				if(m1.isSpecialFolder() && !m2.isSpecialFolder())
					return -1;
				else if(!m1.isSpecialFolder() && m2.isSpecialFolder())
					return 1;

				return m1.getName().compareToIgnoreCase(m2.getName());
			}
		});
	}

	//TODO for debug
	private void printingTreeLevel(FileModel item){
		
		List<FileModel> children = treePanel.getStore().getChildren(item);
		
		if (children != null) {
			
			for (FileModel item2 : children) {

				System.out.println("      " + item2.getName() + " ID " + item2.getIdentifier() + " isDirectory " + item.isDirectory());

				printingTreeLevel(item2);
			}

			System.out.println("  ");
		}
	}
	
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public FileModel getFileModelByIdentifier(String identifier){
		
		return treePanel.getStore().findModel(ConstantsExplorer.IDENTIFIER, identifier);
	}
	
	public int getChildrenNumber(String identifier){
		
		FolderModel fileModel = (FolderModel) getFileModelByIdentifier(identifier);
		return getChildrenNumber(fileModel);
	}
	
	private int getChildrenNumber(FolderModel folder){
		
		return store.getChildCount(folder);
	}
	
	
	/**
	 * 
	 * @param identifier (MANDATORY)
	 * @return
	 */
	public boolean deleteItem(String identifier) {
		
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		
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
	 * @param newName  (MANDATORY)
	 * @param extension OPTIONAL - string or null
	 */
	private boolean renameItem(FileModel fileTarget, String newName, String extension) {

		if(fileTarget!=null){
			Record record = treePanel.getStore().getRecord(fileTarget);
			if(record!=null){
				if(extension!= null)
					record.set(ConstantsExplorer.NAME, newName+extension);
				else
					record.set(ConstantsExplorer.NAME, newName);
				
				return true;
			}
			else
				System.out.println("Record Error: file target with " + fileTarget.getIdentifier() + " identifier not exist in store" );
		}
		else
			System.out.println("Rename Error: file target not exist in store" );
		
		return false;
	}
	
	
	/**
	 * 
	 * @param fileTarget (MANDATORY)
	 * @param newName  (MANDATORY)
	 * @param extension OPTIONAL - string or null
	 */
	public boolean renameItem(String identifier, String newName, String extension) {
		
//		FileModel fileTarget = treePanel.getStore().findModel(ConstantsExplorer.IDENTIFIER, identifier);
		
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		
		return renameItem(fileTarget,newName,extension);
		
	}

	/**
	 * 
	 * @param fileTarget (MANDATORY)
	 * @return
	 */
	private boolean deleteItem(FileModel fileTarget) {

		Record record = treePanel.getStore().getRecord(fileTarget);
		
		if (record != null) {
			
			FileModel item = (FileModel) record.getModel();
			treePanel.getStore().remove(item);
			
			return true;
		} else
			System.out.println("Record Error: file target with "
					+ fileTarget.getIdentifier()
					+ " identifier not exist in store");

		return false;

	}
	
	public void addItem(String parentId, FileModel child, boolean bool) {
		FileModel parent = getFileModelByIdentifier(parentId);
		if(parent!=null)
			addItem(parent,child,bool);
	}
	

	private void addItem(FileModel parent, FileModel child, boolean bool) {
		store.add(parent, child, bool); 
	}

	/**
	 * 
	 * @param parent
	 * @param bool expand true/false
	 */
	private void setExpandTreeLevel(FileModel parent, boolean bool) {
		treePanel.setExpanded(parent, bool);
	}
	
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public boolean isExpanded(String identifier){
		
		if(identifier==null)
			return false;
		
		
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		
		if(fileTarget==null)
			return false;
		
		return treePanel.isExpanded(fileTarget);
	}
	
	public void setExpandTreeLevel(String identifier, boolean bool) {
		FileModel item = getFileModelByIdentifier(identifier);
		if(item!=null)
			treePanel.setExpanded(item, bool); 
	}
	
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public boolean selectItem(String identifier){
		
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		if(fileTarget!=null){
			treePanel.getSelectionModel().select(fileTarget, true); //Select the item
			return true;
		}
		return false;
	}
	
	public boolean reSelectItem(String identifier){
		
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		if(fileTarget!=null){
			treePanel.getSelectionModel().deselect(fileTarget);
			treePanel.getSelectionModel().select(fileTarget, true); //Select the item
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean selectItem(FileModel fileTarget){
		
		if(fileTarget!=null){
			treePanel.getSelectionModel().select(fileTarget, true); //Select the item
			return true;
		}
		return false;
	}

	public boolean isSubTreeLoaded() {
		return isSubTreeLoaded;
	}

	public void setSubTreeLoaded(boolean isSubTreeLoaded) {
		this.isSubTreeLoaded = isSubTreeLoaded;
	}
	
	/**
	 * Select root by default
	 */
	public void removeAllAndRecoveryRoot(){
		store.removeAll();
		loadRootItem(scopeId,true);	
	}
	
	public void removeAllAndRecoveryRoot(boolean selectRoot){
		store.removeAll();
		loadRootItem(scopeId,selectRoot);	
	}
	
	public void selectRootItem(){
		FileModel selectedItem = treePanel.getSelectionModel().getSelectedItem();
		if(selectedItem!=null)
			 treePanel.getSelectionModel().deselect(selectedItem);
		
		treePanel.getSelectionModel().select(store.getRootItems().get(0), true); //select root item
	}
	
	public void deselectItem(String identifier){
		FileModel fileTarget = getFileModelByIdentifier(identifier);
		if(fileTarget!=null)
			 treePanel.getSelectionModel().deselect(fileTarget);
	}
	
	public void deselectItem(FileModel fileTarget){
		if(fileTarget!=null)
			 treePanel.getSelectionModel().deselect(fileTarget);
	}
	
	public FileModel getSelectedFileModelItem(){
		FileModel selectedItem = treePanel.getSelectionModel().getSelectedItem();
		if(selectedItem!=null)
			 return selectedItem;
		
		return store.getRootItems().get(0); //return root item
	}

	public boolean isSearch() {
		return isSearch;
	}

	public void setSearch(boolean isSearch) {
		System.out.println("***Set Search: " + isSearch);
		this.isSearch = isSearch;
	}
	
	public void setSizeTreePanel(int width, int height){
		treePanel.setSize(width, height);
	}


	public ContextMenuTree getContextMenuTree() {
		return contextMenuTree;
	}
	
	public void setHeaderTreeVisible(boolean bool){
		cp.setHeaderVisible(bool);
	}
	
	public FileModel getRootItem(){
		return treePanel.getStore().getRootItems().get(0);
	}
	
}