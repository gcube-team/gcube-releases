package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GetInfoEvent extends GwtEvent<GetInfoEventHandler> {
  public static Type<GetInfoEventHandler> TYPE = new Type<GetInfoEventHandler>();

  private FileModel targetFile = null;
  
	public GetInfoEvent(FileModel target) {
		this.targetFile = target;
	}

	@Override
	public Type<GetInfoEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GetInfoEventHandler handler) {
		handler.onGetInfo(this);
		
	}

	public FileModel getSourceFile() {
		return targetFile;
	}
}