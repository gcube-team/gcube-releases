package org.gcube.informationsystem.registry.impl.porttypes;

import org.apache.axis.utils.XMLUtils;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.informationsystem.registry.impl.contexts.ProfileContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.gcube.informationsystem.registry.impl.state.ProfileResource;
import org.gcube.informationsystem.registry.stubs.GetProfileString;


/**
 * Implementation of the <em>Registry</em> portType
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class Registry extends GCUBEPortType  {
	
	/**
	 * Gets a string representation of the profile
	 * @param voidType
	 * @return a string representation of the profile 
	 * @throws GCUBEFault
	 */
	public String getProfileAsString(GetProfileString voidType)  throws GCUBEFault{
		
		try {
			return XMLUtils.DocumentToString(((ProfileResource)ProfileContext.getContext().getWSHome().find()).getProfile());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GCUBEFault();
		}
	}

	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
