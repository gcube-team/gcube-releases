package org.gcube.application.aquamaps.publisher.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.PublisherConfiguration;
import org.gcube.application.aquamaps.publisher.ReportDescriptor;
import org.gcube.application.aquamaps.publisher.StoreConfiguration;
import org.gcube.application.aquamaps.publisher.StoreConfiguration.StoreMode;
import org.gcube.application.aquamaps.publisher.StoreResponse;
import org.gcube.application.aquamaps.publisher.StoreResponse.PerformedOperation;
import org.gcube.application.aquamaps.publisher.impl.datageneration.ObjectManager;
import org.gcube.application.aquamaps.publisher.impl.model.ContextLayerPair;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.Storable;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;
import org.gcube.application.aquamaps.publisher.impl.model.searchsupport.FileSetSpeciesIdPair;
import org.gcube.application.aquamaps.publisher.impl.model.searchsupport.LayerSpeciesIdPair;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.dbinterface.persistence.ObjectPersistency;
import org.gcube.common.dbinterface.pool.DBSession;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherImpl extends Publisher {

	//************* STATIC 
	protected final static Logger logger= LoggerFactory.getLogger(PublisherImpl.class);
	private static final int MAX_THREADS= 5;

	
	@SuppressWarnings("rawtypes")
	private static Hashtable<CoverageDescriptor,Future> futuresMap = new Hashtable<CoverageDescriptor,Future>(); 
	
	private static PublisherImpl instance = null;
	
	private static ExecutorService  executorService = Executors.newFixedThreadPool(MAX_THREADS);
	
	
	public static String webServerUrl;
	public static File serverPathDir;
	
	
	public static PublisherImpl get(){
		if(instance==null) instance= new PublisherImpl();
		return instance;
	}
	
	
	
	//************* INSTANCE 
	
	
	@Override
	public ReportDescriptor initialize(PublisherConfiguration configuration)
			throws Exception {
		logger.info("connecting to db with "+configuration.getDBUser()+" "+configuration.getDBPassword()+" "+configuration.getDBHost());
		DBSession.initialize("org.gcube.dbinterface.postgres", configuration.getDBUser(), configuration.getDBPassword(), configuration.getDBHost());
		logger.info("connecting to jetty with  "+configuration.getPersistenceRoot()+" "+configuration.getHttpServerBasePath()+" "+configuration.getHttpServerPort());
		startWebserver(configuration.getPersistenceRoot(), configuration.getHttpServerBasePath(), configuration.getHttpServerPort());
		return new ReportDescriptor();		
	}

	
	@SuppressWarnings("unchecked")
	private void startWebserver(File persistenceRoot, String httpServerBasePath, int httpServerPort) throws Exception {
		logger.debug("HTTP Server Base path = " + httpServerBasePath);
		
		
		serverPathDir= new File(persistenceRoot+File.separator+httpServerBasePath);
		if(!serverPathDir.exists())
			serverPathDir.mkdirs();
				
		logger.debug("HTTP Server port = " + httpServerPort);
		webServerUrl="http://"+GHNContext.getContext().getHostname()+":"+httpServerPort+"/";
		logger.debug("WEBSERVER URL: "+webServerUrl);
		//initializing jetty
		Connector connector = new SelectChannelConnector();
		connector.setPort(httpServerPort);
		Server server = new Server(httpServerPort);		
		server.setConnectors(new Connector[]{connector});
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(serverPathDir.getAbsolutePath());
		try{
			ContextHandlerCollection contexts = new ContextHandlerCollection();

			Context ctxADocs= new Context(contexts,"/",Context.SESSIONS);
			ctxADocs.setResourceBase(serverPathDir.getAbsolutePath());
			ServletHolder ctxADocHolder= new ServletHolder();
			ctxADocHolder.setInitParameter("dirAllowed", "true");    
			ctxADocHolder.setServlet(new DefaultServlet());
			ctxADocs.addServlet(ctxADocHolder, "/");

			//contexts.start();

			server.setHandlers(new Handler[]{resourceHandler,contexts});

			//starting the web server
			server.start();
			contexts.start();

			for(Handler h:server.getHandlers()){
				logger.trace("Handler : "+h.toString()+" running state : "+h.isRunning()+" startedState : "+h.isStarted());

			}
			logger.trace("server attributes");
			Enumeration<String> attNames=server.getAttributeNames();
			while(attNames.hasMoreElements()){
				String name=attNames.nextElement();
				logger.trace(name+" : "+server.getAttribute(name));
			}
		}catch(Throwable t){
			logger.error("",t);
		}
		
	}
	

	@SuppressWarnings({"unchecked", "serial" })
	@Override
	public synchronized <T extends CoverageDescriptor> Future<T> get(final Class<T> clazz, final ObjectManager<T> manager,
			final CoverageDescriptor coverageDescriptor) throws Exception {
		Future<T> toReturn;
		logger.debug("executing get method ");
		if ((toReturn =futuresMap.get(coverageDescriptor))!=null){
			logger.debug("coverag found in futuresMap, returning it");
			return toReturn;
		}else {
			logger.debug("futuresMap contains "+futuresMap.size());
		}
		
		//checking if the object is already stored
		long start = System.currentTimeMillis();
		final HashMap<String, Object> fieldsMap = new HashMap<String, Object>(){{
			put("tableId", coverageDescriptor.getTableId());
			put("parameters", coverageDescriptor.getParameters());
			put("customized", false);
		}};
		if (ObjectPersistency.get(clazz).existEntryByFields(fieldsMap)){
			logger.debug("the object exists ");
			toReturn=executorService.submit(new Callable<T>() {
				@Override
				public T call() throws Exception {
					logger.debug("retuning an existing entry");
					return ObjectPersistency.get(clazz).getObjectByFields(fieldsMap).get(0);
				}
			});
			logger.debug("time to execute get without store is "+(System.currentTimeMillis()-start));
			return toReturn;
		}
			
		logger.debug("the object not exists in the db");
		
		//the object is not stored, starting with generation
		toReturn = executorService.submit(new Callable<T>() {
			@Override
			public T call() throws Exception {
				long startGeneration = System.currentTimeMillis();
				logger.debug("generating the object");
				T generated = manager.generate();
				generated.setCustomized(false);
				generated.setParameters(coverageDescriptor.getParameters());
				generated.setTableId(coverageDescriptor.getTableId());
				ObjectPersistency.get(clazz).insert(generated);
				logger.debug("time took to execute generation and insert is "+(System.currentTimeMillis()-startGeneration));
				logger.debug("futuremaps contains element before removing "+futuresMap.size());
				futuresMap.remove(coverageDescriptor);
				logger.debug("futuremaps contains element after removing "+futuresMap.size());
				return generated;
			}

		});
		futuresMap.put(coverageDescriptor, toReturn);
		logger.debug("coverage added to futuresMap");
		logger.trace("time to execute get with store is "+(System.currentTimeMillis()-start));
		return toReturn;
	
	}





	@SuppressWarnings("serial")
	@Override
	public <T extends CoverageDescriptor> Iterator<T> getByCoverage(Class<T> clazz,final CoverageDescriptor descriptor) throws Exception {
		HashMap<String, Object> fieldsMap = new HashMap<String, Object>(){{
			put("tableId", descriptor.getTableId());
			put("parameters", descriptor.getParameters());
			if (!descriptor.isCustomized())put("customized",false);
		}};
		
		return ObjectPersistency.get(clazz).getObjectByFields(fieldsMap).iterator();
	}


	@Override
	public Iterator<WMSContext> getWMSContextByLayer(String layerId)
			throws Exception {
		Iterator<ContextLayerPair> pairIt = ObjectPersistency.get(ContextLayerPair.class).getObjectByField("layerId", layerId).iterator();	
		while (pairIt.hasNext()){
			
		}
		return null;
	}




	@SuppressWarnings("serial")
	@Override
	public <T extends Storable> StoreResponse<T> store(Class<T> clazz, ObjectManager<T> manager, StoreConfiguration config,
			final CoverageDescriptor... descriptor) throws Exception {
		if (descriptor.length>0 && !descriptor[0].isCustomized() && CoverageDescriptor.class.isAssignableFrom(clazz)){
			HashMap<String, Object> fieldsMap = new HashMap<String, Object>(){{
				put("tableId", descriptor[0].getTableId());
				put("parameters", descriptor[0].getParameters());
				put("customized",false);
			}};
			if (ObjectPersistency.get(clazz).existEntryByFields(fieldsMap)){ 
				if(config.getMode()== StoreMode.USE_EXISTING){
					T stored = ObjectPersistency.get(clazz).getObjectByFields(fieldsMap).get(0);
					return new StoreResponse<T>(stored, PerformedOperation.USED_EXISTING);
				}else if (config.getMode() ==StoreMode.UPDATE_EXISTING){
					String oldId = ObjectPersistency.get(clazz).getObjectByFields(fieldsMap).get(0).getId();
					T stored = ObjectPersistency.get(clazz).getObjectByFields(fieldsMap).get(0);
					T generated = manager.update(stored);
					generated.setId(oldId);
					ObjectPersistency.get(clazz).update(generated);
					return new StoreResponse<T>(generated, PerformedOperation.UPDATED_EXISTING);
				}
				else throw new Exception("error storing object");
			}else{
				T generated = manager.generate();
				ObjectPersistency.get(clazz).insert(generated);
				return new StoreResponse<T>(generated, PerformedOperation.NEWLY_INSERTED);
			}
		}else {
			T generated = manager.generate();
			if (ObjectPersistency.get(clazz).existsKey(generated.getId())){
				if(config.getMode()== StoreMode.USE_EXISTING){
					ObjectPersistency.get(clazz).getByKey(generated.getId());
					return new StoreResponse<T>(generated, PerformedOperation.USED_EXISTING);
				} else if(config.getMode()== StoreMode.UPDATE_EXISTING){
					ObjectPersistency.get(clazz).update(generated);
					return new StoreResponse<T>(generated, PerformedOperation.UPDATED_EXISTING);
				} else throw new Exception("error storing object");
			}else{
				ObjectPersistency.get(clazz).insert(generated);
				return new StoreResponse<T>(generated, PerformedOperation.NEWLY_INSERTED);
			}
		}
	
	}




	@Override
	public <T extends Storable> void deleteById(Class<T> clazz,ObjectManager<T> manager, String id) throws Exception {
		manager.destroy(ObjectPersistency.get(clazz).getByKey(id));
		ObjectPersistency.get(clazz).deleteByKey(id);
	}




	@Override
	public <T extends Storable> T getById(Class<T> clazz, String id)
			throws Exception {
		return ObjectPersistency.get(clazz).getByKey(id);
	}

	@Override
	public List<Layer> getLayersBySpeciesIds(String speciesId) throws Exception {
		List<Layer> toReturn= new ArrayList<Layer>();
		Iterator<LayerSpeciesIdPair> layerSpeciesIdPairIt = ObjectPersistency.get(LayerSpeciesIdPair.class).getObjectByField("speciesId", speciesId).iterator();
		while (layerSpeciesIdPairIt.hasNext())
			toReturn.add(ObjectPersistency.get(Layer.class).getByKey(layerSpeciesIdPairIt.next().getId()));
		return toReturn;	
	}

	@Override
	public List<FileSet> getFileSetsBySpeciesIds(String speciesId) throws Exception {
		List<FileSet> toReturn= new ArrayList<FileSet>();
		Iterator<FileSetSpeciesIdPair> fileSetSpeciesIdPairIt = ObjectPersistency.get(FileSetSpeciesIdPair.class).getObjectByField("speciesId", speciesId).iterator();
		while (fileSetSpeciesIdPairIt.hasNext())
			toReturn.add(ObjectPersistency.get(FileSet.class).getByKey(fileSetSpeciesIdPairIt.next().getId()));
		return toReturn;	
	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub
		
	}


	public String getWebServerUrl() {
		return webServerUrl;
	}

	/**
	 * @return the serverPathDir
	 */
	public File getServerPathDir() {
		return serverPathDir;
	}
}
