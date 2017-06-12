package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTemplateManagerProxy  implements TemplateManagerProxy{

	ProxyDelegate<TemplateManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultTemplateManagerProxy.class); 
	
	public DefaultTemplateManagerProxy(ProxyDelegate<TemplateManager> config) {
		this.delegate = config;
	}

	@Override
	public long saveTemplate(final String name, final  String description, final String agency,
			final Template template) {
		Call<TemplateManager, Long> call = new Call<TemplateManager, Long>() {

			@Override
			public Long call(TemplateManager endpoint) throws Exception {
				return endpoint.saveTemplate(name, description, agency, template);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error saving template");
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public TemplateDescription removeTemplate(final long id) throws NoSuchTemplateException {
		Call<TemplateManager, TemplateDescription> call = new Call<TemplateManager, TemplateDescription>() {

			@Override
			public TemplateDescription call(TemplateManager endpoint) throws Exception {
				return endpoint.removeTemplate(id);
			}
		};
		try{
			return delegate.make(call);
		}catch(NoSuchTemplateException nste){
			logger.error("error removing template with id "+id);
			throw nste;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error removing template");
			throw again(e).asServiceException();
		}
	}

	
	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager#updateTemplate(long, org.gcube.data.analysis.tabulardata.commons.templates.model.Template)
	 */
	@Override
	public TemplateDescription updateTemplate(final long id, final Template template)
			throws NoSuchTemplateException {
		Call<TemplateManager, TemplateDescription> call = new Call<TemplateManager, TemplateDescription>() {

			@Override
			public TemplateDescription call(TemplateManager endpoint) throws Exception {
				return endpoint.updateTemplate(id, template);
			}
		};
		try{
			return delegate.make(call);
		}catch(NoSuchTemplateException nste){
			logger.error("error removing template with id "+id);
			throw nste;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error removing template");
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<TemplateDescription> getTemplates() {
		Call<TemplateManager, List<TemplateDescription>> call = new Call<TemplateManager, List<TemplateDescription>>() {

			@Override
			public List<TemplateDescription> call(TemplateManager endpoint) throws Exception {
				return endpoint.getTemplates();
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error getting templates");
			throw again(e).asServiceException();
		}
	}

	@Override
	public TemplateDescription getTemplate(final long id)
			throws NoSuchTemplateException {
		Call<TemplateManager, TemplateDescription> call = new Call<TemplateManager, TemplateDescription>() {

			@Override
			public TemplateDescription call(TemplateManager endpoint) throws Exception {
				return endpoint.getTemplate(id);
			}
		};
		try{
			return delegate.make(call);
		}catch(NoSuchTemplateException nste){
			logger.error("error getting template with id "+id);
			throw nste;
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (Exception e) {
			logger.error("error getting templates");
			throw again(e).asServiceException();
		}
	}

	
	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#share(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TemplateDescription share(final Long entityId,
			final SharingEntity... entities) 
			throws NoSuchTemplateException {
		Call<TemplateManager, TemplateDescription> call = new Call<TemplateManager, TemplateDescription>() {

			@Override
			public TemplateDescription call(TemplateManager endpoint) throws Exception {
				return endpoint.share(entityId, entities);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTemplateException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#unshare(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TemplateDescription unshare(final Long entityId,
			final SharingEntity... entities) throws NoSuchTemplateException {
		Call<TemplateManager, TemplateDescription> call = new Call<TemplateManager, TemplateDescription>() {

			@Override
			public TemplateDescription call(TemplateManager endpoint) throws Exception {
				return endpoint.unshare(entityId, entities);
			}
		};
		try {
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch (NoSuchTemplateException nte) {
			throw nte;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public TaskInfo apply(final long templateId, final long tabularResourceId)
			throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException {
		
		Call<TemplateManager, TaskInfo> call = new Call<TemplateManager, TaskInfo>() {

			@Override
			public TaskInfo call(TemplateManager endpoint) throws Exception {
				return endpoint.apply(templateId, tabularResourceId);
			}
		};
		try{
			return delegate.make(call);
		}catch (InternalSecurityException e) {
			throw new SecurityException(e);
		}catch(TemplateNotCompatibleException tmc){
			logger.error("error applying template, template with id "+templateId+" not applicable to tabular resource with id "+tabularResourceId);
			throw tmc;
		}catch(NoSuchTemplateException nst){
			logger.error("error applying template, template with id "+templateId+" not found");
			throw nst;
		}catch(NoSuchTabularResourceException nstr){
			logger.error("error applying template, tabular resource with id "+tabularResourceId+" not found");
			throw nstr;
		}catch (Exception e) {
			logger.error("error applying template");
			throw again(e).asServiceException();
		}
	}
	
}
