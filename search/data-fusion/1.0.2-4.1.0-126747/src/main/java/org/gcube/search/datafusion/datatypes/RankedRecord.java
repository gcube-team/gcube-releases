package org.gcube.search.datafusion.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class RankedRecord implements Comparable<RankedRecord>, Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private Long position;
	private Float rank;
	private Integer luceneDocID;
	private Boolean includePosition = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(RankedRecord.class);

	
	public RankedRecord(String id, Long position, Float rank, Integer luceneDocID, Boolean includePosition) {
		super();
		this.id = id;
		this.position = position;
		this.rank = rank;
		this.luceneDocID = luceneDocID;
		this.includePosition = includePosition;
	}
	
	public RankedRecord(String id, Long position, Float rank, Integer luceneDocID) {
		this(id, position, rank, luceneDocID, Boolean.FALSE);
	}

	@Override
	public String toString() {
		return "RankedRecord [id=" + id + ", position=" + position + ", rank=" + rank + ", luceneDocID="
				+ getLuceneDocID() + " calculated score=" + calcScore(this) + "]";
	}
	
	public Integer getLuceneDocID() {
		return luceneDocID;
	}

	@Override
	public int compareTo(RankedRecord r) {
		Float pos_score1 = calcScore(this);
		Float pos_score2 = calcScore(r);

		if (pos_score1 < pos_score2)
			return 1;
		else if (pos_score1 > pos_score2)
			return -1;

		return 0;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((includePosition == null) ? 0 : includePosition.hashCode());
		result = prime * result + ((luceneDocID == null) ? 0 : luceneDocID.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RankedRecord other = (RankedRecord) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (includePosition == null) {
			if (other.includePosition != null)
				return false;
		} else if (!includePosition.equals(other.includePosition))
			return false;
		if (luceneDocID == null) {
			if (other.luceneDocID != null)
				return false;
		} else if (!luceneDocID.equals(other.luceneDocID))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		return true;
	}

	/**
	 * Calculates the score of the record. If includePosition is true then
	 * the total score is the sum of the rank and the score that is calculated in {@link RankedRecord#posScore}, else
	 * rank is returned.
	 *  
	 * @param r
	 * @return score of record
	 */
	public static Float calcScore(RankedRecord r) {
		if (!r.includePosition) {
			//LOGGER.trace("score (no pos included): " + r.rank);
			return r.rank;
		} else {
			Float posScore = positionScore(r);
			//LOGGER.trace("position score : " + posScore);
			
			Float totalScore = null;
			
			if (r.rank == null)
				totalScore = posScore;
			else 
				totalScore = (float) (posScore + r.rank);
			
			//LOGGER.trace("total score : " + totalScore);
			return totalScore;
		}
		// return (float) (pos_score * r.rank);
		// return (float) (r.rank);
	}

	/**
	 * The boost score that is calculated from the initial position of the record.
	 * After experiments the following formula gives relatively good results:
	 * a / (b ^ position), where a = 0.9860852999637, b = 1.025038425038 and position is the initial position
	 * 
	 * @param r
	 * @return position score
	 */
	private static Float positionScore(RankedRecord r) {
		double a, b;
		a = 0.9860852999637;
		b = 1.025038425038;

		Float pos_score = (float) (a / Math.pow(b, r.position));
		return pos_score;
	}

	/**
	 * Recalculates the score of the records that are retrieved after querying the lucene index in order to 
	 * perform some other refinements. Currently, if includePosition is true the score of the results is recalculated
	 * from their initial position.  
	 * 
	 * @param recPositions
	 * @param recScores
	 * @param includePosition
	 * @return resorted list of {@link RankedRecord}s
	 */
	public static List<RankedRecord> recalculateScores(Map<String, Long> recPositions, Map<String, Pair> recScores, Boolean includePosition) {
		List<RankedRecord> recs = new ArrayList<RankedRecord>();

		for (Entry<String, Long> recPos : recPositions.entrySet()) {
			String docID = recPos.getKey();
			Long pos = recPos.getValue();
			Float score = null;// (float) 0.00; // null if not in scores
			Integer luceneDocumentID = null;

			Pair p = recScores.get(docID);
			if (p != null) {
				score = p.getScore();
				luceneDocumentID = p.getLuceneDocID();
			} else {
				LOGGER.error("Document with id " + docID + " was not returned by lucene query. Returned hits are : "
						+ recScores);
				continue;
			}

			RankedRecord rr = new RankedRecord(docID, pos, score, luceneDocumentID, includePosition);

			recs.add(rr);
		}

		Collections.sort(recs);

		return recs;
	}

}

