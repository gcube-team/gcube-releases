package org.gcube.data.analysis.tabulardata.model.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.geometry.GeometryShape;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement(name="Geometry")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeometryType extends DataType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 522898962047621730L;

	@XmlAttribute(name="srid")
	private int srid = 0;
	
	@XmlAttribute
	private GeometryShape type;
	
	@XmlAttribute
	private int dimensions;
	
	public GeometryType() {
		setSrid(0);
		setType(GeometryShape.GEOMETRY);
		setDimensions(2);
	}
	
	public GeometryType(int srid, int dimensions) {
		setSrid(srid);
		this.type = GeometryShape.GEOMETRY;
	}
	
	public GeometryType(int dimensions) {
		this.type = GeometryShape.GEOMETRY;
		setDimensions(dimensions);
	}
	
	public GeometryType(int srid, GeometryShape type, int dimensions) {
		setSrid(srid);
		if (type == null )throw new IllegalArgumentException("type cannot be null.");
		this.type = type;
		setDimensions(dimensions);
	}
	
	public GeometryType(GeometryShape type, int dimensions) {
		super();
		this.type = type;
		setDimensions(dimensions);
	}
	
	public int getSrid() {
		return srid;
	}
	
	public void setSrid(int srid) {
		if (srid < 0 ) throw new IllegalArgumentException("System Referencing id must be greater or equal than 0");
		this.srid = srid;
	}

	
	public GeometryShape getGeometryType() {
		return type;
	}
	
	public void setType(GeometryShape type) {
		this.type = type;
	}

	
	public int getDimensions() {
		return dimensions;
	}
	
	public void setDimensions(int dimensions) {
		if (dimensions<2 || dimensions>4) throw new IllegalArgumentException("Dimension must be an integer between 2 and 4.");
		this.dimensions = dimensions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dimensions;
		result = prime * result + srid;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeometryType other = (GeometryType) obj;
		if (dimensions != other.dimensions)
			return false;
		if (srid != other.srid)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Geometry(");
		builder.append(srid);
		builder.append(",");
		builder.append(type);
		builder.append(",");
		builder.append(dimensions);
		builder.append(")");
		return builder.toString();
	}

	@Override
	public String getName() {
		return "Geometry";
	}
	
	@Override
	public TDTypeValue getDefaultValue() {		
		return  new TDGeometry("POINT(0 0)");
	}
	
	@Override
	public TDTypeValue fromString(String value) {
		throw new IllegalArgumentException("Unable to parse value");
	}
}
