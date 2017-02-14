package org.gcube.portlets.user.tokengenerator.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The token generator portlet entry point. 
 * @author Massimiliano Assante, ISTI CNR
 * @author Costantino Perciante, ISTI CNR
 */
public class TokenGenerator implements EntryPoint {

	public void onModuleLoad() {
		RootPanel.get("token-generator-DIV").add(new TokenContainer());
		RootPanel.get("token-generator-DIV").getElement().getStyle().setPadding(2, Unit.PX);
	}

}
