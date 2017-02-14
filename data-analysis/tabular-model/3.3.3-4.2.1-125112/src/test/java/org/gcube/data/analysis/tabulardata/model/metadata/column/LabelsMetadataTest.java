package org.gcube.data.analysis.tabulardata.model.metadata.column;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabelsMetadataTest {
	
	NamesMetadata metadata;

	@Before
	public void setUp() throws Exception {
		List<LocalizedText> texts = new ArrayList<LocalizedText>();
		texts.add(new ImmutableLocalizedText("noLocale"));
		texts.add(new ImmutableLocalizedText("english","en"));
		texts.add(new ImmutableLocalizedText("french","fr"));
		texts.add(new ImmutableLocalizedText("newFrench","fr"));
		texts.add(new ImmutableLocalizedText("spanish","es"));
		metadata = new NamesMetadata(texts);
	}

	@Test
	public void test() {
		System.out.println(metadata);
		Assert.assertEquals(3, metadata.getTexts().size());
		Assert.assertNotNull(metadata.getTextWithLocale("en"));
		Assert.assertEquals("english",metadata.getTextWithLocale("en").getValue());
		Assert.assertNotNull(metadata.getTextWithLocale("fr"));
		Assert.assertEquals("newFrench", metadata.getTextWithLocale("fr").getValue());
	}

}
