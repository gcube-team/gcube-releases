package org.gcube.data.td.resources;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.data.analysis.tabulardata.model.resources.InternalURI;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ImmutableURIResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public class ResourceCreatorTest extends ResourceCreatorWorker{

	
	
	public ResourceCreatorTest(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}

	@Override
	protected ResourcesResult execute() throws WorkerException {
		try {
			return new ResourcesResult(new ImmutableURIResult(new InternalURI(new URI("www.google.it")), "google", "returns google web addres", ResourceType.CSV));
		} catch (URISyntaxException e) {
			throw new WorkerException("error generating address",e);
		}
	}
	
}