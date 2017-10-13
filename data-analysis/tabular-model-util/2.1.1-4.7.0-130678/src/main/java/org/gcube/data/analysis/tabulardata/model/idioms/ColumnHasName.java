package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.column.Column;

import com.google.common.base.Predicate;

public class ColumnHasName implements Predicate<Column> {

	String name;

	public ColumnHasName(String name) {
		super();
		this.name = name;
	}

	@Override
	public boolean apply(Column input) {
		if (input.hasName() && input.getName().equals(name))
			return true;
		else
			return false;
	}

}
