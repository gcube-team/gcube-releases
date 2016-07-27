package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.GcubeDialogExtended;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MetaDataFieldSkeleton extends Composite{

	private static MetaDataFieldSkeletonUiBinder uiBinder = GWT
			.create(MetaDataFieldSkeletonUiBinder.class);

	interface MetaDataFieldSkeletonUiBinder extends
	UiBinder<Widget, MetaDataFieldSkeleton> {
	}

	@UiField Element mandatorySymbol;
	@UiField SpanElement name;
	@UiField SimplePanel elementPanel;
	@UiField FlowPanel noteFieldContainer;
	@UiField Popover noteFieldPopover;
	@UiField ControlLabel controlLabel;
	@UiField Controls controls;
	@UiField Icon infoIcon;
	@UiField FocusPanel focusPanelIconContainer;

	// the element that holds the value (it could be a checkbox, textbox or listbox)
	private Widget holder;

	// the field this object represents
	private MetadataFieldWrapper field;

	// the dialog box for this metadata
	private GcubeDialogExtended dialog;
	
	// save event bus referene
	private HandlerManager eventBus;

	public MetaDataFieldSkeleton(MetadataFieldWrapper field, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));

		// prepare information
		this.field = field;
		
		// event bus
		this.eventBus = eventBus;
		
		// bind
		bind();

		// add custom css properties
		controls.addStyleName("form-controls-custom");
		controlLabel.addStyleName("form-control-label-custom");

		// save the name
		name.setInnerText(field.getFieldName() + ":");

		// check if it is mandatory
		if(!field.getMandatory())
			mandatorySymbol.getStyle().setDisplay(Display.NONE);

		if(field.getIsBoolean()){

			// its a checkbox
			holder = new CheckBox();

			if(field.getDefaulValue() != null)
				((CheckBox)holder).setValue(Boolean.valueOf(field.getDefaulValue()));

			// add to the elementPanel
			elementPanel.add(holder);

		}else{

			// it could be a listbox or a textbox according to the vocabulary fields
			if(field.getVocabulary() == null || field.getVocabulary().isEmpty()){

				// textbox
				holder = new TextBox();

				if(field.getDefaulValue() != null)
					((TextBox)holder).setText(field.getDefaulValue());

				// add to the elementPanel
				elementPanel.add(holder);


			}else{

				// listbox
				holder = new ListBox();

				// if it is not mandatory, add a disabled option
				if(!field.getMandatory()){
					((ListBox)holder).addItem("Select " + field.getFieldName());
					((ListBox)holder).setSelectedValue("Select " + field.getFieldName());
					((ListBox)holder).getElement().getElementsByTagName("option").getItem(0).setAttribute("disabled", "disabled");
				}

				// get vocabulary fields
				List<String> vocabulary = field.getVocabulary();

				for (String term : vocabulary) {

					((ListBox)holder).addItem(term);

				}

				// set default value
				if(field.getDefaulValue() != null)
					((ListBox)holder).setSelectedValue(field.getDefaulValue());

				// add to the elementPanel
				elementPanel.add(holder);
			}
		}

		// set holder width
		if(holder.getClass().equals(ListBox.class))
			holder.setWidth("96%");
		else
			holder.setWidth("95%");

		// set the notes, if any, and the popover
		if(field.getNote() != null && !field.getNote().isEmpty()){
			noteFieldPopover.setText(new HTML("<p style='color:initial'>" + field.getNote() +"</p>").getHTML());
			noteFieldPopover.setHeading(new HTML("<b>" + field.getFieldName() +"</b>").getHTML());
			infoIcon.getElement().getStyle().setCursor(Cursor.HELP);
			noteFieldPopover.setHtml(true);
			noteFieldContainer.setVisible(true);
		}else{
			noteFieldContainer.setVisible(false);
		}

		// add a resize handler to center the dialog box if it's not null
		Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {

				if(dialog != null)
					dialog.center();

			}
		});

	}

	/**
	 * Bind on events
	 */
	private void bind() {
		
		// on close form
		eventBus.addHandler(CloseCreationFormEvent.TYPE, new CloseCreationFormEventHandler() {
			
			@Override
			public void onClose(CloseCreationFormEvent event) {
				
				if(dialog != null)
					dialog.hide();
				
			}
		});
		
	}

	@UiHandler("focusPanelIconContainer")
	void onInfoIconClick(ClickEvent c){

		if(dialog == null){

			// create the dialog box
			dialog = new GcubeDialogExtended(field.getFieldName(), field.getNote());

			// set as non modal
			dialog.setModal(false);
		}

		// else just show and center
		dialog.center();
		dialog.show();
	}

	/**
	 * Check if this field has a valid values
	 * @return a string with the occurred error on error, null otherwise
	 */
	public String isFieldValueValid(){

		if(field.getMandatory()){
			if(holder.getClass().equals(TextBox.class)){

				if(!getFieldCurrentValue().isEmpty())
					if(field.getValidator() == null || field.getValidator().isEmpty())
						return null;
					else return checkValidator(holder, field) ? null : " the inserted value has a wrong format";
				else
					return " a mandatory attribute cannot be empty";

			}else
				return null;

		}else{

			if(holder.getClass().equals(TextBox.class) && getFieldCurrentValue().isEmpty())
				return null;

			return checkValidator(holder, field) ? null : " please select a different value for this field";

		}
	}

	private boolean checkValidator(Widget holder, MetadataFieldWrapper field){

		String validator = field.getValidator();

		if(validator == null || validator.isEmpty())
			return true;
		else if(holder.getClass().equals(TextBox.class)){
			return getFieldCurrentValue().matches(field.getValidator().trim());
		}else
			return true;

	}

	/**
	 * Returns the current value of the field
	 * @return
	 */
	public String getFieldCurrentValue(){

		String value;

		// we validate only listbox and textbox
		if(holder.getClass().equals(ListBox.class)){
			value = ((ListBox)holder).getSelectedItemText();

			// if it was not mandatory but there was no choice, returning empty string
			if(!field.getMandatory())
				if(value.equals("Select " + field.getFieldName()))
					return "";

		}
		else if(holder.getClass().equals(TextBox.class))
			value = ((TextBox)holder).getText();
		else
			value = ((CheckBox)holder).getValue().toString();

		return value;
	}

	/**
	 * Returns the current value of the field
	 * @return
	 */
	public String getFieldName(){

		return field.getFieldName();

	}

	/**
	 * Freeze this widget (after on create)
	 */
	public void freeze() {

		if(holder.getClass().equals(ListBox.class))
			((ListBox)holder).setEnabled(false);
		else if(holder.getClass().equals(TextBox.class))
			((TextBox)holder).setEnabled(false);
		else
			((CheckBox)holder).setEnabled(false);

	}

}
