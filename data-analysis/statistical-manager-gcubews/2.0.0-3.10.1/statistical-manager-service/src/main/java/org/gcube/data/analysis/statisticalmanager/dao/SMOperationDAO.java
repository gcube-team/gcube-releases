package org.gcube.data.analysis.statisticalmanager.dao;

import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMOperation;

public interface SMOperationDAO {
	
	void add(SMOperation operation);
	void delete(String operationId);
	void getById(String operationId);
	
}
