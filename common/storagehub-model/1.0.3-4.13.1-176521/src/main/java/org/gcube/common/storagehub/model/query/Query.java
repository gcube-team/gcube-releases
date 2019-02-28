package org.gcube.common.storagehub.model.query;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.SearchableItem;

public class Query<T extends SearchableItem<?>> {

	private T searchableItem;
	
	private Expression<Boolean> expression;
	
	private int limit = -1;
	
	private int offset =-1;
	
	private List<OrderField> orderFields = null;
	 
	protected Query(T searchableItem) {
		this.searchableItem = searchableItem;
	}
		
	public void setExpression(Expression<Boolean> expression) {
		this.expression= expression;
	}
	
	public void setOrder(OrderField ... fields ) {
		if (fields!=null && fields.length>0)
			this.orderFields = Arrays.asList(fields);
	}
	
	public void setLimit(int limit) {
		this.limit = limit;	
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public T getSearchableItem() {
		return searchableItem;
	}

	public Expression<Boolean> getExpression() {
		return expression;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}

	public List<OrderField> getOrderFields() {
		return orderFields;
	}
	
}
