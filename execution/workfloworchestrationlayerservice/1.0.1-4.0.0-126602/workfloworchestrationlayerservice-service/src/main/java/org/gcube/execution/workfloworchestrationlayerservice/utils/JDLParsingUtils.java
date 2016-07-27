package org.gcube.execution.workfloworchestrationlayerservice.utils;

import gr.uoa.di.madgik.execution.utils.EnvironmentKeyValue;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class JDLParsingUtils is a utility class used during the parsing of a jdl description
 * 
 */
public class JDLParsingUtils
{
	
	
	public static final String NODES = "Nodes";
	public static final String DESCRIPTION = "Description";
	
	public static boolean IsSandboxNameReference(String SandboxName)
	{
		if(SandboxName.startsWith("root.nodes.") && SandboxName.contains(".description.OutputSandbox[") && SandboxName.endsWith("]")) return true;
		return false;
	}
	
	/**
	 * Trims the provided value
	 * 
	 * @param val the value to trim
	 * 
	 * @return the trimmed string
	 */
	public static String Trim(String val)
	{
		if(val==null) return "";
		return val.trim();
	}
	
	/**
	 * Parses a sandbox
	 * 
	 * @param val the sandbox serialization
	 * 
	 * @return a list of the elements of the sandbox initially separated by a comma character
	 */
	public static List<String> ParseSandbox(String val)
	{
		List<String> vals=new ArrayList<String>();
		if(val==null) return vals;
		String[] items=val.split(",");
		for(String it : items)
		{
			vals.add(JDLParsingUtils.StripQuotes(it));
		}
		return vals;
	}
	
	/**
	 * Parses the environment jdl attribute serialization
	 * 
	 * @param val the serialization
	 * 
	 * @return the list of key value pairs parsed
	 */
	public static List<EnvironmentKeyValue> ParseEnvironment(String val)
	{
		List<EnvironmentKeyValue> vals=new ArrayList<EnvironmentKeyValue>();
		if(val==null) return vals;
		String[] items=JDLParsingUtils.StripBrackets(val).split(",");
		for(String it : items)
		{
			String []pair=JDLParsingUtils.StripQuotes(it.trim()).split("=");
			if(pair.length==2 && pair[0]!=null && pair[0].trim().length()!=0 && pair[1]!=null && pair[1].trim().length()!=0)
			{
				vals.add(new EnvironmentKeyValue(pair[0].trim(), pair[1].trim()));
			}
		}
		return vals;
	}

	/**
	 * Strip brackets
	 * 
	 * @param val the string to strip the brackets from
	 * 
	 * @return the stripped value
	 */
	public static String StripBrackets(String val)
	{
		if(val==null) return val;
		String payload=val.trim();
		if(payload.startsWith("{") && payload.endsWith("}"))payload=payload.substring(1, payload.length()-1);
		return payload.trim();
	}
	
	/**
	 * Strip quotes.
	 * 
	 * @param val the string to strip the quotes from
	 * 
	 * @return the stripped value
	 */
	public static String StripQuotes(String val)
	{
		if(val==null) return val;
		String payload=val.trim();
		if(payload.startsWith("\"") && payload.endsWith("\""))payload=payload.substring(1, payload.length()-1);
		return payload;
	}
	
	/**
	 * Strip comments.
	 * 
	 * @param val the value to remove the commented characters from
	 * 
	 * @return the value without the commented characters
	 */
	public static String StripComments(String val)
	{
		StringBuilder buf=new StringBuilder();
		boolean lineComment=false;
		boolean multilineComment=false;
		for(int i=0;i<val.length();i+=1)
		{
			if(lineComment)
			{
				if(val.charAt(i)=='\n') lineComment=false;
			}
			else if(multilineComment)
			{
				if(val.charAt(i)=='*' && val.charAt(i+1)=='/')
				{
					multilineComment=false;
					i+=1;
				}
			}
			else
			{
				if(Character.isWhitespace(val.charAt(i))) buf.append(val.charAt(i));
				else if(val.charAt(i)=='#')lineComment=true;
				else if(val.charAt(i)=='/' && val.charAt(i+1)=='/') lineComment=true;
				else if(val.charAt(i)=='/' && val.charAt(i+1)=='*') multilineComment=true;
				else
				{
					buf.append(val.charAt(i));
				}
			}
		}
		return buf.toString();
	}
	
	/**
	 * Retrieves the the next definition block contained in the provided value including all 
	 * its internally contained definition blocks 
	 * 
	 * @param val the value to retrieve the definition block from
	 * 
	 * @return the definition block
	 */
	public static String GetDefinitionBlock(String val)
	{
		String block=null;
		int startingOffset=JDLParsingUtils.NextNonEmptyChar(val);
		if(startingOffset<0 || startingOffset>=val.length()) return null;
		int innerBlock=0;
		if(val.charAt(startingOffset)!='[') return null;
		for(int i=startingOffset;i<val.length();i+=1)
		{
			if(val.charAt(i)=='[') innerBlock+=1; //first iteration will increase the block counter
			else if(val.charAt(i)==']') innerBlock-=1;
			if(innerBlock==0)
			{
				block=val.substring(startingOffset+1, i);//i-1
				break;
			}
		}
		return block;
	}
	
	/**
	 * Retrieves the position of the next non whitespace character
	 * 
	 * @param val the value to search for non whitespace character
	 * 
	 * @return the position
	 */
	private static int NextNonEmptyChar(String val)
	{
		for(int i=0;i<val.length();i+=1)
		{
			if(!Character.isWhitespace(val.charAt(i))) return i;
		}
		return -1;
	}
	
	/**
	 * Retrieves the dependencies list from the serialization of the respective DAG jdl attribute.
	 * Supported dependency definition includes only pairs and not nested lists
	 * 
	 * @param dependencies the dependencies serialization
	 * 
	 * @return A map containing the declared dependencies.
	 */
	public static Map<String, List<String>> GetDependencies(String dependencies)
	{
		Map<String, List<String>> keyVals=new HashMap<String, List<String>>();
		String deps=JDLParsingUtils.Trim(JDLParsingUtils.StripBrackets(JDLParsingUtils.Trim(dependencies)));
		int offset=0;
		while(true)
		{
			if(offset>=deps.length()) break;
			int openingBracket=deps.indexOf('{',offset);
			int closingBracket=deps.indexOf('}',offset);
			if(openingBracket<0 || closingBracket<0) break;
			offset=closingBracket+1;
			String block=JDLParsingUtils.StripBrackets(deps.substring(openingBracket, closingBracket+1));
			String []elems=block.split(",");
			if(elems.length!=2 || elems[0].trim().length()==0 || elems[1].trim().length()==0) continue;
			if(!keyVals.containsKey(elems[1].trim()))
			{
				keyVals.put(elems[1].trim(), new ArrayList<String>());
			}
			keyVals.get(elems[1].trim()).add(elems[0].trim());
		}
		return keyVals;
	}

	/**
	 * Retrieves the attributes and the respective values that are defined for them within the
	 * provided definition block
	 * 
	 * @param block the definition block to parse
	 * 
	 * @return the map containing the keys and the respective values
	 */
	public static Map<String, String> GetKeyValues(String block)
	{
		Map<String, String> keyVals=new HashMap<String, String>();
		String Key=null;
		String Value=null;
		boolean StartingValueParsing=true;
		StringBuilder wordBuf=new StringBuilder();
		for(int i=0;i<block.length();i+=1)
		{
			//if(Character.isWhitespace(block.charAt(i))) continue;
			if(Key==null)
			{
				if(block.charAt(i)=='=')
				{
					Key=wordBuf.toString().trim();
					wordBuf=new StringBuilder();
				}
				else
				{
					wordBuf.append(block.charAt(i));
				}
			}
			else
			{
				if(StartingValueParsing)
				{
					if(!Character.isWhitespace(block.charAt(i))) StartingValueParsing=false;
					if(block.charAt(i)=='[')
					{
						Value='['+JDLParsingUtils.GetDefinitionBlock(block.substring(i))+']';
						i+=Value.length();
						int nextNonEmpty=JDLParsingUtils.NextNonEmptyChar(block.substring(i));
						if(nextNonEmpty>=0) i+=(nextNonEmpty+1);
						StartingValueParsing=true;
						keyVals.put(Key.trim(), Value.trim());
						wordBuf=new StringBuilder();
						Key=null;
						Value=null;
						continue;
					}
				}
				if(block.charAt(i)==';')
				{
					Value=wordBuf.toString();
					keyVals.put(Key.trim(), Value.trim());
					wordBuf=new StringBuilder();
					StartingValueParsing=true;
					Key=null;
					Value=null;
					continue;
				}
				else
				{
					wordBuf.append(block.charAt(i));
				}
			}
		}
		return keyVals;
	}

	/**
	 * Retrieves the value of the requested key
	 * 
	 * @param KeyValues the map containing the keys and values as returned by {@link JDLParsingUtils#GetKeyValues(String)} 
	 * @param Key the key
	 * @param mandatory Whether the key is mandatory
	 * 
	 * @return the respective value
	 * 
	 * @throws WorkflowValidationException In case the key is mandatory but it is not found
	 */
//	public static String GetKeyValue(Map<String, String> KeyValues,ParsedJDLInfo.KnownKeys Key,boolean mandatory) throws WorkflowValidationException
//	{
//		if(!KeyValues.containsKey(Key.toString())) 
//		{
//			if(mandatory) throw new WorkflowValidationException("Needed key "+Key+" not found");
//			else return null;
//		}
//		String val=KeyValues.get(Key.toString());
//		if(val==null || val.trim().length()==0) 
//		{
//			if(mandatory) throw new WorkflowValidationException("Value of key "+Key+" not found");
//			else return null;
//		}
//		return val.trim();
//	}
	
	
	public static String removeBrackets(String string)
	{
		int indexOfOpenBracket = string.indexOf("[");
		int indexOfLastBracket = string.lastIndexOf("]");
		if(indexOfOpenBracket<0)
			return string;
		return string.substring(indexOfOpenBracket+1, indexOfLastBracket);
		
	}
}

