package org.gcube.data.analysis.tabulardata.service.template;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public interface TemplateInterface {

	TemplateDescription getTemplate(TemplateId id) throws NoSuchTemplateException;
	
	TemplateDescription remove(TemplateId id) throws NoSuchTemplateException;
	
	TemplateDescription update(TemplateId id, Template template) throws NoSuchTemplateException;
	
	List<TemplateDescription> getTemplates();
	
	TemplateId saveTemplate(String name, String description, String agency, Template template);
	
	Task applyTemplate(TemplateId templateId, TabularResourceId tabularResourceId) throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException;

	TemplateDescription share(TemplateId templateId, SharingEntity... entities) throws NoSuchTemplateException, SecurityException;
	
	TemplateDescription unshare(TemplateId templateId, SharingEntity... entities) throws NoSuchTemplateException, SecurityException;

}
