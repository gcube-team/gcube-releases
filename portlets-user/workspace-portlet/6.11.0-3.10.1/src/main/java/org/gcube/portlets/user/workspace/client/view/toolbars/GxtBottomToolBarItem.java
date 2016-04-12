package org.gcube.portlets.user.workspace.client.view.toolbars;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.TrashEvent;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.util.GetPermissionIconByACL;
import org.gcube.portlets.user.workspace.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL.USER_TYPE;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtBottomToolBarItem extends ToolBar{
	
	/**
	 * 
	 */
	protected static final String INFO = "Info";
	/**
	 * 
	 */
	protected static final String READ = "Read";
	/**
	 * 
	 */
	protected static final String HISTORY = "History";
	private static final String EDIT_ADMINISTRATOR = "Edit Admnistrator/s";
	
	private TextField<String> txfName = new TextField<String>();
	private Text txtOwner = new Text("Empty");
	private Text txtCreationTime = new Text("Empty");
	private Text txtDimension = new Text("Empty");
	
	private Button btnGetInfo;
	private Button bHistory;
	private Button bRead;
	private Button btnGetTrash;
	private ACLDivInfo aclDivInfo;
	private Label labelItemsNumber = new Label();
	private HorizontalPanel hpItemsNumber;
	private Button btnAddAdmin;
	
	public GxtBottomToolBarItem(){
		super();
		initToolbar();
	}
	
	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Component#onLoad()
	 */
	@Override
	protected void onLoad() {
		super.onLoad();
		setItemsNumberToCenter();
	}
	
	private void initToolbar(){
		
		btnGetTrash = new Button("Trash");
		btnGetTrash.setIcon(Resources.getTrashEmpty());
		btnGetTrash.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new TrashEvent(WorkspaceTrashOperation.SHOW, null));

			}
		});
		
		bHistory = new Button(HISTORY);
		bHistory.setIcon(Resources.getIconHistory());
		
		bHistory.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				AppController.getEventBus().fireEvent(new AccountingHistoryEvent(null));
				
			}
		});

		bRead = new Button(READ);
		bRead.setIcon(Resources.getIconNotRead());
		
		bRead.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				AppController.getEventBus().fireEvent(new AccountingReadersEvent(null));
				
			}
		});
		
		btnGetInfo = new Button(INFO);
		btnGetInfo.setIcon(Resources.getIconInfo());
		btnGetInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new GetInfoEvent(null));

			}
		});
		
		
		btnAddAdmin = new Button(EDIT_ADMINISTRATOR);
		//TODO CHANGE ICON
		btnAddAdmin.setIcon(Resources.getIconManageAdministrator());
//		btnAddAdmin.setIcon(Resources.getIconAddAdministrator());
		btnAddAdmin.setScale(ButtonScale.MEDIUM);
		btnAddAdmin.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new AddAdministratorEvent(null));
			}
		});
		
		setVisibleAddAdministrators(false);
			
		aclDivInfo = new ACLDivInfo("", null);
		
		//COMMENTED AT 29/08/2013 
//		add(new SeparatorToolItem());
//		add(bRead);

		
		
		add(btnGetTrash);
//		add(new SeparatorMenuItem());
		add(btnGetInfo);
		add(bHistory);
		
		hpItemsNumber = new HorizontalPanel();
		hpItemsNumber.setId("HP-ItemsNumber");
		hpItemsNumber.setStyleAttribute("margin-right", "50px");

		hpItemsNumber.setHorizontalAlign(HorizontalAlignment.RIGHT);
		hpItemsNumber.add(labelItemsNumber);
		
//		hpItemsNumber.addListener(Events.Render, new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				setItemsNumberToCenter();
//			}
//		});

//		add(new FillToolItem());
		add(hpItemsNumber);
		FillToolItem filler = new FillToolItem();
		filler.setId("filler-item");
		add(filler);
		add(btnAddAdmin);
		add(aclDivInfo);

		enableInfoHistoryButtons(false);
	
	}
	
	private void setVisibleAddAdministrators(boolean bool){
		btnAddAdmin.setVisible(bool);
	}
	

	public void resetDetails(){
		
		this.txtDimension.setText("");
		this.txtCreationTime.setText("");
		this.txfName.reset();
		this.txtOwner.setText("");
	}
	
	public void enableInfoHistoryButtons(boolean enable){
		bHistory.setEnabled(enable);
		bRead.setEnabled(enable);
		btnGetInfo.setEnabled(enable);
	}
	
	public void setDetails(String itemName, String description, String dimension, String creationTime, String owner){
		
		this.resetDetails();
		
		this.txtDimension.setText(dimension);
		this.txtCreationTime.setText(creationTime);
		this.txfName.setValue(itemName);
		this.txtOwner.setText(owner);
		
	}

//	/**
//	 * @param markAsRead
//	 */
//	public void setRead(boolean markAsRead) {
//		if(markAsRead)
//			bRead.setIcon(Resources.getIconRead());
//		else
//			bRead.setIcon(Resources.getIconNotRead());
//		
//		bRead.setEnabled(markAsRead);
//	}
	
	
	public void updateACLInfo(WorkspaceACL acl){
		setVisibleAddAdministrators(false);
		
		if(acl==null){
			aclDivInfo.updateInfo(null, null);
			return;
		}
		AbstractImagePrototype img = GetPermissionIconByACL.getImage(acl);
		
//		if(acl.getUserType().equals(USER_TYPE.ADMINISTRATOR)){
//			setVisibleAddAdministrators(true);
//		}
//		
		aclDivInfo.updateInfo(acl.getLabel(), img);
		this.layout();
	}
	
	
	public void updateAddAdministatorInfo(String loginUserLogger, ExtendedWorkspaceACL acl){
		setVisibleAddAdministrators(false);
		
		String loginOwner = acl.getLoginOwner();
		
		if(loginOwner!=null && !loginOwner.isEmpty() && loginUserLogger!=null && !loginUserLogger.isEmpty() && acl.isBaseSharedFolder()){
			GWT.log("Comparing loginUserLogger: "+loginUserLogger +" and loginUserLogger: "+loginUserLogger +" to update AdministorInfo");
			if((loginOwner.compareToIgnoreCase(loginUserLogger)==0) && acl.getUserType().equals(USER_TYPE.ADMINISTRATOR)){
				setVisibleAddAdministrators(true);
			}
		}
		this.layout();
	}
	
	public void updateTrashIcon(boolean trashIsFull){
		
		if(trashIsFull)
			btnGetTrash.setIcon(Resources.getTrashFull());
		else
			btnGetTrash.setIcon(Resources.getTrashEmpty());
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
	
	public void setItemsNumberToCenter(){
		
		if(this.isRendered()){
			String width = this.getElement().getStyle().getWidth();
			width = width.replace("px", "");
			try{
				long intWidth = Long.parseLong(width);
				intWidth = (intWidth/2)-30; //calculate the center
				intWidth = intWidth-(60*3); //previous buttons
				GWT.log("refreshSize width is "+width);
//				hpItemsNumber.setWidth(intWidth+"px");
				hpItemsNumber.setStyleAttribute("margin-left", intWidth+"px");
				hpItemsNumber.layout();
			}catch (Exception e) {
				GWT.log("width is not a long "+e);
			}
		}
	}
}
