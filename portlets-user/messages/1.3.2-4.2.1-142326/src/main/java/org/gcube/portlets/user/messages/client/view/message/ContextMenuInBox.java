package org.gcube.portlets.user.messages.client.view.message;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.event.LoadMessagesEvent;
import org.gcube.portlets.user.messages.client.event.SendMessageEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ContextMenuInBox {
	
	private Menu contextMenu = new Menu();
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
//	private Grid<MessageModel> gridMessages;
	private TreePanel<FileModel> treePanelMessages = null;
	 
	
	public ContextMenuInBox(TreePanel<FileModel> treePanelMessages) {
		
		//Context Menu
	    this.contextMenu.setWidth(150);  
	    this.treePanelMessages = treePanelMessages;
	    createContextMenu();
	    
	    treePanelMessages.setContextMenu(contextMenu);
	}
	
	
	private void createContextMenu() {

		//downloadMessages
	    MenuItem downloadMessages = new MenuItem();  
	    downloadMessages.setId(ConstantsPortletMessages.DWM);
	    downloadMessages.setText(ConstantsPortletMessages.MESSAGE_GET_NEW_MESSAGES);  
	    downloadMessages.setIcon(Resources.getIconDownloadEmails16x16());  
	    
	    downloadMessages.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	          FileModel selected = treePanelMessages.getSelectionModel().getSelectedItem();  

	          if(selected!=null){
	        	  
	        	  if(selected.getIdentifier().equals(GXTCategoryItemInterface.MS_RECEIVED)){
	        		  eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_RECEIVED, false));
	        	  }else if(selected.getIdentifier().equals(GXTCategoryItemInterface.MS_SENT)){
	        		  eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_SENT, false));
	        	  }
	          }
	        }  
	      }); 
	    
	    contextMenu.add(downloadMessages); 
	    contextMenu.add(new SeparatorToolItem()); 
	    
	    
		//create new message
	    MenuItem createNewMessage = new MenuItem();  
	    createNewMessage.setId(ConstantsPortletMessages.CNM);
	    createNewMessage.setText(ConstantsPortletMessages.MESSAGE_CREATE_NEW_MESSAGE);  
	    createNewMessage.setIcon(Resources.getIconNewMail16x16());  
	    createNewMessage.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	eventBus.fireEvent(new SendMessageEvent(null));
	        }
	        		  
	      }); 
	    
	    contextMenu.add(createNewMessage); 
	}
	
	
	public Menu getContextMenu(){
		return contextMenu;
	}

}
