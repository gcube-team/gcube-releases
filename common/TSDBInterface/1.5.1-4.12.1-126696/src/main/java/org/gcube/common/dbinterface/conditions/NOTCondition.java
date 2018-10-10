package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.Condition;

public class NOTCondition extends Condition {

	public Condition condition;
		
	public NOTCondition(Condition condition){
		this.condition=condition;
	}
	
	@Override
	public String getCondition() {
		return " NOT ("+condition.getCondition()+")";
	}
}