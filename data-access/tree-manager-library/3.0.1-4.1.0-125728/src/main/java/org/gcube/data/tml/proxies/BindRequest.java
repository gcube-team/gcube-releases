package org.gcube.data.tml.proxies;

import static org.gcube.data.tml.Utils.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.tml.stubs.Types.AnyWrapper;
import org.w3c.dom.Element;

/**
 * 
 * The input model for a call to the factory.
 * 
 * @author Fabio Simeoni
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"broadcast","plugin","payload"})
public class BindRequest {

	private String plugin;

	private boolean broadcast;

	private AnyWrapper payload = new AnyWrapper();

	//for JAXB
	BindRequest() {
	}

	/** Creates a new instance */
	public BindRequest(String plugin) {
		setPlugin(plugin);
	}
	
	/** Creates a new instance */
	public BindRequest(String plugin, Element payload) {
		this(plugin);
		setPayload(payload);
	}

	/** Creates a new instance */
	public BindRequest(String plugin, Element payload, boolean broadcast) {
		this(plugin, payload);
		setBroadcast(broadcast);
	}

	/**
	 * Returns the name of the target plugin.
	 * 
	 * @return the name
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * 
	 * Sets the name of the target plugin.
	 * 
	 * @param name the name
	 * @throws IllegalArgumentException if the name is <code>null</code>
	 */
	public void setPlugin(String name) throws IllegalArgumentException {
		notNull("plugin", name);
		this.plugin = name;
	}

	/**
	 * Indicates whether the input is to be broadcast to other service endpoints.
	 * 
	 * @return <code>true</code> if it is, <code>false</code>otherwise
	 */
	public boolean isBroadcast() {
		return broadcast;
	}

	/**
	 * Indicates whether the input is to be broadcasted to other running instances.
	 * 
	 * @param broadcast <code>true</code> if it is, <code>false</code>otherwise
	 */
	public void setBroadcast(boolean broadcast) {
		this.broadcast = broadcast;
	}

	/**
	 * Returns the input specific to the plugin.
	 * 
	 * @return the input
	 */
	public Element getPayload() {
		return payload.element;
	}

	/**
	 * Sets the input specific to the plugin.
	 * 
	 * @param payload the input.
	 * @throws IllegalArgumentException if the payload is <code>null</code>
	 */
	public void setPayload(Element payload) {
		notNull("payload", payload);
		this.payload = new AnyWrapper(payload);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (broadcast ? 1231 : 1237);
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
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
		BindRequest other = (BindRequest) obj;
		if (broadcast != other.broadcast)
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (plugin == null) {
			if (other.plugin != null)
				return false;
		} else if (!plugin.equals(other.plugin))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BindParams [plugin=" + plugin + ", broadcast=" + broadcast + ", payload=" + payload + "]";
	}

}
