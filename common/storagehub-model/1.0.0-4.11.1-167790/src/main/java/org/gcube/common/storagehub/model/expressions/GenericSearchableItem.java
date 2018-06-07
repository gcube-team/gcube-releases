package org.gcube.common.storagehub.model.expressions;

import java.util.Calendar;

import org.gcube.common.storagehub.model.items.Item;

public class GenericSearchableItem extends SearchableItem<Item>{

	private static GenericSearchableItem item = new GenericSearchableItem();
	
	public static GenericSearchableItem get() {
		return item;
	}
	
	protected GenericSearchableItem() {
		super(Item.class,"nthl:workspaceItem");
	}
	
	public final SearchableField<String> title = new SearchableField<String>(String.class, "jcr:title");
	
	public final SearchableField<Calendar> lastModification = new SearchableField<Calendar>(Calendar.class, "jcr:lastModified");
	
	public final SearchableField<Calendar> creationTime = new SearchableField<Calendar>(Calendar.class, "jcr:created");
	
}
