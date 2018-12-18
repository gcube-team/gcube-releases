/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class CreateFolderForm.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 1, 2016
 */
public abstract class CreateFolderForm extends Composite {

	@UiField
	TextBox input_folder_name;

	@UiField
	Button close_dialog;

	@UiField
	ControlLabel control_label;

	@UiField
	Button submit_create_folder;

	@UiField
	FluidRow validator_field;

	@UiField
	FluidRow form_fields;

	@UiField
	Form form_create_folder;

	private Alert alert = new Alert();
	private static AbstractFormReleaseUiBinder uiBinder = GWT.create(AbstractFormReleaseUiBinder.class);

	/**
	 * Subtmit handler.
	 *
	 * @param folderName the folder name
	 */
	public abstract void subtmitHandler(String folderName);

	/**
	 * Close handler.
	 */
	public abstract void closeHandler();


	/**
	 * The Interface AbstractFormReleaseUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 19, 2015
	 */
	interface AbstractFormReleaseUiBinder extends UiBinder<Widget, CreateFolderForm> {
	}

	/**
	 * Instantiates a new abstract form release.
	 */
	public CreateFolderForm() {
		initWidget(uiBinder.createAndBindUi(this));
		control_label.addStyleName("margin-right-5px");
		input_folder_name.addStyleName("margin-right-5px");
		input_folder_name.addStyleName("gwt-TextBox-personal");
		submit_create_folder.addStyleName("margin-right-5px");
		form_create_folder.getElement().getStyle().setMarginBottom(0, Unit.PX);

		close_dialog.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				closeHandler();

			}
		});

		input_folder_name.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {

				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					if(validateForm()){
						subtmitHandler(input_folder_name.getValue());
					}
				}
			}
		});

		submit_create_folder.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if(validateForm()){
					subtmitHandler(input_folder_name.getValue());
				}
			}
		});

		alert.addStyleName("marginTop10");
		alert.setType(AlertType.ERROR);
		alert.setVisible(false);
		alert.setClose(false);
		validator_field.add(alert);
	}

	public void showMessage(AlertType type, String message){
		alert.setType(type);
		alert.setHTML(message);
		alert.setVisible(true);
		form_fields.setVisible(false);
	}

	public void hideMessage(){
		if(alert.isVisible())
			alert.setVisible(false);

		form_fields.setVisible(true);
	}

	/**
	 * Validate form.
	 *
	 * @return true, if successful
	 */
	public boolean validateForm(){

		boolean valid = true;

		if(input_folder_name.getValue()==null || input_folder_name.getValue().isEmpty()){
			input_folder_name.setPlaceholder("Required Field!!!");
			valid = false;
		}
//		setAlertErrorVisible(!valid);
		return valid;
	}

	/**
	 * Gets the submit button.
	 *
	 * @return the submit button
	 */
	public Button getSubmitButton() {
		return submit_create_folder;
	}

	/**
	 * Gets the validator_field.
	 *
	 * @return the validator_field
	 */
	public FluidRow getValidator_field() {
		return validator_field;
	}

	/**
	 * Gets the alert error.
	 *
	 * @return the alert error
	 */
	public Alert getAlertError() {
		return alert;
	}

	/**
	 * Gets the text box folder name.
	 *
	 * @return the input_folder_name
	 */
	public TextBox getTextBoxFolderName() {

		return input_folder_name;
	}

}
