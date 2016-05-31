package org.gcube.smartgears.configuration.container;

import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.utils.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.Validator;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.gcube.smartgears.persistence.Persistence;

/**
 * The configuration of the container.
 *  
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@XmlRootElement(name="container")
public class ContainerConfiguration {

	
	@XmlAttribute
	private Mode mode = Mode.online;
	
	@XmlElement
	@NotNull
	String hostname;
	
	@XmlElement
	@NotNull
	Integer port;

	@XmlElement
	@NotNull
	String infrastructure;

	@XmlElement
	@NotNull @IsValid
	Site site;
	
	@XmlElement(name="vo") 
	List<String> vos = new ArrayList<String>();

	@XmlElementRef(type=DefaultApplicationConfiguration.class)
	List<ApplicationConfiguration> apps = new ArrayList<ApplicationConfiguration>();

	@XmlElement(name="property") 
	@IsValid
	List<Property> properties = new ArrayList<Property>();
	
	@XmlElement(name="publication-frequency")
	long publicationFrequency = default_container_publication_frequency;
	
	@XmlElementRef(type=DefaultPersistence.class)
	@IsValid
	private Persistence persistenceManager;
	
	/**
	 * Returns the management mode for the container.
	 * @return the management mode
	 */
	public Mode mode() {
		return mode;
	}
	
	/**
	 * Sets the management mode for the container.
	 * @param mode the management mode
	 * @return this configuration
	 */
	public ContainerConfiguration mode(Mode mode) {
		this.mode=mode;
		return this;
	}
	
	/**
	 * Returns the application configurations included in this configuration.  
	 * @return the application configurations
	 */
	public List<ApplicationConfiguration> apps() {
		return apps;
	}
	
	/**
	 * Returns the configuration of an application with a given context path.
	 * @param context the context path
	 * @return the application configuration
	 */
	public ApplicationConfiguration app(String context) {
				
		for (ApplicationConfiguration app : apps)
			if (context.equals(app.context()))
				return app;
		
		return null;
	}
	
	/**
	 * Adds the configuration of an application to this configuration.
	 * @param app the application configuration
	 * @return this configuration
	 */
	public synchronized ContainerConfiguration app(ApplicationConfiguration app) {
		int indexToRemove =-1;
		int index =0;
		for (ApplicationConfiguration application : apps){
			if (app.context().equals(application.context()))
				indexToRemove = index;
			index++;
		}
		if(indexToRemove!=-1)
			apps.remove(indexToRemove);
		apps.add(app);
		return this;
	}

	/**
	 * Returns the geographical site of the container.
	 * @return the site
	 */
	public Site site() {
		return site;
	}

	/**
	 * Sets the geographical site of the container.
	 * @param site the site
	 * @return this configuration
	 */
	public ContainerConfiguration site(Site site) {
		this.site=site;
		return this;
	}
	
	/**
	 * Returns the infrastructure in which the container is running.
	 * @return the infrastructure
	 */
	public String infrastructure() {
		return infrastructure;
	}

	/**
	 * Sets the infrastructure in which the container is running.
	 * @param infrastructure the infrastructure
	 * @return this configuration
	 */
	public ContainerConfiguration infrastructure(String infrastructure) {
		this.infrastructure=infrastructure;
		return this;
	}
	
	/**
	 * Returns the host name of the container.
	 * @return the host name;
	 */
	public String hostname() {
		return hostname;
	}
	
	/**
	 * Sets the host name of the container.
	 * @param name the host name
	 * @return this configuration
	 */
	public ContainerConfiguration hostname(String name) {
		this.hostname=name;
		return this;
	}
	
	/**
	 * Returns the port at which the container is listening for requests.
	 * @return the port
	 */
	public int port() {
		return port;
	}
	
	/**
	 * Sets the port at which the container is listening for requests.
	 * @param port the port
	 * @return this configuration
	 */
	public ContainerConfiguration port(int port) {
		this.port=port;
		return this;
	}
	
	/**
	 * Returns the VOs in which the container initially operates.
	 * @return the VOs
	 */
	public List<String> startVOs() {
		return vos;
	}

	/**
	 * Sets the VOs in which the container initially operates.
	 * @param vos the VOs
	 * @return this configuration
	 */
	public ContainerConfiguration startVOs(String ... vos) {
		
		notNull("start VOs",vos);
		
		if (vos!=null && vos.length>0)
			this.vos = Arrays.asList(vos);
		
		return this;
	}
	
	
	/**
	 * Returns the scopes in which the container initially operates.
	 * @return the start scopes
	 */
	public Set<String> startScopes() {

		Set<String> scopes = new HashSet<String>();
		scopes.add("/"+infrastructure());
		for (String vo : startVOs())
			scopes.add("/"+infrastructure()+"/"+vo);
		return scopes;
	}
	
	/**
	 * Returns the persistence manager of the container.
	 * @return the manager
	 */
	public Persistence persistence() {
		return persistenceManager;
	}
	
	/**
	 * Sets the persistence manager of the container.
	 * @param manager the manager
	 * @return this configuration
	 */
	public ContainerConfiguration persistence(Persistence manager) {
		this.persistenceManager=manager;
		return this;
	}
	
	/**
	 * Returns the configuration properties of the container.
	 * @return the properties
	 */
	public Map<String,String> properties() {
		Map<String,String> map = new HashMap<String, String>();
		for (Property prop : properties)
			map.put(prop.name, prop.value);
		return map;
	}

	/**
	 * Adds a configuration property to the container.
	 * @param the name of the property
	 * @param the value of the property
	 * @return this configuration
	 */
	public ContainerConfiguration property(String name, String value) {
		properties.add(new Property(name, value));
		return this;
	}
	
	/**
	 * Returns the publication frequency for the container's profile.
	 * @return the frquency;
	 */
	public long publicationFrequency() {
		return publicationFrequency;
	}
	
	/**
	 * Sets the publication frequency for the container's profile.
	 * @param frequency the frequency
	 * @return this configuration
	 */
	public ContainerConfiguration publicationFrequency(long frequency) {
		this.publicationFrequency=frequency;
		return this;
	}
	
	
	
	
	/**
	 * Validates this configuration
	 * 
	 * @throws IllegalStateException if the configuration is invalid
	 */
	public void validate() {

		List<String> msgs = new ArrayList<String>();

		Validator validator = ValidatorFactory.validator();
		
		for (ValidationError error : validator.validate(this))
			msgs.add(error.toString());

		if (!msgs.isEmpty())
			throw new IllegalStateException("invalid configuration: "+msgs);

	}

	
	
	static class Property {
		
		@XmlAttribute @NotNull
		String name;
		
		@XmlAttribute @NotNull
		String value;
		
		Property() {}
		
		Property(String key, String value) {
			this.name=key;
			this.value=value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			Property other = (Property) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		
		
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apps == null) ? 0 : apps.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((infrastructure == null) ? 0 : infrastructure.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((persistenceManager == null) ? 0 : persistenceManager.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + (int) (publicationFrequency ^ (publicationFrequency >>> 32));
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result + ((vos == null) ? 0 : vos.hashCode());
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
		ContainerConfiguration other = (ContainerConfiguration) obj;
		if (apps == null) {
			if (other.apps != null)
				return false;
		} else if (!apps.equals(other.apps))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (infrastructure == null) {
			if (other.infrastructure != null)
				return false;
		} else if (!infrastructure.equals(other.infrastructure))
			return false;
		if (mode != other.mode)
			return false;
		if (persistenceManager == null) {
			if (other.persistenceManager != null)
				return false;
		} else if (!persistenceManager.equals(other.persistenceManager))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (publicationFrequency != other.publicationFrequency)
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		if (vos == null) {
			if (other.vos != null)
				return false;
		} else if (!vos.equals(other.vos))
			return false;
		return true;
	}



	

		
}