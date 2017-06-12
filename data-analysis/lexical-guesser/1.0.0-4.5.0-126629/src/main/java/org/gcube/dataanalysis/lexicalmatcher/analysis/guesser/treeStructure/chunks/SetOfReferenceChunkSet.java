package org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.treeStructure.chunks;


import java.util.ArrayList;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.Engine;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.interfaces.Reference;

public class SetOfReferenceChunkSet {

	ArrayList<Reference> orderedList;
	int referenceIndex;
	LexicalEngineConfiguration config;
	Engine engine;
	
	public SetOfReferenceChunkSet(ArrayList<Reference> OrderedList, LexicalEngineConfiguration Config, Engine engine){
		
		this.engine = engine;
		orderedList = OrderedList;
		referenceIndex = 0;
		config = Config;
	}
	
	//filter selects only one of the categories
	public ReferenceChunkSet getNextChunkSet(){
		ReferenceChunkSet cs = null;
		if (orderedList.size()>referenceIndex){
			Reference ref = orderedList.get(referenceIndex);
			try{
					cs = new ReferenceChunkSet(config.ReferenceChunksToTake,config.chunkSize,ref.getName(),ref.getTableName(),ref.getNumberOfElements(),config, engine);
			}catch (Exception e){
				e.printStackTrace();
			}
			referenceIndex++;
		}

		return cs;
	}
	
}
