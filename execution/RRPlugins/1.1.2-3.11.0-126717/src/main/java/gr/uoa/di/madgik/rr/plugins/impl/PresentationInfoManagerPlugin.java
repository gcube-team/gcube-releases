package gr.uoa.di.madgik.rr.plugins.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.uoa.di.madgik.rr.RRContext.DatastoreType;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.config.StaticConfiguration;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.FieldDao;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.PresentableDao;
import gr.uoa.di.madgik.rr.plugins.Plugin;

public class PresentationInfoManagerPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory
			.getLogger(PresentationInfoManagerPlugin.class);
	
	private Set<String> keywordGroups = new HashSet<String>();
	
	public PresentationInfoManagerPlugin()
	{
		this.type = Type.PRE_UPDATE;
		this.processedItems.add(new ProcessedItemType(Field.class, DatastoreType.LOCAL));
	}

	@Override
	public void setup() throws ResourceRegistryException { }
	
	@Override
	public void readConfiguration(String prefix, Properties properties) throws ResourceRegistryException
	{
		if(properties==null) return;
		String keywordGroupCountProp = properties.getProperty(prefix+".keywordGroupCount");
		if(keywordGroupCountProp==null) throw new ResourceRegistryException("keywordGroupCount property not found");
		
		int keywordGroupCount = Integer.parseInt(keywordGroupCountProp);
		
		for(int i = 0; i < keywordGroupCount; i++)
		{
			String keywordGroup = properties.getProperty(prefix + ".keywordGroup."+i);
			if(keywordGroup == null) throw new ResourceRegistryException("Could not find keyword group #"+i);
	
			this.keywordGroups.add(keywordGroup);
			logger.info("Added keyword group: " + keywordGroup);
		}
		if(keywordGroups.isEmpty())
			logger.warn("No keyword groups were found");
		
	}
	
	private List<Field> getFields() throws ResourceRegistryException
	{
		@SuppressWarnings("unchecked")
		Set<Field> fields = (Set<Field>)this.items.get(new ProcessedItemType(Field.class, DatastoreType.LOCAL));
		if(fields == null) return Field.getAll(false, DatastoreType.LOCAL);
		return new ArrayList<Field>(fields);
	}
	
	@Override
	protected void execute(Set<Class<?>> targets) throws ResourceRegistryException
	{
		
		logger.info( "Executing " + this.type + " plugin: " + this.getClass().getName());
		boolean locked = false;
		Lock writeLock = ResourceRegistry.getContext().getExclusiveLock();
		try
		{
			if(!targets.contains(FieldDao.class) || !targets.contains(PresentableDao.class))
			{
				logger.warn("Targets do not contain " + FieldDao.class.getName() + " or " + PresentableDao.class.getName() + ". Nothing to do.");
				return;
			}
			if(keywordGroups.isEmpty())
			{
				logger.warn("No keyword groups to propagate");
				return;
			}
			
			Set<String> keywords = new HashSet<String>();
			for(String keywordGroup : keywordGroups) keywords.addAll(StaticConfiguration.getInstance().getPresentationInfoKeywords(keywordGroup));
			
			List<Field> allFields = getFields();
			
			for(Field f : allFields)
			{
				for(Presentable p : f.getPresentables())
				{
					boolean updated = false;
					p.load(false, DatastoreType.LOCAL);
					for(String keyword:keywords)
					{
						if(!p.getPresentationInfo().contains(keyword))
						{
							p.getPresentationInfo().add(keyword);
							logger.info("Added presentation info keyword \"" + keyword + " to presentable " + p.getID() + " of field " + p.getField());
							updated = true;
						}
					}
					if(updated) p.store(false, DatastoreType.LOCAL);
				}
			}
			
		}catch(Exception ex)
		{
			throw new ResourceRegistryException("could not align incoming elements", ex);
		}finally
		{
			if(locked) writeLock.unlock();
		}
	}
}
