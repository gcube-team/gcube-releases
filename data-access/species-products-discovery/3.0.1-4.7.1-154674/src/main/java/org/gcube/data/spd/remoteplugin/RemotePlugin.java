package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.ServiceException;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.service.types.SearchCondition;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.streams.Stream;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemotePlugin extends AbstractPlugin {

	volatile static Logger logger = LoggerFactory.getLogger(RemotePlugin.class);

	private ClassificationCapability classification;
	private MappingCapability mapping;
	private ExpansionCapability expand;
	private OccurrencesCapability occurrences;
	private UnfoldCapability unfold;
	private String name;
	private String description;
	private Set<String> remoteUris = new HashSet<String>();
	private RepositoryInfo info;
	private Set<Capabilities> supportedCapabilities = new HashSet<Capabilities>();

	private static Map<String, String> cacheGCoreEnpointsRemoteDispatcherPT = new HashMap<String, String>();

	@Override
	public RepositoryInfo getRepositoryInfo() {
		return info;
	}


	protected static  String getRemoteDispatcher(Collection<String> endpointIds) throws ServiceException{

		if (endpointIds==null || endpointIds.size()==0)
			throw new ServiceException("remote service endpoints are empty");

		boolean notCachedFound = false;
		Set<RemoteUri> uris = new HashSet<RemoteUri>();

		StringBuffer inBuf = new StringBuffer("(");

		for (String endpointId : endpointIds){
			if (cacheGCoreEnpointsRemoteDispatcherPT.containsKey(endpointId))
				uris.add(new RemoteUri(endpointId, cacheGCoreEnpointsRemoteDispatcherPT.get(endpointId)));
			else{
				inBuf.append("'").append(endpointId).append("'").append(",");
				notCachedFound = true;
			}
		}

		if (notCachedFound){
			inBuf.replace(inBuf.lastIndexOf(","), inBuf.length(), ")");

			try{
				SimpleQuery query = queryFor(GCoreEndpoint.class);

				query.addCondition("$resource/Profile/ServiceName/text() eq '"+Constants.SERVICE_NAME+"'")
				.addCondition("$resource/Profile/ServiceClass/text() eq '"+Constants.SERVICE_CLASS+"'")
				.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'") 
				.addCondition("$resource/ID/text() in "+inBuf.toString());

				query.setResult("<RemoteUri><id>{$resource/ID/text()}</id>" +
						"<uri>{$resource/Profile/AccessPoint/RunningInstanceInterfaces//Enpoint[@EntryName/text() eq 'remote-dispatcher'][0]}</uri><RemoteUri>");

				DiscoveryClient<RemoteUri> client = clientFor(RemoteUri.class);

				List<RemoteUri> discoveredUris = client.submit(query);

				for (RemoteUri discoveredUri: discoveredUris){
					uris.add(discoveredUri);
					cacheGCoreEnpointsRemoteDispatcherPT.put(discoveredUri.getEndpointId(), discoveredUri.getUri());
				}

			}catch(Exception e){
				logger.warn("error discoverying remote gCoreEnpoints",e);
			}	
		}

		for (RemoteUri uri : uris){
			try{
				return uri.getUri();
			}catch(Exception e){
				logger.warn("remote dispatcher at "+uri+" is unreachable, it'll be discarded and removed from cache");
				cacheGCoreEnpointsRemoteDispatcherPT.remove(uri.getEndpointId());
			}
		}
		
		throw new ServiceException("no valid uri found for this remote plugin");


	}

	public void addUrl(String url){
		this.remoteUris.add(url);
	}

	public void removeUrl(String url){
		this.remoteUris.remove(url);
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<ResultItem> writer, Condition... properties) {
		//transforming properties
		logger.trace("("+this.getRepositoryName()+" - REMOTE) call arrived in scope "+ScopeProvider.instance.get());
		List<SearchCondition> props = Collections.emptyList();
		if (properties!=null && properties.length>0){
			props = new ArrayList<SearchCondition>(properties.length);
			for (int i = 0 ; i<properties.length; i++)
				props.add(new SearchCondition(properties[i].getType(), properties[i].getOp(),  new XStream().toXML(properties[i].getValue())));
		}

		logger.trace("properties retrieved");

		try{
			String locator = "";// getRemoteDispatcher(remoteUris).search(new SearchRequest(this.name, props, Constants.RESULITEM_RETURN_TYPE, word));
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((ResultItem) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding result item",e);
				}

		}catch (Exception e) {
			logger.error("error executing search",e);
		}
	}

	public void remoteIntitializer(PluginDescription pd, String uri) throws Exception{
		this.setUseCache(true);


		this.name = pd.getName();
		this.description = pd.getDescription();
		this.remoteUris.add(uri);
		this.info = pd.getInfo();

		//adding supported capabilities
		for (Entry<Capabilities, List<Conditions>> capabilityDescriptions: pd.getSupportedCapabilities().entrySet()){

			Conditions[] properties = capabilityDescriptions.getValue().toArray(new Conditions[capabilityDescriptions.getValue().size()]);

			switch (capabilityDescriptions.getKey()) {
			case Classification:
				this.classification = new RemoteClassificationCapability(properties, this.name, remoteUris);
				break;
			case NamesMapping:
				this.mapping = new RemoteNamesMappingCapability(properties, this.name,remoteUris);
				break;
			case Occurrence:
				this.occurrences = new RemoteOccurrencesCapability(properties, this.name, remoteUris);
				break;
			case Expansion:
				this.expand = new RemoteExpandCapability(properties, this.name, remoteUris);
				break;
			case Unfold:
				this.unfold = new RemoteUnfoldCapability(properties, this.name, remoteUris);
				break;	
			default:
				break;
			}
			supportedCapabilities.add(capabilityDescriptions.getKey());
		}

	}

	@Override
	public ClassificationCapability getClassificationInterface() {
		return classification;
	}


	@Override
	public OccurrencesCapability getOccurrencesInterface() {
		return occurrences;
	}

	@Override
	public MappingCapability getMappingInterface() {
		return mapping;
	}


	@Override
	public ExpansionCapability getExpansionInterface() {
		return expand;
	}

	@Override
	public UnfoldCapability getUnfoldInterface() {
		return unfold;
	}

	@Override
	public Set<Capabilities> getSupportedCapabilities() {
		return this.supportedCapabilities;
	}

	public boolean isRemote(){
		return true;
	}

	@Override
	public String getRepositoryName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}


	public Collection<String> getRemoteUris() {
		return remoteUris;
	}


}
