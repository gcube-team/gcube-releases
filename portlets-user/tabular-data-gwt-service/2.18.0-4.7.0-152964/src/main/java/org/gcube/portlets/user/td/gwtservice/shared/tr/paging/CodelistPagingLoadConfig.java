package org.gcube.portlets.user.td.gwtservice.shared.tr.paging;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CodelistPagingLoadConfig implements Serializable {

	private static final long serialVersionUID = -8831947012755493644L;

	protected int offset;
	protected int limit;
	protected ArrayList<OrderInfo> listOrderInfo;
	protected String filter;
	
	public CodelistPagingLoadConfig(){}
	
	public CodelistPagingLoadConfig(int offset, int limit, ArrayList<OrderInfo> listOrderInfo, String filter){
		this.offset=offset;
		this.limit=limit;
		this.listOrderInfo=listOrderInfo;
		this.filter=filter;
	}

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
		return "CodelistPagingLoadConfig [offset=" + offset + ", limit="
				+ limit + ", listOrderInfo=" + listOrderInfo + ", filter="
				+ filter + "]";
	}
	
	
}
