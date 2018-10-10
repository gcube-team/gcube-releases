package org.gcube.vremanagement.vremodeler.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.util.ServicePair;

public abstract class GCUBEGenericFunctionalityResource extends GCUBEResource{
	
		private Body body;

		public Body getBody() {
			return body;
		}

		public void setBody(Body body) {
			this.body = body;
		}

		/**
		 * The type of the resource.
		 */
		public static final String TYPE="GenericResource";
		
		//in the following, some of the most common used secondary types
		/**
		 * Secondary type used for transformation programs
		 */
		public static final String SECONDARYTYPE_TP="TransformationProgram";
		
		/**
		 * Secondary type used for VREs 
		 */
		public static final String SECONDARYTYPE_VRE="VRE";
		
		/**
		 * Secondary type used for VOs 
		 */
		public static final String SECONDARYTYPE_VO="VO";
		
		/**
		 * Secondary type used for INFRASTRUCTUREs 
		 */
		public static final String SECONDARYTYPE_INFRASTRUCTURE="INFRASTRUCTURE";
		
		/**
		 * Secondary type used for user profiles 
		 */
		public static final String SECONDARYTYPE_USERPROFILE="UserProfile";
		
		/**
		 * Secondary type used for IndexDefinition 
		 */
		public static final String SECONDARYTYPE_INDEXDEFINITION="IndexDefinition";
		
		/**
		 * Secondary type used for search configuration
		 */
		public static final String SECONDARYTYPE_SEARCHCONFIG="SearchConfiguration";
		
		/**
		 * Secondary type used for portlet configuration
		 */
		public static final String SECONDARYTYPE_PORTLETCONFIG="PortletConfiguration";
		
		/**
		 * Secondary type used for grid resources
		 */
		public static final String SECONDARYTYPE_GRIDRESOURCE="GridResource";
		
		
		public GCUBEGenericFunctionalityResource() {
			this.type = TYPE;
		}
		
		private String name;
		
		private String description;
		
		private String secondaryType;

		/**
		 * Sets the sercondary type
		 * @return the secondaryType
		 */
		public String getSecondaryType() {
			return secondaryType;
		}

		/**
		 * Gets the secondary type
		 * @param secondaryType the secondaryType to set
		 */
		public void setSecondaryType(String secondaryType) {
			this.secondaryType = secondaryType;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			if (!super.equals(obj)) return false;
			
			final GCUBEGenericFunctionalityResource other = (GCUBEGenericFunctionalityResource) obj;
			
			if (body == null) {
				if (other.body != null)
					return false;
			} else if (! body.equals(other.body))
				return false;
			
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (! name.equals(other.name))
				return false;
			
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (! description.equals(other.description))
				return false;
			
			
			return true;
		}
		
		public List<FunctionalityPersisted> fromResourceToPersistedList()	{
			int id=0;
			ArrayList<FunctionalityPersisted> toReturn = new ArrayList<FunctionalityPersisted>();
			for (MainFunctionality mainFunctionality: this.getBody().getMainFunctionalities()){
				FunctionalityPersisted persistedMainFunc = new FunctionalityPersisted(id, mainFunctionality.getName(), mainFunctionality.getDescription(), mainFunctionality.isMandatory());
				persistedMainFunc.setParent(null);
				persistedMainFunc.setFlag(0);
				toReturn.add(persistedMainFunc);
				id++;
				for (Functionality functionality: mainFunctionality.getFunctionalities()){
					FunctionalityPersisted persistedFunc = new FunctionalityPersisted(id, functionality.getName(), functionality.getDescription(), functionality.isMandatory());
					persistedFunc.setParent(persistedMainFunc);
					persistedFunc.setFlag(0);
					logger.trace("FUNCTIONALITY: "+functionality.getName()+" has "+functionality.getServices().size()+" services ");
					ArrayList<ServicePair> servicePairs = new ArrayList<ServicePair>();
					for (Service service: functionality.getServices())
						servicePairs.add(new ServicePair(service.getServiceName(), service.getServiceClass()));
					persistedFunc.setServices(servicePairs);
					persistedFunc.setPortlets(functionality.getPortlets());
					HashSet<ResourceDefinition<?>> selectableResourcesSet= new HashSet<ResourceDefinition<?>>();
					for (ResourceDefinition<?> resource : functionality.getSelectableResources()){
						resource.setId(persistedFunc.getId()+"-"+resource.getDescription().hashCode());
						selectableResourcesSet.add(resource);
					}
					persistedFunc.setSelectableResources(selectableResourcesSet);
					
					HashSet<ResourceDefinition<?>> mandatoryResourcesSet= new HashSet<ResourceDefinition<?>>();
					for (ResourceDefinition<?> resource : functionality.getMandatoryResources())
						mandatoryResourcesSet.add(resource);
					persistedFunc.setMandatoryResources(mandatoryResourcesSet);
					
					toReturn.add(persistedFunc);
					id++;
				}
			}
			return toReturn;
		}
}
