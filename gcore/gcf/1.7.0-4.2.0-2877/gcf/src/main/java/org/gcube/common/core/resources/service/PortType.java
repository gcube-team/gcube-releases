package org.gcube.common.core.resources.service;

import java.util.ArrayList;
import java.util.List;


public class PortType {

	protected String name;
	protected SecurityInfo security;
	protected String wsdl;

	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public SecurityInfo getSecurity() {return security;}
	public void setSecurity(SecurityInfo security) {this.security = security;}
	public String getWsdl() {return wsdl;}
	public void setWsdl(String wsdl) {this.wsdl = wsdl;}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		final PortType other = (PortType) obj;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (! name.equals(other.name))
			return false;
		
		if (security == null) {
			if (other.security != null)
				return false;
		} else if (! security.equals(other.security))
			return false;
		
		if (wsdl == null) {
			if (other.wsdl != null)
				return false;
		} else if (! wsdl.equals(other.wsdl))
			return false;
		
		return true;
	}	
	
	public static class SecurityInfo {

		protected String securityDescriptor;
		protected List<Operation> operations = new ArrayList<Operation>();
		protected List<String> roles=new ArrayList<String>();;
		protected String name;

		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getDescriptor() {return securityDescriptor;}
		public void setDescriptor(String securityDescriptor) {this.securityDescriptor = securityDescriptor;}
		public List<Operation> getOperations() {return operations;}
		public List<String> getRoles() {return roles;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final SecurityInfo other = (SecurityInfo) obj;
			
			if (securityDescriptor == null) {
				if (other.securityDescriptor != null)
					return false;
			} else if (! securityDescriptor.equals(other.securityDescriptor))
				return false;
			
			if (operations == null) {
				if (other.operations != null)
					return false;
			} else if (! operations.equals(other.operations))
				return false;
			
			if (roles == null) {
				if (other.roles != null)
					return false;
			} else if (! roles.equals(other.roles))
				return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			return true;
		}
		
		public static class Operation {

			protected List<String> roles=new ArrayList<String>();
			protected String description;
			protected String id;
			protected String name;

			public String getDescription() {return description;}
			public void setDescription(String description) {this.description = description;}
			public String getId() {return id;}
			public void setId(String id) {this.id = id;}
			public String getName() {return name;}
			public void setName(String name) {this.name = name;}
			public List<String> getRoles() {return this.roles;}
			
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Operation other = (Operation) obj;
				
				if (roles == null) {
					if (other.roles != null)
						return false;
				} else if (! roles.equals(other.roles))
					return false;
				
				if (description == null) {
					if (other.description != null)
						return false;
				} else if (! description.equals(other.description))
					return false;
				
				if (id == null) {
					if (other.id != null)
						return false;
				} else if (! id.equals(other.id))
					return false;
				
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (! name.equals(other.name))
					return false;
				
				return true;
			}

		}
		
	}
	
	public static class Function {


		protected String name;
		protected List<String> formalParameters = new ArrayList<String>();
		protected String body;

		public String getBody() {return body;}
		public void setBody(String body) {this.body = body;}
		public List<String> getFormalParameters() {return formalParameters;}
		public void setFormalParameters(List<String> formalParameters) {this.formalParameters = formalParameters;}
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Function other = (Function) obj;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (formalParameters == null) {
				if (other.formalParameters != null)
					return false;
			} else if (! formalParameters.equals(other.formalParameters))
				return false;
		
			if (body == null) {
				if (other.body != null)
					return false;
			} else if (! body.equals(other.body))
				return false;
			
			return true;
		}

	}
}

