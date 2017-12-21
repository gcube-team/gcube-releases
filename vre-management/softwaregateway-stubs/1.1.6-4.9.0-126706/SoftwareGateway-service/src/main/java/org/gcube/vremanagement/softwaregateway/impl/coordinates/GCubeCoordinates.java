package org.gcube.vremanagement.softwaregateway.impl.coordinates;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.is.ISManager;
/**
 * Defines a set of gcube coordinates
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class GCubeCoordinates extends Coordinates implements java.io.Serializable{

	protected final GCUBELog logger = new GCUBELog(GCubeCoordinates.class);
	private MavenCoordinates mavenCoordinates;
	
	public GCubeCoordinates(String sn, String sc, String sv, String pn, String pv) throws BadCoordinatesException{
		if((sn != null) && (sn.length()>0))
			setServiceName(sn);
		else
			throw new BadCoordinatesException();
		if((sc != null) && (sc.length() > 0))
			setServiceClass(sc);
		else
			throw new BadCoordinatesException();
		
		setServiceVersion(sv);
		setPackageName(pn);
		setPackageVersion(pv);
		logger.debug("GCubeCoordinates: sc: "+getServiceClass()+" sn: "+getServiceName()+" sv: "+getServiceVersion()+" pn: "+ getPackageName()+" pv: "+getPackageVersion());
	}
	
	@Override
	public Coordinates convert() throws BadCoordinatesException {	
		return mavenCoordinates= new MavenCoordinates(getServiceClass(), getPackageName(), getPackageVersion());
	}

}
