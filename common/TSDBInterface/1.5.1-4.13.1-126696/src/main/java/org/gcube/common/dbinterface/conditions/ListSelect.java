package org.gcube.common.dbinterface.conditions;

import org.gcube.common.dbinterface.queries.Select;

public class ListSelect implements Listable {

	private Select select;

	public ListSelect(Select select) {
		super();
		this.select = select;
	}

	@Override
	public String asStringList() {
		return this.select.getExpression();
	}

}
