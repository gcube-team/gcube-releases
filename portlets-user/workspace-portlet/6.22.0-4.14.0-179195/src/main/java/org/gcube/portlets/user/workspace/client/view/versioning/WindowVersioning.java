package org.gcube.portlets.user.workspace.client.view.versioning;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.UpdateWorkspaceSizeEvent;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.FileVersionModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.shared.WorkspaceVersioningOperation;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;


/**
 * The Class WindowVersioning.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 17, 2017
 */
public class WindowVersioning extends Window {

	private List<FileVersionModel> versionedFiles;
	private VersioningInfoContainer versioningContainer;
	private HorizontalPanel hpItemsNumber;
	private Label labelItemsNumber = new Label();
	private FileModel fileVersioned;


	/**
	 * Instantiates a new window versioning.
	 *
	 * @param fileVersioned the file versioned
	 */
	public WindowVersioning(FileModel fileVersioned) {
		this.fileVersioned = fileVersioned;
		initAccounting();
	    //setIcon(Resources.getTrashFull()); //TODO
	    setHeading("Versions of: "+fileVersioned.getName());
	}

	/**
	 * This method is called only once.
	 */
	private void initAccounting() {
		setLayout(new FitLayout());
		setSize(770, 400);
		setResizable(true);
		setMaximizable(true);
		this.versioningContainer = new VersioningInfoContainer(fileVersioned, this);
		add(versioningContainer);

		ToolBar toolBar = new ToolBar();
		hpItemsNumber = new HorizontalPanel();
		hpItemsNumber.setStyleAttribute("margin-left", "10px");
		hpItemsNumber.setHorizontalAlign(HorizontalAlignment.CENTER);
		hpItemsNumber.add(labelItemsNumber);
		toolBar.add(hpItemsNumber);

		setBottomComponent(toolBar);
		addStoreListeners();
	}


	/**
	 * Adds the store listeners.
	 */
	private void addStoreListeners() {

		versioningContainer.getStore().addListener(Store.Add,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});

		versioningContainer.getStore().addListener(Store.Remove,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});

		versioningContainer.getStore().addListener(Store.Clear,  new Listener<StoreEvent<ModelData>>(){

			@Override
			public void handleEvent(StoreEvent<ModelData> be) {
				updateItemsNumber(storeSize());
				AppController.getEventBus().fireEvent(new UpdateWorkspaceSizeEvent());
			}
		});

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
	 * Sets the window title.
	 *
	 * @param title the new window title
	 */
	public void setWindowTitle(String title) {
		this.setHeading(title);

	}

	/**
	 * Store size.
	 *
	 * @return -1 if store is null. The size otherwise
	 */
	private int storeSize(){

		if(versioningContainer.getStore()!=null && versioningContainer.getStore().getModels()!=null){
			return versioningContainer.getStore().getModels().size();
		}

		return -1;

	}


	/**
	 * Delete file from versioning.
	 *
	 * @param fileModelId the file model id
	 * @return true, if successful
	 */
	public boolean deleteFileFromVersioning(String fileModelId){
		boolean deleted = this.versioningContainer.deleteItem(fileModelId);
		return deleted;
	}


	/**
	 * Update versioning container.
	 *
	 * @param versioningFiles the versioning files
	 */
	public void updateVersioningContainer(List<FileVersionModel> versioningFiles) {

		this.versioningContainer.resetStore();
		this.versionedFiles = versioningFiles;
		this.versioningContainer.updateVersions(versioningFiles);
	}

	/**
	 * Execute operation on trash container.
	 *
	 * @param trashIds the trash ids
	 * @param operation the operation
	 */
	public void executeOperationOnVersioningContainer(List<String> trashIds, WorkspaceVersioningOperation operation) {

		if(operation.equals(WorkspaceVersioningOperation.DELETE_PERMANENTLY)){
			this.mask("Deleting");
			deleteListItems(trashIds);

		}
//		else if(operation.equals(WorkspaceVersioningOperation.RESTORE)){
//			this.mask("Restoring");
//			deleteListItems(trashIds);
//		}

		this.unmask();
	}


	/**
	 * Gets the versions.
	 *
	 * @return the versions
	 */
	public List<FileVersionModel> getVersions() {
		return versionedFiles;
	}

	/**
	 * Delete list items.
	 *
	 * @param trashIds the trash ids
	 */
	private void deleteListItems(List<String> trashIds){

		for (String identifier : trashIds) {
			this.versioningContainer.deleteItem(identifier);
		}

	}

	/**
	 * Mask container.
	 *
	 * @param title the title
	 */
	public void maskContainer(String title){
		this.versioningContainer.mask(title, ConstantsExplorer.LOADINGSTYLE);
	}

	/**
	 * Unmask container.
	 */
	public void unmaskContainer(){
		this.versioningContainer.unmask();
	}

	/**
	 * Show versioning error.
	 *
	 * @param operation the operation
	 * @param errors the errors
	 */
	public void showVersioningError(WorkspaceVersioningOperation operation, List<FileModel> errors){

		if(errors!=null && errors.size()>0){

			List<String> fileNames = new ArrayList<String>(errors.size());

			//BUILDING NAMES
			for (FileModel fileVersionModel : errors) {

				FileModel trashFile = versioningContainer.getFileModelByIdentifier(fileVersionModel.getIdentifier());
				fileNames.add(trashFile.getName());
			}

			String htmlError = "<div style=\"padding:10px 10px 10px 10px\"><b style=\"font-size:12px\">Sorry an error occured on removing the ";
			htmlError+=fileNames.size()>1?"items":"item";
			htmlError+=": </b><br/>";

			for (String fileName : fileNames) {
				htmlError+="<br/> - "+fileName;
			}

			htmlError+="<br/><br/><b>"+"Try again later</b></div>";

			Dialog dialog = new Dialog();
			dialog.setStyleAttribute("background-color", "#FAFAFA");
			dialog.setSize(380, 180);
			dialog.setLayout(new FitLayout());
			dialog.setIcon(Resources.getIconInfo());
			dialog.setModal(true);
			dialog.setHeading("Trash Errors");
			dialog.setButtons(Dialog.OK);
			dialog.setHideOnButtonClick(true);
			dialog.setButtonAlign(HorizontalAlignment.CENTER);
//			dialog.setSize(200, 150);

			ContentPanel cp = new ContentPanel();
			cp.setHeaderVisible(false);
			cp.setFrame(false);
			cp.setScrollMode(Scroll.AUTOY);
			cp.add(new Html(htmlError));
			dialog.add(cp);
			dialog.show();

		}
	}


}
