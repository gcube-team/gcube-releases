package org.gcube.informationsystem.resourceregistry;

import java.util.UUID;

import org.gcube.informationsystem.resourceregistry.api.ContextManagement;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextCreationException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.glassfish.hk2.api.Factory;

public class ContextManagerFactory implements Factory<ContextManagement> {

	@Override
	public void dispose(ContextManagement arg0) {		
	}

	@Override
	public ContextManagement provide() {
		return new ContextManagement() {
			
			@Override
			public boolean delete(UUID uuid) throws ContextNotFoundException {
				return false;
			}
		

			@Override
			public String create(UUID parentContextUUID, String name)
					throws ContextCreationException {
				return "{}";
			}

			@Override
			public String rename(UUID contextUUID, String name)
					throws ContextNotFoundException {
				return "{}";
			}

			@Override
			public String move(UUID newParentUUID, UUID contextToMoveUUID)
					throws ContextNotFoundException {
				return "{}";
			}


			@Override
			public String read(UUID contextUUID)
					throws ContextNotFoundException, ContextException {
				return "{}";
			}

		};
		
		
	}

}