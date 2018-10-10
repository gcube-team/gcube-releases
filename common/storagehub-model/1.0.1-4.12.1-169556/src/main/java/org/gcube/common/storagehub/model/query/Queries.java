package org.gcube.common.storagehub.model.query;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.SearchableGenericFile;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.Item;

public class Queries {

	private static Map<Class<?>,SearchableItem<?>> searchableRegistry = new HashMap<>();
	
	static {
		searchableRegistry.put(Item.class, GenericSearchableItem.get());
		searchableRegistry.put(AbstractFileItem.class, SearchableGenericFile.get());
		
	}
	
	public static <T extends Item> Query<SearchableItem<?>> queryFor(Class<T> searchable) {
		return new Query<>(searchableRegistry.get(searchable));
	}
	
}
