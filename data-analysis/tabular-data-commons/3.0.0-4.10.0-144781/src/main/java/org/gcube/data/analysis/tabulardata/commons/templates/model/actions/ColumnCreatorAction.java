package org.gcube.data.analysis.tabulardata.commons.templates.model.actions;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;

public abstract class ColumnCreatorAction extends TemplateAction<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2620456499248196447L;
	
	public abstract List<TemplateColumn<?>> getCreatedColumns();
	

}
