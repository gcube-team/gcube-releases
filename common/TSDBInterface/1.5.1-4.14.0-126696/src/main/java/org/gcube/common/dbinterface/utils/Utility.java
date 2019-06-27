package org.gcube.common.dbinterface.utils;

import org.gcube.common.dbinterface.CastObject;
import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.DropTable;
import org.gcube.common.dbinterface.queries.alters.CreateIndexOnField;
import org.gcube.common.dbinterface.queries.alters.DropColumn;
import org.gcube.common.dbinterface.queries.alters.DropFieldIndex;
import org.gcube.common.dbinterface.queries.alters.RenameTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.types.Type.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utility {

	private static final Logger logger = LoggerFactory.getLogger(Utility.class);
	
	public static DropTable drop(String tableName) throws Exception{
		DropTable drop = DBSession.getImplementation(DropTable.class);
		drop.setTableName(tableName);
		return drop;
	}
	
	public static RenameTable renameTable(Table actualTable, String newName) throws Exception{
		RenameTable renameTable= DBSession.getImplementation(RenameTable.class);
		renameTable.setNewName(newName);
		renameTable.setTable(actualTable);
		return renameTable;
	}
	
	public static CastObject getCast(SimpleAttribute field, Type type) throws Exception{
		CastObject cast= DBSession.getImplementation(CastObject.class);
		cast.setField(field);
		cast.setType(type);
		return cast;
	}
	
	public static CastObject getCast(String valueToCast, Type type) throws Exception{
		CastObject cast= DBSession.getImplementation(CastObject.class);
		cast.setStringValue(valueToCast);
		cast.setType(type);
		return cast;
	}
	
	public static CastObject getCastToString(String valueToCast) throws Exception{
		CastObject cast= DBSession.getImplementation(CastObject.class);
		cast.setStringValue(valueToCast);
		cast.setType(new Type(Types.TEXT));
		return cast;
	}
	
	public static CastObject getCastToString(SimpleAttribute field) throws Exception{
		CastObject cast= DBSession.getImplementation(CastObject.class);
		cast.setField(field);
		cast.setType(new Type(Types.TEXT));
		return cast;
	}
	
	public static ColumnDefinition getColumnDefinition(String label, Type type, Specification ...specifications ) throws Exception{
		ColumnDefinition cDef= DBSession.getImplementation(ColumnDefinition.class);
		cDef.setSpecification(specifications);
		cDef.setLabel(label);
		cDef.setType(type);
		return cDef;
	}
	
	public static DropColumn dropColumn(String field, Table table) throws Exception{
		DropColumn dropColumn = DBSession.getImplementation(DropColumn.class);
		dropColumn.setColumn(new SimpleAttribute(field));
		dropColumn.setTable(table);
		return dropColumn;
	}
	
	public static CreateIndexOnField createIndexOnField(Table table, String field, boolean useLower) throws Exception{
		CreateIndexOnField createIndexOnField= DBSession.getImplementation(CreateIndexOnField.class);
		createIndexOnField.setTable(table);
		createIndexOnField.setField(field);
		createIndexOnField.setLowerCase(useLower);
		logger.trace("indexes are "+createIndexOnField.getExpression());
		return createIndexOnField;
	}
	
	public static DropFieldIndex dropFieldIndex(Table table, String field) throws Exception{
		DropFieldIndex dropIndex= DBSession.getImplementation(DropFieldIndex.class);
		dropIndex.setTable(table);
		dropIndex.setField(field);
		return dropIndex;
	}
}
