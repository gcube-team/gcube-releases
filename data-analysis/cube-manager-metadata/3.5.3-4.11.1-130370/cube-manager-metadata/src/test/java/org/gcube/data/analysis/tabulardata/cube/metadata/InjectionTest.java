package org.gcube.data.analysis.tabulardata.cube.metadata;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class InjectionTest {

	@Inject
	JPACubeMetadataWrangler bean;

	@Test
	public void test() {
		Assert.assertNotNull(bean);
	}
	
}
