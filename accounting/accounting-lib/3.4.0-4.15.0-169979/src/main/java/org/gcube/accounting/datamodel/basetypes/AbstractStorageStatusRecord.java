/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.validations.annotations.FixDataVolumeSign;
import org.gcube.accounting.datamodel.validations.annotations.ValidDataTypeVolume;
import org.gcube.accounting.datamodel.validations.annotations.ValidURI;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class AbstractStorageStatusRecord extends BasicUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5754343539116896036L;
	
	public enum DataType {
		STORAGE, TREE, GEO, DATABASE, LOCAL, OTHER
	}

	/**
	 * KEY for : Quantity of data in terms of KB
	 */
	@RequiredField @ValidLong @FixDataVolumeSign
	public static final String DATA_VOLUME = "dataVolume";
	
	/**
	 *  KEY for : type of data accessed. 
	 *  The value is a controlled dictionary by StorageStatusRecord.DataType
	 */
	@RequiredField @ValidDataTypeVolume
	public static final String DATA_TYPE = "dataType";
		
	/**
	 * KEY for : data Count number of objects
	 */
	@RequiredField @NotEmpty
	public static final String DATA_COUNT = "dataCount";
	
	/**
	 * KEY for : data service class identifier
	 */
	@RequiredField @NotEmpty
	public static final String DATA_SERVICECLASS = "dataServiceClass";
	
	/**
	 * KEY for : data service name identifier
	 */
	@RequiredField @NotEmpty
	public static final String DATA_SERVICENAME = "dataServiceName";

	/**
	 * KEY for : data service name id
	 */
	@RequiredField @NotEmpty
	public static final String DATA_SERVICEID = "dataServiceId";

	/**
	 * KEY for : providerId the identifier of the provider which is the target of a read/write operation
	 */
	@RequiredField @ValidURI
	public static final String PROVIDER_ID = "providerId";
	
	
	public AbstractStorageStatusRecord() {
		super();
	}
	
	public AbstractStorageStatusRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}

	private static final String ABSTRACT_TO_REPLACE = "Abstract";

	@Override
	public String getRecordType() {
		return AbstractStorageStatusRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	@JsonIgnore
	public long getDataVolume() {
		return (Long) this.resourceProperties.get(DATA_VOLUME);
	}
	
	public void setDataVolume(long dataVolume) throws InvalidValueException {
		setResourceProperty(DATA_VOLUME, dataVolume);
	}
	
	@JsonIgnore
	public DataType getDataType() {
		return (DataType) this.resourceProperties.get(DATA_TYPE);
	}

	public void setDataType(DataType dataType) throws InvalidValueException {
		setResourceProperty(DATA_TYPE, dataType);
	}
	
	@JsonIgnore
	public long getDataCount() {
		return (Long) this.resourceProperties.get(DATA_COUNT);
	}

	public void setDataCount(long dataCount) throws InvalidValueException {
		setResourceProperty(DATA_COUNT, dataCount);
	}
	
	@JsonIgnore
	public String getDataServiceClass() {
		return (String) this.resourceProperties.get(DATA_SERVICECLASS);
	}
	
	public void setDataServiceClass(String dataServiceClass) throws InvalidValueException {
		setResourceProperty(DATA_SERVICECLASS, dataServiceClass);
	}
	
	@JsonIgnore
	public String getDataServiceName() {
		return (String) this.resourceProperties.get(DATA_SERVICENAME);
	}
	
	public void setDataServiceName(String dataServiceName) throws InvalidValueException {
		setResourceProperty(DATA_SERVICENAME, dataServiceName);
	}
	
	@JsonIgnore
	public String getDataServiceId() {
		return (String) this.resourceProperties.get(DATA_SERVICEID);
	}
	
	public void setDataServiceId(String dataServiceId) throws InvalidValueException {
		setResourceProperty(DATA_SERVICEID, dataServiceId);
	}
	
	@JsonIgnore
	public URI getProviderId() {
		return (URI) this.resourceProperties.get(PROVIDER_ID);
	}
	
	public void setProviderId(URI provideId) throws InvalidValueException {
		setResourceProperty(PROVIDER_ID, provideId);
	}
	
}
