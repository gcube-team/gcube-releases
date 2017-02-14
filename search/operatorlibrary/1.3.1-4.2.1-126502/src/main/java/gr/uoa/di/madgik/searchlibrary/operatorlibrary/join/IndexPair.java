package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

public class IndexPair {
	public int first;
	public int second;
	
	public IndexPair(int first, int second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public int hashCode() {
		int hash = first;
	    hash = hash * 31 + second;
	    return hash;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof IndexPair)) return false;
		return (this.first == ((IndexPair)other).first && this.second == ((IndexPair)other).second);
	}
}
