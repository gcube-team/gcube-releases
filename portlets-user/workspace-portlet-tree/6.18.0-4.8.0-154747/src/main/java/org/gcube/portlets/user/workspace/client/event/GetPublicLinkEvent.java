package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GetPublicLinkEvent extends GwtEvent<GetPublicLinkEventHandler> {
  public static Type<GetPublicLinkEventHandler> TYPE = new Type<GetPublicLinkEventHandler>();

  private FileModel targetFile = null;
  
	public GetPublicLinkEvent(FileModel target) {
		this.targetFile = target;
	}

	@Override
	public Type<GetPublicLinkEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GetPublicLinkEventHandler handler) {
		handler.onGetPublicLink(this);
		
	}

	public FileModel getSourceFile() {
		return targetFile;
	}
}