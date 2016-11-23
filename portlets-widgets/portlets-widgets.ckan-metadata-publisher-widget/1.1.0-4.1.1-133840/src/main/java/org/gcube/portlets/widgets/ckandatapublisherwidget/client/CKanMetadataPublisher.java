package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.CreateDatasetForm;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.MetaDataFieldSkeleton;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DataType;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CKanMetadataPublisher implements EntryPoint {

	private HandlerManager eventBus = new HandlerManager(null);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// remove comment to the below line for testing the widget
		//startExample();
		//		testMetadata();

	}

	@SuppressWarnings("unused")
	private void testMetadata() {

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("95%");
		RootPanel.get("ckan-metadata-publisher-div").add(panel);

		// prepare the data
		List<MetadataFieldWrapper> fields = new ArrayList<MetadataFieldWrapper>();

		// checkbox
		//fields.add(new MetadataFieldWrapper("CheckBox", true, DataType.Boolean, "false", "Checkbox example", null, null));

		// number
		//fields.add(new MetadataFieldWrapper("Number", true, DataType.Number, "52", "Number example", null, null));

		// other number
		//fields.add(new MetadataFieldWrapper("Number2", true, DataType.Number, null, "Number example 2", null, "[0-9]+"));

		// other number
		//fields.add(new MetadataFieldWrapper("Number3", false, DataType.Number, null, "Number example 3", null, "[0-9]+"));

		// text area
		//fields.add(new MetadataFieldWrapper("Text Area", false, DataType.Text, "This is the default value", "Text Area", null, "^#\\w+\\.$"));

		// textbox
		//		fields.add(new MetadataFieldWrapper("TextBox", true, DataType.String, null, "TextBox", null, "^#\\w+\\.$"));

		// listbox
		//		fields.add(new MetadataFieldWrapper("ListBox", true, DataType.String, "prova4", "ListBox", Arrays.asList("prova1", "prova2", "prova3"), null));

		// single date
		//		fields.add(new MetadataFieldWrapper("Single date", true, DataType.Time, null, "Single date", null, null));

		// single date
		//		fields.add(new MetadataFieldWrapper("Single date 2", true, DataType.Time, "2045-12-01 21:32", "Single date 2", null, null));

		// time interval
		//		fields.add(new MetadataFieldWrapper("Time interval 1", false, DataType.Time_Interval, "2045-12-01 21:32/2045-12-01 21:32", "Time interval 1", null, null));

		// time interval
		//		fields.add(new MetadataFieldWrapper("Time interval 2", true, DataType.Time_Interval, null, "Time interval 2", null, null));

		// time interval
		//		fields.add(new MetadataFieldWrapper("Time intervals 2", true, DataType.Time_Interval, null, "Time intervals 2", null, null));

		// time interval lists
		fields.add(new MetadataFieldWrapper("Time intervals 2", false, DataType.Times_ListOf, "2010-10-12 15:23", "Time intervals 2", null, null));

		final ArrayList<MetaDataFieldSkeleton> widgetsList = new ArrayList<MetaDataFieldSkeleton>();
		for (MetadataFieldWrapper metadataFieldWrapper : fields) {
			MetaDataFieldSkeleton widget;
			try {
				widget = new MetaDataFieldSkeleton(metadataFieldWrapper, eventBus);
				widgetsList.add(widget);
				panel.add(widget);
			} catch (Exception e) {
				GWT.log("Error!", e);
			}
		}

		Button validator = new Button("Validate");
		panel.add(validator);

		validator.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				for (MetaDataFieldSkeleton field : widgetsList) {
					String error = field.isFieldValueValid();					
					if(error != null)
						Window.alert(field.getFieldName() + " is not valid. Suggestion: " + error);
					else
						Window.alert("No ERROR: " + field.getFieldCurrentValue());

				}
			}
		});

		Button freezeAll = new Button("Freeze");
		panel.add(freezeAll);

		freezeAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				for (MetaDataFieldSkeleton field : widgetsList)
					field.freeze();
			}	
		});

	}


	@SuppressWarnings("unused")
	private void startExample() {

		String idFolderWorkspace = "d3a37eb9-1589-4c95-a9d0-c473a02d4f0f";
		String owner = "costantino.perciante";	
		RootPanel.get("ckan-metadata-publisher-div").add(new CreateDatasetForm(idFolderWorkspace, eventBus));

	}
}
