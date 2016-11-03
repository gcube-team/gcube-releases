/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.sdmx;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.source.Source;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Agencies;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SDMXExportSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;
	
	protected TabResource tabResource;
	protected Agencies agency;
	protected Source source;
	
	protected String id;
	protected String agencyName;
	protected String version;
	protected String registryBaseUrl;
	public TabResource getTabResource() {
		return tabResource;
	}
	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
	}
	public Agencies getAgency() {
		return agency;
	}
	public void setAgency(Agencies agency) {
		this.agency = agency;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgencyName() {
		return agencyName;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRegistryBaseUrl() {
		return registryBaseUrl;
	}
	public void setRegistryBaseUrl(String registryBaseUrl) {
		this.registryBaseUrl = registryBaseUrl;
	}
	
	
	@Override
	public String toString() {
		return "SDMXExportSession [tabResource=" + tabResource + ", agency="
				+ agency + ", source=" + source + ", id=" + id
				+ ", agencyName=" + agencyName + ", version=" + version
				+ ", registryBaseUrl=" + registryBaseUrl + "]";
	}
	


}
