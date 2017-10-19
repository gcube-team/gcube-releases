package org.gcube.data.spd.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.ehcache.CacheManager;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.executor.jobs.SerializableSpeciesJob;
import org.gcube.data.spd.executor.jobs.SpeciesJob;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.utils.ExecutorsContainer;
import org.gcube.smartgears.ApplicationManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppInitializer implements ApplicationManager {

	private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
	
	private static final String jobMapFileNamePrefix = "jobs";
	
	private HashMap<String, SpeciesJob> jobMap; 
	
	private PluginManager pluginManager;
	
	private ApplicationContext ctx = ContextProvider.get();
	
	
	@Override
	public void onInit() {
		logger.info("[TEST] init called for SPD in scope {} ", ScopeProvider.instance.get());
		jobMap= new HashMap<String, SpeciesJob>();
		pluginManager = new PluginManager(ctx);
		loadJobMap();
	}

	@Override
	public void onShutdown() {
		storeJobMap();
		pluginManager.shutdown();
		pluginManager = null;
		ExecutorsContainer.stopAll();
		CacheManager.getInstance().shutdown();
		logger.info("[TEST] App Initializer shut down on "+ScopeProvider.instance.get());
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public HashMap<String, SpeciesJob> getJobMap() {
		return jobMap;
	}
	
	private void storeJobMap(){
		String scopeNamePrefix= ScopeProvider.instance.get().replaceAll("/", ".");
		String jobMapFileName = jobMapFileNamePrefix+scopeNamePrefix;
		logger.trace("[TEST] storing job map file {}",jobMapFileName);
		HashMap<String, SerializableSpeciesJob> spdJobMap = new HashMap<String, SerializableSpeciesJob>();
		for (Entry<String, SpeciesJob> entry : jobMap.entrySet() ){
			logger.trace("[TEST] stored job with id {}",entry.getKey());
			SpeciesJob spdJob = entry.getValue();
			if (spdJob instanceof SerializableSpeciesJob)
				spdJobMap.put(entry.getKey(),(SerializableSpeciesJob)spdJob);
			else 
				spdJobMap.put(entry.getKey(), new SerializableSpeciesJob(spdJob.getStatus(), spdJob.getId(), 
					spdJob.getCompletedEntries(), spdJob.getStartDate(), spdJob.getEndDate()));
		}
		
		File file = null;
		try {
			file = ctx.persistence().file(jobMapFileName);
			if (file.exists()) file.delete();
			file.createNewFile();
			
			try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
				oos.writeObject(spdJobMap);
			}	
		} catch (Exception e) {
			logger.error("error writing jobMapof type "+jobMap.getClass().getName()+" on disk",e);
			if (file !=null && file.exists()) file.delete(); 
		}
	}

	@SuppressWarnings("unchecked")
	private void loadJobMap(){
		String scopeNamePrefix= ScopeProvider.instance.get().replaceAll("/", ".");
		String jobMapFileName = jobMapFileNamePrefix+scopeNamePrefix;
		logger.trace("[TEST]  loading job Map from file {} ",jobMapFileName);
		File file = ctx.persistence().file(jobMapFileName);
		if (file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			logger.error("cannot create file {}",file.getAbsolutePath(),e1);
			jobMap= new HashMap<String, SpeciesJob>(); 
			return;
		}
		try (ObjectInput ois = new ObjectInputStream(new FileInputStream(file))){
			jobMap = (HashMap<String, SpeciesJob>) ois.readObject();
		} catch (Exception e) {
			logger.warn("[TEST]  the file {} doesn't exist, creating an empty map",file.getAbsolutePath());
			jobMap= new HashMap<String, SpeciesJob>(); 
		}
		logger.trace("[TEST]  loaded map is with lenght {} ",jobMap.size());
	}

}
