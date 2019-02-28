package gr.cite.geoanalytics.dataaccess.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import gr.cite.geoanalytics.dataaccess.entities.Entity;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.coverage.dao.CoverageDaoImpl;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDaoImpl;

/**
 * A DAO factory for Spring managed environment
 * 
 * @author Gerasimos Farantatos
 *
 */
public class SpringJpaDaoFactoryGOS implements DaoFactory
{
	public static class SpringApplicationContextGOS implements ApplicationContextAware {

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
		SpringJpaDaoFactoryGOS.persistenceUnit = persistenceUnit;
	}
	
	private static String getBeanName(String className)
	{
		return Character.toLowerCase(className.charAt(0)) + 
				(className.length() > 1 ? className.substring(1) : "");
	}
	private static void populateMappings()
	{
		daoImpls = new HashMap<String, String>();
//		daoImpls.put(Accounting.class.getName(), getBeanName(AccountingDaoImpl.class.getSimpleName()));
//		daoImpls.put(Auditing.class.getName(), getBeanName(AuditingDaoImpl.class.getSimpleName()));
//		daoImpls.put(Annotation.class.getName(), getBeanName(AnnotationDaoImpl.class.getSimpleName()));
//		daoImpls.put(Tenant.class.getName(), getBeanName(TenantDaoImpl.class.getSimpleName()));
//		daoImpls.put(TenantActivation.class.getName(), getBeanName(TenantActivationDaoImpl.class.getSimpleName()));
//		daoImpls.put(Document.class.getName(), getBeanName(DocumentDaoImpl.class.getSimpleName()));
//		daoImpls.put(MimeType.class.getName(), getBeanName(MimeTypeDaoImpl.class.getSimpleName()));
//		daoImpls.put(Project.class.getName(), getBeanName(ProjectDaoImpl.class.getSimpleName()));
//		daoImpls.put(ProjectDocument.class.getName(), getBeanName(ProjectDocumentDaoImpl.class.getSimpleName()));
//		daoImpls.put(ProjectLayer.class.getName(), getBeanName(ProjectLayerDaoImpl.class.getSimpleName()));
		daoImpls.put(Shape.class.getName(), getBeanName(ShapeDaoImpl.class.getSimpleName()));
		daoImpls.put(Coverage.class.getName(), getBeanName(CoverageDaoImpl.class.getSimpleName()));
//		daoImpls.put(ShapeLayer.class.getName(), getBeanName(ShapeLayerDaoImpl.class.getSimpleName()));
//		daoImpls.put(ShapeDocument.class.getName(), getBeanName(ShapeDocumentDaoImpl.class.getSimpleName()));
//		daoImpls.put(SysConfig.class.getName(), getBeanName(SysConfigDaoImpl.class.getSimpleName()));
//		daoImpls.put(GeocodeSystem.class.getName(), getBeanName(GeocodeSystemDaoImpl.class.getSimpleName()));
//		daoImpls.put(TaxonomyTerm.class.getName(), getBeanName(TaxonomyTermDaoImpl.class.getSimpleName()));
//		daoImpls.put(TaxonomyTermLink.class.getName(), getBeanName(TaxonomyTermLinkDaoImpl.class.getSimpleName()));
//		daoImpls.put(GeocodeShape.class.getName(), getBeanName(GeocodeShapeDaoImpl.class.getSimpleName()));
//		daoImpls.put(Principal.class.getName(), getBeanName(UserDaoOldImpl.class.getSimpleName()));
//		daoImpls.put(Workflow.class.getName(), getBeanName(WorkflowDaoImpl.class.getSimpleName()));
//		daoImpls.put(WorkflowTask.class.getName(), getBeanName(WorkflowTaskDaoImpl.class.getSimpleName()));
//		daoImpls.put(WorkflowTaskDocument.class.getName(), getBeanName(WorkflowTaskDocumentDaoImpl.class.getSimpleName()));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Dao getDao(Class<? extends Entity> type) throws Exception
	{
		if(daoImpls == null) populateMappings();
		return (Dao)SpringApplicationContextGOS.getBean(daoImpls.get(type.getName()));
	}

	@Override
	public void overrideMappings(Map<String, String> mappings)
	{
		populateMappings();
		daoImpls.putAll(mappings);
	}
}
