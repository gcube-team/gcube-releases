package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.ServiceException;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.RepositoryInfo;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.ExpansionCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.MappingCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.capabilities.UnfoldCapability;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.stubs.RemoteDispatcherPortType;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.spd.stubs.service.RemoteDispatcherServiceAddressingLocator;
import org.gcube.data.streams.Stream;
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
	private List<String> remoteUris = new ArrayList<String>();
	private RepositoryInfo info;
	private Set<Capabilities> supportedCapabilities = new HashSet<Capabilities>();
	
	@Override
	public RepositoryInfo getRepositoryInfo() {
		return info;
	}

	protected static  RemoteDispatcherPortType getRemoteDispatcher(List<String> uris) throws ServiceException{
		for ( String uri : uris){
			try{
				RemoteDispatcherPortType remoteDispatcher = new RemoteDispatcherServiceAddressingLocator()
				.getRemoteDispatcherPortTypePort(new EndpointReferenceType(new Address(uris.get(0))));			
				remoteDispatcher = 	GCUBERemotePortTypeContext.getProxy(remoteDispatcher);
				return remoteDispatcher;
			}catch (Exception e) {
				logger.warn("uri {} is not valid",uri,e);
			}
		}
		throw new ServiceException("uris "+uris+" are not valid");
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
		//trasforming properties
		logger.trace("call arrived in scope "+ScopeProvider.instance.get());
		SearchCondition[] props;
		if (properties!=null && properties.length>0){
			props = new SearchCondition[properties.length];
			for (int i = 0 ; i<properties.length; i++)
				props[i] = new SearchCondition(properties[i].getOp().name(),  new XStream().toXML(properties[i].getValue()), properties[i].getType().name() );
		}else props = new SearchCondition[0];

		logger.trace("properties retrieved");
		
		try{
			String locator = getRemoteDispatcher(remoteUris).search(new SearchRequest(this.name, props, Constants.RESULITEM_RETURN_TYPE, word));
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


	public List<String> getRemoteUris() {
		return remoteUris;
	}
	
	
}
