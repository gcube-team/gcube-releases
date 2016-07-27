package org.gcube.data.analysis.tabulardata.service.impl.template;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.template;

import java.util.List;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TemplateManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.service.impl.operation.tasks.TaskFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.gcube.data.analysis.tabulardata.service.template.TemplateInterface;

public class TemplateInterfaceImpl implements TemplateInterface{

	private static TemplateManagerProxy templateManager = template().build();

	public TemplateDescription getTemplate(TemplateId id) throws NoSuchTemplateException {
		return templateManager.getTemplate(id.getValue());
	}

	public List<TemplateDescription> getTemplates() {
		return templateManager.getTemplates();
	}

	public TemplateId saveTemplate(String name, String description,
			String agency, Template template) {
		List<ValidationError> errors = ValidatorFactory.validator().validate(template);
		if (errors.size()>0) throw new RuntimeException("template not valid "+errors);
		long id = templateManager.saveTemplate(name, description, agency, template);
		return new TemplateId(id);
	}
	
	

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#share(org.gcube.data.analysis.tabulardata.service.template.TemplateId, java.lang.String[])
	 */
	public TemplateDescription share(TemplateId templateId, AuthorizationToken... tokens)
			throws NoSuchTemplateException {
		try{
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");
			return templateManager.share(templateId.getValue(), tokens);
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#unshare(org.gcube.data.analysis.tabulardata.service.template.TemplateId, java.lang.String[])
	 */
	public TemplateDescription unshare(TemplateId templateId, AuthorizationToken... tokens)
			throws NoSuchTemplateException {
		try {
			if (tokens==null || tokens.length ==0) throw new RuntimeException("list of users is empty");
			return templateManager.unshare(templateId.getValue(), tokens);
		} catch (InternalSecurityException e) {
			throw new SecurityException(e.getMessage());
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.template.TemplateInterface#remove(org.gcube.data.analysis.tabulardata.service.template.TemplateId)
	 */
	public TemplateDescription remove(TemplateId id)
			throws NoSuchTemplateException {
		try{
			return templateManager.removeTemplate(id.getValue());
		}catch (org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException e) {
			throw new NoSuchTemplateException(id.getValue());
		}
	}
	
	public TemplateDescription update(TemplateId id, Template template)
			throws NoSuchTemplateException {
		try{
			return templateManager.updateTemplate(id.getValue(), template);
		}catch (org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException e) {
			throw new NoSuchTemplateException(id.getValue());
		}
	}

	public Task applyTemplate(TemplateId templateId,
			TabularResourceId tabularResourceId)
					throws NoSuchTabularResourceException, NoSuchTemplateException, TemplateNotCompatibleException {
		TaskInfo taskInfo = templateManager.apply(templateId.getValue(), tabularResourceId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);
	}

}
