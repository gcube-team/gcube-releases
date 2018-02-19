package org.gcube.portlets.widgets.wsmail.client.multisuggests;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 *
 */
public class BulletList extends ComplexPanel {
	public BulletList() {
		setElement(DOM.createElement("ul"));
	}

	public void add(Widget w) {
		super.add(w, getElement());
	}

	public void insert(Widget w, int beforeIndex) {
		super.insert(w, getElement(), beforeIndex, true);
	}
}

