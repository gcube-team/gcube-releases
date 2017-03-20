package org.gcube.portlets.user.td.gwtservice.shared.codelisthelper;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.source.Source;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CodelistMappingSession implements Serializable {

	private static final long serialVersionUID = 2381185326076784908L;

	private TRId trId;
	private Source source;
	private ResourceTDDescriptor resourceTDDescriptor;
	private String url;
	private String localFileName;
	private String itemId;
	private TabResource connectedTR;
	private ColumnData connectedColumn;
	
	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ResourceTDDescriptor getResourceTDDescriptor() {
		return resourceTDDescriptor;
	}

	public void setResourceTDDescriptor(ResourceTDDescriptor resourceTDDescriptor) {
		this.resourceTDDescriptor = resourceTDDescriptor;
	}

	public TabResource getConnectedTR() {
		return connectedTR;
	}

	public void setConnectedTR(TabResource connectedTR) {
		this.connectedTR = connectedTR;
	}

	public ColumnData getConnectedColumn() {
		return connectedColumn;
	}

	public void setConnectedColumn(ColumnData connectedColumn) {
		this.connectedColumn = connectedColumn;
	}

	@Override
	public String toString() {
		return "CodelistMappingSession [trId=" + trId + ", source=" + source
				+ ", resourceTDDescriptor=" + resourceTDDescriptor + ", url="
				+ url + ", localFileName=" + localFileName + ", itemId="
				+ itemId + ", connectedTR=" + connectedTR
				+ ", connectedColumn=" + connectedColumn + "]";
	}


	
	

}
