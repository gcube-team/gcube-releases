package org.gcube.portlets.user.td.expressionwidget.client.custom;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface IconButtonAppearance {
	  void render(SafeHtmlBuilder sb);
	  void onUpdateIcon(XElement parent, ImageResource icon);
}
