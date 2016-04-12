/**
 * 
 */
package org.gcube.portlets.user.gisviewerapp.client;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class AbstractFormRelease.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public abstract class WmsForm extends Composite {

	@UiField
	TextBox input_WMS_URI;
	
	@UiField
	TextBox input_layer_name;
	
	@UiField
	TextBox input_layer_title;
	
	@UiField
	Button submit_add_layer;
	
	@UiField
	Button close_dialog;
	
	@UiField
	Form form_wms_add_layer;
	
	@UiField
	ControlGroup input_WMS_URI_group;
	
	@UiField
	ControlGroup input_layer_title_group;
	
	@UiField
	ControlGroup input_layer_name_group;
	
	@UiField
	FluidRow validator_field;
	
	@UiField
	HorizontalPanel hp_form_actions;
	
	private Alert alertError = new Alert("Required Field");
	
	private Alert alertSubmitResult = new Alert("");

	private static AbstractFormReleaseUiBinder uiBinder = GWT.create(AbstractFormReleaseUiBinder.class);
	
	private int width = 300;
	
	/**
	 * Subtmit handler.
	 */
	public abstract void subtmitHandler(String title, String name, String wmsRequest);
	
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
	interface AbstractFormReleaseUiBinder extends UiBinder<Widget, WmsForm> {
	}
	
	/**
	 * Instantiates a new abstract form release.
	 */
	public WmsForm() {
		initWidget(uiBinder.createAndBindUi(this));
//		setSize(width+"px", "200px");
		input_layer_name.setWidth((width-10)+"px");
		input_layer_title.setWidth((width-10)+"px");
		input_WMS_URI.setWidth((width-10)+"px");
		
		close_dialog.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				closeHandler();
				
			}
		});
		
		submit_add_layer.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(validateForm()){
					subtmitHandler(getInputLayerTitle().getValue(), getInputLayerName().getValue(), getInputWMSURI().getValue());
				}
			}
		});
		
		input_WMS_URI.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
//				super.componentKeyDown(event);
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					if(validateForm())
						subtmitHandler(getInputLayerTitle().getValue(), getInputLayerName().getValue(), getInputWMSURI().getValue());
				}
			}
		});
		
		input_layer_name_group.addStyleName("marginTop10");
		input_layer_title_group.addStyleName("marginTop10");
		input_WMS_URI_group.addStyleName("marginTop10");
		
		alertError.addStyleName("marginTop10");
		alertError.setType(AlertType.ERROR);
		alertError.setVisible(false);
		alertError.setClose(false);
		validator_field.add(alertError);
		
//		alertSubmitResult.addStyleName("marginTop10");
		alertSubmitResult.setType(AlertType.INFO);
		alertSubmitResult.setVisible(false);
		alertSubmitResult.setClose(false);
		
		hp_form_actions.setCellHorizontalAlignment(close_dialog, HasHorizontalAlignment.ALIGN_RIGHT);
		
		showAlertSubmitResult(false, "");
		form_wms_add_layer.add(alertSubmitResult);
	}
	
	/**
	 * Validate form.
	 *
	 * @return true, if successful
	 */
	public boolean validateForm(){
		
		boolean valid = true;
//		input_layer_name.setType(ControlGroupType.NONE);
		input_layer_name_group.setType(ControlGroupType.NONE);
		input_WMS_URI_group.setType(ControlGroupType.NONE);
		
		if(input_layer_name.getValue()==null || input_layer_name.getValue().isEmpty()){
			input_layer_name.setControlGroup(input_layer_name_group);
			input_layer_name_group.setType(ControlGroupType.ERROR);
			
			valid = false;
		}
		
		if(input_WMS_URI.getValue()==null || input_WMS_URI.getValue().isEmpty()){
			input_WMS_URI.setControlGroup(input_WMS_URI_group);
			input_WMS_URI_group.setType(ControlGroupType.ERROR);
		
			valid = false;
		}
		
//		alertError.setVisible(!valid);
		setAlertErrorVisible(!valid);
		return valid;
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
	 * Gets the submit_button.
	 *
	 * @return the submit_button
	 */
	public Button getSubmit_button() {
		return submit_add_layer;
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
	 * Sets the alert error visible.
	 *
	 * @param bool the new alert error visible
	 */
	public void setAlertErrorVisible(boolean bool){
		alertError.setVisible(bool);
	}


	/**
	 * Gets the input wmsuri.
	 *
	 * @return the input wmsuri
	 */
	public TextBox getInputWMSURI() {
		return input_WMS_URI;
	}


	/**
	 * Gets the input layer name.
	 *
	 * @return the input layer name
	 */
	public TextBox getInputLayerName() {
		return input_layer_name;
	}


	/**
	 * Gets the input layer title.
	 *
	 * @return the input layer title
	 */
	public TextBox getInputLayerTitle() {
		return input_layer_title;
	}
	
	
}
