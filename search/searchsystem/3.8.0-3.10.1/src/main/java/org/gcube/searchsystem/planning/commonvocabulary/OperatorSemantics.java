package org.gcube.searchsystem.planning.commonvocabulary;

import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.searchsystem.planning.exception.CQLUnsupportedException;

public class OperatorSemantics {

	private OperatorSemantics() {
		
	}
	
	public static String getOrOperationSemantics(String indication) throws CQLUnsupportedException{
		if(indication.equals(Constants.MERGESORT)) {
			return Constants.MERGESORT;
		}
		if(indication.equals(Constants.MERGEFIFO)) {
			return Constants.MERGEFIFO;
		}
		if(indication.equals(Constants.DEFAULT)) {
			return Constants.MERGE;
		}
		if(indication.equalsIgnoreCase(Constants.FUSE)) {
			return Constants.FUSE;
		}
		throw new CQLUnsupportedException("Indication: " + indication 
				+ " is not supported for CQL OR.");
	}

	public static String getAndOperationSemantics(String indication) throws CQLUnsupportedException {
		if(indication.equals(Constants.MERGESORT)||indication.equals(Constants.MERGEFIFO)) {
			return Constants.JOINSORT;
		}
		if(indication.equals(Constants.DEFAULT)) {
			return Constants.JOIN;
		}
		throw new CQLUnsupportedException("Indication: " + indication 
				+ " is not supported for CQL OR.");
	}
	
	public static String getNotOperationSemantics(String indication) throws CQLUnsupportedException {
		if(indication.equals(Constants.MERGESORT)||indication.equals(Constants.MERGEFIFO)) {
			return Constants.EXCEPT;
		}
		if(indication.equals(Constants.DEFAULT)) {
			return Constants.EXCEPT;
		}
		throw new CQLUnsupportedException("Indication: " + indication 
				+ " is not supported for CQL OR.");
	}

	public static HashMap<String, String> createAndOperationArgs(String semantics, 
			String indication, String payloadSide) throws CQLUnsupportedException{
		
		HashMap<String, String> args = new HashMap<String, String>();
		
		//join case
		if(semantics.equals(Constants.JOIN) || semantics.equals(Constants.JOINSORT)) {
			
			//join key is always the default
			
			//payload side
			args.put(Constants.PAYLOADSIDE, payloadSide);
			
			return args;
		} 
		
		throw new CQLUnsupportedException("semantics: " + semantics + ", are not supported for And Operation");
	}

	public static HashMap<String, String> createOrOperationArgs(String semantics,
			String mode, String indication) throws CQLUnsupportedException{

		HashMap<String, String> args = new HashMap<String, String>();
		
		//merge case
		if(semantics.equals(Constants.MERGE)||semantics.equals(Constants.MERGESORT)||semantics.equals(Constants.MERGEFIFO))
		{
			args.put(Constants.SORT, semantics);
			return args;
		}
		
		if(indication.equalsIgnoreCase(Constants.FUSE)) {
			args.put(Constants.FUSE, semantics);
			return args;
		}
		
		throw new CQLUnsupportedException("semantics: " + semantics + ", are not supported for Or Operation");
	}

	public static HashMap<String, String> createNotOperationArgs(String semantics,
			String mode, String indication) throws CQLUnsupportedException{
		
		HashMap<String, String> args = new HashMap<String, String>();
		
		//except case
		if(semantics.equals(Constants.EXCEPT)) {
			
			return args;
		}
		
		throw new CQLUnsupportedException("semantics: " + semantics + ", are not supported for Not Operation");
	}
	
}
