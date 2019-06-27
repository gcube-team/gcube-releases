/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.client.viewbinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 21, 2019
 */
public class PortletTitle extends Composite {

	private static PortletTitleUiBinder uiBinder =
		GWT.create(PortletTitleUiBinder.class);

	@UiField
	HTML thePageHeader;

	interface PortletTitleUiBinder extends UiBinder<Widget, PortletTitle> {
	}

	/**
	 * Because this class has a default constructor, it can be used as a binder
	 * template. In other words, it can be used in other *.ui.xml files as
	 * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 * xmlns:g="urn:import:**user's package**">
	 * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
	 * depending on the widget that is used, it may be necessary to implement
	 * HasHTML instead of HasText.
	 */
	public PortletTitle(String title) {

		initWidget(uiBinder.createAndBindUi(this));
		thePageHeader.setText(title);
	}
	public void setText(String text) {

	}
}
