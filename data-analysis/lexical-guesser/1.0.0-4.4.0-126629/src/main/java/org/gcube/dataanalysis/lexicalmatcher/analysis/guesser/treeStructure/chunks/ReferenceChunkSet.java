package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigDecimal;
import java.math.BigInteger;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.Engine;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.utils.MathFunctions;

public class ReferenceChunkSet extends ChunkSet{

	
	public ReferenceChunkSet(int MaxNumberOfChunks, int ChunkSize, String CategoryName,String CategoryColumn, LexicalEngineConfiguration config, Engine engine) throws Exception{
		super(MaxNumberOfChunks, ChunkSize, CategoryName,CategoryColumn, config, engine);
	}
	
	public ReferenceChunkSet(int MaxNumberOfChunks, int ChunkSize, String CategoryName, String CategoryTable, BigInteger numberOfCategoryElements, LexicalEngineConfiguration config, Engine engine) throws Exception{
		super(MaxNumberOfChunks, ChunkSize, CategoryName, CategoryTable, numberOfCategoryElements, config, engine);
	}
	
	protected BigDecimal calculateNumberOfElements() throws Exception{
		// calculate total entries in the time series
		BigDecimal numberOfElements = new BigDecimal(numberOfEntries);
		return numberOfElements;
	}
	
	
	public ReferenceChunk nextChunk() {

		ReferenceChunk rc = null;

		while (!chunkSet.contains(chunkIndex) && (chunkIndex < chunkSetSize)) {
			chunkIndex++;
		}
		if (chunkIndex < chunkSetSize) {
			BigInteger startIndex = MathFunctions.chunk2Index(chunkIndex, chunkSize);
			try {
				rc = new ReferenceChunk(seriesName, seriesColumn , startIndex, chunkSize, engine);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
		
		chunkIndex++;
		return rc;

	}
	
	
}
