package org.gcube.common.resources.gcore;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 * Partial implementation for resource classes.
 *
 */
@XmlType(propOrder={"id","type","scopes"})
public abstract class Resource {

	//default resource type version
	private static String CURRENT_VERSION = "0.4.x";


	protected transient Lock lock = new ReentrantLock();
	
	/**
	 * The type of the resource
	 */
	public static enum Type {
		
		GENERIC{public String toString() {return "GenericResource"; }},
		ENDPOINT{public String toString() {return "RuntimeResource"; }},
		GCOREENDPOINT{public String toString() {return "RunningInstance"; }},
		SOFTWARE{public String toString() {return "Service"; }},
		NODE{public String toString() {return "GHN"; }};
	
	}
	
	//type string values -> enum values
	private static Map<String,Type> types = new HashMap<String, Resource.Type>();
	
	//populates type map
	static {
		for (Type t : Type.values())
			types.put(t.toString(),t);
	}
	
	@XmlElement(name="ID")
	private String id = UUID.randomUUID().toString();

	@XmlElementWrapper(name = "Scopes")
	@XmlElement(name = "Scope")
	private Set<String> scopes = new LinkedHashSet<String>(); //order preserving

	@XmlElement(name="Type")
	private String type;

	@XmlAttribute
	private String version = CURRENT_VERSION;
	
	public ScopeGroup<String> scopes() {
		return new ScopeGroup<String>(scopes,String.class);
	}
	
	protected String addScope(String scope){
		scopes.add(scope);
		return scope;
	}
	
	protected String removeScope(String scope){
		scopes.remove(scope);
		return scope;
	}
	
	public Type type() {
		return typeOf(type);
	}
	
	protected void type(Type t) {
		this.type=t.toString();
	}

	public String version() {
		return version;
	}
	
	protected void version(String version) {
		this.version = version;
	}

	public String id() {
		return id;
	}

	public void setId(String id){
		this.id=id;
	}
	
	abstract protected Object profile();
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" [id=" + id + ", scopes=" + scopes + ", type=" + type + ", version=" + version
				+ ", profile=" + profile() + "]";
	}


	//helper
	private static Type typeOf(String type) {
		return types.get(type);
	}
	
	//before serialisation, we null the optional fields
    void beforeMarshal(Marshaller marshaller) {
    	if (scopes!=null && scopes.isEmpty())
    		scopes=null;
    }
    
    //after serialisation, we reinitialise them
    void afterMarshal(Marshaller marshaller) {
    	if (scopes==null)
    		scopes=new LinkedHashSet<String>();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((scopes == null) ? 0 : scopes.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((profile() == null) ? 0 : profile().hashCode());
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
		Resource other = (Resource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (scopes == null) {
			if (other.scopes != null)
				return false;
		} else if (!scopes.equals(other.scopes))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (profile() == null) {
			if (other.profile() != null)
				return false;
		} else if (!profile().equals(other.profile())){
			return false;
		}
		return true;
	}

	
}
