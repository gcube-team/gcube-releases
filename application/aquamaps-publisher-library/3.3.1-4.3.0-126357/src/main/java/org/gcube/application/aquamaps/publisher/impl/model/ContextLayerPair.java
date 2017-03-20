package org.gcube.application.aquamaps.publisher.impl.model;

import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class ContextLayerPair {

	@FieldDefinition(precision={40}, specifications={Specification.NOT_NULL})
	private String wmsContextId;
	@FieldDefinition(precision={40}, specifications={Specification.NOT_NULL})
	private String layerId;
	
	
	@SuppressWarnings("unused")
	private ContextLayerPair(){}
		
	public ContextLayerPair(String wmsContextId, String layerId) {
		super();
		this.wmsContextId = wmsContextId;
		this.layerId = layerId;
	}

	
	/**
	 * @return the wmsContextId
	 */
	public String getWmsContextId() {
		return wmsContextId;
	}
	/**
	 * @return the layerId
	 */
	public String getLayerId() {
		return layerId;
	}
	
	
	
}
