/**
 * 
 */
package org.gcube.portlets.user.workspace.client.view.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class DialogEditProperties.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Jun 11, 2015
 */
public class DialogEditProperties extends Dialog {

	private FileModel item;
	private int widthDialog = 450;
	private int heigthDialog = 300;
	private Command commad;
	private List<TextField<String>> fields;
	private FormLayout layout;

	/**
	 * Instantiates a new dialog edit properties.
	 *
	 * @param item
	 *            FileModel
	 * @param command
	 *            Command
	 */
	public DialogEditProperties(FileModel item, Command command) {
		this.item = item;
		this.commad = command;

		layout = new FormLayout();
		layout.setLabelWidth(90);
		layout.setDefaultWidth(300);
		setLayout(layout);

		setHeading("Edit Properties: " + item.getName());
		setButtonAlign(HorizontalAlignment.RIGHT);
		setModal(true);
		// setBodyBorder(true);
		setBodyStyle("padding: 9px; background: none");
		setWidth(widthDialog);
		setHeight(heigthDialog);
		setScrollMode(Scroll.AUTOY);
		setResizable(false);
		setButtons(Dialog.OK);
		getButtonById(Dialog.OK).setText("Save");
		getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				saveProperties(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extjs.gxt.ui.client.widget.Window#show()
	 */
	@Override
	public void show() {
		resetForm();
		loadGcubeItemProperties();
		super.show();
	}

	/**
	 * 
	 */
	private void resetForm() {
		if (fields != null)
			fields.clear();
		fields = null;
		removeAll();
	}

	private void saveProperties(final boolean closeOnSuccess) {
		Map<String, String> prp = new HashMap<String, String>(fields.size());
		for (TextField<String> field : fields) {
			String value = field.getValue() != null ? field.getValue() : "";
			prp.put(field.getFieldLabel(), value);
		}
		AppControllerExplorer.rpcWorkspaceService.setGcubeItemProperties(item.getIdentifier(), prp,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", caught.getMessage(), null);

					}

					@Override
					public void onSuccess(Void result) {
						commad.execute();
						if (closeOnSuccess)
							DialogEditProperties.this.hide();
					}
				});

	}

	private void loadGcubeItemProperties() {
		// mask("Loading properties...");
		AppControllerExplorer.rpcWorkspaceService.loadGcubeItemProperties(item.getIdentifier(),
				new AsyncCallback<Map<String, String>>() {

					@Override
					public void onSuccess(Map<String, String> result) {
						// unmask();
						setProperties(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						// unmask();
						GWT.log("an error occured in loadGcubeItemProperties " + item + " " + caught.getMessage());
					}
				});
	}

	private void setProperties(Map<String, String> result) {
		fields = new ArrayList<TextField<String>>(result.size());
		for (String key : result.keySet()) {
			TextField<String> field = new TextField<String>();
			field.setFieldLabel(key);
			field.setValue(result.get(key));
			add(field);
			fields.add(field);
		}
		layout(true);
	}

}
