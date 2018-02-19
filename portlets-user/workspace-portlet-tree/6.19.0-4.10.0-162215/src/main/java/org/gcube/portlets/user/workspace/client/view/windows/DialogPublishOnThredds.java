/**
 *
 */
package org.gcube.portlets.user.workspace.client.view.windows;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.RefreshFolderEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.GcubeVRE;
import org.gcube.portlets.user.workspace.shared.TransferOnThreddsReport;
import org.gcube.portlets.user.workspace.shared.TransferToThreddsProperty;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class DialogPublishOnThredds.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 27, 2017
 */
public class DialogPublishOnThredds extends Dialog {

	/**
	 *
	 */
	private int widthDialog = 570;
	private TextField<String> txtCatalogueName;
	private TriggerField<String> triggerFieldMetadataFolderName;
	private ComboBox<GcubeVRE> selectVRE;
	//private Button browse = new Button("Browse...");
	private FileModel metadataFolder = null;
	private FileModel theFolderToPublish;
	private ListStore<GcubeVRE> vreStore = new ListStore<GcubeVRE>();

	public static List<GcubeVRE> listOfVres = null;
	private Label infoOnLoading = new Label("");

	private DialogPublishOnThredds INSTANCE = this;


	/**
	 * Instantiates a new dialog publish on thredds.
	 *
	 * @param theFolderToPublish the the folder to publish
	 */
	public DialogPublishOnThredds(FileModel theFolderToPublish) {

		this.theFolderToPublish = theFolderToPublish;
		infoOnLoading.setType(LabelType.INFO);
		infoOnLoading.setVisible(true);
		infoOnLoading.setText("Loading Configurations...");
		initLayout();
		INSTANCE.enablePublish(false);
		AppControllerExplorer.rpcWorkspaceService.getTransferToThreddsProperty(theFolderToPublish.getIdentifier(), new AsyncCallback<TransferToThreddsProperty>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error on loading TransferToThreddsProperty: "+caught.getMessage());
				infoOnLoading.setVisible(false);
				//initiFormFields();
				INSTANCE.enablePublish(true);
			}

			@Override
			public void onSuccess(TransferToThreddsProperty result) {
				GWT.log("Loaded TransferToThreddsProperty: "+result);
				//initiFormFields();

				if(result!=null){
					txtCatalogueName.setValue(result.getCatalogueName());

					for (GcubeVRE gvre : vreStore.getModels()) {
						if(gvre.getScope().compareTo(result.getVreScope())==0){
							selectVRE.setValue(gvre);
							break;
						}
					}

					if(result.getMetadataFolderId()!=null && result.getMetadataFolderName()!=null){

						metadataFolder = new FileModel(result.getMetadataFolderId(), result.getMetadataFolderName(), true);
						triggerFieldMetadataFolderName.setValue(metadataFolder.getName());
					}
				}

				infoOnLoading.setVisible(false);
				INSTANCE.enablePublish(true);
			}
		});

	}

	/**
	 * Load vres for logged user.
	 */
	private void loadVresForLoggedUser() {

		selectVRE.mask();
		selectVRE.setLoadingText("Loading VREs...");
		getButtonById(Dialog.OK).setEnabled(false);
		AppControllerExplorer.rpcWorkspaceService.getListOfVREsForLoggedUser(new AsyncCallback<List<GcubeVRE>>() {

			@Override
			public void onSuccess(List<GcubeVRE> result) {

				listOfVres = result;
				setStoreToSelectVre();
				getButtonById(Dialog.OK).setEnabled(true);
				selectVRE.unmask();
			}

			@Override
			public void onFailure(Throwable caught) {

				GWT.log("Error on getting VREs");
			}
		});

	}



	/**
	 * Sets the store to select vre.
	 */
	private void setStoreToSelectVre() {
		vreStore.removeAll();
		vreStore.add(listOfVres);
	}


	/**
	 * Instantiates a new dialog add folder and smart.
	 */
	private void initLayout() {

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(140);
		layout.setDefaultWidth(380);
		setLayout(layout);
		setButtonAlign(HorizontalAlignment.CENTER);
		// setHideOnButtonClick(true);
		// setIcon(IconHelper.createStyle("user"));
		setHeading("Publish "+Format.ellipse(theFolderToPublish.getName(), 20)+" on Thredds...");
		setModal(true);
		// setBodyBorder(true);
		setBodyStyle("padding: 9px; background: none");
		setWidth(widthDialog);
		setResizable(false);
		setButtons(Dialog.OKCANCEL);

		this.getButtonById(Dialog.OK).setText("Publish");
		this.getButtonById(Dialog.CANCEL).setText("Close");
		// this.getButtonById(Dialog.CANCEL).setText("Reset");

		txtCatalogueName = new TextField<String>();
		txtCatalogueName.setEmptyText("Type a Name...");
		txtCatalogueName.setAllowBlank(false);
		txtCatalogueName.setAutoValidate(true);
	    txtCatalogueName.getMessages().setRegexText(ConstantsExplorer.REGEX_WSFOLDER_NAME_ALERT_MSG);
	    txtCatalogueName.setRegex(ConstantsExplorer.REGEX_TO_WSFOLDER_NAME);
		txtCatalogueName.setFieldLabel("Catalogue Name *");


		triggerFieldMetadataFolderName = new TriggerField<String>();
		triggerFieldMetadataFolderName.setAllowBlank(true);
		triggerFieldMetadataFolderName.setReadOnly(true);
		triggerFieldMetadataFolderName.setAutoValidate(true);
		triggerFieldMetadataFolderName.setFieldLabel("Metadata Folder");
		triggerFieldMetadataFolderName.setEmptyText("Browse a Metadata Folder...");
		triggerFieldMetadataFolderName.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				showWsExplorer();
			}
		});

		selectVRE = new ComboBox<GcubeVRE>();
		selectVRE.setEmptyText("Select a VRE...");
		selectVRE.setFieldLabel("Publish in the VRE *");
		selectVRE.setDisplayField("name");
		selectVRE.setStore(vreStore);
//		selectVRE.setAllowBlank(false);
//		selectVRE.setAutoValidate(true);
		if(listOfVres==null || listOfVres.isEmpty())
			loadVresForLoggedUser();
		else
			setStoreToSelectVre();
		//selectVRE.setStore(scopeNameModels);
		selectVRE.setTypeAhead(true);
		selectVRE.setEditable(false);
		selectVRE.setTriggerAction(TriggerAction.ALL);

		txtCatalogueName.addKeyListener(new KeyListener() { // KEY ENTER

			public void componentKeyPress(ComponentEvent event) {

				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode())
					getButtonById(Dialog.OK).fireEvent(Events.Select);
			}
		});

		this.getButtonById(Dialog.CANCEL).addSelectionListener(
			new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

					hide();
				}
			});

		this.getButtonById(Dialog.OK).addSelectionListener(
			new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {

					if (isValidForm()){
						hide();

						GWT.log("metadataFolder: "+metadataFolder);

						String metadataFolderId = metadataFolder!=null?metadataFolder.getIdentifier():null;

						GcubeVRE vre = selectVRE.getSelection().get(0);
						GWT.log("metadataFolderId: "+metadataFolderId);
						AppControllerExplorer.rpcWorkspaceService.publishOnThreddsCatalogue(theFolderToPublish.getIdentifier(), metadataFolderId, vre.getName(), vre.getScope(), txtCatalogueName.getValue(), new AsyncCallback<TransferOnThreddsReport>() {

							@Override
							public void onFailure(Throwable caught) {

								new MessageBoxAlert("Error", caught.getMessage(), null);

							}

							@Override
							public void onSuccess(TransferOnThreddsReport result) {
								GWT.log("Publishing on Thredds return: "+result);
								InfoDisplay.display("On going", "Transferring started correclty...");
								if(result!=null){
									pollingStart(result);
								}else
									new MessageBoxAlert("Error", "An error occurred during the transferring. Refresh the folder and try again", null);

							}
						});
					}
				}
			});


		setFocusWidget(txtCatalogueName);
		add(infoOnLoading);
		add(txtCatalogueName);
		add(selectVRE);
		//add(browse);
		add(triggerFieldMetadataFolderName);
		Label mandatory = new Label("* mandatory");
		mandatory.setType(LabelType.INFO);
		//mandatory.getElement().getStyle().set("#FF0000");
		add(mandatory);
		this.show();
	}



	/**
	 * Enable publish.
	 *
	 * @param enable the enable
	 */
	private void enablePublish(boolean enable){
		this.getButtonById(Dialog.OK).setEnabled(enable);
	}

	/**
	 * Show result.
	 *
	 * @param result the result
	 */
	public void showResult(TransferOnThreddsReport result){


		if(result.isOnError()){
			 MessageBox.alert("Error", result.getReportMessage(), null);
		}

		if(result.isTransferringReportAvailable()){
			InfoDisplay.display("Transferring completed", "Refreshing folder content...");
			FileModel folderTarget = new FileModel(result.getFolderId(), "", true);
			AppControllerExplorer.getEventBus().fireEvent(new RefreshFolderEvent(folderTarget, false, false, false));
		}

	}

	/**
	 * Polling start.
	 *
	 * @param result the result
	 */
	public void pollingStart(final TransferOnThreddsReport result){

		showResult(result);

		final Timer t =  new Timer(){

			public void run() {

				AppControllerExplorer.rpcWorkspaceService.getStatusOfPublishingOnThreddsCatalogue(result.getTransferId(), new AsyncCallback<TransferOnThreddsReport>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("Error", caught.getMessage(), null);
					}

					@Override
					public void onSuccess(TransferOnThreddsReport result) {
						GWT.log("Get status of Publishing on Thredds return: "+result);

						showResult(result);
						if(result.isOnError() || result.isTransferringReportAvailable()){
							GWT.log("Cancelling timer for Transferring: "+result);
							cancelTimer();
						}

					}
				});

			};

			public void cancelTimer(){
				this.cancel();
			}
		};

		t.scheduleRepeating(2000);

	}

	/**
	 * Checks if is valid form.
	 *
	 * @return true, if is valid form
	 */
	public boolean isValidForm() {

		if (txtCatalogueName.isValid() && txtCatalogueName.getValue() != null && selectVRE.getSelection().size()>0)
			return true;

//		if(selectVRE.getSelection().size()==0)
//			selectVRE.forceInvalid("Required");

		return false;
	}

	/**
	 * Show ws explorer.
	 */
	public void showWsExplorer(){

		final WorkspaceExplorerSelectDialog navigator = new WorkspaceExplorerSelectDialog("Select a folder...", true);

		WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {
				GWT.log("onSelectedItem: "+item);
				navigator.hide();
				triggerFieldMetadataFolderName.setValue(item.getName());
				metadataFolder = new FileModel(item.getId(), item.getName(), true);
			}

			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("onFailed..");

			}

			@Override
			public void onAborted() {
				GWT.log("onAborted..");

			}

			@Override
			public void onNotValidSelection() {
				GWT.log("onNotValidSelection..");

			}
		};

	navigator.addWorkspaceExplorerSelectNotificationListener(listener);
	navigator.setZIndex(XDOM.getTopZIndex()+50);
	navigator.show();
	}
}
