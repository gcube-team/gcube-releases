package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.List;

public interface Transformer<T, R> {

	List<R> transform(List<T> values);
	
}
