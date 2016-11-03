package org.gcube.resources.discovery.icclient;

import static org.gcube.common.scope.impl.ScopeBean.Type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resources.discovery.icclient.stubs.MalformedQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {

	private static final Logger log = LoggerFactory.getLogger(Helper.class);
	
	
	
	public static String queryAddAuthenticationControl(String expression) throws MalformedQueryException
	{
		
		String scope = ScopeProvider.instance.get();
		
		if (scope==null || !new ScopeBean(scope).is(VRE))
			return expression;
		
					
		int wherePathIndex=0;
		int returnPathIndex=0;
		int collIndexEnd=0;
		int collIndexStart=0;
		boolean whereFinded=false;
		String forInsertFinal="";
		String whereInsertFinal="";
		String temp="";
		
		HashMap<String, String> varReplacementMap = new HashMap<String, String>();
		
		
		String forPropertiesString="\n *VAR* in *COLLECTION*/Data \n"; ///child::*[local-name()='Scope']
		String forString=" *VAR* in *COLLECTION*/Scopes \n";
		String authString=" (contains(*VAR*/child::*[local-name()='Scope'],'"+scope+"') or contains(*VAR*/child::*[local-name()='Scope'],'"+scope.substring(0,scope.lastIndexOf("/"))+"')) ";
		String authStringNormal=" contains(*VAR*//Scope,'"+scope+"') ";  
		
		String queryFiltered;
		List<Boolean> collInsert= new ArrayList<Boolean>();
		Pattern wherePattern= Pattern.compile("where");
		//mathcing , $result in $outer/Data 
		Pattern inSubResult= Pattern.compile("(,\\s*([^\\s]*)\\s*in\\s*([^\\s]*)\\s*)(where|order\\sby|return)");
		Pattern returnPattern= Pattern.compile("order\\sby|return");
		

		Pattern collectionPattern= Pattern.compile("[^\\s]*\\s*in\\s*collection\\s*[^\\s,]*");
		Pattern varPattern= Pattern.compile("[^\\s]*");
		Pattern resourcePattern= Pattern.compile("\\scollection\\s*\\([^\\)]*.*/Resource",  Pattern.DOTALL);
		Pattern propertiesPattern=Pattern.compile("\\scollection\\s*\\([^\\)]*.*/Document",  Pattern.DOTALL);
		Matcher varMat;
		Matcher resourceMat;
		Matcher collMat=collectionPattern.matcher(expression);
		collMat.reset();
		String forStringTemp="";
		while (collMat.find(collIndexEnd))
		{
			
			try{
				collIndexEnd=collMat.end();
				temp=collMat.group();
				collIndexStart=collMat.start();
			}catch(IllegalStateException e){ 
				log.warn("error parsing collection statement");
				}
			varMat= varPattern.matcher(temp);
			boolean propBool=false;
			if (temp.contains("/Properties")) 
			{
				resourceMat= propertiesPattern.matcher(temp);
			    propBool=true;
			}
			else resourceMat=resourcePattern.matcher(temp);
			varMat.lookingAt();
			resourceMat.find();
			String tempPath="";
			try{
				tempPath= temp.substring(resourceMat.end());
				
				if (propBool) 
				{
					String resourceMatString= resourceMat.group();
					forStringTemp=forPropertiesString.replace("*VAR*","$entry"+collInsert.size()+"ValueAuth" ).replace("*COLLECTION*", resourceMatString );
					String oldVar = varMat.group();
					String newVar = "$entry"+collInsert.size()+"ValueAuth";
					collInsert.add(true);
					
					varReplacementMap.put(oldVar, newVar+"/.."+tempPath);
				}
				else
				{
					String resourceMatString= resourceMat.group();
					String oldVar = varMat.group();
					String newVar = "$entry"+collInsert.size()+"ValueAuth";
					forStringTemp=forString.replace("*VAR*",newVar ).replace("*COLLECTION*",resourceMatString );
					collInsert.add(resourceMatString.contains("/Profiles/RunningInstance") || resourceMatString.contains("/Profiles/GHN") || resourceMatString.contains("/Profiles/Service"));
					varReplacementMap.put(oldVar, newVar+"/.."+tempPath);
				}
			}catch(IllegalStateException e){ log.debug("error parsing statement");}
			expression=expression.substring(0, collIndexStart)+ forStringTemp +expression.substring(collIndexEnd); 
			collMat=collectionPattern.matcher(expression);
		}
		
		
		
		if (collInsert.size()==0) return expression;
		//concat the let statements
		
		
		for (int i=0; i<collInsert.size(); i++)
			if (i==collInsert.size()-1){
				if(collInsert.get(i))
					whereInsertFinal+=authString.replace("*VAR*", "$entry"+i+"ValueAuth");
				else whereInsertFinal+=authStringNormal.replace("*VAR*", "$entry"+i+"ValueAuth");
			}else{ 
				if(collInsert.get(i))
					whereInsertFinal+=authString.replace("*VAR*", "$entry"+i+"ValueAuth")+" and " ;
				else whereInsertFinal+=authStringNormal.replace("*VAR*", "$entry"+i+"ValueAuth")+" and " ;
			}
		
		Matcher whereMat=wherePattern.matcher(expression);
		Matcher inSubResultMat= inSubResult.matcher(expression);
		Matcher returnMat=returnPattern.matcher(expression);
		whereMat.reset();
		returnMat.reset(); 
		whereFinded=whereMat.find();
		returnMat.find();
		
		try{
			inSubResultMat.find();
		}catch(Exception e){}
		try{ 
			
			wherePathIndex=whereMat.start();
		}catch(IllegalStateException e){ log.debug("where not found");}
		try{
			returnPathIndex=returnMat.start();
		}catch(IllegalStateException e){ log.error("error parsing return statement"); throw new MalformedQueryException("error parsing return statement");}
		
		if (whereFinded) 
			queryFiltered=expression.substring(0,wherePathIndex)+"\nwhere "+whereInsertFinal
			+" and ("+expression.substring(wherePathIndex+5, returnPathIndex)+" ) \n"+expression.substring(returnPathIndex);
		else 
			queryFiltered=expression.substring(0,returnPathIndex)+ "\nwhere "+whereInsertFinal+" \n "+expression.substring(returnPathIndex);
		
				
		//logger.trace("queryFiltered to match: " + queryFiltered);
		Pattern letPattern=Pattern.compile("let.*:=", Pattern.DOTALL);
		Matcher letMat=letPattern.matcher(queryFiltered);
		whereMat=wherePattern.matcher(queryFiltered);
		whereMat.reset();
		whereMat.find();
		
		boolean letFinded=letMat.find(collIndexStart);
		int letPathIndex=0;
		try{
			
			wherePathIndex=whereMat.start();
			if (letFinded) letPathIndex=letMat.start();
		}catch(IllegalStateException e){ log.error("error parsing let statement"); throw new MalformedQueryException("error parsing let statement");} 		
		
		
		if (!letFinded) {		
			queryFiltered=queryFiltered.substring(0,wherePathIndex)+forInsertFinal +queryFiltered.substring(wherePathIndex);
		}
		else {			
			queryFiltered=queryFiltered.substring(0,letPathIndex)+forInsertFinal+queryFiltered.substring(letPathIndex);
		}
		
		for (Entry<String, String> entry: varReplacementMap.entrySet())
			queryFiltered = queryFiltered.replace(entry.getKey(), entry.getValue());
				
/*		
		if (inSubResultString.compareTo("")!=0){
			String newInSubResultString = inSubResultString;
			for (Entry<String, String> entry: varReplacementMap.entrySet())
				newInSubResultString = newInSubResultString.replace(entry.getKey(), entry.getValue());
					
			queryFiltered =queryFiltered.replace(inSubResultString, newInSubResultString);
			/*
			String firstVar=inSubResultMat.group(2);
			String replaceVar=inSubResultMat.group(3);
			
			queryFiltered= queryFiltered.replace(firstVar, replaceVar);
			System.out.println(firstVar);
			System.out.println(replaceVar);
			System.out.println("query filtered final: "+queryFiltered);
			
		} else 
			queryFiltered=queryFiltered.replace(inSubResultString, "");
	*/	
		
		log.trace("submitting filtered query: {}",queryFiltered);
		
		return queryFiltered;
	}
	
	/*	
	private static class Pair {
			private String toInsert;
			//is strange Behaviour is true if we are searching for RI , GHN or Service
			private boolean isStrangeBehaviour;
			
			public Pair(String toInsert, boolean isStrangeBehaviour){
				this.toInsert= toInsert;
				this.isStrangeBehaviour= isStrangeBehaviour;
			}
			
			public String getToInsert(){
				return this.toInsert;
			}
			
			public boolean isStrangeBehaviour(){
				return this.isStrangeBehaviour;
			}
		}
	*/
	
}


