package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GetShareLinkEvent extends GwtEvent<GetSharedLinkEventHandler> {
  public static Type<GetSharedLinkEventHandler> TYPE = new Type<GetSharedLinkEventHandler>();

  private FileModel targetFile = null;
  
	public GetShareLinkEvent(FileModel target) {
		this.targetFile = target;
	}

	@Override
	public Type<GetSharedLinkEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GetSharedLinkEventHandler handler) {
		handler.onGetLink(this);
		
	}

	public FileModel getSourceFile() {
		return targetFile;
	}
}