package org.gcube.portlets.user.messages.client.view.message;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.event.GridMessageSelectedEvent;
import org.gcube.portlets.user.messages.client.event.GridMessageUnSelectedEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent.MarkType;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent.OpenType;
import org.gcube.portlets.user.messages.client.event.PreviewMessageEvent;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentsEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.client.view.message.attach.AttachButton;
import org.gcube.portlets.user.messages.client.view.message.attach.AttachOpenHandler;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridViewConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtGridMessagesFilterPanel extends LayoutContainer {

	private ContentPanel cpMessages = new ContentPanel();
	private ContentPanel cpBody = new ContentPanel();
	private ContentPanel north = new ContentPanel();
	private ContentPanel center = new ContentPanel(); 
	private ListStore<MessageModel> store =  new ListStore<MessageModel>();
	private Grid<MessageModel> gridMessages;
	private ContextMenuMessages contextMenuMessages;
	private List<Integer> rowBold = new ArrayList<Integer>();
	private TextArea textBodyValue = new TextArea();
	private String emptyBody = "\nSelect a message...";
	private String headerTitle;
	private String emptyTilte = "From/To: empty - Date: empty";
	private ToolBar toolBarAttachs = new ToolBar();
	private Button buttSaveAttachs = new Button(ConstantsPortletMessages.MESSAGE_SAVE_ATTACHS);
	private static int MAX_LENGTH = 30;
	private int attachButtonWidth = 200;
	private int attachButtonHeight = 23;
	private String currentMessageType = null;
	
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();

	public GxtGridMessagesFilterPanel() {
		this.textBodyValue.setText(emptyBody);
		this.textBodyValue.setSize("99%", "99%");
		this.textBodyValue.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		this.headerTitle = emptyTilte;
		initGridMessagesPanel();
		addContextMenu();
		addListnerOnContextMenu();
		createButtonSaveAttach();
	}
	
	public List<MessageModel> getMessagesSelected(){
		
		List<MessageModel> selected = gridMessages.getSelectionModel().getSelectedItems();
		
		if(selected!=null && selected.size()>0)
			return selected;
		
		return null;
	}
	
	private void addContextMenu() {
		
		contextMenuMessages = new ContextMenuMessages(gridMessages);
		
	}

	private void initGridMessagesPanel() {
		
		final BorderLayout layout = new BorderLayout();  
		setLayout(layout);  
		    
		ColumnConfig subject = new ColumnConfig(ConstantsPortletMessages.SUBJECT, ConstantsPortletMessages.SUBJECT, 300);
		ColumnConfig fromLogin = new ColumnConfig(ConstantsPortletMessages.FULLNAME, ConstantsPortletMessages.FULLNAME, 100);
		ColumnConfig sentDate = new ColumnConfig(ConstantsPortletMessages.DATE, ConstantsPortletMessages.DATE, 100);
		ColumnConfig numAttachs = new ColumnConfig(ConstantsPortletMessages.ATTACHS, ConstantsPortletMessages.ATTACHS, 100);
		ColumnModel cm = new ColumnModel(Arrays.asList(subject, fromLogin, sentDate, numAttachs));
		
		cpMessages.setBodyBorder(false);
		cpMessages.setHeaderVisible(false);
		cpMessages.setLayout(new FitLayout());

		GridFilters filters = new GridFilters();
		filters.setLocal(true);

		StringFilter subjectFilter = new StringFilter(ConstantsPortletMessages.SUBJECT);
		StringFilter fromFilter = new StringFilter(ConstantsPortletMessages.FULLNAME);
		DateFilter dateFilter = new DateFilter(ConstantsPortletMessages.DATE);
		
		StringFilter attachFilter = new StringFilter(ConstantsPortletMessages.ATTACHS);
		
		filters.addFilter(subjectFilter);
		filters.addFilter(fromFilter);
		filters.addFilter(dateFilter);
		filters.addFilter(attachFilter);
		
		gridMessages = new Grid<MessageModel>(store, cm);
		store.sort(ConstantsPortletMessages.DATE, SortDir.DESC);
		gridMessages.getView().setViewConfig(new GridViewConfig(){
			
			@Override
			 public String getRowStyle(ModelData model, int rowIndex, ListStore<ModelData> ds) {
				
					if (model != null)
						if (model.get(ConstantsPortletMessages.ISREAD).equals("false")){
							return "row-relevant";
						}
					return "";
				 
			 }
        });

		gridMessages.getView().setAutoFill(true);
		gridMessages.getView().setForceFit(true);
		gridMessages.getView().setEmptyText(ConstantsPortletMessages.EMPTY);
		gridMessages.setBorders(false);
		gridMessages.setStripeRows(true);
		gridMessages.setColumnLines(true);
		gridMessages.addPlugin(filters);
		
		gridMessages.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<MessageModel>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<MessageModel> se) {
				System.out.println("selection messageTextArea change");
				
				MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;  
				
		        if(selected==null){
					resetBody();
					resetTitle();
					resetToolBarAttach();
					toolBarAttachs.setEnabled(false);
					eventBus.fireEvent(new GridMessageUnSelectedEvent(null));
		        }
			}
		});
		
		
		gridMessages.addListener(Events.RowDoubleClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null;
				
		        if(selected!=null){
		        	eventBus.fireEvent(new OpenMessageEvent(selected.getId(), OpenType.REPLYALL, selected.getMessageType()));
		        	  if(selected.getIsRead().equals("false"))
		        		  eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.BOTH));
		          }
			}
            
        });
		
		
		gridMessages.addListener(Events.RowClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				System.out.println("click messageTextArea");
				
				MessageModel selected = getMessagesSelected()!=null?getMessagesSelected().get(0):null; 
				
		        if(selected!=null){
		        	eventBus.fireEvent(new PreviewMessageEvent(selected.getId(), selected.getMessageType()));
		        	  if(selected.getIsRead().equals("false"))
		        		  eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.BOTH));
		        	  
		        	  eventBus.fireEvent(new GridMessageSelectedEvent(null));
		        }
				else{
					resetBody();
					resetTitle();
					toolBarAttachs.setEnabled(false);
				}
			}
            
        });
		
		cpMessages.add(gridMessages);
		cpMessages.setScrollMode(Scroll.AUTOY);
		
		north.setLayout(new FitLayout());
		north.setHeaderVisible(false);
		north.add(cpMessages);
		

		center.setLayout(new FitLayout());
		center.setHeaderVisible(true);
		center.setHeading(headerTitle);
		resetToolBarAttach();
		toolBarAttachs.setEnabled(false);
		toolBarAttachs.setHeight(attachButtonHeight+5);
		center.setTopComponent(toolBarAttachs);

		
		cpBody.add(this.textBodyValue);
		cpBody.setHeaderVisible(false);
		cpBody.setStyleAttribute("padding", "5px");
		cpBody.setBodyBorder(false);
		cpBody.setScrollMode(Scroll.AUTOY);
		center.add(cpBody);

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200, 100, 300);
		northData.setCollapsible(true);
		northData.setFloatable(true);
		northData.setHideCollapseTool(true);
		northData.setSplit(true);
		northData.setMargins(new Margins(0));

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0));  
		
		 add(north, northData);  
		 add(center, centerData);  

	}
	
	
	private void addListnerOnContextMenu(){
		
		// Add lister to context menu
		gridMessages.addListener(Events.ContextMenu, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				
				MessageModel mess = getMessagesSelected()!=null?getMessagesSelected().get(0):null;
				
				if(mess!=null)
					contextMenuMessages.contextMenuSwitch(mess);
				else
					be.setCancelled(true);
			}
		});
	}

	private void resetStore(){
		store.removeAll();
	}
	
	public boolean updateStore(List<MessageModel> result){
		
		rowBold.clear();
		
		resetStore();
		if(result!= null){
			store.add(result);
			return true;
			//	this.parent = listItems.get(0).getParent();
		}
		return false;
	}
	
	/**
	 * 
	 * @param identifier (MANDATORY)
	 * @return
	 */
	public boolean deleteMessage(String identifier) {
		
//		FileGridModel fileTarget = (FileGridModel) store.findModel("identifier", identifier);	
		MessageModel messageTarget =  getMessagedModelByIdentifier(identifier);
		
		if(messageTarget!=null){
			Record record = store.getRecord(messageTarget); 
			store.remove((MessageModel) record.getModel());
			return true;
		}
		else
			System.out.println("Delete Error: messageTextArea target with " + identifier + " identifier not exist in store" );
		
		return false;
	}

	public MessageModel getMessagedModelByIdentifier(String id){
		return (MessageModel) store.findModel(ConstantsPortletMessages.ID, id);	
	}
	
	public ListStore<MessageModel> getStore(){
		return store;
	}

	public void markMessageAsRead(String id, boolean isRead) {
		
		MessageModel messageTarget =  getMessagedModelByIdentifier(id);
		
		if(messageTarget!=null){

			messageTarget.set(ConstantsPortletMessages.ISREAD, ""+isRead);
			
			store.update(messageTarget);
		}
		else
			System.out.println("Mark Message As Read" + id + " identifier not exist in store" );
		
		
	}
	
	public void setBodyValue(String subject, String body){
		
//		String text = "\nSubject: "+subject+"\n";
//		text+="\n\n";
		String text ="\n"+body;
		
		this.textBodyValue.setText(text);
		
	}
	
	public void resetBody(){
		this.textBodyValue.setText(emptyBody);
	}
	
	public void resetTitle(){
		this.headerTitle = emptyTilte;
		center.setHeading(headerTitle);
	}
	
	public void setFromTitle(String from, String date, List<String> listTo){
		this.headerTitle = "From: "+from+"  - Date: "+date + " - To: "+getToContact(listTo);
		center.setHeading(headerTitle);
	}
	
	public void setToTitle(List<String> listTo, String date){
	
		this.headerTitle = "To: " +getToContact(listTo);
		this.headerTitle +="  - Date: "+date;
		center.setHeading(headerTitle);
	}
	
	private String getToContact(List<String> listTo){
		
		String toContact="";
		
		for(String to: listTo){
			
			if(to.length()>MAX_LENGTH){
				
				to = to.substring(0, MAX_LENGTH-1);
				to += "...";
			}
			
			toContact+= to + ";";
		}
		return toContact;
	}

	private void resetToolBarAttach(){
		toolBarAttachs.removeAll();
		WidgetComponent imageAttachs = new WidgetComponent(new Image (Resources.getImageAttachs()));
		toolBarAttachs.add(imageAttachs);
	}
	public void setAttachs(List<FileModel> attachs) {
		
		resetToolBarAttach();
		
		if(attachs==null || attachs.size()==0){
			toolBarAttachs.setEnabled(false);
			return;
		
		}
	
		toolBarAttachs.setEnabled(true);
		
		for (final FileModel item : attachs) {

			String itemName = item.getName();
			
			if(item.getName().length()>MAX_LENGTH){
				
				itemName = item.getName().substring(0, MAX_LENGTH-1);
				itemName += "...";
			}
			
			
			/*	
			Button butt = new Button(itemName);
			if(!item.isDirectory())
				butt.setToolTip("SHOW - " + item.getName());
			else
				butt.setEnabled(false);
			
			butt.setWidth(attachButtonWidth);
			butt.setHeight(attachButtonHeight);
			butt.setId(item.getIdentifier());
			butt.setStyleAttribute("margin-left", "20px");
			
			System.out.println("in addAttachs item get type : " + item.getType());
			
			if(item.isDirectory())
				butt.setIcon(Resources.getIconFolder());
			else if(item.getType()!=null)
				butt.setIcon(Resources.getIconByType(item.getType()));
				else
					butt.setIcon(Resources.getIconByFolderItemType(item.getGXTFolderItemType()));

			
			butt.setIconAlign(IconAlign.LEFT);  
	
			butt.addListener(Events.OnClick, new Listener<ButtonEvent>() {

				@Override
				public void handleEvent(ButtonEvent be) {

					MessageModel mess = getMessagesSelected()!=null?getMessagesSelected().get(0):null;
					
					if(mess!=null)
						new AttachOpenHandler(item, mess, be.getClientX(), be.getClientY());
//					if(!item.isDirectory())
//						eventBus.fireEvent(new FileDownloadEvent(item.getIdentifier(), item.getName(), DownloadType.SHOW));
				}

			});*/
			
			String tooltip = "";
			boolean enabled = true;
			if(!item.isDirectory())
				tooltip = "SHOW - " + item.getName();
			else
				enabled = false;
			
			ConstantsPortletMessages.messagesLogger.log(Level.INFO, "in addAttachs item get type : " + item.getType());
			
			AbstractImagePrototype img;
			
			if(item.isDirectory())
				img = Resources.getIconFolder();
			else if(item.getType()!=null)
				img = Resources.getIconByType(item.getType());
				else
					img = Resources.getIconByFolderItemType(item.getGXTFolderItemType());
			
			final AttachButton butt = new AttachButton(itemName, tooltip, img);
			butt.setEnabled(enabled);
			butt.setCommand(new Command() {
				@Override
				public void execute() {
					MessageModel mess = getMessagesSelected()!=null?getMessagesSelected().get(0):null;
					
					if(mess!=null)
						new AttachOpenHandler(item, mess, butt.getClickClientX(), butt.getClickClientY());
					
//					if(!item.isDirectory())
//						eventBus.fireEvent(new FileDownloadEvent(item.getIdentifier(), item.getName(), DownloadType.SHOW));
				}
			});

			
			toolBarAttachs.add(butt);
		}	
		
		toolBarAttachs.add(new FillToolItem());  
		toolBarAttachs.add(buttSaveAttachs);
	}
	
	
	private void createButtonSaveAttach(){
		
		buttSaveAttachs.setIcon(Resources.getIconSaveAttachments());  
		    
	    buttSaveAttachs.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
	        	MessageModel selected = gridMessages.getSelectionModel().getSelectedItem();  

		          if(selected!=null)
		        	  eventBus.fireEvent(new SaveAttachmentsEvent(selected.getId(), selected.getMessageType()));
			}
		
	    }) ;
	}

	public Grid<MessageModel> getGridMessages() {
		return gridMessages;
	}

	public void setMessagesType(String messageType) {
		this.currentMessageType = messageType;
		
	}
	
	public String getCurrentMessageType(){
		return this.currentMessageType;
	}
	
	public void setBorderAsOnSearch(boolean bool){
		
		if(this.north.getElement("body")!=null){
			if(bool)
				this.north.getElement("body").getStyle().setBorderColor("#32CD32");
			else
				this.north.getElement("body").getStyle().setBorderColor("#99BBE8");
		}

	}
	
}