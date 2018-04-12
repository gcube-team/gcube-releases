/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.sdmx;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.document.SDMXDocument;
import org.gcube.portlets.user.td.gwtservice.shared.source.Source;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.type.Codelist;



/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class SDMXImportSession implements Serializable {

	private static final long serialVersionUID = 4176034045408445284L;
	
	protected String id;
	protected SDMXDocument sdmxDocument;//operationID
	protected Source source;
	protected Codelist selectedCodelist;
	protected TabResource tabResource;

	
	public void setId(String id) {
		this.id = id;
	}
	public SDMXDocument getSDMXDocument() {
		return sdmxDocument;
	}
	public void setSDMXDocument(SDMXDocument document) {
		this.sdmxDocument = document;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	
	
	public Codelist getSelectedCodelist() {
		return selectedCodelist;
	}
	public void setSelectedCodelist(Codelist selectedCodelist) {
		this.selectedCodelist = selectedCodelist;
	}
	
	
	public TabResource getTabResource() {
		return tabResource;
	}
	public void setTabResource(TabResource tabResource) {
		this.tabResource = tabResource;
	}
	public String getId() {
		return id;
	}
	@Override
	public String toString() {
		return "SDMXImportSession [id=" + id + ", sdmxDocument=" + sdmxDocument
				+ ", source=" + source + ", selectedCodelist="
				+ selectedCodelist + ", tabResource=" + tabResource + "]";
	}
	


}
