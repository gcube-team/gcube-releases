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
import javax.xml.bind.annotation.XmlTransient;

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

	@XmlAttribute(name="isSecure")
	private boolean secure = false;
	
	@XmlAttribute(name="context")
	String context;
	
	@XmlAttribute(name="proxied")
	private boolean proxied = true;
	
	@XmlElement(name="name" , required=true)
	@NotNull
	String name;

	@XmlElement(name="group", required=true)
	@NotNull
	String group;

	@XmlElement(name="version", required=true)
	@NotNull
	String version;

	@XmlTransient
	Set<String> tokens = new HashSet<String>();
	
	@XmlElement(name="description")
	String description="";
	
	@XmlElementRef
	Set<Exclude> excludes= new LinkedHashSet<Exclude>();
			
	@XmlElementRef(type=DefaultPersistence.class)
	@NotNull @IsValid
	private Persistence persistenceManager;
	
	@Override
	public Set<Exclude> excludes() {
		return excludes;
	}
	
	public DefaultApplicationConfiguration() {}
	
	@Override
	public Mode mode() {
		return mode;
	}

	@Override
	public boolean secure() {
		return secure;
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
	public ApplicationConfiguration excludes(Exclude ... excludes) {
		this.excludes=new HashSet<Exclude>(Arrays.asList(excludes));
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
	public boolean proxied() {
		return proxied;
	}
	
	@Override
	public ApplicationConfiguration proxied(boolean proxied) {
		this.proxied = proxied;
		return this;
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
	public Set<String> startTokens() {
		return tokens;
	}
	
	@Override
	public ApplicationConfiguration startTokens(Set<String> tokens) {
		this.tokens.addAll(tokens);
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
	public ApplicationConfiguration secure(boolean value) {
		this.secure=value;
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
		
		//scopes.addAll(config.startScopes());
				
	}

	
	
	@Override
	public String toString() {
		return "DefaultApplicationConfiguration [mode=" + mode + ", secure="
				+ secure + ", context=" + context + ", proxied=" + proxied
				+ ", name=" + name + ", group=" + group + ", version="
				+ version + ", tokens=" + tokens + ", description="
				+ description + ", excludes=" + excludes
				+ ", persistenceManager=" + persistenceManager + "]";
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
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
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
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (secure!=other.secure)
			return false;
		return true;
	}

}