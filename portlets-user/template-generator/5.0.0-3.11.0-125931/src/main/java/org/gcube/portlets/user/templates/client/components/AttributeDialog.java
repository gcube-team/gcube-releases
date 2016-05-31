package org.gcube.portlets.user.templates.client.components;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AttributeDialog extends Dialog {

	ArrayList<TextBox> values;
	FlexTable grid;

	HTML name = new HTML("Attribute Name: ");
	TextBox nameValue = new TextBox();		
	RadioButton block = new RadioButton("display","Block (One row per option)");
	RadioButton inline = new RadioButton("display","Inline (default)");	
	
	final String TEXTBOX_WIDTH = "200px";

	/**
	 * used for NEW Created Attributes
	 * @param presenter
	 * @param width
	 * @param height
	 * @param useCheckBoxes says if you have to use checkboxes or radio in the report
	 */
	public AttributeDialog(final Presenter presenter, final int width, final int height, final boolean useCheckBoxes) {
		super();		  
		String suffix = useCheckBoxes? " (Multi)" :  " (Unique)";
		
		this.setHeading("Add Attribute"+suffix);  
		setClosable(false);
		this.setButtons(Dialog.OKCANCEL);
		this.setBodyStyleName("gridAttribute");
		this.setWidth(350);

		Html display = new Html("<span style=\"font-weight: bold;\">Display in Reports</span>");
		this.add(display);
		this.add(inline);
		this.add(block);
		
		inline.setStyleName("checkAttribute");
		block.setStyleName("checkAttribute");
		inline.setValue(true);
		
		Html spacer = new Html("&nbsp;");
		this.add(spacer);

		
		values = new ArrayList<TextBox>();
		grid = getContent();
		this.add(grid);

		this.setScrollMode(Scroll.AUTO);  
		this.setHideOnButtonClick(true);

		ButtonBar buttons = this.getButtonBar();

		Button okbutton = (Button) buttons.getItem(0);
		setHideOnButtonClick(false);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				if (validate()) {
					String[] values2Pass = new String[values.size()];
					for (int i=0; i < values.size(); i++) 
						values2Pass[i] = values.get(i).getText();

					presenter.addAttributArea(width, height, nameValue.getText(), values2Pass, useCheckBoxes, block.getValue());
					hide();
				} else
					Info.display("Error", "All fields must be filled");  
			}  
		});  
		
		Button cancelbutton = (Button) buttons.getItem(1);

		cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				hide();
			}  
		});  
		
	}
	/**
	 * used for EDIT an attribute area
	 *	
	 */
	public AttributeDialog(final Presenter presenter, final TemplateComponent myComponent, final AttributeArea toEdit) {
		super();		 
		setClosable(false);
		toEdit.addStyleName("attribute_editing");
		String suffix = (myComponent.getType()==ComponentType.ATTRIBUTE_MULTI) ? " (Multi)" :  " (Unique)";
		
		this.setHeading("Edit Attribute"+suffix);  
		this.setButtons(Dialog.OKCANCEL);
		this.setBodyStyleName("gridAttribute");
		this.setWidth(350);

		Html display = new Html("<span style=\"font-weight: bold;\">Display in Reports</span>");
		this.add(display);
		this.add(inline);
		this.add(block);
				
		inline.setStyleName("checkAttribute");
		block.setStyleName("checkAttribute");
		
		boolean isInline = true;
		for (Metadata md : myComponent.getAllMetadata()) {
			if (md.getAttribute().equals("display")) {
				isInline = md.getValue().compareTo("inline") == 0;
			}
				
		}
		inline.setValue(isInline);
		block.setValue(!isInline);
		
		Html spacer = new Html("&nbsp;");
		this.add(spacer);

		final String attrName = getAttributeName(myComponent.getSerializable().getPossibleContent().toString());
		final String[] valuesToEdit = getValues(myComponent.getSerializable().getPossibleContent().toString());
		
		values = new ArrayList<TextBox>();
		grid = getContent(attrName, valuesToEdit);		
		
		this.add(grid);		
		
		this.setScrollMode(Scroll.AUTO);  
		this.setHideOnButtonClick(true);

		ButtonBar buttons = this.getButtonBar();

		Button okbutton = (Button) buttons.getItem(0);
		setHideOnButtonClick(false);
		Button cancelbutton = (Button) buttons.getItem(1);
		cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				hide();
				toEdit.removeStyleName("attribute_editing");
			}  
		});  
		
		//the ok button edits the current area
		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				if (validate()) {
					String[] values2Pass = new String[values.size()];
					for (int i=0; i < values.size(); i++) 
						values2Pass[i] = values.get(i).getText();

					toEdit.setHtmlToDisplay(nameValue.getText(), values2Pass);  //update the view
					presenter.storeChangeInSession((Widget) toEdit);  //update in the model
					toEdit.removeStyleName("attribute_editing");
					hide();
				} else
					Info.display("Error", "All fields must be filled");  
			}  
		});  
		
	}

	/**
	 * 
	 * @param toParse
	 * @return
	 */
	private String getAttributeName(String toParse) {
		if (toParse == null)
			return "";
		String toReturn = "";
		try {
			toReturn = toParse.substring(0, toParse.indexOf(":"));
		} catch (StringIndexOutOfBoundsException e) {
			//GWT.log("Could not find : returning empty");
		}
		return toReturn;
	}
	/**
	 * 
	 * @param toParse
	 * @return
	 */
	private String[] getValues(String toParse) {
		String toSplit = toParse.substring(toParse.indexOf(":")+1, toParse.length());
		String[] values = toSplit.split("\\|");	
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}
	
	
	/**
	 * check not null in textfields
	 * @return
	 */
	private boolean validate() {
		if (nameValue.getText().compareTo("") == 0)
			return false;
		for (TextBox value : values) {
			if (value.getText().compareTo("") == 0)
				return false;
		}
		return true;
	}

	private FlexTable getContent() {
		FlexTable grid = new FlexTable();
		grid.setWidth("100%");
		grid.setStyleName("gridAttribute");
		name = new HTML("Attribute Name: ");
		HTML value1 = new HTML("Value: ");

		name.setStyleName("dialogText");
		value1.setStyleName("dialogText");

		grid.setWidget(0, 0,name);
		grid.setWidget(1, 0,value1);

		nameValue = new TextBox();			
		TextBox firstValue = new TextBox();

		values.add(firstValue);

		nameValue.setWidth(TEXTBOX_WIDTH);
		firstValue.setWidth(TEXTBOX_WIDTH);
		nameValue.setText("");
		firstValue.setText("");

		Image plus = new Image(TGenConstants.ADD_STATIC_IMAGE);
		plus.setStyleName("selectable");
		plus.addClickHandler(plusHandler);

		grid.setWidget(0, 1,nameValue);
		grid.setWidget(1, 1,firstValue);
		grid.setWidget(1, 2,plus);
		return grid;
	}
	/**
	 * THIS IS USED TO RESTORE THE STATUS OF A PREVIOUS ATTRIBUTE AREA
	 * @param attrNameValue
	 * @param valuesToEdit
	 * @return
	 */
	private FlexTable getContent(String attrNameValue, String[] valuesToEdit) {
		FlexTable grid = getContent();
		nameValue.setText(attrNameValue);
		values.get(0).setText(valuesToEdit[0]);
		//starts from 1 because the first is set already
		for (int i = 1; i < valuesToEdit.length; i++) {
			addOptionValue(valuesToEdit[i], grid);
		}		
		return grid;
	}

	ClickHandler plusHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			addOptionValue("", grid);
		}
	};
	
	private void addOptionValue(String value, final FlexTable grid) {
		final int i = values.size() + 1;
		HTML valueLabel = new HTML("Value : ");
		valueLabel.setStyleName("dialogText");
		final TextBox valueBox = new TextBox();
		valueBox.setWidth(TEXTBOX_WIDTH);
		valueBox.setText(value);
		values.add(valueBox);

		Image delete = new Image(TGenConstants.REMOVE_STATIC_IMAGE);
		delete.setStyleName("selectable");
	
		
		grid.setWidget(i, 0, valueLabel);
		grid.setWidget(i, 1, valueBox);
		grid.setWidget(i, 2, delete);
		
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.removeRow(i);	
				values.remove(valueBox);
			}
		});
	}
}
