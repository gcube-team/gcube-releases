package org.gcube.data.analysis.tabulardata.model.mapping;

import java.sql.Date;

import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@Singleton
public class PostgreSQLModelMapper implements SQLModelMapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDBoolean)
	 */
	@Override
	public String translateModelValueToSQL(TDBoolean value) {
		return value.getValue().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDText)
	 */
	@Override
	public String translateModelValueToSQL(TDText value) {
		return "'"+value.getValue().replaceAll("'", "''")+"'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDInteger)
	 */
	@Override
	public String translateModelValueToSQL(TDInteger value) {
		return value.getValue().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDNumeric)
	 */
	@Override
	public String translateModelValueToSQL(TDNumeric value) {
		return value.getValue().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDDate)
	 */
	@Override
	public String translateModelValueToSQL(TDDate value) {
		Date sqlDate = new Date(value.getValue().getTime());
		return "'"+sqlDate.toString()+"'";
	}

	
	@Override
	public String translateModelValueToSQL(TDGeometry value) {
		return String.format("ST_GeomFromText('%s')",value.getValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateModelValueToSQL(org.gcube.data.analysis.tabulardata
	 * .model.datatype.value.TDTypeValue)
	 */
	@Override
	public String translateModelValueToSQL(TDTypeValue value) {
		if (value instanceof TDBoolean)
			return translateModelValueToSQL((TDBoolean) value);
		if (value instanceof TDText)
			return translateModelValueToSQL((TDText) value);
		if (value instanceof TDInteger)
			return translateModelValueToSQL((TDInteger) value);
		if (value instanceof TDNumeric)
			return translateModelValueToSQL((TDNumeric) value);
		if (value instanceof TDDate)
			return translateModelValueToSQL((TDDate) value);
		if (value instanceof TDGeometry)
			return translateModelValueToSQL((TDGeometry) value);
		throw new RuntimeException(String.format("Do not know how to translate value '%s' into SQL.", value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.data.analysis.tabulardata.model.datatype.value.postgresql.
	 * SQLModelMapper
	 * #translateDataTypeToSQL(org.gcube.data.analysis.tabulardata.
	 * model.datatype.DataType)
	 */
	@Override
	public String translateDataTypeToSQL(DataType type) {
		if (type.getClass().equals(BooleanType.class))
			return "boolean";
		if (type.getClass().equals(DateType.class))
			return "date";
		if (type.getClass().equals(GeometryType.class)) {
			GeometryType geometryType = (GeometryType) type;
			return String.format("geometry(%1$s,%2$s)", generateGeometryTypeString(geometryType),
					geometryType.getSrid());
		}
		if (type.getClass().equals(IntegerType.class))
			return "integer";
		if (type.getClass().equals(NumericType.class)) {

			NumericType numericType = (NumericType) type;
			if (numericType.getPrecision() != null && numericType.getScale() != null) {
				return String.format("numeric(%1$s,%2$s)", numericType.getPrecision(), numericType.getScale());
			}
			if (numericType.getPrecision() != null) {
				return String.format("numeric(%1$s)", numericType.getPrecision());
			}
			return "numeric";
		}
		if (type.getClass().equals(TextType.class)) 
			return "text";
		
		throw new RuntimeException("Don't know how to translate type " + type + " into a SQL type.");

	}

	private String generateGeometryTypeString(GeometryType geometryType) {
		// if (geometryType.getGeometryType() != GeometryShape.GEOMETRY &&
		// geometryType.getDimensions() <= 2) {
		return geometryType.getGeometryType().toString();
		// } else
		// return geometryType.getGeometryType().toString() + "M";
	}

}
