package org.gcube.portlets.user.databasesmanager.client.resources;

import org.gcube.portlets.user.databasesmanager.client.DatabasesManager;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class Images {

	public static AbstractImagePrototype iconSearch() {
		return AbstractImagePrototype.create(DatabasesManager.resources
				.iconSearch());
	}

	public static AbstractImagePrototype iconCancel() {
		return AbstractImagePrototype.create(DatabasesManager.resources
				.iconCancel());
	}
	
	public static AbstractImagePrototype iconDatabase() {
		return AbstractImagePrototype.create(DatabasesManager.resources
				.iconDatabase());
	}
	
	public static AbstractImagePrototype iconSchema() {
		return AbstractImagePrototype.create(DatabasesManager.resources
				.iconSchema());
	}
}
