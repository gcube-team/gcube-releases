package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.client.util.Resources;
import org.gcube.portlets.admin.software_upload_wizard.shared.Url;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class PortTypePanel extends ContentPanel {

	private ListStore<UrlModel> store = new ListStore<UrlModel>();
	private final CheckBoxSelectionModel<UrlModel> sm = new CheckBoxSelectionModel<UrlModel>();
	EditorGrid<UrlModel> editorGrid;

	private Button addButton = new Button("Add",
			AbstractImagePrototype.create(Resources.INSTANCE.addIcon()));
	private Button removeButton = new Button("Remove",
			AbstractImagePrototype.create(Resources.INSTANCE.deleteIcon()));

	public PortTypePanel() {
		FormData formData = new FormData("100%");

		this.setLayout(new FitLayout());
		this.setHeading("URLs");
		this.setHeight(200);

		// Url column
		ColumnConfig urlColumn = new ColumnConfig(UrlModel.URL_CODE, "URL",
				150);
		TextField<String> urlText = new TextField<String>();
		urlText.setAllowBlank(false);
		// TODO ask for regex
		// urlText.setRegex("https?://.*");
		urlText.setEmptyText("e.g. http://www.myappwiki.com/");
		urlColumn.setEditor(new CellEditor(urlText));

		// Url description column
		ColumnConfig descriptionColumn = new ColumnConfig(
				UrlModel.DESCRIPTION_CODE, "Description", 150);
		final SimpleComboBox<String> descriptionComboBox = new SimpleComboBox<String>();
		descriptionComboBox.setForceSelection(false);
		descriptionComboBox.setTriggerAction(TriggerAction.ALL);
		descriptionComboBox.setAllowBlank(false);
		descriptionComboBox.add(Url.DEFAULT_DESCRIPTIONS);
		descriptionComboBox.setEditable(true);
		// descCombo.setValue(descCombo.getStore().getAt(0));
		descriptionColumn.setEditor(new CellEditor(descriptionComboBox) {
			@Override
			public Object preProcessValue(Object value) {
				if (value == null)
					return value;
				return descriptionComboBox.findModel(value.toString());
			}

			@Override
			public Object postProcessValue(Object value) {
				if (value == null)
					return value;
				return ((ModelData) value).get("value");
			}
		});

		ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(sm.getColumn());
		columns.add(urlColumn);
		columns.add(descriptionColumn);

		ColumnModel columnModel = new ColumnModel(columns);

		editorGrid = new EditorGrid<UrlModel>(store, columnModel);
		editorGrid.setSelectionModel(sm);
		editorGrid.setAutoExpandColumn(UrlModel.URL_CODE);

		editorGrid.addPlugin(sm);
		editorGrid.getView().setShowDirtyCells(false);

		this.add(editorGrid, formData);

		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.RIGHT);

		toolBar.add(addButton);
		toolBar.add(removeButton);

		this.setBottomComponent(toolBar);

		bind();
	}

	public boolean isValid() {
		if (store.getCount() == 0)
			return false;
		for (UrlModel m : store.getModels()) {
			if (m.getUrl() == null || m.getUrl().isEmpty())
				return false;
			if (m.getDescription() == null || m.getDescription().isEmpty())
				return false;
		}
		return true;
	}

	private void bind() {
		addButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						UrlModel entryPoint = new UrlModel("http://",
								Url.DEFAULT_DESCRIPTIONS.get(0));
						PortTypePanel.this.editorGrid.stopEditing(true);
						store.insert(entryPoint, 0);
						PortTypePanel.this.editorGrid.startEditing(
								store.indexOf(entryPoint), 1);
					}
				});

		removeButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						for (UrlModel entryPoint : sm.getSelectedItems())
							store.remove(entryPoint);
					}
				});
	}

	public ArrayList<Url> getUrls() {
		ArrayList<Url> urls = new ArrayList<Url>();
		for (UrlModel model : store.getModels()) {
			urls.add(new Url(model.getUrl(), model.getDescription()));
		}
		return urls;
	}

	public void setUrls(ArrayList<Url> urls) {
		store.removeAll();
		for (Url url : urls) {
			store.add(new UrlModel(url.getUrl(), url.getUrlDescription()));
		}
		store.commitChanges();
	}

	private class UrlModel extends BaseModelData {

		public final static String URL_CODE = "URL";
		public final static String DESCRIPTION_CODE = "DESCRIPTION";

		public UrlModel(String url, String description) {
			set(URL_CODE, url);
			set(DESCRIPTION_CODE, description);
		}

		public String getUrl() {
			return get(URL_CODE);
		}

		public String getDescription() {
			return get(DESCRIPTION_CODE);
		}

	}
}
