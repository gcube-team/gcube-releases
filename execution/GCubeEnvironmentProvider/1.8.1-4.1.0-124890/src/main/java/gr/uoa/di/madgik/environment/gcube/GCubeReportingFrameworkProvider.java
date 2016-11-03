package gr.uoa.di.madgik.environment.gcube;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.messaging.accounting.system.SystemAccounting;
//import org.gcube.messaging.accounting.system.SystemAccountingFactory;

import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.reporting.IReportingFrameworkProvider;

/**
 * A reporting provider for the gCube environment. This provider uses the gCube system accounting mechanism of
 * the gCube Messaging Infrastructure
 * 
 * @author gerasimos.farantatos
 *
 */
public class GCubeReportingFrameworkProvider implements IReportingFrameworkProvider {

	private static Logger logger = LoggerFactory.getLogger(GCubeReportingFrameworkProvider.class);
	public static final String GCubeActionScopeHintName="GCubeActionScope";
	public static final String GCubeGHNHintName="GCubeGHN";
	public static final String ReportingFrameworkRIContainerServiceClassHintName="ReportingFrameworkRIContainerServiceClass";
	public static final String ReportingFrameworkRIContainerServiceNameHintName="ReportingFrameworkRIContainerServiceName";
	public static final String ReportingFrameworkRIContainerServiceJNDINameHintName ="ReportingFrameworkRIContainerServiceJNDIName";
	
	private static final Object lockMe = new Object();
	
	private static GCUBEServiceContext context = null;
//	private static SystemAccounting Accounting = null;
	private static String RIContainerServiceClass = null;
	private static String RIContainerServiceName = null;
	private static String RIContainerServiceJNDIName = null;
	private static String GHN = null;


	public void SessionInit(EnvHintCollection Hints) throws EnvironmentReportingException {
		try {
//			GCubeReportingFrameworkProvider.Accounting = SystemAccountingFactory.getSystemAccountingInstance();
			GCubeReportingFrameworkProvider.GHN = GCubeReportingFrameworkProvider.getGHN(Hints);
			GCubeReportingFrameworkProvider.RIContainerServiceClass = GCubeReportingFrameworkProvider.getRIContainerServiceClass(Hints);
			GCubeReportingFrameworkProvider.RIContainerServiceName = GCubeReportingFrameworkProvider.getRIContainerServiceName(Hints);
			GCubeReportingFrameworkProvider.RIContainerServiceJNDIName = GCubeReportingFrameworkProvider.getRIContainerServiceJNDIName(Hints);
			this.context = new GCUBEServiceContext(){ 
				@Override 
				public String getServiceClass() {return GCubeReportingFrameworkProvider.RIContainerServiceClass;} 
				
				@Override 
				public String getName() {return GCubeReportingFrameworkProvider.RIContainerServiceName;} 
				
				@Override
				protected String getJNDIName(){
				return GCubeReportingFrameworkProvider.RIContainerServiceJNDIName; 
				} 
			}; 
		}catch(Exception e) {
			throw new EnvironmentReportingException("Could not initialize session", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void Send(String messageType, Map<String, Object> messageParameters, EnvHintCollection hints) throws EnvironmentReportingException {
		
		HashMap<String, Object> msgParameters = null;
		
		synchronized(GCubeReportingFrameworkProvider.lockMe) {
			try {
				if(!(messageParameters instanceof HashMap))
					msgParameters = new HashMap<String, Object>(messageParameters);
				else
					msgParameters = (HashMap<String, Object>)messageParameters;
				
				logger.debug("Sending message of type: " + messageType + " with message parameters: " + msgParameters + ". Scope is " + 
						GCUBEScope.getScope(GCubeReportingFrameworkProvider.getActionScope(hints)) + " and GHN is " + GCubeReportingFrameworkProvider.GHN); 
//				GCubeReportingFrameworkProvider.Accounting.sendSystemAccountingMessage(context, messageType, msgParameters, GCUBEScope.getScope(GCubeReportingFrameworkProvider.getActionScope(hints)));
				//GCubeReportingFrameworkProvider.Accounting.sendSystemAccountingMessage(messageType,  GCUBEScope.getScope(GCubeReportingFrameworkProvider.getActionScope(hints)), 
				//		GCubeReportingFrameworkProvider.GHN, msgParameters);
			}catch(Exception e) {
				throw new EnvironmentReportingException("Could not send reporting message", e);
			}
		}
	}
	
	private static String getActionScope(EnvHintCollection Hints) {
		if(Hints == null) return null;
		if(!Hints.HintExists(GCubeReportingFrameworkProvider.GCubeActionScopeHintName)) return null;
		return Hints.GetHint(GCubeReportingFrameworkProvider.GCubeActionScopeHintName).Hint.Payload;
	}
	
	private static String getGHN(EnvHintCollection Hints) {
		if(Hints == null) return null;
		if(!Hints.HintExists(GCubeReportingFrameworkProvider.GCubeGHNHintName)) return null;
		return Hints.GetHint(GCubeReportingFrameworkProvider.GCubeGHNHintName).Hint.Payload;
	}
	
	private static String getRIContainerServiceClass(EnvHintCollection Hints) {
		if(Hints == null) return null;
		if(!Hints.HintExists(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceClassHintName)) return null;
		return Hints.GetHint(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceClassHintName).Hint.Payload;
	}
	
	private static String getRIContainerServiceName(EnvHintCollection Hints) {
		if(Hints == null) return null;
		if(!Hints.HintExists(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceNameHintName)) return null;
		return Hints.GetHint(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceNameHintName).Hint.Payload;
	}
	
	private static String getRIContainerServiceJNDIName(EnvHintCollection Hints) {
		if(Hints == null) return null;
		if(!Hints.HintExists(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceJNDINameHintName)) return null;
		return Hints.GetHint(GCubeReportingFrameworkProvider.ReportingFrameworkRIContainerServiceJNDINameHintName).Hint.Payload;
	}

}
