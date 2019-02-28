package org.gcube.portlets.user.dataminerexecutor.server.storage;

import java.io.InputStream;

import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class ItemDownload {
	private ItemDescription itemDescription;
	private InputStream inputStream;

	public ItemDownload(ItemDescription itemDescription, InputStream inputStream) {
		super();
		this.itemDescription = itemDescription;
		this.inputStream = inputStream;
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String toString() {
		return "ItemDownload [itemDescription=" + itemDescription + ", inputStream=" + inputStream + "]";
	}

}
