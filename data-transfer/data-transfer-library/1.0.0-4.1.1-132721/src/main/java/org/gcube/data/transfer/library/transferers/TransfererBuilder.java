package org.gcube.data.transfer.library.transferers;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.data.transfer.library.caches.CapabilitiesCache;
import org.gcube.data.transfer.library.client.Client;
import org.gcube.data.transfer.library.faults.HostingNodeNotFoundException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
@Slf4j
public class TransfererBuilder {

	private static final int timeout=10*1000;


	//e.g. http://pc-fabio.isti.cnr.it:8080/data-transfer-service/gcube/service

	public static Transferer getTransfererByHost(String endpoint) throws UnreachableNodeException, ServiceNotFoundException{
		log.debug("Get transferer by Host "+endpoint);
		try{
			URL url=new URL(endpoint);

			String baseUrl=url.getProtocol()+"://"+url.getHost()+":"+url.getPort();
			//TODO Implement checks

			//		if(!Utils.pingURL(host, timeout)) throw new UnreachableNodeException("No response from host in "+timeout);
			//		String finalHost=host;		
			//		if(!finalHost.endsWith(ServiceConstants.APPLICATION_PATH)){
			//			// adjust host
			//			finalHost=finalHost+(host.endsWith("/")?"":"/")+"data-transfer-service"+ServiceConstants.APPLICATION_PATH;			
			//		}
			//		
			//		
			//		if(!Utils.pingURL(finalHost, timeout)) throw new ServiceNotFoundException("No DT Service found @ "+finalHost);
			//		log.debug("Host is ok, getting targetCapabilities");
//			TransferCapabilities cap=CapabilitiesCache.getInstance().getObject(baseUrl);
						
			// TODO determine method by capabilities checking			
			
			return new HTTPTransferer(new Client(baseUrl)); 
		}catch(Exception e){
			throw new ServiceNotFoundException(e);
		}
	}
	public static Transferer getTransfererByhostingNodeId(String hostId) throws HostingNodeNotFoundException, UnreachableNodeException, ServiceNotFoundException{
		String hostname=retrieveHostnameByNodeId(hostId);
		return getTransfererByHost(hostname);
	}


	private static String retrieveHostnameByNodeId(String nodeId)throws HostingNodeNotFoundException{
		SimpleQuery query = queryFor(HostingNode.class);

		query.addCondition("$resource/ID/text() eq '"+nodeId+"'");						


		List<HostingNode> found= clientFor(HostingNode.class).submit(query);

		if(found.isEmpty()) throw new HostingNodeNotFoundException("No Hosting node with the id "+nodeId);
		
		return "http://"+found.get(0).profile().description().name();
	}
}
