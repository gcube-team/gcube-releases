package org.gcube.informationsystem.resourceregistry;

import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.api.EntityManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
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
			public String updateFacet(UUID uuid, String jsonRepresentation)
					throws FacetNotFoundException {
				return null;
			}

			@Override
			public String readResource(UUID uuid)
					throws ResourceNotFoundException {
				return "resource";
			}

			@Override
			public String readFacet(UUID uuid) throws FacetNotFoundException {
				return "facet";
			}

			@Override
			public boolean deleteResource(UUID uuid)
					throws ResourceNotFoundException, ResourceRegistryException {
				return false;
			}

			@Override
			public boolean deleteFacet(UUID uuid)
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
			public String attachResource(UUID sourceResourceUuid,
					UUID targetResourceUuid, String relatedToType,
					String jsonProperties) throws ResourceNotFoundException,
					ResourceRegistryException {
				return null;
			}

			@Override
			public String attachFacet(UUID resourceUuid, UUID facetUuid,
					String consistOfType, String jsonProperties)
					throws FacetNotFoundException, ResourceNotFoundException,
					ResourceRegistryException {
				return null;
			}

			@Override
			public String readFacet(UUID uuid, String facetType)
					throws FacetNotFoundException, ResourceRegistryException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String readResource(UUID uuid, String resourceType)
					throws ResourceNotFoundException, ResourceRegistryException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean detachFacet(UUID consistOfUUID)
					throws ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean detachResource(UUID relatedToUUID)
					throws ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addResourceToContext(UUID uuid)
					throws ResourceNotFoundException, ContextNotFoundException,
					ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addFacetToContext(UUID uuid)
					throws FacetNotFoundException, ContextNotFoundException,
					ResourceRegistryException {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String updateResource(UUID resourceUUID,
					String jsonRepresentation)
					throws ResourceNotFoundException, ResourceRegistryException {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

}
