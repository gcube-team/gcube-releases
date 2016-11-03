package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.dataset.resources;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherService;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.CKanPublisherServiceAsync;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.AddResourceEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.AddResourceEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteResourceEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.DeleteResourceEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.github.gwtbootstrap.client.ui.Accordion;
import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A summary of the resources added by the user.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddedResourcesSummary extends Composite{

	private static AddedResourcesSummaryUiBinder uiBinder = GWT
			.create(AddedResourcesSummaryUiBinder.class);

	interface AddedResourcesSummaryUiBinder extends
	UiBinder<Widget, AddedResourcesSummary> {
	}

	//Create a remote service proxy to talk to the server-side ckan service.
	private final CKanPublisherServiceAsync ckanServices = GWT.create(CKanPublisherService.class);

	// Event bus
	private HandlerManager eventBus;

	// list of added resources (beans)
	List<ResourceBeanWrapper> addedResources;

	@UiField VerticalPanel addResourcesPanel;

	public AddedResourcesSummary(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		// save bus
		this.eventBus = eventBus;

		// bind on add resource event
		bind();

		// init list
		addedResources = new ArrayList<ResourceBeanWrapper>();
	}

	/**
	 * Bind on add/delete resource event
	 */
	private void bind() {

		// when a new resource is added
		eventBus.addHandler(AddResourceEvent.TYPE, new AddResourceEventHandler() {

			@Override
			public void onAddedResource(AddResourceEvent addResourceEvent) {

				// get the resource
				final ResourceBeanWrapper justAddedResource = addResourceEvent.getResource();

				// Build an accordion to show resource info
				Accordion accordion = new Accordion();
				AccordionGroup accordionGroup = new AccordionGroup();
				accordionGroup.setHeading("- " + justAddedResource.getName());
				accordion.add(accordionGroup);

				// add sub-info such as url and description
				Paragraph pUrl = new Paragraph();
				pUrl.setText("Url : " + justAddedResource.getUrl());
				Paragraph pDescription = new Paragraph();
				pDescription.setText("Description : " + justAddedResource.getDescription());

				// button to delete the resource
				Button deleteButton = new Button();
				deleteButton.setText("Delete");
				deleteButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						eventBus.fireEvent(new DeleteResourceEvent(justAddedResource));

					}
				});

				// fill accordion
				accordionGroup.add(pUrl);
				accordionGroup.add(pDescription);
				accordionGroup.add(deleteButton);

				// add to the list
				addedResources.add(justAddedResource);

				// add to the panel
				addResourcesPanel.add(accordion);
			}
		});

		// when the user wants to delete a resource
		eventBus.addHandler(DeleteResourceEvent.TYPE, new DeleteResourceEventHandler() {

			@Override
			public void onDeletedResource(DeleteResourceEvent deleteResourceEvent) {

				// to delete
				ResourceBeanWrapper	 toDelete = deleteResourceEvent.getResource();

				// find it
				for(int i = 0; i < addedResources.size(); i++){

					if(addedResources.get(i).getId().equals(toDelete.getId())){
						
						// get the associated widget and remove it
						final Widget widget = addResourcesPanel.getWidget(i);

						// remote call to remove it from the dataset
						ckanServices.deleteResourceFromDataset(toDelete, new AsyncCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean result) {
								
								if(result)
									widget.removeFromParent();
							}

							@Override
							public void onFailure(Throwable caught) {

							}
						});
						
						break;
					}
				}
				
				// remove from the list
				addedResources.remove(toDelete);
			}
		});
	}
}
