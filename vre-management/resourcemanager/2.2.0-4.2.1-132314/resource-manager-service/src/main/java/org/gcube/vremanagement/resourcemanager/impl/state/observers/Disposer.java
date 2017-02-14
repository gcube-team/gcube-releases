package org.gcube.vremanagement.resourcemanager.impl.state.observers;

import java.io.File;
import org.gcube.vremanagement.resourcemanager.impl.state.PublishedScopeResource;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * Performs scope disposal operations
 * @author manuele simi (CNR)
 *
 */
public class Disposer extends ScopeObserver {

	@Override
	protected void scopeChanged(ScopeState scopeState) {
		if (scopeState.isDisposed()) {
			logger.info("Disposer activated for " + scopeState.getScope().toString());
			boolean isDeleted=false;
			File serialized=Serializer.getSerializationFile(scopeState.getScope());
//Added this part v2.1.0: delete first the backup file and after the serialized state
//	//delete backup state	
//			isDeleted=new File((serialized.getAbsolutePath())+".backup").delete();
//			if (isDeleted){
//				if(logger.isDebugEnabled())
//					logger.debug("The backup serialization State has been deleted: "+serialized.getAbsolutePath()+".backup");
//			}else{
//				logger.warn("The backup serialization State not deleted correctly: "+serialized.getAbsolutePath()+".backup");
//			}
	//delete serialized state		
			isDeleted=serialized.delete();
			if (isDeleted){
				if(logger.isDebugEnabled())
					logger.debug("The serialization State has been deleted: "+serialized.getAbsolutePath());
			}
	//remove the resource from the IS
			try {
				PublishedScopeResource.getResource(scopeState.getScope()).dismiss();
				logger.debug("The resource has been deleted from IS ");
			} catch (Exception e) {
				logger.error("Unable to delete the resource from the IS for " + scopeState.getScope().toString(),e);
			}

		}
	}

}
