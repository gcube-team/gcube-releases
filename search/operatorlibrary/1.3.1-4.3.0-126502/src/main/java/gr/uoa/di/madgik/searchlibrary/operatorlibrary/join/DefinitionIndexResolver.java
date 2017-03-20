package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import java.util.Map;

public class DefinitionIndexResolver {

	private RecordGenerationPolicy policy = null;
	private Map<IndexPair, Integer> definitionMap = null;
	
	
	public DefinitionIndexResolver(RecordGenerationPolicy policy) {
		this.policy = policy;
	}
	
	public void setDefinitionMap(Map<IndexPair, Integer> definitionMap) {
		this.definitionMap = definitionMap;
	}
	
	public int resolveIndex(int leftIndex, int rightIndex) {
		switch(this.policy) {
		case Concatenate:
			return this.definitionMap.get(new IndexPair(leftIndex, rightIndex));
		case KeepLeft:
			return leftIndex;
		case KeepRight:
			return rightIndex;
		}
		return -1;
	}
}
