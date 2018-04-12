package org.gcube.common.core.resources.service;

import org.gcube.common.core.resources.service.Package.ScopeLevel;

/**
 * Generic dependency among gCube packages
 * 
 * @author Andrea Manzi (ISTI-CNR), Manuele Simi (ISTI-CNR), Luca Frosini (ISTI-CNR)
 */
public class Dependency {

		protected Service service;
		protected String _package;
		protected String version;
		protected ScopeLevel scope=ScopeLevel.GHN;
		protected Boolean optional=true;

		public String getPackage(){return this._package;}
		public void setPackage(String name) {this._package = name;}
		public Boolean getOptional() {return this.optional;}
		public void setOptional(Boolean optional) {this.optional = optional;}
		public Service getService() {return this.service;}
		public void setService(Service service) {this.service = service;}
		public void setScope(ScopeLevel scope) {this.scope=scope;}
		public ScopeLevel getScope() {return this.scope;}
		public String getVersion() {return this.version;}
		public void setVersion(String version) {this.version = version; /*Version.completeVersionRange(version);*/}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Dependency other = (Dependency) obj;
			
			if (scope == null) {
				if (other.scope != null)
					return false;
			} else if (! scope.equals(other.scope))
				return false;
			
			if (optional == null) {
				if (other.optional != null)
					return false;
			} else if (! optional.equals(other.optional))
				return false;
			
			if (_package == null) {
				if (other._package != null)
					return false;
			} else if (! _package.equals(other._package))
				return false;
			
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (! version.equals(other.version))
				return false;
			
			if (service == null) {
				if (other.service != null)
					return false;
			} else if (! service.equals(other.service))
				return false;
			
			
			return true;
		}
		
		public static class Service {


			protected String clazz;
			protected String name;
			protected String version;
			
			public String getClazz() {return clazz;}
			public void setClazz(String clazz) {this.clazz = clazz;}
			public String getName() {return name;}
			public void setName(String name) {this.name = name;}
			public String getVersion() {return version;}
			public void setVersion(String version) {this.version = version; /*Version.completeVersion(version);*/}

			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				
				final Service other = (Service) obj;
				
				if (clazz == null) {
					if (other.clazz != null)
						return false;
				} else if (! clazz.equals(other.clazz))
					return false;
				
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (! name.equals(other.name))
					return false;
				
				if (version == null) {
					if (other.version != null)
						return false;
				} else if (! version.equals(other.version))
					return false;
				
				
				return true;
			}
		}

}
