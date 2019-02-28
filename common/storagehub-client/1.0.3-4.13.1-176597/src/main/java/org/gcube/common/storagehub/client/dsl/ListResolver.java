package org.gcube.common.storagehub.client.dsl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.items.Item;

public class ListResolver {

	ListRetriever retriever;
	ItemManagerClient itemClient;
	
	
	Set<String> excludes = new HashSet<>(Arrays.asList(NodeConstants.ACCOUNTING_NAME, NodeConstants.CONTENT_NAME, NodeConstants.METADATA_NAME));
	Class<? extends Item> onlyType = null;
	boolean includeHidden = false;
	
	protected ListResolver(ListRetriever retriever, ItemManagerClient itemClient) {
		this.retriever = retriever;
		this.itemClient = itemClient;
	}
	
	public ListResolver withAccounting(){
		excludes.remove(NodeConstants.ACCOUNTING_NAME);
		return this;
	}
	
	public ListResolver withContent(){
		excludes.remove(NodeConstants.CONTENT_NAME);
		return this;
	}
	
	public ListResolver withMetadata(){
		excludes.remove(NodeConstants.METADATA_NAME);
		return this;
	}
	
	
	public List<? extends Item> getItems(){
		return retriever.getList(onlyType, includeHidden,  excludes.toArray(new String[excludes.size()]));
	}
	
	public List<ItemContainer<? extends Item>> getContainers(){
		List<? extends Item> items = getItems();
		List<ItemContainer<? extends Item>> toReturn = items.stream().map(i -> Util.getContainerFromItem(i, itemClient)).collect(Collectors.toList());
		return toReturn;
	}
}
