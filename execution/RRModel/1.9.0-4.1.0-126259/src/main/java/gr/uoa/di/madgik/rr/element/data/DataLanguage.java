package gr.uoa.di.madgik.rr.element.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.RRContext.WritePolicy;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;
import gr.uoa.di.madgik.rr.element.RRElement;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainerDao;
import gr.uoa.di.madgik.rr.utils.DatastoreHelper;

public class DataLanguage extends RRElement
{
	private String ID=null;
	private String collection=null;
	private HashMap<String, Set<String>> fieldLanguages=null;
//	private Set<String> language=null;
	private RRContext context=null;

	public RRContext getISContext()
	{
		return this.context;
	}

	public DataLanguage(/*String id*/) throws ResourceRegistryException
	{
		this.ID=UUID.randomUUID().toString();
		this.fieldLanguages=new HashMap<String, Set<String>>();
//		this.language=new HashSet<String>();
		this.context=ResourceRegistry.getContext();
	}
	
	public String getCollection()
	{
		return collection;
	}

	public void setCollection(String collection)
	{
		this.collection = collection;
	}
//
//	public Set<String> getLanguage()
//	{
//		return language;
//	}

	public HashMap<String, Set<String>> getFieldLanguages()
	{
		return fieldLanguages;
	}

	@Override
	public String getID()
	{
		return this.ID;
	}

	@Override
	public void setID(String id)
	{
		this.ID=id;
	}

	@Override
	public IDaoElement getItem()
	{
		return null;
	}
	
	@Override
	public void setDirty()
	{
		//nothing to be done
	}
	
	@Override
	public boolean isEqual(IRRElement target, boolean includeDetails) throws ResourceRegistryException
	{
		if(!(target instanceof DataLanguage)) throw new ResourceRegistryException("cannot apply to target of "+target);
		return true;
	}

	@Override
	public boolean load(boolean loadDetails) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support loading");
	}

	@Override
	public boolean load(boolean loadDetails, RRContext.ReadPolicy policy) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support loading");
	}
	
	@Override
	public boolean load(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support loading");
	}

	@Override
	public void store(boolean storeDetails) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support storing");
	}
	
	@Override
	public void store(boolean storeDetails, WritePolicy policy) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support storing");
	}

	@Override
	public void store(boolean storeDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support storing");
	}

	@Override
	public void delete(boolean loadDetails) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support deletion");
	}

	@Override
	public void delete(boolean loadDetails, DatastoreType persistencyType) throws ResourceRegistryException
	{
		throw new ResourceRegistryException("this element does not support deletion");
	}

	@Override
	public boolean exists() throws ResourceRegistryException
	{
		return true;
	}

	@Override
	public boolean exists(DatastoreType persistencyType) throws ResourceRegistryException
	{
		return true;
	}
	
	public static List<DataLanguage> getLanguages() throws ResourceRegistryException
	{
		HashMap<String,DataLanguage> lng=new HashMap<String,DataLanguage>();
		Set<IDaoElement> conts = null;
		try
		{
			conts = DatastoreHelper.getItems(DatastoreType.LOCAL, FieldIndexContainerDao.class);
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not retrieve data source field info", e);
		}
		for(IDaoElement con : conts)
		{
			FieldIndexContainerDao cont = (FieldIndexContainerDao)con;
			if(!lng.containsKey(cont.getCollection()))
			{
				DataLanguage l=new DataLanguage();
				l.setCollection(cont.getCollection());
				DataLanguage dl=new DataLanguage();
				dl.setCollection(cont.getCollection());
				lng.put(cont.getCollection(), dl);
			}
			if(!lng.get(cont.getCollection()).getFieldLanguages().containsKey(cont.getField()))
			{
				lng.get(cont.getCollection()).getFieldLanguages().put(cont.getField(), new HashSet<String>());
			}
			lng.get(cont.getCollection()).getFieldLanguages().get(cont.getField()).add(cont.getLanguage());
//				lng.get(cont.getCollection()).language.add(cont.getLanguage());
		}
		//}
		return new ArrayList<DataLanguage>(lng.values());
	}
	
	public static DataLanguage getLanguages(String collection) throws ResourceRegistryException
	{
		List<DataLanguage> lng=DataLanguage.getLanguages();
		for(DataLanguage l : lng)
		{
			if(l.getCollection().equals(collection)) return l;
		}
		return null;
	}
	
	public static Set<String> getLanguages(String collection,String field) throws ResourceRegistryException
	{
		DataLanguage lng=DataLanguage.getLanguages(collection);
		if(lng==null) return null;
		if(!lng.getFieldLanguages().containsKey(field)) return null;
		return lng.getFieldLanguages().get(field);
	}
}
