package org.gcube.portlets.user.messages.client.view.message.attach;

import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.alert.InfoDisplay;
import org.gcube.portlets.user.messages.client.alert.MessageBoxAlert;
import org.gcube.portlets.user.messages.client.event.FileDownloadEvent;
import org.gcube.portlets.user.messages.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AttachOpenHandler {
	
	
	private FileModel attachment;
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
	private MessageModel message;
	private int posX;
	private int posY;
	private Menu menu = new Menu();

	/**
	 * 
	 * @param attach
	 */
	public AttachOpenHandler(FileModel attach, MessageModel messageSelected, int clientX, int clientY){
		this.setAttach(attach);
		this.setMessageSelected(messageSelected);
		this.posX = clientX;
		this.posY = clientY;
		
		MenuItem open = new MenuItem();  
		open.setText("Show");
		open.setIcon(Resources.getIconShow());  
	    
		open.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	eventBus.fireEvent(new FileDownloadEvent(attachment.getIdentifier(), attachment.getName(), DownloadType.SHOW));
	        	
	        }  
	     }); 
		
		menu.add(open); 
		
		MenuItem save = new MenuItem();  
		save.setText("Save");
		save.setIcon(Resources.getIconSaveAttachments());  
	    
		save.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        
	        	final InfoDisplay saving = new InfoDisplay("Info","saving in progress...");

	        	MessagesApplicationController.rpcMessagesManagementService.saveAttachment(message.getId(), attachment.getIdentifier(), message.getMessageType(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry an error occurred on server when saving attachment. Please try again", null);
						
					}

					@Override
					public void onSuccess(String oid) {
						
						if(saving.isAttached())
							saving.hide();
						
						if(oid!=null)
							new InfoDisplay("Info","Attachment has been saved..");	
						else	
							new InfoDisplay("Error","Sorry an error occurred when saving attach, please try again");
					}
				});
	        	
	        }  
	     }); 
		    
		menu.add(save); 
		
		addSpecificHandlerForAttach();
		
		menu.showAt(posX, posY);
	}
	
	
	/**
	 * 
	 */
	public void addSpecificHandlerForAttach(){
		
		if(!attachment.isDirectory()){
				
				switch (attachment.getGXTFolderItemType()) {
					case REPORT:
						
						new MenuReportAndTemplate(menu, attachment, message);
						
						break;
						
					case REPORT_TEMPLATE:
						
						new MenuReportAndTemplate(menu, attachment, message);
						
						break;
		
					default:
						
//						eventBus.fireEvent(new FileDownloadEvent(attachment.getIdentifier(), attachment.getName(), DownloadType.SHOW));
						break;
					}
					
			}
	}
	

	public FileModel getAttach() {
		return attachment;
	}

	public void setAttach(FileModel attach) {
		this.attachment = attach;
	}


	public MessageModel getMessageSelected() {
		return message;
	}


	public void setMessageSelected(MessageModel messageSelected) {
		this.message = messageSelected;
	}

}
