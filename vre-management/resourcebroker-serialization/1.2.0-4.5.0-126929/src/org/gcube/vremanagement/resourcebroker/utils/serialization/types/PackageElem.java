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
 * Filename: Package.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.utils.serialization.types;

import org.gcube.vremanagement.resourcebroker.utils.serialization.types.feedback.FeedbackStatus;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class PackageElem {
	public static final String NODE_TAG = "Package";
	@XStreamAlias("ServiceClass")
	private String serviceClass 	= null;
	@XStreamAlias("ServiceName")
	private String serviceName  	= null;
	@XStreamAlias("ServiceVersion")
	private String serviceVersion 	= null;
	@XStreamAlias("PackageName")
	private String packageName		= null;
	@XStreamAlias("PackageVersion")
	private String packageVersion	= null;

	@XStreamAlias("reuse")
   	@XStreamAsAttribute
	private boolean reuse = false; // the reuse attribute

	/**
	 * This attribute is used only in packages defined inside
	 * feedback nodes to indicate the status of their single
	 * deployment.
	 */
	@XStreamAlias("status")
	@XStreamAsAttribute
   	private FeedbackStatus status = null;

	public PackageElem() {
		super();
	}

	/**
	 * Clones the package passed in input
	 * @param p the package to clone
	 */
	public PackageElem(final PackageElem p) {
		this();
		this.reuse = p.isReuse();
		this.packageName = p.getPackageName();
		this.packageVersion = p.getPackageVersion();
		this.serviceClass = p.getServiceClass();
		this.serviceName = p.getServiceName();
		this.serviceVersion = p.getServiceVersion();
	}

	public PackageElem(final boolean reuse, final String serviceClass, final String serviceName,
			final String serviceVersion, final String packageName, final String packageVersion) {
		super();
		this.reuse = reuse;
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
	}

	public PackageElem(final boolean reuse, final String serviceClass, final String serviceName,
			final String serviceVersion, final String packageName, final String packageVersion,
			FeedbackStatus status) {
		this(reuse, serviceClass, serviceName, serviceVersion, packageName, packageVersion);
		this.setStatus(status);
	}
	
	public final void setServiceClass(final String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public final void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	public final void setServiceVersion(final String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public final void setPackageName(final String packageName) {
		this.packageName = packageName;
	}

	public final void setPackageVersion(final String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public final String getServiceClass() {
		return serviceClass;
	}


	public final String getServiceName() {
		return serviceName;
	}


	public final String getServiceVersion() {
		return serviceVersion;
	}


	public final String getPackageName() {
		return packageName;
	}


	public final String getPackageVersion() {
		return packageVersion;
	}

	public final boolean isReuse() {
		return reuse;
	}

	public final void setReuse(final boolean reuse) {
		this.reuse = reuse;
	}

	public final FeedbackStatus getStatus() {
		return this.status;
	}

	public final void setStatus(final FeedbackStatus status) {
		this.status = status;
	}
}
