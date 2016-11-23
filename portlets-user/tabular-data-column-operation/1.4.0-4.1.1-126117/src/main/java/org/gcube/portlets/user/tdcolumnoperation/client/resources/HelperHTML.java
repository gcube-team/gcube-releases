/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client.resources;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 12, 2014
 * 
 */
public class HelperHTML extends Dialog {

	/**
	 * 
	 */
	private static final int _400 = 400;

	/**
	 * 
	 */
	public HelperHTML(String heading, String textAsHtml) {
		setWidth(_400);
		setHeight(_400);
		setHeadingText(heading);
		ContentPanel cp = new ContentPanel();
		cp.setBodyBorder(false);
		cp.setHeaderVisible(false);
		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTO);
		HTML txt = new HTML(textAsHtml);
		txt.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
		v.add(txt);
		setHideOnButtonClick(true);
		cp.add(v);
		add(cp);
	}

}
