package org.gcube.spatial.data.sdi.test;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.spatial.data.sdi.LocalConfiguration;
import org.gcube.spatial.data.sdi.engine.impl.is.ISUtils;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;

public class TestIS {

	public static void main(String[] args) throws MalformedURLException {
		TokenSetter.set("/gcube/devNext/NextNext");
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		ServiceEndpoint e=ISUtils.querySEById("8e1962e9-05a7-40d4-a56f-574431f4c907");
		
		e.profile().description(e.profile().description()+"_modified");
		
		System.out.println("Identity : "+ISUtils.marshal(e).equals(ISUtils.marshal(e)));
		
		
		System.out.println(ISUtils.updateAndWait(e));
		
		System.out.println("PARENTS");
		System.out.println(ScopeUtils.getParentScope("/gcube"));
		System.out.println(ScopeUtils.getParentScope("/gcube/devNext"));
		System.out.println(ScopeUtils.getParentScope("/gcube/devNext/NextNext"));
		
	}

}
