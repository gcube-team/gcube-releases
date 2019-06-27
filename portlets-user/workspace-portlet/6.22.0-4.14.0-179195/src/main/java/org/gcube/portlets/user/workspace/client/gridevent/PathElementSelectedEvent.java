package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class PathElementSelectedEvent extends GwtEvent<PathElementSelectedEventHandler> {
  public static Type<PathElementSelectedEventHandler> TYPE = new Type<PathElementSelectedEventHandler>();

  private FileModel targetFile = null;
  
	public PathElementSelectedEvent(FileModel target) {
	this.targetFile = target;
	}

	@Override
	public Type<PathElementSelectedEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}
	
	@Override
	protected void dispatch(PathElementSelectedEventHandler handler) {
		handler.onPathElementSelected(this);
		
	}

	public FileModel getSourceFile() {
		return targetFile;
	}
}