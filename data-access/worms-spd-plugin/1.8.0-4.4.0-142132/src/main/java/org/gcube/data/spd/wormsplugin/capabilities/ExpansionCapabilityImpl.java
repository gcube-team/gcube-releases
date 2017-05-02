package org.gcube.data.spd.wormsplugin.capabilities;

import java.rmi.RemoteException;

import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wormsplugin.WormsPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aphia.v1_0.worms.AphiaRecord;

public class ExpansionCapabilityImpl implements ExpansionCapability {

	Logger logger = LoggerFactory.getLogger(ExpansionCapabilityImpl.class);
	
	@Override
	public void getSynonyms(ObjectWriter<String> writer, String scientifcName) throws ExternalRepositoryException {

		logger.debug("searching synonyms for "+scientifcName);
		int offset =1;
		int elements =20;
		AphiaRecord[] records = null;
		do{
			try{			
				records = WormsPlugin.binding.getAphiaRecords(scientifcName, true, false, false, offset);
				if (records!=null){
					for (AphiaRecord record : records){
						try{	
							AphiaRecord[] synonyms = WormsPlugin.binding.getAphiaSynonymsByID(record.getAphiaID());
							if (synonyms!=null){
								for (AphiaRecord synonym: synonyms){
									if (!writer.isAlive()) return;
									writer.write(synonym.getScientificname());
									logger.debug("found synonym "+synonym.getScientificname());
								}
							}
						}catch (Exception e) {
							writer.write(new StreamNonBlockingException("WoRMS",scientifcName));
							logger.error("error retrieving synonyms for aphia id "+record.getAphiaID(), e);
						}
					}
				}
			} catch (RemoteException e) {
				throw new ExternalRepositoryException(e);
			}
			offset = elements+offset;
		}while(records!=null && records.length == offset );
	}

}
