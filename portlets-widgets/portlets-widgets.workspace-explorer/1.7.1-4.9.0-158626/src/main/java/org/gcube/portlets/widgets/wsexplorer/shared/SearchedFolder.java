/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.shared;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class SearchedFolder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 12, 2017
 */
public class SearchedFolder implements IsSerializable{


	private Item folder;
	private int clientStartIndex = 0;
	private int limit;
	private int serverEndIndex = 0;
	private boolean isServerSearchFinished = false;


	/**
	 *
	 */
	public SearchedFolder() {
	}

	/**
	 * @param folder
	 * @param clientStartIndex
	 * @param limit
	 * @param serverEndIndex
	 * @param isServerSearchFinished
	 */
	public SearchedFolder(Item folder, int clientStartIndex, int limit, int serverEndIndex, boolean isServerSearchFinished) {

		super();
		this.folder = folder;
		this.clientStartIndex = clientStartIndex;
		this.limit = limit;
		this.serverEndIndex = serverEndIndex;
		this.isServerSearchFinished = isServerSearchFinished;
	}



	/**
	 * @return the folder
	 */
	public Item getFolder() {

		return folder;
	}



	/**
	 * @return the clientStartIndex
	 */
	public int getClientStartIndex() {

		return clientStartIndex;
	}



	/**
	 * @return the limit
	 */
	public int getLimit() {

		return limit;
	}



	/**
	 * @return the serverEndIndex
	 */
	public int getServerEndIndex() {

		return serverEndIndex;
	}



	/**
	 * @return the isServerSearchFinished
	 */
	public boolean isServerSearchFinished() {

		return isServerSearchFinished;
	}



	/**
	 * @param folder the folder to set
	 */
	public void setFolder(Item folder) {

		this.folder = folder;
	}



	/**
	 * @param clientStartIndex the clientStartIndex to set
	 */
	public void setClientStartIndex(int clientStartIndex) {

		this.clientStartIndex = clientStartIndex;
	}



	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {

		this.limit = limit;
	}



	/**
	 * @param serverEndIndex the serverEndIndex to set
	 */
	public void setServerEndIndex(int serverEndIndex) {

		this.serverEndIndex = serverEndIndex;
	}



	/**
	 * @param isServerSearchFinished the isServerSearchFinished to set
	 */
	public void setServerSearchFinished(boolean isServerSearchFinished) {

		this.isServerSearchFinished = isServerSearchFinished;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SearchedFolder [folder=");
		builder.append(folder);
		builder.append(", clientStartIndex=");
		builder.append(clientStartIndex);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", serverEndIndex=");
		builder.append(serverEndIndex);
		builder.append(", isServerSearchFinished=");
		builder.append(isServerSearchFinished);
		builder.append("]");
		return builder.toString();
	}




}
