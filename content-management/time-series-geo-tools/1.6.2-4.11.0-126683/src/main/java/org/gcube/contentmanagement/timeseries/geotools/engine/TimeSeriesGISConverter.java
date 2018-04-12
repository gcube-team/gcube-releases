package org.gcube.contentmanagement.timeseries.geotools.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.filters.AFilter;
import org.gcube.contentmanagement.timeseries.geotools.finder.GeoAreaFinder;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerSaver;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations.featuresTypes;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;
import org.gcube.contentmanagement.timeseries.geotools.tools.GeoGroupCache;
import org.gcube.contentmanagement.timeseries.geotools.utils.Tuple;

public class TimeSeriesGISConverter {

	public ConnectionsManager connectionManager;
	public TSGeoToolsConfiguration currentConfiguration;
	public static String persistenceFile = "timeSeries2GisCache.dat";
	public String persistencePath;
	static int maxTries = 5;
	boolean cached;
	EngineConfiguration tscfg = null;
	EngineConfiguration geocfg = null;
	EngineConfiguration aquacfg = null;
	
	private float status;

	public TimeSeriesGISConverter(TSGeoToolsConfiguration configuration) throws Exception {
		initDataSources(configuration);
	}

	private void initDataSources(TSGeoToolsConfiguration configuration) throws Exception {

		currentConfiguration = configuration;

		String configPath = configuration.getConfigPath();

		if (!configPath.endsWith("/"))
			configPath += "/";

		// persistence file setup
		if (configuration.getPersistencePath() != null)
			persistencePath = configuration.getPersistencePath();
		else
			persistencePath = configPath;
		
		if (!persistencePath.endsWith("/"))
			persistencePath += "/";
		
		persistencePath += persistenceFile;

		AnalysisLogger.setLogger(configPath + "ALog.properties");
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> initializing connections");
		
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> databasesURLS");
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> Aquamaps: "+configuration.getAquamapsDatabase());
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> GeoServer: "+configuration.getGeoServerDatabase());
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> TimeSeries: "+configuration.getTimeSeriesDatabase());
		
		connectionManager = new ConnectionsManager(configPath);

		tscfg = null;
		geocfg = null;
		aquacfg = null;

		if (configuration.getTimeSeriesDatabase() != null) {
			tscfg = new EngineConfiguration();
			tscfg.setConfigPath(configPath);
			tscfg.setDatabaseUserName(configuration.getTimeSeriesUserName());
			tscfg.setDatabasePassword(configuration.getTimeSeriesPassword());
			tscfg.setDatabaseURL(configuration.getTimeSeriesDatabase());
		}
		if (configuration.getAquamapsDatabase() != null) {
			aquacfg = new EngineConfiguration();
			aquacfg.setConfigPath(configPath);
			aquacfg.setDatabaseUserName(configuration.getAquamapsUserName());
			aquacfg.setDatabasePassword(configuration.getAquamapsPassword());
			aquacfg.setDatabaseURL(configuration.getAquamapsDatabase());
		}
		if (configuration.getGeoServerDatabase() != null) {
			geocfg = new EngineConfiguration();
			geocfg.setConfigPath(configPath);
			geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
			geocfg.setDatabasePassword(configuration.getGeoServerPassword());
			geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
		}

		// initialize geogroup cache
		cached = false;
	}

	public TimeSeriesGISConverter(String configPath) throws Exception {
		TSGeoToolsConfiguration cfg = new TSGeoToolsConfiguration();
		cfg.setConfigPath(configPath);
		initDataSources(cfg);
	}

	public void TimeSeriesToGIS(List<AFilter> filters, GISInformation gisInfo) throws Exception {
		TimeSeriesToGIS(filters, gisInfo, false);
	}

//	private String groupName;

	public List<String> checkGroupingInCache(List<AFilter> filters, GISInformation gisInfo) {
//		groupName = "group4" + UUID.randomUUID();
		cached = false;
		List<String> cachedLayers = null;
		if (filters.size() == 1) {
			long t000 = System.currentTimeMillis();
			try {
				GeoGroupCache cache = GeoGroupCache.getInstance(persistencePath);
				long t001 = System.currentTimeMillis();
				AnalysisLogger.getLogger().trace("TimeSeriesToGIS->Cache Loaded in " + (t001 - t000) + "ms");
				Tuple<String> layers = cache.getCachedElement(getTSIdentifier(filters), filters.get(0).getClass().getName());
				AnalysisLogger.getLogger().trace("TimeSeriesToGIS->Time Series: " + filters.get(0).getTimeSeriesName());
				AnalysisLogger.getLogger().trace("TimeSeriesToGIS->Cache: " + filters.get(0).getClass().getName());
				if (layers != null) {
					// check if the group still exists
					if (GISGroupInformation.checkLayers(gisInfo, layers.getElements(), 1)) {
						cached = true;
						AnalysisLogger.getLogger().warn("TimeSeriesToGIS->the Time Series and Filter were present in cache!");
						AnalysisLogger.getLogger().warn("TimeSeriesToGIS->Found Grouping in cache");
						cachedLayers=layers.getElements();
					} else {
						AnalysisLogger.getLogger().trace("...Removing from Cache");
						cache.removeCachedElement(filters.get(0).getTimeSeriesName(), filters.get(0).getClass().getName());
					}
				}
			} catch (Exception e) {
				AnalysisLogger.getLogger().warn("TimeSeriesToGIS->ERROR in checking group " + e.getMessage());
			}
		}
		
		
		return cachedLayers;

	}

	private void initializeConnections() throws Exception{
		connectionManager.initAquamapsConnection(aquacfg);
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> connected to Aquamaps");
		connectionManager.initTimeSeriesConnection(tscfg);
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> connected to Time Series");
		connectionManager.initGeoserverConnection(geocfg);
		AnalysisLogger.getLogger().trace("TimeSeriesGISConverter-> connected to Geo Server");		
	}
	
	public List<String> TimeSeriesToGIS(List<AFilter> filters, GISInformation gisInfo, boolean expandedMode) throws Exception {

		status = 0;
		List<String> createdLayers = null;
		
		GISInformation localGISInfo = new GISInformation();
		
		localGISInfo.setGeoNetworkUrl(gisInfo.getGeoNetworkUrl());
		localGISInfo.setGeoNetworkUserName(gisInfo.getGeoNetworkUserName());
		localGISInfo.setGeoNetworkPwd(gisInfo.getGeoNetworkPwd());
		
		localGISInfo.setGisDataStore(gisInfo.getGisDataStore());
		localGISInfo.setGisPwd(gisInfo.getGisPwd());
		localGISInfo.setGisUrl(gisInfo.getGisUrl());
		localGISInfo.setGisUserName(gisInfo.getGisUserName());
		localGISInfo.setGisWorkspace(gisInfo.getGisWorkspace());
		
		GISLayerSaver gislayersav = null;
		long t0 = System.currentTimeMillis();
		
		createdLayers = checkGroupingInCache(filters, localGISInfo);
		try {
			if (!cached) {
				createdLayers = new ArrayList<String>();
				
				initializeConnections();
				AnalysisLogger.getLogger().warn("TimeSeriesToGIS->the Time Series and Filter were NOT present in cache!");
				List<GISLayer> currentGisLayers = new ArrayList<GISLayer>();
				GeoAreaFinder geofinder = new GeoAreaFinder(connectionManager, currentConfiguration.getReferenceCountriesTable());
				gislayersav = new GISLayerSaver(connectionManager);
				// to be used in non expanded mode
				String tsname = "";
				if (filters.size() > 0)
					// tsname = "compl_"+filters.get(0).getTimeSeriesName();
					tsname = "compl" + UUID.randomUUID();

				GISLayer overallGISLayer = new GISLayer(tsname.replace("-", ""));
				overallGISLayer.setValuesColumnName(AFilter.defaultvaluesColumnName);

				int filtercounter = 0;
				int totalFilters = filters.size();

				// for each Filter create at least one Layer on the GeoServer
				for (AFilter sfilter : filters) {
					try {
						// set the species and country column
						sfilter.setRef_species(currentConfiguration.getReferenceSpeciesTable());
						sfilter.setRef_country(currentConfiguration.getReferenceCountriesTable());

						// inactivate the previous csquares filter
						List<GISLayer> layers = sfilter.filter(null, connectionManager, geofinder);
						AnalysisLogger.getLogger().warn("TimeSeriesToGIS->EXTRACTED " + layers.size() + " LAYERS");
						// in expanded mode create a layer for each filter result
						if (expandedMode) {
							for (GISLayer layer : layers) {
								if (!layer.isEmpty()) {
									AnalysisLogger.getLogger().trace("TimeSeriesToGIS->CREATING GIS LAYER: " + layer.getLayerName());
									// create layer on geoserver db
									gislayersav.createLayerOnDB(layer, featuresTypes.real);
									// add layer to the saving layers
									currentGisLayers.add(layer);
								} else {
									AnalysisLogger.getLogger().warn("TimeSeriesToGIS-> LAYER: " + layer.getLayerName() + " IS EMPTY");
								}
							}
						}
						// in non expanded mode create a layer for all - that is, merge the layers in a single one
						else {
							AnalysisLogger.getLogger().warn("TimeSeriesToGIS->Merging Layers...wait");
							long tt1 = System.currentTimeMillis();
							overallGISLayer.mergeGISLayers(layers);
							long tt2 = System.currentTimeMillis();
							AnalysisLogger.getLogger().warn("TimeSeriesToGIS->Merged in " + (tt2 - tt1) + " ms");
						}
					} catch (Exception e) {
						AnalysisLogger.getLogger().warn("TimeSeriesToGIS->AN ERROR OCCURRED DURING THE APPLICATION OF FILTER NUMBER " + filtercounter);
						e.printStackTrace();
						currentGisLayers = null;
						throw e;
						
					}

					float localstatus = (100f + (float) filtercounter * 100f) / (float) totalFilters;
					status = (localstatus == 100) ? 99 : localstatus;
					filtercounter++;

					AnalysisLogger.getLogger().trace("TimeSeriesToGIS->status: " + status);
				}
				long t1 = System.currentTimeMillis();
				AnalysisLogger.getLogger().warn("Overall Filter Computation Finished - Elapsed Time: " + (t1 - t0) + " ms");
				// if not in expanded mode, create only one layer with all values within

				if (!expandedMode) {
					if (!overallGISLayer.isEmpty()) {
						gislayersav.createLayerOnDB(overallGISLayer, featuresTypes.real);
						// add layer to the saving layers
						currentGisLayers.add(overallGISLayer);
					}
				}

				// create the gis group with all the layers included
				String groupingName = gislayersav.createGISLayers(currentGisLayers, localGISInfo, null, true, true);
				if (groupingName == null)
					throw new Exception("Error in GIS Layers Generation");
				else{
					//create layer names list to be returned
					for (GISLayerInformation layer : localGISInfo.getLayers()){
						AnalysisLogger.getLogger().trace("TimeSeriesToGIS->Adding Layer: "+layer.getLayerName());
						createdLayers.add(layer.getLayerName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().warn("WARNING: AN ERROR OCCURRED DURING OVERALL CALCULATION " + e.getMessage());
			throw e;
		} finally {
			localGISInfo.clean();
			connectionManager.shutdownAll();
			// check if geoserver group has been really generated: http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/rest/layergroups/group4c8cbbdfb-7e33-4ae0-9db4-7916d38e906d.json
			if ((createdLayers!=null) && (createdLayers.size()>0)){ 
				boolean urlcoherence =  GISGroupInformation.checkLayers(gisInfo, createdLayers, maxTries);

				if (!urlcoherence) {
				// remove group from cache
					if ((cached) && (filters.size() == 1)) {
						AnalysisLogger.getLogger().trace("...Removing from Cache");
						GeoGroupCache cache = GeoGroupCache.getInstance(persistencePath);
						cache.removeCachedElement(getTSIdentifier(filters), filters.get(0).getClass().getName());
					}
					throw new Exception("Error in GIS Group Generation - GIS Layers were not created");
				}

				// insert into the cache
				if ((!cached) && (filters.size() == 1)) {
					AnalysisLogger.getLogger().trace("...Caching");
					GeoGroupCache cache = GeoGroupCache.getInstance(persistencePath);
					String[] t = new String [createdLayers.size()];
					createdLayers.toArray(t);
					cache.addCacheElement(getTSIdentifier(filters), filters.get(0).getClass().getName(),t);
				}
				// end insertion in the cache
			}
			
			status = 100f;
			long t2 = System.currentTimeMillis();
			AnalysisLogger.getLogger().warn("Computation Finished - Elapsed Time: " + (t2 - t0) + " ms");
			AnalysisLogger.getLogger().trace("TimeSeriesToGIS->status: " + status);
		}

		return createdLayers;
	}

	public void setStatus(float status) {
		this.status = status;
	}

	public float getStatus() {
		return status;
	}

	
	private String getTSIdentifier(List<AFilter> filters){
		AFilter filter = filters.get(0);
		String id = filter.getTimeSeriesName()+":"+filter.getAggregationColumn()+":"+filter.getInformationColumn()+":"+filter.getQuantitiesColumn()+":"+filter.getRef_country()+":"+filter.getRef_species();
		return id;
	}
}
