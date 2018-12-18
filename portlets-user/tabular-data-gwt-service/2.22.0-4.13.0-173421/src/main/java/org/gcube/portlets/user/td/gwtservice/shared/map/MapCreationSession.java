/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.map;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MapCreationSession implements Serializable {

	private static final long serialVersionUID = 2648327461965415567L;

	private TRId trId;
	private ArrayList<ColumnData> columns;
	private boolean existsGeometryColumn;
	private int countGeometryColumns;
	private ArrayList<ColumnData> geometryColumns;
	private String name;
	private ArrayList<ColumnData> feature;
	private boolean useView;
	private ColumnData geometry;
	private String username;
	private String metaAbstract;
	private String metaPurpose;
	private String metaCredits;
	private ArrayList<String> metaKeywords;

	public MapCreationSession() {
		super();
	}

	public MapCreationSession(TRId trId, ArrayList<ColumnData> columns, boolean existsGeometryColumn,
			int countGeometryColumns, ArrayList<ColumnData> geometryColumns, String name, ArrayList<ColumnData> feature,
			boolean useView, ColumnData geometry, String username, String metaAbstract, String metaPurpose,
			String metaCredits, ArrayList<String> metaKeywords) {
		super();
		this.trId = trId;
		this.columns = columns;
		this.existsGeometryColumn = existsGeometryColumn;
		this.countGeometryColumns = countGeometryColumns;
		this.geometryColumns = geometryColumns;
		this.name = name;
		this.feature = feature;
		this.useView = useView;
		this.geometry = geometry;
		this.username = username;
		this.metaAbstract = metaAbstract;
		this.metaPurpose = metaPurpose;
		this.metaCredits = metaCredits;
		this.metaKeywords = metaKeywords;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnData> columns) {
		this.columns = columns;
	}

	public boolean isExistsGeometryColumn() {
		return existsGeometryColumn;
	}

	public void setExistsGeometryColumn(boolean existsGeometryColumn) {
		this.existsGeometryColumn = existsGeometryColumn;
	}

	public int getCountGeometryColumns() {
		return countGeometryColumns;
	}

	public void setCountGeometryColumns(int countGeometryColumns) {
		this.countGeometryColumns = countGeometryColumns;
	}

	public ArrayList<ColumnData> getGeometryColumns() {
		return geometryColumns;
	}

	public void setGeometryColumns(ArrayList<ColumnData> geometryColumns) {
		this.geometryColumns = geometryColumns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ColumnData> getFeature() {
		return feature;
	}

	public void setFeature(ArrayList<ColumnData> feature) {
		this.feature = feature;
	}

	public boolean isUseView() {
		return useView;
	}

	public void setUseView(boolean useView) {
		this.useView = useView;
	}

	public ColumnData getGeometry() {
		return geometry;
	}

	public void setGeometry(ColumnData geometry) {
		this.geometry = geometry;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMetaAbstract() {
		return metaAbstract;
	}

	public void setMetaAbstract(String metaAbstract) {
		this.metaAbstract = metaAbstract;
	}

	public String getMetaPurpose() {
		return metaPurpose;
	}

	public void setMetaPurpose(String metaPurpose) {
		this.metaPurpose = metaPurpose;
	}

	public String getMetaCredits() {
		return metaCredits;
	}

	public void setMetaCredits(String metaCredits) {
		this.metaCredits = metaCredits;
	}

	public ArrayList<String> getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(ArrayList<String> metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	@Override
	public String toString() {
		return "MapCreationSession [trId=" + trId + ", columns=" + columns + ", existsGeometryColumn="
				+ existsGeometryColumn + ", countGeometryColumns=" + countGeometryColumns + ", geometryColumns="
				+ geometryColumns + ", name=" + name + ", feature=" + feature + ", useView=" + useView + ", geometry="
				+ geometry + ", username=" + username + ", metaAbstract=" + metaAbstract + ", metaPurpose="
				+ metaPurpose + ", metaCredits=" + metaCredits + ", metaKeywords=" + metaKeywords + "]";
	}

}
