package org.gcube.informationsystem.resourceregistry;

import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.glassfish.hk2.api.Factory;

public class EntityManagementFactory implements Factory<EntityManagement> {

	@Override
	public void dispose(EntityManagement arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public EntityManagement provide() {
		return new EntityManagement() {

			@Override
			public String updateFacet(String uuid, String jsonRepresentation)
					throws FacetNotFoundException {
				return null;
			}

			@Override
			public String readResource(String uuid)
					throws ResourceNotFoundException {
				return "resource";
			}

			@Override
			public String readFacet(String uuid) throws FacetNotFoundException {
				return "facet";
			}

			@Override
			public boolean deleteResource(String uuid)
					throws ResourceNotFoundException, ResourceRegistryException {
				return false;
			}

			@Override
			public boolean deleteFacet(String uuid)
					throws FacetNotFoundException, ResourceRegistryException {
				return false;
			}

			@Override
			public String createResource(String resourceType,
					String jsonRepresentation) throws ResourceRegistryException {
				return null;
			}

			@Override
			public String createFacet(String facetType,
					String jsonRepresentation) throws ResourceRegistryException {
				return null;
			}

			@Override
			public String attachResource(String sourceResourceUuid,
					String targetResourceUuid, String relatedToType,
					String jsonProperties) throws ResourceNotFoundException,
					ResourceRegistryException {
				return null;
			}

			@Override
			public String attachFacet(String resourceUuid, String facetUuid,
					String consistOfType, String jsonProperties)
					throws FacetNotFoundException, ResourceNotFoundException,
					ResourceRegistryException {
				return null;
			}

			@Override
			public String readFacet(String uuid, String facetType)
					throws FacetNotFoundException, ResourceRegistryException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String readResource(String uuid, String resourceType)
					throws ResourceNotFoundException, ResourceRegistryException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean detachFacet(String consistOfUUID)
					throws ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean detachResource(String relatedToUUID)
					throws ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

		};
	}

}
