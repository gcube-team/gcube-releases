package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GXTCategorySmartFolder;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class SmartFolderSelectedEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 */
public class SmartFolderSelectedEvent extends GwtEvent<SmartFolderSelectedEventHandler> implements GuiEventInterface{
	public static Type<SmartFolderSelectedEventHandler> TYPE = new Type<SmartFolderSelectedEventHandler>();

	private GXTCategorySmartFolder category;

	private String smartFolderCustomId;

	private String smartFolderCustomName;

	private String query;

	/**
	 * Instantiates a new smart folder selected event.
	 *
	 * @param category the category
	 */
	public SmartFolderSelectedEvent(GXTCategorySmartFolder category) {
		this.category = category;
	}

	
	public SmartFolderSelectedEvent(String identifier, String name, String query) {
		this.smartFolderCustomId = identifier;
		this.smartFolderCustomName = name;
		this.query = query;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SmartFolderSelectedEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SmartFolderSelectedEventHandler handler) {
		handler.onSmartFolderSelected(this);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		// TODO Auto-generated method stub
		return EventsTypeEnum.SMART_FOLDER_EVENT;
	}


	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public GXTCategorySmartFolder getCategory() {
		return category;
	}


	/**
	 * @return the smartFolderCustomId
	 */
	public String getSmartFolderCustomId() {

		return smartFolderCustomId;
	}


	/**
	 * @return the smartFolderCustomName
	 */
	public String getSmartFolderCustomName() {

		return smartFolderCustomName;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {

		return query;
	}


}
