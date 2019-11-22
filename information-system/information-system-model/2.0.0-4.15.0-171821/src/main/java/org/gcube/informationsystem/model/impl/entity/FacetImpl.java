/**
 * 
 */
package org.gcube.informationsystem.model.impl.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.informationsystem.model.reference.entity.Facet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Facet.NAME)
public abstract class FacetImpl extends EntityImpl implements Facet {
	
	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 6075565284892615813L;
	
	@JsonIgnore
	protected Map<String, Object> additionalProperties;
	
	@JsonIgnore
	/**
	 * Used to allow to have an additional property starting with '_' or '@'
	 */
	protected final Set<String> allowedAdditionalKeys;
	
	protected FacetImpl(){
		super();
		this.additionalProperties = new HashMap<>();
		this.allowedAdditionalKeys = new HashSet<>();
		this.allowedAdditionalKeys.add(SUPERCLASSES_PROPERTY);
	}
	
	@Override
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	@Override
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public Object getAdditionalProperty(String key) {
		return additionalProperties.get(key);
	}

	@Override
	public void setAdditionalProperty(String key, Object value) {
		if(!allowedAdditionalKeys.contains(key)){
			if(key.startsWith("_")) {
				return;
			}
			if(key.startsWith("@")) {
				return;
			}
		}
		this.additionalProperties.put(key, value);
	}

	public void addAllowedAdditionalKey(String allowedAdditionalKey){
		this.allowedAdditionalKeys.add(allowedAdditionalKey);
	}

}
