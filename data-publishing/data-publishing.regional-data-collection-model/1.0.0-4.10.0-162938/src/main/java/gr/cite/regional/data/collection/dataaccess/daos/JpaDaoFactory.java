package gr.cite.regional.data.collection.dataaccess.daos;

import java.util.HashMap;
import java.util.Map;

import gr.cite.regional.data.collection.dataaccess.entities.Annotation;
import gr.cite.regional.data.collection.dataaccess.entities.DataCollection;
import gr.cite.regional.data.collection.dataaccess.entities.Domain;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataaccess.entities.Entity;
import gr.cite.regional.data.collection.dataaccess.entities.DataModel;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;

public class JpaDaoFactory implements DaoFactory
{
	@SuppressWarnings("unused")
	private static String persistenceUnit = null;
	private static Map<String, String> daoImpls = null;
	
	public static void setPersistenceContext(String persistenceUnit)
	{
		JpaDaoFactory.persistenceUnit = persistenceUnit;
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
		return (Dao)Class.forName(daoImpls.get(type.getName())).newInstance();
	}

	@Override
	public void overrideMappings(Map<String, String> mappings)
	{
		populateMappings();
		daoImpls.putAll(mappings);
	}
}
