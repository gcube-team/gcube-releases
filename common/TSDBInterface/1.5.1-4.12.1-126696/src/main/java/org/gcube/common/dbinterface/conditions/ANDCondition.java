package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.Condition;


public class ANDCondition extends Condition {

	public Condition[] conditions;
		
	public ANDCondition(Condition ...conditions){
		this.conditions=conditions;
	}
	
	@Override
	public String getCondition() {
		if (this.conditions.length==0) return "";
		StringBuilder toReturn=new StringBuilder();
		for (Condition cond: conditions)
			toReturn.append(cond.getCondition()+" AND ");
		return "("+toReturn.substring(0, toReturn.length()-4)+")";
	}
	
}
