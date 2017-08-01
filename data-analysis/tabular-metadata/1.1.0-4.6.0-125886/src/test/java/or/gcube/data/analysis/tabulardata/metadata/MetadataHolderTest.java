package or.gcube.data.analysis.tabulardata.metadata;

import or.gcube.data.analysis.tabulardata.metadata.table.ContainerMetadata;
import or.gcube.data.analysis.tabulardata.metadata.table.MetaInt;
import or.gcube.data.analysis.tabulardata.metadata.table.MetaString;

import org.gcube.data.analysis.tabulardata.metadata.ArrayListMetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.MetadataHolder;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.junit.Assert;
import org.junit.Test;

public class MetadataHolderTest {

	MetadataHolder<ContainerMetadata> metaHolder = new ArrayListMetadataHolder<ContainerMetadata>();

	@Test
	public final void test() {
		metaHolder.setMetadata(new MetaInt(5));
		Assert.assertEquals(1, metaHolder.getAllMetadata().size());
		Assert.assertTrue(metaHolder.contains(MetaInt.class));
		metaHolder.removeAllMetadata();
		Assert.assertEquals(0, metaHolder.getAllMetadata().size());
		metaHolder.setMetadata(new MetaInt(5));
		metaHolder.setMetadata(new MetaString("ciao"));
		Assert.assertEquals(2, metaHolder.getAllMetadata().size());
	}
	
	@Test(expected=NoSuchMetadataException.class)
	public void testGetEmpty() {
		metaHolder.getMetadata(MetaInt.class);
	}

}
