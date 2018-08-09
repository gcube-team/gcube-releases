package org.gcube.common.storagehub.model.expressions;

import java.util.Calendar;

import org.gcube.common.storagehub.model.items.AbstractFileItem;

public class SearchableGenericFile extends SearchableItem<AbstractFileItem>{

	private static SearchableGenericFile item = new SearchableGenericFile();
	
	public static SearchableGenericFile get() {
		return item;
	}
	
	protected SearchableGenericFile() {
		super(AbstractFileItem.class, "nthl:workspaceLeafItem");
	}

	public final SearchableField<String> title = new SearchableField<String>(String.class, "jcr:title");
	
	public final SearchableField<Calendar> lastModification = new SearchableField<Calendar>(Calendar.class, "jcr:lastModified");
	
	public final SearchableField<Calendar> creationTime = new SearchableField<Calendar>(Calendar.class, "jcr:created");
	
}
