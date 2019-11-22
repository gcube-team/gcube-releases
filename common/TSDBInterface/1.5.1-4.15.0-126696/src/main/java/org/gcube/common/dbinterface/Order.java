package org.gcube.common.dbinterface;

import org.gcube.common.dbinterface.attributes.Attribute;


public class Order {

	public enum OrderType{ASC,DESC};
	
	private OrderType orderType;
	private Attribute orderField;
	
	public Order(OrderType orderType, Attribute orderField ){
		this.orderField= orderField;
		this.orderType= orderType;
	}
	
	public String getOrder(){
		return this.orderField.getAttribute()+" "+this.orderType.toString();
	}
	
}
