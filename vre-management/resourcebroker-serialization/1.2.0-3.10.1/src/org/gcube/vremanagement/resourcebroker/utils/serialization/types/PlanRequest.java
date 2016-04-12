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
 * Filename: PlanRequest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a> 
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import java.util.List;
import java.util.Vector;
import org.gcube.common.core.faults.GCUBEFault;
import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PlanRequest {
	public static final String NODE_TAG = "PlanRequest";
	@XStreamAlias("Scope")
	private String scope = null;
	private List<PackageGroup> packageGroups = new Vector<PackageGroup>();
	@XStreamAlias("GHNList")
	private GHNList ghns = new GHNList();

	/**
	 * @deprecated for serialization only
	 */
	public PlanRequest() {
		super();
	}

	public PlanRequest (String scope) throws GCUBEFault
	{
		this.setScope(scope);
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) throws GCUBEFault {
		if (scope == null || scope.length() == 0)
				throw new GCUBEFault("Invalid parameter");
		this.scope = scope;
	}

	public List<PackageGroup> getPackageGroups(){
		return this.packageGroups;
	}

	@SuppressWarnings("deprecation")
	public PackageGroup createPackageGroup(String serviceName) {
		PackageGroup retval = new PackageGroup();
		this.packageGroups.add(retval);
		// FIXME remove this if a random string is preferred
		retval.setID(String.valueOf(this.packageGroups.size()));
		retval.setServiceName(serviceName);
		return retval;
	}

	public final GHNList getGHNList() {
		return this.ghns;
	}

}
