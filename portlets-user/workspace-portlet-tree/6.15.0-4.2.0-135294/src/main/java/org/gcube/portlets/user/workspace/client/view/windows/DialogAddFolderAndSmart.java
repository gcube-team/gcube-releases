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


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DialogAddFolderAndSmart extends Dialog {
	
	private int widthDialog = 500;
	private int heightTextArea = 150;
	private TextField<String> txtName;
	private TextArea textAreaDescription = new TextArea();
	public enum AddType {FOLDER, SMARTFOLDER};

	
	public DialogAddFolderAndSmart(String headerTitle, AddType type) {
		
	    FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(300);
	    setLayout(layout);

	    setButtonAlign(HorizontalAlignment.CENTER);
//	    setHideOnButtonClick(true);
//	    setIcon(IconHelper.createStyle("user"));
	    if(type.equals(AddType.FOLDER)){
	    	setHeading(ConstantsExplorer.MESSAGE_ADD_FOLDER_IN + " "+ headerTitle);
		    setIcon(Resources.getIconAddFolder());
	    }
	    else{
	    	setHeading(ConstantsExplorer.MESSAGE_ADD_SMART_FOLDER + " "+ headerTitle);
	    	setIcon(Resources.getIconStar());
	    }
	    setModal(true);
//	    setBodyBorder(true);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(widthDialog);
	    setResizable(false);
	    setButtons(Dialog.OKCANCEL);
//	    this.getButtonById(Dialog.CANCEL).setText("Reset");
	    
	    txtName = new TextField<String>();
	    txtName.setAllowBlank(false);
//	    txtName.setRegex("^[a-zA-Z0-9]+[ ]*[a-zA-Z0-9_-]+$");
//	    txtName.setRegex("^[a-zA-Z0-9_]+[-]*[a-zA-Z0-9_\\s]*$");
	 
//	    txtName.setRegex("^[a-zA-Z0-9]+[^.<>\\|?/*%$]*$");
	    
	    txtName.setAutoValidate(true);
	    txtName.getMessages().setRegexText(ConstantsExplorer.REGEX_FOLDER_NAME+": .<>\\|?/*%$ or contains / or \\");
	    txtName.setRegex("^[^.<>\\|?/*%$]+[^\\/]*$");

//	    txt.setMinLength(2);
	    txtName.setFieldLabel(ConstantsExplorer.DIALOG_NAME);
//	    txtName.setValue(msgTxt);

	    textAreaDescription.setFieldLabel(ConstantsExplorer.DIALOG_DESCRIPTION);
	    textAreaDescription.setHeight(heightTextArea);
//	    textAreaDescription.setAllowBlank(false);
	    
//	    formData = new FormData("-20");  
//	    vp = new VerticalPanel();  
//	    vp.setSpacing(10);
	    
	    

	    txtName.addKeyListener(new KeyListener() { // KEY ENTER
	    
			public void componentKeyPress(ComponentEvent event) {
				if (event.getKeyCode() == KeyboardEvents.Enter.getEventCode()) 
					getButtonById(Dialog.OK).fireEvent(Events.Select);
				
			}
		});
	   
	    
        this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
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
        
        setFocusWidget(txtName);
        add(txtName);
		add(textAreaDescription);
      
        this.show();
	}


	public String getName() {
		return txtName.getValue();
	}

	public String getDescription() {
		if(textAreaDescription.getValue()==null)
			return "";
		
		return textAreaDescription.getValue();
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
	
	public boolean isValidForm(){
		
		if(txtName.isValid() && txtName.getValue() != null)
			return true;
		
		return false;
		
	}
}
