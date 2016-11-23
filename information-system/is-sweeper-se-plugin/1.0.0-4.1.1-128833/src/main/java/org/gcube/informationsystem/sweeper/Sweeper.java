package org.gcube.informationsystem.sweeper;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.utils.DateFormatterAdapter;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class Sweeper {
	
	private static final Logger logger = LoggerFactory.getLogger(Sweeper.class);
	
	protected RegistryPublisher registryPublisher;
	protected DateFormatterAdapter dateFormatterAdapter;
	
	public Sweeper(){
		registryPublisher = RegistryPublisherFactory.create();
		dateFormatterAdapter = new DateFormatterAdapter();
	}
	
	protected final static String UNREACHABLE = "unreachable";
	protected final static String CERTIFIED = "certified";
	
	protected String getHostingNodeMinimalInfo(HostingNode hostingNode) throws Exception{
		Calendar lastUpdate = hostingNode.profile().description().lastUpdate();
		return String.format("%s (ID : %s - Name : %s - Status : %s - LastUpdate : %s)", 
				HostingNode.class.getSimpleName(), hostingNode.id(),
				hostingNode.profile().description().name(),
				hostingNode.profile().description().status(),
				dateFormatterAdapter.marshal(lastUpdate));
	}
	
	protected String getGCoreEndpointMinimalInfo(GCoreEndpoint gCoreEndpoint, HostingNode hostingNode) throws Exception{
		return String.format("%s (ID : %s - ServiceClass : %s - ServiceName : %s. Was running on %s)", 
				GCoreEndpoint.class.getSimpleName(), gCoreEndpoint.id(),
				gCoreEndpoint.profile().serviceClass(), 
				gCoreEndpoint.profile().serviceName(),
				getHostingNodeMinimalInfo(hostingNode));
	}
	
	public void sweepExpiredGHNs(int expiringField, int expiringQuantity) throws Exception {
		Calendar expiryCalendar = Calendar.getInstance();
		expiryCalendar.add(expiringField, expiringQuantity);
		
		String formattedDate = dateFormatterAdapter.marshal(expiryCalendar);
				
		String condition = String.format(
				"xs:dateTime($resource/Profile/GHNDescription/LastUpdate/text()) lt xs:dateTime('%s')", 
				formattedDate);
				
		SimpleQuery query = ICFactory.queryFor(HostingNode.class)
				.addCondition(String.format("$resource/Profile/GHNDescription/Status/text() eq '%s'", CERTIFIED))
				.addCondition(condition)
				.setResult("$resource");
		
		DiscoveryClient<HostingNode> client = ICFactory.clientFor(HostingNode.class);
		List<HostingNode> hostingNodes = client.submit(query);
		
		for (HostingNode hostingNode : hostingNodes) {
			try {
				
				logger.debug("Setting {} status to {}", 
						getHostingNodeMinimalInfo(hostingNode),
						UNREACHABLE);
				
				hostingNode.profile().description().status(UNREACHABLE);
				registryPublisher.update(hostingNode);
				
				logger.debug("Request to set status to {} for {} successfully sent\n",
						UNREACHABLE, getHostingNodeMinimalInfo(hostingNode));
				
			}catch(Exception e){
				logger.error("Unable to set status to {} for {}", UNREACHABLE, 
						getHostingNodeMinimalInfo(hostingNode), e);
			}
		}
	}

	public void sweepDeadGHNs(int deadField, int deadQuantity) throws Exception {
		Calendar deadCalendar = Calendar.getInstance();
		deadCalendar.add(deadField, deadQuantity);
		
		String formattedDate = dateFormatterAdapter.marshal(deadCalendar);
				
		String condition = String.format(
				"xs:dateTime($resource/Profile/GHNDescription/LastUpdate/text()) lt xs:dateTime('%s')", 
				formattedDate);
				
		SimpleQuery query = ICFactory.queryFor(HostingNode.class)
				.addCondition(condition)
				.setResult("$resource");
		
		DiscoveryClient<HostingNode> client = ICFactory.clientFor(HostingNode.class);
		List<HostingNode> hostingNodes = client.submit(query);
		
		for (HostingNode hostingNode : hostingNodes) {
			try {
				
				logger.debug("Going to remove dead {}", 
						getHostingNodeMinimalInfo(hostingNode));
				
				registryPublisher.remove(hostingNode);
				
				logger.debug("Request to remove {} successfully sent\n",
						getHostingNodeMinimalInfo(hostingNode));
			}catch(Exception e){
				logger.error("Unable to remove {}", 
						getHostingNodeMinimalInfo(hostingNode), e);
			}
		}
		
	}

	public void sweepOrphanRI() throws Exception {
		
		SimpleQuery hostingNodeQuery = ICFactory.queryFor(HostingNode.class)
				.addCondition(String.format("$resource/Profile/GHNDescription/Status/text() ne '%s'", CERTIFIED))
				.setResult("$resource");
		DiscoveryClient<HostingNode> hostingNodeClient = ICFactory.clientFor(HostingNode.class);
		List<HostingNode> hostingNodes = hostingNodeClient.submit(hostingNodeQuery);
		
		for(HostingNode hostingNode : hostingNodes){
			String condition = String.format("$resource/Profile/GHN/@UniqueID/string() eq '%s'", hostingNode.id());
			
			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class)
					.addCondition(condition)
					.setResult("$resource");
			
			DiscoveryClient<GCoreEndpoint> client = ICFactory.clientFor(GCoreEndpoint.class);
			List<GCoreEndpoint> gCoreEndpoints = client.submit(query);
			
			for (GCoreEndpoint gCoreEndpoint : gCoreEndpoints) {
				try {
					
					logger.debug("Going to remove orphan {}", 
							getGCoreEndpointMinimalInfo(gCoreEndpoint, hostingNode));
					
					registryPublisher.remove(gCoreEndpoint);
					
					logger.debug("Request to remove {} successfully sent\n",
							getGCoreEndpointMinimalInfo(gCoreEndpoint, hostingNode));
				}catch(Exception e){
					logger.error("Unable to remove {}", 
							getGCoreEndpointMinimalInfo(gCoreEndpoint, hostingNode), e);
				}
			}

		}
	}

}
