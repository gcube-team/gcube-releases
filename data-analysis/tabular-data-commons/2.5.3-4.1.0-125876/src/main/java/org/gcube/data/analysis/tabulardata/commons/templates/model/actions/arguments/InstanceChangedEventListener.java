package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

import java.util.List;


public interface InstanceChangedEventListener {

	void onChange(List<Object> values, String senderArgumentId,
			String senderInstanceId);

	boolean isAlive();
}
