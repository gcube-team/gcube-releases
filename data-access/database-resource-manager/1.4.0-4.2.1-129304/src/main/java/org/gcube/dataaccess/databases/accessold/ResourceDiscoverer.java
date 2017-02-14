package org.gcube.dataaccess.databases.accessold;

import java.util.List;

import org.gcube.dataaccess.databases.resources.DBResource;

/** Class that allows to discover generic resources submitting a query to the IS and given a certain scope. 
 *  It retrieves a list of DBResource objects. */
public abstract class ResourceDiscoverer {


	public abstract List<DBResource> discovery(String scope);


}
