package org.gcube.portlets.user.workspace.client.gridevent;

import org.gcube.portlets.user.workspace.client.model.FileGridModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class GridElementSelectedEvent extends GwtEvent<GridElementSelectedEventHandler> {
  public static Type<GridElementSelectedEventHandler> TYPE = new Type<GridElementSelectedEventHandler>();

  private FileGridModel targetFile = null;

  private boolean isMultiSelection;
  
	public GridElementSelectedEvent(ModelData target, boolean isMultiSelection) {
		this.targetFile = (FileGridModel) target;
		this.isMultiSelection = isMultiSelection;
	}

	@Override
	public Type<GridElementSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(GridElementSelectedEventHandler handler) {
		handler.onGridElementSelected(this);
		
	}

	public FileGridModel getSourceFile() {
		return targetFile;
	}

	public boolean isMultiSelection() {
		return isMultiSelection;
	}
}