/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage.release;

import java.util.Date;

import org.gcube.portlets.admin.gcubereleases.client.view.DateUtilFormatter;
import org.gcube.portlets.admin.gcubereleases.client.view.LoaderIcon;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitHandler;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * The Class AbstractFormRelease.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public abstract class AbstractFormRelease extends Composite {

	@UiField
	TextBox input_id;
	
	@UiField
	TextBox input_name;
	
	@UiField
	TextBox input_URI;
	
	@UiField
	TextArea input_description;
	
	@UiField
	DateBox input_release_date;

	@UiField
	ControlGroup input_release_date_group;

	@UiField
	ListBox select_online;
	
	@UiField
	SubmitButton submit_button;
	
	@UiField
	Form form_new_release;
	
	@UiField
	ControlGroup input_id_group;
	
	@UiField
	ControlGroup input_name_group;
	
	@UiField
	ControlGroup input_URI_group;
	
	@UiField
	FluidRow validator_field;
	
	private Alert alertError = new Alert("Required Field");
	
	private Alert alertSubmitResult = new Alert("");
	
	private LoaderIcon loaderIcon = new LoaderIcon();

	private static AbstractFormReleaseUiBinder uiBinder = GWT.create(AbstractFormReleaseUiBinder.class);
	
	/**
	 * Subtmit handler.
	 */
	public abstract void subtmitHandler();
	
	/**
	 * Validate form.
	 *
	 * @return true, if successful
	 */
	public abstract boolean validateForm();

	/**
	 * The Interface AbstractFormReleaseUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Feb 19, 2015
	 */
	interface AbstractFormReleaseUiBinder extends UiBinder<Widget, AbstractFormRelease> {
	}
	
	/**
	 * Instantiates a new abstract form release.
	 */
	public AbstractFormRelease() {
		initWidget(uiBinder.createAndBindUi(this));
		
		form_new_release.addSubmitHandler(new SubmitHandler() {
			
			@Override
			public void onSubmit(SubmitEvent event) {
//				Window.alert("bb");
				 event.cancel();
			}
		});
	
		subtmitHandler();
		
		DefaultFormat format = new DefaultFormat(DateUtilFormatter.formatDate);
		input_release_date.setFormat(format);
		input_release_date.setTitle(DateUtilFormatter.formatDate.getPattern());

		alertError.setType(AlertType.ERROR);
		alertError.setVisible(false);
		alertError.setClose(false);
		validator_field.add(alertError);
		
		alertSubmitResult.setStyleName("marginTop20");
		alertSubmitResult.setType(AlertType.INFO);
		alertSubmitResult.setVisible(false);
		alertSubmitResult.setClose(false);

		showLoading(false, "");
		form_new_release.add(loaderIcon);
		showAlertSubmitResult(false, "");
		form_new_release.add(alertSubmitResult);
	}
	
	/**
	 * Show alert submit result.
	 *
	 * @param visible the visible
	 * @param text the text
	 */
	public void showAlertSubmitResult(boolean visible, String text){
		alertSubmitResult.setText(text);
		alertSubmitResult.setVisible(visible);
	}
	
	/**
	 * Show loading.
	 *
	 * @param visible the visible
	 * @param text the text
	 */
	public void showLoading(boolean visible, String text){
		loaderIcon.setVisible(visible);
		loaderIcon.setText(text);
	}

	/**
	 * Gets the input_id.
	 *
	 * @return the input_id
	 */
	public TextBox getInput_id() {
		return input_id;
	}

	/**
	 * Gets the input_name.
	 *
	 * @return the input_name
	 */
	public TextBox getInput_name() {
		return input_name;
	}

	/**
	 * Gets the input_ uri.
	 *
	 * @return the input_ uri
	 */
	public TextBox getInput_URI() {
		return input_URI;
	}

	/**
	 * Gets the input_description.
	 *
	 * @return the input_description
	 */
	public TextArea getInput_description() {
		return input_description;
	}

	/**
	 * Gets the select_online.
	 *
	 * @return the select_online
	 */
	public ListBox getSelect_online() {
		return select_online;
	}

	/**
	 * Gets the submit_button.
	 *
	 * @return the submit_button
	 */
	public SubmitButton getSubmit_button() {
		return submit_button;
	}

	/**
	 * Gets the form_new_release.
	 *
	 * @return the form_new_release
	 */
	public Form getForm_new_release() {
		return form_new_release;
	}

	/**
	 * Gets the input_id_group.
	 *
	 * @return the input_id_group
	 */
	public ControlGroup getInput_id_group() {
		return input_id_group;
	}

	/**
	 * Gets the input_name_group.
	 *
	 * @return the input_name_group
	 */
	public ControlGroup getInput_name_group() {
		return input_name_group;
	}

	/**
	 * Gets the input_ ur i_group.
	 *
	 * @return the input_ ur i_group
	 */
	public ControlGroup getInput_URI_group() {
		return input_URI_group;
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
		return alertError;
	}

	/**
	 * Gets the alert submit result.
	 *
	 * @return the alert submit result
	 */
	public Alert getAlertSubmitResult() {
		return alertSubmitResult;
	}

	/**
	 * Gets the loader icon.
	 *
	 * @return the loader icon
	 */
	public LoaderIcon getLoaderIcon() {
		return loaderIcon;
	}
	
	/**
	 * Sets the alert error visible.
	 *
	 * @param bool the new alert error visible
	 */
	public void setAlertErrorVisible(boolean bool){
		alertError.setVisible(bool);
	}
	
	/**
	 * Sets the input id value.
	 *
	 * @param value the value
	 * @param readOnly the read only
	 */
	public void setInputIDValue(String value, boolean readOnly) {
		this.input_id.setValue(value);
		this.input_id.setReadOnly(readOnly);
	}

	/**
	 * Sets the input name value.
	 *
	 * @param value the value
	 * @param readOnly the read only
	 */
	public void setInputNameValue(String value, boolean readOnly) {
		this.input_name.setValue(value);
		this.input_name.setReadOnly(readOnly);
	}

	/**
	 * Sets the input uri value.
	 *
	 * @param value the value
	 * @param readOnly the read only
	 */
	public void setInputURIValue(String value, boolean readOnly) {
		this.input_URI.setValue(value);
		this.input_URI.setReadOnly(readOnly);
	}

	/**
	 * Sets the input description value.
	 *
	 * @param value the value
	 * @param readOnly the read only
	 */
	public void setInputDescriptionValue(String value, boolean readOnly) {
		this.input_description.setValue(value);
		this.input_description.setReadOnly(readOnly);
	}

	/**
	 * Sets the select online value.
	 *
	 * @param value the value
	 * @param readOnly the read only
	 */
	public void setSelectOnlineValue(boolean value, boolean readOnly) {
		
		if(readOnly)
			this.select_online.clear();

		select_online.setSelectedValue(Boolean.toString(value).toUpperCase());
	}
	
	/**
	 * Gets the input_release_date.
	 *
	 * @return the input_release_date
	 */
	public DateBox getInput_release_date() {
		return input_release_date;
	}
	

	
	/**
	 * Sets the input release date.
	 *
	 * @param date the new input release date in millisecond
	 */
	public void setInputReleaseDate(Long date) {
		if(date!=null)
			this.input_release_date.setValue(new Date(date));
	}
}
