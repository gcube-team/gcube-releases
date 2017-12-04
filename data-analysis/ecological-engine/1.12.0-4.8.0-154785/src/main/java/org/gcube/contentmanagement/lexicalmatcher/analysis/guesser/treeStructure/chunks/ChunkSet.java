package org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.Engine;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.MathFunctions;

public abstract class ChunkSet {

	
	protected String seriesName;
	protected String seriesColumn;
	protected int chunkSize;
	private int maxNumberOfChunks;
	ArrayList<Integer> chunkSet;
	protected int chunkSetSize;
	protected BigInteger numberOfEntries;
	protected int chunkIndex;
	protected LexicalEngineConfiguration config;
	protected Engine engine;
	
	public ChunkSet(int MaxNumberOfChunks, int ChunkSize, String SeriesName,String SeriesColumn, LexicalEngineConfiguration Config, Engine engine) throws Exception{
		this.engine = engine;
		config = Config;
		setSeriesName(SeriesName);
		setSeriesColumn(SeriesColumn);
		setChunkSize(ChunkSize);
		maxNumberOfChunks = MaxNumberOfChunks;
		generateChunkSet();
		
	}
	
	public ChunkSet(int MaxNumberOfChunks, int ChunkSize, String SeriesName,String SeriesColumn,BigInteger numberOfEntries,LexicalEngineConfiguration Config , Engine engine) throws Exception{
		this.engine = engine;
		config = Config;
		setSeriesName(SeriesName);
		setSeriesColumn(SeriesColumn);
		setChunkSize(ChunkSize);
		setNumberOfEntries(numberOfEntries);
		maxNumberOfChunks = MaxNumberOfChunks;
		generateChunkSet();
		
		
	}
	
	
	
	public void generateChunkSet() throws Exception{
		
		AnalysisLogger.getLogger().trace("ChunkSet->generateChunkSet-> \tGenerating Chunk Set for " + seriesName+ " "+seriesColumn);
		int numberOfChunks = calculateNumberOfCycles();
		//generate chunks to be processed
		chunkSet = MathFunctions.generateRandoms(maxNumberOfChunks, 0, numberOfChunks);
		chunkIndex = 0;
		chunkSetSize = numberOfChunks;
	}
	

	abstract protected BigDecimal calculateNumberOfElements() throws Exception;


	protected int calculateNumberOfCycles() throws Exception {

		int numberofcycles = 0;

		// calculate total entries in the time series
		BigDecimal numberOfElements = calculateNumberOfElements();
		// calculate total cycles of comparison
		BigDecimal intcycles;
		BigDecimal oddcycles;
		BigDecimal[] arraydecimal = numberOfElements.divideAndRemainder(new BigDecimal(BigInteger.valueOf(chunkSize)));
		intcycles = arraydecimal[0];
		oddcycles = arraydecimal[1];
		numberofcycles = intcycles.intValue();
		if ((numberofcycles==0)&&(oddcycles.intValue() > 0)) numberofcycles = numberofcycles + 1;

		return numberofcycles;

	}
	
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}


	public String getSeriesName() {
		return seriesName;
	}


	public void setSeriesColumn(String seriesColumn) {
		this.seriesColumn = seriesColumn;
	}


	public String getSeriesColumn() {
		return seriesColumn;
	}


	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}


	public int getChunkSize() {
		return chunkSize;
	}

	public void setNumberOfEntries(BigInteger numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	public BigInteger getNumberOfEntries() {
		return numberOfEntries;
	}
	
	
	
	abstract public Object nextChunk();
	

}
