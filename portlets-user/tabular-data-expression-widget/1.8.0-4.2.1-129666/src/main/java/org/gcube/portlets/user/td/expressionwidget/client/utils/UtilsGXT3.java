package org.gcube.portlets.user.td.expressionwidget.client.utils;

import com.google.gwt.dom.client.Element;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class UtilsGXT3 {
	public static void mask(Element element) {
		XElement el = element.<XElement> cast();
		el.mask("Loading...");
	}

	public static void umask(Element element) {
		element.<XElement> cast().unmask();
	}
	
	
	public static void alert(String title, String message) {
		final AlertMessageBox d = new AlertMessageBox(title, message);
		d.addHideHandler(new HideHandler() {

			public void onHide(HideEvent event) {

			}
		});
		d.show();

	}
	
	public static void info(String title, String message) {
		final InfoMessageBox d = new InfoMessageBox(title, message);
		d.addHideHandler(new HideHandler() {

			public void onHide(HideEvent event) {

			}
		});
		d.show();

	}
	
}
