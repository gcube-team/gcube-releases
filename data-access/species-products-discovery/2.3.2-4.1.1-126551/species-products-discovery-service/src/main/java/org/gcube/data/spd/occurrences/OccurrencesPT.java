package org.gcube.data.spd.occurrences;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.runtime.AccessPoint;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.tools.PointsMapCreator;
import org.gcube.contentmanagement.timeseries.geotools.utils.OccurrencePointVector2D;
import org.gcube.data.spd.context.ServiceContext;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.OccurrenceWriterManager;
import org.gcube.data.spd.model.KeyValue;
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.PluginManager;
import org.gcube.data.spd.plugin.fwk.readers.LocalReader;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.Writer;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.LocalWrapper;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.ResultWrapper;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.generators.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrencesPT extends GCUBEPortType {

	Logger logger = LoggerFactory.getLogger(OccurrencesPT.class);

	/**{@inheritDoc}*/
	@Override	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	public enum ExecType {
		IDS, 
		KEYS
	} 
	
	public String getByKeys(String keysLocator) throws GCUBEFault{
		try{
			Stream<String> reader = convert(URI.create(keysLocator)).ofStrings().withDefaults();
			ResultWrapper<OccurrencePoint> wrapper = new ResultWrapper<OccurrencePoint>();
			logger.trace("entering in the getOccurrence by productKeys ");
			ServiceContext.getContext().getSearchThreadPool().execute(ScopedTasks.bind(new RunnableOccurrenceSearch(reader, wrapper, ExecType.KEYS)));
			return wrapper.getLocator().toString();
		}catch (Exception e) {
			logger.error("error getting occurrences by ids");
			throw new GCUBEFault(e);
		}
	}

	
	public String getByIds(String IdsLocator) throws GCUBEFault{
		try{
			Stream<String> reader = convert(URI.create(IdsLocator)).ofStrings().withDefaults();
			ResultWrapper<OccurrencePoint> wrapper = new ResultWrapper<OccurrencePoint>();
			ServiceContext.getContext().getSearchThreadPool().execute(ScopedTasks.bind(new RunnableOccurrenceSearch(reader, wrapper, ExecType.IDS)));
			return wrapper.getLocator().toString();
		}catch (Exception e) {
			logger.error("error getting occurrences by ids");
			throw new GCUBEFault(e);
		}		
	}

	//TODO: DELETE this shit method
	public String createLayer(String coordinateLocator) throws GCUBEFault{
		try{
			final Stream<PointInfo> reader = pipe(convert(URI.create(coordinateLocator)).ofStrings().withDefaults()).through(new Generator<String, PointInfo>() {

				@Override
				public PointInfo yield(String value) {
					try {
						return Bindings.fromXml(value);
					} catch (Exception e) {
						logger.trace("error recovering PointInfo object from xml",e);
						throw new StreamSkipSignal();
					}
				}
				
			});
			TSGeoToolsConfiguration configuration = getTSGeotoolsConfiguration();
			GISInformation gisInfo = getGISInformation();
			logger.info("GIS configuration finished");
			List<OccurrencePointVector2D> xyPoints = new ArrayList<OccurrencePointVector2D>();
			int totalRecordSent =0;
			while (reader.hasNext()){
				PointInfo coord = reader.next();
				OccurrencePointVector2D occPoints = new OccurrencePointVector2D((float)coord.getX(),(float) coord.getY());
				
				logger.trace("x="+coord.getX()+" y="+coord.getY());
				//add metadata
				if (coord.getPropertiesList()!=null)
					for (KeyValue entry: coord.getPropertiesList())
						occPoints.addMetadataToMap(entry.getKey(), entry.getValue());			
				xyPoints.add(occPoints);
				totalRecordSent++;
			}
			logger.trace("total record sent "+totalRecordSent);
			PointsMapCreator pmcreator = new PointsMapCreator(configuration);
			String destinationMapTable = "occ"+UUID.randomUUID().toString().replace("-", "");
			String destinationMapName = "occurrence points";
			String groupCreated =pmcreator.createMapFromPoints(xyPoints, destinationMapTable, destinationMapName, gisInfo);
			logger.info("gis tabel is "+destinationMapTable+" and groupId "+groupCreated);
			return groupCreated;
		}catch (Exception e) {
			logger.error("error creating layer",e);
			throw new GCUBEFault(e);
		}

	}

	private TSGeoToolsConfiguration getTSGeotoolsConfiguration() throws Exception{
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath((String)ServiceContext.getContext().getProperty("configDir", true));


		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERuntimeResourceQuery query = client.getQuery(GCUBERuntimeResourceQuery.class);

		query.addAtomicConditions(new AtomicCondition("/Profile/Category","Gis"), new AtomicCondition("/Profile/Name", "TimeSeriesDataStore"));
		List<GCUBERuntimeResource> timeSeriesDatastoreResources =client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		if (timeSeriesDatastoreResources.size()==0){
			String erroreMessage = "no runtimeResources found for timeseries datastore";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}

		boolean timeseriesDatastoreAccesspointFound= false;
		for (AccessPoint accessPoint: timeSeriesDatastoreResources.get(0).getAccessPoints()){
			if (accessPoint.getEntryname().equals("jdbc")){
				configuration.setGeoServerDatabase(accessPoint.getEndpoint());
				configuration.setGeoServerUserName(accessPoint.getUsername());
				configuration.setGeoServerPassword(accessPoint.getPassword());
				timeseriesDatastoreAccesspointFound = true;
				break;
			}
		}
		if (!timeseriesDatastoreAccesspointFound) {
			String erroreMessage = "no accesspoint found for timeseries datastore entry";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}
		return configuration;
	}

	private GISInformation getGISInformation() throws Exception{
		GISInformation gisInfo = new GISInformation();
		//preparing Geonework connection
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERuntimeResourceQuery query = client.getQuery(GCUBERuntimeResourceQuery.class);		
		query.clearConditions();
		query.addAtomicConditions(new AtomicCondition("/Profile/Category","Gis"), new AtomicCondition("/Profile/Name", "GeoNetwork"));
		List<GCUBERuntimeResource> geonetworkResources =client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		if (geonetworkResources.size()==0){
			String erroreMessage = "no runtimeResources found for Geonetwork";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}

		boolean geoNetworkAccesspointFound= false;
		for (AccessPoint accessPoint: geonetworkResources.get(0).getAccessPoints()){
			if (accessPoint.getEntryname().equals("geonetwork")){
				gisInfo.setGeoNetworkUrl(accessPoint.getEndpoint());
				gisInfo.setGeoNetworkUserName(accessPoint.getUsername());
				gisInfo.setGeoNetworkPwd(accessPoint.getPassword());
				geoNetworkAccesspointFound = true;
				break;
			}
		}
		if (!geoNetworkAccesspointFound) {
			String erroreMessage = "no accesspoint found for geonetwork entry";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}


		//preparing Geoserver connection
		query.clearConditions();
		query.addAtomicConditions(new AtomicCondition("/Profile/Category","Gis"), new AtomicCondition("/Profile/Name", "GeoServer"));
		List<GCUBERuntimeResource> geoserverResources =client.execute(query, GCUBEScope.getScope(ScopeProvider.instance.get()));
		if (geoserverResources.size()==0){
			String erroreMessage = "no runtimeResources found for GeoServer";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}
		boolean geoServerAccesspointFound= false;
		for (AccessPoint accessPoint: geoserverResources.get(0).getAccessPoints()){
			if (accessPoint.getEntryname().equals("geoserver")){
				gisInfo.setGisUserName(accessPoint.getUsername());
				gisInfo.setGisPwd(accessPoint.getPassword());
				gisInfo.setGisUrl(accessPoint.getEndpoint());
				gisInfo.setGisDataStore(accessPoint.getProperty("timeseriesDataStore"));
				gisInfo.setGisWorkspace(accessPoint.getProperty("timeseriesWorkspace"));
				geoServerAccesspointFound = true;
				break;
			}
		}
		if (!geoServerAccesspointFound) {
			String erroreMessage = "no accesspoint found for geoserver entry";
			logger.error(erroreMessage);
			throw new Exception(erroreMessage);
		}
		
		return gisInfo;
	}

	public class RunnableOccurrenceSearch implements Runnable{

		private Stream<String> reader;
		private ResultWrapper<OccurrencePoint> wrapper;
		private ExecType execType;
		
		public RunnableOccurrenceSearch(Stream<String> reader,
				ResultWrapper<OccurrencePoint> wrapper, ExecType execType) {
			super();
			this.reader = reader;
			this.wrapper = wrapper;
			this.execType = execType;
		}

		@Override
		public void run(){
			Map<String, Writer<String>> pluginMap= new HashMap<String, Writer<String>>();
			while (reader.hasNext()){
				String key = reader.next();
				try{
					final String provider = Util.getProviderFromKey(key);
					String id = Util.getIdFromKey(key);
					logger.trace("key arrived "+id+" for provider "+provider);
					if (!pluginMap.containsKey(provider)){
						final LocalWrapper<String> localWrapper = new LocalWrapper<String>();
						Writer<String> localWriter = new Writer<String>(localWrapper);
						//localWriter.register();
						pluginMap.put(provider, localWriter);
						if (execType == ExecType.KEYS) 
							ServiceContext.getContext().getSearchThreadPool().execute(ScopedTasks.bind(new RunnableOccurrenceByKeys(provider, wrapper, localWrapper)));
						else ServiceContext.getContext().getSearchThreadPool().execute(ScopedTasks.bind(new RunnableOccurrenceByIds(provider, wrapper, localWrapper)));
					} 
					logger.trace("key put "+id+"? "+( pluginMap.get(provider).write(id)));
				}catch (IdNotValidException e) {
					logger.warn("the key "+key+" is not valid");
				}
			}
			logger.trace("is wrapper closed? "+wrapper.isClosed());
			for (Writer<String> entry : pluginMap.values())
				entry.close();
			reader.close();
		}
	


	}
	
	public class RunnableOccurrenceByKeys implements Runnable{
		
		private String provider;
		private AbstractWrapper<OccurrencePoint> wrapper;
		private LocalWrapper<String> localWrapper;
		
		
		
		public RunnableOccurrenceByKeys(String provider,
				AbstractWrapper<OccurrencePoint> wrapper, 
				LocalWrapper<String> localWrapper) {
			super();
			this.provider = provider;
			this.wrapper = wrapper;
			this.localWrapper = localWrapper;
		}



		@Override
		public void run(){
			logger.trace("call to provider "+provider);
			final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper, new OccurrenceWriterManager(provider));
			writer.register();
			try {
				new QueryRetryCall(){

					@Override
					protected VOID execute() throws ExternalRepositoryException {
						PluginManager.get().plugins().get(provider).getOccurrencesInterface().getOccurrencesByProductKeys(writer, new LocalReader<String>(localWrapper));
						return new VOID();
					}
					
				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamBlockingException(provider));
			}
			writer.close();
			logger.trace("writer is closed ? "+(!writer.isAlive()));
		}
		
	}
	
	public class RunnableOccurrenceByIds implements Runnable{
		
		private String provider;
		private AbstractWrapper<OccurrencePoint> wrapper;
		private LocalWrapper<String> localWrapper;
		
		
		
		public RunnableOccurrenceByIds(String provider,
				AbstractWrapper<OccurrencePoint> wrapper, 
				LocalWrapper<String> localWrapper) {
			super();
			this.provider = provider;
			this.wrapper = wrapper;
			this.localWrapper = localWrapper;
		}



		@Override
		public void run(){
			logger.trace("call to provider "+provider);
			final Writer<OccurrencePoint> writer = new Writer<OccurrencePoint>(wrapper, new OccurrenceWriterManager(provider));
			writer.register();
			try {
				new QueryRetryCall(){

					@Override
					protected VOID execute() throws ExternalRepositoryException {
						PluginManager.get().plugins().get(provider).getOccurrencesInterface().getOccurrencesByIds(writer, new LocalReader<String>(localWrapper));
						return new VOID();
					}
					
				}.call();
			} catch (MaxRetriesReachedException e) {
				writer.write(new StreamBlockingException(provider));
			}
			
			writer.close();
		}
		
	}
	
}
