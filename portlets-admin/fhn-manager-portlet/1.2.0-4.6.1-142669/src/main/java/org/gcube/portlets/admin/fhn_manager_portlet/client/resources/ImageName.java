package org.gcube.portlets.admin.fhn_manager_portlet.client.resources;

import com.google.gwt.resources.client.ImageResource;


public enum ImageName {

	
	DELETE(Icons.ICONS.delete()),
	CREATE(Icons.ICONS.create()),
	REFRESH(Icons.ICONS.refresh()),
	INSPECT(Icons.ICONS.inspect()),
	START(Icons.ICONS.start()),
	STOP(Icons.ICONS.stop()),
	LOADING(Icons.ICONS.loading()),
	ERROR(Icons.ICONS.error()),
	SUCCESS(Icons.ICONS.success()),
	CLOSE(Icons.ICONS.close()),
	
	
	VM_PROVIDER(Icons.ICONS.vmProvider()),
	REMOTE_NODE(Icons.ICONS.remoteNode()),
	SERVICE_PROFILE(Icons.ICONS.serviceProfile()),
	VM_TEMPLATE(Icons.ICONS.vmTemplate());	
	
	
	private ImageResource theImg;

	private ImageName(ImageResource theImg) {
		this.theImg = theImg;
	}
	
	public ImageResource getTheImg() {
		return theImg;
	}
}
