package gr.uoa.di.madgik.execution.report.monitoring;

import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.engine.EngineStatus;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.report.Dispatcher;
import gr.uoa.di.madgik.notificationhandling.NotificationHandling;

import java.util.HashMap;

/**
 * Used to sent monitor notifications asynchronously
 * 
 * @author jgerbe
 *
 */
public class MonitoringDispatcher extends Dispatcher{
	private EnvHintCollection envHint;
	
	/**
	 * @param envHint The environment hint collection to get the gCube Action Scope
	 */
	public MonitoringDispatcher(EnvHintCollection envHint) {
		this.envHint = envHint;
	}
	
	@Override
	public void run() {
		if (envHint == null)
			return;
		
		try {
			EngineStatus status = ExecutionEngine.GetEngineStatus();
			String message = "hostname=" + ExecutionEngine.getLocalhost();
			message += ",load=";
			message += status.PercentageOfUtilization;
			String topicID = NotificationHandling.RegisterNotificationTopic(ExecutionEngine.LOADTOPICNAME, ExecutionEngine.PRODUCERID, null);
			NotificationHandling.SendNotificationToTopic(topicID, message, new HashMap<String, String>(), null);
		} catch (Exception e) {
		}
	}
}
