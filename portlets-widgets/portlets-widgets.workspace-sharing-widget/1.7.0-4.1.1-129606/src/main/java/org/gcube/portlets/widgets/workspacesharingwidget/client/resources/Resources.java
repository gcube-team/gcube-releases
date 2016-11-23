package org.gcube.portlets.widgets.workspacesharingwidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
 * 
 */
public class Resources {

	public static final Icons ICONS = GWT.create(Icons.class);

	public static AbstractImagePrototype getIconWriteOwn() {
		return AbstractImagePrototype.create(ICONS.writeown());
	}

	public static AbstractImagePrototype getIconWriteAll() {
		return AbstractImagePrototype.create(ICONS.writeall());
	}

	public static AbstractImagePrototype getIconReadOnly() {
		return AbstractImagePrototype.create(ICONS.readonly());
	}

	public static AbstractImagePrototype getIconAdministrator() {
		return AbstractImagePrototype.create(ICONS.administrator());
	}

	public static AbstractImagePrototype getIconUsers() {

		return AbstractImagePrototype.create(ICONS.users());
	}

	public static AbstractImagePrototype getIconInfo() {

		return AbstractImagePrototype.create(ICONS.info());
	}

	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconShare() {
		return AbstractImagePrototype.create(ICONS.share());
	}

	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconUser() {
		return AbstractImagePrototype.create(ICONS.user());
	}

	/**
	 * @return
	 */
	public static AbstractImagePrototype getIconGroup() {
		return AbstractImagePrototype.create(ICONS.group());
	}

	public static AbstractImagePrototype getAllLeft() {
		return AbstractImagePrototype.create(ICONS.allLeft());
	}

	public static AbstractImagePrototype getAllRight() {
		return AbstractImagePrototype.create(ICONS.allRight());
	}

	public static AbstractImagePrototype getSelectedLeft() {
		return AbstractImagePrototype.create(ICONS.selectedLeft());
	}

	public static AbstractImagePrototype getSelectedRight() {
		return AbstractImagePrototype.create(ICONS.selectedRight());
	}

}
