package org.gcube.common.core.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.types.DescriptiveProperty;

/**
 * Partial implementation of the main entry point to the functionality and information
 * included in plugins.
 * 
 * <p>
 * 
 * {@link GCUBEPluginContext} exposes information that is common across all services.
 * This includes:
 * <ul>
 * <li>the plugin profile, which is injected into the context during initialisation;
 * <li> (optional) arbitrary descriptive properties whereby plugins may be identified by their clients;
 * <li> (optional) the mappings xml-element->java-type that may be required to deserialise objects exchanged on the wire 
 * and statically unknown to the service and their clients.
 * </ul>
 * <p>
 * Individual services may subclass it to introduce additional requirements
 * on the contexts of their plugins. Typically, they add properties and type mappings 
 * in their constructors to make the information readily available to both service and clients. 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 */
public abstract class GCUBEPluginContext {
	
	
	/**Plugin descriptive properties.*/
	private Map<String,DescriptiveProperty> properties = new HashMap<String,DescriptiveProperty>(); 
	
	/**Plugin's type mappings.*/
	private List<TypeMapping> mappings = new ArrayList<TypeMapping>();

	/** The resource that models the plugin. */
	private GCUBEService plugin;
	
	/**
	 * Creates an instance for a given plugin.
	 * @param plugin the plugin.
	 * */
	public void initialise(GCUBEService plugin) {this.plugin=plugin;}
	
	/**
	 * Returns the {@link GCUBEService} that models the plugin.
	 * @return the name.
	 */
	public GCUBEService getPlugin() {return this.plugin;}
	
	
	/**
	 * Returns (a read-only copy of) the descriptive properties of the plugin.
	 * @return the properties.
	 */
	public Map<String,DescriptiveProperty> getProperties() {return Collections.unmodifiableMap(properties);}
	
	/**
	 * Adds one or more descriptive properties for the plugin.
	 * @param properties the property. 
	 */
	protected void addProperty(DescriptiveProperty ... properties) {
		if (properties!= null)
			for (DescriptiveProperty property : properties) this.properties.put(property.getName(),property);}
	
	/**
	 * Returns (a read-only copy of) the type mappings required by the plugin, if any.
	 * @return the type mappings.
	 */
	public List<TypeMapping> getTypeMappings() {return Collections.unmodifiableList(mappings);}

	/**
	 * Adds one or more type mappings required by the plugin, if any.
	 * @param mappings the type mappings.
	 */
	protected void addTypeMappings(TypeMapping ... mappings) {
		if (mappings!= null)
			this.mappings.addAll(Arrays.asList(mappings));
	}

	
	/** 
	 * Groups the information required to to serialise and deserialise a Java class.
	 * @author Fabio Simeoni (University of Strathclyde) *
	 */
	public class TypeMapping {
		/** The class to be serialised*/
		public Class<?> clazz;
		/**The {@link QName} of the serialisation. */
		public QName qname;
		/**The factory of serialisers*/
		public SerializerFactory sFactory;
		/** The class of deserialiser.*/
		public DeserializerFactory dfactory;

		/**
		 * Creates a new instance.
		 * @param clazz the class to serialise.
		 * @param qname the {@link QName} of its serialisation.
		 * @param sfaFactory the factory of serialisers.
		 * @param dfactory the factory of deserialisers.
		 */
		public TypeMapping(Class<?> clazz,QName qname, SerializerFactory sfaFactory,DeserializerFactory dfactory) {
			this.clazz = clazz;
			this.dfactory = dfactory;
			this.qname = qname;
			this.sFactory = sfaFactory;
		}

		/**
		 * Creates a new instance using default factories for serialisation and deserialisation.
		 * @param clazz the class to serialise.
		 * @param qname the {@link QName} of its serialisation.
		 */
		public TypeMapping(Class<?> clazz,QName qname) {
			this.clazz = clazz;
			this.dfactory = new BeanDeserializerFactory(clazz,qname);
			this.qname = qname;
			this.sFactory = new BeanSerializerFactory(clazz,qname);
		}

		
		
	}
}
