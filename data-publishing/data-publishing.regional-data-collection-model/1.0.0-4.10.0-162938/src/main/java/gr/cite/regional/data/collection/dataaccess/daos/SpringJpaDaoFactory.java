package gr.cite.regional.data.collection.dataaccess.daos;

import java.util.HashMap;
import java.util.Map;

import gr.cite.regional.data.collection.dataaccess.entities.Annotation;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import gr.cite.regional.data.collection.dataaccess.entities.Entity;

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
	
	@SuppressWarnings("unused")
	private static String persistenceUnit = null;
	private static Map<String, String> daoImpls = null;
	
	public static void setPersistenceContext(String persistenceUnit)
	{
		SpringJpaDaoFactory.persistenceUnit = persistenceUnit;
	}
	
	@SuppressWarnings("unused")
	private static String getBeanName(String className)
	{
		return Character.toLowerCase(className.charAt(0)) + 
				(className.length() > 1 ? className.substring(1) : "");
	}
	private static void populateMappings()
	{
		daoImpls = new HashMap<String, String>();
		daoImpls.put(Annotation.class.getName(), AnnotationDaoImpl.class.getName());
		//daoImpls.put(CDT_37.class.getName(), CDTDaoImpl.class.getName());
		daoImpls.put(UserReference.class.getName(), UserReferenceDaoImpl.class.getName());
		daoImpls.put(Domain.class.getName(), DomainDaoImpl.class.getName());
		daoImpls.put(DataModel.class.getName(), DataModelDaoImpl.class.getName());
		daoImpls.put(DataCollection.class.getName(), DataCollectionDaoImpl.class.getName());
		daoImpls.put(DataSubmission.class.getName(), DataSubmissionDaoImpl.class.getName());
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
