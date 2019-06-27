package org.gcube.portlets.user.workspace.client.view.windows;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;


/**
 * The Class DialogAddUrl.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Nov 29, 2016
 */
public class DialogAddUrl extends Dialog {

	private int widthDialog = 500;
	private int heightTextArea = 150;
	private TextField<String> txtName;
	private TextField<String> txtUrl = new TextField<String>();
	private TextArea textAreaDescription = new TextArea();
	private final String [] URL_PROTOCOL = {"http://","https://","ftp://"};
	private Label labelError = new Label();


	/**
	 * Instantiates a new dialog add url.
	 *
	 * @param headerTitle the header title
	 */
	public DialogAddUrl(String headerTitle) {

	    FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(300);
	    setLayout(layout);
	    this.setIcon(Resources.getIconAddUrl());
	    setButtonAlign(HorizontalAlignment.CENTER);
	    setHeading(ConstantsExplorer.MESSAGE_ADD_URL_IN + " "+ headerTitle);
	    setModal(true);
//	    setBodyBorder(true);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(widthDialog);
	    setResizable(false);
	    setButtons(Dialog.OKCANCEL);
	    Label labetInfo = new Label();
	    labetInfo.setText("A valid URL has the following form - http|https|ftp://www.domain.com/path");
	    labetInfo.getElement().getStyle().setMarginBottom(10, Unit.PX);


	    labelError.getElement().getStyle().setColor("red");
	    showError("", false);

	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
//	    txt.setMinLength(2);
	    txtName.setFieldLabel(ConstantsExplorer.DIALOG_NAME);
	    textAreaDescription.setFieldLabel(ConstantsExplorer.DIALOG_DESCRIPTION);
	    textAreaDescription.setHeight(heightTextArea);

	    txtName.addKeyListener(new KeyListener() { // KEY ENTER

			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode())
					getButtonById(Dialog.OK).fireEvent(Events.Select);

			}
		});

	    txtUrl.setFieldLabel(ConstantsExplorer.DIALOG_URL);
        txtUrl.setAllowBlank(false);
        txtUrl.setEmptyText("E.g. http://www.domain.com/");
        //txtUrl.setRegex("^http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?$");
        //txtUrl.setRegex("^(ht|f)tp(s?)://");
//        txtUrl.getMessages().setRegexText("The field should be a valid URL! Ex. http://www.domain.com");
//        txtUrl.setAutoValidate(true);

        this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showError("", false);
				hide();
			}
		});


        this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				showError("", false);
				if(isValidForm()){
					hide();
				}else {
					String error = "URL must start with: ";
					for (int i=0; i<URL_PROTOCOL.length-1; i++) {
						error+=URL_PROTOCOL[i]+" or ";
					}
					error+=URL_PROTOCOL[URL_PROTOCOL.length-1];
					showError(error, true);
					txtUrl.forceInvalid(error);
				}
			}
		});

        add(labetInfo);
        add(txtUrl);
        add(labelError);
        add(txtName);
		add(textAreaDescription);
        setFocusWidget(txtUrl);
        this.show();
	}


	/**
	 * Show error.
	 *
	 * @param txt the txt
	 * @param bool the bool
	 */
	public void showError(String txt, boolean bool){
		labelError.setVisible(bool);
		labelError.setText(txt);
	}


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return txtName.getValue();
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return txtUrl.getValue();
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {

		if(textAreaDescription.getValue()!=null)
			if(textAreaDescription.getValue().length()>0)
					return textAreaDescription.getValue();

		return "";
	}

	/**
	 * Checks if is valid form.
	 *
	 * @return true, if is valid form
	 */
	public boolean isValidForm(){

		if(txtName.getValue() != null && !txtName.getValue().isEmpty() && txtUrl.getValue()!=null && !txtUrl.getValue().isEmpty()){
			for (String prefix : URL_PROTOCOL) {
				if(txtUrl.getValue().startsWith(prefix))
					return true;
			}
		}
		return false;
	}
}
