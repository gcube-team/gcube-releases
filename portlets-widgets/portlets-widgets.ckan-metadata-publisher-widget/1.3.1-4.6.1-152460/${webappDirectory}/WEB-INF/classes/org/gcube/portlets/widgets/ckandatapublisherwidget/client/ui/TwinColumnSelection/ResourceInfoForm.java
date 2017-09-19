package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.TwinColumnSelection;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A resource information form panel
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ResourceInfoForm extends Composite{

	@UiField
	public TextBox resourceName;
	@UiField
	public TextBox resourcePath;
	@UiField
	public TextBox resourceFormat;
	@UiField
	public TextArea resourceDescription;
	@UiField
	public Button updateResourceButton;
	@UiField
	Button closeButton;
	@UiField
	HorizontalPanel commandPanel;
	@UiField
	ControlGroup controlName;

	private ResourceElementBean resourceBean;

	private static ResourceInfoFormUiBinder uiBinder = GWT
			.create(ResourceInfoFormUiBinder.class);

	interface ResourceInfoFormUiBinder extends
	UiBinder<Widget, ResourceInfoForm> {
	}

	public ResourceInfoForm() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ResourceInfoForm(final ResourceElementBean resource, final ValueUpdater<ResourceElementBean> valueUpdater) {
		initWidget(uiBinder.createAndBindUi(this));

		resourceBean = resource;
		resourceDescription.setText(resource.getDescription());
		resourceFormat.setText(resource.getMimeType() == null? "Unavailable" : resource.getMimeType());
		resourceName.setText(resource.getEditableName());
		resourcePath.setText(resource.getFullPath());
		closeButton.getElement().getStyle().setFloat(Float.RIGHT);
		closeButton.setIcon(IconType.REMOVE_CIRCLE);
		commandPanel.setCellHorizontalAlignment(updateResourceButton, HasHorizontalAlignment.ALIGN_RIGHT);
		commandPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		updateResourceButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				resourceBean.setDescription(resourceDescription.getText());

				removeError(controlName);
				String newName = resourceName.getText();
				if(newName == null || newName.isEmpty()){
					showError(controlName);
				}else{
					resourceBean.setEditableName(newName);
					valueUpdater.update(resourceBean);
				}
			}
		});

		closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				clearPanel();

			}
		});
	}

	public String getResourceName() {
		return resourceName.getText();
	}

	public void setResourceName(String resourceName) {
		this.resourceName.setText(resourceName);
	}

	public String getResourcePath() {
		return resourcePath.getText();
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath.setText(resourcePath);
	}

	public String getResourceFormat() {
		return resourceFormat.getText();
	}

	public void setResourceFormat(String resourceFormat) {
		this.resourceFormat.setText(resourceFormat);
	}

	public String getResourceDescription() {
		return resourceDescription.getText();
	}

	public void setResourceDescription(String resourceDescription) {
		this.resourceDescription.setText(resourceDescription);
	}

	public void removeError(ControlGroup control) {
		control.setType(ControlGroupType.NONE);
	}

	public void showError(ControlGroup control) {
		control.setType(ControlGroupType.ERROR);
	}

	protected void clearPanel() {
		this.removeFromParent();
	}
}
