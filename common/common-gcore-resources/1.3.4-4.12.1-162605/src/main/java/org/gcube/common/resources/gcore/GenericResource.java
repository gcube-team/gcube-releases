package org.gcube.common.resources.gcore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.common.resources.gcore.common.AnyMixedWrapper;
import org.w3c.dom.Element;

/**
 * A resource with custom semantics.
 * 
 */
@XmlRootElement(name="Resource")
@XmlType(propOrder={"profile"})
public class GenericResource extends Resource {

	public GenericResource() {
		this.type(Type.GENERIC);
	}
	
	public GenericResource(String id) {
		this();
		this.setId(id);
	}
	
	@XmlElementRef
	private Profile profile;
	
	public Profile profile() {
		return profile;
	};
	
	public Profile newProfile() {
		return profile = new Profile();
	}
	
	@XmlRootElement(name="Profile")
	@XmlType(propOrder={"secondaryType","name","description","body"})
	public static class Profile {
		
		@XmlElement(name="SecondaryType")
		private String secondaryType;	
		
		@XmlElement(name="Name")
		private String name;
		
		@XmlElement(name="Description")
		private String description;
		
		@XmlElementRef
		private Body body;

		public String type() {
			return secondaryType;
		}
		
		public Profile type(String secondaryType) {
			this.secondaryType = secondaryType;
			return this;
		}
		
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

		/**
		 * Returns the body as a DOM document.
		 * @return the body
		 */
		public Element body() {
			return body==null?null:body.root();
		}
		
		/**
		 * Returns the body as a string.
		 * @return the body
		 */
		public String bodyAsString() {
			return body==null?null:body.asString();
		}
		
		public Element newBody() {
			body = new Body();
			return body();
		}
		
		public Profile newBody(String text) {
			body = new Body();
			body.setString(text);
			return this;
		}
		
		@Override
		public String toString() {
			return "[secondaryType=" + secondaryType + ", name=" + name + ", description=" + description
					+ ", body=" + body + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((body == null) ? 0 : body.hashCode());
			result = prime * result + ((description == null) ? 0 : description.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((secondaryType == null) ? 0 : secondaryType.hashCode());
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
			if (body == null) {
				if (other.body != null)
					return false;
			} else if (!body.equals(other.body))
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
			if (secondaryType == null) {
				if (other.secondaryType != null)
					return false;
			} else if (!secondaryType.equals(other.secondaryType))
				return false;
			return true;
		}
		
		
		
	}
	
	@XmlRootElement(name="Body")
	public static class Body extends AnyMixedWrapper{}
	
	@Override
	public String toString() {
		return super.toString()+profile;
	}

}
