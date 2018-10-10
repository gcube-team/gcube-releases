package org.gcube.contentmanagement.blobstorage.resource;
/**
 * define the kind of access to storage manager
 * private: The file uploaded are visibility limited at the owner 
 * shared: the visibility is limited for all user that have the same serviceClass and serviceName
 * public: the visibility is limited to all the infrastructured area
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */

public enum AccessType {
	 PUBLIC, SHARED, PRIVATE
	}