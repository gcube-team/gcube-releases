package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEvent;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.events.CloseCreationFormEventHandler;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata.timeandreanges.DataTimeBox;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.utils.GcubeDialogExtended;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.FieldAsGroup;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.FieldAsTag;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetadataFieldWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.TaggingGroupingValue;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;
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
	@UiField ControlGroup metafieldControlGroup;

	//	private static final String REGEX_IS_NUMBER = "[0-9]+[.]?[0-9]+";

	// the element that holds the value (it could be a checkbox, textbox or listbox, textarea, calendar, two calendars, more calendars)
	private Widget holder;

	// the field this object represents
	private MetadataFieldWrapper field;

	// the dialog box for this metadata
	private GcubeDialogExtended dialog;

	// range list
	private List<DataTimeBox> rangesList = new ArrayList<DataTimeBox>();

	// save event bus reference
	private HandlerManager eventBus;

	// errors
	private static final String MANDATORY_ATTRIBUTE_MISSING = "a mandatory attribute cannot be empty";
	private static final String MALFORMED_ATTRIBUTE = " the inserted value has a wrong format";
	private static final String ADD_NEW_TIME_RANGE = "Add a new Time Range";
	private static final String DELETE_TIME_RANGE = "Delete the last Time Range";

	// missing range value
	private static final String INSERT_MISSING_VALUE = " you cannot specify an end date without a start one";
	private static final String INSERT_MISSING_VALUE_MANDATORY = " one or more range value missing in mandatory attribute";
	private static final String UPPER_RANGE_NOT_SPECIFIED = "Not specified";
	// time range separator
	public static final String RANGE_SEPARATOR = ",";
	private static final String TOOLTIP_MULTISELECTION = "Hold down the Control (CTRL) or Command (CMD) button to select multiple options";

	public MetaDataFieldSkeleton(final MetadataFieldWrapper field, HandlerManager eventBus) throws Exception{
		initWidget(uiBinder.createAndBindUi(this));

		// prepare information
		this.field = field;

		// event bus
		this.eventBus = eventBus;

		// bind
		bind();

		switch(field.getType()){

		case Boolean : 

			// its a checkbox
			holder = new CheckBox();
			if(field.getDefaultValue() != null)
				((CheckBox)holder).setValue(Boolean.valueOf(field.getDefaultValue()));
			break;

		case GeoJSON:
		case Text:	

			holder = new TextArea();

			if(field.getDefaultValue() != null)
				((TextArea)holder).setText(field.getDefaultValue());
			break;

		case Time:

			DataTimeBox ref;
			holder = ref = new DataTimeBox(false);

			// set time, if present
			if(field.getDefaultValue() != null && !field.getDefaultValue().isEmpty()){
				String[] dateAndTime = field.getDefaultValue().split(" ");
				if(dateAndTime.length > 0){
					ref.setStartDate(dateAndTime[0], dateAndTime.length > 1 ? dateAndTime[1] : null);
				}
			}
			break;

		case Time_Interval:

			DataTimeBox rangeBox;
			holder = rangeBox = new DataTimeBox(true);
			setRangeTimeInTimeBox(field.getDefaultValue(), rangeBox);
			rangesList.add(rangeBox);
			break;

		case Times_ListOf: 

			holder = new FlowPanel();

			// start and end range date
			final VerticalPanel containerRanges = new VerticalPanel();
			containerRanges.setWidth("100%");

			SimplePanel panelFirstRange = new SimplePanel();
			DataTimeBox rangeBoxFirst = new DataTimeBox(true);
			setRangeTimeInTimeBox(field.getDefaultValue(), rangeBoxFirst);
			((SimplePanel)panelFirstRange).add(rangeBoxFirst);
			rangesList.add(rangeBoxFirst);

			// Add more button
			Button addRangeButton = new Button();
			addRangeButton.setIcon(IconType.PLUS_SIGN);
			addRangeButton.setTitle(ADD_NEW_TIME_RANGE);

			addRangeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					final FlowPanel newRange = new FlowPanel();
					final DataTimeBox newRangeBox = new DataTimeBox(true);
					setRangeTimeInTimeBox(field.getDefaultValue(), newRangeBox);
					rangesList.add(newRangeBox);

					// delete button
					Button deleteRangeButton = new Button("", IconType.MINUS_SIGN, new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							newRange.removeFromParent();
							rangesList.remove(newRangeBox);
						}
					});
					deleteRangeButton.setTitle(DELETE_TIME_RANGE);
					newRange.add(newRangeBox);
					newRange.add(deleteRangeButton);
					containerRanges.add(newRange);

				}
			});

			// add calendars and plus sign
			containerRanges.add(panelFirstRange);

			// add the vertical panel first, then the button
			((FlowPanel)holder).add(containerRanges);
			((FlowPanel)holder).add(addRangeButton);

			break;

		case Number:

			holder = new TextBox();

			if(field.getDefaultValue() != null)
				((TextBox)holder).setText(field.getDefaultValue());

			break;

		case String:

			// it could be a listbox or a textbox according to the vocabulary fields
			if(field.getVocabulary() == null || field.getVocabulary().isEmpty()){

				// textbox
				holder = new TextBox();

				if(field.getDefaultValue() != null)
					((TextBox)holder).setText(field.getDefaultValue());

			}else{

				// listbox
				ListBox tempListBox;
				holder = tempListBox = new ListBox(field.isMultiSelection());

				if(field.isMultiSelection())
					tempListBox.setTitle(TOOLTIP_MULTISELECTION);

				// if it is not mandatory, add a disabled option
				if(!field.getMandatory()){
					tempListBox.addItem("Select " + field.getFieldName());
					tempListBox.setSelectedValue("Select " + field.getFieldName());
					tempListBox.getElement().getElementsByTagName("option").getItem(0).setAttribute("disabled", "disabled");
				}

				// get vocabulary fields
				List<String> vocabulary = field.getVocabulary();

				for (String term : vocabulary) {
					tempListBox.addItem(term);
				}

				// set default value
				if(field.getDefaultValue() != null)
					tempListBox.setSelectedValue(field.getDefaultValue());

			}

			break;

		default: return;

		}

		// add custom css properties
		controls.addStyleName("form-controls-custom");
		controlLabel.addStyleName("form-control-label-custom");

		// save the name
		name.setInnerText(field.getFieldName() + ":");

		// check if it is mandatory
		if(!field.getMandatory())
			mandatorySymbol.getStyle().setDisplay(Display.NONE);

		// add to the elementPanel
		elementPanel.add(holder);

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
	 * Check if this field has valid values
	 * @return a string with the occurred error on error, null otherwise
	 */
	public String isFieldValueValid(){

		switch(field.getType()){

		case Boolean : 

			// nothing to validate
			return null;

		case Text:	
		case GeoJSON: // validation for geojson will be performed at server side as well TODO

			String textAreaValue = getFieldCurrentValue().get(0);

			if(field.getMandatory()){

				if(!textAreaValue.trim().isEmpty())
					if(field.getValidator() == null || field.getValidator().isEmpty())
						return null; // no further check
					else return checkValidator(textAreaValue, field.getValidator()) ? null : MALFORMED_ATTRIBUTE;
				else return MANDATORY_ATTRIBUTE_MISSING;

			}else{ 

				if(textAreaValue.trim().isEmpty())
					return null;

				else return checkValidator(textAreaValue, field.getValidator()) ? null : MALFORMED_ATTRIBUTE;

			}

		case Time:

			String dateValue = getFieldCurrentValue().get(0);

			if(field.getMandatory()){
				if(dateValue.isEmpty())
					return MANDATORY_ATTRIBUTE_MISSING;
			}

			return null;


		case Time_Interval:

			String rangeValue = rangesList.get(0).getCurrentValue();

			if(field.getMandatory()){
				if(rangeValue.contains(DataTimeBox.MISSING_RANGE_VALUE_START) || rangeValue.contains(DataTimeBox.MISSING_RANGE_VALUE_END))
					return INSERT_MISSING_VALUE_MANDATORY;
			}

			if(rangeValue.contains(DataTimeBox.MISSING_RANGE_VALUE_START) && !rangeValue.equals(DataTimeBox.MISSING_RANGE_VALUE_START + DataTimeBox.RANGE_SEPARATOR_START_END + DataTimeBox.MISSING_RANGE_VALUE_END))
				return INSERT_MISSING_VALUE;

			return null;

		case Times_ListOf: 

			for(DataTimeBox el: rangesList){

				String currentValue = el.getCurrentValue();
				if(field.getMandatory()){
					if(currentValue.contains(DataTimeBox.MISSING_RANGE_VALUE_START) || currentValue.contains(DataTimeBox.MISSING_RANGE_VALUE_END))
						return INSERT_MISSING_VALUE_MANDATORY;
				}

				GWT.log("Printing " + currentValue);
				if(currentValue.contains(DataTimeBox.MISSING_RANGE_VALUE_START) && !currentValue.equals(DataTimeBox.MISSING_RANGE_VALUE_START + DataTimeBox.RANGE_SEPARATOR_START_END + DataTimeBox.MISSING_RANGE_VALUE_END))
					return INSERT_MISSING_VALUE;

			}
			return null;

		case Number:

			String numberValue = ((TextBox)holder).getValue();

			if(field.getMandatory()){

				if(!numberValue.trim().isEmpty())
					if(field.getValidator() == null || field.getValidator().isEmpty())
						return isANumber(numberValue) ? null : MALFORMED_ATTRIBUTE;
					else return checkValidator(numberValue, field.getValidator()) ? null : MALFORMED_ATTRIBUTE;
				else return " a mandatory attribute cannot be empty";

			}else{

				if(numberValue.trim().isEmpty())
					return null;
				else {
					String validatorToUse  = field.getValidator();
					if(validatorToUse != null && !validatorToUse.isEmpty())
						return checkValidator(numberValue, validatorToUse) ? null : MALFORMED_ATTRIBUTE;
					else
						return isANumber(numberValue) ? null : MALFORMED_ATTRIBUTE;
				}
			}

		case String:

			// just handle the case of textbox 
			if(holder.getClass().equals(TextBox.class)){

				String textBoxValue = getFieldCurrentValue().get(0);
				if(field.getMandatory()){
					if(!textBoxValue.trim().isEmpty())
						if(field.getValidator() == null || field.getValidator().isEmpty())
							return null; // no further check
						else return checkValidator(textBoxValue, field.getValidator()) ? null : MALFORMED_ATTRIBUTE;
					else return MANDATORY_ATTRIBUTE_MISSING;

				}else{ 
					if(textBoxValue.trim().isEmpty())
						return null;
					else return checkValidator(textBoxValue, field.getValidator()) ? null : MALFORMED_ATTRIBUTE;
				}
			}
			else{

				List<String> listboxValues = getFieldCurrentValue();
				
				if(listboxValues.isEmpty() && field.getMandatory())
					return MANDATORY_ATTRIBUTE_MISSING;

				for (String value : listboxValues) {
					if(!field.getMandatory()){

						if(field.getValidator() == null || field.getValidator().isEmpty())
							continue;
						else 
							if(checkValidator(value, field.getValidator()))
								continue;
							else return  MALFORMED_ATTRIBUTE;

					}else{

						if(value == null || value.isEmpty())
							return MANDATORY_ATTRIBUTE_MISSING;
						else
							continue;

					}
				}
			}

			return null;

		default: return null;

		}
	}

	/**
	 * Check if this string is a number
	 * @param numberValue
	 * @return
	 */
	private boolean isANumber(String numberValue) {
		return numberValue.matches("-?\\d+(\\.\\d+)?");
	}

	/**
	 * Check if value matches validator (regex). In case validator is null, true is returned.
	 * @param value
	 * @param validator
	 * @return true if validator is null OR value.matches(reges), false otherwise
	 */
	private boolean checkValidator(String value, String validator) {
		GWT.log("Value is " + value);
		GWT.log("Validator is " + validator);
		if(validator == null || validator.isEmpty())
			return true;
		else return value.matches(validator);
	}

	/**
	 * Returns the current value of the field. In case of TimeInterval or TimeList see getTimeIntervalOrTimeListWithoutMissing()
	 * @return
	 */
	public List<String> getFieldCurrentValue(){

		List<String> toReturn = new ArrayList<String>();
		String manipulatedTemp = "";

		switch(field.getType()){

		case Boolean : 

			toReturn.add(((CheckBox)holder).getValue().toString()); 
			break;

		case Text:	
		case GeoJSON:

			toReturn.add(((TextArea)holder).getText());
			break;

		case Time:

			toReturn.add(((DataTimeBox)holder).getCurrentValue().replaceAll(DataTimeBox.MISSING_RANGE_VALUE_START, "")); // it was a noRange metadata
			break;

		case Time_Interval:

			manipulatedTemp = rangesList.get(0).getCurrentValue().replaceAll(DataTimeBox.MISSING_RANGE_VALUE_START, "").replaceAll(DataTimeBox.MISSING_RANGE_VALUE_END, UPPER_RANGE_NOT_SPECIFIED);
			if(toReturn.equals(DataTimeBox.RANGE_SEPARATOR_START_END + UPPER_RANGE_NOT_SPECIFIED))
				manipulatedTemp = "";

			// split to check if the extreme are equals
			String[] temp = manipulatedTemp.split(DataTimeBox.RANGE_SEPARATOR_START_END);
			if(temp[0].equals(temp[1]))
				manipulatedTemp = temp[0];

			toReturn.add(manipulatedTemp);
			break;

		case Times_ListOf: 

			manipulatedTemp = "";
			for (DataTimeBox elem : rangesList) {

				String currentRange = elem.getCurrentValue().replaceAll(DataTimeBox.MISSING_RANGE_VALUE_START, "").replaceAll(DataTimeBox.MISSING_RANGE_VALUE_END, UPPER_RANGE_NOT_SPECIFIED);
				if(currentRange.equals(DataTimeBox.RANGE_SEPARATOR_START_END + UPPER_RANGE_NOT_SPECIFIED))
					continue;

				String[] splitted = currentRange.split(DataTimeBox.RANGE_SEPARATOR_START_END);
				if(splitted[0].equals(splitted[1]))
					manipulatedTemp += (manipulatedTemp.isEmpty()) ? splitted[0] : RANGE_SEPARATOR  + splitted[0];
					else
						manipulatedTemp += (manipulatedTemp.isEmpty()) ? splitted[0] + DataTimeBox.RANGE_SEPARATOR_START_END + splitted[1] : 
							RANGE_SEPARATOR + splitted[0] + DataTimeBox.RANGE_SEPARATOR_START_END + splitted[1];

			}

			if(manipulatedTemp.endsWith(RANGE_SEPARATOR))
				manipulatedTemp = manipulatedTemp.substring(0, manipulatedTemp.length() - 1);

			toReturn.add(manipulatedTemp);
			break;

		case Number:
		case String:

			if(holder.getClass().equals(TextBox.class))
				toReturn.add(((TextBox)holder).getText());
			else{

				// handle multiselected case
				for(int i = 0; i < ((ListBox)holder).getItemCount(); i++){
					if(((ListBox)holder).isItemSelected(i)){
						toReturn.add(((ListBox)holder).getItemText(i));
					}
				}

				// if it was not mandatory but there was no choice, returning empty string
				if(!field.getMandatory())
					if(toReturn.equals("Select " + field.getFieldName())){
						toReturn.clear();
						toReturn.add("");
					}
			}	

			break;

		default: break;

		}

		return toReturn;
	}

	/**
	 * Returns the current name of the field
	 * @return
	 */
	public String getFieldNameOriginal(){

		return field.getFieldName();

	}

	/**
	 * Returns the current name of the field
	 * @return
	 */
	public String getFieldNameQualified(){

		if(field.getFieldNameFromCategory() != null)
			return field.getFieldNameFromCategory();
		return field.getFieldName();

	}

	/**
	 * Freeze this widget (after on create)
	 */
	public void freeze() {

		switch(field.getType()){

		case Boolean : 

			((CheckBox)holder).setEnabled(false);
			break;

		case GeoJSON:
		case Text:	

			((TextArea)holder).setEnabled(false);
			break;

		case Time:

			((DataTimeBox)holder).freeze();
			break;

		case Time_Interval:

			rangesList.get(0).freeze();
			break;

		case Times_ListOf: 

			for(DataTimeBox el : rangesList)
				el.freeze();

			break;

		case Number:

			((TextBox)holder).setEnabled(false);
			break;

		case String:

			if(holder.getClass().equals(ListBox.class))
				((ListBox)holder).setEnabled(false);
			else 
				((TextBox)holder).setEnabled(false);
			break;

		default: break;

		}
	}

	/**
	 * Get the original MetadataFieldWrapper object
	 * @return
	 */
	public MetadataFieldWrapper getField() {
		return field;
	}

	public void removeError() {

		metafieldControlGroup.setType(ControlGroupType.NONE);

	}

	public void showError() {

		metafieldControlGroup.setType(ControlGroupType.ERROR);

	}

	/**
	 * Build the range interval
	 * @param rangeValues
	 * @param tb
	 */
	private void setRangeTimeInTimeBox(String rangeValues, DataTimeBox tb){
		// set time, if present
		if(rangeValues != null && !rangeValues.isEmpty()){
			if(!rangeValues.contains(DataTimeBox.RANGE_SEPARATOR_START_END))
				rangeValues += "/" + rangeValues;

			String[] dateAndTimeRanges = rangeValues.split(DataTimeBox.RANGE_SEPARATOR_START_END);
			if(dateAndTimeRanges.length > 0){
				String[] firstRangeDate = dateAndTimeRanges[0].split(" ");
				tb.setStartDate(firstRangeDate[0], firstRangeDate.length > 1 ? firstRangeDate[1] : null);
				if(dateAndTimeRanges.length > 1){
					String[] secondRangeDate = dateAndTimeRanges[1].split(" ");
					tb.setEndDate(secondRangeDate[0], secondRangeDate.length > 1 ? secondRangeDate[1] : null);
				}
			}
		}
	}

	/**
	 * Given the name and the value of this field, return a tag if it is the case.
	 * @return a tag as string
	 */
	public List<String> getTagFromThisField(){

		FieldAsTag asTag = field.getAsTag();
		List<String> generatedTags = new ArrayList<String>();
		if(asTag != null){
			List<String> values = getFieldCurrentValue();
			for (String value : values) {
				generatedTags.add(TaggingGroupingValue.getComposedValueTag(field.getFieldName(), value, asTag.getSeparator(), asTag.getTaggingValue()));
			}
			return generatedTags;
		}
		return null;
	}

	/**
	 * Given the name and the value of this field, return a group title if it is the case.
	 * @return a tag as string
	 */
	public List<String> getGroupTitleFromThisGroup(){
		FieldAsGroup asGroup = field.getAsGroup();
		List<String> generatedGroups = new ArrayList<String>();
		if(asGroup != null){
			List<String> values = getFieldCurrentValue();
			for (String value : values) {
				generatedGroups.addAll(TaggingGroupingValue.getComposedValueGroup(field.getFieldName(), value, asGroup.getGroupingValue()));
			}
			return generatedGroups;
		}
		return null;
	}

	/**
	 * Check if the group associated (if it exists) should be forced
	 * @return
	 */
	public boolean isGroupToForce(){
		FieldAsGroup asGroup = field.getAsGroup();
		if(asGroup != null){
			return asGroup.getCreate();
		}
		return false;
	}
	
	/**
	 * Check if the propagateUp is set
	 * @return
	 */
	public boolean isPropagateUp(){
		FieldAsGroup asGroup = field.getAsGroup();
		if(asGroup != null){
			return asGroup.isPropagateUp();
		}
		return false;
	}

}