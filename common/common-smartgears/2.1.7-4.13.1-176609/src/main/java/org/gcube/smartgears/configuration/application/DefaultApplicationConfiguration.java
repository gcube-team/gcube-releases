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

	@XmlTransient
	Set<String> tokens = new HashSet<String>();
	
	@XmlElement(name="description")
	String description="";
	
	@XmlElementRef
	@IsValid
	ProxyAddress proxyAddress;
	
	@XmlElementRef
	Set<Exclude> excludes= new LinkedHashSet<Exclude>();
	
	@XmlElementRef
	Set<Include> includes= new LinkedHashSet<Include>();
			
	@XmlElementRef(type=DefaultPersistence.class)
	@NotNull @IsValid
	private Persistence persistenceManager;
	
	@Override
	public Set<Exclude> excludes() {
		return excludes;
	}
		
	
	@Override
	public Set<Include> includes() {
		return includes;
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
	
	public ProxyAddress proxyAddress() {
		return proxyAddress;
	}
	
	@Override
	public ApplicationConfiguration excludes(Exclude ... excludes) {
		this.excludes=new HashSet<Exclude>(Arrays.asList(excludes));
		return this;
	}
	
	@Override
	public ApplicationConfiguration includes(Include... includes) {
		this.includes=new HashSet<Include>(Arrays.asList(includes));
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
	public boolean proxied() {
		return proxyAddress!=null;
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
	public ApplicationConfiguration proxyAddress(ProxyAddress proxyaddress) {
		this.proxyAddress = proxyaddress;
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
		
		if (!this.excludes().isEmpty() && !this.includes().isEmpty())
			msgs.add("exclude tags and includes tags are mutually exclusive");
		
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

	
}