package gr.cite.geoanalytics.geospatialbackend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gr.cite.clustermanager.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.clustermanager.model.GosDefinition;
import gr.cite.clustermanager.model.ZNodeData.ZNodeStatus;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.viewbuilders.AbstractViewBuilder;
import gr.cite.gaap.viewbuilders.PostGISMaterializedViewBuilder;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
import gr.cite.geoanalytics.manager.LayerManager;
import gr.cite.gos.client.GeoserverManagement;
import gr.cite.gos.client.ShapeManagement;

public class LayerReplicator {

	final long INIT_START_DELAY = 20;
	final long RUN_EVERY = 10; 
	
	private static LayerReplicator instance;
	
	private static boolean UNDER_REPLICATION = false;
	
	@Autowired private LayerManager layerManager;
	@Autowired private DataMonitor dataMonitor;
	@Autowired private DataCreatorGeoanalytics dataCreator;
	
	@Autowired private ShapeManagement shapeManagement;
	@Autowired private GeoserverManagement geoserverManagement;
	
	//the ones below are needed by the initiation of the viewbuilder (because viewbuilder is not thread-safe)
	@Autowired private GeocodeManager taxonomyManager;
	@Autowired private ConfigurationManager configurationManager;
	@Autowired private Configuration context;
	
	
	private static final Logger logger = LoggerFactory.getLogger(LayerReplicator.class);
	
	public void initiate (){
		
		if(instance==null){
			instance = new LayerReplicator();
			instance.layerManager = layerManager;
			instance.dataMonitor = dataMonitor;
			instance.dataCreator = dataCreator;
			instance.taxonomyManager = taxonomyManager;
			instance.configurationManager = configurationManager;
			instance.shapeManagement = shapeManagement;
			instance.geoserverManagement = geoserverManagement;
			
			ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
			ses.scheduleAtFixedRate(new Runnable() {
			    @Override
			    public void run() {
			    	
			    	logger.info("Running scheduled replication check");
			    	
			    	try {
			    		
			    		final Map<String, Set<GosDefinition>> availableLayerInstances = instance.dataMonitor.getAvailableLayerToGosDefinitionData();
			    		final Map<String, Set<GosDefinition>> notAvailableLayerInstances = instance.dataMonitor.getNotAvailableLayerToGosDefinitionData();
			    		final Set<GosDefinition> allGosEndpoints = instance.dataMonitor.getAllGosEndpoints();
			    		
			    		if(!UNDER_REPLICATION){
				    		UNDER_REPLICATION = true;
				    		instance.layerManager.getAllLayers().stream().forEach(dblayer -> {
				    			replicateLayer(dblayer.getId().toString(), dblayer.getReplicationFactor(), 
				    					allGosEndpoints,
				    					availableLayerInstances.get(dblayer.getId().toString()),
				    					notAvailableLayerInstances.get(dblayer.getId().toString()));
				    		});
				    		UNDER_REPLICATION = false;
			    		}
			    		else
			    			logger.info("A replication is already under progress, skipping...");
			    			
			    		
			    	} catch (Exception e) {
						e.printStackTrace();
						UNDER_REPLICATION = false;
					}
			    	
			    }
			}, INIT_START_DELAY, RUN_EVERY, TimeUnit.SECONDS);
			
		}
		
		
	}
	
	
	
	
	public void replicateLayer(String layerID, int replicationNum, Set<GosDefinition>  allGosEndpointsForThisLayer, Set<GosDefinition> availableGosForThisLayer, Set<GosDefinition> notAvailableGosForThisLayer){
		
		//copying on different objects to perform remove() and add() without affecting use within other classes.
		Set<GosDefinition> allGos = new HashSet<GosDefinition>();
		if(allGosEndpointsForThisLayer!=null)
			allGos.addAll(allGosEndpointsForThisLayer);
		Set<GosDefinition> availableGos = new HashSet<GosDefinition>();
		if(availableGosForThisLayer!=null)
			availableGos.addAll(availableGosForThisLayer);
		Set<GosDefinition> notAvailableGos = new HashSet<GosDefinition>();
		if(notAvailableGosForThisLayer!=null)
			notAvailableGos.addAll(notAvailableGosForThisLayer);
		
		
		if(availableGos==null || availableGos.isEmpty()){ //nowhere to replicate from
			logger.debug("Tried to replicate layer with id: "+layerID+" but there were no available instances to replicate from");
			return;
		}
		if(availableGos.size()==replicationNum){
//			logger.debug("Layer with id: "+layerID+" is already at replication number "+replicationNum);
			return;
		}
		if(notAvailableGos.size()>0){ //means that there is either something in progress for this layer or an error
			//TODO: distinguish between the two different cases and take different actions
			logger.debug("Layer with id: "+layerID+" has not yet been replicated or has malfunctioning instances. Endpoints: "
								+notAvailableGos.stream().map(gosDef -> gosDef.getGosEndpoint()).collect(Collectors.toList()));
			return;
		}
		
		//by this point we should be sure that a replication for this layer should be done!
		logger.debug("Applying replication of layer with id: "+layerID+" to replication num: "+replicationNum +" on all GOS nodes");
		replicationNum -= availableGos.size(); //reduce to those which not already contain the layer (conceptually: "how many remain to replicate")
		
		
		if(replicationNum<0){ //should remove from some nodes (=how much less than 0 it is)
			int howManyToRemove = Math.abs(replicationNum);
			getSubset(availableGos, howManyToRemove).forEach(gosDefinition -> {
				removeLayerFromGos(layerID, gosDefinition);
			});
		}
		else { //should replicate on some nodes (=how much more than 0 it is)
			int howManyMoreReplicas = Math.abs(replicationNum);
			allGos.removeAll(availableGos);
			allGos.removeAll(notAvailableGos);
			if(allGos.size()>=howManyMoreReplicas){ //ensure that this is not the case that we need to replicate on more than we have (eg. have 1 gos and rep_factor=2)
				getSubset(allGos, howManyMoreReplicas).forEach(gosDefinition -> {
					addLayerOnGos(layerID, getARandomOne(availableGos), gosDefinition);
				});
			}
		}
		
		
	}
	
	
	private void addLayerOnGos(String layerId, GosDefinition fromGos, GosDefinition toGos){
		logger.info("Replication manager: replicating layer "+layerId +" from " +fromGos.getGosEndpoint()+" to " +toGos.getGosEndpoint());
		try {
			//notify zookeeper about the deletion (set layer unavailable)
			dataCreator.updateLayerState(layerId, ZNodeStatus.PENDING, toGos.getGosIdentifier());
		}
		catch(Exception ex){
			logger.error("Could not update zookeeper status of replicating layer "+layerId+" on "+toGos.getGosEndpoint());
		}
		try{
			//replicate shapes
			List<Shape> layerShapes = shapeManagement.getShapesOfLayerID(fromGos.getGosEndpoint(), layerId);
			shapeManagement.insertShapes(toGos.getGosEndpoint(), layerShapes);
			try{
				//create view
				ViewBuilder viewBuilder = new PostGISMaterializedViewBuilder(taxonomyManager, configurationManager); //cannot be injected, it's not thread-safe
				((PostGISMaterializedViewBuilder)viewBuilder).setConfiguration(context);
				((AbstractViewBuilder)viewBuilder).setShapeManagement(shapeManagement);
				((AbstractViewBuilder)viewBuilder).setContext(context);
				viewBuilder.forIdentity(layerId).forShapes(layerShapes).createViewStatement().execute(toGos.getGosEndpoint());
			}
			catch(Exception ex){
				logger.error("Could not create database view of layer "+layerId+" from GOS "+fromGos.getGosEndpoint()+" to GOS "+toGos.getGosEndpoint());
			}
		}
		catch(Exception ex){
			logger.error("Could not replicate shapes of layer "+layerId+" from GOS "+fromGos.getGosEndpoint()+" to GOS "+toGos.getGosEndpoint());
		}
		try{
			//replicate geoserver layer
			FeatureType ft = geoserverManagement.getFeatureType(fromGos.getGosEndpoint(), layerId);
			Map<String, String> slds = configurationManager.getLayerStyles();
			GeoserverLayer geoserverLayer = geoserverManagement.getGeoserverLayer(fromGos.getGosEndpoint(), layerId);
			geoserverManagement.addGeoserverLayer(toGos.getGosEndpoint(), geoserverLayer, ft, slds, null, null);
		}
		catch(Exception ex){
			logger.error("Could not replicate geoserver layer "+layerId+" from GOS "+fromGos.getGosEndpoint()+" to GOS "+toGos.getGosEndpoint());
		}
		try{
			//notify zookeeper again about the insertion by updating the corresponding entry
			dataCreator.updateLayerState(layerId, ZNodeStatus.ACTIVE, toGos.getGosIdentifier());
		}
		catch(Exception ex){
			logger.error("Could not update zookeeper status of replicating layer "+layerId+" on "+toGos.getGosEndpoint() +" back to 'ACTIVE'");
		}
		
	}
	
	
	
	private void removeLayerFromGos(String layerId, GosDefinition gosDefinition){
		logger.info("Replication manager: removing layer "+layerId +" from " +gosDefinition.getGosEndpoint());
		try{
			//notify zookeeper about the deletion (set layer unavailable)
			dataCreator.updateLayerState(layerId, ZNodeStatus.PENDING, gosDefinition.getGosIdentifier());
		}
		catch(Exception ex){
			logger.error("Could not update replication status layer "+layerId+" from GOS "+gosDefinition.getGosEndpoint());
		}
		try{
			//delete from geoserver
			geoserverManagement.deleteGeoserverLayer(gosDefinition.getGosEndpoint(), layerId);
		}
		catch(Exception ex){
			logger.error("Could not delete geoserver layer "+layerId+" on GOS "+gosDefinition.getGosEndpoint());
		}
		try{
			//drop view
			ViewBuilder viewBuilder = new PostGISMaterializedViewBuilder(taxonomyManager, configurationManager); //cannot be injected, it's not thread-safe
			((PostGISMaterializedViewBuilder)viewBuilder).setConfiguration(context);
			((AbstractViewBuilder)viewBuilder).setShapeManagement(shapeManagement);
			((AbstractViewBuilder)viewBuilder).setContext(context);
			viewBuilder.forIdentity(layerId).removeViewStatement().execute(gosDefinition.getGosEndpoint());
		}
		catch(Exception ex){
			logger.error("Could not drop layer view "+layerId+" on GOS "+gosDefinition.getGosEndpoint());
		}
		try{
			//delete shapes of layer (drop layer from table)
			shapeManagement.deleteShapesOfLayer(gosDefinition.getGosEndpoint(), layerId);
		}
		catch(Exception ex){
			logger.error("Could not delete shapes of layer "+layerId+" on GOS "+gosDefinition.getGosEndpoint());
		}
		try{
			//notify zookeeper again about the deletion by removing entirely the corresponding entry
			dataCreator.deleteLayer(layerId, gosDefinition.getGosIdentifier());
		}
		catch(Exception ex){
			logger.error("Could not delete zookeeper layer "+layerId+" of GOS "+gosDefinition.getGosIdentifier() +" on zookeeper");
		}
		
	}
	
	
	private static Set<GosDefinition> getSubset(Set<GosDefinition> populated, int howmany){
		List<GosDefinition> list = new ArrayList<GosDefinition>(populated);
		Collections.shuffle(list);
		return new HashSet<GosDefinition>(list.subList(0, howmany));
	}
	
	
	private static GosDefinition getARandomOne(Set<GosDefinition> gosDefinitions){
		List<GosDefinition> goss = new ArrayList<GosDefinition>(gosDefinitions);
		return goss.get(new Random().nextInt(goss.size()));
	}
	

//	public static void main (String [] args) throws InterruptedException{
//	}
	
	
}