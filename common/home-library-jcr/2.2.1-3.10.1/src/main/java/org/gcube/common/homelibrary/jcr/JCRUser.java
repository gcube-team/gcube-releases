package org.gcube.common.homelibrary.jcr;

import org.gcube.common.homelibrary.home.User;

public class JCRUser implements User {
	
	protected String id;
	protected String portalLogin;
	
	

	public JCRUser(String id, String portalLogin) {
		super();
		this.id = id;
		this.portalLogin = portalLogin;
	}
	
	@Override
	public String getId()  {
		return id;
	}

	@Override
	public String getPortalLogin()  {
		return portalLogin;
	}

}
