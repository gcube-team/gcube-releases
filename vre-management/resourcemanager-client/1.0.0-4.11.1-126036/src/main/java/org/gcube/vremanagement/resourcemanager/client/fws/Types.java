package org.gcube.vremanagement.resourcemanager.client.fws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebFault;

import org.gcube.common.resources.gcore.common.Identity;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class Types {

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AddResourcesParameters{

		@XmlElement(name = "software")
		public SoftwareList softwareList;
		@XmlElement (name= "resources")
		public ResourceList resources;

		@XmlElement (name= "targetScope")
		public String targetScope;

		
		
		public SoftwareList getSoftwareList() {
			return softwareList;
		}



		public void setSoftwareList(SoftwareList softwareList) {
			this.softwareList = softwareList;
		}



		public ResourceList getResources() {
			return resources;
		}



		public void setResources(ResourceList resources) {
			this.resources = resources;
		}



		public String getTargetScope() {
			return targetScope;
		}



		public void setTargetScope(String targetScope) {
			this.targetScope = targetScope;
		}



		@Override
		public String toString() {
			return "AddResourcesParameters [scope=" + softwareList + ", resources="
					+ resources + ", targetScope=" + targetScope + "]";
		}

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class RemoveResourcesParameters{

		@XmlElement(name = "software")
		public SoftwareList softwareList;
		@XmlElement (name= "resources")
		public ResourceList resources;
		@XmlElement (name= "targetScope")
		public String targetScope;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SoftwareList{

		@XmlElement(name="suggestedTargetGHNNames")
		public ArrayList<String> suggestedTargetGHNNames;
		@XmlElement(name="software")
		public ArrayList<PackageItem> software;

	}

	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ResourceList{

		@XmlElement(name="resource")
		public ArrayList<ResourceItem> resource;

		public ArrayList<ResourceItem> getResource() {
			return resource;
		}

		public void setResource(ArrayList<ResourceItem> resource) {
			this.resource = resource;
		}
		
		

	}

	
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PackageItem{

		@XmlElement(name="ServiceClass")
		public String serviceClass;
		@XmlElement(name="ServiceName")
		public String serviceName;
		@XmlElement(name="ServiceVersion")
		public String serviceVersion;
		@XmlElement(name="PackageName")
		public String packageName;
		@XmlElement(name="PackageVersion")
		public String packageVersion;
		@XmlElement(name="TargetGHNName")
		public String targetGHNName;	  

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ResourceItem{

		@XmlElement(name="ID")
		public String id;
		@XmlElement(name="Type")
		public String type;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class OptionsParameters{

		@XmlElement(name="targetScope")
		public String targetScope;

		@XmlElement(name="scopeOptionList")
		public ArrayList<ScopeOption> scopeOptionList;


	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ScopeOption{

		@XmlElement
		public String name;
		@XmlElement
		public String value;

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class CreateScopeParameters{

		@XmlElement
		public String targetScope;
		@XmlElement
		public String serviceMap;

//		@XmlElement(name="optionParameters")
		@XmlElementRef
		public OptionsParameters optionParameters;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SendReportParameters{

		@XmlElement
		public String callbackID;

		@XmlElement
		public String report;

		@XmlElement
		public String targetScope;
	}

	@WebFault(name="InvalidOptionsFault")
	public static class InvalidOptionsFault extends RuntimeException {

		public InvalidOptionsFault(String s) {
			super(s);
		}
	}


	@WebFault(name="InvalidScopeFault")
	public static class InvalidScopeFault extends RuntimeException {

		public InvalidScopeFault(String s) {
			super(s);
		}
	}

	@WebFault(name="NoSuchReportFault")
	public static class NoSuchReportFault extends RuntimeException {

		public NoSuchReportFault(String s) {
			super(s);
		}
	}

	@WebFault(name="ResourcesCreationFault")
	public static class ResourcesCreationFault extends RuntimeException {

		public ResourcesCreationFault(String s) {
			super(s);
		}
	}

	@WebFault(name="ResourcesRemovalFault")
	public static class ResourcesRemovalFault extends RuntimeException {

		public ResourcesRemovalFault(String s) {
			super(s);
		}
	}


}
