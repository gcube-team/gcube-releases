package org.gcube.search.sru.db;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.search.sru.db.common.resources.ExplainInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DB {

	static String DB_TITLE_PROP = "title";
	static String DB_DESCRIPTION_PROP = "description";
	static String DB_LINK_PROP = "link";
	static String DB_LASTBUILDDATE_PROP = "lastBuildDate";
	static String DB_PUBDATE_PROP = "pubDate";
	static String DB_TTL_PROP = "ttl";

	static String DB_TITLE = "This is the title";
	static String DB_LINK = "http://example.com";
	static String DB_DESCRIPTION = "This is a dummy description";
	static String DB_PUBDATE = new Date().toString();
	static String DB_LASTBUILDDATE = new Date().toString();
	static String DB_TTL = "0";

	private String serverHost;
	private Integer serverPort;
	private String databaseName;
	private String databaseType;

	private String databaseUsername;
	private String databasePassword;

	private String defaultTable;

	private ExplainInfo explainInfo;

	private SruDBExplain explain;
	private String databaseTitle;
	private String databaseDescription;
	
	private RecordConverter recordConverter = new RecordConverter();

	private static Map<String, Class<?>> databaseTypes = ImmutableMap
			.<String, Class<?>> builder()
			.put("mysql", com.mysql.jdbc.Driver.class)
			.put("postrgres", org.postgresql.Driver.class)
			.put("sqlite", org.sqlite.JDBC.class).build();

	private static final Logger logger = LoggerFactory.getLogger(DB.class);

	public static class Builder {
		private String serverHost;
		private Integer serverPort;
		private String databaseName;
		private String databaseType;

		private String defaultTable;

		private String databaseUsername;
		private String databasePassword;

		private String databaseTitle;
		private String databaseDescription;

		private String schemaID = "http://www.loc.gov/mods";
		private String schemaName = "rss";

		private String recordSchema = "http://explain.z3950.org/dtd/2.0/";
		private String recordPacking = "xml";

		private Map<String, String> indexSets;
		private Map<String, ArrayList<String>> indexInfo;

		public Builder indexSets(Map<String, String> indexSets) {
			this.indexSets = indexSets;
			return this;
		}

		public Builder indexInfo(Map<String, ArrayList<String>> indexInfo) {
			this.indexInfo = indexInfo;
			return this;
		}

		public Builder schemaName(String schemaName) {
			this.schemaName = schemaName;
			return this;
		}

		public Builder schemaID(String schemaID) {
			this.schemaID = schemaID;
			return this;
		}

		public Builder recordSchema(String recordSchema) {
			this.recordSchema = recordSchema;
			return this;
		}

		public Builder recordPacking(String recordPacking) {
			this.recordPacking = recordPacking;
			return this;
		}

		public Builder serverHost(String serverHost) {
			this.serverHost = serverHost;
			return this;
		}

		public Builder serverPort(Integer serverPort) {
			this.serverPort = serverPort;
			return this;
		}

		public Builder databaseName(String databaseName) {
			this.databaseName = databaseName;
			return this;
		}

		public Builder defaultTable(String defaultTable) {
			this.defaultTable = defaultTable;
			return this;
		}

		public Builder databaseTitle(String databaseTitle) {
			this.databaseTitle = databaseTitle;
			return this;
		}

		public Builder databaseType(String databaseType) {
			this.databaseType = databaseType;
			return this;
		}

		public Builder databaseDescription(String databaseDescription) {
			this.databaseDescription = databaseDescription;
			return this;
		}

		public Builder databaseUsername(String databaseUsername) {
			this.databaseUsername = databaseUsername;
			return this;
		}

		public Builder databasePassword(String databasePassword) {
			this.databasePassword = databasePassword;
			return this;
		}

		public DB build() {
			return new DB(this);
		}
	}

	private DB(Builder builder) {
		this.databaseName = builder.databaseName;
		this.databaseType = builder.databaseType;

		this.databaseUsername = builder.databaseUsername;
		this.databasePassword = builder.databasePassword;

		this.databaseUsername = builder.databaseUsername;
		this.databasePassword = builder.databasePassword;

		this.databaseTitle = builder.databaseTitle;
		this.databaseDescription = builder.databaseDescription;
		this.defaultTable = builder.defaultTable;

		this.serverHost = builder.serverHost;
		this.serverPort = builder.serverPort;

		this.explainInfo = new ExplainInfo();
		this.explainInfo.setIndexInfo(builder.indexInfo);
		this.explainInfo.setIndexSets(builder.indexSets);
		this.explainInfo.setSchemaID(builder.schemaID);
		this.explainInfo.setSchemaName(builder.schemaName);
		this.explainInfo.setRecordPacking(builder.recordPacking);
		this.explainInfo.setRecordSchema(builder.recordSchema);

		logger.info("indexInfo : " + builder.indexInfo);
		logger.info("indexSets : " + builder.indexSets);

	}

	public static void main(String[] args) throws Exception {
		BiMap<String, String> map = HashBiMap.create();
		// map.put("books.author", "creator");
		map.put("creator", "author");

		DB db = new DB("localhost", 3306, "test", "mysql", "root", "alexis87",
				"");
		String sql = db.connectToAndQueryDatabase(
				"books.author = \"myauthor\"", map, false);
		System.out.println(sql);
	}

	public DB(String serverHost, Integer serverPort, String databaseName,
			String databaseType, String databaseUsername,
			String databasePassword, String defaultTable) {
		super();
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.databaseName = databaseName;
		this.databaseType = databaseType;
		this.databaseUsername = databaseUsername;
		this.databasePassword = databasePassword;

		this.defaultTable = defaultTable;

	}

	public String getExplain() {
		return this.explain.getExplainXML();
	}

	public void initializeExplain() {

		explain = new SruDBExplain();
		explain.version = "1.1";
		// explain.recordSchema = "http://explain.z3950.org/dtd/2.0/";
		// explain.recordPacking = "xml";

		explain.recordSchema = this.explainInfo.getRecordSchema();
		explain.recordPacking = this.explainInfo.getRecordPacking();

		explain.serverHost = this.serverHost;
		explain.serverPort = this.serverPort;
		explain.databaseName = this.databaseName;

		explain.databaseTitle = this.databaseTitle;
		explain.databaseDescription = this.databaseDescription;

		explain.indexSets = Maps.newHashMap(this.explainInfo.getIndexSets());

		// explain.indexSets.put("cql", "info:srw/cql-context-set/1/cql-v1.1");
		// explain.indexSets.put("books", "info:srw/cql-context-set/1/db-v1.1");

		explain.indexInfo = Maps.newHashMap(this.explainInfo.getIndexInfo());
		// explain.indexInfo.put("books", Arrays.asList("author", "title"));

		// explain.schemaID = "http://www.loc.gov/mods";
		// explain.schemaName = "rss";

		explain.schemaID = this.explainInfo.getSchemaID();
		explain.schemaName = this.explainInfo.getSchemaName();

	}

	public String connectToAndQueryDatabase(String cqlQuery,
			BiMap<String, String> fieldsMapping, boolean splitLists) throws Exception {
		return connectToAndQueryDatabase(cqlQuery, "SRU", fieldsMapping, splitLists);
	}

	public String connectToAndQueryDatabase(String cqlQuery, String schema,
			BiMap<String, String> fieldsMapping, boolean splitLists) throws Exception {
		logger.info("Driver : " + databaseTypes.get(databaseType));

		try (Connection con = DriverManager.getConnection("jdbc:"
				+ databaseType + "://" + serverHost + ":" + serverPort + "/"
				+ databaseName, databaseUsername, databasePassword);) {

			logger.info("got cql query : " + cqlQuery);

			String cqlQueryMapped = mapCqlQuery(cqlQuery, fieldsMapping);

			logger.info("replaced cql query : " + cqlQueryMapped);
			logger.info("defaultTable : " + this.defaultTable);

			CqlToSql cts = new CqlToSql(cqlQueryMapped, this.defaultTable);
			cts.parseQuery();

			String sqlQuery = cts.getSqlQuery();
			logger.info("sql query       : " + sqlQuery);

			String sqlCountQuery = cts.getSqlCountQuery();
			logger.info("sql count query : " + sqlCountQuery);

			ResultSet rsCount = executeQuery(con, sqlCountQuery);

			Long resultsCnt = null;
			if (rsCount.next()) {
				resultsCnt = rsCount.getLong(1);
			}
			logger.info("count : " + resultsCnt);
			
			ResultSet rs = executeQuery(con, sqlQuery);
			
			List<Map<String, String>> records = dbResultSetToRecords(rs, fieldsMapping.inverse());
			
			logger.trace("record retrieved : " + records);

			String sru = recordConverter.convertRecordsToSru(resultsCnt, records, splitLists);
			
			return sru;

		} catch (Exception e) {

			throw e;
		}
	}

	List<Map<String, String>> dbResultSetToRecords(ResultSet rs,
			Map<String, String> sqlToSchemaMapping) throws SQLException {

		List<Map<String, String>> records = Lists.newArrayList();

		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();

		while (rs.next()) {

			Map<String, String> rec = Maps.newHashMap();

			for (int i = 1; i <= colCount; i++) {
				String sqlColumnName = rsmd.getColumnName(i);
				Object value = rs.getObject(i);
				String strVal = value.toString();

				String schemaFieldName = sqlToSchemaMapping.get(sqlColumnName);
				
				logger.trace("no mapping found for : " + schemaFieldName + ". will try last part if any");
				
				if (schemaFieldName != null) {
					rec.put(schemaFieldName, strVal);
				} else {
					List<String> sqlColumnParts = Splitter.on(".").omitEmptyStrings().splitToList(sqlColumnName);
					if (sqlColumnParts.size() > 1){
						String lastPart = sqlColumnParts.get(sqlColumnParts.size() - 1);
						logger.trace("last part : " + lastPart);
						schemaFieldName = sqlToSchemaMapping.get(lastPart);
						if (schemaFieldName != null) {
							rec.put(schemaFieldName, strVal);
						}
					}
				}
			}
			
			records.add(rec);
		}

		return records;

	}

	private String mapCqlQuery(String cqlQuery,
			Map<String, String> fieldsMapping) {

		String newQuery = cqlQuery;

		for (Entry<String, String> entry : fieldsMapping.entrySet()) {
			String fieldID = entry.getKey();
			String fieldName = entry.getValue();

			newQuery = newQuery.replace(fieldID, fieldName);
		}
		
		newQuery = newQuery.replace("cql.", "");
		newQuery = newQuery.replace("oai_dc.", "");
		newQuery = newQuery.replace("dc.", "");

		return newQuery;
	}

	ResultSet executeQuery(Connection con, String query) throws SQLException {
		Statement stmt = con.createStatement();
		stmt.closeOnCompletion();

		ResultSet rs = stmt.executeQuery(query);

		return rs;
	}

//	public static String convertResultSetToRSS(ResultSet rs)
//			throws TransformerException, ParserConfigurationException,
//			SQLException {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.newDocument();
//		doc.setXmlStandalone(true);
//		Element rss = doc.createElement("rss");
//		rss.setAttribute("version", "2.0");
//		doc.appendChild(rss);
//
//		Element channel = doc.createElement("channel");
//
//		rss.appendChild(channel);
//
//		Element el = doc.createElement(DB_TITLE_PROP);
//		el.appendChild(doc.createTextNode(DB_TITLE));
//		channel.appendChild(el);
//
//		el = doc.createElement(DB_LINK_PROP);
//		el.appendChild(doc.createTextNode(DB_LINK));
//		channel.appendChild(el);
//
//		el = doc.createElement(DB_DESCRIPTION_PROP);
//		el.appendChild(doc.createTextNode(DB_DESCRIPTION));
//		channel.appendChild(el);
//
//		el = doc.createElement(DB_PUBDATE_PROP);
//		el.appendChild(doc.createTextNode(DB_PUBDATE));
//		channel.appendChild(el);
//
//		el = doc.createElement(DB_LASTBUILDDATE_PROP);
//		el.appendChild(doc.createTextNode(DB_LASTBUILDDATE));
//		channel.appendChild(el);
//
//		el = doc.createElement(DB_TTL_PROP);
//		el.appendChild(doc.createTextNode(DB_TTL));
//		channel.appendChild(el);
//
//		// Element
//
//		ResultSetMetaData rsmd = rs.getMetaData();
//		int colCount = rsmd.getColumnCount();
//
//		while (rs.next()) {
//			Element row = doc.createElement("item");
//			channel.appendChild(row);
//			for (int i = 1; i <= colCount; i++) {
//				String columnName = rsmd.getColumnName(i);
//				Object value = rs.getObject(i);
//				Element node = doc.createElement(columnName);
//				node.appendChild(doc.createTextNode(value.toString()));
//				row.appendChild(node);
//			}
//		}
//		DOMSource domSource = new DOMSource(doc);
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer transformer = tf.newTransformer();
//		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//		StringWriter sw = new StringWriter();
//		StreamResult sr = new StreamResult(sw);
//		transformer.transform(domSource, sr);
//
//		return sw.toString();
//	}
//
//	public static String convertResultSetToSRU(Long resultsCnt, List<Map<String, String>> records)
//			throws TransformerException, ParserConfigurationException,
//			SQLException {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//		Document doc = builder.newDocument();
//		doc.setXmlStandalone(true);
//		Element searchRetrieveResponse = doc
//				.createElement("searchRetrieveResponse");
//		searchRetrieveResponse.setAttribute("xmlns:zs",
//				"http://www.loc.gov/zing/srw/");
//		doc.appendChild(searchRetrieveResponse);
//
//		Element el = doc.createElement("zs:version");
//		el.appendChild(doc.createTextNode("1.1"));
//		searchRetrieveResponse.appendChild(el);
//
//		Element recordsEl = doc.createElement("zs:records");
//		searchRetrieveResponse.appendChild(recordsEl);
//
//		// Element el = doc.createElement(DB_TITLE_PROP);
//		// el.appendChild(doc.createTextNode(DB_TITLE));
//		// channel.appendChild(el);
//		//
//		// el = doc.createElement(DB_LINK_PROP);
//		// el.appendChild(doc.createTextNode(DB_LINK));
//		// channel.appendChild(el);
//		//
//		// el = doc.createElement(DB_DESCRIPTION_PROP);
//		// el.appendChild(doc.createTextNode(DB_DESCRIPTION));
//		// channel.appendChild(el);
//		//
//		// el = doc.createElement(DB_PUBDATE_PROP);
//		// el.appendChild(doc.createTextNode(DB_PUBDATE));
//		// channel.appendChild(el);
//		//
//		// el = doc.createElement(DB_LASTBUILDDATE_PROP);
//		// el.appendChild(doc.createTextNode(DB_LASTBUILDDATE));
//		// channel.appendChild(el);
//		//
//		// el = doc.createElement(DB_TTL_PROP);
//		// el.appendChild(doc.createTextNode(DB_TTL));
//		// channel.appendChild(el);
//
//		// Element
//
//		el = doc.createElement("zs:numberOfRecords");
//		el.appendChild(doc.createTextNode(String.valueOf(resultsCnt)));
//		searchRetrieveResponse.appendChild(el);
//
//
//		int recPosition = 1;
//
//		for (Map<String, String> record : records) {
//			Element recordEl = doc.createElement("zs:record");
//			recordsEl.appendChild(recordEl);
//
//			el = doc.createElement("zs:recordSchema");
//			el.appendChild(doc.createTextNode("rss"));
//			recordEl.appendChild(el);
//
//			el = doc.createElement("zs:recordPacking");
//			el.appendChild(doc.createTextNode("xml"));
//			recordEl.appendChild(el);
//
//			Element recordData = doc.createElement("zs:recordData");
//			// el.setAttribute("xmlns:srw_rss", "rss");
//			// el.appendChild(doc.createTextNode("xml"));
//			recordEl.appendChild(recordData);
//
//			Element srw = doc.createElement("srw_rss:rss");
//			srw.setAttribute("xmlns:srw_rss", "rss");
//			srw.setAttribute("xmlns:xsi",
//					"http://www.w3.org/2001/XMLSchema-instance");
//			srw.setAttribute("xmlns", "rss");
//
//			// el.appendChild(doc.createTextNode("xml"));
//			recordData.appendChild(srw);
//
//			// channel.appendChild(row);
//			
//			for (Entry<String, String> field : record.entrySet()){
//				String columnName = field.getKey();
//				String value = field.getValue();
//				Element node = doc.createElement(columnName);
//				if (value == null)
//					node.appendChild(doc.createTextNode(""));
//				else
//					node.appendChild(doc.createTextNode(value.toString()));
//				srw.appendChild(node);
//			}
//			
//			Element recordPosition = doc.createElement("zs:recordPosition");
//			recordPosition.appendChild(doc.createTextNode(String
//					.valueOf(recPosition)));
//			// el.appendChild(doc.createTextNode("xml"));
//			recordEl.appendChild(recordPosition);
//			recPosition++;
//		}
//
//		DOMSource domSource = new DOMSource(doc);
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer transformer = tf.newTransformer();
//		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
//		// "yes");
//		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//		// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//		StringWriter sw = new StringWriter();
//		StreamResult sr = new StreamResult(sw);
//		transformer.transform(domSource, sr);
//
//		return sw.toString();
//	}
}
