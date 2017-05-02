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
 * Filename: PackageGroup.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a> 
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Vector;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PackageGroup implements Cloneable {
	public static final String NODE_TAG = "PackageGroup";
	private List<PackageElem> packages = new Vector<PackageElem>();
	@XStreamAlias("GHN")
	private String ghn = null;
	@XStreamAsAttribute
	@XStreamAlias("ID")
	private String key = null;
	@XStreamAsAttribute
	@XStreamAlias("service")
	private String serviceName = null;
	@XStreamAlias("Requirements")
	private List<Requirement> requirements = null;

	public String getServiceName () {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setID(String id) {
		if (id == null || id.length() == 0) {
			this.key = new BigInteger(64, new SecureRandom()).toString(32);
		} else {
			this.key = id;
		}
	}

	public String getID() {
		return this.key;
	}

	/**
	 * @deprecated use {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanRequest#createPackageGroup(String)}
	 * or {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.PlanResponse#createPackageGroup(String)} instead.
	 */
	public PackageGroup(){
		super();
		this.setID(null);
	}

	protected PackageGroup(String id){
		super();
		this.setID(id);
	}

	public void addPackage(PackageElem p) {
		this.packages.add(p);
	}

	public void setGHN(String ghn) {
		this.ghn = ghn;
	}

	public String getGHN() {
		return this.ghn;
	}

	public List<PackageElem> getPackages() {
		return this.packages;
	}

	@Override
	public PackageGroup clone() throws CloneNotSupportedException {
		PackageGroup retval = new PackageGroup(this.key);
		retval.setServiceName(this.serviceName);
		retval.setGHN(this.ghn);
		for (PackageElem p : this.getPackages()) {
			retval.addPackage(new PackageElem(
					p.isReuse(),
					p.getServiceClass(),
					p.getServiceName(),
					p.getServiceVersion(),
					p.getPackageName(),
					p.getPackageVersion()));
		}
		return retval;
	}

	public final void addRequirement(final Requirement requirement) {
		if (this.requirements == null) {
			this.requirements = new Vector<Requirement>();
		}
		this.requirements.add(requirement);
	}

	public final void addRequirements(final Requirement[] requirements) {
		if (requirements != null) {
			for (Requirement req : requirements) {
				this.addRequirement(req);
			}
		}
	}

	public final boolean hasRequirements() {
		return (this.requirements != null && this.requirements.size() > 0);
	}

	public final List<Requirement> getRequirements() {
		return this.requirements;
	}
}
