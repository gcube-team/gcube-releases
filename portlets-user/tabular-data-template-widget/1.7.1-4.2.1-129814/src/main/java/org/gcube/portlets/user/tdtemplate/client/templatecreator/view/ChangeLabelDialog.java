/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 14, 2015
 */
public class ChangeLabelDialog extends DialogBox implements ClickHandler{
	 
	private TextField<String> textField;
	private Button cancel;
	private Button okButton;
	private ChangeLabelDialog INSTANCE = this;
	private boolean isValidHide;
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		hide();
	}
	
	public ChangeLabelDialog(String title, String label) {
	    setText(title);
	    isValidHide = false;
//	    setWidth("200px");
	    
	    okButton = new Button("Ok");
	    cancel = new Button("Close");
	   
	    textField = new TextField<String>();
	    textField.setValue(label);
	    textField.setAllowBlank(false);
	    textField.focus();
//	    textField.selectAll();
	    textField.setSelectOnFocus(true);
	    
	    KeyListener listener = new KeyListener() {
	        @Override
	        public void componentKeyPress(ComponentEvent event) {
	        	super.componentKeyPress(event);
	            if(event.getKeyCode()==13)
	            	validateField();
	        }
	    };
	    textField.addKeyListener(listener);

	    DockPanel dock = new DockPanel();
	    dock.setSpacing(5);

//	    dock.add(okButton, DockPanel.SOUTH);
//	    dock.add(cancel, DockPanel.SOUTH);
	    
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(okButton);
//	    hp.setWidth("50%");
//	    hp.setStyleAttribute("margin", "0 auto");
	    hp.setHorizontalAlign(HorizontalAlignment.CENTER);
	    cancel.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
	    hp.add(cancel);
	    dock.add(hp, DockPanel.SOUTH);
	    
	    dock.add(textField, DockPanel.CENTER);
	    
	    dock.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    dock.setWidth("100%");
	    setWidget(dock);

	    okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				validateField();
			}
		});
	    
	    cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isValidHide = false;
				INSTANCE.hide();
			}
		});
	}
	
	private void validateField(){
		textField.validate();
		if(textField.getValue()!=null && !textField.getValue().isEmpty()){
			isValidHide = true;
			INSTANCE.hide();
		}
		
		textField.markInvalid("Field is mandatory");
	}

	/**
	 * @return the isValidHide
	 */
	public boolean isValidHide() {
		return isValidHide;
	}

	/**
	 * @return the okButton
	 */
	public Button getOkButton() {
		return okButton;
	}

	/**
	 * @return the textField
	 */
	public TextField<String> getTextField() {
		return textField;
	}


	/**
	 * @param textField the textField to set
	 */
	public void setTextField(TextField<String> textField) {
		this.textField = textField;
	}

	/**
	 * Gets the new label.
	 *
	 * @return the new label
	 */
	public String getNewLabel(){
		return textField.getValue();
	}
	
}
