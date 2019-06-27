package org.gcube.data.spd.model;

import java.util.Calendar;


public class Condition implements Comparable<Condition>{

	public enum Operator {GE, GT, LE, LT, EQ}
	
	Object value;
	Conditions cond;
	Operator op;
	
	public Condition(Conditions cond,Object value, Operator op){
		this.value = value;
		this.cond = cond;
		this.op = op;
	}

	public boolean isValid(){
		return cond.getType().equals(value.getClass());
	}

	public Object getValue() {
		return value;
	}
	
	public Operator getOp() {
		return op;
	}


	public Conditions getType() {
		return cond;
	}

	@Override
	public int compareTo(Condition o) {
		if (o.equals(this)) return 0;
		if (o.getType()!= this.getType()) return o.getType().compareTo(this.getType());
		if (o.getOp()!= this.getOp()) return o.getOp().compareTo(this.getOp());
		switch (this.getType()) {
		case DATE:
			return ((Calendar)this.getValue()).compareTo((Calendar)o.getValue());
		case COORDINATE:
			return ((Coordinate)this.getValue()).compareTo((Coordinate)o.getValue());
		default:
			return 1;
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cond == null) ? 0 : cond.hashCode());
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Condition other = (Condition) obj;
		if (cond != other.cond)
			return false;
		if (op != other.op)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
		
	
	
}


