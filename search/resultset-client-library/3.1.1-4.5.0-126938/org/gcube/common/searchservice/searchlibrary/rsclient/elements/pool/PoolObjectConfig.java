package org.gcube.common.searchservice.searchlibrary.rsclient.elements.pool;

/**
 * pool configuration for a pool object
 * 
 * @author UoA
 */
public class PoolObjectConfig {
	/**
	 * The object type
	 */
	public RSPoolObject.PoolObjectType ObjectType=null;
	/**
	 * The resource type
	 */
	public RSPoolObject.PoolObjectResourceType ResourceType=null;
	/**
	 * The end point of the result set service to use in case of static usage.
	 * If the code is used within the hosting container this can be left null.
	 */
	public String ServiceEndPoint=null;
	/**
	 * The min size of the object pool
	 */
	public int MinSize=0;
	/**
	 * The max size of the object pool
	 */
	public int MaxSize=0;
	/**
	 * Sets whether or not the produced result sets should be also flow control
	 */
	public boolean FlowControl=false;
	
	/**
	 * Property used in RSTextWriter. In rest ommited
	 */
	public boolean WellFormed=false;

	/**
	 * Whether or not the pool object should support flow control 
	 * 
	 * @return whether or not it is supported
	 */
	public boolean isFlowControl() {
		return FlowControl;
	}

	/**
	 * Whether or not the pool object should support flow control
	 * 
	 * @param flowControl whether or not it is supported 
	 */
	public void setFlowControl(boolean flowControl) {
		FlowControl = flowControl;
	}

	/**
	 * The max size of the pool
	 * 
	 * @return the max size
	 */
	public int getMaxSize() {
		return MaxSize;
	}

	/**
	 * The max size of the pool
	 * 
	 * @param maxSize the max size
	 */
	public void setMaxSize(int maxSize) {
		MaxSize = maxSize;
	}

	/**
	 * The min size of the pool
	 * 
	 * @return the min size
	 */
	public int getMinSize() {
		return MinSize;
	}

	/**
	 * The mnin size of the pool 
	 * 
	 * @param minSize the min size
	 */
	public void setMinSize(int minSize) {
		MinSize = minSize;
	}

	/**
	 * The object type to be added in the pool
	 * 
	 * @return the object type
	 */
	public RSPoolObject.PoolObjectType getObjectType() {
		return ObjectType;
	}

	/**
	 * The object type to be added in the pool
	 *  
	 * @param objectType the opbject type
	 */
	public void setObjectType(RSPoolObject.PoolObjectType objectType) {
		ObjectType = objectType;
	}

	/**
	 * The object type to be added in the pool
	 *  
	 * @param objectType the opbject type
	 */
	public void setObjectType(String objectType) {
		ObjectType = RSPoolObject.PoolObjectType.valueOf(objectType);
	}

	/**
	 * The object type to be added in the pool
	 *  
	 * @param objectType the opbject type
	 */
	public void setParsedObjectType(String objectType) {
		ObjectType = RSPoolObject.PoolObjectType.valueOf(objectType);
	}

	/**
	 * The object type to be added in the pool
	 * 
	 * @return the object type
	 */
	public RSPoolObject.PoolObjectResourceType getResourceType() {
		return ResourceType;
	}

	/**
	 * The resource type of the object in the pool
	 * 
	 * @param resourceType the resource type
	 */
	public void setResourceType(RSPoolObject.PoolObjectResourceType resourceType) {
		ResourceType = resourceType;
	}

	/**
	 * The resource type of the object in the pool
	 * 
	 * @param resourceType the resource type
	 */
	public void setResourceType(String resourceType) {
		ResourceType = RSPoolObject.PoolObjectResourceType.valueOf(resourceType);
	}

	/**
	 * The resource type of the object in the pool
	 * 
	 * @param resourceType the resource type
	 */
	public void setParsedResourceType(String resourceType) {
		ResourceType = RSPoolObject.PoolObjectResourceType.valueOf(resourceType);
	}

	/**
	 * The service end point otf the RS to use
	 * 
	 * @return the service end point
	 */
	public String getServiceEndPoint() {
		return ServiceEndPoint;
	}

	/**
	 * The service end point of the RS to use
	 * 
	 * @param serviceEndPoint the service end point
	 */
	public void setServiceEndPoint(String serviceEndPoint) {
		ServiceEndPoint = serviceEndPoint;
		if(serviceEndPoint.trim().length()==0) ServiceEndPoint=null;
	}

	/**
	 * Whether or not the rs is wellformed in cases of a text rs
	 * 
	 * @return true or false
	 */
	public boolean isWellFormed() {
		return WellFormed;
	}

	/**
	 * Whether or not the rs is wellformed in cases of a text rs
	 * 
	 * @param wellFormed true or false
	 */
	public void setWellFormed(boolean wellFormed) {
		WellFormed = wellFormed;
	}
}
