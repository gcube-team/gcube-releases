package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;


/**
 * The Class DialogText.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 10, 2017
 */
public class DialogText extends Dialog {

	private TextField<String> txt;


	/**
	 * Instantiates a new dialog text.
	 *
	 * @param headingTxt the heading txt
	 * @param msgTitle the msg title
	 * @param msgTxt the msg txt
	 * @param isFolder the is folder
	 */
	public DialogText(String headingTxt, String msgTitle, String msgTxt, boolean isFolder) {

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(90);
		layout.setDefaultWidth(300);
		setLayout(layout);

		setButtonAlign(HorizontalAlignment.CENTER);
//		setHideOnButtonClick(true);
		setHeading(headingTxt);
		setModal(true);
		// setBodyBorder(true);
		setBodyStyle("padding: 9px; background: none");
		setWidth(500);
		setResizable(false);
		setButtons(Dialog.OKCANCEL);
		txt = new TextField<String>();
		// txt.setMinLength(2);
		txt.setFieldLabel(msgTitle);
		txt.setValue(msgTxt);
//		txt.setRegex("^[a-zA-Z0-9]+[ a-zA-Z0-9_()-]*");
//		txt.setRegex("^[a-zA-Z0-9]+[^.<>\\|?/*%$]*$");

		if(isFolder){
			txt.getMessages().setRegexText(ConstantsExplorer.REGEX_WSFOLDER_NAME_ALERT_MSG);
			txt.setRegex(ConstantsExplorer.REGEX_TO_WSFOLDER_NAME);
		}else{
			txt.getMessages().setRegexText(ConstantsExplorer.REGEX_WSITEM_NAME_ALERT_MSG);
			txt.setRegex(ConstantsExplorer.REGEX_TO_WSITEM_NAME);
		}

		txt.setAutoValidate(true);
		txt.setAllowBlank(false);

		txt.addKeyListener(new KeyListener() { // KEY ENTER

			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode())
					getButtonById(Dialog.OK).fireEvent(Events.Select);

			}
		});

		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
//				if (isValidForm()) COMMENTED TO PREVENT BACK END EXCEPTION
//					hide();
			}

		});

		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
					hide();
			}
		});

		setFocusWidget(txt);
		add(txt);
		this.show();
	}


	/**
	 * Gets the txt value.
	 *
	 * @return the txt value
	 */
	public String getTxtValue() {

		return txt.getValue().trim();
	}

	/**
	 * Checks for value.
	 *
	 * @param field the field
	 * @return true, if successful
	 */
	protected boolean hasValue(TextField<String> field) {
		return field.getValue() != null && field.getValue().length() > 0;
	}


	/**
	 * Checks if is valid form.
	 *
	 * @return true, if is valid form
	 */
	public boolean isValidForm() {
		if(txt.isValid())
			return true;
		return false;
	}

}