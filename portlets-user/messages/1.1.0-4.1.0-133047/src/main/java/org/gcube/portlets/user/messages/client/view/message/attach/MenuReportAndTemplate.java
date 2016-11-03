package org.gcube.portlets.user.messages.client.view.message.attach;

import java.util.logging.Level;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.alert.MessageBoxAlert;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentAndOpenEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.client.view.window.WindowOpenUrl;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MenuReportAndTemplate implements AttachOpenListner{

	private FileModel file;
	private Menu menu = null;
	private MessageModel message;
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
	private MenuReportAndTemplate instance;
	
	public MenuReportAndTemplate(Menu menu2, FileModel fileModel, MessageModel messageSelected) {
		this.file = fileModel;
		
		if(menu2!=null)
			menu = menu2;
		else
			menu = new Menu();
		
		this.message = messageSelected;
		this.instance = this;

		menu.add(new SeparatorMenuItem());
		
		
		MenuItem saveAndOpen = new MenuItem();  
	    saveAndOpen.setText("Save and Open");
	    
	    if(fileModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.REPORT))
	    	saveAndOpen.setIcon(Resources.getIconReport());  
	    else
	    	saveAndOpen.setIcon(Resources.getIconReportTemplate());
	    
	    saveAndOpen.addSelectionListener(new SelectionListener<MenuEvent>() {  
	        public void componentSelected(MenuEvent ce) { 
	        	
	        	ConstantsPortletMessages.messagesLogger.log(Level.INFO, "Message id:  " + message.getId() + "save and open attach with id: "+file.getIdentifier() + " name: " + file.getName());
	        	
	        	eventBus.fireEvent(new SaveAttachmentAndOpenEvent(message.getId(), message.getMessageType(), file.getIdentifier(), instance));
	        
	        }  
	     }); 
		    
		menu.add(saveAndOpen); 
	}
		
	AsyncCallback<String> rpcOpenReportsOrTemplate = new AsyncCallback<String>(){

		@Override
		public void onFailure(Throwable caught) {
			new MessageBoxAlert("Error", "Sorry an error occurred on server when getting application profile - attribute idreport", null);
			
		}

		@Override
		public void onSuccess(String url) {
			String reportUrl = url;
			System.out.println("reportUrl " +reportUrl);
			new WindowOpenUrl(reportUrl, "_self", "");
			
		}
		
	};

	@Override
	public void onSavingComplete(String oid) {
	
		MessagesApplicationController.rpcMessagesManagementService.getURLFromApplicationProfile(oid, rpcOpenReportsOrTemplate);
		
	}
		
}
