package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.webservice.Sharable;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

public interface TemplateManagerProxy extends Sharable<Long, TemplateDescription,NoSuchTemplateException>{

	long saveTemplate(String name, String description, String agency, Template template);

	List<TemplateDescription> getTemplates();
	
	TemplateDescription getTemplate(long id)
			throws NoSuchTemplateException;
	
	TaskInfo apply(long templateId, long tabularResourceId) throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException;
	
	TemplateDescription removeTemplate(long id)
			throws NoSuchTemplateException;
	
	TemplateDescription updateTemplate(long id, Template template)
			throws NoSuchTemplateException;
	
}
