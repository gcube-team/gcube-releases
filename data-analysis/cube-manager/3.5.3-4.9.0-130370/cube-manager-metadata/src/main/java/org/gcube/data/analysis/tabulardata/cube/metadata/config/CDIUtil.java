package org.gcube.data.analysis.tabulardata.cube.metadata.config;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import org.gcube.common.database.DatabaseEndpointIdentifier;
import org.gcube.common.database.DatabaseProvider;
import org.gcube.data.analysis.tabulardata.cube.metadata.ISEntityManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIUtil {

	private Logger logger = LoggerFactory.getLogger(CDIUtil.class);
	
	@Produces
	public ISEntityManagerProvider createEntityManager(@Named("Metadata-Admin") DatabaseEndpointIdentifier metaDBEndpointId,
			DatabaseProvider dbProvider, InjectionPoint injectionPoint){
		logger.info("ISEntityManagerProvider produced");
		return new ISEntityManagerProvider(metaDBEndpointId, dbProvider);
	}
	
	public void disposeEntityManagerProvider(@Disposes ISEntityManagerProvider emp){
		emp.close();
		logger.info("ISEntityManagerProvider disposed");
	}
	
	
}
