package org.gcube.portlets.admin.software_upload_wizard.client.view.widget;

import java.util.ArrayList;
import java.util.Date;

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
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ReadmeInfoFieldSet extends GenericComponentInfoFieldSet {

	private ReleaseDateField releaseDateField = new ReleaseDateField();
	private UrlPanel urlPanel = new UrlPanel();

	public ReadmeInfoFieldSet() {
		super("Software");
	
		//initToolTip();

		nameField.setRegex("[a-zA-Z0-9-.]+");
		nameField.getMessages().setRegexText(
				"Only alphanumerical charaters, \"-\" and \".\" symbol are allowed (no space or special chars)");

		this.add(releaseDateField);
		this.add(urlPanel);
	}
	
	private void initToolTip(){
		ToolTipConfig fieldSetToolTip = new DefaultTooltipConfig();
	    fieldSetToolTip.setTitle("General software data");  
	    String toolTipText = "<p>All fields are mandatory if not otherwise said.</p>" +
	    		"<ul>" +
	    		"<li><b>Software name</b>: Software descriptive name" +
	    		"<li><b>Software description</b> (optional): Long software description" +
	    		"<li><b>Software version</b>: Software version" +
	    		"<li><b>Software release date</b>: Uploaded software release date (defaults to today's date)" +
	    		"<li><b>Software URLs</b> (optional): URLs that points to software deliverables. For any given URL the user must select a descriptive tag from the combobox." +
	    		"</ul>";
	    
	    fieldSetToolTip.setText(toolTipText);
	    this.setToolTip(fieldSetToolTip);
	}

	public Date getReleaseDate() {
		return releaseDateField.getValue();
	}

	public void setReleaseDate(Date releaseDate) {
		releaseDateField.setValue(releaseDate);

	}

	public ArrayList<Url> getUrls() {
		return urlPanel.getUrls();
	}

	public void setUrls(ArrayList<Url> urls) {
		urlPanel.setUrls(urls);
	}

	public boolean isValid() {
		return urlPanel.isValid();
	}

	public class UrlPanel extends ContentPanel {

		private ListStore<UrlModel> store = new ListStore<UrlModel>();
		private final CheckBoxSelectionModel<UrlModel> sm = new CheckBoxSelectionModel<UrlModel>();
		EditorGrid<UrlModel> urlGrid;

		private Button addButton = new Button("Add",
				AbstractImagePrototype.create(Resources.INSTANCE.addIcon()));
		private Button removeButton = new Button("Remove",
				AbstractImagePrototype.create(Resources.INSTANCE.deleteIcon()));

		public UrlPanel() {
			FormData formData = new FormData("100%");

			this.setLayout(new FitLayout());
			this.setHeading("URLs");
			this.setHeight(250);

			// Url column
			ColumnConfig urlColumn = new ColumnConfig(UrlModel.URL_CODE, "URL",
					150);
			TextField<String> urlText = new TextField<String>();
			urlText.setAllowBlank(false);
			// TODO Ask for regex
			urlText.setRegex("^https?://.*");
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

			urlGrid = new EditorGrid<UrlModel>(store, columnModel);
			urlGrid.setSelectionModel(sm);
			urlGrid.setAutoExpandColumn(UrlModel.URL_CODE);

			urlGrid.addPlugin(sm);
			urlGrid.getView().setShowDirtyCells(false);

			this.add(urlGrid, formData);

			ToolBar toolBar = new ToolBar();
			toolBar.setAlignment(HorizontalAlignment.RIGHT);

			toolBar.add(addButton);
			toolBar.add(removeButton);

			this.setBottomComponent(toolBar);

			bind();
		}

		public boolean isValid() {
			return true;
		}

		private void bind() {
			addButton
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							UrlModel entryPoint = new UrlModel("http://",
									Url.DEFAULT_DESCRIPTIONS.get(0));
							UrlPanel.this.urlGrid.stopEditing(true);
							store.insert(entryPoint, 0);
							UrlPanel.this.urlGrid.startEditing(
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

}
