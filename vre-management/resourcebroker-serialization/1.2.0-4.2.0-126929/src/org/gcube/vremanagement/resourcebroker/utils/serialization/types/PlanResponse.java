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
 * Filename: PlanResponse.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Vector;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PlanResponse {

	public static final String NODE_TAG = "PlanResponse";
	@XStreamAlias("deployID")
	@XStreamAsAttribute
	private String key = null;

	@XStreamAlias("Scope")
	private String scope = null;
	private List<PackageGroup> packageGroups = new Vector<PackageGroup>();

	@XStreamAlias("Status")
	private ResponseStatus status = null;

	/**
	 * @deprecated for serialization only.
	 */
	public PlanResponse() {
		this.key = new BigInteger(130, new SecureRandom()).toString(32);
	}

	public PlanResponse(final PlanBuilderIdentifier workflowSession) {
		this.key = workflowSession.getID();
	}

	public PlanResponse(final PlanBuilderIdentifier workflowSession, final String scope) {
		this(workflowSession);
		this.scope = scope;
	}

	public final String getScope() {
		return scope;
	}
	public final void setScope(final String scope) {
		this.scope = scope;
	}

	public final List<PackageGroup> getPackageGroups() {
		return packageGroups;
	}

	public final String getKey() {
		return key;
	}

	public final void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Instantiate a new {@link PackageGroup} inside the {@link PlanResponse}.
	 * <p>
	 * <b>Note:</b> using this method the newly created element is automatically
	 * inserted inside the package group list of the response plan so that
	 * no explicit add(elem) instruction is required.
	 * </p>
	 * @return a new {@link PackageGroup} and stores it inside the {@link PlanResponse}.
	 */
	@SuppressWarnings("deprecation")
	public final PackageGroup createPackageGroup(String serviceName) {
		PackageGroup retval = new PackageGroup();
		this.packageGroups.add(retval);
		// FIXME remove this if a random string is preferred
		retval.setID(String.valueOf(this.packageGroups.size()));
		retval.setServiceName(serviceName);
		return retval;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public ResponseStatus getStatus() {
		return status;
	}
	
	/**
	 * This method is used to check if the obtained response is
	 * for a failed plan or not.
	 */
	public boolean hasFailed() {
		return this.status.getStatus() == "SUCCESS" && this.getStatus().getMsg() == null;
	}
}
