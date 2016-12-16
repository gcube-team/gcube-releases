package org.gcube.rest.commons.filter;

import java.util.List;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;

public interface IResourceFilter<T extends StatefulResource> {

	public abstract List<T> apply(List<T> objs,
			String filterString);

	public abstract List<String> applyIDs(List<T> objs,
			String filterString);

}