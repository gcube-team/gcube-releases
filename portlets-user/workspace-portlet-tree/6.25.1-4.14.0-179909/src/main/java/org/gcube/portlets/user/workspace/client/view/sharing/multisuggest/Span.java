package org.gcube.portlets.user.workspace.client.view.sharing.multisuggest;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 *
 */
public class Span extends HTML implements HasText {

    public Span() {
        super(DOM.createElement("span"));
    }

    public Span(String text) {
        this();
        setText(text);
    }
}
