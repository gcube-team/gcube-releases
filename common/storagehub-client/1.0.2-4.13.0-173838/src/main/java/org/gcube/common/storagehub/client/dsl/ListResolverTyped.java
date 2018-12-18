package org.gcube.common.storagehub.client.dsl;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.items.Item;

public class ListResolverTyped extends ListResolver {

	protected ListResolverTyped(ListRetriever retriever, ItemManagerClient itemClient) {
		super(retriever, itemClient);
	}

	public ListResolver ofType(Class<? extends Item> type){
		onlyType = type;
		return this;
	}
}
