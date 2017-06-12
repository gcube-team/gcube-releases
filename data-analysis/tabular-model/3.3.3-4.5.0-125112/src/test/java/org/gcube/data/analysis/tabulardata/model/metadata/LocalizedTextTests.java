package org.gcube.data.analysis.tabulardata.model.metadata;



import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.junit.Assert;
import org.junit.Test;

public class LocalizedTextTests {
	
	@Test
	public void testCreation(){
		LocalizedText localizedText = new ImmutableLocalizedText("test");
		Assert.assertEquals("test", localizedText.getValue());
		Assert.assertEquals("en", localizedText.getLocale());
	}

}
