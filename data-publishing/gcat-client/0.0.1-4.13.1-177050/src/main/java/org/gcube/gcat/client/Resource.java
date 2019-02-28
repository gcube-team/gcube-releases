package org.gcube.gcat.client;

import java.net.MalformedURLException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Resource extends GCatClient implements org.gcube.gcat.api.interfaces.Resource<String,Void> {
	
	public Resource() throws MalformedURLException {
		super(Item.ITEMS);
	}

	@Override
	public String list(String itemID) {
		return super.list(null, itemID, RESOURCES);
	}

	@Override
	public String create(String itemID, String json) {
		return super.create(json, itemID, RESOURCES);
	}

	@Override
	public String read(String itemID, String resourceID) {
		return super.read(itemID, RESOURCES, resourceID);
	}

	@Override
	public String update(String itemID, String resourceID, String json) {
		return super.read(json, itemID, RESOURCES, resourceID);
	}

	@Override
	public Void delete(String itemID, String resourceID) {
		super.delete(null, itemID, RESOURCES, resourceID);
		return null;
	}

	
}
