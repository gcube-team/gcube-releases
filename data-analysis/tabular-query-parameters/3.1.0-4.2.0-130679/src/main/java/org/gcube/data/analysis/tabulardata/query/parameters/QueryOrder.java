package org.gcube.data.analysis.tabulardata.query.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

@XmlRootElement(name="Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryOrder {

	private ColumnLocalId orderingColumnId;

	private QueryOrderDirection orderingDirection;
	
	@SuppressWarnings("unused")
	private QueryOrder() {}

	public QueryOrder(ColumnLocalId orderingColumnId, QueryOrderDirection orderingDirection) {
		this.orderingColumnId = orderingColumnId;
		this.orderingDirection = orderingDirection;
	}

	public ColumnLocalId getOrderingColumnId() {
		return orderingColumnId;
	}

	public void setOrderingColumnId(ColumnLocalId orderingColumnId) {
		this.orderingColumnId = orderingColumnId;
	}

	public QueryOrderDirection getOrderingDirection() {
		return orderingDirection;
	}

	public void setOrderingDirection(QueryOrderDirection orderingDirection) {
		this.orderingDirection = orderingDirection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderingColumnId == null) ? 0 : orderingColumnId.hashCode());
		result = prime * result + ((orderingDirection == null) ? 0 : orderingDirection.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryOrder other = (QueryOrder) obj;
		if (orderingColumnId == null) {
			if (other.orderingColumnId != null)
				return false;
		} else if (!orderingColumnId.equals(other.orderingColumnId))
			return false;
		if (orderingDirection != other.orderingDirection)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryOrder [orderingColumnId=");
		builder.append(orderingColumnId);
		builder.append(", orderingDirection=");
		builder.append(orderingDirection);
		builder.append("]");
		return builder.toString();
	}
	
	

}
