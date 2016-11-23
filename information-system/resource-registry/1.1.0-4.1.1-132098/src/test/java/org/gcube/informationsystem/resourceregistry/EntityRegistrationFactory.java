package org.gcube.informationsystem.resourceregistry;

import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.glassfish.hk2.api.Factory;

public class EntityRegistrationFactory implements Factory<SchemaManagement>{

	@Override
	public void dispose(SchemaManagement arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SchemaManagement provide() {
		return new SchemaManagement() {

			@Override
			public String registerEntitySchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getEntitySchema(String entityType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateEntitySchema(String entityType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteEntitySchema(String entityType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerFacetSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getFacetSchema(String facetType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateFacetSchema(String facetType, String jsonSchema)
					throws SchemaNotFoundException, SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteFacetSchema(String facetType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerResourceSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getResourceSchema(String resourceType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateResourceSchema(String resourceType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteResourceSchema(String resourceType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerEmbeddedTypeSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getEmbeddedTypeSchema(String embeddedType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateEmbeddedTypeSchema(String embeddedType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteEmbeddedTypeSchema(String embeddedType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerRelationSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRelationSchema(String relationType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateRelationSchema(String relationType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteRelationSchema(String relationType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerConsistOfSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getConsistOfSchema(String consistOfType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateConsistOfSchema(String consistOfType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteConsistOfSchema(String consistOfType)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String registerRelatedToSchema(String jsonSchema)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRelatedToSchema(String relatedToType)
					throws SchemaNotFoundException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String updateRelatedToSchema(String relatedToType,
					String jsonSchema) throws SchemaNotFoundException,
					SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String deleteRelatedToSchema(String relatedToType)
					throws SchemaException {
				// TODO Auto-generated method stub
				return null;
			}

			
			
		};
	}

}
