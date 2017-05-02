package org.gcube.common.informationsystem.client.eximpl;

import java.util.List;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedQueryException;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.informationsystem.client.impl.AbstractQuery;
import org.gcube.common.core.scope.GCUBEScope;

public abstract class ExistQuery<RESULT> extends AbstractQuery<RESULT> {
	
	public static final String IS_NS="declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';\n";
	public static final String PROVIDER_NS="declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';\n";
	public static final String NS = IS_NS+PROVIDER_NS;
	
	protected String setVREFilter(GCUBEScope scope ) throws ISMalformedQueryException {
		return ExistClientUtil.queryAddAuthenticationControl(getExpression(), scope.toString());
	}

	protected abstract RESULT parseResult(String unparsedResult) throws ISMalformedResultException;

	protected abstract List<EndpointReferenceType> getISICEPRs(GCUBEScope scope) throws ISException;
}
