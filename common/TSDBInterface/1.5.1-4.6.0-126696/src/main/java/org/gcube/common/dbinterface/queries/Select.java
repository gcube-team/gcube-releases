package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.attributes.Attribute;
import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.Limit;
import org.gcube.common.dbinterface.Order;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.tables.Table;

public interface Select extends Selection{

	public void setAttributes(Attribute ... attributes);
	
	public void setFilter(Condition filter);
	
	public void setGroups(SimpleAttribute ... groups);
	
	public void setOrders(Order... orders);
	
	public void setTables(Table ... tables);
	
	public void setLimit(Limit limit);
	
	public void setUseDistinct(boolean useDistinct);
	
	public Limit getLimit();
	
	public Attribute[] getAttributes();
}
