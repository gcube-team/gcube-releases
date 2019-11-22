package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class VariableData implements Serializable, Comparable<VariableData> {

	private static final long serialVersionUID = -5329486981700757821L;

	/**
	 * The key provider that provides the unique ID of a variable.
	 */
	public static final ProvidesKey<VariableData> KEY_PROVIDER = new ProvidesKey<VariableData>() {
		@Override
		public Object getKey(VariableData variableData) {
			return variableData == null ? null : variableData.getId();
		}
	};

	private int id;
	private String fullName;
	private String units;
	private String dataType;
	private String dimensionString;
	private int rank;
	private boolean coordinateVariable;
	private boolean scalar;
	private boolean immutable;
	private boolean unlimited;
	private boolean unsigned;
	private boolean variableLength;
	private boolean memberOfStructure;
	private ArrayList<DimensionData> dimensions;
	private ArrayList<AttributeData> attributes;
	private ArrayList<RangeData> ranges;

	public VariableData() {
		super();
	}

	public VariableData(int id, String fullName, String units, String dataType, String dimensionString, int rank,
			boolean coordinateVariable, boolean scalar, boolean immutable, boolean unlimited, boolean unsigned,
			boolean variableLength, boolean memberOfStructure, ArrayList<DimensionData> dimensions,
			ArrayList<AttributeData> attributes, ArrayList<RangeData> ranges) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.units = units;
		this.dataType = dataType;
		this.dimensionString = dimensionString;
		this.rank = rank;
		this.coordinateVariable = coordinateVariable;
		this.scalar = scalar;
		this.immutable = immutable;
		this.unlimited = unlimited;
		this.unsigned = unsigned;
		this.variableLength = variableLength;
		this.memberOfStructure = memberOfStructure;
		this.dimensions = dimensions;
		this.attributes = attributes;
		this.ranges = ranges;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDimensionString() {
		return dimensionString;
	}

	public void setDimensionString(String dimensionString) {
		this.dimensionString = dimensionString;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isCoordinateVariable() {
		return coordinateVariable;
	}

	public void setCoordinateVariable(boolean coordinateVariable) {
		this.coordinateVariable = coordinateVariable;
	}

	public boolean isScalar() {
		return scalar;
	}

	public void setScalar(boolean scalar) {
		this.scalar = scalar;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public void setUnsigned(boolean unsigned) {
		this.unsigned = unsigned;
	}

	public boolean isVariableLength() {
		return variableLength;
	}

	public void setVariableLength(boolean variableLength) {
		this.variableLength = variableLength;
	}

	public boolean isMemberOfStructure() {
		return memberOfStructure;
	}

	public void setMemberOfStructure(boolean memberOfStructure) {
		this.memberOfStructure = memberOfStructure;
	}

	public ArrayList<DimensionData> getDimensions() {
		return dimensions;
	}

	public void setDimensions(ArrayList<DimensionData> dimensions) {
		this.dimensions = dimensions;
	}

	public ArrayList<AttributeData> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<AttributeData> attributes) {
		this.attributes = attributes;
	}

	public ArrayList<RangeData> getRanges() {
		return ranges;
	}

	public void setRanges(ArrayList<RangeData> ranges) {
		this.ranges = ranges;
	}

	@Override
	public int compareTo(VariableData variableData) {
		return (id < variableData.id) ? -1 : ((id == variableData.id) ? 0 : 1);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof VariableData) {
			return id == ((VariableData) o).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "VariableData [id=" + id + ", fullName=" + fullName + ", units=" + units + ", dataType=" + dataType
				+ ", dimensionString=" + dimensionString + ", rank=" + rank + ", coordinateVariable="
				+ coordinateVariable + ", scalar=" + scalar + ", immutable=" + immutable + ", unlimited=" + unlimited
				+ ", unsigned=" + unsigned + ", variableLength=" + variableLength + ", memberOfStructure="
				+ memberOfStructure + ", dimensions=" + dimensions + ", attributes=" + attributes + ", ranges=" + ranges
				+ "]";
	}

}
