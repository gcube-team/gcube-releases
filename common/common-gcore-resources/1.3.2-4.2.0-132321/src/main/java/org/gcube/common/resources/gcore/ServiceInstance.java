package org.gcube.common.resources.gcore;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.gcube.common.resources.gcore.common.AnyWrapper;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.resources.gcore.utils.Utils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Describes instances of stateful services.
 * 
 */
@XmlRootElement(name="Document")
public class ServiceInstance {

	@XmlElement(name="ID")
	private String id;
	
	@XmlElement(name="Source")
	private URI endpoint;
	
	@XmlElement(name="SourceKey")
	private String key;
	
	@XmlElementRef
	private Reference keyElement;
	
	@XmlRootElement(name="CompleteSourceKey")
	private static class Reference extends AnyWrapper {}
	
	@XmlElement(name="TerminationTime")
	private long terminationTime;
	
	@XmlElement(name="LastUpdateMs")
	private long lastUpdate;
	
	@XmlElement(name="PublicationMode")
	private String publicationMode;
	
	@XmlElementRef
	private Properties properties;

	public String id() {
		return id;
	}
	
	public ServiceInstance id(String id) {
		this.id = id;
		return this;
	}

	public URI endpoint() {
		return endpoint;
	}
	
	public ServiceInstance endpoint(URI endpoint) {
		this.endpoint = endpoint;
		return this;
	}

	public String key() {
		return key;
	}
	
	public ServiceInstance key(String key) {
		this.key = key;
		return this;
	}
	

	public EndpointReference reference() {
		
		return new W3CEndpointReferenceBuilder().address(endpoint.toString()).referenceParameter(keyElement.root()).build();		
	}

	public long terminationTime() {
		return terminationTime;
	}
	
	public ServiceInstance terminationTime(long terminationTime) {
		this.terminationTime = terminationTime;
		return this;
	}

	public long lastUpdate() {
		return lastUpdate;
	}
	
	public ServiceInstance lastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	public String publicationMode() {
		return publicationMode;
	}
	
	public ServiceInstance publicationMode(String publicationMode) {
		this.publicationMode = publicationMode;
		return this;
	}

	public Properties properties() {
		return properties;
	}
	
	public Properties newProperties() {
		return properties=new Properties();
	}

	@Override
	public String toString() {
		return "ServiceInstance [id=" + id + ", endpoint=" + endpoint + ", key=" + key + ", reference=" + reference()
				+ ", terminationTime=" + terminationTime + ", lastUpdate=" + lastUpdate + ", publicationMode="
				+ publicationMode + ", properties=" + properties + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + (int) (lastUpdate ^ (lastUpdate >>> 32));
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((publicationMode == null) ? 0 : publicationMode.hashCode());
		result = prime * result + ((keyElement == null) ? 0 : keyElement.hashCode());
		result = prime * result + (int) (terminationTime ^ (terminationTime >>> 32));
		return result;
	}

	//NOTE: manually adapted, do not regenerate blindly
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceInstance other = (ServiceInstance) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (lastUpdate != other.lastUpdate)
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (publicationMode == null) {
			if (other.publicationMode != null)
				return false;
		} else if (!publicationMode.equals(other.publicationMode))
			return false;
		if (keyElement == null) {
			if (other.keyElement != null)
				return false;
		} else if (!keyElement.equals(other.keyElement))
			return false;
		if (terminationTime != other.terminationTime)
			return false;
		return true;
	}

	@XmlRootElement(name="Data")
	public static class Properties {

		public static final String PROVIDER_NS="http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider";
		
		@XmlElement(name="GHN",namespace=PROVIDER_NS)
		private String nodeId;
		
		@XmlElement(name="RI",namespace=PROVIDER_NS)
		private String endpointId;
		
		@XmlElement(name="ServiceID",namespace=PROVIDER_NS)
		private String serviceId;
		
		@XmlElement(name="ServiceClass",namespace=PROVIDER_NS)
		private String serviceClass;
		
		@XmlElement(name="ServiceName",namespace=PROVIDER_NS)
		private String serviceName;
		
		@XmlElement(name="Scope",namespace=PROVIDER_NS)
		private List<String> scopes = new ArrayList<String>();
		
		//this is the element we return to client for modifications
		private Element root;
		
		@XmlAnyElement
		private List<Element> customProperties;

		public String nodeId() {
			return nodeId;
		}
		
		public Properties nodeId(String nodeId) {
			this.nodeId = nodeId;
			return this;
		}

		public String endpointId() {
			return endpointId;
		}
		
		public Properties endpointId(String endpointId) {
			this.endpointId = endpointId;
			return this;
		}

		public String serviceId() {
			return serviceId;
		}
		
		public Properties serviceId(String serviceId) {
			this.serviceId = serviceId;
			return this;
		}

		public String serviceClass() {
			return serviceClass;
		}

		public Properties serviceClass(String serviceClass) {
			this.serviceClass = serviceClass;
			return this;
		}
		
		public String serviceName() {
			return serviceName;
		}

		public Properties serviceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}
		
		public Collection<String> scopes() {
			return new Group<String>(scopes,String.class);
		}
		
		public Element customProperties() {
			return root;
		}
		
		public Element newCustomProperties() {
			return root = Utils.newDocument().getDocumentElement();
		}

	    //after deserialisation, we link the elements to the root 
	    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
	    	if (customProperties!=null) {
	    		newCustomProperties();
	    		for (Element e: customProperties) 
	    			root.appendChild(root.getOwnerDocument().adoptNode(e));
	    	}
	    	customProperties=null;
	    }
	    
	    //before serialisation we copy the child elements of the root
	    void beforeMarshal(Marshaller marshaller) {
	    	if (root!=null) {
		    	customProperties = new ArrayList<Element>();
		    	NodeList list = root.getChildNodes();
		    	for (int i=0;i<list.getLength();i++) {
		    		Node node = list.item(i);
		    		if (node.getNodeType()==Node.ELEMENT_NODE)
		    			customProperties.add((Element) node);
		    	}
	    	}
	    }

		@Override
		public String toString() {
			return "Properties [nodeId=" + nodeId + ", endpointId=" + endpointId + ", serviceId=" + serviceId
					+ ", serviceClass=" + serviceClass + ", serviceName=" + serviceName + ", scopes=" + scopes
					+ ", customProperties=" + customProperties + "]";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((customProperties == null) ? 0 : customProperties.hashCode());
			result = prime * result + ((endpointId == null) ? 0 : endpointId.hashCode());
			result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
			result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
			result = prime * result + ((serviceClass == null) ? 0 : serviceClass.hashCode());
			result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
			result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
			return result;
		}


		//NOTE: manually adapted for DOM equality, do not regenerate blindly
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Properties other = (Properties) obj;
			//manually adapter
			if (customProperties() == null) {
				if (other.customProperties() != null)
					return false;
			} else if (!customProperties().isEqualNode(other.customProperties()))
				return false;
			if (endpointId == null) {
				if (other.endpointId != null)
					return false;
			} else if (!endpointId.equals(other.endpointId))
				return false;
			if (nodeId == null) {
				if (other.nodeId != null)
					return false;
			} else if (!nodeId.equals(other.nodeId))
				return false;
			if (scopes == null) {
				if (other.scopes != null)
					return false;
			} else if (!scopes.equals(other.scopes))
				return false;
			if (serviceClass == null) {
				if (other.serviceClass != null)
					return false;
			} else if (!serviceClass.equals(other.serviceClass))
				return false;
			if (serviceId == null) {
				if (other.serviceId != null)
					return false;
			} else if (!serviceId.equals(other.serviceId))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			return true;
		}
		
	}
		
}
