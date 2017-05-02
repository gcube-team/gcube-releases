package gr.cite.geoanalytics.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import gr.cite.geoanalytics.dataaccess.entities.accounting.dao.AccountingDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.annotation.dao.AnnotationDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.auditing.Auditing;
import gr.cite.geoanalytics.dataaccess.entities.auditing.dao.AuditingDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.document.Document;
import gr.cite.geoanalytics.dataaccess.entities.document.dao.DocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.TaxonomyTermLinkDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerImport;
import gr.cite.geoanalytics.dataaccess.entities.layer.LayerTag;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerImportDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerTagDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.MimeType;
import gr.cite.geoanalytics.dataaccess.entities.mimetype.dao.MimeTypeDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectDocument;
import gr.cite.geoanalytics.dataaccess.entities.project.ProjectLayer;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectLayerDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.style.Style;
import gr.cite.geoanalytics.dataaccess.entities.style.dao.StyleDaoImpl;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.SysConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.dao.SysConfigDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tag.Tag;
import gr.cite.geoanalytics.dataaccess.entities.tag.dao.TagDaoImpl;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.GeocodeShapeDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.dataaccess.entities.tenant.TenantActivation;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantActivationDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.tenant.dao.TenantDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOldImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.Workflow;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTask;
import gr.cite.geoanalytics.dataaccess.entities.workflow.WorkflowTaskDocument;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDocumentDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.Entity;
import gr.cite.geoanalytics.dataaccess.entities.accounting.Accounting;
import gr.cite.geoanalytics.dataaccess.entities.annotation.Annotation;

/**
 * A DAO factory for Spring managed environment
 * 
 * @author Gerasimos Farantatos
 *
 */
public class SpringJpaDaoFactory implements DaoFactory
{
	public static class SpringApplicationContext implements ApplicationContextAware {

		  private static ApplicationContext CONTEXT;

		  /**
		   * This method is called from within the ApplicationContext once it is 
		   * done starting up, it will stick a reference to itself into this bean.
		   * @param context a reference to the ApplicationContext.
		   */
		  public void setApplicationContext(ApplicationContext context) throws BeansException {
		    CONTEXT = context;
		  }

		  /**
		   * This is about the same as context.getBean("beanName"), except it has its
		   * own static handle to the Spring context, so calling this method statically
		   * will give access to the beans by name in the Spring application context.
		   * As in the context.getBean("beanName") call, the caller must cast to the
		   * appropriate target class. If the bean does not exist, then a Runtime error
		   * will be thrown.
		   * @param beanName the name of the bean to get.
		   * @return an Object reference to the named bean.
		   */
		  public static Object getBean(String beanName) {
		    return CONTEXT.getBean(beanName);
		  }
	}
	
	private static String persistenceUnit = null;
	private static Map<String, String> daoImpls = null;
	
	public static void setPersistenceContext(String persistenceUnit)
	{
		SpringJpaDaoFactory.persistenceUnit = persistenceUnit;
	}
	
	private static String getBeanName(String className)
	{
		return Character.toLowerCase(className.charAt(0)) + 
				(className.length() > 1 ? className.substring(1) : "");
	}
	private static void populateMappings()
	{
		daoImpls = new HashMap<String, String>();
		daoImpls.put(Accounting.class.getName(), getBeanName(AccountingDaoImpl.class.getSimpleName()));
		daoImpls.put(Auditing.class.getName(), getBeanName(AuditingDaoImpl.class.getSimpleName()));
		daoImpls.put(Annotation.class.getName(), getBeanName(AnnotationDaoImpl.class.getSimpleName()));
		daoImpls.put(Document.class.getName(), getBeanName(DocumentDaoImpl.class.getSimpleName()));
		daoImpls.put(GeocodeSystem.class.getName(), getBeanName(GeocodeSystemDaoImpl.class.getSimpleName()));
		daoImpls.put(LayerTag.class.getName(), getBeanName(LayerTagDaoImpl.class.getSimpleName()));	
		daoImpls.put(LayerImport.class.getName(), getBeanName(LayerImportDaoImpl.class.getSimpleName()));	
		daoImpls.put(MimeType.class.getName(), getBeanName(MimeTypeDaoImpl.class.getSimpleName()));	
		daoImpls.put(Shape.class.getName(), getBeanName(ShapeDaoImpl.class.getSimpleName()));
		daoImpls.put(ShapeDocument.class.getName(), getBeanName(ShapeDocumentDaoImpl.class.getSimpleName()));
		daoImpls.put(SysConfig.class.getName(), getBeanName(SysConfigDaoImpl.class.getSimpleName()));
		daoImpls.put(Style.class.getName(), getBeanName(StyleDaoImpl.class.getSimpleName()));
		daoImpls.put(Tag.class.getName(), getBeanName(TagDaoImpl.class.getSimpleName()));
		daoImpls.put(TaxonomyTermLink.class.getName(), getBeanName(TaxonomyTermLinkDaoImpl.class.getSimpleName()));
		daoImpls.put(Tenant.class.getName(), getBeanName(TenantDaoImpl.class.getSimpleName()));
		daoImpls.put(TenantActivation.class.getName(), getBeanName(TenantActivationDaoImpl.class.getSimpleName()));
		daoImpls.put(Principal.class.getName(), getBeanName(UserDaoOldImpl.class.getSimpleName()));
		daoImpls.put(Project.class.getName(), getBeanName(ProjectDaoImpl.class.getSimpleName()));
		daoImpls.put(ProjectDocument.class.getName(), getBeanName(ProjectDocumentDaoImpl.class.getSimpleName()));
		daoImpls.put(ProjectLayer.class.getName(), getBeanName(ProjectLayerDaoImpl.class.getSimpleName()));	
		daoImpls.put(Workflow.class.getName(), getBeanName(WorkflowDaoImpl.class.getSimpleName()));
		daoImpls.put(WorkflowTask.class.getName(), getBeanName(WorkflowTaskDaoImpl.class.getSimpleName()));
		daoImpls.put(WorkflowTaskDocument.class.getName(), getBeanName(WorkflowTaskDocumentDaoImpl.class.getSimpleName()));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Dao getDao(Class<? extends Entity> type) throws Exception
	{
		if(daoImpls == null) populateMappings();
		return (Dao)SpringApplicationContext.getBean(daoImpls.get(type.getName()));
	}

	@Override
	public void overrideMappings(Map<String, String> mappings)
	{
		populateMappings();
		daoImpls.putAll(mappings);
	}
}
