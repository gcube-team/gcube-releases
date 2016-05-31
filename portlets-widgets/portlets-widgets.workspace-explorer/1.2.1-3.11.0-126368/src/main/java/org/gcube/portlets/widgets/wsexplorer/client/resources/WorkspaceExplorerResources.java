package org.gcube.portlets.widgets.wsexplorer.client.resources;

import com.github.gwtbootstrap.client.ui.constants.BaseIconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class WorkspaceExplorerResources {

	public static final WorkspaceExplorerIcons ICONS = GWT.create(WorkspaceExplorerIcons.class);
	 /** Inject the icon's css once at first usage */
    static {
    	WorkspaceExplorerIcons icons = GWT.create(WorkspaceExplorerIcons.class);
        icons.css().ensureInjected();
    }

	public static AbstractImagePrototype getIconCancel() {

		return AbstractImagePrototype.create(ICONS.cancel());
	}

	public static AbstractImagePrototype getIconLoading() {

		return AbstractImagePrototype.create(ICONS.loading());
	}

	public static AbstractImagePrototype getIconInfo() {

		return AbstractImagePrototype.create(ICONS.info());
	}

	/** My custom base icon collection */
	public enum CustomIconType implements BaseIconType {

	    home, // Our runtime access
	    vre_folder,
	    new_folder;

	    private static final String PREFIX = "myBaseIcon_";
	    private String className;

	    private CustomIconType() {
	        this.className = this.name().toLowerCase();
	    }
	    @Override public String get() {
	        return PREFIX + className;
	    }
	}
}
