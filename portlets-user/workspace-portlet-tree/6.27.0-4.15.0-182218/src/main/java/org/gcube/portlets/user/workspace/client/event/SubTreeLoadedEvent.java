package org.gcube.portlets.user.workspace.client.event;

import java.util.ArrayList;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class SubTreeLoadedEvent extends GwtEvent<SubTreeLoadedEventHandler> implements GuiEventInterface{
	public static Type<SubTreeLoadedEventHandler> TYPE = new Type<SubTreeLoadedEventHandler>();
	

	private ArrayList<FileModel> pathParentsList = null;

	
	public SubTreeLoadedEvent(ArrayList<FileModel>  pathParentsList) {
	
		this.pathParentsList = pathParentsList;
	}

	@Override
	public Type<SubTreeLoadedEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(SubTreeLoadedEventHandler handler) {
		handler.onSubTreeLoaded(this);
		
	}

	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.SUBTREE_LOAD_EVENT;
	}

	public ArrayList<FileModel> getPathParentsList() {
		return pathParentsList;
	}
	
	
}
