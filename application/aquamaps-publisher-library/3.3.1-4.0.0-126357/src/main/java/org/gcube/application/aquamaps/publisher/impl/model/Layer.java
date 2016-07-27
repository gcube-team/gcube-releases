package org.gcube.application.aquamaps.publisher.impl.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;
import org.gcube.application.aquamaps.publisher.MetaInformations;
import org.gcube.application.aquamaps.publisher.impl.model.searchsupport.LayerSpeciesIdPair;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.ObjectNotFoundException;
import org.gcube.common.dbinterface.persistence.ObjectPersistency;
import org.gcube.common.dbinterface.persistence.PersistencyCallback;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;

@TableRootDefinition
public class Layer extends CoverageDescriptor implements Storable{

	public static GCUBELog logger = new GCUBELog(Layer.class);
	
	static{
		try{
			ObjectPersistency.get(Layer.class).addCallback(new PersistencyCallback<Layer>() {

				@Override
				public void onObjectLoaded(Layer obj) {
					ArrayList<String> toSet = new ArrayList<String>();
					try{
						Iterator<ContextLayerPair> it = ObjectPersistency.get(ContextLayerPair.class).getObjectByField("layerId", obj.getId()).iterator();
						while (it.hasNext())
							toSet.add(it.next().getWmsContextId());
					}catch(Exception e){ toSet = new ArrayList<String>();}
					obj.setWmsContextsId(toSet);
					toSet = new ArrayList<String>();
					try{
						Iterator<LayerSpeciesIdPair> it = ObjectPersistency.get(LayerSpeciesIdPair.class).getObjectByField("id", obj.getId()).iterator();
						while (it.hasNext())
							toSet.add(it.next().getSpeciesId());
					}catch(Exception e){ toSet = new ArrayList<String>();}
					obj.setSpeciesIds(toSet.toArray(new String[toSet.size()]));
				}

				@Override
				public void onObjectUpdated(Layer obj) {
					try{
						ObjectPersistency.get(ContextLayerPair.class).deleteByValue("layerId", obj.getId());
						for (String contextId : obj.getWmsContextsId())
							ObjectPersistency.get(ContextLayerPair.class).insert(new ContextLayerPair(contextId, obj.getId()));
					}catch(Exception e){logger.warn("error updating layer WMSContext relation",e);}
					try{
						ObjectPersistency.get(LayerSpeciesIdPair.class).deleteByValue("id", obj.getId());
						for (String speciesId : obj.getSpeciesIds())
							ObjectPersistency.get(LayerSpeciesIdPair.class).insert(new LayerSpeciesIdPair(speciesId, obj.getId()));
					}catch(Exception e){logger.warn("error updating layer speciesId relation",e);}
				}

				@Override
				public void onObjectStored(Layer obj) {
					try{
						for (String speciesId : obj.getSpeciesIds())
							ObjectPersistency.get(LayerSpeciesIdPair.class).insert(new LayerSpeciesIdPair(speciesId, obj.getId()));
					}catch(Exception e){logger.warn("error storing layer speciesId relation",e);}
				}
				
				@Override
				public void onObjectDeleted(Layer obj) {
					try{
						ObjectPersistency.get(ContextLayerPair.class).deleteByValue("layerId", obj.getId());
					}catch(Exception e){logger.warn("error deleting layer WMSContext relation",e);}
					try{
						ObjectPersistency.get(LayerSpeciesIdPair.class).deleteByValue("id", obj.getId());
					}catch(Exception e){logger.warn("error deleting layer speciesId relation",e);}
				}
			});
		}catch (Exception e) {
			logger.error("error adding callback",e);
		}
	}
	
	@FieldDefinition(precision={200}, specifications={Specification.NOT_NULL, Specification.PRIMARY_KEY})
	private String id;
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private LayerType type;
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private LayerInfo layerInfo;
	@FieldDefinition(specifications={Specification.NOT_NULL})
	private MetaInformations metaInfo;
	
	private String[] speciesIds;
	
	private ArrayList<String> wmsContextsId;
	
	public Layer(LayerType type, boolean customized,
			LayerInfo layerInfo, CoverageDescriptor coverage, MetaInformations metaInfo, String ... speciesIds) {
		super(coverage.getTableId(), coverage.getParameters());
		this.id = layerInfo.getName();
		this.type = type;
		this.layerInfo = layerInfo;
		this.metaInfo = metaInfo;
		this.wmsContextsId = new ArrayList<String>();
		if (speciesIds!=null)
			this.speciesIds = speciesIds;
		else this.speciesIds = new String[0];
	}
	
	@SuppressWarnings("unused")
	private Layer(){}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	
	
	
	/**
	 * @return the wmsContextsId
	 */
	public ArrayList<String> getWmsContextsId() {
		return wmsContextsId;
	}

	/**
	 * @param wmsContextsId the wmsContextsId to set
	 */
	public void setWmsContextsId(ArrayList<String> wmsContextsId) {
		this.wmsContextsId = wmsContextsId;
	}

	/**
	 * @return the metaInfo
	 */
	public MetaInformations getMetaInfo() {
		return metaInfo;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public LayerType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(LayerType type) {
		this.type = type;
	}
		
	/**
	 * @return the layerInfo
	 */
	public LayerInfo getLayerInfo() {
		return layerInfo;
	}

	
	/**
	 * @return the speciesIds
	 */
	public String[] getSpeciesIds() {
		return speciesIds;
	}

	/**
	 * @param speciesIds the speciesIds to set
	 */
	public void setSpeciesIds(String[] speciesIds) {
		this.speciesIds = speciesIds;
	}

	protected static Layer get(String id) throws ObjectNotFoundException, Exception{
		return ObjectPersistency.get(Layer.class).getByKey(id);
	}
	
	protected static void remove(String id) throws ObjectNotFoundException, Exception{
		ObjectPersistency.get(Layer.class).deleteByKey(id);
	}

	

	



	
}
