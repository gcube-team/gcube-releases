package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ORCondition extends Condition {

	public Condition[] cond;
	
	private static final Logger logger = LoggerFactory.getLogger(ORCondition.class);
	
	public ORCondition(Condition... cond){
		this.cond=cond;
	}
	
	@Override
	public String getCondition() {
		if (this.cond.length==0) return "";
		StringBuilder tmpString=new StringBuilder();
		for (Condition singleCond :cond){
			logger.trace("singleCond is "+singleCond.getCondition());
			tmpString.append(" "+singleCond.getCondition()+" OR ");
		}
		return " ("+tmpString.substring(0, tmpString.length()-3)+")";
	}

}
