/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.search;

/**
 * @author valentina
 *
 */
public class SearchItemByOperator {

	Operator operator = null;
	Object value = null;
	String min = null;
	String max = null;

	public enum Operator {
	    TEST
	}
	
	public SearchItemByOperator(Operator operator, Object value) {
		this.operator = operator;
		this.value = value;
	}

	public SearchItemByOperator(String min, String max, boolean interval) {
		if (interval){
			this.min = min;
			this.max = max;
		}
//		else{
//			this.operator = min;
//			this.value = max;
//		}

	}

	public String getOperator() {
		return null;
	}

	public Object getValue() {
		return value;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}
	
}
