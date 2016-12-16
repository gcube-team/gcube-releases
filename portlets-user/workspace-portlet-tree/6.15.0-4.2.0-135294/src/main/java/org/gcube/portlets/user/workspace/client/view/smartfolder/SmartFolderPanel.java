package org.gcube.portlets.user.workspace.client.view.smartfolder;

import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.SmartFolderSelectedEvent;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class SmartFolderPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Massimiliano Assante ISTI-CNR
 * @version 1.0 May 14th 2012
 */

public class SmartFolderPanel extends LayoutContainer {

	private ContentPanel cp;
	private SmartButton buttDocuments = new SmartButton(GXTCategorySmartFolder.SMF_DOCUMENTS.getValue(), Resources.getIconDocuments(), this);
	private SmartButton buttImages = new SmartButton(GXTCategorySmartFolder.SMF_IMAGES.getValue(), Resources.getIconImages(), this);
	private SmartButton buttLinks = new SmartButton(GXTCategorySmartFolder.SMF_LINKS.getValue(), Resources.getIconLinks(), this);
	private SmartButton buttPublicFolder = new SmartButton(GXTCategorySmartFolder.SMF_PUBLIC_FOLDERS.getValue(), Resources.getIconFolderPublic(), this);

	/**
	 * Instantiates a new smart folder panel.
	 */
	public SmartFolderPanel() {
		this.cp = new ContentPanel();
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setScrollMode(Scroll.AUTO);
		addListeners();
		add(cp);
	}

	/**
	 * Reload panel smart folder.
	 */
	public void reloadPanelSmartFolder(){
		cp.removeAll();
		cp.add(buttDocuments);
		cp.add(buttImages);
		cp.add(buttLinks);
		cp.add(buttPublicFolder);
		cp.layout();
		addUserSmartFolder();

	}

	/**
	 * Deselect all.
	 */
	public void deselectAll(){
		for (int i = 0; i < cp.getItemCount(); i++ )
			if (cp.getItem(i) instanceof SmartButton) {
				SmartButton b = (SmartButton) cp.getItem(i);
				b.toggle(false);
			}
	}

	/**
	 * Adds the user smart folder.
	 */
	private void addUserSmartFolder() {
		AppControllerExplorer.rpcWorkspaceService.getAllSmartFolders(new AsyncCallback<List<SmartFolderModel>>() {
			@Override
			public void onSuccess(List<SmartFolderModel> result) {
				loadSmartFolders(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				new MessageBoxAlert("Error", ConstantsExplorer.SERVER_ERROR + " getting User's Smart Folders. " +ConstantsExplorer.TRY_AGAIN, null);
				GWT.log("Error in load smart folder " + caught.getMessage());
			}
		});

	}

	/**
	 * Adds the listeners.
	 */
	private void addListeners() {

		buttImages.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(GXTCategorySmartFolder.SMF_IMAGES));

			}
		});

		buttLinks.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(GXTCategorySmartFolder.SMF_LINKS));
			}
		});


		buttDocuments.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(GXTCategorySmartFolder.SMF_DOCUMENTS));
			}
		});

		buttPublicFolder.setCommand(new Command() {

			@Override
			public void execute() {

				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(GXTCategorySmartFolder.SMF_PUBLIC_FOLDERS));
			}
		});
	}

	/**
	 * Load smart folders.
	 *
	 * @param result the result
	 */
	public void loadSmartFolders(List<SmartFolderModel> result) {
		if (result != null && result.size() > 0) {
			for (SmartFolderModel smart : result) {
				loadSmartFolder(smart);
			}
			cp.layout();
		}
	}


	/**
	 * Load smart folder.
	 *
	 * @param smart the smart
	 */
	public void loadSmartFolder(final SmartFolderModel smart) {

		SmartButton userSmartFolder = new SmartButton(smart.getName(), Resources.getIconStar(), this, true);
		userSmartFolder.setId(smart.getIdentifier());
		userSmartFolder.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(smart.getIdentifier(), smart.getName(), null));
			}
		});
		cp.add(userSmartFolder);
		cp.layout();
	}

	/**
	 * Sets the size smart panel.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setSizeSmartPanel(int width, int height) {
		cp.setSize(width, height);
	}

	/**
	 * Removes the smart folder.
	 *
	 * @param smartIdentifier the smart identifier
	 */
	public void removeSmartFolder(String smartIdentifier) {
		cp.remove(cp.getItemByItemId(smartIdentifier));
		cp.layout(true);
	}

	/**
	 * Un pressed all toogle.
	 */
	public void unPressedAllToogle(){
		deselectAll();
	}

	/**
	 * Toggle others.
	 *
	 * @param button the button
	 */
	public void toggleOthers(SmartButton button){
		for (int i = 0; i < cp.getItemCount(); i++ )
			if (cp.getItem(i) instanceof SmartButton) {
				SmartButton b = (SmartButton) cp.getItem(i);
				if (! b.equals(button))
					b.toggle(false);
			}
	}
}
