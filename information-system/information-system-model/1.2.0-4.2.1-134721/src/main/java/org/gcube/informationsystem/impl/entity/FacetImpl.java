/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.exceptions.InvalidFacet;

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
	
	/**
	 * Used to allow to have an additional property starting with '_' or '@'
	 */
	protected final static Set<String> allowedAdditionalKeys;
	
	static {
		allowedAdditionalKeys = new HashSet<>();
	}
	
	public FacetImpl(){
		super();
		additionalProperties = new HashMap<>();
	}
	
	@Override
	public void validate() throws InvalidFacet {
		throw new UnsupportedOperationException();
		/*
		for (Method m : this.getClass().getDeclaredMethods()){
			m.setAccessible(true);
			if(m.isAnnotationPresent(ISProperty.class)){
				ISProperty propAnnotation = m.getAnnotation(ISProperty.class);
				if(propAnnotation.mandatory() || !propAnnotation.nullable()) {
					Object o;
					try {
						o = m.invoke(this, (Object[]) null);
					} catch (IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						continue;
					}
					if(o==null){
						throw new InvalidFacet("");
					}
				}
				if(propAnnotation.min()!=-1){
					
				}
				
				if(propAnnotation.min()!=-1){
					
				}
				
				if(propAnnotation.regexpr().compareTo("")!=0){
					
				}
				
			}  

		}
		*/
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
}
