package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherServiceAsync;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.AddResourceEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Form used to add resource(s) to a dataset
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddResourceToDataset extends Composite{

	private static AddResourceToDatasetUiBinder uiBinder = GWT
			.create(AddResourceToDatasetUiBinder.class);

	interface AddResourceToDatasetUiBinder extends
	UiBinder<Widget, AddResourceToDataset> {
	}

	// bus to alert the dataset form about this new resource
	private HandlerManager eventBus;

	// the dataset id
	private String datasetId;

	// the dataset organization
	private String datasetOrg;

	private final CKanPublisherServiceAsync ckanServices = GWT.create(CKanPublisherService.class);

	@UiField TextBox resourceUrlTextBox;
	@UiField TextBox resourceNameTextBox;
	@UiField TextArea resourceDescriptionTextArea;
	@UiField Button addResourceButton;
	@UiField AlertBlock infoBlock;
	@UiField Button goToDatasetButton;

	public AddResourceToDataset(HandlerManager eventBus, String datasetId, String datasetOrg, String owner, final String datasetUrl) {
		initWidget(uiBinder.createAndBindUi(this));

		// save bus
		this.eventBus = eventBus;

		// save dataset id (it is needed when we will add resources)
		this.datasetId = datasetId;

		this.datasetOrg = datasetOrg;
		goToDatasetButton.setText(datasetUrl);
		goToDatasetButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open(datasetUrl, "_blank", "");
				//				Window.Location.assign(datasetUrl);
			}
		});
	}

	@UiHandler("addResourceButton")
	void onAddButtonClick(ClickEvent e){

		infoBlock.setVisible(false);

		// validation
		if(resourceUrlTextBox.getText().isEmpty() || resourceNameTextBox.getText().isEmpty()){

			showAlert("Url and name fields cannot be empty", AlertType.ERROR);
			return;

		}

		// collect data and build up the bean
		final ResourceBeanWrapper resource = 
				new ResourceBeanWrapper(
						resourceUrlTextBox.getText(), 
						resourceNameTextBox.getText(), 
						resourceDescriptionTextArea.getText(),
						null,
						true,
						null,
						datasetOrg);

		// disable add button
		addResourceButton.setEnabled(false);

		// try to create
		ckanServices.addResourceToDataset(resource, datasetId, new AsyncCallback<ResourceBeanWrapper>() {

			@Override
			public void onSuccess(ResourceBeanWrapper result) {

				if(result != null){
					showAlert("Resource created correctly", AlertType.SUCCESS);
					eventBus.fireEvent(new AddResourceEvent(result));

					// remove data
					resourceUrlTextBox.setText("");
					resourceNameTextBox.setText("");
					resourceDescriptionTextArea.setText("");

				}
				else
					showAlert("Unable to add this resource. Check that the url is correct", AlertType.ERROR);

			}

			@Override
			public void onFailure(Throwable caught) {

				showAlert("Unable to add this resource, sorry", AlertType.ERROR);

			}
		});

	}

	/**
	 * Show error/success after resource creation attempt.
	 * @param text
	 * @param type
	 */
	protected void showAlert(String text, AlertType type) {

		infoBlock.setText(text);
		infoBlock.setType(type);
		infoBlock.setVisible(true);
		addResourceButton.setEnabled(true);

		// hide after some seconds
		Timer t = new Timer() {

			@Override
			public void run() {

				infoBlock.setVisible(false);

			}
		};

		t.schedule(4000);
	}
}
