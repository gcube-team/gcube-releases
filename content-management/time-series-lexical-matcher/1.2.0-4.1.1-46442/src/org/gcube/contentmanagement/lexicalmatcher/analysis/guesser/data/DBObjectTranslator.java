package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.DataTypeRecognizer;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class DBObjectTranslator {

	public static void main(String[] args) {

	}

	public ArrayList<RelationEdge> relations;
	public ArrayList<Category> categories;

	public BigInteger totalEntries;
	public BigInteger totalCatElements;
	public BigInteger totalRelationElements;

	public DBObjectTranslator() {
		relations = new ArrayList<RelationEdge>();
		categories = new ArrayList<Category>();
		totalCatElements = BigInteger.ZERO;
		totalRelationElements = BigInteger.ZERO;
		totalEntries = BigInteger.ZERO;
	}

	public BigInteger calculateTotalEntries(SessionFactory dbSession, String timeSeriesName, String timeSeriesColumn) {

		BigInteger count = BigInteger.ZERO;
		String query = "select count(*) from (SELECT distinct " + timeSeriesColumn + " FROM " + timeSeriesName + ") r;";
		// String query = "SELECT count(*) FROM " + timeSeriesName.toLowerCase();

		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);

		for (Object result : resultSet) {

			try {
				BigInteger resultcount = (BigInteger) result;
				totalEntries = totalEntries.add(resultcount);
				count = resultcount;
				AnalysisLogger.getLogger().trace("DBObjectTranslator->calculateTotalEntries: Time Series " + timeSeriesName + " total " + totalEntries);
			} catch (Exception e) {
			}
		}

		return count;
	}

	public ArrayList<String> retrieveTimeSeriesEntries(SessionFactory dbSession, String timeSeriesName, String timeSeriesColumn, BigInteger min, int numberOfElements) {

		// String query = "SELECT distinct "+timeSeriesColumn+" FROM "+timeSeriesName+" r limit "+min+","+numberOfElements;
		String query = "SELECT distinct " + timeSeriesColumn + " FROM " + timeSeriesName + " r limit " + numberOfElements + " offset " + min;
		AnalysisLogger.getLogger().trace("DBObjectTranslator->query: " + query);

		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);
		ArrayList<String> column = new ArrayList<String>();

		for (Object result : resultSet) {
			try {
				String value = "";
				if (result != null)
					value = result.toString();

				column.add(value);

				// AnalysisLogger.getLogger().debug("DBObjectTranslator->retrieveColumnRange: Column Element Added " + value);
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().trace("DBObjectTranslator->retrieveTimeSeriesEntries: Error in adding entry :" + e.getLocalizedMessage());
			}
		}

		AnalysisLogger.getLogger().trace("DBObjectTranslator->retrieveColumnRange: Column " + column.toString());

		return column;
	}

	public ArrayList<Entry> retrieveEntries(SessionFactory dbSession, String timeSeriesName, BigInteger min, int numberOfElements) {

		// clean previous entries
		ArrayList<Entry> currentEntries = new ArrayList<Entry>();

		ArrayList<String> descriptions = new ArrayList<String>();
		ArrayList<String> types = new ArrayList<String>();
		/*
		 * SELECT table_name,ordinal_position,column_name,data_type, is_nullable,character_maximum_length FROM information_schema.COLUMNS WHERE table_name ='ref_area';
		 */

		String queryDesc = "SELECT table_name,ordinal_position,column_name,data_type, is_nullable,character_maximum_length FROM information_schema.COLUMNS WHERE table_name ='" + timeSeriesName.toLowerCase() + "'";

		List<Object> resultSetDesc = DatabaseFactory.executeSQLQuery(queryDesc, dbSession);
		for (Object result : resultSetDesc) {
			Object[] resultArray = (Object[]) result;
			descriptions.add((String) resultArray[2]);
			types.add(DataTypeRecognizer.transformTypeFromDB((String) resultArray[3]));
		}

		if (descriptions.size() > 0) {
			// String query = "SELECT DISTINCT * FROM " + timeSeriesName + " r where id>=" + min.toString() + " and id<=" + max.toString();
			// String query = "SELECT DISTINCT * FROM " + timeSeriesName + " r limit "+min+","+numberOfElements;
			String query = "SELECT DISTINCT * FROM " + timeSeriesName + " r limit " + numberOfElements + " offset " + min;
			AnalysisLogger.getLogger().trace("DBObjectTranslator->retrieveEntries: query " + query);

			List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);

			for (Object result : resultSet) {
				Entry entry = new Entry();
				try {
					Object[] resultArray = (Object[]) result;
					int i = 0;
					for (Object res : resultArray) {
						// build entry
						String value = "";
						if (res != null)
							value = res.toString();

						entry.addAttribute(descriptions.get(i), value);
						entry.addType(descriptions.get(i), types.get(i));
						i++;
					}
					// add entry
					currentEntries.add(entry);
					// AnalysisLogger.getLogger().debug("DBObjectTranslator->retrieveEntries: Entry Added " + entry.toString());
				} catch (Exception e) {
					// e.printStackTrace();
					AnalysisLogger.getLogger().trace("DBObjectTranslator->retrieveEntries: Error in adding entry :" + e.getLocalizedMessage());
				}
			}
		}

//		AnalysisLogger.getLogger().trace("DBObjectTranslator->retrieveEntries: Entries " + currentEntries);
		return currentEntries;
	}

	public void buildRelationsEdges(SessionFactory dbSession) {

		String query = "select * from relation_table;";
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);
		for (Object result : resultSet) {
			Object[] resultArray = (Object[]) result;
			RelationEdge re = null;
			try {
				re = new RelationEdge(((String) resultArray[2]), "" + resultArray[0], "" + resultArray[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (re != null) {
				relations.add(re);
				AnalysisLogger.getLogger().trace("DBObjectTranslator->buildRelationsEdges: add relation " + re.toString());
			}
		}
	}

	public void buildCategories(SessionFactory dbSession, String referenceTable, String referenceColumn, String idColumn, String nameHuman, String description) {

		referenceTable = referenceTable == null ? "reference_table" : referenceTable;
		referenceColumn = referenceColumn == null ? "table_name" : referenceColumn;
		nameHuman = nameHuman == null ? "name_human" : nameHuman;
		idColumn = idColumn == null ? "id" : idColumn;
		description = description == null ? "description" : description;

		String query = "SELECT " + nameHuman + "," + idColumn + "," + referenceColumn + "," + description + " FROM " + referenceTable + " r;";
		List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);
		if (resultSet != null) {
			for (Object result : resultSet) {
				Object[] resultArray = (Object[]) result;
				Category cat = null;
				try {
					// name_human, id, table_name,description
					cat = new Category("" + resultArray[0], "" + resultArray[1], "" + resultArray[2], "" + resultArray[3]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cat != null) {
					categories.add(cat);
					AnalysisLogger.getLogger().trace("DBObjectTranslator->buildCategories: add category " + cat.toString());
				}
			}
		}
	}

	public Category getCategoryfromIndex(String index) {

		Category cat = null;
		for (Category c : categories) {

			if (c.getIndex().equals(index)) {
				cat = c;
				break;
			}
		}

		return cat;
	}

	public void populateRelationWithCategories() {

		for (RelationEdge re : relations) {

			Category from = getCategoryfromIndex(re.getFrom());
			Category to = getCategoryfromIndex(re.getTo());
			re.setCategoryFrom(from.getName());
			re.setCategoryTo(to.getName());
			AnalysisLogger.getLogger().trace("DBObjectTranslator->populateRelationWithCategories: modified Relation " + re.toString());
		}
	}

	public void calculateRelationWeights(SessionFactory dbSession) {

		for (RelationEdge re : relations) {

			String query = "SELECT count(*) FROM " + re.getName().toLowerCase();

			List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);
			for (Object result : resultSet) {

				try {
					BigInteger resultcount = (BigInteger) result;
					re.setWeigth(resultcount);
					totalRelationElements = totalRelationElements.add(resultcount);
					AnalysisLogger.getLogger().trace("DBObjectTranslator->calculateRelationWeights: Relation " + re.getName() + " weight " + re.getWeigth());
				} catch (Exception e) {
				}
			}
		}
	}

	public void calculateCategoriesWeights(SessionFactory dbSession) {

		for (Category cat : categories) {

			String query = "SELECT count(*) FROM " + cat.getTableName().toLowerCase();

			List<Object> resultSet = DatabaseFactory.executeSQLQuery(query, dbSession);

			for (Object result : resultSet) {

				try {
					BigInteger resultcount = (BigInteger) result;
					cat.setNumberOfElements(resultcount);
					totalCatElements = totalCatElements.add(resultcount);
					AnalysisLogger.getLogger().trace("DBObjectTranslator->calculateCategoriesWeights: Category " + cat.getName() + " weight " + cat.getNumberOfElements() + " total " + totalCatElements);
				} catch (Exception e) {
				}
			}
		}
	}

	public void buildCategoriesStructure(SessionFactory dbSession, String referenceTable, String referenceColumn, String idColumn, String nameHuman, String description) {
		buildCategories(dbSession, referenceTable, referenceColumn, idColumn, nameHuman, description);
		calculateCategoriesWeights(dbSession);
		AnalysisLogger.getLogger().trace("DBObjectTranslator->buildWholeStructure: Total Categories Elements " + totalCatElements + " Total Relation Elements " + totalRelationElements);
	}

	public void buildWholeStructure(SessionFactory dbSession, String referenceTable, String referenceColumn, String idColumn, String nameHuman, String description) {

		buildRelationsEdges(dbSession);
		buildCategories(dbSession, referenceTable, referenceColumn, idColumn, nameHuman, description);
		populateRelationWithCategories();
		calculateRelationWeights(dbSession);
		calculateCategoriesWeights(dbSession);

		AnalysisLogger.getLogger().trace("DBObjectTranslator->buildWholeStructure: Total Categories Elements " + totalCatElements + " Total Relation Elements " + totalRelationElements);
	}

}
