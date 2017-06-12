package org.gcube.portlets.user.workspace.client.view.sharing;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.sharing.multisuggest.MultiDragContact;
import org.gcube.portlets.user.workspace.client.view.sharing.multisuggest.MultiValuePanel;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;
import org.gcube.portlets.user.workspace.shared.ListContact;
import org.gcube.portlets.user.workspace.shared.WorkspaceACL;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;



/**
 * The Class DialogShareFolder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 6, 2017
 */
public class DialogShareFolder extends Dialog {

	private int widthDialog = 535;
	private int heightTextArea = 100;
	private TextField<String> txtName;
	private TextArea textAreaDescription = new TextArea();
	private FileModel parentFolder = null;
	private UserStore userStore;
	private MultiValuePanel suggestPanel;
	private Button buttonMultiDrag = new Button("Choose Contacts");
	private TextField<String> txtOwner;
	private PanelTogglePermission permission;
    private HorizontalPanel hpPermission = null;
    private InfoContactModel shareOwner = null;
	private ListContact<InfoContactModel> listAlreadyShared = new ListContact<InfoContactModel>();

	/**
	 * Use to modify a shared folder or share an existing folder.
	 *
	 * @param folderParentName the folder parent name
	 * @param folder the folder
	 * @param eventBus the event bus
	 * @param userStore the user store
	 */
	public DialogShareFolder(String folderParentName, final FileModel folder, HandlerManager eventBus, UserStore userStore) {
		initUserStore(userStore);
		initLayout(folderParentName);
	    this.parentFolder = folder;
	    this.setIcon(Resources.getIconShareFolder());

	    setWidth(widthDialog);
	    setButtons(Dialog.OKCANCEL);
	    setHeading("Share folder: "+folder.getName());

	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
	    txtName.setValue(folder.getName());
	    txtName.setReadOnly(true);
	    txtName.setAutoValidate(true);
	    txtName.setFieldLabel("Folder Name");

	    txtOwner = new TextField<String>();
	    txtOwner.setAllowBlank(true);
	    txtOwner.setValue("");
	    txtOwner.setReadOnly(true);
	    txtOwner.setAutoValidate(false);
	    txtOwner.setFieldLabel("Owner");

	    textAreaDescription.setFieldLabel(ConstantsExplorer.DIALOG_DESCRIPTION);
	    textAreaDescription.setHeight(heightTextArea);
	    textAreaDescription.setWidth(380);
	    textAreaDescription.setValue(folder.getDescription());

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

		AppControllerExplorer.rpcWorkspaceService.getACLs(new AsyncCallback<List<WorkspaceACL>>() {

			@Override
			public void onSuccess(List<WorkspaceACL> result) {
				permission = new PanelTogglePermission(result);

				//ONLY IF FOLDER IS ALREADY SHARED, LOOADING CURRENT ACL FROM HL
				if(folder.isShared())
					selectAclForFolder(folder);

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

	    userStore.getOwner(folder.getIdentifier(), new AsyncCallback<InfoContactModel>() {

			@Override
			public void onFailure(Throwable caught) {
				 txtOwner.setValue("Error on retrieving Owner");
			}

			@Override
			public void onSuccess(InfoContactModel result) {
				 shareOwner = result;
				 txtOwner.setValue(result.getName());
				 //IF THE FOLDER IS NOT SHARED, CHECKS PERMISSIONS
				 if(!folder.isShared())
					 permissionControl(result.getLogin(), true);

				 fillRecipientAlreadyShared(folder.getIdentifier(), lc);
			}

		});

		AsyncCallback<List<InfoContactModel>> callback = new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Alert", "Sorry, an error occurred during on getting Managers from server",null);
				enableFormDialog(false);
				hpPermission.unmask();
			}

			@Override
			public void onSuccess(List<InfoContactModel> listManagers) {
				permissionControl(listManagers, true, folder);
				hpPermission.unmask();
			}
		};

		if(folder.isShared())
			getUsersManagers(folder.getIdentifier(), callback);

	    setFocusWidget(suggestPanel.getBox());
        add(txtName);
        add(txtOwner);
		add(lc);
		addListners();
		enableFormDialog(false); //FORM IS DISABLED BY DEFAULT

        this.show();
	}

	/**
	 * Inits the user store.
	 *
	 * @param userStore the user store
	 */
	private void initUserStore(UserStore userStore){
		this.userStore = userStore;
		this.suggestPanel = new MultiValuePanel(userStore);
	}

	/**
	 * Update recipient of share.
	 *
	 * @param listContacts the list contacts
	 */
	private void updateRecipientOfShare(List<InfoContactModel> listContacts){
		suggestPanel.resetItemSelected();
		for (InfoContactModel contact : listContacts){
			if(contact!=null && contact.getName()!=null){
				if(!isShareOwner(contact)) //skip owner
					suggestPanel.addRecipient(contact.getName(), true);
				else
					suggestPanel.addRecipient(contact.getName(), false); //owner is not deletable
				}
		}
	}


	/**
	 * Fill recipient already shared.
	 *
	 * @param folderId the folder id
	 * @param lc the lc
	 */
	private void fillRecipientAlreadyShared(String folderId, final LayoutContainer lc) {

		userStore.getListSharedUserByFolderId(folderId,
			new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onSuccess(List<InfoContactModel> listContacts) {

					if (listContacts != null) {
						listAlreadyShared.addAll(listContacts);
						updateRecipientOfShare(listContacts);
					}
					lc.unmask();
				}

				@Override
				public void onFailure(Throwable caught) {
					lc.unmask();
				}
		});

	}

	/**
	 * Permission control.
	 *
	 * @param owner the owner
	 * @param showAlert the show alert
	 */
	private void permissionControl(String owner, boolean showAlert){
		GWT.log("Permission control compare between owner: "+owner +" and my login: "+AppControllerExplorer.myLogin);

		if(AppControllerExplorer.myLogin.compareToIgnoreCase(owner)!=0){
			enableFormDialog(false);
			if(showAlert)
				new MessageBoxAlert("Permission denied", "You have no permissions to change sharing. You are not owner of \""+txtName.getValue()+"\"", null);
		}else{
			enableFormDialog(true);
		}
	}

	/**
	 * Enable form dialog.
	 *
	 * @param bool the bool
	 */
	private void enableFormDialog(boolean bool){
		 getButtonById(Dialog.OK).setEnabled(bool);
		 buttonMultiDrag.setEnabled(bool);
		 textAreaDescription.setEnabled(bool);
		 if(permission!=null)
			 permission.setEnabled(bool);

	}

	/**
	 * Gets the parent folder.
	 *
	 * @return the parent folder
	 */
	public FileModel getParentFolder() {
		return parentFolder;
	}

	/**
	 * Inits the layout.
	 *
	 * @param folderParentName the folder parent name
	 */
	public void initLayout(String folderParentName){
		FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(380);
	    setLayout(layout);
	    setModal(true);
	    setScrollMode(Scroll.AUTOY);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(widthDialog);
	    setHeight(ConstantsExplorer.HEIGHT_DIALOG_SHARE_FOLDER);
	    setResizable(true);
	    setButtonAlign(HorizontalAlignment.CENTER);
	    setButtons(Dialog.OKCANCEL);
	}

	/**
	 * Use to create a new shared folder.
	 *
	 * @param folderParentName the folder parent name
	 * @param eventBus the event bus
	 * @param userStore the user store
	 */
	public DialogShareFolder(String folderParentName, HandlerManager eventBus, UserStore userStore) {
		initUserStore(userStore);
		initLayout(folderParentName);
	    this.setIcon(Resources.getIconSharedFolder());
	    setHeading("Create a new shared folder in: "+folderParentName);
	    setHeight(ConstantsExplorer.HEIGHT_DIALOG_SHARE_FOLDER-25);
	    setWidth(widthDialog);
	    setButtons(Dialog.OKCANCEL);

	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
	    txtName.setAutoValidate(true);
	    txtName.setFieldLabel("Folder Name");
	    txtName.getMessages().setRegexText(ConstantsExplorer.REGEX_FOLDER_NAME+": .<>\\|?/*%$ or contains / or \\");
	    txtName.setRegex("^[^.<>\\|?/*%$]+[^\\/]*$");

	    textAreaDescription.setFieldLabel(ConstantsExplorer.DIALOG_DESCRIPTION);
	    textAreaDescription.setHeight(heightTextArea);
	    textAreaDescription.setWidth(380);

	    final LayoutContainer lc = new LayoutContainer();
	    lc.setStyleAttribute("margin-top", "10px");
	    lc.setStyleAttribute("margin-bottom", "10px");
	    lc.setSize(480, 50);

	    final FlexTable flexTable = new FlexTable();
	    flexTable.setStyleName("userssuggest");
	    Label lableShareWith = new Label("Share with users");
	    flexTable.setWidget(0, 0, lableShareWith);
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

		AppControllerExplorer.rpcWorkspaceService.getACLs(new AsyncCallback<List<WorkspaceACL>>() {

			@Override
			public void onSuccess(List<WorkspaceACL> result) {
				permission = new PanelTogglePermission(result);
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
	    lc.add(hpPermission);

        setFocusWidget(txtName);
        add(txtName);
		add(lc);

		addListners();

        this.show();
	}


	/**
	 * Gets the shared list users.
	 *
	 * @return the shared list users
	 */
	public List<InfoContactModel> getSharedListUsers() {
//		printSelectedUser();
		return suggestPanel.getSelectedUser();
	}


	/**
	 * Adds the listners.
	 */
	public void addListners(){

        this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});


        this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(isValidForm(false))
					hide();
			}
		});

        buttonMultiDrag.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final MultiDragContact multiDrag = new MultiDragContact();
				multiDrag.show();

				List<InfoContactModel> exclusiveContacts = userStore.getExclusiveContactsFromAllContact(suggestPanel.getSelectedUser());
				multiDrag.addSourceContacts(exclusiveContacts);

				for (InfoContactModel infoContactModel : suggestPanel.getSelectedUser()) {
//					if(!listAlreadyShared.contains(infoContactModel))
					if(!isShareOwner(infoContactModel))
						multiDrag.addTargetContact(infoContactModel);
				}

//				multiDrag.addAlreadySharedContacts(suggestPanel.getSelectedUser());

				multiDrag.addAlreadySharedContacts(listAlreadyShared);

				multiDrag.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
//						initSuggestContacts();
						suggestPanel.resetItemSelected();
						suggestPanel.addRecipient(shareOwner.getName(),false);
						for (InfoContactModel infoContactModel : multiDrag.getTargetListContact()) {
		 				    suggestPanel.addRecipient(infoContactModel.getName(),true);
		 				}
						suggestPanel.boxSetFocus();
//						printSelectedUser();
					}
				});
			}
		});
	}


	/**
	 * Checks if is share owner.
	 *
	 * @param infoContactModel the info contact model
	 * @return true, if is share owner
	 */
	public boolean isShareOwner(InfoContactModel infoContactModel){

		if(infoContactModel!=null && shareOwner!=null && InfoContactModel.COMPARATORLOGINS.compare(infoContactModel, shareOwner)==0)
			return true;

		return false;
	}


	/**
	 * List already shared contains.
	 *
	 * @param contact the contact
	 * @return true, if successful
	 */
	private boolean listAlreadySharedContains(InfoContactModel contact){

		if(contact==null)
			return false;

		for (InfoContactModel ct : listAlreadyShared) {
			if(InfoContactModel.COMPARATORLOGINS.compare(ct, contact)==0)
//			if(ct.getLogin().compareTo(contact.getLogin())==0)
				return true;
		}
		return false;
	}

	//DEBUG
	/**
	 * Prints the selected user.
	 */
	@SuppressWarnings("unused")
	private void printSelectedUser(){

		System.out.println("SELETECTED USERS: ");
		for (InfoContactModel contact : suggestPanel.getSelectedUser())
			System.out.println(contact);
	}

	/**
	 * Inits the suggest contacts.
	 */
	@SuppressWarnings("unused")
	private void initSuggestContacts(){
		suggestPanel.resetItemSelected();
		for (InfoContactModel contact : listAlreadyShared)
			suggestPanel.addRecipient(contact.getName(), false);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return txtName.getValue();
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		if(textAreaDescription.getValue()==null)
			return "";

		return textAreaDescription.getValue();
	}

	/**
	 * Checks if is valid form.
	 *
	 * @param displayAlert the display alert
	 * @return true, if is valid form
	 */
	public boolean isValidForm(boolean displayAlert){

		if(!txtName.isValid()){
			if(displayAlert)
				new MessageBoxAlert("Attention", "Name must not be empty", null);
			txtName.focus();
			return false;
		}

		if(getSharedListUsers().size()==0){
			if(displayAlert)
				new MessageBoxAlert("Attention", "You must pick at least one user with which share the folder", null);
			suggestPanel.boxSetFocus();
			return false;
		}

		return true;

	}

	/**
	 * Gets the selected acl.
	 *
	 * @return the selected acl
	 */
	public WorkspaceACL getSelectedACL(){
		if(permission!=null)
			return permission.getSelectedACL();
		return null;
	}

	/**
	 * Select acl for folder.
	 *
	 * @param folder the folder
	 */
	private void selectAclForFolder(FileModel folder){
		GWT.log("Loading ACL to: "+folder);
		AppControllerExplorer.rpcWorkspaceService.getACLBySharedFolderId(folder.getIdentifier(), new AsyncCallback<WorkspaceACL>() {

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("An error occurred on selecting current ACL "+arg0);

			}

			@Override
			public void onSuccess(WorkspaceACL arg0) {
				GWT.log("Loaded ACL "+arg0);
				permission.selectACL(arg0);
				hpPermission.layout();
				layout();
			}
		});

	}

	/**
	 * Gets the users managers.
	 *
	 * @param sharedFolderId the shared folder id
	 * @param callback the callback
	 * @return the users managers
	 */
	public void getUsersManagers(final String sharedFolderId,final AsyncCallback<List<InfoContactModel>> callback) {

		AppControllerExplorer.rpcWorkspaceService.getUsersManagerToSharedFolder(sharedFolderId, new AsyncCallback<List<InfoContactModel>>() {

			@Override
			public void onFailure(Throwable arg0) {
				GWT.log("an error occured in getting user managers by Id "+sharedFolderId + " "+arg0.getMessage());
				new MessageBoxAlert("Alert", "Sorry, an error occurred on getting users managers, try again later",null);

			}

			@Override
			public void onSuccess(List<InfoContactModel> listManagers) {
				callback.onSuccess(listManagers);

			}
		});
	}


	/**
	 * Permission control.
	 *
	 * @param listManagers the list managers
	 * @param showAlert the show alert
	 * @param folder the folder
	 */
	private void permissionControl(List<InfoContactModel> listManagers, boolean showAlert, FileModel folder){

		boolean permissionsOk = false;
		for (InfoContactModel infoContactModel : listManagers) {

			GWT.log("DialogPermission control compare between : "+infoContactModel.getLogin() +" and my login: "+AppControllerExplorer.myLogin);
			if(AppControllerExplorer.myLogin.compareToIgnoreCase(infoContactModel.getLogin())==0){
				permissionsOk = true;
				break;
			}

		}

		if(permissionsOk){
			enableFormDialog(true);
		}else{
			enableFormDialog(false);
			if(showAlert)
				new MessageBoxAlert("Permission denied", "You have no permissions to change sharing. You are not manager of \""+folder.getName()+"\"", null);
		}

	}

}
