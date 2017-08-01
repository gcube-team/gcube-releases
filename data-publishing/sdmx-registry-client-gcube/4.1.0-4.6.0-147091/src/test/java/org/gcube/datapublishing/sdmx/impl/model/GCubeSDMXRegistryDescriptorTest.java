package org.gcube.datapublishing.sdmx.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryDescriptor;
import org.gcube.datapublishing.sdmx.api.model.SDMXRegistryInterfaceType;
import org.junit.Assert;
import org.junit.Test;

public class GCubeSDMXRegistryDescriptorTest {

	private SDMXRegistryDescriptor rd = new GCubeSDMXRegistryDescriptor();

	@Test
	public void getRestUrlsOnDevelopment() {
		ScopeProvider.instance.set("/gcube/devsec");
		String url;
		for (SDMXRegistryInterfaceType type : SDMXRegistryInterfaceType.values()) {
			switch (type) {
			case RESTV1:
			case RESTV2:
			case RESTV2_1:
				url = rd.getUrl(type);
				System.out.println(type + ": " + url);
				Assert.assertFalse(url.isEmpty());
			default:
			}
		}
	}

	@Test
	public void getRestUrlsOnProduction() {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps");
		String url;
		for (SDMXRegistryInterfaceType type : SDMXRegistryInterfaceType.values()) {
			switch (type) {
			case RESTV1:
			case RESTV2:
			case RESTV2_1:
				url = rd.getUrl(type);
				System.out.println(type + ": " + url);
				Assert.assertFalse(url.isEmpty());
			default:
			}
		}
	}

	@Test
	public void testMultipleScopes() {
		List<String> scopes = new ArrayList<String>();
		scopes.add("/gcube/devsec");
		scopes.add("/d4science.research-infrastructures.eu/gCubeApps");
		for (String scope : scopes) {
			ScopeProvider.instance.set(scope);
			System.out.println("Scope: " + scope);
			String url;
			for (SDMXRegistryInterfaceType type : SDMXRegistryInterfaceType.values()) {
				switch (type) {
				case RESTV1:
				case RESTV2:
				case RESTV2_1:
					url = rd.getUrl(type);
					System.out.println(type + ": " + url);
					Assert.assertFalse(url.isEmpty());
				default:
				}
			}
		}

	}
}
