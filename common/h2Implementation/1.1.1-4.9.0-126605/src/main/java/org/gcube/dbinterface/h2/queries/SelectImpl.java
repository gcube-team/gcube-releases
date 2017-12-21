package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.Limit;
import org.gcube.common.dbinterface.Order;
import org.gcube.common.dbinterface.attributes.Attribute;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.dbinterface.h2.AbstractSelectQuery;


public class SelectImpl extends AbstractSelectQuery implements Select {

	public SelectImpl(){}
	
	private Attribute[] attributes=null;
	
	private Condition filter=null;
	
	private SimpleAttribute[] groups=null;
	
	private Limit limit=null;
	
	private Order[] orders=null;
	
	private Table[] tables;
	
	private boolean useDistinct=false;
	
	@Override
	public Attribute[] getAttributes() {
		return attributes;
	}

	@Override
	public Condition getFilter() {
		return filter;
	}

	@Override
	public Attribute[] getGroups() {
		return groups;
	}
		

	@Override
	public Order[] getOrders() {
		return orders;
	}

	@Override
	public Table[] getTables() {
		return tables;
	}
	
	@Override
	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public void setTables(Table ... tables) {
		this.tables = tables;
	}
	
	public void setAttributes(Attribute ... attributes) {
		this.attributes = attributes;
	}

	public void setFilter(Condition filter) {
		this.filter = filter;
	}
	
	public void setOrders(Order ... orders) {
		this.orders = orders;
	}

	public void setGroups(SimpleAttribute... groups) {
		this.groups = groups;		
	}

	public void setUseDistinct(boolean useDistinct) {
		this.useDistinct=useDistinct;		
	}

	@Override
	public boolean isUseDistinct() {
		return this.useDistinct;
	}

}
