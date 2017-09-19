package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.List;

public interface ChangeHandler<T> {

	List<T> change(List<Object> values, List<T> baseSelector, String argumentSenderId);
	
}
