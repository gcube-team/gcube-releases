package org.gcube.common.homelibrary.jcr.workspace.search;

import org.gcube.common.homelibrary.home.workspace.search.SearchItemByOperator;

public class JCRAdvancedSearchItem implements SearchItemByOperator{

	String operator = null;
	Object value = null;
	String min = null;
	String max = null;

	public JCRAdvancedSearchItem(String operator, Object value) {
		this.operator = operator;
		this.value = value;
	}

	public JCRAdvancedSearchItem(String min, String max, boolean interval) {
		if (interval){
			this.min = min;
			this.max = max;
		}else{
			this.operator = min;
			this.value = max;
		}

	}

	@Override
	public String getOperator() {
		return operator;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getMin() {
		return min;
	}

	@Override
	public String getMax() {
		return max;
	}

}
