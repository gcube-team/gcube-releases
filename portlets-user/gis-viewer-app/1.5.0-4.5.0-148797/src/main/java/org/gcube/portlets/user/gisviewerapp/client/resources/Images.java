package org.gcube.portlets.user.gisviewerapp.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class Images.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 12, 2016
 */
public class Images {

	public static Resources resources = GWT.create(Resources.class);

	/**
	 * Icon warning.
	 *
	 * @return the abstract image prototype
	 */
	public static AbstractImagePrototype iconWarning() {
		return AbstractImagePrototype.create(resources.iconWarning());
	}
}
