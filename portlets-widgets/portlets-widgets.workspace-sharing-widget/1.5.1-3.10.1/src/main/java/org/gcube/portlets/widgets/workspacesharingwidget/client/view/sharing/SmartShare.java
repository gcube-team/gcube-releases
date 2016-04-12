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
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 3, 2014
 *
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
	 * SmartShare base constructor by default does not retrieve groupss
	 */
	public SmartShare() {
		this(false, false);
	}
	

	/**
	 * 
	 * @param readGroupsFromHL - if true read group names from HL
	 * @param readGroupsFromPortal - if true read group names from Portal (as VRE)
	 * 
	 */
	public SmartShare(boolean readGroupsFromHL, boolean readGroupsFromPortal) {
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
	 					if(infoContactModel.getName()!=null){
	 						listAlreadyShared.add(infoContactModel);
		 				   suggestPanel.addRecipient(infoContactModel.getName(),false);
		 				   layout();
	 					}
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
//        add(txtOwner);
		add(lc);
		addListeners();
		
	}
	
	/**
	 * 
	 * @return
	 */
	public FileModel getFileToShare() {
		return fileToShare;
	}
	
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
	
	private CredentialModel getCredentialModelFromInfoContactModel(InfoContactModel infoContact){
		
		if(infoContact==null)
			return null;
		
		if(infoContact.getReferenceCredential()==null){
			return new CredentialModel(null, infoContact.getLogin(), infoContact.isGroup());
		}else if(infoContact.getReferenceCredential()!=null)
			return infoContact.getReferenceCredential();
		
		return null;
		
	}


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
				
				List<InfoContactModel> exclusiveContacts = userStore.getExclusiveContactsFromAllContact(suggestPanel.getSelectedUser());
				multiDrag.addSourceContacts(exclusiveContacts);
				
				
				for (InfoContactModel infoContactModel : suggestPanel.getSelectedUser()) {
					if(!listAlreadyShared.contains(infoContactModel))
						multiDrag.addTargetContact(infoContactModel);
				}
				
				multiDrag.addAlreadySharedContacts(suggestPanel.getSelectedUser());
				
//				multiDrag.addTargetContacts(suggestPanel.getSelectedUser());
				
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
	
	public void setAsError(String message){
		enableFormDialog(false);
		this.add(new Html("<br/> <p style=\"color:red; font-family:verdana, arial; font-size:14px;\">"+message+"</p>"));
		this.layout();
	}
	
	private void enableFormDialog(boolean bool){
		 getButtonById(Dialog.OK).setEnabled(bool);
		 buttonMultiDrag.setEnabled(bool);
	}

	public String getName() {
		return txtName.getValue();
	}

	/**
	 * 
	 * @param displayAlert in case of error
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
				MessageBox.alert("Attention", SmartConstants.ERROR_NO_USER_SELECTED, null);
			suggestPanel.boxSetFocus();
			return false;
		}
		
		return true;
		
	}


	public boolean isReadGroupsFromHL() {
		return readGroupsFromHL;
	}


	public boolean isReadGroupsFromPortal() {
		return readGroupsFromPortal;
	}

}
