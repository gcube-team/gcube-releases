package org.gcube.application.aquamaps.publisher;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import org.gcube.application.aquamaps.publisher.impl.PublisherImpl;
import org.gcube.application.aquamaps.publisher.impl.datageneration.ObjectManager;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.Storable;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;


public abstract class Publisher {

	public static Publisher getPublisher(){return new PublisherImpl();}
	
	
	//********************* MANAGEMENT
	public abstract ReportDescriptor initialize(PublisherConfiguration configuration)throws Exception;
	
	
	//********************* STORE METHODS
	public abstract <T extends Storable> StoreResponse<T> store(Class<T> clazz, ObjectManager<T> generator, StoreConfiguration config,
			CoverageDescriptor... descriptor) throws Exception ;
	
	//********************* GET METHODS
	public abstract <T extends CoverageDescriptor> Future<T> get(Class<T> clazz, ObjectManager<T> manager, CoverageDescriptor coverageDescriptor)throws Exception;
	
	//********************* DELETE METHODS
	public abstract <T extends Storable> void deleteById(Class<T> clazz, ObjectManager<T> manager, String id)throws Exception;
	
	
	//********************* QUERY METHODS
	public abstract <T extends Storable> T getById(Class<T> clazz, String id) throws Exception;
	
	public abstract <T extends CoverageDescriptor> Iterator<T> getByCoverage(Class<T> clazz,final CoverageDescriptor descriptor) throws Exception;
	
	public abstract List<Layer> getLayersBySpeciesIds(String speciesId) throws Exception;
	
	public abstract List<FileSet> getFileSetsBySpeciesIds(String speciesId) throws Exception;
	
	public abstract Iterator<WMSContext> getWMSContextByLayer(String layerId)throws Exception;
	
	public abstract void shutdown() throws Exception;
	
	public abstract String getWebServerUrl();

	public abstract File getServerPathDir();
}
