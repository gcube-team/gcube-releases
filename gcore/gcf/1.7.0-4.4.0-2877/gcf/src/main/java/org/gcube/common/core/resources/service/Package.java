package org.gcube.common.core.resources.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.resources.common.PlatformDescription;


public class Package {

	public static enum ScopeLevel {NONE,GHN,VRE,VO};
	
	public static enum MavenCoordinate {groupId,artifactId,version,classifier};
	
	protected String description;
	
	/**
	 * The name of the package.
	 */
	private String name;

	/**
	 * The package version.
	 */
	private String version;

	/**
	 * The install scripts associated with the package.
	 */
	private List<String> installScripts=new ArrayList<String>();

	/**
	 * The uninstall scripts associated with the package.
	 */
	private List<String> uninstallScripts=new ArrayList<String>();

	/**
	 * The reboot scripts associated with the package.
	 */
	private List<String> rebootScripts=new ArrayList<String>();

	/**
	 * The package dependencies
	 */
	private List<Dependency> dependencies=new ArrayList<Dependency>();;

	/**
	 * The level of scope at which the package is mandatory.
	 */
	private ScopeLevel mandatoryLevel;

	/**
	 * The level of scope at which the package is shareable.
	 */
	private ScopeLevel sharingLevel= ScopeLevel.VO;

	/**
	 * The GHN requirements for the package. 
	 */
	private List<GHNRequirement> requirements=new ArrayList<GHNRequirement>();

	
	/**
	 * Indicates whether the package can coexist with previous versions on the same GHN.
	 */
	private Boolean multiVersion;
	

	/**
	 * Package-specific data.
	 */
	private String specificData;


	/**
	 *  the target platform
	 */
	private PlatformDescription targetPlatform;
	
	/**
	 * Coordinates of the artifact in a Maven Repository
	 */
	protected Map<MavenCoordinate,String> coordinates = new HashMap<MavenCoordinate, String>();
	
	/**
	 * Returns the name of the package.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the package dependencies dependencies.
	 * @return the dependencies.
	 */
	public List<Dependency> getDependencies() {
		return this.dependencies;
	}


	/**
	 * Returns the GHN requirements for the package.
	 * @return the requirements.
	 */
	public List<GHNRequirement> getGHNRequirements() {
		return this.requirements;
	}


	/**
	 * Returns the names of the install scripts associated with the package.
	 * @return the names.
	 */
	public List<String> getInstallScripts() {
		return this.installScripts;
	}

	/**
	 * Indicates whether the package can coexist with previous versions on the same GHN.
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 */
	public Boolean getMultiVersion() {
		return this.multiVersion;
	}
	
	/**
	 * Indicates whether the package can coexist with previous versions on the same GHN. 
	 * @param support <code>true</code> if it does, <code>false</code> otherwise.
	 */
	public void setMultiVersion(Boolean support) {
		this.multiVersion = support;
	}

	/**
	 * Returns the names of the reboot scripts associated with the package.
	 * @return the names.
	 */
	public List<String> getRebootScripts() {
		return this.rebootScripts;
	}

	/**
	 * Returns the names of the uninstall scripts associated with the package.
	 * @return the names.
	 */
	public List<String> getUninstallScripts() {
		return this.uninstallScripts;
	}

	/**
	 * Returns the package version.
	 * @return the version.
	 */
	public String getVersion() {
		return this.version;
	}


	/**
	 * Returns the level of scope at which the package is mandatory.
	 * @return the scope level.
	 */
	public ScopeLevel getMandatoryLevel() {
		return this.mandatoryLevel;
	}
	
	/**
	 * Sets the level of scope at which the package is mandatory.
	 * @param level the scope level
	 */
	public void setMandatoryLevel(ScopeLevel level) {
		this.mandatoryLevel = level;
	}

	/**
	 * Returns the level of scope at which the package can be shared.
	 * @return the level of scope.
	 */
	public ScopeLevel getSharingLevel() {
		return this.sharingLevel;
	}

	/**
	 * Sets the level of scope at which the package can be shared.
	 * @param level the scope level
	 */
	public void setSharingLevel(ScopeLevel level) {
		this.sharingLevel = level;
	}

	public void setGHNRequirements(List<GHNRequirement> arg0) {
		this.requirements = arg0;
	}


	public void setName(String arg0) {
		this.name = arg0;
	}

	/**
	 * Sets the maven coordinates of the package
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @param classifier
	 */
	public void setMavenCoordinates(String groupId, String artifactId, String version, String...classifier) {
		this.coordinates.put(MavenCoordinate.groupId, groupId);
		this.coordinates.put(MavenCoordinate.artifactId, artifactId);
		this.coordinates.put(MavenCoordinate.version, version);
		if (classifier != null && classifier.length > 0 && classifier[0]!=null)
			this.coordinates.put(MavenCoordinate.classifier, classifier[0]);
	}
	
	/**
	 * Gets a coordinate's value
	 * @param name the name
	 * @return the value
	 */
	public String getMavenCoordinate(MavenCoordinate name) {
		return this.coordinates.get(name);
	}
	
	public void setUninstallScripts(List<String> scripts) {
		this.uninstallScripts=scripts;
	}

	public void setVersion(String version) {
		if (version!=null) 
			this.version = version;// Version.completeVersion(version);					
		else	
			throw new IllegalArgumentException("Attempt to change version of gCube package with an invalid version " + version);
	}

	/**
	 * Sets the install scripts associated with the package.
	 * @param installScripts the scripts.
	 */
	public void setInstallScripts(List<String> installScripts) {
		this.installScripts = installScripts;
	}

	/**
	 * Sets the reboot scripts associated with the package
	 * @param rebootScripts the scripts.
	 */
	public void setRebootScripts(List<String> rebootScripts) {
		this.rebootScripts = rebootScripts;
	}

	/**
	 * Returns package-specific data.
	 * @return the data.
	 */
	public String getSpecificData() {
		return this.specificData;
	}
	
	/**
	 * Sets package-specific data.
	 * @param data the data.
	 */
	public void setSpecificData(String data) {
		this.specificData = data;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param targetPlatform the targetPlatform to set
	 */
	public void setTargetPlatform(PlatformDescription targetPlatform) {
		this.targetPlatform = targetPlatform;
	}

	/**
	 * @return the targetPlatform
	 */
	public PlatformDescription getTargetPlatform() {
		return targetPlatform;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		
		final Package other = (Package) obj;
		
		if (specificData == null) {
			if (other.specificData != null)
				return false;
		} else if (! specificData.equals(other.specificData))
			return false;
		
		if (dependencies == null) {
			if (other.dependencies != null)
				return false;
		} else if (! dependencies.equals(other.dependencies))
			return false;
		
		if (requirements == null) {
			if (other.requirements != null)
				return false;
		} else if (! requirements.equals(other.requirements))
			return false;
		
		if (installScripts == null) {
			if (other.installScripts != null)
				return false;
		} else if (! installScripts.equals(other.installScripts))
			return false;
		
		if (multiVersion == null) {
			if (other.multiVersion != null)
				return false;
		} else if (! multiVersion.equals(other.multiVersion))
			return false;
		
		if (rebootScripts == null) {
			if (other.rebootScripts != null)
				return false;
		} else if (! rebootScripts.equals(other.rebootScripts))
			return false;
		
		if (uninstallScripts == null) {
			if (other.uninstallScripts != null)
				return false;
		} else if (! uninstallScripts.equals(other.uninstallScripts))
			return false;
		
		if (mandatoryLevel == null) {
			if (other.mandatoryLevel != null)
				return false;
		} else if (! mandatoryLevel.equals(other.mandatoryLevel))
			return false;
		
		if (sharingLevel == null) {
			if (other.sharingLevel != null)
				return false;
		} else if (! sharingLevel.equals(other.sharingLevel))
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
		
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (! description.equals(other.description))
			return false;
		
		
		return true;
	}


	public static class GHNRequirement {

		protected Category category;
		protected String scategory;
		protected OpType operator;
		protected String requirement;
		protected String value;
		protected String key;		
		public Category getCategory() {return category;}
		@Deprecated
		public void setCategory(String category) {this.scategory = category;}
		public void setCategory(Category category) {
			this.category = category;
		}
		public void setKey(String key) {this.key = key;}
		public String getKey() {return key;}		
		public OpType getOperator() {return operator;}
		public void setOperator(OpType operator) {this.operator = operator;}
		public String getRequirement() {return requirement;}
		public void setRequirement(String requirement) {this.requirement = requirement;}
		public String getValue() {return value;}
		public void setValue(String value) {this.value = value;}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			final GHNRequirement other = (GHNRequirement) obj;
			
			if (category == null) {
				if (other.category != null)
					return false;
			} else if (! category.equals(other.category))
				return false;
			
			if (operator == null) {
				if (other.operator != null)
					return false;
			} else if (! operator.equals(other.operator))
				return false;
			
			if (requirement == null) {
				if (other.requirement != null)
					return false;
			} else if (! requirement.equals(other.requirement))
				return false;
			
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (! value.equals(other.value))
				return false;
			
			
			return true;
		}
		
		public static enum Category {
			 MEM_RAM_AVAILABLE,
			 MEM_RAM_SIZE,
			 MEM_VIRTUAL_AVAILABLE,
			 MEM_VIRTUAL_SIZE,
			 HOST,
			 OS,
			 DISK_SPACE,
			 LOAD1MIN,
			 LOAD5MIN,
			 LOAD15MIN,
			 PLATFORM,
			 PROCESSOR_NUM,
			 PROCESSOR_BOGOMIPS,
			 SITE_LOCATION,
			 SITE_COUNTRY,
			 SITE_DOMAIN,
			 RUNTIME_ENV_STRING,
			 RUNTIME_ENV_NUMBER;
			 
			 public static boolean hasValue(String v) {
					for (Category c: Category.values()) {
						if (c.toString().compareTo(v)==0) {
							return true;
						}
					}
					return false;
				}
		}
		
		public static enum OpType {
			
			EQ("eq"),EXIST("exist"),GE("ge"),GT("gt"),LE("le"),LT("lt"),NE("ne"),CONTAINS("contains");

			private final String value;
			OpType(String v) {value = v;}
			public String value() {return value;}
			public static OpType fromValue(String v) {
				for (OpType c: OpType.values()) {
					if (c.value.equals(v)) {
						return c;
					}
				}
				throw new IllegalArgumentException(v.toString());
			}

		}
	}
}

