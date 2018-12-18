package org.gcube.common.storagehub.client.dsl;

import java.util.List;

import org.gcube.common.storagehub.model.items.Item;

public interface ListRetriever {

	List<? extends Item> getList(Class<? extends Item> onlyType, String ... excludes);
}
