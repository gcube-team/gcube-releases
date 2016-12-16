package org.gcube.portlets.user.workspace.client.view.toolbars;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.WS_UPLOAD_TYPE;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet.ViewSwitchTypeInResult;
import org.gcube.portlets.user.workspace.client.event.ActiveGroupingView;
import org.gcube.portlets.user.workspace.client.event.AddFolderEvent;
import org.gcube.portlets.user.workspace.client.event.DeleteItemEvent;
import org.gcube.portlets.user.workspace.client.event.DoubleClickElementSelectedEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent;
import org.gcube.portlets.user.workspace.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.workspace.client.event.FileUploadEvent;
import org.gcube.portlets.user.workspace.client.event.GetPublicLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GetShareLinkEvent;
import org.gcube.portlets.user.workspace.client.event.GridRefreshEvent;
import org.gcube.portlets.user.workspace.client.event.ImagePreviewEvent;
import org.gcube.portlets.user.workspace.client.event.ShowUrlEvent;
import org.gcube.portlets.user.workspace.client.event.PasteItemEvent;
import org.gcube.portlets.user.workspace.client.event.RenameItemEvent;
import org.gcube.portlets.user.workspace.client.event.VREChangePermissionsEvent;
import org.gcube.portlets.user.workspace.client.event.WebDavUrlEvent;
import org.gcube.portlets.user.workspace.client.event.WsGetFolderLinkEvent;
import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.grids.GxtGridFilterGroupPanel;
import org.gcube.portlets.user.workspace.client.view.tree.CutCopyAndPaste;
import org.gcube.portlets.user.workspace.client.view.tree.CutCopyAndPaste.OperationType;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.shared.GWT;

/**
 * The Class GxtToolBarItemFunctionality.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class GxtToolBarItemFunctionality {

	private Button btnAddFolder = null;
	private Button btnRefreshFolder = null;
	private Button btnRemoveItem = null;
	private Button btnRenameItem = null;
	private Button btnUploadFile = null;
	private Button btnUploadArchive = null;
	private Button btnDownloadFile = null;
	private Button btnOpen = null;
	private Button btnAccessFromDesktop = null;
	private ToggleButton toggleGroup = new ToggleButton();
	private ToggleButton toggleList = new ToggleButton();
	private ToggleButton toggleIcon = new ToggleButton();
	private GxtGridFilterGroupPanel gridGroupViewContainer;

	private ToolBar toolBar = new ToolBar();
	private Button btnPreview;
	private ToggleButton btnGridView;
//	private Button btnGetInfo;
	private Button btnGetSharedLink;
	private Button btnPublicLink;
	private Button btnFolderLink;
	private Button btnCopyItem;
	private Button btnPasteItem;
	private Button btnCutItem;
	private Button btnSetPermission;

	/**
	 * Instantiates a new gxt tool bar item functionality.
	 */
	public GxtToolBarItemFunctionality() {
		initToolBar();
		addSelectionListenersOnToolBar();
	}

	/**
	 * Instantiates a new gxt tool bar item functionality.
	 *
	 * @param gridFilterGroupContainer the grid filter group container
	 */
	public GxtToolBarItemFunctionality(GxtGridFilterGroupPanel gridFilterGroupContainer) {
		this();
		this.gridGroupViewContainer = gridFilterGroupContainer;
	}

	/**
	 * Inits the tool bar.
	 */
	private void initToolBar() {

	    btnGridView = new ToggleButton(ConstantsPortlet.CATEGORIZE, Resources.getIconGridView());
	    btnGridView.setToolTip("Categorize");
	    btnGridView.setScale(ButtonScale.SMALL);
	    btnGridView.setIconAlign(IconAlign.TOP);
	    btnGridView.toggle(true);
	    btnGridView.setArrowAlign(ButtonArrowAlign.BOTTOM);
	    toolBar.add(btnGridView);

		toolBar.add(new SeparatorToolItem());

		btnAddFolder = new Button(ConstantsPortlet.ADDFOLDER,Resources.getIconAddFolder());
		btnAddFolder.setScale(ButtonScale.SMALL);
		btnAddFolder.setIconAlign(IconAlign.TOP);
		btnAddFolder.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnAddFolder);

		btnUploadFile = new Button(ConstantsPortlet.UPLOADFILE,
				Resources.getIconFileUpload());
		btnUploadFile.setScale(ButtonScale.SMALL);
		btnUploadFile.setIconAlign(IconAlign.TOP);
		btnUploadFile.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnUploadFile);

		btnRefreshFolder = new Button(ConstantsPortlet.REFRESH, Resources.getIconRefresh());
		btnRefreshFolder.setScale(ButtonScale.SMALL);
		btnRefreshFolder.setIconAlign(IconAlign.TOP);
		btnRefreshFolder.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnRefreshFolder);

		toolBar.add(new SeparatorToolItem());
//		btnGetInfo = new Button("Info", Resources.getIconInfo());
//		btnGetInfo.setScale(ButtonScale.SMALL);
//		btnGetInfo.setIconAlign(IconAlign.TOP);
//		btnGetInfo.setArrowAlign(ButtonArrowAlign.BOTTOM);
//
//		toolBar.add(btnGetInfo);
//		toolBar.add(new SeparatorToolItem());

		btnRemoveItem = new Button(ConstantsPortlet.DELETEITEM,
				Resources.getIconDeleteItem());
		btnRemoveItem.setScale(ButtonScale.SMALL);
		btnRemoveItem.setIconAlign(IconAlign.TOP);
		btnRemoveItem.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnRemoveItem);
//		toolBar.add(new SeparatorToolItem());
//		toolBar.add(new SeparatorToolItem());

		btnRenameItem = new Button(ConstantsPortlet.RENAMEITEM, Resources.getIconRenameItem());
		btnRenameItem.setScale(ButtonScale.SMALL);
		btnRenameItem.setIconAlign(IconAlign.TOP);
		btnRenameItem.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnRenameItem);


		//COPY
		btnCopyItem = new Button("Copy", Resources.getIconCopy());
		btnCopyItem.setScale(ButtonScale.SMALL);
		btnCopyItem.setIconAlign(IconAlign.TOP);
		toolBar.add(btnCopyItem);


		//CUT
		btnCutItem = new Button("Cut", Resources.getIconCut());
		btnCutItem.setScale(ButtonScale.SMALL);
		btnCutItem.setIconAlign(IconAlign.TOP);
//		toolBar.add(btnCutItem);

		//PASTE
		btnPasteItem = new Button("Paste", Resources.getIconPaste());
		btnPasteItem.setScale(ButtonScale.SMALL);
		btnPasteItem.setIconAlign(IconAlign.TOP);
		btnPasteItem.setEnabled(false); //BY DEFAULT IS DISABLED

		toolBar.add(btnPasteItem);


		btnDownloadFile = new Button(ConstantsPortlet.DOWNLOADITEM,Resources.getIconDownload());
		btnDownloadFile.setScale(ButtonScale.SMALL);
		btnDownloadFile.setIconAlign(IconAlign.TOP);
		btnDownloadFile.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnDownloadFile);

		btnPreview = new Button(ConstantsPortlet.PREVIEW ,Resources.getIconPreview());
		btnPreview.setScale(ButtonScale.SMALL);
		btnPreview.setIconAlign(IconAlign.TOP);
		btnPreview.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnPreview);

		btnOpen = new Button(ConstantsPortlet.OPEN ,Resources.getIconShow());
		btnOpen.setScale(ButtonScale.SMALL);
		btnOpen.setIconAlign(IconAlign.TOP);
		btnOpen.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnOpen);


//		toolBar.add(new SeparatorToolItem());
		toolBar.add(new SeparatorToolItem());

		btnGetSharedLink = new Button("Get Link", Resources.getIconShareLink());
		btnGetSharedLink.setScale(ButtonScale.SMALL);
		btnGetSharedLink.setIconAlign(IconAlign.TOP);
		btnGetSharedLink.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnGetSharedLink);

		btnPublicLink = new Button("Public Link", Resources.getIconPublicLink());
		btnPublicLink.setScale(ButtonScale.SMALL);
		btnPublicLink.setIconAlign(IconAlign.TOP);
		btnPublicLink.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnPublicLink);
	    toolBar.add(new SeparatorToolItem());


		btnFolderLink = new Button("Folder Link", Resources.getIconFolderPublic());
		btnFolderLink.setScale(ButtonScale.SMALL);
		btnFolderLink.setIconAlign(IconAlign.TOP);
		btnFolderLink.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnFolderLink);
	    toolBar.add(new SeparatorToolItem());

	    btnAccessFromDesktop = new Button(ConstantsPortlet.TITLEACCESSWEBDAV, Resources.getIconWebDav());
	    btnAccessFromDesktop.setScale(ButtonScale.SMALL);
	    btnAccessFromDesktop.setIconAlign(IconAlign.TOP);
	    btnAccessFromDesktop.setArrowAlign(ButtonArrowAlign.BOTTOM);

//	    toolBar.add(btnAccessFromDesktop);
//	    toolBar.add(new SeparatorToolItem());

//		toolBar.add(new SeparatorToolItem());
//		toolBar.add(new SeparatorToolItem());

		btnUploadArchive = new Button(ConstantsPortlet.UPLOADARCHIVE, Resources.getIconArchiveUpload());
		btnUploadArchive.setScale(ButtonScale.SMALL);
		btnUploadArchive.setIconAlign(IconAlign.TOP);
		btnUploadArchive.setArrowAlign(ButtonArrowAlign.BOTTOM);

		toolBar.add(btnUploadArchive);
//		toolBar.add(new SeparatorToolItem());
//		toolBar.add(new SeparatorToolItem());

		btnSetPermission= new Button(ConstantsPortlet.CHANGEPERMISSION,Resources.getIconWriteAll());
		btnSetPermission.setScale(ButtonScale.SMALL);
		btnSetPermission.setIconAlign(IconAlign.TOP);
		btnSetPermission.setArrowAlign(ButtonArrowAlign.BOTTOM);
		btnSetPermission.setToolTip("Change VRE Folder permissions");
		toolBar.add(btnSetPermission);


	    //TODO OLD CODE
		toggleGroup = new ToggleButton();
		toggleGroup.setIcon(Resources.getIconToggleGroup());
		toggleGroup.setToggleGroup("viewgrid");
		toggleGroup.toggle(true);

		toggleIcon = new ToggleButton();
		toggleIcon.setIcon(Resources.getIconToggleIcon());
		toggleIcon.setToggleGroup("viewgrid");

		toggleList = new ToggleButton();
		toggleList.setIcon(Resources.getIconToggleList());
		toggleList.setToggleGroup("viewgrid");

		switchView(ViewSwitchTypeInResult.Group);

//		toolBar.add(new FillToolItem());

		this.activeButtonsOnSelectForOperation(null, false);
	}

	/**
	 * Toggle grid view button.
	 *
	 * @param toogle the toogle
	 */
	public void toggleGridViewButton(boolean toogle){
		btnGridView.toggle(toogle);
	}

	/**
	 * Adds the selection listeners on tool bar.
	 */
	private void addSelectionListenersOnToolBar() {

		btnAddFolder.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new AddFolderEvent(gridGroupViewContainer.getCurrentFolderView(), null));
			}
		});

		btnGridView.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new ActiveGroupingView(btnGridView.isPressed()));

			}
		});

		btnRemoveItem.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {


				List<FileGridModel> listTarget = gridGroupViewContainer.getSelectedItems();
				if(listTarget!=null){

					if(listTarget.size()>1){ //IS MULTI

						AppController.getEventBus().fireEvent(new DeleteItemEvent(listTarget));

					}else{

						final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
						AppController.getEventBus().fireEvent(new DeleteItemEvent(fileGridModel));
					}
				}

			}
		});


		btnGetSharedLink.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				AppController.getEventBus().fireEvent(new GetShareLinkEvent(fileGridModel));

			}
		});



		btnCopyItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<String> listTarget = gridGroupViewContainer.getIdsSelectedItems();
				if(listTarget!=null && listTarget.size()>0){
					CutCopyAndPaste.copy(listTarget, OperationType.COPY);
					btnPasteItem.setEnabled(true);
				}

//				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
//
//				if(fileGridModel.getIdentifier()!=null){
//					CopyAndPaste.copy(fileGridModel.getIdentifier());
//					btnPasteItem.setEnabled(true);
//				}

			}
		});


		btnCutItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<String> listTarget = gridGroupViewContainer.getIdsSelectedItems();
				if(listTarget!=null && listTarget.size()>0){
					CutCopyAndPaste.copy(listTarget, OperationType.CUT);
					btnPasteItem.setEnabled(true);
				}

//				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
//
//				if(fileGridModel.getIdentifier()!=null){
//					CopyAndPaste.copy(fileGridModel.getIdentifier());
//					btnPasteItem.setEnabled(true);
//				}

			}
		});

		btnPasteItem.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
				FileModel parentTarget = getDirectoryOrParent(fileGridModel);

				String parentId = null;

				if(parentTarget!=null)
					parentId=parentTarget.getIdentifier();

				PasteItemEvent event  = new PasteItemEvent(CutCopyAndPaste.getCopiedIdsFilesModel(), parentId, CutCopyAndPaste.getOperationType());

				if(event.getOperationType().equals(OperationType.CUT))
					event.setFolderSourceId(null);

				AppController.getEventBus().fireEvent(event);

				CutCopyAndPaste.setCopiedIdsFileModels(null);
				CutCopyAndPaste.setOperationType(null);
				btnPasteItem.setEnabled(false);

			}
		});


		btnRefreshFolder.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new GridRefreshEvent());

			}
		});

		btnPublicLink.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				AppController.getEventBus().fireEvent(new GetPublicLinkEvent(fileGridModel));

			}
		});

		btnFolderLink.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
				GWT.log("file selected: "+fileGridModel);
				if(fileGridModel.isDirectory())
					AppController.getEventBus().fireEvent(new WsGetFolderLinkEvent(fileGridModel));

			}
		});



//		btnGetInfo.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//
//				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
//
//				AppController.getEventBus().fireEvent(new GetInfoEvent(fileGridModel));
//
//			}
//		});

		btnRenameItem.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				AppController.getEventBus().fireEvent(
						new RenameItemEvent(fileGridModel));

			}
		});

		btnUploadFile.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				if(fileGridModel!=null)
					AppController.getEventBus().fireEvent(new FileUploadEvent(fileGridModel.getParentFileModel(), WS_UPLOAD_TYPE.File));
				else
					AppController.getEventBus().fireEvent(new FileUploadEvent(null, WS_UPLOAD_TYPE.File));
			}
		});

		btnUploadArchive.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				if(fileGridModel!=null)
					AppController.getEventBus().fireEvent(new FileUploadEvent(fileGridModel.getParentFileModel(), WS_UPLOAD_TYPE.Archive));
				else
					AppController.getEventBus().fireEvent(new FileUploadEvent(null, WS_UPLOAD_TYPE.Archive));
			}
		});

		btnSetPermission.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				if(fileGridModel!=null && fileGridModel.isVreFolder()){

					AppController.getEventBus().fireEvent(new VREChangePermissionsEvent(fileGridModel));
//					DialogPermission dialog = new DialogPermission(fileGridModel);
//					dialog.show();
				}

			}
		});


		btnDownloadFile.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();
				AppController.getEventBus().fireEvent(new FileDownloadEvent(fileGridModel.getIdentifier(), fileGridModel.getName(), DownloadType.DOWNLOAD, fileGridModel.isDirectory() || fileGridModel.isVreFolder()));
			}
		});

		btnPreview.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				AppController.getEventBus().fireEvent(new ImagePreviewEvent(fileGridModel, ce.getClientX(), ce.getClientY()+100));

			}
		});

		btnOpen.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				final FileGridModel fileGridModel = gridGroupViewContainer.getSelectedItem();

				if(fileGridModel!=null){
					if(fileGridModel.isDirectory())
						AppController.getEventBus().fireEvent(new DoubleClickElementSelectedEvent(fileGridModel));
//					else if(fileGridModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.REPORT) || fileGridModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.REPORT_TEMPLATE))
//							AppController.getEventBus().fireEvent(new OpenReportsEvent(fileGridModel));
					else if(fileGridModel.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.EXTERNAL_URL)){
						AppController.getEventBus().fireEvent(new ShowUrlEvent(fileGridModel));

					}else
						AppController.getEventBus().fireEvent(new FileDownloadEvent(fileGridModel.getIdentifier(), fileGridModel.getName(), DownloadType.SHOW,fileGridModel.isDirectory() || fileGridModel.isVreFolder()));

					}
			}
		});

	    btnAccessFromDesktop.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AppController.getEventBus().fireEvent(new WebDavUrlEvent(null));
			}
		});


		toggleGroup.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (toggleGroup.isPressed()) {
					switchView(ViewSwitchTypeInResult.Group);

				}
				// Window.alert("ViewSwitchTypeInResult.Group");
			}
		});

		toggleList.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (toggleList.isPressed()) {
					switchView(ViewSwitchTypeInResult.List);

				}
				// Window.alert("ViewSwitchTypeInResult.List");
			}
		});

		toggleIcon.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (toggleIcon.isPressed()) {
					switchView(ViewSwitchTypeInResult.Icons);
				}
			}
		});
	}

	/**
	 * Switch view.
	 *
	 * @param type the type
	 */
	private void switchView(ViewSwitchTypeInResult type) {

		// if (type.compareTo(ViewSwitchTypeInResult.Icons) == 0) {
		// this.gridFilterViewContainer.setVisible(false);
		// this.gridGroupViewContainer.setVisible(false);
		// this.iconsViewContainer.setVisible(true);
		//
		// } else{
		// if (type.compareTo(ViewSwitchTypeInResult.List) == 0) {
		//
		// this.iconsViewContainer.setVisible(false);
		// this.gridGroupViewContainer.setVisible(false);
		// this.gridFilterViewContainer.setVisible(true);
		// }
		// else{
		// this.iconsViewContainer.setVisible(false);
		// this.gridFilterViewContainer.setVisible(false);
		// this.gridGroupViewContainer.setVisible(true);
		// }
		// }

	}

	/**
	 * The method return input file model if is directory otherwise parent of file model.
	 *
	 * @param fileModel the file model
	 * @return the directory or parent
	 */
	private FileModel getDirectoryOrParent(FileGridModel fileModel){

		if(fileModel!=null){
			if(fileModel.isDirectory())
				return fileModel;
			else
				return fileModel.getParentFileModel();
		}

		return null;
	}

	/**
	 * Active buttons on select for operation.
	 *
	 * @param target the target
	 * @param active the active
	 */
	public void activeButtonsOnSelectForOperation(FileModel target, boolean active) {

		this.btnPreview.disable();
		this.btnPublicLink.disable();
		this.btnFolderLink.disable();
		this.btnGetSharedLink.disable();
		this.btnOpen.enable();
		this.btnSetPermission.disable();

		//ADDED 07/03/2014
		this.btnAddFolder.enable();
		this.btnUploadArchive.enable();
		this.btnUploadFile.enable();

		if(target!=null){

			if(active & target.getGXTFolderItemType()!=null){

				switch(target.getGXTFolderItemType()){

				case EXTERNAL_IMAGE:
	//				contextMenu.getItemByItemId(ConstantsExplorer.PRW).setVisible(true);
					this.btnPreview.enable();
					this.btnPublicLink.enable();
					break;
				case EXTERNAL_FILE:
					this.btnPublicLink.enable();
					break;
				case EXTERNAL_PDF_FILE:
					this.btnPublicLink.enable();
					break;
				case EXTERNAL_URL:
	//				contextMenu.getItemByItemId(ConstantsExplorer.LNK).setVisible(true);
					break;
				case REPORT_TEMPLATE:
	//				contextMenu.getItemByItemId(ConstantsExplorer.ORT).setVisible(true);
					break;
				case REPORT:
					break;
				case QUERY:
					break;
				case TIME_SERIES:
					break;
				case PDF_DOCUMENT:
					break;
				case IMAGE_DOCUMENT:
					this.btnPreview.enable();
					break;
				case DOCUMENT:
					break;
				case URL_DOCUMENT:
	//				contextMenu.getItemByItemId(ConstantsExplorer.LNK).setVisible(true);
					break;
				case METADATA:
					break;
				case WORKFLOW_REPORT:
					break;
				case WORKFLOW_TEMPLATE:
					break;
				default:

				}
			}

			//HANDLER SharedLink
			if(target.isShared())
				activeButtonForSharing(active);
			else
				activeButtonForSharing(false);

			//IF TARGET IS NOT NULL AND AN ITEM IS COPIED, PASTE IS ENABLING
			if(CutCopyAndPaste.getCopiedIdsFilesModel()!=null){
				this.btnPasteItem.enable();
			}
		}

		//TARGET (SELECTED ITEM) CAN BE NULL, HIS PARENT IS NOT NULL ACTIVING BUTTONS
		activeButtons(active);

		if(target!=null){

			//HANDLING VRE FOLDER AND SPECIAL FOLDER GRID ITEM SELECTION
			if(target.isVreFolder() || target.isSpecialFolder()){

				this.btnRemoveItem.disable();
				this.btnRenameItem.disable();

				if(target.isVreFolder()) //IS VRE -> ENABLING SET PERMISSION
					this.btnSetPermission.enable();

				if(target.isSpecialFolder()){ //IS SPECIAL FOLDER -> DISABLING COPY AND PASTE
					disableButtonSpecialFolderSelected();
				}
				this.btnRefreshFolder.enable();
			}else if(target.isDirectory()){
				this.btnFolderLink.enable();
				this.btnRefreshFolder.enable();
			}

		}


	}


	/**
	 * Active buttons.
	 *
	 * @param active the active
	 */
	private void activeButtons(boolean active){

		if (!active) {
			this.btnRemoveItem.disable();
			this.btnRenameItem.disable();
			this.btnDownloadFile.disable();
			this.btnOpen.disable();
			this.btnCopyItem.disable();
			this.btnCutItem.disable();

			//ADDED 07/03/2104
//			this.btnAddFolder.disable();
//			this.btnUploadArchive.disable();
//			this.btnUploadFile.disable();

//			this.btnGetInfo.disable();
//			this.btnGetSharedLink.disable();
		} else {
			this.btnRemoveItem.enable();
			this.btnRenameItem.enable();
			this.btnDownloadFile.enable();
			this.btnOpen.enable();
			this.btnCopyItem.enable();
			this.btnCutItem.enable();

			//ADDED 07/03/2104
//			this.btnAddFolder.enable();
//			this.btnUploadArchive.enable();
//			this.btnUploadFile.enable();

//			this.btnGetInfo.enable();
		}

	}

	/**
	 * Active button for sharing.
	 *
	 * @param active the active
	 */
	public void activeButtonForSharing(boolean active){

		this.btnGetSharedLink.setEnabled(active);
	}

	/**
	 * Active all button without group view.
	 *
	 * @param active the active
	 */
	public void activeAllButtonWithoutGroupView(boolean active){

			btnRemoveItem.setEnabled(active);
			btnRenameItem.setEnabled(active);
			btnGetSharedLink.setEnabled(active);
//			btnGetInfo.setEnabled(active);
			btnDownloadFile.setEnabled(active);
			btnPreview.setEnabled(active);
			btnOpen.setEnabled(active);
			btnPublicLink.setEnabled(active);
			btnFolderLink.setEnabled(active);
	}

	/**
	 * Active button for multi selection.
	 *
	 * @param active the active
	 */
	public void activeButtonForMultiSelection(boolean active){
		this.btnRenameItem.setEnabled(!active);
		this.btnDownloadFile.setEnabled(!active);
		this.btnOpen.setEnabled(!active);
		this.btnGetSharedLink.setEnabled(!active);
		this.btnPublicLink.setEnabled(!active);
		this.btnFolderLink.setEnabled(!active);
		this.btnSetPermission.setEnabled(!active);

		//ONLY ENABLED
		this.btnRemoveItem.setEnabled(active);
		this.btnCopyItem.setEnabled(active);
		this.btnCutItem.setEnabled(active);
	}

	/**
	 * Gets the tool bar.
	 *
	 * @return the tool bar
	 */
	public ToolBar getToolBar() {
		return this.toolBar;
	}

	/**
	 * Active buttons on search.
	 *
	 * @param b the b
	 */
	public void activeButtonsOnSearch(boolean b) {
		btnRefreshFolder.setEnabled(!b);
	}

	/**
	 * Active paste button.
	 *
	 * @param b the b
	 */
	public void activePasteButton(boolean b){
		btnPasteItem.setEnabled(b);
	}

	/**
	 * Disable button special folder selected.
	 */
	public void disableButtonSpecialFolderSelected(){
		this.btnPasteItem.disable();
		this.btnCopyItem.disable();
		this.btnAddFolder.disable();
		this.btnUploadArchive.disable();
		this.btnUploadFile.disable();
		this.btnPasteItem.disable();
		this.btnDownloadFile.disable();
		this.btnRemoveItem.disable();
		this.btnRenameItem.disable();
	}

	/**
	 * Enable button for active breadcrumb.
	 *
	 * @param parent the parent
	 */
	public void enableButtonForActiveBreadcrumb(FileModel parent){

		if(parent!=null){
			//IF TARGET IS NOT NULL AND AN ITEM IS COPIED, PASTE IS ENABLING
			if(CutCopyAndPaste.getCopiedIdsFilesModel()!=null){
				this.btnPasteItem.enable();
			}

			this.btnCopyItem.enable();
			this.btnAddFolder.enable();
			this.btnUploadArchive.enable();
			this.btnUploadFile.enable();
			this.btnDownloadFile.enable();
		}
	}

}
