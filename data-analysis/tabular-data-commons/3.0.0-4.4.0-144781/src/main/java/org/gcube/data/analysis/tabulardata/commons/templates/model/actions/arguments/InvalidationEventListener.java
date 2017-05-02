package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;


public interface InvalidationEventListener {

	void onInvalid(String instanceId);
	
	void onSelectorChanged(String instanceId);
}
