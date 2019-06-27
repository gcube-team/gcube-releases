package org.gcube.datatransfer.resolver;

import javax.ws.rs.Path;

import org.gcube.data.analysis.dminvocation.model.DataMinerInvocation;
import org.gcube.datatransfer.resolver.init.UriResolverSmartGearManagerInit;
import org.gcube.datatransfer.resolver.requesthandler.RequestHandler;
import org.gcube.datatransfer.resolver.services.CatalogueResolver;
import org.gcube.datatransfer.resolver.services.tobackward.BackCatalogueResolver;
import org.gcube.smartgears.annotations.ManagedBy;
import org.glassfish.jersey.server.ResourceConfig;

@Path("uri-resolver")
@ManagedBy(UriResolverSmartGearManagerInit.class)
public class UriResolver extends ResourceConfig {

    public UriResolver() {
        // Register all resources present under the package.
        packages(CatalogueResolver.class.getPackage().getName(), RequestHandler.class.getPackage().getName(), BackCatalogueResolver.class.getPackage().getName());
        packages(DataMinerInvocation.class.getPackage().getName());
    }
}

