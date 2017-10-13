package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;

@XmlRootElement(namespace=aquamapsTypesNS)
public class PagedRequestSettings {

	@XmlElement(namespace=aquamapsTypesNS)
	private int limit;
	
	@XmlElement(namespace=aquamapsTypesNS)
	private int offset;
	
	@XmlElement(namespace=aquamapsTypesNS)
	private String orderField;
	
	@XmlElement(namespace=aquamapsTypesNS)
	private OrderDirection orderDirection;
	
	public PagedRequestSettings() {
		// TODO Auto-generated constructor stub
	}

	public PagedRequestSettings(int limit, int offset, String orderField,
			OrderDirection orderDirection) {
		super();
		this.limit = limit;
		this.offset = offset;
		this.orderField = orderField;
		this.orderDirection = orderDirection;
	}

	/**
	 * @return the limit
	 */
	public int limit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void limit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the offset
	 */
	public int offset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void offset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the orderField
	 */
	public String orderField() {
		return orderField;
	}

	/**
	 * @param orderField the orderField to set
	 */
	public void orderField(String orderField) {
		this.orderField = orderField;
	}

	/**
	 * @return the orderDirection
	 */
	public OrderDirection orderDirection() {
		return orderDirection;
	}

	/**
	 * @param orderDirection the orderDirection to set
	 */
	public void orderDirection(OrderDirection orderDirection) {
		this.orderDirection = orderDirection;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PagedRequestSettings [limit=");
		builder.append(limit);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", orderField=");
		builder.append(orderField);
		builder.append(", orderDirection=");
		builder.append(orderDirection);
		builder.append("]");
		return builder.toString();
	}
	
}
