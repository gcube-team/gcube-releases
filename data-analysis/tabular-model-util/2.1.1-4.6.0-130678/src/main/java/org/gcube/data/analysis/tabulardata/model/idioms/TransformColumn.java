package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.column.Column;

import com.google.common.base.Function;

public class TransformColumn<T extends Column> implements Function<Column, T>{

	@SuppressWarnings("unchecked")
	@Override
	public T apply(Column input) {
		return (T)input;
	}

}
