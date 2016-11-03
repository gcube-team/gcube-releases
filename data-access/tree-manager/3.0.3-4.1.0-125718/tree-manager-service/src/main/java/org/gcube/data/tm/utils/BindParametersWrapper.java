package org.gcube.data.tm.utils;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.axis.message.MessageElement;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.stubs.BindParameters;
import org.gcube.data.tm.stubs.Payload;
import org.w3c.dom.Element;


/**
 * 
 * The input model for a call to the factory.
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="Parameters", namespace=Constants.NS)
public class BindParametersWrapper {
	
	@XmlTransient 
	private BindParameters stub;

	/**Creates a new instance*/
	BindParametersWrapper() {
		stub = new BindParameters();
	}
	
	/**Creates a new instance*/
	//ex-novo, client usage
	public BindParametersWrapper(String plugin,Element payload) {
		this();
		setPayload(payload);
		setPlugin(plugin);
	}
	
	/**Creates a new instance*/
	//ex-novo, client usage
	public BindParametersWrapper(String plugin,Element payload,boolean broadcast) {
		this(plugin,payload);
		setBroadcast(broadcast);
	}
	
	/**
	 * Creates an instance around a {@link BindParameters}.
	 * @param parameters the parameters
	 */
	//wrapped mode, server usage.
	public BindParametersWrapper(BindParameters parameters) {
		this.stub=parameters;
	}
	
	
	BindParameters toStub() {
		return stub;
	}
	
	/**
	 * Returns the name of the target plugin.
	 * @return the name
	 */
	public String getPlugin() {
		return stub.getPlugin();
	}
	
	/**
	 * 
	 * Sets the name of the target plugin.
	 * @param name the name
	 * @throws IllegalArgumentException if the name is <code>null</code>
	 */
	@XmlElement 
	public void setPlugin(String name) throws IllegalArgumentException {
		if (name==null)
			throw new IllegalArgumentException("null plugin");
		stub.setPlugin(name);
	}

	
	/**
	 * Indicates whether the input is to be broadcast to other running instances.
	 * @return <code>true</code> if it is, <code>false</code>otherwise
	 */
	public boolean isBroadcast() {
		return stub.isBroadcast();
	}
	
	/**
	 * Indicates whether the input is to be broadcast to other running instances.
	 * @param broadcast <code>true</code> if it is, <code>false</code>otherwise
	 */
	@XmlElement 
	public void setBroadcast(boolean broadcast) {
		stub.setBroadcast(broadcast);
	}

	/**
	 * Returns the input specific to the plugin.
	 * @return the input
	 */
	public Element getPayload() {
		try {
			return stub.getPayload().get_any()[0].getAsDOM();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets the input specific to the plugin.
	 * @param payload the input.
	 * @throws IllegalArgumentException if the payload is <code>null</code>
	 */
	@XmlAnyElement 
	public void setPayload(Element payload) {
		if (payload==null)
			throw new IllegalArgumentException("null payload");
		stub.setPayload(new Payload(new MessageElement[]{new MessageElement(payload)}));
	}
	

	
	

}
