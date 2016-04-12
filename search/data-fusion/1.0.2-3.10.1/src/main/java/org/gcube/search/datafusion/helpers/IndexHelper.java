package org.gcube.search.datafusion.helpers;

import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.gcube.search.datafusion.datatypes.Pair;
import org.gcube.search.datafusion.datatypes.PositionalRecordWrapper;
import org.gcube.search.datafusion.datatypes.RankedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Alex Antoniadis
 *
 */
public class IndexHelper implements Serializable{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexHelper.class);

	private static final Analyzer ANALYZER = new SimpleAnalyzer(Version.LUCENE_41);// StandardAnalyzer(Version.LUCENE_CURRENT);

	public static Directory initializeIndex() throws IOException {
		return new RAMDirectory();
	}

	public static IndexWriter initializeIndexWriter(Directory index) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_41, ANALYZER);

		return new IndexWriter(index, config);
	}

	public static Map<String, Pair> rankDocuments(Directory index, String query, int count) throws ParseException,
			IOException {
		return rankDocuments(index, query, count, count);
	}

	/**
	 * Performs the query on the index and returns a map with key the id of the record and value a pair of score and lucene id (different from record id)
	 * Obviously, the returned result is not sorted but it requires sorting on the score
	 * 
	 * @param index
	 * @param query
	 * @param count
	 * @param hitsCount
	 * @return map of recordIDs -> (score, luceneDocID)
	 * @throws ParseException
	 * @throws IOException
	 */
	public static Map<String, Pair> rankDocuments(Directory index, String query, int count, final int hitsCount)
			throws ParseException, IOException {

		LOGGER.info("Ranking documents. checking for first : " + count + " hits");
		Map<String, Pair> scores = new HashMap<String, Pair>();

		BooleanQuery bq = new BooleanQuery();

		Query q = new QueryParser(Version.LUCENE_41, RecordHelper.QUERY_FIELD, ANALYZER).parse(query);
		MatchAllDocsQuery everyDocClause = new MatchAllDocsQuery();

		bq.add(everyDocClause, BooleanClause.Occur.MUST);
		bq.add(q, BooleanClause.Occur.SHOULD);

		int hitsPerPage = count;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(bq, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		LOGGER.info("hits returned  : " + hits.length);

		for (ScoreDoc hit : hits) {
			int docId = hit.doc;
			Document d = searcher.doc(docId);
			String id = d.get(RecordHelper.ID_FIELD);
			Float score = hit.score;

			// because of duplicate elimination we will get only 1 record for
			// each id (consider map)
			// but since different views (different fields) may have been stored
			// in lucene we will
			// get the one record with the highest score
			// NOTE that duplicates should not happen in usual cases

			if (!scores.containsKey(id) || scores.get(id).getScore() < score) {
				Pair p = new Pair(score, docId);
				scores.put(id, p);
			} // else {
				// System.out.println("dupilcate id : " + id + " old docID : " +
				// scores.get(id).luceneDocID + " this docid : " + docId);
			//}
		}

		LOGGER.info("distinct hits returned  : " + scores.size());
		// in case the returned hits are less than expected
		// this is also the reason we use MatchAllDocsQuery
		// in order to return the most documents.
		// Note that this is a corner case and usually the documents should
		// match the query
		if (scores.size() < hitsCount) {
			scores = null;
			q = null;
			reader.close();
			searcher = null;
			collector = null;
			hits = null;

			return rankDocuments(index, query, 2 * count, hitsCount);
		}
		return scores;
	}

	/**
	 * Performs a multiget on the lucene index from the given list of {@link RankedRecord}s
	 * 
	 * @param recs
	 * @param index
	 * @param fieldsName
	 * @return list of {@link GenericRecord}s retrieved
	 * @throws Exception
	 */
	public static List<GenericRecord> multiget(List<RankedRecord> recs, Directory index, Set<String> fieldsName)
			throws Exception {
		List<GenericRecord> rsRecs = new ArrayList<GenericRecord>();

		IndexReader reader = DirectoryReader.open(index);

		for (RankedRecord rec : recs) {
			Integer docID = rec.getLuceneDocID();
			Document doc = null;
			
			if (docID != null)
				doc = reader.document(docID);

			GenericRecord rsRec = RecordHelper.luceneToRSRecord(doc, fieldsName, RankedRecord.calcScore(rec));
			rsRecs.add(rsRec);
		}

		return rsRecs;
	}
	
	/**
	 * Feeds the lucene index with (maximum count) records that are retrieved from the iter. A payload field that will be used for querying later is generated either
	 * from fields that exist in the snippetFields or by the actual content of the record (objectID).
	 * Also, each fieldName that is faced in records from iter are is gathered to contstruct a union of all fields
	 * from the (different type of) records in the iter. It returns a map that has recordID as key and (the initial) record position as value.
	 * Initial record position is the position that the record had before fusion in its iterator. 
	 * 
	 * @param index
	 * @param iter
	 * @param snippetFields
	 * @param fields
	 * @param count
	 * @return map recordID -> position
	 * @throws Exception
	 */
	public static Map<String, Long> feedLucene(Directory index, Iterator<PositionalRecordWrapper> iter, List<String> snippetFields, Set<String> fields, int count) throws Exception {
		long starttime = 0;
		long endtime = 0;
		
		IndexWriter w = initializeIndexWriter(index);
		Map<String, Long> recs = new HashMap<String, Long>();
		
		int cnt = 0;
		int added = 0;
		
		while (iter.hasNext() && (count == -1 || cnt < count)) {
			starttime = System.currentTimeMillis();
			PositionalRecordWrapper prec = iter.next();
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~~> feedLucene: getting next record time : " + (endtime- starttime) / 1000.0 + " secs");
			
			starttime = System.currentTimeMillis();
			Long pos = prec.getPosition();
			String id = RecordHelper.getRecordID(prec.getRec()); //This is used to do duplicate elimination!!!
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~~> feedLucene: getting record id time : " + (endtime- starttime) / 1000.0 + " secs");
			
			
			//LOGGER.info("recid : " + id);
			starttime = System.currentTimeMillis();
			String queryFieldPayload = RecordHelper.getQuerySnippetFields(prec.getRec(), snippetFields);
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~~> feedLucene: getting record snippet time : " + (endtime- starttime) / 1000.0 + " secs");
			
			starttime = System.currentTimeMillis();
			RecordHelper.rsRecToLucene(w, id, prec.getRec(), queryFieldPayload);
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~~> feedLucene: storing record to lucene time : " + (endtime- starttime) / 1000.0 + " secs");
			
			
			starttime = System.currentTimeMillis();
			if (!recs.containsKey(id)){
				recs.put(id, pos);
				fields.addAll(ResultSetHelper.getRSRecFields(prec.getRec()));
				cnt++;
			} else if (recs.get(id) > pos){
				recs.put(id, pos);
				fields.addAll(ResultSetHelper.getRSRecFields(prec.getRec()));
			}
			endtime = System.currentTimeMillis();
			LOGGER.info(" ~~> feedLucene: getting fields of record time : " + (endtime- starttime) / 1000.0 + " secs");
			
			
			added++;
			LOGGER.info("Total records read from fused iterator : " + cnt);
		}
		
		iter.remove();
		
		LOGGER.info("Distinct records read from fused iterator : " + cnt);
		LOGGER.info("Records added : " + added);
		
		w.close();
		
		return recs;
	}

}
