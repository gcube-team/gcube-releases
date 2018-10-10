package org.gcube.portlets.user.td.gwtservice.shared.tr.paging;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CodelistPagingLoadResult implements Serializable {

	private static final long serialVersionUID = -8831947012755493644L;

	protected int offset;
	protected int limit;
	protected int totalLenght;
	protected ArrayList<TabResource> ltr;
	protected ArrayList<OrderInfo> listOrderInfo;
	protected String filter;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotalLenght() {
		return totalLenght;
	}

	public void setTotalLenght(int totalLenght) {
		this.totalLenght = totalLenght;
	}

	public ArrayList<TabResource> getLtr() {
		return ltr;
	}

	public void setLtr(ArrayList<TabResource> ltr) {
		this.ltr = ltr;
	}

	public ArrayList<OrderInfo> getListOrderInfo() {
		return listOrderInfo;
	}

	public void setListOrderInfo(ArrayList<OrderInfo> listOrderInfo) {
		this.listOrderInfo = listOrderInfo;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return "CodelistPagingLoadResult [offset=" + offset + ", limit="
				+ limit + ", totalLenght=" + totalLenght + ", ltr=" + ltr
				+ ", listOrderInfo=" + listOrderInfo + ", filter=" + filter
				+ "]";
	}

}
