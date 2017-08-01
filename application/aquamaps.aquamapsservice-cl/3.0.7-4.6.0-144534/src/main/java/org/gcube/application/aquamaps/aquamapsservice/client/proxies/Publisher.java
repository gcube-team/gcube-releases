package org.gcube.application.aquamaps.aquamapsservice.client.proxies;

import java.rmi.RemoteException;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;

public interface Publisher {

	public List<AquaMap> getMapsBySpecies(String[] speciesId,boolean includeGis, boolean includeCustom, List<Resource> resources)throws RemoteException,Exception;
	public String getJsonSubmittedByFilters(List<Field> filters, PagedRequestSettings settings)throws RemoteException,Exception;
	public List<File> getFileSetById(String fileSetId)throws RemoteException,Exception;
	public LayerInfo getLayerById(String layerId)throws RemoteException,Exception;
	public List<LayerInfo> getLayersByCoverage(Resource source,String parameters)throws RemoteException,Exception;
	public List<File> getFileSetsByCoverage(Resource source,String parameters)throws RemoteException,Exception;
	public java.io.File getBulkUpdates(boolean includeGis, boolean includeCustom, List<Resource> resources,long fromTime)throws RemoteException,Exception;
}
