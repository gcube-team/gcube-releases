package org.gcube.data.access.storagehub;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.gcube.data.access.storagehub.services.ACLManager;
import org.gcube.data.access.storagehub.services.ItemsCreator;
import org.gcube.data.access.storagehub.services.ItemsManager;
import org.gcube.data.access.storagehub.services.WorkspaceManager;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

@Path("workspace")
public class StorageHub extends Application {

	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register resources and features
        classes.add(MultiPartFeature.class);
        classes.add(WorkspaceManager.class);
        classes.add(ItemsManager.class);
        classes.add(ItemsCreator.class);
        classes.add(ACLManager.class);
        return classes;
    }

}
