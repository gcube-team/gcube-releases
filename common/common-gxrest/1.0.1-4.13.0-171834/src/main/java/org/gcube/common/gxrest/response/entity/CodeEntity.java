package org.gcube.common.gxrest.response.entity;

import javax.ws.rs.core.GenericEntity;

/**
 * An entity to wrap {@link SerializableErrorEntity}s into a {@link WebCodeException}.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class CodeEntity extends GenericEntity<SerializableErrorEntity> {

	/**
	 * @param entity
	 */
	public CodeEntity(SerializableErrorEntity entity) {
		super(entity);
	}

}
