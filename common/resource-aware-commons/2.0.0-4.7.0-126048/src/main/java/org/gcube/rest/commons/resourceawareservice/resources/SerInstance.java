package org.gcube.rest.commons.resourceawareservice.resources;

import java.net.URI;
import java.util.List;

import org.w3c.dom.Node;

public class SerInstance extends GeneralResource {
	private URI endpoint;
	private String key;
	private String serviceName;
	private String serviceClass;
	private NodeProperties properties;

	/**
	 * @param endpoint
	 * @param key
	 * @param properties
	 */
	public SerInstance(URI endpoint, String key, String serviceName, String serviceClass, NodeProperties properties) {
		super();
		this.endpoint = endpoint;
		this.key = key;
		this.serviceName = serviceName;
		this.serviceClass = serviceClass;
		this.properties = properties;
	}

	/**
	 * @return the endpoint
	 */
	public URI getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint
	 *            the endpoint to set
	 */
	public void setEndpoint(URI endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	/**
	 * @return
	 * @return the prop
	 */
	public NodeProperties getProperties() {
		return properties;
	}

	/**
	 * @param prop
	 *            the prop to set
	 */
	public void setProp(NodeProperties properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "SerInstance [endpoint=" + endpoint + ", key=" + key
				+ ", serviceName=" + serviceName + ", serviceClass="
				+ serviceClass + ", properties=" + properties + "]";
	}

	public static class NodeProperties {
		private String nodeId;
		private List<String> scopes;
		private Node customProperties;

		/**
		 * @param nodeId
		 * @param scope
		 * @param customProperties
		 */
		public NodeProperties(String nodeId, List<String> scopes, Node customProperties) {
			super();
			this.nodeId = nodeId;
			this.scopes = scopes;
			this.customProperties = customProperties;
		}

		/**
		 * @return the nodeId
		 */
		public String getNodeId() {
			return nodeId;
		}

		/**
		 * @param nodeId
		 *            the nodeId to set
		 */
		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}

		/**
		 * @return the scopes
		 */
		public List<String> getScopes() {
			return scopes;
		}

		/**
		 * @param scopes
		 *            the scopes to set
		 */
		public void setScopes(List<String> scopes) {
			this.scopes = scopes;
		}

		/**
		 * @return the customProperties
		 */
		public Node getCustomProperties() {
			return customProperties;
		}

		/**
		 * @param customProperties
		 *            the customProperties to set
		 */
		public void setCustomProperties(Node customProperties) {
			this.customProperties = customProperties;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "NodeProperties [nodeId=" + nodeId + ", scopes=" + scopes + ", customProperties=" + customProperties + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((nodeId == null) ? 0 : nodeId.hashCode());
			result = prime * result
					+ ((scopes == null) ? 0 : scopes.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeProperties other = (NodeProperties) obj;
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
			return true;
		}
		
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerInstance other = (SerInstance) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}
	
	
}