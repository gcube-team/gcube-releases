package org.gcube.vomanagement.vomsapi.impl;

import org.gcube.vomanagement.vomsapi.ExtendedVOMSAdmin;
import org.gcube.vomanagement.vomsapi.VOMSAttributeManager;
import org.gridforum.jgss.ExtendedGSSCredential;

public class VOMSAttributeManagerImpl extends VOMSAttributeAdder implements VOMSAttributeManager  
{
	
	VOMSAttributeManagerImpl(VOMSAPIConfiguration configuration,
			ExtendedVOMSAdmin extendedVOMSAdmin) {
		super (configuration,extendedVOMSAdmin);
	}


	@Override
	public ExtendedGSSCredential generateAttributedCredentials(ExtendedGSSCredential credentials) throws VOMSAdminException {

		return addVOMSRoles(credentials, (String []) null);
	}

}
