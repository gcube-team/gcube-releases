package org.gcube.portlets.user.gcubewidgets.client.elements;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 *
 */
public class Div extends HTML implements HasText {

    public Div() {
        super(DOM.createElement("div"));
    }

    public Div(String text) {
        this();
        setText(text);
    }
}
