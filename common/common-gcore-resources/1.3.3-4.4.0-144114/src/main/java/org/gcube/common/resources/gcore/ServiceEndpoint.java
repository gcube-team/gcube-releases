package org.gcube.common.resources.gcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.gcube.common.resources.gcore.common.GHNReference;
import org.gcube.common.resources.gcore.common.Platform;
import org.gcube.common.resources.gcore.utils.Group;

/**
 * Describes endpoints of arbitrary services.
 */
@XmlRootElement(name = "Resource")
@XmlType(propOrder={"profile"})
public class ServiceEndpoint extends Resource {

	public ServiceEndpoint() {
		this.type(Type.ENDPOINT);
	}
	
	@XmlElementRef
	private Profile profile;

	public Profile profile() {
		return profile;
	};
	
	public Profile newProfile() {
		return profile = new Profile();
	};

	@XmlRootElement(name="Profile")
	@XmlType(propOrder={"category","name","version","description","platform","runtime","accessPoints"})
	public static class Profile {

		@XmlElement(name = "Category")
		private String category;

		@XmlElement(name = "Name")
		private String name;
		
		@XmlElement(name = "Version")
		private String version;
	
		@XmlElement(name = "Description")
		private String description;

		@XmlElementRef
		private Platform platform;

		@XmlElementRef
		private Runtime runtime;

		@XmlElementRef
		private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

		public String name() {
			return name;
		}
		
		public Profile name(String name) {
			this.name = name;
			return this;
		}

		public String description() {
			return description;
		}
		
		public Profile description(String description) {
			this.description = description;
			return this;
		}

		public String version() {
			return version;
		}
		
		public Profile version(String version) {
			this.version = version;
			return this;
		}

		public String category() {
			return category;
		}

		public Profile category(String category) {
			this.category = category;
			return this;
		}
		
		public Runtime runtime() {
			return runtime;
		}
		
		public Runtime newRuntime() {
			return runtime=new Runtime();
		}

		public Platform platform() {
			return platform;
		}
		
		public Platform newPlatform() {
			return platform = new Platform();
		}

		public Group<AccessPoint> accessPoints() {
			return new Group<AccessPoint>(accessPoints,AccessPoint.class);
		}

		@Override
		public String toString() {
			return "[name=" + name + ", description=" + description + ", version=" + version + ", category="
					+ category + ", runtime=" + runtime + ", platform=" + platform + ", accessPoints=" + accessPoints
					+ "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accessPoints == null) ? 0 : accessPoints.hashCode());
			result = prime * result + ((category == null) ? 0 : category.hashCode());
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((platform == null) ? 0 : platform.hashCode());
			result = prime * result + ((runtime == null) ? 0 : runtime.hashCode());
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
			Profile other = (Profile) obj;
			if (accessPoints == null) {
				if (other.accessPoints != null)
					return false;
			} else if (!accessPoints.equals(other.accessPoints))
				return false;
			if (category == null) {
				if (other.category != null)
					return false;
			} else if (!category.equals(other.category))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (platform == null) {
				if (other.platform != null)
					return false;
			} else if (!platform.equals(other.platform))
				return false;
			if (runtime == null) {
				if (other.runtime != null)
					return false;
			} else if (!runtime.equals(other.runtime))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
	
		
	}

	@XmlRootElement(name = "RunTime")
	@XmlType(propOrder={"hostedOn","ghn","status"})
	public static class Runtime {

		@XmlElement(name = "HostedOn")
		private String hostedOn;

		@XmlElement(name = "GHN")
		private GHNReference ghn = new GHNReference();

		@XmlElement(name = "Status")
		private String status;
		
		public String hostedOn() {
			return hostedOn;
		}
		
		public Runtime hostedOn(String hostedOn) {
			this.hostedOn = hostedOn;
			return this;
		}

		public String status() {
			return status;
		}
		
		public Runtime status(String status) {
			this.status = status;
			return this;
		}

		public String ghnId() {
			return ghn.id;
		}
		
		public Runtime ghnId(String id) {
			this.ghn.id=id;
			return this;
		}

		//before serialisation, we null the optional fields
	    void beforeMarshal(Marshaller marshaller) {
	    	if (ghn!=null && ghn.id==null)
	    		ghn=null;
	    }
	    
	    //after serialisation, we reinitialise them
	    void afterMarshal(Marshaller marshaller) {
	    	if (ghn==null)
	    		ghn = new GHNReference();
	    }
	    
		@Override
		public String toString() {
			return "[hostedOn=" + hostedOn + ", status=" + status + ", ghn=" + ghn + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((ghn == null) ? 0 : ghn.hashCode());
			result = prime * result + ((hostedOn == null) ? 0 : hostedOn.hashCode());
			result = prime * result + ((status == null) ? 0 : status.hashCode());
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
			Runtime other = (Runtime) obj;
			if (ghn == null) {
				if (other.ghn != null)
					return false;
			} else if (!ghn.equals(other.ghn))
				return false;
			if (hostedOn == null) {
				if (other.hostedOn != null)
					return false;
			} else if (!hostedOn.equals(other.hostedOn))
				return false;
			if (status == null) {
				if (other.status != null)
					return false;
			} else if (!status.equals(other.status))
				return false;
			return true;
		}
		
		
	}
	
	@XmlRootElement(name = "AccessData")
	@XmlType(propOrder={"username","password"})
	static class AccessData {

		@XmlElement(name = "Username")
		private String username;

		@XmlElement(name = "Password")
		private String password;
		
		public void set(String password,String username) {
			this.password = password;
			this.username=username;
		}

		@Override
		public String toString() {
			return "[username=" + username + ", password=" + password + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((password == null) ? 0 : password.hashCode());
			result = prime * result + ((username == null) ? 0 : username.hashCode());
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
			AccessData other = (AccessData) obj;
			if (password == null) {
				if (other.password != null)
					return false;
			} else if (!password.equals(other.password))
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}

		
	}

	@XmlRootElement(name = "Endpoint")
	public static class Endpoint {

		@XmlAttribute(name = "EntryName")
		private String name;
		

		@XmlValue
		private String address;
		

		public String name() {
			return name;
		}
		
		public String address() {
			return address;
		}
		
		public void name(String name) {
			this.name = name;
		}
		
		public void address(String address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return "[name=" + name + ", address=" + address + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((address == null) ? 0 : address.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Endpoint other = (Endpoint) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			} else if (!address.equals(other.address))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

	}

	@XmlRootElement(name = "Interface")
	public static class Interface {

		@XmlElementRef
		private Endpoint endpoint = new Endpoint();

		public Endpoint endpoint() {
			return endpoint;
		}
		
		@Override
		public String toString() {
			return "[endpoints=" + endpoint + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
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
			Interface other = (Interface) obj;
			if (endpoint == null) {
				if (other.endpoint != null)
					return false;
			} else if (!endpoint.equals(other.endpoint))
				return false;
			return true;
		}
		
		

	}

	@XmlRootElement(name = "AccessPoint")
	@XmlType(propOrder={"description","itfce","accessData","properties"})
	public static class AccessPoint {

		@XmlElement(name = "Description")
		private String description;

		@XmlElementRef
		private Interface itfce = new Interface();
		
		@XmlElementRef
		private AccessData accessData;

		@XmlElementWrapper(name = "Properties")
		@XmlElementRef
		private List<Property> properties = new ArrayList<Property>();
	    
		//before serialisation, we null the optional fields
	    void beforeMarshal(Marshaller marshaller) {
	    	if (properties!=null && properties.isEmpty())
	    		properties=null;
	    }
	    
	    //after serialisation, we reinitialise them
	    void afterMarshal(Marshaller marshaller) {
	    	if (properties==null)
	    		properties = new ArrayList<Property>();
	    }
	    
		public String description() {
			return description;
		}
		
		public AccessPoint description(String description) {
			this.description = description;
			return this;
		}

		public String username() {
			return accessData.username;
		}
		
		public String password() {
			return accessData.password;
		}
		
		public AccessPoint credentials(String password,String username) {
			accessData = new AccessData();
			accessData.password=password;
			accessData.username=username;
			return this;
		}
		
		public String name() {
			return itfce.endpoint().name();
		}
		
		public AccessPoint name(String address) {
			itfce.endpoint().name(address);
			return this;
		}
		
		public String address() {
			return itfce.endpoint().address();
		}
		
		public AccessPoint address(String address) {
			itfce.endpoint().address(address);
			return this;
		}

		public Group<Property> properties() {
			return new Group<Property>(properties,Property.class);
		}
		
		public Map<String, Property> propertyMap(){
			Map<String, Property> map=new HashMap<String, Property>();
			for (Property p: properties){
				if(p.name()!=null)
			       map.put(p.name(),p);
			}
			return map;
		}

		@Override
		public String toString() {
			return "AccessPoint [description=" + description + ", accessData=" + accessData + ", interface=" + itfce
					+ ", properties=" + properties + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accessData == null) ? 0 : accessData.hashCode());
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((itfce == null) ? 0 : itfce.hashCode());
			result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
			AccessPoint other = (AccessPoint) obj;
			if (accessData == null) {
				if (other.accessData != null)
					return false;
			} else if (!accessData.equals(other.accessData))
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (itfce == null) {
				if (other.itfce != null)
					return false;
			} else if (!itfce.equals(other.itfce))
				return false;
			if (properties == null) {
				if (other.properties != null)
					return false;
			} else if (!properties.equals(other.properties))
				return false;
			return true;
		}
		
		
	}
	

	@XmlRootElement(name="Property")
	@XmlType(propOrder={"name","value"})
	public static class Property {
		
		@XmlElement(name = "Name")
		private String name;
		
		@XmlElement(name = "Value")
		private PropertyValue value = new PropertyValue();
		
		public String name() {
			return name;
		}
		
		public Property nameAndValue(String name,String value) {
			this.name = name;
			this.value.value=value;
			return this;
		}
		
		public boolean isEncrypted() {
			return value.encrypted;
		}
		
		public Property encrypted(boolean encrypted) {
			this.value.encrypted = encrypted;
			return this;
		}
		
		public String value() {
			return value.value;
		}

		@Override
		public String toString() {
			return "Property [name=" + name + ", value=" + value + "]";
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
	
	public static class PropertyValue {
		
		@XmlAttribute(name="encrypted")
		private boolean encrypted;
		
		@XmlValue
		private String value;
		
		
		@Override
		public String toString() {
			return "[encrypted=" + encrypted + ", value=" + value + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (encrypted ? 1231 : 1237);
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
			PropertyValue other = (PropertyValue) obj;
			if (encrypted != other.encrypted)
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
		
		
	}
}
