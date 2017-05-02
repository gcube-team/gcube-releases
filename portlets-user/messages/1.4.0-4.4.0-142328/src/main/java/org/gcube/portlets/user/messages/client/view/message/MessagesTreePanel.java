package org.gcube.portlets.user.messages.client.view.message;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.event.GetAllNewMessagesEvent;
import org.gcube.portlets.user.messages.client.event.LoadMessagesEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.FolderModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessagesTreePanel extends LayoutContainer{
	
	private TreePanel<FileModel> treeMessagePanel;
	private TreeStore<FileModel> store;
	private ContentPanel cp = new ContentPanel();
	private ContextMenuInBox cm = null;
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
	private Button btnDownloadMessages = null;
	private ToolBar toolBar = new ToolBar();
	
	protected void onRender(Element parent, int pos) {  
	    super.onRender(parent, pos);  
	}
	
	public MessagesTreePanel() {
		
		//Init Store
		store = new TreeStore<FileModel>();
		
		treeMessagePanel = new TreePanel<FileModel>(store){	
			@Override
			public boolean hasChildren(FileModel parent) {
				if (parent instanceof FolderModel) {
					return true;
				}
				return super.hasChildren(parent);
			}	
		};
		
		treeMessagePanel.setStateful(false);
		treeMessagePanel.setDisplayProperty(ConstantsPortletMessages.NAME);
		treeMessagePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		// statefull components need a defined id
		treeMessagePanel.setId("treeMessagePanel");
		treeMessagePanel.setStyleAttribute("height","200px");

		// SET icons in tree panel
		treeMessagePanel.setIconProvider(new ModelIconProvider<FileModel>() {
		
			public AbstractImagePrototype getIcon(FileModel model) {

				if (model.getIdentifier().equals(GXTCategoryItemInterface.MS_RECEIVED)) 
					return Resources.getIconMessagesReceived();
				else if (model.getIdentifier().equals(GXTCategoryItemInterface.MS_SENT)) 
					return Resources.getIconMessagesSent();
					
			
				return Resources.getIconEmail();
			}
		});
		
		createMessagesTree();
		
		cp.setHeading(ConstantsPortletMessages.MESSAGES);
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBodyStyle("padding-top: 5px");
		cp.setScrollMode(Scroll.AUTOY);
		
		initToolBar();
		
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		cp.setTopComponent(toolBar);
		cp.add(treeMessagePanel);
		
		add(cp);

		addListner();
		addContextMenu();
				
	}
	
	
	private void initToolBar() {
		
		btnDownloadMessages = new Button(ConstantsPortletMessages.MESSAGE_GET_NEW_MESSAGES, Resources.getIconDownloadEmails());
		btnDownloadMessages.setScale(ButtonScale.MEDIUM);
		btnDownloadMessages.setIconAlign(IconAlign.TOP);
		btnDownloadMessages.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnDownloadMessages);

		btnDownloadMessages.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
//				if(messagesPanelContainer.getCurrentMessageType().equals(GXTCategoryItemInterface.MS_SENT)){
//					eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_SENT, false));
//				}else if(messagesPanelContainer.getCurrentMessageType().equals(GXTCategoryItemInterface.MS_RECEIVED)){
//						eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_RECEIVED, false));
//					}
				
				eventBus.fireEvent(new GetAllNewMessagesEvent());
			}
			
		});
	}

	private void addContextMenu() {
		cm = new ContextMenuInBox(treeMessagePanel);
	}

	private void createMessagesTree(){
		
		FolderModel rootFolder = new FolderModel(GXTCategoryItemInterface.MS_MESSAGES, "Messages", null, false);
		store.add(rootFolder, false); //add root
		
		List<FileModel> listFolderMessages = new ArrayList<FileModel>();
		
		FileModel sentFolder = new FileModel(GXTCategoryItemInterface.MS_RECEIVED, "Received", store.getRootItems().get(0), false);
		FileModel receivedFolder = new FileModel(GXTCategoryItemInterface.MS_SENT, "Sent", store.getRootItems().get(0), false);
		
		listFolderMessages.add(sentFolder); //add sent to root
		listFolderMessages.add(receivedFolder); //add received to root
		
		store.add(store.getRootItems().get(0), listFolderMessages, true);
		
		treeMessagePanel.setExpanded(store.getRootItems().get(0),true); //expand level 1
					
//		FileModel receivedFolder = store.findModel("Received");
		
	}
	
	public void setSizeMessagesPanel(int width, int height) {
		cp.setSize(width, height);	
	}
	
	
	public void addListner(){
	
		treeMessagePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FileModel>() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent<FileModel> objEvent) {
			
			FileModel fileModel = objEvent.getSelectedItem();

			//IF fileModel is not null and selected is not root
			if(fileModel!= null && !fileModel.getIdentifier().equals(GXTCategoryItemInterface.MS_MESSAGES)){
				
//				AppControllerExplorer.getEventBus().fireEvent()
				
				if(fileModel.getIdentifier().equals(GXTCategoryItemInterface.MS_RECEIVED))
					eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_RECEIVED, false));
				else
					eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_SENT, false));
				
			}
	
		}	
		
		});
		
		treeMessagePanel.addListener(Events.ContextMenu, new Listener<TreePanelEvent<FileModel>>(){

			@Override
			public void handleEvent(TreePanelEvent<FileModel> be) {
				if(getCurrentSelection().getIdentifier().equals(GXTCategoryItemInterface.MS_MESSAGES)){ //Hide context menu on root item
					be.setCancelled(true);
				}
				
			}
		});
		
	
	}
	
	public void setSelect(String messageType){
		
		FileModel target = null;
		
		if(messageType.equals(GXTCategoryItemInterface.MS_RECEIVED))	
			 target = treeMessagePanel.getStore().findModel(ConstantsPortletMessages.IDENTIFIER, GXTCategoryItemInterface.MS_RECEIVED);
		else
			 target = treeMessagePanel.getStore().findModel(ConstantsPortletMessages.IDENTIFIER, GXTCategoryItemInterface.MS_SENT);
		
		if(target!=null){
			if(treeMessagePanel.getSelectionModel().isSelected(target)) //if target is selected
				treeMessagePanel.getSelectionModel().deselect(target); //deselect 
			treeMessagePanel.getSelectionModel().select(target, true); //Select
		}
	}
	
	
	public FileModel getCurrentSelection(){
		
		return treeMessagePanel.getSelectionModel().getSelectedItem();
	}
}
