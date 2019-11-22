package org.gcube.dataanalysis.executor.job.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

import com.thoughtworks.xstream.XStream;

public class GenericWorkerCaller {

	
	public static String getGenericWorkerCall(String algorithm, String session, AlgorithmConfiguration configuration,int leftSetIndex,int rightSetIndex,int leftElements,int rightElements, boolean isduplicate,boolean deleteTemporaryFiles, String callTemplate) throws Exception{
		
		String xmlconfig = new XStream().toXML(configuration);
		xmlconfig = xmlconfig.replace("\n", "").replace("\t", "");
		xmlconfig = xmlconfig.replaceAll(">[ ]+<", "> <");
		
		/*
		AnalysisLogger.getLogger().debug("CONFIG of Task:");
		AnalysisLogger.getLogger().debug("algorithm: "+algorithm);
		AnalysisLogger.getLogger().debug("leftSetIndex: "+leftSetIndex);
		AnalysisLogger.getLogger().debug("leftElements: "+leftElements);
		AnalysisLogger.getLogger().debug("rightSetIndex: "+rightSetIndex);
		AnalysisLogger.getLogger().debug("rightElements: "+rightElements);
		AnalysisLogger.getLogger().debug("session: "+session);
		AnalysisLogger.getLogger().debug("isduplicate: "+isduplicate);
		AnalysisLogger.getLogger().debug("deleteTemporaryFiles: "+deleteTemporaryFiles);
		*/
		//String call=FileTools.loadString(is.getAbsolutePath(), "UTF-8");
		String call = new String(callTemplate.getBytes());
//		String call = callTemplate;
		//AnalysisLogger.getLogger().debug("call template : "+call);
		call = call.replace("#"+GenericWorker.AlgorithmClassParameter+"#", algorithm);
		call = call.replace("#"+GenericWorker.LeftSetStartIndexParameter+"#", ""+leftSetIndex);
		call = call.replace("#"+GenericWorker.NumberOfLeftElementsToProcessParameter+"#", ""+leftElements);
		call = call.replace("#"+GenericWorker.RightSetStartIndexParameter+"#", ""+rightSetIndex);
		call = call.replace("#"+GenericWorker.NumberOfRightElementsToProcessParameter+"#", ""+rightElements);
		call = call.replace("#"+GenericWorker.SessionParameter+"#", session);
		call = call.replace("#"+GenericWorker.IsDuplicateParameter+"#", ""+isduplicate);
		call = call.replace("#"+GenericWorker.DeleteTemporaryFilesParameter+"#", ""+deleteTemporaryFiles);
		call = call.replace("#"+GenericWorker.ConfigurationFileParameter+"#", ""+xmlconfig);
		
		return call;
		
	}
}
