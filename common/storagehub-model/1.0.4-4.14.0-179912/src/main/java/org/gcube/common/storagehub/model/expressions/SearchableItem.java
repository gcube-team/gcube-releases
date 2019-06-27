package org.gcube.common.storagehub.model.expressions;

import org.gcube.common.storagehub.model.items.Item;

public class SearchableItem<T extends Item> {

	private Class<T> type;
	private String nodeValue;
	
	protected SearchableItem(Class<T> _class, String nodeValue){
		this.type = _class;
		this.nodeValue = nodeValue;
	}

	public Class<T> getType() {
		return type;
	}

	public String getNodeValue() {
		return nodeValue;
	}
	
}
