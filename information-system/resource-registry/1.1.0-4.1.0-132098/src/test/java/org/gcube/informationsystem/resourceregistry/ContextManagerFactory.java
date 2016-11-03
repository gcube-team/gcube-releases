package org.gcube.informationsystem.resourceregistry;

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
			public String delete(String uuid) throws ContextNotFoundException {
				return "{fake resource}";
			}
		

			@Override
			public String create(String parentContextUUID, String name)
					throws ContextCreationException {
				return parentContextUUID+"newContext-"+name;
			}

			@Override
			public String rename(String contextUUID, String name)
					throws ContextNotFoundException {
				return contextUUID+"-newName:"+name;
			}

			@Override
			public String move(String newParentUUID, String contextToMoveUUID)
					throws ContextNotFoundException {
				return newParentUUID+"-"+contextToMoveUUID;
			}


			@Override
			public String read(String contextUUID)
					throws ContextNotFoundException, ContextException {
				return contextUUID;
			}
		};
		
		
	}

}