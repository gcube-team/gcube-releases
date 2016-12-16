package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigDecimal;
import java.math.BigInteger;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.Engine;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;


public class SingletonChunkSet extends ChunkSet {

	private String singletonString;
	private String ColumnType;
	public SingletonChunkSet(String SingletonString, LexicalEngineConfiguration config, Engine engine) throws Exception {
		super(1, 1, null, null, config, engine);
		singletonString = SingletonString;
		ColumnType = null;
	}

	protected BigDecimal calculateNumberOfElements() throws Exception {
		// calculate total entries in the time series
		BigDecimal numberOfElements = BigDecimal.ONE;
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
				tsc = new TimeSeriesChunk(singletonString, ColumnType, startIndex, chunkSize, config, engine);
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
