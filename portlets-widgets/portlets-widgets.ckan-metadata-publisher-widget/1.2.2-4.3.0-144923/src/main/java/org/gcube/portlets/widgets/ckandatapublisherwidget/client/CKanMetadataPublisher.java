package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.CreateDatasetForm;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.MetaDataFieldSkeleton;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DataType;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.SelectElement;
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

	// Create a remote service proxy to talk to the server-side ckan service.
	public static final CKanPublisherServiceAsync ckanServices = GWT.create(CKanPublisherService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// remove comment to the below line for testing the widget
		// startExample();
		// testMetadata();
		// testSelectionPanel();
		// testHideOption();
	}

	@SuppressWarnings("unused")
	private void testHideOption() {
		
		ListBox listBox = new ListBox(true);
		listBox.addItem("A");
		listBox.addItem("B");
		listBox.addItem("C");
		listBox.addItem("D");
		listBox.addItem("E");
		listBox.addItem("F");
		
		List<String> toHide = new ArrayList<String>();
		toHide.add("A");
		toHide.add("D");
		
		RootPanel.get("ckan-metadata-publisher-div").add(listBox);
		SelectElement se = listBox.getElement().cast();
		
		// hide
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if(toHide.contains(listBox.getItemText(i))){
				GWT.log("to hide " + listBox.getItemText(i));
				se.getOptions().getItem(i).getStyle().setProperty("display", "none");
			}
		}
		
	}

	@SuppressWarnings("unused")
	private void testSelectionPanel() {

		//		List<ResourceElementBean> listLeft = new ArrayList<ResourceElementBean>();
		//		listLeft.add(new ResourceElementBean(null, "File A", false, null, "File A"));
		//		listLeft.add(new ResourceElementBean(null, "File B", false, null, "File B"));
		//		listLeft.add(new ResourceElementBean(null, "File C", false, null, "File C"));
		//		listLeft.add(new ResourceElementBean(null, "File D", false, null, "File D"));
		//		listLeft.add(new ResourceElementBean(null, "File E", false, null, "File E"));
		//		listLeft.add(new ResourceElementBean(null, "File F", false, null, "File F"));
		//		listLeft.add(new ResourceElementBean(null, "File G", false, null, "File G"));
		//		listLeft.add(new ResourceElementBean(null, "File H", false, null, "File H"));
		//		listLeft.add(new ResourceElementBean(null, "File I", false, null, "File I"));
		//
		//		// test with folder and childs
		//		ArrayList<ResourceElementBean> childrenOfA = new ArrayList<ResourceElementBean>(); 
		//		ArrayList<ResourceElementBean> childrenOfW = new ArrayList<ResourceElementBean>();
		//		
		//		ResourceElementBean elementA = new ResourceElementBean(null, "Folder A", true, childrenOfA, "Folder A");
		//		childrenOfA.add(new ResourceElementBean(elementA, "File X", false, null, "Folder A:File X"));
		//		childrenOfA.add(new ResourceElementBean(elementA, "File Y", false, null, "Folder A:File Y"));
		//		childrenOfA.add(new ResourceElementBean(elementA, "File Z", false, null, "Folder A:File Z"));
		//		childrenOfA.add(new ResourceElementBean(elementA, "File V", false, null, "Folder A:File V"));
		//		ResourceElementBean elementW = new ResourceElementBean(elementA, "Folder W", true, childrenOfW, "Folder A:File W");
		//		childrenOfA.add(elementW);
		//		childrenOfW.add(new ResourceElementBean(elementW, "File J", false, null, "Folder A:File W: File J"));
		//		listLeft.add(elementA);
		//		


		//		String folderId = "e87bfc7d-4fb0-4795-9c79-0c495500ca9c";
		//		ckanServices.getTreeFolder(folderId, new AsyncCallback<ResourceElementBean>() {
		//
		//
		//			@Override
		//			public void onSuccess(ResourceElementBean result) {
		//				if(result != null){
		//					RootPanel.get("ckan-metadata-publisher-div").add(new TwinColumnSelectionMainPanel(result));
		//				}
		//			}
		//
		//			@Override
		//			public void onFailure(Throwable caught) {
		//
		//				Window.alert("Failed to retrieve ResourceElementBean");
		//
		//			}
		//		});
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

		String idFolderWorkspace = "1fede4e2-5859-4f19-bddb-aec7dd5b632f";
		RootPanel.get("ckan-metadata-publisher-div").add(new CreateDatasetForm(idFolderWorkspace, eventBus));

	}
}
