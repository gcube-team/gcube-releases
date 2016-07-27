package org.gcube.smartgears.configuration.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.gcube.smartgears.persistence.Persistence;

/**
 * The configuration of a managed app.
 * <p>
 * Includes the list of its client services.
 *  
 * @author Fabio Simeoni
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
@XmlRootElement(name="application")
public class DefaultApplicationConfiguration implements ApplicationConfiguration {


	@XmlAttribute
	private Mode mode = Mode.online;

	@XmlAttribute(name="context")
	String context;
	
	@XmlElement(name="name" , required=true)
	@NotNull
	String name;

	@XmlElement(name="group", required=true)
	@NotNull
	String group;

	@XmlElement(name="version", required=true)
	@NotNull
	String version;

	@XmlElement(name="scope")
	Set<String> scopes = new HashSet<String>();
	
	@XmlElement(name="description")
	String description="";
	
	@XmlElement(name="exclude")
	Set<String> excludes= new LinkedHashSet<String>();
	
	@XmlElementRef(type=DefaultPersistence.class)
	@NotNull @IsValid
	private Persistence persistenceManager;
	
	@Override
	public Set<String> excludes() {
		return excludes;
	}
	
	public DefaultApplicationConfiguration() {}
	
	@Override
	public Mode mode() {
		return mode;
	}

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public String context() {
		return context;
	}
	
	@Override
	public ApplicationConfiguration context(String context) {
		this.context=context;
		return this;
	}
	
	@Override
	public ApplicationConfiguration name(String name) {
		this.name=name;
		return this;
	}

	@Override
	public String serviceClass() {
		return group;
	}

	@Override
	public ApplicationConfiguration serviceClass(String group) {
		this.group=group;
		return this;
	}
	
	@Override
	public String version() {
		return version;
	}
	
	@Override
	public ApplicationConfiguration version(String version) {
		this.version=version;
		return this;
	}

	@Override
	public Set<String> startScopes() {
		return scopes;
	}
	
	@Override
	public ApplicationConfiguration startScopes(String... scopes) {
		this.scopes.addAll(Arrays.asList(scopes));
		return this;
	}
	
	@Override
	public String description() {
		return description;
	}
	
	@Override
	public ApplicationConfiguration description(String description) {
		this.description=description;
		return this;
	}

	
	@Override
	public Persistence persistence() {
		return persistenceManager;
	}
	
	@Override
	public ApplicationConfiguration persistence(Persistence manager) {
		this.persistenceManager=manager;
		return this;
	}
	
	@Override
	public ApplicationConfiguration mode(Mode mode) {
		this.mode=mode;
		return this;
	}
	
	@Override
	public void validate() {
		
		List<String> msgs = new ArrayList<String>();
		
		Validator validator = ValidatorFactory.validator();
		
		for (ValidationError error : validator.validate(this))
			msgs.add(error.toString());
		
		if (!msgs.isEmpty())
			throw new IllegalStateException("invalid configuration: "+msgs);
		
	}
	
	
	@Override
	public void merge(ApplicationConfiguration config) {
		
		mode(config.mode());
		
		if (config.persistence()!=null)
			persistence(config.persistence());
		
		scopes.addAll(config.startScopes());
				
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((excludes == null) ? 0 : excludes.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((persistenceManager == null) ? 0 : persistenceManager.hashCode());
		result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		DefaultApplicationConfiguration other = (DefaultApplicationConfiguration) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (excludes == null) {
			if (other.excludes != null)
				return false;
		} else if (!excludes.equals(other.excludes))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		if (mode != other.mode)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (persistenceManager == null) {
			if (other.persistenceManager != null)
				return false;
		} else if (!persistenceManager.equals(other.persistenceManager))
			return false;
		if (scopes == null) {
			if (other.scopes != null)
				return false;
		} else if (!scopes.equals(other.scopes))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

}