package gr.uoa.di.madgik.workflow.adaptor.search.utils;

import java.util.Map;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.join.RecordGenerationPolicy;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.OperationMode;
import gr.uoa.di.madgik.workflow.adaptor.search.searchsystemplan.Constants;
import gr.uoa.di.madgik.workflow.adaptor.search.utils.exception.MalformedFunctionalArgumentException;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class FunctionalArgumentParser 
{
	
	private static boolean getBooleanArgumentValue(String argument, boolean defaultValue) throws MalformedFunctionalArgumentException 
	{
		if(argument != null) 
		{
			argument.toLowerCase();
			if(!argument.equals("true") && !argument.equals("1") && !argument.equals("false") && !argument.equals("0"))
				throw new MalformedFunctionalArgumentException("Argument " + argument + " is not of boolean type");
		}
		if(argument == null || argument.equals(Constants.DEFAULT))
			return defaultValue;
		if(argument.equals("true") || argument.equals("1"))
			return true;
		return false;
	}
	
	public static boolean getDuplicateEliminationStatus(Map<String, String> arguments) throws MalformedFunctionalArgumentException 
	{
		return getBooleanArgumentValue(arguments.get(Constants.DUPLICATEELIMINATION), true);
	}
	
	public static RecordGenerationPolicy getPayloadSide(Map<String, String> arguments) throws MalformedFunctionalArgumentException 
	{
		String argument = arguments.get(Constants.PAYLOADSIDE);
		if(argument == null || argument.equals(Constants.DEFAULT))
			return RecordGenerationPolicy.Concatenate;
		if(argument.equals(Constants.PAYLOADLEFT))
			return RecordGenerationPolicy.KeepLeft;
		if(argument.equals(Constants.PAYLOADRIGHT))
			return RecordGenerationPolicy.KeepRight;
		if(argument.equals(Constants.PAYLOADBOTH))
			return RecordGenerationPolicy.Concatenate;
		throw new MalformedFunctionalArgumentException("Invalid value of " + Constants.PAYLOADSIDE);
	}
	
	
	public static OperationMode getMergeOperationMode(Map<String, String> arguments) throws MalformedFunctionalArgumentException 
	{
		//System.out.println("getMergeOperationMode : arguments : " + arguments);
		if (arguments == null)
			return OperationMode.FirstAvailable;
		
		String argument = arguments.get(Constants.FUSE);
		//System.out.println("1.argument : " + argument);
		
		
		if(argument != null  && argument.equals(Constants.FUSE)) {
			//System.out.println("FUSE : ");
			return OperationMode.Fusion;
		}
		
		
		if (!arguments.containsKey(Constants.SORT))
			return OperationMode.FirstAvailable;
		
		argument = arguments.get(Constants.SORT);
		//System.out.println("2.argument : " + argument);
		
		if(argument.equals(Constants.FUSE))
			return OperationMode.Fusion;
		else if(argument.equals(Constants.MERGESORT))
			return OperationMode.Sort;
		else if(argument.equals(Constants.MERGEFIFO))
			return OperationMode.FIFO;
		else
			return OperationMode.FirstAvailable;
	}
	

}
