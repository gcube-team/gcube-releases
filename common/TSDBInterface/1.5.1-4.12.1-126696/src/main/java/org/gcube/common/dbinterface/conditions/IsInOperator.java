
package org.gcube.common.dbinterface.conditions;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;

public class IsInOperator extends OperatorCondition<SimpleAttribute, Listable> {

	public IsInOperator(SimpleAttribute left, Listable right) {
		super(left, right, " IN ");
	}

	@Override
	public String getCondition() {
		return (this.left!=null?this.left:"")+this.operator+"("+this.right.asStringList()+")";
	}
	
}
