package org.gcube.portlets.admin.fhn_manager_portlet.client.resources;

public enum ImageType {

	GRID_BUTTON("imageButton"),
	NAVIGATOR_BUTTON("imageNavigator"),
	RESOURCE_ICON("imageIcon"),
	DIALOG_ICON("dialogIcon"),
	PINNED_RESOURCE("pinnedResourceImage"),
	PINNED_BUTTON("pinnedCloseButton");
	
	private String associatedCss;

	private ImageType(String associatedCss) {
		this.associatedCss = associatedCss;
	}
	
	public String getAssociatedCss() {
		return associatedCss;
	}
}
