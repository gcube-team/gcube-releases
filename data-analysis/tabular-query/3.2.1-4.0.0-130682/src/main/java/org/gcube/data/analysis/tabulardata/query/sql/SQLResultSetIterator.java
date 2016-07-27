package org.gcube.data.analysis.tabulardata.query.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLResultSetIterator implements Iterator<Object[]> {

	Logger log = LoggerFactory.getLogger(SQLResultSetIterator.class);

	ResultSet rs;

	Table table;
	
	boolean empty = true;
	
	public SQLResultSetIterator(ResultSet rs, Table table) {
		super();
		this.rs = rs;
		this.table = table;
		try {
			if (rs.isBeforeFirst())
				empty = false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNext() {
		try {
			if (empty || rs.isClosed())
				return false;
			return !rs.isLast();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to read query result from DB. Check server logs.");
		}
	}

	@Override
	public Object[] next() {
		ResultSetHandler<Object[]> rsh = new ObjectHandler();
		try {
			Object[] result = rsh.handle(rs);
			if (rs.isLast()) {
				DbUtils.closeQuietly(rs.getStatement());
				DbUtils.closeQuietly(rs);
			}
			return result;
		} catch (SQLException e) {
			String msg = "Unable to read from ResultSet.";
			log.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove operations is unsupported.");
	}

	class ObjectHandler implements ResultSetHandler<Object[]> {

		@Override
		public Object[] handle(ResultSet rs) throws SQLException {

			if (!rs.next())
				return null;
			
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			Object[] result = new Object[cols];

			for (int i = 0; i < cols; i++) {
				/*if (table.getColumnByName(meta.getColumnName(i+1)).getDataType().equals(new GeometryType()))
					result[i] = (PGgeometry)rs.getObject(i+1);
				else*/
				result[i] = rs.getObject(i + 1);
			
			}
			
			
			return result;

		}

	}

}
