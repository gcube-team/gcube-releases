package org.gcube.data.analysis.tabulardata.service.tabular.metadata;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class SerializationTests {

	@Test
	public final void testMetadata() throws JAXBException {
		SerializationUtil.roundTripTest(new AgencyMetadata("CNR"));
		SerializationUtil.roundTripTest(new RightsMetadata("My Rights"));
		SerializationUtil.roundTripTest(new NameMetadata("Name"));
		SerializationUtil.roundTripTest(new DescriptionMetadata("Description"));
	}

}
