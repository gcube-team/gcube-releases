package gr.uoa.di.madgik.rr.utils;

import gr.uoa.di.madgik.rr.RRContext;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.element.IDaoElement;
import gr.uoa.di.madgik.rr.element.IRRElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.datanucleus.api.jdo.JDOReplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatastoreHelper
{
	private static final Logger logger = LoggerFactory
			.getLogger(DatastoreHelper.class);
	
	
	public static void replicate(RRContext.DatastoreType source, RRContext.DatastoreType target,Set<Class<?>> objects) throws ResourceRegistryException
	{
		logger.info( "Number of objects types to replicate "+objects.size());
		logger.info( "Objects:  "+ objects);
		
		RRContext context = ResourceRegistry.getContext();
		JDOReplicationManager replicator = new JDOReplicationManager(context.getFactoryForRead(source), context.getFactoryForWrite(target));
		replicator.replicate(objects.toArray(new Class[0]));
	}
	
	/**
	 * Resolves conflicts originating from items being updated in the local datastore during the time an incoming bridging iteration is ongoing
	 * The conflict is resolved by keeping the version in the local datastore so as to avoid having stale data in the local store and missing write-behind updates
	 * 
	 * @param source The datastore used to buffer incoming items
	 * @param target The local datastore
	 * @param objects
	 * @throws ResourceRegistryException
	 */
	public static void resolveUpdateConflicts(RRContext.DatastoreType source, RRContext.DatastoreType target, Set<Class<?>> objects) throws ResourceRegistryException
	{
		RRContext context = ResourceRegistry.getContext();
		PersistenceManager pmR = context.getFactoryForRead(target).getPersistenceManager();
		PersistenceManager pmW = context.getFactoryForWrite(source).getPersistenceManager();
		//JDOReplicationManager replicator = new JDOReplicationManager(context.getFactoryForRead(target), context.getFactoryForWrite(source));
		try 
		{
			for(Class<?> itemType : objects)
			{
				Set<Object> itemsInConflict = new HashSet<Object>();
				Set<IDaoElement> sourceItems = DatastoreHelper.getItems(source, itemType, true);
				Map<String, IDaoElement> sourceItemIndex = new HashMap<String, IDaoElement>();
				for(IDaoElement item : sourceItems) sourceItemIndex.put(item.getID(), item);
				
				Set<IDaoElement> targetItems = DatastoreHelper.getItems(target, itemType, true);
				for(IDaoElement targetItem : targetItems)
				{
					IDaoElement item = sourceItemIndex.get(targetItem.getID());
					if(item == null) continue; //source might not contain an item if it is new
					
					logger.trace("target timestamp : " + targetItem.getTimestamp());
					logger.trace("item timestamp : " + item.getTimestamp());
					
					if (targetItem.getTimestamp() == null){
						logger.info("Problematic target : " + targetItem.toXML());
					}
					if (item.getTimestamp() == null)
						logger.warn("Problematic item : " + item.toXML());
						
					
					if(targetItem.getTimestamp() > item.getTimestamp())
						itemsInConflict.add(targetItem.getID());
				}
//				if(itemsInConflict.size() > 0)
//				{
//					System.out.println("Source (Local Buffer), Item : " + itemType);
//					int i = 0;
//					for(IDaoElement item : sourceItemIndex.values())
//						System.out.println((i++) + " : " + item.getID());
//					System.out.println("Target (Local), Item : " + itemType);
//					for(IDaoElement item : targetItems)
//						System.out.println((i++) + " : " + item.getID());
//				}
				if(itemsInConflict.size() > 0)
				{
					//replicator.replicate(itemsInConflict.toArray(new Object[0]));
					List<Object> objs = new ArrayList<Object>();
					pmR.currentTransaction().begin();
					for(Object itemInConflict : itemsInConflict)
					{
						logger.info( "Conflict: Item: " + itemType + " id: " + itemInConflict.toString());
						objs.add(pmR.detachCopy(pmR.getObjectById(itemType, (String)itemInConflict)));
					}
					pmR.currentTransaction().commit();
					pmW.currentTransaction().begin();
					for(Object obj : objs)
						pmW.makePersistent(obj);
					pmW.currentTransaction().commit();
				}
			}
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not resolve update conflicts", e);
		}finally
		{
			if(pmR.currentTransaction().isActive())
				pmR.currentTransaction().rollback();
			if(pmW.currentTransaction().isActive())
				pmW.currentTransaction().rollback();
			pmR.close();
			pmW.close();
		}
	}

	public static Set<IDaoElement> getItems(RRContext.DatastoreType target, Class<?> obj) throws Exception
	{
		return DatastoreHelper.getItems(target, obj, false);
	}
	
	public static Set<IDaoElement> getItems(RRContext.DatastoreType target, Class<?> obj, boolean overridePrefetched) throws Exception
	{
		return DatastoreHelper.getItems(target, obj, overridePrefetched, false);
	}
	
	public static Set<IDaoElement> getItems(RRContext.DatastoreType target, Class<?> obj, boolean overridePrefetched, boolean detach) throws Exception
	{
		Set<IDaoElement> memRetrieved = new HashSet<IDaoElement>();
		if(target.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(obj.getName()) && !overridePrefetched)
		{
			Set<IRRElement> retrieved = InMemoryStore.getItems(obj);
			if(retrieved != null)
			{
				for(IRRElement element : retrieved)
				{
					if(element.getItem() != null) memRetrieved.add(element.getItem());
				}
				return memRetrieved;
			}
		}
		
		if(obj.isInstance(IRRElement.class))
		{
			IRRElement i = (IRRElement)obj.newInstance();
			if(i.getItem() != null)
				obj = i.getItem().getClass();
		}
		RRContext context = ResourceRegistry.getContext();
		PersistenceManager pm = context.getManagerForRead(target);
		try
		{
			pm.currentTransaction().begin();
				Extent<?> ex = pm.getExtent(obj, true);
				Iterator<?> iter = ex.iterator();
				Set<IDaoElement> coll = new HashSet<IDaoElement>();
				while (iter.hasNext())
				{
					Object o=iter.next();
					if(!(o instanceof IDaoElement)) continue;
					coll.add(detach ? pm.detachCopy((IDaoElement)o) : (IDaoElement)o);
				}
			pm.currentTransaction().commit();
			return coll;
		}
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}
	
	// delete all objects of this class (and subclasses)
	public static void clear(RRContext.DatastoreType target,Set<Class<?>> objects) throws ResourceRegistryException
	{
		for(Class<?> type: objects)
		{
			if(target.equals(DatastoreType.LOCAL) && ResourceRegistry.getContext().isTargetInMemory(type.getName()))
			{
				//System.out.println("deleting from inmemory type : " + type);
				InMemoryStore.setItems(type, null);
			}
		}
		
		RRContext context = ResourceRegistry.getContext();
		PersistenceManager pm = context.getManagerForWrite(target);
		try
		{
			pm.currentTransaction().begin();
			for(Class<?> cls : objects)
			{
				Extent<?> ex = pm.getExtent(cls, true);
				Iterator<?> iter = ex.iterator();
				Set<IDaoElement> coll = new HashSet<IDaoElement>();
				while (iter.hasNext())
				{
					Object obj=iter.next();
					if(!(obj instanceof IDaoElement)) continue;
					coll.add((IDaoElement)obj);
				}
				logger.info( "Number of objects of type " + cls.getName() + " to delete is " + coll.size());
				pm.deletePersistentAll(coll);
			}
			pm.currentTransaction().commit();
		} catch (Exception e){
			logger.error("error while deleting", e);
		}
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}
	
	public static Set<IDaoElement> retrieveAll(RRContext.DatastoreType target,Class<?> object) throws ResourceRegistryException
	{
		Set<IDaoElement> coll = new HashSet<IDaoElement>();
		RRContext context = ResourceRegistry.getContext();
		PersistenceManager pm = context.getManagerForRead(target);
		try
		{
			pm.currentTransaction().begin();
			Extent<?> ex = pm.getExtent(object, true);
			Iterator<?> iter = ex.iterator();
			while (iter.hasNext())
			{
				Object o=iter.next();
				if(!(o instanceof IDaoElement)) continue;
				coll.add(pm.detachCopy((IDaoElement)o));
			}
			logger.trace( "Number of objects of type " + object.getName() + " retrieved is " + coll.size());
			pm.currentTransaction().commit();
			return coll;
		}
		finally
		{
			if (pm.currentTransaction().isActive())
			{
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}
	
	private static void storeItems(Set<IDaoElement> items, DatastoreType persistencyType) throws ResourceRegistryException
	{
		PersistenceManager pm = ResourceRegistry.getContext().getManagerForWrite(persistencyType);
		try
		{
			pm.currentTransaction().begin();
			for(IDaoElement item : items) pm.makePersistent(item);
			pm.currentTransaction().commit();
		} catch (Exception e) {
			logger.warn("storeItems error occured", e);
		}finally
		{
			if (pm.currentTransaction().isActive()) pm.currentTransaction().rollback();
			pm.close();
		}
	}
	
	public static void bufferItems(Set<IDaoElement> items) throws ResourceRegistryException
	{
		storeItems(items, DatastoreType.LOCALBUFFER);
	}
	
	public static void persistItems(Set<IDaoElement> items) throws ResourceRegistryException
	{
		storeItems(items, DatastoreType.LOCAL);
	}
}
