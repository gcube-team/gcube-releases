package org.gcube.common.resources.gcore;

import static junit.framework.Assert.*;
import static org.gcube.common.resources.gcore.Resources.*;
import static org.gcube.common.resources.gcore.TestUtils.*;

import java.math.BigDecimal;
import java.util.Calendar;

import org.gcube.common.resources.gcore.HostingNode;
import org.junit.Test;

/**
 * 
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class HostingNodeTest {

	@Test
	public void bindNode() throws Exception {

		HostingNode node = unmarshal(HostingNode.class, "node.xml");

		print(node);

		validate(node);
		
		HostingNode clone = unmarshal(HostingNode.class, "node.xml");
		
		assertEquals(node,clone);

	}
	
	//helper
	private HostingNode minimalNode() {
		
		HostingNode node = new HostingNode();
		
		node.scopes().add("/some/scope");
		
		node.newProfile().infrastructure("infrastructure");
		
		node.profile().newDescription().name("name").uptime("uptime").lastUpdate(Calendar.getInstance());
		node.profile().description().newArchitecture().platformType("type").smpSize(30).smtSize(20);
		node.profile().description().newOperatingSystem().name("name").version("version").release("release");
		node.profile().description().environmentVariables().add().keyAndValue("key", "value");
		node.profile().description().newMainMemory().ramAvailable(100).ramSize(200).virtualAvailable(300).virtualSize(350);

		node.profile().newSite().location("location").country("it").
		                         domain("domain").latitude("latitude").longitude("longitude");

		return node;
	}
	
	@Test
	public void buildMininimalNode() throws Exception {
		
		HostingNode node = minimalNode();
		
		
		print(node);
		
		validate(node);
	}
	
	@Test
	public void buildMaximalNode() throws Exception {
		
		HostingNode node = minimalNode();
		
		node.profile().description().activationTime(Calendar.getInstance());
		
		node.profile().description().platforms().add().name("name").version((short)2);
		
		node.profile().description().newSecurity().authority("auth").
												   distinguishedName("dn").
												   expirationDate(Calendar.getInstance());
		
		
		node.profile().description().processors().add().
												family("family").
												model("model").
												vendor("vendor").modelName("name").
												cacheL1(10).cacheL1D(20).cacheL1I(30).cacheL2(40).
												bogomips(BigDecimal.valueOf(40000)).
												clockSpeedMhz(BigDecimal.valueOf(3000));

		
		node.profile().description().processors().add().
												family("family2").
												model("model2").
												vendor("vendor2").modelName("name2").
												cacheL1(10).cacheL1D(20).cacheL1I(30).cacheL2(40).
												bogomips(BigDecimal.valueOf(40000)).
												clockSpeedMhz(BigDecimal.valueOf(3000));
		
		node.profile().description().networkAdapters().add().
														inboundIP("inbound").
														outboundIP("outbound").
														ipAddress("address").mtu(10).name("name");
		
		node.profile().description().networkAdapters().add().
													inboundIP("inbound2").
													outboundIP("outbound2").
													ipAddress("address2").mtu(10).name("name2");
		
		
		node.profile().description().newBenchmark().sf(10).si(20);
		
		node.profile().description().storageDevices().add().name("name").size(10).transferRate(10).type("type");
		node.profile().description().storageDevices().add().name("name2").size(10).transferRate(10).type("type2");
		
		node.profile().description().storagePartitions().add().name("name").readRate(10).writeRate(20).size("size");
		node.profile().description().storagePartitions().add().name("name2").readRate(10).writeRate(20).size("size2");
		
		node.profile().description().localFileSystems().add().name("name").size(10).readOnly(true).root("root").type("type");
		node.profile().description().localFileSystems().add().name("name2").size(10).readOnly(true).root("root2").type("type2");

		node.profile().description().remoteFileSystems().add().name("name").size(10).readOnly(true).root("root").type("type");
		node.profile().description().remoteFileSystems().add().name("name2").size(10).readOnly(true).root("root2").type("type2");

		node.profile().description().devicePartitions().add().device("name").name("name");
		node.profile().description().devicePartitions().add().device("name2").name("name2");
		
		node.profile().description().fileSystemPartitions().add().fsName("name").storageName("name");
		node.profile().description().fileSystemPartitions().add().fsName("name2").storageName("name2");
		
		node.profile().description().newLoad().lastMin(4.5).last15Mins(20.6).last5Mins(5.6);
		node.profile().description().newHistoricalLoad().lastDay(67).lastHour(15.3).lastWeek(150.6);
		
		node.profile().description().localAvailableSpace(new Long(100));
		
		node.profile().packages().add().name("name").serviceVersion("1.0.0").packageVersion("version").serviceClass("class").serviceName("name");
		node.profile().packages().add().name("name").serviceVersion("2.0.1").packageVersion("version2").serviceClass("class2").serviceName("name2");
	
		print(node);
		
		validate(node);
	}
}
