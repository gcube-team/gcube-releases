/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: BrokerService.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.services;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderElem;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderException;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.AddRequirements;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.AssignGHNTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.HandleRequirementsTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.InitializeScopeTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.PreselectedGHNTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.SuggestedGHNTask;
import org.gcube.vremanagement.resourcebroker.impl.planbuilders.generictasks.ValidateResponseTask;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNReservation;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.console.PrettyFormatter;
import org.gcube.vremanagement.resourcebroker.utils.performance.PerformanceMonitor;
import org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream.XStreamTransformer;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanBuilderIdentifier;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.ResponseStatus;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.DeployNode;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.Feedback;

/**
 * Implements the ResourceBroker service.
 * Roles:
 * 1) given a {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest} it opportunely assigns the
 * software to deploy to the proper GHN.
 * The decision making in implemented inside the
 * {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService#getPlan(String)}
 * method an built up to several
 * {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderTask}
 * implementing the stages of a {@link org.gcube.vremanagement.resourcebroker.impl.planbuilders.PlanBuilderWorkflow}.
 *
 * The decision making as been though of as composition of
 * decision stages.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class BrokerService extends GCUBEPortType {
	private GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX"));

	@Override
	protected final GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	/**
	 * Receives requests from the VREManagerService
	 * and plans the deploy distribution over the gCore hosting nodes (GHN).
	 * @throws GCUBEFault
	 */
	public final String getPlan(final String planRequest)
	throws GCUBEFault {
		PerformanceMonitor timer = new PerformanceMonitor();
		timer.start();

		// CHECKS the parameters are valid
		// The planRequest is the XML representation of the
		// plan to deploy.
		// It is not needed to trim the parameter.
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(planRequest != null && planRequest.trim().length() > 0,
				new GCUBEFault("Invalid parameter"));

		logger.debug("[PLAN-XML] Building the XStreamTransformer");

		// Loads the XStream factory to handle PlanRequest and PlanResponse
		// objects and their serialization.
		XStreamTransformer transformer = new XStreamTransformer();

		// NOTE refers to the XSD file location defined in XStreamTransformer.SupportedTypes
		// this requires that the schema files in serialization plugin are copied
		// inside the schema directory of service.impl plugin so that they'll
		// be deployed.
		PlanRequest req = transformer.getRequestFromXML(planRequest, true);

		logger.debug("[PLAN] Applying decision making chain");

		PlanBuilderException failure = null;
		PlanBuilderElem result =  null;
		PlanBuilderWorkflow builderFlow = null;
		try {
			/****************************************************************************
			 *  W O R K F L O W    -    C H A I N     -   B E G I N
			 ****************************************************************************/
			// ---  BUILD THE WORKFLOW CHAIN
			builderFlow = new PlanBuilderWorkflow(
					new PlanBuilderElem(req, null));
			// NOTE removed debug only. --
			// -- builderFlow.addPlanBuilderTask(new PrintRequestTask());
			// TASK1 - initialize the response scope
			builderFlow.addPlanBuilderTask(new InitializeScopeTask());
			// TASK2 - fills the response with the package groups pre-assigned to GHN by the client
			builderFlow.addPlanBuilderTask(new PreselectedGHNTask());
			// TASK - handle requirements
			builderFlow.addPlanBuilderTask(new AddRequirements());
			builderFlow.addPlanBuilderTask(new HandleRequirementsTask());
			// TASK3 - uses the GHN suggested in the GHN part of the request to assign to the remaining
			// package groups.
			builderFlow.addPlanBuilderTask(new SuggestedGHNTask());
			// TASK4 - the remaining unassigned PackGroups will be assigned
			// the the ghns having less RI.
			builderFlow.addPlanBuilderTask(new AssignGHNTask());
			// TASK5 - checks there are no packages unassigned to a GHN
			builderFlow.addPlanBuilderTask(new ValidateResponseTask());
			/****************************************************************************
			 *  W O R K F L O W    -    C H A I N     -   E N D
			 ****************************************************************************/
			result = builderFlow.run();

			builderFlow.getPartialResult().getResponse().setStatus(new ResponseStatus("SUCCESS"));
		} catch (PlanBuilderException e) {
			logger.error(PrettyFormatter.bold("[PLAN] ERROR: ") + PrettyFormatter.underlined((e.getMessage())));
			failure = e;
			logger.debug(PrettyFormatter.bold("[PLAN] Releasing reservations for failed plan ") +
					PrettyFormatter.underlined(builderFlow.getPartialResult().getID().toString()));
			if (builderFlow != null && builderFlow.getPartialResult() != null) {
				GHNReservationHandler.getInstance().revokeFailedReservation(builderFlow.getPartialResult().getID());
				if (builderFlow.getPartialResult().getResponse() != null) {
					PlanResponse response = builderFlow.getPartialResult().getResponse();
					response.setStatus(new ResponseStatus("FAILED", failure.getMessage(), failure.getPosition()));
				}
			}
			String retval = transformer.toXML(builderFlow.getPartialResult().getResponse());
			checkResponse(retval);

			timer.stop();
			logger.debug("[PLAN] The access to the service internally takes [" + timer.getLastIntervalSecs() + "s]");
			return retval;
		}

		String retval = null;
		retval = transformer.toXML(result.getResponse());
		checkResponse(retval);

		// Stores the initial request received from the requester.
		/* No more required
		GHNReservation reservation = GHNReservationHandler.getInstance().getReservationFor(result.getID());
		if (reservation != null && builderFlow != null && builderFlow.getInitialRequest() != null) {
			logger.debug("[PLAN] Storing the initial request received from the requester");
			reservation.storePlanRequest(builderFlow.getInitialRequest());
		}
		*/

		timer.stop();
		logger.debug("[PLAN] The access to the service internally takes [" + timer.getLastIntervalSecs() + "s]");
		return retval;
	}

	@SuppressWarnings("deprecation")
	public final VOID handleFeedback(final String feedback)
	throws GCUBEFault {
		// -- logger.debug("[FB-ENTER] received a feedback " + feedback);
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(feedback != null && feedback.trim().length() > 0,
				new GCUBEFault("Invalid feedback parameter received."));

		XStreamTransformer transformer = new XStreamTransformer();
		Feedback fb = transformer.getFeedbackFromXML(feedback, true);
		checker.validate(fb != null && fb.getPlanID() != null,
				new GCUBEFault("Invalid feedback value"));

		logger.debug("[FB-ENTER] received a feedback for plan: " + fb.getPlanID());

		GHNReservationHandler reserver = GHNReservationHandler.getInstance();
		PlanBuilderIdentifier wfID = new PlanBuilderIdentifier(fb.getPlanID());

		// Evaluates the GLOBAL SCORE of received feedback.
		int globalScoreTreshold = BrokerConfiguration.getIntProperty("FEEDBACK_GLOBAL_TRESHOLD");
		int globalScore =  fb.getScore();

		logger.debug("[FB] received a feedback for plan: " + fb.getPlanID() + " having global score: " + globalScore + "/" + globalScoreTreshold);

		// FAILURE FEEDBACK
		//if (globalScore < globalScoreTreshold) {
			logger.debug("[FB-FAILURE] removing all reservations for plan: " + fb.getPlanID());
			// If the whole plan is considered failed all its reservations are revoked.
			GHNReservation reservation = null;
			if (reserver.containsReservationFor(wfID)) {
				reservation = reserver.getReservationFor(wfID);
			}
			if (reservation == null) {
				logger.error("[FB] cannot find reservation for given plan id " + wfID);
				return null;
			}

			logger.debug("[FB-LOCK] acquiring lock on reservation: " + wfID);
			try {
				reservation.lock();
			} catch (InterruptedException e) {
				logger.error("[FB-LOCK] error while acquiring lock on reservation: " + wfID);
				return null;
			}

			// The reservation is revoked and removed from the queue.
			reserver.revokeFailedReservation(wfID);
			// TODO give a negative score
			for (DeployNode dn : fb.getDeployNodes()) {
				int score = dn.getScore();
				String ghn = dn.getPackageGroup().getGHN();
				GHNDescriptor ghnToUpdate = GHNReservationHandler.getInstance().getGHNByID(
						wfID,
						GCUBEScope.getScope(fb.getScope()),
						ghn);
				if (ghnToUpdate != null) {
					logger.debug("[FB-SCORE] assigning score: " + score + "% to GHN: " + ghn + " having actual accurancy " + ghnToUpdate.getAccuracy() + " [DONE]");
					ghnToUpdate.registerScore(score);
					logger.debug("[FB-SCORE] GHN: " + ghn + " has reached accurancy: " + ghnToUpdate.getAccuracy());
				} else {
					logger.error("[FB-SCORE] assigning score: " + score + " to GHN: " + ghn + " [ERR]");
				}
			//}

			logger.debug("[FB-UNLOCK] releasing lock on reservation: " + wfID);
			reservation.unlock();
		}

		logger.debug("[FB-EXIT] feedback stored");

		if (ResourceStorageManager.INSTANCE != null) {
			ResourceStorageManager.INSTANCE.storeScores();
		}

		return null;
	}

	/**
	 * Validate the resulting XML representation of the
	 * {@link PlanResponse} to send to the requester.
	 * @param responseXML
	 */
	private void checkResponse(final String responseXML) {
		// NOTE check on response validation.
		// Checks the resulting response serialization is XML valid.
		// The exception up to now will not impact on the success of
		// the whole workflow.
		XStreamTransformer transformer = new XStreamTransformer();
		String xmlValidationStatus = null;
		try {
			transformer.getResponseFromXML(responseXML, true);
			xmlValidationStatus = "SUCCESS";
		} catch (GCUBEFault e) {
			xmlValidationStatus = "FAILED";
		}
		logger.debug("[PLAN-XML] Validating response XML... [" + xmlValidationStatus + "]");
	}
}
