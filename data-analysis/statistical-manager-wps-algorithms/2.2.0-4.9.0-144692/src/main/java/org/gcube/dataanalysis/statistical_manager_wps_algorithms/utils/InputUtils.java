/**
 * 
 */
package org.gcube.dataanalysis.statistical_manager_wps_algorithms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.io.data.binding.literal.LiteralDateTimeBinding;


public class InputUtils {

	/**
	 * @param inputData 
	 * @param string
	 * @return
	 */
	public static List<String> getListStringInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		return getListStringInputParameter(inputData, parameterName, false);
	}

	/**
	 * @param inputData 
	 * @param string
	 * @return
	 */
	public static List<String> getListStringInputParameter(Map<String, List<IData>> inputData, String parameterName, boolean almostOne) {
		List<IData> list = getIdataList(inputData, parameterName, almostOne);
		List<String> res = new ArrayList<String>();
		for (IData iData: list)
			res.add( ((LiteralStringBinding)iData).getPayload() );
		return res;
	}

	/**
	 * @param inputData 
	 * @param string
	 * @return
	 */
	public static List<Integer> getListIntegerInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		return getListIntegerInputParameter(inputData, parameterName, false);
	}

	/**
	 * @param inputData 
	 * @param string
	 * @return
	 */
	public static List<Integer> getListIntegerInputParameter(Map<String, List<IData>> inputData, String parameterName, boolean almostOne) {
		List<IData> list = getIdataList(inputData, parameterName, almostOne);
		
		List<Integer> res = new ArrayList<Integer>();
		for (IData iData: list)
			res.add( ((LiteralIntBinding)iData).getPayload() );
		return res;
	}

	/**
	 * @param inputData
	 * @param parameterName
	 * @return
	 */
	private static List<IData> getIdataList(Map<String, List<IData>> inputData, String parameterName, boolean almostOne) {
		List<IData> list = inputData.get(parameterName);
		if (list==null || list.size()==0)
			if (almostOne)
				throw new RuntimeException("Parameter \"" + parameterName + "\" must have almost one element.");
			else
				return new ArrayList<IData>();
		else
			return list;		
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static String getStringInputParameter(Map<String, List<IData>> inputData, String parameterName, String defaultValue) {
		String ris = getStringInputParameter(inputData, parameterName, false);
		return (ris==null ? defaultValue : ris);
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static String getStringInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		return getStringInputParameter(inputData, parameterName, false);
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static String getStringInputParameter(Map<String, List<IData>> inputData, String parameterName, boolean mandatory) {
		List<IData> list = inputData.get(parameterName);
		if (list==null || list.size()==0)
			if (mandatory)
				throw new RuntimeException("Parameter \"" + parameterName + "\" is mandatory.");
			else
				return null;
		
		IData iData = list.get(0);
		return ((LiteralStringBinding)iData).getPayload();
	}

	
	
	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static Integer getIntegerInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		return getIntegerInputParameter(inputData, parameterName, false);
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static Integer getIntegerInputParameter(Map<String, List<IData>> inputData, String parameterName, boolean mandatory) {
		List<IData> list = inputData.get(parameterName);
		if (list==null || list.size()==0)
			if (mandatory)
				throw new RuntimeException("Parameter \"" + parameterName + "\" is mandatory.");
			else
				return null;
		
		IData iData = list.get(0);
		return ((LiteralIntBinding)iData).getPayload();
	}

	public static Integer getIntegerInputParameter(Map<String, List<IData>> inputData, String parameterName, int defaultValue) {
		Integer ris = getIntegerInputParameter(inputData, parameterName, false);
		return (ris==null ? defaultValue : ris);
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static String getDateTimeInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		List<IData> list = inputData.get(parameterName);
		if (list==null || list.size()==0)
			return null;
		IData iData = list.get(0);
		return ((LiteralDateTimeBinding)iData).getPayload().toString();
	}

	/**
	 * @param inputData
	 * @param string
	 * @return
	 */
	public static Boolean getBooleanInputParameter(Map<String, List<IData>> inputData, String parameterName) {
		return getBooleanInputParameter(inputData, parameterName, false);
	}

	public static Boolean getBooleanInputParameter(Map<String, List<IData>> inputData, String parameterName, boolean mandatory) {
		List<IData> list = inputData.get(parameterName);
		if (list==null || list.size()==0)
			if (mandatory)
				throw new RuntimeException("Parameter \"" + parameterName + "\" is mandatory.");
			else
				return null;
		
		IData iData = list.get(0);
		String value = ((LiteralStringBinding)iData).getPayload().toLowerCase();
		return (value.contentEquals("true") || value.contentEquals("on") || value.contentEquals("1") || value.contentEquals("y") || value.contentEquals("t") || value.contentEquals("yes"));
	}

	public static Boolean getBooleanInputParameter(Map<String, List<IData>> inputData, String parameterName, Boolean defaultValue) {
		Boolean ris = getBooleanInputParameter(inputData, parameterName, false);
		return (ris==null ? defaultValue : ris);
	}
}
