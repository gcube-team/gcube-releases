package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.PublicLink;
import org.gcube.portlets.user.workspace.shared.SessionExpiredException;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class DialogGetLink.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 13, 2016
 */
public class DialogGetLink extends Dialog {

	private TextField<String> txtCompleteURL;
	private TextField<String> txtShortURL;
	private int widht = 450;
	private int height = 210;
	private VerticalPanel vp = new VerticalPanel();
	private FileModel item;
	private boolean setAsPublic = false;

	/**
	 * The Enum Link_Type.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Sep 13, 2016
	 */
	public enum Link_Type {PUBLIC_LINK, FOLDER_LINK};

	/**
	 * Instantiates a new dialog get link.
	 *
	 * @param headingTxt the heading txt
	 * @param item the item
	 * @param type the type
	 * @param setAsPublic the set as public
	 */
	public DialogGetLink(String headingTxt, final FileModel item, Link_Type type, boolean setAsPublic) {
		this.item = item;
		this.setAsPublic = setAsPublic;
		setButtonAlign(HorizontalAlignment.CENTER);
		vp.setHorizontalAlign(HorizontalAlignment.CENTER);
		vp.setVerticalAlign(VerticalAlignment.MIDDLE);
		vp.getElement().getStyle().setPadding(1, Unit.PX);
		setHeading(headingTxt);
		setModal(true);
		setBodyStyle("padding: 3px; background: none");
		setWidth(widht);
		setHeight(height);
		setResizable(false);
		setButtons(Dialog.CLOSE);
		setScrollMode(Scroll.AUTOY);

//		label.setText(msgTitle);
//		label.setStyleName("myWebDavStyle");
		VerticalPanel vp1 = new VerticalPanel();
		vp1.setStyleAttribute("margin-top", "8px");
		txtCompleteURL = new TextField<String>();
		txtCompleteURL.setStyleAttribute("margin-top", "1px");
		txtCompleteURL.setWidth(widht-20);
		txtCompleteURL.setReadOnly(true);
//		txtCompleteURL.mask("Getting Link...");
		vp1.add(new Label("Link"));
		vp1.add(txtCompleteURL);

		VerticalPanel vp2 = new VerticalPanel();
		vp2.setStyleAttribute("margin-top", "8px");
		txtShortURL = new TextField<String>();
		txtShortURL.setStyleAttribute("margin-top", "1px");
		txtShortURL.setWidth(widht-20);
//		txtShortURL.mask("Getting Link...");
		vp2.add(new Label("Short Link"));
		vp2.add(txtShortURL);


		switch (type) {
		case PUBLIC_LINK:

			vp.mask("Getting Public Link...");
			setIcon(Resources.getIconPublicLink());

			if(item.getIdentifier()!=null && !item.getIdentifier().isEmpty()){
				AppControllerExplorer.rpcWorkspaceService.getPublicLinkForFolderItemId(item.getIdentifier(), true, new AsyncCallback<PublicLink>() {

					@Override
					public void onSuccess(PublicLink publicLink) {
						vp.unmask();
						txtCompleteURL.setValue(publicLink.getCompleteURL());
						txtShortURL.setValue(publicLink.getShortURL());
						selectTxt();
					}

					@Override
					public void onFailure(Throwable caught) {
						vp.unmask();
						if(caught instanceof SessionExpiredException){
							GWT.log("Session expired");
							AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
							return;
						}
						new MessageBoxAlert("Error", caught.getMessage(), null);
					}
				});

			}else{
				txtCompleteURL.unmask();
				new MessageBoxAlert("Error", "The item identifier is null", null);
			}

			break;

		case FOLDER_LINK:

			vp.mask("Updating Folder Link... checking permissions");
			if(item.isShared()){
				setIcon(Resources.getIconFolderSharedPublic());
			}else
				setIcon(Resources.getIconFolderPublic());

			if(item.getIdentifier()!=null && !item.getIdentifier().isEmpty()){
				AppControllerExplorer.rpcWorkspaceService.getOwnerByItemId(item.getIdentifier(), new AsyncCallback<InfoContactModel>() {

					@Override
					public void onFailure(Throwable caught) {
						vp.unmask();
						if(caught instanceof SessionExpiredException){
							GWT.log("Session expired");
							AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
							return;
						}
						new MessageBoxAlert("Error", caught.getMessage(), null);

					}

					@Override
					public void onSuccess(InfoContactModel result) {
						vp.unmask();
						if(result.getLogin().compareToIgnoreCase(AppControllerExplorer.myLogin)==0){
							String msg = DialogGetLink.this.setAsPublic?"Getting":"Removing";
							msg=msg+" Folder Link... permissions granted";
							vp.mask(msg);
							allowAccessToFolderLink(item.getIdentifier(), DialogGetLink.this.setAsPublic);
						}else
							new MessageBoxAlert("Permission Denied", "You have not permission to get Folder Link, you must be owner or administrator to the folder", null);

					}
				});
			}

			break;
		default:
			break;
		}

		this.getButtonById(Dialog.CLOSE).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}

		});


//		vp.add(label);
		vp.add(vp1);
		vp.add(vp2);

		setFocusWidget(txtCompleteURL);

		add(vp);
	}

	/**
	 * Allow access to folder link.
	 *
	 * @param folderId the folder id
	 * @param setIsPublic the set is public
	 */
	protected void allowAccessToFolderLink(String folderId, final boolean setIsPublic){

		AppControllerExplorer.rpcWorkspaceService.markFolderAsPublicForFolderItemId(folderId, setIsPublic, new AsyncCallback<PublicLink>() {

			@Override
			public void onSuccess(PublicLink publicLink) {

				if(!setIsPublic && publicLink==null){
					DialogGetLink.this.hide();
					MessageBox.info("Folder Link Removed", "Folder Link to the folder: "+item.getName()+ " removed correctly", null);
					AppControllerExplorer.getEventBus().fireEvent(new RefreshFolderEvent(item.getParentFileModel(), true, false, false));
					return;
				}

				vp.unmask();
				txtCompleteURL.setValue(publicLink.getCompleteURL());
				txtShortURL.setValue(publicLink.getShortURL());
				selectTxt();
				AppControllerExplorer.getEventBus().fireEvent(new RefreshFolderEvent(item.getParentFileModel(), true, false, false));
			}

			@Override
			public void onFailure(Throwable caught) {
				vp.unmask();
				if(caught instanceof SessionExpiredException){
					GWT.log("Session expired");
					AppControllerExplorer.getEventBus().fireEvent(new SessionExpiredEvent());
					return;
				}
				new MessageBoxAlert("Error", caught.getMessage(), null);
			}
		});
	}

	/**
	 * Gets the txt value.
	 *
	 * @return the txt value
	 */
	public String getTxtValue() {

		return txtCompleteURL.getValue();
	}

	/**
	 * Select txt.
	 */
	public void selectTxt(){

		if(txtCompleteURL.getValue()!=null)
			txtCompleteURL.select(0, txtCompleteURL.getValue().length());
	}
}