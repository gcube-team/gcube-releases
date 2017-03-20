package org.gcube.common.core.resources;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.node.Description;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.scope.GCUBEScope.Type;

public abstract class GCUBEHostingNode extends GCUBEResource {

	/**
	 * The type of the resource.
	 */
	public static final String TYPE="GHN";
	
	private static final String VERSION = "1.3.0";

    /**The file prefix of service map files.*/
    public static final String MAP_PREFIX="ServiceMap_";
	
	private String infrastructure;	
	private List<Package> packages=new ArrayList<Package>();
	private Site site;
	private Description nodeDescription;

	public GCUBEHostingNode() {this.type = TYPE;this.logger.setPrefix(TYPE);}
	public Description getNodeDescription() {return nodeDescription;}
	public void setNodeDescription(Description description) {this.nodeDescription = description;}
	public String getInfrastructure() {return infrastructure;}
	public void setInfrastructure(String description) {this.infrastructure = description;}
	public List<Package> getDeployedPackages() {return packages;}
	public Site getSite() {return this.site;}
	public void setSite(Site site) {this.site=site;}		
	public String getLastResourceVersion() {return VERSION;}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if (!super.equals(obj)) return false;
		
		final GCUBEHostingNode other = (GCUBEHostingNode) obj;
		
		if (nodeDescription == null) {
			if (other.nodeDescription != null)
				return false;
		} else if (! nodeDescription.equals(other.nodeDescription))
			return false;
		
		if (infrastructure == null) {
			if (other.infrastructure != null)
				return false;
		} else if (! infrastructure.equals(other.infrastructure))
			return false;
		
		if (packages == null) {
			if (other.packages != null)
				return false;
		} else if (! packages.equals(other.packages))
			return false;
		
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (! site.equals(other.site))
			return false;
		
		
		return true;
	}

	public static class Site {
		
		String location, country, latitude, longitude, domain;

		public String getLocation() {return location;}
		public void setLocation(String location) {this.location = location;}
		public String getCountry() {return country;}
		public void setCountry(String country) {this.country = country;}
		public String getLatitude() {return latitude;}
		public void setLatitude(String latitude) {this.latitude = latitude;}
		public String getLongitude() {return longitude;}
		public void setLongitude(String longitude) {this.longitude = longitude;}
		public String getDomain() {return domain;}
		public void setDomain(String domain) {this.domain = domain;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final Site other = (Site) obj;
			
			if (latitude == null) {
				if (other.latitude != null)
					return false;
			} else if (! latitude.equals(other.latitude))
				return false;
			
			if (longitude == null) {
				if (other.longitude != null)
					return false;
			} else if (! longitude.equals(other.longitude))
				return false;
			
			if (domain == null) {
				if (other.domain != null)
					return false;
			} else if (! domain.equals(other.domain))
				return false;
			
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (! location.equals(other.location))
				return false;
			
			if (country == null) {
				if (other.country != null)
					return false;
			} else if (! country.equals(other.country))
				return false;
			
			
			return true;
		}
	}
	
	
	/** {@inheritDoc} */
	@Override public synchronized Set<GCUBEScope> validateAddScopes(GCUBEScope ... scopes) {
		Set<GCUBEScope> validScopes = super.validateAddScopes(scopes);
		
		GCUBEScope infrastructure = null;
		if(this.getScopes().size()==0) {//if first add, derive infrastructure from first new scope
			infrastructure = scopes[0].getInfrastructure();
			validScopes.add(infrastructure);
		}
		else infrastructure = this.getScopes().values().iterator().next().getInfrastructure();//derive infrastructure from (first) existing scope
		
		for (GCUBEScope scope : scopes) 
			if (scope.getInfrastructure().equals(infrastructure)) // must all have same root VO
				switch (scope.getType()) {//add only subVOs
					case VRE :validScopes.add(scope.getEnclosingScope()); validScopes.add(scope);
					case VO: validScopes.add(scope);
					case INFRASTRUCTURE:; //nothing to do
				}
			else logger.warn("Cannot add "+scope+" to "+this.getClass().getSimpleName()+"("+this.getID()+") because outside the scope of the current infrastructure");
		
		// if this is not the GHN profile of the local GHN, there is no need to load the service maps
		if ( (GHNContext.getContext().getGHNID() != null) && (GHNContext.getContext().getGHNID().compareToIgnoreCase(this.getID()) != 0))
			return validScopes;
		
		Set<GCUBEScope> toremoveScopes = new HashSet<GCUBEScope>();
		for (GCUBEScope scope : validScopes) {
			File mapFile =null;
		    try {
		    	ServiceMap map = new ServiceMap();
		    	logger.trace("Loading service map for scope " + scope.getName());
		    	mapFile= this.getMapFile(scope);
		    	map.load(new FileReader(mapFile));
				scope.setServiceMap(map);
			} catch (Exception e) {
				logger.warn("Cannot add "+scope+" to "+this.getClass().getSimpleName()+"("+this.getID()+") because did not find the service map file "+mapFile.getPath(), e);
				toremoveScopes.remove(scope);
			}		    
		}
		validScopes.removeAll(toremoveScopes);
		return validScopes;
	}
	
	/**
	 * Utility method to return the map file for a given scope;
	 * @param scope the scope.
	 * @return the file.
	 */
	public File getMapFile(GCUBEScope scope) {
		
		if (scope.getType() == Type.VRE)
			return GHNContext.getContext().getFile(MAP_PREFIX + scope.getEnclosingScope().getName() + ".xml");
		
		return GHNContext.getContext().getFile(MAP_PREFIX + scope.getName() + ".xml");
		}

	/** {@inheritDoc} */
	@Override public synchronized Set<GCUBEScope> validateRemoveScopes(GCUBEScope ... scopes) {
		Set<GCUBEScope> set = new HashSet<GCUBEScope>();
		for (GCUBEScope scope : scopes)	//no infrastructures please
			if (!scope.isInfrastructure()) set.add(scope); 
			else logger.warn("Cannot remove "+this.getClass().getSimpleName()+"("+this.getID()+") from its infrastructure");
		return set;
	}
	
	public static class Package {
		
		String packageName;
		String packageVersion;
		String serviceName;
		String serviceClass;
		String serviceVersion;
		public String getPackageName() {return packageName;}
		public void setPackageName(String packageName) {this.packageName = packageName;}
		public String getPackageVersion() {return packageVersion;}
		public void setPackageVersion(String packageVersion) {this.packageVersion = packageVersion;}
		public String getServiceName() {return serviceName;}
		public void setServiceName(String serviceName) {this.serviceName = serviceName;}
		public String getServiceClass() {return serviceClass;}
		public void setServiceClass(String serviceClass) {this.serviceClass = serviceClass;}
		public String getServiceVersion() {return serviceVersion;}
		public void setServiceVersion(String serviceVersion) {this.serviceVersion = serviceVersion;}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((packageName == null) ? 0 : packageName.hashCode());
			result = prime
					* result
					+ ((packageVersion == null) ? 0 : packageVersion.hashCode());
			result = prime * result
					+ ((serviceClass == null) ? 0 : serviceClass.hashCode());
			result = prime * result
					+ ((serviceName == null) ? 0 : serviceName.hashCode());
			result = prime
					* result
					+ ((serviceVersion == null) ? 0 : serviceVersion.hashCode());
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
			Package other = (Package) obj;
			if (packageName == null) {
				if (other.packageName != null)
					return false;
			} else if (!packageName.equals(other.packageName))
				return false;
			if (packageVersion == null) {
				if (other.packageVersion != null)
					return false;
			} else if (!packageVersion.equals(other.packageVersion))
				return false;
			if (serviceClass == null) {
				if (other.serviceClass != null)
					return false;
			} else if (!serviceClass.equals(other.serviceClass))
				return false;
			if (serviceName == null) {
				if (other.serviceName != null)
					return false;
			} else if (!serviceName.equals(other.serviceName))
				return false;
			if (serviceVersion == null) {
				if (other.serviceVersion != null)
					return false;
			} else if (!serviceVersion.equals(other.serviceVersion))
				return false;
			return true;
		}
		
	}

}
