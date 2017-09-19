package org.gcube.data.analysis.tabulardata.commons.templates.model.actions;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;

public interface  GroupAction {

	List<TemplateAction<Long>> getActions();
	
	String getGroupId();
	
}
