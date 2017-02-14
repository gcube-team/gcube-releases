package org.gcube.data.harmonization.occurrence.impl.model;

public class PagedRequestSettings {

	public static enum Order{
		ASC,DESC
	}
	
	
	private String orderByField;
	private Order order; 
	private long offset;
	private long pageSize;
	
	
	
	public PagedRequestSettings() {
		// TODO Auto-generated constructor stub
	}



	
	
	
	public PagedRequestSettings(String orderByField, Order order, long offset,
			long pageSize) {
		super();
		this.orderByField = orderByField;
		this.order = order;
		this.offset = offset;
		this.pageSize = pageSize;
	}






	/**
	 * @return the orderByField
	 */
	public String getOrderByField() {
		return orderByField;
	}



	/**
	 * @param orderByField the orderByField to set
	 */
	public void setOrderByField(String orderByField) {
		this.orderByField = orderByField;
	}



	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}



	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}



	/**
	 * @return the offset
	 */
	public long getOffset() {
		return offset;
	}



	/**
	 * @param offset the offset to set
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}



	/**
	 * @return the pageSize
	 */
	public long getPageSize() {
		return pageSize;
	}



	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}






	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PagedRequestSettings [orderByField=");
		builder.append(orderByField);
		builder.append(", order=");
		builder.append(order);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", pageSize=");
		builder.append(pageSize);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
