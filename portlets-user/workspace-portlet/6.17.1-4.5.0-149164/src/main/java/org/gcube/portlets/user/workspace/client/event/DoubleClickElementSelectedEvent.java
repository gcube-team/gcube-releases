package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileGridModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DoubleClickElementSelectedEvent extends GwtEvent<DoubleClickElementSelectedEventHandler> {
  public static Type<DoubleClickElementSelectedEventHandler> TYPE = new Type<DoubleClickElementSelectedEventHandler>();

  private FileGridModel targetFile = null;
  
	public DoubleClickElementSelectedEvent(ModelData target) {
		this.targetFile = (FileGridModel) target;
	}

	@Override
	public Type<DoubleClickElementSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(DoubleClickElementSelectedEventHandler handler) {
		handler.onDoubleClickElementGrid(this);
		
	}

	public FileGridModel getSourceFile() {
		return targetFile;
	}
}