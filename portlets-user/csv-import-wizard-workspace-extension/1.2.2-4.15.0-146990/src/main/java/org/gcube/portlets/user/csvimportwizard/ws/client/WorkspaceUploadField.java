/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.ws.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class WorkspaceUploadField extends MultiField<String> {

	protected TextField<String> fileUploadField;
	protected Button browseButton;
	protected String workspaceItemId;

	public WorkspaceUploadField(String fieldLabel)
	{
		super();
		setFieldLabel(fieldLabel);

		fileUploadField = new TextField<String>();
		fileUploadField.setReadOnly(true);
		fileUploadField.setAllowBlank(false);
		fileUploadField.setToolTip("Select the csv file to import from your workspace");
		fileUploadField.setEmptyText("a CSV file...");

		add(fileUploadField);

		browseButton = new Button("Browse");
		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				showSelection();
			}
		});
		add(new AdapterField(browseButton));
		
		setSpacing(3);
		setResizeFields(false);
	}
	
	protected void showSelection() {
		List<ItemType> selectableTypes = new ArrayList<ItemType>();
		selectableTypes.add(ItemType.EXTERNAL_FILE);
		List<ItemType> showableTypes = new ArrayList<ItemType>();
		showableTypes.addAll(Arrays.asList(ItemType.values()));

		List<String> allowedMimeTypes = Arrays.asList("text/csv",
				"application/zip", "application/x-zip",
				"application/x-zip-compressed", "application/octet-stream",
				"application/x-compress", "application/x-compressed",
				"multipart/x-zip");
		List<String> allowedFileExtensions = Arrays.asList("csv", "zip");

		FilterCriteria filterCriteria = new FilterCriteria(allowedMimeTypes,
				allowedFileExtensions, new HashMap<String, String>());

		WorkspaceExplorerSelectDialog wselectDialog = new WorkspaceExplorerSelectDialog(
				"Select a file", filterCriteria, selectableTypes);

		WorskpaceExplorerSelectNotificationListener handler = new WorskpaceExplorerSelectNotificationListener() {

			@Override
			public void onSelectedItem(Item item) {
				if (item.getType() == ItemType.EXTERNAL_FILE) {
					String filename = item.getName();
					if (filename != null && !filename.isEmpty()) {
						fileUploadField.setValue(filename);
						workspaceItemId = item.getId();
					} else {
						fileUploadField.setValue("");
						workspaceItemId = null;
					}

				} else {
					fileUploadField.setValue("");
					workspaceItemId = null;
				}

			}

			@Override
			public void onFailed(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onAborted() {

			}

			@Override
			public void onNotValidSelection() {
				fileUploadField.setValue("");
				workspaceItemId = null;
			}
		};

		wselectDialog.addWorkspaceExplorerSelectNotificationListener(handler);
		wselectDialog.setZIndex(XDOM.getTopZIndex());
		wselectDialog.show();

	}

	/**
	 * @return the fileUploadField
	 */
	public TextField<String> getFileUploadField() {
		return fileUploadField;
	}

	/**
	 * @return the browseButton
	 */
	public Button getBrowseButton() {
		return browseButton;
	}

	/**
	 * @return the workspaceItemId
	 */
	public String getWorkspaceItemId() {
		return workspaceItemId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);

		if (orientation == Orientation.HORIZONTAL) {
			if (!GXT.isBorderBox) {
				width -= ((fields.size() - 1) * spacing);
			}
			int fileUploadFieldWidth = width - browseButton.getWidth() - spacing;
			fileUploadField.setWidth(fileUploadFieldWidth);
		} else {
			for (Field<?> f : fields) {
				f.setWidth(width);
			}
		}

		if (GXT.isIE) {
			el().repaint();
		}
	}
}
