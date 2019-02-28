package org.gcube.portlets.user.workspace.client.view.toolbars;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.AccountingHistoryEvent;
import org.gcube.portlets.user.workspace.client.event.AccountingReadersEvent;
import org.gcube.portlets.user.workspace.client.event.AddAdministratorEvent;
import org.gcube.portlets.user.workspace.client.event.GetInfoEvent;
import org.gcube.portlets.user.workspace.client.event.TrashEvent;
import org.gcube.portlets.user.workspace.client.gridevent.FileVersioningEvent;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.util.GetPermissionIconByACL;
import org.gcube.portlets.user.workspace.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL.USER_TYPE;
import org.gcube.portlets.user.workspace.shared.WorkspaceTrashOperation;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class GxtBottomToolBarItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class GxtBottomToolBarItem extends ToolBar{

	/**
	 *
	 */
	private static final String VERSIONS = "Versions";
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
	private static final String EDIT_ADMINISTRATOR = "Edit Administrator/s";

	private TextField<String> txfName = new TextField<String>();
	private Text txtOwner = new Text("Empty");
	private Text txtCreationTime = new Text("Empty");
	private Text txtDimension = new Text("Empty");

	private Button btnGetInfo;
	private Button bHistory;
	private Button bRead;
	private Button btnGetTrash;
	private Button btnVersioning;
	private ACLDivInfo aclDivInfo;
	private Label labelItemsNumber = new Label();
	private HorizontalPanel hpItemsNumber;
	private Button btnAddAdmin;

	/**
	 * Instantiates a new gxt bottom tool bar item.
	 */
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

	/**
	 * Inits the toolbar.
	 */
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

		btnVersioning = new Button(VERSIONS);
		btnVersioning.setIcon(Resources.getIconVersioning());
		btnVersioning.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				AppController.getEventBus().fireEvent(new FileVersioningEvent(WorkspaceVersioningOperation.SHOW, null, null, null, null));
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
		add(btnGetTrash);
		add(btnGetInfo);
		add(bHistory);
		add(btnVersioning);

		hpItemsNumber = new HorizontalPanel();
		hpItemsNumber.setId("HP-ItemsNumber");
		hpItemsNumber.setStyleAttribute("margin-right", "50px");

		hpItemsNumber.setHorizontalAlign(HorizontalAlignment.RIGHT);
		hpItemsNumber.add(labelItemsNumber);
		add(hpItemsNumber);
		FillToolItem filler = new FillToolItem();
		filler.setId("filler-item");
		add(filler);
		add(btnAddAdmin);
		add(aclDivInfo);
		enableInfoHistoryButtons(null, false);
	}

	/**
	 * Sets the visible add administrators.
	 *
	 * @param bool the new visible add administrators
	 */
	private void setVisibleAddAdministrators(boolean bool){
		btnAddAdmin.setVisible(bool);
	}


	/**
	 * Reset details.
	 */
	public void resetDetails(){

		this.txtDimension.setText("");
		this.txtCreationTime.setText("");
		this.txfName.reset();
		this.txtOwner.setText("");
	}


	/**
	 * Enable info history buttons.
	 *
	 * @param selectedFile the selected file
	 * @param enable the enable
	 */
	public void enableInfoHistoryButtons(FileModel target, boolean enable){
		bHistory.setEnabled(enable);
		bRead.setEnabled(enable);
		btnGetInfo.setEnabled(enable);

		btnVersioning.setEnabled(false);
		if(target!=null){
			GXTFolderItemTypeEnum category = target.getGXTFolderItemType();
			if(category!=null && (category.equals(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE) || category.equals(GXTFolderItemTypeEnum.EXTERNAL_FILE) || category.equals(GXTFolderItemTypeEnum.EXTERNAL_IMAGE)))
				btnVersioning.setEnabled(enable);
		}
	}

	/**
	 * Sets the details.
	 *
	 * @param itemName the item name
	 * @param description the description
	 * @param dimension the dimension
	 * @param creationTime the creation time
	 * @param owner the owner
	 */
	public void setDetails(String itemName, String description, String dimension, String creationTime, String owner){

		this.resetDetails();

		this.txtDimension.setText(dimension);
		this.txtCreationTime.setText(creationTime);
		this.txfName.setValue(itemName);
		this.txtOwner.setText(owner);

	}

	/**
	 * Update acl info.
	 *
	 * @param acl the acl
	 */
	public void updateACLInfo(WorkspaceACL acl){
		setVisibleAddAdministrators(false);

		if(acl==null){
			aclDivInfo.updateInfo(null, null);
			return;
		}

		AbstractImagePrototype img = GetPermissionIconByACL.getImage(acl);
		aclDivInfo.updateInfo(acl.getLabel(), img);
		this.layout();
	}


	/**
	 * Update add administator info.
	 *
	 * @param loginUserLogger the login user logger
	 * @param acl the acl
	 */
	public void updateAddAdministatorInfo(String loginUserLogger, ExtendedWorkspaceACL acl){
		setVisibleAddAdministrators(false);

		if(loginUserLogger==null || loginUserLogger.isEmpty())
			MessageBox.alert("Error", "I  could not get you username yet.. please try again", null);

		ConstantsExplorer.log("acl isBaseSharedFolder: "+acl.isBaseSharedFolder() +", user type: "+acl.getUserType() + ", owner: "+acl.getLoginOwner() + ", acl item id: "+acl.getWorkspaceItemId());

		if(acl.isBaseSharedFolder()){
			if(acl.getUserType().equals(USER_TYPE.ADMINISTRATOR)){
				setVisibleAddAdministrators(true);
			}
		}
		this.layout();
	}

	/**
	 * Update trash icon.
	 *
	 * @param trashIsFull the trash is full
	 */
	public void updateTrashIcon(boolean trashIsFull){

		if(trashIsFull)
			btnGetTrash.setIcon(Resources.getTrashFull());
		else
			btnGetTrash.setIcon(Resources.getTrashEmpty());
	}

	/**
	 * Update items number.
	 *
	 * @param size the size
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

	/**
	 * Sets the items number to center.
	 */
	public void setItemsNumberToCenter(){

		if(this.isRendered()){
			String width = this.getElement().getStyle().getWidth();
			width = width.replace("px", "");
			try{
				long intWidth = Long.parseLong(width);
				intWidth = intWidth/2-30; //calculate the center
				intWidth = intWidth-60*3; //previous buttons
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
