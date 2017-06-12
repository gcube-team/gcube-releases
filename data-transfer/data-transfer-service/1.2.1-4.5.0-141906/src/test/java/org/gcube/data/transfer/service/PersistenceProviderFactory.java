package org.gcube.data.transfer.service;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.glassfish.hk2.api.Factory;

class PersistenceProviderFactory implements Factory<PersistenceProvider>{

	@Override
	public PersistenceProvider provide() {
		return new PersistenceProvider() {
			
			@Override
			public File getPersistenceFolderById(String persistenceId) throws DestinationAccessException {
				return new File(System.getProperty("java.io.tmpdir"));
			}
			
			@Override
			public Set<String> getAvaileblContextIds() {
				return Collections.emptySet();
			}
		};
	}
	
	@Override
	public void dispose(PersistenceProvider arg0) {
		// TODO Auto-generated method stub
		
	}
}
