package org.gcube.portlets.user.workspace.client.view.sharing.multisuggest;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 * changed by Francesco Mangiacrapa
 *
 */
public class Paragraph extends Widget implements HasText {
	
	boolean isRemovable = true;

	public Paragraph() {
        setElement(DOM.createElement("p"));
    }

    public Paragraph(String text) {
        this();
        setText(text);
    }

    public String getText() {
        return getElement().getInnerText();
    }

    public void setText(String text) {
        getElement().setInnerText(text);
    }
    
    public boolean isRemovable(){
		return isRemovable;
    }
    
	public void setRemovable(boolean isRemovable) {
		this.isRemovable = isRemovable;
	}
}
