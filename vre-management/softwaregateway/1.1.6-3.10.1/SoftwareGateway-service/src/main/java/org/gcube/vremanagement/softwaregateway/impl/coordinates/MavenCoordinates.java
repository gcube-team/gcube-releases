package org.gcube.vremanagement.softwaregateway.impl.coordinates;

import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.is.ISManager;
import org.gcube.vremanagement.softwaregateway.impl.is.ISProxy;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;
/**
 * Defines a set of Maven Coordinates
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class MavenCoordinates extends Coordinates {
	
	private GCubeCoordinates gcubeCoordinates;
	protected final GCUBELog logger = new GCUBELog(MavenCoordinates.class);
	public MavenCoordinates(String gId, String aId, String v) throws BadCoordinatesException {
		if((gId !=null) && (gId.length()>0))
			setGroupId(gId);
		else
			throw new BadCoordinatesException();
		if((aId !=null) && (aId.length()>0))
			setArtifactId(aId);
		else
			throw new BadCoordinatesException();
		setVersion(v);
		
	}
	@Override
	public Coordinates convert() throws BadCoordinatesException {
		return gcubeCoordinates= new GCubeCoordinates(artifactId, groupId, "1.0.0", artifactId, version);
	}
	
	
	
/**
 * Conversion from gcubeCoordinates to maven coordinates	
 * @return
 * @throws ServiceNotAvaiableFault
 */
	public GCubeCoordinates getGcubeCoordinates() throws ServiceNotAvaiableFault {
		logger.trace("getGcubeCoordinates() method");
		logger.info("try to convert from maven to gcube: gid: "+getGroupId()+" aid "+getArtifactId()+" v" +getVersion());
		if(gcubeCoordinates == null){
			GCUBEScope scope=ServiceContext.getContext().getScope();
			ISProxy is=new ISProxy(scope, false);
			try{
				gcubeCoordinates=is.getGcubeCoordinates(this);
			}catch(Exception e){
				try {
			// automatically convert method		
					convert();
				} catch (BadCoordinatesException e1) {
					throw new ServiceNotAvaiableFault("impossible convert coordinate frm maven to gcube");
				}
			}
			
		}
		return gcubeCoordinates;
	}
	public void setGcubeCoordinates(GCubeCoordinates gcubeCoordinates) {
		this.gcubeCoordinates = gcubeCoordinates;
	}
}
