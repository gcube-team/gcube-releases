package org.gcube.spatial.data.sdi.test;

import java.io.IOException;

import org.gcube.spatial.data.sdi.NetUtils;

public class NetTests {

	public static void main(String[] args) throws IOException {
		TokenSetter.set("/gcube/devNext");
		NetUtils.makeAuthorizedCall("thredds-d-d4s.d4science.org", "thredds/admin/debug?catalogs/reinit", "tds", "trythat");
	}

}
