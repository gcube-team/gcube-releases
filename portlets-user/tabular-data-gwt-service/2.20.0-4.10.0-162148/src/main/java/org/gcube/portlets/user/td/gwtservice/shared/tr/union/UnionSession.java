package org.gcube.portlets.user.td.gwtservice.shared.tr.union;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class UnionSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	protected TRId trId;
	protected TabResource currentTabularResource;
	protected TabResource unionTabularResource;
	protected ArrayList<UnionColumnsMapping> columnsMatch;

	public UnionSession() {

	}

	public UnionSession(TRId trId, TabResource currentTabularResource,
			TabResource unionTabularResource,
			ArrayList<UnionColumnsMapping> columnsMatch) {
		this.trId = trId;
		this.currentTabularResource=currentTabularResource;
		this.unionTabularResource = unionTabularResource;
		this.columnsMatch = columnsMatch;

	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public TabResource getUnionTabularResource() {
		return unionTabularResource;
	}

	public void setUnionTabularResource(TabResource unionTabularResource) {
		this.unionTabularResource = unionTabularResource;
	}

	public ArrayList<UnionColumnsMapping> getColumnsMatch() {
		return columnsMatch;
	}

	public void setColumnsMatch(ArrayList<UnionColumnsMapping> columnsMatch) {
		this.columnsMatch = columnsMatch;
	}

	public TabResource getCurrentTabularResource() {
		return currentTabularResource;
	}

	public void setCurrentTabularResource(TabResource currentTabularResource) {
		this.currentTabularResource = currentTabularResource;
	}

	@Override
	public String toString() {
		return "UnionSession [trId=" + trId + ", currentTabularResource="
				+ currentTabularResource + ", unionTabularResource="
				+ unionTabularResource + ", columnsMatch=" + columnsMatch + "]";
	}

	

}
