package org.gcube.data.spd.client.manager;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.plugins.AbstractPlugin;
import org.gcube.data.spd.client.proxies.OccurrenceClient;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrencesTest {

	private static Logger logger = LoggerFactory.getLogger(OccurrencesTest.class);


	private static List<String> occurrenceIds=Arrays.asList("GBIF:1238408827","GBIF:1234813179","GBIF:1136918536","GBIF:1050224578",
			"GBIF:1084012520","GBIF:1084013323","GBIF:1084013326","GBIF:1084013281", "GBIF:857015137","GBIF:856828010");

	private static List<String> occurrenceKeys = Arrays.asList("GBIF:84028840-f762-11e1-a439-00145eb45e9a^^Marine and Coastal Management - Demersal Surveys (years 1991-1995) (AfrOBIS)^^Marine and Coastal Management - Demersal Surveys^^0e0fc0f0-828e-11d8-b7ed-b8a03c50a862^^Ocean Biogeographic Information System||5208593" , "GBIF:ed820bdb-4345-4143-a280-4fbffaacd31d^^The Pisces Collection at the Staatssammlung für Anthropologie und Paläoanatomie München^^Staatliche Naturwissenschaftliche Sammlungen Bayerns: The Pisces Collection at the Staatssammlung für Anthropologie und Paläoanatomie München^^0674aea0-a7e1-11d8-9534-b8a03c50a862^^Staatliche Naturwissenschaftliche Sammlungen Bayerns||5712279", "GBIF:8609f1a0-f762-11e1-a439-00145eb45e9a^^Marine and Coastal Management - Linefish Dataset (Second Semester of 1992) (AfrOBIS)^^Marine and Coastal Management - Linefish Dataset^^0e0fc0f0-828e-11d8-b7ed-b8a03c50a862^^Ocean Biogeographic Information System||5208602" );

	@Test
	public void getOccurencesByIds() throws Exception{
		SecurityTokenProvider.instance.set("94a3b80a-c66f-4000-ae2f-230f5dfad793-98187548");
		ScopeProvider.instance.set("/gcube/devsec");
		OccurrenceClient occurrences = AbstractPlugin.occurrences().build();
		Stream<OccurrencePoint> stream =  occurrences.getByIds(occurrenceIds);

		int i =0;
		while (stream.hasNext())
			System.out.println(i+++")"+stream.next());

		stream.close();	

	}

	@Test
	public void getOccurencesByKeys() throws Exception{
		SecurityTokenProvider.instance.set("94a3b80a-c66f-4000-ae2f-230f5dfad793-98187548");
		ScopeProvider.instance.set("/gcube/devsec");
		OccurrenceClient occurrences = AbstractPlugin.occurrences().build();

		Stream<OccurrencePoint> stream =  occurrences.getByKeys(occurrenceKeys);

		int i =0;
		while (stream.hasNext())
			System.out.println(i+++")"+stream.next());

		stream.close();			
	}

}
