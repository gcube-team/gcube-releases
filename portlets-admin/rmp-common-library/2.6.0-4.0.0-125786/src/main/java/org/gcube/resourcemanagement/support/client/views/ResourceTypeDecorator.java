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
 * Filename: ResourceTypeDecorator.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.client.views;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public enum ResourceTypeDecorator {
	/****************************************
	 * RESOURCES IN THE TREE
	 ****************************************/
	GHN("gCube Hosting Node", "ghn-icon","Hosting Node"),
	RunningInstance("Running Instances", "runninginstance-icon", "GCore Endpoint"),
	Service("Software", "service-icon","Software"),
	VIEW("View", "metadatacollection-icon",""),
	GenericResource("Generic Resources", "genericresource-icon","Generic Resource"),
	Collection("Collection", "collection-icon",""),
	WSResource("WSResource", "wsresources-icon",""),
	Empty("Empty Tree", "empty-icon",""),
	RuntimeResource("Runtime Resources", "runtimeresource-icon", "Service Endpoint"),

	/****************************************
	 * Other components
	 ****************************************/
	// For deploying services - similar to the software but with an
	// extension to handle checkbox for install
	InstallableSoftware("InstallableSoftware", "empty-icon",""),
	// In the taskbar for handlig the refresh of deployment reports
	DeployReport("Deploy Report", "report-big-icon",""),
	AddScopeReport("Add to Scope Report", "report-big-icon",""),
	RemoveScopeReport("Remove from Scope Report", "report-big-icon",""),


	/****************************************
	 * Related resources
	 ****************************************/
	GHNRelated("Running Instances", "runninginstance-icon",""),
	ServiceRelated("Running Instances", "runninginstance-icon",""),
	RunningInstanceRelated("Running Instances", "runninginstance-icon",""),

	/****************************************
	 * Models for SWEEPER
	 ***************************************/
	Sweeper_GHN("gCube Hosting Node", "ghn-icon","Hosting Node"),
	Sweeper_RI("Running Instance", "runninginstance-icon", "GCore Endpoint"),
	Sweeper_RI_Orphan("Orphan Running Instance", "orphan-runninginstance-icon", "Orphan GCore Endpoint"),
	Sweeper_GHN_Expired("Expired gHN", "expired-ghn-icon", "Expired Nodes"),
	Sweeper_GHN_Dead("Dead gHN", "dead-ghn-icon", "Unreachable Nodes");


	private String fwsName = null;
	private String label = null;
	private String icon = null;

	ResourceTypeDecorator(final String label, final String icon, final String fwsName) {
		this.label = label;
		this.icon = icon;
		this.fwsName = fwsName;
	}

	public String getLabel() {
		return this.label;
	}

	public String getIcon() {
		return this.icon;
	}
	
	public String getFWSName() {
		if (this.fwsName == null || this.fwsName.equals(""))
			return this.label;
		return this.fwsName;
	}
}
