package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigDecimal;
import java.math.BigInteger;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.Engine;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.dataanalysis.lexicalmatcher.utils.MathFunctions;
import org.hibernate.SessionFactory;

public class TimeSeriesChunkSet extends ChunkSet {

	private String ColumnType;

	public TimeSeriesChunkSet(int MaxNumberOfChunks, int ChunkSize, String TimeSeriesName, String TimeSeriesColumn, LexicalEngineConfiguration config, Engine engine) throws Exception {
		super(MaxNumberOfChunks, ChunkSize, TimeSeriesName, TimeSeriesColumn, config,engine);
		ColumnType = null;
	}

	protected BigDecimal calculateNumberOfElements() throws Exception {
		// calculate total entries in the time series
		DBObjectTranslator dbo = new DBObjectTranslator();
		SessionFactory sess = engine.getDBSession();
		BigDecimal numberOfElements = new BigDecimal(dbo.calculateTotalEntries(sess, seriesName, seriesColumn));
		return numberOfElements;
	}

	public TimeSeriesChunk nextChunk() {

		TimeSeriesChunk tsc = null;

		while (!chunkSet.contains(chunkIndex) && (chunkIndex < chunkSetSize)) {
			chunkIndex++;
		}
		if (chunkIndex < chunkSetSize) {
			BigInteger startIndex = MathFunctions.chunk2Index(chunkIndex, chunkSize);
			try {
				tsc = new TimeSeriesChunk(seriesName, seriesColumn, ColumnType, startIndex, chunkSize, config, engine);
				if (ColumnType == null) {
					ColumnType = tsc.getColumnType();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		chunkIndex++;
		return tsc;

	}

}
