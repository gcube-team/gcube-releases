package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnDataTypeMap {

	public static DataType map(ColumnDataType columnDataType) {
		if (columnDataType == ColumnDataType.Integer) {
			return new IntegerType();
		} else {
			if (columnDataType == ColumnDataType.Numeric) {
				return new NumericType();
			} else {
				if (columnDataType == ColumnDataType.Boolean) {
					return new BooleanType();
				} else {
					if (columnDataType == ColumnDataType.Geometry) {
						return new GeometryType();
					} else {
						if (columnDataType == ColumnDataType.Text) {
							return new TextType();
						} else {
							if (columnDataType == ColumnDataType.Date) {
								return new DateType();
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}

	public static ColumnDataType map(DataType dataType) {
		if (dataType == null) {
			return null;
		}

		if (dataType instanceof IntegerType) {
			return ColumnDataType.Integer;
		} else {
			if (dataType instanceof NumericType) {
				return ColumnDataType.Numeric;
			} else {
				if (dataType instanceof BooleanType) {
					return ColumnDataType.Boolean;
				} else {
					if (dataType instanceof GeometryType) {
						return ColumnDataType.Geometry;
					} else {
						if (dataType instanceof TextType) {
							return ColumnDataType.Text;
						} else {
							if (dataType instanceof DateType) {
								return ColumnDataType.Date;
							} else {
								return null;
							}
						}
					}
				}
			}

		}
	}

	public static Class<? extends DataType> mapToDataTypeClass(
			ColumnDataType columnDataType) {
		
		if (columnDataType == ColumnDataType.Integer) {
			return IntegerType.class;
		} else {
			if (columnDataType == ColumnDataType.Numeric) {
				return NumericType.class;
			} else {
				if (columnDataType == ColumnDataType.Boolean) {
					return BooleanType.class;
				} else {
					if (columnDataType == ColumnDataType.Geometry) {
						return GeometryType.class;
					} else {
						if (columnDataType == ColumnDataType.Text) {
							return TextType.class;
						} else {
							if (columnDataType == ColumnDataType.Date) {
								return DateType.class;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}
	
	public static ColumnDataType mapFromDataTypeClass(
			Class<? extends DataType> dataTypeClass) {
		if (dataTypeClass == IntegerType.class) {
			return ColumnDataType.Integer;
		} else {
			if (dataTypeClass == NumericType.class) {
				return ColumnDataType.Numeric;
			} else {
				if (dataTypeClass == BooleanType.class) {
					return ColumnDataType.Boolean;
				} else {
					if (dataTypeClass == GeometryType.class) {
						return ColumnDataType.Geometry;
					} else {
						if (dataTypeClass == TextType.class) {
							return ColumnDataType.Text;
						} else {
							if (dataTypeClass == DateType.class) {
								return ColumnDataType.Date;
							} else {
								return null;
							}
						}
					}
				}
			}
		}
	}

}
