package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to hold the hashtable for the hash join algorithm
 * 
 * @author UoA
 */
public class HashJoin {
	/**
	 * The Logger used by this class
	 */
	private static Logger logger = LoggerFactory.getLogger(HashJoin.class.getName());
	/**
	 * The hash table used
	 */
	private Hashtable<String,JoinElement> hash=null;
	
	/**
	 * Creates a new {@link HashJoin}
	 */
	public HashJoin(){
		this.hash=new Hashtable<String,JoinElement>();
	}
	
	/**
	 * Checks if the provided element exists in the hashtable
	 * 
	 * @param elem The element to lookup
	 * @return The element that is stored in the hashtable if it exists, <code>null</code> otherwise
	 */
	public JoinElement lookup(JoinElement elem){
		try{
			return hash.get(elem.getValue().getKey());
		}catch(Exception e){
			logger.error("Could not lookup for element. returning null", e);
			return null;
		}
	}
	
	/**
	 * Adds the provided element in the hashtable
	 * 
	 * @param elem The element to add
	 */
	public void add(JoinElement elem){
		try{
			JoinElement inHash=this.lookup(elem);
			if(inHash==null)
				hash.put(elem.getValue().getKey(),elem);
			else{
				for(int i=0;i<elem.getRecordIndices().size();i+=1){
					inHash.getRecordIndices().add(elem.getRecordIndices().get(i));
				}
			}
		}catch(Exception e){
			logger.error("Could not add element. Continuing", e);
		}
	}
	
	/**
	 * Checks if the hashtable is empty
	 * 
	 * @return <code>true</code> if it is, <code>false</code> otherwise
	 */
	public boolean isEmpty(){
		try{
			if(hash.size()>0) return false;
			return true;
		}catch(Exception e){
			logger.error("Could not get hashtable size. Returing true", e);
			return true;
		}
	}
}
