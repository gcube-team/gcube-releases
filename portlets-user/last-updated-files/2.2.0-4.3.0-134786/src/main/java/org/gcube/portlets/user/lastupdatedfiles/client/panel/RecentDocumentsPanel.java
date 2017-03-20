package org.gcube.portlets.user.lastupdatedfiles.client.panel;

import java.util.ArrayList;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portlets.user.lastupdatedfiles.client.FileService;
import org.gcube.portlets.user.lastupdatedfiles.client.FileServiceAsync;
import org.gcube.portlets.user.lastupdatedfiles.client.panel.ui.FileItem;
import org.gcube.portlets.user.lastupdatedfiles.shared.FileItemsWrapper;
import org.gcube.portlets.user.lastupdatedfiles.shared.LufFileItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class RecentDocumentsPanel extends Composite {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final FileServiceAsync fileService = GWT.create(FileService.class);

	private static final String loading = GWT.getModuleBaseURL() + "../images/loader.gif";
	
	
	
	private Image loadingImage;
	private VerticalPanel containerPanel = new VerticalPanel();
	private VerticalPanel mainPanel = new VerticalPanel();

	public RecentDocumentsPanel() {
		loadingImage = new Image(loading);
		containerPanel.add(mainPanel);
		initWidget(containerPanel);
		containerPanel.setStyleName("luf-frame");
		showLoader();
		fileService.getWorkspaceFolderURL(new AsyncCallback<String>() {			
			@Override
			public void onFailure(Throwable caught) {
				showServError();				
			}
			@Override
			public void onSuccess(String result) {
				HTML showAll = new HTML("<a class=\"luflink\" href=\""+result+"\">Show all ...</a>");
				showAll.setStyleName("showAll-row");
				containerPanel.add(showAll);
				fetchRecentDocuments();
			}
		});
	}
	
	private void fetchRecentDocuments() {
		fileService.getLastUpdateFiles(new AsyncCallback<FileItemsWrapper>() {
			@Override
			public void onSuccess(FileItemsWrapper result) {
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);				
				ArrayList<LufFileItem> files = new ArrayList<LufFileItem>();
				if (result == null || result.getItems() == null) {
					showServError();
				}
				else {
					files = result.getItems();
					if (! result.isInfrastructure()) {
						String vreName = result.getFolderName();
						if (vreName.compareTo("BlueBridgeProject")==0)
							vreName = "BlueBRIDGEProject";
						if (vreName.length() > 31)
							vreName = vreName.substring(0, 27) + "...";
					}			
					
					if (files != null) {
						for (LufFileItem item : files) {
							FileItem toAdd = new FileItem(fileService, item);
							toAdd.addStyleName("luf-item-row");
							mainPanel.add(toAdd);
						}
					}					
				}			
			}
			@Override
			public void onFailure(Throwable caught) {	
			}
		});
	}

	private void showLoader() {
		mainPanel.clear();
		mainPanel.setWidth("100%");
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.add(loadingImage);
	}

	private void showConnError() {
		mainPanel.clear();
		containerPanel.setStyleName("luf-frame");
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Sorry, looks like something is broken with the server connection<br> " +
				"Please check your connection and try refresh this page.</div>"));
	}

	private void showServError() {
		mainPanel.clear();
		containerPanel.setStyleName("luf-frame");
		mainPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Sorry, it seems something is wrong with this folder.<br><br> " +
				"Please, report this issue.</div>"));
	}
}
