package org.gcube.dataaccess.databases.converter;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;

/**
 * Class that allows to convert a query specifying a sql dialect by means of a SwisSQL API
 */
public class SqlDialectConverter {

	private SwisSQLAPI obj;

	// Constructor
	public SqlDialectConverter(String query) {

		obj = new SwisSQLAPI(query);

	}

	public String convert(int dialect) throws ParseException, ConvertException {

		String queryConverted = null;

		queryConverted = obj.convert(dialect);

		AnalysisLogger.getLogger().debug(
				"In SqlDialectConverter-> query converted: " + queryConverted);

		return queryConverted;

	}

}
