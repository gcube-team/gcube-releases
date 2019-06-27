package org.gcube.common.storagehub.model.expressions;

public final  class SearchableField<T> {

	private Class<T> type;
	
	private String name;
	
	protected SearchableField() {}
	
	protected SearchableField(Class<T> _class, String name) {
		this.type = _class;
		this.name = name;
	}

	@Override
	public String toString() {
		return "[" + name +"]";
	}

	public Class<T> getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	
}
