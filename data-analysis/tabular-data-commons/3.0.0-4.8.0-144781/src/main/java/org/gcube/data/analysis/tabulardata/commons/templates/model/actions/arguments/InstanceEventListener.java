package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

public interface InstanceEventListener {

	public void onInvalid(ArgumentInstance<?> instance);
	
	public void onValid(ArgumentInstance<?> instance);
	
	public void onCreate(ArgumentInstance<?> instance);
}
