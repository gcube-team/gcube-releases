package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.math.BigInteger;
import java.util.ArrayList;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.Engine;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.Entry;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;
import org.hibernate.SessionFactory;

public class ReferenceChunk extends Chunk{

	
	
	private String categoryName;
	private String categoryTableName;
	private ArrayList<Entry> referenceEntries;
	
	private BigInteger startPoint;
	private int chunkSize;
	
	public ReferenceChunk(String CategoryName, String CategoryTableName, BigInteger StartPoint, int ChunkSize, Engine engine){
		super(engine);
		chunkSize = ChunkSize;
		categoryName = CategoryName;
		categoryTableName = CategoryTableName;
		startPoint = StartPoint;
		LexicalLogger.getLogger().trace("ReferenceChunk-> \t\tTOOK CATEGORY CHUNK FOR CATEGORY: " + categoryName+" - index : "+startPoint);
	}
	
	
	//takes references on demand from DB
	public ArrayList<Entry> getReferenceEntries() throws Exception{
		
		DBObjectTranslator dbo = new DBObjectTranslator();
		SessionFactory sess = engine.getDBSession();
//		AnalysisLogger.getLogger().debug("ReferenceChunk->getReferenceEntries-> \tCATEGORY CHUNK START : " + startPoint);
		referenceEntries = dbo.retrieveEntries(sess, categoryTableName, startPoint, chunkSize);
		return referenceEntries;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	
	
	
	
	
}
