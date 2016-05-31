package org.gcube.portlets.user.workspace.client.view.smartfolder;

import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.event.SmartFolderSelectedEvent;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategoryItemInterface;
import org.gcube.portlets.user.workspace.client.model.SmartFolderModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.windows.MessageBoxAlert;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 May 14th 2012
 */

public class SmartFolderPanel extends LayoutContainer {

	private ContentPanel cp;

	private SmartButton buttDocuments = new SmartButton(GXTCategoryItemInterface.SMF_DOCUMENTS, Resources.getIconDocuments(), this);
	
	private SmartButton buttImages = new SmartButton(GXTCategoryItemInterface.SMF_IMAGES, Resources.getIconImages(), this);
	private SmartButton buttLinks = new SmartButton(GXTCategoryItemInterface.SMF_LINKS, Resources.getIconLinks(), this);
//	private SmartButton buttReports = new SmartButton(GXTCategoryItemInterface.SMF_REPORTS, Resources.getIconReport(), this);
//	private SmartButton buttTimeSeries = new SmartButton(GXTCategoryItemInterface.SMF_TIMESERIES, Resources.getIconTimeSeries(), this);

	private HashMap<String, SmartFolderModel> hashSmartFolderModel = new HashMap<String, SmartFolderModel>();
	private HashMap<String, HorizontalPanel> hashMapPanelSmartFolder = new HashMap<String, HorizontalPanel>();

	public SmartFolderPanel() {
		this.cp = new ContentPanel();
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		cp.setScrollMode(Scroll.AUTO);
		addListeners();
		add(cp);
	}

	public void reloadPanelSmartFolder(){
		cp.removeAll();

		cp.add(buttDocuments);
		
		cp.add(buttImages);
		cp.add(buttLinks);
//		cp.add(buttReports);
//		cp.add(buttTimeSeries);
		cp.layout();
		addUserSmartFolder();

	}

	public void deselectAll(){		
		for (int i = 0; i < cp.getItemCount(); i++ ) 
			if (cp.getItem(i) instanceof SmartButton) {
				SmartButton b = (SmartButton) cp.getItem(i);		
				b.toggle(false);
			}
	}

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

	private void addListeners() {

		buttImages.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(null, GXTCategoryItemInterface.SMF_IMAGES, GXTCategoryItemInterface.SMF_IMAGES));

			}
		});

		buttLinks.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(null, GXTCategoryItemInterface.SMF_LINKS, GXTCategoryItemInterface.SMF_LINKS));
			}				
		});


		buttDocuments.setCommand(new Command() {
			@Override
			public void execute() {
				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(null, GXTCategoryItemInterface.SMF_DOCUMENTS, GXTCategoryItemInterface.SMF_DOCUMENTS));
			}
		});

//		buttReports.setCommand(new Command() {
//			@Override
//			public void execute() {
//				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(null, GXTCategoryItemInterface.SMF_REPORTS, GXTCategoryItemInterface.SMF_REPORTS));
//			}
//		});
//
//		buttTimeSeries.setCommand(new Command() {
//			@Override
//			public void execute() {
//				AppControllerExplorer.getEventBus().fireEvent(new SmartFolderSelectedEvent(null, GXTCategoryItemInterface.SMF_TIMESERIES, GXTCategoryItemInterface.SMF_TIMESERIES));
//			}
//		});
	}

	/**
	 * 
	 * @param result
	 */
	public void loadSmartFolders(List<SmartFolderModel> result) {
		if (result != null && result.size() > 0) {
			for (SmartFolderModel smart : result) {
				loadSmartFolder(smart);
			}
			cp.layout();
		}
	}

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

	public void setSizeSmartPanel(int width, int height) {
		cp.setSize(width, height);	
	}

	public void removeSmartFolder(String smartIdentifier) {		
		cp.remove(cp.getItemByItemId(smartIdentifier));
		cp.layout(true);
	}

	public void unPressedAllToogle(){
		deselectAll();
	}

	public void toggleOthers(SmartButton button){		
		for (int i = 0; i < cp.getItemCount(); i++ ) 
			if (cp.getItem(i) instanceof SmartButton) {
				SmartButton b = (SmartButton) cp.getItem(i);	
				if (! b.equals(button))
					b.toggle(false);
			}
	}
}
