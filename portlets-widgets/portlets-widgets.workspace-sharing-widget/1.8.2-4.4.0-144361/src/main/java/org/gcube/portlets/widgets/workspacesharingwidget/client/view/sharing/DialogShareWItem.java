package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.WorkspaceSharingController;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.MultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.MultiValuePanel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 27, 2014
 *
 */
public class DialogShareWItem extends Dialog {


	private int heightTextArea = 100;
	private TextField<String> txtName;
	private TextArea textAreaDescription = new TextArea();
	private FileModel fileToShare = null;
	private UserStore userStore = new UserStore();
	private MultiValuePanel suggestPanel = null;
	private Button buttonMultiDrag = new Button("Choose Contacts");
	private TextField<String> txtOwner;
	private PanelTogglePermission permission;
    private HorizontalPanel hpPermission = null;
    private boolean showError = false;
	private boolean readGroupsFromHL;
	private boolean readGroupsFromPortal;

   /**
    *  By DEFAULT DOESN'T READ GROUPS FROM SERVER
    */
	public DialogShareWItem() {
		this(false, false);
	}

    /**
     *
     * @param readGroupsFromHL
     * @param readGroupsFromPortal
     */
	public DialogShareWItem(boolean readGroupsFromHL, boolean readGroupsFromPortal) {
		this.readGroupsFromHL = readGroupsFromHL;
		this.readGroupsFromPortal = readGroupsFromPortal;
		suggestPanel = new MultiValuePanel(userStore, readGroupsFromHL, readGroupsFromPortal);
		initLayout();
	}


	private List<InfoContactModel> listAlreadyShared = new ArrayList<InfoContactModel>(){

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Compare Login
		 */
		@Override
		public boolean contains(Object o) {

			if(o==null)
				return false;

			InfoContactModel contact = (InfoContactModel) o;

			for (int i = 0; i < listAlreadyShared.size(); i++){
                if (contact.getName().compareTo(listAlreadyShared.get(i).getName())==0)
                    return true;
        	}

			return false;

		};


	};



	/**
	 * Use to modify a shared folder or share an existing folder
	 * @param fileModel
	 * @param type
	 */

	public void updateSharingDialog(FileModel fileModel, final boolean shareOnlyOwner, final ACL_TYPE defaultACL){
		this.fileToShare = fileModel;

	    String heading = "Share workspace";
	    if(fileModel.isDirectory())
	    	heading+=" folder: ";
	    else
	    	heading+= " item: ";

	    setHeading(heading+fileModel.getName());

	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
	    txtName.setValue(fileModel.getName());
	    txtName.setReadOnly(true);
	    txtName.setAutoValidate(true);
	    txtName.setFieldLabel("Folder Name");

	    txtOwner = new TextField<String>();
	    txtOwner.setAllowBlank(true);
	    txtOwner.setValue("");
	    txtOwner.setReadOnly(true);
	    txtOwner.setAutoValidate(false);
	    txtOwner.setFieldLabel("Owner");

	    textAreaDescription.setFieldLabel("Description");
	    textAreaDescription.setHeight(heightTextArea);
	    textAreaDescription.setWidth(380);

	    textAreaDescription.setValue(fileModel.getDescription());

	    final LayoutContainer lc = new LayoutContainer();
	    lc.setStyleAttribute("margin-top", "10px");
	    lc.setStyleAttribute("margin-bottom", "10px");
	    lc.setSize(480, 50);

	    final FlexTable flexTable = new FlexTable();
	    flexTable.setStyleName("userssuggest");
	    Label labelShareWith = new Label("Share with users");
	    flexTable.setWidget(0, 0, labelShareWith);
	    flexTable.setWidget(0, 1, suggestPanel);

	    buttonMultiDrag.setStyleName("wizardButton");


	    flexTable.setWidget(1, 0, new Label("Add more"));
	    flexTable.setWidget(1, 1, buttonMultiDrag);

	    Label labelDescription = new Label("Description");
	    flexTable.setWidget(2, 0, labelDescription);
	    flexTable.setWidget(2, 1, textAreaDescription);


    	hpPermission = new HorizontalPanel();
    	hpPermission.setVerticalAlign(VerticalAlignment.MIDDLE);
    	Label labelProperty = new Label("Permissions");
    	hpPermission.add(labelProperty);

		WorkspaceSharingController.rpcWorkspaceSharingService.getACLs(new AsyncCallback<List<WorkspaceACL>>() {

			@Override
			public void onSuccess(List<WorkspaceACL> result) {
				permission = new PanelTogglePermission(result,defaultACL);
				hpPermission.add(permission);
				hpPermission.layout();
				layout();
			}

			@Override
			public void onFailure(Throwable caught) {
				hpPermission.add(new Label("Error on recovering ACLs"));
				hpPermission.layout();
				layout();
			}
		});


	    lc.add(flexTable);
	    if(hpPermission!=null)
	    	lc.add(hpPermission);
	    lc.mask();

	    userStore.getListSharedUserByFolderId(fileModel.getIdentifier(), new AsyncCallback<List<InfoContactModel>>() {

	 			@Override
	 			public void onSuccess(List<InfoContactModel> result) {

	 				for (InfoContactModel infoContactModel : result) {
	 					if(infoContactModel.getName()!=null){
		 					listAlreadyShared.add(infoContactModel);
		 				    suggestPanel.addRecipient(infoContactModel.getName(),false);
	 					}
	 				}

	 				lc.unmask();
	 			}

	 			@Override
	 			public void onFailure(Throwable caught) {
	 				 lc.unmask();

	 			}
	 		});


	    userStore.getOwner(fileModel.getIdentifier(), new AsyncCallback<InfoContactModel>() {

			@Override
			public void onFailure(Throwable caught) {
				 txtOwner.setValue("Error on retrieving Owner");
			}

			@Override
			public void onSuccess(InfoContactModel result) {
				 txtOwner.setValue(result.getName());

				 if(shareOnlyOwner){
					 permissionControl(result.getLogin(), true);
				 }
			}
		});

	    setFocusWidget(suggestPanel.getBox());
        add(txtName);
        add(txtOwner);
		add(lc);
		addListners();

		if(shareOnlyOwner)
			enableFormDialog(false); //FORM IS DISABLED BY DEFAULT
	}

	private void permissionControl(String owner, boolean showAlert){
		GWT.log("Permission control compare between owner: "+owner +" and my login: "+WorkspaceSharingController.getMyLogin());

		if(WorkspaceSharingController.getMyLogin().compareToIgnoreCase(owner)!=0){
			enableFormDialog(false);
			if(showAlert){
				showError = true;
			}
		}else{
			enableFormDialog(true);
		}
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Window#afterShow()
	 */
	@Override
	protected void afterShow() {
		super.afterShow();

		if(showError){
			MessageBox.alert("Permission denied", "You have no permissions to change sharing. You are not owner of \""+txtName.getValue()+"\"", null);
		}
	}

	private void enableFormDialog(boolean bool){
		 getButtonById(Dialog.OK).setEnabled(bool);
		 buttonMultiDrag.setEnabled(bool);
		 textAreaDescription.setEnabled(bool);
		 if(permission!=null)
			 permission.setEnabled(bool);
	}

	public void setAsError(String message){
		enableFormDialog(false);
		this.add(new Html("<br/> <p style=\"color:red; font-family:verdana, arial; font-size:14px;\">"+message+"</p>"));
		this.layout();
	}

	/**
	 *
	 * @return
	 */
	public FileModel getFileToShare() {
		return fileToShare;
	}

	public void initLayout(){
		FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(380);
	    setLayout(layout);
	    setModal(true);
	    setScrollMode(Scroll.AUTOY);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(ConstantsSharing.WIDTH_DIALOG);
	    setHeight(ConstantsSharing.HEIGHT_DIALOG);
	    setResizable(true);
	    setButtonAlign(HorizontalAlignment.CENTER);
	    setButtons(Dialog.OKCANCEL);
	}


	public List<InfoContactModel> getSharedListUsers() {
//		printSelectedUser();
		return suggestPanel.getSelectedUser();
	}


	public void addListners(){

        this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});


        /*this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(isValidForm(true))
					hide();
			}
		});
        */

        buttonMultiDrag.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final DialogMultiDragContact dialog = new DialogMultiDragContact(true,true);
				final MultiDragContact multiDrag = dialog.getMultiDrag();
				dialog.show();

				List<InfoContactModel> exclusiveContacts = userStore.getExclusiveContactsFromAllContact(suggestPanel.getSelectedUser());
				multiDrag.addSourceContacts(exclusiveContacts);

				for (InfoContactModel infoContactModel : suggestPanel.getSelectedUser()) {
					if(!listAlreadyShared.contains(infoContactModel))
						multiDrag.addTargetContact(infoContactModel);
				}

				multiDrag.addAlreadySharedContacts(suggestPanel.getSelectedUser());
				dialog.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {

						initSuggestContacts();

						for (InfoContactModel infoContactModel : multiDrag.getTargetListContact()) {
		 				    suggestPanel.addRecipient(infoContactModel.getName(),true);
		 				}

//						for (InfoContactModel infoContactModel : multiDrag.getTargetListContact()) {
//
//							if(!listAlreadySharedContains(infoContactModel))
//								suggestPanel.addRecipient(infoContactModel.getName(),true);
//		 				}

						suggestPanel.boxSetFocus();

//						printSelectedUser();
					}
				});
			}
		});
	}

	private boolean listAlreadySharedContains(InfoContactModel contact){

		if(contact==null)
			return false;

		for (InfoContactModel ct : listAlreadyShared) {

			if(ct.getLogin().compareTo(contact.getLogin())==0){

				return true;
			}

		}

		return false;

	}

	//DEBUG
	private void printSelectedUser(){

		System.out.println("SELETECTED USERS: ");
		for (InfoContactModel contact : suggestPanel.getSelectedUser())
			System.out.println(contact);
	}

	private void initSuggestContacts(){

		suggestPanel.resetItemSelected();

		for (InfoContactModel contact : listAlreadyShared) {
			suggestPanel.addRecipient(contact.getName(), false);
		}


	}

	public String getName() {
		return txtName.getValue();
	}

	public String getDescription() {
		if(textAreaDescription.getValue()==null)
			return "";

		return textAreaDescription.getValue();
	}

	/**
	 *
	 * @param displayAlert
	 * @return
	 */
	public boolean isValidForm(boolean displayAlert){

		if(!txtName.isValid()){
			if(displayAlert)
				MessageBox.alert("Attention", "Name must not be empty", null);
			txtName.focus();
			return false;
		}

		if(getSharedListUsers().size()==0){
			if(displayAlert)
				MessageBox.alert("Attention", "You must pick at least one user with which share the folder", null);
			suggestPanel.boxSetFocus();
			return false;
		}

		return true;

	}

	public WorkspaceACL getSelectedACL(){
		if(permission!=null)
			return permission.getSelectedACL();
		return null;
	}

	public boolean isReadGroupsFromHL() {
		return readGroupsFromHL;
	}

	public boolean isReadGroupsFromPortal() {
		return readGroupsFromPortal;
	}
}
