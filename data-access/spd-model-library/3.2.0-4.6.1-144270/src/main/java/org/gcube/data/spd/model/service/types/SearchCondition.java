package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Conditions;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchCondition {

	private Conditions type;
    private Operator op;
    private String value;
	
    protected SearchCondition() {
		super();
	}
	
    public SearchCondition(Conditions type, Operator op, String value) {
		super();
		this.type = type;
		this.op = op;
		this.value = value;
	}

	public Conditions getType() {
		return type;
	}
	public Operator getOperator() {
		return op;
	}
	public String getValue() {
		return value;
	}
	
    
}
