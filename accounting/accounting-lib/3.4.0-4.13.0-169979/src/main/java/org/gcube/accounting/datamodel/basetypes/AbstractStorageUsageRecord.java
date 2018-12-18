/**
 * 
 */
package org.gcube.accounting.datamodel.basetypes;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.validations.annotations.FixDataVolumeSign;
import org.gcube.accounting.datamodel.validations.annotations.ValidDataType;
import org.gcube.accounting.datamodel.validations.annotations.ValidOperationType;
import org.gcube.accounting.datamodel.validations.annotations.ValidURI;
import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.RequiredField;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmptyIfNotNull;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class AbstractStorageUsageRecord extends BasicUsageRecord {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1381025822586583326L;

	public enum OperationType {
		CREATE, READ, UPDATE, DELETE
	}
	
	public enum DataType {
		STORAGE, TREE, GEO, DATABASE, LOCAL, OTHER
	}
	
	/**
	 * KEY for : The Owner of the stored Resource
	 */
	@RequiredField @NotEmpty
	public static final String RESOURCE_OWNER = "resourceOwner";
	/**
	 * KEY for : The Scope where the Resource was stored
	 */
	@RequiredField @NotEmpty
	public static final String RESOURCE_SCOPE = "resourceScope";
	
	/**
	 * KEY for : The URI of the Stored Resource
	 */
	@RequiredField @ValidURI
	public static final String RESOURCE_URI = "resourceURI";
	
	@RequiredField @ValidURI
	public static final String PROVIDER_URI = "providerURI";
	
	/**
	 * KEY for : The operation performed over the stored resource.
	 * The value is a controlled dictionary by StorageUsageRecord.OperationType
	 */
	@RequiredField @ValidOperationType @FixDataVolumeSign
	public static final String OPERATION_TYPE = "operationType";
	/**
	 *  KEY for : type of data accessed. 
	 *  The value is a controlled dictionary by StorageUsageRecord.DataType
	 */
	@RequiredField @ValidDataType
	public static final String DATA_TYPE = "dataType";
	
	/**
	 * KEY for : Quantity of data in terms of KB
	 */
	@RequiredField @ValidLong @FixDataVolumeSign
	public static final String DATA_VOLUME = "dataVolume";
	
	/**
	 * KEY for : Qualifies the data in terms of data (e.g. MIME type for the 
	 * Storage, domain for a database)
	 */
	@NotEmptyIfNotNull
	public static final String QUALIFIER = "qualifier";
	
	/**
	 * KEY for : callerQualifier
	 */
	/*
	@NotEmptyIfNotNull
	public static final String CALLERQUALIFIER = "callerQualifier";
	*/
	public AbstractStorageUsageRecord() {
		super();
	}
	
	public AbstractStorageUsageRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		super(properties);
	}

	private static final String ABSTRACT_TO_REPLACE = "Abstract";
	
	@Override
	public String getRecordType() {
		return AbstractStorageUsageRecord.class.getSimpleName().replace(ABSTRACT_TO_REPLACE, "");
	}
	
	/**
	 * Return the identity id of the storage resource owner
	 * @return the identity id of the accounting owner
	 */
	@JsonIgnore
	public String getResourceOwner() {
		return (String) this.resourceProperties.get(RESOURCE_OWNER);
	}
	
	/**
	 * Set the identity id of the storage resource owner
	 * @param owner the identity id of the storage resource owner
	 * @throws InvalidValueException
	 */
	public void setResourceOwner(String owner) throws InvalidValueException {
		setResourceProperty(RESOURCE_OWNER, owner);
	}
	/**
	 * Return the scope of the storage resource
	 * @return The scope id of the storage resource
	 */
	@JsonIgnore
	public String getResourceScope() {
		return (String) this.resourceProperties.get(RESOURCE_SCOPE);
	}
	
	/**
	 * Set the scope of the storage resource
	 * @param scope the scope of the storage resource
	 * @throws InvalidValueException
	 */
	public void setResourceScope(String scope) throws InvalidValueException {
		setResourceProperty(RESOURCE_SCOPE, scope);
	}
	
	@JsonIgnore
	public URI getProviderURI() {
		return (URI) this.resourceProperties.get(PROVIDER_URI);
	}

	public void setProviderURI(URI providerURI) throws InvalidValueException {
		setResourceProperty(PROVIDER_URI, providerURI);
	}
	
	@JsonIgnore
	public URI getResourceURI() {
		return (URI) this.resourceProperties.get(RESOURCE_URI);
	}

	public void setResourceURI(URI resourceURI) throws InvalidValueException {
		setResourceProperty(RESOURCE_URI, resourceURI);
	}
	
	@JsonIgnore
	public OperationType getOperationType() {
		return (OperationType) this.resourceProperties.get(OPERATION_TYPE);
	}
	
	public void setOperationType(OperationType operationType) throws InvalidValueException {
		setResourceProperty(OPERATION_TYPE, operationType);
	}
	
	@JsonIgnore
	public DataType getDataType() {
		return (DataType) this.resourceProperties.get(DATA_TYPE);
	}

	public void setDataType(DataType dataType) throws InvalidValueException {
		setResourceProperty(DATA_TYPE, dataType);
	}
	
	@JsonIgnore
	public long getDataVolume() {
		return (Long) this.resourceProperties.get(DATA_VOLUME);
	}

	public void setDataVolume(long dataVolume) throws InvalidValueException {
		setResourceProperty(DATA_VOLUME, dataVolume);
	}
	
	@JsonIgnore
	public String getQualifier() {
		return (String) this.resourceProperties.get(QUALIFIER);
	}

	public void setQualifier(String qualifier) throws InvalidValueException {
		setResourceProperty(QUALIFIER, qualifier);
	}
	
}
