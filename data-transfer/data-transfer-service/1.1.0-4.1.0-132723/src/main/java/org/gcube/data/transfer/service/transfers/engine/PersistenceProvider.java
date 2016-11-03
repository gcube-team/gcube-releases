package org.gcube.data.transfer.service.transfers.engine;

import java.io.File;
import java.util.Set;

import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;

public interface PersistenceProvider {

	public File getPersistenceFolderById(String persistenceId) throws DestinationAccessException;	
	
	public Set<String> getAvaileblContextIds();
}
