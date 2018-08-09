package org.gcube.data.transfer.service;

import java.io.File;
import java.io.IOException;

import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.gcube.data.transfer.service.transfers.engine.impl.PersistenceProviderImpl;
import org.glassfish.hk2.api.Factory;

class PersistenceProviderFactory implements Factory<PersistenceProvider>{

	@Override
	public PersistenceProvider provide() {
		return new PersistenceProviderImpl() {
			
//			
//			
			@Override
			public File getPersistedFile(String persistenceId, String subPath) throws DestinationAccessException {
				File persistenceFolder=getPersistenceFolderById(persistenceId);
				File toReturn=new File(persistenceFolder.getAbsolutePath()+"/"+subPath);
				try {
					toReturn.getParentFile().mkdirs();
					toReturn.createNewFile();
				} catch (IOException e) {
					throw new DestinationAccessException(e);
				}
				return toReturn;
			}
//						
			@Override
			public File getPersistenceFolderById(String persistenceId) throws DestinationAccessException {
				return new File(System.getProperty("java.io.tmpdir"));
			}
//			
//			@Override
//			public Set<String> getAvaileblContextIds() {
//				return Collections.singleton("data-transfer-service");
//			}
//
//			@Override
//			public File prepareDestination(Destination dest) throws DestinationAccessException {
//				try {
//					return File.createTempFile("dest", "");
//				} catch (IOException e) {
//					return null;
//				}
//			}
		};
	}
	
	@Override
	public void dispose(PersistenceProvider arg0) {
		// TODO Auto-generated method stub
		
	}
}
