package org.gcube.vremanagement.softwaregateway.impl.porttypes;

import java.util.List;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.packages.GCubePackage;
import org.gcube.vremanagement.softwaregateway.stubs.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.GetPackageResponse;
import org.gcube.vremanagement.softwaregateway.stubs.GetPluginResponse;
import org.gcube.vremanagement.softwaregateway.stubs.LocationItem;
import org.gcube.vremanagement.softwaregateway.stubs.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.SACoordinates;
import org.gcube.vremanagement.softwaregateway.stubs.ServiceCoordinates;



public class Access  extends GCUBEPortType {
	
	protected final GCUBELog logger = new GCUBELog(Access.class);
	
	
	/**
	 * Return the url location corresponds to the coordinates
	 * @param pack coordinates
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public String getLocation(PackageCoordinates pack) throws ServiceNotAvaiableFault{
		logger.debug("getLocation() invoked method");
		String url=null;
// build object gCube Coordinates
		GCubeCoordinates gcubeC=null;
		try{
			gcubeC= new GCubeCoordinates(pack.getServiceName() , pack.getServiceClass(), pack.getServiceVersion(), pack.getPackageName(), pack.getPackageVersion());
		}catch(BadCoordinatesException e){
			throw new BadCoordinatesException("gcube coordinates");
		}finally{
			checkPackageCoordinates(gcubeC);
		}
		
		GCubePackage gCubeP= new GCubePackage(gcubeC);
		url=gCubeP.getLocation();
		return url;
	}

	/**
	 * @param gcubeC
	 * @throws ServiceNotAvaiableFault
	 */
	private void checkPackageCoordinates(GCubeCoordinates gcubeC)
			throws ServiceNotAvaiableFault {
		if(gcubeC.getPackageName()== null || gcubeC.getPackageVersion().isEmpty() || gcubeC.getPackageVersion() == null || gcubeC.getPackageVersion().isEmpty()){
			throw new ServiceNotAvaiableFault("incorrect packageName or packageVersion for method getLocation ");
		}
	}

	/**
	 * Return the url location of the SoftwareArchive corresponds to the coordinates
	 * @param pack coordinates
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public String getSALocation(SACoordinates pack) throws ServiceNotAvaiableFault{
		logger.debug("getSALocation() invoked");
		GCubeCoordinates gcubeC=null;
		try{
			gcubeC= new GCubeCoordinates(pack.getServiceName() , pack.getServiceClass(), pack.getServiceVersion(), pack.getPackageName(), pack.getPackageVersion());
		}catch(BadCoordinatesException e){
			throw new BadCoordinatesException("gcube coordinates");
		}finally{
			checkPackageCoordinates(gcubeC);
		}
		GCubePackage gCubeP= new GCubePackage(gcubeC);
		return gCubeP.getSALocation();
		
	}

	/**
	 * Return a XML Document that contains the dependencies tree 
	 * @param pack coordinates
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public String getDependencies(DependenciesCoordinates pack) throws ServiceNotAvaiableFault{
		logger.debug("getDependencies() invoked");
		String result=null;
		try{
			GCubeCoordinates coordinates=new GCubeCoordinates(pack.getServiceName() , pack.getServiceClass(), pack.getServiceVersion(), pack.getPackageName(), pack.getPackageVersion());
			GCubePackage gCubeP=new GCubePackage(coordinates);
			result=gCubeP.getDependencies();
		}catch(Exception e){
			logger.error("Failed to retrive software deps for " + pack.getServiceName(), e);
			throw new ServiceNotAvaiableFault(e.getMessage());
		}
		return result;	
	}

	/**
	 * Return a List that contains the GCube coordinates for every package belongs to the service
	 * @param service
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public GetPackageResponse getPackages(ServiceCoordinates service) throws ServiceNotAvaiableFault{
		logger.debug("getpackages() invoked");
		GCubeCoordinates coordinates=new GCubeCoordinates(service.getServiceName(), service.getServiceClass(), service.getServiceVersion(), null, null);
		GCubePackage gCubeP= new GCubePackage(coordinates);
		List<GCubePackage> list=gCubeP.getPackages();
		
		if(list != null){
			GetPackageResponse result= new GetPackageResponse();
			result= fillResult(list, result);
			return result;
		}else return null;
	}

	/**
	 * Return a list of cordinates for every plugin 
	 * @param pack
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public GetPluginResponse getPlugins(PluginCoordinates pack) throws ServiceNotAvaiableFault{
		logger.debug("getPlugins() invoked");
		GCubeCoordinates coordinates=new GCubeCoordinates(pack.getServiceName() , pack.getServiceClass(), pack.getServiceVersion(), pack.getPackageName(), pack.getPackageVersion());
		GCubePackage gCubeP= new GCubePackage(coordinates);
		List<GCubePackage> list=gCubeP.getPlugins();
		GetPluginResponse result= new GetPluginResponse();
		if(list != null)
			result= fillResult(list, result);
		return result;
	}
	
	/**
	 * Return the current serviceContext
	 */
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	/**
	 * fill the result List
	 * @param list
	 * @param result
	 * @return
	 */
	private GetPackageResponse fillResult(List<GCubePackage> list, GetPackageResponse result) {
		if(result == null)
			result = new GetPackageResponse();
		if(list != null && list.size()> 0){
			LocationItem [] items=new LocationItem[list.size()];
			for(int i=0; i<list.size(); i++){
				logger.debug("add package: pn: "+list.get(i).getCoordinates().getPackageName()+" pv: "+list.get(i).getCoordinates().getPackageVersion()+" to service : "+list.get(i).getCoordinates().getServiceClass()+" "+list.get(i).getCoordinates().getServiceName()+" "+list.get(i).getCoordinates().getServiceVersion() );
				LocationItem item = new LocationItem(list.get(i).getCoordinates().getPackageName(), list.get(i).getCoordinates().getPackageVersion(), list.get(i).getCoordinates().getServiceClass(), list.get(i).getCoordinates().getServiceName(), list.get(i).getCoordinates().getServiceVersion());
				items[i]=item;
			}
			result.setItems(items);
		}
		return result;
	}
	
	/**
	 * fill the result list
	 * @param list
	 * @param result
	 * @return
	 */
	private GetPluginResponse fillResult(List<GCubePackage> list, GetPluginResponse result) {
		if(result == null)
			result = new GetPluginResponse();
		if(list != null && list.size()> 0){
			LocationItem [] items=new LocationItem[list.size()];
			for(int i=0; i<list.size(); i++){
				logger.debug("add package: pn: "+list.get(i).getCoordinates().getPackageName()+" pv: "+list.get(i).getCoordinates().getPackageVersion()+" to service : "+list.get(i).getCoordinates().getServiceClass()+" "+list.get(i).getCoordinates().getServiceName()+" "+list.get(i).getCoordinates().getServiceVersion() );
				LocationItem item = new LocationItem(list.get(i).getCoordinates().getPackageName(), list.get(i).getCoordinates().getPackageVersion(), list.get(i).getCoordinates().getServiceClass(), list.get(i).getCoordinates().getServiceName(), list.get(i).getCoordinates().getServiceVersion());
				items[i]=item;
			}
			result.setItems(items);
		}
		return result;
	}

}
