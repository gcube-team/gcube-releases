/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.ResourceItem;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class ResourcesExporter {

	// public static void exportTable(TableItemSimple tableItem) {
	// exportResource(tableItem.getId(), tableItem.getName());
	// }
	//
	public static void exportResource(final ResourceItem resourceItem) {
		final String fName = resourceItem.getName()
				+ (resourceItem.isTable() ? ".csv" : "");
		//
		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.FOLDER);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		final WorkspaceExplorerSaveDialog wsaveDialog = new WorkspaceExplorerSaveDialog(
				"Select the location to save the resource", fName,
				showableTypes);

		WorskpaceExplorerSaveNotificationListener handler = new WorskpaceExplorerSaveNotificationListener() {

			@Override
			public void onSaving(Item destinationFolder, String fileName) {
				Timer timer = new Timer() {

					@Override
					public void run() {
						if (wsaveDialog != null) {
							wsaveDialog.hide();
						}

					}
				};
				timer.schedule(500);

				StatisticalManager.getService().exportResource(
						destinationFolder.getId(), fileName, resourceItem,
						new AsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								// EventBusProvider.getInstance().fireEvent(
								// new MaskEvent(null));
								MessageBox
										.info("File saved",
												"The file \""
														+ result
														+ "\" has been exported in the workspace.",
												null);
							}

							@Override
							public void onFailure(Throwable caught) {

								// EventBusProvider.getInstance().fireEvent(
								// new MaskEvent(null));
								MessageBox.alert(
										"Error",
										"Impossible to export the file into the Workspace<br/>Cause: "
												+ caught.getCause()
												+ "<br/>Message: "
												+ caught.getMessage(), null);
							}
						});

			}

			@Override
			public void onFailed(Throwable throwable) {
				throwable.printStackTrace();
				Timer timer = new Timer() {
					@Override
					public void run() {
						if (wsaveDialog != null) {
							wsaveDialog.hide();
						}
					}
				};
				timer.schedule(500);
				MessageBox.alert("Error",
						"Impossible select destination into the Workspace<br/>Cause: "
								+ throwable.getCause() + "<br/>Message: "
								+ throwable.getMessage(), null);

			}

			@Override
			public void onAborted() {
				Timer timer = new Timer() {
					@Override
					public void run() {
						if (wsaveDialog != null) {
							wsaveDialog.hide();
						}
					}
				};
				timer.schedule(500);
			}
		};

		wsaveDialog.addWorkspaceExplorerSaveNotificationListener(handler);
		wsaveDialog.setZIndex(XDOM.getTopZIndex());
		wsaveDialog.show();

	}

}
