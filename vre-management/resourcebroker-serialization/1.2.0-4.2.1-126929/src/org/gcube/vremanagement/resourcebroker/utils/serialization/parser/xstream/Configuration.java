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
 * Filename: Configuration.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.parser.xstream;

/**
 * The set of global configuration entries.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class Configuration {

	/** The broker configuration file path */
	public static final String BROKER_CONF_FILE = "etc/org.gcube.vremanagement.resourcebroker/broker.properties";

	public static final String BROKER_LOCAL_CONF_FILE = "etc/broker.properties";

	/** Delay in seconds for retrying operations. */
	public static final int SLEEP_TIME = 10;

	// NOTE: the name set in the deploy-jndi
	/** The JNDI name used to publish the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService}. */
	public static final String JNDI_SERVICE_NAME = "gcube/vremanagement/ResourceBroker";

	/** Max tries for publishing the resource. */
	public static final int RESOURCE_PUBLICATION_MAX_ATTEMPTS = 20;

	/**
	 * The max number of minutes from last profile update to consider a GHN
	 * alive.
	 */
	public static final String LIVE_GHN_MAX_MINUTES = "40";

	/** The name space used for {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService} elements. */
	public static final String NS_CONTEXT = "http://gcube-system.org/namespaces/resourcebroker/ResourceBroker";

	/**
	 * The namespace associated to the Registry service used to subscribe for
	 * notifications.
	 */
	public static final String NS_REGISTRY = "http://gcube-system.org/namespaces/informationsystem/registry";

	/** The key to retrieve/publish the resource used in a singleton pattern. */
	public static final String SINGLETON_RESOURCE_KEY = "BrokerService";

	/** The max time a GHN reservation expires. */
	public static final int GHN_RESERVATION_TTL_MINUTES = 1;

	/** The delay for next ghn update notification. */
	public static final int GHN_PROFILE_UPDATER_TTL_MINUTES = 2;

	/**
	 * The package name as specified in build.properties.
	 */
	public static final String PRJ_PACKAGE_NAME = "org.gcube.vremanagement.resourcebroker";

	/**
	 * If the subscription to notifications for modifications to the GHNs should
	 * be done in the
	 * {@link org.gcube.vremanagement.resourcebroker.impl.services.GHNReservationHandler}
	 * .
	 */
	public static final boolean ENABLE_GHN_NOTIFICATIONS = false;
	public static final boolean ENABLE_RI_NOTIFICATIONS = false;
	// TIMED THREADS - default true
	// Observe that this service is absolutely mandatory so it cannot be
	// disabled. Added just for debugging reason.
	public static final boolean ENABLE_REVOKE_RESERVATION_HANDLER = true;
	public static final boolean ENABLE_UPDATE_GHN_HANDLER = false;

	/**
	 * The prefix to use in all classes of this project for logging.
	 */
	public static final String LOGGING_PREFIX = "BMM";

	/**
	 * Each single node (inside a PackageGroup block) can be
	 * considered accepted or failed according to the score associated in the
	 * Feedback. The FEEDBACK_NODE_TRESHOLD defines the minimum value allowed in
	 * percentage (0..100) for accepting such node.
	 */
	public static final int FEEDBACK_NODE_TRESHOLD = 30;

	/**
	 * The threshold for global score of a Feedback.
	 */
	public static final int FEEDBACK_GLOBAL_TRESHOLD = 40;

	/**
	 * Declares how much a ghn feedback score influences
	 * the choice of "best" GHNs.
	 * Namely a bad feedback (0) will have weight 5 to
	 * sum on the number of RIs on the GHN.
	 */
	public static final int GHN_ACCURACY_WEIGHT = 5;

	/**
	 * how much several reservations on the same GHN influence its sorting.
	 */
	public static final float GHN_RESERVATION_WEIGHT = 0.4f;

	public static final float GHN_RI_COUNT_WEIGHT = 1f;
	public static final float  GHN_LOAD_WEIGHT = 7f;

	public static final boolean PREFETCH_GHNS = true;

	public static final String QUERY_COMMENT_TOKEN = "#";

	public static final boolean ENABLE_PERSISTENCE_REFRESH = true;
	public static final int PERSISTENCE_REFRESH_TTL_MINUTES = 1;
	public static final String GHN_SCORE_KEY = "GHNScoreTBL";

	public static final String CONTEXT_SCOPE = "/gcube/devsec";
}
