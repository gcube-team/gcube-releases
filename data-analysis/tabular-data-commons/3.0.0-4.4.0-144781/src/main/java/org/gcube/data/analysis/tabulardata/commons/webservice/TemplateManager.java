package org.gcube.data.analysis.tabulardata.commons.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

@WebService(targetNamespace=Constants.TEMPLATE_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface TemplateManager extends Sharable<Long, TemplateDescription,NoSuchTemplateException>{

	public static final String SERVICE_NAME = "templatemanager";

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	long saveTemplate(String name, String description, String agency, Template template) throws InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<TemplateDescription> getTemplates() throws InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TemplateDescription getTemplate(long id)
			throws NoSuchTemplateException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TaskInfo apply(long templateId, long tabularResourceId) throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TemplateDescription removeTemplate(long id)
			throws NoSuchTemplateException, InternalSecurityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	TemplateDescription updateTemplate(long id, Template template)
			throws NoSuchTemplateException, InternalSecurityException;
}
