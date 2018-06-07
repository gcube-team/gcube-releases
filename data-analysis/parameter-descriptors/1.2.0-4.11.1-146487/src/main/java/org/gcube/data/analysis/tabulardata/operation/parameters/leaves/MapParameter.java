package org.gcube.data.analysis.tabulardata.operation.parameters.leaves;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.LeafParameter;

@SuppressWarnings("rawtypes")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapParameter extends LeafParameter<Map>{

	@SuppressWarnings("unused")
	private MapParameter() {}
	
	private Class<?> keyInstanceType=String.class;
	private Class<?> valueInstanceType=String.class;
	private List<?> expectedKeys=new ArrayList<>();
	
	
	public MapParameter(String identifier, String name, String description,
			Cardinality cardinality, Class<?> keyInstanceType, Class<?> valueInstanceType) {
		super(identifier, name, description, cardinality);	
		this.keyInstanceType = keyInstanceType;
		this.valueInstanceType = valueInstanceType;
	}

	

	public MapParameter(String identifier, String name, String description,
			Cardinality cardinality, Class<?> keyInstanceType,
			Class<?> valueInstanceType, List<?> expectedKeys) {
		super(identifier, name, description, cardinality);
		this.keyInstanceType = keyInstanceType;
		this.valueInstanceType = valueInstanceType;
		this.expectedKeys = expectedKeys;
	}



	@Override
	public Class<Map> getParameterType() {		
		return Map.class;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapParameter [keyInstanceType=");
		builder.append(keyInstanceType);
		builder.append(", valueInstanceType=");
		builder.append(valueInstanceType);
		builder.append(", expectedKeys=");
		builder.append(expectedKeys);
		builder.append(", getIdentifier()=");
		builder.append(getIdentifier());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getCardinality()=");
		builder.append(getCardinality());
		builder.append("]");
		return builder.toString();
	}



	public Class<?> getKeyInstanceType() {
		return keyInstanceType;
	}


	public Class<?> getValueInstanceType() {
		return valueInstanceType;
	}
	
	public List<?> getExpectedKeys() {
		return expectedKeys;
	}
	
	@Override
	public void validateValue(Object valueObj) throws Exception {
		super.validateValue(valueObj);
		Map value=(Map) valueObj;
		for(Object key:expectedKeys)
			if(!value.containsKey(key)) throw new Exception(String.format("Expected key %s not found in map parameter %s",key,getName()));
		if (!value.isEmpty()){
			for (Entry<?,?> entry: (Iterable<Entry<?,?>>)value.entrySet()){
				if(!(keyInstanceType.isInstance(entry.getKey()))) throw new Exception (String.format("Found invalid key class %s in map parameter %s, expected %s",entry.getKey().getClass(),this.getName(),this.keyInstanceType));
				if(!(valueInstanceType.isInstance(entry.getValue()))) throw new Exception (String.format("Found invalid value class %s in map parameter %s, expected %s",entry.getValue().getClass(),this.getName(),this.valueInstanceType));				
			}
		}
	}
}
