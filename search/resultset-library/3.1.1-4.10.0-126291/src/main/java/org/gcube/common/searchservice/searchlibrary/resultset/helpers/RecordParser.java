package org.gcube.common.searchservice.searchlibrary.resultset.helpers;

import java.util.Vector;

/**
 * Helper class used by the {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet} element
 * 
 * @author UoA
 */
public class RecordParser {
	
	/**
	 *Creates a new {@link RecordParser} 
	 */
	public RecordParser(){}
	
	/**
	 * Checks if the provided string contains any records, parses them and retrieves a vector holding the 
	 * offset they start from in the string  
	 * 
	 * @param line The line to parse
	 * @return The offset of the records
	 */
	public Vector<Integer> containsRecord(String line){
		Vector<Integer> ret=new Vector<Integer>();
		int offset=0;
		while(true){
			int index=line.indexOf("<"+RSConstants.RecordTag,offset);
			if(index>=0){
				offset=index+("<"+RSConstants.RecordTag).length();
				ret.add(new Integer(index));
			}
			else break;
		}
		return ret;
	}
	
	/**
	 * Retrives the part of the proovided string that is before the given offset
	 * 
	 * @param line The string to use
	 * @param offset The offset to use
	 * @return The required substring
	 */
	public String getHead(String line,int offset){
		return line.substring(0,offset);
	}

	/**
	 * Retrives the part of the proovided string that is after the given offset
	 * 
	 * @param line The string to use
	 * @param offset The offset to use
	 * @return The required substring
	 */
	public String getTail(String line,int offset){
		return line.substring(offset);
	}
	
	/**
	 * Retrieves the full records available in the provided string as they are denoted by the provided offsets
	 * 
	 * @param line The string to use
	 * @param count A vector holding the offsets of the strings
	 * @return A vector holding the retrieved records
	 */
	public Vector<String> getFullRecords(String line,Vector<Integer> count){
		Vector<String> recs=new Vector<String>();
		if(count.size()<2) return recs;
		for(int i=0;i<count.size()-1;i+=1){
			recs.add(line.substring(count.get(i),count.get(i+1)));
		}
		return recs;
	}
}
