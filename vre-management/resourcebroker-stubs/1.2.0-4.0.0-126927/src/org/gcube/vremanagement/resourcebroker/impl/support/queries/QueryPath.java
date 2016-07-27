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
 * Filename: QueryPath.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.support.queries;

import java.io.InputStream;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum QueryPath {

	/**
	 * Returns the set of GHNs and the number of RI allocated on them.
	 * Here the GHNs are filtered by type 'Dynamic' and is returned the number
	 * of minuted elapsed from last update (to check if they are still alive).
	 * <p>
	 * <b>Params:</b> <b><i>MAXWAIT</i></b> an integer representing the max minutes elapsed from
	 * the last update that have to be considered, the other GHNs will be excluded.
	 * </p>
	 * <pre>
	 * Returns tuples of elements of the form:
	 *
	 * &lt;RIONGHN&gt;
	 * 	&lt;ID&gt;b82e4460-1d85-11df-8246-af2e4af0f652&lt;/ID&gt;
	 * 	&lt;AllocatedRI&gt;4&lt;/AllocatedRI&gt;
	 * 	&lt;LastUpdate&gt;2010-02-23T15:08:05+01:00&lt;/LastUpdate&gt;
	 * 	&lt;UpdateMinutesElapsed&gt;2&lt;/UpdateMinutesElapsed&gt;
	 * &lt;/RIONGHN&gt;
	 * </pre>
	 */
	COUNT_RI_ON_DYNAMIC_ALIVE_GHN("ghns.xq"),


	/**
	 * Returns the information of a GHN and the number of RI allocated on it.
	 * <pre>
	 * <b>Params:</b>
	 *
	 *  <b><i>MAXWAIT</i></b> an integer representing the max minutes elapsed from
	 * 	the last update that have to be considered, the other GHNs will be excluded.
	 *
	 *  <b><i>PARAM_GHNID</i></b> the ID of GHN.
	 * </pre>
	 * <pre>
	 * <b>Returns a tuple (if exists) of the form:</b>
	 * <i>
	 * &lt;RIONGHN&gt;
	 * 	&lt;GHNID&gt;b82e4460-1d85-11df-8246-af2e4af0f652&lt;/GHNID&gt;
	 * 	&lt;AllocatedRI&gt;4&lt;/AllocatedRI&gt;
	 * 	&lt;LastUpdate&gt;2010-02-23T15:08:05+01:00&lt;/LastUpdate&gt;
	 * 	&lt;UpdateMinutesElapsed&gt;2&lt;/UpdateMinutesElapsed&gt;
	 * &lt;/RIONGHN&gt;
	 * </i>
	 * </pre>
	 */
	COUNT_RI_ON_DYNAMIC_ALIVE_GHN_BY_ID("ghnsByID.xq"),


	/**
	 * <p>
	 * Given a RI unique ID retrieves the GHN ID on which it is running.
	 * Requires a parameter .
	 * </p>
	 * <pre>
	 * <b>Params:</b>
	 *
	 *	<b>RI_ID</b> corresponding to the unique ID of retrieved RunningInstance.
	 * </pre>
	 * <pre>
	 * <b>Returns a tuple (if exists) of the form:</b>
	 * <i>
	 * &lt;RunningInstance&gt;
	 * 	&lt;RiID&gt;ffc03e80-1fa8-11df-a7f2-82c9a6c0f9ae&lt;/RiID&gt;
	 * 	&lt;GHNID&gt;da5e2300-126a-11df-9d89-bc30db7399aa&lt;/GHNID&gt;
	 * &lt;/RunningInstance&gt;
	 * </i>
	 * </pre>
	 */
	GET_GHN_FOR_RI("ghnByRI.xq"),


	/**
	 * Given a quintuple representing a package to deploy of the form:
	 * <pre>
	 * 	&lt;Package reuse="true"&gt;
	 *		&lt;ServiceClass&gt;PkgServiceClass&lt;/ServiceClass&gt;
	 *		&lt;ServiceName&gt;PkgServiceName&lt;/ServiceName&gt;
	 *		&lt;ServiceVersion&gt;PkgServiceVersion&lt;/ServiceVersion&gt;
	 *		&lt;PackageName&gt;PkgPackageName&lt;/PackageName&gt;
	 *		&lt;PackageVersion&gt;PkgPackageVersion&lt;/PackageVersion&gt;
	 *	&lt;/Package&gt;
	 * </pre>
	 * it retrieves from the IS the list of all service corresponding to these.
	 * <p>
	 * <b>Parameters:</b>
	 * <ul>
	 * <li>
	 * <b>SERVICE_CLASS</b>
	 * </li>
	 * <li>
	 * <b>SERVICE_NAME</b>
	 * </li>
	 * <li>
	 * <b>PKG_NAME</b>
	 * </li>
	 * <li>
	 * <b>PKG_CLASS</b>
	 * </li>
	 * </ul>
	 * </p>
	 */
	GET_SERVICE_REQS("serviceReqs.xq");

	private String path = "org/gcube/vremanagement/resourcebroker/impl/support/queries/xquery/";
	private String filename = null;
	QueryPath(final String filename) {
		this.filename = filename;
	}
	public InputStream getFileName() {
		return this.getClass().getClassLoader().getResourceAsStream(this.path + this.filename);
	}
}
