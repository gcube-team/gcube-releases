package org.gcube.application.aquamaps.publisher.impl.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.ObjectPersistency;
import org.gcube.common.dbinterface.persistence.PersistencyCallback;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;
import org.gcube.common.gis.datamodel.enhanced.WMSContextInfo;

@TableRootDefinition
public class WMSContext implements Storable{

	public static GCUBELog logger = new GCUBELog(WMSContext.class);
	
	static{
		try{
			ObjectPersistency.get(WMSContext.class).addCallback(new PersistencyCallback<WMSContext>() {

				@Override
				public void onObjectLoaded(WMSContext obj) {
					ArrayList<String> toSet = new ArrayList<String>();
					try{
						Iterator<ContextLayerPair> it = ObjectPersistency.get(ContextLayerPair.class).getObjectByField("wmsContextId", obj.getId()).iterator();
						while (it.hasNext())
							toSet.add(it.next().getLayerId());
					}catch(Exception e){ toSet = new ArrayList<String>();}
					obj.setLayersId(toSet);
				}

				@Override
				public void onObjectStored(WMSContext obj) {
					try{
						for (String layerId : obj.getLayersId())
							ObjectPersistency.get(ContextLayerPair.class).insert(new ContextLayerPair(obj.getId(), layerId));
					}catch(Exception e){logger.warn("error storin layer WMSContext relation",e);}
				}

				@Override
				public void onObjectUpdated(WMSContext obj) {
					try{
						ObjectPersistency.get(ContextLayerPair.class).deleteByValue("wmsContextId", obj.getId());
						for (String layerId : obj.getLayersId())
							ObjectPersistency.get(ContextLayerPair.class).insert(new ContextLayerPair(obj.getId(), layerId));
					}catch(Exception e){logger.warn("error storin layer WMSContext relation",e);}
					
				}

				@Override
				public void onObjectDeleted(WMSContext obj) {
					try{
						ObjectPersistency.get(ContextLayerPair.class).deleteByValue("wmsContextId", obj.getId());
					}catch(Exception e){logger.warn("error deleting layer WMSContext relation",e);}
				}
			});
		}catch (Exception e) {
			logger.error("error adding callback",e);
		}
	}
	
	@FieldDefinition(precision={40}, specifications={Specification.NOT_NULL, Specification.PRIMARY_KEY})
	private String id;
	@FieldDefinition()
	private WMSContextInfo wmsContextInfo;
	
	private ArrayList<String> layersId;
	
	@SuppressWarnings("unused")
	private WMSContext(){}
	
	public WMSContext(WMSContextInfo wmsContextInfo, ArrayList<String> layersId) {
		this.id = wmsContextInfo.getName();
		this.wmsContextInfo = wmsContextInfo;
		this.layersId = layersId;
	}
	/**
	 * @return the layersId
	 */
	public ArrayList<String> getLayersId() {
		return layersId;
	}
		
	/**
	 * @param layersId the layersId to set
	 */
	public void setLayersId(ArrayList<String> layersId) {
		this.layersId = layersId;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the wmsContextInfo
	 */
	public WMSContextInfo getWmsContextInfo() {
		return wmsContextInfo;
	}
	
}
