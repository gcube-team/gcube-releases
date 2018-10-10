/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.SmartConstants;
import org.gcube.portlets.widgets.workspacesharingwidget.client.resources.Resources;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.DialogMultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.MultiDragContact;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest.MultiValuePanel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * The Class SmartShare.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 3, 2014
 */
public class SmartShare extends Dialog implements SmartDialogInterface{

	private TextField<String> txtName;
	private FileModel fileToShare = null;
	private UserStore userStore = new UserStore();
	private MultiValuePanel suggestPanel = null;
	private Button buttonMultiDrag = new Button("Choose Contacts");
	private boolean readGroupsFromHL;
	private boolean readGroupsFromPortal;

    /**
     * SmartShare base constructor by default does not retrieve groupss.
     */
	public SmartShare() {
		this(false, false);
	}


	/**
	 * Instantiates a new smart share.
	 *
	 * @param readGroupsFromHL - if true read group names from HL
	 * @param readGroupsFromPortal - if true read group names from Portal (as VRE)
	 */
	public SmartShare(boolean readGroupsFromHL, boolean readGroupsFromPortal) {
		this.readGroupsFromHL = readGroupsFromHL;
		this.readGroupsFromPortal = readGroupsFromPortal;
		suggestPanel = new MultiValuePanel(userStore, readGroupsFromHL, readGroupsFromPortal);
		initLayout();
	}

	private List<InfoContactModel> listAlreadyShared = new ArrayList<InfoContactModel>(){

		private static final long serialVersionUID = -610980920163628336L;

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
	 * Use to modify a shared folder or share an existing folder.
	 *
	 * @param fileModel the file model
	 * @param listAlreadySharedContact the list already shared contact
	 */

	public void updateSharingDialog(FileModel fileModel, List<CredentialModel> listAlreadySharedContact){
		this.fileToShare = fileModel;
		String heading;
		if(SmartConstants.HEADER_TITLE==null){
			heading = "Share";
		    if(fileModel.isDirectory())
		    	heading+=" folder: ";
		    else
		    	heading+= " : ";

		    setHeading(heading+fileModel.getName());
		}else
			setHeading(SmartConstants.HEADER_TITLE);

	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
	    txtName.setValue(fileModel.getName());
	    txtName.setReadOnly(true);
	    txtName.setAutoValidate(true);

	    if(SmartConstants.ITEM_NAME!=null)
	    	txtName.setFieldLabel(SmartConstants.ITEM_NAME);


	    final LayoutContainer lc = new LayoutContainer();
	    lc.setStyleAttribute("margin-top", "10px");
	    lc.setStyleAttribute("margin-bottom", "10px");
	    lc.setSize(480, 50);

	    final FlexTable flexTable = new FlexTable();
	    flexTable.setStyleName("userssuggest");
	    Label labelShareWith = new Label("");
	    if(SmartConstants.SHARE_WITH_USERS!=null)
	    	labelShareWith.setText(SmartConstants.SHARE_WITH_USERS);

	    flexTable.setWidget(0, 0, labelShareWith);
	    flexTable.setWidget(0, 1, suggestPanel);

	    buttonMultiDrag.setStyleName("wizardButton");

	    Label labelAddMore = new Label("");
	    if(SmartConstants.ADD_MORE!=null)
	    	labelAddMore.setText(SmartConstants.ADD_MORE);

	    flexTable.setWidget(1, 0, labelAddMore);
	    flexTable.setWidget(1, 1, buttonMultiDrag);

	    if(listAlreadySharedContact!=null && listAlreadySharedContact.size()>0){

	    	userStore.getInfoContactModelsFromCredential(listAlreadySharedContact, new AsyncCallback<List<InfoContactModel>>() {

				@Override
				public void onSuccess(List<InfoContactModel> result) {
					for (InfoContactModel infoContactModel : result) {
						String name = infoContactModel.getName()!=null && !infoContactModel.getName().isEmpty()?infoContactModel.getName():infoContactModel.getLogin();
						listAlreadyShared.add(infoContactModel);
 						suggestPanel.addRecipient(name,false);
 						layout();
	 				}
	 				lc.unmask();
	 				lc.layout();
	 				layout();
				}

				@Override
				public void onFailure(Throwable caught) {
					 lc.unmask();
				}
			});
	    }

	    lc.add(flexTable);
	    setFocusWidget(suggestPanel.getBox());
        add(txtName);
		add(lc);
		addListeners();

	}

	/**
	 * Gets the file to share.
	 *
	 * @return the file to share
	 */
	public FileModel getFileToShare() {
		return fileToShare;
	}

	/**
	 * Inits the layout.
	 */
	private void initLayout(){
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
	    setIcon(Resources.getIconShare());
	}


	/**
	 * Gets the shared list users.
	 *
	 * @return the selected contacts (as InfoContactModel)
	 */
	public List<InfoContactModel> getSharedListUsers() {
//		printSelectedUser();

		List<InfoContactModel> contacts = new ArrayList<InfoContactModel>();

		for (InfoContactModel wsuser : suggestPanel.getSelectedUser()) {
			CredentialModel credential = getCredentialModelFromInfoContactModel(wsuser);
			wsuser.setReferenceCredential(credential);
			contacts.add(wsuser);
		}

		return contacts;
	}


	/**
	 * Gets the shared list users credential.
	 *
	 * @return the selected contacts (as CredentialModel)
	 */
	public List<CredentialModel> getSharedListUsersCredential() {

		List<CredentialModel> toReturn = new ArrayList<CredentialModel>();

		for (InfoContactModel wsuser : suggestPanel.getSelectedUser()) {
			CredentialModel credential = wsuser.getReferenceCredential();

			if(credential==null)
				credential = new CredentialModel(null, "UNKWNOWN LOGIN", wsuser.isGroup());

			toReturn.add(credential);
		}
		return toReturn;
	}

	/**
	 * Gets the credential model from info contact model.
	 *
	 * @param infoContact the info contact
	 * @return the credential model from info contact model
	 */
	private CredentialModel getCredentialModelFromInfoContactModel(InfoContactModel infoContact){

		if(infoContact==null)
			return null;

		if(infoContact.getReferenceCredential()==null){
			return new CredentialModel(null, infoContact.getLogin(), infoContact.isGroup());
		}else if(infoContact.getReferenceCredential()!=null)
			return infoContact.getReferenceCredential();

		return null;

	}

	/**
	 * Adds the listeners.
	 */
	public void addListeners(){

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
				final DialogMultiDragContact dialog = new DialogMultiDragContact(true,true);
				final MultiDragContact multiDrag = dialog.getMultiDrag();
				dialog.show();
//				printSelectedUser();
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
					}
				});
			}
		});
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
			if(ct.getLogin().compareTo(contact.getLogin())==0){
				return true;
			}
		}
		return false;
	}

	//DEBUG
	/**
	 * Prints the selected user.
	 */
	/*private void printSelectedUser(){

		GWT.log("SELETECTED USERS: ");
		for (InfoContactModel contact : suggestPanel.getSelectedUser())
			GWT.log(contact.toString());
	}*/

	/**
	 * Inits the suggest contacts.
	 */
	private void initSuggestContacts(){

		suggestPanel.resetItemSelected();

		for (InfoContactModel contact : listAlreadyShared) {
			suggestPanel.addRecipient(contact.getName(), false);
		}
	}


	/**
	 * Enable confirm button.
	 *
	 * @param enabled the enabled
	 */
	public void enableConfirmButton(boolean enabled){
		this.getButtonById(Dialog.OK).setEnabled(enabled);
	}

	/**
	 * Sets the as error.
	 *
	 * @param message the new as error
	 */
	public void setAsError(String message){
		enableFormDialog(false);
		this.add(new Html("<br/> <p style=\"color:red; font-family:verdana, arial; font-size:14px;\">"+message+"</p>"));
		this.layout();
	}

	/**
	 * Enable form dialog.
	 *
	 * @param bool the bool
	 */
	private void enableFormDialog(boolean bool){
		 getButtonById(Dialog.OK).setEnabled(bool);
		 buttonMultiDrag.setEnabled(bool);
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
	 * Checks if is valid form.
	 *
	 * @param displayAlert in case of error
	 * @return true, if is valid form
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
				MessageBox.alert("Attention", SmartConstants.ERROR_NO_USER_SELECTED, null);
			suggestPanel.boxSetFocus();
			return false;
		}
		return true;
	}


	/**
	 * Checks if is read groups from hl.
	 *
	 * @return true, if is read groups from hl
	 */
	public boolean isReadGroupsFromHL() {
		return readGroupsFromHL;
	}


	/**
	 * Checks if is read groups from portal.
	 *
	 * @return true, if is read groups from portal
	 */
	public boolean isReadGroupsFromPortal() {
		return readGroupsFromPortal;
	}

}
