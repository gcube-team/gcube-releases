package org.gcube.portlets.user.messages.client.view.message;

import java.util.List;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.event.DeleteMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent.MarkType;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent.OpenType;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentsEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ContextMenuMessages {
	
	
	private Menu contextMenu = new Menu();
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
	private Grid<MessageModel> gridMessages;
	 
	
	public ContextMenuMessages(Grid<MessageModel> gridMessages) {
		
		//Context Menu
	    this.contextMenu.setWidth(140);  
	    this.gridMessages = gridMessages;
	    createContextMenu();
	    
	    gridMessages.setContextMenu(contextMenu);
	}
	
	public List<MessageModel> getMessagesSelected(){
		return gridMessages.getSelectionModel().getSelectedItems();  
	}
	
	
	private void createContextMenu() {

	    
	    MenuItem openMessage = new MenuItem();  
	    openMessage.setId(ConstantsPortletMessages.OPM);
	    openMessage.setText(ConstantsPortletMessages.MESSAGE_REPLY);  
	    openMessage.setIcon(Resources.getIconOpenEmail());  
	    
	    openMessage.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	List<MessageModel> listMessageModel = getMessagesSelected();  

		        for(MessageModel message : listMessageModel){
	        	  eventBus.fireEvent(new OpenMessageEvent(message.getId(), OpenType.REPLY, message.getMessageType()));
	        	  if(message.getIsRead().equals("false"))
	        		  eventBus.fireEvent(new MarkMessageEvent(message, true, MarkType.BOTH));
		        }
	        }  
	      }); 
	    
	    contextMenu.add(openMessage); 
	    
	    
	    MenuItem saveAttachs = new MenuItem();  
	    saveAttachs.setId(ConstantsPortletMessages.SVA);
	    saveAttachs.setText(ConstantsPortletMessages.MESSAGE_SAVE_ATTACHS);  
	    saveAttachs.setIcon(Resources.getIconSaveAttachments());  
	    
	    saveAttachs.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;  

	          if(selected!=null)
	        	  eventBus.fireEvent(new SaveAttachmentsEvent(selected.getId(), selected.getMessageType()));
	        }  
	      }); 
	    
	    contextMenu.add(saveAttachs); 
	    
	    
	    MenuItem forwardMessage = new MenuItem();  
	    forwardMessage.setId(ConstantsPortletMessages.FWM);
	    forwardMessage.setText(ConstantsPortletMessages.MESSAGE_FORWARD_MESSAGE);  
	    forwardMessage.setIcon(Resources.getIconEmailForward16x16());  
	    
	    forwardMessage.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	          MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;  

	          if(selected!=null)
	        	  eventBus.fireEvent(new OpenMessageEvent(selected.getId(), OpenType.FORWARD, selected.getMessageType()));
	        }  
	      }); 
	    
	    contextMenu.add(forwardMessage); 
	    
	    
	    MenuItem deleteMessage = new MenuItem();  
	    deleteMessage.setId(ConstantsPortletMessages.DLM);
	    deleteMessage.setText(ConstantsPortletMessages.MESSAGE_DELETE_MESSAGE);  
	    deleteMessage.setIcon(Resources.getIconDeleteMessage16x16());  
	    
	    deleteMessage.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        List<MessageModel> listMessageModel = getMessagesSelected();
	        final MessageModel message = listMessageModel.get(0);
	        
	        if(message!=null){
	        	 final MessageBox confirm = MessageBox.confirm("Delete confirm?", "Do you want delete: \""+message.getSubject()+"\"?", null);
	        	 
	        	 confirm.addCallback(new Listener<MessageBoxEvent>() {
					
					@Override
					public void handleEvent(MessageBoxEvent be) {
						//IF NOT CANCELLED
						String clickedButton = be.getButtonClicked().getItemId();
						if(clickedButton.equals(Dialog.YES)){
							 eventBus.fireEvent(new DeleteMessageEvent(message));
							 confirm.close();
						}
						
						if(clickedButton.equals(Dialog.NO)){
							confirm.close();
						}
					}
				});
	        	 
	        	confirm.show();
	        }

//	        for(MessageModel message : listMessageModel){
//	        	  eventBus.fireEvent(new DeleteMessageEvent(message));
//	        	}
	        }  
	      }); 
	    
	    contextMenu.add(deleteMessage); 
	    
	    contextMenu.add(new SeparatorMenuItem());

	    MenuItem markRead = new MenuItem();  
	    markRead.setId(ConstantsPortletMessages.MKR);
	    markRead.setText(ConstantsPortletMessages.MESSAGE_MARK_AS_READ);  
	    markRead.setIcon(Resources.getIconEmailRead());  
	    
	    markRead.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;   

	            if(selected!=null)
		        	  eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.READ));
	        }  
	      }); 
	    
	    contextMenu.add(markRead); 
	    
	    
	    MenuItem markeNotRead = new MenuItem();  
	    markeNotRead.setId(ConstantsPortletMessages.MKNR);
	    markeNotRead.setText(ConstantsPortletMessages.MESSAGE_MARK_AS_NOTREAD);  
	    markeNotRead.setIcon(Resources.getIconEmailNotRead());  
	    
	    markeNotRead.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	         MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;   

	          if(selected!=null)
	        	  eventBus.fireEvent(new MarkMessageEvent(selected, false, MarkType.READ));
	        }  
	      }); 
	    
	    contextMenu.add(markeNotRead); 
  
	}

	public Menu getContextMenu() {
		return contextMenu;
	}
	
	public void contextMenuSwitch(MessageModel selectedItem) {
		
		
		contextMenu.getItemByItemId(ConstantsPortletMessages.MKR).setVisible(false); //mark read
		contextMenu.getItemByItemId(ConstantsPortletMessages.MKNR).setVisible(false); //mark not read

		
		if(selectedItem.getIsRead().equals("true"))
			contextMenu.getItemByItemId(ConstantsPortletMessages.MKNR).setVisible(true);
		else
			contextMenu.getItemByItemId(ConstantsPortletMessages.MKR).setVisible(true);
		
		if(selectedItem.getNumAttchments()>0)
			contextMenu.getItemByItemId(ConstantsPortletMessages.SVA).setVisible(true); //save attach
		else
			contextMenu.getItemByItemId(ConstantsPortletMessages.SVA).setVisible(false);
	}
	
	
	

}
