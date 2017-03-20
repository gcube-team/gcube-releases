package org.gcube.data.analysis.tabulardata.model.metadata.table;



import org.junit.Assert;
import org.junit.Test;

public class VersionMetadataTest {

	@Test
	public final void testCreation() {
		try {
			new VersionMetadata("1.0");
			new VersionMetadata("3.4");
			new VersionMetadata("13.45");
			new VersionMetadata("134333.401203");
		} catch (IllegalArgumentException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreationWrongCases() {
		new VersionMetadata("13.05");
		new VersionMetadata("1.00000");
		new VersionMetadata("013.050");
		new VersionMetadata("13.050");
		new VersionMetadata("0001231.00000");
		new VersionMetadata("0001231.00000.ciao");
	}

}
