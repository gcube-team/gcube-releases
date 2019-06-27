package org.gcube.common.storagehub.client.dsl;

import java.util.List;

import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.Item;

public interface ListRetriever {

	List<? extends Item> getList(Class<? extends Item> onlyType, boolean includeHidden, String ... excludes) throws StorageHubException;
}
