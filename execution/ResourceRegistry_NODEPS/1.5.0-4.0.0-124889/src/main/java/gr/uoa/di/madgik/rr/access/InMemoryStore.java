package gr.uoa.di.madgik.rr.access;

import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.IRRElement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore 
{
	
	private static Map<String, Map<String, IRRElement>> store =  new ConcurrentHashMap<String, Map<String, IRRElement>>();
	
	private static Map<String, String> inMemoryTargetItemTypes = new ConcurrentHashMap<String, String>();
	private static Map<String, String> revInMemoryTargetItemTypes = new ConcurrentHashMap<String, String>();
	
	private InMemoryStore() { }
	
	public static void setItems(Class<?> type, Set<? extends IRRElement> items) throws ResourceRegistryException
	{
		try
		{
			store.put(type.getName(), new ConcurrentHashMap<String, IRRElement>());
			if(items!=null)
			{
				for(IRRElement item : items)
				{
					store.get(type.getName()).put(item.getID(), item);
				}
			
				if(items.size() > 0)
				{
					if(!inMemoryTargetItemTypes.containsKey(type.getName()))
					{
						if(IRRElement.class.isAssignableFrom(type))
						{
							IRRElement obj = (IRRElement)type.newInstance();
							if(obj.getItem() != null)
							{
								inMemoryTargetItemTypes.put(obj.getItem().getClass().getName(), type.getName());
								revInMemoryTargetItemTypes.put(type.getName(), obj.getItem().getClass().getName());
							}
						}
					}
				}
			}
			if(items == null || items.size() == 0)
			{
				if(IRRElement.class.isAssignableFrom(type))
				{
					IRRElement obj = (IRRElement)type.newInstance();
					if(obj.getItem() != null)
					{
						inMemoryTargetItemTypes.remove(obj.getItem().getClass().getName());
						revInMemoryTargetItemTypes.remove(type.getName());
					}
				}
			}
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not set items", e);
		}
	}
	
	public static void setItem(Class<?> type, IRRElement item) throws ResourceRegistryException
	{
		try
		{
			if(store.get(type.getName()) == null) store.put(type.getName(), new ConcurrentHashMap<String, IRRElement>());
			store.get(type.getName()).put(item.getID(), item);
			if(!inMemoryTargetItemTypes.containsKey(type.getName()))
			{
				if(IRRElement.class.isAssignableFrom(type))
				{
					IRRElement obj = (IRRElement)type.newInstance();
					if(obj.getItem() != null)
					{
						inMemoryTargetItemTypes.put(obj.getItem().getClass().getName(), type.getName());
						revInMemoryTargetItemTypes.put(type.getName(), obj.getItem().getClass().getName());
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new ResourceRegistryException("Could not set item", e);
		}
	}
	
	public static void removeItem(Class<?> type, String id)
	{
		String sType = type.getName();
		boolean rev = false;
		Map<String, IRRElement> itemStore = store.get(sType);
		if(itemStore == null)
		{
			rev = true;
			sType = revInMemoryTargetItemTypes.get(sType);
			if(sType == null) return;
			itemStore = store.get(sType);
			if(itemStore == null) return;
		}
		if(itemStore.containsKey(id))
		{
			itemStore.remove(id);
			if(itemStore.size()==0)
			{
				store.put(sType, new ConcurrentHashMap<String, IRRElement>());
				if(!rev) 
				{
					inMemoryTargetItemTypes.remove(revInMemoryTargetItemTypes.get(sType));
					revInMemoryTargetItemTypes.remove(sType);
				}
				else
				{
					revInMemoryTargetItemTypes.remove(inMemoryTargetItemTypes.get(sType));
					inMemoryTargetItemTypes.remove(sType);
				}
			}
		}
	}
	public static Set<IRRElement> getItems(Class<?> type)
	{
		String sType = type.getName();
		if(store.get(sType) == null)
		{
			sType = revInMemoryTargetItemTypes.get(sType);
			if(sType == null) return null;
			if(store.get(sType) == null) return null;
		}
		return new HashSet<IRRElement>(store.get(type.getName()).values());
	}
	
	public static IRRElement getItem(Class<?> type, String id)
	{
		String sType = type.getName();
		Map<String, IRRElement> itemStore = store.get(sType);
		if(itemStore == null)
		{
			sType = revInMemoryTargetItemTypes.get(sType);
			if(sType == null) return null;
			itemStore = store.get(sType);
			if(itemStore == null) return null;
		}
		return itemStore.get(id);
	}
	
	public static boolean hasItem(Class<?> type, String id)
	{
		String sType = type.getName();
		Map<String, IRRElement> itemStore = store.get(type.getName());
		if(itemStore == null)
		{
			sType = revInMemoryTargetItemTypes.get(sType);
			if(sType == null) return false;
			itemStore = store.get(sType);
			if(itemStore == null) return false;
		}
		return itemStore.containsKey(id);
	}
	
	public static void clear()
	{
		store =  new ConcurrentHashMap<String, Map<String, IRRElement>>();
		
		inMemoryTargetItemTypes = new ConcurrentHashMap<String, String>();
		revInMemoryTargetItemTypes = new ConcurrentHashMap<String, String>();
	}
	
	public static boolean containsItemType(String type)
	{
		return inMemoryTargetItemTypes.containsKey(type);
	}
}
