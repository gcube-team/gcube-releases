package org.gcube.vremanagement.resourcemanager.impl.brokerage.servicebroker;

import java.io.IOException;
import java.util.Set;

import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageGroup;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.DeployNode;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.FeedbackStatus;
import org.gcube.vremanagement.resourcemanager.impl.reporting.Session;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;

/**
 * 
 * Feedback for deployment plans. It uses the serialization API provided by the Broker Service
 * to create the feedback information.
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Feedback {

	/**
	 * Creates the feedback information for the Broker Service
	 * @param session the current session
	 * @return the XML representation of the feedback information
	 */
	@SuppressWarnings("deprecation")
	public static String create(Session session) throws IOException {
		Set<ScopedDeployedSoftware> services = session.getServices();
		XStreamTransformer transformer = new XStreamTransformer();
		PlanResponse resp = transformer.getResponseFromXML(session.getDeploymentPlan(), false);			
		org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback fb = 
			new org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback();
		fb.setPlanID(resp.getKey());
		fb.setScope(resp.getScope());
		for (PackageGroup group : resp.getPackageGroups()) {
			ScopedDeployedSoftware service = null;
			for (ScopedDeployedSoftware s : services) {
				if (s.getId().compareToIgnoreCase(group.getServiceName()) == 0) {
					service = s;
					break;
				}
			}
			//set the status of each package elemnt
			for (org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem p : group.getPackages()) {
				if (service == null)
					p.setStatus(FeedbackStatus.FAILED);
				else if (service.isSuccess()) 
					p.setStatus(FeedbackStatus.SUCCESS);	
				else //this has to be improved by using the PARTIAL state?
					p.setStatus(FeedbackStatus.FAILED);
			}
			
			DeployNode pgToAdd = new DeployNode(group);			
			fb.addDeployNode(pgToAdd);
		}
		return transformer.toXML(fb);
	}

		
}
