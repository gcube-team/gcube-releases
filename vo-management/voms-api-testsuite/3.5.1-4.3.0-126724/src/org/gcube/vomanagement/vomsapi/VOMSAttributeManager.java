package org.gcube.vomanagement.vomsapi;

import org.gcube.vomanagement.vomsapi.impl.VOMSAdminException;
import org.gridforum.jgss.ExtendedGSSCredential;

public interface VOMSAttributeManager extends VOMSServerManager
{
	ExtendedGSSCredential generateAttributedCredentials(ExtendedGSSCredential credentials) throws VOMSAdminException;
	
}
