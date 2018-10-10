package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.ScopeUtils;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;

public class TestIS {

	public static void main(String[] args) throws MalformedURLException {
		TokenSetter.set("/gcube/devNext");
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		ServiceEndpoint e=ISUtils.querySEById("c8afa8c0-c781-11e2-8f67-be3b57c971ee");
		System.out.println(ISUtils.updateAndWait(e));
		
		System.out.println("PARENTS");
		System.out.println(ScopeUtils.getParentScope("/gcube"));
		System.out.println(ScopeUtils.getParentScope("/gcube/devNext"));
		System.out.println(ScopeUtils.getParentScope("/gcube/devNext/NextNext"));
		
	}

}
