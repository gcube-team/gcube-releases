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
import com.google.gwt.user.client.ui.Label;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DialogAddUrl extends Dialog {
	
	private int widthDialog = 500;
	private int heightTextArea = 150;
	private TextField<String> txtName;
	private TextField<String> txtUrl = new TextField<String>();
	private TextArea textAreaDescription = new TextArea();

	
	public DialogAddUrl(String headerTitle) {
		
	    FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(300);
	    setLayout(layout);
	    this.setIcon(Resources.getIconAddUrl());
	    
	    setButtonAlign(HorizontalAlignment.CENTER);
//	    setHideOnButtonClick(true);
//	    setIcon(IconHelper.createStyle("user"));
	    setHeading(ConstantsExplorer.MESSAGE_ADD_URL_IN + " "+ headerTitle);
	    setModal(true);
//	    setBodyBorder(true);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(widthDialog);
	    setResizable(false);
	    setButtons(Dialog.OKCANCEL);
//	    this.getButtonById(Dialog.CANCEL).setText("Reset");
	    
	    
	    Label labetInfo = new Label();
//	    labetInfo.setText("If you want create an http/ftp uri, you must include one of this prefix: \"http://\" or \"ftp://\"");
//	    labetInfo.setText("An correct URL for document directly displayable through browser must have the following form - http://host.name/path");
//	    labetInfo.setText("An correct URL must have the following form http://host.name/path");
	    labetInfo.setText("A correct url (for a document displayable directly through the browser) has the following form - http://host.name/path");

	    
	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
//	    txt.setMinLength(2);
	    txtName.setFieldLabel(ConstantsExplorer.DIALOG_NAME);
//	    txtName.setValue(msgTxt);

	    textAreaDescription.setFieldLabel(ConstantsExplorer.DIALOG_DESCRIPTION);
	    textAreaDescription.setHeight(heightTextArea);
	    
//	    formData = new FormData("-20");  
//	    vp = new VerticalPanel();  
//	    vp.setSpacing(10);
	    
	    
//	    textAreaDescription.addKeyListener(new KeyListener() { // KEY ENTER
//		    
//			public void componentKeyPress(ComponentEvent event) {
//				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode()) 
//					getButtonById(Dialog.OK).fireEvent(Events.OnClick);
//				
//			}
//		});
	    
	    
	    txtName.addKeyListener(new KeyListener() { // KEY ENTER
		    
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode()) 
					getButtonById(Dialog.OK).fireEvent(Events.Select);
				
			}
		});
	    
	    
	    
	    
	    txtUrl.setFieldLabel(ConstantsExplorer.DIALOG_URL);
        txtUrl.setAllowBlank(false);
        txtUrl.setEmptyText("http://host.name/path");
//        txtUrl.setRegex("^http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?$");
        
        
//        txtUrl.setRegex("^(ht|f)tp(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$");
//        
//        txtUrl.getMessages().setRegexText("The field should be a valid URL! Ex. http://www.domain.com");
//        txtUrl.setAutoValidate(true);

        this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
//				txtUrl.reset();
//				txtName.reset();
//				textAreaDescription.reset();
				hide();
			}
		});
        
	    
        this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(isValidForm())
					hide();
			}
		});
        

		
        
        add(txtUrl);
        add(txtName);
		add(textAreaDescription);
        add(labetInfo);

        setFocusWidget(txtName);
        this.show();
	}


	public String getName() {
		return txtName.getValue();
	}

	public String getUrl() {
		return txtUrl.getValue();
	}

	public String getDescription() {
		
		if(textAreaDescription.getValue()!=null)
			if(textAreaDescription.getValue().length()>0)
					return textAreaDescription.getValue();
		
		return "";
	}
	
	public boolean isValidForm(){
		
		if(txtName.getValue() != null)
			return true;
		
		return false;
		
	}
	
//	private boolean isValidUrl(){
//
//		try {
//		    URL url = new URL(txtUrl.getValue());
//		    URLConnection conn = url.openConnection();
//		    conn.connect();
//		} catch (MalformedURLException e) {
//			System.out.println(ConstantsExplorer.ERRORURLNOTREACHABLE);
//			return false;
//		} catch (IOException e) {
//			System.out.println(ConstantsExplorer.ERRORURLNOTREACHABLE);
//			return false;
//		}
//		
//		return true;
//	}
}
