package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

public interface ContainerInstanceEvent {

	public void onInstanceCreated(ContainerInstance instance);
	
	public void onInstanceRemoved(ContainerInstance instance);
	
}
