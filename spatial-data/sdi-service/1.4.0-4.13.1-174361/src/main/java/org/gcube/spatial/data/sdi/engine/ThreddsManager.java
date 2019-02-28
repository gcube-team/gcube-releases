package org.gcube.spatial.data.sdi.engine;

import java.io.File;

import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.engine.impl.faults.ThreddsOperationFault;
import org.gcube.spatial.data.sdi.model.service.ThreddsDescriptor;
import org.gcube.spatial.data.sdi.model.services.ThreddsDefinition;

public interface ThreddsManager extends GeoServiceManager<ThreddsDescriptor, ThreddsDefinition>{

	public ThreddsCatalog publishCatalog(File catalogFile,String catalogReference) throws ConfigurationNotFoundException, ThreddsOperationFault;
	
	public ThreddsCatalog createCatalogFromTemplate(String authorityUrl,String catalogPath,
			String datasetScanId,String datasetScanName, String subFolder, String catalogReference)throws Exception;
}
