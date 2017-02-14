package gr.cite.geoanalytics.dataaccess.typedefinition;

public class PostgreSQLDatabaseColumnType implements DatabaseColumnType {

	@Override
	public String getType(DataType dt) {
		switch (dt) {
		case TINY:
		case SHORT:
		case INTEGER:
			return "integer";
		case LONG:
			return "bigint";
		case FLOAT:
		case DOUBLE:
			return "numeric";
		case DATE:
			return "timestamp";
		case STRING:
			return "character varying(250)";
		case TEXT:
			return "text";
		}
		return "character varying(250)";
	}
}
