package org.gcube.common.storagehub.model.expressions;

public class OrderField {

	public enum MODE{
		ASC, DESC
	}
	
	private MODE mode;
	private SearchableField<?> field;
	
	public OrderField(SearchableField<?> field, MODE mode) {
		this.mode = mode;
		this.field = field;
	}
	
	public OrderField(SearchableField<?> field) {
		this.mode = MODE.ASC;
		this.field = field;
	}

	public MODE getMode() {
		return mode;
	}

	public SearchableField<?> getField() {
		return field;
	}
	
}
