package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.queries.Select;

public class OperatorCondition<LEFT, RIGHT> extends Condition{

	protected LEFT left;
	protected RIGHT right;
	protected String operator;
	
	public OperatorCondition(LEFT left, RIGHT right, String operator) {
		this.left= left;
		this.right= right;
		this.operator= operator;
	}
	
	public OperatorCondition(RIGHT right, String operator) {
		this.left= null;
		this.right= right;
		this.operator= operator;
	}
	
	@Override
	public String getCondition() {
		String rightOperand;
		if (this.right==null) rightOperand=null;
		else if (String.class.isInstance(this.right))
			rightOperand= "'"+this.right+"'";
		else if (Select.class.isInstance(this.right))
			rightOperand= "("+this.right+")";
		else rightOperand= this.right.toString();
			
		String leftOperand;
		if (this.left==null) leftOperand="";
		else if (String.class.isInstance(this.left))
			leftOperand= "'"+this.left+"'";
		else leftOperand = this.left.toString();
		return leftOperand+" "+operator+" "+rightOperand;
		
	}
}
