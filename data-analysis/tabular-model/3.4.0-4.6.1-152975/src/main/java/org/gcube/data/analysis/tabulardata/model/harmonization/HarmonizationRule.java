package org.gcube.data.analysis.tabulardata.model.harmonization;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

public class HarmonizationRule {

	private static JAXBContext jaxbContext = null;
	private static Unmarshaller unmarshaller = null;
	
	private static Marshaller marshaller;
	
	private static synchronized void initContext() throws JAXBException{
		if(jaxbContext==null){
			jaxbContext = JAXBContext.newInstance(Expression.class);
			unmarshaller=jaxbContext.createUnmarshaller();
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		}
	}
	
	
	
	public static final String TO_CHANGE_VALUE_FIELD="to_change_value";
	public static final String TO_SET_VALUE_FIELD="to_set_value";
	public static final String REFERRED_CODELIST_COLUMN="referred_column";
	public static final String ENABLED="enabled";
	public static final String ID="id";
	public static final String TO_CHANGE_VALUE_DESCRIPTION="to_change_value_description";
	public static final String TO_SET_VALUE_DESCRIPTION="to_set_value_description";
	
	private static final List<String> mandatoryFields=Arrays.asList(new String[]{
			TO_CHANGE_VALUE_FIELD,
			TO_SET_VALUE_FIELD,
			REFERRED_CODELIST_COLUMN,
	});
	
	
	private TDTypeValue toChangeValue=null;
	private TDTypeValue toSetValue=null;

	private ColumnLocalId referredCodelistColumn=null;

	private Boolean enabled=true;
	
	private long id=0;
	
	private String toChangeValueDescription=null;
	
	private String toSetValueDescription=null;
	
	/**
	 * SET DATASET.TARGET_COLUMN=TO_CHANGE_VALUE WHERE DATASET.TARGET_COLUMN=TO_SET_VALUE  
	 *  
	 * @param toChangeValue
	 * @param toSetValue
	 */
	public HarmonizationRule(TDTypeValue toChangeValue, TDTypeValue toSetValue,ColumnLocalId reference) {
		super();
		this.toChangeValue = toChangeValue;
		this.toSetValue = toSetValue;
		this.referredCodelistColumn=reference;
	}

	public HarmonizationRule(Map<String,String> fields) throws JAXBException{
		for(String mandatory:mandatoryFields) 
			if(!fields.containsKey(mandatory) && fields.get(mandatory)==null) throw new IllegalArgumentException("Field "+mandatory+" is mandatory");
		
		initContext();
		
		for(Entry<String,String> entry:fields.entrySet()){
			if(entry.getKey().equals(ENABLED)) this.setEnabled(Boolean.parseBoolean(entry.getValue()));
			else if(entry.getKey().equals(ID)) this.setId(Long.parseLong(entry.getValue()));
			else if (entry.getKey().equals(REFERRED_CODELIST_COLUMN)) this.setReferredCodelistColumn(new ColumnLocalId(entry.getValue()));
			else if (entry.getKey().equals(TO_CHANGE_VALUE_DESCRIPTION)) this.setToChangeValueDescription(entry.getValue());
			else if (entry.getKey().equals(TO_CHANGE_VALUE_FIELD)) this.setToChangeValue((TDTypeValue) unmarshaller.unmarshal(new StringReader(entry.getValue())));
			else if (entry.getKey().equals(TO_SET_VALUE_DESCRIPTION)) this.setToSetValueDescription(entry.getValue());
			else if (entry.getKey().equals(TO_SET_VALUE_FIELD)) this.setToSetValue((TDTypeValue) unmarshaller.unmarshal(new StringReader(entry.getValue())));
		}
	}
	
	public HarmonizationRule(TDTypeValue toChangeValue, TDTypeValue toSetValue,
			ColumnLocalId reference, Boolean enabled,
			String toChangeValueDescription, String toSetValueDescription) {
		super();
		this.toChangeValue = toChangeValue;
		this.toSetValue = toSetValue;
		this.referredCodelistColumn = reference;
		this.enabled = enabled;
		this.toChangeValueDescription = toChangeValueDescription;
		this.toSetValueDescription = toSetValueDescription;
	}



	/**
	 * @return the toChangeValue
	 */
	public TDTypeValue getToChangeValue() {
		return toChangeValue;
	}

	/**
	 * @param toChangeValue the toChangeValue to set
	 */
	public void setToChangeValue(TDTypeValue toChangeValue) {
		this.toChangeValue = toChangeValue;
	}

	/**
	 * @return the toSetValue
	 */
	public TDTypeValue getToSetValue() {
		return toSetValue;
	}

	/**
	 * @param toSetValue the toSetValue to set
	 */
	public void setToSetValue(TDTypeValue toSetValue) {
		this.toSetValue = toSetValue;
	}

	/**
	 * @return the referredCodelistColumn
	 */
	public ColumnLocalId getReferredCodelistColumn() {
		return referredCodelistColumn;
	}

	/**
	 * @param referredCodelistColumn the referredCodelistColumn to set
	 */
	public void setReferredCodelistColumn(ColumnLocalId referredCodelistColumn) {
		this.referredCodelistColumn = referredCodelistColumn;
	}

	/**
	 * @return the enabled
	 */
	public Boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the toChangeValueDescription
	 */
	public String getToChangeValueDescription() {
		return toChangeValueDescription;
	}

	/**
	 * @param toChangeValueDescription the toChangeValueDescription to set
	 */
	public void setToChangeValueDescription(String toChangeValueDescription) {
		this.toChangeValueDescription = toChangeValueDescription;
	}

	/**
	 * @return the toSetValueDescription
	 */
	public String getToSetValueDescription() {
		return toSetValueDescription;
	}

	/**
	 * @param toSetValueDescription the toSetValueDescription to set
	 */
	public void setToSetValueDescription(String toSetValueDescription) {
		this.toSetValueDescription = toSetValueDescription;
	}
	
	
	public Map<String,String> asMap() throws JAXBException{
		HashMap<String,String> toReturn=new HashMap<>();
		toReturn.put(ENABLED, this.isEnabled().toString());
		toReturn.put(ID, getId()+"");
		toReturn.put(REFERRED_CODELIST_COLUMN, getReferredCodelistColumn().getValue());		
		toReturn.put(TO_CHANGE_VALUE_FIELD, serializeExpression(getToChangeValue()));
		toReturn.put(TO_CHANGE_VALUE_DESCRIPTION,getToChangeValueDescription());
		toReturn.put(TO_SET_VALUE_FIELD, serializeExpression(getToSetValue()));
		toReturn.put(TO_SET_VALUE_DESCRIPTION, getToSetValueDescription());
		return toReturn;
	}
	
	private static String serializeExpression(Expression toSerialize) throws JAXBException{
		initContext();
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(toSerialize, stringWriter);
		return stringWriter.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((referredCodelistColumn == null) ? 0
						: referredCodelistColumn.hashCode());
		result = prime * result
				+ ((toChangeValue == null) ? 0 : toChangeValue.hashCode());
		result = prime
				* result
				+ ((toChangeValueDescription == null) ? 0
						: toChangeValueDescription.hashCode());
		result = prime * result
				+ ((toSetValue == null) ? 0 : toSetValue.hashCode());
		result = prime
				* result
				+ ((toSetValueDescription == null) ? 0 : toSetValueDescription
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HarmonizationRule other = (HarmonizationRule) obj;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (id != other.id)
			return false;
		if (referredCodelistColumn == null) {
			if (other.referredCodelistColumn != null)
				return false;
		} else if (!referredCodelistColumn.equals(other.referredCodelistColumn))
			return false;
		if (toChangeValue == null) {
			if (other.toChangeValue != null)
				return false;
		} else if (!toChangeValue.equals(other.toChangeValue))
			return false;
		if (toChangeValueDescription == null) {
			if (other.toChangeValueDescription != null)
				return false;
		} else if (!toChangeValueDescription
				.equals(other.toChangeValueDescription))
			return false;
		if (toSetValue == null) {
			if (other.toSetValue != null)
				return false;
		} else if (!toSetValue.equals(other.toSetValue))
			return false;
		if (toSetValueDescription == null) {
			if (other.toSetValueDescription != null)
				return false;
		} else if (!toSetValueDescription.equals(other.toSetValueDescription))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HarmonizationRule [toChangeValue=");
		builder.append(toChangeValue);
		builder.append(", toSetValue=");
		builder.append(toSetValue);
		builder.append(", referredCodelistColumn=");
		builder.append(referredCodelistColumn);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", id=");
		builder.append(id);
		builder.append(", toChangeValueDescription=");
		builder.append(toChangeValueDescription);
		builder.append(", toSetValueDescription=");
		builder.append(toSetValueDescription);
		builder.append("]");
		return builder.toString();
	}
	
	
}
