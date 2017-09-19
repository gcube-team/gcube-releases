package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
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
				if(config.context().equals(persistenceId)||config.context().equals("/"+persistenceId)){
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
			String toAddID=config.context();
			if(toAddID.startsWith("/")) toAddID=toAddID.substring(1);
			toReturn.add(toAddID);
		}
		return toReturn;
	}

	@Override
	public File prepareDestination(Destination dest) throws DestinationAccessException{
		File persistenceFolder=getPersistenceFolderById(dest.getPersistenceId());
		if(!persistenceFolder.canWrite()) throw new DestinationAccessException("Cannot write to selecte persistenceFolder [ID :"+dest.getPersistenceId()+"]");
		log.debug("Got Persistence folder PATH {}, ID {}",persistenceFolder.getAbsolutePath(),dest.getPersistenceId());
		String subFolderName=dest.getSubFolder();
		File subFolder=persistenceFolder;
		if(subFolderName!=null){
			log.debug("Looking for subFolder : "+subFolder);
			if(subFolderName.startsWith(File.pathSeparator)) throw new DestinationAccessException("SubFolder cannot be absolute.");
			//			 String[] pathItems=subFolderName.split(File.pathSeparator);
			//			 for(String subPath:pathItems){				
			////				 Set<String> existingFiles=new HashSet<String>(Arrays.asList(subFolder.list()));
			//				 subFolder=new File(subFolder,subPath);
			//				 if(subFolder.exists()){
			//					 if(!subFolder.canRead()) throw new DestinationAccessException("Cannot write to "+subFolder.getAbsolutePath());
			//				 }else if(dest.getCreateSubfolders()) subFolder.mkdir();
			//				 else throw new DestinationAccessException("Destination subfolder {} not found. Set createSubFolder=true to create intermediary directories.");
			//			 }

			subFolder=new File(persistenceFolder,subFolderName);
			if(subFolder.exists()){
				if(!subFolder.canRead()) throw new DestinationAccessException("Cannot write to "+subFolder.getAbsolutePath());
				manageClash(dest.getOnExistingSubFolder(),subFolder);
			}else if(dest.getCreateSubfolders()) subFolder.mkdirs();
			else throw new DestinationAccessException("SubFolder not found. Use createSubFolders=true to create it.");
		}

		File destination=new File(subFolder,dest.getDestinationFileName());
		if(destination.exists()) return manageClash(dest.getOnExistingFileName(),destination);
		else {
			try{
				destination.createNewFile();
				return destination;
			}catch(IOException e){
				throw new DestinationAccessException("Unable to create file ",e);
			}
		}
	}

	public static final File manageClash(DestinationClashPolicy policy, File clashing ) throws DestinationAccessException{
		log.debug("Managing clash for {}, policy is {} ",clashing.getAbsolutePath(),policy);
		boolean dir=clashing.isDirectory();
		try{
			switch(policy){
			case ADD_SUFFIX : {
				String clashingName=clashing.getName();
				String clashingBaseName=(!dir&&clashingName.contains("."))?clashingName.substring(0, clashingName.lastIndexOf(".")):clashingName;		
				String destinationExtension=(!dir&&clashingName.contains("."))?clashingName.substring(clashingName.lastIndexOf(".")):"";
				
				
				
				int counter=1;
				while(clashing.exists()){					
					clashing=new File(clashing.getParentFile(),clashingBaseName+"("+counter+")"+destinationExtension);
					counter++;
				}
				if(dir)clashing.mkdirs();
				else clashing.createNewFile();
				break;
			}
			case FAIL: throw new DestinationAccessException("Found existing "+clashing.getAbsolutePath()+"policy is "+policy); 
			case REWRITE : {
				Files.deleteIfExists(Paths.get(clashing.getAbsolutePath()));			
				if(dir)clashing.mkdirs();
				else clashing.createNewFile();
				break;
			}	
			}
		}catch(IOException e){
			throw new DestinationAccessException("Unable to rewrite existing destination",e);
		}
		return clashing;
	}
}
