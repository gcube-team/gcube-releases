package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistenceProviderImpl implements PersistenceProvider {






	@Override
	public File getPersistenceFolderById(String persistenceId) throws DestinationAccessException {
		File toReturn=null;
		log.debug("looking for persistence ID : {}",persistenceId);

		if(persistenceId.equalsIgnoreCase(Destination.DEFAULT_PERSISTENCE_ID)){
			log.debug("Persistence ID is default");
			ApplicationContext context=ContextProvider.get();
			toReturn=new File(context.persistence().location());
		}else{
			for(ApplicationConfiguration config:ContextProvider.get().container().configuration().apps()){
				if(config.context().equals(persistenceId)){
					log.debug("Found persistence ID {}",persistenceId);
					toReturn= new File(config.persistence().location());
					break;					
				}	
			}
		}
		if(toReturn== null) throw new DestinationAccessException("Persistence ID "+persistenceId+" not found.");
			if(!toReturn.exists()) throw new DestinationAccessException("Persistence ID "+persistenceId+", location "+toReturn.getAbsolutePath()+" location doesn't exists.");
			if(!toReturn.canWrite()) throw new DestinationAccessException("Cannot write to Persistence ID "+persistenceId+", location "+toReturn.getAbsolutePath()+" .");
			if(!toReturn.isDirectory()) throw new DestinationAccessException("Persistence ID "+persistenceId+", location "+toReturn.getAbsolutePath()+" is a directory.");
			if(!toReturn.canWrite()) throw new DestinationAccessException("Cannot write to Persistence ID "+persistenceId+", location "+toReturn.getAbsolutePath()+" .");
			return toReturn;

	}


	@Override
	public Set<String> getAvaileblContextIds() {
		HashSet<String> toReturn=new HashSet<>();
		for(ApplicationConfiguration config:ContextProvider.get().container().configuration().apps()){
			toReturn.add(config.context());
		}
		return toReturn;
	}

}
