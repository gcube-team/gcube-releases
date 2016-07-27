package gr.uoa.di.madgik.environment.gcube;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.jms.IJMSProvider;

import org.gcube.common.messaging.endpoints.BrokerEndpoints;
import org.gcube.common.messaging.endpoints.ScheduledRetriever;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An accounting provider for the gCube environment.
 * 
 * @author jgerbe
 * 
 */
public class GCubeJMSProvider implements IJMSProvider {

	private static Logger logger = LoggerFactory.getLogger(GCubeJMSProvider.class);

//	private static ResourceAccounting accounting = null;
	private String JMSHost = null;

	@Override
	public String getJMSPRovider(EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		ScopeProvider.instance.set(Hints.GetHint("GCubeActionScope").Hint.Payload);

		ScheduledRetriever retriever = null;
		try {
			retriever = BrokerEndpoints.getRetriever(60, 60);
			JMSHost = retriever.getFailoverEndpoint();
		} catch (Exception e) {
			logger.warn("Could not find JMSHost for scope " + Hints.GetHint("GCubeActionScope").Hint.Payload, e);
		}
		return JMSHost;
	}
}
