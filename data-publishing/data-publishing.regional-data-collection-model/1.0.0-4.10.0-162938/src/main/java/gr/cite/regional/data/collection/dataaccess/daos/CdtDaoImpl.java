package gr.cite.regional.data.collection.dataaccess.daos;

import gr.cite.regional.data.collection.dataaccess.dsd.ColumnAndType;
import gr.cite.regional.data.collection.dataaccess.entities.Cdt;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.Status;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CdtDaoImpl/* extends JpaDao<Cdt, UUID>*/ implements CdtDao {
	private static final String TABLE_NAME_PREFIX = "CDT_";
	private static final String COLUMN_NAME_PREFIX = "DM_";
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void createTable(String tableNameSuffix, List<ColumnAndType> dmColumnsAndTypes) {
		this.entityManager.createNativeQuery(
				"CREATE TABLE " + "\"" + getTableName(tableNameSuffix) + "\"" + " (" +
						"\"ID\" uuid PRIMARY KEY," +
						"\"Ordinal\" INTEGER NOT NULL," +
						"\"Status\" INTEGER NOT NULL," +
						"\"DataSubmission\" INTEGER NOT NULL," +
						getColumnsAndTypesOrdered(dmColumnsAndTypes) +
						")").executeUpdate();
	}
	
	@Override
	public void deleteTable(String tableNameSuffix) {
		this.entityManager.createNativeQuery("DROP TABLE " + "\"" + getTableName(tableNameSuffix) + "\"").executeUpdate();
	}
	
	private String getColumnsAndTypesOrdered(List<ColumnAndType> dmColumnsAndTypes) {
		return dmColumnsAndTypes.stream()
				.map(columnAndType -> "\"" + getColumnName(columnAndType.getName()) + "\" " + mapDatatypeToSqlDatatype(columnAndType.getDatatype()))
				.collect(Collectors.joining(","));
	}
	
	private String mapDatatypeToSqlDatatype(String datatype) {
		switch (datatype) {
			case "string":
				return "VARCHAR(250)";
			case "integer":
				return "INTEGER";
			default:
				return "VARCHAR(250)";
		}
	}
	
	@Override
	public Cdt create(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes, String tableNameSuffix) {
		if (cdt.getId() == null) {
			cdt.setId(UUID.randomUUID());
		}
		
		this.entityManager.createNativeQuery(
				"INSERT INTO " + "\"" + getTableName(tableNameSuffix) + "\"" + " VALUES (" +
						"'" + cdt.getId() + "'," +
						cdt.getOrdinal() + "," +
						cdt.getStatus() + "," +
						cdt.getDataSubmission().getId() + "," +
						stringifyValuesOrdered(cdt, dmColumnsAndTypes) +
						")").executeUpdate();
		return cdt;
	}
	
	private String stringifyValuesOrdered(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes) {
		return dmColumnsAndTypes.stream()
				.map(columnAndType ->
						mapValueToSqlValue(cdt.getData().get(columnAndType.getName()), columnAndType.getDatatype()))
				.collect(Collectors.joining(","));
	}
	
	public Cdt update(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes, String tableNameSuffix) {
		this.entityManager.createNativeQuery(
				"UPDATE " + "\"" + getTableName(tableNameSuffix) + "\"" + " " +
						"SET " +
						"\"Ordinal\"=" + cdt.getOrdinal() + ", " +
						"\"Status\"=" + cdt.getStatus() + ", " +
						"\"DataSubmission\"=" + cdt.getDataSubmission().getId() + ", " +
						stringifyColumnEqualsValue(cdt, dmColumnsAndTypes) + " " +
						"WHERE \"ID\"='" + cdt.getId() + "'").executeUpdate();
		return cdt;
	}
	
	private String stringifyColumnEqualsValue(Cdt cdt, List<ColumnAndType> dmColumnsAndTypes) {
		return dmColumnsAndTypes.stream()
				.map(columnAndType ->
						"\"" + getColumnName(columnAndType.getName()) + "\"=" + mapValueToSqlValue(cdt.getData().get(columnAndType.getName()), columnAndType.getDatatype()))
				.collect(Collectors.joining(","));
	}
	
	private String mapValueToSqlValue(Object value, String datatype) {
		switch (datatype) {
			case "string":
				return "'" + value + "'";
			case "integer":
				return value.toString();
			default:
				return "'" + value + "'";
		}
	}
	
	@Override
	public Cdt read(UUID id, String tableNameSuffix) {
		return (Cdt) this.entityManager.createNativeQuery(
				"SELECT * FROM " + "\"" + getTableName(tableNameSuffix) + "\"" + " " +
						"WHERE \"ID\"='" + id + "'", Cdt.class).getSingleResult();
	}
	
	@Override
	public List<Cdt> getByDataSubmissionId(Integer dataSubmissionId, String tableNameSuffix, List<String> dmColumnNames) {
		List<Object> rows = this.entityManager.createNativeQuery(
				"SELECT " +
						"CAST(\"ID\" AS VARCHAR)," +
						" \"Ordinal\", " +
						" \"Status\", " +
						"\"DataSubmission\", " +
						dmColumnNames.stream().map(field -> "\"" + CdtDaoImpl.COLUMN_NAME_PREFIX + field + "\"").collect(Collectors.joining(", ")) +
						" FROM " + "\"" + getTableName(tableNameSuffix) + "\"" +
						" WHERE \"DataSubmission\"='" + dataSubmissionId + "'" + " AND " + "\"Status\"=" + Status.ACTIVE.getStatusCode()).getResultList();
		
		return rows.stream().map(row -> transformRowToCdt((Object[])row, dmColumnNames)).collect(Collectors.toList());
	}
	
	private Cdt transformRowToCdt(Object[] row, List<String> dmColumnNames) {
		Cdt cdtEntity = new Cdt();
		Map<String, Object> data = new HashMap<>();
		
		for (int i = 0; i < row.length; i ++) {
			mapColumnToCdtField(row[i], i, dmColumnNames, cdtEntity, data);
		}
		cdtEntity.setData(data);
		
		return cdtEntity;
	}
	
	private void mapColumnToCdtField(Object column, int columnIndex, List<String> dmColumnNames, Cdt cdtEntity, Map<String, Object> data) {
		switch (columnIndex) {
			case 0:
				cdtEntity.setId(UUID.fromString(column.toString()));
				break;
			case 1:
				cdtEntity.setOrdinal(Integer.parseInt(column.toString()));
				break;
			case 2:
				cdtEntity.setStatus(Integer.parseInt(column.toString()));
				break;
			case 3:
				DataSubmission dataSubmission = new DataSubmission();
				dataSubmission.setId(Integer.parseInt(column.toString()));
				cdtEntity.setDataSubmission(dataSubmission);
				break;
			default:
				data.put(dmColumnNames.get(columnIndex - 4), column);
				break;
		}
	}
	
	private String getTableName(String tableNameSuffix) {
		return CdtDaoImpl.TABLE_NAME_PREFIX + tableNameSuffix;
	}
	
	private String getColumnName(String columnNameSuffix) {
		return CdtDaoImpl.COLUMN_NAME_PREFIX + columnNameSuffix;
	}
}
