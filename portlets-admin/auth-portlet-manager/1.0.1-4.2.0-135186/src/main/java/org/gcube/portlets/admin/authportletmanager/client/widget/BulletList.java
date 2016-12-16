package org.gcube.portlets.admin.authportletmanager.client.widget;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class BulletList extends ComplexPanel {
	@SuppressWarnings("deprecation")
	public BulletList() {
		setElement(DOM.createElement("UL"));
	}

	@SuppressWarnings("deprecation")
	public void add(Widget w) {
		super.add(w, getElement());
	}

	@SuppressWarnings("deprecation")
	public void insert(Widget w, int beforeIndex) {
		super.insert(w, getElement(), beforeIndex, true);
	}
}

