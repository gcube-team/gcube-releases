package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;

public class NotInOperator extends OperatorCondition<SimpleAttribute, Listable> {

	public NotInOperator(SimpleAttribute left, Listable right) {
		super(left, right, " NOT IN ");
	}

	@Override
	public String getCondition() {
		return (this.left!=null?this.left:"")+this.operator+"("+this.right.asStringList()+")";
	}
	
}