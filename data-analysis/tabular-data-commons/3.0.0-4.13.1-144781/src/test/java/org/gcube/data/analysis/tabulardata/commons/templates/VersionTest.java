package org.gcube.data.analysis.tabulardata.commons.templates;

import org.gcube.data.analysis.tabulardata.commons.utils.Version;
import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

	@Test
	public void versionTest(){
		Assert.assertNotNull(Version.parse("1.0.0-3"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void versionErrorTest(){
		Version.parse("1.0-3");
	}
}
