package org.gcube.portlets.widgets.wsmail.client.multisuggests;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 *
 */
public class Paragraph extends Widget implements HasText {

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
}
