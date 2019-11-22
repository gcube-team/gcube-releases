package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WsPortletInitOperation;
import org.gcube.portlets.user.workspace.client.event.CreateSharedFolderEvent;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;
import org.gcube.portlets.widgets.switchbutton.client.SwitchButton;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingServiceAsync;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.AllowAccess;

import com.extjs.gxt.ui.client.util.Format;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Well;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class DialogShareableLink.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 19, 2019
 */
public class DialogShareableLink extends Composite {

	private static DialogShareableLinkUiBinder uiBinder = GWT.create(DialogShareableLinkUiBinder.class);

	private Modal modalBox = new Modal();

	@UiField
	ControlGroup cgPublicLink;

//	@UiField
//	ControlGroup cgRemovePublicLink;

//	@UiField
//	ControlGroup cgPrivateLink;

	@UiField
	TextBox textPublicLink;

	@UiField
	TextBox textPrivateLink;

//	@UiField
//	Button removePublicLink;
	
	@UiField
	Alert errorAlert;
	
	@UiField
	Alert actionAlert;
	
	@UiField
	Well alertFilePublicLink;
	
	@UiField
	Well alertFolderPublicLink;
	
	@UiField
	Fieldset fieldSetPrivate;
	
	@UiField
	Fieldset fieldSetPublic;
	
	@UiField
	SwitchButton switchButton;
	
	@UiField
	VerticalPanel fieldPrivateSharing;
	
	@UiField
	SwitchButton switchButtonPrivate;
	
	@UiField
	VerticalPanel filedEnableDisableSharing;
	
	@UiField
	Label labelLinkSharing;
	
	@UiField
	Button buttonShareSettings;
	
	@UiField
	Well wellPrivateLinkDescription;
	
//	@UiField
//	HTMLPanel panelFieldsContainer;

	private FileModel fileItem;

	private String fileVersion;

	private boolean itemIsPublicStatus;
	
	private final String privateShareToFileDescription = "By sharing the following Private Link "
			+ "with your coworkers, you will enact the users of the group the folder is shared with, "
			+ "to access the file and the shared folder content. Login required";
	
	
	/**
	 * The Interface DialogShareableLinkUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 * Sep 19, 2019
	 */
	interface DialogShareableLinkUiBinder extends UiBinder<Widget, DialogShareableLink> {
	}

	/**
	 * Instantiates a new dialog shareable link.
	 *
	 * @param item the item
	 * @param version the version
	 */
	public DialogShareableLink(FileModel item, String version) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.fileItem = item;
		this.fileVersion = version;
		this.actionAlert.setAnimation(true);
		switchButtonPrivate.setValue(true);
		
		showShareableLinkOptions(item, version);
		//getElement().setClassName("gwt-DialogBoxNew");
		modalBox.setTitle("Get Shareable Link to: "+Format.ellipse(item.getName(), 15));
		ModalFooter modalFooter = new ModalFooter();
		final Button buttClose = new Button("Close");
		modalFooter.add(buttClose);
		
		switchButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				GWT.log("Switch to: "+event.getValue());
				acessToFolderLink(fileItem, event.getValue());
			}
			
		});
		
		buttClose.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				modalBox.hide();
			}
		});
		
		buttonShareSettings.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AppControllerExplorer.getEventBus().fireEvent(new CreateSharedFolderEvent(fileItem, fileItem.getParentFileModel(),false));
				modalBox.hide();
			}
		});
		
		modalBox.add(this);
		modalBox.add(modalFooter);
		modalBox.show();
		
	}
	
	/**
	 * Show shareable link options.
	 *
	 * @param item the item
	 * @param version the version
	 */
	public void showShareableLinkOptions(FileModel item, String version) {
		
		//cgRemovePublicLink.setVisible(false);
		fieldSetPrivate.setVisible(false);
		fieldPrivateSharing.setVisible(false);
		cgPublicLink.setVisible(false);
		alertFilePublicLink.setVisible(false);
		alertFolderPublicLink.setVisible(false);
		filedEnableDisableSharing.setVisible(false);
		buttonShareSettings.setVisible(false);
		switchButton.setVisible(true);
		//panelFieldsContainer.setVisible(false);
		showMessage("", false);
		this.itemIsPublicStatus = item.isPublic();

		if(item.isDirectory()) { //CASE FOLDER -  MANAGING AS PUBLIC LINK TO FOLDER
			switchButton.setValue(item.isPublic());
			//IT IS PRIVATE LINK
			if(item.isPublic()) { //CASE PUBLIC FOLDER
				alertFolderPublicLink.setVisible(true);
				//cgRemovePublicLink.setVisible(true);
				filedEnableDisableSharing.setVisible(true);
				labelLinkSharing.setText("Anyone with the Public Link can view");
				cgPublicLink.setVisible(true);
				acessToFolderLink(item, true);
			}
			
			if(item.isShared()) { //CASE SHARED FOLDER 
				//panelFieldsContainer.setVisible(true);
				fieldSetPrivate.setVisible(true);
				fieldPrivateSharing.setVisible(true);
				
				if(item.isShareable()) {
					buttonShareSettings.setVisible(true);
				}
				
				loadAndShowPrivateLink(item, textPrivateLink);
				
				if(!item.isPublic()) { //THE FOLDER IS NOT PUBLIC
					cgPublicLink.setVisible(false);
					filedEnableDisableSharing.setVisible(true);
					labelLinkSharing.setText("");
					//labelLinkSharing.setText("Only your cowokers (sharing members) can access to the content");
				}
			}

			//THE FOLDER IS PRIVATE - NO SHARED AND NO PUBLIC
			if(!item.isPublic() && !item.isShared()) {
				
				//SHARING WITH COWORKERS
				if(item.isShareable()) {
					fieldPrivateSharing.setVisible(true);
					buttonShareSettings.setVisible(true);
					buttonShareSettings.setText("Enable Share");
				}
				
				//SHARING WITH EVERYONE
				filedEnableDisableSharing.setVisible(true);
				alertFolderPublicLink.setVisible(true);
				labelLinkSharing.setText("The folder is private, not shared");
			}

		}else { //CASE FILE	- MANAGING AS PUBLIC LINK TO FILE
			//panelFieldsContainer.setVisible(true);
			cgPublicLink.setVisible(true);
			filedEnableDisableSharing.setVisible(true);
			alertFilePublicLink.setVisible(true);
			if(!item.isPublic()) { //THE FILE IS PRIVATE
				switchButton.setVisible(false);
				if(version!=null && !version.isEmpty()) {
					showPublicLinkForFileItemIdToVersion(item, textPublicLink, version);
				}else {
					loadAndShowPublicLinkForItem(item, textPublicLink);
				}
			}
			
			if(item.isShared()) {
				wellPrivateLinkDescription.clear();
				wellPrivateLinkDescription.getElement().setInnerHTML(privateShareToFileDescription);
				fieldSetPrivate.setVisible(true);
				fieldPrivateSharing.setVisible(true);
				loadAndShowPrivateLink(item, textPrivateLink);
			}
		}
		
	}
	

	/**
	 * Load and show private link.
	 *
	 * @param item the item
	 * @param toTextBox the to text box
	 */
	private void loadAndShowPrivateLink(FileModel item, final TextBox toTextBox) {
		
		String currentUrl = portalURL();
		int lastChar = currentUrl.lastIndexOf("?");
		currentUrl = lastChar > -1 ? currentUrl.substring(0, lastChar) : currentUrl;
		String shareLinkUrl = currentUrl + "?" + ConstantsExplorer.GET_ITEMID_PARAMETER + "=" + item.getIdentifier();
		shareLinkUrl += "&" + ConstantsExplorer.GET_OPERATION_PARAMETER + "=" + WsPortletInitOperation.gotofolder;
		
		final String longURL = shareLinkUrl;
		AppControllerExplorer.rpcWorkspaceService.getShortUrl(shareLinkUrl, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				toTextBox.setText(longURL);
			}

			@Override
			public void onSuccess(String shorURL) {
				
				if(shorURL!=null && !shorURL.isEmpty()) {
					toTextBox.setText(shorURL);
				}else {
					toTextBox.setText(longURL);
				}
				
			}
		});
	}
	
	/**
	 * Portal url.
	 *
	 * @return the string
	 */
	public static native String portalURL()/*-{
		return $wnd.location.href;
	}-*/;
	


	/**
	 * Load and show public link for item.
	 *
	 * @param item the item
	 * @param toTextBox the to text box
	 */
	private void loadAndShowPublicLinkForItem(FileModel item, final TextBox toTextBox) {

		AppControllerExplorer.rpcWorkspaceService.getPublicLinkForFileItemId(item.getIdentifier(), true,
			new AsyncCallback<PublicLink>() {

			@Override
			public void onSuccess(PublicLink publicLink) {
				String toURL = publicLink.getShortURL() != null && !publicLink.getShortURL().isEmpty()
						? publicLink.getShortURL()
						: publicLink.getCompleteURL();
				toTextBox.setValue(toURL);
			}

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof SessionExpiredException) {
					GWT.log("Session expired");
					AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
					return;
				}
				//new MessageBoxAlert("Error", caught.getMessage(), null);
				//toTextBox.setText(caught.getMessage());
				showError(caught.getMessage());
			}
		});
	}
	

	/**
	 * Show public link for file item id to version.
	 *
	 * @param item the item
	 * @param toTextBox the to text box
	 * @param version the version
	 */
	private void showPublicLinkForFileItemIdToVersion(FileModel item, final TextBox toTextBox, final String version) {
	
		AppControllerExplorer.rpcWorkspaceService.getPublicLinkForFileItemIdToVersion(item.getIdentifier(),
			version, true, new AsyncCallback<PublicLink>() {

			@Override
			public void onSuccess(PublicLink publicLink) {
				String toURL = getValidURL(publicLink);
				toTextBox.setValue(toURL);
			}
	
			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof SessionExpiredException) {
					GWT.log("Session expired");
					AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
					return;
				}
				//new MessageBoxAlert("Error", caught.getMessage(), null);
				showError(caught.getMessage());
			}
		});
	}
	
	/**
	 * Show error.
	 *
	 * @param msg the msg
	 */
	private void showError(String msg) {
		errorAlert.setVisible(true);
		errorAlert.setText(msg);
	}
	
	/**
	 * Gets the valid URL.
	 *
	 * @param publicLink the public link
	 * @return the valid URL
	 */
	private String getValidURL(PublicLink publicLink) {
		
		if(publicLink==null)
			return "Error on getting a valid shareable link";
		
		return publicLink.getShortURL() != null && !publicLink.getShortURL().isEmpty()
				? publicLink.getShortURL()
				: publicLink.getCompleteURL();
	}
	
	/**
	 * Acess to folder link.
	 *
	 * @param item the item
	 * @param setAsPublic the set as public
	 */
	private void acessToFolderLink(final FileModel item, final boolean setAsPublic) {
		
		//panelFieldsContainer.setVisible(false);
		fieldSetPublic.setVisible(false);
		
		WorkspaceSharingServiceAsync.INSTANCE.accessToFolderLink(item.getIdentifier(),
			new AsyncCallback<AllowAccess>() {

				@Override
				public void onFailure(Throwable caught) {
					fieldSetPublic.setVisible(false);
					if (caught instanceof SessionExpiredException) {
						GWT.log("Session expired");
						AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
						return;
					}
					showError(caught.getMessage());
				}

				@Override
				public void onSuccess(AllowAccess result) {
					fieldSetPublic.setVisible(true);
					GWT.log("AllowAccess? " + result);
					if (result.getAccessGranted()) {
						String msg = setAsPublic ? "Getting" : "Removing";
						msg = msg + " Public Link... permissions granted";
						showMessage(msg, true);
						allowAccessToFolderLink(item.getIdentifier(), setAsPublic);
					} else {
						showError("Permission Denied!" + " "+result.getAccessAllowDenyMotivation());
					}

				}
			});
	}
	
	
	/**
	 * Show message.
	 *
	 * @param msg the msg
	 * @param visible the visible
	 */
	private void showMessage(String msg, boolean visible) {
		actionAlert.setVisible(visible);
		actionAlert.setText(msg==null?"":msg);

	}
	
	/**
	 * Allow access to folder link.
	 *
	 * @param folderId
	 *            the folder id
	 * @param setIsPublic
	 *            the set is public
	 */
	protected void allowAccessToFolderLink(String folderId, final boolean setIsPublic) {

		AppControllerExplorer.rpcWorkspaceService.markFolderAsPublicForFolderItemId(folderId, setIsPublic,
			new AsyncCallback<PublicLink>() {

				@Override
				public void onSuccess(PublicLink publicLink) {

					if (!setIsPublic && publicLink == null) {
						String msg = "Public Link to the folder '" + fileItem.getName() + "' removed correctly";
						showMessage(msg, true);
						AppControllerExplorer.getEventBus().fireEvent(new RefreshFolderEvent(fileItem.getParentFileModel(), true, false, false));
						
						//REFRESHING ONLY ON CHANGING STATUS
						if(itemIsPublicStatus) {
							//HERE THE PREVIOUS STATUS WAS ISPUBLIC = TRUE
							fileItem.setIsPublic(false);
							showShareableLinkOptions(fileItem,fileVersion);
						}
						return;
					}
					
					String validURL = getValidURL(publicLink);
					textPublicLink.setValue(validURL);

					Timer t = new Timer() {
						@Override
						public void run() {
							GWT.log("Runing refresh after wait: " + AppControllerExplorer.delayTime);
							AppControllerExplorer.getEventBus()
							.fireEvent(new RefreshFolderEvent(fileItem.getParentFileModel(), true, false, false));
						}
					};

					t.schedule(AppControllerExplorer.delayTime);

					showMessage("", false);
					//REFRESHING ONLY ON CHANGING STATUS
					if(!itemIsPublicStatus) {
						//HERE THE PREVIOUS STATUS WAS ISPUBLIC = FALSE
						fileItem.setIsPublic(true);
						showShareableLinkOptions(fileItem,fileVersion);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					fieldSetPublic.setVisible(false);
					if (caught instanceof SessionExpiredException) {
						GWT.log("Session expired");
						AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
						return;
					}
					showError(caught.getMessage());
				}
			});
	}
}
