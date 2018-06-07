package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.List;

public interface SelectionProvider<T> {
		
	void onUpdate(ArgumentInstance<?> modifiedInstance);
	
	List<T> getSelection();
		
}
