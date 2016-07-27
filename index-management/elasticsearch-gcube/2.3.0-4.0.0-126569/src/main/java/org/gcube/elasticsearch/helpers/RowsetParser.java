package org.gcube.elasticsearch.helpers;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.record.field.StringField;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.indexmanagement.common.FullTextIndexType;
import org.gcube.indexmanagement.common.IndexField;
import org.gcube.indexmanagement.common.IndexType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RowsetParser {
	private static final Logger logger = LoggerFactory.getLogger(RowsetParser.class);

	private static String regexIdxTypeNameRowset = "<ROWSET[^>]*idxType=\"([^\"]*?)\"";
	private static Pattern patternIdxTypeNameRowset = Pattern.compile(regexIdxTypeNameRowset);

	public static String getIdxTypeNameRowset(String rowset) {
		return getMatchRegex(patternIdxTypeNameRowset, rowset);
	}

	private static String regexLangRowset = "<ROWSET[^>]*lang=\"([^\"]*?)\"";
	private static Pattern patternLangRowset = Pattern.compile(regexLangRowset);

	public static String getLangRowset(String rowset) {
		return getMatchRegex(patternLangRowset, rowset);
	}

	private static String regexColIDRowset = "<ROWSET[^>]*colID=\"([^\"]*?)\"";
	private static Pattern patternColIDRowset = Pattern.compile(regexColIDRowset);

	public static String getColIDRowset(String rowset) {
		return getMatchRegex(patternColIDRowset, rowset);
	}

	private static String getMatchRegex(Pattern pattern, String rowset) {
		Matcher m = pattern.matcher(rowset);
		String match = null;
		try {
			if (m.find()) {
				match = m.group(1).trim();
				return !match.equals("") ? match : null;
			}
		} catch (Exception e) {
			logger.error("exception while getting idxType", e);
		}
		return match;
	}

	public static boolean addToFieldInfo(List<String> toBeAdded, String field, String colIDandLang,
			FullTextIndexType idxType) {
		boolean found = false;
		boolean searchable = false;
		boolean presentable = false;
		// search all the fields defined in the indexType

		// TODO: uncomment when IS communication is ready

		// TODO: change it to get idxType at the beggining of the feeding
		// FullTextIndexType idxType = retrieveIndexType(indexType, scope,
		// cache);
		for (IndexField idxField : idxType.getFields()) {
			// if we find the field in the indexType
			if (idxField.name.equals(field)) {
				// tag the searchable and presentable info
				found = true;
				searchable = idxField.index;
				presentable = idxField.returned;
				break;
			}
		}

		if (field.equalsIgnoreCase(IndexType.DOCID_FIELD)) {
			searchable = true;
			// it is not presentable because it is a default attribute in each
			// result
			presentable = false;
		} else if (!found) {
			logger.error("The field: " + field + ", is not declared in the indexType.");
			return false;
		}
		if (searchable)
			toBeAdded.add(colIDandLang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG
					+ IndexType.SEPERATOR_FIELD_INFO + field);
		if (presentable)
			toBeAdded.add(colIDandLang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG
					+ IndexType.SEPERATOR_FIELD_INFO + field);
		return true;
	}

	private static final String ROWSETFIELD = "Rowset";

	public static String getRowsetFromResult(Record record) throws GRS2RecordDefinitionException, GRS2BufferException {
		return ((StringField) record.getField(ROWSETFIELD)).getPayload();
	}

}
