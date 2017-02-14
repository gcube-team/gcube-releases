package org.gcube.data.spd.wordssplugin.capabilities;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wordssplugin.WordssPlugin;

import aphia.v1_0.AphiaRecord;

public class NamesMappingImpl implements MappingCapability{

	private static GCUBELog logger = new GCUBELog(NamesMappingImpl.class);

	@Override
	public void getRelatedScientificNames(ObjectWriter<String> writer,
			String commonName) throws ExternalRepositoryException{
		try {
			logger.debug("retrieving mapping for "+commonName);
			AphiaRecord[] records;
			final int offsetlimit=50;
			int offset =1;
			Set<String> snSet = new HashSet<String>();
			do{
				records = WordssPlugin.binding.getAphiaRecordsByVernacular(commonName, true, offset);
				if (records!=null){
					for (AphiaRecord record : records){
						if (!writer.isAlive()) return;
						if (!snSet.contains(record.getScientificname())){
							logger.trace("writing (COMMONNAMESMAPPING) "+record.getScientificname() );
							writer.write(record.getScientificname());
							snSet.add(record.getScientificname());
						}
					}
				}
				offset+=offsetlimit;
			} while (records!=null && records.length==offsetlimit);
			
		} catch (RemoteException e) {
			throw new ExternalRepositoryException(e);
		} 
	}

}
